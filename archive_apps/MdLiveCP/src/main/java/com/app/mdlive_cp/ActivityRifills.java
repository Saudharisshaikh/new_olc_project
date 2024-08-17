package com.app.mdlive_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.RefillsAdapter;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.RefillBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityRifills extends AppCompatActivity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	SharedPreferences prefs;
	CustomToast customToast;

	ListView lvRefills;
	TextView tvNoRefills;
	

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

		lvRefills = (ListView) findViewById(R.id.lvRefills);
		tvNoRefills = (TextView) findViewById(R.id.tvNoRefills);
		
		
		
		lvRefills.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				DATA.selectedRefillBean = refillBeans.get(pos);
				openActivity.open(ActivityRefillDetails.class, false);
				
			}
		});
	}//end oncreate

	ArrayList<RefillBean> refillBeans;
	public void refillRequests() {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		
		client.get(DATA.baseUrl+"/refillRequests/"+prefs.getString("id", ""), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--reaponce in refillRequests: "+content);

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

							String live_checkup_id = jsonArray.getJSONObject(i).getString("live_checkup_id");
							String patient_id = jsonArray.getJSONObject(i).getString("patient_id");

							String vitals = jsonArray.getJSONObject(i).getString("vitals");
							String diagnosis = jsonArray.getJSONObject(i).getString("diagnosis");

							temp = new RefillBean(id, parent_id, prescription_id, olc_message_id, surescript_message_id, pon, drug_descriptor_id, potency_unit_code, quantity, refill, start_date, end_date, directions, refill_status, drug_name, dateof, first_name, last_name, image,
									birthdate,residency,phone,gender,StoreName,PhonePrimary,live_checkup_id,patient_id,vitals,diagnosis);
							refillBeans.add(temp);
							temp = null;
						}

						lvRefills.setAdapter(new RefillsAdapter(activity, refillBeans));
						if (refillBeans.isEmpty()) {
							tvNoRefills.setVisibility(View.VISIBLE);
						} else {
							tvNoRefills.setVisibility(View.GONE);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: refillRequests, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail refillRequests: " +content);
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
