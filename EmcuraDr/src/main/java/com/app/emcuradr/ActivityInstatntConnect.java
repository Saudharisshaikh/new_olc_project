package com.app.emcuradr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.app.emcuradr.adapter.VVReportImagesAdapter2;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.MyAppointmentsModel;
import com.app.emcuradr.model.StatesBeans;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.DatePickerFragment;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityInstatntConnect extends BaseActivity {



    EditText etInstantConnectFirstName, etInstantConnectLastName, etInstantConnectCellNo, etInstantConnectEmail,
    etInstantConnectAddress,etInstantConnectCity,etInstantConnectState,
            etInstantConnectZipcode,etInstantConnectBirthday;

    RadioGroup radioGroupGender;
    Button btnInstantConnectStart,btnInstantConnectNotNow;
    ImageView ivBack;

    CheckBox cbInstantConnectAddFamily;
    LinearLayout layInstantConnectFamily;
    EditText etInstantConnectFirstNameFamily, etInstantConnectLastNameFamily, etInstantConnectCellNoFamily, etInstantConnectEmailFamily;

    String selectedGender = "";
    String patId;
    Spinner spStates;
    ArrayList<StatesBeans> statesBeans;

    String stateName,state_Code = "";
    public static boolean isFromPatientProfile;
    public static String call_id_instant_connect;

    String email,phone, first_name,last_name,birthday,address,state,city,zipcode,patientId;

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
        etInstantConnectAddress = findViewById(R.id.et_address);
        etInstantConnectCity = findViewById(R.id.et_city);
        //etInstantConnectState = findViewById(R.id.et_state);
        etInstantConnectZipcode = findViewById(R.id.et_zipcode);
        etInstantConnectBirthday = findViewById(R.id.etSignupBirthdate);
        etInstantConnectBirthday.setCursorVisible(false);
        btnInstantConnectStart = findViewById(R.id.btnInstantConnectStart);
        btnInstantConnectNotNow = findViewById(R.id.btnInstantConnectNotNow);
        ivBack = findViewById(R.id.ivBack);
        //cbInstantConnectAddFamily = findViewById(R.id.cbInstantConnectAddFamily);
        //layInstantConnectFamily = findViewById(R.id.layInstantConnectFamily);
//        etInstantConnectFirstNameFamily = findViewById(R.id.etInstantConnectFirstNameFamily);
//        etInstantConnectLastNameFamily = findViewById(R.id.etInstantConnectLastNameFamily);
//        etInstantConnectCellNoFamily = findViewById(R.id.etInstantConnectCellNoFamily);
//        etInstantConnectEmailFamily = findViewById(R.id.etInstantConnectEmailFamily);
        spStates = findViewById(R.id.spState);
        spStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int statePos, long l) {
                stateName = statePos ==0 ?"":statesBeans.get(statePos).full_name;
                state_Code = statePos ==0?"":statesBeans.get(statePos).short_name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGroupGender = findViewById(R.id.rgSignupStates);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioFemale) {
                    selectedGender = "0";
                }else if (checkedId == R.id.radioMale) {
                    selectedGender = "1";
                }

            }
        });


        etInstantConnectBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //DATA.isDatePickerCallFromSignup = true;
                    DialogFragment newFragment = new DatePickerFragment(etInstantConnectBirthday);
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });


        etInstantConnectBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etInstantConnectBirthday);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        if(isFromPatientProfile){
            etInstantConnectFirstName.setText(ActivityTcmDetails.ptFname);
            etInstantConnectLastName.setText(ActivityTcmDetails.ptLname);
            etInstantConnectCellNo.setText(ActivityTcmDetails.ptPhone);
            etInstantConnectEmail.setText(ActivityTcmDetails.ptEmail);

            etInstantConnectFirstName.setFocusable(false);
            etInstantConnectFirstName.setInputType(InputType.TYPE_NULL);
            etInstantConnectLastName.setFocusable(false);
            etInstantConnectLastName.setInputType(InputType.TYPE_NULL);
           // cbInstantConnectAddFamily.setVisibility(View.GONE);
        }

