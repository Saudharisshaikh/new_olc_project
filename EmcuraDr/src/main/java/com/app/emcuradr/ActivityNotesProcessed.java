package com.app.emcuradr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.emcuradr.adapter.TelemedicineAdapter;
import com.app.emcuradr.adapter.TelemedicineAdapter2;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.model.AllScriptPatient;
import com.app.emcuradr.model.ConditionsModel;
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.model.SymptomsModel;
import com.app.emcuradr.model.TelemedicineCatData;
import com.app.emcuradr.model.TelemedicineCategories;
import com.app.emcuradr.model.TimeDiff;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.ExpandableHeightGridView;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.app.emcuradr.util.GloabalMethods;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActivityNotesProcessed extends BaseActivity {


    ArrayList<TelemedicineCategories> telemedicineCategories;
    ListView lvTelemed2;
    TextView tvElivCatName;
    FloatingGroupExpandableListView lvTelemedicineData;
    StringBuilder sbSelectedTMSCodes, sbSelectedTMSCodesWithNames;
    String tmsCodes, tmsCodesWithNames;

    GloabalMethods gloabalMethods;
    Dialog dialogTelemedicineSer;
    boolean fromChatGPT = false;

    Dialog dialogNote;

    TextView patName,tvSaveNote,tvShowPreviousNotes;
    String gptFinalQuery;
    ExistingPatient searchPatient;
    String patientType;
    AllScriptPatient allScriptPatient;
    public static String use_for_billing = "Yes";
    EditText etEncountNotes;
    ImageView ivCloseAi;

    Button nextBtn,previousBtn;

    ExpandableHeightGridView gvSymptomNew, gvConditionNew;
    private String selectedConditionId = "0";
    private boolean isSymptomSelectd = false;
    private String selectedSympId = "";
    List<SymptomsModel> symptomsModels = new ArrayList<>();
    ImageView back;
    String startTime = "",endTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_processed);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        gvSymptomNew = findViewById(R.id.gvConditionNew);
        nextBtn = findViewById(R.id.btnNext);
        previousBtn = findViewById(R.id.btnPrevious);
        back = findViewById(R.id.ivBack);
        gptFinalQuery = getIntent().getStringExtra("gptFinalQuery");
        patientType = getIntent().getStringExtra("patient_type");
        startTime = getIntent().getStringExtra("start_time");
        endTime = getIntent().getStringExtra("end_time");
        DATA.print("--startTime:"+startTime+" - "+endTime);
        if(patientType.equals("search_patient")){
            searchPatient = getIntent().getParcelableExtra("patient_data");
        }
        else {
            allScriptPatient = getIntent().getParcelableExtra("patient_data");
        }

        loadSymtomsConditions();

        //List<SymptomsModel> symptomsModels = new ArrayList<>();
       // List<SymptomsModel> symptomsModels = getAllSymptoms();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        gvSymptomNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(!symptomsModels.isEmpty()) {
                   // selectedSympId = symptomsModels.get(i).symptomId;
                   // List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;

                   ConditionsModel selectedCondition = symptomsModels.get(0).conditionsModelList.get(i);
                    Log.d("--selectedCons", "onItemClick: "+selectedCondition.conditionName);
                }
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SparseBooleanArray checked = gvSymptomNew.getCheckedItemPositions();
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

                        selectedConditionId = selectedConditionId + symptomsModels.get(0).conditionsModelList.get(position1).conditionName + ", ";
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

                if(selectedConditionId.equals("")){

                    customToast.showToast("Please select symptom before proceeding.",0,0);
                }
                customToast.showToast("done"+selectedConditionId,0,0);


