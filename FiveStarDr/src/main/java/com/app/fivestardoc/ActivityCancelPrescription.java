package com.app.fivestardoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.app.fivestardoc.api.ApiCallBack;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.util.CustomToast;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityCancelPrescription extends BaseActivity implements ApiCallBack {

    Activity activity;
    SharedPreferences prefs;
    OpenActivity openActivity;
    CustomToast customToast;
    ApiCallBack apiCallBack;

    Button btnNotnow,btnCancelPrescription;

    String prescripID;

    TextView tvPrescDate,tvPrescPtName,tvPrescPatientDOB,tvPrescPatientGender,tvPrescPatientPhone,
            tvPrescPatientAdress,tvPrescPharmacy,tvPrescPharmacyPhone,tvDrugName,tvDrugStrength,tvDrugDosageForm
            ,tvDrugQuantity,tvDrugDuration,tvDrugPotencyCode,tvDrugInstructions,tvDrugRefils,tvRxFillIndec , tvInstrucationNote,
            tvPrescDrName,tvPrescDrPhone;

    public static boolean isShouldreloadDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_prescription);

        activity = ActivityCancelPrescription.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);


        prescripID = getIntent().getStringExtra("prescripID");

        tvPrescDate = findViewById(R.id.tvPrescDate);
        tvPrescPtName = findViewById(R.id.tvPrescPtName);
        tvPrescPatientDOB = findViewById(R.id.tvPrescPatientDOB);
        tvPrescPatientGender = findViewById(R.id.tvPrescPatientGender);
        tvPrescPatientPhone = findViewById(R.id.tvPrescPatientPhone);
        tvPrescPatientAdress = findViewById(R.id.tvPrescPatientAdress);
        tvPrescPharmacy = findViewById(R.id.tvPrescPharmacy);
        tvPrescPharmacyPhone = findViewById(R.id.tvPrescPharmacyPhone);
        tvDrugName = findViewById(R.id.tvDrugName);
        tvDrugStrength = findViewById(R.id.tvDrugStrength);
        tvDrugDosageForm = findViewById(R.id.tvDrugDosageForm);
        tvDrugQuantity = findViewById(R.id.tvDrugQuantity);
        tvDrugDuration = findViewById(R.id.tvDrugDuration);
        tvDrugPotencyCode = findViewById(R.id.tvDrugPotencyCode);
        tvDrugInstructions = findViewById(R.id.tvDrugInstructions);
        tvInstrucationNote = findViewById(R.id.tvInstrucationNote);
        tvDrugRefils = findViewById(R.id.tvDrugRefils);
        tvRxFillIndec = findViewById(R.id.tvRxFillIndec);
        tvPrescDrName = findViewById(R.id.tvPrescDrName);
        tvPrescDrPhone = findViewById(R.id.tvPrescDrPhone);

        btnNotnow = findViewById(R.id.btnNotnow);
        btnCancelPrescription = findViewById(R.id.btnCancelPrescription);


        getcancelPrescriptionData();


        btnNotnow.setOnClickListener(view ->
        {
            onBackPressed();
        });

        btnCancelPrescription.setOnClickListener(view ->
        {
            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm").
                    setMessage("Are you sure? Do you want to cancel this prescription.").
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            cancelPrescription();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            }).show();
        });
    }

    private void cancelPrescription() {
        RequestParams params = new RequestParams();
        params.put("request_id", prescripID);
        params.put("doctor_id", prefs.getString("id", "0"));
        ApiManager apiManager = new ApiManager(ApiManager.CANCEL_PRESCRIPTION, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void getcancelPrescriptionData() {

        RequestParams params = new RequestParams();
        params.put("request_id", prescripID);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CANCEL_PRESCRIPTION_DATA, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_CANCEL_PRESCRIPTION_DATA)){

            try {
                JSONObject jsonObject = new JSONObject(content);

                String date = jsonObject.getString("date");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String gender = jsonObject.getString("gender");
                String dob = jsonObject.getString("dob");
                String phone = jsonObject.getString("phone");
                String address = jsonObject.getString("address");
                String pharmacy = jsonObject.getString("pharmacy");
                String pharmacy_phone = jsonObject.getString("pharmacy_phone");
                String drug_name = jsonObject.getString("drug_name");
                String quantity = jsonObject.getString("quantity");
                String days = jsonObject.getString("days");
                String potency_code = jsonObject.getString("potency_code");
                String signature = jsonObject.getString("signature");
                String refills = jsonObject.getString("refills");
                String notes = jsonObject.getString("notes");
                String prescriber_lastname = jsonObject.getString("prescriber_lastname");
                String prescriber_firstname = jsonObject.getString("prescriber_firstname");
                String prescriber_phone = jsonObject.getString("prescriber_phone");

                tvPrescDate.setText(date);
                tvPrescPtName.setText(first_name+" "+last_name);
                tvPrescPatientGender.setText(gender);
                tvPrescPatientDOB.setText(dob);
                tvPrescPatientPhone.setText(phone);
                tvPrescPatientAdress.setText(address);
                tvPrescPharmacy.setText(pharmacy);
                tvPrescPharmacyPhone.setText(pharmacy_phone);
                tvDrugName.setText(drug_name);
                tvDrugQuantity.setText(quantity);
                tvDrugDuration.setText(days +" Days");
                tvDrugInstructions.setText(signature);
                tvInstrucationNote.setText(notes);
                tvDrugPotencyCode.setText(potency_code);
                tvDrugRefils.setText(refills);
                tvPrescDrName.setText(prescriber_firstname+" "+prescriber_lastname);
                tvPrescDrPhone.setText(prescriber_phone);


            } catch (JSONException e) {
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                e.printStackTrace();
            }
        }

        else if(apiName.equalsIgnoreCase(ApiManager.CANCEL_PRESCRIPTION)){

            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");

                JSONObject res = jsonObject.getJSONObject("res");
                String msg = res.getString("message");
                String statusmsg = res.getString("status");
                if (statusmsg.equalsIgnoreCase("success"))
                {
                    customToast.showToast(msg , 0 , 1);
                    isShouldreloadDate = true;
                    onBackPressed();
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    customToast.showToast(msg , 0 , 0);
                }
            } catch (JSONException e) {
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                e.printStackTrace();
            }
        }
    }
}