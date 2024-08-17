package com.app.fivestardoc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.fivestardoc.adapter.LiveCareAdapter;
import com.app.fivestardoc.api.ApiCallBack;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.model.MyAppointmentsModel;
import com.app.fivestardoc.model.ReportsModel;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.Database;
import com.app.fivestardoc.util.GloabalSocket;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LiveCare extends BaseActivity implements ApiCallBack,GloabalSocket.SocketEmitterCallBack{
	
	AppCompatActivity activity;
	ListView lvLiveCare,lvLiveCareCompleted;
	TextView tvNoLiveCares,tvNoLiveCaresCompleted;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;

	TextView tvLiveCare , tvCompletedCare;

	ViewFlipper vfLiveCare;

	boolean isForLiveCare = false;
	public static boolean isFromCompletedLiveCare = false;

	//BroadcastReceiver liveCareRefreshBroadcast;

	int selectedChild = 0;
	
	@Override
	protected void onStart() {
		/*registerReceiver(liveCareRefreshBroadcast, new IntentFilter("refresh_live_care_queue"));
		startService(new Intent(activity,LiveCareRefreshService.class));*/
		super.onStart();
	} 
	
	@Override
	protected void onStop() {
		super.onStop();
		/*stopService(new Intent(activity,LiveCareRefreshService.class));
		unregisterReceiver(liveCareRefreshBroadcast);*/
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getLiveCarePatiensList();
	}

	GloabalSocket gloabalSocket;

	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_care_1);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Patient Care");
		}

		isForLiveCare = getIntent().getBooleanExtra("isForLiveCare",false);
		
		activity = LiveCare.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		apiCallBack = this;

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_NEW_PATIENT);

		gloabalSocket = new GloabalSocket(activity,this);

		vfLiveCare = (ViewFlipper) findViewById(R.id.vfLiveCare);
		lvLiveCare = (ListView) findViewById(R.id.lvLiveCare);
		tvNoLiveCares = (TextView) findViewById(R.id.tvNoLiveCares);
		lvLiveCareCompleted = findViewById(R.id.lvLiveCareCompleted);
		tvNoLiveCaresCompleted = findViewById(R.id.tvNoLiveCaresCompleted);
		tvNoLiveCares = (TextView) findViewById(R.id.tvNoLiveCares);

//		tvLiveCare = findViewById(R.id.tvLiveCare);
//		tvCompletedCare = findViewById(R.id.tvCompletedCare);

		lvLiveCare.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {


				isFromCompletedLiveCare = false;
				DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
				DATA.selectedUserCallId = DATA.allAppointments.get(position).id;

				System.out.println("-- GMGM : "+DATA.selectedUserCallId);

				DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name;
				DATA.selectedUserCallDOB = DATA.allAppointments.get(position).birthdate;
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

				DATA.selectedLiveCare = DATA.allAppointments.get(position);

				DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

				Intent i = new Intent(activity, LiveCareDetails.class);//SelectedDoctorsProfile
				activity.startActivity(i);
				//		activity.finish();

			}
		});

		//completed live care
		lvLiveCareCompleted.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				isFromCompletedLiveCare = true;
				DATA.selectedUserQbid = DATA.allAppointmentsCompleted.get(position).patients_qbid;
				DATA.selectedUserCallId = DATA.allAppointmentsCompleted.get(position).id;
				DATA.selectedUserCallName = DATA.allAppointmentsCompleted.get(position).first_name+" "+DATA.allAppointmentsCompleted.get(position).last_name;
				DATA.selectedUserCallDOB = DATA.allAppointments.get(position).birthdate;
				DATA.selectedUserCallSympTom = DATA.allAppointmentsCompleted.get(position).symptom_name;
				DATA.selectedUserCallCondition = DATA.allAppointmentsCompleted.get(position).condition_name;
				DATA.selectedUserCallDescription = DATA.allAppointmentsCompleted.get(position).description;
				DATA.selectedUserAppntID = DATA.allAppointmentsCompleted.get(position).liveCheckupId;

				DATA.selectedUserLatitude =  DATA.allAppointmentsCompleted.get(position).latitude;
				DATA.selectedUserLongitude =  DATA.allAppointmentsCompleted.get(position).longitude;

				DATA.selectedUserCallImage = DATA.allAppointmentsCompleted.get(position).image;

				//filterReports(DATA.selectedUserCallId);
				DATA.allReportsFiltered = DATA.allAppointmentsCompleted.get(position).sharedReports;

				DATA.isFromDocToDoc = false;
				//DATA.isConfirence = false;

				DATA.selectedLiveCare = DATA.allAppointmentsCompleted.get(position);

				DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

				Intent i = new Intent(activity, LiveCareDetails.class);//SelectedDoctorsProfile
				activity.startActivity(i);
				//activity.finish();

			}
		});

		View.OnClickListener tabsClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
