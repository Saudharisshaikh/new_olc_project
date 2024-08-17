package com.app.priorityone_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.priorityone_uc.R;
import com.app.priorityone_uc.model.SlotBean;
import com.app.priorityone_uc.util.DATA;

import java.util.ArrayList;

public class DrScheduleAdapter extends ArrayAdapter<SlotBean> {

	Activity activity;
	ArrayList<SlotBean> slotBeans;
	SharedPreferences prefs;


	public DrScheduleAdapter(Activity activity,ArrayList<SlotBean> slotBeans) {
		super(activity, R.layout.dr_schedule_row, slotBeans);

		this.activity = activity;
		this.slotBeans = slotBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		TextView tvScheduleDate,tvScheduleDay,tvScheduleWorkngHrs;
		RadioButton rbSelectSlot;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.dr_schedule_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvScheduleDate = (TextView) convertView.findViewById(R.id.tvScheduleDate);
			viewHolder.tvScheduleDay = (TextView) convertView.findViewById(R.id.tvScheduleDay);
			viewHolder.tvScheduleWorkngHrs = (TextView) convertView.findViewById(R.id.tvScheduleWorkngHrs);
			viewHolder.rbSelectSlot = (RadioButton) convertView.findViewById(R.id.rbSelectSlot);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvScheduleDate, viewHolder.tvScheduleDate);
			convertView.setTag(R.id.tvScheduleDay, viewHolder.tvScheduleDay);
			convertView.setTag(R.id.tvScheduleWorkngHrs, viewHolder.tvScheduleWorkngHrs);
			convertView.setTag(R.id.rbSelectSlot, viewHolder.rbSelectSlot);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvScheduleDate.setText(slotBeans.get(position).getDay());
		viewHolder.tvScheduleDate.setTag(slotBeans.get(position).getDay());

		viewHolder.tvScheduleDay.setText(slotBeans.get(position).getFrom_time());
		viewHolder.tvScheduleDay.setTag(slotBeans.get(position).getFrom_time());

		viewHolder.tvScheduleWorkngHrs.setText(slotBeans.get(position).getTo_time());
		viewHolder.tvScheduleWorkngHrs.setTag(slotBeans.get(position).getTo_time());

		if (prefs.getString("apptmntDate", "").contains(slotBeans.get(position).getFrom_time())) {

			viewHolder.tvScheduleDate.setTextColor(Color.parseColor(DATA.APP_THEME_RED_COLOR));//#32CD32 green_light
			viewHolder.tvScheduleDay.setTextColor(Color.parseColor(DATA.APP_THEME_RED_COLOR));
			viewHolder.tvScheduleWorkngHrs.setTextColor(Color.parseColor(DATA.APP_THEME_RED_COLOR));
			viewHolder.rbSelectSlot.setChecked(true);
		} else {
			viewHolder.tvScheduleDate.setTextColor(Color.parseColor("#cc000000"));
			viewHolder.tvScheduleDay.setTextColor(Color.parseColor("#cc000000"));
			viewHolder.tvScheduleWorkngHrs.setTextColor(Color.parseColor("#cc000000"));
			viewHolder.rbSelectSlot.setChecked(false);
		}
		return convertView;
	}

}
