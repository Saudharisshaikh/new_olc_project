package com.app.msu_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.app.msu_cp.adapters.RvBodyPartAdapter;
import com.app.msu_cp.adapters.RvPainAdapter;
import com.app.msu_cp.adapters.VVReportImagesAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.model.ConditionsModel;
import com.app.msu_cp.model.SymptomsModel;
import com.app.msu_cp.reliance.pt_reports.Reports;
import com.app.msu_cp.util.ActionSheetPopup;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.Database;
import com.app.msu_cp.util.ExpandableHeightGridView;
import com.app.msu_cp.util.GPSTracker;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.app.msu_cp.util.RecyclerItemClickListener;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;

public class GetLiveCareForm extends AppCompatActivity implements ApiCallBack{


    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    Database db;

    ArrayAdapter<String> symptomsAdapter, conditionsAdapter;
    Spinner spLiveSelectSymptm,spLiveSelectCondtn,spPainSeverity;
    EditText etLiveExtraInfo,etPainWhere,etDescribeSymptoms,
            etOTBPSys,etOTBPDia,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeightFt,etOTHeightIn,etOTWeight;
    AutoCompleteTextView autoLiveTvSymptoms;
    Button btnLiveSbmtSymptom;

    String sympArr[];
    private String selectedSympId = "";
    private String selectedConditionId = "0";
    private boolean isSymptomSelectd = false;

    public static final String[] painSeverity = {"No Pain","Mild","Moderate","Severe","Very Severe","Worst pain possible"};
    String selectedPainSeverity = "";

    public static Button btnSelectImages;
    ExpandableHeightGridView gvReportImages;


    Spinner spSymptomNew;
    ExpandableHeightGridView gvSymptomNew, gvConditionNew;
    RecyclerView rvPainSeverity;
    public static final int[] painSevEmojies = {R.drawable.pain_nopain,R.drawable.pain_mild,R.drawable.pain_moderate,R.drawable.pain_severe,R.drawable.pain_verysevere,R.drawable.pain_worstpainpossible};


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

    ImageView ivFaceSheet,ivDeleteFaceSheet, ivEKG,ivDeleteEKG;

