package com.app.onlinecare;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.onlinecare.adapter.CovidListAdapter;
import com.app.onlinecare.api.ApiManager;
import com.app.onlinecare.model.CovidFormListBean;
import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.GloabalMethods;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_form_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Covid Testing");
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
}
