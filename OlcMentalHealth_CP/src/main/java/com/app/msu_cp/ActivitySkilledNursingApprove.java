package com.app.msu_cp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.app.msu_cp.adapters.SkilledNursingAdapter;
import com.app.msu_cp.model.SkilledNursingCheckBean;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.ExpandableHeightListView;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySkilledNursingApprove extends AppCompatActivity{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    //ApiCallBack apiCallBack;

    EditText etHomeCarePtName,etHomeCareInsurance,etHomeCareDOB,etHomeCareAddress,etHomeCareZipcode,etHomeCarePhone,
            etHomeCareReferralDate,etHomeCareRefMD,etHomeCareManagingPhy,etHomeCarePriDiag,etHomeCareNextAptDate,
            etHomeCareLastOfcVisit;

    ExpandableHeightListView lvServices,lvRequestedTreatments;
    EditText etSkilledDate,etSkilledDiagnosis,etSkilledPrecaution,
             etSkilledOtherTreatments,etSkilledFrequency,etSkilledDuration,
            etSkilledName,etSkilledEmail,etSkilledFax;//etSkilledPatientName,etSkilledPatientPhone
    Button btnSkilledDone;
    //ImageView ivSignature;
    //CheckBox cbApprove;

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
        //apiCallBack = this;

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
        //ivSignature = (ImageView) findViewById(R.id.ivSignature);
        //cbApprove = (CheckBox) findViewById(R.id.cbApprove);

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


        btnSkilledDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        etSkilledDate.setEnabled(false);
        etHomeCareLastOfcVisit.setEnabled(false);
        etHomeCareNextAptDate.setEnabled(false);
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
            DATA.print("-- paramsKey: "+paramsKey);
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
            DATA.print("-- paramsKey: "+paramsKey);
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

}
