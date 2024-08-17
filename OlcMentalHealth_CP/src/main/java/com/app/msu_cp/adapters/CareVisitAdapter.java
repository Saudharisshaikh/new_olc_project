package com.app.msu_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.ActivityTcmDetails;
import com.app.msu_cp.R;
import com.app.msu_cp.model.CareVistBean;
import com.app.msu_cp.model.DoctorsModel;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;


public class CareVisitAdapter extends ArrayAdapter<CareVistBean>{

	Activity activity;
	ArrayList<CareVistBean> careVistBeans;
	
	public CareVisitAdapter(Activity activity, ArrayList<CareVistBean> careVistBeans) {
		super(activity, R.layout.care_visit_row);

		this.activity = activity;
		this.careVistBeans = careVistBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return careVistBeans.size();
	}

	static class ViewHolder {
		ImageView ivPatient,ivIsonline,ivDoc,ivDocIsOnline;
		TextView tvPtName,tvApptDate,tvFromTime,tvToTime,tvAptStatus,tvAptSymp,tvAptCond,tvCallPatient,
				tvSendMsgPatient,tvDocName,tvCallDoc,tvSendMsgDoc;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.care_visit_row, null);
			
			viewHolder = new ViewHolder();

			viewHolder.ivPatient = (ImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			viewHolder.ivDoc = (ImageView) convertView.findViewById(R.id.ivDoc);
			viewHolder.ivDocIsOnline = (ImageView) convertView.findViewById(R.id.ivDocIsOnline);
			viewHolder.tvPtName = (TextView) convertView.findViewById(R.id.tvPtName);
			viewHolder.tvApptDate = (TextView) convertView.findViewById(R.id.tvApptDate);
			viewHolder.tvFromTime = (TextView) convertView.findViewById(R.id.tvFromTime);
			viewHolder.tvToTime = (TextView) convertView.findViewById(R.id.tvToTime);
			viewHolder.tvAptStatus = (TextView) convertView.findViewById(R.id.tvAptStatus);
			viewHolder.tvAptSymp = (TextView) convertView.findViewById(R.id.tvAptSymp);
			viewHolder.tvAptCond = (TextView) convertView.findViewById(R.id.tvAptCond);
			viewHolder.tvCallPatient = (TextView) convertView.findViewById(R.id.tvCallPatient);
			viewHolder.tvSendMsgPatient = (TextView) convertView.findViewById(R.id.tvSendMsgPatient);
			viewHolder.tvDocName = (TextView) convertView.findViewById(R.id.tvDocName);
			viewHolder.tvCallDoc = (TextView) convertView.findViewById(R.id.tvCallDoc);
			viewHolder.tvSendMsgDoc = (TextView) convertView.findViewById(R.id.tvSendMsgDoc);
			
			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			convertView.setTag(R.id.ivDoc, viewHolder.ivDoc);
			convertView.setTag(R.id.ivDocIsOnline, viewHolder.ivDocIsOnline);
			convertView.setTag(R.id.tvPtName, viewHolder.tvPtName);
			convertView.setTag(R.id.tvApptDate, viewHolder.tvApptDate);
			convertView.setTag(R.id.tvFromTime, viewHolder.tvFromTime);
			convertView.setTag(R.id.tvToTime, viewHolder.tvToTime);
			convertView.setTag(R.id.tvAptStatus, viewHolder.tvAptStatus);
			convertView.setTag(R.id.tvAptSymp, viewHolder.tvAptSymp);
			convertView.setTag(R.id.tvAptCond, viewHolder.tvAptCond);
			convertView.setTag(R.id.tvCallPatient, viewHolder.tvCallPatient);
			convertView.setTag(R.id.tvSendMsgPatient, viewHolder.tvSendMsgPatient);
			convertView.setTag(R.id.tvDocName, viewHolder.tvDocName);
			convertView.setTag(R.id.tvCallDoc, viewHolder.tvCallDoc);
			convertView.setTag(R.id.tvSendMsgDoc, viewHolder.tvSendMsgDoc);
			
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPtName.setText(careVistBeans.get(position).first_name+" "+careVistBeans.get(position).last_name);
		viewHolder.tvApptDate.setText(careVistBeans.get(position).date);
		viewHolder.tvFromTime.setText(careVistBeans.get(position).from_time);
		viewHolder.tvToTime.setText(careVistBeans.get(position).to_time);
		viewHolder.tvAptStatus.setText(careVistBeans.get(position).status);
		viewHolder.tvAptSymp.setText(careVistBeans.get(position).symptom_name);
		viewHolder.tvAptCond.setText(careVistBeans.get(position).condition_name);
		//viewHolder.tvCallPatient.setText(careVistBeans.get(position).first_name);
		//viewHolder.tvSendMsgPatient.setText(careVistBeans.get(position).first_name);
		viewHolder.tvDocName.setText(careVistBeans.get(position).doc_name);
		//viewHolder.tvCallDoc.setText(careVistBeans.get(position).first_name);
		//viewHolder.tvSendMsgDoc.setText(careVistBeans.get(position).first_name);

		if(careVistBeans.get(position).is_online.equalsIgnoreCase("1")){
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
			viewHolder.tvCallPatient.setEnabled(true);
		}else {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
			viewHolder.tvCallPatient.setEnabled(false);
		}
		if(careVistBeans.get(position).d_is_online.equalsIgnoreCase("1")){
			viewHolder.ivDocIsOnline.setImageResource(R.drawable.icon_online);
			viewHolder.tvCallDoc.setEnabled(true);
		}else {
			viewHolder.ivDocIsOnline.setImageResource(R.drawable.icon_notification);
			viewHolder.tvCallDoc.setEnabled(false);
		}

		DATA.loadImageFromURL(careVistBeans.get(position).image,R.drawable.icon_call_screen,viewHolder.ivPatient);
		DATA.loadImageFromURL(careVistBeans.get(position).dimage,R.drawable.icon_call_screen,viewHolder.ivDoc);

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvCallPatient:
						//filterReports(DATA.selectedUserCallId);
						//DATA.allReportsFiltered = DATA.allAppointments.get(position).sharedReports;
						//DATA.selectedRefferedLiveCare = DATA.allAppointments.get(position);
						//DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
						//DATA.selectedUserAppntID = careVistBeans.get(position).liveCheckupId;
						//DATA.selectedUserLatitude =  careVistBeans.get(position).latitude;
						//DATA.selectedUserLongitude =  careVistBeans.get(position).longitude;

						DATA.selectedUserCallId = careVistBeans.get(position).patient_id;
						DATA.selectedUserCallName = careVistBeans.get(position).first_name+" "+careVistBeans.get(position).last_name;
						DATA.selectedUserCallSympTom = careVistBeans.get(position).symptom_name;
						DATA.selectedUserCallCondition = careVistBeans.get(position).condition_name;
						DATA.selectedUserCallDescription = careVistBeans.get(position).description;
						DATA.selectedUserCallImage = careVistBeans.get(position).image;
						DATA.isFromDocToDoc = false;
						DATA.incomingCall = false;
						ActivityTcmDetails.primary_patient_id = careVistBeans.get(position).primary_patient_id;
						ActivityTcmDetails.family_is_online = "1";//not good but api limitation
						Intent myIntent = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent);
						//finish();
						break;
					case R.id.tvSendMsgPatient:
						DATA.isFromDocToDoc = false;
						DATA.selectedUserCallId = careVistBeans.get(position).patient_id;
						DATA.selectedUserCallName = careVistBeans.get(position).first_name+" "+careVistBeans.get(position).last_name;
						DATA.selectedUserCallImage = careVistBeans.get(position).image;

