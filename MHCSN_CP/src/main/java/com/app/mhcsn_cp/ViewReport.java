package com.app.mhcsn_cp;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.adapters.ViewReportsAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.ReportsModel;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ViewReport extends AppCompatActivity {
	
	Activity activity;
	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	
	ListView lvViewReports;
	ViewReportsAdapter viewReportsAdapter;
	TextView tvNoReports;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_reports);
		
		activity = ViewReport.this;
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);

        getSupportActionBar().setTitle("Medical Reports");
		
		lvViewReports = (ListView) findViewById(R.id.lvViewReports);
		tvNoReports = (TextView) findViewById(R.id.tvNoReports);
		
		/*viewReportsAdapter = new ViewReportsAdapter(activity);
		lvViewReports.setAdapter(viewReportsAdapter);*/
		
		lvViewReports.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				DATA.selectedPtReportUrl = DATA.allReportsFiltered.get(position).url;
				
				OpenActivity op = new OpenActivity(activity);
				op.open(ViewReportFull.class, false);
			}
		});

		if(checkInternetConnection.isConnectedToInternet()){
			getPatientReports();
		}else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
		}
		
		/*if (DATA.allReportsFiltered.isEmpty()) {
			tvNoReports.setVisibility(View.VISIBLE);
			lvViewReports.setVisibility(View.GONE);
		}else {
			tvNoReports.setVisibility(View.GONE);
			lvViewReports.setVisibility(View.VISIBLE);
		}*/
	}

	public void getPatientReports() {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		final String reqURL = DATA.baseUrl+"getPatientReports/"+DATA.selectedDrIdForNurse+"/"+DATA.selectedUserCallId;

		DATA.print("-- URL in getPatientReports: "+reqURL);

		client.get(reqURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getPatientReports: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						JSONArray reports = jsonObject.getJSONArray("reports");
						DATA.allReportsFiltered = new ArrayList<ReportsModel>();
						ReportsModel reportsModel;
						for (int i = 0; i < reports.length(); i++) {
							reportsModel = new ReportsModel();

							reportsModel.name = reports.getJSONObject(i).getString("file_display_name");
							reportsModel.url = reports.getJSONObject(i).getString("report_name");
							reportsModel.patientID = reports.getJSONObject(i).getString("patient_id");

							DATA.allReportsFiltered.add(reportsModel);
							reportsModel = null;
						}
						viewReportsAdapter = new ViewReportsAdapter(activity);
						lvViewReports.setAdapter(viewReportsAdapter);

						if (DATA.allReportsFiltered.isEmpty()) {
							tvNoReports.setVisibility(View.VISIBLE);
							lvViewReports.setVisibility(View.GONE);
						}else {
							tvNoReports.setVisibility(View.GONE);
							lvViewReports.setVisibility(View.VISIBLE);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getPatientReports, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail getPatientReports: " +content);
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
