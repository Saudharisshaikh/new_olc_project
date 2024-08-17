package com.app.omrandr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.omrandr.adapter.PatientRefillAdapter;
import com.app.omrandr.api.ApiCallBack;
import com.app.omrandr.api.ApiManager;
import com.app.omrandr.model.PatientRefillBean;
import com.app.omrandr.util.CheckInternetConnection;
import com.app.omrandr.util.CustomToast;
import com.app.omrandr.util.DATA;
import com.app.omrandr.util.Database;
import com.app.omrandr.util.HideShowKeypad;
import com.app.omrandr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityPatientRefill extends BaseActivity implements ApiCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ListView lvPatientRefill;
    TextView tvNoRefills;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_refill);

        activity = ActivityPatientRefill.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_REFILL);

        lvPatientRefill = (ListView) findViewById(R.id.lvPatientRefill);
        tvNoRefills = (TextView) findViewById(R.id.tvNoRefills);

        if (checkInternetConnection.isConnectedToInternet()) {
            getPatientsRefillRequests();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
//String response_type,String prescription_detail_id,String id,String patient_id
        lvPatientRefill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (checkInternetConnection.isConnectedToInternet()) {
                    new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm")
                            .setMessage("Are you sure? Would you like to send refill request to pharmacy for this prescription.")
                            .setPositiveButton("OK, Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    patientsRefillRequestResponse("approved",patientRefillBeens.get(position).prescription_detail_id,
                                            patientRefillBeens.get(position).id,patientRefillBeens.get(position).patient_id);
                                }
                            }).setNegativeButton("Cancel Request", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            patientsRefillRequestResponse("cancelled",patientRefillBeens.get(position).prescription_detail_id,
                                    patientRefillBeens.get(position).id,patientRefillBeens.get(position).patient_id);
                        }
                    }).setNeutralButton("Not, Now",null).show();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
            }
        });
    }

    ArrayList<PatientRefillBean> patientRefillBeens;
    public void getPatientsRefillRequests() {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_REFILL_REQUESTS,"post",params,this, activity);
        apiManager.loadURL();

    }

    public void patientsRefillRequestResponse(String response_type,String prescription_detail_id,String id,String patient_id) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("response_type", response_type);
        params.put("prescription_detail_id", prescription_detail_id);
        params.put("id", id);
        params.put("patient_id", patient_id);

        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_REFILL_REQUEST_RESPONSE,"post",params,this, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENT_REFILL_REQUESTS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() == 0) {
                    tvNoRefills.setVisibility(View.VISIBLE);
                }else {
                    tvNoRefills.setVisibility(View.GONE);
                }
                patientRefillBeens = new ArrayList<PatientRefillBean>();
                PatientRefillBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String prescription_detail_id = data.getJSONObject(i).getString("prescription_detail_id");
                    String first_name = data.getJSONObject(i).getString("first_name");
                    String last_name = data.getJSONObject(i).getString("last_name");
                    String start_date = data.getJSONObject(i).getString("start_date");
                    String end_date = data.getJSONObject(i).getString("end_date");
                    String drug_name = data.getJSONObject(i).getString("drug_name");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String StoreName = data.getJSONObject(i).getString("StoreName");

                    bean = new PatientRefillBean(id,prescription_detail_id,first_name,last_name,start_date,end_date,drug_name,patient_id,StoreName);
                    patientRefillBeens.add(bean);
                }
                lvPatientRefill.setAdapter(new PatientRefillAdapter(activity,patientRefillBeens));
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.PATIENT_REFILL_REQUEST_RESPONSE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                customToast.showToast(jsonObject.getString("message"),0,1);
                getPatientsRefillRequests();

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }
        }
    }
}
