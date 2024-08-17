package com.app.priorityone_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.priorityone_dr.R;
import com.app.priorityone_dr.model.TelemedicineCatData;

import java.util.ArrayList;

public class TelemedicineAdapter2 extends ArrayAdapter<TelemedicineCatData> {

	Activity activity;
	ArrayList<TelemedicineCatData> telemedicineCatDatas;

	public TelemedicineAdapter2(Activity activity, ArrayList<TelemedicineCatData> telemedicineCatDatas) {
		super(activity, R.layout.lv_telemed_row, telemedicineCatDatas);

		this.activity = activity;
		this.telemedicineCatDatas = telemedicineCatDatas;
	}

	static class ViewHolder {
		TextView tvChildCategoryData;
		RadioButton cbSelectCategoryData;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return telemedicineCatDatas.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_telemed_row, null);
			
			viewHolder = new ViewHolder();

			viewHolder.tvChildCategoryData = (TextView) convertView.findViewById(R.id.tvChildCategoryData);
			viewHolder.cbSelectCategoryData = (RadioButton) convertView.findViewById(R.id.cbSelectCategoryData);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvChildCategoryData, viewHolder.tvChildCategoryData);
			convertView.setTag(R.id.cbSelectCategoryData, viewHolder.cbSelectCategoryData);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvChildCategoryData.setText(telemedicineCatDatas.get(position).hcpcs_code+" - "+telemedicineCatDatas.get(position).service_name);


		viewHolder.cbSelectCategoryData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				telemedicineCatDatas.get(position).isSelected = isChecked;
			}
		});
		viewHolder.cbSelectCategoryData.setChecked(telemedicineCatDatas.get(position).isSelected);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				telemedicineCatDatas.get(position).isSelected = !telemedicineCatDatas.get(position).isSelected;
				notifyDataSetChanged();
			}
		});

		return convertView;
	}
}
