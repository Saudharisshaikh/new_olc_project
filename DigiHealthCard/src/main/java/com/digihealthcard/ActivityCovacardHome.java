package com.digihealthcard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.digihealthcard.R;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.model.SubscriptionPlanBean;
import com.digihealthcard.permission.PermissionsActivity;
import com.digihealthcard.permission.PermissionsChecker;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ActivityCovacardHome extends ActivityBaseDrawer{


	TextView tvAddCard,tvShowCard,tvTestLocations,tvTestResults,tvIdCard,tvTeleHealth,tvProfile,tvLogout,tvShareApp,tvTrialMsg,tvTrialTitle;
	LinearLayout layPermissions;
	Button btnReviewPerm;
	LinearLayout layTrialExpired;
	Button btnSubscribeNow;
	View viewScanCardDim,viewShowCardDim,viewTestResultsDim,viewTestLocationsDim,viewIdCardDim;

	ProgressDialog pd;

	@Override
	protected void onResume() {
		super.onResume();
		/*GPSTracker gpsTracker = new GPSTracker(activity);
		if (gpsTracker.canGetLocation()) {
			prefs.edit().putString("userLatitude", gpsTracker.getLatitude()+"").commit();
			prefs.edit().putString("userLongitude", gpsTracker.getLongitude()+"").commit();
			System.out.println("-- mainActivity on resume : lat: "+gpsTracker.getLatitude()+" lng: "+gpsTracker.getLongitude());
			gpsTracker.stopUsingGPS();
		}*/

		layPermissions.setVisibility(lacksPermission() ? View.VISIBLE:View.GONE);

		if(ActivityPackages.isPaymentDone){
			ActivityPackages.isPaymentDone = false;
			/*if(dialogTrialExpired != null){
				dialogTrialExpired.dismiss();
			}*/

			//lockApp(false);
			checkTrialExpiry();

			//sharedPrefsHelper.save("isPaidTemp", true);
		}

		if(ActivityPackages.isSubcriptionCancelled){
			ActivityPackages.isSubcriptionCancelled = false;
			checkTrialExpiry();
		}
	}

	private Boolean lacksPermission(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
			return (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONSANDROID13));
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
			return (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONANDROID12));
		}else{
			return (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS));
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setContentView(R.layout.activity_covacard_home);
		getLayoutInflater().inflate(R.layout.activity_covacard_home, container_frame);


		/*setSupportActionBar(findViewById(R.id.toolbar));
		Button btnToolbar = findViewById(R.id.btnToolbar);
		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			getSupportActionBar().setHomeAsUpIndicator(R.drawable.outline_menu_white_24);// set drawable icon
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		btnToolbar.setVisibility(View.GONE);*/

		ivToolbarBack.setVisibility(View.GONE);
		ivToolbarHome.setVisibility(View.GONE);
		btnToolbarAdd.setVisibility(View.GONE);

		tvAddCard = findViewById(R.id.tvAddCard);
		tvShowCard = findViewById(R.id.tvShowCard);
		tvTestLocations = findViewById(R.id.tvTestLocations);
		tvTestResults = findViewById(R.id.tvTestResults);
		tvIdCard = findViewById(R.id.tvIdCard);
		tvTeleHealth = findViewById(R.id.tvTeleHealth);
		tvProfile = findViewById(R.id.tvProfile);
		tvLogout = findViewById(R.id.tvLogout);
		tvShareApp = findViewById(R.id.tvShareApp);

		layTrialExpired = findViewById(R.id.layTrialExpired);
		btnSubscribeNow = findViewById(R.id.btnSubscribeNow);
		viewScanCardDim = findViewById(R.id.viewScanCardDim);
		viewShowCardDim = findViewById(R.id.viewShowCardDim);
		viewTestResultsDim = findViewById(R.id.viewTestResultsDim);
		viewTestLocationsDim = findViewById(R.id.viewTestLocationsDim);
		viewIdCardDim = findViewById(R.id.viewIdCardDim);
		tvTrialMsg = findViewById(R.id.tvTrialMsg);
		tvTrialTitle = findViewById(R.id.tvTrialTitle);

		ImageView ivAdView = findViewById(R.id.ivAdView);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}
		OnClickListener onClickListener = view -> {
			//dialog.dismiss();
			switch (view.getId()){
				case R.id.tvAddCard:
					openActivity.open(ActivityAddCard.class, false);
					break;

				case R.id.tvShowCard:
					openActivity.open(ActivityCardsList.class, false);
					break;

				case R.id.tvTestLocations:
					openActivity.open(ActivityTestLocations.class, false);
					break;

				case R.id.tvTestResults:
					openActivity.open(ActivityTestResultsList.class, false);//ActivityAddTestResults
					break;
				case R.id.tvProfile:
					openActivity.open(UpdateProfile.class, false);
					break;
				case R.id.tvLogout:
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
							.setMessage("Are you sure ? Do you want to logout ?")
							.setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									/*boolean debug_logs = sharedPrefsHelper.get("debug_logs", false);
									boolean dummy_subscription = sharedPrefsHelper.get("dummy_subscription", false);
									SharedPreferences.Editor editor = prefs.edit();
									editor.clear();
									editor.apply();
									SharedPrefsHelper.getInstance().clearAllData();
									sharedPrefsHelper.save("debug_logs", debug_logs);
									sharedPrefsHelper.save("dummy_subscription", dummy_subscription);

									Intent intent1 = new Intent(getApplicationContext(), Login.class);
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(intent1);*/

									logout();
								}
							})
							.setNegativeButton("Not Now", null).create().show();
					break;
				case R.id.ivAdView:
					DATA.addIntent(activity);
					break;
				case R.id.tvTeleHealth:
					showTeleHealthDialog();
					break;
				case R.id.btnSubscribeNow:
					openActivity.open(ActivityPackages.class,false);
					break;
				case R.id.tvIdCard:
					openActivity.open(ActivityIdCardsList.class, false);
					break;
				case R.id.tvShareApp:
					shareApp();
					break;
				default:
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Comming soon")
							.setMessage("This feature will be available in the near future")
							.setPositiveButton("Done",null).create();
					alertDialog.show();
					break;
			}
		};
		tvAddCard.setOnClickListener(onClickListener);
		tvShowCard.setOnClickListener(onClickListener);
		tvTestLocations.setOnClickListener(onClickListener);
		tvTestResults.setOnClickListener(onClickListener);
		tvIdCard.setOnClickListener(onClickListener);
		tvTeleHealth.setOnClickListener(onClickListener);
		tvLogout.setOnClickListener(onClickListener);
		tvProfile.setOnClickListener(onClickListener);
		ivAdView.setOnClickListener(onClickListener);
		btnSubscribeNow.setOnClickListener(onClickListener);
		tvShareApp.setOnClickListener(onClickListener);


		layPermissions = findViewById(R.id.layPermissions);
		btnReviewPerm = findViewById(R.id.btnReviewPerm);
		btnReviewPerm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(PermissionsActivity.class, false);
			}
		});

		loadCardTypes();
		checkTrialExpiry();
		paymentCredentials();
		loadIDCardTypes();

		new GloabalMethods(activity).getFirebaseToken();
	}

	private void loadCardTypes(){
		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.CARD_TYPES,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	private void loadIDCardTypes(){
		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.ID_CARD_TYPES,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	private void checkTrialExpiry(){

		String ExpiryCheckData = sharedPrefsHelper.get("ExpiryCheckData", "");
		if(!TextUtils.isEmpty(ExpiryCheckData)){
			parseExpiryData(ExpiryCheckData);
		}

		ApiManager.shouldShowLoader = false;
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_EXPIRY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void paymentCredentials(){
		ApiManager.shouldShowLoader = false;
		RequestParams params = new RequestParams();
		ApiManager apiManager = new ApiManager(ApiManager.PAYMENT_CREDS, "post", params, apiCallBack, activity);
		apiManager.loadURL();
	}





	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.CARD_TYPES)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				ArrayList<String> cardTypeList = gson.fromJson(data.toString(), new TypeToken<ArrayList<String>>() {}.getType());

				try {
					long limit = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse("12/30/2021").getTime();
					long today = new Date().getTime();
					System.out.println("-- GM : "+limit+ " | "+today + " | "+ (today <= limit));
					if(today <= limit){
						/*for (int i = 0; i < cardTypeList.size(); i++) {
							if(cardTypeList.get(i).contains("vaccin")){
								cardTypeList.remove(i);
							}
						}*/
						Iterator<String> iterator = cardTypeList.iterator();
						while (iterator.hasNext()) {
							String s = iterator.next(); // must be called before you can call i.remove()
							if(s.toLowerCase().contains("vaccin")){
								iterator.remove();
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}


				sharedPrefsHelper.save("covacard_cardtypes", gson.toJson(cardTypeList));

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.ID_CARD_TYPES)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				ArrayList<String> cardTypeList = gson.fromJson(data.toString(), new TypeToken<ArrayList<String>>() {}.getType());
				sharedPrefsHelper.save("covacard_id_cardtypes", gson.toJson(cardTypeList));

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.CHECK_EXPIRY)){

			parseExpiryData(content);

		}else if(apiName.equalsIgnoreCase(ApiManager.PAYMENT_CREDS)){
			/*{
				"status": "success",
					"data": {
				"stripe": {
					"stripe_pub_key": "pk_test_YGQVbRrz1utJrlEGgXcHp95f00ipD4Kred",
							"stripe_secret_key": "sk_test_T7HgyGoo6CltQh9Bd2Dr4xmG00zqmkVSAq"
				},
				"braintree": {
					"brain_auth_key": null
				}
			},
				"message": "Payment method is not configured. Please contact clinic."
			}*/
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONObject data = jsonObject.getJSONObject("data");
				JSONObject stripe = data.getJSONObject("stripe");
				String stripe_pub_key = stripe.getString("stripe_pub_key");
				//String paymentMessage = jsonObject.getString("message");
				sharedPrefsHelper.save("covacard_stripe_key", stripe_pub_key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}


	private void parseExpiryData(String content){

		// {"status":"success","billing":"subscription","data":{"id":"2","patient_id":"4590","package_id":"3","expiry":"2022-02-20","status":"active","stripe_plan_id":"0","pkg_mode":"Self"},"message":"Your subscription will expire on 02\/20\/2022"}
		//{"status":"error","message":"Your trial period expired."}
		//{"status":"success","billing":"trail","message":"Your trail perid will expire on 08-30-2021"}
		try {
			JSONObject jsonObject = new JSONObject(content);

			if(jsonObject.optString("status").equalsIgnoreCase("error")){

					/*boolean isPaidTemp = sharedPrefsHelper.get("isPaidTemp", false);
					if(!isPaidTemp){
						//showTrialExpiredDialog();
						lockApp(true);
					}else {
						lockApp(false);
					}*/
				tvTrialMsg.setText(jsonObject.optString("message", getResources().getString(R.string.trial_expied_msg)));

				lockApp(true);

				sharedPrefsHelper.saveSubscriptionPlan(null);
			}else {
				lockApp(false);
				SharedPrefsHelper.getInstance().save("SubscriptionStatus" , jsonObject.optString("status"));
				SharedPrefsHelper.getInstance().save("billingStatus" , jsonObject.optString("billing"));
				if(jsonObject.optString("billing").equalsIgnoreCase("trail")){
					layTrialExpired.setVisibility(View.VISIBLE);
					btnSubscribeNow.setVisibility(View.GONE);
					tvTrialMsg.setText(jsonObject.optString("message"));
					tvTrialTitle.setText(getResources().getString(R.string.trial_period));
					sharedPrefsHelper.saveSubscriptionPlan(null);
				}else {
					layTrialExpired.setVisibility(View.GONE);
					btnSubscribeNow.setVisibility(View.VISIBLE);
					tvTrialMsg.setText(getResources().getString(R.string.trial_expied_msg));
					tvTrialTitle.setText(getResources().getString(R.string.trial_expired));

					SubscriptionPlanBean planBean = gson.fromJson(jsonObject.getJSONObject("data").toString(), SubscriptionPlanBean.class);
					planBean.message = jsonObject.getString("message");
					sharedPrefsHelper.saveSubscriptionPlan(planBean);
				}
			}
			sharedPrefsHelper.save("ExpiryCheckData", content);

		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}

	}

	protected void lockApp(boolean lock){
		sharedPrefsHelper.save("isAppLocked", lock);
		super.lockApp(lock);
		if(lock){
			tvAddCard.setEnabled(false);
			tvShowCard.setEnabled(false);
			tvTestLocations.setEnabled(false);
			tvTestResults.setEnabled(false);
			tvIdCard.setEnabled(false);

			tvAddCard.setAlpha(0.5f);
			tvShowCard.setAlpha(0.5f);
			tvTestLocations.setAlpha(0.5f);
			tvTestResults.setAlpha(0.5f);
			tvIdCard.setAlpha(0.5f);

			viewScanCardDim.setVisibility(View.VISIBLE);
			viewShowCardDim.setVisibility(View.VISIBLE);
			viewTestLocationsDim.setVisibility(View.VISIBLE);
			viewTestResultsDim.setVisibility(View.VISIBLE);
			viewIdCardDim.setVisibility(View.VISIBLE);
			layTrialExpired.setVisibility(View.VISIBLE);
		}else {
			tvAddCard.setEnabled(true);
			tvShowCard.setEnabled(true);
			tvTestLocations.setEnabled(true);
			tvTestResults.setEnabled(true);
			tvIdCard.setEnabled(true);

			tvAddCard.setAlpha(1.0f);
			tvShowCard.setAlpha(1.0f);
			tvTestLocations.setAlpha(1.0f);
			tvTestResults.setAlpha(1.0f);
			tvIdCard.setAlpha(1.0f);

			viewScanCardDim.setVisibility(View.GONE);
			viewShowCardDim.setVisibility(View.GONE);
			viewTestLocationsDim.setVisibility(View.GONE);
			viewTestResultsDim.setVisibility(View.GONE);
			viewIdCardDim.setVisibility(View.GONE);
			layTrialExpired.setVisibility(View.GONE);
		}
	}

	Dialog dialogTrialExpired;
	public void showTrialExpiredDialog() {
		if(dialogTrialExpired != null){
			dialogTrialExpired.dismiss();
		}
		dialogTrialExpired = new Dialog(activity);
		dialogTrialExpired.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogTrialExpired.setContentView(R.layout.dialog_trial_expired);
		dialogTrialExpired.setCancelable(false);

		Button btnSubscribeNow = dialogTrialExpired.findViewById(R.id.btnSubscribeNow);
		Button btnCancel = dialogTrialExpired.findViewById(R.id.btnCancel);


		btnSubscribeNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialogTrialExpired.dismiss();
				openActivity.open(ActivityPackages.class, false);
			}
		});

		btnCancel.setOnClickListener(v -> {
			dialogTrialExpired.dismiss();
			activity.finish();
		});

		dialogTrialExpired.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogTrialExpired.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTrialExpired.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogTrialExpired.show();
        dialogTrialExpired.getWindow().setAttributes(lp);*/
	}




	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//return super.onCreateOptionsMenu(menu);
		return false;
	}*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;//return true back activity state maintains if false back activity oncreate called
		}
		return super.onOptionsItemSelected(item);
	}

	public void logout() {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		ApiManager.setupKeystores(client);
		RequestParams params = new RequestParams();

		params.put("id", prefs.getString("id", "0"));
		params.put("type", "patient");


		client.post("https://digihealthcard.com/app/logout", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--reaponce in logout"+content);
					//01-06 22:09:54.586: I/System.out(2570): --reaponce in logout{"status":"success","msg":"Successfully Logged Out"}
					try {
						JSONObject jsonObject = new JSONObject(content);

						if (jsonObject.has("status")&& jsonObject.getString("status").equalsIgnoreCase("success")) {

							boolean debug_logs = sharedPrefsHelper.get("debug_logs", false);
							boolean dummy_subscription = sharedPrefsHelper.get("dummy_subscription", false);
							SharedPreferences.Editor editor = prefs.edit();
							editor.clear();
							editor.apply();
							SharedPrefsHelper.getInstance().clearAllData();
							sharedPrefsHelper.save("debug_logs", debug_logs);
							sharedPrefsHelper.save("dummy_subscription", dummy_subscription);

							Intent intent1 = new Intent(getApplicationContext(), Login.class);
							intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent1);

						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: logout, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("-- responce in logout: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}
}
