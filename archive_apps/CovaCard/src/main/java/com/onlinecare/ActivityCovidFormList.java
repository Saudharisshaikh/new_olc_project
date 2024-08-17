package com.onlinecare;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.onlinecare.adapter.CovidListAdapter;
import com.covacard.api.ApiManager;
import com.covacard.BaseActivity;
import com.covacard.R;
import com.onlinecare.model.CovidFormListBean;
import com.covacard.util.DATA;
import com.covacard.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityCovidFormList extends BaseActivity {


    ListView lvCovidList;
    TextView tvNoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Covid Test Results");
        }

        lvCovidList = findViewById(R.id.lvCovidList);
        tvNoData = findViewById(R.id.tvNoData);

        /*lvCovidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view/"+covidFormListBeans.get(position).id+"?platform=mobile" , "Covid Testing");
            }
        });*/


        loadListData();
    }

    public void loadListData(){
        String patient_id = ActivityCovidTest.isForFamilyMemberCovid ? GloabalMethods.subUsersModel.id : prefs.getString("id", "");
        RequestParams params = new RequestParams("patient_id", patient_id);
        ApiManager apiManager = new ApiManager(ApiManager.COVID_FORMS_LIST_PATIENT,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<CovidFormListBean> covidFormListBeans;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.COVID_FORMS_LIST_PATIENT)){
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

                lvCovidList.setAdapter(new CovidListAdapter(activity, covidFormListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
        /*else if(apiName.equalsIgnoreCase(ApiManager.CHANGE_COVID_FORM_STATUS)){
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
                                loadListData();
                            }
                        }
                    });
                }
                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }*/
    }


    /*Dialog dialogCovidActionToDismiss;
    public void showCovidActionDialog(int listPos){
        final Dialog dialogCovidAction = new Dialog(activity);//, R.style.TransparentThemeH4B
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
        Button btnCancel = dialogCovidAction.findViewById(R.id.btnCancel);

		*//*tvScore.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Total score : </font>"+ score));
		tvScoreCri.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Severity : </font>"+ severity));
		tvSelfText.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Self Care : \n</font>"+ self_text));*//*



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

                if(cbSwabSendOut.isChecked()){
                    params.put("swab_send_out", "1");
                }
                if(cbInHouseSwab.isChecked()){
                    params.put("in_house_swab", "1");
                }
                if(cbStrepTest.isChecked()){
                    params.put("strep_test", "1");
                }
                if(cbRapidFlu.isChecked()){
                    params.put("rapid_flu", "1");
                }
                if(cbDnaFlu.isChecked()){
                    params.put("dna_flu", "1");
                }

                ApiManager apiManager = new ApiManager(ApiManager.CHANGE_COVID_FORM_STATUS,"post", params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        btnCancel.setOnClickListener(v -> {
            dialogCovidAction.dismiss();
        });


        dialogCovidAction.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCovidAction.show();

        *//*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCovidAction.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //askDialog.setCanceledOnTouchOutside(false);
        dialogCovidAction.show();
        dialogCovidAction.getWindow().setAttributes(lp);*//*

        dialogCovidActionToDismiss = dialogCovidAction;
    }*/


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
        TextView tvDNA_Flu = dialogCovidResults.findViewById(R.id.tvDNA_Flu);

        ImageView ivRapidCOVID = dialogCovidResults.findViewById(R.id.ivRapidCOVID);
        ImageView ivPCR_COVID = dialogCovidResults.findViewById(R.id.ivPCR_COVID);
        ImageView ivRapidStrep = dialogCovidResults.findViewById(R.id.ivRapidStrep);
        ImageView ivStrepCulture = dialogCovidResults.findViewById(R.id.ivStrepCulture);
        ImageView ivRapidFlu = dialogCovidResults.findViewById(R.id.ivRapidFlu);
        ImageView ivDNA_Flu = dialogCovidResults.findViewById(R.id.ivDNA_Flu);


        Button btnDone = dialogCovidResults.findViewById(R.id.btnDone);
        TextView tvPatientName = dialogCovidResults.findViewById(R.id.tvPatientName);
        TextView tvDate = dialogCovidResults.findViewById(R.id.tvDate);
        ImageView ivPatient = dialogCovidResults.findViewById(R.id.ivPatient);


        LinearLayout layRow1 = dialogCovidResults.findViewById(R.id.layRow1);
        LinearLayout layRow2 = dialogCovidResults.findViewById(R.id.layRow2);
        LinearLayout layRow3 = dialogCovidResults.findViewById(R.id.layRow3);
        LinearLayout layRow4 = dialogCovidResults.findViewById(R.id.layRow4);
        LinearLayout layRow5 = dialogCovidResults.findViewById(R.id.layRow5);
        LinearLayout layRow6 = dialogCovidResults.findViewById(R.id.layRow6);

        View sep1 = dialogCovidResults.findViewById(R.id.sep1);
        View sep2 = dialogCovidResults.findViewById(R.id.sep2);
        View sep3 = dialogCovidResults.findViewById(R.id.sep3);
        View sep4 = dialogCovidResults.findViewById(R.id.sep4);
        View sep5 = dialogCovidResults.findViewById(R.id.sep5);

        layRow1.setVisibility(covidFormListBeans.get(listPos).in_house_swab.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//Rapid covid
        sep1.setVisibility(covidFormListBeans.get(listPos).in_house_swab.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        layRow2.setVisibility(covidFormListBeans.get(listPos).swab_send_out.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//PCR coovid
        sep2.setVisibility(covidFormListBeans.get(listPos).swab_send_out.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        layRow3.setVisibility(covidFormListBeans.get(listPos).strep_test.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//Rapid Strep
        sep3.setVisibility(covidFormListBeans.get(listPos).strep_test.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        layRow4.setVisibility(covidFormListBeans.get(listPos).culture.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//Strep Culture
        //sep4.setVisibility(covidFormListBeans.get(listPos).culture.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        layRow5.setVisibility(covidFormListBeans.get(listPos).rapid_flu.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//Rapid Flu
        sep5.setVisibility(covidFormListBeans.get(listPos).rapid_flu.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        layRow6.setVisibility(covidFormListBeans.get(listPos).dna_flu.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);//DNA Flu
        sep4.setVisibility(covidFormListBeans.get(listPos).dna_flu.equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);

        tvPatientName.setText(covidFormListBeans.get(listPos).patient_name);
        tvDate.setText(covidFormListBeans.get(listPos).dateof);
        //DATA.loadImageFromURL(covidFormListBeans.get(listPos).image, R.drawable.ic_launcher, ivPatient);


        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_covid_result)){
            tvRapidCOVID.setText("N/A");
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Positive")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Negative")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Not performed")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).rapid_covid_result.equalsIgnoreCase("Pending")){
            tvRapidCOVID.setText(covidFormListBeans.get(listPos).rapid_covid_result);
            ivRapidCOVID.setImageResource(R.drawable.cust_cir_res_pending);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).pcr_covid_result)){
            tvPCR_COVID.setText("N/A");
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Positive")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Negative")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Not performed")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).pcr_covid_result.equalsIgnoreCase("Pending")){
            tvPCR_COVID.setText(covidFormListBeans.get(listPos).pcr_covid_result);
            ivPCR_COVID.setImageResource(R.drawable.cust_cir_res_pending);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_strep_result)){
            tvRapidStrep.setText("N/A");
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Positive")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Negative")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Not performed")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).rapid_strep_result.equalsIgnoreCase("Pending")){
            tvRapidStrep.setText(covidFormListBeans.get(listPos).rapid_strep_result);
            ivRapidStrep.setImageResource(R.drawable.cust_cir_res_pending);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).strep_culture_result)){
            tvStrepCulture.setText("N/A");
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Positive")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Negative")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Not performed")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).strep_culture_result.equalsIgnoreCase("Pending")){
            tvStrepCulture.setText(covidFormListBeans.get(listPos).strep_culture_result);
            ivStrepCulture.setImageResource(R.drawable.cust_cir_res_pending);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).rapid_flu_result)){
            tvRapidFlu.setText("N/A");
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Positive")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Negative")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Not performed")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).rapid_flu_result.equalsIgnoreCase("Pending")){
            tvRapidFlu.setText(covidFormListBeans.get(listPos).rapid_flu_result);
            ivRapidFlu.setImageResource(R.drawable.cust_cir_res_pending);
        }

        if(TextUtils.isEmpty(covidFormListBeans.get(listPos).dna_flu_result)){
            tvDNA_Flu.setText("N/A");
            ivDNA_Flu.setImageResource(R.drawable.cust_cir_res_processing);
        }else if(covidFormListBeans.get(listPos).dna_flu_result.equalsIgnoreCase("Positive")){
            tvDNA_Flu.setText(covidFormListBeans.get(listPos).dna_flu_result);
            ivDNA_Flu.setImageResource(R.drawable.cust_cir_res_positvie);
        }else if(covidFormListBeans.get(listPos).dna_flu_result.equalsIgnoreCase("Negative")){
            tvDNA_Flu.setText(covidFormListBeans.get(listPos).dna_flu_result);
            ivDNA_Flu.setImageResource(R.drawable.cust_cir_res_negatvie);
        }else if(covidFormListBeans.get(listPos).dna_flu_result.equalsIgnoreCase("Not performed")){
            tvDNA_Flu.setText(covidFormListBeans.get(listPos).dna_flu_result);
            ivDNA_Flu.setImageResource(R.drawable.cust_cir_res_not_performed);
        }else if(covidFormListBeans.get(listPos).dna_flu_result.equalsIgnoreCase("Pending")){
            tvDNA_Flu.setText(covidFormListBeans.get(listPos).dna_flu_result);
            ivDNA_Flu.setImageResource(R.drawable.cust_cir_res_pending);
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
