package com.app.OnlineCareTDC_Dr.adapter;

import java.util.ArrayList;

import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.DrScheduleBean;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrScheduleAdapter extends ArrayAdapter<DrScheduleBean> {

	Activity activity;
	ArrayList<DrScheduleBean> drScheduleBeansList;

	public DrScheduleAdapter(Activity activity,ArrayList<DrScheduleBean> drScheduleBeansList) {
		super(activity, R.layout.dr_schedule_row, drScheduleBeansList);

		this.activity = activity;
		this.drScheduleBeansList = drScheduleBeansList;
	}

	static class ViewHolder {

		TextView tvScheduleDay,tvScheduleMorning,tvScheduleEvening;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.dr_schedule_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvScheduleDay = (TextView) convertView.findViewById(R.id.tvScheduleDay);
			viewHolder.tvScheduleMorning = (TextView) convertView.findViewById(R.id.tvScheduleMorning);
			viewHolder.tvScheduleEvening= (TextView) convertView.findViewById(R.id.tvScheduleEvening);
			
			convertView.setTag(viewHolder);
			
			convertView.setTag(R.id.tvScheduleDay, viewHolder.tvScheduleDay);
			convertView.setTag(R.id.tvScheduleMorning, viewHolder.tvScheduleMorning);
			convertView.setTag(R.id.tvScheduleEvening, viewHolder.tvScheduleEvening);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		viewHolder.tvScheduleDay.setText(drScheduleBeansList.get(position).getDay());
		viewHolder.tvScheduleMorning.setText("Morning: "+drScheduleBeansList.get(position).getFrom_time()+" To "+drScheduleBeansList.get(position).getTo_time());
		viewHolder.tvScheduleEvening.setText("Evening: "+drScheduleBeansList.get(position).getEvening_from_time()+" To "+drScheduleBeansList.get(position).getEvening_to_time());

		
		viewHolder.tvScheduleDay.setTag(drScheduleBeansList.get(position).getDay());
		viewHolder.tvScheduleMorning.setTag("Morning: "+drScheduleBeansList.get(position).getFrom_time()+" To "+drScheduleBeansList.get(position).getTo_time());
		viewHolder.tvScheduleEvening.setTag("Evening: "+drScheduleBeansList.get(position).getEvening_from_time()+" To "+drScheduleBeansList.get(position).getEvening_to_time());

		return convertView;
	}
}
