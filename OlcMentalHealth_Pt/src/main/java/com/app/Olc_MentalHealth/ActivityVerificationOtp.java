package com.app.Olc_MentalHealth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.model.SubUsersModel;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.HideShowKeypad;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActivityVerificationOtp extends BaseActivity{


    OtpView otp_viewEmail , otp_viewSms;
    Button btnProceed , btnResendCode,btnCancel;
    TextView tvOtpTimer;

    CountDownTimer countDownTimer;
    LinearLayout layResendCode;
    ImageView ivClose;
    SharedPreferences prefs;

    Boolean SignInStatus;
    String patientID , userEmail , userPhone;
    TextView txtUserEnteredEmail , txtUserEnteredPhone;

    String userEmailFromsocial , userPhoneFromSocial;
    AlertDialog alertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otp_verify);

        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("OTP Verification");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        otp_viewEmail = findViewById(R.id.otp_viewEmail);
        otp_viewSms = findViewById(R.id.otp_viewSms);
        btnProceed = findViewById(R.id.btnProceed);
        tvOtpTimer = findViewById(R.id.tvOtpTimer);
        ivClose = findViewById(R.id.ivClose);
        txtUserEnteredEmail = findViewById(R.id.txtUserEnteredEmail);
        txtUserEnteredPhone = findViewById(R.id.txtUserEnteredPhone);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnCancel = findViewById(R.id.btnCancel);

        layResendCode = findViewById(R.id.layResendCode);

        tvOtpTimer.setTypeface(Typeface.createFromAsset(getAssets(), "digital-7.ttf"));

        SignInStatus = prefs.getBoolean("UserSignupSuccess", false);
        patientID = prefs.getString("id", "");
        userEmail = prefs.getString("userEnteremail" , "");
        userPhone = prefs.getString("userEnterpass" , "");
        userEmailFromsocial = prefs.getString("userEmailFromSocial" ,"");
        userPhoneFromSocial = prefs.getString("userPhoneFromSocial" , "");



        btnCancel.setOnClickListener(view ->
        {

            onBackPressed();
//            alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
//                    .setTitle(getResources().getString(R.string.app_name))
//                    .setMessage("Are you sure? Do you want to cancel the verification process? please make sure your account will not be verified if you cancel the verification.").
//                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            // TODO Auto-generated method stub
//                            onBackPressed();
//                        }
//                    }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            // TODO Auto-generated method stub
//                            alertDialog.dismiss();
//                        }
//                    }).create();
//            alertDialog.show();
        });

        //if user lost the otp then ask to resend otp for verification
        if (SignInStatus == true) {
            layResendCode.setVisibility(View.VISIBLE);
            btnProceed.setEnabled(false);
            btnResendCode.setEnabled(true);
            tvOtpTimer.setVisibility(View.GONE);
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            txtUserEnteredPhone.setText("PHONE: "+userPhone);
            txtUserEnteredEmail.setText("EMAIL: "+userEmail);
        }
        //if user from signup screen then timmer will be start auto
        if (Signup.otpTimerStatus.equals("1")) {
            tvOtpTimer.setVisibility(View.VISIBLE);
            layResendCode.setVisibility(View.GONE);
            Signup.otpTimerStatus = "0";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            long milis = 60000; //1 mnt
            countDownTimer = new CountDownTimer(milis, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                    DATA.print("-- contdowntimer is running GM");
                    btnResendCode.setEnabled(false);
                }

                public void onFinish() {
                    layResendCode.setVisibility(View.VISIBLE);
                    btnResendCode.setEnabled(true);
                }

            };
            countDownTimer.start();

            txtUserEnteredEmail.setText("EMAIL: "+userEmail);
            txtUserEnteredPhone.setText("PHONE: "+userPhone);
            DATA.print("-- UserSignUpSuccess " + SignInStatus);
        }

        //login from social
        if (ActivityVerificationSocial.loginFrom.equals("1"))
        {
            tvOtpTimer.setVisibility(View.VISIBLE);
            Signup.otpTimerStatus = "0";
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            long milis = 60000; //1 mnt
            countDownTimer = new CountDownTimer(milis, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                    DATA.print("-- contdowntimer is running GM");
                    btnResendCode.setEnabled(false);
                }

                public void onFinish() {
                    layResendCode.setVisibility(View.VISIBLE);
                    btnResendCode.setEnabled(true);
                }

            };
            countDownTimer.start();

            txtUserEnteredEmail.setText("EMAIL: "+userEmailFromsocial);
            txtUserEnteredPhone.setText("PHONE: "+userPhoneFromSocial);
            DATA.print("-- UserSignUpSuccess " + SignInStatus);
        }

        //login from simple login
        else if (ActivityVerificationSocial.loginFrom.equals("2"))
        {
            reSendOtp();
            tvOtpTimer.setVisibility(View.GONE);
            layResendCode.setVisibility(View.GONE);
//            if (countDownTimer != null) {
//                countDownTimer.cancel();
//                countDownTimer = null;
//            }
//            long milis = 60000; //1 mnt
//            countDownTimer = new CountDownTimer(milis, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
//                    btnResendCode.setEnabled(true);
//                }
//
//                public void onFinish() {
//                    layResendCode.setVisibility(View.VISIBLE);
//                    btnResendCode.setEnabled(true);
//                }
//
//            };
//            countDownTimer.start();
            txtUserEnteredEmail.setText("EMAIL: "+userEmail);
            txtUserEnteredPhone.setText("PHONE: "+userPhone);
            DATA.print("-- UserSignUpSuccess " + SignInStatus);
        }

        otp_viewSms.setOtpCompletionListener(otp -> {
            //otpCode = otp;
            DATA.print("-- onOtpCompleted OTP : "+ otp);
            new HideShowKeypad(activity).hidekeyboardOnDialog();
            btnProceed.setEnabled(true);
        });
        otp_viewSms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                btnProceed.setEnabled(otp_viewSms.getText().toString().length() == 4);
            }
        });

        otp_viewEmail.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //otpCode = otp;
                DATA.print("-- onOtpCompleted OTP : "+ otp);
                new HideShowKeypad(activity).hidekeyboardOnDialog();
                //  btnProceed.setEnabled(true);
            }
        });
        otp_viewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                //btnProceed.setEnabled(otp_viewEmail.getText().toString().length() == 4);
            }
        });

        btnProceed.setOnClickListener(view -> {
           // String otpCodeEmail = otp_viewEmail.getText().toString();
            String otpCodeSms = otp_viewSms.getText().toString();

            if(otpCodeSms.length() < 4){
                customToast.showToast("Please enter the complete OTP code",0,1);
            }else {
                if (ActivityVerificationSocial.loginFrom.equals("1"))
                {
                    optVerifyfromSocial(otpCodeSms);
                }
                else if (ActivityVerificationSocial.loginFrom.equals("0"))
                {
                    optVerify(otpCodeSms);
                }
                else
                {
                    optVerify(otpCodeSms);
                }
            }
        });

        btnResendCode.setOnClickListener(view ->
        {
            reSendOtp();
        });

        ivClose.setOnClickListener(view ->
        {
            if(countDownTimer != null){
                countDownTimer.cancel();
                countDownTimer = null;
            }
            onBackPressed();
        });
    }

    private void optVerify(String otpCodeSms){
        RequestParams params = new RequestParams();
        params.put("patient_id" , patientID);
        params.put("sms_code" , otpCodeSms);
        ApiManager apiManager = new ApiManager(ApiManager.OTP_VERIFY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void optVerifyfromSocial(String otpCodeSms){
        RequestParams params = new RequestParams();
        params.put("patient_id" , patientID);
        params.put("sms_code" , otpCodeSms);
        params.put("email" , userEmailFromsocial);
        params.put("phone" , userPhoneFromSocial);
        ApiManager apiManager = new ApiManager(ApiManager.OTP_VERIFY_SOCIAL, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void reSendOtp()
    {
        RequestParams params = new RequestParams();
        params.put("patient_id" , patientID);
        ApiManager apiManager = new ApiManager(ApiManager.RESEND_OTP_CODE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private String convrtTime(long millis) {

//		long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

//		return hours+" hours, "+minutes+" minutes";

        String h = hours < 10 ? "0"+hours : ""+hours;
        String m = minutes < 10 ? "0"+minutes : ""+minutes;
        String s = seconds < 10 ? "0"+seconds : ""+seconds;
        return h+":"+m+":"+s;
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.OTP_VERIFY)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                DATA.print("-- chk"+ jsonObject);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equals("success")) {
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    SharedPreferences.Editor ed1 = prefs.edit();
                    ed1.putBoolean("UserSignupSuccess" , false);
                    ed1.commit();
                    ActivityVerificationSocial.loginFrom = "0";
                    openActivity.open(Login.class, true);
                    customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."
                }
                else if (status.equals("error"))
                {
                    customToast.showToast(msg, 0, 1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (apiName.equals(ApiManager.OTP_VERIFY_SOCIAL)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                DATA.print("-- chk"+jsonObject.toString());

                String after_urgentcare_form = jsonObject.optString("after_urgentcare_form");
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equals("success")) {
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    ActivityVerificationSocial.loginFrom = "0";
                    SharedPreferences.Editor edd = prefs.edit();
                    edd.putBoolean("UserSignupSuccess" , false);
                    edd.commit();
                    customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."

                    String userStr = jsonObject.getString("user");
                    JSONObject user_info = new JSONObject(userStr);

                    DATA.allSubUsers = new ArrayList<SubUsersModel>();

                    SubUsersModel temp1 = new SubUsersModel();

                    temp1.id = user_info.getString("id");
                    temp1.firstName = user_info.getString("first_name");
                    temp1.lastName = user_info.getString("last_name");
                    ;
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
                    ed.putBoolean("HaveShownPrefs", true);
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
                        ed.putString("vacation_mode", user_info.getString("vacation_mode"));
                    }
                    if (user_info.has("folder_name")) {
                        ed.putString("folder_name", user_info.getString("folder_name"));
                    }
                    if (user_info.has("hospital_id")) {
                        ed.putString("hospital_id", user_info.getString("hospital_id"));
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

                    for (int j = 0; j < subUsersArray.length(); j++) {

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

                        temp = null;
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

					/*Intent intent1 = new Intent();
					intent1.setAction("com.app.onlinecare.START_SERVICE");
					activity.sendBroadcast(intent1);

					openActivity.open(SubUsersList.class, true);*/
                    DATA.baseUrl = DATA.ROOT_Url + prefs.getString("folder_name", "no_folder_recieved_in_login") + DATA.POST_FIX;
                    getOAuthToken();
                }
                else if (status.equals("error"))
                {
                    customToast.showToast(msg, 0, 1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (apiName.equals(ApiManager.RESEND_OTP_CODE))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
//                DATA.print("-- chk"+jsonObject.toString());
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equalsIgnoreCase("success"))
                {
                    tvOtpTimer.setVisibility(View.VISIBLE);
                    btnResendCode.setText("Resend Code");
                    customToast.showToast(msg, 0, 1);
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    long milis = 60000; //1 mnt
                    countDownTimer = new CountDownTimer(milis, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                            DATA.print("-- contdowntimer is running GM");
                            btnResendCode.setEnabled(false);
                        }
                        public void onFinish() {
                            layResendCode.setVisibility(View.VISIBLE);
                            btnResendCode.setEnabled(true);
                        }

                    };
                    countDownTimer.start();
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    customToast.showToast(msg, 0, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(apiName.equals(ApiManager.CREATE_TOKEN)){
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
        }
    }


    @Override
    public void onBackPressed() {

        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure ? Do you want to exit the verification process. Please make sure that your account will not be created.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putBoolean("UserSignupSuccess" , false);
                        ed.commit();
                        finish();
                    }
                })
                .setNegativeButton("Not Now", null)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();
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

}
