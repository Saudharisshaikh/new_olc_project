package com.app.msu_cp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.Dialog_CustomProgress;
import com.app.msu_cp.permission.PermissionsChecker;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.Database;
import com.app.msu_cp.util.GPSTracker;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.OpenActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;


public class Login extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternet;
    CustomToast customToast;
    OpenActivity openActivity;
    ApiCallBack apiCallBack;
    Dialog_CustomProgress customProgressDialog;

    TextView tvLoginForgotPass;
    Button btnLogin, btnSignup;
    EditText etLoginUsername, etLoginPassword;

    private boolean haveWeShownPreferences = false;

    GPSTracker gps;
    double latitude, longitude;

    public static final String CURRENT_APP = "mh_therapist",//""msu_cp",
            HOSP_ID_PREFS_KEY = "hospital_id_from_servicve";

    public static String HOSPITAL_ID_reliance = "51";//Reliance hosp id

    String hospitalIdLogin = "51";

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

        if (!TextUtils.isEmpty(prefs.getString(HOSP_ID_PREFS_KEY, ""))) {
            HOSPITAL_ID_reliance = prefs.getString(HOSP_ID_PREFS_KEY, "");
        }
        hospitalIdLogin = HOSPITAL_ID_reliance;

        //Ahmer code
        getHospitalDataById(prefs.getString("hospitalIdLogin", "51"));

        //check hospital id here ahmer move this code here from onResume - 15/07/22
		/*if (TextUtils.isEmpty(prefs.getString(HOSP_ID_PREFS_KEY, ""))) {
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString(HOSP_ID_PREFS_KEY, HOSPITAL_ID_reliance);
			ed.commit();
		}*/

        //check if already logged-in, get data from preferences if yes.
        haveWeShownPreferences = prefs.getBoolean("HaveShownPrefs", false);

        DATA.print("--online care prefsshown: " + haveWeShownPreferences);

        if (haveWeShownPreferences) {

            Intent intent1 = new Intent();
            intent1.setAction("com.app.onlinecarespecialist.START_SERVICE");
            activity.sendBroadcast(intent1);

            Intent intent = new Intent(activity, MainActivityNew.class);// Splash
            startActivity(intent);
            finish();
        } else {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.cancel(NOTIFICATION_ID);
            notificationManager.cancelAll();


            gps = new GPSTracker(activity);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                gps.stopUsingGPS();
            } else if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gps.showSettingsAlert();//moved down in onRequestPermissionsResult if permission not granted
                //gps.enableLocationPopup();
            }


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

        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().hide();

        activity = Login.this;
        apiCallBack = this;
        checkInternet = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customProgressDialog = new Dialog_CustomProgress(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        Database db = new Database(activity);
        db.createDatabase();

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        etLoginUsername = (EditText) findViewById(R.id.et_email);
        etLoginPassword = (EditText) findViewById(R.id.et_password);

        tvLoginForgotPass = (TextView) findViewById(R.id.tvLoginForgotPass);
        tvLoginForgotPass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openActivity.open(ForgotPassword.class, false);
            }
        });

        btnSignup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openActivity.open(Signup.class, false);
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
                    params.put("type", "specialist");
                    params.put("current_app", CURRENT_APP);

                    params.put("device_token", device_token);
                    params.put("platform", "android");
                    params.put("timezone", TimeZone.getDefault().getID());
                    params.put("hospital_id", hospitalIdLogin /*prefs.getString(HOSP_ID_PREFS_KEY,"")*/);

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

                    ApiManager apiManager = new ApiManager(ApiManager.DOCTOR_LOGIN, "post", params, apiCallBack, activity);
                    apiManager.loadURL();

                }//if isEmpty ends
            }
        });

        TextView tvLabel = (TextView) findViewById(R.id.tvLabel);
        String styledText = "<font color='" + DATA.APP_THEME_RED_COLOR + "'>OnlineCare.com</font> services are currently available in the state of Michigan only for now. <br>(Additional states will be added in the near future)";
        tvLabel.setText(Html.fromHtml(styledText));


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        DATA.print("-- Fetching FCM registration token failed" + task.getException());
                        task.getException().printStackTrace();
                        return;
                    }
                    device_token = task.getResult();
                    String msg = getString(R.string.msg_token_fmt, device_token);
                    DATA.print("-- " + msg);
                });

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
                    int checkedItem = hospitalIdLogin.equalsIgnoreCase(Login.HOSPITAL_ID_reliance) ? 0 : 1;
                    String[] arr = {"Live/Production Mode", "Demo/Debug Mode"};
                    final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Switch Application Mode")
                            //.setMessage("Please select the application mode.")
                            .setSingleChoiceItems(arr, checkedItem, (dialog, which) -> {
                                hospitalIdLogin = which == 0 ? Login.HOSPITAL_ID_reliance : "1";
                                SharedPreferences.Editor ed = prefs.edit();
                                ed.putString("hospitalIdLogin", hospitalIdLogin);
                                ed.commit();
                                DATA.print("-- hospitalIdLogin after convert : " + prefs.getString("hospitalIdLogin", ""));
                                String mode = hospitalIdLogin.equalsIgnoreCase(Login.HOSPITAL_ID_reliance) ? "Live/Production Mode" : "Demo/Debug Mode";
                                customToast.showToast("Application has been switched to the " + mode + " successfully.", 0, 0);
                                getHospitalDataById(hospitalIdLogin);
                                dialog.dismiss();
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
        });

        getHospitalDataById(prefs.getString("hospitalIdLogin", "51"));
    }

    private int count = 0;
    Handler handler;
    Runnable runnable;

    String device_token;

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
                    ed.putString("address", user_info.getString("address1"));
                    ed.putString("access_token", user_info.optString("access_token"));

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
                    if (user_info.has("qualification")) {
                        ed.putString("qualification", user_info.getString("qualification"));
                    }
                    if (user_info.has("career_data")) {
                        ed.putString("career_data", user_info.getString("career_data"));
                    }
                    if (user_info.has("doctor_category")) {
                        ed.putString("doctor_category", user_info.getString("doctor_category"));
                    }
                    if (user_info.has("vacation_mode")) {
                        ed.putString("vacation_mode", user_info.getString("vacation_mode"));
                    }
                    if (user_info.has("folder_name")) {
                        ed.putString("folder_name", user_info.getString("folder_name"));
                    }

                    //"support_msg":"Dear valued customer, currently we are available to support you from Monday till Friday between 09:00 AM - 07:00 PM EST, for any query you may call us at the number (313)-974-6533 for support, you may also send an email to us at mdslivenow.mtcu@gmail.com",
                    // "support_email":"mdslivenow.mtcu@gmail.com"
					/*String support_msg = jsonObject.optString("support_msg");
					String support_email = jsonObject.optString("support_email");
					ed.putString("support_text", support_msg);
					ed.putString("support_email", support_email);*/

