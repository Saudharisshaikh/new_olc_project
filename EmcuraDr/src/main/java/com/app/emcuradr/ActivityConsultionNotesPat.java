package com.app.emcuradr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.adapter.AllScriptsPatientAdapter;
import com.app.emcuradr.adapter.ExistingPatientAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.AllScriptPatient;
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.model.MyAppointmentsModel;
import com.app.emcuradr.util.AllScriptPatientListener;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.ExistingPatientCall;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityConsultionNotesPat extends BaseActivity
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


    TextView activityName;

    EditText patfirstName,patlastName,patphone,patzipcode,patdob;

    AllScriptsPatientAdapter allScriptsPatientAdapter;
    ArrayList<AllScriptPatient> allScriptPatientArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_notepat);

        activity = ActivityConsultionNotesPat.this;
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
        activityName = findViewById(R.id.activity_name);
        activityName.setText("Search Patient");
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        existingPatientArrayList = new ArrayList<>();
        adapter = new ExistingPatientAdapter(existingPatientArrayList,this,this);
        recyclerView.setAdapter(adapter);
        allScriptPatientArrayList = new ArrayList<>();
        allScriptsPatientAdapter = new AllScriptsPatientAdapter(allScriptPatientArrayList,this,this);
        allscriptRecyclerview.setAdapter(allScriptsPatientAdapter);
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
                            params, apiCallBack, ActivityConsultionNotesPat.this);
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

    Intent intent = new Intent(ActivityConsultionNotesPat.this,ActivityConsultionNotes.class);
    intent.putExtra("search_patient",existingPatient);
    intent.putExtra("patient_type","search_patient");
    startActivity(intent);


    }

    @Override
    public void selectedPatient(AllScriptPatient patient) {

        Intent intent = new Intent(ActivityConsultionNotesPat.this,ActivityConsultionNotes.class);
        intent.putExtra("allscript_patient",patient);
        intent.putExtra("patient_type","allscript_patient");
        startActivity(intent);

    }
}