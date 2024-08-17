package com.app.onlinecare_pk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.onlinecare_pk.adapter.SelecReportsAdapter;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.model.ReportsModel;
import com.app.onlinecare_pk.util.CheckInternetConnection;
import com.app.onlinecare_pk.util.CustomToast;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SelectReports extends AppCompatActivity {
	
	ListView lvSelectReports;
	SelecReportsAdapter selecReportsAdapter;
	CheckInternetConnection checkInternetConnection;
	Button btnSelectReprtOK;

	JSONObject jsonObject;
	AsyncHttpClient client;
	String msg, status;
	ProgressDialog pd;
	SharedPreferences prefs;
	StringBuilder s;
	TextView tvNoReport;
	CustomToast customToast;

	Activity activity;
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		DATA.NumOfReprtsSelected = 0;
		DATA.isReprtSelected = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		DATA.NumOfReprtsSelected = 0;
		
		if(checkInternetConnection.isConnectedToInternet()) {

			getReprtsCall();
			
		}
		else {
			customToast.showToast("No internet connection. Can not get reports.", 0, 0);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_reports);
		
		activity = SelectReports.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		lvSelectReports = (ListView) findViewById(R.id.lvSelectReports);
		btnSelectReprtOK = (Button) findViewById(R.id.btnSelectReprtOK);
		tvNoReport = (TextView) findViewById(R.id.tvNoReport);
		//tvNoReport.setVisibility(View.GONE);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
			}else{
				pd = new ProgressDialog(activity );
			}
		
		pd.setMessage("Searching your reports...    ");
		pd.setCanceledOnTouchOutside(false);
		
		s = new StringBuilder(200);
		
		customToast = new CustomToast(activity);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		btnSelectReprtOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(DATA.allReports != null ) {

					for (int i = 0; i < DATA.allReports.size(); i++) {
						
						if(DATA.allReports.get(i).status.equals("1")) {

							s.append(DATA.allReports.get(i).id);
							s.append("-");
							
							DATA.NumOfReprtsSelected++;
						}
					}
						
						if(s.toString().length() != 0) {
							
							DATA.isReprtSelected = true;
							DATA.selectedReportIdsForApntmt = s.toString();
							DATA.selectedReportIdsForApntmt = DATA.selectedReportIdsForApntmt.substring(0, DATA.selectedReportIdsForApntmt.length()-1);

						}
						else {

							customToast.showToast("No report selected", 0, 0);

							DATA.isReprtSelected = false;
						}
					
				}
			
//				customToast.showToast(DATA.selectedReportIdsForApntmt, 0, 0);
				finish();

				
			}
		});

	}
	
	private void getReprtsCall() {
		
		pd.show();

		client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		
		params.put("patient_id", prefs.getString("id", ""));
		//params.put("sub_patient_id", prefs.getString("subPatientID", ""));//this causes issue no folder shown 8/11/2018

		DATA.print("--URL: "+DATA.baseUrl+"getReports");
		DATA.print("--params "+params.toString());

		client.get(DATA.baseUrl+"getReports", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in api getReports : "+content);

					try {

						jsonObject = new JSONObject(content);

						status = jsonObject.getString("status");
						//msg = jsonObject.getString("msg");


						if(status.equals("success"))
						{

							String reprtsArrayStr = jsonObject.getString("files");

							JSONArray reportsArray = new JSONArray(reprtsArrayStr);

							DATA.allReports = new ArrayList<ReportsModel>();

							ReportsModel temp;

							for (int i = 0; i < reportsArray.length(); i++) {

								temp = new ReportsModel();

								temp.id = reportsArray.getJSONObject(i).getString("id");
								temp.name = reportsArray.getJSONObject(i).getString("file_display_name");
								temp.date = reportsArray.getJSONObject(i).getString("dateof");
								temp.report_url = reportsArray.getJSONObject(i).getString("report_url");

								DATA.allReports.add(temp);

								temp = null;

							}


							selecReportsAdapter = new SelecReportsAdapter(activity);
							lvSelectReports.setAdapter(selecReportsAdapter);
						} else {
							tvNoReport.setVisibility(View.VISIBLE);
							customToast.showToast("No report found", 0, 0);
						}


					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getReports, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- getReports on failure : "+content);
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
