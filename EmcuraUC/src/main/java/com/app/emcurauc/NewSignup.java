package com.app.emcurauc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.emcurauc.adapter.PatientInsuranceAdapter;
import com.app.emcurauc.adapter.PrimaryCareAdapter;
import com.app.emcurauc.api.ApiCallBack;
import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.model.Organization;
import com.app.emcurauc.model.PatientInfo;
import com.app.emcurauc.model.PayerBean;
import com.app.emcurauc.model.PrimaryCareBean;
import com.app.emcurauc.model.ScheduleLocModel;
import com.app.emcurauc.model.StatesBeans;
import com.app.emcurauc.util.CheckInternetConnection;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.DatePickerFragment;
import com.app.emcurauc.util.GloabalMethods;
import com.app.emcurauc.util.LiveCareInsuranceCardhelper;
import com.app.emcurauc.util.LiveCareInsuranceInterface;
import com.app.emcurauc.util.OpenActivity;
import com.app.emcurauc.util.SelectedFormListener;
import com.app.emcurauc.util.SharedPrefsHelper;
import com.app.emcurauc.util.ValidationCallBack;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewSignup extends AppCompatActivity implements ApiCallBack , SelectedFormListener, LiveCareInsuranceInterface, ValidationCallBack {

    Activity activity;
    CheckInternetConnection checkInternet;
    CustomToast customToast;
    OpenActivity openActivity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    SharedPrefsHelper sharedPrefsHelper;

    String selectInsuranceType = "";

    int pos = 0;

    Organization selectedOrganizationForm;

    public int selectedTab = 0;

    String fname = "";
    String lname = "";
    String email = "";
    String password = "";
    String phone = "";
    String birthDate = "";
    String address = "";
    String zipcode = "";
    String confirmPassword = "";
    String city = "";

    boolean isShowList = false;





    public void setShowList(boolean isShow){
        isShowList = isShow;
    }


    boolean isValidEmailPhone = false;



    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{4,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public void validData() {

        viewFlipper.showNext();
        getPatientInsurance();
        int selectedChild = viewFlipper.getDisplayedChild();
        setupViewFiliperAndTabs(selectedChild);
        setInd();
        btnFlipPrev.setEnabled(true);
        btnFlipNext.setEnabled(true);
        btnSelectInsurance.callOnClick();

            }



    @Override
    public void selectedFormData() {

        Log.d("--see", "selectedFormData: "+selectedOrganizationForm);

        if(isShowList && selectedOrganizationForm == null){
            AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("You have not selected the insurance.")
                    .setPositiveButton("Select Insurance", (dialog, which) -> {
                        dialog.dismiss();

                    })
                    .setNegativeButton("Proceed",((dialogInterface, i) -> {
                        btnFlipPrev.setEnabled(true);
                        btnFlipNext.setEnabled(true);
                        btnFlipNext.setText("Submit");
                        viewFlipper.showNext();
                        dialogInterface.dismiss();
                    }))
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
        else {
            btnFlipPrev.setEnabled(true);
            btnFlipNext.setEnabled(true);
            btnFlipNext.setText("Submit");
            viewFlipper.showNext();
            int selectedChild = viewFlipper.getDisplayedChild();
            setupViewFiliperAndTabs(selectedChild);
        }



    }


    ScrollView scrollView;




    ViewFlipper viewFlipper;

    String selectedGender = "1";

    Button btnFlipPrev, btnFlipNext;

    TextView tvPatientReg, tvAppointmentDetails, tvInsurance;

    private String iCardFrontImgPath = "", iCardBackImgPath = "",
            idCardFrontImgPath = "", idCardBackImgPath = "";

    private ImageView ivICfrontImg, ivDeleteICfrontImg, ivICbackImg, ivDeleteICbackImg,
            ivIDfrontImg, ivDeleteIDfrontImg, ivIDbackImg, ivDeleteIDbackImg;

    RadioGroup radioGroupGender;
    RadioButton radioButtonMale,radioButtonFemale,radioButtonOthers;

    EditText etSignupFname, etSignupLname, etSignupEmail, etSignupPhone, etSignupBirthdate, etSignupZipcode, etSignupPass,etSignupCity,etSignConfirmPass,
            etSignupAddress, etpolicyNumberMemberId, etInsuranceCode, etInuranceGroup;
    Button btnSubmitSignup, btnSelectInsurance,btnAddNewInsurance;

    Spinner spScheduleLocation, spInsurancePayer,spStates;

//    RadioGroup rgSignupAppt;
//    TextView txtAppointType;
    ArrayList<PayerBean> payerBeens;
    ArrayList<StatesBeans> statesBeans;

    ArrayList<Organization> organizationBeans;

    ArrayList<ScheduleLocModel> scheduleLocModels;
    String scheduleLoc = "";
    String payerName = "";
    String location_message;
    String waiting_message;
    String after_doctor_assign_message;
    String appointmentType = "";

    String stateName ="";
    String state_Code = "";

    boolean isSelectedFormRv = false;

    public int formSize = 0;


    public static String userId = "";

    public static String otpTimerStatus = "0";


    public static String signupUsername = "", signupPassword = "";
    public static boolean is_signup_successfull = false;

    ImageView ivPrivacyPolicyInfo, ivEndUserAgreementInfo, ivPatientAuthorizationInfo;
    public CheckBox cbPrivacyPolicy, cbEndUserAgreement, cbPatientAuthSignup;

    TextView patInsuranceError;

    RecyclerView patInsuranceRv;

    ArrayList<Organization> organizationArrayList;

    boolean isShowRv;
    PatientInsuranceAdapter patientInsuranceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_signup);

        activity = NewSignup.this;
        checkInternet = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        hideSoftKeyboard();

        patInsuranceRv = findViewById(R.id.select_form_rv);
        organizationArrayList = new ArrayList<>();
        patientInsuranceAdapter = new PatientInsuranceAdapter(this,organizationArrayList,this);
        patInsuranceRv.setLayoutManager(new LinearLayoutManager(this));
        patInsuranceRv.setAdapter(patientInsuranceAdapter);

        patInsuranceError = findViewById(R.id.patIns_error);

        scrollView = findViewById(R.id.scroll_view_registration);

        viewFlipper = findViewById(R.id.viewFlipperSignup);

        btnFlipNext = (Button) findViewById(R.id.btnFlipNext);
        btnFlipPrev = (Button) findViewById(R.id.btnFlipPrev);

        ivICfrontImg = findViewById(R.id.ivICfrontImg);
        ivDeleteICfrontImg = findViewById(R.id.ivDeleteICfrontImg);
        ivICbackImg = findViewById(R.id.ivICbackImg);
        ivDeleteICbackImg = findViewById(R.id.ivDeleteICbackImg);

        ivIDfrontImg = findViewById(R.id.ivIDfrontImg);
        ivDeleteIDfrontImg = findViewById(R.id.ivDeleteIDfrontImg);
        ivIDbackImg = findViewById(R.id.ivIDbackImg);
        ivDeleteIDbackImg = findViewById(R.id.ivDeleteIDbackImg);

        hideSoftKeyboard();

        etSignupFname = (EditText) findViewById(R.id.etSignupFname);
        etSignupLname = (EditText) findViewById(R.id.etSignupLname);
        etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
        etSignupPhone = (EditText) findViewById(R.id.etSignupPhone);
        etSignupBirthdate = (EditText) findViewById(R.id.etSignupBirthdate);
        etSignupZipcode = (EditText) findViewById(R.id.etSignupZipcode);
        etSignupPass = (EditText) findViewById(R.id.etSignupPass);
        etSignupAddress = (EditText) findViewById(R.id.etSignupAddress);
        etSignupCity = (EditText) findViewById(R.id.etSignupCity);
        etSignConfirmPass = (EditText)findViewById(R.id.etSignupConfirmPass);

        etpolicyNumberMemberId = (EditText) findViewById(R.id.etpolicyNumberMemberId);
        etInsuranceCode = (EditText) findViewById(R.id.etInsuranceCode);
        etInuranceGroup = (EditText) findViewById(R.id.etInuranceGroup);

        radioGroupGender = findViewById(R.id.rgSignupStates);
        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        radioButtonOthers = findViewById(R.id.radioOthers);

//        spScheduleLocation = findViewById(R.id.spScheduleLocation);
        spInsurancePayer = findViewById(R.id.spInsurancePayer);
        spStates = findViewById(R.id.spState);
//        rgSignupAppt = findViewById(R.id.rgSignupAppt);
//        txtAppointType = findViewById(R.id.txtAppointType);

        ivPrivacyPolicyInfo = findViewById(R.id.ivPrivacyPolicyInfo);
        ivEndUserAgreementInfo = findViewById(R.id.ivEndUserAgreementInfo);
        ivPatientAuthorizationInfo = findViewById(R.id.ivPatientAuthorizationInfo);
        cbPatientAuthSignup = findViewById(R.id.cbPatientAuthSignup);
        cbPrivacyPolicy = (CheckBox) findViewById(R.id.cbPrivacyPolicy);
        cbEndUserAgreement = (CheckBox) findViewById(R.id.cbEndUserAgreement);
        btnSubmitSignup = findViewById(R.id.btnSubmitSignup);
        btnAddNewInsurance = findViewById(R.id.add_new_btn);
        btnSelectInsurance = findViewById(R.id.select_insurance_btn);


//        rgSignupAppt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedRadioBtnID) {
//                RadioButton rb = findViewById(checkedRadioBtnID);
//                if (rb != null) {
//                    try {
//                        appointmentType = rb.getTag().toString();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        spScheduleLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
//                // TODO Auto-generated method stub
//                scheduleLoc = pos == 0 ? "" : scheduleLocModels.get(pos).getKey();
//                if (pos == 1) {
//                    new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle(getString(R.string.app_name))
//                            .setMessage(location_message)
//                            .setPositiveButton("Continue", (dialog, which) -> {
//
//                            }).setNegativeButton("Cancel", null).create().show();
//                    rgSignupAppt.setVisibility(View.GONE);
//                    txtAppointType.setVisibility(View.GONE);
//                } else {
//                    rgSignupAppt.setVisibility(View.VISIBLE);
//                    txtAppointType.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });

        btnSelectInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = 0;
                scrollView.setVisibility(View.GONE);
                btnAddNewInsurance.setBackgroundResource(R.drawable.button_drawable_white_selected);
                btnAddNewInsurance.setTextColor(getResources().getColor(R.color.theme_red));
                btnSelectInsurance.setTextColor(getResources().getColor(R.color.white));
                btnSelectInsurance.setBackgroundResource(R.drawable.button_drawable);
                selectInsuranceType = "surescript";
                if(isSelectedFormRv){
                    patInsuranceRv.setVisibility(View.VISIBLE);
                    patInsuranceError.setVisibility(View.GONE);
                }
                else {
                    patInsuranceRv.setVisibility(View.GONE);
                    patInsuranceError.setVisibility(View.VISIBLE);

                }

            }
        });

        btnAddNewInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = 1;
                scrollView.setVisibility(View.VISIBLE);
                btnSelectInsurance.setBackgroundResource(R.drawable.button_drawable_white_selected);
                btnSelectInsurance.setTextColor(getResources().getColor(R.color.theme_red));
                btnAddNewInsurance.setTextColor(getResources().getColor(R.color.white));
                btnAddNewInsurance.setBackgroundResource(R.drawable.button_drawable);
                patInsuranceError.setVisibility(View.GONE);
                patInsuranceRv.setVisibility(View.GONE);
                selectInsuranceType = "other";
            }
        });

        spInsurancePayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int payerPos, long l) {
                payerName = payerPos == 0 ? "" : payerBeens.get(payerPos).payer_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioFemale) {
                   selectedGender = "0";
                }else if (checkedId == R.id.radioMale) {
                    selectedGender = "1";
                }
                else if(checkedId == R.id.radioOthers){
                    selectedGender = "2";
                }
            }
        });

        spStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int statePos, long l) {
                stateName = statePos ==0 ?"":statesBeans.get(statePos).full_name;
                state_Code = statePos ==0?"":statesBeans.get(statePos).short_name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnFlipNext.setOnClickListener(v -> {
            DATA.print("--viewFlipper " + viewFlipper.getDisplayedChild());

//            viewFlipper.showNext();
//            setupViewFiliperAndTabs(selectedChild);
                int selectedChild = viewFlipper.getDisplayedChild();

                DATA.print("-- selectedChild btn next : " + selectedChild);

            if (selectedChild == 0) {
                if (isvalid()) {
                    System.out.println("selectedforms" + selectedTab + " " + isShowRv);

                }
            }
            else if(selectedChild ==1){

                if(selectedTab == 0 && isShowList){

                selectedFormData();


            }
            else{
                viewFlipper.showNext();
                btnFlipPrev.setEnabled(true);
                btnFlipNext.setText("Submit");
                btnFlipNext.setEnabled(true);
            }

            }
            else if(selectedChild == 2){


                if (Login.loginFrom.equalsIgnoreCase("social")) {
//                if (appointmentType.isEmpty()) {
//                    customToast.showToast("Please select Schedule location", 0, 0);
//                    return;
//                }
                    if (isvalid()) {
                        String fname = etSignupFname.getText().toString().trim();
                        String lname = etSignupLname.getText().toString().trim();
                        String email = etSignupEmail.getText().toString().trim();
                        String phone = etSignupPhone.getText().toString().trim();
                        String birthDate = etSignupBirthdate.getText().toString().trim();
                        String address = etSignupAddress.getText().toString().trim();
                        String zipcode = etSignupZipcode.getText().toString().trim();
                        String policyId = etpolicyNumberMemberId.getText().toString().trim();
                        String insuranceCode = etInsuranceCode.getText().toString().trim();
                        String insuranceGrp = etInuranceGroup.getText().toString().trim();


                        RequestParams params = new RequestParams();
                        params.put("hospital_id", prefs.getString("hospitalIdLogin" , ""));//selectedHospitalId
                        params.put("current_app", Login.CURRENT_APP);
                        params.put("first_name", fname);
                        params.put("last_name", lname);
                        params.put("phone", phone);
                        params.put("birthdate", birthDate);
                        params.put("email", email);
                        params.put("from_social", Login.fromSocial);
                        params.put("social_id", Login.socialId);
                        params.put("residency", address);
                        params.put("zipcode", zipcode);
                        try {
                            params.put("id_front", new File(idCardFrontImgPath));
                            params.put("id_back", new File(idCardBackImgPath));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        params.put("scheduling_location", scheduleLoc);
                        params.put("appointment_type", appointmentType);
                        params.put("insurance", payerName);
                        params.put("policy_number", policyId);
                        params.put("group", insuranceGrp);
                        params.put("code", insuranceCode);
                        try {
                            params.put("inc_front", new File(iCardFrontImgPath));
                            params.put("inc_back", new File(iCardBackImgPath));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        signupUsername = etSignupEmail.getText().toString().trim();
                        signupPassword = etSignupPass.getText().toString().trim();

                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("userPhoneFromSocial", phone);
                        ed.commit();

                        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP_NEW, "post", params, apiCallBack, activity);
                        apiManager.loadURL();

                    }
                }
                else {
//                if (appointmentType.isEmpty()) {
//                    customToast.showToast("Please select Schedule location", 0, 0);
//                    return;
//                }
                    if (!cbPrivacyPolicy.isChecked()) {
                        customToast.showToast("Please check and accept our Privacy Policy", 0, 0);
                    }
                    else if (!cbEndUserAgreement.isChecked()) {
                        customToast.showToast("Please check and accept our End User Agreement", 0, 0);
                    }
                    else if (!cbPatientAuthSignup.isChecked()) {
                        customToast.showToast("Please check and accept our Patient Authorization", 0, 0);
                    }
                    else if (isvalid()) {
                        String fname = etSignupFname.getText().toString().trim();
                        String lname = etSignupLname.getText().toString().trim();
                        String email = etSignupEmail.getText().toString().trim();
                        String phone = etSignupPhone.getText().toString().trim();
                        String birthDate = etSignupBirthdate.getText().toString().trim();
                        String address = etSignupAddress.getText().toString().trim();
                        String zipcode = etSignupZipcode.getText().toString().trim();
                        String policyId = etpolicyNumberMemberId.getText().toString().trim();
                        String insuranceCode = etInsuranceCode.getText().toString().trim();
                        String insuranceGrp = etInuranceGroup.getText().toString().trim();
                        String password = etSignupPass.getText().toString().trim();
                        RequestParams params = new RequestParams();
                        params.put("hospital_id", prefs.getString("hospitalIdLogin" , ""));//selectedHospitalId
                        params.put("current_app", Login.CURRENT_APP);
                        params.put("first_name", fname);
                        params.put("last_name", lname);
                        params.put("phone", phone);
                        params.put("birthdate", birthDate);
                        params.put("email", email);
                        params.put("from_social", "");
                        params.put("social_id", "");
                        params.put("residency", address);
                        params.put("zipcode", zipcode);
                        params.put("city",city);
                        params.put("state",state_Code);
                        params.put("gender",selectedGender);
                        params.put("password",password);

                        if(!selectInsuranceType.isEmpty()){
                            if(selectInsuranceType.equals("surescript")){
                                Log.d("--mySelectedForm", "onCreate: "+selectedOrganizationForm);
                                params.put("insurance_form",selectInsuranceType);
                                params.put("insurance_date",selectedOrganizationForm);
                            }
                            else if(selectInsuranceType.equals("other")){
                                params.put("insurance_form",selectInsuranceType);
                                params.put("insurance", payerName);
                                params.put("policy_number", policyId);
                                params.put("group", insuranceGrp);
                                params.put("code", insuranceCode);
                                try {
                                    params.put("inc_front", new File(iCardFrontImgPath));
                                    params.put("inc_back", new File(iCardBackImgPath));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }



                        try {
                            params.put("id_front", new File(idCardFrontImgPath));
                            params.put("id_back", new File(idCardBackImgPath));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
//                    params.put("scheduling_location", scheduleLoc);
//                    params.put("appointment_type", appointmentType);


                        signupUsername = etSignupEmail.getText().toString().trim();
                        signupPassword = etSignupPass.getText().toString().trim();

                        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP_NEW, "post", params, apiCallBack, activity);
                        apiManager.loadURL();
                    }
                }

            }

//                else if (selectedChild == 2) {
//
//                    if (selectedTab == 0) {
//
//                        selectedFormData();
//
//
//                    } else {
//                        btnFlipPrev.setEnabled(true);
//                        btnFlipNext.setEnabled(true);
//                    }
//
//
//                }


                setInd();

        });

        etSignupBirthdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //DATA.isDatePickerCallFromSignup = true;
                    DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        etSignupBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });


        btnFlipPrev.setOnClickListener(v -> {
            DATA.print("--viewFlipper " + viewFlipper.getDisplayedChild());
            viewFlipper.showPrevious();

            int selectedChild = viewFlipper.getDisplayedChild();

            setupViewFiliperAndTabs(selectedChild);

            DATA.print("-- selectedChild btn previos : " + selectedChild);

            if (selectedChild == 2) {
                //btnFlipNext.setEnabled(false);
                btnFlipPrev.setEnabled(true);

            }

            if(selectedChild == 1){
                btnFlipNext.setText("Next");

                btnFlipPrev.setEnabled(true);
                btnFlipNext.setEnabled(true);
                selectedTab = 0;
                scrollView.setVisibility(View.GONE);
                btnAddNewInsurance.setBackgroundResource(R.drawable.button_drawable_white_selected);
                btnAddNewInsurance.setTextColor(getResources().getColor(R.color.theme_red));
                btnSelectInsurance.setTextColor(getResources().getColor(R.color.white));
                btnSelectInsurance.setBackgroundResource(R.drawable.button_drawable);
                selectInsuranceType = "surescript";
                if(isSelectedFormRv){
                    patInsuranceRv.setVisibility(View.VISIBLE);
                    patInsuranceError.setVisibility(View.GONE);
                }
                else {
                    patInsuranceRv.setVisibility(View.GONE);
                    patInsuranceError.setVisibility(View.VISIBLE);

                }

            }

            if (selectedChild == 0) {
                btnFlipPrev.setEnabled(false);
            } else {
                btnFlipNext.setText("Next");
                btnFlipPrev.setEnabled(true);
            }

            setInd();
        });


        //========================Tabs================================================================================
        tvPatientReg = (TextView) findViewById(R.id.tvPatientReg);
        tvAppointmentDetails = (TextView) findViewById(R.id.tvAppointmentDetails);
        tvInsurance = (TextView) findViewById(R.id.tvInsurance);


        View.OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.tvPatientReg:
                    setupViewFiliperAndTabs(0);
                    break;
                case R.id.tvAppointmentDetails:
                    if (isvalid()) {
                        setupViewFiliperAndTabs(1);
                    }
                    break;
                case R.id.tvInsurance:
                    if (isvalid()) {
                        setupViewFiliperAndTabs(2);
                    }
                    break;
                case R.id.ivICfrontImg:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkPermission13()) {
                            callPicCardImgMethodSignup(1, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    else {
                        if (checkPermission()) {
                            callPicCardImgMethodSignup(1, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    break;
                case R.id.ivDeleteICfrontImg:
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to delete this file")
                            .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    iCardFrontImgPath = "";
                                    ivDeleteICfrontImg.setVisibility(View.GONE);
                                    ivICfrontImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                    ivICfrontImg.setScaleType(ImageView.ScaleType.CENTER);
                                }
                            })
                            .setNegativeButton("Not Now", null)
                            .create().show();
                    break;
                case R.id.ivICbackImg:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkPermission13()) {
                            callPicCardImgMethodSignup(2, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    else {
                        if (checkPermission()) {
                            callPicCardImgMethodSignup(2, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    break;
                case R.id.ivDeleteICbackImg:
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to delete this file")
                            .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    iCardBackImgPath = "";
                                    ivDeleteICbackImg.setVisibility(View.GONE);
                                    ivICbackImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                    ivICbackImg.setScaleType(ImageView.ScaleType.CENTER);
                                }
                            })
                            .setNegativeButton("Not Now", null)
                            .create().show();
                    break;


                case R.id.ivIDfrontImg:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkPermission13()) {
                            callPicCardImgMethodSignup(3, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    else {
                        if (checkPermission()) {
                            callPicCardImgMethodSignup(3, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    break;
                case R.id.ivDeleteIDfrontImg:
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to delete this file")
                            .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    idCardFrontImgPath = "";
                                    ivDeleteIDfrontImg.setVisibility(View.GONE);
                                    ivIDfrontImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                    ivIDfrontImg.setScaleType(ImageView.ScaleType.CENTER);
                                }
                            })
                            .setNegativeButton("Not Now", null)
                            .create().show();
                    break;
                case R.id.ivIDbackImg:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkPermission13()) {
                            callPicCardImgMethodSignup(4, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    else {
                        if (checkPermission()) {
                            callPicCardImgMethodSignup(4, NewSignup.this);
                        } else {
                            requestPermissions();
                        }
                    }
                    break;
                case R.id.ivDeleteIDbackImg:
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to delete this file")
                            .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    idCardBackImgPath = "";
                                    ivDeleteIDbackImg.setVisibility(View.GONE);
                                    ivIDbackImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                    ivIDbackImg.setScaleType(ImageView.ScaleType.CENTER);
                                }
                            })
                            .setNegativeButton("Not Now", null)
                            .create().show();
                    break;
                default:
                    break;
            }

            setInd();
            int selectedChild = viewFlipper.getDisplayedChild();
            DATA.print("-- selectedChild tab click : " + selectedChild);
            if (selectedChild == 2) {
                btnFlipNext.setEnabled(true);
                btnFlipNext.setText("Submit");
            } else {
                btnFlipNext.setText("Next");
                btnFlipNext.setEnabled(true);
            }

            if (selectedChild == 0) {
                btnFlipNext.setText("Next");
                btnFlipPrev.setEnabled(false);
            } else {
                btnFlipNext.setText("Next");
                btnFlipPrev.setEnabled(true);
            }
        };

        tvPatientReg.setOnClickListener(onClickListener);
        tvAppointmentDetails.setOnClickListener(onClickListener);
        tvInsurance.setOnClickListener(onClickListener);

        ivICfrontImg.setOnClickListener(onClickListener);
        ivDeleteICfrontImg.setOnClickListener(onClickListener);
        ivICbackImg.setOnClickListener(onClickListener);
        ivDeleteICbackImg.setOnClickListener(onClickListener);

        ivIDfrontImg.setOnClickListener(onClickListener);
        ivDeleteIDfrontImg.setOnClickListener(onClickListener);
        ivIDbackImg.setOnClickListener(onClickListener);
        ivDeleteIDbackImg.setOnClickListener(onClickListener);

        getLabels();

        getPayers();

        loadStates();

        btnSubmitSignup.setOnClickListener(view ->
        {
            if (Login.loginFrom.equalsIgnoreCase("social")) {
//                if (appointmentType.isEmpty()) {
//                    customToast.showToast("Please select Schedule location", 0, 0);
//                    return;
//                }
                if (isvalid()) {
                    String fname = etSignupFname.getText().toString().trim();
                    String lname = etSignupLname.getText().toString().trim();
                    String email = etSignupEmail.getText().toString().trim();
                    String phone = etSignupPhone.getText().toString().trim();
                    String birthDate = etSignupBirthdate.getText().toString().trim();
                    String address = etSignupAddress.getText().toString().trim();
                    String zipcode = etSignupZipcode.getText().toString().trim();
                    String policyId = etpolicyNumberMemberId.getText().toString().trim();
                    String insuranceCode = etInsuranceCode.getText().toString().trim();
                    String insuranceGrp = etInuranceGroup.getText().toString().trim();


                    RequestParams params = new RequestParams();
                    params.put("hospital_id", prefs.getString("hospitalIdLogin" , ""));//selectedHospitalId
                    params.put("current_app", Login.CURRENT_APP);
                    params.put("first_name", fname);
                    params.put("last_name", lname);
                    params.put("phone", phone);
                    params.put("birthdate", birthDate);
                    params.put("email", email);
                    params.put("from_social", Login.fromSocial);
                    params.put("social_id", Login.socialId);
                    params.put("residency", address);
                    params.put("zipcode", zipcode);
                    try {
                        params.put("id_front", new File(idCardFrontImgPath));
                        params.put("id_back", new File(idCardBackImgPath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    params.put("scheduling_location", scheduleLoc);
                    params.put("appointment_type", appointmentType);
                    params.put("insurance", payerName);
                    params.put("policy_number", policyId);
                    params.put("group", insuranceGrp);
                    params.put("code", insuranceCode);
                    try {
                        params.put("inc_front", new File(iCardFrontImgPath));
                        params.put("inc_back", new File(iCardBackImgPath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    signupUsername = etSignupEmail.getText().toString().trim();
                    signupPassword = etSignupPass.getText().toString().trim();

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("userPhoneFromSocial", phone);
                    ed.commit();

                    ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP_NEW, "post", params, apiCallBack, activity);
                    apiManager.loadURL();

                }
            }
            else {
//                if (appointmentType.isEmpty()) {
//                    customToast.showToast("Please select Schedule location", 0, 0);
//                    return;
//                }
                if (!cbPrivacyPolicy.isChecked()) {
                    customToast.showToast("Please check and accept our Privacy Policy", 0, 0);
                }
                else if (!cbEndUserAgreement.isChecked()) {
                    customToast.showToast("Please check and accept our End User Agreement", 0, 0);
                }
                else if (!cbPatientAuthSignup.isChecked()) {
                    customToast.showToast("Please check and accept our Patient Authorization", 0, 0);
                }
                else if (isvalid()) {
                    String fname = etSignupFname.getText().toString().trim();
                    String lname = etSignupLname.getText().toString().trim();
                    String email = etSignupEmail.getText().toString().trim();
                    String phone = etSignupPhone.getText().toString().trim();
                    String birthDate = etSignupBirthdate.getText().toString().trim();
                    String address = etSignupAddress.getText().toString().trim();
                    String zipcode = etSignupZipcode.getText().toString().trim();
                    String policyId = etpolicyNumberMemberId.getText().toString().trim();
                    String insuranceCode = etInsuranceCode.getText().toString().trim();
                    String insuranceGrp = etInuranceGroup.getText().toString().trim();
                    String password = etSignupPass.getText().toString().trim();
                    RequestParams params = new RequestParams();
                    params.put("hospital_id", prefs.getString("hospitalIdLogin" , ""));//selectedHospitalId
                    params.put("current_app", Login.CURRENT_APP);
                    params.put("first_name", fname);
                    params.put("last_name", lname);
                    params.put("phone", phone);
                    params.put("birthdate", birthDate);
                    params.put("email", email);
                    params.put("from_social", "");
                    params.put("social_id", "");
                    params.put("residency", address);
                    params.put("zipcode", zipcode);
                    params.put("city",city);
                    params.put("state",state_Code);
                    params.put("gender",selectedGender);
                    params.put("password",password);

                    if(!selectInsuranceType.isEmpty()){
                        if(selectInsuranceType.equals("surescript")){
                            Log.d("--mySelectedForm", "onCreate: "+selectedOrganizationForm);
                            params.put("insurance_form",selectInsuranceType);
                            params.put("insurance_date",selectedOrganizationForm);
                        }
                        else if(selectInsuranceType.equals("other")){
                            params.put("insurance_form",selectInsuranceType);
                            params.put("insurance", payerName);
                            params.put("policy_number", policyId);
                            params.put("group", insuranceGrp);
                            params.put("code", insuranceCode);
                            try {
                                params.put("inc_front", new File(iCardFrontImgPath));
                                params.put("inc_back", new File(iCardBackImgPath));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }



                    try {
                        params.put("id_front", new File(idCardFrontImgPath));
                        params.put("id_back", new File(idCardBackImgPath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    params.put("scheduling_location", scheduleLoc);
//                    params.put("appointment_type", appointmentType);


                    signupUsername = etSignupEmail.getText().toString().trim();
                    signupPassword = etSignupPass.getText().toString().trim();

                    ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP_NEW, "post", params, apiCallBack, activity);
                    apiManager.loadURL();
                }
            }
        });

        if (Login.loginFrom.equalsIgnoreCase("social")) {
            etSignupFname.setText(Login.fnameFromSocial);
            etSignupEmail.setText(Login.emailFromSocial);
        } else if (Login.loginFrom.equalsIgnoreCase("signup")) {
            etSignupFname.setText("");
            etSignupEmail.setText("");
        }

        ivPrivacyPolicyInfo.setOnClickListener(v -> new GloabalMethods(activity).showWebviewDialog(DATA.PRIVACY_POLICY_URL, "Privacy Policy"));
        ivEndUserAgreementInfo.setOnClickListener(v -> new GloabalMethods(activity).showWebviewDialog(DATA.USER_AGREEMENT_URL, "OnlineCare and its partner’s Virtual Care End User Agreement"));
        ivPatientAuthorizationInfo.setOnClickListener(view -> new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "/" + ApiManager.PATIENT_AUTH + "/" + Login.HOSPITAL_ID_EMCURA, "Patient Authorization"));

        //customToast.showToast("ID "+prefs.getString("hospitalIdLogin", "") , 0 , 1);

    }//end Oncreate



    public void checkPatientRegistration(String phone,String email){
        RequestParams params = new RequestParams();
        params.put("email",email);
        params.put("phone",phone);


        ApiManager apiManager = new ApiManager(ApiManager.CHECK_PATIENT_REGISTRATION,
                "post",params,apiCallBack, activity);
        apiManager.loadURL();

    }

    void setupViewFiliperAndTabs(int pos) {
        switch (pos) {
            case 0:
                tvPatientReg.setBackgroundColor(getResources().getColor(R.color.theme_red));
                //tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
                tvAppointmentDetails.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                //tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvInsurance.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                viewFlipper.setDisplayedChild(0);
                break;
//            case 1:
//                tvPatientReg.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//                //tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
//                tvAppointmentDetails.setBackgroundColor(getResources().getColor(R.color.theme_red));
//                //tvObjective.setTextColor(Color.parseColor("#FFFFFF"));
//                tvInsurance.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//                //tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
//                //tvPlan.setTextColor(getResources().getColor(R.color.theme_red));
//
//                viewFlipper.setDisplayedChild(1);
//                break;
            case 1:
                tvPatientReg.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                //tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvAppointmentDetails.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                //tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvInsurance.setBackgroundColor(getResources().getColor(R.color.theme_red));
                //tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
                viewFlipper.setDisplayedChild(1);
                break;

            case 2:
                tvPatientReg.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                //tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvAppointmentDetails.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                //tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvInsurance.setBackgroundColor(getResources().getColor(R.color.theme_red));
                //tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
                viewFlipper.setDisplayedChild(2);
                break;

            default:

                break;
        }
    }

    public void setInd() {
        ImageView cir1 = (ImageView) findViewById(R.id.cir1);
//        ImageView cir2 = (ImageView) findViewById(R.id.cir2);
        ImageView cir3 = (ImageView) findViewById(R.id.cir3);
        int pos = viewFlipper.getDisplayedChild();

        switch (pos) {
            case 0:
                cir1.setImageResource(R.drawable.indicator_blavk_unselected);
//                cir2.setImageResource(R.drawable.indicator_black_unselected);
                cir3.setImageResource(R.drawable.indicator_black_unselected);
                break;
//            case 1:
//                cir1.setImageResource(R.drawable.indicator_black_unselected);
////                cir2.setImageResource(R.drawable.indicator_blavk_unselected);
//                cir3.setImageResource(R.drawable.indicator_black_unselected);
//                break;
            case 1:
                cir1.setImageResource(R.drawable.indicator_black_unselected);
//                cir2.setImageResource(R.drawable.indicator_black_unselected);
                cir3.setImageResource(R.drawable.indicator_blavk_unselected);
                break;
        }
    }

    @Override
    public void displayICfrontImg(String imgPath) {
        try {
            iCardFrontImgPath = imgPath;
            ivDeleteICfrontImg.setVisibility(View.VISIBLE);

            File imageFile = new File(iCardFrontImgPath);
            String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivICfrontImg);
            ivICfrontImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayICbackImg(String imgPath) {
        try {
            iCardBackImgPath = imgPath;
            ivDeleteICbackImg.setVisibility(View.VISIBLE);

            File imageFile = new File(iCardBackImgPath);
            String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivICbackImg);
            ivICbackImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayIDcardFrontImg(String imgPath) {
        try {
            idCardFrontImgPath = imgPath;
            ivDeleteIDfrontImg.setVisibility(View.VISIBLE);

            File imageFile = new File(idCardFrontImgPath);
            String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivIDfrontImg);
            ivIDfrontImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayIDcardBackImg(String imgPath) {
        try {
            idCardBackImgPath = imgPath;
            ivDeleteIDbackImg.setVisibility(View.VISIBLE);

            File imageFile = new File(idCardBackImgPath);
            String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivIDbackImg);
            ivIDbackImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //pick Insurance card image front + back on add new insurance
    LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;

    public void callPicCardImgMethodSignup(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface) {
        liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
        liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (liveCareInsuranceCardhelper != null) {
            //Insurance card image
            liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*============ Check Permission Code ===================*/
    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String[] PERMISSIONSANDROID13 = {
            android.Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
    };

    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private boolean checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        if (granted) {

        } else {
            requestPermissions();
        }
        return granted;
    }

    private boolean checkPermission13() {
        boolean granted = true;
        for (String per : PERMISSIONSANDROID13) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        if (granted) {

        } else {
            requestPermissions();
        }
        return granted;
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity, PERMISSIONSANDROID13, PERMISSION_REQ_CODE);
        }
        else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {

            } else {
                showDeniedResponse(grantResults);
                // customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
            }
        }
    }


    private void showDeniedResponse(int[] grantResults) {

        boolean shouldShowDialog = false;
        String msg = getString(R.string.allow_camera_permission_msg);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(activity, "Permission not granted for: "+permissionFeatures.values()[i], Toast.LENGTH_SHORT).show();
                //msg = msg + "\n* "+permissionFeatures.values()[i];
                shouldShowDialog = true;
            }
        }


        if (shouldShowDialog) {
            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    //.setTitle("Permission ")
                    .setMessage(msg)
                    .setNegativeButton("Allow Permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openAppSettings();
                            /*checkPermissions();*/
                        }
                    })
                    .setPositiveButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    /*============ Check Permission Code ===================*/


    public void getLabels() {
        String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
        if (!TextUtils.isEmpty(app_general_labels)) {
            parseLabelsData(app_general_labels);
            ApiManager.shouldShowLoader = false;
        }
        ApiManager.shouldShowLoader = false;
        ApiManager apiManager = new ApiManager(ApiManager.LABELS, "post", null, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getPatientInsurance(){

        RequestParams params = new RequestParams();
        params.put("first_name",fname);
        params.put("last_name",lname);
        params.put("residency",address);
        params.put("city",city);
        params.put("state",state_Code);
        params.put("phone",phone);
        params.put("email",email);
        params.put("zipcode",zipcode);
        params.put("gender",selectedGender);
        params.put("birthdate",birthDate);
        params.put("phone",phone);
        params.put("email",email);

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_INSURANCE,"post",params,this, activity);
        apiManager.loadURL();
    }




    String msg = "";
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.PATIENT_SIGNUP_NEW)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");

                if (jsonObject.has("msg")) {
                    msg = jsonObject.getString("msg");
                }

                if (jsonObject.has("data"))
                {
                JSONObject data = jsonObject.getJSONObject("data");
                userId = data.getString("user_id");

                if (status.equalsIgnoreCase("success")) {

                    SharedPreferences.Editor ed = prefs.edit();
                    if (jsonObject.has("folder_name")) {
                        ed.putString("folder_name", jsonObject.getString("folder_name"));
                    }

                    ed.putBoolean("UserSignupSuccess", true);
                    ed.putString("id", userId);
                    ed.putString("userEnteremail", etSignupEmail.getText().toString().trim());
                    ed.putString("userEnterPhone", etSignupPhone.getText().toString().trim());
                    ed.commit();


                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(msg)
                            .setPositiveButton("Done", (dialog, which) -> {
                                otpTimerStatus = "1";
                                openActivity.open(ActivityVerificationOtp.class, true);
                            })
                            .create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    //customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."

                    is_signup_successfull = true;
                }
                } else {

                    customToast.showToast(msg, 0, 1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (apiName.equalsIgnoreCase(ApiManager.LABELS)) {
            parseLabelsData(content);
        }
        else if (apiName.equalsIgnoreCase(ApiManager.GET_PAYERS)) {
            try {
                payerBeens = new ArrayList<PayerBean>();
                PayerBean payerBean;
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String payer_id = jsonArray.getJSONObject(i).getString("payer_id");
                    String payer_name = jsonArray.getJSONObject(i).getString("payer_name");

                    payerBean = new PayerBean(id, payer_id, payer_name);
                    payerBeens.add(payerBean);
                    payerBean = null;
                }
                ArrayAdapter<PayerBean> payerBeanArrayAdapter =
                        new ArrayAdapter<>(activity, R.layout.spinner_item_lay,
                                android.R.id.text1, payerBeens);
                payerBeens.add(0, new PayerBean("", "", "Select"));
                spInsurancePayer.setAdapter(payerBeanArrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            }
        }


        else if (apiName.equalsIgnoreCase(ApiManager.GET_STATES)) {
            try {
                // Assuming 'content' is the API response string
                JSONObject jsonObject = new JSONObject(content);

                // Check if the desired data is within a key named "data" (modify based on your API structure)
                if (jsonObject.has("data") && jsonObject.get("data") instanceof JSONArray) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    statesBeans = new ArrayList<StatesBeans>();
                    StatesBeans statesBean;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject stateObject = jsonArray.getJSONObject(i);
                        String id = stateObject.getString("id");
                        String full_name = stateObject.getString("full_name");
                        String short_name = stateObject.getString("short_name");

                        statesBean = new StatesBeans(id, full_name, short_name);
                        statesBeans.add(statesBean);
                        statesBean = null;
                    }
                } else {
                    // Handle the case where "data" is missing or not a JSONArray (optional)
                    customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
                }

                // Assuming statesBeans is populated correctly at this point
                ArrayAdapter<StatesBeans> statesArrayAdapter =
                        new ArrayAdapter<StatesBeans>(activity, R.layout.spinner_item_lay,
                                android.R.id.text1, statesBeans);
                statesBeans.add(0, new StatesBeans("", "Select", ""));
                spStates.setAdapter(statesArrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            }

        }

        else if(apiName.equalsIgnoreCase(ApiManager.CHECK_PATIENT_REGISTRATION)){

            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");

                if(status.equalsIgnoreCase("success")){
                    isValidEmailPhone = true;
                    validData();


                }else {
                    String error = jsonObject.getString("msg");
                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage(error)
                            .setPositiveButton("OK", null)
                            .create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {


                        }
                    });
                    alertDialog.show();
                         isValidEmailPhone = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENT_INSURANCE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                organizationBeans = new ArrayList<>();

                String jsonString = "..."; // Replace with your unknown JSON response

                // Parse the JSON string


                // Check if "organization" key exists
                if (jsonObject.has("organization")) {
                    try {
                        // Get the "organization" array (assuming it's an array)
                        JSONArray organizationArray = jsonObject.getJSONArray("organization");
                        if(organizationArray.length() == 0){
                            patInsuranceError.setText("No insurance found.");
                            patInsuranceError.setVisibility(View.VISIBLE);
                            System.out.println("orgEmpty");
                            patInsuranceRv.setVisibility(View.GONE);
                            isSelectedFormRv = false;
                            isShowRv = false;
                            setShowList(false);



                        } else if(! organizationArray.getJSONObject(0).optString("error_message").isEmpty()){

                            String errorMessage = organizationArray.getJSONObject(0).getString("error_message");
                            patInsuranceError.setText(errorMessage);
                            patInsuranceError.setVisibility(View.VISIBLE);
                            System.out.println("Extracted error: " + errorMessage);
                            isSelectedFormRv = false;
                            patInsuranceRv.setVisibility(View.GONE);
                            isShowRv = false;
                            setShowList(false);

                        }
                        else {

                            isShowRv = true;
                            isSelectedFormRv = true;
                            patInsuranceRv.setVisibility(View.VISIBLE);
                            patInsuranceError.setVisibility(View.GONE);
                            organizationArrayList.clear();
                            setShowList(true);

                            for(int i=0; i<jsonObject.getJSONArray("organization").length();i++){
                                Organization organization = new Organization();
                                organization.setOrg_name(organizationArray.getJSONObject(0).getString("org_name"));
                                organization.setMember_id(organizationArray.getJSONObject(0).getString("member_id"));
                                organization.setIdentification_code(organizationArray.getJSONObject(0).getString("identification_code"));
                                organization.setCov(organizationArray.getJSONObject(0).optString("cov"));// not coming from server
                                organization.setPlans(organizationArray.getJSONObject(0).optString("plans"));// not coming from server
                               // organization.setAlt(organizationArray.getJSONObject(0).getString("alt"));// not coming from server
                                //organization.setFsl(organizationArray.getJSONObject(0).getString("fsl"));// not coming from server

                                JSONObject patientInfoJson = organizationArray.getJSONObject(0).getJSONObject("patient_info");
                                PatientInfo patientInfo = new PatientInfo();

                                patientInfo.setFirst_name(patientInfoJson.getString("first_name"));
                                patientInfo.setLast_name(patientInfoJson.getString("last_name"));
                                patientInfo.setMiddle_name(patientInfoJson.optString("middle_name", ""));  // Handle optional field
                                patientInfo.setSuffix(patientInfoJson.optString("suffix", ""));        // Handle optional field
                                patientInfo.setMember_id(patientInfoJson.getString("member_id"));
                                patientInfo.setStreet_address(patientInfoJson.getString("street_address"));
                                patientInfo.setStreet_address_2(patientInfoJson.optString("street_address_2", null));  // Handle optional field
                                patientInfo.setCity(patientInfoJson.getString("city"));
                                patientInfo.setState(patientInfoJson.getString("state"));
                                patientInfo.setZip(patientInfoJson.getString("zip"));
                                patientInfo.setDob(patientInfoJson.getString("dob"));
                                patientInfo.setGender(patientInfoJson.getString("gender"));
                                organization.setPatient_info(patientInfo);


                                organizationArrayList.add(organization);
                                formSize = organizationArrayList.size();

                                patientInsuranceAdapter.notifyDataSetChanged();
                            }
                            Log.d("--orgSize", "fetchDataCallback: "+organizationArrayList.size());




                            // Print the extracted org_name
                            System.out.println("Extracted org_name: "+organizationArrayList.size());

                        }
                        // Extract the "org_name"

                    } catch (Exception e) {
                        // Handle potential exceptions (e.g., not an array or missing key)
                        System.err.println("Error extracting org_name: " + e.getMessage());
                        DATA.print(e.getMessage());
                    }
                } else {
                    System.out.println("Organization key not found in the JSON response.");
                }


//                if(organizationBeans.isEmpty()){
//                    customToast.showToast("list is empty.",0,0);
//                }
//                else {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String fname = jsonArray.getJSONObject(i).getJSONObject("patient_info").getString("first_name");
//                        String lname = jsonArray.getJSONObject(i).getJSONObject("patient_info").getString("last_name");
//                        String memberId = jsonArray.getJSONObject(i).getString("member_id");
//                        String identificationCode = jsonArray.getJSONObject(i).getString("identification_code");
//                        String cov = jsonArray.getJSONObject(i).getString("cov");
//                        String plans = jsonArray.getJSONObject(i).getString("plans");
//
//                    }
//
//                }

            }
            catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }



        }



        }







    private void parseLabelsData(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            sharedPrefsHelper.save("app_general_labels", content);

            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject scheduleLoc = data.getJSONObject("scheduling_location");
            JSONObject appointmentType = data.getJSONObject("appointment_type");

            location_message = data.getString("location_message");
            waiting_message = data.getString("waiting_message");
            after_doctor_assign_message = data.getString("after_doctor_assign_message");

            sharedPrefsHelper.save("waiting_message", waiting_message);
            sharedPrefsHelper.save("after_doctor_assign_message", after_doctor_assign_message);


/*            if (scheduleLoc != null) {
                scheduleLocModels = new ArrayList<>();
                Iterator iter = scheduleLoc.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = scheduleLoc.getString(key);
                    scheduleLocModels.add(new ScheduleLocModel(key, value));
                }
                ArrayAdapter<ScheduleLocModel> scheduleLocModelArrayAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item_lay, android.R.id.text1, scheduleLocModels);
                scheduleLocModels.add(0, new ScheduleLocModel("", "Select"));
                spScheduleLocation.setAdapter(scheduleLocModelArrayAdapter);
            }
            if (appointmentType != null) {
                RadioButton radioButton = null;
                Iterator iter = appointmentType.keys();
//                rgSignupAppt.removeAllViews();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = appointmentType.getString(key);
                    radioButton = new RadioButton(this);
                    radioButton.setText(value);

                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked}, //disabled
                                    new int[]{android.R.attr.state_checked} //enabled
                            },
                            new int[]{
                                    getResources().getColor(R.color.theme_red),//disabled
                                    getResources().getColor(R.color.theme_red) //enabled
                            }
                    );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        radioButton.setButtonTintList(colorStateList);
                    }

                    radioButton.setTag(key);
                    radioButton.setId(View.generateViewId());
//                    rgSignupAppt.setOrientation(RadioGroup.VERTICAL);
                    radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    rgSignupAppt.addView(radioButton);

                    if (key.equalsIgnoreCase("COV19SW")) {
                        radioButton.setChecked(true);
                    }
                }
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
        }

    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void getPayers() {
        String reqURL = ApiManager.GET_PAYERS;
        ApiManager apiManager = new ApiManager(reqURL, "get", null, this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }



    public void loadStates()
    {
        ApiManager.shouldShowLoader = false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_STATES,"get",null,this, activity);
        apiManager.loadURL();
    }


    public boolean isvalid() {
         fname = etSignupFname.getText().toString().trim();
         lname = etSignupLname.getText().toString().trim();
         email = etSignupEmail.getText().toString().trim();
         password = etSignupPass.getText().toString().trim();
         phone = etSignupPhone.getText().toString().trim();
         birthDate = etSignupBirthdate.getText().toString().trim();
         address = etSignupAddress.getText().toString().trim();
         zipcode = etSignupZipcode.getText().toString().trim();
         confirmPassword = etSignConfirmPass.getText().toString().trim();
         city = etSignupCity.getText().toString().trim();

        if (TextUtils.isEmpty(fname)) {
            etSignupFname.setError("This field is required");
            customToast.showToast("Please enter first name", 0, 0);
            return false;
        }
        if (TextUtils.isEmpty(lname)) {
            etSignupLname.setError("This field is required");
            customToast.showToast("Please enter last name", 0, 0);
        }
        if (TextUtils.isEmpty(email)) {
            etSignupEmail.setError("This field is required");
            customToast.showToast("Please enter email", 0, 0);
            return false;
        }
       /* if (TextUtils.isEmpty(password)) {
            etSignupPass.setError("This field is required");
            customToast.showToast("Please enter password", 0, 0);
            return false;
        }*/
        if (TextUtils.isEmpty(phone)) {
            etSignupPhone.setError("This field is required");
            customToast.showToast("Please enter phone", 0, 0);
            return false;
        }
        if (TextUtils.isEmpty(birthDate)) {
            etSignupBirthdate.setError("This field is required");
            customToast.showToast("Please enter birthdate", 0, 0);
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            etSignupAddress.setError("This field is required");
            customToast.showToast("Please enter address", 0, 0);
            return false;
        }
        if(TextUtils.isEmpty(city)){
            etSignupCity.setError("This field is required");
            customToast.showToast("Please enter city",0,0);
            return false;
        }

        if(stateName.equals("")){
            customToast.showToast("Please select state",0,0);
            return false;
        }
        if (TextUtils.isEmpty(zipcode)) {
            etSignupZipcode.setError("This field is required");
            customToast.showToast("Please enter zipcode", 0, 0);
            return false;
        }
        if(radioGroupGender.getCheckedRadioButtonId() == -1){
            customToast.showToast("Please select your gender",0,0);
            return false;
        }

        if(TextUtils.isEmpty(password)){
            etSignupPass.setError("This field is required");
            customToast.showToast("Please enter password",0,0);
            return false;
        }
        if(!isValidPassword(password)){
            etSignupPass.setError("Weak password");
            customToast.showToast("Password must have 1 capital letter, 1 small letter and a number",0,0);
            return false;
        }

        if(TextUtils.isEmpty(confirmPassword)){
            etSignConfirmPass.setError("This field is required");
            customToast.showToast("Please confirm your password",0,0);
            return false;
        }

        if(!confirmPassword.equals(password)){
            etSignConfirmPass.setError("password mismatch");
            customToast.showToast("Please type same password",0,0);
            return false;
        }
        if (idCardFrontImgPath.isEmpty()) {
            customToast.showToast("Please add id card front image", 0, 0);
            return false;
        }



        if (idCardBackImgPath.isEmpty()) {
            customToast.showToast("Please add id card back image", 0, 0);
            return false;
        } else if (!GloabalMethods.isValidEmail(etSignupEmail.getText())) {
            customToast.showToast("Please enter valid email address", 0, 0);
            return false;
        }
        else if (!GloabalMethods.isValidEmail(etSignupEmail.getText())) {
            customToast.showToast("Please enter your valid email adderss.", 0, 0);
        return false;
        }
        else {
            checkPatientRegistration(phone,email);
            // temporay keep here
           // getPatientInsurance();
            return  true;
        }
    }




    @Override
    public void onBackPressed() {
            //super.onBackPressed();
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Are you sure Do you want to exit?")
                    .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Not Now", null)
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

    @Override
    public void selectedForm(Organization organization) {
        selectedOrganizationForm = organization;
        Log.d("--organizationForms", "selectedForm: "+selectedOrganizationForm.getPatient_info().first_name);


    }

    @Override
    public void unSelectedForm(Organization organization) {
        selectedOrganizationForm = null;
    }
}
