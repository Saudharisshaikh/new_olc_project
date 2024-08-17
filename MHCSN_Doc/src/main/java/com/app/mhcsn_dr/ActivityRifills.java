package com.app.mhcsn_dr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.mhcsn_dr.adapter.RefillsAdapter;
import com.app.mhcsn_dr.api.ApiManager;
import com.app.mhcsn_dr.model.RefillBean;
import com.app.mhcsn_dr.util.CheckInternetConnection;
import com.app.mhcsn_dr.util.CustomToast;
import com.app.mhcsn_dr.util.DATA;
import com.app.mhcsn_dr.util.Database;
import com.app.mhcsn_dr.util.GloabalMethods;
import com.app.mhcsn_dr.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityRifills extends BaseActivity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	SharedPreferences prefs;
	CustomToast customToast;

	ListView lvRefills;
	TextView tvNoRefills,tvNoUnknownRefills;
	
	ViewFlipper vfRefills;
	TextView tvRefills,tvUnknownRefills;
	ListView lvUnknownRefills;
	int selectedChild = 0;
	

	@Override
	protected void onPostResume() {
		if (checkInternetConnection.isConnectedToInternet()) {
			refillRequests();
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}
		super.onPostResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rifills);

		activity = ActivityRifills.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_REFILL);

		lvRefills = (ListView) findViewById(R.id.lvRefills);
		tvNoRefills = (TextView) findViewById(R.id.tvNoRefills);
		tvNoUnknownRefills = (TextView) findViewById(R.id.tvNoUnknownRefills);
		
		 vfRefills = (ViewFlipper) findViewById(R.id.vfRefills);
		 tvRefills = (TextView) findViewById(R.id.tvRefills);
		 tvUnknownRefills = (TextView) findViewById(R.id.tvUnknownRefills);
		 lvUnknownRefills = (ListView) findViewById(R.id.lvUnknownRefills);
		
		
		
		lvRefills.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				DATA.selectedRefillBean = refillBeans.get(pos);
				openActivity.open(ActivityRefillDetails.class, false);
				
			}
		});
		
		lvUnknownRefills.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				DATA.selectedRefillBean = unknownRefillBeans.get(pos);
				//openActivity.open(ActivityRefillDetails.class, false);
				Intent intent = new Intent(activity, ActivityRefillDetails.class);
				intent.putExtra("isFromUnknownRefills", true);
				startActivity(intent);
				
			}
		});
		
		tvRefills.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				tvRefills.setBackgroundColor(getResources().getColor(R.color.theme_red));
				tvRefills.setTextColor(getResources().getColor(android.R.color.white));
				tvUnknownRefills.setBackgroundColor(getResources().getColor(android.R.color.white));
				tvUnknownRefills.setTextColor(getResources().getColor(R.color.theme_red));
				
				selectedChild = 0;
				if (selectedChild > vfRefills.getDisplayedChild()) {

					vfRefills.setInAnimation(activity, R.anim.in_right);
					vfRefills.setOutAnimation(activity, R.anim.out_left);
				} else {

					vfRefills.setInAnimation(activity, R.anim.in_left);
					vfRefills.setOutAnimation(activity, R.anim.out_right);
				}
				if (vfRefills.getDisplayedChild() != selectedChild) {
					vfRefills.setDisplayedChild(selectedChild);
				}
			
			}
		});
		tvUnknownRefills.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				tvRefills.setBackgroundColor(getResources().getColor(android.R.color.white));
				tvRefills.setTextColor(getResources().getColor(R.color.theme_red));
				tvUnknownRefills.setBackgroundColor(getResources().getColor(R.color.theme_red));
				tvUnknownRefills.setTextColor(getResources().getColor(android.R.color.white));
				
				
				selectedChild = 1;
				if (selectedChild > vfRefills.getDisplayedChild()) {
					vfRefills.setInAnimation(activity, R.anim.in_right);
					vfRefills.setOutAnimation(activity, R.anim.out_left);
				} else {
					vfRefills.setInAnimation(activity, R.anim.in_left);
					vfRefills.setOutAnimation(activity, R.anim.out_right);
				}
				if (vfRefills.getDisplayedChild() != selectedChild) {
					vfRefills.setDisplayedChild(selectedChild);
				}
			
			}
		});
	}//end oncreate

	ArrayList<RefillBean> refillBeans,unknownRefillBeans;;
	public void refillRequests() {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		String reqURL = DATA.baseUrl+"/refillRequests/"+prefs.getString("id", "");

		DATA.print("-- Request : "+reqURL);
		//DATA.print("-- params : "+params.toString());
		
		client.get(reqURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in refillRequests: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);

						JSONArray denialReasonsAndCodes = jsonObject.getJSONArray("denialReasonsAndCodes");
						prefs.edit().putString("denialReasonsAndCodes", denialReasonsAndCodes+"").commit();

						JSONArray jsonArray = jsonObject.getJSONArray("refills");
						refillBeans = new ArrayList<>();
						RefillBean temp;
						for (int i = 0; i < jsonArray.length(); i++) {
							String id = jsonArray.getJSONObject(i).getString("id");
							String parent_id = jsonArray.getJSONObject(i).getString("parent_id");
							String prescription_id = jsonArray.getJSONObject(i).getString("prescription_id");
							String olc_message_id = jsonArray.getJSONObject(i).getString("olc_message_id");
							String surescript_message_id = jsonArray.getJSONObject(i).getString("surescript_message_id");
							String pon = jsonArray.getJSONObject(i).getString("pon");
							String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
							String potency_unit_code = jsonArray.getJSONObject(i).getString("potency_unit_code");
							String quantity = jsonArray.getJSONObject(i).getString("quantity");
							String refill = jsonArray.getJSONObject(i).getString("refill");
							String start_date = jsonArray.getJSONObject(i).getString("start_date");
							String end_date = jsonArray.getJSONObject(i).getString("end_date");
							String directions = jsonArray.getJSONObject(i).getString("directions");
							String refill_status = jsonArray.getJSONObject(i).getString("refill_status");
							String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
							String dateof = jsonArray.getJSONObject(i).getString("dateof");
							String first_name = jsonArray.getJSONObject(i).getString("first_name");
							String last_name = jsonArray.getJSONObject(i).getString("last_name");
							String image = jsonArray.getJSONObject(i).getString("image");

							String birthdate = jsonArray.getJSONObject(i).getString("birthdate");
							String residency = jsonArray.getJSONObject(i).getString("residency");
							String phone = jsonArray.getJSONObject(i).getString("phone");
							String gender = jsonArray.getJSONObject(i).getString("gender");
							String StoreName = jsonArray.getJSONObject(i).getString("StoreName");
							String PhonePrimary = jsonArray.getJSONObject(i).getString("PhonePrimary");

							//String live_checkup_id = jsonArray.getJSONObject(i).getString("live_checkup_id");
							String patient_id = jsonArray.getJSONObject(i).getString("patient_id");

							String vitals = jsonArray.getJSONObject(i).getString("vitals");
							String diagnosis = jsonArray.getJSONObject(i).getString("diagnosis");

							temp = new RefillBean(id, parent_id, prescription_id, olc_message_id, surescript_message_id, pon, drug_descriptor_id, potency_unit_code, quantity, refill, start_date, end_date, directions, refill_status, drug_name, dateof, first_name, last_name, image,
									birthdate,residency,phone,gender,StoreName,PhonePrimary,patient_id,vitals,diagnosis);
							refillBeans.add(temp);
							temp = null;
						}

						lvRefills.setAdapter(new RefillsAdapter(activity, refillBeans));


						if (refillBeans.isEmpty()) {
							tvNoRefills.setVisibility(View.VISIBLE);
						} else {
							tvNoRefills.setVisibility(View.GONE);
						}

						JSONArray unknownRefills = jsonObject.getJSONArray("unknownRefills");
						unknownRefillBeans = new ArrayList<>();
						RefillBean temp1;
						for (int i = 0; i < unknownRefills.length(); i++) {
							String id = unknownRefills.getJSONObject(i).getString("id");
							String parent_id = unknownRefills.getJSONObject(i).getString("parent_id");
							String prescription_id = unknownRefills.getJSONObject(i).getString("prescription_id");
							String olc_message_id = unknownRefills.getJSONObject(i).getString("olc_message_id");
							String surescript_message_id = unknownRefills.getJSONObject(i).getString("surescript_message_id");
							String pon = unknownRefills.getJSONObject(i).getString("pon");
							String drug_descriptor_id = unknownRefills.getJSONObject(i).getString("drug_descriptor_id");
							String potency_unit_code = unknownRefills.getJSONObject(i).getString("potency_unit_code");
							String quantity = unknownRefills.getJSONObject(i).getString("quantity");
							String refill = unknownRefills.getJSONObject(i).getString("refill");
							String start_date = unknownRefills.getJSONObject(i).getString("start_date");
							String end_date = unknownRefills.getJSONObject(i).getString("end_date");
							String directions = unknownRefills.getJSONObject(i).getString("directions");
							String refill_status = unknownRefills.getJSONObject(i).getString("refill_status");
							String drug_name = unknownRefills.getJSONObject(i).getString("drug_name");
							String dateof = unknownRefills.getJSONObject(i).getString("dateof");
							String first_name = unknownRefills.getJSONObject(i).getString("first_name");
							String last_name = unknownRefills.getJSONObject(i).getString("last_name");
							String image = unknownRefills.getJSONObject(i).getString("image");

							if (image.isEmpty()) {
								image = "no image";
							}

							String birthdate = unknownRefills.getJSONObject(i).getString("birthdate");
							String residency = unknownRefills.getJSONObject(i).getString("residency");
							String phone = unknownRefills.getJSONObject(i).getString("phone");
							String gender = unknownRefills.getJSONObject(i).getString("gender");
							String StoreName = unknownRefills.getJSONObject(i).getString("StoreName");
							String PhonePrimary = unknownRefills.getJSONObject(i).getString("PhonePrimary");

							//String live_checkup_id = unknownRefills.getJSONObject(i).getString("live_checkup_id");
							String patient_id = "";//unknownRefills.getJSONObject(i).getString("patient_id");

							String vitals = unknownRefills.getJSONObject(i).getString("vitals");
							String diagnosis = unknownRefills.getJSONObject(i).getString("diagnosis");

							temp1 = new RefillBean(id, parent_id, prescription_id, olc_message_id, surescript_message_id, pon, drug_descriptor_id, potency_unit_code, quantity, refill, start_date, end_date, directions, refill_status, drug_name, dateof, first_name, last_name, image,
									birthdate,residency,phone,gender,StoreName,PhonePrimary,patient_id,vitals,diagnosis);
							unknownRefillBeans.add(temp1);
							temp = null;
						}

						lvUnknownRefills.setAdapter(new RefillsAdapter(activity, unknownRefillBeans));

						if (unknownRefillBeans.isEmpty()) {
							tvNoUnknownRefills.setVisibility(View.VISIBLE);
						} else {
							tvNoUnknownRefills.setVisibility(View.GONE);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
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
	

}
