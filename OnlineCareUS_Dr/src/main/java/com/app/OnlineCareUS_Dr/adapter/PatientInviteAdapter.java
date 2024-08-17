package com.app.OnlineCareUS_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareUS_Dr.ActivityGroupMessages;
import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.model.MyAppointmentsModel;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class PatientInviteAdapter extends ArrayAdapter<MyAppointmentsModel> {

	Activity activity;
	ArrayList<MyAppointmentsModel> myAppointmentsModels;

	public PatientInviteAdapter(Activity activity,ArrayList<MyAppointmentsModel> myAppointmentsModels) {
		super(activity, R.layout.patient_invite_row, myAppointmentsModels);

		this.activity = activity;
		this.myAppointmentsModels = myAppointmentsModels;
	}
	static class ViewHolder {

		TextView tvPatientName,tvInvite;
		CircularImageView ivPatient;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.patient_invite_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvInvite = (TextView) convertView.findViewById(R.id.tvInvite);
			viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.ivIsonline= (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvInvite, viewHolder.tvInvite);
			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
				
		viewHolder.tvPatientName.setText(myAppointmentsModels.get(position).first_name+" "+myAppointmentsModels.get(position).last_name);

		DATA.loadImageFromURL(myAppointmentsModels.get(position).image , R.drawable.icon_call_screen, viewHolder.ivPatient);

		if (myAppointmentsModels.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}
		if(ActivityGroupMessages.groupParticepentsPatientIds != null){
			if(ActivityGroupMessages.groupParticepentsPatientIds.contains(myAppointmentsModels.get(position).id)){
				viewHolder.tvInvite.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
				viewHolder.tvInvite.setText("Added");
			}else {
				viewHolder.tvInvite.setBackgroundResource(R.drawable.btn_selector);
				viewHolder.tvInvite.setText("Add");
			}
		}

		return convertView;
	}
	
}
