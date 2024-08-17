package com.app.msu_cp.reliance.greatlakes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.ActivityAddNewPatient;
import com.app.msu_cp.ActivityTcmDetails;
import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.MyAppointmentsModel;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalSocket;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityMyPatients extends BaseActivity implements GloabalSocket.SocketEmitterCallBack {


	ListView lvMyPatients;
	TextView tvNoData;
	SwipeRefreshLayout srPatients;
	EditText etFilter;

	public static boolean shouldRefreshPatients = false;
	@Override
	protected void onResume() {
		super.onResume();

		if(shouldRefreshPatients){
			shouldRefreshPatients = false;
			loadMyPatients(true);
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
		setContentView(R.layout.activity_my_patients);


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
				Intent intent = new Intent(activity, ActivityAddNewPatient.class);
				intent.putExtra("isFromMyPatient", true);
				startActivity(intent);
			}
		});


		lvMyPatients = findViewById(R.id.lvMyPatients);
		tvNoData = findViewById(R.id.tvNoData);
		etFilter = findViewById(R.id.etFilter);

		etFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(myPatientAdapter != null){
					myPatientAdapter.filter(s.toString());
				}
			}
		});


		lvMyPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				DATA.selectedUserQbid = "";
				DATA.selectedUserCallId = myPatientBeans.get(position).patient_id;
				DATA.selectedUserCallName = myPatientBeans.get(position).first_name+" "+myPatientBeans.get(position).last_name;
				DATA.selectedUserCallSympTom = "";
				DATA.selectedUserCallCondition = "";
				DATA.selectedUserCallDescription = "";
				DATA.selectedUserAppntID = "";

				DATA.selectedUserLatitude =  0;
				DATA.selectedUserLongitude =  0;

				DATA.selectedUserCallImage = myPatientBeans.get(position).image;

				//filterReports(DATA.selectedUserCallId);
				DATA.allReportsFiltered = new ArrayList<>();

				DATA.isFromDocToDoc = false;

				DATA.selectedRefferedLiveCare = new MyAppointmentsModel();
				DATA.selectedRefferedLiveCare.id = myPatientBeans.get(position).patient_id;
				DATA.selectedRefferedLiveCare.is_online = myPatientBeans.get(position).is_online;
				DATA.selectedRefferedLiveCare.first_name = myPatientBeans.get(position).first_name;
				DATA.selectedRefferedLiveCare.last_name = myPatientBeans.get(position).last_name;
				DATA.selectedRefferedLiveCare.patient_phone = myPatientBeans.get(position).phone;

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
						}else {
							//toggleViews(true);
						}
						loadMyPatients(false);
					}
				}
		);
		//======================swip to refresh ends=============================


		loadMyPatients(true);
	}




	public void loadMyPatients(boolean showLoader){
		ApiManager.shouldShowPD = showLoader;
		RequestParams params = new RequestParams();
		ApiManager apiManager = new ApiManager(ApiManager.MY_PATIENTS + prefs.getString("id", "") ,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<MyPatientBean> myPatientBeans;
	MyPatientAdapter myPatientAdapter;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.contains(ApiManager.MY_PATIENTS)){
			srPatients.setRefreshing(false);
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				Type listType = new TypeToken<ArrayList<MyPatientBean>>() {}.getType();
				myPatientBeans = gson.fromJson(data+"", listType);

				if(myPatientBeans != null){
					myPatientAdapter = new MyPatientAdapter(activity, myPatientBeans);
					lvMyPatients.setAdapter(myPatientAdapter);
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
				for (int i = 0; i < myPatientBeans.size(); i++) {
					if(myPatientBeans.get(i).patient_id.equalsIgnoreCase(id)){
						if(status.equalsIgnoreCase("login")){
							myPatientBeans.get(i).is_online = "1";
						}else if(status.equalsIgnoreCase("logout")){
							myPatientBeans.get(i).is_online = "0";
						}
					}
				}
				myPatientAdapter.notifyDataSetChanged();

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
