package com.app.msu_cp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.adapters.FollowupAdapter3;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.FollowupBean;
import com.app.msu_cp.model.MyAppointmentsModel;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityFollowUps_3 extends BaseActivity implements View.OnClickListener,ApiCallBack{

    //this activity is to show followups created by cp
    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvFollowUps;
    TextView tvNoData;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome;
    EditText etFilterDate;

    public static String followUpDateTime = "";
    public static FollowupBean selectedFollowupBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_ups);

        activity = ActivityFollowUps_3.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, MODE_PRIVATE);

        lvFollowUps = (ListView) findViewById(R.id.lvFollowUps);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        etFilterDate = findViewById(R.id.etFilterDate);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);

        etFilterDate.setVisibility(View.VISIBLE);

        etFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etFilterDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        if (checkInternetConnection.isConnectedToInternet()) {
            careprovider_schedules("");
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }

        lvFollowUps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DATA.selectedUserQbid = "";
                DATA.selectedUserCallId = followupBeens.get(position).patient_id;
                DATA.selectedUserCallName = followupBeens.get(position).first_name+" "+followupBeens.get(position).last_name;
                DATA.selectedUserCallSympTom = "";
                DATA.selectedUserCallCondition = "";
                DATA.selectedUserCallDescription = "";
                DATA.selectedUserAppntID = "";

                DATA.selectedUserLatitude =  0;
                DATA.selectedUserLongitude =  0;

                DATA.selectedUserCallImage = followupBeens.get(position).image;

                //filterReports(DATA.selectedUserCallId);
                DATA.allReportsFiltered = new ArrayList<>();

                DATA.isFromDocToDoc = false;

                DATA.selectedRefferedLiveCare = new MyAppointmentsModel();
                DATA.selectedRefferedLiveCare.id = followupBeens.get(position).patient_id;
                DATA.selectedRefferedLiveCare.is_online = followupBeens.get(position).is_online;
                DATA.selectedRefferedLiveCare.first_name = followupBeens.get(position).first_name;
                DATA.selectedRefferedLiveCare.last_name = followupBeens.get(position).last_name;
                DATA.selectedRefferedLiveCare.patient_phone = "-";//followupBeens.get(position).phone;

                Intent i = new Intent(activity, ActivityTcmDetails.class);
                startActivity(i);
                //activity.finish();
            }
        });
    }


    ArrayList<FollowupBean> followupBeens,followupBeensOrig;
    FollowupAdapter3 followupAdapter;
    public void careprovider_schedules(String filterDate) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        if(!TextUtils.isEmpty(filterDate)){
            params.put("date", filterDate);
        }

        ApiManager apiManager = new ApiManager(ApiManager.CAREPROVIDER_SCHEDULES,"post",params,this, activity);
        apiManager.loadURL();
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
                followupAdapter = new FollowupAdapter3(activity,followupBeens);
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
                followupAdapter = new FollowupAdapter3(activity,followupBeens);
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
                followupAdapter = new FollowupAdapter3(activity,followupBeens);
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
                followupAdapter = new FollowupAdapter3(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void setUpFollwUpDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Confirm");
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkInternetConnection.isConnectedToInternet()) {
                    createfollowup();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                }
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();
    }

    public void createfollowup() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("patient_id", selectedFollowupBean.patient_id);
        params.put("dateof",followUpDateTime);

        if(!selectedFollowupBean.followup_id.isEmpty()){
            params.put("followup_id",selectedFollowupBean.followup_id);
        }

        client.post(DATA.baseUrl+"createfollowup", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    DATA.print("--reaponce in createfollowup: "+content);
                    //{"status":"error","message":"Already Assigned Followup on given time."}
                    // {"status":"success","message":"Saved","followup_id":"5"}
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("success")) {
                            selectedFollowupBean.followup_id = jsonObject.getString("followup_id");
                            followupAdapter.notifyDataSetChanged();
                            new AlertDialog.Builder(activity).setTitle("Info").
                                    setMessage("You scheduled the follow up successfully").
                                    setPositiveButton("Done",null).show();
                        }else {
                            customToast.showToast(message,0,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: createfollowup, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail createfollowup: " +content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.CAREPROVIDER_SCHEDULES)){
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
                followupAdapter = new FollowupAdapter3(activity,followupBeens);
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
        }
    }
}
