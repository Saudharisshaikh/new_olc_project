package com.app.OnlineCareUS_Pt;

import static com.app.OnlineCareUS_Pt.util.SharedPrefsHelper.PKG_AMOUNT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.OnlineCareUS_Pt.adapter.RvBodyPartAdapter;
import com.app.OnlineCareUS_Pt.adapter.RvPainAdapter;
import com.app.OnlineCareUS_Pt.adapter.VVReportImagesAdapter;
import com.app.OnlineCareUS_Pt.api.ApiCallBack;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.model.ConditionsModel;
import com.app.OnlineCareUS_Pt.model.StateBean;
import com.app.OnlineCareUS_Pt.model.SymptomsModel;
import com.app.OnlineCareUS_Pt.paypal.PaymentLiveCare;
import com.app.OnlineCareUS_Pt.util.ActionSheetPopup;
import com.app.OnlineCareUS_Pt.util.CheckInternetConnection;
import com.app.OnlineCareUS_Pt.util.CustomToast;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.Database;
import com.app.OnlineCareUS_Pt.util.ExpandableHeightGridView;
import com.app.OnlineCareUS_Pt.util.GloabalMethods;
import com.app.OnlineCareUS_Pt.util.HideShowKeypad;
import com.app.OnlineCareUS_Pt.util.LiveCareInsurance;
import com.app.OnlineCareUS_Pt.util.OpenActivity;
import com.app.OnlineCareUS_Pt.util.RecyclerItemClickListener;
import com.app.OnlineCareUS_Pt.util.SharedPrefsHelper;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetLiveCare extends BaseActivity {

    SharedPreferences prefs;
    Database db;
    AppCompatActivity activity;
    ApiCallBack apiCallBack;
    CustomToast customToast;
    OpenActivity openActivity;
    CheckInternetConnection checkInternetConnection;
    HideShowKeypad hideShowKeypad;
    SharedPrefsHelper sharedPrefsHelper;

    String sympArr[];
    ArrayList<StateBean> stateBeans;
    private String selectedSympId = "";
    private String selectedConditionId = "0";
    private boolean isSymptomSelectd = false;

    String userState = "MI";

    ImageView imgAd;


    ArrayAdapter<String> symptomsAdapter, conditionsAdapter;
    Spinner spgetLivecareState;//spinner is hidden in UI, for update state uncommit this
    Spinner spLiveSelectSymptm, spLiveSelectCondtn, spPainSeverity;
    Button btnLiveSbmtSymptom, btnLiveShareReports, btnPharmacy;
    EditText etLiveExtraInfo, etLiveZipCode, etPainWhere,
            etOTBPSys, etOTBPDia, etOTHR, etOTRespirations, etOTO2Saturations, etOTBloodSugar, etOTTemperature, etOTHeightFt, etOTHeightIn, etOTWeight, etgetLiveCareState;
    ImageView ic_mike_LiveExtraInfo;
    AutoCompleteTextView autoLiveTvSymptoms; //autoTvDrSpeciality;
    TextView tvNumReprtsSelctd, tvSelPharmacy, tvSelPharmacyAddress;

    public static final String[] painSeverity = {"No Pain", "Mild", "Moderate", "Severe", "Very Severe", "Worst pain possible"};
    String selectedPainSeverity = "";

    GloabalMethods gloabalMethods;
    BroadcastReceiver showSelectedPharmacyBroadcast;

    CheckBox cbPatientAuth;

    public Button btnSelectImages;
    ExpandableHeightGridView gvReportImages;

    Spinner spSymptomNew;
    ExpandableHeightGridView gvSymptomNew, gvConditionNew;
    RecyclerView rvPainSeverity;
    public static final int[] painSevEmojies = {R.drawable.pain_nopain, R.drawable.pain_mild, R.drawable.pain_moderate, R.drawable.pain_severe, R.drawable.pain_verysevere, R.drawable.pain_worstpainpossible};

    List<SymptomsModel> symptomsModels;


    RadioGroup rgInternational;
    RadioButton radioInternationalYes, radioInternationalNo;

    RecyclerView rvBodyPart;
    public static final String[] bodyParts = {
            "Neck",
            "Head",
            "Left Arm",
            "Right Arm",
            "Left Leg",
            "Right Leg",
            "Stomach",
            "Upper Back",
            "Lower Back",
            "Left Shoulder",
            "Right Shoulder",
            "Left Knee",
            "Right Knee",
            "Left Wrist",
            "Right Wrist",
            "Left Ankle",
            "Right Ankle",
            "Chest",
            "Other"
    };
    public static final int[] bodyPartIcons = {
            R.drawable.bp_neck,
            R.drawable.bp_head,
            R.drawable.bp_l_arm,
            R.drawable.bp_r_arm,
            R.drawable.bp_l_leg,
            R.drawable.bp_r_leg,
            R.drawable.bp_stomach,
            R.drawable.bp_upper_back,
            R.drawable.bp_lower_back,
            R.drawable.bp_shoulder_left,
            R.drawable.bp_shoulder_right,
            R.drawable.bp_knee_left,
            R.drawable.bp_knee_right,
            R.drawable.bp_wrist_left,
            R.drawable.bp_wrist_right,
            R.drawable.bp_ankle_left,
            R.drawable.bp_ankle_right,
            R.drawable.bp_chest,
            R.drawable.bp_other
    };
	/*public static final String[] bodyParts = {
			"Head",
			"Chest",
			"Neck",
			"Left Arm",
			"Right Arm",
			"Left Wrist",
			"Right Wrist",
			"Left Shoulder",
			"Right Shoulder",
			"Upper Back",
			"Lower Back",
			"Stomach",
			"Left Leg",
			"Right Leg",
			"Left Knee",
			"Right Knee",
			"Left Ankle",
			"Right Ankle",
			"Other"
	};
	public static final int[] bodyPartIcons = {
			R.drawable.bp_head,
			R.drawable.bp_chest,
			R.drawable.bp_neck,
			R.drawable.bp_l_arm,
			R.drawable.bp_r_arm,
			R.drawable.bp_wrist_left,
			R.drawable.bp_wrist_right,
			R.drawable.bp_shoulder_left,
			R.drawable.bp_shoulder_right,
			R.drawable.bp_upper_back,
			R.drawable.bp_lower_back,
			R.drawable.bp_stomach,
			R.drawable.bp_l_leg,
			R.drawable.bp_r_leg,
			R.drawable.bp_knee_left,
			R.drawable.bp_knee_right,
			R.drawable.bp_ankle_left,
			R.drawable.bp_ankle_right,
			R.drawable.bp_other
	};*/

    RvPainAdapter rvPainAdapter;
    RvBodyPartAdapter rvBodyPartAdapter;

    boolean isForEdit;
    LinearLayout layHideOnEdit1, layHideOnEdit2;

    @Override
    protected void onStart() {
        registerReceiver(showSelectedPharmacyBroadcast, new IntentFilter(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(showSelectedPharmacyBroadcast);
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (!DATA.isReprtSelected || DATA.NumOfReprtsSelected == 0) {
            tvNumReprtsSelctd.setText("");
            tvNumReprtsSelctd.setVisibility(View.GONE);
        } else {
            tvNumReprtsSelctd.setText("Number of reports selected: " + DATA.NumOfReprtsSelected);
            tvNumReprtsSelctd.setVisibility(View.VISIBLE);
        }

        if (DATA.isLiveCarePaymentDone && DATA.isFreeCare) {

            if (PaymentLiveCare.insuranceOrCredit) {
                getLiveCareCall(PaymentLiveCare.payment_typeIC);
            } else {
                getLiveCareCall("free");
            }
        } else if (DATA.isLiveCarePaymentDone && (!DATA.isFreeCare)) {
            getLiveCareCall("paypal");
        } else if (ActivityCheckInsurance.Frominsurance == true) {
            getLiveCareCall("NewInsurance");
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_live_care);

        isForEdit = getIntent().getBooleanExtra("isForEdit", false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Clinical Care");//Clinical Care  Immediate Care
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // getActionBar().setHomeButtonEnabled(true);
        }

        activity = GetLiveCare.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        db = new Database(activity);

        tvNumReprtsSelctd = findViewById(R.id.tvNumReprtsSelctd);
        tvSelPharmacy = findViewById(R.id.tvSelPharmacy);
        tvSelPharmacyAddress = findViewById(R.id.tvSelPharmacyAddress);
        imgAd = findViewById(R.id.imgAd);
        imgAd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DATA.addIntent(activity);

            }
        });
        btnLiveSbmtSymptom = findViewById(R.id.btnLiveSbmtSymptom);
        btnLiveShareReports = findViewById(R.id.btnLiveShareReports);
        btnPharmacy = findViewById(R.id.btnPharmacy);
        autoLiveTvSymptoms = findViewById(R.id.autoLiveTvSymptoms);
        spLiveSelectCondtn = findViewById(R.id.spLiveSelectCondtn);
        spLiveSelectSymptm = findViewById(R.id.spLiveSelectSymptm);
        spPainSeverity = findViewById(R.id.spPainSeverity);
        etLiveExtraInfo = findViewById(R.id.etLiveExtraInfo);
        etLiveZipCode = findViewById(R.id.etLiveZipCode);
        etPainWhere = findViewById(R.id.etPainWhere);
        etOTBPSys = findViewById(R.id.etOTBPSys);
        etOTBPDia = findViewById(R.id.etOTBPDia);
        etOTHR = findViewById(R.id.etOTHR);
        etOTRespirations = findViewById(R.id.etOTRespirations);
        etOTO2Saturations = findViewById(R.id.etOTO2Saturations);
        etOTBloodSugar = findViewById(R.id.etOTBloodSugar);
        etOTTemperature = findViewById(R.id.etOTTemperature);
        etOTHeightFt = findViewById(R.id.etOTHeightFt);
        etOTHeightIn = findViewById(R.id.etOTHeightIn);
        etOTWeight = findViewById(R.id.etOTWeight);
        ic_mike_LiveExtraInfo = findViewById(R.id.ic_mike_LiveExtraInfo);

        etgetLiveCareState = findViewById(R.id.etgetLiveCareState);
        spgetLivecareState = findViewById(R.id.spgetLivecareState);

        cbPatientAuth = findViewById(R.id.cbPatientAuth);

        rgInternational = findViewById(R.id.rgInternational);
        radioInternationalYes = findViewById(R.id.radioInternationalYes);
        radioInternationalNo = findViewById(R.id.radioInternationalNo);


        rgInternational.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioInternationalYes:
                        DATA.stateFromLiveCare = "all";
                        DATA.print("-- selected Travel International Value " + DATA.stateFromLiveCare);
                        break;
                    case R.id.radioInternationalNo:

                        break;
                    default:
                        break;
                }
            }
        });

        etLiveZipCode.setText(prefs.getString("zipcode", ""));

        //Ahmer change zipcode to state// Ahmer remove this state service 12-08-2022 Sir Jamal
        loadStates();

		/*for (int i = 0; i < stateBeans.size(); i++) {
			if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) {
				etgetLiveCareState.setSelection(i);
			}
		}*/

        spgetLivecareState.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // TODO Auto-generated method stub
                userState = pos == 0 ? "" : stateBeans.get(pos).getAbbreviation();
                DATA.stateFromLiveCare = pos == 0 ? "" : userState;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        //end

        spPainSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPainSeverity = painSeverity[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, painSeverity);
        spPainSeverity.setAdapter(adapter);


        findViewById(R.id.ivReviewAuth).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "/" + ApiManager.PATIENT_AUTH, "Patient Authorization");
            }
        });

        //symptoms from db....
        DATA.allSymptoms = new ArrayList<SymptomsModel>();
        DATA.allSymptoms = db.getAllSymptoms();

        sympArr = new String[DATA.allSymptoms.size()];
        for (int i = 0; i < DATA.allSymptoms.size(); i++) {

            sympArr[i] = DATA.allSymptoms.get(i).symptomName;

        }
        symptomsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, sympArr);
        autoLiveTvSymptoms.setAdapter(symptomsAdapter);
        //autoLiveTvSymptoms.setThreshold(1);
		/*autoLiveTvSymptoms.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				selectedSympId = "0";
				GetLiveCare.this.symptomsAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});*/
        autoLiveTvSymptoms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSympId = "0";
                //GetLiveCare.this.symptomsAdapter.getFilter().filter("");
                autoLiveTvSymptoms.setText("");
            }
        });

        String condAr[] = {"possible condition"};
        conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condAr);
        spLiveSelectCondtn.setAdapter(conditionsAdapter);


        autoLiveTvSymptoms.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                isSymptomSelectd = true;
                String selectedSymptomName = (String) arg0.getItemAtPosition(position);
                DATA.print("--online care selected symptom name: " + selectedSymptomName);

                getSelectedSymptomId(selectedSymptomName);

                DATA.allConditions = new ArrayList<ConditionsModel>();
                DATA.allConditions = db.getAllConditions(selectedSympId);

                if (DATA.allConditions != null) {

                    DATA.print("--online care DATA.allConditions.size on mainscreen: " + DATA.allConditions.size());

                    String condArr[] = new String[DATA.allConditions.size()];
                    for (int i = 0; i < DATA.allConditions.size(); i++) {

                        condArr[i] = DATA.allConditions.get(i).conditionName;
                    }

                    conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condArr);
                    spLiveSelectCondtn.setAdapter(conditionsAdapter);
                }

            }
        });


        spLiveSelectCondtn.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (isSymptomSelectd) {
                    selectedConditionId = DATA.allConditions.get(arg2).conditionId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                selectedConditionId = "0";
            }
        });


        ic_mike_LiveExtraInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity(etLiveExtraInfo);
            }
        });
        btnLiveSbmtSymptom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideShowKeypad.hideSoftKeyboard();
                if (checkInternetConnection.isConnectedToInternet()) {
                    if (!shouldShowPharmacyPopup) {
                        new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle(getString(R.string.app_name))
                                .setMessage("You don't have selected your pharmacy. Please select pharmacy before apply for Immediate/Live Care.")
                                .setPositiveButton("Select Pharmacy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (GloabalMethods.pharmacyBeans != null) {
                                            gloabalMethods.showPharmacyDialog();
                                        } else {
                                            DATA.print("-- pharmacyBeans list is null");
                                            if (checkInternetConnection.isConnectedToInternet()) {
                                                gloabalMethods.getPharmacy("", true, "");
                                            } else {
                                                customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                                            }
                                        }
                                    }
                                }).setNegativeButton("Not Now", null).create().show();

                        return;
                    }

                    //etLiveExtraInfo.getText().toString().isEmpty() ||      //valeidation Removed by Jamal
                    //int gvCondPos = gvConditionNew.getCheckedItemPosition();
                    //selectedConditionId = gvCondPos < 0 ? "" : ((List<ConditionsModel>)gvConditionNew.getTag()).get(gvCondPos).conditionId;

                    SparseBooleanArray checked = gvConditionNew.getCheckedItemPositions();
                    DATA.print("--checked: " + checked);
                    DATA.print("--checked size: " + checked.size());
                    int c = 0;

                    selectedConditionId = "";

                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int position1 = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i)) {
                            DATA.print("--pos checked " + position1);
                            c++;
                            selectedConditionId = selectedConditionId + ((List<ConditionsModel>) gvConditionNew.getTag()).get(position1).conditionName + ", ";
                        }
                    }
                    if (c == 0) {
                        selectedConditionId = "";
                    } else {
                        selectedConditionId = selectedConditionId.substring(0, selectedConditionId.length() - 2);
                    }

                    if (selectedSympId.equalsIgnoreCase("907")) {//Other
                        selectedConditionId = "0";
                    }

                    if (selectedSympId.equals("0") //|| selectedConditionId.equals("0")  etLiveZipCode.getText().toString().isEmpty() ||
                            || selectedSympId.equals("") || selectedConditionId.equals("") /*|| userState.equals("")*/ || rgInternational.getCheckedRadioButtonId() == -1) {

                        customToast.showToast("Please make sure you have filled all mandatory information marked with * and then proceed.", 0, 0);

                    }
                    //Ahmer set validation height and weight - Date / 1-04-2022
                    else if (etOTHeightFt.getText().toString().isEmpty() || etOTHeightIn.getText().toString().isEmpty()) {
                        customToast.showToast("Height Required", 0, 0);
                        etOTHeightIn.requestFocus();
                        etOTHeightFt.requestFocus();
                    } else if (etOTWeight.getText().toString().isEmpty()) {
                        customToast.showToast("Weight Required", 0, 0);
                        etOTWeight.requestFocus();
                    }
                    //Ahmer set validation height and weight - Date / 1-04-2022 end
                    //ahmer add this condition for travel International radio button validate
					/*else if (radioInternationalYes.isChecked())
					{
						DATA.stateFromLiveCare = "all";
						System.out.println("-- radio check travel Internationally Yes ");
					}*/
                    else if (radioInternationalNo.isChecked() && userState.equals("")) {
                        customToast.showToast("State Required", 0, 0);
                    }
                    // end 27-10-2022

                    //body part starts
                    else {
                        if (!cbPatientAuth.isChecked()) {
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).
                                    setTitle(getString(R.string.app_name)).
                                    setMessage("In order for us to provide you with virtual care services you must accept and agree with patient virtual care consent authorization")
                                    .setPositiveButton("Ok", null).show();

                            return;
                        }

                        DATA.print("-- RvBodyPartAdapter.selectedPositons: " + RvBodyPartAdapter.selectedPositons);
                        if (!RvBodyPartAdapter.selectedPositons.isEmpty()) {
                            String bodyPart = "";
                            for (int i = 0; i < bodyParts.length; i++) {
                                if (RvBodyPartAdapter.selectedPositons.contains(i)) {
                                    bodyPart = bodyPart + bodyParts[i] + ", ";
                                }
                            }
                            try {
                                bodyPart = bodyPart.substring(0, bodyPart.length() - 2);
                                DATA.print("-- bodyPart: " + bodyPart);
                                DATA.selectedPainBodyPart = bodyPart;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //body part ends

                        DATA.getLiveCaresymptom_id = selectedSympId;
                        DATA.getLiveCarecondition_id = selectedConditionId;
                        DATA.getLiveCaredescription = etLiveExtraInfo.getText().toString();
                        DATA.getLiveCarereport_ids = DATA.selectedReportIdsForApntmt;
                        DATA.painWhere = etPainWhere.getText().toString();
                        DATA.selectedPainSeverity = selectedPainSeverity;

                        String bpSys = etOTBPSys.getText().toString();
                        String bpDia = etOTBPDia.getText().toString();
                        if (!bpDia.isEmpty()) {
                            bpSys = bpSys + "/" + bpDia;
                        }
                        DATA.ot_bp = bpSys;
                        DATA.ot_hr = etOTHR.getText().toString();
                        DATA.ot_respirations = etOTRespirations.getText().toString();
                        DATA.ot_saturation = etOTO2Saturations.getText().toString();
                        DATA.ot_blood_sugar = etOTBloodSugar.getText().toString();
                        DATA.ot_temperature = etOTTemperature.getText().toString();

                        String h_ft = etOTHeightFt.getText().toString();
                        String h_in = etOTHeightIn.getText().toString();
                        if (!h_in.isEmpty()) {
                            h_ft = h_ft + "." + h_in;
                        }

                        DATA.ot_height = h_ft;
                        DATA.ot_weight = etOTWeight.getText().toString();

                        String zipCode = etLiveZipCode.getText().toString();
                        DATA.zipCodeFromLiveCare = TextUtils.isEmpty(zipCode) ? "0" : zipCode;

                        //if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
                        //startActivity(new Intent(GetLiveCare.this, ActivityUrgentCareDoc.class));
                        //}else{
                        //startActivity(new Intent(GetLiveCare.this, MapActivity.class));
                        //}

                        //Note: as this is urgent care app so showing only urgent care screens  GM
                        //startActivity(new Intent(activity, ActivityUrgentCareDoc.class));
                        if (isForEdit) {
                            updateLiveCareSymptoms();
                        } else {
                            showAskEliveDialog();
                        }
                    }

                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                }
            }
        });

        btnLiveShareReports.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideShowKeypad.hideSoftKeyboard();
                if (checkInternetConnection.isConnectedToInternet()) {
                    OpenActivity op = new OpenActivity(activity);
                    op.open(SelectReports.class, false);
                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                }
            }
        });

        gloabalMethods = new GloabalMethods(activity);

        if (checkInternetConnection.isConnectedToInternet()) {
            if (!isForEdit) {
                checkPatientqueue();
            }
            gloabalMethods.getPharmacy("", false, "");
        } else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
        }

        btnPharmacy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GloabalMethods.pharmacyBeans != null) {
                    gloabalMethods.showPharmacyDialog();
                } else {
                    DATA.print("-- pharmacyBeans list is null");
                    if (checkInternetConnection.isConnectedToInternet()) {
                        gloabalMethods.getPharmacy("", true, "");
                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }
            }
        });

        showSelectedPharmacyBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if (intent.getAction().equals(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION)) {

                    if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
                        btnPharmacy.setText("Select Pharmacy");
                        tvSelPharmacy.setText("You don't have selected your pharmacy. Please select pharmacy before apply for Immediate/Live Care.");
                        //btnLiveSbmtSymptom.setEnabled(false);
                        shouldShowPharmacyPopup = false;
						/*if(shouldShowPharmacyPopup){  //removed by JAmal in emcura app shifted to button click
							shouldShowPharmacyPopup = false;
							new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
									.setMessage("You don't have selected your pharmacy. Please select pharmacy before apply for Immediate/Live Care.")
									.setPositiveButton("Select Pharmacy", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (GloabalMethods.pharmacyBeans != null) {
												gloabalMethods.showPharmacyDialog();
											} else {
												DATA.print("-- pharmacyBeans list is null");
												if (checkInternetConnection.isConnectedToInternet()) {
													gloabalMethods.getPharmacy("",true);
												} else {
													customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
												}
											}
										}
									}).setNegativeButton("Not Now",null).create().show();
						}*/
                    } else {
                        btnPharmacy.setText("Change Pharmacy");
                        //btnLiveSbmtSymptom.setEnabled(true);
                        shouldShowPharmacyPopup = true;
                        tvSelPharmacy.setText(Html.fromHtml("Selected Pharmacy: <b>" + GloabalMethods.selectedPharmacyBean.StoreName + "</b>"));
                        tvSelPharmacyAddress.setText(Html.fromHtml("Address: <b>" + GloabalMethods.selectedPharmacyBean.address + "</b>"));
                    }
                }
            }
        };


        gvReportImages = findViewById(R.id.gvReportImages);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetPopup(activity).showActionSheet();
            }
        });
        vvReportImagesAdapter = new VVReportImagesAdapter(activity, new ArrayList<Image>());
        gvReportImages.setAdapter(vvReportImagesAdapter);
        gvReportImages.setExpanded(true);
        gvReportImages.setPadding(5, 5, 5, 5);


        //symtoms new
        symptomsModels = gloabalMethods.getAllSymptoms();
        spSymptomNew = findViewById(R.id.spSymptomNew);
        gvSymptomNew = findViewById(R.id.gvSymptomNew);
        gvConditionNew = findViewById(R.id.gvConditionNew);

        if (symptomsModels.isEmpty()) {
            return;
        } else {
            selectedSympId = symptomsModels.get(0).symptomId;
        }

        gvSymptomNew.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedSympId = symptomsModels.get(i).symptomId;
                List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;
                ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, conditionsModels);
                //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
                gvConditionNew.setAdapter(conditionsAdapter);
                gvConditionNew.setExpanded(true);
                gvConditionNew.setTag(conditionsModels);//important

            }
        });
        ArrayAdapter<SymptomsModel> symptomAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, symptomsModels);
        //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
        gvSymptomNew.setAdapter(symptomAdapter);
        gvSymptomNew.setExpanded(true);


        //gvSymptomNew.setSelection(0);
        //gvSymptomNew.setItemChecked(0,true);
		/*int mActivePosition = 0;
		gvSymptomNew.performItemClick(
				gvSymptomNew.getAdapter().getView(mActivePosition, null, null),
				mActivePosition,
				gvSymptomNew.getAdapter().getItemId(mActivePosition));*/
        List<ConditionsModel> conditionsModels = symptomsModels.get(0).conditionsModelList;
        ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, conditionsModels);
        //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
        gvConditionNew.setAdapter(conditionsAdapter);
        gvConditionNew.setExpanded(true);
        gvConditionNew.setTag(conditionsModels);//important

        spSymptomNew.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSympId = symptomsModels.get(i).symptomId;
                List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;
                ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, conditionsModels);
                //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
                gvConditionNew.setAdapter(conditionsAdapter);
                gvConditionNew.setExpanded(true);
                gvConditionNew.setTag(conditionsModels);//important

                if (isForEdit && conditions_txt != null) {
                    for (int j = 0; j < conditionsModels.size(); j++) {
                        if (conditions_txt.contains(conditionsModels.get(j).conditionName)) {
                            gvConditionNew.setItemChecked(j, true);
                        }
                    }
                }

                if (symptomsModels.get(i).symptomName.equalsIgnoreCase("other")) {

                    openKeyboardOnetExraInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<SymptomsModel> spSymptomsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, symptomsModels);
        spSymptomNew.setAdapter(spSymptomsAdapter);


        //pain severity emojies new
        rvPainSeverity = findViewById(R.id.rvPainSeverity);
        rvPainAdapter = new RvPainAdapter(activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvPainSeverity.setLayoutManager(mLayoutManager);
        rvPainSeverity.setItemAnimator(new DefaultItemAnimator());
        rvPainSeverity.setAdapter(rvPainAdapter);

        rvPainSeverity.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvPainSeverity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                selectedPainSeverity = painSeverity[position];

                //spPainSeverity.setSelection(position);

                RvPainAdapter.selectedPos = position;

                rvPainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                DATA.print("-- item long press pos: " + position);
            }
        }));


        //body parts RV
        rvBodyPart = findViewById(R.id.rvBodyPart);
        rvBodyPartAdapter = new RvBodyPartAdapter(activity);
        //RecyclerView.LayoutManager mLayoutManagerRVBP = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        int numberOfColumns = 5;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(this, numberOfColumns);

        rvBodyPart.setLayoutManager(mLayoutManagerRVBP);

        //rvBodyPart.setHasFixedSize(true);//this line was for com.app.emcurauc.util.AutofitRecyclerView

        rvBodyPart.setItemAnimator(new DefaultItemAnimator());
        rvBodyPart.setAdapter(rvBodyPartAdapter);

        rvBodyPart.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvBodyPart, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //selectedPainSeverity = painSeverity[position];

                if (RvBodyPartAdapter.selectedPositons.contains(position)) {
                    boolean isremoved = RvBodyPartAdapter.selectedPositons.remove(Integer.valueOf(position));
                    DATA.print("-- removed pos: " + position + " isremoved: " + isremoved);
                } else {
                    RvBodyPartAdapter.selectedPositons.add(position);
                }

                rvBodyPartAdapter.notifyDataSetChanged();

                if (bodyParts[position].equalsIgnoreCase("Other")) {
                    openKeyboardOnetExraInfo();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                DATA.print("-- item long press pos: " + position);
            }
        }));


        etOTHeightFt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    etOTHeightIn.requestFocus();
                }
            }
        });
        etOTBPSys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3) {
                    etOTBPDia.requestFocus();
                }
            }
        });

		/*DATA.print("-- RV item view: "+rvPainSeverity.findViewHolderForAdapterPosition(0).itemView);
		if(rvPainSeverity.findViewHolderForAdapterPosition(0).itemView != null){
			rvPainSeverity.findViewHolderForAdapterPosition(0).itemView.performClick();
		}*/

        layHideOnEdit1 = findViewById(R.id.layHideOnEdit1);
        layHideOnEdit2 = findViewById(R.id.layHideOnEdit2);
        DATA.print("-- isForEdit " + isForEdit);
        if (isForEdit) {
            layHideOnEdit1.setVisibility(View.GONE);
            layHideOnEdit2.setVisibility(View.GONE);
            RequestParams params = new RequestParams();
            params.put("id", prefs.getString("getLiveCareApptID", ""));
            ApiManager apiManager = new ApiManager(ApiManager.GET_UC_DETAILS, "post", params, apiCallBack, activity);
            apiManager.loadURL();
        }
    }//oncreate


    private void openKeyboardOnetExraInfo() {
        etLiveExtraInfo.setSelection(etLiveExtraInfo.getText().length());

        etLiveExtraInfo.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etLiveExtraInfo, InputMethodManager.SHOW_IMPLICIT);
    }


    boolean shouldShowPharmacyPopup = false;

    public void getSelectedSymptomId(String selectedSymptomName) {

        for (int i = 0; i < DATA.allSymptoms.size(); i++) {
            if (DATA.allSymptoms.get(i).symptomName.equals(selectedSymptomName)) {
                selectedSympId = DATA.allSymptoms.get(i).symptomId;
            }
        }
    }

    private void getLiveCareCall(String payment_type) {

        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        params.put("sub_patient_id", prefs.getString("subPatientID", ""));
        params.put("symptom_id", DATA.getLiveCaresymptom_id);
        params.put("condition_id", DATA.getLiveCarecondition_id);
        params.put("description", DATA.getLiveCaredescription);
        params.put("report_ids", DATA.selectedReportIdsForApntmt);
        params.put("doctor_id", DATA.doctorIdForLiveCare);
        params.put("pain_where", DATA.painWhere);
        params.put("pain_severity", DATA.selectedPainSeverity);
        params.put("pain_related", DATA.selectedPainBodyPart);

        params.put("ot_bp", DATA.ot_bp);
        params.put("ot_hr", DATA.ot_hr);
        params.put("ot_respirations", DATA.ot_respirations);
        params.put("ot_saturation", DATA.ot_saturation);
        params.put("ot_blood_sugar", DATA.ot_blood_sugar);
        params.put("ot_temperature", DATA.ot_temperature);
        params.put("ot_height", DATA.ot_height);
        params.put("ot_weight", DATA.ot_weight);

        params.put("payment_type", payment_type);

        params.put("transaction_id", PaymentLiveCare.transaction_id);

        //if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
        params.put("care_center_id", ActivityUrgentCareDoc.urgentCareCenterId);
        //}

        if (payment_type.equals("credit_card")) {
            params.put("creditCardType", PaymentLiveCare.selectedCardType);
            params.put("creditCardNumber", PaymentLiveCare.cardNo);
            params.put("expDateMonth", PaymentLiveCare.selectedExpMonth);
            params.put("expDateYear", PaymentLiveCare.selectedExpYear);
            params.put("cvv2Number", PaymentLiveCare.cvvCode);
            params.put("total_amount", sharedPrefsHelper.get(PKG_AMOUNT, "5"));

        } else if (payment_type.equals("insurance")) {

            params.put("creditCardType", PaymentLiveCare.selectedCardType);
            params.put("creditCardNumber", PaymentLiveCare.cardNo);
            params.put("expDateMonth", PaymentLiveCare.selectedExpMonth);
            params.put("expDateYear", PaymentLiveCare.selectedExpYear);
            params.put("cvv2Number", PaymentLiveCare.cvvCode);

            params.put("total_amount", LiveCareInsurance.coPayAmount);

            params.put("selected_insurance", LiveCareInsurance.selected_insurance);
        } else if (payment_type.equals("NewInsurance")) {

//			params.put("creditCardType", PaymentLiveCare.selectedCardType);
//			params.put("creditCardNumber", PaymentLiveCare.cardNo);
//			params.put("expDateMonth", PaymentLiveCare.selectedExpMonth);
//			params.put("expDateYear", PaymentLiveCare.selectedExpYear);
//			params.put("cvv2Number", PaymentLiveCare.cvvCode);
//
//			params.put("total_amount", LiveCareInsurance.coPayAmount);

            params.put("selected_insurance", ActivityCheckInsurance.insuranceID);
        }


        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                try {
					/*long tim = System.currentTimeMillis();
					File file = new File(images.get(i).path);
					String folderPath = file.getParent();
					String origFilePath = images.get(i).path;
					String ext = origFilePath.substring(origFilePath.lastIndexOf("."));

					File fileToSend = new File(folderPath, tim+"v_image_"+(i+1)+ext);
					boolean isRenamed = file.renameTo(fileToSend);
					if(isRenamed){
						params.put("v_image_"+(i+1),fileToSend);
						images.get(i).path = fileToSend.getAbsolutePath();
						DATA.print("--Renamed: "+ fileToSend.getPath()+" \nAbsPath"+fileToSend.getAbsolutePath());
					}else {
						params.put("v_image_"+(i+1),new File(images.get(i).path));
					}*/

                    params.put("v_image_" + (i + 1), new File(images.get(i).path));
                    DATA.print("--" + i + " filesize: " + new File(images.get(i).path).length());
                    DATA.print("-- path: " + images.get(i).path);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            params.put("num_images", images.size() + "");
        }

        ApiManager apiManager = new ApiManager(ApiManager.ADD_LIVE_CHECKUP, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    private void updateLiveCareSymptoms() {


        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        params.put("sub_patient_id", prefs.getString("subPatientID", ""));
        params.put("symptom_id", DATA.getLiveCaresymptom_id);
        params.put("conditions_txt", DATA.getLiveCarecondition_id);//condition_id
        params.put("description", DATA.getLiveCaredescription);
        params.put("report_ids", DATA.selectedReportIdsForApntmt);
        params.put("doctor_id", DATA.doctorIdForLiveCare);
        params.put("pain_where", DATA.painWhere);
        params.put("pain_severity", DATA.selectedPainSeverity);
        params.put("pain_related", DATA.selectedPainBodyPart);

        params.put("ot_bp", DATA.ot_bp);
        params.put("ot_hr", DATA.ot_hr);
        params.put("ot_respirations", DATA.ot_respirations);
        params.put("ot_saturation", DATA.ot_saturation);
        params.put("ot_blood_sugar", DATA.ot_blood_sugar);
        params.put("ot_temperature", DATA.ot_temperature);
        params.put("ot_height", DATA.ot_height);
        params.put("ot_weight", DATA.ot_weight);

        params.put("payment_type", "free");

        params.put("id", liveCareID);

        //if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
        params.put("care_center_id", ActivityUrgentCareDoc.urgentCareCenterId);
        //}
        ApiManager apiManager = new ApiManager(ApiManager.UPDATE_UC_SYMP, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }

    public void checkPatientqueue() {
        ApiManager apiManager = new ApiManager(ApiManager.CHECK_PATIENT_QUEUE + "/" + prefs.getString("id", "0"), "get", null, apiCallBack, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }

    public void checkUrgentCarePatientqueue() {
        ApiManager apiManager = new ApiManager(ApiManager.CHECK_URGENT_CARE_PATIENT_QUEUE + "/" + prefs.getString("id", "0"), "get", null, apiCallBack, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }


    public void saveTransaction(String patient_id, String livecare_id, String amount, String payment_method,
                                String transaction_id) {

        RequestParams params = new RequestParams();
        params.put("patient_id", patient_id);
        params.put("livecare_id", livecare_id);
        params.put("amount", amount);
        params.put("payment_method", payment_method);
        params.put("transaction_id", transaction_id);
        params.put("checkup_type", "livecheckup");

        ApiManager apiManager = new ApiManager(ApiManager.SAVE_TRANSACTION, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equals(ApiManager.ADD_LIVE_CHECKUP)) {

            DATA.isLiveCarePaymentDone = false;

            PaymentLiveCare.isBraintreePaymentDone = false;

            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                if (status.equalsIgnoreCase("error")) {
                    customToast.showToast(msg, 0, 1);
                    return;
                }

                DATA.liveCareIdForPayment = jsonObject.getInt("id");

                //if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){//jugaar --- need to refine


                String ucFlag = prefs.getString("after_urgentcare_form", "");
                boolean ucFlagBool = ucFlag != null && ucFlag.equalsIgnoreCase("doctors");


                if (!ucFlagBool) {//  true   noneed to check hospId for urgent care app
                    if (!ActivityUrgentCareDoc.urgentCareCenterId.equalsIgnoreCase("a1")) {
                        DATA.NumOfReprtsSelected = 0;
                        DATA.isReprtSelected = false;
                        DATA.selectedReportIdsForApntmt = "";

                        new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info").
                                setMessage("Your Immediate Care request has been submitted. It will be assigned to the doctor soon.")
                                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                        checkUrgentCarePatientqueue();
                                    }
                                }).setCancelable(false).show();
                        return;
                    }
                }

                String docName = "Your have applied for eLiveCare with " + jsonObject.getString("doctor_name");
                prefs.edit().putString("doctor_queue_msg", docName).commit();

                String docId_eLiveCare = jsonObject.optString("doctor_id");//urgent care--> no doc selected-->bugg due to hospital_id cond not matched
                prefs.edit().putString("docId_eLiveCare", docId_eLiveCare).commit();

                DATA.NumOfReprtsSelected = 0;
                DATA.isReprtSelected = false;
                DATA.selectedReportIdsForApntmt = "";

                if (status.equals("success")) {
                    ActivityCheckInsurance.Frominsurance = false;
                    String patient_id = prefs.getString("id", "");
                    String livecare_id = DATA.liveCareIdForPayment + "";
                    String amount = sharedPrefsHelper.get(PKG_AMOUNT, "5");
                    String payment_method = "paypal";
                    String transaction_id = DATA.livecarePaymentPaypalInfo;
                    if (DATA.isFreeCare) {
                        DATA.isFreeCare = false;

                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putBoolean("livecareTimerRunning", true);
                        ed.putString("getLiveCareApptID", DATA.liveCareIdForPayment + "");
                        ed.commit();

						/*Intent intent1 = new Intent();
						intent1.setAction("LIVE_CARE_WAITING_TIMER");
						sendBroadcast(intent1);*/

                        customToast.showToast("Registered for live care", 0, 0);
                        openActivity.open(LiveCareWaitingArea.class, true);

                    } else {
                        //saveTransaction(patient_id, livecare_id, amount, payment_method, transaction_id);
                        //note : this service will no longer work as its connected with lagacy paypal SDK
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putBoolean("livecareTimerRunning", true);
                        ed.putString("getLiveCareApptID", DATA.liveCareIdForPayment + "");
                        ed.commit();

						/*Intent intent1 = new Intent();
						 intent1.setAction("LIVE_CARE_WAITING_TIMER");
						sendBroadcast(intent1);*/
                        ActivityCheckInsurance.Frominsurance = false;
                        customToast.showToast("Registered for live care", 0, 0);
                        openActivity.open(LiveCareWaitingArea.class, true);
                    }
                } else {

					/*Intent intent1 = new Intent();
					intent1.setAction("LIVE_CARE_WAITING_TIMER");
					sendBroadcast(intent1);*/

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("getLiveCareApptID", jsonObject.getString("id"));
                    ed.putBoolean("livecareTimerRunning", true);
                    ed.commit();

                    Toast.makeText(activity, "You have already applied for live care", Toast.LENGTH_LONG).show();
                    openActivity.open(LiveCareWaitingArea.class, true);

                }
            } catch (JSONException e) {
                DATA.print("--Exception in getLiveCareCall: " + e);
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equals(ApiManager.SAVE_TRANSACTION)) {

            customToast.showToast("Transaction Successfull !", 0, 0);

            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("livecareTimerRunning", true);
            ed.putString("getLiveCareApptID", DATA.liveCareIdForPayment + "");
            ed.commit();

			/*Intent intent1 = new Intent();
			intent1.setAction("LIVE_CARE_WAITING_TIMER");
			sendBroadcast(intent1);*/

            customToast.showToast("Registered for live care", 0, 0);
            openActivity.open(LiveCareWaitingArea.class, true);

        } else if (apiName.equalsIgnoreCase(ApiManager.CHECK_PATIENT_QUEUE + "/" + prefs.getString("id", "0"))) {

            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    String message = jsonObject.getString("message");
                    String livecare_id = jsonObject.getString("livecare_id");

                    String docId_eLiveCare = jsonObject.getString("doctor_id");
                    prefs.edit().putString("docId_eLiveCare", docId_eLiveCare).commit();

					/*Intent intent1 = new Intent();
					intent1.setAction("LIVE_CARE_WAITING_TIMER");
					sendBroadcast(intent1);*/

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("getLiveCareApptID", livecare_id);
                    ed.putBoolean("livecareTimerRunning", true);
                    ed.putString("doctor_queue_msg", message);
                    ed.commit();

                    customToast.showToast(message, 0, 1);
                    openActivity.open(LiveCareWaitingArea.class, true);

                } else {
                    DATA.print("--message " + jsonObject.getString("message"));

                    checkUrgentCarePatientqueue();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.CHECK_URGENT_CARE_PATIENT_QUEUE + "/" + prefs.getString("id", "0"))) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String message = jsonObject.getString("message");

                if (jsonObject.has("success")) {
                    String doctor_id = jsonObject.getString("doctor_id");
                    String livecare_id = jsonObject.getString("livecare_id");

                    if (doctor_id.equalsIgnoreCase("0")) {
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("doctor_queue_msg", message);
                        ed.putString("getLiveCareApptID", livecare_id);
                        ed.putString("docId_eLiveCare", "0");//jugaar
                        ed.putBoolean("livecareTimerRunning", true);
                        ed.commit();

						/*Intent intent1 = new Intent();
						intent1.setAction("LIVE_CARE_WAITING_TIMER");
						sendBroadcast(intent1);*/

                        customToast.showToast(message, 0, 1);
                        openActivity.open(LiveCareWaitingArea.class, true);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GET_UC_DETAILS)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject data = jsonObject.getJSONObject("data");

                liveCareID = data.getString("id");

                String symptom_id = data.getString("symptom_id");
                conditions_txt = data.getString("conditions_txt");
                String description = data.getString("description");


                //if spinner silection not changed then this code will fill conditions gv
                ArrayList<ConditionsModel> conditionsModels = (ArrayList<ConditionsModel>) gvConditionNew.getTag();
                for (int j = 0; j < conditionsModels.size(); j++) {
                    if (conditions_txt.contains(conditionsModels.get(j).conditionName)) {
                        gvConditionNew.setItemChecked(j, true);
                    }
                }

                for (int i = 0; i < symptomsModels.size(); i++) {
                    if (symptomsModels.get(i).symptomId.equalsIgnoreCase(symptom_id)) {
                        spSymptomNew.setSelection(i);
                    }
                }

                etLiveExtraInfo.setText(description);
                etLiveExtraInfo.setSelection(etLiveExtraInfo.getText().toString().length());

                //gvConditionNew items are selected in spSymptomNew selected listener

                JSONObject virtual_visit = jsonObject.optJSONObject("virtual_visit");

                if (virtual_visit != null) {
                    JSONObject additional_data = new JSONObject(virtual_visit.getString("additional_data"));
                    String pain_severity = additional_data.getString("pain_severity");
                    JSONObject ot_data = new JSONObject(virtual_visit.getString("ot_data"));
                    String ot_saturation = ot_data.getString("ot_saturation");
                    String ot_height = ot_data.getString("ot_height");
                    String ot_weight = ot_data.getString("ot_weight");
                    String ot_respirations = ot_data.getString("ot_respirations");
                    String ot_blood_sugar = ot_data.getString("ot_blood_sugar");
                    String ot_hr = ot_data.getString("ot_hr");
                    String ot_temperature = ot_data.getString("ot_temperature");
                    String ot_bp = ot_data.getString("ot_bp");
                    String ot_bmi = ot_data.getString("ot_bmi");

                    String pain_related = virtual_visit.getString("pain_related");

                    List<String> pain_relatedArrList = Arrays.asList(pain_related.split(", "));

                    for (int i = 0; i < painSeverity.length; i++) {
                        if (pain_severity.equalsIgnoreCase(painSeverity[i])) {
                            RvPainAdapter.selectedPos = i;
                            selectedPainSeverity = painSeverity[i];
                        }
                    }
                    rvPainAdapter.notifyDataSetChanged();
                    for (int i = 0; i < bodyParts.length; i++) {
                        if (pain_relatedArrList.contains(bodyParts[i])) {
                            RvBodyPartAdapter.selectedPositons.add(i);
                        }
                    }
                    rvBodyPartAdapter.notifyDataSetChanged();


					/*etDescribeSymptoms.setText(DATA.selectedRefferedLiveCare.symptom_details);
					etPainWhere.setText(DATA.selectedRefferedLiveCare.pain_where);
					etLiveExtraInfo.setText(DATA.selectedRefferedLiveCare.description);*/

                    String[] bpArray = ot_bp.split("/");
                    if (bpArray.length > 1) {
                        etOTBPSys.setText(bpArray[0]);
                        etOTBPDia.setText(bpArray[1]);
                    }
                    //String[] hightArray = ot_height.split("\\.");
                    String[] hightArray = ot_height.split("-");//changed after
                    if (hightArray.length > 1) {
                        etOTHeightFt.setText(hightArray[0]);
                        etOTHeightIn.setText(hightArray[1]);
                    }


                    etOTHR.setText(ot_hr);
                    etOTRespirations.setText(ot_respirations);
                    etOTO2Saturations.setText(ot_saturation);
                    etOTBloodSugar.setText(ot_blood_sugar);
                    etOTTemperature.setText(ot_temperature);
                    etOTWeight.setText(ot_weight);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.UPDATE_UC_SYMP)) {
            //{"status":"Error","msg":"id parameter is missing OR missing value","message":"Id Required"}
            //{"status":"success","msg":"Added Successfully"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                customToast.showToast(jsonObject.optString("msg"), 0, 1);
                if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }

    }

    String liveCareID, conditions_txt;


    ArrayList<Image> images;
    VVReportImagesAdapter vvReportImagesAdapter;
    final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if (images == null) {
                images = new ArrayList<>();
            }
            ArrayList<Image> imagesFromGal = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            images.addAll(imagesFromGal);

            vvReportImagesAdapter = new VVReportImagesAdapter(activity, images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5, 5, 5, 5);
            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //String imgPath = ImageFilePath.getPath(activity, ActionSheetPopup.outputFileUriVV);//Android O issue, file provider,  GM
            String imgPath = ActionSheetPopup.capturedImagePath;
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(new Image(0, "", imgPath, true));

            vvReportImagesAdapter = new VVReportImagesAdapter(activity, images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5, 5, 5, 5);

            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/

        } else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            // do whatever you want with the results
            if (this.editText != null) {
                this.editText.setText(matches.get(0));
            }
        }
    }


    EditText editText;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private void startVoiceRecognitionActivity(EditText editText) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // identifying your application to the Google service
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // hint in the dialog
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name));
        // hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        // recognition language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        this.editText = editText;
    }

    public void showAskEliveDialog() {
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_medhist_privacy);
        dialogSupport.setCancelable(false);

        Button btnYes = dialogSupport.findViewById(R.id.btnYes);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        TextView tvMsg = dialogSupport.findViewById(R.id.tvMsg);


        btnYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
                //startActivity(new Intent(GetLiveCare.this, ActivityUrgentCareDoc.class));
                //}else{
                //startActivity(new Intent(GetLiveCare.this, MapActivity.class));
                //}
                //Note: as this is urgent care app so showing only urgent care screens  GM
                //startActivity(new Intent(activity, ActivityUrgentCareDoc.class));

                //now this decision will made from server in login API. GM
                String ucFlag = prefs.getString("after_urgentcare_form", "");
                if (ucFlag != null && ucFlag.equalsIgnoreCase("doctors")) {
                    startActivity(new Intent(GetLiveCare.this, MapActivity.class));
                } else {
                    startActivity(new Intent(GetLiveCare.this, ActivityUrgentCareDoc.class));
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (btnCancel.getText().toString().equalsIgnoreCase("Not Now")) {
                    dialogSupport.dismiss();
                } else {
                    tvMsg.setText("Please find a quite secure place with privacy, so you can discuss your medical history with the provider online");
                    btnYes.setText("Ready");
                    btnCancel.setText("Not Now");
                }
            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }


    //Ahmer Worked here for checking runtime permission - Date 28-12-2021
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == ActionSheetPopup.PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {

            } else {
                showDeniedResponse(grantResults);
                //customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
            }
        }
    }

    private void showDeniedResponse(int[] grantResults) {

        boolean shouldShowDialog = false;
        String msg = activity.getString(R.string.allow_camera_permission_msg);

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

                        }
                    })
                    .create().show();
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        startActivity(intent);
    }
    //Ahmer Worked here for checking runtime permission - Date 28-12-2021 End


    StateBean bean = null;

    private void loadStates() {

        stateBeans = new ArrayList<>();

        String prefsDataStr = prefs.getString("states", "");
        System.out.println("-- states " + prefsDataStr);
        if (!TextUtils.isEmpty(prefsDataStr)) {
            try {
                JSONObject jsonObject = new JSONObject(prefsDataStr);

                String status = jsonObject.getString("status");
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (status.equalsIgnoreCase("success")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String name = jsonArray.getJSONObject(i).getString("full_name");
                        String abbreviation = jsonArray.getJSONObject(i).getString("short_name");

                        bean = new StateBean(name, abbreviation);
                        stateBeans.add(bean);
                        bean = null;
                    }
                    etgetLiveCareState.setVisibility(View.GONE);
                    //final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
                    ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(
                            activity,
                            R.layout.spinner_item_lay,
                            stateBeans
                    );
                    stateBeans.add(0, new StateBean("Select State", ""));
                    spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spgetLivecareState.setAdapter(spStateAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        }

    }
}
