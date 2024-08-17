package com.app.mdlive_uc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_uc.api.ApiCallBack;
import com.app.mdlive_uc.api.ApiManager;
import com.app.mdlive_uc.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.app.mdlive_uc.Login.HOSPITAL_ID_EMCURA;

public class ForgotPassword extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    ApiCallBack apiCallBack;
    EditText etForgotPwdEmail, etForgotPwdUsername;
    TextView tvforgt, tvforgt1;
    Button btnForgotPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_forgot_password);

        activity = ForgotPassword.this;
        apiCallBack = this;

        btnForgotPwd = (Button) findViewById(R.id.btnForgotPwd);
        etForgotPwdEmail = (EditText) findViewById(R.id.etForgotPwdEmail);
        etForgotPwdUsername = (EditText) findViewById(R.id.etForgotPwdUsername);
        tvforgt = (TextView) findViewById(R.id.tvforgt);
        tvforgt1 = (TextView) findViewById(R.id.tvforgt1);

        btnForgotPwd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = etForgotPwdEmail.getText().toString().trim();
                String username = etForgotPwdUsername.getText().toString().trim();

                if(email.isEmpty() && username.isEmpty()){
                    Toast.makeText(activity, "Please enter your email address OR username", Toast.LENGTH_SHORT).show();
                }else {
                    if(!email.isEmpty() && !validEmail(email)){
                        etForgotPwdEmail.setError("Invalid Email Address");
                    }else {
                        String emailTemp = email.isEmpty() ? null : email;
                        String usernameTemp = username.isEmpty() ? null : username;
                        forgotPasswordCall(emailTemp, usernameTemp);
                    }
                }
            }
        });



        etForgotPwdEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String email = etForgotPwdEmail.getText().toString().trim();

                    if(email.isEmpty()){
                        etForgotPwdEmail.setError("Please enter your email address");
                    }else if(validEmail(email)){
                        forgotPasswordCall(email , null);
                    }else {
                        etForgotPwdEmail.setError("Invalid Email Address");
                    }

                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });

        etForgotPwdUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String username = etForgotPwdUsername.getText().toString().trim();

                    if(username.isEmpty()){
                        etForgotPwdUsername.setError("Please enter your username");
                    }else {
                        forgotPasswordCall(null , username);
                    }

                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });

        findViewById(R.id.imgAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.addIntent(activity);
            }
        });

    }


    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void forgotPasswordCall(String email, String username) {

        RequestParams params = new RequestParams();

        if(email != null){
            params.put("email", email);
        }
        if(username != null){
            params.put("username", username);
        }
        params.put("type", "patient");

        params.put("hospital_id", HOSPITAL_ID_EMCURA);

        ApiManager apiManager = new ApiManager(ApiManager.FORGOT_PASSWORD, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.FORGOT_PASSWORD)) {
            try {

                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(msg)
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


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
