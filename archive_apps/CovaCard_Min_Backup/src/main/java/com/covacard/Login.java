package com.covacard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.covacard.api.ApiCallBack;
import com.covacard.api.ApiManager;
import com.covacard.model.SubUsersModel;
import com.covacard.util.CheckInternetConnection;
import com.covacard.util.CustomToast;
import com.covacard.util.DATA;
import com.covacard.util.GPSTracker;
import com.covacard.util.GloabalMethods;
import com.covacard.util.OpenActivity;
import com.covacard.util.SharedPrefsHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.RequestParams;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ApiCallBack{

	Activity activity;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	SharedPrefsHelper sharedPrefsHelper;

	TextView tvLoginForgotPass;
	Button btnLogin, btnSignup;
	ImageView btnLoginFB,btnLoginGoogle,ivLoginTwitter;//Button //change to button only if change ui
	EditText etLoginUsername, etLoginPassword;
	//RadioGroup rgOptions;

	private boolean haveWeShownPreferences = false;
	private boolean isSubUserSelected = false ;
	public static String DrOrPatient;
	double latitude,longitude;

	//Database db;
	GPSTracker gps;

	TwitterLoginButton loginButton;

	//public static final String CURRENT_APP = "patient",
	public static final String CURRENT_APP = "patient_emcura",
			HOSP_ID_PREFS_KEY = "hospital_id_from_servicve";

	public static String HOSPITAL_ID_EMCURA = "13";//Emcura hosp id

	final int LOCATION_PERMISSION_REQUEST_CODE = 954;
	AlertDialog alertDialogLoc;

	@Override
	protected void onResume() {
		super.onResume();

		//---------------RunTime Permissions----------------------------------
		/*PermissionsChecker permissionsChecker = new PermissionsChecker(activity);
		if(permissionsChecker.lacksPermissions(PermissionsChecker.PERMISSIONS)){
			startActivity(new Intent(activity, PermissionsActivity.class));
			return;
		}else{
			//btnPermission.setText("All permissions granted");
		}*/

		//---------------RunTime Permissions----------------------------------

		GloabalMethods.shouldUpdatePopAppear = true;

		//check if already logged-in, get data from preferences if yes. 

		haveWeShownPreferences = prefs.getBoolean("HaveShownPrefs", false);
		isSubUserSelected = prefs.getBoolean("subUserSelected", false);

		System.out.println("--online care prefsshown: "+haveWeShownPreferences);
		System.out.println("--online care isUserSelected: "+isSubUserSelected);

		if(! TextUtils.isEmpty(prefs.getString(HOSP_ID_PREFS_KEY, ""))){
			HOSPITAL_ID_EMCURA = prefs.getString(HOSP_ID_PREFS_KEY, "");
		}

		if(haveWeShownPreferences && isSubUserSelected) {

			Intent intent = new Intent(activity,Splash.class);
			//Intent intent = new Intent(activity, ActivityCovacardHome.class);
			startActivity(intent);
			finish();

		}else {
			/*gps = new GPSTracker(Login.this);
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				gps.stopUsingGPS();
			} else if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
				gps.showSettingsAlert();//moved down in onRequestPermissionsResult if permission not granted
				//gps.enableLocationPopup();
			}*/


			//---------------RunTime Permissions----------------------------------
			//PermissionsChecker permissionsChecker = new PermissionsChecker(activity);
		/*if(permissionsChecker.lacksPermissions(PermissionsChecker.PERMISSIONS)){
			startActivity(new Intent(activity, PermissionsActivity.class));
			return;
		}else{
			//btnPermission.setText("All permissions granted");
		}*/



			/*if(permissionsChecker.lacksPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)){
				if(alertDialogLoc != null){
					alertDialogLoc.dismiss();
				}
				alertDialogLoc =
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle("Location Permission Required ")
								.setMessage("Please allow the application to access your device location. We need your location information for our care providers in order to provide you the best quality medical care. We also use your location information to show you nearby urgent care centers, pharmacies, and test locations, etc.")
								.setPositiveButton("Allow Location", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,};
										ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
									}
								})
								.setNegativeButton("Not Now", null)
								.create();
				alertDialogLoc.setCanceledOnTouchOutside(false);
				alertDialogLoc.show();
			}*/

			//---------------RunTime Permissions----------------------------------



			NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
			//notificationManager.cancel(NOTIFICATION_ID);
			notificationManager.cancelAll();


			//Just open this to get hosp id by clinical code
			/*if(TextUtils.isEmpty(prefs.getString(HOSP_ID_PREFS_KEY, ""))){
				openActivity.open(ActivityPinView.class, false);
				return;
			}*/

			if(Signup.is_signup_successfull){
				Signup.is_signup_successfull = false;
				etLoginUsername.setText(Signup.signupUsername);
				etLoginPassword.setText(Signup.signupPassword);
				btnLogin.performClick();
			}
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
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternet = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		sharedPrefsHelper = SharedPrefsHelper.getInstance();

		//db = new Database(activity);
		//db.createDatabase();

		printKeyhash();
		//loginWithFaceBook();
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		AppEventsLogger.activateApp(getApplication());

		btnLogin = (Button) findViewById(R.id.btn_login);
		btnSignup = (Button) findViewById(R.id.btnSignup);
		btnLoginFB = findViewById(R.id.btnLoginFB);
		btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
		ivLoginTwitter = findViewById(R.id.ivLoginTwitter);
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

		//rgOptions = (RadioGroup) findViewById(R.id.rg);
/*		rgOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.radioDoctor:
					
					DrOrPatient = "doctor";
					
					customToast.showToast(DrOrPatient, 0, 0);
					break;
				case R.id.radioPatient:
					DrOrPatient = "patient";
					customToast.showToast(DrOrPatient, 0, 0);
					
					break;
				default:
					break;
				}
			}
		});*/
		DrOrPatient = "patient";

		btnSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openActivity.open(Signup.class, false);
			}
		});
		btnLoginFB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//loginWithFaceBook();
				fbLogin2();
			}
		});

		btnLoginGoogle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginWithGoogle();
			}
		});

		ivLoginTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginButton.performClick();
				loginButton.setPressed(true);
				loginButton.invalidate();
				loginButton.setPressed(false);
				loginButton.invalidate();
			}
		});


		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(etLoginUsername.getText().toString().isEmpty() || etLoginPassword.getText().toString().isEmpty()) {
					customToast.showToast("Please enter username and password",0,0);
				}else if(etLoginUsername.getText().toString().trim().equalsIgnoreCase("olc12345")
						&& etLoginPassword.getText().toString().trim().equalsIgnoreCase("olc12345")){

					etLoginUsername.setText("");
					etLoginPassword.setText("");
					sharedPrefsHelper.save(HOSP_ID_PREFS_KEY, "");
					onResume();
				}else if(etLoginUsername.getText().toString().trim().equalsIgnoreCase("olc12345678")
						&& etLoginPassword.getText().toString().trim().equalsIgnoreCase("olc12345678")){

					/*etLoginUsername.setText("");
					etLoginPassword.setText("");*/

					new GloabalMethods(activity).showDebugLogsDialog();

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

					params.put("type", "patient");
					params.put("current_app", CURRENT_APP);

					params.put("device_token", device_token);

					params.put("platform", "android");

					System.out.println("--latlong in params "+latitude+" "+longitude);
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("qb_username", etLoginUsername.getText().toString());
					ed.putString("qb_password", etLoginPassword.getText().toString());
					ed.commit();

					ApiManager apiManager = new ApiManager(ApiManager.PATIENT_LOGIN,"post",params,apiCallBack, activity);
					apiManager.loadURL();

				}//if isEmpty ends
			}
		});


		//------------Twitter

		loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
		loginButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				// Do something with result, which provides a TwitterSession for making API calls
				System.out.println("-------"+result.data.getUserName());

				//System.out.println("-------"+result.response);
				//System.out.println("-------"+result.data.toString());

				//getDetails(result.data);
				//serviceCall.doLoginSocial(result.data.getUserName(), result.data.getUserName(), progressDialog);

				//-------------------------------------

				/*TwitterAuthClient authClient = new TwitterAuthClient();
				authClient.requestEmail(result.data, new Callback<String>() {
					@Override
					public void success(Result<String> result) {
						// Do something with the result, which provides the email address
						System.out.println("-- *************************************************");
						System.out.println(result.data);
						//System.out.println(result.response.raw());
						System.out.println(result.response);
						//System.out.println(result.response.message());
					}

					@Override
					public void failure(TwitterException exception) {
						// Do something on failure
					}
				});*/

				TwitterAuthClient authClient = new TwitterAuthClient();
				//authClient.
				TwitterApiClient twitterApiClient = new TwitterApiClient(result.data);
				Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);

				call.enqueue(new Callback<User>() {

					@Override
					public void success(Result<User> result) {
						System.out.println("-- data: "+result.data.toString());
						System.out.println("-- responce: "+result.response);

						User user = result.data;
						System.out.println("-- email: "+user.email);
						System.out.println("-- name: "+user.name);
						System.out.println("-- id: "+user.id);
						System.out.println("-- idStr: "+user.idStr);
						System.out.println("-- profileBannerUrl: "+user.profileBannerUrl);
						System.out.println("-- profileImageUrl: "+user.profileImageUrl);
						System.out.println("-- screenName: "+user.screenName);
						System.out.println("-- location: "+user.location);
						System.out.println("-- timeZone: "+user.timeZone);

						//serviceCall.doLoginSocial(user.name," ", user.screenName,user.email , progressDialog);

						RequestParams params = new RequestParams();
						params.put("gender", "_");
						params.put("social_id",user.id+"");
						params.put("email",user.email);
						params.put("picture",user.profileImageUrl);
						params.put("first_name",user.name);
						params.put("hospital_id", HOSPITAL_ID_EMCURA);
						params.put("from_social","twitter");
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

						params.put("current_app", CURRENT_APP);

						params.put("device_token", device_token);

						params.put("platform", "android");

						ApiManager apiManager = new ApiManager(ApiManager.PATIENT__SOCIAL_LOGIN,"post",params,apiCallBack,activity);
						apiManager.loadURL();

					}

					@Override
					public void failure(TwitterException exception) {

					}
				});

			}

			@Override
			public void failure(TwitterException exception) {
				// Do something on failure
				System.out.println(exception.getMessage());
				System.out.println("--- exception");
				exception.printStackTrace();
			}
		});

		//getOAuthToken();

		TextView tvLabel = (TextView) findViewById(R.id.tvLabel);
		String styledText = "<font color='" +DATA.APP_THEME_RED_COLOR+ "'>Virtual Care</font> services are currently available in the state of Michigan only for now. <br>(Additional states will be added in the near future)";
		tvLabel.setText(Html.fromHtml(styledText));

		FirebaseInstanceId.getInstance().getInstanceId()
				.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
					@Override
					public void onComplete(@NonNull Task<InstanceIdResult> task) {
						if (!task.isSuccessful()) {
							System.out.println("-- getInstanceId failed"+ task.getException());
							return;
						}
						// Get new Instance ID token
						device_token = task.getResult().getToken();
						//saveToken(token);
					}
				});


		getLabels();




		ImageView ivLogo = findViewById(R.id.ivLogo);
		ivLogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count++;
				if (count > 0) {
					runnable = new Runnable() {
						@Override
						public void run() {
							count = 0;
							System.out.println("-- Counter reset : "+count);
						}
					};
					if(handler != null){
						handler.removeCallbacks(runnable);
					}
					handler = new Handler();
					handler.postDelayed(runnable, 2000);  // clear counter if user does not touch for one sec
				}

				System.out.println("-- Counter : "+count);

				if (count == 10) {
					String baseURL = sharedPrefsHelper.get("baseURL", ApiManager.COVACARD_BASE_URL);
					int checkedItem = baseURL.equalsIgnoreCase(ApiManager.COVACARD_BASE_URL) ? 0 : 1;
					String[] arr = {"Live/Production Mode", "Demo/Debug Mode"};
					final AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
							.setTitle("Switch Application Mode")
							//.setMessage("Please select the application mode.")
							.setSingleChoiceItems(arr, checkedItem, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String urlToSave = which == 0 ? ApiManager.COVACARD_BASE_URL : ApiManager.COVACARD_DEMO_URL;
									String mode = urlToSave.equalsIgnoreCase(ApiManager.COVACARD_BASE_URL) ? "Live/Production Mode" : "Demo/Debug Mode";
									sharedPrefsHelper.save("baseURL", urlToSave);
									customToast.showToast("Application has been switched to the "+mode+" successfully.",0,0);
									dialog.dismiss();
								}
							})
							.create();
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}
			}
		});
	}

	private int count = 0;
	Handler handler;
	Runnable runnable;

	String device_token;

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equals(ApiManager.PATIENT_LOGIN)){

			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");
				String msg = "";
				if(jsonObject.has("msg")) {
					msg = jsonObject.getString("msg");
				}

				if(status.equals("success")) {


					String after_urgentcare_form = jsonObject.optString("after_urgentcare_form");

					String userStr = jsonObject.getString("user");
					JSONObject user_info = new JSONObject(userStr);

					DATA.allSubUsers = new ArrayList<SubUsersModel>();

					SubUsersModel temp1 = new SubUsersModel();

					temp1.id = user_info.getString("id");
					temp1.firstName = user_info.getString("first_name");
					temp1.lastName = user_info.getString("last_name");;
					temp1.image = user_info.getString("image");
					temp1.occupation = user_info.getString("occupation");
					temp1.marital_status = user_info.getString("marital_status");
					temp1.dob = user_info.getString("birthdate");
					temp1.gender = user_info.getString("gender");

					temp1.phone = user_info.getString("phone");
					temp1.residency = user_info.getString("residency");
					temp1.city = user_info.getString("city");
					temp1.state = user_info.getString("state");
					temp1.country = user_info.getString("country");
					temp1.zipcode = user_info.getString("zipcode");

					temp1.relationship = "Primary Member";//"Parent user"  "Main User"  "Primary User"

					temp1.insurance = user_info.getString("insurance");

					DATA.allSubUsers.add(temp1);
					sharedPrefsHelper.saveParentUser(temp1);

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("id", user_info.getString("id"));
					ed.putString("first_name", user_info.getString("first_name"));
					ed.putString("last_name", user_info.getString("last_name"));
					ed.putString("email", user_info.getString("email"));
					ed.putString("username", user_info.getString("username"));
					ed.putString("gender", user_info.getString("gender"));
					ed.putString("birthdate", user_info.getString("birthdate"));
					ed.putString("phone", user_info.getString("phone"));
					ed.putString("image", user_info.getString("image"));
					ed.putString("qb_id", user_info.getString("qb_id"));
					ed.putString("reg_date", user_info.getString("reg_date"));
					ed.putString("is_online", user_info.getString("is_online"));
					ed.putString("occupation", user_info.getString("occupation"));

					ed.putString("city", user_info.getString("city"));
					ed.putString("state", user_info.getString("state"));

					ed.putString("zipcode", user_info.getString("zipcode"));
					ed.putString("marital_status", user_info.getString("marital_status"));
					ed.putBoolean("HaveShownPrefs",true);
					ed.putString("DrOrPatient", Login.DrOrPatient);


					//country  residency
					ed.putString("country", user_info.getString("country"));
					ed.putString("address", user_info.getString("residency"));
					if (user_info.has("pin_code")) {
						ed.putString("pincode", user_info.getString("pin_code"));
					}
					if (user_info.has("insurance")) {
						ed.putString("insurance", user_info.getString("insurance"));
						if (!user_info.getString("insurance").isEmpty()) {
							ed.putBoolean("isInsuranceInfoAdded", true);
						}
					}
					if (user_info.has("policy_number")) {
						ed.putString("policy_number", user_info.getString("policy_number"));
					}
					if (user_info.has("insurance_group")) {
						ed.putString("group", user_info.getString("insurance_group"));
					}
					if (user_info.has("insurance_code")) {
						ed.putString("code", user_info.getString("insurance_code"));
					}
					if (user_info.has("vacation_mode")) {
						ed.putString("vacation_mode",user_info.getString("vacation_mode"));
					}
					if(user_info.has("folder_name")){
						ed.putString("folder_name",user_info.getString("folder_name"));
					}
					if(user_info.has("hospital_id")){
						ed.putString("hospital_id",user_info.getString("hospital_id"));
					}


					ed.putString("after_urgentcare_form", after_urgentcare_form);

					//"support_msg":"Dear valued customer, currently we are available to support you from Monday till Friday between 09:00 AM - 07:00 PM EST, for any query you may call us at the number (313)-974-6533 for support, you may also send an email to us at mdslivenow.mtcu@gmail.com",
					// "support_email":"mdslivenow.mtcu@gmail.com"
					/*String support_msg = jsonObject.optString("support_msg");
					String support_email = jsonObject.optString("support_email");
					ed.putString("support_text", support_msg);
					ed.putString("support_email", support_email);*/

					ed.commit();

					//String subUsrs = jsonObject.getString("sub_users");

					JSONArray subUsersArray = new JSONArray();//new JSONArray(subUsrs);


					SubUsersModel temp;

					for(int j = 0; j<subUsersArray.length(); j++) {

						temp = new SubUsersModel();

						temp.id = subUsersArray.getJSONObject(j).getString("id");
						temp.firstName = subUsersArray.getJSONObject(j).getString("first_name");
						temp.lastName = subUsersArray.getJSONObject(j).getString("last_name");
						temp.patient_id = subUsersArray.getJSONObject(j).getString("patient_id");
						temp.image = subUsersArray.getJSONObject(j).getString("image");
						temp.gender = subUsersArray.getJSONObject(j).getString("gender");
						temp.dob = subUsersArray.getJSONObject(j).getString("dob");
						temp.marital_status = subUsersArray.getJSONObject(j).getString("marital_status");
						temp.relationship = subUsersArray.getJSONObject(j).getString("relationship");
						temp.occupation = subUsersArray.getJSONObject(j).getString("occupation");

						temp.insurance = subUsersArray.getJSONObject(j).getString("insurance");

						temp.is_primary = subUsersArray.getJSONObject(j).getString("is_primary");

						temp.phone = subUsersArray.getJSONObject(j).getString("phone");
						temp.residency = subUsersArray.getJSONObject(j).getString("residency");
						temp.city = subUsersArray.getJSONObject(j).getString("city");
						temp.state = subUsersArray.getJSONObject(j).getString("state");
						temp.country = subUsersArray.getJSONObject(j).getString("country");
						temp.zipcode = subUsersArray.getJSONObject(j).getString("zipcode");

						DATA.allSubUsers.add(temp);

						temp  = null;
					}

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

					DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
					getOAuthToken();

				}else if(status.equals("error")) {
					customToast.showToast(msg, 0, 0);
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(apiName.equals(ApiManager.PATIENT__SOCIAL_LOGIN)){

			try {
				JSONObject jsonObject = new JSONObject(content);

				String after_urgentcare_form = jsonObject.optString("after_urgentcare_form");

				String status = jsonObject.getString("status");
				String msg = "";
				if(jsonObject.has("msg")) {
					msg = jsonObject.getString("msg");
				}

				if(status.equals("success")) {

					String userStr = jsonObject.getString("user");
					JSONObject user_info = new JSONObject(userStr);

					DATA.allSubUsers = new ArrayList<SubUsersModel>();

					SubUsersModel temp1 = new SubUsersModel();

					temp1.id = user_info.getString("id");
					temp1.firstName = user_info.getString("first_name");
					temp1.lastName = user_info.getString("last_name");;
					temp1.image = user_info.getString("image");
					temp1.occupation = user_info.getString("occupation");
					temp1.marital_status = user_info.getString("marital_status");
					temp1.dob = user_info.getString("birthdate");
					temp1.gender = user_info.getString("gender");
					temp1.relationship = "Primary Member";//"Parent user"  "Main User"  "Primary User"

					temp1.phone = user_info.getString("phone");
					temp1.residency = user_info.getString("residency");
					temp1.city = user_info.getString("city");
					temp1.state = user_info.getString("state");
					temp1.country = user_info.getString("country");
					temp1.zipcode = user_info.getString("zipcode");

					DATA.allSubUsers.add(temp1);
					sharedPrefsHelper.saveParentUser(temp1);

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("id", user_info.getString("id"));
					ed.putString("first_name", user_info.getString("first_name"));
					ed.putString("last_name", user_info.getString("last_name"));
					ed.putString("email", user_info.getString("email"));
					ed.putString("username", user_info.getString("username"));
					ed.putString("gender", user_info.getString("gender"));
					ed.putString("birthdate", user_info.getString("birthdate"));
					ed.putString("phone", user_info.getString("phone"));
					ed.putString("image", user_info.getString("image"));
					ed.putString("qb_id", user_info.getString("qb_id"));
					ed.putString("reg_date", user_info.getString("reg_date"));
					ed.putString("is_online", user_info.getString("is_online"));
					ed.putString("occupation", user_info.getString("occupation"));

					ed.putString("city", user_info.getString("city"));
					ed.putString("state", user_info.getString("state"));

					ed.putString("zipcode", user_info.getString("zipcode"));
					ed.putString("marital_status", user_info.getString("marital_status"));
					ed.putBoolean("HaveShownPrefs",true);
					ed.putString("DrOrPatient", Login.DrOrPatient);


					//country  residency
					ed.putString("country", user_info.getString("country"));
					ed.putString("address", user_info.getString("residency"));
					if (user_info.has("pin_code")) {
						ed.putString("pincode", user_info.getString("pin_code"));
					}
					if (user_info.has("insurance")) {
						ed.putString("insurance", user_info.getString("insurance"));
						if (!user_info.getString("insurance").isEmpty()) {
							ed.putBoolean("isInsuranceInfoAdded", true);
						}
					}
					if (user_info.has("policy_number")) {
						ed.putString("policy_number", user_info.getString("policy_number"));
					}
					if (user_info.has("insurance_group")) {
						ed.putString("group", user_info.getString("insurance_group"));
					}
					if (user_info.has("insurance_code")) {
						ed.putString("code", user_info.getString("insurance_code"));
					}
					if (user_info.has("vacation_mode")) {
						ed.putString("vacation_mode",user_info.getString("vacation_mode"));
					}
					if(user_info.has("folder_name")){
						ed.putString("folder_name",user_info.getString("folder_name"));
					}
					if(user_info.has("hospital_id")){
						ed.putString("hospital_id",user_info.getString("hospital_id"));
					}

					ed.putString("after_urgentcare_form", after_urgentcare_form);

					//"support_msg":"Dear valued customer, currently we are available to support you from Monday till Friday between 09:00 AM - 07:00 PM EST, for any query you may call us at the number (313)-974-6533 for support, you may also send an email to us at mdslivenow.mtcu@gmail.com",
					// "support_email":"mdslivenow.mtcu@gmail.com"
					/*String support_msg = jsonObject.optString("support_msg");
					String support_email = jsonObject.optString("support_email");
					ed.putString("support_text", support_msg);
					ed.putString("support_email", support_email);*/

					ed.commit();

					//String subUsrs = jsonObject.getString("sub_users");

					JSONArray subUsersArray = new JSONArray();//new JSONArray(subUsrs);


					SubUsersModel temp;

					for(int j = 0; j<subUsersArray.length(); j++) {

						temp = new SubUsersModel();

						temp.id = subUsersArray.getJSONObject(j).getString("id");
						temp.firstName = subUsersArray.getJSONObject(j).getString("first_name");
						temp.lastName = subUsersArray.getJSONObject(j).getString("last_name");
						temp.patient_id = subUsersArray.getJSONObject(j).getString("patient_id");
						temp.image = subUsersArray.getJSONObject(j).getString("image");
						temp.gender = subUsersArray.getJSONObject(j).getString("gender");
						temp.dob = subUsersArray.getJSONObject(j).getString("dob");
						temp.marital_status = subUsersArray.getJSONObject(j).getString("marital_status");
						temp.relationship = subUsersArray.getJSONObject(j).getString("relationship");
						temp.occupation = subUsersArray.getJSONObject(j).getString("occupation");

						temp.phone = subUsersArray.getJSONObject(j).getString("phone");
						temp.residency = subUsersArray.getJSONObject(j).getString("residency");
						temp.city = subUsersArray.getJSONObject(j).getString("city");
						temp.state = subUsersArray.getJSONObject(j).getString("state");
						temp.country = subUsersArray.getJSONObject(j).getString("country");
						temp.zipcode = subUsersArray.getJSONObject(j).getString("zipcode");

						DATA.allSubUsers.add(temp);

						temp  = null;
					}

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

					/*Intent intent1 = new Intent();
					intent1.setAction("com.app.onlinecare.START_SERVICE");
					activity.sendBroadcast(intent1);

					openActivity.open(SubUsersList.class, true);*/
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


				Intent intent1 = new Intent();
				intent1.setAction("com.app.onlinecare.START_SERVICE");
				activity.sendBroadcast(intent1);
				openActivity.open(SubUsersList.class, true);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if (apiName.equalsIgnoreCase(ApiManager.LABELS)){
			parseLabelsData(content);
		}
	}

	public void printKeyhash() {

		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				//System.out.println("--keyhash: "+Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}
	LoginButton fbLoginBtn;
	CallbackManager callbackManager;
	public void loginWithFaceBook(){

		fbLoginBtn = (LoginButton) findViewById(R.id.fbLoginBtn);
		fbLoginBtn.setReadPermissions("public_profile", "email", "user_friends");

		fbLoginBtn.registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.d("Success", "Login");

						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject object, GraphResponse response) {
										Log.v("LoginActivity", response.toString());
										System.out.println("-- json "+object.toString()+"\n-- graph "+response.toString());
										// Application code
										try{
											String email = object.getString("email");
											String fullName = object.getString("name"); //
											String id = object.getString("id");
											String gender = object.getString("gender");
											String imageUrl = "";
											if (object.has("picture")) {
												imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
												// set profile image to imageview using Picasso or Native methods
												System.out.println("image url: "+imageUrl);
											}

											RequestParams params = new RequestParams();
											params.put("gender", gender);
											params.put("social_id",id);
											params.put("email",email);
											params.put("picture",imageUrl);
											params.put("first_name",fullName);
											params.put("hospital_id", HOSPITAL_ID_EMCURA);
											params.put("from_social","facebook");
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

											params.put("current_app", CURRENT_APP);

											params.put("device_token", device_token);

											params.put("platform", "android");

											ApiManager apiManager = new ApiManager(ApiManager.PATIENT__SOCIAL_LOGIN,"post",params,apiCallBack,activity);
											apiManager.loadURL();

											LoginManager loginManager = LoginManager.getInstance();
											loginManager.logOut();

										}catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
						Bundle parameters = new Bundle();
						parameters.putString("fields", "id,name,email,gender,picture.type(large)");
						request.setParameters(parameters);
						request.executeAsync();
					}

					@Override
					public void onCancel() {
						customToast.showToast("Login cancelled",0,1);
					}

					@Override
					public void onError(FacebookException exception) {
						Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				});




		fbLoginBtn.performClick();
		fbLoginBtn.setPressed(true);
		fbLoginBtn.invalidate();
		fbLoginBtn.setPressed(false);
		fbLoginBtn.invalidate();
	}


	public void fbLogin2(){
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.d("Success", "Login");
						System.out.println("-- Login Resul: "+loginResult.toString());

						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject object, GraphResponse response) {
										Log.v("LoginActivity", response.toString());
										System.out.println("-- json "+object.toString()+"\n-- graph "+response.toString());
										// Application code
										try{
											String email = object.getString("email");
											String fullName = object.getString("name"); //
											String id = object.getString("id");
											//String gender = object.getString("gender");
											String imageUrl = "";
											if (object.has("picture")) {
												imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
												// set profile image to imageview using Picasso or Native methods
												System.out.println("image url: "+imageUrl);
											}

											RequestParams params = new RequestParams();
											params.put("gender", "_");
											params.put("social_id",id);
											params.put("email",email);
											params.put("picture",imageUrl);
											params.put("first_name",fullName);
											params.put("hospital_id", HOSPITAL_ID_EMCURA);
											params.put("from_social","facebook");
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

											params.put("current_app", CURRENT_APP);

											params.put("device_token", device_token);

											params.put("platform", "android");

											ApiManager apiManager = new ApiManager(ApiManager.PATIENT__SOCIAL_LOGIN,"post",params,apiCallBack,activity);
											apiManager.loadURL();

											LoginManager loginManager = LoginManager.getInstance();
											loginManager.logOut();

										}catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});

						Bundle parameters = new Bundle();
						parameters.putString("fields", "id,name,email,gender,picture.type(large)");
						request.setParameters(parameters);
						request.executeAsync();
					}

					@Override
					public void onCancel() {
						Toast.makeText(activity, "Login Cancel", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onError(FacebookException exception) {
						Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				});

		//LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
		LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
	}


	/*-- Request code: 9001 result code: 0 intentData: Intent { (has extras) }  google canceled
-- Request code: 9001 result code: -1 intentData: Intent { (has extras) }  google logged in
-- Request code: 140 result code: 0 intentData: Intent { (has extras) }     twitter Onbackpress
-- Request code: 140 result code: 1 intentData: Intent { (has extras) }     twitter canceled
-- Request code: 140 result code: -1 intentData: Intent { (has extras) }  twitter logged in
-- Request code: 64206 result code: 0 intentData: Intent { (has extras) }  facebook canceled
-- Request code: 64206 result code: -1 intentData: Intent { (has extras) }   facebook logged in*/
	private static final int RC_SIGN_IN_FACEBOOK = 64206;
	private static final int RC_SIGN_IN_TWITTER = 140;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("-- Request code: "+requestCode+" result code: "+resultCode+" intentData: "+data);

		if (requestCode == RC_SIGN_IN_GOOGLE) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}else if (requestCode == RC_SIGN_IN_FACEBOOK){
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}else if (requestCode == RC_SIGN_IN_TWITTER){
			loginButton.onActivityResult(requestCode, resultCode, data);
		}
	}

	private GoogleApiClient mGoogleApiClient;
	private static final int RC_SIGN_IN_GOOGLE = 9001;
	public void loginWithGoogle(){
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		if(mGoogleApiClient == null){
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
					.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
					.build();
		}

		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);

	}

	private void handleSignInResult(GoogleSignInResult result) {
		try {
			Log.d("", "handleSignInResult:" + result.isSuccess());
			System.out.println("--sign in result "+result.getSignInAccount().getEmail()+" ; "+result.getSignInAccount().getId()
					+" account: "+result.getSignInAccount().toString());
			if (result.isSuccess()) {
				// Signed in successfully, show authenticated UI.
				GoogleSignInAccount acct = result.getSignInAccount();
				System.out.println("--sign in information "+acct.getEmail());

				String id = acct.getId();
				String email = acct.getEmail();
				String imageUrl = "https://onlinecare.com/";
				if(acct.getPhotoUrl() != null){
					imageUrl = acct.getPhotoUrl().toString();
				}
				String fullName = acct.getDisplayName();

				RequestParams params = new RequestParams();
				params.put("gender", "_");
				params.put("social_id",id);
				params.put("email",email);
				params.put("picture",imageUrl);
				params.put("first_name",fullName);
				params.put("hospital_id", HOSPITAL_ID_EMCURA);
				params.put("from_social","google");
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

				params.put("current_app", CURRENT_APP);

				params.put("device_token", device_token);

				params.put("platform", "android");

				ApiManager apiManager = new ApiManager(ApiManager.PATIENT__SOCIAL_LOGIN,"post",params,apiCallBack,activity);
				apiManager.loadURL();
				mGoogleApiClient.disconnect();
			} else {
				// Signed out, show unauthenticated UI.
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	public void getOAuthToken(){
		RequestParams params = new RequestParams();
		params.put("grant_type", "client_credentials");
		params.put("client_id", "zohaib");
		params.put("client_secret", "123");
		params.put("code", "123");

		ApiManager apiManager = new ApiManager(ApiManager.CREATE_TOKEN,"post",params,apiCallBack,activity);
		apiManager.loadURL();
	}



	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
			gps = new GPSTracker(activity);
			if (!gps.canGetLocation()) {
				gps.showSettingsAlert();
			}
			customToast.showToast("Location permission had been granted.", 0, 0);
		} else {
			customToast.showToast("Location permission had been denied.", 0, 0);
		}
	}
	private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}




	public void getLabels(){
		String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
		if(!TextUtils.isEmpty(app_general_labels)){
			parseLabelsData(app_general_labels);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.LABELS,"post",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	private void parseLabelsData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);

			/*JSONArray patient_registeration = jsonObject.getJSONObject("data").getJSONArray("patient_registeration");
			Type listType = new TypeToken<ArrayList<LinkBean>>() {}.getType();
			linkBeans = new Gson().fromJson(patient_registeration.toString(), listType);
			linkAdapter = new LinkAdapter(activity, linkBeans);
			lvPrivacy.setAdapter(linkAdapter);
			lvPrivacy.setExpanded(true);*/

			sharedPrefsHelper.save("app_general_labels", content);

			String testLocSearchURL = jsonObject.getJSONObject("data").getJSONObject("test_locations").getString("url");
			sharedPrefsHelper.save("testLocSearchURL", testLocSearchURL);

			String city_search =  jsonObject.getJSONObject("data").getJSONObject("test_locations").getString("city_search");
			sharedPrefsHelper.save("city_search", city_search);

			String test_results_email = jsonObject.getJSONObject("data").getJSONObject("testresults").getString("email");
			sharedPrefsHelper.save("test_results_email", test_results_email);

			if(sharedPrefsHelper.get("debug_logs", false)){
				System.out.println("-- Test Loc Search URL : "+testLocSearchURL);
				System.out.println("-- city search URL : "+city_search);
				System.out.println("-- test results email : "+test_results_email);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}
}
