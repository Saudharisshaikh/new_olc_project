package com.app.mhcsn_uc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_uc.adapter.InsuranceAdapter;
import com.app.mhcsn_uc.api.ApiCallBack;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.model.MyInsuranceBean;
import com.app.mhcsn_uc.util.DATA;
import com.app.mhcsn_uc.util.LiveCareInsurance;
import com.app.mhcsn_uc.util.LiveCareInsuranceCardhelper;
import com.app.mhcsn_uc.util.LiveCareInsuranceInterface;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityInsurance extends BaseActivity implements ApiCallBack{


	ListView lvMyInsurance;
	LinearLayout layNoInsurance;
	TextView tvNoInsurance;
	String selectedPrimaryInsurance = "";
	Button btnToolbar;


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance);


		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setTitle("Medical History");
		btnToolbar = findViewById(R.id.btnToolbar);

		btnToolbar.setText("Add Insurance");
		btnToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(FallRiskForm.class, false);
				new LiveCareInsurance(activity).getSimpleState();//getPayers();
			}
		});
		//btnToolbar.setVisibility(View.GONE);

		Button btnAddIns = findViewById(R.id.btnAddIns);
		btnAddIns.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LiveCareInsurance(activity).getSimpleState();//getPayers();
			}
		});

		Button btnSkipIns = findViewById(R.id.btnSkipIns);
		btnSkipIns.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finishAndContinue();
			}
		});

		lvMyInsurance = findViewById(R.id.lvMyInsurance);
		layNoInsurance = findViewById(R.id.layNoInsurance);
		tvNoInsurance = findViewById(R.id.tvNoInsurance);

		lvMyInsurance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				selectedPrimaryInsurance = myInsuranceBeen.get(position).insurance;

				RequestParams params = new RequestParams();
				params.put("patient_id",prefs.getString("id",""));
				params.put("id",myInsuranceBeen.get(position).id);
				ApiManager apiManager = new ApiManager(ApiManager.SET_DEFAULT_INSURANCE,"post",params,ActivityInsurance.this, activity);
				apiManager.loadURL();
			}
		});


		Button btnContinue = findViewById(R.id.btnContinue);
		btnContinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish();
				finishAndContinue();
			}
		});

		getMyInsurance();

	}



	public void getMyInsurance(){
		RequestParams params = new RequestParams();
		params.put("patient_id",prefs.getString("id",""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_MY_INSURANCE,"post",params,this, activity);
		apiManager.loadURL();
	}





	ArrayList<MyInsuranceBean> myInsuranceBeen;
	InsuranceAdapter insuranceAdapter;
	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equals(ApiManager.GET_MY_INSURANCE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");


				if(data.length() == 0){
					tvNoInsurance.setText(jsonObject.optString("message"));
					layNoInsurance.setVisibility(View.VISIBLE);
				}else {
					layNoInsurance.setVisibility(View.GONE);
				}

				myInsuranceBeen = new ArrayList<>();
				MyInsuranceBean myInsuranceBean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String patient_id = data.getJSONObject(i).getString("patient_id");
					String insurance = data.getJSONObject(i).getString("insurance");
					String policy_number = data.getJSONObject(i).getString("policy_number");
					String insurance_group = data.getJSONObject(i).getString("insurance_group");
					String insurance_code = data.getJSONObject(i).getString("insurance_code");
					String payer_name = data.getJSONObject(i).getString("payer_name");
					String copay_uc = data.getJSONObject(i).getString("copay_uc");

					myInsuranceBean = new MyInsuranceBean(id,patient_id,insurance,policy_number,insurance_group,insurance_code,payer_name,copay_uc);
					myInsuranceBeen.add(myInsuranceBean);
					myInsuranceBean = null;
				}

				insuranceAdapter = new InsuranceAdapter(activity,myInsuranceBeen);
				lvMyInsurance.setAdapter(insuranceAdapter);

				sharedPrefsHelper.savePatientInrances(myInsuranceBeen);

				//btnToolbar.setVisibility( (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE);

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.SET_DEFAULT_INSURANCE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.has("success")){
					customToast.showToast("Your primary insurance has been added",0,0);
					prefs.edit().putString("insurance",selectedPrimaryInsurance).commit();

					if(insuranceAdapter != null){
						insuranceAdapter.notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}


	public void finishAndContinue(){
		finish();
		if(FirstLogin.btnSkipp != null){
			try {
				FirstLogin.btnSkipp.performClick();
			}catch (Exception e){e.printStackTrace();}
		}else {
			DATA.print("-- FirstLogin.btnSkipp is NULL : "+FirstLogin.btnSkipp);
		}
	}



	public void showNoDataLbls(){
		int vis = myInsuranceBeen == null || myInsuranceBeen.isEmpty() ? View.VISIBLE : View.GONE;
		layNoInsurance.setVisibility(vis);
	}





	//pick Insurance card image front + back on add new insurance
	LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
	public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
		liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
		liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(liveCareInsuranceCardhelper != null){
			//Insurance card image
			liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
