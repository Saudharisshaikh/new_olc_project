package com.app.fivestardoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.app.fivestardoc.R;
import com.app.fivestardoc.model.PastHistoryBean;
import com.app.fivestardoc.util.DATA;

import java.util.ArrayList;

public class PastHistoryAdapter extends ArrayAdapter<PastHistoryBean> {
	
	Activity activity;
	
	int getPosition;
	
	//String[] deseaseNames;
	ArrayList<PastHistoryBean> pastHistoryBeans;

	public PastHistoryAdapter(Activity activity , ArrayList<PastHistoryBean> pastHistoryBeans) {
		super(activity, R.layout.lay_past_history_row,pastHistoryBeans);
		this.activity = activity;
		this.pastHistoryBeans = pastHistoryBeans;
	}

	static class ViewHolder {
		//protected TextView tvDiseaseNameRow;
		protected CheckBox checkSelectDisease;

}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lay_past_history_row, null);

			 viewHolder = new ViewHolder();
			 //viewHolder.tvDiseaseNameRow = (TextView) convertView.findViewById(R.id.tvUsersRowName);
			 viewHolder.checkSelectDisease = (CheckBox) convertView.findViewById(R.id.checkSelectContact);

				convertView.setTag(viewHolder);
				//convertView.setTag(R.id.tvUsersRowName, viewHolder.tvDiseaseNameRow);
				convertView.setTag(R.id.checkSelectContact,viewHolder.checkSelectDisease);

				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//			viewHolder.checkSelectContact.setOnCheckedChangeListener(null);
		}

		viewHolder.checkSelectDisease.setTag(position); // This line is important.

		viewHolder.checkSelectDisease.setText(pastHistoryBeans.get(position).getDiseases());

		//viewHolder.tvDiseaseNameRow.setText(pastHistoryBeans.get(position).getDiseases());
				
		
		
		viewHolder.checkSelectDisease.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
				DATA.print("--checkchange listener ");
				DATA.pastHistoryBeans.get(getPosition).setSelected(isChecked);
			}
		});
		
		
		viewHolder.checkSelectDisease.setChecked(pastHistoryBeans.get(position).isSelected());
		return convertView;
	}

}
