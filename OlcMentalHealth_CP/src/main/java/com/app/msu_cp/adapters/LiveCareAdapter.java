package com.app.msu_cp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.ActivityTcmDetails;
import com.app.msu_cp.LiveCare;
import com.app.msu_cp.R;
import com.app.msu_cp.model.MyAppointmentsModel;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class LiveCareAdapter extends ArrayAdapter<MyAppointmentsModel> {

	Activity activity;
	ArrayList<MyAppointmentsModel> allAppointmentsOrig;
	OpenActivity openActivity;

	public LiveCareAdapter(Activity activity) {
		super(activity, R.layout.my_appointments_row2, DATA.allAppointments);

		this.activity = activity;

		allAppointmentsOrig = new ArrayList<>();
		allAppointmentsOrig.addAll(DATA.allAppointments);

		openActivity = new OpenActivity(activity);
	}
	static class ViewHolder {

		TextView tvMyappntDate,tvPatientQueue;
		CircularImageView imgPetientLivecare;
		ImageView ivIsonline;
		Button btnMessage,btnVideoCall,btnViewReport;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.my_appointments_row2, null);

			viewHolder = new ViewHolder();

			viewHolder.tvMyappntDate = (TextView) convertView.findViewById(R.id.tvMyappntDate);
			viewHolder.tvPatientQueue = (TextView) convertView.findViewById(R.id.tvPatientQueue);
			viewHolder.imgPetientLivecare = (CircularImageView) convertView.findViewById(R.id.imgPetientLivecare);
			viewHolder.ivIsonline= (ImageView) convertView.findViewById(R.id.ivIsonline);
			viewHolder.btnMessage= (Button) convertView.findViewById(R.id.btnMessage);
			viewHolder.btnVideoCall= (Button) convertView.findViewById(R.id.btnVideoCall);
			viewHolder.btnViewReport= (Button) convertView.findViewById(R.id.btnViewReport);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMyappntDate, viewHolder.tvMyappntDate);
			convertView.setTag(R.id.tvPatientQueue, viewHolder.tvPatientQueue);
			convertView.setTag(R.id.imgPetientLivecare, viewHolder.imgPetientLivecare);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			convertView.setTag(R.id.btnMessage, viewHolder.btnMessage);
			convertView.setTag(R.id.btnVideoCall, viewHolder.btnVideoCall);
			convertView.setTag(R.id.btnViewReport, viewHolder.btnViewReport);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvMyappntDate.setText(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
		viewHolder.tvMyappntDate.setTag(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);

		viewHolder.tvPatientQueue.setText(""+(position+1));
		viewHolder.tvPatientQueue.setTag(""+(position+1));

		int vis = activity instanceof LiveCare ? View.VISIBLE : View.INVISIBLE;
		viewHolder.tvPatientQueue.setVisibility(vis);

		//UrlImageViewHelper.setUrlDrawable(viewHolder.imgPetientLivecare, DATA.allAppointments.get(position).image, R.drawable.icon_call_screen);
		//Picasso.with(activity).load(DATA.allAppointments.get(position).image).placeholder(R.drawable.icon_call_screen).into(viewHolder.imgPetientLivecare);
		DATA.loadImageFromURL(DATA.allAppointments.get(position).image, R.drawable.icon_call_screen,viewHolder.imgPetientLivecare);

		if (DATA.allAppointments.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
				DATA.selectedUserCallId = DATA.allAppointments.get(position).id;
				DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name;
				DATA.selectedUserCallSympTom = DATA.allAppointments.get(position).symptom_name;
				DATA.selectedUserCallCondition = DATA.allAppointments.get(position).condition_name;
				DATA.selectedUserCallDescription = DATA.allAppointments.get(position).description;
				DATA.selectedUserAppntID = DATA.allAppointments.get(position).liveCheckupId;
				DATA.selectedUserLatitude =  DATA.allAppointments.get(position).latitude;
				DATA.selectedUserLongitude =  DATA.allAppointments.get(position).longitude;
				DATA.selectedUserCallImage = DATA.allAppointments.get(position).image;
				//filterReports(DATA.selectedUserCallId);
				DATA.allReportsFiltered = DATA.allAppointments.get(position).sharedReports;
				DATA.isFromDocToDoc = false;
				//DATA.isConfirence = false;
				DATA.selectedRefferedLiveCare = DATA.allAppointments.get(position);
				DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				//Intent i = new Intent(activity, LiveCareDetails.class);//SelectedDoctorsProfile
				//activity.startActivity(i);

				switch (v.getId()){
					case R.id.btnMessage:
						new GloabalMethods(activity).initMsgDialog();
						break;
					case R.id.btnVideoCall:
						if(DATA.selectedRefferedLiveCare.is_online.equals("1")){
							DATA.isVideoCallFromLiveCare = true;
							DATA.incomingCall = false;
							Intent myIntent = new Intent(activity.getBaseContext(), MainActivity.class);//SampleActivity.class
							myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							activity.startActivity(myIntent);
							activity.finish();
						}else {
							new AlertDialog.Builder(activity).setTitle("Patient offline")
									.setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
									.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											new GloabalMethods(activity).initMsgDialog();
										}
									}).setNegativeButton("Cancel",null).show();
						}
						break;
					case R.id.btnViewReport:
						openActivity.open(ActivityTcmDetails.class, false);
						break;
				}
			}
		};
		viewHolder.btnMessage.setOnClickListener(onClickListener);
		viewHolder.btnVideoCall.setOnClickListener(onClickListener);
		viewHolder.btnViewReport.setOnClickListener(onClickListener);


//		viewHolder.imgMyappntStts.setImageResource(DATA.allAppointments.get(position).icon);
//		viewHolder.imgMyappntStts.setTag(DATA.allAppointments.get(position).icon);

//		viewHolder.imgMyappntStts.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//			
//				
//				DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
//				DATA.selectedUserCallId = DATA.allAppointments.get(position).id;
//				DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name;
//
//				Intent i = new Intent(activity, VideoCallScreen.class);
//				activity.startActivity(i);
//				activity.finish();
//
////				getLiveCarePatiensList();
//			}
//		});	

		return convertView;
	}


	public void filter(String filterText) {

		try {
			DATA.allAppointments.clear();

			filterText = filterText.toLowerCase(Locale.getDefault());

			DATA.print("---doctorsModelsOrg size: "+allAppointmentsOrig.size());

			if(filterText.length() == 0) {
				DATA.allAppointments.addAll(allAppointmentsOrig);
			}

			else {

				for(MyAppointmentsModel temp :allAppointmentsOrig) {

					if(temp.first_name.toLowerCase(Locale.getDefault()).startsWith(filterText) ||
							temp.last_name.toLowerCase(Locale.getDefault()).startsWith(filterText)) {

						DATA.allAppointments.add(temp);
					}

				}
			}

		/*if (allPosts.size()==0) {
			DATA.noRecordingFound = true;
		} else {
			DATA.noRecordingFound = false;
		}*/
			notifyDataSetChanged();
		}catch (Exception e){
			e.printStackTrace();
		}

	}//end filter
}
