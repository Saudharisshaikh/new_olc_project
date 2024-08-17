package com.app.omrandr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.omrandr.adapter.CallLogsAdapter2;
import com.app.omrandr.adapter.LiveCareAdapter;
import com.app.omrandr.api.ApiCallBack;
import com.app.omrandr.api.ApiManager;
import com.app.omrandr.model.CallLogBean;
import com.app.omrandr.model.MyAppointmentsModel;
import com.app.omrandr.model.ReportsModel;
import com.app.omrandr.util.CheckInternetConnection;
import com.app.omrandr.util.CustomToast;
import com.app.omrandr.util.DATA;
import com.app.omrandr.util.DialogCallDetails;
import com.app.omrandr.util.GloabalSocket;
import com.app.omrandr.util.HideShowKeypad;
import com.app.omrandr.util.OpenActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityTCM extends BaseActivity implements View.OnClickListener, ApiCallBack,GloabalSocket.SocketEmitterCallBack{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    //ViewFlipper vfTCM;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome,tvTabAll;
    ListView lvTCM;//,lvCC,lvHomeHealth,lvNursingHome;

    GloabalSocket gloabalSocket;

    EditText etFilter;


    SwipeRefreshLayout srCallLog,srTCM;
    PagingListView lvAllCalls;
    TextView tvNoCalls;
    ViewFlipper vfMyPatients;
    Button btnTabMyPatients,btnTabEmcPatients;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcm);

        activity = ActivityTCM.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        gloabalSocket = new GloabalSocket(activity,this);

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
        etFilter = (EditText) findViewById(R.id.etFilter);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);
        tvTabAll.setOnClickListener(this);

        lvTCM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                //DATA.isConfirence = false;

                DATA.selectedLiveCare = DATA.allAppointments.get(position);

                DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                Intent i = new Intent(activity, ActivityTcmDetails.class);//SelectedDoctorsProfile
                activity.startActivity(i);
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (liveCareAdapter != null) {
                    liveCareAdapter.filter(s.toString());
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



        srCallLog = findViewById(R.id.srCallLog);
        lvAllCalls = findViewById(R.id.lvAllCalls);
        tvNoCalls = findViewById(R.id.tvNoCalls);
        vfMyPatients = findViewById(R.id.vfMyPatients);
        btnTabMyPatients = findViewById(R.id.btnTabMyPatients);
        btnTabEmcPatients = findViewById(R.id.btnTabEmcPatients);

        btnTabMyPatients.setOnClickListener(this);
        btnTabEmcPatients.setOnClickListener(this);


        lvAllCalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                try {
                    //selectedBean = callLogBeans.get(position);
                    new DialogCallDetails(activity).initDetailsDialog(callLogBeans.get(position));
                }catch (Exception e){e.printStackTrace();}
            }
        });

        //===========Paging Listview starts===========================

        callLogsAdapter2 = new CallLogsAdapter2(activity,callLogBeans);
        lvAllCalls.setAdapter(callLogsAdapter2);

        lvAllCalls.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                //DATA.print("-- pagging listener called at page :"+page);
                pageNoAllCalls = pageNoAllCalls+1;
                getAllCalls(pageNoAllCalls);
            }
        });

        //=============Paging Listview Ends============================



        //======================swip to refresh==================================
        //mySwipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh);

        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        srCallLog.setColorSchemeColors(colorsArr);
        srCallLog.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("--", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        //myUpdateOperation();
                        if(!checkInternetConnection.isConnectedToInternet()){
                            srCallLog.setRefreshing(false);
                        }else {
                            //toggleViews(true);
                        }
                        getAllCalls(0);
                    }
                }
        );
        //======================swip to refresh ends=============================

        srTCM = findViewById(R.id.srTCM);
        srTCM.setColorSchemeColors(colorsArr);
        srTCM.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("--", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        //myUpdateOperation();
                        if(!checkInternetConnection.isConnectedToInternet()){
                            srTCM.setRefreshing(false);
                        }else {
                            //toggleViews(true);
                        }
                        ApiManager.shouldShowPD = false;
                        getPatientBycategory(patient_category);
                    }
                }
        );

        if (checkInternetConnection.isConnectedToInternet()){
            getPatientBycategory(patient_category);
            getAllCalls(0);
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }

    }


    int selectedChild = 0;

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

                //vfTCM.setDisplayedChild(0);
                patient_category = "tcm";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
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

                //vfTCM.setDisplayedChild(1);
                patient_category = "complex_care";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
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

                //vfTCM.setDisplayedChild(2);
                patient_category = "home_health";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
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

                //vfTCM.setDisplayedChild(3);
                patient_category = "nursing_home";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
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

                //vfTCM.setDisplayedChild(3);
                patient_category = "all";
                if (checkInternetConnection.isConnectedToInternet()){
                    getPatientBycategory(patient_category);
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }
                break;

            case R.id.btnTabMyPatients:

                btnTabMyPatients.setBackgroundColor(getResources().getColor(R.color.theme_red));
                btnTabMyPatients.setTextColor(getResources().getColor(android.R.color.white));
                btnTabEmcPatients.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnTabEmcPatients.setTextColor(getResources().getColor(R.color.theme_red));

                selectedChild = 0;
                if (selectedChild > vfMyPatients.getDisplayedChild()) {

                    vfMyPatients.setInAnimation(activity, R.anim.in_right);
                    vfMyPatients.setOutAnimation(activity, R.anim.out_left);
                } else {
                    vfMyPatients.setInAnimation(activity, R.anim.in_left);
                    vfMyPatients.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfMyPatients.getDisplayedChild() != selectedChild) {
                    vfMyPatients.setDisplayedChild(selectedChild);
                }
                break;

            case R.id.btnTabEmcPatients:
                btnTabMyPatients.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnTabMyPatients.setTextColor(getResources().getColor(R.color.theme_red));
                btnTabEmcPatients.setBackgroundColor(getResources().getColor(R.color.theme_red));
                btnTabEmcPatients.setTextColor(getResources().getColor(android.R.color.white));

                selectedChild = 1;
                if (selectedChild > vfMyPatients.getDisplayedChild()) {

                    vfMyPatients.setInAnimation(activity, R.anim.in_right);
                    vfMyPatients.setOutAnimation(activity, R.anim.out_left);
                } else {
                    vfMyPatients.setInAnimation(activity, R.anim.in_left);
                    vfMyPatients.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfMyPatients.getDisplayedChild() != selectedChild) {
                    vfMyPatients.setDisplayedChild(selectedChild);
                }
                break;

            default:
                break;
        }
    }

    String patient_category = "all";
    private void getPatientBycategory(String patient_category) {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_category", patient_category);

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_BY_CATEGORY,"post",params,this, activity);
        apiManager.loadURL();
    }


    int pageNoAllCalls = 0;
    private void getAllCalls(int pageNo) {

        pageNoAllCalls = pageNo;

        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));

        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.ALL_CALLS+"/"+pageNo ,"post",params,this, activity);
        apiManager.loadURL();
    }

    LiveCareAdapter liveCareAdapter;

    ArrayList<CallLogBean> callLogBeans = new ArrayList<>();
    CallLogsAdapter2 callLogsAdapter2;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENT_BY_CATEGORY)){
            try {
                srTCM.setRefreshing(false);

                JSONObject jsonObject = new JSONObject(content);
                JSONArray appointmentsArray = jsonObject.getJSONArray("livecheckups");

                        /*if (appointmentsArray.length() == 0) {
                            lvLiveCare.setVisibility(View.GONE);
                            tvNoLiveCares.setVisibility(View.VISIBLE);
                        } else {
                            lvLiveCare.setVisibility(View.VISIBLE);
                            tvNoLiveCares.setVisibility(View.GONE);*/

                DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
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
                    temp.pharmacy_address = appointmentsArray.getJSONObject(i).optString("pharmacy_address", "-");

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

                    DATA.allAppointments.add(temp);

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

                liveCareAdapter = new LiveCareAdapter(activity);
                lvTCM.setAdapter(liveCareAdapter);

                //}



            } catch (JSONException e) {
                DATA.print("--online care exception in getlivecare patients: "+e);
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                e.printStackTrace();
            }
        }else if(apiName.contains(ApiManager.ALL_CALLS)){
            try {

                srCallLog.setRefreshing(false);

                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<CallLogBean>>() {}.getType();
                ArrayList<CallLogBean> callLogBeansAPI = new Gson().fromJson(data.toString(), listType);

                if(callLogBeansAPI != null){
                    if(pageNoAllCalls == 0){
                        callLogBeans.clear();
                    }
                    callLogBeans.addAll(callLogBeansAPI);
                    callLogsAdapter2.notifyDataSetChanged();


                    //tell listview to load more
                    boolean view_more = !callLogBeansAPI.isEmpty();//jsonObject.optBoolean("view_more");
                    lvAllCalls.setHasMoreItems(view_more);
                    lvAllCalls.onFinishLoading(view_more, callLogBeans);
                }


                int vis = callLogBeans.isEmpty() ? View.VISIBLE : View.GONE;
                tvNoCalls.setVisibility(vis);

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

            if(usertype.equalsIgnoreCase("patient")){
                //patients listview
                for (int i = 0; i < DATA.allAppointments.size(); i++) {
                    if(DATA.allAppointments.get(i).id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            DATA.allAppointments.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            DATA.allAppointments.get(i).is_online = "0";
                        }
                    }
                }
                liveCareAdapter.notifyDataSetChanged();


                //Call logs list view
                if(callLogBeans != null){
                    for (int i = 0; i < callLogBeans.size(); i++) {
                        if(callLogBeans.get(i).getCallto().equalsIgnoreCase("patient") &&
                        callLogBeans.get(i).getPatient_id().equalsIgnoreCase(id)){

                            if(status.equalsIgnoreCase("login")){
                                callLogBeans.get(i).is_online = "1";
                            }else if(status.equalsIgnoreCase("logout")){
                                callLogBeans.get(i).is_online = "0";
                            }
                        }
                    }
                    if(callLogsAdapter2 != null){callLogsAdapter2.notifyDataSetChanged();}
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
