package com.app.emcurauc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.model.CovidTestLocationBean;
import com.app.emcurauc.model.MyInsuranceBean;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.ExpandableHeightGridView;
import com.app.emcurauc.util.GloabalMethods;
import com.app.emcurauc.util.LiveCareInsurance;
import com.app.emcurauc.util.LiveCareInsuranceCardhelper;
import com.app.emcurauc.util.LiveCareInsuranceInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityCovidTest extends BaseActivity {


    TextView tvPatientName, tvPatientDOB, tvPatientPhone;//, tvPatientInsurance;
    RadioGroup rgCovidSymp, rgExposure, rgTravel, rgSmoke, rgDrink, rgDrugs, rgSurgeries;
    ExpandableHeightGridView gvCovidSymp;
    LinearLayout layExposure,layTravel;
    CheckBox cbExposureFamily,cbExposureWork,cbExposureFriends;
    CheckBox[] checkboxes;
    EditText etTravelFrom,etTravelTo,etPMH,etDrugAllergies,etPrescriptionMeds, etOTHeightFt,etOTHeightIn,etOTWeight,etOTTemperature,etOTHR,etOTO2Saturations,
            etSSN;
    Spinner spSelectInsurance,spSelectLocation;
    Button btnAddInsurance, btnSubmit;

    List<String> covidSymptoms;
    String selectedCovidSymptoms;

    ArrayList<MyInsuranceBean> myInsuranceBeen;
    ArrayAdapter<MyInsuranceBean> spInsuranceAdapter;
    String insurance_id = "";

    public static boolean isForFamilyMemberCovid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isForFamilyMemberCovid = getIntent().getBooleanExtra("isForFamilyMemberCovid", false);

        setContentView(R.layout.activity_covid_test);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Covid Test");
        }
        Button btnToolbar = findViewById(R.id.btnToolbar);
        btnToolbar.setText("Review Past Tests");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityCovidFormList.class, false);
            }
        });

        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientDOB = findViewById(R.id.tvPatientDOB);
        tvPatientPhone = findViewById(R.id.tvPatientPhone);
        //tvPatientInsurance = findViewById(R.id.tvPatientInsurance);

        rgCovidSymp = findViewById(R.id.rgCovidSymp);
        rgExposure = findViewById(R.id.rgExposure);
        rgTravel = findViewById(R.id.rgTravel);
        rgSmoke = findViewById(R.id.rgSmoke);
        rgDrink = findViewById(R.id.rgDrink);
        rgDrugs = findViewById(R.id.rgDrugs);
        rgSurgeries = findViewById(R.id.rgSurgeries);

        gvCovidSymp = findViewById(R.id.gvCovidSymp);

        layExposure = findViewById(R.id.layExposure);
        layTravel = findViewById(R.id.layTravel);

        cbExposureFamily = findViewById(R.id.cbExposureFamily);
        cbExposureWork = findViewById(R.id.cbExposureWork);
        cbExposureFriends = findViewById(R.id.cbExposureFriends);
        checkboxes = new CheckBox[]{cbExposureFamily, cbExposureWork, cbExposureFriends};


        etTravelFrom = findViewById(R.id.etTravelFrom);
        etTravelTo = findViewById(R.id.etTravelTo);
        etPMH = findViewById(R.id.etPMH);
        etDrugAllergies = findViewById(R.id.etDrugAllergies);
        etPrescriptionMeds = findViewById(R.id.etPrescriptionMeds);
        etOTHeightFt = findViewById(R.id.etOTHeightFt);
        etOTHeightIn = findViewById(R.id.etOTHeightIn);
        etOTWeight = findViewById(R.id.etOTWeight);
        etOTTemperature = findViewById(R.id.etOTTemperature);
        etOTHR = findViewById(R.id.etOTHR);
        etOTO2Saturations = findViewById(R.id.etOTO2Saturations);
        etSSN = findViewById(R.id.etSSN);

        spSelectInsurance = findViewById(R.id.spSelectInsurance);

        spSelectLocation = findViewById(R.id.spSelectLocation);

        btnAddInsurance = findViewById(R.id.btnAddInsurance);
        btnSubmit = findViewById(R.id.btnSubmit);


        etOTHeightFt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 1){
                    etOTHeightIn.requestFocus();
                }
            }
        });


        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getId()){
                    case R.id.rgCovidSymp:
                        if(checkedId == R.id.rbCovidSympYes){
                            gvCovidSymp.setVisibility(View.VISIBLE);
                        }else if(checkedId == R.id.rbCovidSympNo){
                            gvCovidSymp.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.rgExposure:
                        if(checkedId == R.id.rbExposureYes){
                            layExposure.setVisibility(View.VISIBLE);
                        }else if(checkedId == R.id.rbExposureNo){
                            layExposure.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.rgTravel:
                        if(checkedId == R.id.rbTravelYes){
                            layTravel.setVisibility(View.VISIBLE);
                        }else if(checkedId == R.id.rbTravelNo){
                            layTravel.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        };
        rgCovidSymp.setOnCheckedChangeListener(onCheckedChangeListener);
        rgExposure.setOnCheckedChangeListener(onCheckedChangeListener);
        rgTravel.setOnCheckedChangeListener(onCheckedChangeListener);

        GloabalMethods gloabalMethods = new GloabalMethods(activity);
        covidSymptoms = gloabalMethods.getCovidSymptoms();
        ArrayAdapter<String> gvCovidSympAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, covidSymptoms);
        //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
        gvCovidSymp.setAdapter(gvCovidSympAdapter);
        gvCovidSymp.setExpanded(true);
        //gvCovidSymp.setTag(covidSymptoms);//important



        spSelectInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                insurance_id = myInsuranceBeen.get(position).id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if(isForFamilyMemberCovid){
            tvPatientName.setText(GloabalMethods.subUsersModel.firstName+" "+GloabalMethods.subUsersModel.lastName);
            tvPatientDOB.setText(GloabalMethods.subUsersModel.dob);
            tvPatientPhone.setText(GloabalMethods.subUsersModel.phone);
            getMyInsurance();
        }else {
            tvPatientName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
            tvPatientDOB.setText(prefs.getString("birthdate", ""));
            tvPatientPhone.setText(prefs.getString("phone", ""));

            myInsuranceBeen = sharedPrefsHelper.getPatientInrances();
            if(myInsuranceBeen.isEmpty()){
                getMyInsurance();
            }else {
                //showInsuranceListDialog();
                spInsuranceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, myInsuranceBeen);
                spSelectInsurance.setAdapter(spInsuranceAdapter);
            }
        }


        btnAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isForFamilyMemberCovid){
                    LiveCareInsurance.familyFistName = GloabalMethods.subUsersModel.firstName;
                    LiveCareInsurance.familyLastName = GloabalMethods.subUsersModel.lastName;
                    LiveCareInsurance.familyDob = GloabalMethods.subUsersModel.dob;
                    new LiveCareInsurance(activity).getSimpleState(true);//getPayers();
                }else {
                    new LiveCareInsurance(activity).getSimpleState(false);//getPayers();
                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSelectedCovidSypmtops();

                String patient_id = isForFamilyMemberCovid ? GloabalMethods.subUsersModel.id : prefs.getString("id", "");
                String is_symptom = rgCovidSymp.getCheckedRadioButtonId() == R.id.rbCovidSympYes ?  "1" : "0";
                String symptom_txt = selectedCovidSymptoms;
                String is_exposure = rgExposure.getCheckedRadioButtonId() == R.id.rbExposureYes ?  "1" : "0";
                String exposure_txt = "";
                for (int i = 0; i < checkboxes.length; i++) {
                    if(checkboxes[i].isChecked()){
                        exposure_txt = exposure_txt + checkboxes[i].getText().toString() + ", ";
                    }
                }
                if(!TextUtils.isEmpty(exposure_txt)){
                    exposure_txt = exposure_txt.substring(0, exposure_txt.length()-2);
                }
                String is_travel = rgTravel.getCheckedRadioButtonId() == R.id.rbTravelYes ?  "1" : "0";
                String to_travel = etTravelTo.getText().toString().trim();
                String from_travel = etTravelFrom.getText().toString().trim();
                String pmh = etPMH.getText().toString().trim();
                String drug_allergies = etDrugAllergies.getText().toString().trim();
                String prescription_meds = etPrescriptionMeds.getText().toString().trim();
                String is_smoke = rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes ?  "1" : "0";
                String is_drink = rgDrink.getCheckedRadioButtonId() == R.id.rbDrinkYes ?  "1" : "0";
                String is_drugs = rgDrugs.getCheckedRadioButtonId() == R.id.rbDrugsYes ?  "1" : "0";
                String is_surgeries = rgSurgeries.getCheckedRadioButtonId() == R.id.rbSurgeriesYes ?  "1" : "0";
                String h_ft = etOTHeightFt.getText().toString().trim();
                String h_in = etOTHeightIn.getText().toString().trim();
                if(!h_in.isEmpty()){
                    h_ft = h_ft+"."+h_in;
                }
                String height = h_ft;
                String weight = etOTWeight.getText().toString().trim();
                String temp = etOTTemperature.getText().toString().trim();
                String hr = etOTHR.getText().toString().trim();
                String sp = etOTO2Saturations.getText().toString().trim();

                String social_security_number = etSSN.getText().toString().trim();

                String test_location = "";
                if(covidTestLocationBeans != null){
                    test_location = covidTestLocationBeans.get(spSelectLocation.getSelectedItemPosition()).id;
                }

                if(TextUtils.isEmpty(insurance_id)){
                    customToast.showToast("Please select insurance",0,0);
                    return;
                }


                RequestParams params = new RequestParams();
                params.put("patient_id", patient_id);
                params.put("insurance_id", insurance_id);
                params.put("is_symptom", is_symptom);
                params.put("symptom_txt", symptom_txt);
                params.put("is_exposure", is_exposure);
                params.put("exposure_txt", exposure_txt);
                params.put("is_travel", is_travel);
                params.put("to_travel", to_travel);
                params.put("from_travel", from_travel);
                params.put("pmh", pmh);
                params.put("drug_allergies", drug_allergies);
                params.put("prescription_meds", prescription_meds);
                params.put("is_smoke", is_smoke);
                params.put("is_drink", is_drink);
                params.put("is_drugs", is_drugs);
                params.put("is_surgeries", is_surgeries);
                params.put("height", height);
                params.put("weight", weight);
                params.put("temp", temp);
                params.put("hr", hr);
                params.put("sp", sp);
                params.put("social_security_number", social_security_number);
                params.put("test_location", test_location);

                ApiManager apiManager = new ApiManager(ApiManager.SAVE_COVID_TEST_FORM,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        ApiManager apiManager = new ApiManager(ApiManager.COVID_TEST_LOCATIONS,"get",null,apiCallBack, activity);
        apiManager.loadURL();

    }


    private void getSelectedCovidSypmtops(){
        SparseBooleanArray checked = gvCovidSymp.getCheckedItemPositions();
        DATA.print("--checked: "+checked);
        DATA.print("--checked size: "+checked.size());
        int c = 0;
        selectedCovidSymptoms = "";
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position1 = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i)){
                DATA.print("--pos checked "+position1);
                c++;
                selectedCovidSymptoms = selectedCovidSymptoms+ covidSymptoms.get(position1)+", ";
            }
        }
        if (c == 0) {
            selectedCovidSymptoms = "";
        }else {
            selectedCovidSymptoms = selectedCovidSymptoms.substring(0, selectedCovidSymptoms.length()-2);
        }
    }


    ArrayList<CovidTestLocationBean> covidTestLocationBeans;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if(apiName.equalsIgnoreCase(ApiManager.SAVE_COVID_TEST_FORM)){
            //{"status":"success","message":"Your information has been saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Done",null)
                        .create();
                if(jsonObject.optString("status").equalsIgnoreCase("success")){

                    ActivityCovidTest2.cid = jsonObject.optString("id");

                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            openActivity.open(ActivityCovidTest2.class, true);
                        }
                    });
                }
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equals(ApiManager.GET_MY_INSURANCE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");


                /*if(data.length() == 0){
                    tvNoInsurance.setText(jsonObject.optString("message"));
                    layNoInsurance.setVisibility(View.VISIBLE);
                }else {
                    layNoInsurance.setVisibility(View.GONE);
                }*/

                myInsuranceBeen = new ArrayList<>();
                MyInsuranceBean myInsuranceBean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String insurance = data.getJSONObject(i).getString("insurance");
                    String policy_number = data.getJSONObject(i).getString("policy_number");
                    String insurance_group = data.getJSONObject(i).getString("insurance_group");
                    String insurance_code = data.getJSONObject(i).getString("insurance_code");
                    String payer_name = data.getJSONObject(i).getString("payer_name");
                    String copay_uc = data.getJSONObject(i).getString("copay_uc");

                    String inc_front = data.getJSONObject(i).optString("inc_front");
                    String inc_back = data.getJSONObject(i).optString("inc_back");
                    String id_front = data.getJSONObject(i).optString("id_front");
                    String id_back = data.getJSONObject(i).optString("id_back");

                    myInsuranceBean = new MyInsuranceBean(id,patient_id,insurance,policy_number,insurance_group,insurance_code,payer_name,copay_uc,
                            inc_front, inc_back, id_front, id_back);
                    myInsuranceBeen.add(myInsuranceBean);
                    myInsuranceBean = null;
                }

                spInsuranceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, myInsuranceBeen);
                spSelectInsurance.setAdapter(spInsuranceAdapter);

                if(!isForFamilyMemberCovid){
                    sharedPrefsHelper.savePatientInrances(myInsuranceBeen);
                }

                //btnToolbar.setVisibility( (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.COVID_TEST_LOCATIONS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<CovidTestLocationBean>>() {}.getType();
                covidTestLocationBeans = new Gson().fromJson(data.toString(), listType);

                ArrayAdapter<CovidTestLocationBean> spTestLocationsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, covidTestLocationBeans);
                spSelectLocation.setAdapter(spTestLocationsAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    public void getMyInsurance(){
        RequestParams params = new RequestParams();
        if(isForFamilyMemberCovid){
            params.put("patient_id", GloabalMethods.subUsersModel.id);
        }else {
            params.put("patient_id", prefs.getString("id",""));
        }
        ApiManager apiManager = new ApiManager(ApiManager.GET_MY_INSURANCE,"post",params,this, activity);
        apiManager.loadURL();
    }





    //pick Insurance card image front + back on add new insurance
    LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
    public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
        liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
        liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(liveCareInsuranceCardhelper != null){
            //Insurance card image
            liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
