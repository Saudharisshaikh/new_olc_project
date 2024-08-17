package com.app.emcuradr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcuradr.ActivityGroupMessages;
import com.app.emcuradr.R;
import com.app.emcuradr.model.DoctorsModel;
import com.app.emcuradr.util.DATA;

import java.util.ArrayList;

public class DoctorInviteAdapter extends ArrayAdapter<DoctorsModel> {

	Activity activity;

	TextView tvDoctorName,tvDoctorDesignation,tvInvite;

	ImageView ivDoctorImage,ivIsonline;

	ArrayList<DoctorsModel> doctorsModels;

	public DoctorInviteAdapter(Activity activity,ArrayList<DoctorsModel> doctorsModels) {
		super(activity, R.layout.doctors_invite_row, doctorsModels);
		this.activity = activity;
		this.doctorsModels = doctorsModels;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.doctors_invite_row, null);

		tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
		tvDoctorDesignation = (TextView) convertView.findViewById(R.id.tvDoctorDesignation);
		tvInvite = (TextView) convertView.findViewById(R.id.tvInvite);
		ivDoctorImage = (ImageView) convertView.findViewById(R.id.ivDoctorImage);
		ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);


		DATA.loadImageFromURL(doctorsModels.get(position).image, R.drawable.icon_call_screen, ivDoctorImage);


		tvDoctorName.setText(doctorsModels.get(position).fName + " "+doctorsModels.get(position).lName);

		if (doctorsModels.get(position).current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
			tvDoctorDesignation.setText("Doctor");
		} else {
			tvDoctorDesignation.setText(doctorsModels.get(position).speciality_name);
		}
		if (doctorsModels.get(position).is_online.equals("1")) {
			ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			ivIsonline.setImageResource(R.drawable.icon_notification);
		}

		if(ActivityGroupMessages.groupParticepentsDoctorIds != null) {
			if (ActivityGroupMessages.groupParticepentsDoctorIds.contains(doctorsModels.get(position).id)) {
				tvInvite.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
				tvInvite.setText("Added");
			} else {
				tvInvite.setBackgroundResource(R.drawable.btn_selector);
				tvInvite.setText("Add");
			}
		}

		return convertView;
	}

}
