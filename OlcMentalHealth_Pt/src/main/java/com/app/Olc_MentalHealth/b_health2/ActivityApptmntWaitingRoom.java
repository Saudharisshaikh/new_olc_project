package com.app.Olc_MentalHealth.b_health2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.app.Olc_MentalHealth.BaseActivity;
import com.app.Olc_MentalHealth.MainActivityNew;
import com.app.Olc_MentalHealth.MyAppointments;
import com.app.Olc_MentalHealth.R;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import ss.com.bannerslider.views.BannerSlider;

public class ActivityApptmntWaitingRoom extends BaseActivity{


	TextView tvWaitingMsg,tvLiveCareTicker,tvWaitMsgDocInfo;
	Button btnPaperwork, btnExitWaitingRoom;

	//boolean isAlreadyInRoom;

    /*Typeface timerfonts;
    public static final String ISIN_ICROOM_PREFS_KEY = "isAlreadyInICroom";
    boolean isFromDocList;*/

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aptmnt_waitingroom);

        //isFromDocList = getIntent().getBooleanExtra("isFromDocList", false);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Waiting Room");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //timerfonts = Typeface.createFromAsset(getAssets(),"digital-7.ttf");

        tvWaitingMsg = findViewById(R.id.tvWaitingMsg);
		btnPaperwork = findViewById(R.id.btnPaperwork);
		btnExitWaitingRoom = findViewById(R.id.btnExitWaitingRoom);
        tvLiveCareTicker = findViewById(R.id.tvLiveCareTicker);
		tvWaitMsgDocInfo = findViewById(R.id.tvWaitMsgDocInfo);

		BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);
		new GloabalMethods(activity).setupBannerSlider(bannerSlider, true);

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.btnPaperwork:
					Intent intent1 = new Intent(getApplicationContext(), MainActivityNew.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent1);
					break;

				default:
					break;
			}
		};
		btnPaperwork.setOnClickListener(onClickListener);
		btnExitWaitingRoom.setOnClickListener(onClickListener);


		joinAppointment();
	}


	public void joinAppointment(){
		RequestParams params = new RequestParams();
		params.put("app_id", MyAppointments.myAppointmentsModelSelected.appointment_id);

		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_JOIN_APTMNT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}



	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_JOIN_APTMNT)){
			//{"status":"success","message":"Exit successfully"}
			try {
				JSONObject jsonObject = new JSONObject(content);

				tvWaitMsgDocInfo.setText(jsonObject.optString("message"));

				/*AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle(getResources().getString(R.string.app_name))
						.setMessage(jsonObject.optString("message"))
						.setPositiveButton("Done",null)
						.create();
				alertDialog.show();*/
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}

}
