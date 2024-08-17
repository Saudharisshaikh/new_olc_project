package com.app.fivestaruc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fivestaruc.adapter.PatientPrescriptionAdapter;
import com.app.fivestaruc.api.ApiCallBack;
import com.app.fivestaruc.api.ApiManager;
import com.app.fivestaruc.model.ConversationBean;
import com.app.fivestaruc.model.MyPrescriptionModel;
import com.app.fivestaruc.util.CheckInternetConnection;
import com.app.fivestaruc.util.DATA;
import com.app.fivestaruc.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyPrescriptionActivity extends BaseActivity{

	Activity activity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;

	LinearLayout noPrescriptionLayout;

	RecyclerView prescriptionRv;
	TextView tvNoConv;
	
	ListView lvConversations;
	ArrayList<ConversationBean> conversationBeans;

	PatientPrescriptionAdapter patientPrescriptionAdapter;

	ArrayList<MyPrescriptionModel> myPrescriptionModelArrayList;

	@Override
	protected void onResume() {

		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myprescription);

		sharedPrefsHelper.save("pres_badge_count", 0);
		
		activity = MyPrescriptionActivity.this;
		apiCallBack = this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);

		noPrescriptionLayout = findViewById(R.id.no_prescription_layout);
		prescriptionRv = findViewById(R.id.prescription_rv);
		prescriptionRv.setLayoutManager(new LinearLayoutManager(this));
		myPrescriptionModelArrayList = new ArrayList<>();
		patientPrescriptionAdapter = new PatientPrescriptionAdapter(this,myPrescriptionModelArrayList);
		prescriptionRv.setAdapter(patientPrescriptionAdapter);

		getPrescriptions();

		
	}//oncreate



	public void getPrescriptions() {
		RequestParams params = new RequestParams();
		params.put("type", "patient");
		ApiManager apiManager = new ApiManager(ApiManager.MY_PATIENT_PRESCRIPTION,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {


		if(apiName.equalsIgnoreCase(ApiManager.MY_PATIENT_PRESCRIPTION)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					noPrescriptionLayout.setVisibility(View.VISIBLE);
				} else {
					noPrescriptionLayout.setVisibility(View.GONE);
				}


				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					String id = object.getString("id");
					String  patient_id= object.getString("patient_id");
					String  doctor_id= object.getString("doctor_id");
					String  vitals= object.getString("vitals");
					String  diagnosis= object.getString("diagnosis");
					String  treatment= object.getString("treatment");
					String  dateof= object.getString("dateof");
					String  send_education = object.getString("send_education_to_patient");
					String  first_name= object.getString("first_name");
					String  last_name= object.getString("last_name");


					MyPrescriptionModel myPrescriptionModel = new MyPrescriptionModel();
					myPrescriptionModel.setId(id);
					myPrescriptionModel.setPatientId(patient_id);
					myPrescriptionModel.setDoctorId(doctor_id);
					myPrescriptionModel.setVitals(vitals);
					myPrescriptionModel.setDiagnosis(diagnosis);
					myPrescriptionModel.setTreatment(treatment);
					myPrescriptionModel.setDateof(dateof);
					myPrescriptionModel.setSendEducationToPatient(send_education);
					myPrescriptionModel.setFirstName(first_name);
					myPrescriptionModel.setLastName(last_name);

					myPrescriptionModelArrayList.add(myPrescriptionModel);
				}
				patientPrescriptionAdapter.notifyDataSetChanged();


				//
	}
			catch (Exception e){
				e.printStackTrace();
			}
		}
}
}
