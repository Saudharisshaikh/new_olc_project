package com.app.msu_cp.reliance.careteam;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCareTeam extends BaseActivity implements View.OnClickListener{


    LinearLayout layTcmOptions;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,
            tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabOM,tvTabSup;
    ListView lvNurse;
    EditText etSearchQuery;
    ImageView ivSearchQuery;

    //String patientType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_team);

        //patientType = getIntent().getStringExtra("patientType");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Care Team");
        }


        layTcmOptions = (LinearLayout) findViewById(R.id.layTcmOptions);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        lvNurse = (ListView) findViewById(R.id.lvNurse);

        etSearchQuery = (EditText) findViewById(R.id.etSearchQuery);
        ivSearchQuery = (ImageView) findViewById(R.id.ivSearchQuery);

        tvTabNurse = (TextView) findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = (TextView) findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = (TextView) findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = (TextView) findViewById(R.id.tvTabDietitian);
        tvTabOT = (TextView) findViewById(R.id.tvTabOT);
        tvTabPharmacist = (TextView) findViewById(R.id.tvTabPharmacist);
        tvTabMA = (TextView) findViewById(R.id.tvTabMA);
        tvTabOM = (TextView) findViewById(R.id.tvTabOM);
        tvTabSup = (TextView) findViewById(R.id.tvTabSup);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);

        tvTabNurse.setOnClickListener(this);
        tvTabNursePractitioner.setOnClickListener(this);
        tvTabSocialWorker.setOnClickListener(this);
        tvTabDietitian.setOnClickListener(this);
        tvTabOT.setOnClickListener(this);
        tvTabPharmacist.setOnClickListener(this);
        tvTabMA.setOnClickListener(this);
        tvTabOM.setOnClickListener(this);
        tvTabSup.setOnClickListener(this);



        /*DATA.print("-- on nurse activity patientType = "+patientType);
        try {
            int count = patientType.split(",").length;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvTabTCM.getLayoutParams();
            params.weight = (float) 4/count;

            patient_cat = patientType.split(",")[0];
            if (checkInternetConnection.isConnectedToInternet()){
                find_nurse("", DATA.selectedUserCallId,patient_cat);
            }else {
                customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
            }

            if (patientType.contains("tcm")){
                tvTabTCM.setVisibility(View.VISIBLE);
                tvTabTCM.setLayoutParams(params);
            }else {
                tvTabTCM.setVisibility(View.GONE);
            }
            if (patientType.contains("complex_care")){
                tvTabCC.setVisibility(View.VISIBLE);
                tvTabCC.setLayoutParams(params);
            }else {
                tvTabCC.setVisibility(View.GONE);
            }
            if (patientType.contains("home_health")){
                tvTabHomeHealth.setVisibility(View.VISIBLE);
                tvTabHomeHealth.setLayoutParams(params);
            }else {
                tvTabHomeHealth.setVisibility(View.GONE);
            }
            if (patientType.contains("nursing_home")){
                tvTabNursingHome.setVisibility(View.VISIBLE);
                tvTabNursingHome.setLayoutParams(params);
            }else {
                tvTabNursingHome.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/

        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listPos = position;
                if (checkInternetConnection.isConnectedToInternet()){
                    if (careTeamBeans.get(position).is_added.equals("0")){
                        request_to_add_nurse(careTeamBeans.get(position).id,careTeamBeans.get(position).patient_category);
                    }else {
                        delete_care_management(careTeamBeans.get(position).id,careTeamBeans.get(position).patient_category);
                    }
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
            }
        });

        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadCareTeam();

        /*ivSearchQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearchQuery.getText().toString().isEmpty()) {
                    customToast.showToast("Please type care provider name to search",0,0);
                }else {
                    if (checkInternetConnection.isConnectedToInternet()){
                        find_nurse(etSearchQuery.getText().toString(),DATA.selectedUserCallId,patient_cat);
                    }else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                    }
                }
            }
        });*/
    }


    public void loadCareTeam(){
        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);

        ApiManager apiManager = new ApiManager(ApiManager.CARE_TEAM,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
    public void request_to_add_nurse(String nurse_id, String patient_category) {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("nurse_id", nurse_id);
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("patient_category", "tcm");//patient_category

        /*if (DATA.isFromAppointment) {
            params.put("care_type", "appointment");
        } else {
            params.put("care_type", "livecare");
        }*/

        ApiManager apiManager = new ApiManager(ApiManager.ADD_CARETEAM,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    public void delete_care_management(String nurse_id,String patient_category) {

        RequestParams params = new RequestParams();
        params.put("nurse_id", nurse_id);
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("patient_category", "tcm");//patient_category

        ApiManager apiManager = new ApiManager(ApiManager.DEL_CARETEAM,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }




    ArrayList<CareTeamBean> careTeamBeansOrig,
            careTeamBeans = new ArrayList<>();
    int listPos;
    CareTeamAdapter careTeamAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.equalsIgnoreCase(ApiManager.CARE_TEAM)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                JSONArray nurses = jsonObject.getJSONArray("nurses");
                Type listType = new TypeToken<ArrayList<CareTeamBean>>() {}.getType();
                careTeamBeansOrig = new Gson().fromJson(nurses.toString(), listType);

                if(careTeamBeansOrig != null){

                    for (int i = 0; i <careTeamBeansOrig.size() ; i++) {
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);

                    tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                }


            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_CARETEAM)){
            //{"success":1,"message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")){
                    careTeamBeans.get(listPos).is_added = "1";
                    careTeamAdapter.notifyDataSetChanged();
                }else {
                    customToast.showToast(DATA.CMN_ERR_MSG,0,1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.CMN_ERR_MSG,0,1);
            }


        }else if(apiName.equalsIgnoreCase(ApiManager.DEL_CARETEAM)){
            //{"status":"success","msg":"deleted"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equals("success")){
                    careTeamBeans.get(listPos).is_added = "0";
                    careTeamAdapter.notifyDataSetChanged();
                }else {
                    customToast.showToast(DATA.CMN_ERR_MSG,0,1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.CMN_ERR_MSG,0,1);
            }
        }
    }

    //String patient_cat = "";
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "tcm";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "complex_care";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "home_health";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                patient_cat = "nursing_home";
                if (checkInternetConnection.isConnectedToInternet()){
                    find_nurse("a",DATA.selectedUserCallId,patient_cat);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;*/





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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Pharmacist")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("MA")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Office Manager")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
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

                if (careTeamBeansOrig !=null){
                    careTeamBeans = new ArrayList<>();
                    for (int i = 0; i<careTeamBeansOrig.size();i++){
                        if (careTeamBeansOrig.get(i).doctor_category.equalsIgnoreCase("Supervisor")){
                            careTeamBeans.add(careTeamBeansOrig.get(i));
                        }
                    }
                    careTeamAdapter = new CareTeamAdapter(activity,careTeamBeans);
                    lvNurse.setAdapter(careTeamAdapter);
                }
                break;
            default:
                break;
        }
    }
}
