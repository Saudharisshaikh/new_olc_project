package com.app.emcuradr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.adapter.LiveCareAdapter;
import com.app.emcuradr.adapter.PatientMedicalHistoryAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.MyAppointmentsModel;
import com.app.emcuradr.model.PatientMedicalHistoryBean;
import com.app.emcuradr.model.ReportsModel;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.Database;
import com.app.emcuradr.util.GloabalSocket;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	String userID;

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

		recyclerView = findViewById(R.id.rvLiveCare);
		noLayout = findViewById(R.id.no_layout);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(adapter);

		getMedHistory();

	}//oncreate


	public void getMedHistory(){

		RequestParams params = new RequestParams();
		//params.put("doctor_id", prefs.getString("id", ""));
		//String patId = SharedPrefsHelper.getInstance().get("patId");
		//DATA.print(patId);
		userID = DATA.selectedUserCallId.equals("614") ?"42946":DATA.selectedUserCallId;
		ApiManager.shouldShowPD = true;
		apiRoute = ApiManager.GET_PRES_HISTORY+userID+"?"+"doctor_id="+prefs.getString("id","");
		ApiManager apiManager = new ApiManager(apiRoute,"get",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String status, String apiName, String content) {


		if(apiName.equals(apiRoute)){
			try {

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
				//customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}


}
