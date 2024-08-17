package com.app.emcurama;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.app.emcurama.adapter.AllFallRiskAdapter;
import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.api.CustomSnakeBar;
import com.app.emcurama.model.FallRiskBean;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.HideShowKeypad;
import com.app.emcurama.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAllFallRisk extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ListView lvAllFallRisk;

    @Override
    protected void onResume() {
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.ALL_FALL_RISK_ASSESMENT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fall_risk);

        activity = ActivityAllFallRisk.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvAllFallRisk = (ListView) findViewById(R.id.lvAllFallRisk);

        lvAllFallRisk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFallRiskBean = fallRiskBeens.get(position);

                Intent intent = new Intent(activity,FallRiskForm.class);
                intent.putExtra("isEdit",true);
                startActivity(intent);
            }
        });


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(FallRiskForm.class, false);
            }
        });

    }


    ArrayList<FallRiskBean> fallRiskBeens;
    public static FallRiskBean selectedFallRiskBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray data = jsonObject.getJSONArray("data");
            fallRiskBeens = new ArrayList<>();
            FallRiskBean bean;
            for (int i = 0; i < data.length(); i++) {
                String id = data.getJSONObject(i).getString("id");
                String patient_id = data.getJSONObject(i).getString("patient_id");
                String doctor_id = data.getJSONObject(i).getString("doctor_id");
                String age_65 = data.getJSONObject(i).getString("age_65");
                String diagnosis = data.getJSONObject(i).getString("diagnosis");
                String prior_history = data.getJSONObject(i).getString("prior_history");
                String incontinence = data.getJSONObject(i).getString("incontinence");
                String visual_impairment = data.getJSONObject(i).getString("visual_impairment");
                String impaired_functional_mobility = data.getJSONObject(i).getString("impaired_functional_mobility");
                String environment_hazards = data.getJSONObject(i).getString("environment_hazards");
                String poly_pharmacy = data.getJSONObject(i).getString("poly_pharmacy");
                String pain_affection = data.getJSONObject(i).getString("pain_affection");
                String cognitive_impairment = data.getJSONObject(i).getString("cognitive_impairment");
                String score = data.getJSONObject(i).getString("score");
                String dateof = data.getJSONObject(i).getString("dateof");
                String status = data.getJSONObject(i).getString("status");
                String is_deleted = data.getJSONObject(i).getString("is_deleted");
                String delete_date = data.getJSONObject(i).getString("delete_date");

                bean = new FallRiskBean(id,patient_id,doctor_id,age_65,diagnosis,prior_history,incontinence,visual_impairment,impaired_functional_mobility
                ,environment_hazards,poly_pharmacy,pain_affection,cognitive_impairment,score,dateof,status,is_deleted,delete_date);
                fallRiskBeens.add(bean);
                bean = null;
            }

            lvAllFallRisk.setAdapter(new AllFallRiskAdapter(activity,fallRiskBeens));
        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(FallRiskForm.class, false);//not used!

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }
}
