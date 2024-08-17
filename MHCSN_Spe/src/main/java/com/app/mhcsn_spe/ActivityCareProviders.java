package com.app.mhcsn_spe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_spe.adapter.NurseAdapter2;
import com.app.mhcsn_spe.api.ApiCallBack;
import com.app.mhcsn_spe.api.ApiManager;
import com.app.mhcsn_spe.model.NurseBean2;
import com.app.mhcsn_spe.util.CheckInternetConnection;
import com.app.mhcsn_spe.util.CustomToast;
import com.app.mhcsn_spe.util.DATA;
import com.app.mhcsn_spe.util.GloabalSocket;
import com.app.mhcsn_spe.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityCareProviders extends BaseActivity implements ApiCallBack,GloabalSocket.SocketEmitterCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    CustomToast customToast;
    OpenActivity openActivity;

    ListView lvNurse;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabOM,tvTabSup,tvTabAll;

    GloabalSocket gloabalSocket;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_providers);

        activity = ActivityCareProviders.this;
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        gloabalSocket = new GloabalSocket(activity,this);

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
        tvTabOT = (TextView) findViewById(R.id.tvTabOT);
        tvTabPharmacist = (TextView) findViewById(R.id.tvTabPharmacist);
        tvTabMA = (TextView) findViewById(R.id.tvTabMA);
        tvTabOM = (TextView) findViewById(R.id.tvTabOM);
        tvTabSup = (TextView) findViewById(R.id.tvTabSup);
        tvTabAll = findViewById(R.id.tvTabAll);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvTabNurse:
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;

                    case R.id.tvTabNursePractitioner:
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabSocialWorker:
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabDietitian:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabOT:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                        nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabPharmacist:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Pharmacist")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabMA:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("MA")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabOM:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Office Manager")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabSup:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Supervisor")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabAll:
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));

                        if (nurseBeensOrig !=null){
                            /*nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }*/
                            nurseBeens.addAll(nurseBeensOrig);
                            nurseAdapter = new NurseAdapter2(activity,nurseBeens);
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
        tvTabOT.setOnClickListener(onClickListener);
        tvTabPharmacist.setOnClickListener(onClickListener);
        tvTabMA.setOnClickListener(onClickListener);
        tvTabOM.setOnClickListener(onClickListener);
        tvTabSup.setOnClickListener(onClickListener);
        tvTabAll.setOnClickListener(onClickListener);


        if (checkInternetConnection.isConnectedToInternet()){
            find_nurse("");
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }

    }



    ArrayList<NurseBean2> nurseBeens;
    ArrayList<NurseBean2> nurseBeensOrig;
    NurseBean2 bean;
    NurseAdapter2 nurseAdapter;
    public void find_nurse(String keyword) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        if(!keyword.isEmpty()){
            params.put("keyword", keyword);
        }
        params.put("patient_id", "0");
        params.put("patient_category", "0");

        ApiManager apiManager = new ApiManager(ApiManager.FIND_NURSE,"post",params,this, activity);
        apiManager.loadURL();

    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.FIND_NURSE)){
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
                        //if (doctor_category.equalsIgnoreCase("Nurse")){//adding all
                        nurseBeens.add(bean);
                        //}
                        nurseBeensOrig.add(bean);
                    }

                    bean = null;

                }
                nurseAdapter = new NurseAdapter2(activity,nurseBeens);
                lvNurse.setAdapter(nurseAdapter);

                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
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
                for (int i = 0; i < nurseBeens.size(); i++) {
                    if(nurseBeens.get(i).id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            nurseBeens.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            nurseBeens.get(i).is_online = "0";
                        }
                    }
                }
                nurseAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
