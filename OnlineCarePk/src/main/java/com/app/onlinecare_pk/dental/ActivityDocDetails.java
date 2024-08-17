package com.app.onlinecare_pk.dental;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.GetLiveCare;
import com.app.onlinecare_pk.LiveCareWaitingArea;
import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.model.EmoWelOptionBean;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.GloabalMethods;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDocDetails extends BaseActivity{


	TextView tvGeneralCare,tvBehavioralHealth,tvDentalCare;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(getResources().getString(R.string.app_name));//Anatomy of Oral Cavity
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dental_header));
		}

		tvGeneralCare = findViewById(R.id.tvGeneralCare);
		tvBehavioralHealth = findViewById(R.id.tvBehavioralHealth);
		tvDentalCare = findViewById(R.id.tvDentalCare);



		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvGeneralCare:
						urgentCareClick();
						break;
					case R.id.tvBehavioralHealth:
						loadEmoWellOptions();
						break;
					case R.id.tvDentalCare:
						openActivity.open(ActivityDentalCareForm.class, false);
						break;

				}
			}
		};
		tvGeneralCare.setOnClickListener(onClickListener);
		tvBehavioralHealth.setOnClickListener(onClickListener);
		tvDentalCare.setOnClickListener(onClickListener);


	}






	public void urgentCareClick(){
		if(prefs.getBoolean("livecareTimerRunning", false)) {
			openActivity.open(LiveCareWaitingArea.class, false);
		} else {
			openActivity.open(GetLiveCare.class, false);
			//checkClinicTimings();
		}
	}



	public void loadEmoWellOptions(){
		ApiManager apiManager = new ApiManager(ApiManager.LOAD_EMO_WELL_OPTIONS,"post",null, apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.LOAD_EMO_WELL_OPTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.optJSONArray("data");
				if(data != null){
					ArrayList<EmoWelOptionBean> emoWelOptionBeans = new GsonBuilder().create().fromJson(data.toString(), new TypeToken<ArrayList<EmoWelOptionBean>>() {}.getType());
					if(emoWelOptionBeans != null){
						new GloabalMethods(activity).showAskEmoWellDialog(emoWelOptionBeans);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
