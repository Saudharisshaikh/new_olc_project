package com.app.OnlineCareUS_Dr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.OnlineCareUS_Dr.adapter.NurseAdapter;
import com.app.OnlineCareUS_Dr.api.ApiManager;
import com.app.OnlineCareUS_Dr.model.NurseBean;
import com.app.OnlineCareUS_Dr.util.CheckInternetConnection;
import com.app.OnlineCareUS_Dr.util.CustomToast;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.app.OnlineCareUS_Dr.util.GloabalMethods;
import com.app.OnlineCareUS_Dr.util.HideShowKeypad;
import com.app.OnlineCareUS_Dr.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityFindNurse extends AppCompatActivity implements View.OnClickListener{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    LinearLayout layTcmOptions;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,
            tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabAll;
    ListView lvNurse;
    EditText etSearchQuery;
    ImageView ivSearchQuery;

    String patientType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nurse);

        patientType = getIntent().getStringExtra("patientType");

        activity = ActivityFindNurse.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        layTcmOptions = (LinearLayout) findViewById(R.id.layTcmOptions);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        lvNurse = (ListView) findViewById(R.id.lvNurse);

        etSearchQuery = (EditText) findViewById(R.id.etSearchQuery);
        ivSearchQuery = (ImageView) findViewById(R.id.ivSearchQuery);

        tvTabNurse = (TextView) findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = (TextView) findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = (TextView) findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = (TextView) findViewById(R.id.tvTabDietitian);
        tvTabOT = (TextView) findViewById(R.id.tvTabOT);
        tvTabPharmacist = (TextView) findViewById(R.id.tvTabPharmacist);
        tvTabMA = (TextView) findViewById(R.id.tvTabMA);
        tvTabAll = findViewById(R.id.tvTabAll);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);

        tvTabNurse.setOnClickListener(this);
        tvTabNursePractitioner.setOnClickListener(this);
        tvTabSocialWorker.setOnClickListener(this);
        tvTabDietitian.setOnClickListener(this);
        tvTabOT.setOnClickListener(this);
        tvTabPharmacist.setOnClickListener(this);
        tvTabMA.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);




        DATA.print("-- on nurse activity patientType = "+patientType);
        try {
            int count = patientType.split(",").length;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvTabTCM.getLayoutParams();
            params.weight = (float) 4/count;

            patient_cat = patientType.split(",")[0];
            if (checkInternetConnection.isConnectedToInternet()){
                find_nurse("",DATA.selectedUserCallId,patient_cat);
            }else {
                customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
            }

            if (patientType.contains("tcm")){
                tvTabTCM.setVisibility(View.VISIBLE);
                tvTabTCM.setLayoutParams(params);
            }else {
                tvTabTCM.setVisibility(View.GONE);
            }
            if (patientType.contains("complex_care")){
                tvTabCC.setVisibility(View.VISIBLE);
                tvTabCC.setLayoutParams(params);
            }else {
                tvTabCC.setVisibility(View.GONE);
            }
            if (patientType.contains("home_health")){
                tvTabHomeHealth.setVisibility(View.VISIBLE);
                tvTabHomeHealth.setLayoutParams(params);
            }else {
                tvTabHomeHealth.setVisibility(View.GONE);
            }
            if (patientType.contains("nursing_home")){
                tvTabNursingHome.setVisibility(View.VISIBLE);
                tvTabNursingHome.setLayoutParams(params);
            }else {
                tvTabNursingHome.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkInternetConnection.isConnectedToInternet()){
                    if (nurseBeens.get(position).is_added.equals("0")){
                        request_to_add_nurse(nurseBeens.get(position).id,DATA.selectedUserCallId,patient_cat,position);
                    }else {
                        delete_care_management(nurseBeens.get(position).id,position,patient_cat);
                    }
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
            }
        });

        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivSearchQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearchQuery.getText().toString().isEmpty()) {
                    customToast.showToast("Please type care provider name to search",0,0);
                }else {
                    if (checkInternetConnection.isConnectedToInternet()){
                        find_nurse(etSearchQuery.getText().toString(),DATA.selectedUserCallId,patient_cat);
                    }else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                    }
                }
            }
        });
    }

    ArrayList<NurseBean> nurseBeens;
    ArrayList<NurseBean> nurseBeensOrig;
    NurseBean bean;
    NurseAdapter nurseAdapter;
    public void find_nurse(String keyword,String patient_id,String patient_category) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity,client);
        //DATA.print("-- token: "+"Bearer "+prefs.getString("access_token",""));
        //client.addHeader("Oauthtoken","Bearer "+prefs.getString("access_token",""));

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        if (!keyword.isEmpty()){
            params.put("keyword", keyword);
        }
        params.put("patient_id", patient_id);
        params.put("patient_category", patient_category);


        String reqURL = DATA.baseUrl+"find_nurse";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params: "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in find_nurse "+content);

                    try {
                        nurseBeensOrig = new ArrayList<NurseBean>();
                        nurseBeens = new ArrayList<NurseBean>();
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray nurses = jsonObject.getJSONArray("nurses");
                        for (int i = 0; i<nurses.length(); i++){
                            String id = nurses.getJSONObject(i).getString("id");
                            String first_name = nurses.getJSONObject(i).getString("first_name");
                            String last_name = nurses.getJSONObject(i).getString("last_name");
                            String image = nurses.getJSONObject(i).getString("image");
                            String doctor_category = nurses.getJSONObject(i).getString("doctor_category");
                            String is_added = nurses.getJSONObject(i).getString("is_added");
                            String is_online = nurses.getJSONObject(i).getString("is_online");

                            bean = new NurseBean(id,first_name,last_name,image,doctor_category, is_added,is_online);
                            //if (doctor_category.equalsIgnoreCase("Nurse")){//adding all
                            nurseBeens.add(bean);
                            //}
                            nurseBeensOrig.add(bean);
                            bean = null;

                        }
                        nurseAdapter = new NurseAdapter(activity,nurseBeens);
                        lvNurse.setAdapter(nurseAdapter);

                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure find_nurse : "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }

    String patient_cat = "";
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "tcm";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "complex_care";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "home_health";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "nursing_home";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabNurse:
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }

                break;

            case R.id.tvTabNursePractitioner:
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;
            case R.id.tvTabSocialWorker:
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;
            case R.id.tvTabDietitian:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;

            case R.id.tvTabOT:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;

            case R.id.tvTabPharmacist:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Pharmacist")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;
            case R.id.tvTabMA:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("MA")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }
                break;

            case R.id.tvTabAll:
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));

                if (nurseBeensOrig !=null){
                    /*nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }*/
                    nurseBeens.addAll(nurseBeensOrig);
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurse.setAdapter(nurseAdapter);
                }

                break;
            default:
                break;
        }
    }

    public void request_to_add_nurse(String nurse_id, String patient_id, String patient_category, final int listPos) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("nurse_id", nurse_id);
        params.put("patient_id", patient_id);
        params.put("patient_category", patient_category);

        if (DATA.isFromAppointment) {
            params.put("care_type", "appointment");
        } else {
            params.put("care_type", "livecare");
        }

        String reqURL = DATA.baseUrl+"request_to_add_nurse";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in request_to_add_nurse "+content);
                    //{"success":1,"message":"Saved"}
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("success")){
                            nurseBeens.get(listPos).is_added = "1";
                            nurseAdapter.notifyDataSetChanged();
                        }else {
                            customToast.showToast("Something went wrong !",0,1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure request_to_add_nurse : "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }


    public void delete_care_management(String nurse_id, final int listPos,String patient_category) {

        DATA.showLoaderDefault(activity, "");

        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();
        params.put("nurse_id", nurse_id);
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("patient_category", patient_category);

        String reqURL = DATA.baseUrl+"delete_care_management";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in delete_care_management "+content);
                    //{"status":"success","msg":"deleted"}
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.getString("status").equals("success")){
                            nurseBeens.get(listPos).is_added = "0";
                            nurseAdapter.notifyDataSetChanged();
                        }else {
                            customToast.showToast("Something went wrong",0,1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
}
