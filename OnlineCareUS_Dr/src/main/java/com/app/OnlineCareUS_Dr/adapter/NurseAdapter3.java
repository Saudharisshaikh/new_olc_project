package com.app.OnlineCareUS_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareUS_Dr.ActivityGroupMessages;
import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.model.NurseBean2;
import com.app.OnlineCareUS_Dr.util.CheckInternetConnection;
import com.app.OnlineCareUS_Dr.util.CustomToast;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.app.OnlineCareUS_Dr.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class NurseAdapter3 extends ArrayAdapter<NurseBean2> {

	Activity activity;
	ArrayList<NurseBean2> nurseBeens;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	OpenActivity openActivity;

	public NurseAdapter3(Activity activity , ArrayList<NurseBean2> nurseBeens) {
		super(activity, R.layout.lv_nurse_row3, nurseBeens);

		this.activity = activity;
		this.nurseBeens = nurseBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvNurseName,tvNurseType,tvAddToGroup;
		CircularImageView ivNurse;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_nurse_row3, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNurseName = (TextView) convertView.findViewById(R.id.tvNurseName);
			viewHolder.tvNurseType = (TextView) convertView.findViewById(R.id.tvNurseType);
			viewHolder.tvAddToGroup = (TextView) convertView.findViewById(R.id.tvAddToGroup);
			viewHolder.ivNurse = (CircularImageView) convertView.findViewById(R.id.ivNurse);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNurseName, viewHolder.tvNurseName);
			convertView.setTag(R.id.tvNurseType, viewHolder.tvNurseType);
			convertView.setTag(R.id.tvAddToGroup, viewHolder.tvAddToGroup);
			convertView.setTag(R.id.ivNurse, viewHolder.ivNurse);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvNurseName.setText(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		viewHolder.tvNurseName.setTag(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		
		
		viewHolder.tvNurseType.setText(nurseBeens.get(position).doctor_category);
		viewHolder.tvNurseType.setTag(nurseBeens.get(position).doctor_category);
		


		DATA.loadImageFromURL(nurseBeens.get(position).image , R.drawable.icon_call_screen , viewHolder.ivNurse);


		if(ActivityGroupMessages.groupParticepentsDoctorIds != null) {
			if (ActivityGroupMessages.groupParticepentsDoctorIds.contains(nurseBeens.get(position).id)) {
				viewHolder.tvAddToGroup.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
				viewHolder.tvAddToGroup.setText("Added");
			} else {
				viewHolder.tvAddToGroup.setBackgroundResource(R.drawable.btn_selector);
				viewHolder.tvAddToGroup.setText("Add");
			}
		}

		return convertView;
	}
}
