package com.app.msu_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.app.msu_cp.adapters.FallRiskFieldAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.model.FallRiskFieldBean;
import com.app.msu_cp.reliance.assessment.AssessSubmit;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.ExpandableHeightGridView;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FallRiskForm extends BaseActivity implements ApiCallBack, AssessSubmit {


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    public boolean isEdit = false, isReadOnly = false;

    EditText etAge65,etTotal;//etDiagnosis,etPriorHistoryoffalls,etIncontinence,etVisualImpairment,etImpairedFunctionalMobility,etEnvironmentalhazards,etPolyPharmacy,etPainAffectingLevel,etCognitiveImpairment,

    ExpandableHeightGridView lvFallRiskForm;
    Button btnSubmit;
    ScrollView svForm;
    ArrayList<FallRiskFieldBean> fallRiskFieldBeen;
    FallRiskFieldAdapter fallRiskFieldAdapter;

    String start_time, end_time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_risk_form);

        isEdit = getIntent().getBooleanExtra("isEdit",false);

        isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

        start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());


        activity = FallRiskForm.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        etAge65 = (EditText) findViewById(R.id.etAge65);
        /*etDiagnosis = (EditText) findViewById(etDiagnosis);
        etPriorHistoryoffalls = (EditText) findViewById(etPriorHistoryoffalls);
        etIncontinence = (EditText) findViewById(etIncontinence);
        etVisualImpairment = (EditText) findViewById(etVisualImpairment);
        etImpairedFunctionalMobility = (EditText) findViewById(etImpairedFunctionalMobility);
        etEnvironmentalhazards = (EditText) findViewById(etEnvironmentalhazards);
        etPolyPharmacy = (EditText) findViewById(etPolyPharmacy);
        etPainAffectingLevel = (EditText) findViewById(etPainAffectingLevel);
        etCognitiveImpairment = (EditText) findViewById(etCognitiveImpairment);*/
        etTotal = (EditText) findViewById(R.id.etTotal);

        lvFallRiskForm = (ExpandableHeightGridView) findViewById(R.id.lvFallRiskForm);
        btnSubmit = findViewById(R.id.btnSubmit);
        svForm = findViewById(R.id.svForm);

        fallRiskFieldBeen = getFallRiskFields();
        fallRiskFieldAdapter = new FallRiskFieldAdapter(activity,fallRiskFieldBeen);
        lvFallRiskForm.setAdapter(fallRiskFieldAdapter);
        lvFallRiskForm.setExpanded(true);



        if(isEdit){
            etAge65.setText(ActivityAllFallRisk.selectedFallRiskBean.age_65);
            /*etDiagnosis.setText(ActivityAllFallRisk.selectedFallRiskBean.diagnosis);
            etPriorHistoryoffalls.setText(ActivityAllFallRisk.selectedFallRiskBean.prior_history);
            etIncontinence.setText(ActivityAllFallRisk.selectedFallRiskBean.incontinence);
            etVisualImpairment.setText(ActivityAllFallRisk.selectedFallRiskBean.visual_impairment);
            etImpairedFunctionalMobility.setText(ActivityAllFallRisk.selectedFallRiskBean.impaired_functional_mobility);
            etEnvironmentalhazards.setText(ActivityAllFallRisk.selectedFallRiskBean.environment_hazards);
            etPolyPharmacy.setText(ActivityAllFallRisk.selectedFallRiskBean.poly_pharmacy);
            etPainAffectingLevel.setText(ActivityAllFallRisk.selectedFallRiskBean.pain_affection);
            etCognitiveImpairment.setText(ActivityAllFallRisk.selectedFallRiskBean.cognitive_impairment);*/
            //etTotal.setText(ActivityAllFallRisk.selectedFallRiskBean.score);

            for (int i = 0; i < fallRiskFieldBeen.size(); i++) {
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("diagnosis")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.diagnosis;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("prior_history")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.prior_history;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("incontinence")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.incontinence;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("visual_impairment")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.visual_impairment;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("impaired_functional_mobility")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.impaired_functional_mobility;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("environment_hazards")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.environment_hazards;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("poly_pharmacy")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.poly_pharmacy;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("pain_affection")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.pain_affection;
                }
                if(fallRiskFieldBeen.get(i).key.equalsIgnoreCase("cognitive_impairment")){
                    fallRiskFieldBeen.get(i).radioValue = ActivityAllFallRisk.selectedFallRiskBean.cognitive_impairment;
                }


                fallRiskFieldAdapter.notifyDataSetChanged();
            }

        }else {
            etAge65.setText(DATA.selectedUserCallAge);
        }

        if(isReadOnly){
            btnSubmit.setText("Done");
        }


        setScore();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isReadOnly){
                    finish();
                }else {

                    /*if(! validateDASTForm()){ // make function if needed
                        if(dastFormAdapter != null){
                            DastFormAdapter.validateFlag = true;
                            dastFormAdapter.notifyDataSetChanged();
                        }
                        customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

                        return;
                    }*/

                    String assesTittle = getResources().getString(R.string.app_name);
                    if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
                        assesTittle = getSupportActionBar().getTitle().toString();
                    }
                    new GloabalMethods(activity).showConfSaveAssesDialog(FallRiskForm.this, assesTittle);

                }
            }
        });




        PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesForm = activity;

    }


    public ArrayList<FallRiskFieldBean> getFallRiskFields(){
        ArrayList<FallRiskFieldBean> fallRiskFieldBeen = new ArrayList<>();
        fallRiskFieldBeen.add(new FallRiskFieldBean("diagnosis","Diagnosis (3 or more co-existing)","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("prior_history","Prior History of falls","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("incontinence","Incontinence","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("visual_impairment","Visual Impairment","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("impaired_functional_mobility","Impaired Functional Mobility","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("environment_hazards","Environmental hazards","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("poly_pharmacy","Poly Pharmacy","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("pain_affection","Pain Affecting Level","0"));
        fallRiskFieldBeen.add(new FallRiskFieldBean("cognitive_impairment","Cognitive Impairment","0"));
        return fallRiskFieldBeen;
    }

    int score = 0;
    public void setScore(){
        score = 0;
        for (int i = 0; i < fallRiskFieldBeen.size(); i++) {
            if(fallRiskFieldBeen.get(i).radioValue.equalsIgnoreCase("1")){
                score = score+1;
            }
        }
        etTotal.setText(score+"");
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.FALL_RISK_ASSESMENT)){
            //result: {"success":"Form Submitted"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){

                    customToast.showToast(jsonObject.getString("success"),0,1);

                    ActivityAllFallRisk.shoulRefresh = true;

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void submitForm(String is_lock){

        if(etAge65.getText().toString().isEmpty()){
            etAge65.setError("Please enter the age");
            return;
        }
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("doctor_id",prefs.getString("id",""));

        end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        params.put("start_time", start_time);
        params.put("end_time", end_time);
        params.put("score",  score+"");
        params.put("is_lock", is_lock);

        if(isEdit){
            params.put("id",ActivityAllFallRisk.selectedFallRiskBean.id);//for update only
        }

        params.put("age_65",etAge65.getText().toString());
                /*params.put("diagnosis",etDiagnosis.getText().toString());
                params.put("prior_history",etPriorHistoryoffalls.getText().toString());
                params.put("incontinence",etIncontinence.getText().toString());
                params.put("visual_impairment",etVisualImpairment.getText().toString());
                params.put("impaired_functional_mobility",etImpairedFunctionalMobility.getText().toString());
                params.put("environment_hazards",etEnvironmentalhazards.getText().toString());
                params.put("poly_pharmacy",etPolyPharmacy.getText().toString());
                params.put("pain_affection",etPainAffectingLevel.getText().toString());
                params.put("cognitive_impairment",etCognitiveImpairment.getText().toString());*/

        for (int i = 0; i < fallRiskFieldBeen.size(); i++) {
            params.put(fallRiskFieldBeen.get(i).key,fallRiskFieldBeen.get(i).radioValue);
        }

        ApiManager apiManager = new ApiManager(ApiManager.FALL_RISK_ASSESMENT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void submitAssessment(String is_lock_asses) {
        submitForm(is_lock_asses);
    }
}
