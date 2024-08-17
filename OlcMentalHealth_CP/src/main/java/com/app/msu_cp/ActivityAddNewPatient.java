package com.app.msu_cp;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.DoctorBean;
import com.app.msu_cp.reliance.ActivityCompany;
import com.app.msu_cp.reliance.greatlakes.ActivityMyPatients;
import com.app.msu_cp.reliance.mdlive.ActivityCompanyDoctors;
import com.app.msu_cp.reliance.mdlive.CompanyDoctorBean;
import com.app.msu_cp.reliance.mdlive.CompanyDoctorExpListAdapter;
import com.app.msu_cp.reliance.mdlive.DialogCompanyDoctors;
import com.app.msu_cp.util.CustomAnimations;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.ExpandableHeightListView;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityAddNewPatient extends BaseActivity {


    EditText etSignupFname,etSignupLname,etSignupCity,etSignupEmail, etSignupPhone,etSignupBirthdate,etSignupUsername,etSignupZipcode,etSignupResidency,etSignupState;
    RadioGroup rgSignupGender;
    Spinner spDoc;
    EditText etSignupMultiDoc;
    Button btnSubmitSignup;
    ExpandableHeightListView lvSelectedProviders;

    private ArrayList<CompanyDoctorBean> companyDoctorBeansToAdd;
    CompanyDoctorExpListAdapter companyDoctorExpListAdapter;

    private  boolean isGenderSelected = false;
    private String selectedGender = "";

    boolean isTyping = false;
    ProgressBar pbAutoComplete;
    ImageView ivClear;

    boolean isFromMyDoc = false;
    boolean isFromMyPatient = false;//from new my patient activity - Great Lakes
    boolean isMultiDoc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_patient);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        isFromMyDoc = getIntent().getBooleanExtra("isFromMyDoc", false);
        isFromMyPatient = getIntent().getBooleanExtra("isFromMyPatient", false);
        isMultiDoc = getIntent().getBooleanExtra("isMultiDoc", false);

        etSignupFname = (EditText) findViewById(R.id.etSignupFname);
        etSignupLname = (EditText) findViewById(R.id.etSignupLname);
        etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
        etSignupCity = (EditText) findViewById(R.id.etSignupCity);
        etSignupResidency = (EditText) findViewById(R.id.etSignupResidency);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPhone = (EditText) findViewById(R.id.etSignupPhone);
        etSignupBirthdate = (EditText) findViewById(R.id.etSignupBirthdate);
        etSignupZipcode = (EditText) findViewById(R.id.etSignupZipcode);
        etSignupState = (EditText) findViewById(R.id.etSignupState);

        rgSignupGender = (RadioGroup) findViewById(R.id.rgSignupGender);
        spDoc = findViewById(R.id.spDoc);
        etSignupMultiDoc = findViewById(R.id.etSignupMultiDoc);
        btnSubmitSignup = (Button) findViewById(R.id.btnSubmitSignup);
        lvSelectedProviders = findViewById(R.id.lvSelectedProviders);

        companyDoctorBeansToAdd = new ArrayList<>();
        companyDoctorExpListAdapter = new CompanyDoctorExpListAdapter(activity, companyDoctorBeansToAdd);
        lvSelectedProviders.setAdapter(companyDoctorExpListAdapter);
        lvSelectedProviders.setExpanded(true);

        /*etSignupBirthdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //DATA.isDatePickerCallFromSignup = true;
                    DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });*/
        etSignupBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        rgSignupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioMale:

                        selectedGender = "1";
                        isGenderSelected = true;

                        break;
                    case R.id.radioFemale:

                        selectedGender = "0";
                        isGenderSelected = true;

                        break;
                    default:
                        break;
                }
            }
        });


        btnSubmitSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = etSignupFname.getText().toString().trim();
                String lname = etSignupLname.getText().toString().trim();
                String city = etSignupCity.getText().toString().trim();
                String email = etSignupEmail.getText().toString().trim();
                String phone = etSignupPhone.getText().toString().trim();
                String birthdate = etSignupBirthdate.getText().toString().trim();
                String residency = etSignupResidency.getText().toString().trim();
                String username = etSignupUsername.getText().toString().trim();
                String zipcode = etSignupZipcode.getText().toString().trim();


                boolean validated = true;
                if(fname.isEmpty()){
                    etSignupFname.setError("Required");
                    validated = false;
                }
                if(lname.isEmpty()){
                    etSignupLname.setError("Required");
                    validated = false;
                }
                if(city.isEmpty()){
                    etSignupCity.setError("Required");
                    validated = false;
                }
                if(phone.isEmpty()){
                    etSignupPhone.setError("Required");
                    validated = false;
                }
                if(birthdate.isEmpty()){
                    etSignupBirthdate.setError("Required");
                    validated = false;
                }
                if(residency.isEmpty()){
                    etSignupResidency.setError("Required");
                    validated = false;
                }
                if(username.isEmpty()){
                    etSignupUsername.setError("Required");
                    validated = false;
                }
                if(zipcode.isEmpty()){
                    etSignupZipcode.setError("Required");
                    validated = false;
                }

                //Ahmer remove email validation as per sir jamal instruction = date - 24-05-2022