//                if (selectedSympId.equals("0") //|| selectedConditionId.equals("0")
//                        || selectedSympId.equals("") || selectedConditionId.equals("") ) {
//
//                    customToast.showToast("Please make sure you have filled all mandatory information marked with * and then proceed.", 0, 0);
//
//                }

            }
        });

        //showTelemedicineDialog();
    }

    public void loadSymtomsConditions(){
        ApiManager apiManager = new ApiManager(ApiManager.SYMP_COND,"get",null,this, ActivityNotesProcessed.this);
        apiManager.loadURL();
    }
    public void addToFav() {
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services[" + (i + j) + "]", telemedicineCategories.get(i)
                                .telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if (isServicesSelected) {
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_FAVOURITE_SERVICES, "post", params, apiCallBack, ActivityNotesProcessed.this);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }


    public TimeDiff getTimeDiff(Date startTime, Date endTime) {
        // d1, d2 are dates
        long diff = endTime.getTime() - startTime.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        /*DATA.print("-- Time diffrence");
        System.out.print("-- "+diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");*/

        return new TimeDiff(diffSeconds, diffMinutes, diffHours, diffDays);
    }

    public void showTelemedicineDialog() {
        dialogTelemedicineSer = new Dialog(ActivityNotesProcessed.this, R.style.TransparentThemeH4B);
        dialogTelemedicineSer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //dialogAddTemplPresc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogTelemedicineSer.setContentView(R.layout.dialog_telemedicine_services);

        FloatingGroupExpandableListView lvTelemedicineData = (FloatingGroupExpandableListView) dialogTelemedicineSer.findViewById(R.id.lvTelemedicineData);
        Button btnDone = (Button) dialogTelemedicineSer.findViewById(R.id.btnDone);

        Button btnAddToFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnAddToFav);
        Button btnRemoveFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnRemoveFav);

        ListView lvTelemed2 = dialogTelemedicineSer.findViewById(R.id.lvTelemed2);
        CheckBox cbToggleExpList = dialogTelemedicineSer.findViewById(R.id.cbToggleExpList);
        TextView tvEliveSessionTime = dialogTelemedicineSer.findViewById(R.id.tvEliveSessionTime);
        tvElivCatName = dialogTelemedicineSer.findViewById(R.id.tvElivCatName);

        Button btnUseForBillingYes = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingYes);
        Button btnUseForBillingNo = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingNo);
        RelativeLayout layUseForBilling = dialogTelemedicineSer.findViewById(R.id.layUseForBilling);

        lvTelemed2.setVisibility(View.VISIBLE);
        cbToggleExpList.setVisibility(View.VISIBLE);
        tvEliveSessionTime.setVisibility(View.VISIBLE);
        tvElivCatName.setVisibility(View.VISIBLE);


        DATA.elivecare_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        try {
            Date elivecareStartTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_start_time);
            Date elivecareEndTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_end_time);

            TimeDiff timeDiff = getTimeDiff(elivecareStartTime, elivecareEndTime);

            long mm = timeDiff.diffMinutes, ss = timeDiff.diffSeconds, hh = timeDiff.diffHours;
            StringBuilder sb = new StringBuilder();

            StringBuilder sb2 = new StringBuilder();

            if (hh < 10) {
                sb.append("0" + String.valueOf(hh));
                if (hh > 0) {
                    sb2.append("0" + String.valueOf(hh) + " hr, ");
                }
            } else {
                sb.append(String.valueOf(hh));
                if (hh > 0) {
                    sb2.append(String.valueOf(hh) + " hr, ");
                }
            }
            sb.append(":");

            if (mm < 10) {
                sb.append("0" + String.valueOf(mm));
                sb2.append("0" + String.valueOf(mm) + " min, ");
            } else {
                sb.append(String.valueOf(mm));
                sb2.append(String.valueOf(mm) + " min, ");
            }
            sb.append(":");
            if (ss < 10) {
                sb.append("0" + String.valueOf(ss));
                sb2.append("0" + String.valueOf(ss) + " sec");
            } else {
                sb.append(String.valueOf(ss));
                sb2.append(String.valueOf(ss) + " sec");
            }

            tvEliveSessionTime.setText("Session Time : " + sb2.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.btnDone) {
                    doneProceed();
                } else if (view.getId() == R.id.btnAddToFav) {
                    addToFav();
                } else if (view.getId() == R.id.btnRemoveFav) {
                    removeFav();
                } else if (view.getId() == R.id.btnUseForBillingYes) {
                    layUseForBilling.setVisibility(View.GONE);
                    use_for_billing = "Yes";
                } else if (view.getId() == R.id.btnUseForBillingNo) {
                    use_for_billing = "No";
                    tmsCodes = "";
                    tmsCodesWithNames = "";
                    DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                    if (dialogTelemedicineSer != null) {
                        sharedPrefsHelper.save("encounter_notesDurringCall", "");
//                        btnAfterCallEndConv.setVisibility(View.VISIBLE);
//                        dialogBoxAiResultFinal.setVisibility(View.GONE);
//                        layEndConvOptions.setVisibility(View.GONE);
//                        dialogBoxAiResultFinal.setVisibility(View.GONE);
                        dialogTelemedicineSer.dismiss();
                    }
                    //Ahmer code
                    if (fromChatGPT) {
                        fromChatGPT = false;
                        billWithoutNote(tmsCodes, gptFinalQuery);
                        //btnAfterCallEndConv.setVisibility(View.GONE);
                        //dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
                    } else {
                        initAiNoteDialog();
                        //initNoteDialog();
                    }
                }
            }
        };
        btnDone.setOnClickListener(onClickListener);
        btnAddToFav.setOnClickListener(onClickListener);
        btnRemoveFav.setOnClickListener(onClickListener);

        btnUseForBillingYes.setOnClickListener(onClickListener);
        btnUseForBillingNo.setOnClickListener(onClickListener);

        cbToggleExpList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lvTelemed2.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        ImageView ivClose = dialogTelemedicineSer.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> {
            dialogTelemedicineSer.dismiss();
            // dialogBoxAiResultFinal.setVisibility(View.GONE);
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTelemedicineSer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogTelemedicineSer.setCanceledOnTouchOutside(false);
        dialogTelemedicineSer.show();
        dialogTelemedicineSer.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogAddTemplPresc;

        this.lvTelemedicineData = lvTelemedicineData;
        this.lvTelemed2 = lvTelemed2;

        getTelemedicineServices();
    }


    public void getTelemedicineServices() {

        String checheData = sharedPrefsHelper.get(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, "");
        if (!TextUtils.isEmpty(checheData)) {
            parseTelemedicineData(checheData);
            ApiManager.shouldShowPD = false;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("livecare_id", "");
        ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES, "post", params, apiCallBack, ActivityNotesProcessed.this);
        apiManager.loadURL();
    }



    public void removeFav() {
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services[" + (i + j) + "]", telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if (isServicesSelected) {
                ApiManager apiManager = new ApiManager(ApiManager.REMOVE_FAVOURITE_SERVICES, "post", params, apiCallBack, ActivityNotesProcessed.this);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }


    public void doneProceed() {

        if (telemedicineCategories != null) {
            sbSelectedTMSCodes = new StringBuilder();
            sbSelectedTMSCodesWithNames = new StringBuilder();
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        sbSelectedTMSCodes.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code);
                        sbSelectedTMSCodes.append(",");

                        sbSelectedTMSCodesWithNames.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code
                                + " - " + telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_name);
                        sbSelectedTMSCodesWithNames.append(",");
                        sbSelectedTMSCodesWithNames.append("\n");
                    }
                }
            }

            tmsCodes = sbSelectedTMSCodes.toString();
            tmsCodesWithNames = sbSelectedTMSCodesWithNames.toString();
            if (tmsCodes.isEmpty()) {
                //customToast.showToast("Please select telemedicine services", 0, 1);
                new AlertDialog.Builder(ActivityNotesProcessed.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
                        setMessage("You do not selected any service. Do you want to skip ?")
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tmsCodes = "";
                                tmsCodesWithNames = "";
                                DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                                if (dialogTelemedicineSer != null) {
                                    sharedPrefsHelper.save("encounter_notesDurringCall", "");
                                    //dialogBoxAiResultFinal.setVisibility(View.GONE);
                                    dialogTelemedicineSer.dismiss();
                                }
                                if (fromChatGPT) {
                                    fromChatGPT = false;
                                    billWithoutNote(tmsCodes, gptFinalQuery);


                                } else {
                                    initAiNoteDialog();
                                    //initNoteDialog();
                                }
								/*if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
									openActivity.open(ActivitySoapNotesNew.class, true);//ActivitySoapNotes
								}else if(ActivitySOAP.addSoapFlag == 2){
									openActivity.open(ActivitySoapNotesEmpty.class, true);
								}else if(ActivitySOAP.addSoapFlag == 4){
									finish();
								}*/

                                /*else if(ActivitySOAP.addSoapFlag == 5){//this condition is in oncreate - without selecting services
									getMedicalNoteTxt();
								}*/
                            }
                        }).setNegativeButton("No", null).create().show();
            } else {
                tmsCodes = tmsCodes.substring(0, tmsCodes.length() - 1);
                tmsCodesWithNames = tmsCodesWithNames.substring(0, tmsCodesWithNames.length() - 1);
                DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                if (dialogTelemedicineSer != null) {
                    //dialogBoxAiResultFinal.setVisibility(View.GONE);
                    dialogTelemedicineSer.dismiss();
                }
                if (fromChatGPT) {
                    fromChatGPT = false;

                    billWithoutNote(tmsCodes, gptFinalQuery);
                } else {
                    initAiNoteDialog();
                    // initNoteDialog();
                }

				/*if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
					openActivity.open(ActivitySoapNotesNew.class, true);////ActivitySoapNotes
				}else if(ActivitySOAP.addSoapFlag == 2){
					openActivity.open(ActivitySoapNotesEmpty.class, true);
				}else if(ActivitySOAP.addSoapFlag == 4){
					initNoteDialog();
				}*/
                /*else if(ActivitySOAP.addSoapFlag == 5){////this condition is in oncreate - without selecting services
					getMedicalNoteTxt();
				}*/
            }

        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }

    }


    public void initAiNoteDialog() {
        dialogNote = new Dialog(ActivityNotesProcessed.this);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.getWindow().setGravity(Gravity.TOP);
        //dialogNote.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogNote.setContentView(R.layout.dialog_recorded_notes);
        dialogNote.setCanceledOnTouchOutside(false);
        dialogNote.setCancelable(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);

        etEncountNotes = dialogNote.findViewById(R.id.etNotes);
        ivCloseAi = dialogNote.findViewById(R.id.ivClose);
        patName = dialogNote.findViewById(R.id.tvName);
        tvSaveNote = dialogNote.findViewById(R.id.tvsavenotes);
        tvShowPreviousNotes = dialogNote.findViewById(R.id.tvshowprevious);

        patName.setText(patientType.equals("search_patient")?searchPatient.firstName+" "+searchPatient.lastName:allScriptPatient.firstName+" "+allScriptPatient.lastName);
        etEncountNotes.setText(gptFinalQuery);
        ivCloseAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNote.dismiss();
            }
        });
        tvSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNote.dismiss();
                billWithoutNote(tmsCodes, gptFinalQuery);
            }
        });

        //Ahmer's code for AI




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogNote.show();

        dialogNote.getWindow().setAttributes(lp);
    }



    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {



        if (apiName.equalsIgnoreCase(ApiManager.BILL_WITHOUT_NOTE)) {
            //{"success":1,"message":"Saved.","note_id":1878}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    sharedPrefsHelper.save("encounter_notesDurringCall", "");
                    customToast.showToast("Auto Encounter Notes submitted successfully", 0, 0);
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);

            }
        }

        else if (apiName.equalsIgnoreCase(ApiManager.GET_TELEMEDICINE_SERVICES)) {

            parseTelemedicineData(content);

        }


        else if(apiName.equalsIgnoreCase(ApiManager.SYMP_COND)){
            if(! TextUtils.isEmpty(content)){
               setAllSymptoms(content);
            }
        }


    }


    public List<SymptomsModel> getAllSymptoms(){
        return symptomsModels;
    }

    public void setAllSymptoms(String content){


        symptomsModels = new ArrayList<>();
        if(!content.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String symptom_name = data.getJSONObject(i).getString("symptom_name");
                    List<ConditionsModel> conditionsModels = new ArrayList<>();
                    String conditionsStr = data.getJSONObject(i).getString("conditions");
                    String condition_id = "", symptom_id="", condition_name="";
                    if(! TextUtils.isEmpty(conditionsStr)){
                        JSONArray conditions = data.getJSONObject(i).getJSONArray("conditions");
                        for (int j = 0; j < conditions.length(); j++) {
                             condition_id = conditions.getJSONObject(j).getString("id");
                             symptom_id = conditions.getJSONObject(j).getString("symptom_id");
                             condition_name = conditions.getJSONObject(j).getString("condition_name");

                            conditionsModels.add(new ConditionsModel(condition_id,symptom_id,condition_name));
                        }
                    }

                    symptomsModels.add(new SymptomsModel(condition_id,condition_name,conditionsModels));

                }
                DATA.print("--syms:"+symptomsModels.size());
                if(!symptomsModels.isEmpty()){
                    ArrayAdapter<ConditionsModel> symptomAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, symptomsModels.get(0).conditionsModelList);
                    //ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
                    gvSymptomNew.setAdapter(symptomAdapter);
                    gvSymptomNew.setExpanded(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                DATA.print("--symp:"+e.getMessage().toString());
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }

    }


    public void parseTelemedicineData(String content) {
        telemedicineCategories = new ArrayList<>();
        TelemedicineCategories temp;

        try {
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {

                ArrayList<TelemedicineCatData> catData = new ArrayList<>();
                TelemedicineCatData telemedicineCatDataTEMP;

                String category_name = jsonArray.getJSONObject(i).getString("category_name");
                JSONArray data = jsonArray.getJSONObject(i).getJSONArray("data");

                for (int j = 0; j < data.length(); j++) {
                    String category_name1 = data.getJSONObject(j).getString("category_name");
                    String hcpcs_code = data.getJSONObject(j).getString("hcpcs_code");
                    String service_name = data.getJSONObject(j).getString("service_name");
                    String category_id = data.getJSONObject(j).getString("category_id");
                    String non_fac_fee = data.getJSONObject(j).getString("non_fac_fee");
                    String service_id = data.getJSONObject(j).getString("service_id");

                    telemedicineCatDataTEMP = new TelemedicineCatData(category_name1, hcpcs_code, service_name, category_id, non_fac_fee, false, service_id);
                    catData.add(telemedicineCatDataTEMP);
                    telemedicineCatDataTEMP = null;
                }

                temp = new TelemedicineCategories(category_name, catData);
                telemedicineCategories.add(temp);

                if (category_name.equalsIgnoreCase("Most Common") && lvTelemed2 != null) {
                    lvTelemed2.setAdapter(new TelemedicineAdapter2(ActivityNotesProcessed.this, catData));
                    tvElivCatName.setText(category_name);
                }

            }

            TelemedicineAdapter adapter = new TelemedicineAdapter(ActivityNotesProcessed.this, telemedicineCategories);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            if (lvTelemedicineData != null) {
                lvTelemedicineData.setAdapter(wrapperAdapter);
            }

            sharedPrefsHelper.save(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, content);

        } catch (JSONException e) {
            DATA.print("js:"+e.getMessage());
            // TODO Auto-generated catch block
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            e.printStackTrace();
        }

    }


    public void billWithoutNote(String tmsCodes, String note_text) {
        RequestParams params = new RequestParams();
        String patientId = patientType.equals("search_patient")?searchPatient.id:allScriptPatient.ID;
        DATA.print("--pates"+patientId);
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", patientId);
        params.put("treatment_codes", tmsCodes);
        params.put("start_time", DATA.elivecare_start_time);
        params.put("note_text", note_text);
        params.put("use_for_billing", use_for_billing);//(Yes,No)

        //GM added call_id
        String callId = "0";
//        if (DATA.incomingCall) {
//            callId = DATA.incommingCallId;
//        }
//        else  if(DATA.isExistingPatCall){
//            callId = DATA.incommingCallId;
//        }
//        else {
//            callId = prefs.getString("callingID", "");
//        }
        params.put("call_id", callId);
        //GM added call_id

        ApiManager apiManager = new ApiManager(ApiManager.BILL_WITHOUT_NOTE, "post", params, apiCallBack, ActivityNotesProcessed.this);
        apiManager.loadURL();
    }
}