//					case R.id.tvLiveCare:
//						setUpTabs(0);
//						break;
//					case R.id.tvCompletedCare:
//						setUpTabs(1);
//						break;
					default:
						break;
				}
			}
		};

//		tvLiveCare.setOnClickListener(tabsClick);
//		tvCompletedCare.setOnClickListener(tabsClick);
		
		lvLiveCare.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
				DATA.selectedUserCallId = DATA.allAppointments.get(position).id;
				DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name;
				DATA.selectedUserCallDOB = DATA.allAppointments.get(position).birthdate;
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
				
				DATA.selectedLiveCare = DATA.allAppointments.get(position);

				DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
								
				Intent i = new Intent(activity, LiveCareDetails.class);//SelectedDoctorsProfile
				activity.startActivity(i);
		//		activity.finish();

			}
		});
		
		
		 /*liveCareRefreshBroadcast = new BroadcastReceiver() {
				
				@Override
				public void onReceive(Context context, Intent intent) {
					DATA.print("--broadcast recieved");
					//myAppointmentsAdapter.notifyDataSetChanged();
					if (DATA.allAppointments.size() == 0) {
						lvLiveCare.setVisibility(View.GONE);
						tvNoLiveCares.setVisibility(View.VISIBLE);
					} else {
						lvLiveCare.setVisibility(View.VISIBLE);
						tvNoLiveCares.setVisibility(View.GONE);
						}
					myAppointmentsAdapter = new LiveCareAdapter(activity);
					lvLiveCare.setAdapter(myAppointmentsAdapter);
				}
			};*/
	}//oncreate

	private void getLiveCarePatiensList() {

		if(isForLiveCare){
			setUpTabs(0);
		}else {
//			setUpTabs(1);
		}

		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_MY_LIVE_CHECKUPS,"post",params,apiCallBack, activity);
		apiManager.loadURL();

