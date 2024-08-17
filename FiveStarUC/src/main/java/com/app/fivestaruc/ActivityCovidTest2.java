package com.app.fivestaruc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.fivestaruc.adapter.LvCovidFormAdapter;
import com.app.fivestaruc.api.ApiManager;
import com.app.fivestaruc.model.CovidFormItemBean;
import com.app.fivestaruc.util.DATA;
import com.app.fivestaruc.util.ExpandableHeightListView;
import com.app.fivestaruc.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityCovidTest2 extends BaseActivity {


    ExpandableHeightListView lvCovidForm;
    TextView tvFormDesc;//, tvTotalScore;
    ScrollView svForm;
    Button btnSubmitForm;

    TextView tvPtLastName,tvPtFirstName,tvPtDOB,tvPtGender,tvPtAddress,tvPtZipcode,tvPtCity,tvPtState,tvPtCountry,tvPtPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_covid_test2);

        //setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Covid Test Reporting Sheet");
        }

        lvCovidForm = findViewById(R.id.lvCovidForm);
        tvFormDesc = findViewById(R.id.tvFormDesc);
        btnSubmitForm = findViewById(R.id.btnSubmitForm);
        svForm = findViewById(R.id.svForm);
        //tvTotalScore = findViewById(R.id.tvTotalScore);

        tvPtLastName = findViewById(R.id.tvPtLastName);
        tvPtFirstName = findViewById(R.id.tvPtFirstName);
        tvPtDOB = findViewById(R.id.tvPtDOB);
        tvPtGender = findViewById(R.id.tvPtGender);
        tvPtAddress = findViewById(R.id.tvPtAddress);
        tvPtZipcode = findViewById(R.id.tvPtZipcode);
        tvPtCity = findViewById(R.id.tvPtCity);
        tvPtState = findViewById(R.id.tvPtState);
        tvPtCountry = findViewById(R.id.tvPtCountry);
        tvPtPhone = findViewById(R.id.tvPtPhone);


        if(ActivityCovidTest.isForFamilyMemberCovid){
            tvPtLastName.setText(GloabalMethods.subUsersModel.firstName);
            tvPtFirstName.setText(GloabalMethods.subUsersModel.lastName);
            tvPtDOB.setText(GloabalMethods.subUsersModel.dob);
            String gender = GloabalMethods.subUsersModel.gender.equalsIgnoreCase("0") ? "Female" : "Male";
            tvPtGender.setText(gender);
            tvPtAddress.setText(GloabalMethods.subUsersModel.residency);
            tvPtZipcode.setText(GloabalMethods.subUsersModel.zipcode);
            tvPtCity.setText(GloabalMethods.subUsersModel.city);
            tvPtState.setText(GloabalMethods.subUsersModel.state);
            tvPtCountry.setText(GloabalMethods.subUsersModel.country);
            tvPtPhone.setText(GloabalMethods.subUsersModel.phone);
        }else {
            tvPtLastName.setText(prefs.getString("last_name", ""));
            tvPtFirstName.setText(prefs.getString("first_name", ""));
            tvPtDOB.setText(prefs.getString("birthdate", ""));
            String gender = prefs.getString("gender", "1").equalsIgnoreCase("0") ? "Female" : "Male";
            tvPtGender.setText(gender);
            tvPtAddress.setText(prefs.getString("address", ""));
            tvPtZipcode.setText(prefs.getString("zipcode", ""));
            tvPtCity.setText(prefs.getString("city", ""));
            tvPtState.setText(prefs.getString("state", ""));
            tvPtCountry.setText(prefs.getString("country", ""));
            tvPtPhone.setText(prefs.getString("phone", ""));
        }


        loadCovidTest2Form();



        btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! validateCovid2Form()){
                    if(lvCovidFormAdapter != null){
                        LvCovidFormAdapter.validateFlag = true;
                        lvCovidFormAdapter.notifyDataSetChanged();
                    }
                    customToast.showToast("Please make sure you have filled the required fields.", 0, 0);
                    return;
                }

                submitForm();
            }
        });

    }





    public void loadCovidTest2Form(){
        String savedJSON = prefs.getString("covid2_form_json", "");
        if(!savedJSON.isEmpty()){
            parseAdlFormData(savedJSON);
            ApiManager.shouldShowLoader = false;
        }
        ApiManager apiManager = new ApiManager(ApiManager.COVID_FORM2,"get",null,apiCallBack, activity);
        apiManager.loadURL();
    }

    public ArrayList<CovidFormItemBean> covidFormItemBeans;
    public LvCovidFormAdapter lvCovidFormAdapter;
    public void parseAdlFormData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            String form_title = jsonObject.getString("form_title");
            String form_text = jsonObject.getString("form_text");
            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(form_title);
            }
            if(! form_text.isEmpty()){
                tvFormDesc.setText(form_text);
            }else {
                tvFormDesc.setText(form_title);
            }

            prefs.edit().putString("covid2_form_json", content).apply();

            JSONArray questions = jsonObject.getJSONArray("questions");
            covidFormItemBeans = new ArrayList<>();
            CovidFormItemBean covidFormItemBean;
            for (int i = 0; i < questions.length(); i++) {

                String label = questions.getJSONObject(i).getString("label");

                List<CovidFormItemBean.CovidOptionBean> optionsList = new ArrayList<>();
                JSONArray options = questions.getJSONObject(i).getJSONArray("options");

                for (int j = 0; j < options.length(); j++) {
                    CovidFormItemBean.CovidOptionBean adLoptionBean = new CovidFormItemBean().new CovidOptionBean(options.getString(j));//new AdlFormBean.ADLoptionBean(options.getString(j))
                    optionsList.add(adLoptionBean);
                }

                covidFormItemBean = new CovidFormItemBean(label, optionsList);

                covidFormItemBean.isCellVisible = i != 5;
                covidFormItemBean.isAnswered = i == 5;

                /*if(isEdit){
                    //view data part starts
                    try {
                        JSONObject dataJSON = new JSONObject(ActivityAdlList.selectedAdlListBean.form_data);
                        if(dataJSON.has(i + "")){
                            adlFormBean.isAnswered = true;
                            adlFormBean.score = dataJSON.getInt(i+"");

                            for (int j = 0; j < adlFormBean.options.size(); j++) {
                                if(j == adlFormBean.score){
                                    adlFormBean.options.get(j).isSelected = true;
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //view data part ends
                }*/

                covidFormItemBeans.add(covidFormItemBean);
                covidFormItemBean = null;
            }
            lvCovidFormAdapter = new LvCovidFormAdapter(activity, covidFormItemBeans);
            lvCovidForm.setAdapter(lvCovidFormAdapter);
            lvCovidForm.setExpanded(true);
        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }
    }



    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.equalsIgnoreCase(ApiManager.COVID_FORM2)){
            parseAdlFormData(content);
        } else if(apiName.equalsIgnoreCase(ApiManager.SAVE_COVID_TEST_FORM2)){
            // {"status":"success","message":"Saved successfully."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Ok, Done",null)
                        .create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }

                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    public void setDateAtIndex5(String date){
        if(covidFormItemBeans != null){
            covidFormItemBeans.get(5).selectedAns = date;
        }
    }



    static String cid;
    public void submitForm(){
        RequestParams params = new RequestParams();

        String patient_id = ActivityCovidTest.isForFamilyMemberCovid ? GloabalMethods.subUsersModel.id : prefs.getString("id", "");
        params.put("patient_id", patient_id);

        params.put("cid", cid);

        List<String> answersList = new ArrayList<>();
        for (int i = 0; i < covidFormItemBeans.size(); i++) {
            if(covidFormItemBeans.get(i).isAnswered){
                //params.put("ans["+i+"]", covidFormItemBeans.get(i).selectedAns);
                answersList.add(covidFormItemBeans.get(i).selectedAns);
            }
        }
        params.put("ans", answersList);

        ApiManager apiManager = new ApiManager(ApiManager.SAVE_COVID_TEST_FORM2,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public boolean validateCovid2Form(){
        boolean validated = true;
        if(covidFormItemBeans != null){
            for (int i = 0; i < covidFormItemBeans.size(); i++) {
                if(! covidFormItemBeans.get(i).isAnswered){
                    validated = false;
                }
            }
        }
        return validated;
    }
}
