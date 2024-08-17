package com.app.mdlive_cp;

import android.app.Activity;
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

import com.app.mdlive_cp.adapters.HomecareFieldAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.api.CustomSnakeBar;
import com.app.mdlive_cp.model.HomeCareFieldBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.app.mdlive_cp.util.ExpandableHeightListView;
import com.app.mdlive_cp.util.HideShowKeypad;
import com.app.mdlive_cp.util.OpenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityHomeCareFormApprove extends AppCompatActivity implements ApiCallBack{


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    ExpandableHeightListView lvHomeCareServices,lvHomeCareFaceToFace;
    EditText etHomeCarePtName,etHomeCareInsurance,etHomeCareDOB,etHomeCareAddress,etHomeCareZipcode,etHomeCarePhone,
            etHomeCareReferralDate,etHomeCareRefMD,etHomeCareManagingPhy,etHomeCarePriDiag,etHomeCareNextAptDate,
            etHomeCareLastOfcVisit,etHomeCareFacetoFace,etHomeCareName,etHomeCareEmail,etHomeCareFax;
    Button btnHomeCareDone;

    //ImageView ivSignature;
    //CheckBox cbApprove;

    //static Map<String, String> paramsMap = new HashMap<>();

    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_care_form);

        activity = ActivityHomeCareFormApprove.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;

        lvHomeCareServices = (ExpandableHeightListView) findViewById(R.id.lvHomeCareServices);
        lvHomeCareFaceToFace = (ExpandableHeightListView) findViewById(R.id.lvHomeCareFaceToFace);
        btnHomeCareDone = (Button) findViewById(R.id.btnHomeCareDone);

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
        etHomeCareFacetoFace = (EditText) findViewById(R.id.etHomeCareFacetoFace);
        etHomeCareName = (EditText) findViewById(R.id.etHomeCareName);
        etHomeCareEmail = (EditText) findViewById(R.id.etHomeCareEmail);
        etHomeCareFax = (EditText) findViewById(R.id.etHomeCareFax);

        //ivSignature = (ImageView) findViewById(ivSignature);
        //cbApprove = (CheckBox) findViewById(R.id.cbApprove);

        /*ivSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).initSignatureDialog("Sign for approval",ivSignature,ActivityHomeCareFormApprove.this);
            }
        });*/


        try {
            jsonObject = new JSONObject(ActivitySOAPReferral.selectedSoapReferralBean.homecare_referral);

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

            etHomeCareFacetoFace.setText(jsonObject.getString("facetoface"));
            etHomeCareName.setText(jsonObject.getString("name"));
            etHomeCareEmail.setText(jsonObject.getString("email"));
            etHomeCareFax.setText(jsonObject.getString("fax"));
        }catch (Exception e){
            e.printStackTrace();
        }


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


        lvHomeCareFaceToFace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrWithoutTextField.get(position).isChecked = !arrWithoutTextField.get(position).isChecked;
                lvFaceToFaceAdapter.notifyDataSetChanged();
            }
        });


        btnHomeCareDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ApiManager apiManager = new ApiManager(ApiManager.HOMECARE_FORM_FIELDS,"post",null,apiCallBack, activity);
        apiManager.loadURL();


        etHomeCareLastOfcVisit.setEnabled(false);
        etHomeCareNextAptDate.setEnabled(false);
    }


    /*public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue()+"\n");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }

    ArrayList<HomeCareFieldBean> arrWithTextField,arrWithoutTextField;
    HomecareFieldAdapter lvFaceToFaceAdapter;

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.HOMECARE_FORM_FIELDS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray ar1 = jsonObject.getJSONArray("ar1");
                JSONArray ar2 = jsonObject.getJSONArray("ar2");

                arrWithTextField = new ArrayList<>();
                arrWithoutTextField = new ArrayList<>();
                HomeCareFieldBean bean;

                for (int i = 0; i < ar1.length(); i++) {
                    String key = ar1.getJSONObject(i).getString("key");
                    String value = ar1.getJSONObject(i).getString("value");

                    boolean isChecked = false;
                    String textField = "";

                    if(this.jsonObject != null){
                        if(this.jsonObject.has(key+"_field")){
                            textField = this.jsonObject.getString(key+"_field");
                        }
                        if(this.jsonObject.has(key)){
                            if(this.jsonObject.getString(key).equalsIgnoreCase("1")){
                                isChecked = true;
                            }
                        }
                    }

                    bean = new HomeCareFieldBean(key,value,isChecked,textField);
                    arrWithTextField.add(bean);
                    bean = null;
                }

                lvHomeCareServices.setAdapter(new HomecareFieldAdapter(activity,arrWithTextField,true));
                lvHomeCareServices.setExpanded(true);

                for (int i = 0; i < ar2.length(); i++) {
                    String key = ar2.getJSONObject(i).getString("key");
                    String value = ar2.getJSONObject(i).getString("value");

                    boolean isChecked = false;
                    String textField = "";

                    if(this.jsonObject != null){
                        /*if(this.jsonObject.has(key+"_field")){
                            textField = this.jsonObject.getString(key+"_field");
                        }*/
                        if(this.jsonObject.has(key)){
                            if(this.jsonObject.getString(key).equalsIgnoreCase("1")){
                                isChecked = true;
                            }
                        }
                    }

                    bean = new HomeCareFieldBean(key,value,isChecked,textField);
                    arrWithoutTextField.add(bean);
                    bean = null;
                }

                lvFaceToFaceAdapter = new HomecareFieldAdapter(activity,arrWithoutTextField,false);
                lvHomeCareFaceToFace.setAdapter(lvFaceToFaceAdapter);
                lvHomeCareFaceToFace.setExpanded(true);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
