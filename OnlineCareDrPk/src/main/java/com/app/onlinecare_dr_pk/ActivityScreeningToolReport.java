package com.app.onlinecare_dr_pk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.onlinecare_dr_pk.adapter.ScreeningToolAdapter;
import com.app.onlinecare_dr_pk.api.ApiCallBack;
import com.app.onlinecare_dr_pk.api.ApiManager;
import com.app.onlinecare_dr_pk.api.CustomSnakeBar;
import com.app.onlinecare_dr_pk.model.ScreeningToolQuestionBean;
import com.app.onlinecare_dr_pk.util.CheckInternetConnection;
import com.app.onlinecare_dr_pk.util.CustomToast;
import com.app.onlinecare_dr_pk.util.DATA;
import com.app.onlinecare_dr_pk.util.HideShowKeypad;
import com.app.onlinecare_dr_pk.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityScreeningToolReport extends AppCompatActivity implements ApiCallBack {


    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvScreeningTool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screening_tool_report);

        activity = ActivityScreeningToolReport.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;


        lvScreeningTool = (ListView) findViewById(R.id.lvScreeningTool);

        lvScreeningTool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(screeningToolQuestionBeens.get(position).answer.equalsIgnoreCase("No")){
                    screeningToolQuestionBeens.get(position).answer = "Yes";
                    DATA.print("-- ans:Yes  position:"+position);
                }else {
                    screeningToolQuestionBeens.get(position).answer = "No";
                    DATA.print("-- ans:No  position:"+position);
                }
                screeningToolAdapter.notifyDataSetChanged();
            }
        });


        ApiManager apiManager = new ApiManager(ApiManager.SCREENINGTOOL_REPORT+"/"+DATA.selectedUserCallId,"get",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    static ArrayList<ScreeningToolQuestionBean> screeningToolQuestionBeens;
    ScreeningToolAdapter screeningToolAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.contains(ApiManager.SCREENINGTOOL_REPORT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                screeningToolQuestionBeens = new ArrayList<>();
                ScreeningToolQuestionBean bean;
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String answer = data.getJSONObject(i).getString("answer");
                    String question = data.getJSONObject(i).getString("question");
                    String question_id = data.getJSONObject(i).getString("question_id");

                    bean = new ScreeningToolQuestionBean(answer,question,question_id);
                    screeningToolQuestionBeens.add(bean);
                    bean = null;
                }
                screeningToolAdapter = new ScreeningToolAdapter(activity,screeningToolQuestionBeens);
                lvScreeningTool.setAdapter(screeningToolAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equals(ApiManager.SAVE_ANWERS)){
            //{"success":"Answers has been successfully submitted"}
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_soap_notes, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {
            saveAnwers();
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void saveAnwers(){
        try {
            RequestParams params = new RequestParams();
            params.put("patient_id",DATA.selectedUserCallId);

            for (int i = 0; i < screeningToolQuestionBeens.size(); i++) {
                params.put("question_id["+screeningToolQuestionBeens.get(i).question_id+"]",
                        screeningToolQuestionBeens.get(i).answer);
            }

            ApiManager apiManager = new ApiManager(ApiManager.SAVE_ANWERS,"post",params,apiCallBack, activity);
            apiManager.loadURL();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
