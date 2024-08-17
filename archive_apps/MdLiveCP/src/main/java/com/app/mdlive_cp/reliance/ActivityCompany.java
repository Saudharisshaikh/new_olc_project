package com.app.mdlive_cp.reliance;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.ActivityAddNewPatient;
import com.app.mdlive_cp.ActivityTcmDetails;
import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.MyAppointmentsModel;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.GloabalSocket;
import com.app.mdlive_cp.util.SpinnerCustom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCompany extends BaseActivity implements  GloabalSocket.SocketEmitterCallBack{

	TextView tvActivePatient,tvInActivePatients;
	ListView lvCompanyPatients;
	TextView tvNoData;
	SpinnerCustom spCompany;

	public static final String COMPANY_PREFS_KEY = "cp_assigned_companies_relaince";

	public static boolean shouldRefreshPatients = false;

	SwipeRefreshLayout srPatients;

	@Override
	protected void onResume() {
		super.onResume();

		if(shouldRefreshPatients && (!TextUtils.isEmpty(selectedCompanyID))){
			shouldRefreshPatients = false;
			loadPatientsByCompany(selectedCompanyID);
		}
	}


	GloabalSocket gloabalSocket;
	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);


		gloabalSocket = new GloabalSocket(activity,this);

		setSupportActionBar(findViewById(R.id.toolbar));
		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("My Patients");
		}
		Button btnToolbar = findViewById(R.id.btnToolbar);

		btnToolbar.setText("Add Patient");
		btnToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(selectedCompanyID)){
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
							.setMessage("You are not added in any company. Please contact admin to assign you a company")
							.setPositiveButton("Done", null).show();
				}else {
					openActivity.open(ActivityAddNewPatient.class, false);
				}
			}
		});

		tvActivePatient = findViewById(R.id.tvActivePatient);
		tvInActivePatients = findViewById(R.id.tvInActivePatients);
		lvCompanyPatients = findViewById(R.id.lvCompanyPatients);
		tvNoData = findViewById(R.id.tvNoData);
		spCompany = findViewById(R.id.spCompany);


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

		View.OnClickListener onClickListener = v -> {
			switch (v.getId()){
				case R.id.tvActivePatient:
					filterAndShowData(1);
					break;
				case R.id.tvInActivePatients:
					filterAndShowData(0);
					break;
				default:
					break;
			}
		};
		tvActivePatient.setOnClickListener(onClickListener);
		tvInActivePatients.setOnClickListener(onClickListener);


		//======================swip to refresh==================================
		//mySwipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh);

		srPatients = findViewById(R.id.srPatients);
		int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
		srPatients.setColorSchemeColors(colorsArr);
		srPatients.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.i("--", "onRefresh called from SwipeRefreshLayout");

						// This method performs the actual data-refresh operation.
						// The method calls setRefreshing(false) when it's finished.
						//myUpdateOperation();
						if(!checkInternetConnection.isConnectedToInternet()){
							srPatients.setRefreshing(false);
						}else {
							//toggleViews(true);
						}
						loadAndShowData();
					}
				}
		);
		//======================swip to refresh ends=============================


		loadAndShowData();
	}


	public void loadAndShowData(){
		String chechedData = sharedPrefsHelper.get(COMPANY_PREFS_KEY, "");
		if(TextUtils.isEmpty(chechedData)){
			loadCompany();
		}else {
			parseCompanyData(chechedData);

			new GloabalMethods(activity).getCompany();//companies in which CP assigned
		}
	}



	public void loadCompany(){
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));

		ApiManager apiManager = new ApiManager(ApiManager.GET_COMPANIES,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}
	public void loadPatientsByCompany(String compamyId){
		RequestParams params = new RequestParams();
		params.put("company_id", compamyId);
		params.put("doctor_id", prefs.getString("id", ""));

		ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENTS_BY_COMPANY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<CompanyBean> companyBeans;
	ArrayList<CompanyPatientBean> companyPatientBeans = new ArrayList<>(), companyPatientBeansAll;
	public static String selectedCompanyID = "";
	CompanyPatientAdapter companyPatientAdapter;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.GET_COMPANIES)){
			parseCompanyData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENTS_BY_COMPANY)){
			srPatients.setRefreshing(false);
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray patients = jsonObject.getJSONArray("patients");
				Type listType = new TypeToken<ArrayList<CompanyPatientBean>>() {}.getType();
				companyPatientBeansAll = new Gson().fromJson(patients+"", listType);

				filterAndShowData(1);

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}

	public void parseCompanyData(String content){

		try {
			JSONObject jsonObject = new JSONObject(content);

			JSONArray companies = jsonObject.getJSONArray("companies");
			Type listType = new TypeToken<ArrayList<CompanyBean>>() {}.getType();
			companyBeans = new Gson().fromJson(companies+"", listType);

			if(companyBeans != null){
				//lvCompanyPatients.setAdapter(new CompanyPatientAdapter(activity, companyPatientBeans));

				sharedPrefsHelper.save(ActivityCompany.COMPANY_PREFS_KEY, content);//also save here to refresh data in local

				ArrayAdapter<CompanyBean> spCompanyAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, companyBeans);
				spCompany.setAdapter(spCompanyAdapter);

				if(companyBeans.isEmpty()){selectedCompanyID = "";}//reset selected comp_id if list empty

				spCompany.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3, boolean isUserSelect) {
						// TODO Auto-generated method stub
						//if(isUserSelect){}
						selectedCompanyID = companyBeans.get(pos).company_id;
						loadPatientsByCompany(selectedCompanyID);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});
			}

		} catch (JSONException e) {
			e.printStackTrace();
			customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
		}

	}

	public void filterAndShowData(int isActive){//isActive-> 0=InActive, 1=Active

		if(isActive == 1){
			tvActivePatient.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
			tvActivePatient.setTextColor(getResources().getColor(android.R.color.white));
			tvInActivePatients.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvInActivePatients.setTextColor(getResources().getColor(R.color.app_blue_color));
		}else if(isActive == 0){
			tvActivePatient.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvActivePatient.setTextColor(getResources().getColor(R.color.app_blue_color));
			tvInActivePatients.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
			tvInActivePatients.setTextColor(getResources().getColor(android.R.color.white));
		}

		companyPatientBeans.clear();
		if(companyPatientBeansAll != null){
			for (int i = 0; i < companyPatientBeansAll.size(); i++) {
				if(companyPatientBeansAll.get(i).is_active.equalsIgnoreCase(String.valueOf(isActive))){
					companyPatientBeans.add(companyPatientBeansAll.get(i));
				}
			}
		}
		int vis = companyPatientBeans.isEmpty() ? View.VISIBLE : View.GONE;
		tvNoData.setVisibility(vis);
		companyPatientAdapter = new CompanyPatientAdapter(activity, companyPatientBeans);
		lvCompanyPatients.setAdapter(companyPatientAdapter);
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
				companyPatientAdapter.notifyDataSetChanged();

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
