package com.app.fivestardoc;

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
import android.widget.ImageView;

import com.app.fivestardoc.adapter.HomecareFieldAdapter;
import com.app.fivestardoc.api.ApiCallBack;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.api.CustomSnakeBar;
import com.app.fivestardoc.model.FaxHistBean;
import com.app.fivestardoc.model.HomeCareFieldBean;
import com.app.fivestardoc.util.CheckInternetConnection;
import com.app.fivestardoc.util.CustomToast;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.DatePickerFragment;
import com.app.fivestardoc.util.ExpandableHeightListView;
import com.app.fivestardoc.util.GloabalMethods;
import com.app.fivestardoc.util.HideShowKeypad;
import com.app.fivestardoc.util.OpenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityHomeCareFormEdit extends AppCompatActivity implements ApiCallBack{


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
            etHomeCareLastOfcVisit,etHomeCareFacetoFace,etHomeCareEmail,etHomeCareFax,etHomeCareName;
    Button btnHomeCareDone;
    //AutoCompleteTextView actvHomeCareName;
    ImageView ivSearchFax;

    static Map<String, String> paramsMap = new HashMap<>();

    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_care_form);

        activity = ActivityHomeCareFormEdit.this;
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
                paramsMap = new HashMap<String, String>();
                paramsMap.put("homecare_referral[insurance_policy_no]", etHomeCareInsurance.getText().toString());
                paramsMap.put("homecare_referral[referral_date]",etHomeCareReferralDate.getText().toString());
                paramsMap.put("homecare_referral[referring_md]",etHomeCareRefMD.getText().toString());
                paramsMap.put("homecare_referral[managing_physician]",etHomeCareManagingPhy.getText().toString());
                paramsMap.put("homecare_referral[primary_home_care_diagnosis]",etHomeCarePriDiag.getText().toString());
                paramsMap.put("homecare_referral[next_appt_date]",etHomeCareNextAptDate.getText().toString());
                paramsMap.put("homecare_referral[last_office_visit]",etHomeCareLastOfcVisit.getText().toString());

                paramsMap.put("homecare_referral[facetoface]",etHomeCareFacetoFace.getText().toString());
                paramsMap.put("homecare_referral[name]",etHomeCareName.getText().toString());
                paramsMap.put("homecare_referral[email]",etHomeCareEmail.getText().toString());
                paramsMap.put("homecare_referral[fax]",etHomeCareFax.getText().toString());
                if(! GloabalMethods.faxId.isEmpty()){
                    paramsMap.put("homecare_referral[fax_id]", GloabalMethods.faxId);
                }

                for (int i = 0; i < arrWithTextField.size(); i++) {
                    if(arrWithTextField.get(i).isChecked){
                        paramsMap.put("homecare_referral["+arrWithTextField.get(i).key+"]", "1");

                        paramsMap.put("homecare_referral["+arrWithTextField.get(i).key+"_field]", arrWithTextField.get(i).textField);
                    }
                }

                for (int i = 0; i < arrWithoutTextField.size(); i++) {
                    if(arrWithoutTextField.get(i).isChecked){
                        paramsMap.put("homecare_referral["+arrWithoutTextField.get(i).key+"]", "1");
                    }
                }

                //printMap(paramsMap);

                ActivitySoapNotesNew.isHomecareFormDone = true;
                finish();
            }
        });

        ApiManager apiManager = new ApiManager(ApiManager.HOMECARE_FORM_FIELDS,"post",null,apiCallBack, activity);
        apiManager.loadURL();

        findViewById(R.id.laySignature).setVisibility(View.GONE);


        //actvHomeCareName = (AutoCompleteTextView) findViewById(etHomeCareName);
        ivSearchFax = (ImageView) findViewById(R.id.ivSearchFax);
        ivSearchFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GloabalMethods(activity).showDmeFaxDialog(etHomeCareName,etHomeCareEmail, etHomeCareFax);

                /*String keyword = actvHomeCareName.getText().toString();
                if(keyword.isEmpty()){
                    actvHomeCareName.setError("Please enter the name to search");
                    return;
                }

                RequestParams params = new RequestParams();
                //params.put("user_id", prefs.getString("id", ""));
                params.put("keyword", keyword);
                ApiManager apiManager = new ApiManager(ApiManager.GET_FAX_HISTORY,"post",params,apiCallBack, activity);
                apiManager.loadURL();*/
            }
        });

        /*actvHomeCareName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvHomeCareName.setText(faxHistBeens.get(position).name);
                etHomeCareEmail.setText(faxHistBeens.get(position).email);
                etHomeCareFax.setText(faxHistBeens.get(position).fax_number);

                faxId = faxHistBeens.get(position).id;
            }
        });*/



        //---------------------------Edit Code--------------------
        try {
            if(ActivitySoapNotesEditNew.selectedNotesBean.homecare_referral.isEmpty()){
                jsonObject = new JSONObject();
            }else{
                jsonObject = new JSONObject(ActivitySoapNotesEditNew.selectedNotesBean.homecare_referral);
            }

            etHomeCarePtName.setText(DATA.selectedUserCallName);
            etHomeCareDOB.setText(ActivityTcmDetails.ptDOB);
            etHomeCareAddress.setText(ActivityTcmDetails.ptAddress);
            etHomeCareZipcode.setText(ActivityTcmDetails.ptZipcode);
            etHomeCarePhone.setText(ActivityTcmDetails.ptPhone);
            //etHomeCareInsurance.setText(ActivityTcmDetails.ptPolicyNo);
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

            etHomeCareFacetoFace.setText(jsonObject.getString("facetoface"));
            etHomeCareName.setText(jsonObject.getString("name"));
            etHomeCareEmail.setText(jsonObject.getString("email"));
            etHomeCareFax.setText(jsonObject.getString("fax"));
        }catch (Exception e){
            e.printStackTrace();
        }

        etHomeCareLastOfcVisit.setEnabled(false);
        etHomeCareNextAptDate.setEnabled(false);
    }


    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            DATA.print(pair.getKey() + " = " + pair.getValue()+"\n");
            it.remove(); // avoids a ConcurrentModificationException
        }
    }


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

    ArrayList<FaxHistBean> faxHistBeens;
    String faxId = "";
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
        /*else if(apiName.equalsIgnoreCase(ApiManager.GET_FAX_HISTORY)){
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
                actvHomeCareName.setAdapter(faxAdapter);
                actvHomeCareName.showDropDown();
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }*/
    }
}
