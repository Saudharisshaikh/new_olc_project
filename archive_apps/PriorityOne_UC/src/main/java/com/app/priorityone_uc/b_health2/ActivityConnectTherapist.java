package com.app.priorityone_uc.b_health2;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.priorityone_uc.BaseActivity;
import com.app.priorityone_uc.Login;
import com.app.priorityone_uc.R;
import com.app.priorityone_uc.api.ApiManager;
import com.app.priorityone_uc.util.DATA;

import org.json.JSONException;
import org.json.JSONObject;

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
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_CLINIC_TIMING+ Login.HOSPITAL_ID_EMCURA,"post",null, apiCallBack, activity);
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
					checkPatientqueue();
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