//		RequestParams params1 = new RequestParams();
//		params1.put("doctor_id", prefs.getString("id", ""));
//		ApiManager apiManager1 = new ApiManager(ApiManager.GET_MY_LIVE_CHECKUPS_COMPLETED,"post",params1,apiCallBack, activity);
//		apiManager1.loadURL();
	}

	LiveCareAdapter myAppointmentsAdapter;
	MyAppointmentsModel temp;
	String msg, status;
	JSONObject jsonObject;
	JSONArray appointmentsArray, reportsArray;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.GET_MY_LIVE_CHECKUPS)){
			try {

				jsonObject = new JSONObject(content);
				status = jsonObject.getString("status");
//					msg = jsonObject.getString("msg");

				if(status.equals("success"))
				{

					String appts = jsonObject.getString("livecheckups");
					appointmentsArray = new JSONArray(appts);

					if (appointmentsArray.length() == 0) {
						lvLiveCare.setVisibility(View.GONE);
						tvNoLiveCares.setVisibility(View.VISIBLE);
					} else {
						lvLiveCare.setVisibility(View.VISIBLE);
						tvNoLiveCares.setVisibility(View.GONE);

						DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
						temp = new MyAppointmentsModel();

						for(int i = 0; i<appointmentsArray.length(); i++) {


							temp = new MyAppointmentsModel();
							temp.id = appointmentsArray.getJSONObject(i).getString("id");
							temp.liveCheckupId = appointmentsArray.getJSONObject(i).getString("live_checkup_id");
							temp.first_name = appointmentsArray.getJSONObject(i).getString("first_name");
							temp.last_name = appointmentsArray.getJSONObject(i).getString("last_name");
							temp.symptom_name = appointmentsArray.getJSONObject(i).getString("symptom_name");
							temp.condition_name = appointmentsArray.getJSONObject(i).getString("condition_name");
							temp.description = appointmentsArray.getJSONObject(i).getString("description");
							temp.patients_qbid = appointmentsArray.getJSONObject(i).getString("patients_qbid");
							temp.datetime = appointmentsArray.getJSONObject(i).getString("datetime");

							temp.latitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("latitude"));
							temp.longitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("longitude"));
							if (appointmentsArray.getJSONObject(i).has("image")) {
								temp.image = appointmentsArray.getJSONObject(i).getString("image");
							} else {
								temp.image = "";
							}

							temp.birthdate = appointmentsArray.getJSONObject(i).getString("birthdate");
							temp.gender = appointmentsArray.getJSONObject(i).getString("gender");
							temp.residency = appointmentsArray.getJSONObject(i).getString("residency");
							temp.patient_phone = appointmentsArray.getJSONObject(i).getString("patient_phone");
							temp.StoreName = appointmentsArray.getJSONObject(i).getString("StoreName");
							temp.PhonePrimary = appointmentsArray.getJSONObject(i).getString("PhonePrimary");
							temp.pharmacy_address = appointmentsArray.getJSONObject(i).optString("pharmacy_address", "-");

							String additional_data = "";
							if(appointmentsArray.getJSONObject(i).has("additional_data")){
								additional_data = appointmentsArray.getJSONObject(i).getString("additional_data");
							}
							temp.pain_where = "None";
							temp.pain_severity = "None";
							if(!additional_data.isEmpty()){
								JSONObject additional_dataJSON = new JSONObject(additional_data);
								if(additional_dataJSON.has("pain_where")){
									temp.pain_where = additional_dataJSON.getString("pain_where");
								}
								if(additional_dataJSON.has("pain_severity")){
									temp.pain_severity = additional_dataJSON.getString("pain_severity");
								}
							}

							if (appointmentsArray.getJSONObject(i).has("reports")) {
								String rp = appointmentsArray.getJSONObject(i).getString("reports");
								JSONArray reports = new JSONArray(rp);
								temp.sharedReports = new ArrayList<ReportsModel>();
								//ReportsModel reportsModel;
								for (int j = 0; j < reports.length(); j++) {
									ReportsModel reportsModel = new ReportsModel();

									reportsModel.name = reports.getJSONObject(j).getString("file_display_name");
									reportsModel.url = reports.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
									reportsModel.patientID = reports.getJSONObject(j).getString("patient_id");//patient_id

									temp.sharedReports.add(reportsModel);

									//reportsModel = null;
								}
							} else {
								temp.sharedReports = new ArrayList<ReportsModel>();
							}

							DATA.allAppointments.add(temp);

							temp = null;
						}

						/*String reprts = jsonObject.getString("reports");

						reportsArray = new JSONArray(reprts);

						DATA.allReports = new ArrayList<ReportsModel>();

						ReportsModel temp1;

						for(int j = 0; j<reportsArray.length(); j++) {

							temp1 = new ReportsModel();

							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id

							DATA.allReports.add(temp1);

							temp1 = null;
						}*/

						myAppointmentsAdapter = new LiveCareAdapter(activity , DATA.allAppointments);
						lvLiveCare.setAdapter(myAppointmentsAdapter);

					}
				}

				else {

					Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
				}


			} catch (JSONException e) {
				DATA.print("--online care exception in getlivecare patients: "+e);
				e.printStackTrace();
			}
		}
