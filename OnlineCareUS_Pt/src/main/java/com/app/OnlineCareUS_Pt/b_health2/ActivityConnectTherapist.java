package com.app.OnlineCareUS_Pt.b_health2;

import static com.app.OnlineCareUS_Pt.Login.HOSP_ID_PREFS_KEY;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.OnlineCareUS_Pt.BaseActivity;
import com.app.OnlineCareUS_Pt.LiveCareWaitingArea;
import com.app.OnlineCareUS_Pt.Login;
import com.app.OnlineCareUS_Pt.MainActivityNew;
import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityConnectTherapist extends BaseActivity{


	Button btn_call911,btnImmCare,btnAppointment;

	boolean isForDirectLiveCare,isForDirectApptmnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_therapist);

		isForDirectLiveCare = getIntent().getBooleanExtra("isForDirectLiveCare", false);
		isForDirectApptmnt = getIntent().getBooleanExtra("isForDirectApptmnt", false);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Connect Therapist");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		btn_call911 = findViewById(R.id.btn_call911);
		btnImmCare = findViewById(R.id.btnImmCare);
		btnAppointment = findViewById(R.id.btnAppointment);

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.btn_call911:
					try {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(callIntent);
					}catch (Exception  e){e.printStackTrace();}
					break;
				case R.id.btnImmCare:
					if(sharedPrefsHelper.get(ActivityIcWaitingRoom.ISIN_ICROOM_PREFS_KEY, false)){
						openActivity.open(ActivityIcWaitingRoom.class, false);
					}else {
						ActivityConsent.flagNav = 1;
						openActivity.open(ActivityConsent.class, false);
					}
					break;
				case R.id.btnAppointment:
					ActivityConsent.flagNav = 2;
					openActivity.open(ActivityConsent.class, false);
					break;
				default:
					break;
			}
		};
		btn_call911.setOnClickListener(onClickListener);
		btnImmCare.setOnClickListener(onClickListener);
		btnAppointment.setOnClickListener(onClickListener);


		if(isForDirectApptmnt){
			btnAppointment.performClick();
		}else {
			checkClinicTimings();
		}
	}


	public void checkClinicTimings(){
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_CLINIC_TIMING+ prefs.getString(HOSP_ID_PREFS_KEY, ""),"post",null, apiCallBack, activity);
		apiManager.loadURL();
	}

	public void getTherapistDoctors(){
		RequestParams params = new RequestParams();
		params.put("is_online", "1");
		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_THERAPIST_DOC,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void checkPatientqueue() {
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_PATIENT_QUEUE+"/"+prefs.getString("id", "0"),"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.contains(ApiManager.CHECK_CLINIC_TIMING)){
			//{"status":"success","message":"Available"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				if(status.equalsIgnoreCase("success")){
					getTherapistDoctors();
				}else {
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(message)
							.setPositiveButton("Done", null)
							.create().show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPIST_DOC)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray doctors = jsonObject.getJSONArray("doctors");
				if(doctors.length() == 0){
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(jsonObject.optString("message"))
							.setPositiveButton("Done", null)
							.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							finish();
						}
					});
					alertDialog.show();
				}else {
					checkPatientqueue();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.CHECK_PATIENT_QUEUE+"/"+prefs.getString("id", "0"))){

			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success")) {


					sharedPrefsHelper.save(ActivityIcWaitingRoom.ISIN_ICROOM_PREFS_KEY, true);
					btnImmCare.setText("Already Applied For Immediate Care");
					btnImmCare.setBackgroundResource(R.drawable.btn_green);

					/*String message = jsonObject.getString("message");
					String livecare_id = jsonObject.getString("livecare_id");

					String docId_eLiveCare = jsonObject.getString("doctor_id");
					prefs.edit().putString("docId_eLiveCare", docId_eLiveCare).commit();

					*//*Intent intent1 = new Intent();
					intent1.setAction("LIVE_CARE_WAITING_TIMER");
					sendBroadcast(intent1);*//*

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("getLiveCareApptID", livecare_id);
					ed.putBoolean("livecareTimerRunning", true);
					ed.putString("doctor_queue_msg", message);
					ed.commit();

					customToast.showToast(message,0,1);
					openActivity.open(LiveCareWaitingArea.class, true);*/

				} else {
					sharedPrefsHelper.save(ActivityIcWaitingRoom.ISIN_ICROOM_PREFS_KEY, false);
					btnImmCare.setText("Immediate Care");
					btnImmCare.setBackgroundResource(R.drawable.btn_selector);
				}

				if(isForDirectLiveCare){
					btnImmCare.performClick();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