//                if(email.isEmpty() || !Signup.isValidEmail(email)){
//                    etSignupEmail.setError("Invalid email address");
//                    validated = false;
//                }

                if (! validated){
                    customToast.showToast("Please make sure you have filled the required fields.",0,1);
                    return;
                }


                if(!isGenderSelected){
                    customToast.showToast("Please select the gender",0,1);
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("first_name",fname);
                params.put("last_name", lname);
                params.put("city", city);
                params.put("email", email);
                params.put("phone", phone);
                params.put("birthdate", birthdate);
                params.put("residency", residency);
                params.put("username", username);
                params.put("zipcode", zipcode);
                params.put("gender", selectedGender);
                params.put("state", "MI");
                params.put("careprovider_id", prefs.getString("id",""));
                if(isFromMyDoc){
                    params.put("pcp", DATA.selectedDrId);
                }else if(isMultiDoc){
                    if(companyDoctorBeansToAdd.isEmpty()){
                        new CustomAnimations().shakeAnimate(etSignupMultiDoc, 1000, etSignupMultiDoc);
                        customToast.showToast("Please select provider",0,0);
                        return;
                    }else {
                        StringBuilder doctor_ids = new StringBuilder();
                        for (int i = 0; i < companyDoctorBeansToAdd.size(); i++) {
                            doctor_ids.append(companyDoctorBeansToAdd.get(i).doctor_id);
                            if(i < (companyDoctorBeansToAdd.size() - 1)){
                                doctor_ids.append(",");
                            }
                        }
                        params.put("multi_doctor", "1");
                        params.put("doctor_ids", doctor_ids.toString());
                        params.put("company_id", ActivityCompanyDoctors.selectedCompanyID);
                    }
                } else if(isFromMyPatient){//from greart lakes mypatient screen
                    int spDocPos = spDoc.getSelectedItemPosition();
                    if(doctorBeans != null && (!doctorBeans.isEmpty())){
                        params.put("pcp", doctorBeans.get(spDocPos).id);
                    }
                }else {
                    int spDocPos = spDoc.getSelectedItemPosition();
                    /*if(spDocPos == 0 || doctorBeans == null){
                        new CustomAnimations().shakeAnimate(spDoc, 1000, spDoc);
                        customToast.showToast("Please select a PCP",0,0);
                        return;
                    }*/
                    if(doctorBeans != null && (!doctorBeans.isEmpty())){
                        params.put("pcp", doctorBeans.get(spDocPos).id);
                    }
                    params.put("company_id", ActivityCompany.selectedCompanyID);
                }

                ApiManager apiManager = new ApiManager(ApiManager.ADD_NEW_PATIENT,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        //=====================Check Availablity=============================================

        pbAutoComplete = (ProgressBar) findViewById(R.id.pbAutoComplete);
        ivClear = (ImageView) findViewById(R.id.ivClear);
        //svMain = (ScrollView) findViewById(R.id.svMain);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.theme_red), android.graphics.PorterDuff.Mode.MULTIPLY);


        etSignupUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //boolean isType = false;
                    /*if(etSignupUsername.getText().toString().isEmpty()){
                        isType = false;
                    }else{
                        isType = true;
                    }*/
                    //GPSTracker gpsTracker = new GPSTracker(activity);
                    //LatLng latLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    checkUsername(etSignupUsername.getText().toString());
                    //gpsTracker.stopUsingGPS();
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });

        final int TYPING_TIMEOUT = 1000; // 5 seconds timeout = 5000, 500=half second
        final Handler timeoutHandler = new Handler();
        final Runnable typingTimeout = new Runnable() {
            public void run() {
                isTyping = false;
                //serviceCall();
                DATA.print("-- srevice call here 1");
                pbAutoComplete.setVisibility(View.VISIBLE);

                checkUsername(etSignupUsername.getText().toString());
            }
        };

        etSignupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // reset the timeout
                timeoutHandler.removeCallbacks(typingTimeout);

                if (etSignupUsername.getText().toString().trim().length() > 2) {
                    // schedule the timeout
                    timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);

                    if (!isTyping) {
                        isTyping = true;
                        //serviceCall();
                        DATA.print("-- srevice call here 2");
                    }
                } else {
                    isTyping = false;
                    //serviceCall();
                    DATA.print("-- srevice call here 3");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if(isFromMyDoc){
            spDoc.setVisibility(View.GONE);
            //etSignupMultiDoc.setVisibility(View.GONE);
            etSignupMultiDoc.setText(DATA.selectedDrName);
        }else if(isMultiDoc){
            spDoc.setVisibility(View.GONE);
            etSignupMultiDoc.setVisibility(View.VISIBLE);
            etSignupMultiDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogCompanyDoctors(activity).loadAndShowData();
                }
            });
        }else {//great lakes satisfied in this else condition
            spDoc.setVisibility(View.VISIBLE);
            etSignupMultiDoc.setVisibility(View.GONE);
            getAllDoctors();
        }
    }


    public void checkUsername(String username){
        RequestParams params = new RequestParams();
        params.put("username",username);

        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.CHECK_USERNAME_AVAILABLITY,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);
        //result: {"status":"success","message":"Patient has been registered successfully."}
        //{"status":"Error","msg":"gender parameter is missing OR missing value","message":"Gender Required"}
        if(apiName.equalsIgnoreCase(ApiManager.ADD_NEW_PATIENT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    customToast.showToast(jsonObject.getString("message"),0,1);

                    if(isFromMyDoc){
                        //no action
                    }else if(isMultiDoc){
                        //no action
                    }else if(isFromMyPatient){//from great lakes
                        ActivityMyPatients.shouldRefreshPatients = true;
                    }else {
                        ActivityCompany.shouldRefreshPatients = true;//else means came from ActivityCompany
                    }
                    finish();
                }else {
                    customToast.showToast(jsonObject.getString("msg"),0,1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CHECK_USERNAME_AVAILABLITY)){
            pbAutoComplete.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    etSignupUsername.setError(null);
                    btnSubmitSignup.setEnabled(true);
                }else {
                    etSignupUsername.setError("Username not available");
                    btnSubmitSignup.setEnabled(false);
                    customToast.showToast("This username is not available. Please try a diffrent one",0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_ALL_DOCTORS)){
            parseDocData(content);
        }

    }


    List<DoctorBean> doctorBeans;
    public void getAllDoctors(){

        String cachedData = sharedPrefsHelper.get("all_doctors_json", "");
        if(!cachedData.isEmpty()){
            ApiManager.shouldShowPD = false;
            parseDocData(cachedData);
        }

        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_ALL_DOCTORS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void parseDocData(String content){

        try {
            JSONArray jsonArray = new JSONArray(content);

            Type listType = new TypeToken<ArrayList<DoctorBean>>() {}.getType();
            doctorBeans = gson.fromJson(jsonArray+"", listType);

            if(doctorBeans != null){
                DoctorBean doctorBean = new DoctorBean();
                doctorBean.first_name = "Select ";
                doctorBean.last_name = "Provider";
                //doctorBean.id = "-";
                doctorBeans.add(0, doctorBean);

                ArrayAdapter<DoctorBean> spDoctorAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, doctorBeans);
                spDoc.setAdapter(spDoctorAdapter);

                sharedPrefsHelper.save("all_doctors_json", content);


                /*if(isFromMyDoc){
                    for (int i = 0; i < doctorBeans.size(); i++) {
                        if(doctorBeans.get(i).id!=null && doctorBeans.get(i).id.equalsIgnoreCase(DATA.selectedDrId)){
                            spDoc.setSelection(i);
                        }
                    }
                }*/
            }else {
                DATA.print("-- doctorBeans is null: "+doctorBeans);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }

    }


    public void setupMultiDocField(){
        for (int i = 0; i < DialogCompanyDoctors.companyDoctorBeansSelected.size(); i++) {
            if(! isProviderAlreadyAdded(DialogCompanyDoctors.companyDoctorBeansSelected.get(i))){
                companyDoctorBeansToAdd.add(DialogCompanyDoctors.companyDoctorBeansSelected.get(i));
            }else {
                DATA.print("-- Provider Alredy added ! : "+DialogCompanyDoctors.companyDoctorBeansSelected.get(i).first_name);
            }
        }

        companyDoctorExpListAdapter.notifyDataSetChanged();

        setFieldLabel();
    }

    public void setFieldLabel(){
        if(companyDoctorBeansToAdd.isEmpty()){
            etSignupMultiDoc.setText("No provider selected");
        }else {
            etSignupMultiDoc.setText(companyDoctorBeansToAdd.size()+" provider(s) selected");
        }
    }

    private boolean isProviderAlreadyAdded(CompanyDoctorBean bean){
        boolean isAlreadyAdded = false;
        if(companyDoctorBeansToAdd != null){
            for (int i = 0; i < companyDoctorBeansToAdd.size(); i++) {
                if(companyDoctorBeansToAdd.get(i).doctor_id.equalsIgnoreCase(bean.doctor_id)){
                    isAlreadyAdded = true;
                }
            }
        }
        return isAlreadyAdded;
    }
}