//		else if (apiName.equals(ApiManager.GET_MY_LIVE_CHECKUPS_COMPLETED)) {
//			try {
//				jsonObject = new JSONObject(content);
//				status = jsonObject.getString("status");
////					msg = jsonObject.getString("msg");
//
//				if(status.equals("success"))
//				{
//					String appts = jsonObject.getString("livecheckups");
//					appointmentsArray = new JSONArray(appts);
//
//					if (appointmentsArray.length() == 0) {
//						lvLiveCareCompleted.setVisibility(View.GONE);
//						tvNoLiveCaresCompleted.setVisibility(View.VISIBLE);
//					} else {
//						lvLiveCareCompleted.setVisibility(View.VISIBLE);
//						tvNoLiveCaresCompleted.setVisibility(View.GONE);
//
//						DATA.allAppointmentsCompleted = new ArrayList<MyAppointmentsModel>();
//						temp = new MyAppointmentsModel();
//
//						for(int i = 0; i<appointmentsArray.length(); i++) {
//
//
//							temp = new MyAppointmentsModel();
//							temp.id = appointmentsArray.getJSONObject(i).getString("id");
//							temp.liveCheckupId = appointmentsArray.getJSONObject(i).getString("live_checkup_id");
//							temp.first_name = appointmentsArray.getJSONObject(i).getString("first_name");
//							temp.last_name = appointmentsArray.getJSONObject(i).getString("last_name");
//							temp.symptom_name = appointmentsArray.getJSONObject(i).getString("symptom_name");
//							temp.condition_name = appointmentsArray.getJSONObject(i).getString("condition_name");
//							temp.description = appointmentsArray.getJSONObject(i).getString("description");
//							temp.patients_qbid = appointmentsArray.getJSONObject(i).getString("patients_qbid");
//							temp.datetime = appointmentsArray.getJSONObject(i).getString("datetime");
//
//							temp.latitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("latitude"));
//							temp.longitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("longitude"));
//
//							if (appointmentsArray.getJSONObject(i).has("image")) {
//								temp.image = appointmentsArray.getJSONObject(i).getString("image");
//							} else {
//								temp.image = "";
//							}
//
//							temp.birthdate = appointmentsArray.getJSONObject(i).getString("birthdate");
//							temp.gender = appointmentsArray.getJSONObject(i).getString("gender");
//							temp.residency = appointmentsArray.getJSONObject(i).getString("residency");
//							temp.patient_phone = appointmentsArray.getJSONObject(i).getString("patient_phone");
//							temp.StoreName = appointmentsArray.getJSONObject(i).getString("StoreName");
//							temp.PhonePrimary = appointmentsArray.getJSONObject(i).getString("PhonePrimary");
//							temp.pharmacy_address = appointmentsArray.getJSONObject(i).optString("pharmacy_address", "-");
//
//							String additional_data = "";
//							if(appointmentsArray.getJSONObject(i).has("additional_data")){
//								additional_data = appointmentsArray.getJSONObject(i).getString("additional_data");
//							}
//							temp.pain_where = "None";
//							temp.pain_severity = "None";
//							if(!additional_data.isEmpty()){
//								JSONObject additional_dataJSON = new JSONObject(additional_data);
//								if(additional_dataJSON.has("pain_where")){
//									temp.pain_where = additional_dataJSON.getString("pain_where");
//								}
//								if(additional_dataJSON.has("pain_severity")){
//									temp.pain_severity = additional_dataJSON.getString("pain_severity");
//								}
//							}
//
//							if (appointmentsArray.getJSONObject(i).has("reports")) {
//								String rp = appointmentsArray.getJSONObject(i).getString("reports");
//								JSONArray reports = new JSONArray(rp);
//								temp.sharedReports = new ArrayList<ReportsModel>();
//								//ReportsModel reportsModel;
//								for (int j = 0; j < reports.length(); j++) {
//									ReportsModel reportsModel = new ReportsModel();
//
//									reportsModel.name = reports.getJSONObject(j).getString("file_display_name");
//									reportsModel.url = reports.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
//									reportsModel.patientID = reports.getJSONObject(j).getString("patient_id");//patient_id
//
//									temp.sharedReports.add(reportsModel);
//
//									//reportsModel = null;
//								}
//							} else {
//								temp.sharedReports = new ArrayList<ReportsModel>();
//							}
//
//							DATA.allAppointmentsCompleted.add(temp);
//
//							temp = null;
//						}
//
//						/*String reprts = jsonObject.getString("reports");
//
//						reportsArray = new JSONArray(reprts);
//
//						DATA.allReports = new ArrayList<ReportsModel>();
//
//						ReportsModel temp1;
//
//						for(int j = 0; j<reportsArray.length(); j++) {
//
//							temp1 = new ReportsModel();
//
//							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
//							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
//							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id
//
//							DATA.allReports.add(temp1);
//
//							temp1 = null;
//						}*/
//
//						myAppointmentsAdapter = new LiveCareAdapter(activity , DATA.allAppointmentsCompleted);
//						lvLiveCareCompleted.setAdapter(myAppointmentsAdapter);
//
//					}
//				}
//
//				else {
//
//					Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
//				}
//
//
//			} catch (JSONException e) {
//				DATA.print("--online care exception in getlivecare patients: "+e);
//
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void onSocketCallBack(String emitterResponse) {

		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("patient")){
				getLiveCarePatiensList();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	/*public void filterReports(String patientID) {

		DATA.allReportsFiltered = new ArrayList<ReportsModel>();
		ReportsModel temp;

		for(int i = 0; i<DATA.allReports.size(); i++) {

			temp = new ReportsModel();

			if(DATA.allReports.get(i).patientID.equals(patientID)) {
				
				DATA.print("item added");

				temp.name = DATA.allReports.get(i).name;
				temp.url = DATA.allReports.get(i).url;
				temp.patientID = DATA.allReports.get(i).patientID;

				DATA.allReportsFiltered.add(temp);	

				temp = null;
			}

		}

	}*/

	public void setUpTabs(int index){
		if(index == 0){
//			tvLiveCare.setBackgroundColor(getResources().getColor(R.color.theme_red));
//			tvLiveCare.setTextColor(getResources().getColor(android.R.color.white));
//			tvCompletedCare.setBackgroundColor(getResources().getColor(android.R.color.white));
//			tvCompletedCare.setTextColor(getResources().getColor(R.color.theme_red));

			selectedChild = 0;
			if (selectedChild > vfLiveCare.getDisplayedChild()) {

				vfLiveCare.setInAnimation(activity, R.anim.in_right);
				vfLiveCare.setOutAnimation(activity, R.anim.out_left);
			}
			else {

				vfLiveCare.setInAnimation(activity, R.anim.in_left);
				vfLiveCare.setOutAnimation(activity, R.anim.out_right);
			}
			if (vfLiveCare.getDisplayedChild() != selectedChild) {
				vfLiveCare.setDisplayedChild(selectedChild);
			}
		}

//		else if(index == 1){
//			tvCompletedCare.setBackgroundColor(getResources().getColor(R.color.theme_red));
//			tvCompletedCare.setTextColor(getResources().getColor(android.R.color.white));
//			tvLiveCare.setBackgroundColor(getResources().getColor(android.R.color.white));
//			tvLiveCare.setTextColor(getResources().getColor(R.color.theme_red));
//
//			selectedChild = 1;
//			if (selectedChild > vfLiveCare.getDisplayedChild()) {
//				vfLiveCare.setInAnimation(activity, R.anim.in_right);
//				vfLiveCare.setOutAnimation(activity, R.anim.out_left);
//			} else {
//				vfLiveCare.setInAnimation(activity, R.anim.in_left);
//				vfLiveCare.setOutAnimation(activity, R.anim.out_right);
//			}
//			if (vfLiveCare.getDisplayedChild() != selectedChild) {
//				vfLiveCare.setDisplayedChild(selectedChild);
//			}
//		}
	}
}
