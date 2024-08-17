package com.app.msu_cp.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.app.msu_cp.PatientMedicalHistoryNew;
import com.app.msu_cp.R;
import com.app.msu_cp.adapters.RvBodyPartAdapter;
import com.app.msu_cp.adapters.RvPainAdapter;
import com.app.msu_cp.adapters.VVReportImagesAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.model.ConditionsModel;
import com.app.msu_cp.model.SymptomsModel;
import com.app.msu_cp.GetLiveCareForm;
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

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class DialogPatientVV implements ApiCallBack {



    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;


    public DialogPatientVV(AppCompatActivity activity) {

        this.activity = activity;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        if(activity instanceof MainActivity){
            ((MainActivity) activity).dialogPatientVV = this;
        }

    }

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


    String selectedPainSeverity = "";

    public static Button btnSelectImages;
    public ExpandableHeightGridView gvReportImages;


    Spinner spSymptomNew;
    ExpandableHeightGridView gvSymptomNew, gvConditionNew;
    RecyclerView rvPainSeverity;

    RecyclerView rvBodyPart;

    Dialog dialogForDismiss;
    public void showAddVirtualVisitDialog(){
        Dialog dialogAddVV = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogAddVV.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddVV.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddVV.setContentView(R.layout.get_live_care);

        dialogAddVV.findViewById(R.id.topBar).setVisibility(View.VISIBLE);
        ImageView ivClose = (ImageView) dialogAddVV.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> dialogAddVV.dismiss());


        //==============From GetLiveCareForm Activity=======================
        btnLiveSbmtSymptom = dialogAddVV.findViewById(R.id.btnLiveSbmtSymptom);
        autoLiveTvSymptoms = dialogAddVV.findViewById(R.id.autoLiveTvSymptoms);
        spLiveSelectCondtn = dialogAddVV.findViewById(R.id.spLiveSelectCondtn);
        spLiveSelectSymptm = dialogAddVV.findViewById(R.id.spLiveSelectSymptm);
        spPainSeverity = dialogAddVV.findViewById(R.id.spPainSeverity);
        etLiveExtraInfo = dialogAddVV.findViewById(R.id.etLiveExtraInfo);
        etPainWhere = dialogAddVV.findViewById(R.id.etPainWhere);
        etDescribeSymptoms = dialogAddVV.findViewById(R.id.etDescribeSymptoms);
        etOTBPSys = dialogAddVV.findViewById(R.id.etOTBPSys);
        etOTBPDia = dialogAddVV.findViewById(R.id.etOTBPDia);
        etOTHR = dialogAddVV.findViewById(R.id.etOTHR);
        etOTRespirations = dialogAddVV.findViewById(R.id.etOTRespirations);
        etOTO2Saturations = dialogAddVV.findViewById(R.id.etOTO2Saturations);
        etOTBloodSugar = dialogAddVV.findViewById(R.id.etOTBloodSugar);
        etOTTemperature = dialogAddVV.findViewById(R.id.etOTTemperature);
        etOTHeightFt = dialogAddVV.findViewById(R.id.etOTHeightFt);
        etOTHeightIn = dialogAddVV.findViewById(R.id.etOTHeightIn);
        etOTWeight = dialogAddVV.findViewById(R.id.etOTWeight);

        ScrollView svForm = dialogAddVV.findViewById(R.id.oio);
        PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

        spPainSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPainSeverity = GetLiveCareForm.painSeverity[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, GetLiveCareForm.painSeverity);
        spPainSeverity.setAdapter(adapter);


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
                        DATA.print("-- RvBodyPartAdapter.selectedPositons: "+ RvBodyPartAdapter.selectedPositons);
                        if(!RvBodyPartAdapter.selectedPositons.isEmpty()){
                            String bodyPart = "";
                            for (int i = 0; i < GetLiveCareForm.bodyParts.length; i++) {
                                if(RvBodyPartAdapter.selectedPositons.contains(i)){
                                    bodyPart = bodyPart + GetLiveCareForm.bodyParts[i]+", ";
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


                        ApiManager apiManager = new ApiManager(ApiManager.SAVE_VIRTUAL_VISIT,"post",params,apiCallBack, activity);
                        apiManager.loadURL();
                    }

                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                }
            }
        });


        gvReportImages = dialogAddVV.findViewById(R.id.gvReportImages);
        btnSelectImages = dialogAddVV.findViewById(R.id.btnSelectImages);
        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetPopup(activity).showAskGalCamDialog();//showActionSheet(); //ActionSheet cannot display over dialog
            }
        });
        vvReportImagesAdapter = new VVReportImagesAdapter(activity,new ArrayList<Image>());
        gvReportImages.setAdapter(vvReportImagesAdapter);
        gvReportImages.setExpanded(true);
        gvReportImages.setPadding(5,5,5,5);


        GloabalMethods gloabalMethods = new GloabalMethods(activity);

        //symtoms new
        List<SymptomsModel> symptomsModels = gloabalMethods.getAllSymptoms();
        spSymptomNew = dialogAddVV.findViewById(R.id.spSymptomNew);
        gvSymptomNew = dialogAddVV.findViewById(R.id.gvSymptomNew);
        gvConditionNew = dialogAddVV.findViewById(R.id.gvConditionNew);

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
        rvPainSeverity = dialogAddVV.findViewById(R.id.rvPainSeverity);
        RvPainAdapter rvPainAdapter = new RvPainAdapter(activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rvPainSeverity.setLayoutManager(mLayoutManager);
        rvPainSeverity.setItemAnimator(new DefaultItemAnimator());
        rvPainSeverity.setAdapter(rvPainAdapter);

        rvPainSeverity.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvPainSeverity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                selectedPainSeverity = GetLiveCareForm.painSeverity[position];

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
        rvBodyPart = dialogAddVV.findViewById(R.id.rvBodyPart);
        RvBodyPartAdapter rvBodyPartAdapter = new RvBodyPartAdapter(activity);
        //RecyclerView.LayoutManager mLayoutManagerRVBP = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        int numberOfColumns = 5;
        RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(activity, numberOfColumns);

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

                if(GetLiveCareForm.bodyParts[position].equalsIgnoreCase("Other")){
                    GloabalMethods.openKeyboardOnEditText(activity, etLiveExtraInfo);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                DATA.print("-- item long press pos: "+position);
            }
        }));

        //==============From GetLiveCareForm Activity=======================

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddVV.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddVV.setCanceledOnTouchOutside(false);
        dialogAddVV.show();
        dialogAddVV.getWindow().setAttributes(lp);

        dialogForDismiss = dialogAddVV;

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

                    //finish();
                    if(dialogForDismiss != null){
                        try {
                            dialogForDismiss.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    public ArrayList<Image> images;
    public VVReportImagesAdapter vvReportImagesAdapter;
    //these fiellds will be accessed from MainActivity OnActivityResult -- during call
}
