package com.app.msu_cp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.adapters.AllFallRiskAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.model.FallRiskBean;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityAllFallRisk extends BaseActivity implements ApiCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ListView lvAllFallRisk;
    TextView tvNoData;


    static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
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
        tvNoData = findViewById(R.id.tvNoData);

        lvAllFallRisk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewOrEditForm(position, true);
            }
        });

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(FallRiskForm.class, false);
            }
        });


        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesList = activity;


        loadListData();

    }

    public void loadListData(){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.ALL_FALL_RISK_ASSESMENT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    ArrayList<FallRiskBean> fallRiskBeens;
    public static FallRiskBean selectedFallRiskBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {

        if(apiName.equalsIgnoreCase(ApiManager.ALL_FALL_RISK_ASSESMENT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                /*fallRiskBeens = new ArrayList<>();
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
                    String is_lock = data.getJSONObject(i).getString("is_lock");

                    bean = new FallRiskBean(id,patient_id,doctor_id,age_65,diagnosis,prior_history,incontinence,visual_impairment,impaired_functional_mobility
                            ,environment_hazards,poly_pharmacy,pain_affection,cognitive_impairment,score,dateof,status,is_deleted,delete_date, is_lock);
                    fallRiskBeens.add(bean);
                    bean = null;
                }*/

                Type listType = new TypeToken<ArrayList<FallRiskBean>>() {}.getType();
                fallRiskBeens = new Gson().fromJson(data.toString(), listType);

                if(fallRiskBeens != null){
                    lvAllFallRisk.setAdapter(new AllFallRiskAdapter(activity,fallRiskBeens));

                    tvNoData.setVisibility(fallRiskBeens.isEmpty() ? View.VISIBLE : View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(FallRiskForm.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }
    */



    public void viewOrEditForm(int position, boolean isReadOnly){

        selectedFallRiskBean = fallRiskBeens.get(position);

        Intent intent = new Intent(activity,FallRiskForm.class);
        intent.putExtra("isEdit",true);

        intent.putExtra("isReadOnly", isReadOnly);

        startActivity(intent);
    }
}
