package com.app.mhcsn_cp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.adapters.MyAppointmentsAdapter;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.MyAppointmentsModel;
import com.app.mhcsn_cp.model.ReportsModel;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalSocket;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.app.mhcsn_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityTCM extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener, ApiCallBack,GloabalSocket.SocketEmitterCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,tvTabAll;
    ListView lvTCM;
    TextView tvNoData;

    GloabalSocket gloabalSocket;

    EditText etFilter;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
    }

    //String tabKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcm);

        //tabKey = getIntent().getStringExtra(MainActivityNew.TAB_KEY);

        activity = ActivityTCM.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        gloabalSocket = new GloabalSocket(activity,this);


        lvTCM = (ListView) findViewById(R.id.lvTCM);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);
        tvTabAll = (TextView) findViewById(R.id.tvTabAll);

        etFilter = (EditText) findViewById(R.id.etFilter);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);

        if (checkInternetConnection.isConnectedToInternet()){
            getPatientBycategory();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }

        lvTCM.setOnItemClickListener(this);



        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (myAppointmentsAdapter != null) {
                    myAppointmentsAdapter.filter(s.toString());
                    //	searchableAdapter.filterRecordings(s+"");
                    /*DATA.print("--value of DATA.noRecordingFound " +DATA.noRecordingFound);
                    if (DATA.noRecordingFound) {
                        tvNoRecordingsFound.setVisibility(View.VISIBLE);
                        lvRecordings.setVisibility(View.GONE);
                    } else {
                        lvRecordings.setVisibility(View.VISIBLE);
                        tvNoRecordingsFound.setVisibility(View.GONE);
                    }*/
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {
        etFilter.setText("");
        switch (v.getId()){
            case R.id.tvTabTCM:

                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (checkInternetConnection.isConnectedToInternet()){
                    DATA.selectedTabFromMain = MainActivityNew.TAB_TCM;
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }

                break;
            case R.id.tvTabCC:

                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (checkInternetConnection.isConnectedToInternet()){
                    DATA.selectedTabFromMain = MainActivityNew.TAB_CC;
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabHomeHealth:

                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (checkInternetConnection.isConnectedToInternet()){
                    DATA.selectedTabFromMain = MainActivityNew.TAB_HH;
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            case R.id.tvTabNursingHome:

                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));


                if (checkInternetConnection.isConnectedToInternet()){
                    DATA.selectedTabFromMain = MainActivityNew.TAB_NH;
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;

            case R.id.tvTabAll:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));


                if (checkInternetConnection.isConnectedToInternet()){
                    DATA.selectedTabFromMain = MainActivityNew.TAB_ALL;
                    getPatientBycategory();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;
            default:
                break;
        }
    }

    //ArrayList<MyAppointmentsModel> allAppointmentsOrig;
    private void getPatientBycategory() {

        RequestParams params = new RequestParams();
        params.put("doctor_id", DATA.selectedDrId);
        params.put("patient_category", DATA.selectedTabFromMain);

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_BY_CATEGORY,"post",params,this, activity);
        apiManager.loadURL();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
        DATA.selectedUserCallId = DATA.allAppointments.get(position).id;
        DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name;
        DATA.selectedUserCallSympTom = DATA.allAppointments.get(position).symptom_name;
        DATA.selectedUserCallCondition = DATA.allAppointments.get(position).condition_name;
        DATA.selectedUserCallDescription = DATA.allAppointments.get(position).description;
        DATA.selectedUserAppntID = DATA.allAppointments.get(position).liveCheckupId;

        DATA.selectedUserLatitude =  DATA.allAppointments.get(position).latitude;
        DATA.selectedUserLongitude =  DATA.allAppointments.get(position).longitude;

        DATA.selectedUserCallImage = DATA.allAppointments.get(position).image;

        //filterReports(DATA.selectedUserCallId);
        DATA.allReportsFiltered = DATA.allAppointments.get(position).sharedReports;

        DATA.isFromDocToDoc = false;

        DATA.selectedRefferedLiveCare = DATA.allAppointments.get(position);

        Intent i = new Intent(activity, ActivityTcmDetails.class);
        startActivity(i);
        //activity.finish();
    }

    MyAppointmentsAdapter myAppointmentsAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENT_BY_CATEGORY)){
            try {

                JSONObject jsonObject = new JSONObject(content);
                JSONArray appointmentsArray = jsonObject.getJSONArray("livecheckups");

                        if (appointmentsArray.length() == 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }

                DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
                //allAppointmentsOrig = new ArrayList<MyAppointmentsModel>();
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

                    temp.patient_category = appointmentsArray.getJSONObject(i).getString("patient_category");

                    temp.is_online = appointmentsArray.getJSONObject(i).getString("is_online");

                    String additional_data = "";
                    if(appointmentsArray.getJSONObject(i).has("additional_data")){
                        additional_data = appointmentsArray.getJSONObject(i).getString("additional_data");
                    }
                    temp.pain_where = "None";
                    temp.pain_severity = "None";
                    if(!additional_data.isEmpty()){
                        JSONObject additional_dataJSON = new JSONObject(additional_data);
                        if(additional_dataJSON.has("pain_where")){
                            temp.pain_where = additional_dataJSON.getString("pain_where");
                        }
                        if(additional_dataJSON.has("pain_severity")){
                            temp.pain_severity = additional_dataJSON.getString("pain_severity");
                        }
                    }
                    if(appointmentsArray.getJSONObject(i).has("symptom_details")){
                        temp.symptom_details = appointmentsArray.getJSONObject(i).getString("symptom_details");
                    }

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


                    //allAppointmentsOrig.add(temp);
                    //if (temp.patient_category.equals("tcm")){
                    DATA.allAppointments.add(temp);
                    //}

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

                myAppointmentsAdapter = new MyAppointmentsAdapter(activity);
                lvTCM.setAdapter(myAppointmentsAdapter);


            } catch (JSONException e) {
                DATA.print("--online care exception in getlivecare patients: "+e);
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                e.printStackTrace();
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

            if(usertype.equalsIgnoreCase("patient")){
                for (int i = 0; i < DATA.allAppointments.size(); i++) {
                    if(DATA.allAppointments.get(i).id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            DATA.allAppointments.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            DATA.allAppointments.get(i).is_online = "0";
                        }
                    }
                }
                myAppointmentsAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
