package com.app.emcuradr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.adapter.AllScriptsPatientAdapter;
import com.app.emcuradr.adapter.ExistingPatientAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.CustomSnakeBar;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.model.AllScriptPatient;
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.model.IcdCodeBean;
import com.app.emcuradr.model.MyAppointmentsModel;
import com.app.emcuradr.util.AllScriptPatientListener;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.Database;
import com.app.emcuradr.util.DialogPatientInfo;
import com.app.emcuradr.util.ExistingPatientCall;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityExistingPatCall extends BaseActivity
        implements ApiCallBack, ExistingPatientCall, AllScriptPatientListener {

    Activity activity;
    CustomToast customToast;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    ImageView backImage;

    EditText editTextSearch;
    Button btnSearch,btnSearch2;
    RecyclerView recyclerView,allscriptRecyclerview;
    ArrayList<ExistingPatient> existingPatientArrayList;
    ExistingPatientAdapter adapter;
    LinearLayout noLayout,noLayout2;
    LinearLayout mainLayout,mainLayout2;
    Button onlinecarebtn,allscriptbtn;
    LinearLayout onlinecareLayout,allscriptLayout;
    String patId;
    String patName;
    String patDOB;
    Double patLatitude;
    Double patLongitude;
    String patientType = "online";
    public static String call_id_instant_connect;

    EditText patfirstName,patlastName,patphone,patzipcode,patdob;

    AllScriptsPatientAdapter allScriptsPatientAdapter;
    ArrayList<AllScriptPatient> allScriptPatientArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_pat_cal);

        activity = ActivityExistingPatCall.this;
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        backImage = findViewById(R.id.ivBack);
        apiCallBack = this;
        editTextSearch = findViewById(R.id.edt_search_pat);
        btnSearch = findViewById(R.id.btn_search_pat);
        recyclerView = findViewById(R.id.rv_existing_pat);
        noLayout = findViewById(R.id.no_layout);
        mainLayout = findViewById(R.id.main_layout);
        mainLayout2 = findViewById(R.id.main_layout2);
        onlinecarebtn = findViewById(R.id.btnOnlineCare);
        allscriptbtn = findViewById(R.id.btnAllScript);
        onlinecareLayout = findViewById(R.id.onlinecareLayout);
        allscriptLayout = findViewById(R.id.allscriptLayout);
        patfirstName = findViewById(R.id.et_firstname);
        patlastName = findViewById(R.id.et_lastname);
        patphone = findViewById(R.id.et_phone);
        patzipcode = findViewById(R.id.et_zipcode);
        patdob  = findViewById(R.id.et_dob);
        btnSearch2 = findViewById(R.id.btn_search_pat2);
        noLayout2 = findViewById(R.id.no_layout2);
        allscriptRecyclerview = findViewById(R.id.rv_allscript);
        allscriptRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        allScriptPatientArrayList = new ArrayList<>();
        allScriptsPatientAdapter = new AllScriptsPatientAdapter(allScriptPatientArrayList,this,this);
        allscriptRecyclerview.setAdapter(allScriptsPatientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        existingPatientArrayList = new ArrayList<>();
        adapter = new ExistingPatientAdapter(existingPatientArrayList,this,this);
        recyclerView.setAdapter(adapter);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString();
                if(searchText.equals("")){
                    customToast.showToast("Please first type to search.",0,0);
                }
                else {
                    searchPatient(searchText);
                }
            }
        });

        onlinecarebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                patientType = "online";
                onlinecarebtn.setBackgroundResource(R.drawable.btn_selector);
                onlinecarebtn.setTextColor(getResources().getColor(R.color.cardview_light_background));
                allscriptbtn.setBackgroundResource(R.drawable.button_drawable_white_selected);
                allscriptbtn.setTextColor(getResources().getColor(R.color.theme_red));
                onlinecareLayout.setVisibility(View.VISIBLE);
                allscriptLayout.setVisibility(View.GONE);

                if(existingPatientArrayList.size()>1){
                    noLayout.setVisibility(View.GONE);
                    noLayout2.setVisibility(View.GONE);
                }
                else {
                    noLayout.setVisibility(View.VISIBLE);
                    noLayout2.setVisibility(View.GONE);
                }

            }
        });

        allscriptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientType = "allscript";
                allscriptbtn.setBackgroundResource(R.drawable.btn_selector);
                allscriptbtn.setTextColor(getResources().getColor(R.color.cardview_light_background));
                onlinecarebtn.setBackgroundResource(R.drawable.button_drawable_white_selected);
                onlinecarebtn.setTextColor(getResources().getColor(R.color.theme_red));
                onlinecareLayout.setVisibility(View.GONE);
                allscriptLayout.setVisibility(View.VISIBLE);

                if(allScriptPatientArrayList.size()>1){
                    noLayout.setVisibility(View.GONE);
                    noLayout2.setVisibility(View.GONE);
                }
                else {
                    noLayout.setVisibility(View.GONE);
                    noLayout2.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstNames = patfirstName.getText().toString();
                String lastNames  = patlastName.getText().toString();
                String phones = patphone.getText().toString();
                String zipcodes = patzipcode.getText().toString();
                String dobs = patdob.getText().toString();

                if(firstNames.equals("") && lastNames.equals("") && phones.equals("") && zipcodes.equals("") && dobs.equals("")){
                    customToast.showToast("Please fill at least one field to search.",0,0);
                }
                else {

                    RequestParams params = new RequestParams();
                    params.put("first_name", firstNames);
                    params.put("last_name", lastNames);
                    params.put("phone", phones);
                    params.put("zip_code", zipcodes);
                    params.put("dob", dobs);
                    ApiManager apiManager = new ApiManager(ApiManager.ALLSCRIPTPATIENT_SEARCH, "post",
                            params, apiCallBack, ActivityExistingPatCall.this);
                    apiManager.loadURL();

                }

            }
        });



    }

    public void searchPatient(String searchText){

        RequestParams params = new RequestParams();
        params.put("keyword", searchText);
        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_EXISTING_SEARCH, "post",
                params, apiCallBack, activity);
        apiManager.loadURL();

    }


    //Ahmer code for chatGPT auto Note

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

        DATA.print("patExist:"+content);
        if (apiName.equals(ApiManager.PATIENT_EXISTING_SEARCH)) {
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length()<1){
                    customToast.showToast("No patient found.",0,0);
                    noLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                }
                else {
                    noLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    existingPatientArrayList.clear();

                    for(int i=0;i<data.length();i++){
                        ExistingPatient existingPatient = new ExistingPatient();
                        existingPatient.firstName = data.getJSONObject(i).getString("first_name");
                        existingPatient.lastName = data.getJSONObject(i).getString("last_name");
                        existingPatient.email = data.getJSONObject(i).getString("email");
                        existingPatient.phone = data.getJSONObject(i).getString("phone");
                        existingPatient.is_online = data.getJSONObject(i).getString("is_online");
                        existingPatient.id = data.getJSONObject(i).getString("id");
                        existingPatient.birthdate = data.getJSONObject(i).getString("birthdate");
                        existingPatient.latitude = data.getJSONObject(i).getString("latitude");
                        existingPatient.longitude = data.getJSONObject(i).getString("longitude");

                        DATA.print("isO:"+existingPatient.is_online);
                        existingPatientArrayList.add(existingPatient);
                    }
                    adapter.notifyDataSetChanged();

                }


            }
            catch (Exception e){
            }
        }

        else if(apiName.equalsIgnoreCase(ApiManager.CALLING_INVITE_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                // {"redirect":"https:\/\/www.onlinecare.com\/fivestars\/video_chat\/open_chat\/24763","call_id":24763}
                //if (jsonObject.optString("status").equalsIgnoreCase("success")){}
                if (jsonObject.has("redirect")){


                    call_id_instant_connect = jsonObject.optString("call_id", "0");

                    DialogPatientInfo.patientIdGCM = patId;

                    DATA.isExistingPatCall = true;
                    DATA.isFromDocToDoc = false;//To ensure correct flow after call
                    DATA.incomingCall = false;
                    DATA.selectedUserCallId = patId;
                    DATA.selectedUserCallName = patName;
                    DATA.selectedUserCallDOB = patDOB;
                    DATA.incommingCallId = call_id_instant_connect;
                    DATA.selectedUserCallSympTom ="";
                    DATA.selectedUserCallCondition = "";
                    DATA.selectedUserQbid = "";
                    DATA.date = "";
                    DATA.selectedUserLatitude = patLatitude;
                    DATA.selectedUserLongitude = patLongitude;
                    DATA.print("calId:"+DATA.incommingCallId+" "+patId+" "+patName+" "+call_id_instant_connect+" "+patDOB);




                    DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
                    MyAppointmentsModel temp = new MyAppointmentsModel();
                    temp.id = patId;
                    temp.liveCheckupId = "";//appointmentsArray.getJSONObject(i).getString("live_checkup_id");
                    temp.first_name = patName;
                    temp.last_name = patName;
                    temp.symptom_name = "";//appointmentsArray.getJSONObject(i).getString("symptom_name");
                    temp.condition_name = "";//appointmentsArray.getJSONObject(i).getString("condition_name");
                    temp.description = "";//appointmentsArray.getJSONObject(i).getString("description");
                    temp.patients_qbid = "";
                    temp.datetime = "";//appointmentsArray.getJSONObject(i).getString("datetime");

                    temp.latitude = patLatitude;
                    temp.longitude = patLongitude;
                    temp.image = "";
                    temp.birthdate = patDOB;
                    temp.gender = "";
                    temp.residency = "";
                    temp.patient_phone = "";
                    temp.StoreName = "";//appointmentsArray.getJSONObject(i).getString("StoreName");
                    temp.PhonePrimary = "";//appointmentsArray.getJSONObject(i).getString("PhonePrimary");
                    temp.pharmacy_address = "";

                    temp.is_online = "false";

                    DATA.selectedLiveCare = temp;


                    if(patientType.equals("allscript")){
                        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Instant connect invitation has been sent successfully.")
                                .setPositiveButton("Start Instant Session",null).create();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {


                                Intent i = new Intent(activity, MainActivity.class);
                                i.putExtra("isFromExistingConnect", true);
                                startActivity(i);

                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                    else {
                        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+patId,"get",null,this, activity);
                        apiManager.loadURL();
                    }



                }

                else if(jsonObject.has("message")){
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
                temp.first_name = pName;
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




                        Intent i = new Intent(activity, MainActivity.class);
                        i.putExtra("isFromExistingConnect", true);
                        startActivity(i);

                        finish();
                    }
                });
                alertDialog.show();







            } catch (JSONException e) {
                e.printStackTrace();

                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }

        if (apiName.equals(ApiManager.ALLSCRIPTPATIENT_SEARCH)) {
            try {
                JSONObject jsonObject = new JSONObject(content);


                String status = jsonObject.getString("status");
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length()<1){
                    customToast.showToast("No patient found.",0,0);
                    noLayout2.setVisibility(View.VISIBLE);
                    mainLayout2.setVisibility(View.GONE);
                }
                else {
                    noLayout2.setVisibility(View.GONE);
                    mainLayout2.setVisibility(View.VISIBLE);
                    allScriptPatientArrayList.clear();

                    for(int i=0;i<data.length();i++){
                        AllScriptPatient existingPatient = new AllScriptPatient();
                        existingPatient.firstName = data.getJSONObject(i).optString("firstname");
                        existingPatient.lastName = data.getJSONObject(i).optString("lastname");
                        existingPatient.emailAddress = data.getJSONObject(i).optString("Email_Address");
                        existingPatient.cellPhone = data.getJSONObject(i).optString("CellPhone");
                        existingPatient.gender = data.getJSONObject(i).optString("gender");
                        existingPatient.homePhone = data.getJSONObject(i).optString("HomePhone");
                        existingPatient.dateOfBirth = data.getJSONObject(i).optString("dateofbirth");
                        existingPatient.addressLine1 = data.getJSONObject(i).optString("AddressLine1");
                        existingPatient.ID = data.getJSONObject(i).optString("ID");


                        allScriptPatientArrayList.add(existingPatient);

                    }
                    allScriptsPatientAdapter.notifyDataSetChanged();


                }


            }
            catch (Exception e){
                DATA.print("--alls:"+e.getMessage());
            }
        }



    }

    @Override
    public void existingPatient(ExistingPatient existingPatient) {



        DATA.rndSessionId = new VCallModule(activity).randomStr();
        Constants.ROOM_NAME_MULTI = DATA.rndSessionId;

        RequestParams params = new RequestParams();

        params.put("phone_num",existingPatient.phone);
        params.put("email", existingPatient.email);
        params.put("patient_id", existingPatient.id);
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("call_session_id", DATA.rndSessionId);

        patId = existingPatient.id;
        patName = existingPatient.firstName+" "+existingPatient.lastName;
        patDOB = existingPatient.birthdate;
        patLatitude = Double.parseDouble(existingPatient.latitude);
        patLongitude = Double.parseDouble(existingPatient.longitude) ;
        ApiManager apiManager = new ApiManager(ApiManager.CALLING_INVITE_CALL,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void selectedPatient(AllScriptPatient patient) {


        DATA.rndSessionId = new VCallModule(activity).randomStr();
        Constants.ROOM_NAME_MULTI = DATA.rndSessionId;

        RequestParams params = new RequestParams();

        params.put("phone_num",patient.cellPhone);
        params.put("email", patient.emailAddress);
        params.put("patient_id", patient.ID);
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("call_session_id", DATA.rndSessionId);

        patId = patient.ID;
        patName = patient.firstName+" "+patient.lastName;
        patDOB = patient.dateOfBirth;
        patLatitude = 0.0;
        patLongitude = 0.0;
        ApiManager apiManager = new ApiManager(ApiManager.CALLING_INVITE_CALL,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
}