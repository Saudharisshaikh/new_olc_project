package com.app.emcurama;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcurama.adapter.PatientInviteAdapter;
import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.MyAppointmentsModel;
import com.app.emcurama.model.ReportsModel;
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
//this activity is for add patients to chat groups
public class ActivityTCM_3 extends BaseActivity implements View.OnClickListener,ApiCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

   // ViewFlipper vfTCM;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,tvTabAll;
    ListView lvTCM;//,lvCC,lvHomeHealth,lvNursingHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcm2);

        activity = ActivityTCM_3.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        //vfTCM = (ViewFlipper) findViewById(R.id.vfTCM);
        lvTCM = (ListView) findViewById(R.id.lvTCM);
        //lvCC = (ListView) findViewById(R.id.lvCC);
        //lvHomeHealth = (ListView) findViewById(R.id.lvHomeHealth);
        //lvNursingHome = (ListView) findViewById(R.id.lvNursingHome);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        tvTabAll = (TextView) findViewById(R.id.tvTabAll);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);

        lvTCM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("user_id", myAppointmentsModels.get(position).id);
                params.put("user_type", "patient");
                params.put("group_id",ActivityGroupChat.selectedGroupBean.id);

                ApiManager apiManager = new ApiManager(ApiManager.ADD_TO_GROUP,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        getPatientBycategory();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(0);
                patient_category = "tcm";
                getPatientBycategory();
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(1);
                patient_category = "complex_care";
                getPatientBycategory();
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(2);
                patient_category = "home_health";
                getPatientBycategory();
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                //vfTCM.setDisplayedChild(3);
                patient_category = "nursing_home";
                getPatientBycategory();
                break;
            case R.id.tvTabAll:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));

                //vfTCM.setDisplayedChild(3);
                patient_category = "all";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            default:
                break;
        }
    }

    String patient_category = "all";
    private void getPatientBycategory(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_category", patient_category);

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_BY_CATEGORY,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<MyAppointmentsModel> myAppointmentsModels;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_PATIENT_BY_CATEGORY)){
            try {

                JSONObject jsonObject = new JSONObject(content);
                JSONArray appointmentsArray = jsonObject.getJSONArray("livecheckups");

                        /*if (appointmentsArray.length() == 0) {
                            lvLiveCare.setVisibility(View.GONE);
                            tvNoLiveCares.setVisibility(View.VISIBLE);
                        } else {
                            lvLiveCare.setVisibility(View.VISIBLE);
                            tvNoLiveCares.setVisibility(View.GONE);*/

                myAppointmentsModels = new ArrayList<MyAppointmentsModel>();
                MyAppointmentsModel temp = new MyAppointmentsModel();

                for(int i = 0; i<appointmentsArray.length(); i++) {


                    temp = new MyAppointmentsModel();
                    temp.id = appointmentsArray.getJSONObject(i).getString("id");
                    temp.liveCheckupId = "";//appointmentsArray.getJSONObject(i).getString("live_checkup_id");
                    temp.first_name = appointmentsArray.getJSONObject(i).getString("first_name");
                    temp.last_name = appointmentsArray.getJSONObject(i).getString("last_name");
                    temp.symptom_name = "";//appointmentsArray.getJSONObject(i).getString("symptom_name");
                    temp.condition_name = "";//appointmentsArray.getJSONObject(i).getString("condition_name");
                    temp.description = "";//appointmentsArray.getJSONObject(i).getString("description");
                    temp.patients_qbid = appointmentsArray.getJSONObject(i).getString("patients_qbid");
                    temp.datetime = "";//appointmentsArray.getJSONObject(i).getString("datetime");

                    temp.latitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("latitude"));
                    temp.longitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("longitude"));
                    if (appointmentsArray.getJSONObject(i).has("image")) {
                        temp.image = appointmentsArray.getJSONObject(i).getString("image");
                    } else {
                        temp.image = "";
                    }

                    temp.birthdate = appointmentsArray.getJSONObject(i).getString("birthdate");
                    temp.gender = appointmentsArray.getJSONObject(i).getString("gender");
                    temp.residency = appointmentsArray.getJSONObject(i).getString("residency");
                    temp.patient_phone = appointmentsArray.getJSONObject(i).getString("patient_phone");
                    temp.StoreName = "";//appointmentsArray.getJSONObject(i).getString("StoreName");
                    temp.PhonePrimary = "";//appointmentsArray.getJSONObject(i).getString("PhonePrimary");

                    temp.is_online = appointmentsArray.getJSONObject(i).getString("is_online");

                    if (appointmentsArray.getJSONObject(i).has("reports")) {
                        String rp = appointmentsArray.getJSONObject(i).getString("reports");
                        JSONArray reports = new JSONArray(rp);
                        temp.sharedReports = new ArrayList<ReportsModel>();
                        //ReportsModel reportsModel;
                        for (int j = 0; j < reports.length(); j++) {
                            ReportsModel reportsModel = new ReportsModel();

                            reportsModel.name = reports.getJSONObject(j).getString("file_display_name");
                            reportsModel.url = reports.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
                            reportsModel.patientID = reports.getJSONObject(j).getString("patient_id");//patient_id

                            temp.sharedReports.add(reportsModel);

                            //reportsModel = null;
                        }
                    } else {
                        temp.sharedReports = new ArrayList<ReportsModel>();
                    }

                    myAppointmentsModels.add(temp);

                    temp = null;
                }

						/*String reprts = jsonObject.getString("reports");

						reportsArray = new JSONArray(reprts);

						DATA.allReports = new ArrayList<ReportsModel>();

						ReportsModel temp1;

						for(int j = 0; j<reportsArray.length(); j++) {

							temp1 = new ReportsModel();

							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id

							DATA.allReports.add(temp1);

							temp1 = null;
						}*/

                PatientInviteAdapter patientInviteAdapter = new PatientInviteAdapter(activity,myAppointmentsModels);
                lvTCM.setAdapter(patientInviteAdapter);

                //}



            } catch (JSONException e) {
                DATA.print("--online care exception in getlivecare patients: "+e);
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                e.printStackTrace();
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
