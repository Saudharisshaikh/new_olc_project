package com.app.greatriverma;

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

import com.app.greatriverma.adapter.AllDepressionAdapter;
import com.app.greatriverma.api.ApiCallBack;
import com.app.greatriverma.api.ApiManager;
import com.app.greatriverma.api.CustomSnakeBar;
import com.app.greatriverma.model.DepressionBean;
import com.app.greatriverma.util.CheckInternetConnection;
import com.app.greatriverma.util.CustomToast;
import com.app.greatriverma.util.DATA;
import com.app.greatriverma.util.HideShowKeypad;
import com.app.greatriverma.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAllDepression extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ListView lvAllDepression;

    @Override
    protected void onResume() {
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.ALL_DEPRESSION_SCALE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_depression);

        activity = ActivityAllDepression.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvAllDepression = (ListView) findViewById(R.id.lvAllDepression);

        lvAllDepression.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDepressionBean = depressionBeens.get(position);
                Intent intent = new Intent(activity, ActivityDepressionForm.class);
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
                openActivity.open(ActivityDepressionForm.class, false);
            }
        });

    }


    ArrayList<DepressionBean> depressionBeens;
    public static DepressionBean selectedDepressionBean;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ALL_DEPRESSION_SCALE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                depressionBeens = new ArrayList<>();
                DepressionBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String doctor_id = data.getJSONObject(i).getString("doctor_id");
                    String depression_data = data.getJSONObject(i).getString("depression_data");
                    String score = data.getJSONObject(i).getString("score");
                    String dateof = data.getJSONObject(i).getString("dateof");

                    bean = new DepressionBean(id,patient_id,doctor_id,depression_data,score,dateof);
                    depressionBeens.add(bean);
                    bean = null;
                }

                lvAllDepression.setAdapter(new AllDepressionAdapter(activity,depressionBeens));
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
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityDepressionForm.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }
}
