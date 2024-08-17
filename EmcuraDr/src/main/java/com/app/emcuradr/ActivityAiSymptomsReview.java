package com.app.emcuradr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActivityAiSymptomsReview extends AppCompatActivity {


    TextView txtptName, txtptSymp;
    EditText etAiAnswerDignosis, etAiAnswerCarePlan, etAiAnswerPresc;
    Button btnGenAiReview, btnGenAiCarePlan, btnGenAiPresc, btnGoback;

    Activity activity;

    Dialog_CustomProgress customProgressDialog;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    ScrollView mScrlMain;


    SharedPrefsHelper sharedPrefsHelper;
    ArrayList<String> gptQueryList;

    String disclaimer = "";

    String queryForDiagonosis = "",queryForCarePlans= "",queryForPrescriptions="";

    LinearLayout layoutDiagnosis, layoutPrescription;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_symptoms_review);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("AI Suggested Diagnosis");
        }

        activity = ActivityAiSymptomsReview.this;
        customProgressDialog = new Dialog_CustomProgress(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        gptQueryList = sharedPrefsHelper.getGptQuerylist();
        if(!gptQueryList.isEmpty()){
            queryForDiagonosis = gptQueryList.get(1);
            queryForDiagonosis =  queryForDiagonosis .replace("[patientCondition]", DATA.selectedUserCallCondition)
                    .replace("[patientDesc]", DATA.selectedUserCallDescription)
                    .replace("\\/", " or ");
            DATA.print("diaQ"+queryForDiagonosis);

            queryForCarePlans = gptQueryList.get(2);
            queryForCarePlans = queryForCarePlans.replace("[patientCondition]", DATA.selectedUserCallCondition)
                    .replace("[patientDesc]", DATA.selectedUserCallDescription)
                    .replace("\\/", " or ");
            DATA.print("carQ"+queryForCarePlans);

            queryForPrescriptions = gptQueryList.get(3);
            queryForPrescriptions = queryForPrescriptions.replace("[patientCondition]", DATA.selectedUserCallCondition)
                    .replace("[patientDesc]", DATA.selectedUserCallDescription)
                    .replace("\\/", " or ");
            DATA.print("PreQ"+queryForPrescriptions);
        }
        else {
            customToast.showToast("No queries found",0,0);
        }

         disclaimer = sharedPrefsHelper.get("disclaimer");

        txtptName = findViewById(R.id.txtptName);
        txtptSymp = findViewById(R.id.txtptSymp);
        mScrlMain = findViewById(R.id.mScrlMain);

        layoutDiagnosis = findViewById(R.id.layoutDiagnosis);
        layoutPrescription = findViewById(R.id.layoutPrescription);

        etAiAnswerDignosis = findViewById(R.id.etAiAnswerDignosis);
        etAiAnswerCarePlan = findViewById(R.id.etAiAnswerCarePlan);
        etAiAnswerPresc = findViewById(R.id.etAiAnswerPresc);
        btnGenAiReview = findViewById(R.id.btnGenAiReview);
        btnGenAiCarePlan = findViewById(R.id.btnGenAiCarePlan);
        btnGenAiPresc = findViewById(R.id.btnGenAiPresc);

        btnGoback = findViewById(R.id.btnGoback);

        //solution to make edit text scrollable inside scroll view.
        etAiAnswerDignosis.setOnTouchListener((view, motionEvent) -> {
            mScrlMain.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        etAiAnswerCarePlan.setOnTouchListener((view, motionEvent) -> {
            mScrlMain.requestDisallowInterceptTouchEvent(true);
            return false;
        });

        etAiAnswerPresc.setOnTouchListener((view, motionEvent) -> {
            mScrlMain.requestDisallowInterceptTouchEvent(true);
            return false;
        });
        //end

        txtptName.setText(ActivityTcmDetails.ptFname + " " + ActivityTcmDetails.ptLname);
        txtptSymp.setText(DATA.selectedUserCallCondition);


        btnGenAiReview.setOnClickListener(v -> {
            layoutDiagnosis.setVisibility(View.VISIBLE);
            layoutPrescription.setVisibility(View.GONE);
            //callChatGPTAPI("Write a diagnosis for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : " + DATA.selectedUserCallDescription + ", Patient's DOB is "+DATA.selectedUserCallDOB+". Please do not add any information from your side.");

            GeneralAlertDialog.callAlert(ActivityAiSymptomsReview.this,disclaimer,()->{
                callChatGPTAPI(queryForDiagonosis);
            },()->{

            },"OK","Not Now","Disclaimer");



        });

        btnGenAiCarePlan.setOnClickListener(v -> {
            layoutDiagnosis.setVisibility(View.VISIBLE);
            layoutPrescription.setVisibility(View.GONE);
            //callChatCarePlanGPTAPI("Write a care plan for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : " + DATA.selectedUserCallDescription +  ", Patient's DOB is "+DATA.selectedUserCallDOB+", Please do not add any information from your side.");

            GeneralAlertDialog.callAlert(ActivityAiSymptomsReview.this,disclaimer,()->{
                callChatCarePlanGPTAPI(queryForCarePlans);
            },()->{

            },"OK","Not Now","Disclaimer");


        });

        btnGenAiPresc.setOnClickListener(v -> {
            layoutDiagnosis.setVisibility(View.GONE);
            layoutPrescription.setVisibility(View.VISIBLE);
            etAiAnswerPresc.setText("");
            //callChatGPTAPIAiPresc("Write prescription/medication suggestions for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : " + DATA.selectedUserCallDescription + ", Patient's DOB is "+DATA.selectedUserCallDOB+". Please do not add any information from your side.");


            GeneralAlertDialog.callAlert(ActivityAiSymptomsReview.this,disclaimer,()->{
                callChatGPTAPIAiPresc(queryForPrescriptions);
            },()->{

            },"OK","Not Now","Disclaimer");



        });

        btnGoback.setOnClickListener(v -> {
            etAiAnswerDignosis.setText("");
            etAiAnswerCarePlan.setText("");
            etAiAnswerPresc.setText("");
            onBackPressed();
        });
    }

    //Ahmer code for chatGPT auto Note
    final String API_URL = "https://api.openai.com/v1/completions";
    final String API_SECRET_KEY = SharedPrefsHelper.getInstance().get("gpt_key","");
    String clearedResponse;

    private void callChatGPTAPI(String query) {
        customProgressDialog.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("max_tokens", 1000);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), API_URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                DATA.print("-- GPT RESPONSE " + response);
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);

                    // No need to modify the response, use it directly
                    //clearedResponse = text;

                    DATA.print("-- cleanedResponse frm chatgpt : " + clearedResponse);
                    DATA.print("-- response frm chatgpt : " + response);
                    customProgressDialog.dismissProgressDialog();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(ActivityTcmDetails.ptFname).append(" ")
                                    .append(ActivityTcmDetails.ptLname)
                                            .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                                    .append("\n").append(text);

                    etAiAnswerDignosis.setText(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                DATA.print(errorResponse.toString());
                customProgressDialog.dismissProgressDialog();
            }
        });

    }

    //Ahmer code for chatGPT auto Note

    private void callChatCarePlanGPTAPI(String query) {
        customProgressDialog.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("max_tokens", 1000);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), API_URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    //clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    //clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + text);
                    DATA.print("-- response frm chatgpt : " + response);
                    customProgressDialog.dismissProgressDialog();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(ActivityTcmDetails.ptFname).append(" ")
                            .append(ActivityTcmDetails.ptLname)
                            .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                            .append("\n").append(text);


                    etAiAnswerCarePlan.setText(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                customProgressDialog.dismissProgressDialog();
            }
        });

    }

    private void callChatGPTAPIAiPresc(String query) {
        customProgressDialog.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("max_tokens", 1000);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), API_URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    //clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    //clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + text);
                    DATA.print("-- response frm chatgpt : " + response);
                    customProgressDialog.dismissProgressDialog();


                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(ActivityTcmDetails.ptFname).append(" ")
                            .append(ActivityTcmDetails.ptLname)
                            .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                            .append("\n").append(text);

                    etAiAnswerPresc.setText(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                customProgressDialog.dismissProgressDialog();
            }
        });

    }
}