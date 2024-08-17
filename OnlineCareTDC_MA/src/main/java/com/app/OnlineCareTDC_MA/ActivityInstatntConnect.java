package com.app.OnlineCareTDC_MA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.OnlineCareTDC_MA.api.ApiManager;
import com.app.OnlineCareTDC_MA.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityInstatntConnect extends BaseActivity {



    EditText etInstantConnectFirstName, etInstantConnectLastName, etInstantConnectCellNo, etInstantConnectEmail;
    Button btnInstantConnectStart,btnInstantConnectNotNow;
    ImageView ivBack;

    CheckBox cbInstantConnectAddFamily;
    LinearLayout layInstantConnectFamily;
    EditText etInstantConnectFirstNameFamily, etInstantConnectLastNameFamily, etInstantConnectCellNoFamily, etInstantConnectEmailFamily;


    public static boolean isFromPatientProfile;
    public static String call_id_instant_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instatnt_connect);

        isFromPatientProfile = getIntent().getBooleanExtra("isFromPatientProfile", false);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        etInstantConnectFirstName = findViewById(R.id.etInstantConnectFirstName);
        etInstantConnectLastName = findViewById(R.id.etInstantConnectLastName);
        etInstantConnectCellNo = findViewById(R.id.etInstantConnectCellNo);
        etInstantConnectEmail = findViewById(R.id.etInstantConnectEmail);
        btnInstantConnectStart = findViewById(R.id.btnInstantConnectStart);
        btnInstantConnectNotNow = findViewById(R.id.btnInstantConnectNotNow);
        ivBack = findViewById(R.id.ivBack);

        cbInstantConnectAddFamily = findViewById(R.id.cbInstantConnectAddFamily);
        layInstantConnectFamily = findViewById(R.id.layInstantConnectFamily);
        etInstantConnectFirstNameFamily = findViewById(R.id.etInstantConnectFirstNameFamily);
        etInstantConnectLastNameFamily = findViewById(R.id.etInstantConnectLastNameFamily);
        etInstantConnectCellNoFamily = findViewById(R.id.etInstantConnectCellNoFamily);
        etInstantConnectEmailFamily = findViewById(R.id.etInstantConnectEmailFamily);

        if(isFromPatientProfile){
            etInstantConnectFirstName.setText(ActivityTcmDetails.ptFname);
            etInstantConnectLastName.setText(ActivityTcmDetails.ptLname);
            etInstantConnectCellNo.setText(ActivityTcmDetails.ptPhone);
            etInstantConnectEmail.setText(ActivityTcmDetails.ptEmail);

            etInstantConnectFirstName.setFocusable(false);
            etInstantConnectFirstName.setInputType(InputType.TYPE_NULL);
            etInstantConnectLastName.setFocusable(false);
            etInstantConnectLastName.setInputType(InputType.TYPE_NULL);
            cbInstantConnectAddFamily.setVisibility(View.GONE);
        }

        cbInstantConnectAddFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int vis = isChecked ? View.VISIBLE : View.GONE;
            layInstantConnectFamily.setVisibility(vis);
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivBack:
                        onBackPressed();
                        break;
                    case R.id.btnInstantConnectStart:
                        if(isFromPatientProfile){
                            sendCallInvite();
                        }else {
                            sendInstantConnectInvite();
                        }
                        break;
                    case R.id.btnInstantConnectNotNow:
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        };
        ivBack.setOnClickListener(onClickListener);
        btnInstantConnectStart.setOnClickListener(onClickListener);
        btnInstantConnectNotNow.setOnClickListener(onClickListener);

    }


    private void sendInstantConnectInvite(){

        String first_name = etInstantConnectFirstName.getText().toString().trim();
        String last_name = etInstantConnectLastName.getText().toString().trim();
        String email = etInstantConnectEmail.getText().toString().trim();
        String phone = etInstantConnectCellNo.getText().toString().trim();

        boolean validated = true;
        if(TextUtils.isEmpty(first_name)){
            etInstantConnectFirstName.setError("Required");
            validated = false;
        }
        if(TextUtils.isEmpty(last_name)){
            etInstantConnectLastName.setError("Required");
            validated = false;
        }
        /*if(! Signup.isValidEmail(email)){
            etInstantConnectEmail.setError("Invalid email address");
            validated = false;
        }
        if(TextUtils.isEmpty(phone)){
            etInstantConnectCellNo.setError("Required");
            validated = false;
        }*/

        if (etInstantConnectCellNo.getText().toString().isEmpty())
        {
            validated = false;
            customToast.showToast("Please enter phone no", 0 ,0);
        }
        if(!Signup.isValidEmail(email)){
            validated = false;
            customToast.showToast("Please enter valid email address", 0 ,0);
        }

        String family_first_name = "" , family_last_name = "", family_email = "", family_phone = "";
        if(cbInstantConnectAddFamily.getVisibility() == View.VISIBLE && cbInstantConnectAddFamily.isChecked()){
            family_first_name = etInstantConnectFirstNameFamily.getText().toString().trim();
            family_last_name = etInstantConnectLastNameFamily.getText().toString().trim();
            family_email = etInstantConnectEmailFamily.getText().toString().trim();
            family_phone = etInstantConnectCellNoFamily.getText().toString().trim();

            if(TextUtils.isEmpty(family_phone) && ! Signup.isValidEmail(family_email)){
                validated = false;
                customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
            }
        }

        if(!validated){
            customToast.showToast("Please enter the required information",0,0);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("phone", phone);
        params.put("email", email);

        if(cbInstantConnectAddFamily.getVisibility() == View.VISIBLE && cbInstantConnectAddFamily.isChecked()){
            params.put("family_firstname", family_first_name);
            params.put("family_lastname", family_last_name);
            params.put("family_email", family_email);
            params.put("family_phone", family_phone);
        }

        ApiManager apiManager = new ApiManager(ApiManager.INSTANT_PATIENT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    private void sendCallInvite(){

        //String first_name = etInstantConnectFirstName.getText().toString().trim();
        //String last_name = etInstantConnectLastName.getText().toString().trim();
        String email = etInstantConnectEmail.getText().toString().trim();
        String phone = etInstantConnectCellNo.getText().toString().trim();

        boolean validated = true;
        /*if(TextUtils.isEmpty(first_name)){
            etInstantConnectFirstName.setError("Required");
            validated = false;
        }
        if(TextUtils.isEmpty(last_name)){
            etInstantConnectLastName.setError("Required");
            validated = false;
        }*/
        /*if(! Signup.isValidEmail(email)){
            etInstantConnectEmail.setError("Invalid email address");
            validated = false;
        }
        if(TextUtils.isEmpty(phone)){
            etInstantConnectCellNo.setError("Required");
            validated = false;
        }*/

        if(TextUtils.isEmpty(phone) && ! Signup.isValidEmail(email)){
            validated = false;
            customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
        }

        if(!validated){
            customToast.showToast("Please enter the required information",0,0);
            return;
        }

        DATA.rndSessionId = new VCallModule(activity).randomStr();
        Constants.ROOM_NAME_MULTI = DATA.rndSessionId;

        RequestParams params = new RequestParams();

        params.put("phone_num", phone);
        params.put("email", email);
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("call_session_id", DATA.rndSessionId);

        ApiManager apiManager = new ApiManager(ApiManager.CALLING_INVITE_CALL,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }



    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if(apiName.equalsIgnoreCase(ApiManager.INSTANT_PATIENT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                //if (jsonObject.optString("status").equalsIgnoreCase("success")){}
                if (jsonObject.has("call_session_id")){

                    call_id_instant_connect = jsonObject.optString("call_id", "0");

                    DATA.rndSessionId = jsonObject.getString("call_session_id");
                    Constants.ROOM_NAME_MULTI = DATA.rndSessionId;

                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Instant connect invitation has been sent successfully.")
                            .setPositiveButton("Start Instant Session",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            DATA.incomingCall = false;
                            DATA.isFromDocToDoc = false;//To ensure correct flow after call
                            Intent i = new Intent(activity, MainActivity.class);
                            i.putExtra("isFromInstantConnect", true);
                            startActivity(i);

                            finish();
                        }
                    });
                    alertDialog.show();

                }else if(jsonObject.has("message")){
                    customToast.showToast(jsonObject.getString("message"),0,0);
                }else if(jsonObject.has("msg")){
                    customToast.showToast(jsonObject.getString("msg"),0,0);
                }else {
                    customToast.showToast(jsonObject.toString(),0,0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CALLING_INVITE_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                // {"redirect":"https:\/\/www.onlinecare.com\/fivestars\/video_chat\/open_chat\/24763","call_id":24763}
                //if (jsonObject.optString("status").equalsIgnoreCase("success")){}
                if (jsonObject.has("redirect")){

                    call_id_instant_connect = jsonObject.optString("call_id", "0");

                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Instant connect invitation has been sent successfully.")
                            .setPositiveButton("Start Instant Session",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            DATA.isFromDocToDoc = false;//To ensure correct flow after call
                            DATA.incomingCall = false;
                            Intent i = new Intent(activity, MainActivity.class);
                            i.putExtra("isFromInstantConnect", true);
                            startActivity(i);

                            finish();
                        }
                    });
                    alertDialog.show();
                }else if(jsonObject.has("message")){
                    customToast.showToast(jsonObject.getString("message"),0,0);
                }else if(jsonObject.has("msg")){
                    customToast.showToast(jsonObject.getString("msg"),0,0);
                }else {
                    customToast.showToast(jsonObject.toString(),0,0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


}
