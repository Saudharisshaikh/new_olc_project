package com.app.msu_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.msu_uc.R;
import com.app.msu_uc.model.UrgentCareBean;

import java.util.ArrayList;

public class UrgentCareAdapter extends ArrayAdapter<UrgentCareBean> {

	Activity activity;
	ArrayList<UrgentCareBean> urgentCareBeens;
	public int ucAdapterSelPos = -1;

	public UrgentCareAdapter(Activity activity,ArrayList<UrgentCareBean> urgentCareBeens) {
		super(activity, R.layout.urgent_care_row, urgentCareBeens);

		this.activity = activity;
		this.urgentCareBeens = urgentCareBeens;

		ucAdapterSelPos = -1;
	}

	static class ViewHolder {
		TextView tvUrgentCareName,tvUrgentCareStatus,tvUrgentCareTap;
		RadioButton rbUcCell;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.urgent_care_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvUrgentCareName = convertView.findViewById(R.id.tvUrgentCareName);
			viewHolder.tvUrgentCareStatus = convertView.findViewById(R.id.tvUrgentCareStatus);
			viewHolder.tvUrgentCareTap = convertView.findViewById(R.id.tvUrgentCareTap);
			viewHolder.rbUcCell = convertView.findViewById(R.id.rbUcCell);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvUrgentCareName, viewHolder.tvUrgentCareName);
			convertView.setTag(R.id.tvUrgentCareStatus, viewHolder.tvUrgentCareStatus);
			convertView.setTag(R.id.tvUrgentCareTap, viewHolder.tvUrgentCareTap);
			convertView.setTag(R.id.rbUcCell, viewHolder.rbUcCell);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvUrgentCareName.setText(urgentCareBeens.get(position).center_name);
		if(urgentCareBeens.get(position).id.equalsIgnoreCase("a1")){
			viewHolder.tvUrgentCareTap.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvUrgentCareTap.setVisibility(View.GONE);
		}

		if(urgentCareBeens.get(position).total_doctors.equalsIgnoreCase("0")){
			viewHolder.tvUrgentCareStatus.setText("No doctor available");
			viewHolder.tvUrgentCareStatus.setTextColor(activity.getResources().getColor(R.color.theme_red));
		}else{
			viewHolder.tvUrgentCareStatus.setText(urgentCareBeens.get(position).total_doctors+" doctor(s) available");
			viewHolder.tvUrgentCareStatus.setTextColor(Color.parseColor("#43A047"));
		}


		boolean rbCheck = ucAdapterSelPos == position ? true : false;
		viewHolder.rbUcCell.setChecked(rbCheck);

		return convertView;
	}
}
