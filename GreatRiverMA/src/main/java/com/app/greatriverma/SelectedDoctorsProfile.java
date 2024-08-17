package com.app.greatriverma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.greatriverma.api.ApiManager;
import com.app.greatriverma.util.CheckInternetConnection;
import com.app.greatriverma.util.CustomToast;
import com.app.greatriverma.util.DATA;
import com.app.greatriverma.util.GloabalMethods;
import com.app.greatriverma.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class SelectedDoctorsProfile extends BaseActivity{
	


	OpenActivity openActivity;
	Activity activity;
	SharedPreferences prefs;
	CustomToast customToast;
	CheckInternetConnection connection;

	TextView tvSelPtName;
	ImageView imgSelPtImage;
	Button btnSendMsg,btnSelPtStartCheckup;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(DATA.isFromDocToDoc) {
			getSupportActionBar().setTitle("Doctor Details");
			if (DoctorsList.isFromPCPRefer) {
				btnSelPtStartCheckup.setText("Refer to the doctor");
			} else {
				btnSelPtStartCheckup.setText("Call The Doctor");
			}
		}
	}//end onResume
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_livecare_patient_details);
		
		activity = SelectedDoctorsProfile.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		connection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);

		tvSelPtName = (TextView) findViewById(R.id.tvSelPtName);
		imgSelPtImage = (ImageView) findViewById(R.id.imgSelPtImage);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnSelPtStartCheckup = (Button) findViewById(R.id.btnSelPtStartCheckup);

		if(DATA.isFromDocToDoc) {
			tvSelPtName.setText(DATA.selectedDrName);
			DATA.loadImageFromURL(DATA.selectedDrImage, R.drawable.icon_call_screen, imgSelPtImage);
		}

		
		btnSelPtStartCheckup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			//	DATA.isOutgoingCall = true;

//				openActivity.open(VideoCallScreen.class, false);
				
//				startActivity(new Intent(SelectedDoctorsProfile.this,AfterCallDialog.class));
//				finish();
				/*if(DATA.isConfirence){
					callUser1();
					
				}else{
				Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(myIntent);
				finish();	
						
				}*/
				if (DoctorsList.isFromPCPRefer) {
					new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
					setMessage("Are you sure? You want to refer the patient "+DATA.selectedUserCallName+" to specialist "+DATA.selectedDrName).
					setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (connection.isConnectedToInternet()) {
								referlivecareToDoctor(DATA.selectedUserAppntID, DATA.selectedDrId);
							} else {
								customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
							}
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					}).show();
				}else if(DATA.isFromDocToDoc){
					DATA.incomingCall = false;
					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(myIntent);
					finish();
				}
			}
		});
		

		btnSendMsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).initMsgDialog();
				
			}
		});
	}

	public String getDeviceTimeZone() {
		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		Log.d("Time zone","="+tz.getDisplayName());	
		DATA.print("--time zone "+tz.getDisplayName());
		DATA.print("--time zone from util "+TimeZone.getDefault());
		DATA.print("--time zone id from util "+TimeZone.getDefault().getID());
		
		return TimeZone.getDefault().getID();
	}
	
	
	public void referlivecareToDoctor(String live_checkup_id,String doctor_id) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("live_checkup_id", live_checkup_id);
		params.put("doctor_id", doctor_id);

		String reqURL = DATA.baseUrl+"referlivecareToDoctor";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in referlivecareToDoctor "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						String status = jsonObject.getString("status");
						if (status.equalsIgnoreCase("success")) {
							customToast.showToast("Patient has been referd to "+DATA.selectedDrName , 0, 1);

							Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//This will clear all the activities on top of home.
							startActivity(intent);
						} else {
							customToast.showToast("Something went wrong !", 0, 1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}







	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.contains(ApiManager.PATIENT_DETAIL)){}
	}
}