//									ed.putString("qb_id", user_info.getString("qb_id"));
//									ed.putString("reg_date", user_info.getString("reg_date"));
//									ed.putString("is_online", user_info.getString("is_online"));
//									ed.putString("marital_status", user_info.getString("marital_status"));address1 address2 country
                    ed.putBoolean("HaveShownPrefs", true);
                    ed.putString("DrOrPatient", "doctor");
                    ed.commit();

                    DATA.print("--check : " + prefs.getString("id", ""));
									/*Intent intent1 = new Intent();
									intent1.setAction("com.app.onlinecarespecialist.START_SERVICE");
									activity.sendBroadcast(intent1);*/


                    //openActivity.open(MainActivityNew.class, true);// Splash
                    DATA.baseUrl = DATA.ROOT_Url + prefs.getString("folder_name", "no_folder_recieved_in_login") + DATA.POST_FIX;
//					getOAuthToken();
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
    }

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
        customProgressDialog.showProgressDialog();
        String RequestUrl = DATA.SIGNUP_URL + "hospitals/getHospitalDataById";
        RequestParams params = new RequestParams();
        params.put("hospital_id", hospitalID);

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
                        //ed.putString("folder_name", "onlinecare_newdesign");
                        ed.commit();
                        DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
                    }

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

}
