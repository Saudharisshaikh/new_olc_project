package com.app.emcuradr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.CustomSnakeBar;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActivityAiCarePlan extends AppCompatActivity implements ApiCallBack {


    TextView txtptData, txtAiAnswer;
    Button btnGenAiCarePlan, btnGoback;
    LinearLayout layoutRev;

    Activity activity;

    Dialog_CustomProgress customProgressDialog;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;
    String disclaimer = "";
    SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_care_plan);

        activity = ActivityAiCarePlan.this;
        customProgressDialog = new Dialog_CustomProgress(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);

        txtptData = findViewById(R.id.txtptData);
        txtAiAnswer = findViewById(R.id.txtAiAnswer);
        layoutRev = findViewById(R.id.layoutRev);
        btnGenAiCarePlan = findViewById(R.id.btnGenAiCarePlan);
        btnGoback = findViewById(R.id.btnGoback);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if(DATA.isExistingPatCall || DATA.isInstantConnect){
            txtptData.setText("Patient name : " + DATA.selectedUserCallName + "\nDOB : " + DATA.selectedUserCallDOB
                    + "\nSymptoms : " + DATA.selectedUserCallCondition);
        }

        else {
            txtptData.setText("Patient name : " + ActivityTcmDetails.ptFname + " " + ActivityTcmDetails.ptLname + "\nDOB : " + ActivityTcmDetails.ptDOB
                    + "\nSymptoms : " + DATA.selectedUserCallCondition);
        }


        disclaimer = sharedPrefsHelper.get("disclaimer");
        generateCarePlan();
        btnGenAiCarePlan.setOnClickListener(v -> {
            //callChatGPTAPI("Write a care plan for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : Describe your condition");

            generateCarePlan();



        });

        btnGoback.setOnClickListener(v -> {
            txtAiAnswer.setText("");
            onBackPressed();
        });
    }

    private void generateCarePlan() {
        RequestParams params = new RequestParams();
        //GM added call_id
        String callId = "0";
//        if (DATA.incomingCall) {
//            callId = DATA.incommingCallId;
//        }
//        else  if(DATA.isExistingPatCall){
//            callId = DATA.incommingCallId;
//        }
        if(!DATA.incommingCallId.equals("")){
            callId = DATA.incommingCallId;
        }
        else {
            callId = prefs.getString("callingID", "");
        }
        params.put("call_id", callId);
        //GM added call_id
        ApiManager apiManager = new ApiManager(ApiManager.GENERATE_CAREPLAN, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    //Ahmer code for chatGPT auto Note
    final String API_URL = "https://api.openai.com/v1/completions";
    final String API_SECRET_KEY = SharedPrefsHelper.getInstance().get("gpt_key","");//"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";
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
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    //clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    //clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + clearedResponse);
                    DATA.print("-- response frm chatgpt : " + response);
                    customProgressDialog.dismissProgressDialog();
                    layoutRev.setVisibility(View.VISIBLE);
                    txtAiAnswer.setText(text);
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

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GENERATE_CAREPLAN)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    String data = jsonObject.getString("data");
                    layoutRev.setVisibility(View.VISIBLE);
                    txtAiAnswer.setText(data);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    String msg = jsonObject.getString("msg");
                    customToast.showToast(msg, 0, 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}