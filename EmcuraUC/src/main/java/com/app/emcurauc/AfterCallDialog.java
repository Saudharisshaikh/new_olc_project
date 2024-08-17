package com.app.emcurauc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AfterCallDialog extends BaseActivity {


	TextView tvDialogNoThanks,tvDialogSubmit,tvDialogAddFollowup;
	ProgressDialog pd;
	RatingBar ratingBar1,rbRateVisit,rbNeedsMet,rbRRecTher;
	int ratng = 0;


	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if(android.os.Build.VERSION.SDK_INT >= 21) {
			finishAndRemoveTask();
		}
	}

	@Override
	protected void onResume() {
		/*if (new GloabalMethods(activity).checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(this, RegistrationIntentService.class);
			startService(intent);
		}*/

		new GloabalMethods(activity).getFirebaseToken();

		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.after_call_dialog);

		if(getSupportActionBar() != null){
			getSupportActionBar().hide();
		}
		try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//requestWindowFeature(Window.FEATURE_NO_TITLE);
        }catch (Exception e){e.printStackTrace();}

		pd = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		pd.setMessage("loading...");

		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		rbRateVisit = (RatingBar) findViewById(R.id.rbRateVisit);
		rbNeedsMet = (RatingBar) findViewById(R.id.rbNeedsMet);
		rbRRecTher = (RatingBar) findViewById(R.id.rbRRecTher);
		tvDialogSubmit = (TextView) findViewById(R.id.tvDialogSubmit);
		tvDialogNoThanks = (TextView) findViewById(R.id.tvDialogNoThanks);
		tvDialogAddFollowup = (TextView) findViewById(R.id.tvDialogAddFollowup);

		tvDialogNoThanks.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*Intent i = new Intent(activity, MainActivityNew.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);*/
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("livecareTimerRunning", false);
				ed.commit();
				/*Intent i = new Intent();
				i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
				sendBroadcast(i);*/

				if(android.os.Build.VERSION.SDK_INT >= 21) {
					finishAndRemoveTask();
				} else {
					finish();
				}


			}
		});

		tvDialogSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//sendBhealthRating();

				if(checkInternetConnection.isConnectedToInternet()) {
					sendCallRating();
				} else {
					customToast.showToast("Not connected to Internet", 0, 0);
				}
			}
		});

		ratingBar1.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				ratng  = (int) rating;
			}
		});

		tvDialogAddFollowup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("livecareTimerRunning", false);
				ed.commit();
				/*Intent i = new Intent();
				i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
				sendBroadcast(i);*/

				Intent intent = new Intent(activity, SearchADoctor.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
		});
	}

	private void sendCallRating() {

		pd.show();

		RequestParams params = new RequestParams();
		ratng = (int) ratingBar1.getRating();
		params.put("rating", ratng+"");
		params.put("id", DATA.incommingCallId);

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		client.post(DATA.baseUrl+"saveRating/",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in saveRating: "+content);

//				Intent i = new Intent(activity, MainActivityNew.class);
//				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				startActivity(i);
					SharedPreferences.Editor ed = prefs.edit();
					ed.putBoolean("livecareTimerRunning", false);
					ed.commit();
				/*Intent i = new Intent();
				i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
				sendBroadcast(i);*/

					if(android.os.Build.VERSION.SDK_INT >= 21) {
						finishAndRemoveTask();
					} else {
						finish();
					}

//				try {
//
//					jsonObject = new JSONObject(response);
//
//					status = jsonObject.getString("success");
//
//					if(status.equals("success"))
//					{
//
//						Intent i = new Intent(activity, MainActivityNew.class);
//						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//						startActivity(i);
//						finish();
//
//					}
//
//					else {
//
//						Toast.makeText(activity, "Something went wrong, please try again.", 0).show();
//					}


//				} catch (JSONException e) {
//					DATA.print("--online care exception in getlivecare patients: "+e);
//
//					e.printStackTrace();
//				}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveRating, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					  new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	private  void sendBhealthRating(){
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		params.put("call_id", DATA.incommingCallId);
		params.put("visit_rating", String.valueOf(rbRateVisit.getRating()));
		params.put("met_visit", String.valueOf(rbNeedsMet.getRating()));
		params.put("recommend_therapist", String.valueOf(rbRRecTher.getRating()));

		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_SAVE_RATING,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_SAVE_RATING)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("livecareTimerRunning", false);
				ed.commit();
				/*Intent i = new Intent();
				i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
				sendBroadcast(i);*/

				if(android.os.Build.VERSION.SDK_INT >= 21) {
					finishAndRemoveTask();
				} else {
					finish();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
