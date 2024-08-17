package com.digihealthcard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digihealthcard.api.ApiCallBack;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.model.SubUsersModel;
import com.digihealthcard.util.CheckInternetConnection;
import com.digihealthcard.util.CustomToast;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.HideShowKeypad;
import com.digihealthcard.util.OpenActivity;
import com.digihealthcard.util.SharedPrefsHelper;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActivityVerification  extends BaseActivity {


    EditText etUserVerifyFname , etUserVerifyLname , etUserVerifyEmail , etUserVerifyPhoneNum;
    Button btnUserVerifySendOtp , btnResendCode , btnProceed;

    CardView cvLayoutSendOTP , cvOtp;
    TextView tvOtpTimer;
    CountryCodePicker ccp_CountryCode;
    OtpView otp_view;
    TextView tvInvalidPhone;

    CountDownTimer countDownTimer;
    ImageView ivClose;
    LinearLayout layResendCode;
    String phoneInput = "";
    String phone = "";

    String userVerifyFName , userVerifyLName , userVerifyEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("OTP Verification");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        etUserVerifyFname = findViewById(R.id.etUserVerifyFname);
        etUserVerifyLname = findViewById(R.id.etUserVerifyLname);
        etUserVerifyEmail = findViewById(R.id.etUserVerifyEmail);
        etUserVerifyPhoneNum = findViewById(R.id.etUserVerifyPhoneNum);
        btnUserVerifySendOtp = findViewById(R.id.btnUserVerifySendOtp);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnProceed = findViewById(R.id.btnProceed);
        tvOtpTimer = findViewById(R.id.tvOtpTimer);
        ivClose = findViewById(R.id.ivClose);
        layResendCode = findViewById(R.id.layResendCode);
        cvLayoutSendOTP = findViewById(R.id.cvLayoutSendOTP);
        cvOtp = findViewById(R.id.cvOtp);
        otp_view = findViewById(R.id.otp_view);
        ccp_CountryCode = findViewById(R.id.ccp_CountryCode);
        tvInvalidPhone = findViewById(R.id.tvInvalidPhone);
        ccp_CountryCode.setNumberAutoFormattingEnabled(true);

        etUserVerifyFname.setText(Login.fullNameUserVerify);
        etUserVerifyEmail.setText(Login.emailUserVerify);



        tvOtpTimer.setTypeface(Typeface.createFromAsset(getAssets(),"digital-7.ttf"));

        if (Signup.fromSignUp.equals("1")){

            cvLayoutSendOTP.setVisibility(View.GONE);
            cvOtp.setVisibility(View.VISIBLE);

            //if user from signup screen then timmer will be start auto
            if(countDownTimer != null){
                countDownTimer.cancel();
                countDownTimer = null;
            }
            long milis = 60000; //1 mnt
            countDownTimer = new CountDownTimer(milis, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                    System.out.println("-- contdowntimer is running GM");
                    btnResendCode.setEnabled(false);
                }
                public void onFinish() {
                    layResendCode.setVisibility(View.VISIBLE);
                    btnResendCode.setEnabled(true);
                }

            };
            countDownTimer.start();

        }else if (Signup.fromSignUp.equals("2")){
            /*from home*/
            cvLayoutSendOTP.setVisibility(View.VISIBLE);
            cvOtp.setVisibility(View.GONE);

            try {
                System.out.println("-- user phone "+prefs.getString("phone", ""));
                String userPhone = GloabalMethods.userVerphone;
                String countryCode = userPhone.substring(0, userPhone.indexOf("-"));
                System.out.println("-- count code : "+countryCode);
                String phoneNum = userPhone.substring(countryCode.length()+1, userPhone.length());
                System.out.println("-- phone num : "+ phoneNum);

                ccp_CountryCode.setCountryForPhoneCode(Integer.parseInt(countryCode.replace("+", "")));
                etUserVerifyPhoneNum.setText(phoneNum);
            }catch (Exception e){
                e.printStackTrace();
            }

            etUserVerifyFname.setText(GloabalMethods.userVerFName);
            etUserVerifyLname.setText(GloabalMethods.userVerLName);
            etUserVerifyEmail.setText(GloabalMethods.userVerEmail);

            btnUserVerifySendOtp.setEnabled(true);
        }
        else if (Signup.fromSignUp.equals("0"))
        {
            cvLayoutSendOTP.setVisibility(View.VISIBLE);
            cvOtp.setVisibility(View.GONE);
        }


        //close icon otp timmer layout
        ivClose.setOnClickListener(view -> {
            if (Signup.fromSignUp.equals("1"))
            {

                if(countDownTimer != null){
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                onBackPressed();
            }
            else if (Signup.fromSignUp.equals("2"))
            {
                if(countDownTimer != null){
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                /*moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);*/
                onBackPressed();
            }
            else {
                cvLayoutSendOTP.setVisibility(View.VISIBLE);
                cvOtp.setVisibility(View.GONE);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            }
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

        btnProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialogVerifyOTP.dismiss();
                String otpCode = otp_view.getText().toString();
                if(otpCode.length() < 4){
                    customToast.showToast("Please enter the complete OTP code",0,1);
                }else if (Signup.fromSignUp.equals("0")){
                    otpVerify(otpCode);
                }
                else if (Signup.fromSignUp.equals("1"))
                {
                    otpFromSignupVerify(otpCode);
                }
                else if (Signup.fromSignUp.equals("2"))
                {
                    otpFromHomeVerify(otpCode);
                }

            }
        });


        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Signup.fromSignUp.equals("1"))
                {
                    otpRequest(Signup.phoneNumSignup);
                }
                else if (Signup.fromSignUp.equals("0"))
                {
                    otpRequest(phoneInput);
                }
                else if (Signup.fromSignUp.equals("2"))
                {
                    otpRequest(phoneInput);
                }
            }
        });


        btnUserVerifySendOtp.setOnClickListener(view -> {

            userVerifyFName = etUserVerifyFname.getText().toString().trim();
            userVerifyLName = etUserVerifyLname.getText().toString().trim();
            userVerifyEmail = etUserVerifyEmail.getText().toString().trim();
            // String userVerifyPhoneNum = etUserVerifyPhoneNum.getText().toString().trim();

            String countryCode = ccp_CountryCode.getSelectedCountryCodeWithPlus();
            phone = etUserVerifyPhoneNum.getText().toString();
            phoneInput = countryCode+"-"+phone;

            boolean validated = true;
            if (userVerifyFName.isEmpty()) {etUserVerifyFname.setError("This field is required");validated = false;}
            if (userVerifyLName.isEmpty()) {etUserVerifyLname.setError("This field is required");validated = false;}
            if (userVerifyEmail.isEmpty()) {etUserVerifyEmail.setError("This field is required");validated = false; }
            if (!GloabalMethods.isValidEmail(etUserVerifyEmail.getText())) {etUserVerifyEmail.setError("Please enter a valid email address.");validated = false;}
            if (phone.isEmpty()) {etUserVerifyPhoneNum.setError("This field is required");validated = false;}

            if(validated){
                otpRequest(phoneInput);
            }else {
                customToast.showToast("Please enter the required information.",0,0);
            }

        });

        etUserVerifyPhoneNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnUserVerifySendOtp.performClick();
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });

        etUserVerifyPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
                if(phone.startsWith("0") || phone.length() != 12){
                    btnUserVerifySendOtp.setEnabled(false);
                    tvInvalidPhone.setVisibility(View.VISIBLE);
                }else{
                    btnUserVerifySendOtp.setEnabled(true);
                    tvInvalidPhone.setVisibility(View.GONE);
                }
            }
        });
    }

    private void otpFromHomeVerify(String otpCode) {

        RequestParams params = new RequestParams();
        params.put("phone" , phoneInput);
        params.put("code" , otpCode);
        params.put("user_id" , GloabalMethods.userVerfyId);
        ApiManager apiManager = new ApiManager(ApiManager.SIMPLE_OTP_VERIFY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    private void otpRequest(String phoneNumber) {
        RequestParams params = new RequestParams();
        params.put("phone" , phoneNumber);
        ApiManager apiManager = new ApiManager(ApiManager.REQUEST_OTP_CODE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void otpVerify(String otpCode)
    {
        RequestParams params = new RequestParams();
        params.put("phone" , phoneInput);
        params.put("code" , otpCode);
        params.put("first_name" , userVerifyFName);
        params.put("last_name" , userVerifyLName);
        params.put("email" , userVerifyEmail);
        params.put("from_social" , Login.socialFromUserVerify);
        params.put("social_id" , Login.socialIdUserVerify);
        ApiManager apiManager = new ApiManager(ApiManager.OTP_VERIFY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void otpFromSignupVerify(String otpCode)
    {
        RequestParams params = new RequestParams();
        params.put("phone" , Signup.phoneNumSignup);
        params.put("code" , otpCode);
        params.put("first_name" , Signup.fNameSignup);
        params.put("last_name" , Signup.lNameSignup);
        params.put("email" , Signup.emailSignup);
        params.put("password",Signup.signupPassword);
        params.put("from_social" , "registerform");
        params.put("social_id" , "111");
        ApiManager apiManager = new ApiManager(ApiManager.OTP_VERIFY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    /* String otpCode;*/
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.REQUEST_OTP_CODE))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
                System.out.println("-- chk"+jsonObject.toString());
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");
                /* otpCode = jsonObject.getString("code");*/

                if (status.equalsIgnoreCase("success"))
                {
                    cvLayoutSendOTP.setVisibility(View.GONE);
                    cvOtp.setVisibility(View.VISIBLE);
                    customToast.showToast(msg, 0, 1);
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    long milis = 60000; //1 mnt
                    countDownTimer = new CountDownTimer(milis, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tvOtpTimer.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                            System.out.println("-- contdowntimer is running GM");
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
        else if (apiName.equals(ApiManager.OTP_VERIFY))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
                System.out.println("-- chk"+jsonObject.toString());

                String after_urgentcare_form = jsonObject.optString("after_urgentcare_form");
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");

                if (status.equals("success"))
                {
                    Signup.fromSignUp = "0";
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    String userStr = jsonObject.getString("user");
                    JSONObject user_info = new JSONObject(userStr);
                  /*  UserVerifiedBean userVerifiedBean = new Gson().fromJson(jsonObject.getString("user") + "", UserVerifiedBean.class);
                    JSONObject userJSON = new JSONObject(new Gson().toJson(userVerifiedBean));

                    openActivity.open(ActivityCovacardHome.class , false);
                    customToast.showToast(msg , 0 , 1);*/
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
//								System.out.println("--onlinecare symptom name"+symptomsArray.getJSONObject(i).getString("symptom_name"));
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
//								System.out.println("--onlinecare condition name"+conditionsArray.getJSONObject(i).getString("condition_name"));
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
//								System.out.println("--onlinecare speciality name"+specialityArray.getJSONObject(i).getString("speciality_name"));
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
        else if (apiName.equals(ApiManager.SIMPLE_OTP_VERIFY))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
                System.out.println("-- chk"+jsonObject.toString());

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");
                if (status.equals("success")) {
                    Signup.fromSignUp = "0";
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    openActivity.open(ActivityCovacardHome.class , true);
                    customToast.showToast(msg , 0 , 1);
                }
                else if (status.equals("error"))
                {
                    customToast.showToast(msg, 0, 1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(apiName.equals(ApiManager.CREATE_TOKEN)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String access_token = jsonObject.getString("access_token");
                prefs.edit().putString("access_token", access_token).commit();


                Intent intent1 = new Intent();
                intent1.setAction("com.app.onlinecare.START_SERVICE");
                activity.sendBroadcast(intent1);
                openActivity.open(SubUsersList.class, true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

        //System.out.println("--online care waiting time millis: "+millis + "hrs: "+hours+" mins: "+minutes);

//		return hours+" hours, "+minutes+" minutes";

        String h = hours < 10 ? "0"+hours : ""+hours;
        String m = minutes < 10 ? "0"+minutes : ""+minutes;
        String s = seconds < 10 ? "0"+seconds : ""+seconds;
        return h+":"+m+":"+s;
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

    @Override
    public void onBackPressed() {

        if (Signup.fromSignUp.equals("2"))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Are you sure ? Do you want to exit the verification process. Please make sure that your account will not be activated until you complete the verification process.")
                    .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            otp_view.setText("");
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    })
                    .setNegativeButton("Not Now", null)
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
        else {
            //super.onBackPressed();
            AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Are you sure ? Do you want to exit the verification process. Please make sure that your account will not be created.")
                    .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            otp_view.setText("");
                        }
                    })
                    .setNegativeButton("Not Now", null)
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }


}