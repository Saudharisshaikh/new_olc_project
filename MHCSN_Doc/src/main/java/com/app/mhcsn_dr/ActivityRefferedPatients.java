package com.app.mhcsn_dr;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_dr.adapter.RefPatientsAdapter;
import com.app.mhcsn_dr.api.ApiManager;
import com.app.mhcsn_dr.model.MyAppointmentsModel;
import com.app.mhcsn_dr.model.ReferedPatientBean;
import com.app.mhcsn_dr.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityRefferedPatients extends BaseActivity {


	ListView lvRefPatients;
	TextView tvNoRefPatients;

	public static String referredId = "";

	public static boolean shouldRefresh = false;

	@Override
	protected void onResume() {
		if(shouldRefresh){
			shouldRefresh = false;
			loadRefPatients();
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refered_patients);

		lvRefPatients = (ListView) findViewById(R.id.lvRefPatients);
		tvNoRefPatients = (TextView) findViewById(R.id.tvNoRefPatients);

		lvRefPatients.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ReferedPatientBean referedPatientBean = referedPatientBeans.get(position);

				DATA.selectedUserCallId = referedPatientBean.patient_id;
				DATA.selectedUserCallName = referedPatientBean.patient_name;
				DATA.selectedUserCallImage = referedPatientBean.pimage;
				DATA.selectedLiveCare = new MyAppointmentsModel();
				DATA.selectedLiveCare.is_online = referedPatientBean.is_online;

				ActivityRefferedPatients.referredId = referedPatientBean.id;
				DATA.isVideoCallFromRefPt = true;

				openActivity.open(ActivityTcmDetails.class,false);
			}
		});

		loadRefPatients();
	}

	public void loadRefPatients(){
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));

		ApiManager apiManager = new ApiManager(ApiManager.REFERRED_PATIENTS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<ReferedPatientBean> referedPatientBeans;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.REFERRED_PATIENTS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				if(data.length() > 0){
					tvNoRefPatients.setVisibility(View.GONE);
				}else {
					tvNoRefPatients.setVisibility(View.VISIBLE);
				}
				referedPatientBeans = new ArrayList<>();
				ReferedPatientBean referedPatientBean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String dateof = data.getJSONObject(i).getString("dateof");
					String nurse_id = data.getJSONObject(i).getString("nurse_id");
					String doctor_id = data.getJSONObject(i).getString("doctor_id");
					String patient_id = data.getJSONObject(i).getString("patient_id");
					String patient_name = data.getJSONObject(i).getString("patient_name");
					String pimage = data.getJSONObject(i).getString("pimage");
					String doctor_name = data.getJSONObject(i).getString("doctor_name");
					String is_online = data.getJSONObject(i).getString("is_online");

					referedPatientBean = new ReferedPatientBean(id,dateof,nurse_id,doctor_id,patient_id,patient_name,pimage,doctor_name,is_online);
					referedPatientBeans.add(referedPatientBean);
					referedPatientBean = null;
				}

				lvRefPatients.setAdapter(new RefPatientsAdapter(activity,referedPatientBeans));
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}



}
