package com.app.mhcsn_ma.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_ma.ActivityCareCenterDocList;
import com.app.mhcsn_ma.ActivityCareCentersList;
import com.app.mhcsn_ma.R;
import com.app.mhcsn_ma.model.CareCenterDoctorBean;
import com.app.mhcsn_ma.model.DoctorsModel;
import com.app.mhcsn_ma.util.DATA;
import com.app.mhcsn_ma.util.GloabalMethods;

import java.util.ArrayList;
import java.util.Locale;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class CareCenterDocAdapter extends ArrayAdapter<CareCenterDoctorBean> {

	Activity activity;
	ArrayList<CareCenterDoctorBean> careCenterDoctorBeans, careCenterDoctorBeansOrig;

	public CareCenterDocAdapter(Activity activity , ArrayList<CareCenterDoctorBean> careCenterDoctorBeans) {
		super(activity, R.layout.lv_care_center_doc_row, careCenterDoctorBeans);
		this.activity = activity;
		this.careCenterDoctorBeans = careCenterDoctorBeans;

		careCenterDoctorBeansOrig = new ArrayList<>();
		careCenterDoctorBeansOrig.addAll(careCenterDoctorBeans);
	}

	static class ViewHolder {
		ImageView ivDocImg,ivIsonline;
		TextView tvDocName, tvCall, tvMessage,tvAssignNow;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_care_center_doc_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivDocImg = convertView.findViewById(R.id.ivDocImg);
			viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);
			viewHolder.tvDocName = convertView.findViewById(R.id.tvDocName);
			viewHolder.tvCall = convertView.findViewById(R.id.tvCall);
			viewHolder.tvMessage = convertView.findViewById(R.id.tvMessage);
			viewHolder.tvAssignNow = convertView.findViewById(R.id.tvAssignNow);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivDocImg , viewHolder.ivDocImg);
			convertView.setTag(R.id.ivIsonline , viewHolder.ivIsonline);
			convertView.setTag(R.id.tvDocName , viewHolder.tvDocName);
			convertView.setTag(R.id.tvCall , viewHolder.tvCall);
			convertView.setTag(R.id.tvMessage , viewHolder.tvMessage);
			convertView.setTag(R.id.tvAssignNow , viewHolder.tvAssignNow);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDocName.setText(careCenterDoctorBeans.get(position).first_name+ " "+careCenterDoctorBeans.get(position).last_name);

		DATA.loadImageFromURL(careCenterDoctorBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivDocImg);

		int drawableResId = careCenterDoctorBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(drawableResId);

		viewHolder.tvCall.setEnabled(careCenterDoctorBeans.get(position).is_online.equalsIgnoreCase("1"));

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvCall:
						DATA.selectedDrId = careCenterDoctorBeans.get(position).id;
						//DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedUserCallId = ActivityCareCentersList.careRequestBean.id;//careVistBeans.get(position).patient_id;
						DATA.selectedDrName = careCenterDoctorBeans.get(position).first_name+" "+careCenterDoctorBeans.get(position).last_name;
						DATA.selectedDrImage = careCenterDoctorBeans.get(position).image;
						DATA.isFromDocToDoc = true;
						DATA.incomingCall = false;
						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = careCenterDoctorBeans.get(position).current_app;

						Intent myIntent1 = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent1);
						break;
					case R.id.tvMessage:
						//DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedDrId = careCenterDoctorBeans.get(position).id;
						DATA.selectedDrName = careCenterDoctorBeans.get(position).first_name+" "+careCenterDoctorBeans.get(position).last_name;
						DATA.selectedDrImage = careCenterDoctorBeans.get(position).image;
						DATA.isFromDocToDoc = true;

						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = careCenterDoctorBeans.get(position).current_app;
						new GloabalMethods(activity).initMsgDialog();

						break;
					case R.id.tvAssignNow:
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
								.setMessage("Are you sure?  Do you want to assign care request by patient "+ ActivityCareCentersList.careRequestBean.first_name+" "+
										ActivityCareCentersList.careRequestBean.last_name+ " to "+ careCenterDoctorBeans.get(position).first_name+" "+careCenterDoctorBeans.get(position).last_name+ "?")
								.setPositiveButton("Yes Assign", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										((ActivityCareCenterDocList) activity).assignLiveCareToDoc(position);
									}
								})
								.setNegativeButton("Not Now",null)
								.create().show();
						break;
				}
			}
		};
		viewHolder.tvCall.setOnClickListener(onClickListener);
		viewHolder.tvMessage.setOnClickListener(onClickListener);
		viewHolder.tvAssignNow.setOnClickListener(onClickListener);

		return convertView;
	}



	public void filter(String filterText) {
		careCenterDoctorBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- careCenterDoctorBeansOrig size: "+careCenterDoctorBeansOrig.size());
		if(filterText.length() == 0) {
			careCenterDoctorBeans.addAll(careCenterDoctorBeansOrig);
		} else {
			for(CareCenterDoctorBean temp : careCenterDoctorBeansOrig) {
				if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					careCenterDoctorBeans.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
