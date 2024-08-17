package com.app.mhcsn_cp.reliance.mdlive;

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

import com.app.mhcsn_cp.ActivityAddNewPatient;
import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.DoctorsList;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.SelectedDoctorsProfile;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DoctorsModel;
import com.app.mhcsn_cp.reliance.ActivityCompany;
import com.app.mhcsn_cp.reliance.CompanyBean;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.GloabalSocket;
import com.app.mhcsn_cp.util.SpinnerCustom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCompanyDoctors extends BaseActivity implements  GloabalSocket.SocketEmitterCallBack{


	ListView lvCompanyPatients;
	TextView tvNoData;
	SpinnerCustom spCompany;
	SwipeRefreshLayout srPatients;


	GloabalSocket gloabalSocket;
	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_doc);


		gloabalSocket = new GloabalSocket(activity,this);


		setSupportActionBar(findViewById(R.id.toolbar));
		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Nursing Home");
		}
		Button btnToolbar = findViewById(R.id.btnToolbar);

		btnToolbar.setText("Add Patient");
		btnToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(activity, ActivityAddNewPatient.class);
				intent.putExtra("isMultiDoc", true);
				startActivity(intent);

				/*if(TextUtils.isEmpty(selectedCompanyID)){
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
							.setMessage("You are not added in any company. Please contact admin to assign you a company")
							.setPositiveButton("Done", null).show();
				}else {
					openActivity.open(ActivityAddNewPatient.class, false);
				}*/
			}
		});




		lvCompanyPatients = findViewById(R.id.lvCompanyPatients);
		tvNoData = findViewById(R.id.tvNoData);
		spCompany = findViewById(R.id.spCompany);


		lvCompanyPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				DATA.selectedDrIdForNurse = companyDoctorBeans.get(position).doctor_id;
				DATA.selectedDrId = companyDoctorBeans.get(position).doctor_id;

				prefs.edit().putString(DoctorsList.SELCTED_DR_ID_PREFS_KEY, companyDoctorBeans.get(position).doctor_id).commit();

				//DATA.selectedUserCallId = companyDoctorBeans.get(position).doctor_id;

				DATA.selectedDrName = companyDoctorBeans.get(position).first_name + " " + companyDoctorBeans.get(position).last_name;
				DATA.selectedDrQbId = "0";//companyDoctorBeans.get(position).qb_id;
				DATA.selectedDrImage = companyDoctorBeans.get(position).image;
				DATA.selectedDrQualification = "-";//companyDoctorBeans.get(position).qualification;

				DATA.selectedDoctorsModel = new DoctorsModel();
				DATA.selectedDoctorsModel.current_app = companyDoctorBeans.get(position).current_app;
				DATA.selectedDoctorsModel.is_online = companyDoctorBeans.get(position).is_online;
				DATA.selectedDoctorsModel.id = companyDoctorBeans.get(position).doctor_id;

				DATA.isFromDocToDoc = true;

				Intent intent = new Intent(activity, SelectedDoctorsProfile.class);
				intent.putExtra("isFromMyDoctors",true);//isFromMyDoctors

				intent.putExtra("isFromCompanyDoc", true);//for view patients button click

				startActivity(intent);
				//finish();


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
		String chechedData = sharedPrefsHelper.get(ActivityCompany.COMPANY_PREFS_KEY, "");
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
	public void loadDoctorsByCompany(String compamyId){
		RequestParams params = new RequestParams();
		params.put("company_id", compamyId);

		ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTORS_BY_COMPANY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<CompanyBean> companyBeans;
	ArrayList<CompanyDoctorBean> companyDoctorBeans;
	public static String selectedCompanyID = "";
	CompanyDoctorAdapter companyPatientAdapter;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.GET_COMPANIES)){
			parseCompanyData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.GET_DOCTORS_BY_COMPANY)){
			srPatients.setRefreshing(false);
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray doctors = jsonObject.getJSONArray("doctors");
				Type listType = new TypeToken<ArrayList<CompanyDoctorBean>>() {}.getType();
				companyDoctorBeans = new Gson().fromJson(doctors+"", listType);

				if(companyDoctorBeans != null){
					int vis = companyDoctorBeans.isEmpty() ? View.VISIBLE : View.GONE;
					tvNoData.setVisibility(vis);
					companyPatientAdapter = new CompanyDoctorAdapter(activity, companyDoctorBeans);
					lvCompanyPatients.setAdapter(companyPatientAdapter);
				}

				//filterAndShowData(1);

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
						loadDoctorsByCompany(selectedCompanyID);
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



	@Override
	public void onSocketCallBack(String emitterResponse) {
		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("doctor")){
				for (int i = 0; i < companyDoctorBeans.size(); i++) {
					if(companyDoctorBeans.get(i).doctor_id.equalsIgnoreCase(id)){
						if(status.equalsIgnoreCase("login")){
							companyDoctorBeans.get(i).is_online = "1";
						}else if(status.equalsIgnoreCase("logout")){
							companyDoctorBeans.get(i).is_online = "0";
						}
					}
				}
				companyPatientAdapter.notifyDataSetChanged();

				if(DATA.selectedDoctorsModel != null && DATA.selectedDoctorsModel.id.equalsIgnoreCase(id)){
					if(status.equalsIgnoreCase("login")){
						DATA.selectedDoctorsModel.is_online = "1";
					}else if(status.equalsIgnoreCase("logout")){
						DATA.selectedDoctorsModel .is_online = "0";
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
