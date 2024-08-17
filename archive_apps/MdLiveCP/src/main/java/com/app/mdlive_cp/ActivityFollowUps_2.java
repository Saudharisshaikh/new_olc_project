package com.app.mdlive_cp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.FollowupAdapter2;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.FollowupBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.HideShowKeypad;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityFollowUps_2 extends AppCompatActivity implements View.OnClickListener{

    //note: this activity is to select patients for soap notes after doctodoc call
    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvFollowUps;
    TextView tvNoData;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome;

    //public static String followUpDateTime = "";
    //public static FollowupBean selectedFollowupBean;

    boolean isForProgrssNotes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_ups);

        isForProgrssNotes = getIntent().getBooleanExtra("isForProgrssNotes",false);

        activity = ActivityFollowUps_2.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        openActivity = new OpenActivity(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        lvFollowUps = (ListView) findViewById(R.id.lvFollowUps);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);

        if (checkInternetConnection.isConnectedToInternet()) {
            followup_patients();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }

        lvFollowUps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DATA.selectedUserCallId = followupBeens.get(position).patient_id;

                if(isForProgrssNotes){
                    openActivity.open(ActivityProgressNotes.class,true);
                }else{
                    new GloabalMethods(activity).showAddSOAPDialog();
                    //finish();  activity will finish in showAddSOAPDialog method
                }
            }
        });
    }


    ArrayList<FollowupBean> followupBeens,followupBeensOrig;
    FollowupAdapter2 followupAdapter;
    public void followup_patients() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));

        client.post(DATA.baseUrl+"followup_patients", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in followup_patients: "+content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray data = jsonObject.getJSONArray("data");

                        followupBeensOrig = new ArrayList<FollowupBean>();
                        FollowupBean bean;
                        for (int i = 0; i < data.length(); i++) {
                            String first_name = data.getJSONObject(i).getString("first_name");
                            String last_name = data.getJSONObject(i).getString("last_name");
                            String image = data.getJSONObject(i).getString("image");
                            String patient_category = data.getJSONObject(i).getString("patient_category");
                            String patient_id = data.getJSONObject(i).getString("patient_id");
                            String doctor_nurses_id = data.getJSONObject(i).getString("doctor_nurses_id");
                            String followup_id = data.getJSONObject(i).getString("followup_id");
                            String dateof = data.getJSONObject(i).getString("dateof");
                            String is_online = data.getJSONObject(i).getString("is_online");

                            bean = new FollowupBean(first_name,last_name,image,patient_category,patient_id,doctor_nurses_id,followup_id,dateof,is_online);
                            followupBeensOrig.add(bean);
                        }

                        followupBeens = new ArrayList<FollowupBean>();
                        for (FollowupBean temp : followupBeensOrig) {
                            if(temp.patient_category.equals("tcm")){
                                followupBeens.add(temp);
                            }
                        }
                        followupAdapter = new FollowupAdapter2(activity,followupBeens);
                        lvFollowUps.setAdapter(followupAdapter);

                        if (followupBeens.isEmpty()) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            tvNoData.setVisibility(View.GONE);
                        }

                        tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: followup_patients, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail followup_patients: " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("tcm")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter2(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("complex_care")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter2(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("home_health")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter2(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("nursing_home")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter2(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
        }
    }

}
