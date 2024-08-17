package com.app.Olc_MentalHealth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.Olc_MentalHealth.paypal.PaymentLiveCare;
import com.app.Olc_MentalHealth.util.CustomToast;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.OpenActivity;
import com.app.Olc_MentalHealth.util.SharedPrefsHelper;
import com.app.Olc_MentalHealth.R;
import com.app.Olc_MentalHealth.api.ApiCallBack;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPinView extends AppCompatActivity implements ApiCallBack {


	AppCompatActivity activity;
	OpenActivity openActivity;
	CustomToast customToast;
	SharedPreferences prefs;

	OtpView otp_view;

	LinearLayout contOTP,contAfterOTP;
	TextView tvHospName;
	Button btn_continueToLogin,btn_cancel;


	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_view);

		activity = ActivityPinView.this;
		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		otp_view = findViewById(R.id.otp_view);

		contOTP = findViewById(R.id.contOTP);
		contAfterOTP = findViewById(R.id.contAfterOTP);
		tvHospName = findViewById(R.id.tvHospName);
		btn_continueToLogin = findViewById(R.id.btn_continueToLogin);
		btn_cancel = findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				otp_view.setText("");
				prefs.edit().putString(Login.HOSP_ID_PREFS_KEY, "").commit();
				contOTP.setVisibility(View.VISIBLE);
				contAfterOTP.setVisibility(View.GONE);

				//otp_view.requestFocus();
			}
		});


		btn_continueToLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
			@Override public void onOtpCompleted(String otp) {

				// do Stuff
				Log.d("onOtpCompleted=>", otp);

				DATA.print("-- OTP : "+ otp);

				getHospitalsData(otp);
			}
		});


		//otp_view.requestFocus();
	}



	public void getHospitalsData(String otpCode){
		RequestParams params = new RequestParams();
		//params.put("user_id", prefs.getString("id", ""));
		params.put("code", otpCode);

		ApiManager apiManager = new ApiManager(ApiManager.GET_HOSPITALS_DATA,"post",params,this, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.GET_HOSPITALS_DATA)){

			// {"status":"error","message":"Invalid Clinical Code"}
			//{"status":"success","hospital_data":{"id":"1","hospital_name":"OnlineCare Demo",
			// "folder_name":"onlinecare_newdesign","category_id":"2","hospital_zipcode":"48503","is_visible":"0"}}
			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("error")){
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle("Info")
							.setMessage(jsonObject.getString("message"))
							.setPositiveButton("Done",null).create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							otp_view.setText("");
						}
					});
					alertDialog.show();
				}else if(status.equalsIgnoreCase("success")){
					contOTP.setVisibility(View.GONE);
					contAfterOTP.setVisibility(View.VISIBLE);

					JSONObject hospital_data = jsonObject.getJSONObject("hospital_data");
					String hospital_name = hospital_data.getString("hospital_name");
					String id = hospital_data.getString("id");

					String support_text = hospital_data.getString("support_text");
					String hphones = hospital_data.getString("hphones");
					String haddress = hospital_data.getString("haddress");

					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(Login.HOSP_ID_PREFS_KEY, id);

					editor.putString("support_text", support_text);
					editor.putString("hphones", hphones);
					editor.putString("haddress", haddress);

					editor.commit();

					tvHospName.setText(hospital_name);//"Welcome to : "+


					String paypal_clientid = hospital_data.getString("paypal_clientid");
					if(TextUtils.isEmpty(paypal_clientid)){
						paypal_clientid = PaymentLiveCare.CONFIG_CLIENT_ID_JAMAL;
					}
					SharedPrefsHelper.getInstance().save(PaymentLiveCare.CONFIG_CLIENT_ID_PREFS_KEY, paypal_clientid);

					String stripe_pub_key = hospital_data.getString("stripe_pub_key");
					String stripe_secret_key = hospital_data.getString("stripe_pub_key");
					if(TextUtils.isEmpty(stripe_pub_key)){
						stripe_pub_key = PaymentLiveCare.publishableKeyJamal;
					}
					SharedPrefsHelper.getInstance().save(PaymentLiveCare.PREFS_KEY_SRIPE_PK, stripe_pub_key);
				}


			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
