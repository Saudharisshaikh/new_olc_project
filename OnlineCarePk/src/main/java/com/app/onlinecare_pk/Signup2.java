package com.app.onlinecare_pk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.onlinecare_pk.api.ApiCallBack;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.api.CustomSnakeBar;
import com.app.onlinecare_pk.util.CheckInternetConnection;
import com.app.onlinecare_pk.util.CustomToast;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.HideShowKeypad;
import com.app.onlinecare_pk.util.OpenActivity;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class Signup2 extends AppCompatActivity implements ApiCallBack {


    Activity activity;
    CheckInternetConnection checkInternet;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    OpenActivity openActivity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    EditText etSignupName,etSignupMobNo;
    Button btnSignupSubmit;

    String phone = "";
    String first_name = "";

    Typeface timerfonts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup2);

        activity = Signup2.this;
        checkInternet = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        openActivity = new OpenActivity(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        timerfonts = Typeface.createFromAsset(getAssets(),"digital-7.ttf");

        etSignupName = findViewById(R.id.etSignupName);
        etSignupMobNo = findViewById(R.id.etSignupMobNo);
        btnSignupSubmit = findViewById(R.id.btnSignupSubmit);


        btnSignupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etSignupMobNo.getText().toString().trim();
                first_name = etSignupName.getText().toString().trim();

                boolean validate = true;
                if(TextUtils.isEmpty(first_name)){ etSignupName.setError("Required");validate = false;}
                if(TextUtils.isEmpty(phone)){etSignupMobNo.setError("Required");validate = false;}
                if(!phone.startsWith("0") || phone.length() != 11){customToast.showToast("Please enter your valid mobile number",0,1);validate = false;}

                if(validate){
                    otpRequest();
                }
            }
        });
    }


    private void otpRequest(){
        RequestParams params = new RequestParams();
        params.put("phone", phone);

        ApiManager apiManager = new ApiManager(ApiManager.OTP_REQUEST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }



    //String code;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.OTP_REQUEST)){
            //{"code":5948,"status":"success","message":"OTP has been sent"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if(status.equalsIgnoreCase("success")){
                    //code = jsonObject.getString("code");
                    customToast.showToast("Code is : "+jsonObject.getString("code"), 0, 1);

                    if(dialogVerifyOTPDismiss != null){
                        dialogVerifyOTPDismiss.dismiss();
                    }

                    showVerifyOtpDialog();
                }else {
                    customToast.showToast(message, 0, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.OTP_VERIFY)){
            //{"status":"error","message":"Invalid OTP"}
            //{"status":"success","message":"Phone has been verified"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                customToast.showToast(message,0,1);
                if(status.equalsIgnoreCase("success")){

                    if(dialogVerifyOTPDismiss != null){
                        dialogVerifyOTPDismiss.dismiss();
                    }

                    RequestParams params = new RequestParams();
                    params.put("phone", phone);
                    params.put("first_name", first_name);
                    params.put("patient_current_app", Login.CURRENT_APP);
                    params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);

                    ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP_PK,"post",params,apiCallBack, activity);
                    apiManager.loadURL();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.PATIENT_SIGNUP_PK)){

            //{"status":"success","msg":"Your account has been created. Please check sms inbox to get account details",
            // "data":{"user_id":5,"username":"03009285023","password":3201}}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Info")
                        .setMessage(msg)
                        .setPositiveButton("Done",null).create();
                if(status.equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            try {
                                Signup.is_signup_successfull = true;
                                Signup.signupUsername = jsonObject.getJSONObject("data").getString("username");
                                Signup.signupPassword = jsonObject.getJSONObject("data").getString("password");
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                            }
                        }
                    });
                }
                alertDialog.show();


            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }





    //String otpCode = "";
    Dialog dialogVerifyOTPDismiss;
    CountDownTimer countDownTimer;
    public void showVerifyOtpDialog(){
        final Dialog dialogVerifyOTP = new Dialog(activity);
        dialogVerifyOTP.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogVerifyOTP.setContentView(R.layout.dialog_verify_otp);
        dialogVerifyOTP.setCancelable(false);

        Button btnProceed = dialogVerifyOTP.findViewById(R.id.btnProceed);
        ImageView ivClose = dialogVerifyOTP.findViewById(R.id.ivClose);
        OtpView otp_view = dialogVerifyOTP.findViewById(R.id.otp_view);

        LinearLayout layResendCode = dialogVerifyOTP.findViewById(R.id.layResendCode);
        TextView tvOtpTimer = dialogVerifyOTP.findViewById(R.id.tvOtpTimer);
        Button btnResendCode = dialogVerifyOTP.findViewById(R.id.btnResendCode);

        tvOtpTimer.setTypeface(timerfonts);

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //otpCode = otp;
                DATA.print("-- onOtpCompleted OTP : "+ otp);
                new HideShowKeypad(activity).hidekeyboardOnDialog();
                btnProceed.setEnabled(true);
            }
        });
        otp_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                btnProceed.setEnabled(otp_view.getText().toString().length() == 4);
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialogVerifyOTP.dismiss();

                String otpCode = otp_view.getText().toString();
                if(otpCode.length() < 4){
                    customToast.showToast("Please enter the complete OTP code",0,1);
                }else {
                    RequestParams params = new RequestParams();
                    params.put("phone", phone);
                    params.put("code", otpCode);

                    ApiManager apiManager = new ApiManager(ApiManager.OTP_VERIFY,"post",params,apiCallBack, activity);
                    apiManager.loadURL();
                }

            }
        });

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpRequest();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogVerifyOTP.dismiss();
            }
        });

        dialogVerifyOTP.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(countDownTimer != null){
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            }
        });

        dialogVerifyOTP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVerifyOTP.show();

        dialogVerifyOTPDismiss = dialogVerifyOTP;



        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        long milis = 60000; //1 mnt
        countDownTimer = new CountDownTimer(milis, 1000) {
            public void onTick(long millisUntilFinished) {
                tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                DATA.print("-- contdowntimer is running");
            }
            public void onFinish() {
                layResendCode.setVisibility(View.VISIBLE);
            }

        };
        countDownTimer.start();





        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogVerifyOTP.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogVerifyOTP.show();
        dialogVerifyOTP.getWindow().setAttributes(lp);*/
    }







    /*public void countDownTimer(long milis) {
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isTimerRunning = true;
        tvLiveCareTicker.setTypeface(timerfonts);
        tvLiveCareTicker.setTextSize(30.0f);
        countDownTimer = new CountDownTimer(milis, 1000) {

            public void onTick(long millisUntilFinished) {
                //isTimerRunning = true;
                //tvLiveCareTicker.setText("seconds remaining: " + millisUntilFinished / 1000);
                //tvLiveCareTicker.setTypeface(timerfonts);
                //tvLiveCareTicker.setTextSize(30.0f);
                tvLiveCareTicker.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                //here you can have your logic to set text to edittext

                DATA.print("-- contdowntimer is running");
            }

            public void onFinish() {
                isTimerRunning = false;
                //tvLiveCareTicker.setTypeface(Typeface.DEFAULT);
                //tvLiveCareTicker.setTextSize(18.0f);
                //tvLiveCareTicker.setText("Its your trun. Be ready for live video checkup.");

                try {
                    new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Sorry, it has been longer then the anticipated wait time. Office Manager has been informed and the provider will be with your shortly. Thank you for your patience.")
                            .setPositiveButton("Done",null)
                            .create().show();//causes nullpointer when activity not running

                    timerAlert();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        countDownTimer.start();
    }*/




    private String convrtTime(long millis) {


//		long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        //DATA.print("--online care waiting time millis: "+millis + "hrs: "+hours+" mins: "+minutes);

//		return hours+" hours, "+minutes+" minutes";

        String h = hours < 10 ? "0"+hours : ""+hours;
        String m = minutes < 10 ? "0"+minutes : ""+minutes;
        String s = seconds < 10 ? "0"+seconds : ""+seconds;
        return h+":"+m+":"+s;
    }
}
