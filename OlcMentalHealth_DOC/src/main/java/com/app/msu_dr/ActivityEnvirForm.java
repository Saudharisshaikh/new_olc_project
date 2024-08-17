package com.app.msu_dr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.msu_dr.R;
import com.app.msu_dr.adapter.EnvirFieldAdapter;
import com.app.msu_dr.api.ApiCallBack;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.api.CustomSnakeBar;
import com.app.msu_dr.model.EnvirFieldBean;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.ExpandableHeightListView;
import com.app.msu_dr.util.HideShowKeypad;
import com.app.msu_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityEnvirForm extends AppCompatActivity implements ApiCallBack {


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ExpandableHeightListView lvEnvLivingArrangement,lvEnvHomeSafety,lvEnvOutsideEnvironment,lvEnvHomeEnvironment;
    EditText etEnvWhere,etEnvPersonchoosestolive,etEnvEvacuationPlan,etEnvHomeSafetyNotes;
    Button btnEnvSub;

    boolean isEdit = false;
    JSONObject envEditJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envir_form);

        isEdit = getIntent().getBooleanExtra("isEdit",false);

        activity = ActivityEnvirForm.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvEnvLivingArrangement = findViewById(R.id.lvEnvLivingArrangement);
        lvEnvHomeSafety = findViewById(R.id.lvEnvHomeSafety);
        lvEnvOutsideEnvironment = findViewById(R.id.lvEnvOutsideEnvironment);
        lvEnvHomeEnvironment = findViewById(R.id.lvEnvHomeEnvironment);

        etEnvWhere = (EditText) findViewById(R.id.etEnvWhere);
        etEnvPersonchoosestolive = (EditText) findViewById(R.id.etEnvPersonchoosestolive);
        etEnvEvacuationPlan = (EditText) findViewById(R.id.etEnvEvacuationPlan);
        etEnvHomeSafetyNotes = (EditText) findViewById(R.id.etEnvHomeSafetyNotes);


        if(isEdit){
            try {
                envEditJSON = new JSONObject(ActivityAllEnvir.selectedEnvirBean.data);

                etEnvWhere.setText(envEditJSON.getString("if_yes_where"));
                etEnvPersonchoosestolive.setText(envEditJSON.getString("person_chooses_to_live"));
                etEnvEvacuationPlan.setText(envEditJSON.getString("evacuation_plan"));
                etEnvHomeSafetyNotes.setText(envEditJSON.getString("home_safety_notes"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnEnvSub = (Button) findViewById(R.id.btnEnvSub);
        btnEnvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String if_yes_where = etEnvWhere.getText().toString();
                if (if_yes_where.isEmpty()){
                    if_yes_where = "None";
                }
                String person_chooses_to_live = etEnvPersonchoosestolive.getText().toString();
                if (person_chooses_to_live.isEmpty()){
                    person_chooses_to_live = "None";
                }
                String evacuation_plan = etEnvEvacuationPlan.getText().toString();
                if (evacuation_plan.isEmpty()){
                    evacuation_plan = "None";
                }
                String home_safety_notes = etEnvHomeSafetyNotes.getText().toString();
                if (home_safety_notes.isEmpty()){
                    home_safety_notes = "None";
                }

                RequestParams params  = new RequestParams();

                if(isEdit){
                    params.put("id", ActivityAllEnvir.selectedEnvirBean.id);
                }
                params.put("patient_id",DATA.selectedUserCallId);
                params.put("doctor_id",prefs.getString("id",""));
                params.put("if_yes_where",if_yes_where);
                params.put("person_chooses_to_live",person_chooses_to_live);
                params.put("evacuation_plan",evacuation_plan);
                params.put("home_safety_notes",home_safety_notes);

                for (int i = 0; i < arrLivingArrangement.size(); i++) {
                    params.put(arrLivingArrangement.get(i).key,arrLivingArrangement.get(i).radioValue);
                }
                for (int i = 0; i < arrHomeSafety.size(); i++) {
                    params.put(arrHomeSafety.get(i).key,arrHomeSafety.get(i).radioValue);
                }
                for (int i = 0; i < arrOutsideEnvironment.size(); i++) {
                    params.put(arrOutsideEnvironment.get(i).key,arrOutsideEnvironment.get(i).radioValue);
                }
                for (int i = 0; i < arrHomeEnvironment.size(); i++) {
                    params.put(arrHomeEnvironment.get(i).key,arrHomeEnvironment.get(i).radioValue);
                }

                ApiManager apiManager = new ApiManager(ApiManager.ENVIR_ASSESMENT,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        ApiManager apiManager = new ApiManager(ApiManager.ENVIR_FORM_FIELDS,"post",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<EnvirFieldBean> envirFieldBeens,arrLivingArrangement,arrHomeSafety, arrOutsideEnvironment,arrHomeEnvironment;
    EnvirFieldAdapter envirFieldAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {

        if(apiName.equals(ApiManager.ENVIR_FORM_FIELDS)){
            try {
                JSONArray jsonArray = new JSONArray(content);
                envirFieldBeens = new ArrayList<>();
                EnvirFieldBean bean;
                for (int i = 0; i < jsonArray.length(); i++) {
                    String key = jsonArray.getJSONObject(i).getString("key");
                    String value = jsonArray.getJSONObject(i).getString("value");
                    String radioValue = "0";

                    if(isEdit){
                        if(envEditJSON.has(key)){
                           radioValue = envEditJSON.getString(key);
                        }
                    }

                    bean = new EnvirFieldBean(key,value,radioValue);
                    envirFieldBeens.add(bean);
                    bean = null;
                }

                arrLivingArrangement = new ArrayList<>();
                arrHomeSafety = new ArrayList<>();
                arrOutsideEnvironment = new ArrayList<>();
                arrHomeEnvironment = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    arrLivingArrangement.add(envirFieldBeens.get(i));
                }

                for (int i = 3; i < 11; i++) {
                    arrHomeSafety.add(envirFieldBeens.get(i));
                }

                for (int i = 11; i < 14; i++) {
                    arrOutsideEnvironment.add(envirFieldBeens.get(i));
                }

                for (int i = 14; i < envirFieldBeens.size(); i++) {
                    arrHomeEnvironment.add(envirFieldBeens.get(i));
                }

                envirFieldAdapter = new EnvirFieldAdapter(activity,arrLivingArrangement);
                lvEnvLivingArrangement.setAdapter(envirFieldAdapter);
                lvEnvLivingArrangement.setExpanded(true);

                envirFieldAdapter = new EnvirFieldAdapter(activity,arrHomeSafety);
                lvEnvHomeSafety.setAdapter(envirFieldAdapter);
                lvEnvHomeSafety.setExpanded(true);

                envirFieldAdapter = new EnvirFieldAdapter(activity,arrOutsideEnvironment);
                lvEnvOutsideEnvironment.setAdapter(envirFieldAdapter);
                lvEnvOutsideEnvironment.setExpanded(true);

                envirFieldAdapter = new EnvirFieldAdapter(activity,arrHomeEnvironment);
                lvEnvHomeEnvironment.setAdapter(envirFieldAdapter);
                lvEnvHomeEnvironment.setExpanded(true);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ENVIR_ASSESMENT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast(jsonObject.getString("success"),0,1);

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
