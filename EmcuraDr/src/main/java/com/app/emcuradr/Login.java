package com.app.emcuradr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.permission.PermissionsActivity;
import com.app.emcuradr.permission.PermissionsChecker;
import com.app.emcuradr.services.GPSTracker;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.Database;
import com.app.emcuradr.util.GloabalMethods;
import com.app.emcuradr.util.OpenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternet;
    CustomToast customToast;
    Dialog_CustomProgress customProgressDialog;
    OpenActivity openActivity;
    SharedPreferences prefs;

    TextView tvLoginForgotPass;
    RadioGroup rgOptions;
    Button btnLogin, btnSignup,btnAppUpdate;
    EditText etLoginUsername, etLoginPassword;
    ScrollView mainLayout;
    LinearLayout versionLayout;

    Database db;


    GPSTracker gps;
    double latitude, longitude;
    private boolean haveWeShownPreferences = false;
    private boolean isSubUserSelected = false;


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
        hospitalIdLogin = "13";
        getHospitalDataById(hospitalIdLogin);

        //check if already logged-in, get data from preferences if yes.

        haveWeShownPreferences = prefs.getBoolean("HaveShownPrefs", false);
        isSubUserSelected = prefs.getBoolean("subUserSelected", false);

        DATA.print("--online care prefsshown: " + haveWeShownPreferences);
        DATA.print("--online care isUserSelected: " + isSubUserSelected);

        if (!TextUtils.isEmpty(prefs.getString(HOSP_ID_PREFS_KEY, ""))) {
            HOSPITAL_ID_EMCURA = prefs.getString(HOSP_ID_PREFS_KEY, "");
        }
        hospitalIdLogin = HOSPITAL_ID_EMCURA;

        if (haveWeShownPreferences) {
            Intent intent = new Intent(activity, MainActivityNew.class);//Splash
            startActivity(intent);
            finish();
        } else {
            gps = new GPSTracker(activity);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                gps.stopUsingGPS();
            } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gps.showSettingsAlert();//moved down in onRequestPermissionsResult if permission not granted
            }
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.cancel(NOTIFICATION_ID);
            notificationManager.cancelAll();


            //---------------RunTime Permissions----------------------------------
            PermissionsChecker permissionsChecker = new PermissionsChecker(activity);
		/*if(permissionsChecker.lacksPermissions(PermissionsChecker.PERMISSIONS)){
			startActivity(new Intent(activity, PermissionsActivity.class));
			return;
		}else{
			//btnPermission.setText("All permissions granted");
		}*/
            if (permissionsChecker.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (alertDialogLoc != null) {
                    alertDialogLoc.dismiss();
                }
                alertDialogLoc =
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Location Permission Required ")
                                .setMessage("Please allow the application to access your device location. We need your location information for the patients to show them their nearby care providers within their area. The administrator can also see your location from where you logged in last time.")
                                .setPositiveButton("Allow Location", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,};
                                        ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                                    }
                                })
                                .setNegativeButton("Not Now", null)
                                .create();
                alertDialogLoc.setCanceledOnTouchOutside(false);
                alertDialogLoc.show();
            }
            //---------------RunTime Permissions----------------------------------
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
        versionLayout = findViewById(R.id.version_layout);
        mainLayout = findViewById(R.id.main_layout);
        btnAppUpdate = findViewById(R.id.btn_app_update);
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
        customProgressDialog = new Dialog_CustomProgress(activity);
        openActivity = new OpenActivity(activity);

        findViewById(R.id.btnSignupCont).setVisibility(View.INVISIBLE);//Doc registration removed by Jamal Nov 2018
        btnSignup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                openActivity.open(Signup.class, false);
            }
        });


        btnAppUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.app.emcuradr&hl=en_US"));
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                if (etLoginUsername.getText().toString().isEmpty() || etLoginPassword.getText().toString().isEmpty()) {
                    customToast.showToast("Please enter username and password", 0, 0);
                } else if (etLoginUsername.getText().toString().trim().equalsIgnoreCase("olc12345678")
                        && etLoginPassword.getText().toString().trim().equalsIgnoreCase("olc12345678")) {

					/*etLoginUsername.setText("");
					etLoginPassword.setText("");*/

                    new GloabalMethods(activity).showDebugLogsDialog();

                } else {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etLoginUsername.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(etLoginPassword.getWindowToken(), 0);

                    RequestParams params = new RequestParams();
                    params.put("username", etLoginUsername.getText().toString());
                    params.put("password", etLoginPassword.getText().toString());
                    if (latitude == 0.0 || longitude == 0.0) {
                        gps = new GPSTracker(activity);
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            gps.stopUsingGPS();
                        }
                    }
                    params.put("latitude", latitude + "");
                    params.put("longitude", longitude + "");

                    params.put("type", "doctor");
                    params.put("current_app", CURRENT_APP);
                    params.put("hospital_id", hospitalIdLogin);

                    params.put("device_token", device_token);
                    params.put("timezone", TimeZone.getDefault().getID());
                    params.put("platform", "android");

                    DATA.print("--latlong in params " + latitude + " " + longitude);

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("qb_username", etLoginUsername.getText().toString());
                    ed.putString("qb_password", etLoginPassword.getText().toString());
                    ed.commit();

                    ApiManager apiManager = new ApiManager(ApiManager.DOCTOR_LOGIN, "post", params, apiCallBack, activity);
                    apiManager.loadURL();


                }//if isEmpty ends
            }
        });


        TextView tvLabel = (TextView) findViewById(R.id.tvLabel);
        String styledText = "<font color='" + DATA.APP_THEME_RED_COLOR + "'>Virtual Care</font> services are currently available in the state of Michigan only for now. <br>(Additional states will be added in the near future)";
        tvLabel.setText(Html.fromHtml(styledText));


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            DATA.print("-- getInstanceId failed" + task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        device_token = task.getResult().getToken();
                        //saveToken(token);
                    }
                });

        ImageView ivLogo = findViewById(R.id.ivlogo);
        ivLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count > 0) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            count = 0;
                            DATA.print("-- Counter reset : " + count);
                        }
                    };
                    if (handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    handler = new Handler();
                    handler.postDelayed(runnable, 2000);  // clear counter if user does not touch for one sec
                }

                DATA.print("-- Counter : " + count);

                if (count == 5) {
                    int checkedItem = hospitalIdLogin.equalsIgnoreCase(Login.HOSPITAL_ID_EMCURA) ? 0 : 1;
                    String[] arr = {"Live/Production Mode", "Demo/Debug Mode"};
                    final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Switch Application Mode")
                            //.setMessage("Please select the application mode.")
                            .setSingleChoiceItems(arr, checkedItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hospitalIdLogin = which == 0 ? Login.HOSPITAL_ID_EMCURA : "1";
                                    String mode = hospitalIdLogin.equalsIgnoreCase(Login.HOSPITAL_ID_EMCURA) ? "Live/Production Mode" : "Demo/Debug Mode";
                                    customToast.showToast("Application has been switched to the " + mode + " successfully.", 0, 0);
                                    getHospitalDataById(hospitalIdLogin);
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
        });

        getHospitalDataById(prefs.getString("hospitalIdLogin", "13"));
    }

    private int count = 0;
    Handler handler;
    Runnable runnable;

    String device_token;

    //public static final String CURRENT_APP = "doctor";
    public static final String CURRENT_APP = "doctor_emcura";//by zohaib for emcura doc
    public static final String HOSP_ID_PREFS_KEY = "hospital_id_from_servicve";
    public static String HOSPITAL_ID_EMCURA = "13";//13 Emcura hosp id
    String hospitalIdLogin;

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.DOCTOR_LOGIN)) {
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = "";

                if (jsonObject.has("msg")) {
                    msg = jsonObject.getString("msg");
                }

                if (status.equals("success")) {

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
                    ed.putString("access_token", user_info.optString("access_token"));

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
                        ed.putString("vacation_mode", user_info.getString("vacation_mode"));
                    }
                    if (user_info.has("folder_name")) {
                        ed.putString("folder_name", user_info.getString("folder_name"));
                    }
					/*else
					{
						ed.putString("folder_name","onlinecareus");
					}*/
                    if (user_info.has("is_available_onlinecare")) {
                        ed.putString("is_available_onlinecare", user_info.getString("is_available_onlinecare"));
                    }

                    if (!user_info.optString("centers").isEmpty()) {
                        JSONArray centers = user_info.getJSONArray("centers");
                        String dr_clinics = "";
                        for (int i = 0; i < centers.length(); i++) {
                            String center_name = centers.getJSONObject(i).getString("center_name");
                            dr_clinics = dr_clinics + center_name;
                            if (i != (centers.length() - 1)) {
                                dr_clinics = dr_clinics + ", ";
                            }
                        }
                        ed.putString("dr_clinics", dr_clinics);
                    }

                    //String support_text = user_info.optString("support_text");
                    //ed.putString("support_text", support_text);
                    //"support_msg":"Dear valued customer, currently we are available to support you from Monday till Friday between 09:00 AM - 07:00 PM EST, for any query you may call us at the number (313)-974-6533 for support, you may also send an email to us at mdslivenow.mtcu@gmail.com",
                    // "support_email":"mdslivenow.mtcu@gmail.com"
                    String support_msg = jsonObject.optString("support_msg");
                    String support_email = jsonObject.optString("support_email");
                    ed.putString("support_text", support_msg);
                    ed.putString("support_email", support_email);

                    ed.putBoolean("HaveShownPrefs", true);
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
//								DATA.print("--onlinecare symptom name"+symptomsArray.getJSONObject(i).getString("symptom_name"));
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
//								DATA.print("--onlinecare condition name"+conditionsArray.getJSONObject(i).getString("condition_name"));
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
//								DATA.print("--onlinecare speciality name"+specialityArray.getJSONObject(i).getString("speciality_name"));
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
                    DATA.baseUrl = DATA.ROOT_Url + prefs.getString("folder_name", "no_folder_recieved_in_login") + DATA.POST_FIX;
                    //getOAuthToken();
                    openActivity.open(MainActivityNew.class, true);//Splash

                } else if (status.equals("error")) {
                    customToast.showToast(msg, 0, 0);
                }

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (apiName.equals(ApiManager.CREATE_TOKEN)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String access_token = jsonObject.getString("access_token");
                prefs.edit().putString("access_token", access_token).commit();


                openActivity.open(MainActivityNew.class, true);//Splash

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(apiName.equals(ApiManager.CHECK_APP_UPDATES)){
            try {
                compareVersions(content);
                DATA.print(content);
            } catch (Exception e) {
                customToast.showToast(e.getMessage(),0,0);
            }

        }


    }

    public void compareVersions(String content){


        try {
            JSONObject jsonObject = new JSONObject(content);
            String app_version = jsonObject.getString("app_version");//"1.1.0"
            if(TextUtils.isEmpty(app_version)){
                return;
            }
            String myAppVersion = BuildConfig.VERSION_NAME;//"1.0.19"
            String coloredVer = "<b><font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+app_version+"</font></b>";

            String updateMsg = "New version "+coloredVer+" is available on the google play. Please keep your app up to date in order to get latest features and better performance.";
            try {

                String myAppVerBeforeLastDecimal = myAppVersion.substring(0, myAppVersion.lastIndexOf("."));
                String storeAppVerBeforeLastDecimal = app_version.substring(0, app_version.lastIndexOf("."));
                DATA.print("-- substr BeforeLastDecimal myVer : "+myAppVerBeforeLastDecimal+" , Store Ver: "+ storeAppVerBeforeLastDecimal);

                double myVerPre = Double.parseDouble(myAppVerBeforeLastDecimal);
                double storeVerPre = Double.parseDouble(storeAppVerBeforeLastDecimal);

                DATA.print("-- before last decimal after cast to doub myVer: "+myVerPre+" ** PlayStore ver: "+storeVerPre);

                String myAppVerAfterLastDecimal = myAppVersion.substring(myAppVersion.lastIndexOf(".")+1);
                String storeAppVerAfterLastDecimal = app_version.substring(app_version.lastIndexOf(".")+1);

                DATA.print("-- substr AfterLastDecimal myVer : "+myAppVerAfterLastDecimal+" , Store Ver: "+ storeAppVerAfterLastDecimal);

                int myVerPost = Integer.parseInt(myAppVerAfterLastDecimal);
                int storeVerPost = Integer.parseInt(storeAppVerAfterLastDecimal);
                DATA.print("-- after last decimal after cast to int myVer: "+myVerPost+" ** PlayStore ver: "+storeVerPost);


                if(myVerPre < storeVerPre ||  myVerPost < storeVerPost){
                    versionLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                }
                else {
                    versionLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);

                }
            }catch (Exception e){
                e.printStackTrace();
                if(! myAppVersion.equalsIgnoreCase(app_version)){

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


	/*private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            DATA.print("--service already running"+serviceClass.getName());

	        	return true;

	        }
	    }
        DATA.print("--service not running"+serviceClass.getName());
	    return false;
	}*/

    public void getOAuthToken() {
        RequestParams params = new RequestParams();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "zohaib");
        params.put("client_secret", "123");
        params.put("code", "123");

        ApiManager apiManager = new ApiManager(ApiManager.CREATE_TOKEN, "post", params, apiCallBack, activity);
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

    private void getHospitalDataById(String hospitalID) {
        String RequestUrl = DATA.SIGNUP_URL + "hospitals/getHospitalDataById";
        RequestParams params = new RequestParams();
        params.put("hospital_id", hospitalID);

        customProgressDialog.showProgressDialog();
        DATA.print("-- params in getHospitalDataById " + params);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(RequestUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String content = new String(responseBody);

                    JSONObject jsonObject = new JSONObject(content);

                    DATA.print("-- result " + jsonObject);
                    customProgressDialog.dismissProgressDialog();

                    JSONObject jsonObj = jsonObject.getJSONObject("hospital_data");

                    if (jsonObj.has("folder_name")) {
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("folder_name", jsonObj.getString("folder_name"));
                        ed.commit();

                        DATA.baseUrl = DATA.ROOT_Url + prefs.getString("folder_name", "no_folder_recieved_in_login") + DATA.POST_FIX;
                    }
                    checkAppVersion();
                } catch (JSONException e) {
                    customProgressDialog.dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                DATA.print("-- getHospitalById onFailure " + error.getMessage());
                customProgressDialog.dismissProgressDialog();
            }
        });
    }

    public void checkAppVersion(){

        RequestParams params = new RequestParams();
        params.put("platform", "android");
        params.put("current_app", CURRENT_APP);

        ApiManager apiManager = new ApiManager(ApiManager.CHECK_APP_UPDATES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }
}
