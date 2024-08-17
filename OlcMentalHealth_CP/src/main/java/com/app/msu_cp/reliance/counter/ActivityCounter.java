package com.app.msu_cp.reliance.counter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.careplan.IcdCodeBean;
import com.app.msu_cp.careplan.IcdCodesAdapter;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityCounter extends BaseActivity {


    Button btnTabFalls,btnTabHospAdm,btnTabEmergRoom,btnTabNursingHome, btnAddFall,btnAddHospAdm,btnAddEmergRoom,
            btnFallChart,btnHospAdmChart,btnEmergRoomChart,btnNursingHomeChart,btnAddNursingHome;
    ViewFlipper vfCounter;
    ListView lvFallList,lvHospAdmList,lvEmergRoomList, lvNursingHomeList;
    TextView tvNoFallData,tvNoHospAdmData,tvNoEmergRoomData,tvNoNursingHomeData;

    int selectedChild = 0;


    public static int counterFlag = 1;//1=Fall, 2=Hospital Admission, 3=Nursing Home, 4=Emergency Room

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Encounter");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnTabFalls = findViewById(R.id.btnTabFalls);
        btnTabHospAdm = findViewById(R.id.btnTabHospAdm);
        btnTabEmergRoom = findViewById(R.id.btnTabEmergRoom);
        btnAddFall = findViewById(R.id.btnAddFall);
        btnAddHospAdm = findViewById(R.id.btnAddHospAdm);
        btnAddEmergRoom = findViewById(R.id.btnAddEmergRoom);

        btnAddNursingHome = findViewById(R.id.btnAddNursingHome);
        btnNursingHomeChart = findViewById(R.id.btnNursingHomeChart);
        btnTabNursingHome = findViewById(R.id.btnTabNursingHome);
        lvNursingHomeList = findViewById(R.id.lvNursingHomeList);
        tvNoNursingHomeData = findViewById(R.id.tvNoNursingHomeData);

        vfCounter = findViewById(R.id.vfCounter);
        lvFallList = findViewById(R.id.lvFallList);
        lvHospAdmList = findViewById(R.id.lvHospAdmList);
        lvEmergRoomList = findViewById(R.id.lvEmergRoomList);;
        tvNoFallData = findViewById(R.id.tvNoFallData);
        tvNoHospAdmData = findViewById(R.id.tvNoHospAdmData);
        tvNoEmergRoomData = findViewById(R.id.tvNoEmergRoomData);

        btnFallChart = findViewById(R.id.btnFallChart);
        btnHospAdmChart = findViewById(R.id.btnHospAdmChart);
        btnEmergRoomChart = findViewById(R.id.btnEmergRoomChart);

        /*lvDastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                selectedDastListBean = dastListBeans.get(position);
                Intent intent = new Intent(activity,ActivityDAST_Form.class);
                intent.putExtra("isEdit",true);
                startActivity(intent);
            }
        });*/

        /*setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Network Drug Abuse Screening Test (DAST-10)");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityDAST_Form.class, false);
            }
        });*/



        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnTabFalls:
                        btnTabFalls.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabFalls.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabEmergRoom.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabEmergRoom.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabNursingHome.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabNursingHome.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 0;
                        if (selectedChild > vfCounter.getDisplayedChild()) {

                            vfCounter.setInAnimation(activity, R.anim.in_right);
                            vfCounter.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfCounter.setInAnimation(activity, R.anim.in_left);
                            vfCounter.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfCounter.getDisplayedChild() != selectedChild) {
                            vfCounter.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnTabHospAdm:
                        btnTabHospAdm.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabHospAdm.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabFalls.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabFalls.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabEmergRoom.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabEmergRoom.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabNursingHome.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabNursingHome.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 1;
                        if (selectedChild > vfCounter.getDisplayedChild()) {

                            vfCounter.setInAnimation(activity, R.anim.in_right);
                            vfCounter.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfCounter.setInAnimation(activity, R.anim.in_left);
                            vfCounter.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfCounter.getDisplayedChild() != selectedChild) {
                            vfCounter.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnTabNursingHome:
                        btnTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabNursingHome.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabFalls.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabFalls.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabEmergRoom.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabEmergRoom.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 2;
                        if (selectedChild > vfCounter.getDisplayedChild()) {

                            vfCounter.setInAnimation(activity, R.anim.in_right);
                            vfCounter.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfCounter.setInAnimation(activity, R.anim.in_left);
                            vfCounter.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfCounter.getDisplayedChild() != selectedChild) {
                            vfCounter.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnTabEmergRoom:
                        btnTabEmergRoom.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabEmergRoom.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabHospAdm.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabFalls.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabFalls.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabNursingHome.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabNursingHome.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 3;
                        if (selectedChild > vfCounter.getDisplayedChild()) {

                            vfCounter.setInAnimation(activity, R.anim.in_right);
                            vfCounter.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfCounter.setInAnimation(activity, R.anim.in_left);
                            vfCounter.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfCounter.getDisplayedChild() != selectedChild) {
                            vfCounter.setDisplayedChild(selectedChild);
                        }
                        break;


                    case  R.id.btnAddFall:
                        showAddFallDialog();
                        break;
                    case  R.id.btnAddHospAdm:
                        showAddHospAdmDialog();
                        break;
                    case  R.id.btnAddEmergRoom:
                        showAddEmergRoomDialog();
                        break;
                    case  R.id.btnAddNursingHome:
                        showAddNursingHomeDialog();
                        break;
                    case R.id.btnFallChart:

                        String chartURL = DATA.baseUrl + "assessments/fall_chart_data/"+DATA.selectedUserCallId+"?platform=mobile";
                        new GloabalMethods(activity).showWebviewDialog(chartURL, "Encounter - Falls");

                        //new GloabalMethods(activity).showWebviewDialog("https://onlinecare.com/onlinecare_newdesign/chart.html", "Chart Test");
                        break;
                    case R.id.btnHospAdmChart:
                        String chartURL2 = DATA.baseUrl + "assessments/hospital_admission_chart_data/"+DATA.selectedUserCallId+"?platform=mobile";
                        new GloabalMethods(activity).showWebviewDialog(chartURL2, "Encounter - Hospital Admission");
                        break;
                    case R.id.btnEmergRoomChart:
                        String chartURL3 = DATA.baseUrl + "assessments/emergency_room_chart_data/"+DATA.selectedUserCallId+"?platform=mobile";
                        new GloabalMethods(activity).showWebviewDialog(chartURL3, "Encounter - Emergency Room");
                        break;

                    case R.id.btnNursingHomeChart:
                        String chartURL4 = DATA.baseUrl + "counter/nursing_home_hospital_admission_chart_data/"+DATA.selectedUserCallId+"?platform=mobile";
                        new GloabalMethods(activity).showWebviewDialog(chartURL4, "Encounter - Nursing Home");
                        break;

                    default:

                        break;
                }
            }
        };
        btnTabFalls.setOnClickListener(onClickListener);
        btnTabHospAdm.setOnClickListener(onClickListener);
        btnTabEmergRoom.setOnClickListener(onClickListener);
        btnAddFall.setOnClickListener(onClickListener);
        btnAddHospAdm.setOnClickListener(onClickListener);
        btnAddEmergRoom.setOnClickListener(onClickListener);
        btnFallChart.setOnClickListener(onClickListener);
        btnHospAdmChart.setOnClickListener(onClickListener);
        btnEmergRoomChart.setOnClickListener(onClickListener);

        btnAddNursingHome.setOnClickListener(onClickListener);
        btnNursingHomeChart.setOnClickListener(onClickListener);
        btnTabNursingHome.setOnClickListener(onClickListener);



        loadFallListData();
        //note : this data was loading one by one in fetchdata callback. but now we need to load data symaltaniously
        //b/c come from popup directly to any tab
        loadHospAdmData(false);
        loadNursingHomeData(false);
        loadEmergRoomData(false);


        if(counterFlag == 1){
            btnTabFalls.performClick();
        }else if(counterFlag == 2){
            btnTabHospAdm.performClick();
        }else if(counterFlag == 3){
            btnTabNursingHome.performClick();
        }else if(counterFlag == 4){
            btnTabEmergRoom.performClick();
        }
    }

    public void loadFallListData(){
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.FALL_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }
    public void loadHospAdmData(boolean showLoader){
        ApiManager.shouldShowPD = showLoader;
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.HOSP_ADM_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }
    public void loadEmergRoomData(boolean showLoader){
        ApiManager.shouldShowPD = showLoader;
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.EMERG_ROOM_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }
    public void loadNursingHomeData(boolean showLoader){
        ApiManager.shouldShowPD = showLoader;
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.NURSING_HOME_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<FallListBean> fallListBeans;
    ArrayList<HospAdmListBean> hospAdmListBeans;
    ArrayList<EmergRoomListBean> emergRoomListBeans;
    ArrayList<NursingHomeListBean> nursingHomeListBeans;
    List<CounterHospBean> counterHospBeans;
    //public static DastListBean selectedDastListBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.FALL_LIST)){

            //loadHospAdmData(false);

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                int vis = data.length() > 0 ? View.GONE : View.VISIBLE;
                tvNoFallData.setVisibility(vis);

                Type listType = new TypeToken<ArrayList<FallListBean>>() {}.getType();
                fallListBeans = new Gson().fromJson(data.toString(), listType);

                lvFallList.setAdapter(new LvFallListAdapter(activity, fallListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if (apiName.equalsIgnoreCase(ApiManager.HOSP_ADM_LIST)){

            //loadNursingHomeData(false);

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                int vis = data.length() > 0 ? View.GONE : View.VISIBLE;
                tvNoHospAdmData.setVisibility(vis);

                Type listType = new TypeToken<ArrayList<HospAdmListBean>>() {}.getType();
                hospAdmListBeans = new Gson().fromJson(data.toString(), listType);

                lvHospAdmList.setAdapter(new HospAdmAdapter(activity, hospAdmListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if (apiName.equalsIgnoreCase(ApiManager.NURSING_HOME_LIST)){
            //loadEmergRoomData(false);
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                int vis = data.length() > 0 ? View.GONE : View.VISIBLE;
                tvNoNursingHomeData.setVisibility(vis);

                Type listType = new TypeToken<ArrayList<NursingHomeListBean>>() {}.getType();
                nursingHomeListBeans = new Gson().fromJson(data.toString(), listType);

                lvNursingHomeList.setAdapter(new NursingHomeAdapter(activity, nursingHomeListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if (apiName.equalsIgnoreCase(ApiManager.EMERG_ROOM_LIST)){

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                int vis = data.length() > 0 ? View.GONE : View.VISIBLE;
                tvNoEmergRoomData.setVisibility(vis);

                Type listType = new TypeToken<ArrayList<EmergRoomListBean>>() {}.getType();
                emergRoomListBeans = new Gson().fromJson(data.toString(), listType);

                lvEmergRoomList.setAdapter(new LvEmergRoomListAdapter(activity, emergRoomListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_FALL)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Information saved successfully.")
                                .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogForDismiss != null){
                        dialogForDismiss.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadFallListData();
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_HOSP_ADM)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Information saved successfully.")
                        .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogForDismiss != null){
                        dialogForDismiss.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadHospAdmData(true);
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_NURSING_HOME)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Information saved successfully.")
                        .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogForDismiss != null){
                        dialogForDismiss.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadNursingHomeData(true);
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_EMERG_ROOM)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Information saved successfully.")
                        .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogForDismiss != null){
                        dialogForDismiss.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadEmergRoomData(true);
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SAVE_ENC_HOSP)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Information saved successfully.")
                        .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogAddHosp != null){
                        dialogAddHosp.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            loadHospitals(false);
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }

        else if(apiName.equalsIgnoreCase(ApiManager.GET_ICD_CODES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<IcdCodeBean>>() {}.getType();
                List<IcdCodeBean> icdCodeBeans = gson.fromJson(data.toString(), listType);

                /*List<IcdCodeBean> icdCodeBeans = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    icdCodeBeans.add(gson.fromJson(data.getJSONObject(i)+"", IcdCodeBean.class));
                }*/

                pbAutoComplete.setVisibility(View.GONE);
                if(icdCodeBeans != null){
                    //IMPORTANT: set data here and notify
                    icdCodesAdapter.setData(icdCodeBeans);
                    icdCodesAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_ENC_HOSP)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<CounterHospBean>>() {}.getType();
                counterHospBeans = gson.fromJson(data.toString(), listType);

                if(counterHospBeans!=null && spHospAdmHospital!=null){
                    ArrayAdapter<CounterHospBean> spHospAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, counterHospBeans);
                    spHospAdmHospital.setAdapter(spHospAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    Dialog dialogForDismiss = null;
    public void showAddFallDialog(){
        Dialog dialogAddFall = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddFall.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddFall.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddFall.setContentView(R.layout.dialog_add_fall);

        ImageView ivCancel = (ImageView) dialogAddFall.findViewById(R.id.ivCancel);
        Button btnAddFallD = (Button) dialogAddFall.findViewById(R.id.btnAddFall);

        EditText etDateofFall = (EditText) dialogAddFall.findViewById(R.id.etDateofFall);
        RadioGroup rgAddFallInjury = dialogAddFall.findViewById(R.id.rgAddFallInjury);
        EditText etAddFallDescription = (EditText) dialogAddFall.findViewById(R.id.etAddFallDescription);

        LinearLayout layInjuryOpt = dialogAddFall.findViewById(R.id.layInjuryOpt);
        Spinner spAddFallMediCare = dialogAddFall.findViewById(R.id.spAddFallMediCare);
        RadioGroup rgAddFallInjuryResult = dialogAddFall.findViewById(R.id.rgAddFallInjuryResult);

        String[] medicalCareArr = {"Physician Office", "Physician Virtual Visit", "Urgent Care", "Emergency Room", "No treatment per choice"};
        ArrayAdapter<String> spMediCareAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, medicalCareArr);
        spAddFallMediCare.setAdapter(spMediCareAdapter);

        rgAddFallInjury.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int vis = checkedId == R.id.rbAddFallInjuryYes ? View.VISIBLE:View.GONE;
                layInjuryOpt.setVisibility(vis);
            }
        });

        etDateofFall.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDateofFall);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });


        btnAddFallD.setOnClickListener(v -> {


            String fall_date = etDateofFall.getText().toString().trim();
            String is_injury = rgAddFallInjury.getCheckedRadioButtonId() == R.id.rbAddFallInjuryYes ? "1" : "0";
            String description = etAddFallDescription.getText().toString().trim();

            if(fall_date.isEmpty()){
                etDateofFall.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                return;
            }
            if(description.isEmpty()){
                etAddFallDescription.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                return;
            }


            String medical_care = medicalCareArr[spAddFallMediCare.getSelectedItemPosition()];
            String injury_result = rgAddFallInjuryResult.getCheckedRadioButtonId() == R.id.rbAddFallInjuryResultYes ? "1" : "0";


            RequestParams params = new RequestParams();
            params.put("fall_date", fall_date);
            params.put("is_injury",is_injury);
            params.put("description",description);

            params.add("medical_care", medical_care);
            params.put("injury_result", injury_result);


            params.put("patient_id", DATA.selectedUserCallId);
            params.put("author_id", prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_FALL,"post",params, apiCallBack, activity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddFall.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddFall.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddFall.setCanceledOnTouchOutside(false);
        dialogAddFall.show();
        dialogAddFall.getWindow().setAttributes(lp);

        dialogForDismiss = dialogAddFall;
    }


    ProgressBar pbAutoComplete;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
    private Handler handler;
    private IcdCodesAdapter icdCodesAdapter;

    Spinner spHospAdmHospital;

    public void showAddHospAdmDialog(){
        Dialog dialogAddAllergies = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddAllergies.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddAllergies.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddAllergies.setContentView(R.layout.dialog_add_hosp_adm);

        ImageView ivCancel = (ImageView) dialogAddAllergies.findViewById(R.id.ivCancel);
        Button btnAddHospAdmD = (Button) dialogAddAllergies.findViewById(R.id.btnAddHospAdm);

        EditText etAddHospAdmDate = (EditText) dialogAddAllergies.findViewById(R.id.etAddHospAdmDate);
        EditText etHospAdmAdmit = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmAdmit);
        EditText etHospAdmDischarge = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmDischarge);
        //EditText etHospAdmHospital = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmHospital);
        EditText etHospAdmDiagnosis = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmDiagnosis);
        EditText etHospAdmDescription = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmDescription);
        EditText etHospAdmDeschSumm = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmDeschSumm);

        Spinner spHospAdmHospital = dialogAddAllergies.findViewById(R.id.spHospAdmHospital);
        this.spHospAdmHospital = spHospAdmHospital;

        Button btnAddHosp = dialogAddAllergies.findViewById(R.id.btnAddHosp);
        btnAddHosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHospAdmHospDialog();
            }
        });

        etAddHospAdmDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddHospAdmDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });

        etHospAdmAdmit.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etHospAdmAdmit);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });

        etHospAdmDischarge.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etHospAdmDischarge);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });



        //===============AutoComplete========================
        pbAutoComplete = (ProgressBar) dialogAddAllergies.findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final AppCompatAutoCompleteTextView autoCompleteTextView = (AppCompatAutoCompleteTextView) dialogAddAllergies.findViewById(R.id.auto_complete_edit_text);

        //Setting up the adapter for AutoSuggest
        icdCodesAdapter = new IcdCodesAdapter(activity, android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(icdCodesAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        String diagnosis = icdCodesAdapter.getObject(position).desc+ " ("+icdCodesAdapter.getObject(position).code+")";

                        String diag = etHospAdmDiagnosis.getText().toString();
                        if(TextUtils.isEmpty(diag)){
                            diag = diagnosis;
                        }else {
                            diag = diag + "\n"+diagnosis;//,
                        }
                        etHospAdmDiagnosis.setText(diag);

                        autoCompleteTextView.setText("");

                        //hideShowKeypad.hideSoftKeyboard();
                        hideShowKeypad.hidekeyboardOnDialog();

                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        pbAutoComplete.setVisibility(View.VISIBLE);
                        RequestParams params = new RequestParams();
                        params.put("keyword", autoCompleteTextView.getText().toString());
                        ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES,"post",params, apiCallBack, activity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    }
                }
                return false;
            }
        });


        btnAddHospAdmD.setOnClickListener(v -> {


            String dateof = etAddHospAdmDate.getText().toString().trim();
            String admit = etHospAdmAdmit.getText().toString().trim();
            String discharge = etHospAdmDischarge.getText().toString().trim();
            String diagnosis = etHospAdmDiagnosis.getText().toString().trim();
            String description = etHospAdmDescription.getText().toString().trim();

            String hospital = "-";//etHospAdmHospital.getText().toString().trim();
            if(spHospAdmHospital!= null && counterHospBeans != null){
                hospital = counterHospBeans.get(spHospAdmHospital.getSelectedItemPosition()).hospital_name;
            }

            String discharge_summary = etHospAdmDeschSumm.getText().toString().trim();

            boolean validatedEmpty = true;
            if(dateof.isEmpty()){
                etAddHospAdmDate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(admit.isEmpty()){
                etHospAdmAdmit.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(discharge.isEmpty()){
                etHospAdmDischarge.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            /*if(hospital.isEmpty()){
                etHospAdmHospital.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }*/
            if(diagnosis.isEmpty()){
                etHospAdmDiagnosis.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(description.isEmpty()){
                etHospAdmDescription.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(discharge_summary.isEmpty()){
                etHospAdmDeschSumm.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }

            if(! validatedEmpty){
                return;
            }

            RequestParams params = new RequestParams();
            params.put("dateof", dateof);
            params.put("admit",admit);
            params.put("discharge",discharge);
            params.put("hospital", hospital);
            params.put("diagnosis",diagnosis);
            params.put("description",description);
            params.put("discharge_summary",discharge_summary);

            params.put("patient_id", DATA.selectedUserCallId);
            params.put("author_id", prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_HOSP_ADM,"post",params, apiCallBack, activity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddAllergies.dismiss());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddAllergies.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddAllergies.setCanceledOnTouchOutside(false);
        dialogAddAllergies.show();
        dialogAddAllergies.getWindow().setAttributes(lp);

        dialogForDismiss = dialogAddAllergies;


        loadHospitals(true);
    }


    Dialog dialogAddHosp = null;
    public void showAddHospAdmHospDialog(){
        Dialog dialogAddAllergies = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddAllergies.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddAllergies.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddAllergies.setContentView(R.layout.dialog_add_hosp_adm_hosp);

        ImageView ivCancel = (ImageView) dialogAddAllergies.findViewById(R.id.ivCancel);
        Button btnAddHospAdmD = (Button) dialogAddAllergies.findViewById(R.id.btnAddHospAdm);

        EditText etHospAdmHospitalName = (EditText) dialogAddAllergies.findViewById(R.id.etHospAdmHospitalName);



        btnAddHospAdmD.setOnClickListener(v -> {


            String hospital_name = etHospAdmHospitalName.getText().toString().trim();

            boolean validatedEmpty = true;
            if(hospital_name.isEmpty()){
                etHospAdmHospitalName.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }


            if(! validatedEmpty){
                return;
            }

            RequestParams params = new RequestParams();
            params.put("hospital_name", hospital_name);

            //params.put("patient_id", DATA.selectedUserCallId);
            //params.put("author_id", prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.SAVE_ENC_HOSP,"post",params, apiCallBack, activity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddAllergies.dismiss());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddAllergies.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddAllergies.setCanceledOnTouchOutside(false);
        dialogAddAllergies.show();
        dialogAddAllergies.getWindow().setAttributes(lp);

        dialogAddHosp = dialogAddAllergies;
    }

    public void loadHospitals(boolean showLoader){
        ApiManager.shouldShowPD = showLoader;
        ApiManager apiManager = new ApiManager(ApiManager.GET_ENC_HOSP,"post",null, apiCallBack, activity);
        apiManager.loadURL();
    }


    public void showAddNursingHomeDialog(){
        Dialog dialogAddAllergies = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddAllergies.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddAllergies.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddAllergies.setContentView(R.layout.dialog_add_nursing_home);

        ImageView ivCancel = (ImageView) dialogAddAllergies.findViewById(R.id.ivCancel);
        Button btnAddNH = (Button) dialogAddAllergies.findViewById(R.id.btnAddNH);

        EditText etAddNH_NOF = (EditText) dialogAddAllergies.findViewById(R.id.etAddNH_NOF);
        EditText etAddNHAdmitDate = (EditText) dialogAddAllergies.findViewById(R.id.etAddNHAdmitDate);
        EditText etAddNHDischDate = (EditText) dialogAddAllergies.findViewById(R.id.etAddNHDischDate);
        EditText etAddNH_DeschSumm = (EditText) dialogAddAllergies.findViewById(R.id.etAddNH_DeschSumm);
        EditText etAddNH_Diagnosis = (EditText) dialogAddAllergies.findViewById(R.id.etAddNH_Diagnosis);

        EditText etAddNHDate = dialogAddAllergies.findViewById(R.id.etAddNHDate);
        EditText etAddNH_AdmSumm = dialogAddAllergies.findViewById(R.id.etAddNH_AdmSumm);


        etAddNHDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddNHDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });

        etAddNHAdmitDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddNHAdmitDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });

        etAddNHDischDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddNHDischDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });



        //===============AutoComplete========================
        pbAutoComplete = (ProgressBar) dialogAddAllergies.findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final AppCompatAutoCompleteTextView autoCompleteTextView = (AppCompatAutoCompleteTextView) dialogAddAllergies.findViewById(R.id.auto_complete_edit_text);

        //Setting up the adapter for AutoSuggest
        icdCodesAdapter = new IcdCodesAdapter(activity, android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(icdCodesAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        String diagnosis = icdCodesAdapter.getObject(position).desc+ " ("+icdCodesAdapter.getObject(position).code+")";

                        String diag = etAddNH_Diagnosis.getText().toString();
                        if(TextUtils.isEmpty(diag)){
                            diag = diagnosis;
                        }else {
                            diag = diag + "\n"+diagnosis;//,
                        }
                        etAddNH_Diagnosis.setText(diag);

                        autoCompleteTextView.setText("");

                        //hideShowKeypad.hideSoftKeyboard();
                        //HideShowKeypad.hideKeyboard(activity);

                        //dialogAddAllergies.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        hideShowKeypad.hidekeyboardOnDialog();

                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        pbAutoComplete.setVisibility(View.VISIBLE);
                        RequestParams params = new RequestParams();
                        params.put("keyword", autoCompleteTextView.getText().toString());
                        ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES,"post",params, apiCallBack, activity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    }
                }
                return false;
            }
        });


        btnAddNH.setOnClickListener(v -> {

            String name_of_facility = etAddNH_NOF.getText().toString().trim();
            String admit = etAddNHAdmitDate.getText().toString().trim();
            //icd_codes:
            String diagnosis = etAddNH_Diagnosis.getText().toString().trim();
            String discharge = etAddNHDischDate.getText().toString().trim();
            String discharge_summary = etAddNH_DeschSumm.getText().toString().trim();


            String date_reported = etAddNHDate.getText().toString().trim();
            String admission_summary = etAddNH_AdmSumm.getText().toString().trim();


            boolean validatedEmpty = true;
            if(name_of_facility.isEmpty()){
                etAddNH_NOF.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(admit.isEmpty()){
                etAddNHAdmitDate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(diagnosis.isEmpty()){
                etAddNH_Diagnosis.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(discharge.isEmpty()){
                etAddNHDischDate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(discharge_summary.isEmpty()){
                etAddNH_DeschSumm.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(admission_summary.isEmpty()){
                etAddNH_AdmSumm.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }
            if(date_reported.isEmpty()){
                etAddNHDate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                //return;
                validatedEmpty = false;
            }

            if(! validatedEmpty){
                return;
            }

            RequestParams params = new RequestParams();
            params.put("name_of_facility", name_of_facility);
            params.put("admit",admit);
            params.put("diagnosis", diagnosis);
            params.put("discharge",discharge);
            params.put("discharge_summary",discharge_summary);

            params.put("admission_summary",admission_summary);
            params.put("date_reported",date_reported);


            params.put("patient_id", DATA.selectedUserCallId);
            params.put("author_id", prefs.getString("id", ""));


            ApiManager apiManager = new ApiManager(ApiManager.ADD_NURSING_HOME,"post",params, apiCallBack, activity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddAllergies.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddAllergies.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddAllergies.setCanceledOnTouchOutside(false);
        dialogAddAllergies.show();
        dialogAddAllergies.getWindow().setAttributes(lp);

        dialogForDismiss = dialogAddAllergies;
    }


    public void showAddEmergRoomDialog(){
        Dialog dialogAddEmergRoom = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddEmergRoom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddEmergRoom.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddEmergRoom.setContentView(R.layout.dialog_add_emergroom);

        ImageView ivCancel = (ImageView) dialogAddEmergRoom.findViewById(R.id.ivCancel);
        Button btnAddER = (Button) dialogAddEmergRoom.findViewById(R.id.btnAddER);

        EditText etAddERvisitDate = (EditText) dialogAddEmergRoom.findViewById(R.id.etAddERvisitDate);
        Spinner spAddERType = dialogAddEmergRoom.findViewById(R.id.spAddERType);
        EditText etAddERFacilityName = dialogAddEmergRoom.findViewById(R.id.etAddERFacilityName);
        EditText etAddERAddInfo = dialogAddEmergRoom.findViewById(R.id.etAddERAddInfo);
        RadioGroup rgAddERAdmittedObservation = dialogAddEmergRoom.findViewById(R.id.rgAddERAdmittedObservation);


        String[] typeOfER_Arr = {"Physician Office", "Physician Virtual Visit", "Urgent Care", "Emergency Room-Hospital", "Emergency Room-Satelite location"};
        ArrayAdapter<String> sptypeOfERAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, typeOfER_Arr);
        spAddERType.setAdapter(sptypeOfERAdapter);


        etAddERvisitDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddERvisitDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });


        btnAddER.setOnClickListener(v -> {


            /*trip_date: 10/07/2019
            type_of_visit: Emergency Room-Hospital
            facility_name: fac name
            admitted_observation: Hospital
            additional_info: add info
            patient_id: 51
            author_id: 191*/

            String trip_date = etAddERvisitDate.getText().toString().trim();
            String type_of_visit = typeOfER_Arr[spAddERType.getSelectedItemPosition()];
            String facility_name = etAddERFacilityName.getText().toString().trim();
            String admitted_observation = rgAddERAdmittedObservation.getCheckedRadioButtonId() == R.id.rbAddERAdmObservHosp ? "Hospital" : "None";
            String additional_info = etAddERAddInfo.getText().toString().trim();

            if(trip_date.isEmpty()){
                etAddERvisitDate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                return;
            }

            RequestParams params = new RequestParams();
            params.put("trip_date", trip_date);
            params.put("type_of_visit",type_of_visit);
            params.put("facility_name",facility_name);
            params.put("admitted_observation",admitted_observation);
            params.put("additional_info",additional_info);

            params.put("patient_id", DATA.selectedUserCallId);
            params.put("author_id", prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_EMERG_ROOM,"post",params, apiCallBack, activity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddEmergRoom.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddEmergRoom.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddEmergRoom.setCanceledOnTouchOutside(false);
        dialogAddEmergRoom.show();
        dialogAddEmergRoom.getWindow().setAttributes(lp);

        dialogForDismiss = dialogAddEmergRoom;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityBradenScale.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }*/
}
