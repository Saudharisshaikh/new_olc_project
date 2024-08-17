package com.app.OnlineCareUS_MA;

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

import com.app.OnlineCareUS_MA.adapter.AllEnvirAdapter;
import com.app.OnlineCareUS_MA.api.ApiCallBack;
import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.api.CustomSnakeBar;
import com.app.OnlineCareUS_MA.model.EnvirBean;
import com.app.OnlineCareUS_MA.util.CheckInternetConnection;
import com.app.OnlineCareUS_MA.util.CustomToast;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.HideShowKeypad;
import com.app.OnlineCareUS_MA.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAllEnvir extends AppCompatActivity implements ApiCallBack {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ListView lvAllEnv;

    @Override
    protected void onResume() {
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.ALL_ENVIR_ASSES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_depression);

        activity = ActivityAllEnvir.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvAllEnv = (ListView) findViewById(R.id.lvAllDepression);

        lvAllEnv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedEnvirBean= envirBeens.get(position);
                Intent intent = new Intent(activity, ActivityEnvirForm.class);
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
                openActivity.open(ActivityEnvirForm.class, false);
            }
        });

    }


    ArrayList<EnvirBean> envirBeens;
    public static EnvirBean selectedEnvirBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ALL_ENVIR_ASSES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                envirBeens = new ArrayList<>();
                EnvirBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String doctor_id = data.getJSONObject(i).getString("doctor_id");
                    String data1 = data.getJSONObject(i).getString("data");
                    String dateof = data.getJSONObject(i).getString("dateof");
                    String status = data.getJSONObject(i).getString("status");
                    String is_deleted = data.getJSONObject(i).getString("is_deleted");
                    String delete_date = data.getJSONObject(i).getString("delete_date");

                    bean = new EnvirBean(id,patient_id,doctor_id,data1,dateof,status,is_deleted,delete_date);
                    envirBeens.add(bean);
                    bean = null;
                }

                lvAllEnv.setAdapter(new AllEnvirAdapter(activity,envirBeens));

                DATA.print("--size: "+envirBeens.size());
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

            openActivity.open(ActivityEnvirForm.class, false);//not used!

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }
}
