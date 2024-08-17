package com.app.emcurama;

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

import com.app.emcurama.adapter.SkilledNursingAdapter;
import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.SkilledNursingCheckBean;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.DatePickerFragment;
import com.app.emcurama.util.ExpandableHeightListView;
import com.app.emcurama.util.GloabalMethods;
import com.app.emcurama.util.HideShowKeypad;
import com.app.emcurama.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ActivityDmeRefferalApprove extends AppCompatActivity implements GloabalMethods.SignatureCallBack,ApiCallBack{

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

    EditText etSkilledDate,etSkilledDiagnosis,etSkilledPrecaution, etDmeOtherTreatments,etDmeName,etDmeEmail,etDmeFax;//etSkilledPatientName,etSkilledPatientPhone,
    ExpandableHeightListView lvWalker,lvCane,lvHospitalBed,lvCommode,lvOtherDME;
    Button btnDmeDone;
    ImageView ivSignature;
    CheckBox cbApprove;

    boolean isForOTApproval = false;//this flag recieves from ActivitySOAPReferralOT.. to approve OT Dme referrals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dme_refferal);

        isForOTApproval = getIntent().getBooleanExtra("isForOTApproval",false);

        activity = ActivityDmeRefferalApprove.this;
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

        //etSkilledPatientName = (EditText) findViewById(etSkilledPatientName);
        etSkilledDate = (EditText) findViewById(R.id.etSkilledDate);
        //etSkilledPatientPhone = (EditText) findViewById(etSkilledPatientPhone);
        etSkilledDiagnosis = (EditText) findViewById(R.id.etSkilledDiagnosis);
        etSkilledPrecaution = (EditText) findViewById(R.id.etSkilledPrecaution);
        etDmeOtherTreatments = (EditText) findViewById(R.id.etDmeOtherTreatments);
        etDmeName = (EditText) findViewById(R.id.etDmeName);
        etDmeEmail = (EditText) findViewById(R.id.etDmeEmail);
        etDmeFax = (EditText) findViewById(R.id.etDmeFax);
        lvWalker = (ExpandableHeightListView) findViewById(R.id.lvWalker);
        lvCane = (ExpandableHeightListView) findViewById(R.id.lvCane);
        lvHospitalBed = (ExpandableHeightListView) findViewById(R.id.lvHospitalBed);
        lvCommode = (ExpandableHeightListView) findViewById(R.id.lvCommode);
        lvOtherDME = (ExpandableHeightListView) findViewById(R.id.lvOtherDME);
        btnDmeDone = (Button) findViewById(R.id.btnDmeDone);
        ivSignature = (ImageView) findViewById(R.id.ivSignature);
        cbApprove = (CheckBox) findViewById(R.id.cbApprove);

        //etSkilledPatientName.setText(DATA.selectedUserCallName);
        //etSkilledPatientPhone.setText(PrescriptionsPopup.phone);
        etSkilledDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etSkilledDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //==============================DME Name=========================================================
        //add actv for dme name and service to get dme names, fax, email copy from original forms
        //==============================DME Name=========================================================

        //===========================================set data here===============================================
        try {
            if(dmeRef.isEmpty()){
                jsonObject = new JSONObject();
            }else {
                jsonObject = new JSONObject(dmeRef);
            }
            //==========================================================================================
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
            //==========================================================================================

            if(jsonObject.has("dateof")){
                etSkilledDate.setText(jsonObject.getString("dateof"));
            }
            if(jsonObject.has("diagnosis")){
                etSkilledDiagnosis.setText(jsonObject.getString("diagnosis"));
            }
            if(jsonObject.has("precautions")){
                etSkilledPrecaution.setText(jsonObject.getString("precautions"));
            }
            if(jsonObject.has("other")){
                etDmeOtherTreatments.setText(jsonObject.getString("other"));
            }
            if(jsonObject.has("dme_name")){
                etDmeName.setText(jsonObject.getString("dme_name"));
            }
            if(jsonObject.has("email")){
                etDmeEmail.setText(jsonObject.getString("email"));
            }
            if(jsonObject.has("fax")){
                etDmeFax.setText(jsonObject.getString("fax"));
            }
        }catch (Exception e){
            e.printStackTrace();
            jsonObject = new JSONObject();
        }
        //===========================================set data here===============================================

        lvWalker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                walkerList.get(position).isChecked = !walkerList.get(position).isChecked;
                lvWalkerAdapter.notifyDataSetChanged();
            }
        });
        walkerList = getWalkerList();
        lvWalkerAdapter = new SkilledNursingAdapter(activity,walkerList);
        lvWalker.setAdapter(lvWalkerAdapter);
        lvWalker.setExpanded(true);

        lvCane.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                caneList.get(position).isChecked = !caneList.get(position).isChecked;
                lvCaneAdapter.notifyDataSetChanged();
            }
        });
        caneList = getCaneList();
        lvCaneAdapter = new SkilledNursingAdapter(activity,caneList);
        lvCane.setAdapter(lvCaneAdapter);
        lvCane.setExpanded(true);

        lvHospitalBed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hospitalBedList.get(position).isChecked = !hospitalBedList.get(position).isChecked;
                lvhospitalBedAdapter.notifyDataSetChanged();
            }
        });
        hospitalBedList = gethospitalBedList();
        lvhospitalBedAdapter = new SkilledNursingAdapter(activity,hospitalBedList);
        lvHospitalBed.setAdapter(lvhospitalBedAdapter);
        lvHospitalBed.setExpanded(true);


        lvCommode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                commodeList.get(position).isChecked = !commodeList.get(position).isChecked;
                lvCommodeAdapter.notifyDataSetChanged();
            }
        });
        commodeList = getCommodeList();
        lvCommodeAdapter = new SkilledNursingAdapter(activity,commodeList);
        lvCommode.setAdapter(lvCommodeAdapter);
        lvCommode.setExpanded(true);


        lvOtherDME.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                othersList.get(position).isChecked = !othersList.get(position).isChecked;
                lvOthersAdapter.notifyDataSetChanged();
            }
        });
        othersList = getOthersList();
        lvOthersAdapter = new SkilledNursingAdapter(activity,othersList);
        lvOtherDME.setAdapter(lvOthersAdapter);
        lvOtherDME.setExpanded(true);



        ivSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).initSignatureDialog("Sign for approval",ivSignature,ActivityDmeRefferalApprove.this);
            }
        });
        btnDmeDone.setOnClickListener(new View.OnClickListener() {
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
                params.put("type", "dme");
                try {
                    if(!signaturePath.isEmpty()){
                        params.put("signature",new File(signaturePath));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                params.put("dme_referral[insurance_policy_no]", etHomeCareInsurance.getText().toString());
                params.put("dme_referral[referral_date]",etHomeCareReferralDate.getText().toString());
                params.put("dme_referral[referring_md]",etHomeCareRefMD.getText().toString());
                params.put("dme_referral[managing_physician]",etHomeCareManagingPhy.getText().toString());
                params.put("dme_referral[primary_home_care_diagnosis]",etHomeCarePriDiag.getText().toString());
                params.put("dme_referral[next_appt_date]",etHomeCareNextAptDate.getText().toString());
                params.put("dme_referral[last_office_visit]",etHomeCareLastOfcVisit.getText().toString());

                params.put("dme_referral[dateof]",etSkilledDate.getText().toString());
                params.put("dme_referral[diagnosis]",etSkilledDiagnosis.getText().toString());
                params.put("dme_referral[precautions]",etSkilledPrecaution.getText().toString());
                params.put("dme_referral[other]",etDmeOtherTreatments.getText().toString());
                params.put("dme_referral[dme_name]",etDmeName.getText().toString());
                params.put("dme_referral[email]",etDmeEmail.getText().toString());
                params.put("dme_referral[fax]",etDmeFax.getText().toString());

                if(! GloabalMethods.faxId.isEmpty()){
                    params.put("dme_referral[fax_id]", GloabalMethods.faxId);
                }

                for (int i = 0; i < walkerList.size(); i++) {
                    String servicesKey = walkerList.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "dme_referral[walker_";
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

                    if(walkerList.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        // params.put(paramsKey,"0");
                    }
                }

                for (int i = 0; i < caneList.size(); i++) {
                    String servicesKey = caneList.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "dme_referral[cane_";
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

                    if(caneList.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //params.put(paramsKey,"0");
                    }
                }

                for (int i = 0; i < hospitalBedList.size(); i++) {
                    String servicesKey = hospitalBedList.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "dme_referral[hospital_bed_";
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

                    if(hospitalBedList.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //params.put(paramsKey,"0");
                    }
                }

                for (int i = 0; i < commodeList.size(); i++) {
                    String servicesKey = commodeList.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "dme_referral[commode_";
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

                    if(commodeList.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //params.put(paramsKey,"0");
                    }
                }


                for (int i = 0; i < othersList.size(); i++) {
                    String servicesKey = othersList.get(i).checkBoxLabel.toLowerCase();
                    String paramsKey = "dme_referral[";
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

                    if(othersList.get(i).isChecked){
                        params.put(paramsKey,"1");
                    }else{
                        //params.put(paramsKey,"0");
                    }
                }

                if(isForOTApproval){
                    approveDMEApi = ApiManager.APPROVE_OT_REFERRAL;
                }else {
                    approveDMEApi = ApiManager.APPROVE_REFERRAL;
                }
                ApiManager apiManager = new ApiManager(approveDMEApi,"post",params,apiCallBack, activity);
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
                new GloabalMethods(activity).showDmeFaxDialog(etDmeName,etDmeEmail, etDmeFax);
            }
        });
    }

    String approveDMEApi = ApiManager.APPROVE_REFERRAL;

    String dmeRef = ActivitySOAPReferral.selectedSoapReferralBean.dme_referral;
    JSONObject jsonObject;

    ArrayList<SkilledNursingCheckBean> walkerList;
    SkilledNursingAdapter lvWalkerAdapter;
    public ArrayList<SkilledNursingCheckBean> getWalkerList(){
        ArrayList<SkilledNursingCheckBean> walkerList = new ArrayList<>();
        walkerList.add(new SkilledNursingCheckBean("Standard",false));
        walkerList.add(new SkilledNursingCheckBean("Rolling",false));
        walkerList.add(new SkilledNursingCheckBean("4 Wheeled",false));
        walkerList.add(new SkilledNursingCheckBean("Standard Bariatric",false));
        walkerList.add(new SkilledNursingCheckBean("Rolling Bariatric",false));
        walkerList.add(new SkilledNursingCheckBean("4 Wheeled Bariatric",false));

        for (int i = 0; i < walkerList.size(); i++) {
            String servicesKey = walkerList.get(i).checkBoxLabel.toLowerCase();
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

            if(jsonObject.has("walker_"+paramsKey)){
                walkerList.get(i).isChecked = true;
            }else{
                walkerList.get(i).isChecked = false;
            }
        }

        return walkerList;
    }

    ArrayList<SkilledNursingCheckBean> caneList;
    SkilledNursingAdapter lvCaneAdapter;
    public ArrayList<SkilledNursingCheckBean> getCaneList(){
        ArrayList<SkilledNursingCheckBean> caneList = new ArrayList<>();
        caneList.add(new SkilledNursingCheckBean("Single Point",false));
        caneList.add(new SkilledNursingCheckBean("Quad Cane",false));
        caneList.add(new SkilledNursingCheckBean("Single Point Bariatric",false));
        caneList.add(new SkilledNursingCheckBean("Quad Cane Bariatric",false));

        for (int i = 0; i < caneList.size(); i++) {
            String servicesKey = caneList.get(i).checkBoxLabel.toLowerCase();
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

            if(jsonObject.has("cane_"+paramsKey)){
                caneList.get(i).isChecked = true;
            }else{
                caneList.get(i).isChecked = false;
            }
        }
        return caneList;
    }

    ArrayList<SkilledNursingCheckBean> hospitalBedList;
    SkilledNursingAdapter lvhospitalBedAdapter;
    public ArrayList<SkilledNursingCheckBean> gethospitalBedList(){
        ArrayList<SkilledNursingCheckBean> hospitalBedList = new ArrayList<>();
        hospitalBedList.add(new SkilledNursingCheckBean("Fully Electric",false));
        hospitalBedList.add(new SkilledNursingCheckBean("Fully Electric Bariatric",false));

        for (int i = 0; i < hospitalBedList.size(); i++) {
            String servicesKey = hospitalBedList.get(i).checkBoxLabel.toLowerCase();
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

            if(jsonObject.has("hospital_bed_"+paramsKey)){
                hospitalBedList.get(i).isChecked = true;
            }else{
                hospitalBedList.get(i).isChecked = false;
            }
        }
        return hospitalBedList;
    }

    ArrayList<SkilledNursingCheckBean> commodeList;
    SkilledNursingAdapter lvCommodeAdapter;
    public ArrayList<SkilledNursingCheckBean> getCommodeList(){
        ArrayList<SkilledNursingCheckBean> commodeList = new ArrayList<>();
        commodeList.add(new SkilledNursingCheckBean("Drop Arm",false));
        commodeList.add(new SkilledNursingCheckBean("Drop Arm Bariatric",false));

        for (int i = 0; i < commodeList.size(); i++) {
            String servicesKey = commodeList.get(i).checkBoxLabel.toLowerCase();
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

            if(jsonObject.has("commode_"+paramsKey)){
                commodeList.get(i).isChecked = true;
            }else{
                commodeList.get(i).isChecked = false;
            }
        }
        return commodeList;
    }



    ArrayList<SkilledNursingCheckBean> othersList;
    SkilledNursingAdapter lvOthersAdapter;
    public ArrayList<SkilledNursingCheckBean> getOthersList(){
        ArrayList<SkilledNursingCheckBean> othersList = new ArrayList<>();
        othersList.add(new SkilledNursingCheckBean("Shower Chair",false));
        othersList.add(new SkilledNursingCheckBean("Shower Chair Bariatric",false));
        othersList.add(new SkilledNursingCheckBean("Transfer Bench",false));
        othersList.add(new SkilledNursingCheckBean("Transfer Bench Bariatric",false));
        othersList.add(new SkilledNursingCheckBean("Wound Care Supplies",false));
        othersList.add(new SkilledNursingCheckBean("Glucose Strips",false));

        for (int i = 0; i < othersList.size(); i++) {
            String servicesKey = othersList.get(i).checkBoxLabel.toLowerCase();
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

            if(jsonObject.has(paramsKey)){//"commode_"+
                othersList.get(i).isChecked = true;
            }else{
                othersList.get(i).isChecked = false;
            }
        }

        return othersList;
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
        if(apiName.equalsIgnoreCase(approveDMEApi)){
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
