package com.app.OnlineCareTDC_Dr.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareTDC_Dr.ActivityRefferedPatients;
import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.ReferedPatientBean;
import com.app.OnlineCareTDC_Dr.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.GloabalMethods;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class RefPatientsAdapter extends ArrayAdapter<ReferedPatientBean> {

	Activity activity;
	ArrayList<ReferedPatientBean> referedPatientBeans;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;

	public RefPatientsAdapter(Activity activity, ArrayList<ReferedPatientBean> referedPatientBeans) {
		super(activity, R.layout.lv_ref_patients_row, referedPatientBeans);

		this.activity = activity;
		this.referedPatientBeans = referedPatientBeans;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		CircularImageView ivCallLog;
		TextView tvCallLogName,tvCallLogTime,tvDocOrPatient;
		ImageView ivIsonline;
		TextView tvCall,tvMsg;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_ref_patients_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivCallLog = (CircularImageView) convertView.findViewById(R.id.ivCallLog);
			viewHolder.tvDocOrPatient = (TextView) convertView.findViewById(R.id.tvDocOrPatient);
			viewHolder.tvCallLogName = (TextView) convertView.findViewById(R.id.tvCallLogName);
			viewHolder.tvCallLogTime= (TextView) convertView.findViewById(R.id.tvCallLogTime);
			viewHolder.ivIsonline= (ImageView) convertView.findViewById(R.id.ivIsonline);
			viewHolder.tvCall= (TextView) convertView.findViewById(R.id.tvCall);
			viewHolder.tvMsg= (TextView) convertView.findViewById(R.id.tvMsg);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivCallLog , viewHolder.ivCallLog);
			convertView.setTag(R.id.tvDocOrPatient , viewHolder.tvDocOrPatient);
			convertView.setTag(R.id.tvCallLogName, viewHolder.tvCallLogName);
			convertView.setTag(R.id.tvCallLogTime, viewHolder.tvCallLogTime);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			convertView.setTag(R.id.tvCall, viewHolder.tvCall);
			convertView.setTag(R.id.tvMsg, viewHolder.tvMsg);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL( referedPatientBeans.get(position).pimage, R.drawable.icon_call_screen,viewHolder.ivCallLog);
		viewHolder.tvCallLogName.setText(referedPatientBeans.get(position).patient_name);
		viewHolder.tvDocOrPatient.setText("Referred by: "+referedPatientBeans.get(position).doctor_name);
		viewHolder.tvCallLogTime.setText("Dated: "+referedPatientBeans.get(position).dateof);

		if (referedPatientBeans.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}


		viewHolder.tvMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ActivityRefferedPatients.referredId = referedPatientBeans.get(position).id;

				DATA.selectedUserCallId = referedPatientBeans.get(position).patient_id;
				DATA.selectedUserCallName = referedPatientBeans.get(position).patient_name;
				DATA.isFromDocToDoc = false;

				new GloabalMethods(activity).initMsgDialog();
			}
		});

		viewHolder.tvCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ActivityRefferedPatients.referredId = referedPatientBeans.get(position).id;

				DATA.isVideoCallFromRefPt = true;

				DATA.selectedUserCallId = referedPatientBeans.get(position).patient_id;
				DATA.selectedUserCallName = referedPatientBeans.get(position).patient_name;
				DATA.isFromDocToDoc = false;

				//new GloabalMethods(activity).initMsgDialog();

				if(referedPatientBeans.get(position).is_online.equals("1")){
					DATA.incomingCall = false;
					Intent myIntent = new Intent(activity, MainActivity.class);//SampleActivity.class
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					activity.startActivity(myIntent);
					//finish();
				}else {
					new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Patient Offline")
							.setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
							.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new GloabalMethods(activity).initMsgDialog();
								}
							}).setNegativeButton("Cancel",null).show();
				}
			}
		});
		

	
		return convertView;
	}

}
