package com.app.onlinecare_pk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.app.onlinecare_pk.api.ApiCallBack;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.util.CustomToast;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.HideShowKeypad;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class ForgotPassword2 extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    ApiCallBack apiCallBack;
    CustomToast customToast;

    EditText etForgotPassMobNo;
    Button btnForgotPassSubmit, btnResetPassSubmit;

    EditText etForgotPassNewPass, etForgotPassConfirmPass;

    LinearLayout layResendCode;
    TextView tvOtpTimer;
    Button btnResendCode;

    CardView cvForgotLay, cvOtp, cvResetPass;

    String phone = "", code = "";

    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_forgot_password2);


        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Forgot Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // getActionBar().setHomeButtonEnabled(true);
        }

        activity = ForgotPassword2.this;
        apiCallBack = this;
        customToast = new CustomToast(activity);

        etForgotPassMobNo =  findViewById(R.id.etForgotPassMobNo);
        btnForgotPassSubmit =  findViewById(R.id.btnForgotPassSubmit);

        cvForgotLay =  findViewById(R.id.cvForgotLay);
        cvOtp =  findViewById(R.id.cvOtp);

        cvResetPass = findViewById(R.id.cvResetPass);
        btnResetPassSubmit = findViewById(R.id.btnResetPassSubmit);
        etForgotPassNewPass = findViewById(R.id.etForgotPassNewPass);
        etForgotPassConfirmPass = findViewById(R.id.etForgotPassConfirmPass);

        Button btnProceed = findViewById(R.id.btnProceed);
        ImageView ivClose = findViewById(R.id.ivClose);
        OtpView otp_view = findViewById(R.id.otp_view);

        layResendCode = findViewById(R.id.layResendCode);
        tvOtpTimer = findViewById(R.id.tvOtpTimer);
        btnResendCode = findViewById(R.id.btnResendCode);

        tvOtpTimer.setTypeface(Typeface.createFromAsset(getAssets(),"digital-7.ttf"));


        btnForgotPassSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                phone = etForgotPassMobNo.getText().toString().trim();

                boolean validate = true;
                //if(TextUtils.isEmpty(first_name)){ etSignupName.setError("Required");validate = false;}
                if(TextUtils.isEmpty(phone)){etForgotPassMobNo.setError("Required");validate = false;}
                if(!phone.startsWith("0") || phone.length() != 11){customToast.showToast("Please enter your valid mobile number",0,1);validate = false;}

                if(validate){
                    otpRequest();
                }
            }
        });


        etForgotPassMobNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnForgotPassSubmit.performClick();
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });
        etForgotPassMobNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
                if(!phone.startsWith("0") || phone.length() != 11){
                    btnForgotPassSubmit.setEnabled(false);
                }else {
                    btnForgotPassSubmit.setEnabled(true);
                }
            }
        });

        findViewById(R.id.imgAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.addIntent(activity);
            }
        });


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
                cvForgotLay.setVisibility(View.VISIBLE);
                cvOtp.setVisibility(View.GONE);

                if(countDownTimer != null){
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            }
        });




        btnResetPassSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etForgotPassNewPass.getText().toString().trim();
                String confirm_pass = etForgotPassConfirmPass.getText().toString().trim();
                boolean validate = true;
                if(TextUtils.isEmpty(password)){etForgotPassNewPass.setError("Required");validate = false;}
                if(TextUtils.isEmpty(confirm_pass)){ etForgotPassConfirmPass.setError("Required");validate = false;}
                if(!password.equals(confirm_pass)){customToast.showToast("Passwords donot matched", 0, 1);validate = false;}


                if(validate){
                    RequestParams params = new RequestParams();
                    params.put("password", password);
                    params.put("confirm_pass", confirm_pass);
                    params.put("key",  code);

                    ApiManager apiManager = new ApiManager(ApiManager.FORGOT_PASSWORD_PK,"post",params,apiCallBack, activity);
                    apiManager.loadURL();
                }
            }
        });

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

        //DATA.print("--online care waiting time millis: "+millis + "hrs: "+hours+" mins: "+minutes);

//		return hours+" hours, "+minutes+" minutes";

        String h = hours < 10 ? "0"+hours : ""+hours;
        String m = minutes < 10 ? "0"+minutes : ""+minutes;
        String s = seconds < 10 ? "0"+seconds : ""+seconds;
        return h+":"+m+":"+s;
    }


    private void otpRequest(){
        RequestParams params = new RequestParams();
        params.put("phone", phone);

        ApiManager apiManager = new ApiManager(ApiManager.OTP_REQUEST_PASSWORD_PK,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {


        if(apiName.equalsIgnoreCase(ApiManager.OTP_REQUEST_PASSWORD_PK)){
            //{"code":5948,"status":"success","message":"OTP has been sent"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if(status.equalsIgnoreCase("success")){
                    //code = jsonObject.getString("code");
                    customToast.showToast("Code is : "+jsonObject.getString("code"), 0, 1);

                    /*if(dialogVerifyOTPDismiss != null){
                        dialogVerifyOTPDismiss.dismiss();
                    }
                    showVerifyOtpDialog();*/

                    cvForgotLay.setVisibility(View.GONE);
                    cvOtp.setVisibility(View.VISIBLE);

                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    long milis = 60000; //1 mnt
                    countDownTimer = new CountDownTimer(milis, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                            DATA.print("-- contdowntimer is running");
                            btnResendCode.setEnabled(false);
                        }
                        public void onFinish() {
                            layResendCode.setVisibility(View.VISIBLE);
                            btnResendCode.setEnabled(true);
                        }

                    };
                    countDownTimer.start();
                }else {
                    customToast.showToast(message, 0, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.OTP_VERIFY)){
            //{"status":"error","message":"Invalid OTP"}
            //{"status":"success","message":"Phone has been verified"}
            //{"code":"p12PGScAzv88RlDETSXMx2gOtR\/Fd\/E7gmLzdP46S\/VPr\/VX1ra0o\/vo7Lq1usVu6A1A4smAkiRtXYtTOpty5A==","status":"success","message":"Phone has been verified"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                customToast.showToast(message,0,1);
                if(status.equalsIgnoreCase("success")){
                    code = jsonObject.getString("code");
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    cvOtp.setVisibility(View.GONE);
                    cvForgotLay.setVisibility(View.GONE);
                    cvResetPass.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.FORGOT_PASSWORD_PK)){
            // result: {"status":"Error","msg":"key parameter is missing OR missing value","message":"Key Required"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                //String msg = jsonObject.getString("msg");

                //customToast.showToast(message,0,1);
                //if(status.equalsIgnoreCase("success")){}

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(message)
                        .setPositiveButton("Done",null).create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (status.equals("success")) {
                            finish();
                        }
                    }
                });
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