						ActivityTcmDetails.primary_patient_id = careVistBeans.get(position).primary_patient_id;
						ActivityTcmDetails.family_is_online = "1";//not good but api limitation
						new GloabalMethods(activity).initMsgDialog();
						break;
					case R.id.tvCallDoc:
						//DATA.selectedDrQbId = doctorsModels.get(position).qb_id;
						//DATA.selectedDrQualification = doctorsModels.get(position).qualification;
						//DATA.selectedDoctorsModel = doctorsModels.get(position);

						DATA.selectedDrId = careVistBeans.get(position).doctor_id;
						DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedUserCallId = careVistBeans.get(position).patient_id;
						DATA.selectedDrName = careVistBeans.get(position).doc_name;
						DATA.selectedDrImage = careVistBeans.get(position).dimage;
						DATA.isFromDocToDoc = true;
						DATA.incomingCall = false;
						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = careVistBeans.get(position).current_app;

						Intent myIntent1 = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent1);
						//finish();

						break;
					case R.id.tvSendMsgDoc:
						//DATA.selectedDrQbId = DATA.allDoctors.get(position).qb_id;
						//DATA.selectedDrQualification = DATA.allDoctors.get(position).qualification;
						//DATA.selectedDoctorsModel = DATA.allDoctors.get(position);
						//DATA.selectedUserCallId = DATA.allDoctors.get(position).id;

						DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedDrId = careVistBeans.get(position).doctor_id;
						DATA.selectedDrName = careVistBeans.get(position).doc_name;
						DATA.selectedDrImage = careVistBeans.get(position).image;
						DATA.isFromDocToDoc = true;

						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = careVistBeans.get(position).current_app;
						new GloabalMethods(activity).initMsgDialog();

						break;
					default:
						break;
				}

			}
		};
		viewHolder.tvCallPatient.setOnClickListener(onClickListener);
		viewHolder.tvCallDoc.setOnClickListener(onClickListener);
		viewHolder.tvSendMsgPatient.setOnClickListener(onClickListener);
		viewHolder.tvSendMsgDoc.setOnClickListener(onClickListener);

		return convertView;
	}

}