//        cbInstantConnectAddFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            int vis = isChecked ? View.VISIBLE : View.GONE;
//            //layInstantConnectFamily.setVisibility(vis);// already commited
//        });

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
        loadStates();

    }
    public void loadStates()
    {
        ApiManager.shouldShowPD= false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_STATES,"get",null,this, activity);
        apiManager.loadURL();
    }


    private void sendInstantConnectInvite(){

         first_name = etInstantConnectFirstName.getText().toString().trim();
         last_name = etInstantConnectLastName.getText().toString().trim();
         email = etInstantConnectEmail.getText().toString().trim();
         phone = etInstantConnectCellNo.getText().toString().trim();
         birthday = etInstantConnectBirthday.getText().toString().trim();
         address = etInstantConnectAddress.getText().toString().trim();
         zipcode = etInstantConnectZipcode.getText().toString().trim();
//         state = etInstantConnectState.getText().toString().trim();
         city = etInstantConnectCity.getText().toString().trim();

        boolean validated = true;

        String generalMessage = "First name is required.\nLast name is required.\nGender is required.\nBirthdate is required.\nResidency is required.\nCity is required.\nState is required.\nZipcode is required.\nEmail is required.";
        if(TextUtils.isEmpty(first_name)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
         return;
        }

        if(TextUtils.isEmpty(last_name)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        if(TextUtils.isEmpty(email)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }
        if(TextUtils.isEmpty(phone)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        if(TextUtils.isEmpty(birthday)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        if(TextUtils.isEmpty(address)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        if(TextUtils.isEmpty(zipcode)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        if(TextUtils.isEmpty(state_Code)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }
        if(TextUtils.isEmpty(city)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }
        if(selectedGender.equals("")){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","","Emcura Doc");
            return;
        }

        validated = true;

//        if(TextUtils.isEmpty(first_name)){
//            etInstantConnectFirstName.setError("Required");
//            validated = false;
//        }
//        if(TextUtils.isEmpty(last_name)){
//            etInstantConnectLastName.setError("Required");
//            validated = false;
//        }


        /*if(! Signup.isValidEmail(email)){
            etInstantConnectEmail.setError("Invalid email address");
            validated = false;
        }
        if(TextUtils.isEmpty(phone)){
            etInstantConnectCellNo.setError("Required");
            validated = false;
        }*/

//        if(TextUtils.isEmpty(phone) && ! Signup.isValidEmail(email)){
//            validated = false;
//            customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
//        }


        String family_first_name = "" , family_last_name = "", family_email = "", family_phone = "";
//        if(cbInstantConnectAddFamily.getVisibility() == View.VISIBLE && cbInstantConnectAddFamily.isChecked()){
//            family_first_name = etInstantConnectFirstNameFamily.getText().toString().trim();
//            family_last_name = etInstantConnectLastNameFamily.getText().toString().trim();
//            family_email = etInstantConnectEmailFamily.getText().toString().trim();
//            family_phone = etInstantConnectCellNoFamily.getText().toString().trim();
//
//            if(TextUtils.isEmpty(family_phone) && ! Signup.isValidEmail(family_email)){
//                validated = false;
//                customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
//            }
//        }

//        if(!validated){
//            customToast.showToast("Please enter the required information",0,0);
//            return;
//        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("phone", phone);
        params.put("email", email);
        params.put("birthdate",birthday);
        params.put("residency",address);
        params.put("city",city);
        params.put("state",state_Code);
        params.put("zipcode",zipcode);
        params.put("gender",selectedGender);


//        if(cbInstantConnectAddFamily.getVisibility() == View.VISIBLE && cbInstantConnectAddFamily.isChecked()){
//            params.put("family_firstname", family_first_name);
//            params.put("family_lastname", family_last_name);
//            params.put("family_email", family_email);
//            params.put("family_phone", family_phone);
//        }

        ApiManager apiManager = new ApiManager(ApiManager.INSTANT_PATIENT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    private void sendCallInvite(){

        //String first_name = etInstantConnectFirstName.getText().toString().trim();
        //String last_name = etInstantConnectLastName.getText().toString().trim();
        String email = etInstantConnectEmail.getText().toString().trim();
        String phone = etInstantConnectCellNo.getText().toString().trim();
        String firstName = etInstantConnectFirstName.getText().toString().trim();
        String lastName = etInstantConnectLastName.getText().toString().trim();
        String birthday = etInstantConnectBirthday.getText().toString().trim();
        String address = etInstantConnectAddress.getText().toString().trim();
        String zipcode = etInstantConnectZipcode.getText().toString().trim();
        String state = etInstantConnectState.getText().toString().trim();
        String city = etInstantConnectCity.getText().toString().trim();

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

        String generalMessage = "First name is required.\nLast name is required.\nGender is required.\nBirthdate is required.\nResidency is required.\nCity is required.\nState is required.\nZipcode is required.\nEmail is required.";
        if(TextUtils.isEmpty(firstName)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(lastName)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(email)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }
        if(TextUtils.isEmpty(phone)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(birthday)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(address)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(zipcode)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        if(TextUtils.isEmpty(state)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }
        if(TextUtils.isEmpty(city)){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }
        if(selectedGender.equals("")){
            validated = false;

            GeneralAlertDialog.callAlert(ActivityInstatntConnect.this,generalMessage,()->{

            },()->{

            },"OK","Not Now","Emcura Doc");

        }

        validated = true;




//        if(TextUtils.isEmpty(phone) && ! Signup.isValidEmail(email)){
//            validated = false;
//            customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
//        }
//
//        if(!validated){
//            customToast.showToast("Please enter the required information",0,0);
//            return;
//        }

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

                    DATA.isInstantConnect = true;
                    DATA.rndSessionId = jsonObject.getString("call_session_id");
                    Constants.ROOM_NAME_MULTI = DATA.rndSessionId;
                     patId = jsonObject.getString("patient_id");
                    DATA.selectedUserCallId = patId;
                    DATA.selectedUserCallName = first_name+" "+last_name;
                    DATA.selectedUserCallDOB = "";
                    DATA.incommingCallId = call_id_instant_connect;
                    DATA.selectedUserCallSympTom = "";
                    DATA.selectedUserCallCondition = "";
                    DATA.selectedUserQbid = "";
                    DATA.date = "";
                    DATA.selectedUserCallDescription = "";



                    ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+patId,"get",null,this, activity);
                    apiManager.loadURL();





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



        else if(apiName.equalsIgnoreCase(ApiManager.CALLING_INVITE_CALL)){
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

        else if(apiName.contains(ApiManager.PATIENT_DETAIL)){

            String  symptoms = "";
            String condition = "";
            String descrption = "";
            String symDetails = "";

            DATA.print("datas:");

            try {
                JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
                String pName = jsonObject.getJSONObject("patient_data").getString("first_name")+" "+
                        jsonObject.getJSONObject("patient_data").getString("last_name");
                String pImage = jsonObject.getJSONObject("patient_data").getString("pimage");
                String pPhone   = jsonObject.getJSONObject("patient_data").getString("phone");
                String tvSelPtEmail =jsonObject.getJSONObject("patient_data").getString("email");
                String  tvSelPtAddress = jsonObject.getJSONObject("patient_data").getString("residency");
                String tvSelPtDOB =jsonObject.getJSONObject("patient_data").getString("birthdate");

                String patient_ID_ICD = sharedPrefsHelper.get("patient_ID_ICD", "");
                String patient_icd_codes = sharedPrefsHelper.get("patient_icd_codes", "");

                if(!jsonObject.getString("virtual_visit").isEmpty()){
                    JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");

                    symptoms = virtual_visit.getString("symptom_name");
                    condition =  virtual_visit.getString("condition_name");
                    descrption = virtual_visit.getString("description");


                    DATA.selectedUserCallSympTom = symptoms;
                    DATA.selectedUserCallCondition = condition;
                    DATA.selectedUserCallDescription = descrption;

                    //svPatientDetails.scrollTo(0,0);
                }

                DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
                MyAppointmentsModel temp = new MyAppointmentsModel();
                temp.id = patId;
                temp.liveCheckupId = "";//appointmentsArray.getJSONObject(i).getString("live_checkup_id");
                temp.first_name = first_name;
                temp.last_name = last_name;
                temp.symptom_name = symptoms;//appointmentsArray.getJSONObject(i).getString("symptom_name");
                temp.condition_name = condition;//appointmentsArray.getJSONObject(i).getString("condition_name");
                temp.description = descrption;//appointmentsArray.getJSONObject(i).getString("description");
                temp.patients_qbid = "";
                temp.datetime = "";//appointmentsArray.getJSONObject(i).getString("datetime");

                temp.latitude = 0.0;
                temp.longitude = 0.0;
                temp.image = "";
                temp.birthdate = "";
                temp.gender = "";
                temp.residency = "";
                temp.patient_phone = "";
                temp.StoreName = "";//appointmentsArray.getJSONObject(i).getString("StoreName");
                temp.PhonePrimary = "";//appointmentsArray.getJSONObject(i).getString("PhonePrimary");
                temp.pharmacy_address = "";

                temp.is_online = "false";

                DATA.selectedLiveCare = temp;

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








            } catch (JSONException e) {
                e.printStackTrace();

                DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
                MyAppointmentsModel temp = new MyAppointmentsModel();
                temp.id = patId;
                temp.liveCheckupId = "";//appointmentsArray.getJSONObject(i).getString("live_checkup_id");
                temp.first_name = first_name;
                temp.last_name = last_name;
                temp.symptom_name = symptoms;//appointmentsArray.getJSONObject(i).getString("symptom_name");
                temp.condition_name = condition;//appointmentsArray.getJSONObject(i).getString("condition_name");
                temp.description = descrption;//appointmentsArray.getJSONObject(i).getString("description");
                temp.patients_qbid = "";
                temp.datetime = "";//appointmentsArray.getJSONObject(i).getString("datetime");

                temp.latitude = 0.0;
                temp.longitude = 0.0;
                temp.image = "";
                temp.birthdate = "";
                temp.gender = "";
                temp.residency = "";
                temp.patient_phone = "";
                temp.StoreName = "";//appointmentsArray.getJSONObject(i).getString("StoreName");
                temp.PhonePrimary = "";//appointmentsArray.getJSONObject(i).getString("PhonePrimary");
                temp.pharmacy_address = "";

                temp.is_online = "false";

                DATA.selectedLiveCare = temp;

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



                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }

        else if (apiName.equalsIgnoreCase(ApiManager.GET_STATES)) {
            try {
                // Assuming 'content' is the API response string
                JSONObject jsonObject = new JSONObject(content);

                // Check if the desired data is within a key named "data" (modify based on your API structure)
                if (jsonObject.has("data") && jsonObject.get("data") instanceof JSONArray) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    statesBeans = new ArrayList<StatesBeans>();
                    StatesBeans statesBean;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject stateObject = jsonArray.getJSONObject(i);
                        String id = stateObject.getString("id");
                        String full_name = stateObject.getString("full_name");
                        String short_name = stateObject.getString("short_name");

                        statesBean = new StatesBeans(id, full_name, short_name);
                        statesBeans.add(statesBean);
                        statesBean = null;
                    }
                } else {
                    // Handle the case where "data" is missing or not a JSONArray (optional)
                    customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
                }

                // Assuming statesBeans is populated correctly at this point
                ArrayAdapter<StatesBeans> statesArrayAdapter =
                        new ArrayAdapter<StatesBeans>(activity, R.layout.spinner_item_lay,
                                android.R.id.text1, statesBeans);
                statesBeans.add(0, new StatesBeans("", "Select", ""));
                spStates.setAdapter(statesArrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            }

        }
    }


}
