package com.app.mdlive_dr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.mdlive_dr.adapter.SkilledNursingAdapter;
import com.app.mdlive_dr.api.ApiCallBack;
import com.app.mdlive_dr.api.ApiManager;
import com.app.mdlive_dr.model.SkilledNursingCheckBean;
import com.app.mdlive_dr.util.CheckInternetConnection;
import com.app.mdlive_dr.util.CustomToast;
import com.app.mdlive_dr.util.DATA;
import com.app.mdlive_dr.util.DatePickerFragment;
import com.app.mdlive_dr.util.ExpandableHeightListView;
import com.app.mdlive_dr.util.GloabalMethods;
import com.app.mdlive_dr.util.HideShowKeypad;
import com.app.mdlive_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ActivitySkilledNursingApprove extends AppCompatActivity implements GloabalMethods.SignatureCallBack,ApiCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    EditText etHomeCarePtName,etHomeCareInsurance,etHomeCareDOB,etHomeCareAddress,etHomeCareZipcode,etHomeCarePhone,
             etHomeCareReferralDate,etHomeCareRefMD,etHomeCareManagingPhy,etHomeCarePriDiag,etHomeCareNextAptDate,
             etHomeCareLastOfcVisit;

    ExpandableHeightListView lvServices,lvRequestedTreatments;
    EditText etSkilledDate,etSkilledDiagnosis,etSkilledPrecaution,
             etSkilledOtherTreatments,etSkilledFrequency,etSkilledDuration,
            etSkilledName,etSkilledEmail,etSkilledFax;//etSkilledPatientName,etSkilledPatientPhone
    Button btnSkilledDone;
    ImageView ivSignature;
    CheckBox cbApprove;

    ArrayList<SkilledNursingCheckBean> services;
    ArrayList<SkilledNursingCheckBean> requestedTreatments;
    SkilledNursingAdapter servicesAdapter,requestedTreatmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skilled_nursing);

        activity = ActivitySkilledNursingApprove.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        etHomeCarePtName = (EditText) findViewById(R.id.etHomeCarePtName);
        etHomeCareInsurance = (EditText) findViewById(R.id.etHomeCareInsurance);
        etHomeCareDOB = (EditText) findViewById(R.id.etHomeCareDOB);
        etHomeCareAddress = (EditText) findViewById(R.id.etHomeCareAddress);
        etHomeCareZipcode = (EditText) findViewById(R.id.etHomeCareZipcode);
        etHomeCarePhone = (EditText) findViewById(R.id.etHomeCarePhone);
        etHomeCareReferralDate = (EditText) findViewById(R.id.etHomeCareReferralDate);
        etHomeCareRefMD = (EditText) findViewById(R.id.etHomeCareRefMD);
        etHomeCareManagingPhy = (EditText) findViewById(R.id.etHomeCareManagingPhy);
        etHomeCarePriDiag = (EditText) findViewById(R.id.etHomeCarePriDiag);
        etHomeCareNextAptDate = (EditText) findViewById(R.id.etHomeCareNextAptDate);
        etHomeCareLastOfcVisit = (EditText) findViewById(R.id.etHomeCareLastOfcVisit);

        lvServices = (ExpandableHeightListView) findViewById(R.id.lvServices);
        lvRequestedTreatments = (ExpandableHeightListView) findViewById(R.id.lvRequestedTreatments);
        //etSkilledPatientName = (EditText) findViewById(etSkilledPatientName);
        etSkilledDate = (EditText) findViewById(R.id.etSkilledDate);
        //etSkilledPatientPhone = (EditText) findViewById(etSkilledPatientPhone);
        etSkilledDiagnosis = (EditText) findViewById(R.id.etSkilledDiagnosis);
        etSkilledPrecaution = (EditText) findViewById(R.id.etSkilledPrecaution);
        etSkilledOtherTreatments = (EditText) findViewById(R.id.etSkilledOtherTreatments);
        etSkilledFrequency = (EditText) findViewById(R.id.etSkilledFrequency);
        etSkilledDuration = (EditText) findViewById(R.id.etSkilledDuration);
        etSkilledName = (EditText) findViewById(R.id.etSkilledName);
        etSkilledEmail = (EditText) findViewById(R.id.etSkilledEmail);
        etSkilledFax = (EditText) findViewById(R.id.etSkilledFax);
        btnSkilledDone = (Button) findViewById(R.id.btnSkilledDone);
        ivSignature = (ImageView) findViewById(R.id.ivSignature);
        cbApprove = (CheckBox) findViewById(R.id.cbApprove);

        //etSkilledPatientName.setText(DATA.selectedUserCallName);
        //etSkilledPatientPhone.setText(PrescriptionsPopup.phone);



        lvServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                services.get(position).isChecked = !services.get(position).isChecked;
                servicesAdapter.notifyDataSetChanged();
            }
        });
        lvRequestedTreatments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestedTreatments.get(position).isChecked = !requestedTreatments.get(position).isChecked;
                requestedTreatmentsAdapter.notifyDataSetChanged();
            }
        });

        services = getServices();
        servicesAdapter = new SkilledNursingAdapter(activity,services);
        lvServices.setAdapter(servicesAdapter);
        lvServices.setExpanded(true);

        requestedTreatments = getRequestedTreatments();
        requestedTreatmentsAdapter = new SkilledNursingAdapter(activity,requestedTreatments);
        lvRequestedTreatments.setAdapter(requestedTreatmentsAdapter);
        lvRequestedTreatments.setExpanded(true);

        etSkilledDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etSkilledDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        try {
            JSONObject jsonObject = new JSONObject(skn);
            //=======================================================================================================
            etHomeCarePtName.setText(ActivitySOAPReferral.selectedSoapReferralBean.patient_firstname+" "
                    +ActivitySOAPReferral.selectedSoapReferralBean.patient_lastname);  //get these details by https://onlinecare.com/odev/app/getNoteByNoteID/1371  id is ActivitySOAPReferral.selectedSoapReferralBean.id
            etHomeCareDOB.setText(ActivitySOAPReferral.selectedSoapReferralBean.patient_birthdate);
            etHomeCareAddress.setText(ActivitySOAPReferral.selectedSoapReferralBean.patient_address);
            etHomeCareZipcode.setText(ActivitySOAPReferral.selectedSoapReferralBean.patient_zipcode);
            etHomeCarePhone.setText(ActivitySOAPReferral.selectedSoapReferralBean.patient_phone);

            if(jsonObject.has("referral_date")){
                etHomeCareReferralDate.setText(jsonObject.getString("referral_date"));
            }
            if(jsonObject.has("primary_home_care_diagnosis")){
                etHomeCarePriDiag.setText(jsonObject.getString("primary_home_care_diagnosis"));
            }
            if(jsonObject.has("insurance_policy_no")){
                etHomeCareInsurance.setText(jsonObject.getString("insurance_policy_no"));
            }
            if(jsonObject.has("referring_md")){
                etHomeCareRefMD.setText(jsonObject.getString("referring_md"));
            }
            if(jsonObject.has("managing_physician")){
                etHomeCareManagingPhy.setText(jsonObject.getString("managing_physician"));
            }
            if(jsonObject.has("last_office_visit")){
                etHomeCareLastOfcVisit.setText(jsonObject.getString("last_office_visit"));
            }
            if(jsonObject.has("next_appt_date")){
                etHomeCareNextAptDate.setText(jsonObject.getString("next_appt_date"));
            }
            //=======================================================================================================
            if(jsonObject.has("dateof")){
                etSkilledDate.setText(jsonObject.getString("dateof"));
            }
            if(jsonObject.has("other_treatment")){
                etSkilledOtherTreatments.setText(jsonObject.getString("other_treatment"));
            }
            if(jsonObject.has("frequency")){
                etSkilledFrequency.setText(jsonObject.getString("frequency"));
            }
            if(jsonObject.has("diagnosis")){
                etSkilledDiagnosis.setText(jsonObject.getString("diagnosis"));
            }
            if(jsonObject.has("precautions")){
                etSkilledPrecaution.setText(jsonObject.getString("precautions"));
            }
            if(jsonObject.has("duration")){
                etSkilledDuration.setText(jsonObject.getString("duration"));
            }
            if(jsonObject.has("name")){
                etSkilledName.setText(jsonObject.getString("name"));
            }
            if(jsonObject.has("email")){
                etSkilledEmail.setText(jsonObject.getString("email"));
            }
            if(jsonObject.has("fax")){
                etSkilledFax.setText(jsonObject.getString("fax"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ivSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).initSignatureDialog("Sign for approval",ivSignature,ActivitySkilledNursingApprove.this);
            }
        });
        btnSkilledDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signaturePath.isEmpty()){
                    if(! cbApprove.isChecked()){
                        customToast.showToast("Please mark approve or add your signature for the approval",0,1);
                        return;
                    }
                }

                RequestParams params = new RequestParams();
                params.put("note_id", ActivitySOAPReferral.selectedSoapReferralBean.id);
                params.put("type", "skilled_nursing");
                try {
                    if(!signaturePath.isEmpty()){
                        params.put("signature",new File(signaturePath));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                params.put("skilled_nursing[insurance_policy_no]", etHomeCareInsurance.getText().toString());
                params.put("skilled_nursing[referral_date]",etHomeCareReferralDate.getText().toString());
                params.put("skilled_nursing[referring_md]",etHomeCareRefMD.getText().toString());
                params.put("skilled_nursing[managing_physician]",etHomeCareManagingPhy.getText().toString());
                params.put("skilled_nursing[primary_home_care_diagnosis]",etHomeCarePriDiag.getText().toString());
                params.put("skilled_nursing[next_appt_date]",etHomeCareNextAptDate.getText().toString());
                params.put("skilled_nursing[last_office_visit]",etHomeCareLastOfcVisit.getText().toString());

                params.put("skilled_nursing[dateof]",etSkilledDate.getText().toString());
                params.put("skilled_nursing[other_treatment]",etSkilledOtherTreatments.getText().toString());
                params.put("skilled_nursing[frequency]",etSkilledFrequency.getText().toString());
                params.put("skilled_nursing[diagnosis]",etSkilledDiagnosis.getText().toString());
                params.put("skilled_nursing[precautions]",etSkilledPrecaution.getText().toString());
                params.put("skilled_nursing[duration]",etSkilledDuration.getText().toString());
                params.put("skilled_nursing[name]",etSkilledName.getText().toString());
                params.put("skilled_nursing[email]",etSkilledEmail.getText().toString());
                params.put("skilled_nursing[fax]",etSkilledFax.getText().toString());

                if(! GloabalMethods.faxId.isEmpty()){
                    params.put("skilled_nursing[fax_id]", GloabalMethods.faxId);
                }

                for (int i = 0; i < services.size(); i++) {
                    String servicesKey = services.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "skilled_nursing[";
                    if(servicesKey.contains("(")){
                        paramsKey = paramsKey + servicesKey.split("\\s+")[0];
                        paramsKey = paramsKey+"]";
                    }else{
                        String[] splited = servicesKey.split("\\s+");
                        for (int j = 0; j < splited.length; j++) {
                            paramsKey = paramsKey+splited[j]+"_";
                        }
                        paramsKey = paramsKey.substring(0,paramsKey.length()-1);
                        paramsKey = paramsKey+"]";
                    }

                    if(services.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //params.put(paramsKey,"0");
                    }
                }

                for (int i = 0; i < requestedTreatments.size(); i++) {
                    String servicesKey = requestedTreatments.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "skilled_nursing[";
                    if(servicesKey.contains("(")){
                        paramsKey = paramsKey + servicesKey.split("\\s+")[0];
                        if(paramsKey.contains("/")){
                            paramsKey = paramsKey.substring(0,paramsKey.indexOf("/"));
                        }
                        paramsKey = paramsKey+"]";
                    }else{
                        String[] splited = servicesKey.split("\\s+");
                        for (int j = 0; j < splited.length; j++) {
                            paramsKey = paramsKey+splited[j]+"_";
                        }
                        paramsKey = paramsKey.substring(0,paramsKey.length()-1);
                        if(paramsKey.contains("/")){
                            paramsKey = paramsKey.substring(0,paramsKey.indexOf("/"));
                        }
                        paramsKey = paramsKey+"]";
                    }

                    if(requestedTreatments.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
                    }
                }

                ApiManager apiManager = new ApiManager(ApiManager.APPROVE_REFERRAL,"post",params,apiCallBack, activity);
                apiManager.loadURL();

            }
        });


        etSkilledDate.setEnabled(false);
        etHomeCareLastOfcVisit.setEnabled(false);
        etHomeCareNextAptDate.setEnabled(false);


        ImageView ivSearchFax = (ImageView) findViewById(R.id.ivSearchFax);
        ivSearchFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GloabalMethods(activity).showDmeFaxDialog(etSkilledName,etSkilledEmail, etSkilledFax);

            }
        });
    }

    String skn = ActivitySOAPReferral.selectedSoapReferralBean.skilled_nursing;
    public ArrayList<SkilledNursingCheckBean> getServices(){
        ArrayList<SkilledNursingCheckBean> services = new ArrayList<>();
        services.add(new SkilledNursingCheckBean("Skilled Nursing",false));
        services.add(new SkilledNursingCheckBean("Physical Therapy",false));
        services.add(new SkilledNursingCheckBean("Occupational Therapy",false));

        for (int i = 0; i < services.size(); i++) {
            String servicesKey = services.get(i).checkBoxLabel.toLowerCase();
            String paramsKey = "";
            if(servicesKey.contains("(")){
                paramsKey = paramsKey + servicesKey.split("\\s+")[0];
                //paramsKey = paramsKey+"]";
            }else{
                String[] splited = servicesKey.split("\\s+");
                for (int j = 0; j < splited.length; j++) {
                    paramsKey = paramsKey+splited[j]+"_";
                }
                paramsKey = paramsKey.substring(0,paramsKey.length()-1);
                //paramsKey = paramsKey+"]";
            }
            System.out.println("-- paramsKey: "+paramsKey);
            if(skn.contains(paramsKey)){
                services.get(i).isChecked = true;
            }else{
                services.get(i).isChecked = false;
            }
        }
        return services;
    }

    public ArrayList<SkilledNursingCheckBean> getRequestedTreatments(){
        ArrayList<SkilledNursingCheckBean> requestedTreatments = new ArrayList<>();
        requestedTreatments.add(new SkilledNursingCheckBean("Evaluate and Treat",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Evaluate and Consult",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Exercise (Strength/Endurance)",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Splinting/Orthotics Rom (Active/Passive)",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Wound Care/Scar",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Management Gait Training",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Massage",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Sensory Integration",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Posture (Exercise/Education)",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Cognitive Skills",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Modalities (Ice,Heat,Ultrasound)",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Manual Therapy",false));
        requestedTreatments.add(new SkilledNursingCheckBean("Phonophoresis",false));

        for (int i = 0; i < requestedTreatments.size(); i++) {
            String servicesKey = requestedTreatments.get(i).checkBoxLabel.toLowerCase();
            String paramsKey = "";
            if(servicesKey.contains("(")){
                paramsKey = paramsKey + servicesKey.split("\\s+")[0];
                //paramsKey = paramsKey+"]";
            }else{
                String[] splited = servicesKey.split("\\s+");
                for (int j = 0; j < splited.length; j++) {
                    paramsKey = paramsKey+splited[j]+"_";
                }
                paramsKey = paramsKey.substring(0,paramsKey.length()-1);
                //paramsKey = paramsKey+"]";
            }

            if(paramsKey.contains("/")){
                paramsKey = paramsKey.substring(0,paramsKey.indexOf("/"));
            }
            System.out.println("-- paramsKey: "+paramsKey);
            if(skn.contains(paramsKey)){
                requestedTreatments.get(i).isChecked = true;
            }else{
                requestedTreatments.get(i).isChecked = false;
            }
        }

        return requestedTreatments;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }

    String signaturePath = "";
    @Override
    public void onSignCallBack(String signPath) {
        signaturePath = signPath;
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.APPROVE_REFERRAL)){
            //{"status":"success","msg":"Referral successfully approved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast(jsonObject.getString("msg"),0,1);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
