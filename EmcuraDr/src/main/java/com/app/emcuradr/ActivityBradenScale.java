package com.app.emcuradr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.CustomSnakeBar;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.HideShowKeypad;
import com.app.emcuradr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityBradenScale extends AppCompatActivity implements ApiCallBack {


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    Spinner spSensoryPerception,spMoisture,spActivity,spMobility,spNutrition,spFrictionAndShear;

    String [] arr1 = {"COMPLETELY LIMITED","VERY LIMITED","SLIGHTLY LIMITED","NO IMPAIRMENT"};
    String [] arr2 = {"CONSTANTLY MOIST","OFTEN MOIST","OCCASIONALLY MOIST","RERELY MOIST"};
    String [] arr3 = {"BEDFAST","CHAIRFAST","WALKS OCCASIONALY","WALKS FREQUENTLY"};
    String [] arr4 = {"COMPLETELY IMMOBILE","VERY LIMITED","SLIGHTLY LIMITED","NO LIMITATION"};
    String [] arr5 = {"VERY POOR","PROBABLY INADEQUATE","ADEQUATE","EXCELLENT"};
    String [] arr6 = {"PROBLEM","POTENTIAL PROBLEM","NO APPARENT PROBLEM"};

    String spSensoryPerceptionVal = "",spMoistureVal = "",spActivityVal = "",
            spMobilityVal = "",spNutritionVal = "",spFrictionAndShearVal = "";

    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braden_scale);

        isEdit = getIntent().getBooleanExtra("isEdit", false);

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

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestParams params = new RequestParams();
                params.put("patient_id",DATA.selectedUserCallId);
                params.put("doctor_id",prefs.getString("id",""));

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
        });
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        //{"success":"Form Submitted"}
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
