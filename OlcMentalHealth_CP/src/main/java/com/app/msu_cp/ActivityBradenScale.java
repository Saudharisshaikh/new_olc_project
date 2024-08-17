package com.app.msu_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.reliance.assessment.AssessSubmit;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityBradenScale extends AppCompatActivity implements ApiCallBack, AssessSubmit {


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    Spinner spSensoryPerception,spMoisture,spActivity,spMobility,spNutrition,spFrictionAndShear;

    Button btnSubmit;

    String [] arr1 = {"COMPLETELY LIMITED","VERY LIMITED","SLIGHTLY LIMITED","NO IMPAIRMENT"};
    String [] arr2 = {"CONSTANTLY MOIST","OFTEN MOIST","OCCASIONALLY MOIST","RERELY MOIST"};
    String [] arr3 = {"BEDFAST","CHAIRFAST","WALKS OCCASIONALY","WALKS FREQUENTLY"};
    String [] arr4 = {"COMPLETELY IMMOBILE","VERY LIMITED","SLIGHTLY LIMITED","NO LIMITATION"};
    String [] arr5 = {"VERY POOR","PROBABLY INADEQUATE","ADEQUATE","EXCELLENT"};
    String [] arr6 = {"PROBLEM","POTENTIAL PROBLEM","NO APPARENT PROBLEM"};

    String spSensoryPerceptionVal = "",spMoistureVal = "",spActivityVal = "",
            spMobilityVal = "",spNutritionVal = "",spFrictionAndShearVal = "";

    boolean isEdit = false,isReadOnly = false;

    String start_time, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braden_scale);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        isReadOnly = getIntent().getBooleanExtra("isReadOnly", false);

        start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        activity = ActivityBradenScale.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        spSensoryPerception = (Spinner) findViewById(R.id.spSensoryPerception);
        spMoisture = (Spinner) findViewById(R.id.spMoisture);
        spActivity = (Spinner) findViewById(R.id.spActivity);
        spMobility = (Spinner) findViewById(R.id.spMobility);
        spNutrition = (Spinner) findViewById(R.id.spNutrition);
        spFrictionAndShear = (Spinner) findViewById(R.id.spFrictionAndShear);

        btnSubmit = findViewById(R.id.btnSubmit);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.spSensoryPerception:
                        spSensoryPerceptionVal = (position+1)+"";
                        break;
                    case R.id.spMoisture:
                        spMoistureVal = (position+1)+"";
                        break;
                    case R.id.spActivity:
                        spActivityVal = (position+1)+"";
                        break;
                    case R.id.spMobility:
                        spMobilityVal = (position+1)+"";
                        break;
                    case R.id.spNutrition:
                        spNutritionVal = (position+1)+"";
                        break;
                    case R.id.spFrictionAndShear:
                        spFrictionAndShearVal = (position+1)+"";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spSensoryPerception.setOnItemSelectedListener(onItemSelectedListener);
        spMoisture.setOnItemSelectedListener(onItemSelectedListener);
        spActivity.setOnItemSelectedListener(onItemSelectedListener);
        spMobility.setOnItemSelectedListener(onItemSelectedListener);
        spNutrition.setOnItemSelectedListener(onItemSelectedListener);
        spFrictionAndShear.setOnItemSelectedListener(onItemSelectedListener);

        ArrayAdapter<String> spAdapter1 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr1);
        spAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSensoryPerception.setAdapter(spAdapter1);

        ArrayAdapter<String> spAdapter2 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr2);
        spAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoisture.setAdapter(spAdapter2);

        ArrayAdapter<String> spAdapter3 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr3);
        spAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivity.setAdapter(spAdapter3);

        ArrayAdapter<String> spAdapter4 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr4);
        spAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMobility.setAdapter(spAdapter4);

        ArrayAdapter<String> spAdapter5 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr5);
        spAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNutrition.setAdapter(spAdapter5);

        ArrayAdapter<String> spAdapter6 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arr6);
        spAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrictionAndShear.setAdapter(spAdapter6);


        if(isEdit){
            try {
                spSensoryPerception.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.sensory_perception) - 1));
                spMoisture.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.moisture) - 1));
                spActivity.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.activity) - 1));
                spMobility.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.mobility) - 1));
                spNutrition.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.nutrition) - 1));
                spFrictionAndShear.setSelection((Integer.parseInt(ActivityAllBradenScale.selectedBradenScaleBean.friction_shear) - 1));

            }catch (Exception e){
                e.printStackTrace();
            }
        }


        if(isReadOnly){

            spSensoryPerception.setEnabled(false);
            spMoisture.setEnabled(false);
            spActivity.setEnabled(false);
            spMobility.setEnabled(false);
            spNutrition.setEnabled(false);
            spFrictionAndShear.setEnabled(false);

            /*spSensoryPerception.setClickable(false);
            spMoisture.setClickable(false);
            spActivity.setClickable(false);
            spMobility.setClickable(false);
            spNutrition.setClickable(false);
            spFrictionAndShear.setClickable(false);*/

            btnSubmit.setText("Done");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadOnly){
                    finish();
                }else {
                    String assesTittle = getResources().getString(R.string.app_name);
                    if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
                        assesTittle = getSupportActionBar().getTitle().toString();
                    }
                    new GloabalMethods(activity).showConfSaveAssesDialog(ActivityBradenScale.this, assesTittle);
                }
            }
        });


        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesForm = activity;
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        //{"success":"Form Submitted"}
        try {
            JSONObject jsonObject = new JSONObject(content);
            if(jsonObject.has("success")){
                customToast.showToast(jsonObject.getString("success"),0,1);
                ActivityAllBradenScale.shoulRefresh = true;
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }


    public void submitForm(String is_lock){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("doctor_id",prefs.getString("id",""));


        end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        params.put("start_time", start_time);
        params.put("end_time", end_time);
        params.put("is_lock", is_lock);

        if(isEdit){
            params.put("id",ActivityAllBradenScale.selectedBradenScaleBean.id);//for update only
        }

        params.put("sensory_perception",spSensoryPerceptionVal);
        params.put("moisture",spMoistureVal);
        params.put("activity",spActivityVal);
        params.put("mobility",spMobilityVal);
        params.put("nutrition",spNutritionVal);
        params.put("friction_shear",spFrictionAndShearVal);

        ApiManager apiManager = new ApiManager(ApiManager.BRADEN_SCALE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void submitAssessment(String is_lock_asses) {
        submitForm(is_lock_asses);
    }
}
