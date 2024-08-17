package com.app.mhcsn_dr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.app.mhcsn_dr.adapter.DepressionAdapter;
import com.app.mhcsn_dr.api.ApiCallBack;
import com.app.mhcsn_dr.api.ApiManager;
import com.app.mhcsn_dr.api.CustomSnakeBar;
import com.app.mhcsn_dr.model.DepressionFieldBean;
import com.app.mhcsn_dr.util.CheckInternetConnection;
import com.app.mhcsn_dr.util.CustomToast;
import com.app.mhcsn_dr.util.DATA;
import com.app.mhcsn_dr.util.ExpandableHeightListView;
import com.app.mhcsn_dr.util.HideShowKeypad;
import com.app.mhcsn_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDepressionForm extends AppCompatActivity implements ApiCallBack {

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

    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depression_form);

        isEdit = getIntent().getBooleanExtra("isEdit",false);

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

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        ApiManager apiManager = new ApiManager(ApiManager.DEPRESSION_FIELDS,"post",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void submitForm(){

        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("doctor_id",prefs.getString("id",""));
        params.put("score",score+"");

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

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.DEPRESSION_SCALE)){
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
