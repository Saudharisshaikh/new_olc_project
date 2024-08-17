package com.app.mhcsn_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.adapters.PatientInviteAdapter;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.PatientInviteBean;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.app.mhcsn_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatientInviteActivity extends AppCompatActivity implements ApiCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvPatient;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_invite);

        activity = PatientInviteActivity.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvPatient = (ListView) findViewById(R.id.lvPatient);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        lvPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("user_id", patientInviteBeens.get(position).patient_id);
                params.put("user_type", "patient");
                params.put("group_id",ActivityGroupChat.selectedGroupBean.id);

                ApiManager apiManager = new ApiManager(ApiManager.ADD_TO_GROUP,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.NURSE_PATIENTS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<PatientInviteBean> patientInviteBeens;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.NURSE_PATIENTS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                patientInviteBeens = new ArrayList<>();
                PatientInviteBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String first_name = data.getJSONObject(i).getString("first_name");
                    String last_name = data.getJSONObject(i).getString("last_name");
                    String image = data.getJSONObject(i).getString("image");
                    String patient_category = data.getJSONObject(i).getString("patient_category");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String doctor_nurses_id = data.getJSONObject(i).getString("doctor_nurses_id");
                    String is_online = data.getJSONObject(i).getString("is_online");

                    bean = new PatientInviteBean(first_name,last_name,image,patient_category,patient_id,doctor_nurses_id,is_online);
                    patientInviteBeens.add(bean);
                    bean = null;
                }

                PatientInviteAdapter patientInviteAdapter = new PatientInviteAdapter(activity,patientInviteBeens);
                lvPatient.setAdapter(patientInviteAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equals(ApiManager.ADD_TO_GROUP)){
            //result: {"status":"success","message":"User has been added."}
            try {
                JSONObject jsonObject = new JSONObject(content);

                customToast.showToast(jsonObject.getString("message"),0,0);
                String s = jsonObject.getString("status");
                if (s.equalsIgnoreCase("success")) {
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