    boolean isForEdit;
    LinearLayout layLiveCareImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_live_care);//this layout is also used in DialogPatientVV.showAddVirtualVisitDialog()

        isForEdit = getIntent().getBooleanExtra("isForEdit", false);

        activity = GetLiveCareForm.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;
        db = new Database(activity);


        btnLiveSbmtSymptom = findViewById(R.id.btnLiveSbmtSymptom);
        autoLiveTvSymptoms = findViewById(R.id.autoLiveTvSymptoms);
        spLiveSelectCondtn = findViewById(R.id.spLiveSelectCondtn);
        spLiveSelectSymptm = findViewById(R.id.spLiveSelectSymptm);
        spPainSeverity = findViewById(R.id.spPainSeverity);
        etLiveExtraInfo = findViewById(R.id.etLiveExtraInfo);
        etPainWhere = findViewById(R.id.etPainWhere);
        etDescribeSymptoms = findViewById(R.id.etDescribeSymptoms);
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
        layLiveCareImages = findViewById(R.id.layLiveCareImages);

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

        //symptoms from db....
        DATA.allSymptoms = new ArrayList<SymptomsModel>();
        DATA.allSymptoms = db.getAllSymptoms();

        sympArr = new String[DATA.allSymptoms.size()];
        for(int i = 0; i<DATA.allSymptoms.size(); i++) {

            sympArr[i] = DATA.allSymptoms.get(i).symptomName;

        }
        symptomsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, sympArr);
        autoLiveTvSymptoms.setAdapter(symptomsAdapter);
        /*autoLiveTvSymptoms.setThreshold(1);
        autoLiveTvSymptoms.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                selectedSympId = "0";
                symptomsAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        autoLiveTvSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSympId = "0";
                //GetLiveCare.this.symptomsAdapter.getFilter().filter("");
                autoLiveTvSymptoms.setText("");

                //autoLiveTvSymptoms.showDropDown();  also can be used
            }
        });

        String condAr[] = {"possible condition"};
        conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condAr);
        spLiveSelectCondtn.setAdapter(conditionsAdapter);


        autoLiveTvSymptoms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                isSymptomSelectd = true;
                String selectedSymptomName = (String) arg0.getItemAtPosition(position);
                DATA.print("--online care selected symptom name: "+selectedSymptomName);

                getSelectedSymptomId(selectedSymptomName);

                DATA.allConditions = new ArrayList<ConditionsModel>();
                DATA.allConditions = db.getAllConditions(selectedSympId);

                if(DATA.allConditions != null) {

                    DATA.print("--online care DATA.allConditions.size on mainscreen: "+DATA.allConditions.size());

                    String condArr[] = new String[DATA.allConditions.size()];
                    for(int i = 0; i<DATA.allConditions.size(); i++) {

                        condArr[i] = DATA.allConditions.get(i).conditionName;
                    }

                    conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condArr);
                    spLiveSelectCondtn.setAdapter(conditionsAdapter);
                }

            }
        });


        spLiveSelectCondtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(isSymptomSelectd) {
                    selectedConditionId = DATA.allConditions.get(arg2).conditionId;
                    selectedCond = DATA.allConditions.get(arg2).conditionName;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                selectedConditionId = "0";
            }
        });

        btnLiveSbmtSymptom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideShowKeypad.hideSoftKeyboard();
                if(checkInternetConnection.isConnectedToInternet()) {

                    SparseBooleanArray checked = gvConditionNew.getCheckedItemPositions();
                    DATA.print("--checked: "+checked);
                    DATA.print("--checked size: "+checked.size());
                    int c = 0;

                    selectedConditionId = "";

                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int position1 = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i)){
                            DATA.print("--pos checked "+position1);
                            c++;

                            selectedConditionId = selectedConditionId+((List<ConditionsModel>)gvConditionNew.getTag()).get(position1).conditionName+", ";
                        }
                    }
                    if (c == 0) {
                        selectedConditionId = "";
                    }else {
                        selectedConditionId = selectedConditionId.substring(0, selectedConditionId.length()-2);
                    }

                    if(selectedSympId.equalsIgnoreCase("907")){//Other
                        selectedConditionId = "0";
                    }

                    if(selectedSympId.equals("0") //|| selectedConditionId.equals("0")  //etLiveExtraInfo.getText().toString().isEmpty() //valeidation Removed by Jamal
                            || selectedSympId.equals("")
                            || selectedConditionId.equals("")) {

                        //customToast.showToast("Please complete the form", 0, 0);

                        customToast.showToast("Please select patient symptoms", 0, 0);
                        int resId = selectedConditionId.equals("") ? R.drawable.cust_border_grey_outline_red : R.drawable.cust_border_grey_outline;
                        gvConditionNew.setBackgroundResource(resId);

                    }else {

                        String getLiveCaresymptom_id = selectedSympId;
                        String getLiveCarecondition_id = selectedConditionId;
                        String getLiveCaredescription = etLiveExtraInfo.getText().toString();
                        String painWhere = etPainWhere.getText().toString();
                        String selectedPainSeverity1 = selectedPainSeverity;
                        String describeSymptom = etDescribeSymptoms.getText().toString();

                        if (describeSymptom.isEmpty()){
                            etDescribeSymptoms.setError("Please describe patient symptoms");
                            GloabalMethods.openKeyboardOnEditText(activity, etDescribeSymptoms);
                            customToast.showToast("Please describe patient symptoms",0,0);
                            return;
                        }


                        RequestParams params = new RequestParams();

                        //body part starts
                        DATA.print("-- RvBodyPartAdapter.selectedPositons: "+RvBodyPartAdapter.selectedPositons);
                        if(!RvBodyPartAdapter.selectedPositons.isEmpty()){
                            String bodyPart = "";
                            for (int i = 0; i < bodyParts.length; i++) {
                                if(RvBodyPartAdapter.selectedPositons.contains(i)){
                                    bodyPart = bodyPart + bodyParts[i]+", ";
                                }
                            }
                            try {
                                bodyPart = bodyPart.substring(0, bodyPart.length()-2);
                                DATA.print("-- bodyPart: "+bodyPart);

                                params.put("pain_related", bodyPart);

                            }catch (Exception e){e.printStackTrace();}
                        }
                        //body part ends

                        params.put("patient_id",DATA.selectedUserCallId);
                        params.put("symptom_id",getLiveCaresymptom_id);
                        params.put("condition_id",getLiveCarecondition_id);
                        params.put("description",getLiveCaredescription);
                        params.put("pain_where",painWhere);
                        params.put("pain_severity",selectedPainSeverity1);
                        //params.put("doctor_id",DATA.selectedDrId);
                        params.put("doctor_id",prefs.getString("id",""));
                        params.put("symptom_details",describeSymptom);

                        String bpSys = etOTBPSys.getText().toString();
                        String bpDia = etOTBPDia.getText().toString();
                        if(!bpDia.isEmpty()){
                            bpSys = bpSys+"/"+bpDia;
                        }
                        params.put("ot_bp" , bpSys);
                        params.put("ot_hr" , etOTHR.getText().toString());
                        params.put("ot_respirations" , etOTRespirations.getText().toString());
                        params.put("ot_saturation" , etOTO2Saturations.getText().toString());
                        params.put("ot_blood_sugar" , etOTBloodSugar.getText().toString());

                        params.put("ot_temperature" , etOTTemperature.getText().toString());

                        String h_ft = etOTHeightFt.getText().toString();
                        String h_in = etOTHeightIn.getText().toString();
                        if(!h_in.isEmpty()){
                            h_ft = h_ft+"."+h_in;
                        }
                        params.put("ot_height" , h_ft);
                        params.put("ot_weight" , etOTWeight.getText().toString());

                        DATA.visit_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        params.put("visit_start_time",DATA.visit_start_time);
                        params.put("visit_end_time",DATA.visit_end_time);

                        if(images!=null){
                            for (int i = 0; i < images.size(); i++) {
                                try {
                                    params.put("v_image_"+(i+1),new File(images.get(i).path));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            params.put("num_images",images.size()+"");
                        }

                        GPSTracker gpsTracker = new GPSTracker(activity);
                        if(gpsTracker.canGetLocation()){
                            params.put("latitude",gpsTracker.getLatitude()+"");
                            params.put("longitude",gpsTracker.getLongitude()+"");
                            gpsTracker.stopUsingGPS();
                        }

                        if(!TextUtils.isEmpty(faceSheetFilePath)){
                            try {
                                params.put("facesheet", new File(faceSheetFilePath));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!TextUtils.isEmpty(ekgFilePath)){
                            try {
                                params.put("ekg", new File(ekgFilePath));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }


                        ApiManager apiManager = null;
                        if(isForEdit){
                            params.put("id", DATA.selectedRefferedLiveCare.virtualVisitId);
                            apiManager = new ApiManager(ApiManager.UPDATE_VIRTUAL_VISIT,"post",params,apiCallBack, activity);
                        }else {
                            apiManager = new ApiManager(ApiManager.SAVE_VIRTUAL_VISIT,"post",params,apiCallBack, activity);
                        }
                        apiManager.loadURL();
                    }

                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                }
            }
        });


        gvReportImages = findViewById(R.id.gvReportImages);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetPopup(activity).showActionSheet();
            }
        });
        vvReportImagesAdapter = new VVReportImagesAdapter(activity,new ArrayList<Image>());
        gvReportImages.setAdapter(vvReportImagesAdapter);
        gvReportImages.setExpanded(true);
        gvReportImages.setPadding(5,5,5,5);


        GloabalMethods gloabalMethods = new GloabalMethods(activity);

        //symtoms new
        List<SymptomsModel> symptomsModels = gloabalMethods.getAllSymptoms();
        spSymptomNew = findViewById(R.id.spSymptomNew);
        gvSymptomNew = findViewById(R.id.gvSymptomNew);
        gvConditionNew = findViewById(R.id.gvConditionNew);

        gvSymptomNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


        spSymptomNew.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSympId = symptomsModels.get(i).symptomId;
                List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;
                ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, conditionsModels);
                //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
                gvConditionNew.setAdapter(conditionsAdapter);
                gvConditionNew.setExpanded(true);
                gvConditionNew.setTag(conditionsModels);//important

                if(isForEdit){
                    for (int j = 0; j < conditionsModels.size(); j++) {
                        if(DATA.selectedRefferedLiveCare.condition_name.contains(conditionsModels.get(j).conditionName)){
                            gvConditionNew.setItemChecked(j, true);
                        }
                    }
                }

                if(symptomsModels.get(i).symptomName.equalsIgnoreCase("other")){
                    GloabalMethods.openKeyboardOnEditText(activity, etLiveExtraInfo);
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
        RvPainAdapter rvPainAdapter = new RvPainAdapter(activity);
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
                DATA.print("-- item long press pos: "+position);
            }
        }));



        //body parts RV
        rvBodyPart = findViewById(R.id.rvBodyPart);
        RvBodyPartAdapter rvBodyPartAdapter = new RvBodyPartAdapter(activity);
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

                if(RvBodyPartAdapter.selectedPositons.contains(position)){
                    boolean isremoved = RvBodyPartAdapter.selectedPositons.remove(Integer.valueOf(position));
                    DATA.print("-- removed pos: "+position+" isremoved: "+isremoved);
                }else {
                    RvBodyPartAdapter.selectedPositons.add(position);
                }

                rvBodyPartAdapter.notifyDataSetChanged();

                if(bodyParts[position].equalsIgnoreCase("Other")){
                    GloabalMethods.openKeyboardOnEditText(activity, etLiveExtraInfo);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                DATA.print("-- item long press pos: "+position);
            }
        }));



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
        etOTBPSys.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 3){
                    etOTBPDia.requestFocus();
                }
            }
        });


        ivFaceSheet = findViewById(R.id.ivFaceSheet);
        ivDeleteFaceSheet = findViewById(R.id.ivDeleteFaceSheet);
        ivEKG = findViewById(R.id.ivEKG);
        ivDeleteEKG = findViewById(R.id.ivDeleteEKG);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivFaceSheet:
                        onPickPhoto();
                        break;
                    case R.id.ivDeleteFaceSheet:
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? Do you want to delete this file")
                                .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        faceSheetFilePath = "";
                                        ivDeleteFaceSheet.setVisibility(View.GONE);
                                        ivFaceSheet.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                        ivFaceSheet.setScaleType(ImageView.ScaleType.CENTER);
                                    }
                                })
                                .setNegativeButton("Not Now",null)
                                .create().show();
                        break;
                    case R.id.ivEKG:
                        onPickDoc();
                        break;
                    case R.id.ivDeleteEKG:
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? Do you want to delete this file")
                                .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ekgFilePath = "";
                                        ivDeleteEKG.setVisibility(View.GONE);
                                        ivEKG.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                        ivEKG.setScaleType(ImageView.ScaleType.CENTER);
                                    }
                                })
                                .setNegativeButton("Not Now",null)
                                .create().show();
                        break;
                    default:
                        break;
                }
            }
        };
        ivFaceSheet.setOnClickListener(onClickListener);
        ivDeleteFaceSheet.setOnClickListener(onClickListener);
        ivEKG.setOnClickListener(onClickListener);
        ivDeleteEKG.setOnClickListener(onClickListener);


        DATA.print("-- isForEdit "+isForEdit);
        if(isForEdit){
            layLiveCareImages.setVisibility(View.GONE);
            for (int i = 0; i < symptomsModels.size(); i++) {
                if(symptomsModels.get(i).symptomName.equalsIgnoreCase(DATA.selectedRefferedLiveCare.symptom_name)){
                    spSymptomNew.setSelection(i);
                }
            }
            //gvConditionNew items are selected in spSymptomNew selected listener

            for (int i = 0; i < painSeverity.length; i++) {
                if(DATA.selectedRefferedLiveCare.pain_severity.equalsIgnoreCase(painSeverity[i])){
                    RvPainAdapter.selectedPos = i;
                    selectedPainSeverity = painSeverity[i];
                }
            }
            rvPainAdapter.notifyDataSetChanged();
            for (int i = 0; i < bodyParts.length; i++) {
                if (DATA.selectedRefferedLiveCare.pain_related.contains(bodyParts[i])){
                    RvBodyPartAdapter.selectedPositons.add(i);
                }
            }
            rvBodyPartAdapter.notifyDataSetChanged();

            etDescribeSymptoms.setText(DATA.selectedRefferedLiveCare.symptom_details);
            etPainWhere.setText(DATA.selectedRefferedLiveCare.pain_where);
            etLiveExtraInfo.setText(DATA.selectedRefferedLiveCare.description);

            String[] bpArray = DATA.selectedRefferedLiveCare.ot_bp.split("/");
            if(bpArray.length > 1){
                etOTBPSys.setText(bpArray[0]);
                etOTBPDia.setText(bpArray[1]);
            }
            String[] hightArray = DATA.selectedRefferedLiveCare.ot_height.split("\\.");
            if(hightArray.length > 1){
                etOTHeightFt.setText(hightArray[0]);
                etOTHeightIn.setText(hightArray[1]);
            }


            etOTHR.setText(DATA.selectedRefferedLiveCare.ot_hr);
            etOTRespirations.setText(DATA.selectedRefferedLiveCare.ot_respirations);
            etOTO2Saturations.setText(DATA.selectedRefferedLiveCare.ot_saturation);
            etOTBloodSugar.setText(DATA.selectedRefferedLiveCare.ot_blood_sugar);
            etOTTemperature.setText(DATA.selectedRefferedLiveCare.ot_temperature);
            etOTWeight.setText(DATA.selectedRefferedLiveCare.ot_weight);
        }

    }




    public void getSelectedSymptomId(String selectedSymptomName) {

        for(int i = 0; i<DATA.allSymptoms.size();i++) {
            if(DATA.allSymptoms.get(i).symptomName.equals(selectedSymptomName)) {
                selectedSympId = DATA.allSymptoms.get(i).symptomId;
            }
        }
    }

    String selectedCond = "";
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.contains(ApiManager.SAVE_VIRTUAL_VISIT)){
            try {
                //{"success":"Saved","hide_modal":1,"modal_id":"virtual_visit"}
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){

                    DATA.virtual_visit_id = jsonObject.getString("virtual_visit_id");

                    customToast.showToast("Information has been saved",0,1);

                    DATA.selectedUserCallSympTom = autoLiveTvSymptoms.getText().toString();
                    DATA.selectedUserCallCondition = selectedCond;
                    DATA.selectedUserCallDescription = etLiveExtraInfo.getText().toString();
                    if(DATA.selectedRefferedLiveCare != null){
                        DATA.selectedRefferedLiveCare.pain_where =  etPainWhere.getText().toString();
                        DATA.selectedRefferedLiveCare.pain_severity = selectedPainSeverity;
                        DATA.selectedRefferedLiveCare.symptom_details = etDescribeSymptoms.getText().toString();
                    }

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.UPDATE_VIRTUAL_VISIT)){
            //{"status":"success","message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Done", null)
                        .create();
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    ArrayList<Image> images;
    VVReportImagesAdapter vvReportImagesAdapter;
    final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if(images == null){
                images = new ArrayList<>();
            }
            ArrayList<Image> imagesFromGal = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            images.addAll(imagesFromGal);

            vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5,5,5,5);
            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //String imgPath = ImageFilePath.getPath(activity, ActionSheetPopup.outputFileUriVV);//Android O issue, file provider,  GM
            String imgPath = ActionSheetPopup.capturedImagePath;
            if(images == null){
                images = new ArrayList<>();
            }
            images.add(new Image(0,"",imgPath,true));

            vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5,5,5,5);

            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/

        }else {

            //face sheet and ekg
            photoPaths = new ArrayList<>();
            docPaths = new ArrayList<>();
            switch (requestCode) {
                case Reports.CUSTOM_REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        photoPaths = new ArrayList<>();
                        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    }
                    break;

                case FilePickerConst.REQUEST_CODE_DOC:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        docPaths = new ArrayList<>();
                        docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    }
                    break;
            }

            //addThemToView(photoPaths, docPaths);
            ArrayList<String> filePaths = new ArrayList<>();
            if (photoPaths != null) filePaths.addAll(photoPaths);

            if (docPaths != null) filePaths.addAll(docPaths);

            DATA.print("--total files: "+filePaths.size());

            if(!filePaths.isEmpty()){
                String filePath = filePaths.get(0);
                if(fileFlag == 1){
                    faceSheetFilePath = filePath;
                    ivDeleteFaceSheet.setVisibility(View.VISIBLE);
                    try {
                        File imageFile = new File(faceSheetFilePath);
                        String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", imageFile).toString();
                        final String decoded = Uri.decode(uri);
                        DATA.loadImageFromURL(decoded,R.drawable.ic_placeholder_2, ivFaceSheet);
                        ivFaceSheet.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(fileFlag == 2){
                    ekgFilePath = filePath;
                    ivDeleteEKG.setVisibility(View.VISIBLE);
                    ivEKG.setImageResource(R.drawable.ic_pickfile_pdf);
                    ivEKG.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();
    int fileFlag = 1;//1 = facesheet, 2 = EKG
    String faceSheetFilePath = "", ekgFilePath = "";

    public void onPickPhoto() {
        fileFlag = 1;
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .setActivityTitle("Please select a report picture")
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(false)
                .enableSelectAll(true)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.custom_camera)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickPhoto(this, Reports.CUSTOM_REQUEST_CODE);
    }

    public void onPickDoc() {
        fileFlag = 2;
        String[] zips = { ".zip", ".rar" };
        String[] pdfs = { ".pdf" };
        String[] doc = { ".doc", ".docx" };
        String[] ppt = { ".ppt", ".pptx" };
        String[] xlsx = {".xls", ".xlsx"};
        String[] txt = {".txt"};


        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(docPaths)
                .setActivityTheme(R.style.FilePickerTheme)//DrawerTheme2
                .setActivityTitle("Please select a file")
                //.addFileSupport("ZIP", zips)
                .addFileSupport("PDF", pdfs, R.drawable.ic_pickfile_pdf)
                .addFileSupport("DOC", doc, R.drawable.ic_pickfile_docx)
                .addFileSupport("PPT", ppt, R.drawable.ic_pickfile_pptx)
                .addFileSupport("XLSX", xlsx, R.drawable.ic_pickfile_xlsx)
                //.addFileSupport("TXT", txt, R.drawable.ic_pickfile_txt)

                .enableDocSupport(false)
                .enableSelectAll(true)
                .sortDocumentsBy(SortingTypes.name)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(this);
    }
}
