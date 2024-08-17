package com.app.mdlive_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.model.PastHistoryBean;
import com.app.mdlive_cp.util.DATA;

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
			//viewHolder.tvDiseaseNameRow = (TextView) convertView.findViewById(R.id.tvDiseaseNameRow);
			viewHolder.checkSelectDisease = (CheckBox) convertView.findViewById(R.id.checkSelectDisease);

			convertView.setTag(viewHolder);
			//convertView.setTag(R.id.tvDiseaseNameRow, viewHolder.tvDiseaseNameRow);
			convertView.setTag(R.id.checkSelectDisease,viewHolder.checkSelectDisease);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//			viewHolder.checkSelectContact.setOnCheckedChangeListener(null);
		}

		viewHolder.checkSelectDisease.setTag(position); // This line is important.

		//viewHolder.tvDiseaseNameRow.setText(pastHistoryBeans.get(position).getDiseases());
		viewHolder.checkSelectDisease.setText(pastHistoryBeans.get(position).getDiseases());



		viewHolder.checkSelectDisease.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
				// DATA.allReports.get(getPosition).setSelected(buttonView.isChecked());// Set the value of checkbox to maintain its state.
				if(buttonView.isChecked()) {
					//DATA.allReports.get(getPosition).status = "1";
					//DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory + deseaseNames[position]+",";

				}else {
					//DATA.allReports.get(getPosition).status = "0";
					//DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory.replace(deseaseNames[position]+",", "");

				}
				System.out.println("--checkchange listener ");
				DATA.pastHistoryBeans.get(getPosition).setSelected(isChecked);
			}
		});


		viewHolder.checkSelectDisease.setChecked(pastHistoryBeans.get(position).isSelected());
		return convertView;
	}

}
