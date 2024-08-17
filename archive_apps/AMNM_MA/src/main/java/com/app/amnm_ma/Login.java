package com.app.amnm_ma;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.amnm_ma.api.ApiCallBack;
import com.app.amnm_ma.api.ApiManager;
import com.app.amnm_ma.permission.PermissionsActivity;
import com.app.amnm_ma.permission.PermissionsChecker;
import com.app.amnm_ma.services.GPSTracker;
import com.app.amnm_ma.util.CheckInternetConnection;
import com.app.amnm_ma.util.CustomToast;
import com.app.amnm_ma.util.DATA;
import com.app.amnm_ma.util.Database;
import com.app.amnm_ma.util.GloabalMethods;
import com.app.amnm_ma.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements ApiCallBack{

	Activity activity;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;

    TextView tvLoginForgotPass;
    RadioGroup rgOptions;
	Button btnLogin, btnSignup;
	EditText etLoginUsername, etLoginPassword;

	Database db;
	GPSTracker gps;
	double latitude,longitude;
	private boolean haveWeShownPreferences = false;
	private boolean isSubUserSelected = false ;

	@Override
	protected void onResume() {
		super.onResume();


		//---------------RunTime Permissions----------------------------------
		PermissionsChecker permissionsChecker = new PermissionsChecker(activity);

		if(permissionsChecker.lacksPermissions(PermissionsChecker.PERMISSIONS)){
			startActivity(new Intent(activity, PermissionsActivity.class));
			return;
		}else{
			//btnPermission.setText("All permissions granted");
		}

		GloabalMethods.shouldUpdatePopAppear = true;

		//---------------RunTime Permissions----------------------------------

		//check if already logged-in, get data from preferences if yes. 

		haveWeShownPreferences = prefs.getBoolean("HaveShownPrefs", false);
		isSubUserSelected = prefs.getBoolean("subUserSelected", false);

		System.out.println("--online care prefsshown: "+haveWeShownPreferences);
		System.out.println("--online care isUserSelected: "+isSubUserSelected);

		if(haveWeShownPreferences) {
			Intent intent = new Intent(activity,MainActivityNew.class);//Splash
			startActivity(intent);
			finish();
		}else {
			gps = new GPSTracker(activity);
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				gps.stopUsingGPS();
			} else {
				gps.showSettingsAlert();
			}
			NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
			//notificationManager.cancel(NOTIFICATION_ID);
			notificationManager.cancelAll();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_login);
		
		//getSupportActionBar().setTitle(R.string.app_name);
		getSupportActionBar().hide();

		activity = Login.this;
		apiCallBack = this;
		
		db = new Database(activity);
		db.createDatabase();
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnSignup = (Button) findViewById(R.id.btnSignup);
		etLoginUsername = (EditText) findViewById(R.id.et_email);
		etLoginPassword = (EditText) findViewById(R.id.et_password);
		
		tvLoginForgotPass = (TextView) findViewById(R.id.tvLoginForgotPass);
		tvLoginForgotPass.setText(Html.fromHtml("<u>Forgot Password?</u>"));
		tvLoginForgotPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				openActivity.open(ForgotPassword.class, false);
			}
		});
		
		rgOptions = (RadioGroup) findViewById(R.id.rg);
		
		checkInternet = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);

		findViewById(R.id.btnSignupCont).setVisibility(View.INVISIBLE);//Doc registration removed by Jamal Nov 2018
		btnSignup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				openActivity.open(Signup.class, false);
			}
		});
		
		
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				if(etLoginUsername.getText().toString().isEmpty() || etLoginPassword.getText().toString().isEmpty()) {
					customToast.showToast("Please enter username and password",0,0);
				} else {

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etLoginUsername.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(etLoginPassword.getWindowToken(), 0);
					
					RequestParams params = new RequestParams();
					params.put("username",etLoginUsername.getText().toString());
					params.put("password", etLoginPassword.getText().toString());
					if (latitude == 0.0 || longitude==0.0) {
						gps = new GPSTracker(activity);
						 if (gps.canGetLocation()) {
							   latitude = gps.getLatitude();
								longitude = gps.getLongitude();
								gps.stopUsingGPS();
							  }
					}
					params.put("latitude", latitude+"");
					params.put("longitude", longitude+"");
					
					params.put("type", "doctor");// "specialist" -> CP app
					params.put("current_app", CURRENT_APP);
					
					System.out.println("--latlong in params "+latitude+" "+longitude);
					
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("qb_username", etLoginUsername.getText().toString());
					ed.putString("qb_password", etLoginPassword.getText().toString());
					ed.commit();

						ApiManager apiManager = new ApiManager(ApiManager.DOCTOR_LOGIN,"post",params,apiCallBack, activity);
						apiManager.loadURL();


			}//if isEmpty ends
			}
		});


		TextView tvLabel = (TextView) findViewById(R.id.tvLabel);
		String styledText = "<font color='" + DATA.APP_THEME_RED_COLOR + "'>Virtual Care</font> services are currently available in the state of Michigan only for now. <br>(Additional states will be added in the near future)";
		tvLabel.setText(Html.fromHtml(styledText));
	}

	//public static final String CURRENT_APP = "doctor";
	public static final String CURRENT_APP = "amnm_ma";//by zohaib for AMNM doc
	public static final String HOSPITAL_ID_EMCURA = "46";//AMNM hosp id

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equals(ApiManager.DOCTOR_LOGIN)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");
				String msg = "";

				if(jsonObject.has("msg")) {
				msg = jsonObject.getString("msg");
				}

				if(status.equals("success")) {

					String userStr = jsonObject.getString("user");
					JSONObject user_info = new JSONObject(userStr);

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("id", user_info.getString("id"));
					ed.putString("first_name", user_info.getString("first_name"));
					ed.putString("last_name", user_info.getString("last_name"));
					ed.putString("email", user_info.getString("email"));
					ed.putString("username", user_info.getString("username"));
					ed.putString("gender", user_info.getString("gender"));
					ed.putString("birthdate", user_info.getString("birthdate"));
					ed.putString("mobile", user_info.getString("mobile"));
					ed.putString("image", user_info.getString("image"));
					ed.putString("designation", user_info.getString("designation"));
					ed.putString("country", user_info.getString("country"));

					ed.putString("address", user_info.getString("address1"));

					if (user_info.has("zip_code")) {
						ed.putString("zipcode", user_info.getString("zip_code"));
					}
					if (user_info.has("city")) {
						ed.putString("city", user_info.getString("city"));
					}
					if (user_info.has("state")) {
						ed.putString("state", user_info.getString("state"));
					}
					if (user_info.has("introduction")) {
						ed.putString("introduction", user_info.getString("introduction"));
					}
					if (user_info.has("pincode")) {
						ed.putString("pincode", user_info.getString("pincode"));
					}
					if (user_info.has("qualification")) {
						ed.putString("qualification", user_info.getString("qualification"));
					}
					if (user_info.has("career_data")) {
						ed.putString("career_data", user_info.getString("career_data"));
					}
					if (user_info.has("vacation_mode")) {
						ed.putString("vacation_mode",user_info.getString("vacation_mode"));
					}
					if(user_info.has("folder_name")){
						ed.putString("folder_name",user_info.getString("folder_name"));
					}
					if(user_info.has("is_available_onlinecare")){
						ed.putString("is_available_onlinecare",user_info.getString("is_available_onlinecare"));
					}

					if(! user_info.optString("centers").isEmpty()){
						JSONArray centers = user_info.getJSONArray("centers");
						String dr_clinics = "";
						for (int i = 0; i < centers.length(); i++) {
							String center_name = centers.getJSONObject(i).getString("center_name");
							dr_clinics = dr_clinics + center_name;
							if(i != (centers.length() - 1)){
								dr_clinics = dr_clinics+", ";
							}
						}
						ed.putString("dr_clinics", dr_clinics);
					}

					String support_text = user_info.optString("support_text");
					ed.putString("support_text", support_text);

					ed.putBoolean("HaveShownPrefs",true);
					ed.putString("DrOrPatient", "doctor");
					ed.commit();

//							db.deleteSymptoms();
//							db.deleteConditions();
//							db.deleteSpecialities();
//
//							//getting all symptoms
//							String symptomStr = jsonObject.getString("symptoms");
//
//							symptomsArray = new JSONArray(symptomStr);
//
//							for(int i = 0; i<symptomsArray.length(); i++) {
//
//								System.out.println("--onlinecare symptom name"+symptomsArray.getJSONObject(i).getString("symptom_name"));
//
//								db.insertSymptoms(symptomsArray.getJSONObject(i).getString("id"),
//												  symptomsArray.getJSONObject(i).getString("symptom_name")
//										);
//							}
//
//							//getting all conditions
//							String conditionsStr = jsonObject.getString("conditions");
//
//							conditionsArray = new JSONArray(conditionsStr);
//
//							for(int i = 0; i<conditionsArray.length(); i++) {
//
//								System.out.println("--onlinecare condition name"+conditionsArray.getJSONObject(i).getString("condition_name"));
//
//								db.insertConditions(conditionsArray.getJSONObject(i).getString("id"),
//										conditionsArray.getJSONObject(i).getString("symptom_id"),
//										conditionsArray.getJSONObject(i).getString("condition_name")
//
//										);
//							}
//
//							//getting all specialities
//							String specialityStr = jsonObject.getString("specialities");
//
//							specialityArray = new JSONArray(specialityStr);
//
//							for(int i = 0; i<specialityArray.length(); i++) {
//
//								System.out.println("--onlinecare speciality name"+specialityArray.getJSONObject(i).getString("speciality_name"));
//
//								db.insertSpeciality(specialityArray.getJSONObject(i).getString("id"),
//										specialityArray.getJSONObject(i).getString("speciality_name")
//
//										);
//							}

//							Intent intent1 = new Intent();
//							intent1.setAction("com.app.onlinecaredr.START_SERVICE");
//							activity.sendBroadcast(intent1);


					//openActivity.open(MainActivityNew.class, true);//Splash
					DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
					getOAuthToken();

				}else if(status.equals("error")) {

					customToast.showToast(msg, 0, 0);

				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(apiName.equals(ApiManager.CREATE_TOKEN)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				String access_token = jsonObject.getString("access_token");
				prefs.edit().putString("access_token",access_token).commit();


				openActivity.open(MainActivityNew.class, true);//Splash

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}


	/*private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            System.out.println("--service already running"+serviceClass.getName());

	        	return true;

	        }
	    }
        System.out.println("--service not running"+serviceClass.getName());
	    return false;
	}*/

	public void getOAuthToken(){
		RequestParams params = new RequestParams();
		params.put("grant_type", "client_credentials");
		params.put("client_id", "zohaib");
		params.put("client_secret", "123");
		params.put("code", "123");

		ApiManager apiManager = new ApiManager(ApiManager.CREATE_TOKEN,"post",params,apiCallBack,activity);
		apiManager.loadURL();
	}
}
