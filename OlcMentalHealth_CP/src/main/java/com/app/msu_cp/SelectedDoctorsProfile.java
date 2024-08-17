package com.app.msu_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.reliance.mdlive.ActivityPatientsByCpDoc;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class SelectedDoctorsProfile extends BaseActivity implements ApiCallBack{
	

	ImageView imgSelPtImage;
	TextView tvSelPtName;
	Button btnSelPtStartCheckup,btnSendMsg, btnLinkToDr,btnViewPatients,btnAddPatient;//btnLeaveDr

	OpenActivity openActivity;
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection connection;
	CustomToast customToast;
	ApiCallBack apiCallBack;



	boolean isFromMyDoctors = false;
	boolean isFromCompanyDoc = false;//for view patients button click
	
	@Override
	protected void onResume() {
		super.onResume();

        if (DATA.isFromDocToDoc) {
            getSupportActionBar().setTitle("Doctors Info");

            btnSelPtStartCheckup.setText("CALL THE DOCTOR");

			tvSelPtName.setText(DATA.selectedDrName);

			DATA.loadImageFromURL(DATA.selectedDrImage,R.drawable.icon_call_screen,imgSelPtImage);
		}
	}//end onResume
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_doc_profile);

		isFromMyDoctors = getIntent().getBooleanExtra("isFromMyDoctors",false);
		isFromCompanyDoc = getIntent().getBooleanExtra("isFromCompanyDoc", false);
		
		activity = SelectedDoctorsProfile.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, MODE_PRIVATE);
		connection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);
		apiCallBack = this;



		btnSelPtStartCheckup = (Button) findViewById(R.id.btnSelPtStartCheckup);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnLinkToDr = (Button) findViewById(R.id.btnLinkToDr);
		btnViewPatients = (Button) findViewById(R.id.btnViewPatients);
		btnAddPatient = (Button) findViewById(R.id.btnAddPatient);

		tvSelPtName = (TextView) findViewById(R.id.tvSelPtName);
		imgSelPtImage = (ImageView)findViewById(R.id.imgSelPtImage);

		//int v = isFromCompanyDoc ? View.GONE : View.VISIBLE;
		//btnAddPatient.setVisibility(v);

		btnSelPtStartCheckup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(DATA.isFromDocToDoc){
					if(DATA.selectedDoctorsModel.is_online.equals("1")){
						DATA.incomingCall = false;
						Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
						myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(myIntent);
						finish();
					}else {
						new AlertDialog.Builder(activity).setTitle("Doctor Offline")
								.setMessage("Doctor is offline and can't be connected right now. Leave a message instead ?")
								.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										new GloabalMethods(activity).initMsgDialog();
									}
								}).setNegativeButton("Cancel",null).show();
					}

				}else {}

			}
		});
		btnSendMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).initMsgDialog();
			}
		});

		btnLinkToDr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (connection.isConnectedToInternet()){
					addDoctorList(DATA.selectedDoctorsModel.id);
				}else {
					Toast.makeText(activity,DATA.NO_NETWORK_MESSAGE,Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnViewPatients.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFromCompanyDoc){
					openActivity.open(ActivityPatientsByCpDoc.class, true);
				}else {
					Intent intent = new Intent(activity,ActivityTCM.class);
					//intent.putExtra("doctor_id",DATA.selectedDoctorsModel.id);
					startActivity(intent);
					finish();
				}

			}
		});
		btnAddPatient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ActivityAddNewPatient.class);
				intent.putExtra("isFromMyDoc", true);
				startActivity(intent);
			}
		});
		/*btnLeaveDr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(activity)
						.setMessage("Do you want to unfollow this doctor?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (connection.isConnectedToInternet()){
									removeDoctor(DATA.selectedDoctorsModel.id);
								}else {
									customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
								}
							}
						}).setNegativeButton("No",null).show();
			}
		});*/
		
	}//oncreate

	public void addDoctorList(String drId) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("my_id", prefs.getString("id", "0"));
		params.put("doctor_id", drId);

		DATA.print("-- params in addDoctorList: "+params.toString());

		client.post(DATA.baseUrl+"addDoctorList", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in addDoctorList"+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")){
							customToast.showToast("You are added with the doctor",0,1);

							btnViewPatients.setVisibility(View.VISIBLE);
							btnSendMsg.setVisibility(View.VISIBLE);
							btnSelPtStartCheckup.setVisibility(View.VISIBLE);
							//btnLeaveDr.setVisibility(View.VISIBLE);
							btnLinkToDr.setVisibility(View.GONE);
						}else {

						}
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: addDoctorList, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail addDoctorList " +content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}

	public void removeDoctor(String drId) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("my_id", prefs.getString("id", "0"));
		params.put("doctor_id", drId);

		DATA.print("-- params in removeDoctor: "+params.toString());

		client.post(DATA.baseUrl+"removeDoctor", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					//{"success":1,"message":"Removed Successfully"}
					DATA.print("--reaponce in removeDoctor "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("message")){
							customToast.showToast(jsonObject.getString("message"),0,1);

							btnViewPatients.setVisibility(View.GONE);
							btnSendMsg.setVisibility(View.GONE);
							btnSelPtStartCheckup.setVisibility(View.GONE);
							//btnLeaveDr.setVisibility(View.GONE);
							btnLinkToDr.setVisibility(View.VISIBLE);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: removeDoctor, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail removeDoctor " +content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}




	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (! DATA.isFromDocToDoc){
			getMenuInflater().inflate(R.menu.menu_add_new, menu);
			menu.getItem(0).setTitle("Virtual Visit");
		}

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {

			openActivity.open(GetLiveCareForm.class,false);

		}else if(item.getItemId() == android.R.id.home){
			onBackPressed();
			return true;//return true back activity state maintains if false back activity oncreate called
		}
		return super.onOptionsItemSelected(item);
	}


}
