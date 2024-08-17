package com.app.mhcsn_ma;

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
import android.widget.ImageView;

import com.app.mhcsn_ma.adapter.SkilledNursingAdapter;
import com.app.mhcsn_ma.model.SkilledNursingCheckBean;
import com.app.mhcsn_ma.util.CheckInternetConnection;
import com.app.mhcsn_ma.util.CustomToast;
import com.app.mhcsn_ma.util.DATA;
import com.app.mhcsn_ma.util.DatePickerFragment;
import com.app.mhcsn_ma.util.ExpandableHeightListView;
import com.app.mhcsn_ma.util.GloabalMethods;
import com.app.mhcsn_ma.util.HideShowKeypad;
import com.app.mhcsn_ma.util.OpenActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivitySkilledNursingEdit extends AppCompatActivity{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    //ApiCallBack apiCallBack;

    EditText etHomeCarePtName,etHomeCareInsurance,etHomeCareDOB,etHomeCareAddress,etHomeCareZipcode,etHomeCarePhone,
            etHomeCareReferralDate,etHomeCareRefMD,etHomeCareManagingPhy,etHomeCarePriDiag,etHomeCareNextAptDate,etHomeCareLastOfcVisit;

    ExpandableHeightListView lvServices,lvRequestedTreatments;
    EditText etSkilledDate,etSkilledDiagnosis,etSkilledPrecaution,
             etSkilledOtherTreatments,etSkilledFrequency,etSkilledDuration,
             etSkilledEmail,etSkilledFax,etSkilledName;//etSkilledPatientName,etSkilledPatientPhone
    //AutoCompleteTextView actvSkilledName;
    ImageView ivSearchFax;
    Button btnSkilledDone;

    ArrayList<SkilledNursingCheckBean> services;
    ArrayList<SkilledNursingCheckBean> requestedTreatments;
    SkilledNursingAdapter servicesAdapter,requestedTreatmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skilled_nursing);

        activity = ActivitySkilledNursingEdit.this;
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

        //etSkilledPatientName.setText(DATA.selectedUserCallName);
        //etSkilledPatientPhone.setText(PrescriptionsPopup.phone);

        etHomeCareReferralDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        etHomeCareReferralDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etHomeCareReferralDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        etHomeCareNextAptDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etHomeCareNextAptDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        etHomeCareLastOfcVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etHomeCareLastOfcVisit);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


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

        etSkilledDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        etSkilledDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etSkilledDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        btnSkilledDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                skilled_nursing[skilled_nursing]
                skilled_nursing[physical_therapy]
                skilled_nursing[occupational_therapy]
                skilled_nursing[evaluate_and_treat]
                skilled_nursing[sensory_integration]
                skilled_nursing[evaluate_and_consult]
                skilled_nursing[posture]
                skilled_nursing[exercise]
                skilled_nursing[cognitive_skills]
                skilled_nursing[splinting]
                skilled_nursing[modalities]
                skilled_nursing[wound_care]
                skilled_nursing[manual_therapy]
                skilled_nursing[management_gait_training]
                skilled_nursing[phonophoresis]
                skilled_nursing[massage]*/

                paramsMap = new HashMap<String, String>();

                paramsMap.put("skilled_nursing[insurance_policy_no]", etHomeCareInsurance.getText().toString());
                paramsMap.put("skilled_nursing[referral_date]",etHomeCareReferralDate.getText().toString());
                paramsMap.put("skilled_nursing[referring_md]",etHomeCareRefMD.getText().toString());
                paramsMap.put("skilled_nursing[managing_physician]",etHomeCareManagingPhy.getText().toString());
                paramsMap.put("skilled_nursing[primary_home_care_diagnosis]",etHomeCarePriDiag.getText().toString());
                paramsMap.put("skilled_nursing[next_appt_date]",etHomeCareNextAptDate.getText().toString());
                paramsMap.put("skilled_nursing[last_office_visit]",etHomeCareLastOfcVisit.getText().toString());

                paramsMap.put("skilled_nursing[dateof]",etSkilledDate.getText().toString());
                paramsMap.put("skilled_nursing[other_treatment]",etSkilledOtherTreatments.getText().toString());
                paramsMap.put("skilled_nursing[frequency]",etSkilledFrequency.getText().toString());
                paramsMap.put("skilled_nursing[diagnosis]",etSkilledDiagnosis.getText().toString());
                paramsMap.put("skilled_nursing[precautions]",etSkilledPrecaution.getText().toString());
                paramsMap.put("skilled_nursing[duration]",etSkilledDuration.getText().toString());
                paramsMap.put("skilled_nursing[name]",etSkilledName.getText().toString());
                paramsMap.put("skilled_nursing[email]",etSkilledEmail.getText().toString());
                paramsMap.put("skilled_nursing[fax]",etSkilledFax.getText().toString());

                if(! GloabalMethods.faxId.isEmpty()){
                    paramsMap.put("skilled_nursing[fax_id]", GloabalMethods.faxId);
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
                    }
                }

                //printMap(paramsMap);

                ActivitySoapNotesNew.isSkilledNursingFormDone = true;
                finish();
            }
        });

        findViewById(R.id.laySignature).setVisibility(View.GONE);


        //actvSkilledName = (AutoCompleteTextView) findViewById(R.id.etSkilledName);
        ivSearchFax = (ImageView) findViewById(R.id.ivSearchFax);
        ivSearchFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GloabalMethods(activity).showDmeFaxDialog(etSkilledName,etSkilledEmail, etSkilledFax);

                /*String keyword = actvSkilledName.getText().toString();
                if(keyword.isEmpty()){
                    actvSkilledName.setError("Please enter the name to search");
                    return;
                }

                RequestParams params = new RequestParams();
                //params.put("user_id", prefs.getString("id", ""));
                params.put("keyword", keyword);
                ApiManager apiManager = new ApiManager(ApiManager.GET_FAX_HISTORY,"post",params,apiCallBack, activity);
                apiManager.loadURL();*/
            }
        });

        /*actvSkilledName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvSkilledName.setText(faxHistBeens.get(position).name);
                etSkilledEmail.setText(faxHistBeens.get(position).email);
                etSkilledFax.setText(faxHistBeens.get(position).fax_number);

                faxId = faxHistBeens.get(position).id;
            }
        });*/



        try {
            JSONObject jsonObject = new JSONObject(skn);
            //=======================================================================================================
            etHomeCarePtName.setText(DATA.selectedUserCallName);
            //etHomeCareInsurance.setText(ActivityTcmDetails.ptPolicyNo);
            etHomeCareDOB.setText(ActivityTcmDetails.ptDOB);
            etHomeCareAddress.setText(ActivityTcmDetails.ptAddress);
            etHomeCareZipcode.setText(ActivityTcmDetails.ptZipcode);
            etHomeCarePhone.setText(ActivityTcmDetails.ptPhone);
            //etHomeCareReferralDate.setText(ActivityTcmDetails.ptRefDate);
            //etHomeCareRefMD.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));//ActivityTcmDetails.ptRefDr
            //etHomeCareManagingPhy.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));//ActivityTcmDetails.ptRefDr
            //etHomeCarePriDiag.setText(ActivitySoapNotesEditNew.selectedNotesBean.complain);

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


        etSkilledDate.setEnabled(false);
        etHomeCareLastOfcVisit.setEnabled(false);
        etHomeCareNextAptDate.setEnabled(false);
    }

    String skn = ActivitySoapNotesEditNew.selectedNotesBean.skilled_nursing;

    public static Map<String,String> paramsMap = new HashMap<String, String>();
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            DATA.print(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

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

    /*ArrayList<FaxHistBean> faxHistBeens;
    String faxId = "";
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_FAX_HISTORY)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if(data.length() == 0){
                    customToast.showToast("No results found for the given name",0,1);
                }
                faxHistBeens = new ArrayList<>();
                FaxHistBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String name = data.getJSONObject(i).getString("name");
                    String email = data.getJSONObject(i).getString("email");
                    String fax_number = data.getJSONObject(i).getString("fax_number");

                    bean = new FaxHistBean(id,name,email,fax_number);
                    faxHistBeens.add(bean);
                    bean = null;
                }

                ArrayAdapter<FaxHistBean> faxAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, faxHistBeens);
                actvSkilledName.setAdapter(faxAdapter);
                actvSkilledName.showDropDown();
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }*/
}
