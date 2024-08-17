package com.app.onlinecare_dr_pk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.app.onlinecare_dr_pk.adapter.CovidBillingCodesAdapter;
import com.app.onlinecare_dr_pk.adapter.CovidListAdapter;
import com.app.onlinecare_dr_pk.api.ApiManager;
import com.app.onlinecare_dr_pk.model.CovidBillingCodeBean;
import com.app.onlinecare_dr_pk.model.CovidFormListBean;
import com.app.onlinecare_dr_pk.model.CovidTestLocationBean;
import com.app.onlinecare_dr_pk.util.DATA;
import com.app.onlinecare_dr_pk.util.Database;
import com.app.onlinecare_dr_pk.util.DatePickerFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityCovidFormList extends BaseActivity{


    ListView lvCovidList;
    TextView tvNoData;
    EditText etSearch;

    Spinner spTestLocation;
    EditText etDateFilter;
    ImageView ivClearDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Covid Testing");
        }

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_COVID_REQ);

        lvCovidList = findViewById(R.id.lvCovidList);
        tvNoData = findViewById(R.id.tvNoData);

        spTestLocation = findViewById(R.id.spTestLocation);
        etDateFilter = findViewById(R.id.etDateFilter);
        ivClearDate = findViewById(R.id.ivClearDate);

        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(covidListAdapter != null){
                    covidListAdapter.filter(s.toString());
                }
            }
        });

        /*lvCovidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view/"+covidFormListBeans.get(position).id+"?platform=mobile" , "Covid Testing");
            }
        });*/


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.etDateFilter:
                        DialogFragment newFragment = new DatePickerFragment(etDateFilter);
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                        break;
                    case R.id.ivClearDate:
                        if(!TextUtils.isEmpty(etDateFilter.getText().toString().trim())){
                            etDateFilter.setText("");
                            loadListData();
                        }
                        break;
                }
            }
        };

        etDateFilter.setOnClickListener(onClickListener);
        ivClearDate.setOnClickListener(onClickListener);


        spTestLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadListData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        String chechedData = sharedPrefsHelper.get("covid_test_locations", "");
        if(!TextUtils.isEmpty(chechedData)){
            parseTestLocationsData(chechedData);
            ApiManager.shouldShowPD = false;
        }
        ApiManager apiManager = new ApiManager(ApiManager.COVID_TEST_LOCATIONS,"get",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void loadListData(){

        String location_id = "";
        if(covidTestLocationBeans != null){
            location_id = spTestLocation.getSelectedItemPosition() == 0 ? "" : covidTestLocationBeans.get(spTestLocation.getSelectedItemPosition()).id;
        }
        String date = etDateFilter.getText().toString().trim();

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        if(!TextUtils.isEmpty(location_id)){params.put("location_id", location_id);}
        if(! TextUtils.isEmpty(date)){params.put("date", date);}
        ApiManager apiManager = new ApiManager(ApiManager.COVID_FORMS_LIST,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void loadCovidBillingCodes(){
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.COVID_BILLING_CODES,"get", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<CovidFormListBean> covidFormListBeans;
    CovidListAdapter covidListAdapter;

    List<CovidTestLocationBean> covidTestLocationBeans;
    ArrayAdapter<CovidTestLocationBean> spTestLocationsAdapter;

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.COVID_FORMS_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<CovidFormListBean>>() {}.getType();
                covidFormListBeans = new Gson().fromJson(data.toString(), listType);

                covidListAdapter = new CovidListAdapter(activity, covidFormListBeans);
                lvCovidList.setAdapter(covidListAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CHANGE_COVID_FORM_STATUS) || apiName.equalsIgnoreCase(ApiManager.COVID_SEND_RESULTS) || apiName.equalsIgnoreCase(ApiManager.COVID_SAVE_VITALS)){
            //{"status":"success","message":"Saved successfully."}
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle("Info")
                        .setMessage(message)
                        .setPositiveButton("Done", null)
                        .create();
                if(status.equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(dialogCovidActionToDismiss != null){
                                dialogCovidActionToDismiss.dismiss();
                            }
                            if(dialogCovidBillingToDismiss != null){
                                dialogCovidBillingToDismiss.dismiss();
                            }
                            if(dialogCovidResultsToDismiss != null){
                                dialogCovidResultsToDismiss.dismiss();
                            }
                            if(dialogCovidAddVitalsToDismiss != null){
                                dialogCovidAddVitalsToDismiss.dismiss();
                            }
                            loadListData();
                        }
                    });
                }
                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }else if (apiName.contains(ApiManager.COVID_BILLING_CODES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<CovidBillingCodeBean>>() {}.getType();
                covidBillingCodeBeans = gson.fromJson(data.toString(), listType);

                covidBillingCodesAdapter = new CovidBillingCodesAdapter(activity, covidBillingCodeBeans);
                lvBillingCodes.setAdapter(covidBillingCodesAdapter);

            } catch (Exception e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.COVID_TEST_LOCATIONS)){
            parseTestLocationsData(content);
        }
    }


    public void parseTestLocationsData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray data = jsonObject.getJSONArray("data");

            if(spTestLocationsAdapter == null){
                Type listType = new TypeToken<ArrayList<CovidTestLocationBean>>() {}.getType();
                covidTestLocationBeans = new Gson().fromJson(data.toString(), listType);
                covidTestLocationBeans.add(0, new CovidTestLocationBean("", "All Locations"));
                spTestLocationsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, covidTestLocationBeans);
                spTestLocation.setAdapter(spTestLocationsAdapter);
            }

            sharedPrefsHelper.save("covid_test_locations", content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }


    Dialog dialogCovidActionToDismiss;
    public void showCovidActionDialog(int listPos){
        final Dialog dialogCovidAction = new Dialog(activity, R.style.TransparentThemeH4B);//, R.style.TransparentThemeH4B
        dialogCovidAction.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCovidAction.setContentView(R.layout.dialog_covid_action);//dialog_asses_saved_old
        dialogCovidAction.setCanceledOnTouchOutside(false);

        RadioGroup rgCulture = dialogCovidAction.findViewById(R.id.rgCulture);
        CheckBox cbSwabSendOut = dialogCovidAction.findViewById(R.id.cbSwabSendOut);
        CheckBox cbInHouseSwab = dialogCovidAction.findViewById(R.id.cbInHouseSwab);
        CheckBox cbStrepTest = dialogCovidAction.findViewById(R.id.cbStrepTest);
        CheckBox cbRapidFlu = dialogCovidAction.findViewById(R.id.cbRapidFlu);
        CheckBox cbDnaFlu = dialogCovidAction.findViewById(R.id.cbDnaFlu);
        Button btnSubmit = dialogCovidAction.findViewById(R.id.btnSubmit);
        ImageView ivCancel = dialogCovidAction.findViewById(R.id.ivCancel);

		/*tvScore.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Total score : </font>"+ score));
		tvScoreCri.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Severity : </font>"+ severity));
		tvSelfText.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Self Care : \n</font>"+ self_text));*/

        TextView tvPatientName = dialogCovidAction.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidAction.findViewById(R.id.tvDate);

        tvPatientName.setText(covidFormListBeans.get(listPos).patient_name);
        tvDate.setText(covidFormListBeans.get(listPos).dateof);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogCovidAction.dismiss();

                String culture = rgCulture.getCheckedRadioButtonId() == R.id.rbCultureYes ? "1" : "0";
                String id = covidFormListBeans.get(listPos).id;

                RequestParams params = new RequestParams();
                //params.put("doctor_id", prefs.getString("id", ""));
                params.put("id", id);
                params.put("culture", culture);

                params.put("swab_send_out", cbSwabSendOut.isChecked() ? "1" : "0");
                params.put("in_house_swab", cbInHouseSwab.isChecked() ? "1" : "0");
                params.put("strep_test",  cbStrepTest.isChecked() ? "1" : "0");
                params.put("rapid_flu", cbRapidFlu.isChecked() ? "1" : "0");
                params.put("dna_flu",  cbDnaFlu.isChecked() ? "1" : "0");

                /*ApiManager apiManager = new ApiManager(ApiManager.CHANGE_COVID_FORM_STATUS,"post", params, apiCallBack, activity);
                apiManager.loadURL();*/

                showCovidBillingCodesDialog(covidFormListBeans.get(listPos), params);
            }
        });


        ivCancel.setOnClickListener(v -> {
            dialogCovidAction.dismiss();
        });


        /*dialogCovidAction.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCovidAction.show();*/

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidAction.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //askDialog.setCanceledOnTouchOutside(false);
        dialogCovidAction.show();
        dialogCovidAction.getWindow().setAttributes(lp);

        dialogCovidActionToDismiss = dialogCovidAction;
    }




    ListView lvBillingCodes;
    ArrayList<CovidBillingCodeBean> covidBillingCodeBeans;
    CovidBillingCodesAdapter covidBillingCodesAdapter;
    Dialog dialogCovidBillingToDismiss;
    public void showCovidBillingCodesDialog(CovidFormListBean covidFormListBean, RequestParams requestParams){
        Dialog dialogCovidBilling = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogCovidBilling.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogCovidBilling.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogCovidBilling.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogCovidBilling.setContentView(R.layout.dialog_covid_billing_codes);

        ImageView ivCancel =  dialogCovidBilling.findViewById(R.id.ivCancel);
        lvBillingCodes =  dialogCovidBilling.findViewById(R.id.lvBillingCodes);
        Button btnSubmit = dialogCovidBilling.findViewById(R.id.btnSubmit);

        TextView tvPatientName = dialogCovidBilling.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidBilling.findViewById(R.id.tvDate);

        tvPatientName.setText(covidFormListBean.patient_name);
        tvDate.setText(covidFormListBean.dateof);


        lvBillingCodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                covidBillingCodeBeans.get(position).isChecked = !covidBillingCodeBeans.get(position).isChecked;
                covidBillingCodesAdapter.notifyDataSetChanged();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(covidBillingCodeBeans != null){
                    StringBuilder sbBillingCodes = new StringBuilder();
                    for (int i = 0; i < covidBillingCodeBeans.size(); i++) {
                        if(covidBillingCodeBeans.get(i).isChecked){
                            sbBillingCodes.append(covidBillingCodeBeans.get(i).hcpcs_code);
                            sbBillingCodes.append(",");
                        }
                    }
                    String billing_codes = sbBillingCodes.toString();
                    if(TextUtils.isEmpty(billing_codes)){

                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm").
                                setMessage("You do not selected any service. Do you want to skip ?")
                                .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApiManager apiManager = new ApiManager(ApiManager.CHANGE_COVID_FORM_STATUS,"post", requestParams, apiCallBack, activity);
                                        apiManager.loadURL();
                                    }
                                }).setNegativeButton("No",null).create().show();
                    }else {
                        try {
                            billing_codes = billing_codes.substring(0, billing_codes.length()-1);
                            requestParams.put("billing_codes", billing_codes);
                        }catch (Exception e){e.printStackTrace();}
                        ApiManager apiManager = new ApiManager(ApiManager.CHANGE_COVID_FORM_STATUS,"post", requestParams, apiCallBack, activity);
                        apiManager.loadURL();
                    }

                }
            }
        });


        ivCancel.setOnClickListener(v -> dialogCovidBilling.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidBilling.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogCovidBilling.setCanceledOnTouchOutside(false);
        dialogCovidBilling.show();
        dialogCovidBilling.getWindow().setAttributes(lp);


        /*subUsersModels = SharedPrefsHelper.getInstance().getAllSubUsers();
        if(subUsersModels.isEmpty()){
            loadSubPatients();
        }else {
            lvSubusers.setAdapter(new SubUsersAdapter3(activity, subUsersModels));
        }*/

        loadCovidBillingCodes();

        dialogCovidBillingToDismiss = dialogCovidBilling;
    }




    Dialog dialogCovidResultsToDismiss;
    public void showCovidSendResultDialog(int listPos){
        final Dialog dialogCovidResults = new Dialog(activity, R.style.TransparentThemeH4B);//, R.style.TransparentThemeH4B
        dialogCovidResults.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCovidResults.setContentView(R.layout.dialog_covid_send_result);
        dialogCovidResults.setCanceledOnTouchOutside(false);

        RadioGroup rgRapidCovid = dialogCovidResults.findViewById(R.id.rgRapidCovid);
        RadioGroup rgPcrCovid = dialogCovidResults.findViewById(R.id.rgPcrCovid);
        RadioGroup rgRapidStrep = dialogCovidResults.findViewById(R.id.rgRapidStrep);
        RadioGroup rgStrepCulture = dialogCovidResults.findViewById(R.id.rgStrepCulture);
        RadioGroup rgRapidFlu = dialogCovidResults.findViewById(R.id.rgRapidFlu);

        Button btnSubmit = dialogCovidResults.findViewById(R.id.btnSubmit);
        ImageView ivCancel = dialogCovidResults.findViewById(R.id.ivCancel);

        RelativeLayout layRow1 = dialogCovidResults.findViewById(R.id.layRow1);
        RelativeLayout layRow2 = dialogCovidResults.findViewById(R.id.layRow2);
        RelativeLayout layRow3 = dialogCovidResults.findViewById(R.id.layRow3);
        RelativeLayout layRow4 = dialogCovidResults.findViewById(R.id.layRow4);
        RelativeLayout layRow5 = dialogCovidResults.findViewById(R.id.layRow5);

		/*tvScore.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Total score : </font>"+ score));
		tvScoreCri.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Severity : </font>"+ severity));
		tvSelfText.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Self Care : \n</font>"+ self_text));*/


        TextView tvPatientName = dialogCovidResults.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidResults.findViewById(R.id.tvDate);

        tvPatientName.setText(covidFormListBeans.get(listPos).patient_name);
        tvDate.setText(covidFormListBeans.get(listPos).dateof);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogCovidResults.dismiss();

                String rapid_covid_result = "";
                String pcr_covid_result = "";
                String rapid_strep_result = "";
                String strep_culture_result = "";
                String rapid_flu_result = "";

                if(rgRapidCovid.getCheckedRadioButtonId() == R.id.rbRapidCovidPositive){
                    rapid_covid_result = "Positive";
                }else if(rgRapidCovid.getCheckedRadioButtonId() == R.id.rbRapidCovidNegative){
                    rapid_covid_result = "Negative";
                }
                if(rgPcrCovid.getCheckedRadioButtonId() == R.id.rbPcrCovidPositive){
                    pcr_covid_result = "Positive";
                }else if(rgPcrCovid.getCheckedRadioButtonId() == R.id.rbPcrCovidNegative){
                    pcr_covid_result = "Negative";
                }
                if(rgRapidStrep.getCheckedRadioButtonId() == R.id.rbRapidStrepPositive){
                    rapid_strep_result = "Positive";
                }else if(rgRapidStrep.getCheckedRadioButtonId() == R.id.rbRapidStrepNegative){
                    rapid_strep_result = "Negative";
                }
                if(rgStrepCulture.getCheckedRadioButtonId() == R.id.rbStrepCulturePositive){
                    strep_culture_result = "Positive";
                }else if(rgStrepCulture.getCheckedRadioButtonId() == R.id.rbStrepCultureNegative){
                    strep_culture_result = "Negative";
                }
                if(rgRapidFlu.getCheckedRadioButtonId() == R.id.rbRapidFluPositive){
                    rapid_flu_result = "Positive";
                }else if(rgRapidFlu.getCheckedRadioButtonId() == R.id.rbRapidFluNegative){
                    rapid_flu_result = "Negative";
                }

                String id = covidFormListBeans.get(listPos).id;

                layRow1.setBackgroundColor(TextUtils.isEmpty(rapid_covid_result) ? getResources().getColor(R.color.theme_red_opaque_60) : getResources().getColor(android.R.color.transparent));
                layRow2.setBackgroundColor(TextUtils.isEmpty(pcr_covid_result) ? getResources().getColor(R.color.theme_red_opaque_60) : getResources().getColor(android.R.color.transparent));
                layRow3.setBackgroundColor(TextUtils.isEmpty(rapid_strep_result) ? getResources().getColor(R.color.theme_red_opaque_60) : getResources().getColor(android.R.color.transparent));
                layRow4.setBackgroundColor(TextUtils.isEmpty(strep_culture_result) ? getResources().getColor(R.color.theme_red_opaque_60) : getResources().getColor(android.R.color.transparent));
                layRow5.setBackgroundColor(TextUtils.isEmpty(rapid_flu_result) ? getResources().getColor(R.color.theme_red_opaque_60) : getResources().getColor(android.R.color.transparent));

                if(TextUtils.isEmpty(rapid_covid_result) || TextUtils.isEmpty(pcr_covid_result) || TextUtils.isEmpty(rapid_strep_result) ||
                        TextUtils.isEmpty(strep_culture_result) || TextUtils.isEmpty(rapid_flu_result)){
                    customToast.showToast("Please select all COVID test results",0,0);
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("doctor_id", prefs.getString("id", ""));
                params.put("id", id);
                params.put("rapid_covid_result", rapid_covid_result);
                params.put("pcr_covid_result", pcr_covid_result);
                params.put("rapid_strep_result", rapid_strep_result);
                params.put("strep_culture_result", strep_culture_result);
                params.put("rapid_flu_result", rapid_flu_result);

                new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle("Confirm")
                        .setMessage("Are you sure? Do you want to submit COVID test results? Please make sure you can not change it later.")
                        .setPositiveButton("Yes Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ApiManager apiManager = new ApiManager(ApiManager.COVID_SEND_RESULTS,"post", params, apiCallBack, activity);
                                apiManager.loadURL();
                            }
                        })
                        .setNegativeButton("Not Now", null)
                        .create().show();
            }
        });


        ivCancel.setOnClickListener(v -> {
            dialogCovidResults.dismiss();
        });


        //dialogCovidResults.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogCovidResults.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidResults.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //askDialog.setCanceledOnTouchOutside(false);
        dialogCovidResults.show();
        dialogCovidResults.getWindow().setAttributes(lp);

        dialogCovidResultsToDismiss = dialogCovidResults;
    }




    Dialog dialogCovidAddVitalsToDismiss;
    public void showCovidAddVitalsDialog(int listPos){
        final Dialog dialogCovidAddVitals = new Dialog(activity, R.style.TransparentThemeH4B);//, R.style.TransparentThemeH4B
        dialogCovidAddVitals.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCovidAddVitals.setContentView(R.layout.dialog_covid_add_vitals);
        dialogCovidAddVitals.setCanceledOnTouchOutside(false);

        EditText etOTHeightFt = dialogCovidAddVitals.findViewById(R.id.etOTHeightFt);
        EditText etOTHeightIn = dialogCovidAddVitals.findViewById(R.id.etOTHeightIn);
        EditText etOTWeight = dialogCovidAddVitals.findViewById(R.id.etOTWeight);
        EditText etOTTemperature = dialogCovidAddVitals.findViewById(R.id.etOTTemperature);
        EditText etOTHR = dialogCovidAddVitals.findViewById(R.id.etOTHR);
        EditText etOTO2Saturations = dialogCovidAddVitals.findViewById(R.id.etOTO2Saturations);

        Button btnSubmit = dialogCovidAddVitals.findViewById(R.id.btnSubmit);
        ImageView ivCancel = dialogCovidAddVitals.findViewById(R.id.ivCancel);

		/*tvScore.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Total score : </font>"+ score));
		tvScoreCri.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Severity : </font>"+ severity));
		tvSelfText.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Self Care : \n</font>"+ self_text));*/


        TextView tvPatientName = dialogCovidAddVitals.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidAddVitals.findViewById(R.id.tvDate);

        tvPatientName.setText(covidFormListBeans.get(listPos).patient_name);
        tvDate.setText(covidFormListBeans.get(listPos).dateof);


        etOTHeightFt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 1){
                    etOTHeightIn.requestFocus();
                }
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dialogCovidAddVitals.dismiss();

                String h_ft = etOTHeightFt.getText().toString().trim();
                String h_in = etOTHeightIn.getText().toString().trim();
                if(!h_in.isEmpty()){
                    h_ft = h_ft+"."+h_in;
                }
                String height = h_ft;
                String weight = etOTWeight.getText().toString().trim();
                String temp = etOTTemperature.getText().toString().trim();
                String hr = etOTHR.getText().toString().trim();
                String sp = etOTO2Saturations.getText().toString().trim();

                RequestParams params = new RequestParams();
                params.put("cid", covidFormListBeans.get(listPos).id);
                params.put("doctor_id", prefs.getString("id", ""));
                params.put("height", height);
                params.put("weight", weight);
                params.put("temp", temp);
                params.put("hr", hr);
                params.put("sp", sp);

                ApiManager apiManager = new ApiManager(ApiManager.COVID_SAVE_VITALS,"post", params, apiCallBack, activity);
                apiManager.loadURL();

                /*String result = "";
                if(rgCovidResults.getCheckedRadioButtonId() == R.id.rbCovidPositive){
                    result = "Positive";
                }else if(rgCovidResults.getCheckedRadioButtonId() == R.id.rbCovidNegative){
                    result = "Negative";
                }
                String id = covidFormListBeans.get(listPos).id;

                if(TextUtils.isEmpty(result)){
                    customToast.showToast("Please select the COVID test results",0,0);
                    return;
                }


                String finalResult = result;
                new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle("Confirm")
                        .setMessage("Are you sure? Do you want to submit COVID test result as "+result+"? Please make sure you can not change it later.")
                        .setPositiveButton("Yes Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RequestParams params = new RequestParams();
                                //params.put("doctor_id", prefs.getString("id", ""));
                                params.put("id", id);
                                params.put("result", finalResult);

                                ApiManager apiManager = new ApiManager(ApiManager.COVID_SEND_RESULTS,"post", params, apiCallBack, activity);
                                apiManager.loadURL();
                            }
                        })
                        .setNegativeButton("Not Now", null)
                        .create().show();*/
            }
        });


        ivCancel.setOnClickListener(v -> {
            dialogCovidAddVitals.dismiss();
        });


        //dialogCovidAddVitals.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogCovidAddVitals.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidAddVitals.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //askDialog.setCanceledOnTouchOutside(false);
        dialogCovidAddVitals.show();
        dialogCovidAddVitals.getWindow().setAttributes(lp);

        dialogCovidAddVitalsToDismiss = dialogCovidAddVitals;
    }




    public void showCovidViewDialog(int listPos){
        final Dialog dialogCovidResults = new Dialog(activity);
        dialogCovidResults.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCovidResults.setContentView(R.layout.dialog_view_covid_res);
        dialogCovidResults.setCanceledOnTouchOutside(false);

        TextView tvRapidCOVID = dialogCovidResults.findViewById(R.id.tvRapidCOVID);
        TextView tvPCR_COVID = dialogCovidResults.findViewById(R.id.tvPCR_COVID);
        TextView tvRapidStrep = dialogCovidResults.findViewById(R.id.tvRapidStrep);
        TextView tvStrepCulture = dialogCovidResults.findViewById(R.id.tvStrepCulture);
        TextView tvRapidFlu = dialogCovidResults.findViewById(R.id.tvRapidFlu);

        ImageView ivRapidCOVID = dialogCovidResults.findViewById(R.id.ivRapidCOVID);
        ImageView ivPCR_COVID = dialogCovidResults.findViewById(R.id.ivPCR_COVID);
        ImageView ivRapidStrep = dialogCovidResults.findViewById(R.id.ivRapidStrep);
        ImageView ivStrepCulture = dialogCovidResults.findViewById(R.id.ivStrepCulture);
        ImageView ivRapidFlu = dialogCovidResults.findViewById(R.id.ivRapidFlu);


        Button btnDone = dialogCovidResults.findViewById(R.id.btnDone);
        TextView tvPatientName = dialogCovidResults.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidResults.findViewById(R.id.tvDate);
        ImageView ivPatient = dialogCovidResults.findViewById(R.id.ivPatient);

        tvPatientName.setText(covidFormListBeans.get(listPos).patient_name);
        tvDate.setText(covidFormListBeans.get(listPos).dateof);
        DATA.loadImageFromURL(covidFormListBeans.get(listPos).image, R.drawable.ic_launcher, ivPatient);


        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_covid_result)){
            tvRapidCOVID.setText("Processing");
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Positive")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Negative")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_negatvie);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).pcr_covid_result)){
            tvPCR_COVID.setText("Processing");
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Positive")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Negative")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_negatvie);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_strep_result)){
            tvRapidStrep.setText("Processing");
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Positive")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Negative")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_negatvie);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).strep_culture_result)){
            tvStrepCulture.setText("Processing");
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Positive")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Negative")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_negatvie);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_flu_result)){
            tvRapidFlu.setText("Processing");
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Positive")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Negative")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_negatvie);
        }




        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogCovidResults.dismiss();
            }
        });


        dialogCovidResults.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCovidResults.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidResults.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogCovidResults.show();
        dialogCovidResults.getWindow().setAttributes(lp);*/
    }
}
