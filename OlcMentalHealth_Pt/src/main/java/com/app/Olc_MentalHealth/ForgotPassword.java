package com.app.Olc_MentalHealth;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.Olc_MentalHealth.api.ApiCallBack;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.HideShowKeypad;
import com.app.Olc_MentalHealth.util.SharedPrefsHelper;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    ApiCallBack apiCallBack;
    EditText etForgotPhone, etForgotPwdUsername,etNewPass,etConfirmPass;
    TextView tvforgt, tvforgt1,txtphoneNumber;
    Button btnForgotPwd;

    LinearLayout cvOtpLayout,phoneNoLayout,layoutChangePassword;

    String phone;
    OtpView otp_viewSms;
    Button btnProceed,btnUpdatePass,btnResendCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_forgot_password);

        activity = ForgotPassword.this;
        apiCallBack = this;

        btnForgotPwd = (Button) findViewById(R.id.btnForgotPwd);
        etForgotPhone = (EditText) findViewById(R.id.etForgotPhone);
        etForgotPwdUsername = (EditText) findViewById(R.id.etForgotPwdUsername);
        tvforgt = (TextView) findViewById(R.id.tvforgt);
        tvforgt1 = (TextView) findViewById(R.id.tvforgt1);
        txtphoneNumber = findViewById(R.id.txtphoneNumber);


        cvOtpLayout = findViewById(R.id.cvOtpLayout);
        phoneNoLayout = findViewById(R.id.phoneNoLayout);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);

        otp_viewSms = findViewById(R.id.otp_viewSms);
        btnProceed = findViewById(R.id.btnProceed);
        etNewPass =(EditText) findViewById(R.id.etNewPass);
        etConfirmPass =(EditText) findViewById(R.id.etConfirmPass);
        btnUpdatePass = (Button) findViewById(R.id.btnUpdatePassword);
        btnResendCode = findViewById(R.id.btnResendCode);

        btnResendCode.setOnClickListener(view ->
        {
            cvOtpLayout.setVisibility(View.GONE);
            phoneNoLayout.setVisibility(View.VISIBLE);
            layoutChangePassword.setVisibility(View.GONE);
        });

        otp_viewSms.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                //otpCode = otp;
                DATA.print("-- onOtpCompleted OTP : "+ otp);
                new HideShowKeypad(activity).hidekeyboardOnDialog();
                btnProceed.setEnabled(true);
            }
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

        btnForgotPwd.setOnClickListener(v -> {
            phone = etForgotPhone.getText().toString().trim();

            if(phone.isEmpty()) {
                Toast.makeText(activity, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            }else if(!phone.isEmpty()){
                forgotPasswordCall(phone);
                SharedPrefsHelper.getInstance().save("phoneNoForgot" , phone);
            }else {
                etForgotPhone.setError("Invalid phone number");
            }
        });

        etForgotPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                phone = etForgotPhone.getText().toString().trim();

                if(phone.isEmpty()){
                    etForgotPhone.setError("Please enter your phone number");
                }else if(!phone.isEmpty()){
                    forgotPasswordCall(phone);
                    SharedPrefsHelper.getInstance().save("phoneNoForgot" , phone);
                }else {
                    etForgotPhone.setError("Invalid phone number");
                }
                //return true; return true not closes keyboard
                return false;
            }
            return false;
        });



        btnProceed.setOnClickListener(view ->
        {
            forgotPasswordVerifyOtp(otp_viewSms.getText().toString());
        });

        findViewById(R.id.imgAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.addIntent(activity);
            }
        });


        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String newPass = etNewPass.getText().toString();
                String confirmPass = etConfirmPass.getText().toString();
                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(activity, "Please enter password", Toast.LENGTH_SHORT).show();
                }else if (!newPass.equals(confirmPass)){
                    Toast.makeText(activity, "Password not matched", Toast.LENGTH_SHORT).show();
                } else
                {
                    updatePassword(newPass);
                }
            }
        });

        txtphoneNumber.setText(SharedPrefsHelper.getInstance().get("phoneNoForgot",""));
    }

    public void forgotPasswordCall(String phone) {

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("hospital_id", SharedPrefsHelper.getInstance().get(Login.HOSP_ID_PREFS_KEY, ""));
        ApiManager apiManager = new ApiManager(ApiManager.FORGOT_PASSWORD_REQUEST, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }

    public void forgotPasswordVerifyOtp(String otpCode) {

        RequestParams params = new RequestParams();
        params.put("verify_key", SharedPrefsHelper.getInstance().get("verify_keyForgotPassword" , ""));
        params.put("sms_code", otpCode);
        ApiManager apiManager = new ApiManager(ApiManager.FORGOT_PASSWORD_VERIFY_OTP, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }

    public void updatePassword(String newPass) {

        RequestParams params = new RequestParams();
        params.put("pcode" , SharedPrefsHelper.getInstance().get("pcode" , ""));
        params.put("new_password", newPass);

        ApiManager apiManager = new ApiManager(ApiManager.FORGOT_PASSWORD_CHANGE_PASSWORD,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.FORGOT_PASSWORD_REQUEST)) {
            try {

                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equalsIgnoreCase("success"))
                {
                    cvOtpLayout.setVisibility(View.VISIBLE);
                    phoneNoLayout.setVisibility(View.GONE);
                    layoutChangePassword.setVisibility(View.GONE);
                    SharedPrefsHelper.getInstance().save("verify_keyForgotPassword" , jsonObject.getString("verify_key"));
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (apiName.equalsIgnoreCase(ApiManager.FORGOT_PASSWORD_VERIFY_OTP))
        {
            try {

                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equalsIgnoreCase("success"))
                {
                    cvOtpLayout.setVisibility(View.GONE);
                    phoneNoLayout.setVisibility(View.GONE);
                    layoutChangePassword.setVisibility(View.VISIBLE);
                    SharedPrefsHelper.getInstance().save("pcode" , jsonObject.getString("pcode"));
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (apiName.equalsIgnoreCase(ApiManager.FORGOT_PASSWORD_CHANGE_PASSWORD))
        {
            try {

                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equalsIgnoreCase("success"))
                {
                    finish();
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
