package com.app.mdlive_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.DepressionAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.api.CustomSnakeBar;
import com.app.mdlive_cp.model.DepressionFieldBean;
import com.app.mdlive_cp.reliance.assessment.AssessSubmit;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.ExpandableHeightListView;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.HideShowKeypad;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityDepressionForm extends BaseActivity implements ApiCallBack, AssessSubmit {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ExpandableHeightListView lvDepression;
    TextView tvScore;

    public boolean isEdit = false, isReadOnly = false;

    String start_time, end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depression_form);

        isEdit = getIntent().getBooleanExtra("isEdit",false);

        isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

        start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        activity = ActivityDepressionForm.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvDepression = (ExpandableHeightListView) findViewById(R.id.lvDepression);
        tvScore = (TextView) findViewById(R.id.tvScore);


        lvDepression.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                depressionFieldBeen.get(position).isChecked = !depressionFieldBeen.get(position).isChecked;
                depressionAdapter.notifyDataSetChanged();

                setScore();
            }
        });

        Button btnSubmit = findViewById(R.id.btnSubmit);

        if(isReadOnly){
            btnSubmit.setText("Done");
        }
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
                    new GloabalMethods(activity).showConfSaveAssesDialog(ActivityDepressionForm.this, assesTittle);

                }
            }
        });

        ScrollView svDepForm = findViewById(R.id.svDepForm);
        PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svDepForm);


        loadForm();


        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesForm = activity;
    }

    public void loadForm(){
        String cachedForm = sharedPrefsHelper.get("depr_scale_form_json", "");
        if(! cachedForm.isEmpty()){
            ApiManager.shouldShowPD = false;
            parseForm(cachedForm);
        }
        ApiManager apiManager = new ApiManager(ApiManager.DEPRESSION_FIELDS,"post",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void submitForm(String is_lock){

        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("doctor_id",prefs.getString("id",""));
        params.put("score",score+"");


        end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        params.put("start_time", start_time);
        params.put("end_time", end_time);
        params.put("is_lock", is_lock);

        if (isEdit){
            params.put("id",ActivityAllDepression.selectedDepressionBean.id);  //this will go if edit existing
        }

        for (int i = 0; i < depressionFieldBeen.size(); i++) {
            String key = depressionFieldBeen.get(i).question;
            if(key.contains("rather than going out and doing new things")){
                key = key.replace(" rather than going out and doing new things","");
            }

            key = key.toLowerCase().replaceAll("\\s+","_").replace(",","");
            String paramsKey = "depression["+key+"]";
            if(depressionFieldBeen.get(i).isChecked){
                params.put(paramsKey,"Yes");
            }else{
                params.put(paramsKey,"No");
            }
        }

        ApiManager apiManager = new ApiManager(ApiManager.DEPRESSION_SCALE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    int score = 0;
    public void setScore(){
        score = 0;
        try {
            if(depressionFieldBeen!=null){
                for (int i = 0; i <depressionFieldBeen.size() ; i++) {
                    if(depressionFieldBeen.get(i).isChecked){
                        score = score + Integer.parseInt(depressionFieldBeen.get(i).Yes);
                    }else {
                        score = score + Integer.parseInt(depressionFieldBeen.get(i).No);
                    }
                }
            }
            tvScore.setText("Score: "+score);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    ArrayList<DepressionFieldBean> depressionFieldBeen;
    DepressionAdapter depressionAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.DEPRESSION_FIELDS)){
            parseForm(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.DEPRESSION_SCALE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast(jsonObject.getString("success"),0,1);
                    ActivityAllDepression.shoulRefresh = true;
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void parseForm(String content){

        try {
            JSONArray jsonArray = new JSONArray(content);
            depressionFieldBeen = new ArrayList<>();
            DepressionFieldBean depressionFieldBean;
            for (int i = 0; i < jsonArray.length(); i++) {
                String question = jsonArray.getJSONObject(i).getString("question");
                String Yes = jsonArray.getJSONObject(i).getString("Yes");
                String No = jsonArray.getJSONObject(i).getString("No");

                boolean isChecked = false;

                if(isEdit){
                    try {
                        String key = question;
                        if(key.contains("rather than going out and doing new things")){
                            key = key.replace(" rather than going out and doing new things","");
                        }
                        key = key.toLowerCase().replaceAll("\\s+","_").replace(",","");
                        JSONObject jsonObject = new JSONObject(ActivityAllDepression.selectedDepressionBean.depression_data);
                        if(jsonObject.getString(key).equalsIgnoreCase("Yes")){
                            isChecked = true;
                        }else{
                            isChecked = false;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                depressionFieldBean = new DepressionFieldBean(question,Yes,No,isChecked);
                depressionFieldBeen.add(depressionFieldBean);
                depressionFieldBean = null;
            }

            depressionAdapter = new DepressionAdapter(activity,depressionFieldBeen);
            lvDepression.setAdapter(depressionAdapter);
            lvDepression.setExpanded(true);

            setScore();

            sharedPrefsHelper.save("depr_scale_form_json", content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }

    }


    @Override
    public void submitAssessment(String is_lock_asses) {
        submitForm(is_lock_asses);
    }
}
