package com.app.mdlive_cp.careplan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityCarePlan extends BaseActivity {


    TextView tvNoData;
    ListView lvCarePlanList;
    Button btnAddCarePlan;

    static boolean shouldLoadCP = false;

    @Override
    protected void onResume() {
        if(shouldLoadCP){
            shouldLoadCP = false;
            loadCarePlan();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan);

        lvCarePlanList = (ListView) findViewById(R.id.lvCarePlanList);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        btnAddCarePlan = (Button) findViewById(R.id.btnAddCarePlan);

        lvCarePlanList.setOnItemClickListener((parent, view, position, id) -> {
            slectedCareID = cpListBeans.get(position).id;
            openActivity.open(ActivityCarePlanDetail.class,false);
        });

        btnAddCarePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddCarePlan.class, false);
            }
        });

        loadCarePlan();
    }

    void loadCarePlan(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);

        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CPListBean> cpListBeans;
    public static String slectedCareID;

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cpListBeans = new ArrayList<>();
                CPListBean cpListBean;
                Gson gson = new Gson();
                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                for (int i = 0; i < data.length(); i++) {
                    cpListBean = gson.fromJson(data.getJSONObject(i)+"",CPListBean.class);
                    cpListBeans.add(cpListBean);
                    cpListBean = null;
                }
                lvCarePlanList.setAdapter(new CPListAdapter(activity, cpListBeans));
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
