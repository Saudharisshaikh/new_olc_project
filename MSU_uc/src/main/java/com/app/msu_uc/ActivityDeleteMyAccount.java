package com.app.msu_uc;

import static com.app.msu_uc.api.ApiManager.PREF_APP_LBL_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.msu_uc.api.ApiCallBack;
import com.app.msu_uc.api.ApiManager;
import com.app.msu_uc.util.CheckInternetConnection;
import com.app.msu_uc.util.CustomToast;
import com.app.msu_uc.util.DATA;
import com.app.msu_uc.util.HideShowKeypad;
import com.app.msu_uc.util.OpenActivity;
import com.app.msu_uc.util.SharedPrefsHelper;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDeleteMyAccount extends BaseActivity {

    Activity activity;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    OpenActivity openActivity;
    SharedPreferences prefs;

    Button btnYesDelete,btnCancelDelete,btnProceed,btnResendCode,btnCancelOtpStep;
    CardView cvOtp;
    LinearLayout layoutButtons;
    OtpView otp_view;
    ImageView ivClose;
    TextView txtphoneNumber,txtUserName,txtLabel_0,txtLabel_1,txtLabel_2,txtLabel_3_heading,txtLabel_4,
            txtLabel_5,txtLabel_6,txtLabel_7;

    LinearLayout linear_layoutDeleteAcc_Labels ,linearlayoutOtpView;
    String subscriptionLabel,btnDeleteAccountLabel;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_my_account);

        activity = ActivityDeleteMyAccount.this;
        customToast = new CustomToast(activity);
        apiCallBack = this;
        openActivity = new OpenActivity(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        hideShowKeypad = new HideShowKeypad(activity);

        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        txtLabel_0 = findViewById(R.id.txtLabel_0);
        txtLabel_1 = findViewById(R.id.txtLabel_1);
        txtLabel_2 = findViewById(R.id.txtLabel_2);
        txtLabel_3_heading = findViewById(R.id.txtLabel_3_heading);
        txtLabel_4 = findViewById(R.id.txtLabel_4);
        txtLabel_5 = findViewById(R.id.txtLabel_5);
        txtLabel_6 = findViewById(R.id.txtLabel_6);
        txtLabel_7 = findViewById(R.id.txtLabel_7);


        btnYesDelete = findViewById(R.id.btnYesDelete);
        btnCancelDelete = findViewById(R.id.btnCancelDelete);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnCancelOtpStep = findViewById(R.id.btnCancelOtpStep);
        cvOtp = findViewById(R.id.cvOtp);
        otp_view = findViewById(R.id.otp_view);
        btnProceed = findViewById(R.id.btnProceed);
        ivClose = findViewById(R.id.ivClose);
        txtphoneNumber = findViewById(R.id.txtphoneNumber);
        txtUserName = findViewById(R.id.txtUserName);

        layoutButtons = findViewById(R.id.layoutButtons);
        linearlayoutOtpView = findViewById(R.id.linearlayoutOtpView);
        linear_layoutDeleteAcc_Labels = findViewById(R.id.linear_layoutDeleteAcc_Labels);

        txtUserName.setText("Hi, "+prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));

        btnResendCode.setOnClickListener(view ->
        {
            linear_layoutDeleteAcc_Labels.setVisibility(View.VISIBLE);
            layoutButtons.setVisibility(View.VISIBLE);
            linearlayoutOtpView.setVisibility(View.GONE);
            cvOtp.setVisibility(View.GONE);
        });

        btnCancelOtpStep.setOnClickListener(view ->
        {
            finish();
        });

        btnYesDelete.setOnClickListener(view ->
        {
            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                    .setMessage("Are you sure do you want to send OTC Request for permanently delete your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            otpRequestForAccountDelete();
                        }
                    })
                    .setNegativeButton("Not Now", null).create().show();
        });

        btnCancelDelete.setOnClickListener(view ->
        {
            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                    .setMessage("Are you sure ? Do you want to cancel this process?")
                    .setPositiveButton("Yes cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Not Now", null).create().show();
        });

        ivClose.setOnClickListener(view ->
        {
            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                    .setMessage("Are you sure ? Do you want to exit this process?")
                    .setPositiveButton("Yes exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            linearlayoutOtpView.setVisibility(View.GONE);
                            cvOtp.setVisibility(View.GONE);
                            linear_layoutDeleteAcc_Labels.setVisibility(View.VISIBLE);
                            layoutButtons.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton("Not Now", null).create().show();
        });


        btnProceed.setOnClickListener(view ->
        {
            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                    .setMessage("Please make sure, once you confirm, all of your account data will be deleted permanently.")
                    .setPositiveButton("Permanently delete my account", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            otpVerifyForAccountDelete();
                        }
                    })
                    .setNegativeButton("Not Now", null).create().show();
        });


        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //otpCode = otp;
                System.out.println("-- onOtpCompleted OTP : "+ otp);
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

        getLabels();
    }

    private void otpVerifyForAccountDelete()
    {
        RequestParams params = new RequestParams();
        params.put("verify_key" , prefs.getString("verify_key" , ""));
        params.put("code" , otp_view.getText().toString());
        params.put("patient_id" , prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.DEL_ACC_OTP_VERIFY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void otpRequestForAccountDelete() {

        RequestParams params = new RequestParams();
        params.put("patient_id" , prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.DEL_ACC_OTPREQUEST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.DEL_ACC_OTPREQUEST))
        {
            try {

                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");
                if (jsonObject.has("verify_key")) {
                    String verify_key = jsonObject.getString("verify_key");
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("verify_key", verify_key);
                    ed.commit();
                }

                if (status.equalsIgnoreCase("success"))
                {
                    linearlayoutOtpView.setVisibility(View.VISIBLE);
                    cvOtp.setVisibility(View.VISIBLE);
                    linear_layoutDeleteAcc_Labels.setVisibility(View.GONE);
                    try {
                        System.out.println("-- user phone "+prefs.getString("phone", ""));
                        String userPhone = prefs.getString("phone", "");
                        String countryCode = userPhone.substring(0, userPhone.indexOf("-"));
                        System.out.println("-- count code : "+countryCode);
                        String phoneNum = userPhone.substring(countryCode.length()+1, userPhone.length());
                        System.out.println("-- phone num : "+ phoneNum);
                        txtphoneNumber.setText(phoneNum);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    layoutButtons.setVisibility(View.GONE);
                    customToast.showToast(msg,0,0);
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    customToast.showToast(msg,0,0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if (apiName.equals(ApiManager.DEL_ACC_OTP_VERIFY))
        {
            try {

                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");
                if (status.equalsIgnoreCase("success"))
                {
                    layoutButtons.setVisibility(View.GONE);
                    linearlayoutOtpView.setVisibility(View.VISIBLE);
                    cvOtp.setVisibility(View.VISIBLE);
                    linear_layoutDeleteAcc_Labels.setVisibility(View.GONE);
                    customToast.showToast(msg,0,1);
                    boolean debug_logs = sharedPrefsHelper.get("debug_logs", false);
                    String states_logs = SharedPrefsHelper.getInstance().get("states", "");
                    String folder_name = SharedPrefsHelper.getInstance().get("folder_name", "");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    SharedPrefsHelper.getInstance().clearAllData();
                    sharedPrefsHelper.save("debug_logs", debug_logs);
                    sharedPrefsHelper.save("states" , states_logs);
                    sharedPrefsHelper.save("folder_name" , folder_name);
                    Intent intent1 = new Intent(getApplicationContext(), Login.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    customToast.showToast(msg,0,0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    String labalDeleteAccount;
    public void getLabels()
    {

        String prefsDataStr = prefs.getString(PREF_APP_LBL_KEY, "");
        if(! TextUtils.isEmpty(prefsDataStr)){
            try {
                JSONObject jsonObject = new JSONObject(prefsDataStr);

                JSONObject jsonObj = jsonObject.getJSONObject("data");;

                JSONArray jsonArray = jsonObj.getJSONArray("delete_page_labels");

                txtLabel_0.setText(jsonArray.getString(0));
                txtLabel_1.setText(jsonArray.getString(1));
                txtLabel_2.setText(jsonArray.getString(2));
                txtLabel_3_heading.setText(jsonArray.getString(3));
                txtLabel_4.setText(jsonArray.getString(4));
                txtLabel_5.setText(jsonArray.getString(5));
                txtLabel_6.setText(jsonArray.getString(6));
                txtLabel_7.setText(jsonArray.getString(7));
                btnDeleteAccountLabel = jsonArray.getString(8);
                subscriptionLabel = jsonArray.getString(9);

                btnProceed.setText(btnDeleteAccountLabel);

                if (jsonObj.has("delete_text"))
                {
                    labalDeleteAccount = jsonObj.getString("delete_text");

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}