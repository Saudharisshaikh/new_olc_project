package com.app.Olc_MentalHealth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.app.Olc_MentalHealth.api.ApiCallBack;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.util.CustomToast;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.OpenActivity;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class ActivityVerificationSocial extends AppCompatActivity implements ApiCallBack {

    EditText etUserVerifyEmail , etUserPhoneFromSocial;
    Button btnUserVerifySendOtp,btnCancel;

    //simple login 0 -- social login 1
    public static String loginFrom = "0";

    CustomToast customToast;
    Activity activity;
    SharedPreferences prefs;
    OpenActivity openActivity;
    ApiCallBack apiCallBack;
    AlertDialog alertDialog;

    String socialID , patientID , socialFrom , emailFromsocial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_social);

        activity = ActivityVerificationSocial.this;
        apiCallBack = this;
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        etUserVerifyEmail = findViewById(R.id.etUserVerifyEmail);
        etUserPhoneFromSocial = findViewById(R.id.etUserPhoneFromSocial);
        btnUserVerifySendOtp = findViewById(R.id.btnUserVerifySendOtp);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(view ->
        {
            alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Are you sure? Do you want to cancel the verification process? please make sure your account will not be verified if you cancel the verification.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            activity.finish();
                        }
                    }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            alertDialog.dismiss();
                        }
                    }).create();
            alertDialog.show();
        });

        socialFrom = prefs.getString("social_from" , "");
        socialID = prefs.getString("social_id" , "");
        patientID = prefs.getString("id" , "");
        emailFromsocial = prefs.getString("email" , "");

        if (TextUtils.isEmpty(emailFromsocial))
        { etUserVerifyEmail.setText("");
        }
        else { etUserVerifyEmail.setText(emailFromsocial);
        }

        btnUserVerifySendOtp.setOnClickListener(view ->
        {
            //String email = etUserVerifyEmail.getText().toString().trim();
            String phone = etUserPhoneFromSocial.getText().toString().trim();

           /* if(TextUtils.isEmpty(email)){
                etUserVerifyEmail.setError("This field is required");
                customToast.showToast("Please enter email",0,0);
                return;
            }*/
            if(TextUtils.isEmpty(phone)){
                etUserPhoneFromSocial.setError("This field is required");
                customToast.showToast("Please enter phone",0,0);
                return;
            }
            else
            {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("userEmailFromSocial" , emailFromsocial);
                ed.putString("userPhoneFromSocial" , phone);
                ed.commit();
                RequestParams params = new RequestParams();
                params.put("social_id",socialID);
                params.put("email",emailFromsocial);
                params.put("phone",phone);
                params.put("social_from",socialFrom);
                params.put("patient_id",patientID);
                ApiManager apiManager = new ApiManager(ApiManager.SOCIAL_SENDOTP,"post",params,apiCallBack,activity);
                apiManager.loadURL();

            }
        });

    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.SOCIAL_SENDOTP)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                DATA.print("-- chk" + jsonObject.toString());

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equals("success")) {
//                    SharedPreferences.Editor ed = prefs.edit();
//                    ed.putBoolean("UserSignupSuccess" , false);
//                    ed.commit();
                    loginFrom = "1";
                    openActivity.open(ActivityVerificationOtp.class, true);
                    customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."
                }
                else if (status.equals("error"))
                {
                    customToast.showToast(msg, 0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}