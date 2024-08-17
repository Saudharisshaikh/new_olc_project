package com.app.priorityone_dr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.api.CustomSnakeBar;
import com.app.priorityone_dr.model.PrescriptionCPBean;
import com.app.priorityone_dr.util.CheckInternetConnection;
import com.app.priorityone_dr.util.CustomToast;
import com.app.priorityone_dr.util.DATA;
import com.app.priorityone_dr.util.GloabalMethods;
import com.app.priorityone_dr.util.HideShowKeypad;
import com.app.priorityone_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class ActivityPrescDetailCP extends BaseActivity implements ApiCallBack,GloabalMethods.SignatureCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    public static PrescriptionCPBean prescriptionCPBean;

    TextView tvPressPatientName,tvPressDate,tvPressMedication,tvPressInstructions,
            tvPressQuantity,tvPressRefills,tvPressStatus;
    CircularImageView ivPresc;
    Button btnApprove,btnNotNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presc_detail_cp);

        activity = ActivityPrescDetailCP.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        apiCallBack = this;

        ivPresc = (CircularImageView) findViewById(R.id.ivPresc);
        tvPressPatientName = (TextView) findViewById(R.id.tvPressPatientName);
        tvPressDate = (TextView) findViewById(R.id.tvPressDate);
        tvPressMedication = (TextView) findViewById(R.id.tvPressMedication);
        tvPressInstructions = (TextView) findViewById(R.id.tvPressInstructions);
        tvPressQuantity = (TextView) findViewById(R.id.tvPressQuantity);
        tvPressRefills = (TextView) findViewById(R.id.tvPressRefills);
        tvPressStatus = (TextView) findViewById(R.id.tvPressStatus);
        btnApprove = (Button) findViewById(R.id.btnApprove);
        btnNotNow = (Button) findViewById(R.id.btnNotNow);


        tvPressPatientName.setText(prescriptionCPBean.first_name+" "+prescriptionCPBean.last_name);
        tvPressDate.setText(prescriptionCPBean.dateof);
        tvPressMedication.setText(prescriptionCPBean.drug_name);
        tvPressInstructions.setText(prescriptionCPBean.directions);
        tvPressQuantity.setText(prescriptionCPBean.quantity);
        tvPressRefills.setText(prescriptionCPBean.refill);
        tvPressStatus.setText(prescriptionCPBean.ppstatus);
        DATA.loadImageFromURL(prescriptionCPBean.image,R.drawable.icon_call_screen,ivPresc);


        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).initSignatureDialog("Sign your Prescriptions",null,ActivityPrescDetailCP.this);
            }
        });

    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.APPROVE_PRESCRIPTION)){
            try {
                //{"success":1,"message":"Saved"}
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast("You have approved the prescription",0,1);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }


    @Override
    public void onSignCallBack(String signPath) {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("prescription_id", prescriptionCPBean.id);
        try {
            params.put("signature", new File(signPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiManager apiManager = new ApiManager(ApiManager.APPROVE_PRESCRIPTION,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
}
