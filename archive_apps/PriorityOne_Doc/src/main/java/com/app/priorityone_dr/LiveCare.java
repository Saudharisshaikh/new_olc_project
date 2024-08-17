package com.app.priorityone_dr;

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

import com.app.priorityone_dr.adapter.LiveCareAdapter;
import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.model.MyAppointmentsModel;
import com.app.priorityone_dr.model.ReportsModel;
import com.app.priorityone_dr.util.DATA;
import com.app.priorityone_dr.util.Database;
import com.app.priorityone_dr.util.GloabalSocket;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LiveCare extends BaseActivity implements ApiCallBack,GloabalSocket.SocketEmitterCallBack{
	
	AppCompatActivity activity;
	ListView lvLiveCare;
	TextView tvNoLiveCares;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;

	//BroadcastReceiver liveCareRefreshBroadcast;
	
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
		
		activity = LiveCare.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		apiCallBack = this;

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_NEW_PATIENT);

		gloabalSocket = new GloabalSocket(activity,this);

		lvLiveCare = (ListView) findViewById(R.id.lvLiveCare);
		tvNoLiveCares = (TextView) findViewById(R.id.tvNoLiveCares);
		
		lvLiveCare.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

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
					System.out.println("--broadcast recieved");
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
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_MY_LIVE_CHECKUPS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
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

						myAppointmentsAdapter = new LiveCareAdapter(activity);
						lvLiveCare.setAdapter(myAppointmentsAdapter);

					}
				}

				else {

					Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
				}


			} catch (JSONException e) {
				System.out.println("--online care exception in getlivecare patients: "+e);

				e.printStackTrace();
			}
		}
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
				
				System.out.println("item added");

				temp.name = DATA.allReports.get(i).name;
				temp.url = DATA.allReports.get(i).url;
				temp.patientID = DATA.allReports.get(i).patientID;

				DATA.allReportsFiltered.add(temp);	

				temp = null;
			}

		}

	}*/
}
