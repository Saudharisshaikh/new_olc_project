package com.app.Olc_MentalHealth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.Olc_MentalHealth.api.ApiCallBack;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.model.InsuranceModel;
import com.app.Olc_MentalHealth.paypal.PaymentLiveCare;
import com.app.Olc_MentalHealth.util.CheckInternetConnection;
import com.app.Olc_MentalHealth.util.CustomToast;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.ExpandableHeightGridView;
import com.app.Olc_MentalHealth.util.HideShowKeypad;
import com.app.Olc_MentalHealth.util.OpenActivity;
import com.app.Olc_MentalHealth.util.SharedPrefsHelper;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityCheckInsurance extends BaseActivity {

    SharedPreferences prefs;
    AppCompatActivity activity;
    ApiCallBack apiCallBack;
    CustomToast customToast;
    OpenActivity openActivity;
    CheckInternetConnection checkInternetConnection;
    HideShowKeypad hideShowKeypad;
    SharedPrefsHelper sharedPrefsHelper;

    ExpandableHeightGridView gvInsuranceList;
    List<InsuranceModel> insuranceModel = new ArrayList<>();

    private String selectedInsuranceID = "";

    Button btnNo,btnYes;

    TextView tvInsurfName,tvInsurlName,tvInsurBDate,tvInsurPhones,tvInsurAddress,tvInsurCity,tvInsurZipCode,tvInsurState
            ,tvplsContinue,tvSelectInsurance;

    public static String insuranceID ;

    LinearLayout linearlayoutUserData;

    public static boolean Frominsurance = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_insurance);

        activity = ActivityCheckInsurance.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        gvInsuranceList = findViewById(R.id.gvInsuranceList);
        linearlayoutUserData = findViewById(R.id.linearlayoutUserData);
        tvplsContinue = findViewById(R.id.tvplsContinue);
        tvSelectInsurance = findViewById(R.id.tvSelectInsurance);
        btnNo = findViewById(R.id.btnNo);
        btnYes = findViewById(R.id.btnYes);
        tvInsurfName = findViewById(R.id.tvInsurfName);
        tvInsurlName = findViewById(R.id.tvInsurlName);
        tvInsurBDate = findViewById(R.id.tvInsurBDate);
        tvInsurPhones = findViewById(R.id.tvInsurPhones);
        tvInsurAddress = findViewById(R.id.tvInsurAddress);
        tvInsurCity = findViewById(R.id.tvInsurCity);
        tvInsurZipCode = findViewById(R.id.tvInsurZipCode);
        tvInsurState = findViewById(R.id.tvInsurState);


        loadInsuranceListData();

        gvInsuranceList.setOnItemClickListener((adapterView, view, i, l) -> {

            selectedInsuranceID = insuranceModel.get(i).payer_name;
            insuranceID = insuranceModel.get(i).id;
        });

        btnNo.setOnClickListener(view ->
        {
            activity.startActivity(new Intent(activity, PaymentLiveCare.class));
            finish();
        });


        btnYes.setOnClickListener(view ->
        {
            if (selectedInsuranceID.equals(""))
            {
                customToast.showToast("Please Select Insurance" , 0 , 0);
            }
            else if (!selectedInsuranceID.isEmpty())
            {
                new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                        .setMessage("Are you sure ? Do you want to apply for Immediate/Live Care?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PaymentLiveCare.payment_typeIC = "NewInsurance";
                                Frominsurance = true;
                                openActivity.open(GetLiveCare.class , true);
                                activity.finish();
                            }
                        })
                        .setNegativeButton("Not Now", null).create().show();
            }
        });



    }

    public void loadInsuranceListData() {

        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        params.put("doctor_id", DATA.doctorIdForLiveCare);
        ApiManager apiManager = new ApiManager(ApiManager.GET_INSURANCE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_INSURANCE)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                
                getLabels();

                JSONArray result = jsonObject.getJSONArray("data");

                if (result.isNull(0))
                {
                    tvSelectInsurance.setText(labelSelectInsu2);
                    tvplsContinue.setText(labelplscontinue2);
                    linearlayoutUserData.setVisibility(View.VISIBLE);
                    gvInsuranceList.setVisibility(View.GONE);
                    tvInsurfName.setText(prefs.getString("first_name", ""));
                    tvInsurlName.setText(prefs.getString("last_name", ""));
                    tvInsurBDate.setText(prefs.getString("birthdate", ""));
                    tvInsurAddress.setText(prefs.getString("address", ""));
                    tvInsurZipCode.setText(prefs.getString("zipcode", ""));
                    tvInsurPhones.setText(prefs.getString("phone", ""));
                    tvInsurCity.setText(prefs.getString("city", ""));
                    tvInsurState.setText(prefs.getString("state", ""));
                    btnYes.setVisibility(View.GONE);
                }

                for (int i = 0; i < result.length(); i++) {
                    String id = result.getJSONObject(i).getString("id");
                    String payer_name = result.getJSONObject(i).getString("payer_name");
                    String policy_number = result.getJSONObject(i).getString("policy_number");

                    insuranceModel.add(new InsuranceModel(id ,payer_name , policy_number));
                    ArrayAdapter<InsuranceModel> insuranceModelArrayAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, insuranceModel);
                    gvInsuranceList.setAdapter(insuranceModelArrayAdapter);
                    gvInsuranceList.setExpanded(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }

    String labelSelectInsu2,labelplscontinue2;
    public void getLabels()
    {
        String prefsDataStr = prefs.getString(ApiManager.PREF_APP_LBL_KEY, "");
        if(! TextUtils.isEmpty(prefsDataStr)){
            try {
                JSONObject jsonObject = new JSONObject(prefsDataStr);
                JSONArray jsonArray = jsonObject.getJSONArray("new_insurance_labels");
                tvSelectInsurance.setText(jsonArray.getString(0));
                tvplsContinue.setText(jsonArray.getString(1));
                labelSelectInsu2 = jsonArray.getString(2);
                labelplscontinue2 = jsonArray.getString(3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}