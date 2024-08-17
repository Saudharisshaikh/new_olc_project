package com.app.msu_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.adapters.NurseAdapter3;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.NurseBean2;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//this activity is for add care providers to chat groups
public class ActivityCareProvidersInvite extends AppCompatActivity implements ApiCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    CustomToast customToast;
    OpenActivity openActivity;
    ApiCallBack apiCallBack;

    ListView lvNurse;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_providers);

        activity = ActivityCareProvidersInvite.this;
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;


        final Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lvNurse = (ListView) findViewById(R.id.lvNurse);
        tvTabNurse = (TextView) findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = (TextView) findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = (TextView) findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = (TextView) findViewById(R.id.tvTabDietitian);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvTabNurse:
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter3(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;

                    case R.id.tvTabNursePractitioner:
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter3(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabSocialWorker:
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter3(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabDietitian:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter3(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        tvTabNurse.setOnClickListener(onClickListener);
        tvTabNursePractitioner.setOnClickListener(onClickListener);
        tvTabSocialWorker.setOnClickListener(onClickListener);
        tvTabDietitian.setOnClickListener(onClickListener);

        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("user_id", nurseBeens.get(position).id);
                params.put("user_type", "doctor");
                params.put("group_id",ActivityGroupChat.selectedGroupBean.id);

                ApiManager apiManager = new ApiManager(ApiManager.ADD_TO_GROUP,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        if (checkInternetConnection.isConnectedToInternet()){
            find_nurse("");
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }

    }



    ArrayList<NurseBean2> nurseBeens;
    ArrayList<NurseBean2> nurseBeensOrig;
    NurseBean2 bean;
    NurseAdapter3 nurseAdapter;
    public void find_nurse(String keyword) {
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        if(!keyword.isEmpty()){
            params.put("keyword", keyword);
        }
        params.put("patient_id", "0");
        params.put("patient_category", "0");

        ApiManager apiManager = new ApiManager(ApiManager.FIND_NURSE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.FIND_NURSE)){
            try {
                nurseBeensOrig = new ArrayList<NurseBean2>();
                nurseBeens = new ArrayList<NurseBean2>();
                JSONObject jsonObject = new JSONObject(content);
                JSONArray nurses = jsonObject.getJSONArray("nurses");
                for (int i = 0; i<nurses.length(); i++){

                    String id = nurses.getJSONObject(i).getString("id");
                    String is_online = nurses.getJSONObject(i).getString("is_online");
                    String first_name = nurses.getJSONObject(i).getString("first_name");
                    String last_name = nurses.getJSONObject(i).getString("last_name");
                    String image = nurses.getJSONObject(i).getString("image");
                    String doctor_category = nurses.getJSONObject(i).getString("doctor_category");

                    bean = new NurseBean2(id,is_online,first_name,last_name,image,doctor_category);
                    if (!id.equals(prefs.getString("id",""))) {
                        if (doctor_category.equalsIgnoreCase("Nurse")){
                            nurseBeens.add(bean);
                        }
                        nurseBeensOrig.add(bean);
                    }

                    bean = null;

                }
                nurseAdapter = new NurseAdapter3(activity,nurseBeens);
                lvNurse.setAdapter(nurseAdapter);

                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }
        }else if(apiName.equals(ApiManager.ADD_TO_GROUP)){
            //result: {"status":"success","message":"User has been added."}
            try {
                JSONObject jsonObject = new JSONObject(content);

                customToast.showToast(jsonObject.getString("message"),0,0);
                String s = jsonObject.getString("status");
                if (s.equalsIgnoreCase("success")) {
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
