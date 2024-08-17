package com.app.msu_cp.reliance.mdlive;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.ActivityAddNewPatient;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.reliance.ActivityCompany;
import com.app.msu_cp.reliance.CompanyBean;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.SharedPrefsHelper;
import com.app.msu_cp.util.SpinnerCustom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DialogCompanyDoctors implements ApiCallBack {

    Activity activity;
    SharedPrefsHelper sharedPrefsHelper;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;

    public DialogCompanyDoctors(Activity activity) {
        this.activity = activity;
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        customToast = new CustomToast(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
    }


    public static ArrayList<CompanyDoctorBean> companyDoctorBeansSelected = new ArrayList<>();

    ListView lvCompanyPatients;
    TextView tvNoData;
    //SwipeRefreshLayout srPatients;

    public void showCompanyDocDialog(){
        Dialog dialogCompanyDoc = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogCompanyDoc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogCompanyDoc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogCompanyDoc.setContentView(R.layout.dialog_company_doc);

        ImageView ivCancel = (ImageView) dialogCompanyDoc.findViewById(R.id.ivCancel);
        Button btnSelectDocDone = (Button) dialogCompanyDoc.findViewById(R.id.btnSelectDocDone);

        lvCompanyPatients = dialogCompanyDoc.findViewById(R.id.lvCompanyPatients);
        tvNoData = dialogCompanyDoc.findViewById(R.id.tvNoData);
        //srPatients = dialogCompanyDoc.findViewById(R.id.srPatients);
        SpinnerCustom spCompany = dialogCompanyDoc.findViewById(R.id.spCompany);

        ivCancel.setOnClickListener(v -> dialogCompanyDoc.dismiss());


        lvCompanyPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    companyDoctorBeans.get(position).isSelected = ! companyDoctorBeans.get(position).isSelected;
                    companyDoctorDialogAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnSelectDocDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyDoctorBeansSelected.clear();
                if(companyDoctorBeans != null){
                    for (int i = 0; i < companyDoctorBeans.size(); i++) {
                        if(companyDoctorBeans.get(i).isSelected){
                            companyDoctorBeansSelected.add(companyDoctorBeans.get(i));
                        }
                    }
                }
                dialogCompanyDoc.dismiss();
                if(activity instanceof ActivityAddNewPatient){
                    ((ActivityAddNewPatient) activity).setupMultiDocField();
                }
            }
        });

        //=====================swipe to refresh========================================
        /*int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        srPatients.setColorSchemeColors(colorsArr);
        srPatients.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("--", "onRefresh called from SwipeRefreshLayout");
                        if(!checkInternetConnection.isConnectedToInternet()){
                            srPatients.setRefreshing(false);
                        }else {//toggleViews(true);

                        }
                        loadAndShowData();
                    }
                }
        );*/
        //=======================swipe to refresh=====================================


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCompanyDoc.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogCompanyDoc.setCanceledOnTouchOutside(false);
        dialogCompanyDoc.show();
        dialogCompanyDoc.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogCompanyDoc;




        //load doc according to company
        ArrayAdapter<CompanyBean> spCompanyAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, companyBeans);
        spCompany.setAdapter(spCompanyAdapter);

        if(companyBeans.isEmpty()){selectedCompanyID = "";}//reset selected comp_id if list empty

        spCompany.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3, boolean isUserSelect) {
                // TODO Auto-generated method stub
                //if(isUserSelect){}
                selectedCompanyID = companyBeans.get(pos).company_id;
                loadDoctorsByCompany(selectedCompanyID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }






    public void loadAndShowData(){
        String chechedData = sharedPrefsHelper.get(ActivityCompany.COMPANY_PREFS_KEY, "");
        if(TextUtils.isEmpty(chechedData)){
            loadCompany();
        }else {
            parseCompanyData(chechedData);

            new GloabalMethods(activity).getCompany();//companies in which CP assigned
        }
    }



    public void loadCompany(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        ApiManager apiManager = new ApiManager(ApiManager.GET_COMPANIES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void loadDoctorsByCompany(String compamyId){
        RequestParams params = new RequestParams();
        params.put("company_id", compamyId);

        ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTORS_BY_COMPANY,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    ArrayList<CompanyBean> companyBeans;
    ArrayList<CompanyDoctorBean> companyDoctorBeans;
    public String selectedCompanyID = "";//static
    CompanyDoctorDialogAdapter companyDoctorDialogAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {

        if(apiName.equalsIgnoreCase(ApiManager.GET_COMPANIES)){
            parseCompanyData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_DOCTORS_BY_COMPANY)){
            try {
                //srPatients.setRefreshing(false);

                JSONObject jsonObject = new JSONObject(content);
                JSONArray doctors = jsonObject.getJSONArray("doctors");
                Type listType = new TypeToken<ArrayList<CompanyDoctorBean>>() {}.getType();
                companyDoctorBeans = new Gson().fromJson(doctors+"", listType);

                if(companyDoctorBeans != null){
                    int vis = companyDoctorBeans.isEmpty() ? View.VISIBLE : View.GONE;
                    tvNoData.setVisibility(vis);
                    companyDoctorDialogAdapter = new CompanyDoctorDialogAdapter(activity, companyDoctorBeans);
                    lvCompanyPatients.setAdapter(companyDoctorDialogAdapter);
                }

                //filterAndShowData(1);

            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }

    public void parseCompanyData(String content){

        try {
            JSONObject jsonObject = new JSONObject(content);

            JSONArray companies = jsonObject.getJSONArray("companies");
            Type listType = new TypeToken<ArrayList<CompanyBean>>() {}.getType();
            companyBeans = new Gson().fromJson(companies+"", listType);

            if(companyBeans != null){
                //lvCompanyPatients.setAdapter(new CompanyPatientAdapter(activity, companyPatientBeans));

                sharedPrefsHelper.save(ActivityCompany.COMPANY_PREFS_KEY, content);//also save here to refresh data in local

                showCompanyDocDialog();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }

    }
}
