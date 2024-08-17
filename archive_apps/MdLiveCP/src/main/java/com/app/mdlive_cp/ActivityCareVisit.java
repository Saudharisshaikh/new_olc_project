package com.app.mdlive_cp;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.CareVisitAdapter;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.CareVistBean;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalSocket;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityCareVisit extends BaseActivity implements GloabalSocket.SocketEmitterCallBack {


    ListView lvCareVisit;
    TextView tvNoData;

    GloabalSocket gloabalSocket;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_visit);

        gloabalSocket = new GloabalSocket(activity,this);

        lvCareVisit = (ListView) findViewById(R.id.lvCareVisit);
        tvNoData = (TextView) findViewById(R.id.tvNoData);


        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        ApiManager apiManager = new ApiManager(ApiManager.GET_SCHEDULE_VISIT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    ArrayList<CareVistBean> careVistBeans;
    CareVisitAdapter careVisitAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.equalsIgnoreCase(ApiManager.GET_SCHEDULE_VISIT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray appointments = jsonObject.getJSONArray("appointments");
                if(appointments.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                careVistBeans = new ArrayList<>();
                CareVistBean careVistBean;
                Gson gson = new Gson();
                for (int i = 0; i < appointments.length(); i++) {
                    careVistBean = gson.fromJson(appointments.getJSONObject(i)+"", CareVistBean.class);
                    careVistBeans.add(careVistBean);
                    careVistBean = null;
                }
                //set adapter
                careVisitAdapter = new CareVisitAdapter(activity,careVistBeans);
                lvCareVisit.setAdapter(careVisitAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    @Override
    public void onSocketCallBack(String emitterResponse) {

        try {
            JSONObject jsonObject = new JSONObject(emitterResponse);
            String id = jsonObject.getString("id");
            String usertype = jsonObject.getString("usertype");
            String status = jsonObject.getString("status");

            if(usertype.equalsIgnoreCase("doctor")){
                for (int i = 0; i < careVistBeans.size(); i++) {
                    if(careVistBeans.get(i).doctor_id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            careVistBeans.get(i).d_is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            careVistBeans.get(i).d_is_online = "0";
                        }
                    }
                }
                careVisitAdapter.notifyDataSetChanged();
            }

            if(usertype.equalsIgnoreCase("patient")){
                for (int i = 0; i < careVistBeans.size(); i++) {
                    if(careVistBeans.get(i).patient_id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            careVistBeans.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            careVistBeans.get(i).is_online = "0";
                        }
                    }
                }
                careVisitAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
