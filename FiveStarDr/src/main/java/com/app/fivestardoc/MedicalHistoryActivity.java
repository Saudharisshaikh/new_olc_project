package com.app.fivestardoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fivestardoc.adapter.PatientMedicalHistoryAdapter;
import com.app.fivestardoc.api.ApiCallBack;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.model.PatientMedicalHistoryBean;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.SharedPrefsHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedicalHistoryActivity extends BaseActivity{
	
	AppCompatActivity activity;
	ListView lvLiveCare;
	TextView tvNoLiveCares;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;

	RecyclerView recyclerView;

	PatientMedicalHistoryAdapter adapter;
	ArrayList<PatientMedicalHistoryBean> beanArrayList;

	String apiRoute = "";

	public  int count = 0;

	LinearLayout noLayout;

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medical_history);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Prescription History");
		}
		
		activity = MedicalHistoryActivity.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		apiCallBack = this;

		beanArrayList = new ArrayList<>();
		adapter = new PatientMedicalHistoryAdapter(beanArrayList);
		noLayout = findViewById(R.id.no_layout);
		recyclerView = findViewById(R.id.rvLiveCare);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		getMedHistory();

	}//oncreate


	public void getMedHistory(){

		RequestParams params = new RequestParams();
		//params.put("doctor_id", prefs.getString("id", ""));
		String patId = SharedPrefsHelper.getInstance().get("patId");
		DATA.print(patId);
		ApiManager.shouldShowPD = true;
		apiRoute = ApiManager.GET_PRES_HISTORY+patId+"?"+"doctor_id="+prefs.getString("id","");
		ApiManager apiManager = new ApiManager(apiRoute,"get",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String status, String apiName, String content) {


		if(apiName.equals(apiRoute)){
			try {
				DATA.print("data");
				beanArrayList.clear();

				JSONArray jsonArray = new JSONArray(content);

				if(jsonArray.length()>0){

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);

					String drug = jsonObject.getString("Drug");
					String prescriberName = jsonObject.getString("PrescriberName");
					String quantity = jsonObject.getString("Quantity");
					String daysSupply = jsonObject.getString("DaysSupply");
					String lastFillDate = jsonObject.getString("LastFillDate");
					String pharmacy = jsonObject.getString("Pharmacy");

					// Ignoring the Note field for simplicity

					PatientMedicalHistoryBean patientMedicalHistory = new PatientMedicalHistoryBean(
							drug, prescriberName, quantity, daysSupply, lastFillDate, pharmacy);
					beanArrayList.add(patientMedicalHistory);
				}
				adapter.notifyDataSetChanged();

				}

				else {
					DATA.print("zeroIndex");
					noLayout.setVisibility(View.VISIBLE);
					if(count ==0){
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								++count;
								getMedHistory();

							}
						},3000);
					}
				}

			} catch (Exception e) {
				DATA.print("--online care exception in getlivecare patients: "+e);

				noLayout.setVisibility(View.VISIBLE);
				e.printStackTrace();
			}
		}
	}


}
