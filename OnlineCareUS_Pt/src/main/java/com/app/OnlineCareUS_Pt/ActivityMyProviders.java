package com.app.OnlineCareUS_Pt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.app.OnlineCareUS_Pt.adapter.ProvidersAdapter;
import com.app.OnlineCareUS_Pt.api.ApiCallBack;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.api.CustomSnakeBar;
import com.app.OnlineCareUS_Pt.model.PatientProviderBean;
import com.app.OnlineCareUS_Pt.util.CheckInternetConnection;
import com.app.OnlineCareUS_Pt.util.CustomToast;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.HideShowKeypad;
import com.app.OnlineCareUS_Pt.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityMyProviders extends AppCompatActivity implements ApiCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvMyProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_providers);

        activity = ActivityMyProviders.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        lvMyProviders = (ListView) findViewById(R.id.lvMyProviders);


        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));

        ApiManager apiManager = new ApiManager(ApiManager.LIST_PROVIDERS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.LIST_PROVIDERS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray doctors = jsonObject.getJSONArray("doctors");
                if(doctors.length() == 0){
                    findViewById(R.id.tvNoProvider).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.tvNoProvider).setVisibility(View.GONE);
                }
                ArrayList<PatientProviderBean> patientProviderBeens = new ArrayList<>();
                PatientProviderBean bean;
                for (int i = 0; i < doctors.length(); i++) {
                    String is_online = doctors.getJSONObject(i).getString("is_online");
                    String first_name = doctors.getJSONObject(i).getString("first_name");
                    String last_name = doctors.getJSONObject(i).getString("last_name");
                    String doctor_category = doctors.getJSONObject(i).getString("doctor_category");
                    String image = doctors.getJSONObject(i).getString("image");
                    String current_app = doctors.getJSONObject(i).getString("current_app");
                    String doctor_id = doctors.getJSONObject(i).getString("doctor_id");

                    bean = new PatientProviderBean(is_online,first_name,last_name,doctor_category,image,current_app,doctor_id);
                    patientProviderBeens.add(bean);
                    bean = null;
                }

                lvMyProviders.setAdapter(new ProvidersAdapter(activity,patientProviderBeens));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
