package com.app.msu_cp.reliance.mdlive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.ActivityTcmDetails;
import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.MyAppointmentsModel;
import com.app.msu_cp.reliance.CompanyPatientBean;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityPatientsByCpDoc extends BaseActivity implements GloabalSocket.SocketEmitterCallBack {


	ListView lvCompanyPatients;
	TextView tvNoData;
	SwipeRefreshLayout srPatients;
	EditText etSearchDoc;


	GloabalSocket gloabalSocket;
	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patients_by_cpdoc);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("My Patients");
		}

		gloabalSocket = new GloabalSocket(activity,this);
		lvCompanyPatients = findViewById(R.id.lvCompanyPatients);
		tvNoData = findViewById(R.id.tvNoData);
		etSearchDoc = findViewById(R.id.etSearchDoc);

		etSearchDoc.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(companyPatientAdapter2 != null){companyPatientAdapter2.filter(s.toString());}
			}
		});


		lvCompanyPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				DATA.selectedUserQbid = "";
				DATA.selectedUserCallId = companyPatientBeans.get(position).patient_id;
				DATA.selectedUserCallName = companyPatientBeans.get(position).first_name+" "+companyPatientBeans.get(position).last_name;
				DATA.selectedUserCallSympTom = "";
				DATA.selectedUserCallCondition = "";
				DATA.selectedUserCallDescription = "";
				DATA.selectedUserAppntID = "";

				DATA.selectedUserLatitude =  0;
				DATA.selectedUserLongitude =  0;

				DATA.selectedUserCallImage = companyPatientBeans.get(position).image;

				//filterReports(DATA.selectedUserCallId);
				DATA.allReportsFiltered = new ArrayList<>();

				DATA.isFromDocToDoc = false;

				DATA.selectedRefferedLiveCare = new MyAppointmentsModel();
				DATA.selectedRefferedLiveCare.id = companyPatientBeans.get(position).patient_id;
				DATA.selectedRefferedLiveCare.is_online = companyPatientBeans.get(position).is_online;
				DATA.selectedRefferedLiveCare.first_name = companyPatientBeans.get(position).first_name;
				DATA.selectedRefferedLiveCare.last_name = companyPatientBeans.get(position).last_name;
				DATA.selectedRefferedLiveCare.patient_phone = companyPatientBeans.get(position).phone;

				Intent i = new Intent(activity, ActivityTcmDetails.class);
				startActivity(i);
				//activity.finish();
			}
		});

		//======================swip to refresh==================================
		//mySwipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh);

		srPatients = findViewById(R.id.srPatients);
		int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
		srPatients.setColorSchemeColors(colorsArr);
		srPatients.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						if(!checkInternetConnection.isConnectedToInternet()){
							srPatients.setRefreshing(false);
							customSnakeBar.showToast(DATA.NO_NETWORK_MESSAGE);
						}else {
							ApiManager.shouldShowPD = false;
							loadPatientsByCompany();
						}
					}
				}
		);
		//======================swip to refresh ends=============================


		loadPatientsByCompany();
	}




	public void loadPatientsByCompany(){
		RequestParams params = new RequestParams();
		params.put("company_id", ActivityCompanyDoctors.selectedCompanyID);
		params.put("cpid", prefs.getString("id", ""));
		params.put("doctor_id", DATA.selectedDrId);

		ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENTS_BY_CP_DOC,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	//ArrayList<CompanyBean> companyBeans;
	ArrayList<CompanyPatientBean> companyPatientBeans;
	//public static String selectedCompanyID = "";
	CompanyPatientAdapter2 companyPatientAdapter2;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENTS_BY_CP_DOC)){
			srPatients.setRefreshing(false);
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray patients = jsonObject.getJSONArray("patients");
				Type listType = new TypeToken<ArrayList<CompanyPatientBean>>() {}.getType();
				companyPatientBeans = new Gson().fromJson(patients+"", listType);

				if(companyPatientBeans != null){
					int vis = companyPatientBeans.isEmpty() ? View.VISIBLE : View.GONE;
					tvNoData.setVisibility(vis);
					companyPatientAdapter2 = new CompanyPatientAdapter2(activity, companyPatientBeans);
					lvCompanyPatients.setAdapter(companyPatientAdapter2);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}




	@Override
	public void onSocketCallBack(String emitterResponse) {
		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("patient")){
				for (int i = 0; i < companyPatientBeans.size(); i++) {
					if(companyPatientBeans.get(i).patient_id.equalsIgnoreCase(id)){
						if(status.equalsIgnoreCase("login")){
							companyPatientBeans.get(i).is_online = "1";
						}else if(status.equalsIgnoreCase("logout")){
							companyPatientBeans.get(i).is_online = "0";
						}
					}
				}
				companyPatientAdapter2.notifyDataSetChanged();

				if(DATA.selectedRefferedLiveCare != null && DATA.selectedRefferedLiveCare.id.equalsIgnoreCase(id)){
					if(status.equalsIgnoreCase("login")){
						DATA.selectedRefferedLiveCare.is_online = "1";
					}else if(status.equalsIgnoreCase("logout")){
						DATA.selectedRefferedLiveCare .is_online = "0";
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
