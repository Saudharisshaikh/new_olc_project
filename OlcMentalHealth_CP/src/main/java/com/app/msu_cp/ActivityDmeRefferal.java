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
import android.widget.ImageView;

import com.app.msu_cp.adapters.SkilledNursingAdapter;
import com.app.msu_cp.model.SkilledNursingCheckBean;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.ExpandableHeightListView;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityDmeRefferal extends BaseActivity{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
   // ApiCallBack apiCallBack;

    EditText etHomeCarePtName,etHomeCareInsurance,etHomeCareDOB,etHomeCareAddress,etHomeCareZipcode,etHomeCarePhone,
            etHomeCareReferralDate,etHomeCareRefMD,etHomeCareManagingPhy,etHomeCarePriDiag,etHomeCareNextAptDate,etHomeCareLastOfcVisit;

    EditText etSkilledDate,etSkilledDiagnosis,etSkilledPrecaution, etDmeOtherTreatments,etDmeEmail,etDmeFax,etDmeName;//etSkilledPatientName,etSkilledPatientPhone,
    ExpandableHeightListView lvWalker,lvCane,lvHospitalBed,lvCommode,lvOtherDME;
    Button btnDmeDone;

    //AutoCompleteTextView actvDmeName;
    ImageView ivSearchFax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dme_refferal);

        activity = ActivityDmeRefferal.this;
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



        etHomeCarePtName.setText(DATA.selectedUserCallName);
        etHomeCareInsurance.setText(ActivityTcmDetails.ptPolicyNo);
        etHomeCareDOB.setText(ActivityTcmDetails.ptDOB);
        etHomeCareAddress.setText(ActivityTcmDetails.ptAddress);
        etHomeCareZipcode.setText(ActivityTcmDetails.ptZipcode);
        etHomeCarePhone.setText(ActivityTcmDetails.ptPhone);
        etHomeCareReferralDate.setText(ActivityTcmDetails.ptRefDate);
        etHomeCareRefMD.setText(DATA.selectedDrName);//SelectedDoctorsProfile.ptRefDr
        etHomeCareManagingPhy.setText(DATA.selectedDrName);//SelectedDoctorsProfile.ptRefDr
        etHomeCarePriDiag.setText(ActivityTcmDetails.ptPriHomeCareDiag);

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


        //etSkilledPatientName.setText(DATA.selectedUserCallName);
        //etSkilledPatientPhone.setText(PrescriptionsPopup.phone);
        etSkilledDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        etSkilledDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etSkilledDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

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


        btnDmeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramsMap = new HashMap<String, String>();

                paramsMap.put("dme_referral[insurance_policy_no]", etHomeCareInsurance.getText().toString());
                paramsMap.put("dme_referral[referral_date]",etHomeCareReferralDate.getText().toString());
                paramsMap.put("dme_referral[referring_md]",etHomeCareRefMD.getText().toString());
                paramsMap.put("dme_referral[managing_physician]",etHomeCareManagingPhy.getText().toString());
                paramsMap.put("dme_referral[primary_home_care_diagnosis]",etHomeCarePriDiag.getText().toString());
                paramsMap.put("dme_referral[next_appt_date]",etHomeCareNextAptDate.getText().toString());
                paramsMap.put("dme_referral[last_office_visit]",etHomeCareLastOfcVisit.getText().toString());

                paramsMap.put("dme_referral[dateof]",etSkilledDate.getText().toString());
                paramsMap.put("dme_referral[diagnosis]",etSkilledDiagnosis.getText().toString());
                paramsMap.put("dme_referral[precautions]",etSkilledPrecaution.getText().toString());
                paramsMap.put("dme_referral[other]",etDmeOtherTreatments.getText().toString());
                paramsMap.put("dme_referral[dme_name]",etDmeName.getText().toString());
                paramsMap.put("dme_referral[email]",etDmeEmail.getText().toString());
                paramsMap.put("dme_referral[fax]",etDmeFax.getText().toString());
                if(! GloabalMethods.faxId.isEmpty()){
                    paramsMap.put("dme_referral[fax_id]",GloabalMethods.faxId);
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                       // paramsMap.put(paramsKey,"0");
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
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
                        paramsMap.put(paramsKey,"1");
                    }else{
                        //paramsMap.put(paramsKey,"0");
                    }
                }

                //printMap(paramsMap);

            ActivitySoapNotesNew.isDMEFormDone = true;
            finish();
            }
        });


        //actvDmeName = (AutoCompleteTextView) findViewById(R.id.etDmeName);
        ivSearchFax = (ImageView) findViewById(R.id.ivSearchFax);
        ivSearchFax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GloabalMethods(activity).showDmeFaxDialog(etDmeName,etDmeEmail, etDmeFax);
                /*String keyword = actvDmeName.getText().toString();
                if(keyword.isEmpty()){
                    actvDmeName.setError("Please enter the name to search");
                    return;
                }

                RequestParams params = new RequestParams();
                //params.put("user_id", prefs.getString("id", ""));
                params.put("keyword", keyword);
                ApiManager apiManager = new ApiManager(ApiManager.GET_FAX_HISTORY,"post",params,apiCallBack, activity);
                apiManager.loadURL();*/
            }
        });

        /*actvDmeName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvDmeName.setText(faxHistBeens.get(position).name);
                etDmeEmail.setText(faxHistBeens.get(position).email);
                etDmeFax.setText(faxHistBeens.get(position).fax_number);

                faxId = faxHistBeens.get(position).id;
            }
        });*/
    }

    public static Map<String,String> paramsMap = new HashMap<String, String>();
    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            DATA.print(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

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
        return caneList;
    }

    ArrayList<SkilledNursingCheckBean> hospitalBedList;
    SkilledNursingAdapter lvhospitalBedAdapter;
    public ArrayList<SkilledNursingCheckBean> gethospitalBedList(){
        ArrayList<SkilledNursingCheckBean> hospitalBedList = new ArrayList<>();
        hospitalBedList.add(new SkilledNursingCheckBean("Fully Electric",false));
        hospitalBedList.add(new SkilledNursingCheckBean("Fully Electric Bariatric",false));
        return hospitalBedList;
    }

    ArrayList<SkilledNursingCheckBean> commodeList;
    SkilledNursingAdapter lvCommodeAdapter;
    public ArrayList<SkilledNursingCheckBean> getCommodeList(){
        ArrayList<SkilledNursingCheckBean> commodeList = new ArrayList<>();
        commodeList.add(new SkilledNursingCheckBean("Drop Arm",false));
        commodeList.add(new SkilledNursingCheckBean("Drop Arm Bariatric",false));

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
                actvDmeName.setAdapter(faxAdapter);
                actvDmeName.showDropDown();
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }*/
}
