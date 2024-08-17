package com.app.amnm_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.amnm_dr.R;
import com.app.amnm_dr.model.ScreeningToolQuestionBean;

import java.util.ArrayList;

public class ScreeningToolAdapter extends ArrayAdapter<ScreeningToolQuestionBean> {

	Activity activity;

	int getPosition;

	//String[] deseaseNames;
	ArrayList<ScreeningToolQuestionBean> screeningToolQuestionBeen;

	public ScreeningToolAdapter(Activity activity , ArrayList<ScreeningToolQuestionBean> screeningToolQuestionBeen) {
		super(activity, R.layout.depression_row,screeningToolQuestionBeen);
		this.activity = activity;
		this.screeningToolQuestionBeen = screeningToolQuestionBeen;
	}

	static class ViewHolder {
		protected TextView tvDiseaseNameRow;
		protected CheckBox checkSelectDisease;

}
	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.depression_row, null);

			 viewHolder = new ViewHolder();
			 viewHolder.tvDiseaseNameRow = (TextView) convertView.findViewById(R.id.tvDepressionLabel);
			 viewHolder.checkSelectDisease = (CheckBox) convertView.findViewById(R.id.cbDepression);

				convertView.setTag(viewHolder);
				convertView.setTag(R.id.tvDepressionLabel, viewHolder.tvDiseaseNameRow);
				convertView.setTag(R.id.cbDepression,viewHolder.checkSelectDisease);

				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//			viewHolder.checkSelectContact.setOnCheckedChangeListener(null);
		}

		viewHolder.checkSelectDisease.setEnabled(true);
		viewHolder.checkSelectDisease.setTag(position); // This line is important.

		viewHolder.tvDiseaseNameRow.setText(screeningToolQuestionBeen.get(position).question);
				
		
		
		viewHolder.checkSelectDisease.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
				System.out.println("--checkchange listener ");
				//pastHistoryBeans.get(getPosition).setSelected(isChecked);
				if(isChecked){
					screeningToolQuestionBeen.get(position).answer = "Yes";
					viewHolder.checkSelectDisease.setText("Yes");
				}else{
					screeningToolQuestionBeen.get(position).answer = "No";
					viewHolder.checkSelectDisease.setText("No");
				}
			}
		});
		
		if(screeningToolQuestionBeen.get(position).answer.equalsIgnoreCase("No")){
			viewHolder.checkSelectDisease.setChecked(false);
		}else {
			viewHolder.checkSelectDisease.setChecked(true);
		}

		return convertView;
	}

}
