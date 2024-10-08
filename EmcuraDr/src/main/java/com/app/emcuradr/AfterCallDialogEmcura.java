package com.app.emcuradr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.emcuradr.adapter.LabReportsAdapter;
import com.app.emcuradr.adapter.TelemedicineAdapter;
import com.app.emcuradr.adapter.TelemedicineAdapter2;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.model.LabReportBean;
import com.app.emcuradr.model.TelemedicineCatData;
import com.app.emcuradr.model.TelemedicineCategories;
import com.app.emcuradr.model.TimeDiff;
import com.app.emcuradr.reliance.DialogAllEncNotes;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.app.emcuradr.util.GloabalMethods;
import com.app.emcuradr.util.HideShowKeypad;
import com.app.emcuradr.util.OpenActivity;
import com.app.emcuradr.util.PrescriptionModule;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class AfterCallDialogEmcura extends BaseActivity {

    AppCompatActivity activity;
    OpenActivity openActivity;
    HideShowKeypad hideShowKeypad;
    GloabalMethods gloabalMethods;


    LinearLayout patientOptions, doctorOptions, layPatientOptionsEmcura, dialogBoxAiResultFinal;
    TextView tvDialogCompleteConversation, tvDialogAddSOAP, tvDialogBillWithNote, tvDialogMedNoteACD, tvDialogSendPrescription, tvDialogRecall, tvDialogShareToSpecialist, tvAddProgressNotes,
            tvDialogDiscusToSpecialist, tvSymptoms, tvSubmitRating, tvNoThanks, tvSaveAiNote, tvDiscardAiNote;
    RatingBar ratingBar;

    EditText etDischareNote;

    String disclaimer = "";

    ProgressDialog pd;
    AsyncHttpClient client;
    String status = "";
    JSONObject jsonObject;
    CheckInternetConnection checkInternetConnection;

    SharedPreferences prefs;
    EditText etNoteFromAiFinal;


    Button btnAfterCallEndConv, btnAfterCallAddNotes, btnAfterCallAddNotePresc, btnAfterCallRefillPresc, btnAfterCallLabReq, btnAfterCallDiscNotes, btnAfterCallGenCarePlan, btnAfterCallGenCarePlanAi, btnAfterCallGenAutoNote, btnAfterCallGenAiNote, btnAfterCallAutoNotes, btnAfterCallNotNow;
    LinearLayout layEndConvOptions;

    Dialog_CustomProgress customProgressDialog;
    boolean isForRemoveFromQueueFromUcDet = false;
    public static boolean autoNoteStatus = false;

    boolean fromChatGPT = false;


    ArrayList<String> gptQueryList;

    String queryForAiNotes = "",queryForDischargeSummery= "";

    @Override
    protected void onResume() {
        super.onResume();

        if (DATA.isFromDocToDoc) {
            layPatientOptionsEmcura.setVisibility(View.GONE);
            doctorOptions.setVisibility(View.VISIBLE);
            startActivity(new Intent(activity, CustomDialogActivity.class));
            finish();
        } else if (MainActivity.isFromCallToCoordinator) {
            layPatientOptionsEmcura.setVisibility(View.GONE);
            doctorOptions.setVisibility(View.VISIBLE);
        } else if (MainActivity.isFromInstantConnect && !ActivityInstatntConnect.isFromPatientProfile) {
            layPatientOptionsEmcura.setVisibility(View.VISIBLE);
            doctorOptions.setVisibility(View.GONE);
        }
        else if (MainActivity.isFromExistingInstantConnect && !ActivityInstatntConnect.isFromPatientProfile) {
            layPatientOptionsEmcura.setVisibility(View.VISIBLE);
            doctorOptions.setVisibility(View.GONE);
        }
        //note : this is commented b/c after history call show patient options for emcura
		/*else if(DATA.isFromCallHistoryOrMsgs){
			layPatientOptionsEmcura.setVisibility(View.GONE);
			doctorOptions.setVisibility(View.VISIBLE);
		}*/
        else {
            layPatientOptionsEmcura.setVisibility(View.VISIBLE);
            doctorOptions.setVisibility(View.GONE);
			 /*tvDialogOKDONE.setVisibility(View.VISIBLE);
			 tvDialogRecall.setVisibility(View.VISIBLE);
			 tvSymptoms.setVisibility(View.VISIBLE);*/
        }


        if (DATA.isSOAP_NotesSent) {
            //DATA.print("-- code executed after finish");
            DATA.isSOAP_NotesSent = false;

            if (DATA.isVideoCallFromLiveCare) {//Same code on complete the conversation click
                DATA.isVideoCallFromLiveCare = false;
                initRemoveDialog(true);
            } else if (DATA.isVideoCallFromRefPt) {
                DATA.isVideoCallFromRefPt = false;
                initRemoveFromMyPatientsDialog();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        }

//		 if(DATA.isFromDocToDoc){
//			 patientOptions.setVisibility(View.GONE);
//			 doctorOptions.setVisibility(View.VISIBLE);
//			 startActivity(new Intent(activity,CustomDialogActivity.class));
//			 finish();
//			 /*tvDialogOKDONE.setVisibility(View.GONE);
//			 tvDialogRecall.setVisibility(View.GONE);
//			 tvSymptoms.setVisibility(View.GONE);*/
//
//		 }else if(MainActivity.isFromCallToCoordinator){
//			 patientOptions.setVisibility(View.GONE);
//			 doctorOptions.setVisibility(View.VISIBLE);
//		 }else if(DATA.isFromCallHistoryOrMsgs){
//			 patientOptions.setVisibility(View.GONE);
//			 doctorOptions.setVisibility(View.VISIBLE);
//		 }else{
//			 patientOptions.setVisibility(View.VISIBLE);
//			 doctorOptions.setVisibility(View.GONE);
//			 /*tvDialogOKDONE.setVisibility(View.VISIBLE);
//			 tvDialogRecall.setVisibility(View.VISIBLE);
//			 tvSymptoms.setVisibility(View.VISIBLE);*/
//		 }
//
//		 if (DATA.isSOAP_NotesSent) {
//			 DATA.print("-- code executed after finish");
//			 DATA.isSOAP_NotesSent = false;
//
//			 if(DATA.isVideoCallFromLiveCare){//Same code on complete the conversation click
//				 DATA.isVideoCallFromLiveCare = false;
//				 initRemoveDialog(true);
//			 }
//
//			 if(DATA.isVideoCallFromRefPt){
//				 DATA.isVideoCallFromRefPt = false;
//				 initRemoveFromMyPatientsDialog();
//			 }
//		}
        //super.onResume();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();  // no action occur on back button
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure ? Do you want to exit.")
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.after_call_dialog_emcura);

        activity = AfterCallDialogEmcura.this;

        hideShowKeypad = new HideShowKeypad(activity);
        openActivity = new OpenActivity(activity);
        gloabalMethods = new GloabalMethods(activity);
        customProgressDialog = new Dialog_CustomProgress(activity);

        pd = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("Please wait..");
        pd.setMessage("Removing the current appointment from queue");

        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);



        patientOptions = findViewById(R.id.patientOptions);
        doctorOptions = findViewById(R.id.doctorOptions);
        layPatientOptionsEmcura = findViewById(R.id.layPatientOptionsEmcura);

        tvDialogCompleteConversation = findViewById(R.id.tvDialogCompleteConversation);
        tvDialogAddSOAP = findViewById(R.id.tvDialogAddSOAP);
        tvDialogBillWithNote = findViewById(R.id.tvDialogBillWithNote);
        tvDialogMedNoteACD = findViewById(R.id.tvDialogMedNoteACD);
        tvDialogSendPrescription = findViewById(R.id.tvDialogSendPrescription);
        tvAddProgressNotes = findViewById(R.id.tvAddProgressNotes);

        tvDialogRecall = findViewById(R.id.tvDialogRecall);
        tvSymptoms = findViewById(R.id.textView1);

        tvSubmitRating = findViewById(R.id.tvSubmitRating);
        tvNoThanks = findViewById(R.id.tvNoThanks);
        ratingBar = findViewById(R.id.ratingBar1);

        gptQueryList = sharedPrefsHelper.getGptQuerylist();
        if(!gptQueryList.isEmpty()){

            queryForDischargeSummery = gptQueryList.get(5);
            queryForDischargeSummery =  queryForDischargeSummery .replace("[patientCondition]", DATA.selectedUserCallCondition)
                    .replace("[patientDesc]", DATA.selectedUserCallDescription)
                    .replace("\\/", " or ")
                    .replace("]","");
            DATA.print("disch"+queryForDischargeSummery);

            queryForAiNotes = gptQueryList.get(0);
            queryForAiNotes = queryForAiNotes.replace("patientSymp]", DATA.selectedUserCallCondition)
                    .replace("\\/", " or ")
                    .replace("[","")
            ;
            DATA.print("noty"+queryForAiNotes);
        }
        else {
            customToast.showToast("No queries found",0,0);
        }
        disclaimer = sharedPrefsHelper.get("disclaimer");
        Button btnReferToPCP = findViewById(R.id.btnReferToPCP);
        btnReferToPCP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).referToPCP(DATA.selectedUserAppntID);
            }
        });

        //initPrescripDialog();//shifted to separite module

        tvDialogBillWithNote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySOAP.addSoapFlag = 4;
                openActivity.open(ActivityTelemedicineServices.class, false);
            }
        });
        tvDialogMedNoteACD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySOAP.addSoapFlag = 5;
                openActivity.open(ActivityTelemedicineServices.class, false);
            }
        });

        tvDialogAddSOAP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //initRemoveDialog();
                //startActivity(new Intent(activity,ActivityTelemedicineServices.class));
                new GloabalMethods(activity).showAddSOAPDialog();
            }
        });
        tvDialogCompleteConversation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DATA.isVideoCallFromLiveCare) {//Same code in onResume if DATA.isSoapnotessent true
                    DATA.isVideoCallFromLiveCare = false;
                    initRemoveDialog(false);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        tvDialogSendPrescription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new PrescriptionModule(activity).initDoubleVerficationDialog();
            }
        });

        tvDialogRecall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

				/*Intent i = new Intent(activity, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();*/
                Intent i = new Intent(activity, MainActivity.class);//SampleActivity.class
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }
        });

        tvDialogShareToSpecialist = findViewById(R.id.tvDialogShareToSpecialist);//tvShare

        tvDialogShareToSpecialist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DATA.referToSpecialist = true;
                DATA.onlyReports = "0";
                //DATA.onlyReports = "1"; //this value was not removing case from my queue
                startActivity(new Intent(AfterCallDialogEmcura.this, DocToDoc.class));
                finish();

            }
        });


        tvDialogDiscusToSpecialist = findViewById(R.id.tvDialogDiscusToSpecialist);
        tvDialogDiscusToSpecialist.setVisibility(View.GONE);
        tvDialogDiscusToSpecialist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DATA.referToSpecialist = false;
                startActivity(new Intent(AfterCallDialogEmcura.this, DocToDoc.class));
                finish();

            }
        });

        tvSubmitRating.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(activity, "Thank you!", Toast.LENGTH_SHORT).show();

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });
        tvNoThanks.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });


        tvAddProgressNotes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityProgressNotes.class, false);
            }
        });


        //Emcura New Flow
        btnAfterCallEndConv = findViewById(R.id.btnAfterCallEndConv);
        btnAfterCallAddNotes = findViewById(R.id.btnAfterCallAddNotes);
        btnAfterCallAutoNotes = findViewById(R.id.btnAfterCallAutoNotes);
        btnAfterCallAddNotePresc = findViewById(R.id.btnAfterCallAddNotePresc);
        btnAfterCallRefillPresc = findViewById(R.id.btnAfterCallRefillPresc);
        layEndConvOptions = findViewById(R.id.layEndConvOptions);
        btnAfterCallLabReq = findViewById(R.id.btnAfterCallLabReq);
        btnAfterCallDiscNotes = findViewById(R.id.btnAfterCallDiscNotes);
        btnAfterCallGenCarePlan = findViewById(R.id.btnAfterCallGenCarePlan);
        btnAfterCallGenCarePlanAi = findViewById(R.id.btnAfterCallGenCarePlanAi);
        btnAfterCallGenAutoNote = findViewById(R.id.btnAfterCallGenAutoNote);
        btnAfterCallGenAiNote = findViewById(R.id.btnAfterCallGenAiNote);
        btnAfterCallNotNow = findViewById(R.id.btnAfterCallNotNow);


        //Ahmer work new
        etNoteFromAiFinal = findViewById(R.id.etNoteFromAiFinal);
        tvSaveAiNote = findViewById(R.id.tvSaveAiNote);
        tvDiscardAiNote = findViewById(R.id.tvDiscardAiNote);
        dialogBoxAiResultFinal = findViewById(R.id.dialogBoxAiResultFinal);
        String finalNote = sharedPrefsHelper.get("encounter_notesDurringCall", "");
        System.out.println("-- finalNote " + finalNote);

        if (finalNote != null && !finalNote.trim().isEmpty()) {
            DATA.print("-- finalNote is not empty");
            fromChatGPT = true;
            //showTelemedicineDialog();
            btnAfterCallEndConv.setVisibility(View.GONE);
            dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
        }

        etNoteFromAiFinal.setText(finalNote);

        tvDiscardAiNote.setOnClickListener(v -> {
            sharedPrefsHelper.save("encounter_notesDurringCall", "");
            dialogBoxAiResultFinal.setVisibility(View.GONE);
            btnAfterCallEndConv.setVisibility(View.VISIBLE);
        });

        //AHMER WORK FOR TAKE BILLING CODE BEFORE SUBMIT THE NOTES 03/04/2024
        tvSaveAiNote.setOnClickListener(v -> {
            fromChatGPT = true;
            autoNoteStatus = true;
            showTelemedicineDialog();
        });

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.btnAfterCallEndConv:
                        btnAfterCallEndConv.setVisibility(View.GONE);
                        layEndConvOptions.setVisibility(View.VISIBLE);
                        break;
                    case R.id.btnAfterCallAddNotes:
                        showTelemedicineDialog();
                        break;
                    case R.id.btnAfterCallAutoNotes:
                        if (DATA.selectedUserCallCondition.isEmpty()) {
                            customToast.showToast("Patient did not select any symptoms", 0, 0);
                        } else {
                            autoNoteStatus = true;
                           // callChatGPTAPI("write medical note for symptoms " + DATA.selectedUserCallCondition + "patient name is :" + DATA.selectedUserCallName + "and patient DOB is: " + DATA.selectedUserCallDOB);

                            GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                                callChatGPTAPI(queryForAiNotes);
                            },()->{

                            },"OK","Not Now","Disclaimer");


                        }
                    case R.id.btnAfterCallAddNotePresc:
                        isNoteAndPresc = true;
                        new PrescriptionModule(activity).initDoubleVerficationDialog();
                        break;
                    case R.id.btnAfterCallRefillPresc:
                        isNoteAndPresc = false;
                        new PrescriptionModule(activity).initDoubleVerficationDialog();
                        break;
                    case R.id.btnAfterCallLabReq:
                        getLabReports();
                        break;
                    case R.id.btnAfterCallDiscNotes:
                        initDiscNoteDialog();
                        break;
                    case R.id.btnAfterCallGenCarePlan:
                        GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                            Intent intentRev = new Intent(activity, ActivityAiCarePlan.class);
                            startActivity(intentRev);
                        },()->{

                        },"OK","Not Now","Disclaimer");
                        break;
                    case R.id.btnAfterCallGenCarePlanAi:

                        Intent intentRevAi = new Intent(activity, ActivityAiSymptomsReview.class);
                        startActivity(intentRevAi);

                        break;
                    case R.id.btnAfterCallGenAutoNote:
                        GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                            getConvoNotes();
                        },()->{

                        },"OK","Not Now","Disclaimer");

                        break;
                    case R.id.btnAfterCallGenAiNote:
                        GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                            initAiNoteDialog();
                        },()->{

                        },"OK","Not Now","Disclaimer");

                        break;
                    case R.id.btnAfterCallNotNow:
                        if (DATA.isVideoCallFromLiveCare) {
                            DATA.isVideoCallFromLiveCare = false;
                            initRemoveDialog(true);
                        } else {
                            finish();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        btnAfterCallEndConv.setOnClickListener(onClickListener);
        btnAfterCallAddNotes.setOnClickListener(onClickListener);
        btnAfterCallAddNotePresc.setOnClickListener(onClickListener);
        btnAfterCallRefillPresc.setOnClickListener(onClickListener);
        btnAfterCallLabReq.setOnClickListener(onClickListener);
        btnAfterCallDiscNotes.setOnClickListener(onClickListener);
        btnAfterCallGenCarePlan.setOnClickListener(onClickListener);
        btnAfterCallGenCarePlanAi.setOnClickListener(onClickListener);
        btnAfterCallGenAutoNote.setOnClickListener(onClickListener);
        btnAfterCallGenAiNote.setOnClickListener(onClickListener);
        btnAfterCallNotNow.setOnClickListener(onClickListener);

        View viewHideActivity = findViewById(R.id.viewHideActivity);

        isForRemoveFromQueueFromUcDet = getIntent().getBooleanExtra("isForRemoveFromQueueFromUcDet", false);
        if (isForRemoveFromQueueFromUcDet) {
            viewHideActivity.setVisibility(View.VISIBLE);
            initRemoveDialog(false);
        } else {
            viewHideActivity.setVisibility(View.GONE);
        }

    }//end oncreate


    static boolean isNoteAndPresc = false;

    public void proceedAfterSendPresc() {
        if (isNoteAndPresc) {
            showTelemedicineDialog();
        } else {
            if (DATA.isVideoCallFromLiveCare) {
                DATA.isVideoCallFromLiveCare = false;
                initRemoveDialog(true);
            } else {
                finish();
            }
        }
    }


    String patientType = "";

    private void sendCallDoneReport() {

        pd.setTitle("Please wait..");
        pd.setMessage("Removing the current appointment from queue");
        pd.show();

//		RequestParams params = new RequestParams();
//		params.put("appointment_id", DATA.selectedUserAppntID);

        client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        String removeURL = "";
        if (cbSendAVS.isChecked()) {
            removeURL = DATA.baseUrl + "appointmentDone/" + DATA.selectedUserAppntID + "/livecheckup?send_avs=1";
        } else {
            removeURL = DATA.baseUrl + "appointmentDone/" + DATA.selectedUserAppntID + "/livecheckup?send_avs=0";
        }

        if (cbSendAVSToPCP.isChecked()) {
            removeURL = removeURL + "&is_avs_to_pcp=1";
        } else {
            removeURL = removeURL + "&is_avs_to_pcp=0";
        }


        if (cbSendBillingSummarytoBillers.isChecked()) {
            removeURL = removeURL + "&billing_summary=1";
        } else {
            removeURL = removeURL + "&billing_summary=0";
        }

        if (cbProcessTheBillingProcessDirectly.isChecked()) {
            removeURL = removeURL + "&billing_process=1";
        } else {
            removeURL = removeURL + "&billing_process=0";
        }

        patientType = "";
        if (cbTCM.isChecked()) {
            patientType = patientType + "tcm,";
        }
        if (cbCC.isChecked()) {
            patientType = patientType + "complex_care,";
        }
        if (cbHomeHealth.isChecked()) {
            patientType = patientType + "home_health,";
        }
        if (cbNursingHome.isChecked()) {
            patientType = patientType + "nursing_home,";
        }
        if (!patientType.isEmpty()) {
            patientType = patientType.substring(0, patientType.lastIndexOf(","));
        }
        if (cbTCM.isChecked() || cbCC.isChecked() || cbHomeHealth.isChecked() || cbNursingHome.isChecked()) {
            removeURL = removeURL + "&patient_category=" + patientType;
        } else {
            patientType = "";
        }
		/*int checkedRGPatientType = rgPatientType.getCheckedRadioButtonId();
		switch (checkedRGPatientType){
			case R.id.rbTCM:
				removeURL = removeURL + "&patient_category=tcm";
				break;
			case R.id.rbCC:
				removeURL = removeURL + "&patient_category=complex_care";
				break;
			case R.id.rbHomeHealth:
				removeURL = removeURL + "&patient_category=home_health";
				break;
			case R.id.rbNursingHome:
				removeURL = removeURL + "&patient_category=nursing_home";
				break;
			default:

				break;
		}*/

        DATA.print("-- sendCallDoneReport: " + removeURL);

        String reqURL = removeURL;
        DATA.print("-- Request : " + reqURL);
        //DATA.print("-- params : "+params.toString());

        client.get(removeURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                pd.dismiss();
                try {
                    String content = new String(response);

                    DATA.print("--online care response in sendCAllDoneReport: " + content);

                    try {

                        jsonObject = new JSONObject(content);

                        status = jsonObject.getString("success");

                        if (status.equals("success")) {

						/*Intent i = new Intent(activity, MainActivityNew.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(i);*/
                            if (patientType.isEmpty()) {
                                finish();
                            } else {
                                Intent i = new Intent(activity, ActivityFindNurse.class);
                                i.putExtra("patientType", patientType);
                                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }

                        } else {
                            Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        DATA.print("--online care exception in getlivecare patients: " + e);

                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: " + reqURL + ", http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.dismiss();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : " + reqURL + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });
    }


    public void saveAppointmentStatus() {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();
        params.put("id", DATA.drAppointmentModel.getId());
        params.put("status", "Completed");

        if (cbSendAVS.isChecked()) {
            params.put("send_avs", "1");
        } else {
            params.put("send_avs", "0");
        }

        if (cbSendAVSToPCP.isChecked()) {
            params.put("is_avs_to_pcp", "1");
        } else {
            params.put("is_avs_to_pcp", "0");
        }

        if (cbSendBillingSummarytoBillers.isChecked()) {
            params.put("billing_summary", "1");
        } else {
            params.put("billing_summary", "0");
        }

        if (cbProcessTheBillingProcessDirectly.isChecked()) {
            params.put("billing_process", "1");
        } else {
            params.put("billing_process", "0");
        }

        patientType = "";
        if (cbTCM.isChecked()) {
            patientType = patientType + "tcm,";
        }
        if (cbCC.isChecked()) {
            patientType = patientType + "complex_care,";
        }
        if (cbHomeHealth.isChecked()) {
            patientType = patientType + "home_health,";
        }
        if (cbNursingHome.isChecked()) {
            patientType = patientType + "nursing_home,";
        }
        if (!patientType.isEmpty()) {
            patientType = patientType.substring(0, patientType.lastIndexOf(","));
        }
        if (cbTCM.isChecked() || cbCC.isChecked() || cbHomeHealth.isChecked() || cbNursingHome.isChecked()) {
            //removeURL = removeURL + "&patient_category="+patientType;
            params.put("patient_category", patientType);
        } else {
            patientType = "";
        }
		/*int checkedRGPatientType = rgPatientType.getCheckedRadioButtonId();
		switch (checkedRGPatientType){
			case R.id.rbTCM:
				params.put("patient_category", "tcm");
				break;
			case R.id.rbCC:
				params.put("patient_category", "complex_care");
				break;
			case R.id.rbHomeHealth:
				params.put("patient_category", "home_health");
				break;
			case R.id.rbNursingHome:
				params.put("patient_category", "nursing_home");
				break;
			default:

				break;
		}*/

        //DATA.print("--in saveAppointmentStatus  params: "+params.toString());
        pd.show();

        String reqURL = DATA.baseUrl + "/saveAppointmentStatus";

        DATA.print("-- Request : " + reqURL);
        DATA.print("-- params : " + params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in saveAppointmentStatus " + content);
                    //--reaponce in saveAppointmentStatus {"success":1,"message":"Saved."}
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("success")) {
                            Toast.makeText(activity, "Appointment processed", Toast.LENGTH_SHORT).show();

					/*Intent i = new Intent(activity, MainActivityNew.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);*/

                            if (patientType.isEmpty()) {
                                finish();
                            } else {
                                Intent i = new Intent(activity, ActivityFindNurse.class);
                                i.putExtra("patientType", patientType);
                                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: " + reqURL + ", http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.dismiss();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : " + reqURL + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }//end saveAppointmentStatus


    EditText editText;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    public void startVoiceRecognitionActivity(EditText editText) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // do whatever you want with the results
            this.editText.setText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    Dialog dialogRemovePatient;
    CheckBox cbSendAVS, cbSendAVSToPCP, cbSendBillingSummarytoBillers, cbProcessTheBillingProcessDirectly,
            cbTCM, cbCC, cbHomeHealth, cbNursingHome;
    //RadioGroup rgPatientType;
    //EditText etNote;
    //TextView btnAddNotes;


    public void initRemoveDialog(boolean shouldShowAssignOptions) {
        dialogRemovePatient = new Dialog(activity);
        dialogRemovePatient.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRemovePatient.setContentView(R.layout.dialog_remove_from_queue);
        dialogRemovePatient.setCanceledOnTouchOutside(false);
        dialogRemovePatient.setCancelable(false);

        TextView btnRemove = dialogRemovePatient.findViewById(R.id.btnRemove);
        TextView btnNotRemove = dialogRemovePatient.findViewById(R.id.btnNotRemove);
        cbSendAVS = dialogRemovePatient.findViewById(R.id.cbSendAVS);
        cbSendAVSToPCP = dialogRemovePatient.findViewById(R.id.cbSendAVSToPCP);
        cbSendBillingSummarytoBillers = dialogRemovePatient.findViewById(R.id.cbSendBillingSummarytoBillers);
        cbProcessTheBillingProcessDirectly = dialogRemovePatient.findViewById(R.id.cbProcessTheBillingProcessDirectly);
        cbTCM = dialogRemovePatient.findViewById(R.id.cbTCM);
        cbCC = dialogRemovePatient.findViewById(R.id.cbCC);
        cbHomeHealth = dialogRemovePatient.findViewById(R.id.cbHomeHealth);
        cbNursingHome = dialogRemovePatient.findViewById(R.id.cbNursingHome);
        //rgPatientType = (RadioGroup) dialogRemovePatient.findViewById(R.id.rgPatientType);
        // btnAddNotes = (TextView) dialogRemovePatient.findViewById(R.id.btnAddNotes);
        //etNote = (EditText) dialogRemovePatient.findViewById(R.id.etNote);

        LinearLayout layAssignOptions = dialogRemovePatient.findViewById(R.id.layAssignOptions);
        int vis = shouldShowAssignOptions ? View.VISIBLE : View.GONE;
        layAssignOptions.setVisibility(vis);

        View d1 = dialogRemovePatient.findViewById(R.id.d1);
        View d2 = dialogRemovePatient.findViewById(R.id.d2);
        View d3 = dialogRemovePatient.findViewById(R.id.d3);

        //check condtion current pt or past pt -- Ahmer 31 - 8 - 22
//		DATA.print("-- isFromCompleted " + LiveCareDetails.fromCompletedCare);
//		if (LiveCareDetails.fromCompletedCare) {
//			LiveCareDetails.fromCompletedCare = false;
//			btnRemove.setVisibility(View.GONE);
//		} else {
//			btnRemove.setVisibility(View.VISIBLE);
//		}
        //end

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRemove:
                        dialogRemovePatient.dismiss();
                        if (DATA.isFromAppointment) {
                            saveAppointmentStatus();
                        } else {
                            sendCallDoneReport(); // to remove patient from queue
                        }

                        break;
                    case R.id.btnNotRemove:
                        dialogRemovePatient.dismiss();
                        finish();
                        break;
				/*case R.id.btnAddNotes:
					//dialogRemovePatient.dismiss();
					//initAddNotesDialog();
					//add notes

					String note = etNote.getText().toString();
					if (note.isEmpty()) {
						Toast.makeText(activity, "Please add a note !", Toast.LENGTH_SHORT).show();
					} else {

						if (checkInternetConnection.isConnectedToInternet()) {
							//add note here
							saveDrNotes(DATA.selectedUserAppntID, note);//userID here
						} else {
							Toast.makeText(activity, "No internet connection !", Toast.LENGTH_SHORT).show();
						}
					}

					break;*/

                    default:
                        break;
                }

            }
        };

        btnRemove.setOnClickListener(clickListener);
        btnNotRemove.setOnClickListener(clickListener);
        //btnAddNotes.setOnClickListener(clickListener);


        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (cbTCM.isChecked() && cbCC.isChecked()) {
                    d1.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    d1.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                }
                if (cbCC.isChecked() && cbHomeHealth.isChecked()) {
                    d2.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    d2.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                }
                if (cbHomeHealth.isChecked() && cbNursingHome.isChecked()) {
                    d3.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    d3.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                }

                switch (buttonView.getId()) {
                    case R.id.cbTCM:
                        cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        break;
                    case R.id.cbCC:
                        cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        break;
                    case R.id.cbHomeHealth:
                        cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        break;
                    case R.id.cbNursingHome:
                        cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
                        break;
                    default:
                        break;
                }
            }
        };
        cbTCM.setOnCheckedChangeListener(onCheckedChangeListener);
        cbCC.setOnCheckedChangeListener(onCheckedChangeListener);
        cbHomeHealth.setOnCheckedChangeListener(onCheckedChangeListener);
        cbNursingHome.setOnCheckedChangeListener(onCheckedChangeListener);

        dialogRemovePatient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogRemovePatient.show();
    }


    public void initRemoveFromMyPatientsDialog() {
        final Dialog dialogRemovePatient = new Dialog(activity);
        dialogRemovePatient.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRemovePatient.setContentView(R.layout.dialog_remove_from_mypatients);

        TextView btnRemove = dialogRemovePatient.findViewById(R.id.btnRemove);
        TextView btnNotRemove = dialogRemovePatient.findViewById(R.id.btnNotRemove);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRemove:
                        dialogRemovePatient.dismiss();

                        removeFromMyPatients();

                        break;
                    case R.id.btnNotRemove:
                        dialogRemovePatient.dismiss();
                        finish();
                        break;

                    default:
                        break;
                }

            }
        };

        btnRemove.setOnClickListener(clickListener);
        btnNotRemove.setOnClickListener(clickListener);
        //btnAddNotes.setOnClickListener(clickListener);

        dialogRemovePatient.show();
    }


    public void removeFromMyPatients() {
        RequestParams params = new RequestParams();
        //params.put("user_id", prefs.getString("id", ""));
        params.put("id", ActivityRefferedPatients.referredId);

        ApiManager apiManager = new ApiManager(ApiManager.REMOVE_REFERRED, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if (apiName.equalsIgnoreCase(ApiManager.REMOVE_REFERRED)) {
            try {//{"status":"success","message":"Deleted."}
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    ActivityRefferedPatients.shouldRefresh = true;
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GET_TELEMEDICINE_SERVICES)) {

            parseTelemedicineData(content);

        } else if (apiName.equalsIgnoreCase(ApiManager.GET_LIVECARE_TELEMEDICINE_SERVICES)) {

            parseLiveCareTelemedicineData(content);

        } else if (apiName.equals(ApiManager.SAVE_FAVOURITE_SERVICES)) {
            //{"status":"success","message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    customToast.showToast("Selected services has been saved to your favourite", 0, 1);
                    if (DATA.isVideoCallFromLiveCare) {
                        getLiveCareTelemedicineServices();
                    } else {
                        getTelemedicineServices();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equals(ApiManager.REMOVE_FAVOURITE_SERVICES)) {
            //{"status":"success","message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    customToast.showToast("Selected services has been removed from your favourite", 0, 1);
                    if (DATA.isVideoCallFromLiveCare) {
                        getLiveCareTelemedicineServices();
                    } else {
                        getTelemedicineServices();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        }
        else if (apiName.equalsIgnoreCase(ApiManager.BILL_WITHOUT_NOTE)) {
            //{"success":1,"message":"Saved.","note_id":1878}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    if (autoNoteStatus) {
                        autoNoteStatus = false;
                        dialogBoxAiResultFinal.setVisibility(View.GONE);
                        btnAfterCallEndConv.setVisibility(View.GONE);
                        layEndConvOptions.setVisibility(View.VISIBLE);
                        sharedPrefsHelper.save("encounter_notesDurringCall", "");
                        customToast.showToast("Auto Encounter Notes submitted successfully", 0, 0);
                    } else {
                        sharedPrefsHelper.save("encounter_notesDurringCall", "");
                        customToast.showToast("Your service billing codes request has been submitted successfully", 0, 0);
                        //DATA.isSOAP_NotesSent = true;
                        //finish();

                        if (DATA.isVideoCallFromLiveCare) {
                            DATA.isVideoCallFromLiveCare = false;
                            initRemoveDialog(true);
                        } else {
                            finish();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SAVE_DISCHARGE_NOTE)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    customToast.showToast("Your Discharge Summary saved successfully", 0, 0);
                    // finish();
//                    if (DATA.isVideoCallFromLiveCare) {
//                        DATA.isVideoCallFromLiveCare = false;
//                        initRemoveDialog(true);
//                    } else {
//                        finish();
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.contains(ApiManager.GET_LABS)) {
            try {
                JSONArray jsonArray = new JSONArray(content);
                ArrayList<LabReportBean> labReportBeans = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<LabReportBean>>() {
                }.getType());
                if (labReportBeans != null) {
                    showLabReportsDialog(labReportBeans);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SAVE_LABS)) {
            //{"status":"success","message":"Lab request has been saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(R.string.app_name)
                            .setMessage(jsonObject.getString("message"))
                            .setPositiveButton("Done", null)
                            .create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (dialogLabReportsToDismiss != null) {
                                dialogLabReportsToDismiss.dismiss();
                            }

                            if (DATA.isVideoCallFromLiveCare) {//Same code on complete the conversation click
                                DATA.isVideoCallFromLiveCare = false;
                                initRemoveDialog(true);
                            } else if (DATA.isVideoCallFromRefPt) {
                                DATA.isVideoCallFromRefPt = false;
                                initRemoveFromMyPatientsDialog();
                            } else {
                                if (android.os.Build.VERSION.SDK_INT >= 21) {
                                    finishAndRemoveTask();
                                } else {
                                    finish();
                                }
                            }
                        }
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SAVE_FAV_LABS) || apiName.equalsIgnoreCase(ApiManager.REM_FAV_LABS)) {
            //{"status":"success","message":"Deleted"}
            //{"status":"success","message":"Saved"}

            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    if (dialogLabReportsToDismiss != null) {
                        dialogLabReportsToDismiss.dismiss();
                    }
                    getLabReports();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GENERATE_CALL_CONVERSATION)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    initAutoConvoNoteDialog(jsonObject.getString("data"));
                }
                if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    customToast.showToast(jsonObject.getString("message"), 0, 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
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
                    lvTelemed2.setAdapter(new TelemedicineAdapter2(activity, catData));
                    tvElivCatName.setText(category_name);
                }

            }

            TelemedicineAdapter adapter = new TelemedicineAdapter(activity, telemedicineCategories);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            if (lvTelemedicineData != null) {
                lvTelemedicineData.setAdapter(wrapperAdapter);
            }

            sharedPrefsHelper.save(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, content);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            e.printStackTrace();
        }

    }

    public void parseLiveCareTelemedicineData(String content) {
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

                if (category_name.equalsIgnoreCase("AI Suggested") && lvTelemed2 != null) {
                    tvElivCatName.setText(category_name);
                    lvTelemed2.setAdapter(new TelemedicineAdapter2(activity, catData));
                }

            }
            TelemedicineAdapter adapter = new TelemedicineAdapter(activity, telemedicineCategories);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            if (lvTelemedicineData != null) {
                lvTelemedicineData.setAdapter(wrapperAdapter);
            }

            sharedPrefsHelper.save(ActivityTelemedicineServices.LIVECARE_TELEMEDICINE_PREFS_KEY, content);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            e.printStackTrace();
        }

    }


    //==========================Emcura New Flow


    //Lab Reports

    public void getLabReports() {
        RequestParams params = new RequestParams();
        //params.put("doctor_id",prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_LABS + "/" + prefs.getString("id", ""), "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void saveLabReports(String selectedLabIds, String diagnosis_desc) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("code_ids", selectedLabIds);
        params.put("diagnosis_desc", diagnosis_desc);

        ApiManager apiManager = new ApiManager(ApiManager.SAVE_LABS, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void addLabToFav(List<String> selectedLabIds) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("code_ids", selectedLabIds);

        ApiManager apiManager = new ApiManager(ApiManager.SAVE_FAV_LABS, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void removeLabToFav(List<String> selectedLabIds) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("code_ids", selectedLabIds);

        ApiManager apiManager = new ApiManager(ApiManager.REM_FAV_LABS, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    Dialog dialogLabReportsToDismiss;

    public void showLabReportsDialog(ArrayList<LabReportBean> labReportBeans) {
        Dialog dialogLabReports = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogLabReports.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //dialogAddTemplPresc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogLabReports.setContentView(R.layout.dialog_lab_reports);

        FloatingGroupExpandableListView lvLabReports = (FloatingGroupExpandableListView) dialogLabReports.findViewById(R.id.lvLabReports);
        Button btnSendLab = (Button) dialogLabReports.findViewById(R.id.btnSendLab);

        Button btnLabAddFav = (Button) dialogLabReports.findViewById(R.id.btnLabAddFav);
        Button btnLabRemFav = (Button) dialogLabReports.findViewById(R.id.btnLabRemFav);
        ImageView ivClose = dialogLabReports.findViewById(R.id.ivClose);

        EditText etLabRepDiag = dialogLabReports.findViewById(R.id.etLabRepDiag);

        LabReportsAdapter labReportsAdapter = new LabReportsAdapter(activity, labReportBeans);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(labReportsAdapter);
        lvLabReports.setAdapter(wrapperAdapter);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.btnSendLab) {
                    StringBuilder sbSelectedLabIds = new StringBuilder();
                    for (int i = 0; i < labReportBeans.size(); i++) {
                        ArrayList<LabReportBean.LabReportDataBean> labReportDataBeans = labReportBeans.get(i).labReportDataBeans;
                        for (int j = 0; j < labReportDataBeans.size(); j++) {
                            if (labReportDataBeans.get(j).isSelected) {
                                sbSelectedLabIds.append(labReportDataBeans.get(j).id);
                                sbSelectedLabIds.append(",");
                            }
                        }
                    }

                    String selectedLabIds = sbSelectedLabIds.toString();
                    if (!selectedLabIds.isEmpty()) {
                        if (selectedLabIds.endsWith(",")) {
                            selectedLabIds = selectedLabIds.substring(0, selectedLabIds.length() - 1);
                        }
                        saveLabReports(selectedLabIds, etLabRepDiag.getText().toString().trim());
                    } else {
                        customToast.showToast("Please select any lab request.", 0, 1);
                    }
                } else if (view.getId() == R.id.btnLabAddFav) {
                    //dialogLabReports.dismiss();

                    List<String> selectedLabIds = new ArrayList<>();
                    for (int i = 0; i < labReportBeans.size(); i++) {
                        ArrayList<LabReportBean.LabReportDataBean> labReportDataBeans = labReportBeans.get(i).labReportDataBeans;
                        for (int j = 0; j < labReportDataBeans.size(); j++) {
                            if (labReportDataBeans.get(j).isSelected) {
                                selectedLabIds.add(labReportDataBeans.get(j).id);
                            }
                        }
                    }
                    if (!selectedLabIds.isEmpty()) {
                        addLabToFav(selectedLabIds);
                    } else {
                        customToast.showToast("Please select any lab request.", 0, 1);
                    }

                } else if (view.getId() == R.id.btnLabRemFav) {
                    //dialogLabReports.dismiss();

                    List<String> selectedLabIds = new ArrayList<>();
                    for (int i = 0; i < labReportBeans.size(); i++) {
                        ArrayList<LabReportBean.LabReportDataBean> labReportDataBeans = labReportBeans.get(i).labReportDataBeans;
                        for (int j = 0; j < labReportDataBeans.size(); j++) {
                            if (labReportDataBeans.get(j).isSelected) {
                                selectedLabIds.add(labReportDataBeans.get(j).id);
                            }
                        }
                    }
                    if (!selectedLabIds.isEmpty()) {
                        removeLabToFav(selectedLabIds);
                    } else {
                        customToast.showToast("Please select any lab request.", 0, 1);
                    }

                } else if (view.getId() == R.id.ivClose) {
                    dialogLabReports.dismiss();
                }
            }
        };
        btnSendLab.setOnClickListener(onClickListener);
        btnLabAddFav.setOnClickListener(onClickListener);
        btnLabRemFav.setOnClickListener(onClickListener);
        ivClose.setOnClickListener(onClickListener);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogLabReports.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogLabReports.setCanceledOnTouchOutside(false);
        dialogLabReports.show();
        dialogLabReports.getWindow().setAttributes(lp);

        dialogLabReportsToDismiss = dialogLabReports;

    }


    //Telemedicine billing codes

    ArrayList<TelemedicineCategories> telemedicineCategories;

    public void getLiveCareTelemedicineServices() {

        String checheData = sharedPrefsHelper.get(ActivityTelemedicineServices.LIVECARE_TELEMEDICINE_PREFS_KEY, "");
        if (!TextUtils.isEmpty(checheData)) {
            parseLiveCareTelemedicineData(checheData);
            ApiManager.shouldShowPD = false;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("livecare_id", DATA.selectedLiveCare.liveCheckupId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_LIVECARE_TELEMEDICINE_SERVICES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getTelemedicineServices() {

        String checheData = sharedPrefsHelper.get(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, "");
        if (!TextUtils.isEmpty(checheData)) {
            parseTelemedicineData(checheData);
            ApiManager.shouldShowPD = false;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("livecare_id", DATA.selectedLiveCare.liveCheckupId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    FloatingGroupExpandableListView lvTelemedicineData;
    ListView lvTelemed2;
    Dialog dialogTelemedicineSer;
    TextView tvElivCatName;

    public void showTelemedicineDialog() {
        dialogTelemedicineSer = new Dialog(activity, R.style.TransparentThemeH4B);
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

        OnClickListener onClickListener = new OnClickListener() {
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
                        btnAfterCallEndConv.setVisibility(View.VISIBLE);
                        dialogBoxAiResultFinal.setVisibility(View.GONE);
                        layEndConvOptions.setVisibility(View.GONE);
                        dialogBoxAiResultFinal.setVisibility(View.GONE);
                        dialogTelemedicineSer.dismiss();
                    }
                    //Ahmer code
                    if (fromChatGPT) {
                        fromChatGPT = false;
                        billWithoutNote(tmsCodes, etNoteFromAiFinal.getText().toString());
                        //btnAfterCallEndConv.setVisibility(View.GONE);
                        //dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
                    } else {
                        initNoteDialog();
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
            dialogBoxAiResultFinal.setVisibility(View.GONE);
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

        if (DATA.isVideoCallFromLiveCare) {
            getLiveCareTelemedicineServices();
        } else {
            getTelemedicineServices();
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


    StringBuilder sbSelectedTMSCodes, sbSelectedTMSCodesWithNames;
    String tmsCodes, tmsCodesWithNames;

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
                new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
                        setMessage("You do not selected any service. Do you want to skip ?")
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tmsCodes = "";
                                tmsCodesWithNames = "";
                                DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                                if (dialogTelemedicineSer != null) {
                                    sharedPrefsHelper.save("encounter_notesDurringCall", "");
                                    dialogBoxAiResultFinal.setVisibility(View.GONE);
                                    dialogTelemedicineSer.dismiss();
                                }
                                if (fromChatGPT) {
                                    fromChatGPT = false;
                                    billWithoutNote(tmsCodes, etNoteFromAiFinal.getText().toString());
                                    //sharedPrefsHelper.save("encounter_notesDurringCall", "");
                                    btnAfterCallEndConv.setVisibility(View.VISIBLE);
                                    dialogBoxAiResultFinal.setVisibility(View.GONE);
                                } else {
                                    initNoteDialog();
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
                    dialogBoxAiResultFinal.setVisibility(View.GONE);
                    dialogTelemedicineSer.dismiss();
                }
                if (fromChatGPT) {
                    fromChatGPT = false;
                    btnAfterCallEndConv.setVisibility(View.GONE);
                    layEndConvOptions.setVisibility(View.VISIBLE);
                    dialogBoxAiResultFinal.setVisibility(View.GONE);
                    billWithoutNote(tmsCodes, etNoteFromAiFinal.getText().toString());
                } else {
                    initNoteDialog();
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


    public void addToFav() {
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
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_FAVOURITE_SERVICES, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
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
                ApiManager apiManager = new ApiManager(ApiManager.REMOVE_FAVOURITE_SERVICES, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }


    //============Virtual Visit Note
    public static String use_for_billing = "Yes";

    public void billWithoutNote(String tmsCodes, String note_text) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("treatment_codes", tmsCodes);
        params.put("start_time", DATA.elivecare_start_time);
        params.put("note_text", note_text);
        params.put("use_for_billing", use_for_billing);//(Yes,No)

        //GM added call_id
        String callId = "0";
        if (DATA.incomingCall) {
            callId = DATA.incommingCallId;
        }
        else  if(DATA.isExistingPatCall){
            callId = DATA.incommingCallId;
        }
        else {
            callId = prefs.getString("callingID", "");
        }
        params.put("call_id", callId);
        //GM added call_id

        ApiManager apiManager = new ApiManager(ApiManager.BILL_WITHOUT_NOTE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    String note_text = "";

    public void initNoteDialog() {
        Dialog dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.setContentView(R.layout.dialog_billwithnote);
        dialogNote.setCanceledOnTouchOutside(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        EditText etNote = dialogNote.findViewById(R.id.etNote);
        TextView tvSubmitNote = dialogNote.findViewById(R.id.tvSubmitNote);
        TextView tvPreviousNotes = dialogNote.findViewById(R.id.tvPreviousNotes);
        ImageView ivClose = dialogNote.findViewById(R.id.ivClose);

        tvNotePatientName.setText(DATA.selectedUserCallName);
        tvNoteBillingCodes.setText(tmsCodesWithNames);


        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNote.dismiss();
            }
        });

        tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogAllEncNotes(activity).getNotes(DATA.selectedUserCallId);
            }
        });

        tvSubmitNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                note_text = etNote.getText().toString().trim();
                if (note_text.isEmpty()) {
                    customToast.showToast("Please enter your virtual visit note", 0, 0);
                    etNote.setError("Please enter your virtual visit note");
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {

                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? You want to save your notes or edit and review first.")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogNote.dismiss();
                                        //billWithoutNote(tmsCodes, note_text);
                                        billWithoutNote(tmsCodes, note_text);
                                    }
                                })
                                .setNegativeButton("Edit", null)
                                .create()
                                .show();

                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogNote.show();
        dialogNote.getWindow().setAttributes(lp);
    }

    //Ahmer code for chatGPT auto Note
    final String API_URL = "https://api.openai.com/v1/completions";
    final String API_SECRET_KEY = SharedPrefsHelper.getInstance().get("gpt_key","");//"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";

    private void callChatGPTAPI(String query) {
        customProgressDialog.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("temperature", 0);
            params.put("max_tokens", 100);
            params.put("top_p", 1);
            params.put("frequency_penalty", 0.0);
            params.put("presence_penalty", 0.0);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), "https://api.openai.com/v1/completions", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    String clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + clearedResponse);
                    DATA.print("-- response frm chatgpt : " + response);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(DATA.selectedUserCallName).append(" ")
                            .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                            .append("\n").append(clearedResponse);
                    customProgressDialog.dismissProgressDialog();
                    initAutoNoteDialog(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("GPT Error", errorResponse.toString());
                customProgressDialog.dismissProgressDialog();
            }
        });

    }

    public void initAutoNoteDialog(String autoNote) {
        Dialog dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.setContentView(R.layout.dialog_chatgpt_autonote);
        dialogNote.setCanceledOnTouchOutside(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        EditText etNote = dialogNote.findViewById(R.id.etNote);
        TextView tvSubmitNote = dialogNote.findViewById(R.id.tvSubmitNote);
        TextView tvPreviousNotes = dialogNote.findViewById(R.id.tvPreviousNotes);
        ImageView ivClose = dialogNote.findViewById(R.id.ivClose);

        tvNotePatientName.setText(DATA.selectedUserCallName);
        tvNoteBillingCodes.setText(tmsCodesWithNames);

        etNote.setText(autoNote);


        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNote.dismiss();
            }
        });

        tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogAllEncNotes(activity).getNotes(DATA.selectedUserCallId);
            }
        });

        tvSubmitNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                note_text = etNote.getText().toString().trim();
                if (note_text.isEmpty()) {
                    customToast.showToast("Please enter your virtual visit note", 0, 0);
                    etNote.setError("Please enter your virtual visit note");
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {

                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? You want to save your notes or edit and review first.")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogNote.dismiss();
                                        billWithoutNote(tmsCodes, note_text);
                                    }
                                })
                                .setNegativeButton("Edit", null)
                                .create()
                                .show();

                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogNote.show();
        dialogNote.getWindow().setAttributes(lp);
    }

    //Ahmer new work for disc Notes
    String disc_note_text = "";

    public void initDiscNoteDialog() {
        Dialog dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.setContentView(R.layout.dialog_discnote);
        dialogNote.setCanceledOnTouchOutside(false);
        dialogNote.setCancelable(false);
        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        etDischareNote = dialogNote.findViewById(R.id.etNote);
        TextView tvSubmitDiscNote = dialogNote.findViewById(R.id.tvSubmitDiscNote);
        TextView tvPreviousNotes = dialogNote.findViewById(R.id.tvPreviousNotes);
        TextView tvPatSymptoms = dialogNote.findViewById(R.id.pat_symptoms);
        ImageView ivClose = dialogNote.findViewById(R.id.ivClose);
        TextView tvAIDischargeNote = dialogNote.findViewById(R.id.tvAiDischargeSummary);
        tvNotePatientName.setText(DATA.selectedUserCallName);
        tvNoteBillingCodes.setText(tmsCodesWithNames);
        tvPatSymptoms.setText(DATA.selectedUserCallCondition);



        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNote.dismiss();
            }
        });
        tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogAllEncNotes(activity).getNotes(DATA.selectedUserCallId);
            }
        });
        tvSubmitDiscNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                disc_note_text = etDischareNote.getText().toString().trim();
                if (disc_note_text.isEmpty()) {
                    customToast.showToast("Please enter your virtual visit note", 0, 0);
                    etDischareNote.setError("Please enter your virtual visit note");
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {

                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? You want to save your notes or edit and review first.")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogNote.dismiss();
                                        addDischargeNotes(disc_note_text);
                                    }
                                })
                                .setNegativeButton("Edit", null)
                                .create()
                                .show();

                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        tvAIDischargeNote.setOnClickListener(view -> {
           // String query = "Write a discharge summary for a patient "+DATA.selectedUserCallName+" having symptoms : "+DATA.selectedUserCallCondition+". Patient further told that : "+DATA.selectedUserCallDescription+".  Please do not add any information from your side. Patient's DOB is : "+DATA.selectedUserCallDOB;
             // getAIDischargeNotes(query);

            GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                getAIDischargeNotes(queryForDischargeSummery);
            },()->{

            },"OK","Not Now","Disclaimer");

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogNote.show();
        dialogNote.getWindow().setAttributes(lp);
    }

    private void initAutoConvoNoteDialog(String data) {
        Dialog dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.setContentView(R.layout.dialog_autoconvo_note);
        dialogNote.setCanceledOnTouchOutside(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        EditText etNoteConvo = dialogNote.findViewById(R.id.etNoteConvo);
        TextView tvSubmitDiscNote = dialogNote.findViewById(R.id.tvSubmitDiscNote);
        TextView tvDiscardNote = dialogNote.findViewById(R.id.tvDiscardNote);
        ImageView ivClose = dialogNote.findViewById(R.id.ivClose);

        tvNotePatientName.setText(DATA.selectedUserCallName);
        tvNoteBillingCodes.setText(tmsCodesWithNames);

        etNoteConvo.setText(data);

        ivClose.setOnClickListener(v -> dialogNote.dismiss());

        tvSubmitDiscNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                autoNoteStatus = true;
                btnAfterCallEndConv.setVisibility(View.GONE);
                layEndConvOptions.setVisibility(View.GONE);
                dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
                etNoteFromAiFinal.setText(etNoteConvo.getText().toString());
                //billWithoutNote(tmsCodes, etNoteConvo.getText().toString());
                etNoteConvo.setText("");
                dialogNote.dismiss();
            }
        });

        tvDiscardNote.setOnClickListener(v -> {
            etNoteConvo.setText("");
            dialogNote.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogNote.show();
        dialogNote.getWindow().setAttributes(lp);
    }



   private  void getAIDischargeNotes(String query){

       customProgressDialog.showProgressDialog();
       AsyncHttpClient client = new AsyncHttpClient();
       JSONObject params = new JSONObject();
       try {
           params.put("model", "gpt-3.5-turbo-instruct");
           params.put("prompt", query);
           params.put("max_tokens", 1000);

       } catch (JSONException e) {
           throw new RuntimeException(e);
       }

       DATA.print("-- query " + query);

       StringEntity entity = new StringEntity(params.toString(), "UTF-8");

       client.addHeader("Content-Type", "application/json");
       client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

       client.post(activity, API_URL, entity, "application/json", new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               try {
                   String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                   DATA.print("-- GPT RESPONSE " + text);
                   // Remove first word before "\n"
                   //clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                   // Remove both instances of "\n"
                   //clearedResponse = clearedResponse.replaceAll("\\n", "");
                   DATA.print("-- cleanedResponse frm chatgpt : " + text);
                   DATA.print("-- response frm chatgpt : " + response);

                   customProgressDialog.dismissProgressDialog();
                   StringBuilder sb = new StringBuilder();
                   sb.append("Patient Name : ").append(DATA.selectedUserCallName).append(" ")
                           .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                           .append("\n").append(text);
                   DATA.print("dobs="+DATA.selectedUserCallDOB+" name = "+DATA.selectedUserCallName+" "+text);
                       etDischareNote.setText(sb.toString());


               } catch (Exception e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               customProgressDialog.dismissProgressDialog();
           }
       });


   }
    private void addDischargeNotes(String noteText) {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("note_text", noteText);
        ApiManager apiManager = new ApiManager(ApiManager.SAVE_DISCHARGE_NOTE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    private void getConvoNotes() {
        String callId = "0";
//        if (DATA.incomingCall) {
//            callId = DATA.incommingCallId;
//        }
//        else  if(DATA.isExistingPatCall){
//            callId = DATA.incommingCallId;
//        }
        if(!DATA.incommingCallId.equals("")){
            callId = DATA.incommingCallId;
        }
        else {
            callId = prefs.getString("callingID", "");
        }
        RequestParams params = new RequestParams();
        params.put("call_id", callId);
        ApiManager apiManager = new ApiManager(ApiManager.GENERATE_CALL_CONVERSATION, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    //ahmer work for ai based notes after call

    //ahmer's work for taking note during video call - 23-02-2023
    //Ahmer's code for Add note during call functionality
    LinearLayout dialogBoxSimpleNote, dialogBoxAskAi, dialogBoxAiResultaftercall;
    String note_text_ai = "";
    Dialog dialogNote;
    TextView tvGotoAiNotes, tvAskNowAi, tvAddAiNote,tvCancel;
    EditText etNote, etAiAskNote, etNoteFromAi;
    ImageView ivCloseAi;

    public void initAiNoteDialog() {
        dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.getWindow().setGravity(Gravity.TOP);
        //dialogNote.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogNote.setContentView(R.layout.dialog_chatgpt_autonote);
        dialogNote.setCanceledOnTouchOutside(false);
        dialogNote.setCancelable(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        etNote = dialogNote.findViewById(R.id.etNote);
        ImageView ic_mike_LiveExtraInfo = dialogNote.findViewById(R.id.ic_mike_LiveExtraInfo);
        TextView tvSubmitNote = dialogNote.findViewById(R.id.tvSubmitNote);
        TextView tvPreviousNotes = dialogNote.findViewById(R.id.tvPreviousNotes);
        tvCancel = dialogNote.findViewById(R.id.tvCancelDialog);
        tvPreviousNotes.setVisibility(View.GONE);
        ic_mike_LiveExtraInfo.setVisibility(View.GONE);
        ivCloseAi = dialogNote.findViewById(R.id.ivClose);
        //ivCloseAi.setVisibility(View.GONE);

        //Ahmer's code for AI
        tvGotoAiNotes = dialogNote.findViewById(R.id.tvGotoAiNotes);
        tvAskNowAi = dialogNote.findViewById(R.id.tvAskNowAi);
        tvAddAiNote = dialogNote.findViewById(R.id.tvAddAiNote);
        etAiAskNote = dialogNote.findViewById(R.id.etAiAskNote);
        etNoteFromAi = dialogNote.findViewById(R.id.etNoteFromAi);
        dialogBoxSimpleNote = dialogNote.findViewById(R.id.dialogBoxSimpleNote);
        dialogBoxAskAi = dialogNote.findViewById(R.id.dialogBoxAskAi);
        dialogBoxAiResultaftercall = dialogNote.findViewById(R.id.dialogBoxAiResultaftercall);
        callChatGPTAPI_forAiBasedNotes(queryForAiNotes);

        tvGotoAiNotes.setOnClickListener(v -> {
            dialogBoxSimpleNote.setVisibility(View.GONE);
            dialogBoxAskAi.setVisibility(View.VISIBLE);
        });

       // etAiAskNote.setText("write medical note for the patient having symptoms: " + DATA.selectedUserCallCondition + " patient name is :" + DATA.selectedUserCallName + " and patient DOB is: " + DATA.selectedUserCallDOB);
        etAiAskNote.setText(queryForAiNotes);
        String query = etAiAskNote.getText().toString();
        //callChatGPTAPI_forAiBasedNotes(query);

        tvAskNowAi.setOnClickListener(v -> {
            callChatGPTAPI_forAiBasedNotes(queryForAiNotes);

        });

        tvAddAiNote.setOnClickListener(v -> {
//            dialogBoxSimpleNote.setVisibility(View.GONE);
//            dialogBoxAskAi.setVisibility(View.GONE);
//            dialogBoxAiResultaftercall.setVisibility(View.GONE);
            dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
            etNoteFromAiFinal.setText(clearedResponse);
            etNote.setText(clearedResponse);
            dialogNote.dismiss();
        });

        //end

        tvNotePatientName.setText(DATA.selectedUserCallName);
        //tvNoteBillingCodes.setText(tmsCodesWithNames);

        ivCloseAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layEndConvOptions.setVisibility(View.VISIBLE);
                dialogNote.dismiss();
            }
        });

        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                layEndConvOptions.setVisibility(View.VISIBLE);
                dialogNote.dismiss();
            }
        });

        /*tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogAllEncNotes(activity).getNotes(DATA.selectedUserCallId);
            }
        });*/

        tvSubmitNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                note_text = etNote.getText().toString().trim();
                if (note_text.isEmpty()) {
                    customToast.showToast("Please enter your virtual visit note", 0, 0);
                    etNote.setError("Please enter your virtual visit note");
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {
                        note_text = etNote.getText().toString();
                        etNoteFromAiFinal.setText(note_text);
                        dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
                        dialogNote.dismiss();

                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

       // dialogNote.show();

        dialogNote.getWindow().setAttributes(lp);
    }

    String clearedResponse;

    private void callChatGPTAPI_forAiBasedNotes(String query) {
        customProgressDialog.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("max_tokens", 1000);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), API_URL, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + clearedResponse);
                    DATA.print("-- response frm chatgpt : " + response);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(DATA.selectedUserCallName).append(" ")
                            .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                            .append("\n").append(text);
                    customProgressDialog.dismissProgressDialog();
                    dialogBoxAskAi.setVisibility(View.VISIBLE);
                    dialogBoxAiResultaftercall.setVisibility(View.VISIBLE);
                    etNoteFromAi.setText(sb.toString());
                    btnAfterCallEndConv.setVisibility(View.GONE);
                    layEndConvOptions.setVisibility(View.GONE);
                    tvAskNowAi.setText("Generate Note Again");
                    ivCloseAi.setVisibility(View.VISIBLE);
                    dialogNote.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                customProgressDialog.dismissProgressDialog();
            }
        });

    }
}
