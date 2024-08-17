package com.app.amnm_uc.b_health2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.app.amnm_uc.BaseActivity;
import com.app.amnm_uc.R;
import com.app.amnm_uc.api.ApiManager;
import com.app.amnm_uc.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityApptmntProviders extends BaseActivity {



    ListView lvMyProviders;
    EditText etSearchDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apptmnt_providers);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Select a Therapist");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvMyProviders = (ListView) findViewById(R.id.lvMyProviders);
        etSearchDoc = findViewById(R.id.etSearchDoc);

        etSearchDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(lvAptProvidersAdapter != null){
                    lvAptProvidersAdapter.filter(s.toString());
                }
            }
        });


        RequestParams params = new RequestParams();
        //params.put("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_THERAPIST_DOC,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<DoctorBean> doctorBeans;
    LvAptProvidersAdapter lvAptProvidersAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPIST_DOC)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray doctors = jsonObject.getJSONArray("doctors");
                if(doctors.length() == 0){
                    findViewById(R.id.tvNoProvider).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.tvNoProvider).setVisibility(View.GONE);
                }

                doctorBeans = gson.fromJson(doctors.toString(), new TypeToken<ArrayList<DoctorBean>>() {}.getType());
                if(doctorBeans != null){
                    lvAptProvidersAdapter = new LvAptProvidersAdapter(activity,doctorBeans);
                    lvMyProviders.setAdapter(lvAptProvidersAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
