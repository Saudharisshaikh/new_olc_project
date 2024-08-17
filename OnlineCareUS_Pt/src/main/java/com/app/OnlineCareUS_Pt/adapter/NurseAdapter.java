package com.app.OnlineCareUS_Pt.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.model.NurseBean;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class NurseAdapter extends ArrayAdapter<NurseBean> {

	Activity activity;
	ArrayList<NurseBean> nurseBeens;
	SharedPreferences prefs;

	public NurseAdapter(Activity activity , ArrayList<NurseBean> nurseBeens) {
		super(activity, R.layout.lv_nurse_row, nurseBeens);

		this.activity = activity;
		this.nurseBeens = nurseBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvNurseName,tvNurseType,tvAssign,tvPatientCat;
		CircularImageView ivNurse;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_nurse_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNurseName = (TextView) convertView.findViewById(R.id.tvNurseName);
			viewHolder.tvNurseType = (TextView) convertView.findViewById(R.id.tvNurseType);
			viewHolder.tvAssign = (TextView) convertView.findViewById(R.id.tvAssign);
			viewHolder.ivNurse = (CircularImageView) convertView.findViewById(R.id.ivNurse);
			viewHolder.tvPatientCat = (TextView) convertView.findViewById(R.id.tvPatientCat);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNurseName, viewHolder.tvNurseName);
			convertView.setTag(R.id.tvNurseType, viewHolder.tvNurseType);
			convertView.setTag(R.id.tvAssign, viewHolder.tvAssign);
			convertView.setTag(R.id.ivNurse, viewHolder.ivNurse);
			convertView.setTag(R.id.tvPatientCat, viewHolder.tvPatientCat);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvNurseName.setText(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		viewHolder.tvNurseName.setTag(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		
		
		viewHolder.tvNurseType.setText(nurseBeens.get(position).doctor_category);
		viewHolder.tvNurseType.setTag(nurseBeens.get(position).doctor_category);

		//viewHolder.tvPatientCat.setText("You are assigned as: "+nurseBeens.get(position).patient_category);
		//viewHolder.tvPatientCat.setTag("You are assigned as: "+nurseBeens.get(position).patient_category);
		String patientCat = "";
		if(nurseBeens.get(position).patient_category.equalsIgnoreCase("tcm")){
			patientCat = "TCM";
		}else if(nurseBeens.get(position).patient_category.equalsIgnoreCase("complex_care")){
			patientCat = "Complex Care";
		}else if(nurseBeens.get(position).patient_category.equalsIgnoreCase("home_health")){
			patientCat = "Home Health";
		}else if(nurseBeens.get(position).patient_category.equalsIgnoreCase("nursing_home")){
			patientCat = "Nursing Home";
		}
		viewHolder.tvPatientCat.setText(patientCat);
		viewHolder.tvPatientCat.setTag(patientCat);
		
		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivConv, conversationBeans.get(position).getImage(), R.drawable.ic_launcher);

		DATA.loadImageFromURL(nurseBeens.get(position).image, R.drawable.icon_call_screen, viewHolder.ivNurse);

		if (nurseBeens.get(position).is_online.equals("1")) {
			viewHolder.tvAssign.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
		}else {
			viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_drawable_grey);
		}
		
		return convertView;
	}

}
