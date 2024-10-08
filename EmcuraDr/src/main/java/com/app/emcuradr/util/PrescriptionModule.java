package com.app.emcuradr.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.emcuradr.ActivitySoapNotes;
import com.app.emcuradr.ActivitySoapNotesEditNew;
import com.app.emcuradr.ActivitySoapNotesEmpty;
import com.app.emcuradr.ActivitySoapNotesNew;
import com.app.emcuradr.ActivityTcmDetails;
import com.app.emcuradr.AfterCallDialogEmcura;
import com.app.emcuradr.R;
import com.app.emcuradr.adapter.DrugsAdapter;
import com.app.emcuradr.adapter.IcdCodesAdapter;
import com.app.emcuradr.adapter.TemplPrescAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.model.DrugBean;
import com.app.emcuradr.model.IcdCodeBean;
import com.app.emcuradr.model.PotencyUnitBean;
import com.app.emcuradr.model.PrescripTempletBean;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PrescriptionModule implements ApiCallBack {


    AppCompatActivity activity;
    ProgressDialog pd;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    GloabalMethods gloabalMethods;
    ApiCallBack apiCallBack;
    public HideShowKeypad hideShowKeypad;

    Dialog_CustomProgress customProgressDialog;

    ViewFlipper viewFlipper;

    Button btnFlipPrev, btnFlipNext;
    TextView tvStep1, tvStep2;



     EditText etInstructions;

    final String API_URL = "https://api.openai.com/v1/completions";
    final String API_SECRET_KEY = SharedPrefsHelper.getInstance().get("gpt_key", "");

    ActionEditText actionEditText;

    SharedPrefsHelper sharedPrefsHelper;

    ArrayList<String> gptQueryList;

    String queryForInstructions = "",queryForPrescription = "";
    String disclaimer = "";


    public PrescriptionModule(AppCompatActivity activity) {
        this.activity = activity;
        hideShowKeypad = new HideShowKeypad(activity);
        customProgressDialog = new Dialog_CustomProgress(activity);

        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        gloabalMethods = new GloabalMethods(activity);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        gptQueryList = sharedPrefsHelper.getGptQuerylist();
        if(!gptQueryList.isEmpty()){



            queryForPrescription = gptQueryList.get(3);
            queryForPrescription = queryForPrescription.replace("[patientCondition]", DATA.selectedUserCallCondition)
                    .replace("\\/", " or ")
                    .replace("[patientDesc]",DATA.selectedUserCallDescription);
            DATA.print("noty"+queryForPrescription);
        }
        else {
            customToast.showToast("No queries found",0,0);
        }
        disclaimer = sharedPrefsHelper.get("disclaimer");
        customToast = new CustomToast(activity);
        apiCallBack = this;
        pd = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle(activity.getResources().getString(R.string.app_name));
        pd.setMessage("Please wait ...");

        initPrescripDialog();
    }


    private void callChatGPTAPIAiPresc(String query, boolean isRefresh) {

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
                    if (isRefresh) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Patient Name : ").append(DATA.selectedUserCallName).append(" ")
                                .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                                .append("\n").append(text);
                        actionEditText.setText(sb.toString());
                    } else {
                        showAISuggestedPrescription(text);
                    }


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




    private void callChatGPTAPIAiInstructions(String query) {

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
                    etInstructions.setText(sb.toString());

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


    Dialog verDialog;

    String[] rxFillIndecArr = {"None", "All Fill Statuses", "All Fill Statuses Except Transferred"
            , "Cancel All Fill Statuses", "Not Dispensed", "Not Dispensed And Transferred",
            "Partially Dispensed", "Partially Dispensed And Not Dispensed"};

    public void initDoubleVerficationDialog() {
        verDialog = new Dialog(activity);
        verDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verDialog.setContentView(R.layout.dialog_verification);

        final TextView tvMessage = verDialog.findViewById(R.id.tvMessage);
        final EditText etPincode = verDialog.findViewById(R.id.etPincode);
        Button btnEnterPincode = verDialog.findViewById(R.id.btnEnterPincode);
        Button btnForgotPincode = verDialog.findViewById(R.id.btnForgotPincode);

        btnEnterPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (etPincode.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter your prescription pincode", Toast.LENGTH_SHORT).show();
                } else {
                    if (prefs.getString("pincode", "").equals(etPincode.getText().toString())) {


                        if (checkInternetConnection.isConnectedToInternet()) {

                            verDialog.dismiss();
                            //sendCallDoneReport();  to remove patient from queue
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(prescriptionsDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            prescriptionsDialog.show();
                            prescriptionsDialog.getWindow().setAttributes(lp);

                        } else {

                            Toast.makeText(activity, "Not connected to Internet", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        tvMessage.setText("Incorrect pincode. Possible typing mistake?");
                    }
                }
            }
        });

        btnForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                initForgotPincodeDialogDialog();
            }
        });

        verDialog.show();

    }


    Dialog forgotPincodeDialog;

    public void initForgotPincodeDialogDialog() {
        forgotPincodeDialog = new Dialog(activity);
        forgotPincodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgotPincodeDialog.setContentView(R.layout.dialog_forgot_pincode);


        final EditText etEmailForgotPincode = forgotPincodeDialog.findViewById(R.id.etEmailForgotPincode);
        Button btnEnterForgotPincode = forgotPincodeDialog.findViewById(R.id.btnEnterForgotPincode);
        Button btnCancelForgotPincode = forgotPincodeDialog.findViewById(R.id.btnCancelForgotPincode);

        btnEnterForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (etEmailForgotPincode.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter your email used for OnlineCare account", Toast.LENGTH_SHORT).show();
                } else {

                    if (checkInternetConnection.isConnectedToInternet()) {
                        forgotPincode(etEmailForgotPincode.getText().toString());
                    } else {

                        Toast.makeText(activity, "Not connected to Internet", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        btnCancelForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                forgotPincodeDialog.dismiss();
            }
        });

        forgotPincodeDialog.show();

    }


    public void forgotPincode(String email) {

        pd.setMessage("Please wait...    ");
        pd.show();
        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();

        params.put("email", email);

        String reqURL = DATA.baseUrl + "/forgotPincode";

        DATA.print("-- Request : " + reqURL);
        DATA.print("-- params : " + params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in forgotPincode " + content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        customToast.showToast(msg, 0, 1);

                        forgotPincodeDialog.dismiss();

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

    }//end forgotPincode


    private Handler handler;
    private IcdCodesAdapter icdCodesAdapter;
    ProgressBar pbAutoComplete, pbAutoCompleteSecondary;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
    List<String> diagnosisList;

    Dialog prescriptionsDialog;
    ImageView ivSignature;
    ExpandableHeightListView lvDrugs;
    ScrollView svSendPres;
    //ArrayList<DrugBean> temp;
    String vitals = "";
    String diagnoses = "";
    String primaryDiagnosis = "";
    String secondaryDiagnosis = "";
    String notes = "";
    String rxfillindicator = "";

    String treatments = "";

    String drugs = "", quantity = "", directions = "";
    String potency_code = "", refill = "";

    String start_date = "", end_date = "";

    String diagnosisCodePrimary = "", diagnosisCodeSecondary = "";

    public static TextView tvPrescPharmacy, tvPrescPharmacyPhone, tvPrescPharmacyAddress;

    CheckBox drugEducationCheckbox;
    Button btnPriDiagAiSugg,getBtnSecDiagAiSugg;

    public void initPrescripDialog() {
        DATA.drugBeans = new ArrayList<DrugBean>();
        prescriptionsDialog = new Dialog(activity, R.style.TransparentThemeH4B);//, android.R.style.Theme_DeviceDefault_Light_Dialog
        prescriptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prescriptionsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        prescriptionsDialog.setContentView(R.layout.send_prescription_dialog);
        prescriptionsDialog.setTitle("Send prescriptions to patient");
        prescriptionsDialog.setCanceledOnTouchOutside(false);
        prescriptionsDialog.setCancelable(false);

        prescriptionsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prescriptionsDialog.dismiss();
            }
        });

        TextView tvSendPres = prescriptionsDialog.findViewById(R.id.tvSendPresAndDone);
        TextView tvSendPresCencel = prescriptionsDialog.findViewById(R.id.tvSendPresCencel);
        final EditText etVitals = prescriptionsDialog.findViewById(R.id.etVitals);
        final EditText etDiagnoses = prescriptionsDialog.findViewById(R.id.etDiagnosis);
        //final EditText etTreatment =  (EditText) prescriptionsDialog.findViewById(R.id.etTreatment);
        lvDrugs = prescriptionsDialog.findViewById(R.id.lvDrugs);
        svSendPres = prescriptionsDialog.findViewById(R.id.svSendPres);
        TextView tvPrescPtName = prescriptionsDialog.findViewById(R.id.tvPrescPtName);
        TextView tvPrescDate = prescriptionsDialog.findViewById(R.id.tvPrescDate);

        TextView tvPrescPatientDOB = prescriptionsDialog.findViewById(R.id.tvPrescPatientDOB);
        TextView tvPrescPatientGender = prescriptionsDialog.findViewById(R.id.tvPrescPatientGender);
        TextView tvPrescPatientPhone = prescriptionsDialog.findViewById(R.id.tvPrescPatientPhone);
        TextView tvPrescPatientAdress = prescriptionsDialog.findViewById(R.id.tvPrescPatientAdress);
        tvPrescPharmacy = prescriptionsDialog.findViewById(R.id.tvPrescPharmacy);
        tvPrescPharmacyPhone = prescriptionsDialog.findViewById(R.id.tvPrescPharmacyPhone);
        tvPrescPharmacyAddress = prescriptionsDialog.findViewById(R.id.tvPrescPharmacyAddress);
        TextView tvPrescDrName = prescriptionsDialog.findViewById(R.id.tvPrescDrName);
        TextView tvPrescDrPhone = prescriptionsDialog.findViewById(R.id.tvPrescDrPhone);
        TextView tvPrescDrClinics = prescriptionsDialog.findViewById(R.id.tvPrescDrClinics);
        TextView tvPrescPatientSymptom = prescriptionsDialog.findViewById(R.id.tvPrescPatientSymptom);

        // Saud work starts here...

        LinearLayout parentLayout = prescriptionsDialog.findViewById(R.id.parent_layout);
        Button btnNextPresp = prescriptionsDialog.findViewById(R.id.btnNextPresp);
        Button btnAiPresp = prescriptionsDialog.findViewById(R.id.btnAiSuggestedMedicine);
        LinearLayout backPrespBtn = prescriptionsDialog.findViewById(R.id.back_presp);
        drugEducationCheckbox = prescriptionsDialog.findViewById(R.id.drug_education_checkbox);
        btnPriDiagAiSugg = prescriptionsDialog.findViewById(R.id.btnPriDiagAiSugg);
        getBtnSecDiagAiSugg = prescriptionsDialog.findViewById(R.id.btnSecDiagAiSugg);


        btnNextPresp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int backgroundColor = ContextCompat.getColor(activity, R.color.design_default_color_background);
                svSendPres.setVisibility(View.GONE);
                parentLayout.setBackgroundColor(backgroundColor);
            }
        });




        backPrespBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int backgroundColor = ContextCompat.getColor(activity, R.color.theme_red);
                parentLayout.setBackgroundColor(backgroundColor);
                svSendPres.setVisibility(View.VISIBLE);

            }
        });

        btnAiPresp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //callChatGPTAPIAiPresc("Write prescription/medication suggestions for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : " + DATA.selectedUserCallDescription + ", Patient's DOB is :"+DATA.selectedUserCallDOB+". Please do not add any information from your side.", false);

                GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                    callChatGPTAPIAiPresc(queryForPrescription,false);
                },()->{

                },"OK","Not Now","Disclaimer");


            }
        });


        final AppCompatAutoCompleteTextView autoCompleteTextViewPrimaryDiagnosis = (AppCompatAutoCompleteTextView) prescriptionsDialog.findViewById(R.id.autoComplete_etPrimaryDiagnosis);
        final AppCompatAutoCompleteTextView autoCompleteTextViewSecondaryDiagnosis = (AppCompatAutoCompleteTextView) prescriptionsDialog.findViewById(R.id.autoComplete_etSecondaryDiagnosis);


        btnPriDiagAiSugg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String patient_icd_codes = sharedPrefsHelper.get("patient_ID_ICD", "");
                if(patient_icd_codes.equalsIgnoreCase(DATA.selectedUserCallId)){
                    ArrayList<IcdCodeBean> icdCodeBeans = sharedPrefsHelper.getIcdCodes();

                    String []icdCodeItems = new String[icdCodeBeans.size()];
                    for(int v=0; v<icdCodeBeans.size(); v++){
                        icdCodeItems[v]= icdCodeBeans.get(v).code+" - "+icdCodeBeans.get(v).desc;
                    }

                    ListDialog.showListDialogGenericList(activity,icdCodeItems,R.string.icd_code_title,position -> {
                        IcdCodeBean selectedCode = icdCodeBeans.get(position);

                        autoCompleteTextViewPrimaryDiagnosis.setText(selectedCode.code);


                    });



                }else {
                    customToast.showToast("ICD Codes not found",0,0);
                }

                DATA.print("-- patient_icd_codes : "+ patient_icd_codes + " | DATA.selectedUserCallId : "+DATA.selectedUserCallId + " -- count : "+sharedPrefsHelper.getIcdCodes().size());
            }
        });


        getBtnSecDiagAiSugg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String patient_icd_codes = sharedPrefsHelper.get("patient_ID_ICD", "");
                if(patient_icd_codes.equalsIgnoreCase(DATA.selectedUserCallId)){
                    ArrayList<IcdCodeBean> icdCodeBeans = sharedPrefsHelper.getIcdCodes();

                    String []icdCodeItems = new String[icdCodeBeans.size()];
                    for(int v=0; v<icdCodeBeans.size(); v++){
                        icdCodeItems[v]= icdCodeBeans.get(v).code+" - "+icdCodeBeans.get(v).desc;
                    }

                    ListDialog.showListDialogGenericList(activity,icdCodeItems,R.string.icd_code_title,position -> {
                        IcdCodeBean selectedCode = icdCodeBeans.get(position);

                        autoCompleteTextViewSecondaryDiagnosis.setText(selectedCode.code);


                    });



                }else {
                    customToast.showToast("ICD Codes not found",0,0);
                }



            }
        });

        //===============AutoComplete========================
        pbAutoComplete = (ProgressBar) prescriptionsDialog.findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        pbAutoCompleteSecondary = (ProgressBar) prescriptionsDialog.findViewById(R.id.pbAutoCompleteSecondary);
        pbAutoCompleteSecondary.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);


        //Setting up the adapter for AutoSuggest
        icdCodesAdapter = new IcdCodesAdapter(activity, android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewPrimaryDiagnosis.setThreshold(2);
        autoCompleteTextViewPrimaryDiagnosis.setAdapter(icdCodesAdapter);
        autoCompleteTextViewPrimaryDiagnosis.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        String diagnosis = icdCodesAdapter.getObject(position).desc + " (" + icdCodesAdapter.getObject(position).code + ")";
                        diagnosisCodePrimary = icdCodesAdapter.getObject(position).code;

                        if (diagnosisList == null) {
                            diagnosisList = new ArrayList<>();
                        }
                        diagnosisList.add(diagnosis);

                        autoCompleteTextViewPrimaryDiagnosis.setText(diagnosisCodePrimary);
                        pbAutoComplete.setVisibility(View.GONE);
                        hideShowKeypad.hideSoftKeyboard();


                    }
                });

        autoCompleteTextViewPrimaryDiagnosis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
//                pbAutoComplete.setVisibility(View.VISIBLE);
//                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
                pbAutoComplete.setVisibility(View.VISIBLE);
                pbAutoCompleteSecondary.setVisibility(View.GONE);
                handerforICDCODES(autoCompleteTextViewPrimaryDiagnosis.getText().toString(), TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextViewSecondaryDiagnosis.setThreshold(2);
        autoCompleteTextViewSecondaryDiagnosis.setAdapter(icdCodesAdapter);
        autoCompleteTextViewSecondaryDiagnosis.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        String diagnosis = icdCodesAdapter.getObject(position).desc + " (" + icdCodesAdapter.getObject(position).code + ")";
                        diagnosisCodeSecondary = icdCodesAdapter.getObject(position).code;

                        if (diagnosisList == null) {
                            diagnosisList = new ArrayList<>();
                        }
                        diagnosisList.add(diagnosis);
                        autoCompleteTextViewSecondaryDiagnosis.setText(diagnosisCodeSecondary);
                        pbAutoCompleteSecondary.setVisibility(View.GONE);
                        hideShowKeypad.hideSoftKeyboard();


                    }
                });

        autoCompleteTextViewSecondaryDiagnosis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
//                pbAutoCompleteSecondary.setVisibility(View.VISIBLE);
//                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
                pbAutoComplete.setVisibility(View.GONE);
                pbAutoCompleteSecondary.setVisibility(View.VISIBLE);
                handerforICDCODES(autoCompleteTextViewSecondaryDiagnosis.getText().toString(), TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //new Diagnosis ILC_CODES field Ends


        if (DATA.isFromAppointment) {
            tvPrescPatientDOB.setText(DATA.drAppointmentModel.getBirthdate());
            if (DATA.drAppointmentModel.getGender().equals("1")) {
                tvPrescPatientGender.setText("Male");
            } else {
                tvPrescPatientGender.setText("Female");
            }
            tvPrescPatientPhone.setText(DATA.drAppointmentModel.getPhone());
            tvPrescPatientAdress.setText(DATA.drAppointmentModel.getResidency());
            tvPrescPharmacy.setText(DATA.drAppointmentModel.getStoreName());
            tvPrescPharmacyPhone.setText(Html.fromHtml("<font color='#2979ff'><u>" + DATA.drAppointmentModel.getPhonePrimary() + "</u></font>"));
            tvPrescPharmacyAddress.setText(DATA.drAppointmentModel.pharmacy_address);
            tvPrescPatientSymptom.setText(DATA.drAppointmentModel.getCondition_name());
            etDiagnoses.setText(DATA.drAppointmentModel.getCondition_name());
        } else {
            tvPrescPatientSymptom.setText(DATA.selectedUserCallCondition);
            etDiagnoses.setText(DATA.selectedUserCallCondition);
            if (DATA.selectedLiveCare != null) {
                tvPrescPatientDOB.setText(DATA.selectedLiveCare.birthdate);
                if (DATA.selectedLiveCare.gender.equals("1")) {
                    tvPrescPatientGender.setText("Male");
                } else {
                    tvPrescPatientGender.setText("Female");
                }
                tvPrescPatientPhone.setText(DATA.selectedLiveCare.patient_phone);
                tvPrescPatientAdress.setText(DATA.selectedLiveCare.residency);
                tvPrescPharmacy.setText(DATA.selectedLiveCare.StoreName);
                tvPrescPharmacyPhone.setText(Html.fromHtml("<font color='#2979ff'><u>" + DATA.selectedLiveCare.PhonePrimary + "</u></font>"));
                tvPrescPharmacyAddress.setText(DATA.selectedLiveCare.pharmacy_address);
            }
        }

        tvPrescPharmacyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Uri.encode(tvPrescPharmacyPhone.getText().toString())));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(callIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        tvPrescDrName.setText(prefs.getString("first_name", "") + " " + prefs.getString("last_name", ""));
        tvPrescDrPhone.setText(prefs.getString("mobile", ""));
        tvPrescDrClinics.setText(prefs.getString("dr_clinics", ""));

        ivSignature = prescriptionsDialog.findViewById(R.id.ivSignature);
        ivSignature.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initSignatureDialog(tvSendPres);
            }
        });


        tvPrescPtName.setText(DATA.selectedUserCallName);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  h:mm a");
        String currentDateandTime = sdf.format(new Date());
        tvPrescDate.setText(currentDateandTime);


        Button btnChangePharmacyPresc = prescriptionsDialog.findViewById(R.id.btnChangePharmacyPresc);
        btnChangePharmacyPresc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        tvSendPresCencel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (activity instanceof AfterCallDialogEmcura) {
                    ((AfterCallDialogEmcura) activity).proceedAfterSendPresc();
                } else {
                    if (prescriptionsDialog != null) {
                        prescriptionsDialog.dismiss();
                    }
                }

            }
        });

        //Vitals from prescriptions removed by Jamal in Nov 2018
        etVitals.setText("-");
        RelativeLayout etVitalsCont = prescriptionsDialog.findViewById(R.id.etVitalsCont);
        etVitalsCont.setVisibility(View.GONE);
        tvSendPres.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (DATA.drugBeans == null) {
                    DATA.print("-- in DATA.drugBeans = null condition");
                    AlertDialog alertDialog = new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.app_name))
                            .setMessage("Please add medications.")
                            .setPositiveButton("Ok, Done", null).create();
                    alertDialog.show();
                    return;
                } else if (DATA.drugBeans.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(activity.getResources().getString(R.string.app_name))
                            .setMessage("Please add medications.")
                            .setPositiveButton("Ok, Done", null).create();
                    alertDialog.show();
                    return;
                }

                vitals = etVitals.getText().toString();
                diagnoses = etDiagnoses.getText().toString();
                primaryDiagnosis = diagnosisCodePrimary;
                secondaryDiagnosis = diagnosisCodeSecondary;
                ActivitySoapNotes.vitals = vitals;
                ActivitySoapNotes.diagnosis = diagnoses;
                //String treatments = etTreatment.getText().toString();
				/*String treatments ="";

				String drugs = "",quantity = "",directions = "";
				String potency_code = "",refill = "";

				String start_date = "",end_date = "";*/
                treatments = "";
                drugs = "";
                quantity = "";
                directions = "";
                notes = "";
                rxfillindicator = "";
                potency_code = "";
                refill = "";
                start_date = "";
                end_date = "";

                if (DATA.drugBeans != null) {
                    for (int i = 0; i < DATA.drugBeans.size(); i++) {
                        treatments = treatments + (i + 1) + ": " + DATA.drugBeans.get(i).getDrug_name() + "\n";

                        drugs = drugs + DATA.drugBeans.get(i).getDrug_descriptor_id() + ",";
                        quantity = quantity + DATA.drugBeans.get(i).totalQuantity + ",";
                        directions = directions + DATA.drugBeans.get(i).instructions + "^|";
                        notes = notes + DATA.drugBeans.get(i).instructionNote + "^|";
                        rxfillindicator = rxfillindicator + DATA.drugBeans.get(i).rxfillIndicator + ",";

                        potency_code = potency_code + DATA.drugBeans.get(i).getPotency_code() + ",";
                        refill = refill + DATA.drugBeans.get(i).refill + ",";

                        start_date = start_date + DATA.drugBeans.get(i).start_date + ",";
                        end_date = end_date + DATA.drugBeans.get(i).end_date + ",";
                    }
                } else {
                    treatments = "";

                    drugs = "";
                    quantity = "";
                    directions = "";

                    notes = "";
                    rxfillindicator = "";

                    potency_code = "";
                    refill = "";

                    start_date = "";
                    end_date = "";

                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(activity.getResources().getString(R.string.app_name))
                            .setMessage("Please add medication(s)")
                            .setPositiveButton("Done", null)
                            .create().show();
                }
                if (vitals.isEmpty() || diagnoses.isEmpty() || treatments.isEmpty() || drugs.isEmpty() || quantity.isEmpty() || directions.isEmpty() ||
                        potency_code.isEmpty() || refill.isEmpty()) {
                    Toast.makeText(activity, "All fields are required", Toast.LENGTH_SHORT).show();
                    if (vitals.isEmpty()) {
                        etVitals.setError("Vitals can't be empty");
                    }
                    if (diagnoses.isEmpty()) {
                        etDiagnoses.setError("Diagnoses can't be empty");
                    }
                    svSendPres.scrollTo(0, 0);
                } else if (signaturePath.isEmpty()) {
                    Toast.makeText(activity, "Please add your signature to prescriptions", Toast.LENGTH_SHORT).show();
                    initSignatureDialog(tvSendPres);
                } else {

                    drugs = drugs.substring(0, (drugs.length() - 1));
                    quantity = quantity.substring(0, (quantity.length() - 1));
                    directions = directions.substring(0, (directions.length() - 2));
                    notes = notes.substring(0, (notes.length() - 2));
                    rxfillindicator = rxfillindicator.substring(0, (rxfillindicator.length() - 1));

                    potency_code = potency_code.substring(0, (potency_code.length() - 1));
                    refill = refill.substring(0, (refill.length() - 1));

                    start_date = start_date.substring(0, (start_date.length() - 1));
                    end_date = end_date.substring(0, (end_date.length() - 1));


                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm").
                            setMessage("Are you sure? You are going to send prescriptions request to the pharmacy. Patient will recieve prescriptions from the pharmacy.").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    if (checkInternetConnection.isConnectedToInternet()) {
                                        sendPresscription(prefs.getString("id", ""), DATA.selectedUserCallId, vitals, diagnoses, primaryDiagnosis, secondaryDiagnosis, treatments, signaturePath,
                                                DATA.selectedUserAppntID, drugs, quantity, directions, notes, rxfillindicator, potency_code, refill, start_date, end_date);
                                    } else {
                                        Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
                }


            }
        });

        ImageView ic_mike_vitals = prescriptionsDialog.findViewById(R.id.ic_mike_vitals);
        if (activity instanceof AfterCallDialogEmcura) {
            ic_mike_vitals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    ((AfterCallDialogEmcura) activity).startVoiceRecognitionActivity(etVitals);
                }
            });
        } else {
            ic_mike_vitals.setVisibility(View.GONE);
        }

        ImageView ic_mike_Diagnosis = prescriptionsDialog.findViewById(R.id.ic_mike_Diagnosis);
        if (activity instanceof AfterCallDialogEmcura) {
            ic_mike_Diagnosis.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    ((AfterCallDialogEmcura) activity).startVoiceRecognitionActivity(etDiagnoses);
                }
            });
        } else {
            ic_mike_Diagnosis.setVisibility(View.GONE);
        }


        Button btnAddDrugs = prescriptionsDialog.findViewById(R.id.btnAddDrugs);
        btnAddDrugs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //initDrugsDialog(false, null, 0); GM commented, its moved down to templet presc dialog

                getPrescTemplates();
            }
        });

        Button btnAdditionalMed = prescriptionsDialog.findViewById(R.id.btnAdditionalMed);
        btnAdditionalMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDrugsDialog(false, null, 0);
            }
        });

		/*if (checkInternetConnection.isConnectedToInternet()) {
			getDosageForms();
		} else {
			Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
		}*/
    }

    //Ahmer create this handerler method for double diagnosis search 14-02-2022
    private void handerforICDCODES(String value, int msg, long delay) {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!value.isEmpty()) {
                        RequestParams params = new RequestParams();
                        params.put("keyword", value);
                        ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES, "post", params, apiCallBack, activity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    } else if (value.isEmpty()) {
                        pbAutoComplete.setVisibility(View.GONE);
                        pbAutoCompleteSecondary.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

        handler.removeMessages(TRIGGER_AUTO_COMPLETE);
        handler.sendEmptyMessageDelayed(msg, delay);
    }
    //=================== end


    //======================Add drugs starts

    DrugBean selectedDrugBean;
    Dialog drugsDialog;
    Spinner spinnerDrugName, spinnerRxFillIndec;

    String selectedDrugName ="";
    //String dosage_form = "";

    //Ahmer work for new ui
    TextView tvRoute, tvDosageForm, tvStrenght, tvUnit;
    NumberPicker numPicker_Quantity, numPicker_refill;


    //String dosage = "",freq1 = "",freq2 = "";//dosage_formVal = "",
    public void initDrugsDialog(boolean isForEditDrug, DrugBean drugBeanEdit, int editPosition) {//for edit added drug from DrugsAdapter
        drugsDialog = new Dialog(activity, R.style.TransparentThemeH4B);
        drugsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        drugsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        drugsDialog.setContentView(R.layout.dialog_add_drugs);
        drugsDialog.setCancelable(false);

        drugsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drugsDialog.dismiss();
            }
        });

        TextView tvAddDrugTitle = drugsDialog.findViewById(R.id.tvAddDrugTitle);
        LinearLayout layChangeMedicine = drugsDialog.findViewById(R.id.layChangeMedicine);

        final EditText etSearchQuery = drugsDialog.findViewById(R.id.etSearchQuery);
        ImageView ivSearchQuery = drugsDialog.findViewById(R.id.ivSearchQuery);
        //final Spinner spinnerDrugForm = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugForm);
        spinnerDrugName = drugsDialog.findViewById(R.id.spinnerDrugName);
        final Spinner spinnerRoute = drugsDialog.findViewById(R.id.spinner1);
        final Spinner spinnerDosageForm = drugsDialog.findViewById(R.id.spinner2);
        final Spinner spinnerStrength = drugsDialog.findViewById(R.id.spinner3);
        final Spinner spinnerUnit = drugsDialog.findViewById(R.id.spinner4);

        //Ahmer work for viewFliper
        viewFlipper = drugsDialog.findViewById(R.id.viewFlipperSignup);

        tvStep1 = drugsDialog.findViewById(R.id.tvStep1);
        tvStep2 = drugsDialog.findViewById(R.id.tvStep2);
        btnFlipNext = drugsDialog.findViewById(R.id.btnFlipNext);
        btnFlipPrev = drugsDialog.findViewById(R.id.btnFlipPrev);

        btnFlipNext.setOnClickListener(v -> {
            DATA.print("--viewFlipper " + viewFlipper.getDisplayedChild());


            if (isvalid()) {
                viewFlipper.showNext();
                int selectedChild = viewFlipper.getDisplayedChild();
                setupViewFiliperAndTabs(selectedChild);
                DATA.print("-- selectedChild btn next : " + selectedChild);

                if (selectedChild == 1) {
                    btnFlipNext.setEnabled(false);
                } else {
                    btnFlipNext.setEnabled(true);
                }
                if (selectedChild == 0) {
                    btnFlipPrev.setEnabled(false);
                } else {
                    btnFlipPrev.setEnabled(true);
                }
                setInd();
            }
        });

        btnFlipPrev.setOnClickListener(v -> {
            DATA.print("--viewFlipper " + viewFlipper.getDisplayedChild());
            viewFlipper.showPrevious();

            int selectedChild = viewFlipper.getDisplayedChild();

            setupViewFiliperAndTabs(selectedChild);

            DATA.print("-- selectedChild btn previos : " + selectedChild);

            if (selectedChild == 1) {
                btnFlipNext.setEnabled(false);
            } else {
                btnFlipNext.setEnabled(true);
            }

            if (selectedChild == 0) {
                btnFlipPrev.setEnabled(false);
            } else {
                btnFlipPrev.setEnabled(true);
            }

            setInd();
        });
        //end

        //Ahmer work
        tvRoute = drugsDialog.findViewById(R.id.tvRoute);
        tvDosageForm = drugsDialog.findViewById(R.id.tvDosageForm);
        tvStrenght = drugsDialog.findViewById(R.id.tvStrenght);
        tvUnit = drugsDialog.findViewById(R.id.tvUnit);

        numPicker_Quantity = drugsDialog.findViewById(R.id.numPicker_Quantity);
        numPicker_refill = drugsDialog.findViewById(R.id.numPicker_refill);


        spinnerRxFillIndec = drugsDialog.findViewById(R.id.spinnerRxFillIndec);

        ArrayAdapter<String> spRxFillIndectorAdapter = new ArrayAdapter<String>(
                activity,
                R.layout.spinner_item_lay,
                rxFillIndecArr
        );

        spRxFillIndectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRxFillIndec.setAdapter(spRxFillIndectorAdapter);



		 /*Spinner spinnerDosage = (Spinner) drugsDialog.findViewById(R.id.spinner5);
		 Spinner spinnerFrequency1 = (Spinner) drugsDialog.findViewById(R.id.spinner6);
		 Spinner spinnerFrequency2 = (Spinner) drugsDialog.findViewById(R.id.spinner7);*/
        final Spinner spinnerRefill = drugsDialog.findViewById(R.id.spinner8);

        final Spinner spinner_potency_unit = drugsDialog.findViewById(R.id.spinner_potency_unit);
        final EditText etTotalQuantity = drugsDialog.findViewById(R.id.etTotalQuantity);

        Button btnAddDrugs = drugsDialog.findViewById(R.id.btnAddDrugs);
        Button btnAddDrugsCancel = drugsDialog.findViewById(R.id.btnAddDrugsCancel);
        final EditText etStartDate = drugsDialog.findViewById(R.id.etStartDate);
        final EditText etEndtDate = drugsDialog.findViewById(R.id.etEndtDate);
        final EditText  etInstructionsNote = drugsDialog.findViewById(R.id.etInstructionsNote);
        Button btnAiSuggestedInstruction = drugsDialog.findViewById(R.id.btnAiSuggustedInstructions);
         etInstructions = drugsDialog.findViewById(R.id.etInstructions);
        ImageView ic_mike_Instructions = drugsDialog.findViewById(R.id.ic_mike_Instructions);
        if (activity instanceof AfterCallDialogEmcura) {
            ic_mike_Instructions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    ((AfterCallDialogEmcura) activity).startVoiceRecognitionActivity(etInstructions);
                }
            });
        } else {
            ic_mike_Instructions.setVisibility(View.GONE);
        }

        etStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etStartDate);
                newFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });
        etEndtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etEndtDate);
                newFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });
        btnAiSuggestedInstruction.setOnClickListener(view -> {


            //String searchQuery = "Please suggest the usage instructions for the medication: +"+selectedDrugName+". Please note that your response should be maximum 140 characters.";
            //callChatGPTAPIAiInstructions(searchQuery);

            if(!gptQueryList.isEmpty()){
                queryForInstructions = gptQueryList.get(4);
                queryForInstructions =  queryForInstructions .replace("[medicineName]", selectedDrugName)
                        .replace("[patientDesc]", DATA.selectedUserCallDescription)
                        .replace("\\/", " or ")
                        .replace("]","");
                DATA.print("disch"+queryForInstructions);
            }
            else {
                customToast.showToast("Queries not found.",0,0);
            }

            GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                callChatGPTAPIAiInstructions(queryForInstructions);
            },()->{

            },"OK","Not Now","Disclaimer");


        });

		 /*if (dosageFormsBeans != null) {
			 ArrayAdapter<DosageFormsBean> spDosageFormAdapter = new ArrayAdapter<DosageFormsBean>(
				        activity,
				        R.layout.view_spinner_item,
				        dosageFormsBeans
				);
			 spDosageFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 spinnerDrugForm.setAdapter(spDosageFormAdapter);
		} else {
			if (checkInternetConnection.isConnectedToInternet()) {
				getDosageForms();
			} else {
				Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
			}
		}*/


		 /*spinnerDrugForm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dosage_form = dosageFormsBeans.get(arg2).getField_value();
				dosage_formVal = dosageFormsBeans.get(arg2).getDosage_form();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

        //final String[] dosage={"1","2","3","4","5","6","7","8","9","10"};//,"11","12","13","14","15","16","17","18","19","20"

		 /*ArrayAdapter<String> spDosageAdapter = new ArrayAdapter<String>(
			        this,
			         R.layout.spinner_item_lay,
			        dosage
			);
		 spDosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDosage.setAdapter(spDosageAdapter);


			//final String[] f1={"once","twice","thrice"};
			 ArrayAdapter<String> spFreq1Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f1
				);
			 spFreq1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency1.setAdapter(spFreq1Adapter);

			//final String[] f2={"day","week","month"};
			 ArrayAdapter<String> spFreq2Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f2
				);
			 spFreq2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency2.setAdapter(spFreq2Adapter);


		    spinnerDosage.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					AfterCallDialog.this.dosage = dosage[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq1 = f1[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency2.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq2 = f2[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});*/

        ivSearchQuery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //hideShowKeypad.hideSoftKeyboard();
                // TODO Auto-generated method stub
                if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length() < 2) {
                    Toast.makeText(activity, "Please enter at least 2 characters of a medication name to search the medication", Toast.LENGTH_LONG).show();
                    etSearchQuery.setError("Please enter at least 2 characters of a medication name to search the medication");
                } else if (!checkInternetConnection.isConnectedToInternet()) {
                    Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                } else {
                    new HideShowKeypad(activity).hidekeyboardOnDialog();
                    getDrugs(etSearchQuery.getText().toString().trim(), false, null);
                }
            }
        });

        etSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length() < 2) {
                        Toast.makeText(activity, "Please enter atleast 2 characters of a drug name to search medication", Toast.LENGTH_LONG).show();
                        etSearchQuery.setError("Please enter at least 2 characters of a medication name to search the medication");
                    } else if (!checkInternetConnection.isConnectedToInternet()) {
                        Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                    } else {
                        getDrugs(etSearchQuery.getText().toString().trim(), false, null);
                    }
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });


        spinnerDrugName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // TODO Auto-generated method stub
                selectedDrugBean = drugBeans.get(pos);

                ArrayList<String> route = new ArrayList<String>();
                route.add(selectedDrugBean.getRoute());

                ArrayList<String> dosage_form = new ArrayList<String>();
                dosage_form.add(selectedDrugBean.getDosage_form());

                ArrayList<String> strength = new ArrayList<String>();
                strength.add(selectedDrugBean.getStrength());

                ArrayList<String> strength_unit_of_measure = new ArrayList<String>();
                strength_unit_of_measure.add(selectedDrugBean.getStrength_unit_of_measure());

                selectedDrugName = selectedDrugBean.getDrug_name();

                //------------------------potency_unit------------------------------------------------

                final ArrayList<PotencyUnitBean> potencyUnitBeans = new ArrayList<>();
                String[] pu = selectedDrugBean.getPotency_unit().split(",");
                String[] pc = selectedDrugBean.getPotency_code().split(",");

                for (int i = 0; i < pu.length; i++) {
                    potencyUnitBeans.add(new PotencyUnitBean(pu[i], pc[i]));
                }

                //ahmer comment this code for new ui
                spinner_potency_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                        // TODO Auto-generated method stub
                        selectedDrugBean.setPotency_code(potencyUnitBeans.get(pos).getPotency_code());
                        selectedDrugBean.setPotency_unit(potencyUnitBeans.get(pos).getPotency_unit());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

                ArrayAdapter<PotencyUnitBean> spPtencyUnitAdapter = new ArrayAdapter<PotencyUnitBean>(
                        activity,
                        R.layout.spinner_item_lay,
                        potencyUnitBeans
                );

                spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_potency_unit.setAdapter(spPtencyUnitAdapter);

                //ahmer comment this code for new ui

                //--------------set Refill value for selectedDrugBean--------------------------
                final String[] refillArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"
                        , "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
                        "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                        "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
                        "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88"
                        , "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"};

                spinnerRefill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        selectedDrugBean.refill = refillArray[arg2];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });


                ArrayAdapter<String> spRefillAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        refillArray
                );
                spRefillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRefill.setAdapter(spRefillAdapter);

                //---------------------set Refill value for selectedDrugBean----------------------

                //------------------------potency_unit----------------------------------------------------
                ArrayAdapter<String> sprouteAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        route
                );

//              sprouteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//              spinnerRoute.setAdapter(sprouteAdapter);

                for (int i = 0; i < sprouteAdapter.getCount(); i++) {
                    tvRoute.setText(sprouteAdapter.getItem(i));
                }

                ArrayAdapter<String> spdosage_formAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        dosage_form
                );

                spdosage_formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDosageForm.setAdapter(spdosage_formAdapter);

                for (int i = 0; i < spdosage_formAdapter.getCount(); i++) {
                    tvDosageForm.setText(spdosage_formAdapter.getItem(i));
                }

                ArrayAdapter<String> spstrengthAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        strength
                );

//              spstrengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//              spinnerStrength.setAdapter(spstrengthAdapter);

                for (int i = 0; i < spstrengthAdapter.getCount(); i++) {
                    tvStrenght.setText(spstrengthAdapter.getItem(i));
                }

                //Ahmer work 08-02-2022

                //issue in edit drug set selection code moved to abve in method

                /*rxFillIndecArr = new String[]{"None", "All Fill Statuses", "All Fill Statuses Except Transferred"
                        , "Cancel All Fill Statuses", "Not Dispensed", "Not Dispensed And Transferred",
                        "Partially Dispensed", "Partially Dispensed And Not Dispensed"};

                ArrayAdapter<String> spRxFillIndectorAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        rxFillIndecArr
                );
                spRxFillIndectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRxFillIndec.setAdapter(spRxFillIndectorAdapter);*/
                //end

                ArrayAdapter<String> spstrength_unit_of_measureAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        strength_unit_of_measure
                );

//              spstrength_unit_of_measureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//              spinnerUnit.setAdapter(spstrength_unit_of_measureAdapter);

                for (int i = 0; i < spstrength_unit_of_measureAdapter.getCount(); i++) {
                    tvUnit.setText(spstrength_unit_of_measureAdapter.getItem(i));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        etTotalQuantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.toString().startsWith(".")) {
                    etTotalQuantity.setText("0" + arg0.toString());
                    etTotalQuantity.setSelection(etTotalQuantity.getText().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        etTotalQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    if (!etTotalQuantity.getText().toString().isEmpty()) {
                        etTotalQuantity.setText(validateQuantity(etTotalQuantity.getText().toString()));
                        Toast.makeText(activity, "Leading and trailing zeeros will be truncated from quantity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnAddDrugsCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                drugsDialog.dismiss();
            }
        });
        btnAddDrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (selectedDrugBean != null) {
                    if (etStartDate.getText().toString().equalsIgnoreCase("Start Date") ||
                            etEndtDate.getText().toString().equalsIgnoreCase("End Date")) {
                        Toast.makeText(activity, "Please add medicine start date and end date", Toast.LENGTH_LONG).show();
                    } else if (numPicker_Quantity.getValue() == 0) {
                        Toast.makeText(activity, "Please enter total quantity for the medicine", Toast.LENGTH_SHORT).show();
                    } else if (etInstructions.getText().toString().trim().isEmpty()) {
                        Toast.makeText(activity, "Please enter usage instructions for the medicine", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name());
						/*+"\n"+AfterCallDialog.this.dosage+" "+selectedDrugBean.getDfdesc()+" "+freq1+" a "+freq2
						+"\nFrom date: "+etStartDate.getText().toString()+" To date: "+etEndtDate.getText().toString()*/

                        //this is total quantity for the medicine i.e sent in savePrescription
                        //selectedDrugBean.setDosage_form(validateQuantity(etTotalQuantity.getText().toString()));//AfterCallDialog.this.dosage
                        //selectedDrugBean.totalQuantity = validateQuantity(etTotalQuantity.getText().toString());
                        selectedDrugBean.totalQuantity = String.valueOf(numPicker_Quantity.getValue());
                        selectedDrugBean.refill = String.valueOf(numPicker_refill.getValue());

                        selectedDrugBean.start_date = etStartDate.getText().toString();
                        selectedDrugBean.end_date = etEndtDate.getText().toString();

                        if (!etInstructions.getText().toString().trim().isEmpty()) {
							/*selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name()
									+"\n\nUsage Instructions: "+etInstructions.getText().toString().trim()+"\n");*/

                            selectedDrugBean.instructions = etInstructions.getText().toString().trim();
                        }

                        selectedDrugBean.duration = getDateDifference(etStartDate.getText().toString(), etEndtDate.getText().toString());
                        if (spinnerRxFillIndec.getSelectedItem().toString().equals("None")) {
                            selectedDrugBean.rxfillIndicator = "";

                        } else {
                            selectedDrugBean.rxfillIndicator = rxFillIndecArr[spinnerRxFillIndec.getSelectedItemPosition()];//spinnerRxFillIndec.getSelectedItem().toString().trim();
                        }
                        selectedDrugBean.instructionNote = etInstructionsNote.getText().toString().trim();


                        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle(activity.getResources().getString(R.string.app_name))
                                .setMessage("Selected medication has been added/modified successfully.")
                                .setPositiveButton("Done", null).create();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if (isForEditDrug) {
                                    try {
                                        DATA.drugBeans.remove(editPosition);
                                        DATA.drugBeans.add(editPosition, selectedDrugBean);
                                        DrugsAdapter adapter = new DrugsAdapter(activity, PrescriptionModule.this);
                                        lvDrugs.setAdapter(adapter);
                                        lvDrugs.setExpanded(true);// This actually does the magic
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    DATA.drugBeans.add(selectedDrugBean);
                                    DrugsAdapter adapter = new DrugsAdapter(activity, PrescriptionModule.this);
                                    lvDrugs.setAdapter(adapter);
                                    lvDrugs.setExpanded(true);// This actually does the magic
                                    //lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                }
                                drugsDialog.dismiss();
                            }
                        });
                        alertDialog.show();


                    }
                } else {
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle(activity.getResources().getString(R.string.app_name))
                            .setMessage("Please select a medication to add")
                            .setPositiveButton("Done", null).create().show();
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(drugsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;

        drugsDialog.show();
        drugsDialog.getWindow().setAttributes(lp);


        if (isForEditDrug) {

            drugBeans = new ArrayList<>();
            drugBeans.add(drugBeanEdit);
            ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<>(
                    activity,
                    R.layout.view_spinner_item,
                    drugBeans
            );

            spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDrugName.setAdapter(spDrugNameAdapter);

            etStartDate.setText(drugBeanEdit.start_date);
            etEndtDate.setText(drugBeanEdit.end_date);
            etTotalQuantity.setText(drugBeanEdit.totalQuantity);
            etInstructions.setText(drugBeanEdit.instructions);
            etInstructionsNote.setText(drugBeanEdit.instructionNote);

            numPicker_Quantity.setValue(Integer.parseInt(drugBeanEdit.totalQuantity));
            numPicker_refill.setValue(Integer.parseInt(drugBeanEdit.refill));

            for (int i = 0; i < rxFillIndecArr.length; i++) {
                if (rxFillIndecArr[i].equalsIgnoreCase(drugBeanEdit.rxfillIndicator)) {
                    spinnerRxFillIndec.setSelection(i);
                    break;
                }

            }


            String searchKeyword = "";
            try {
                searchKeyword = drugBeanEdit.getDrug_name().split("\\s+")[0];
            } catch (Exception e) {
                e.printStackTrace();
                searchKeyword = drugBeanEdit.getDrug_name();
            }
            etSearchQuery.setText(searchKeyword);
            getDrugs(searchKeyword, true, drugBeanEdit);

        }


        if (isForEditDrug) {
            tvAddDrugTitle.setText("Edit Medication");
            btnAddDrugs.setText("Save Medication");

            if (drugBeanEdit.isTemplateDrug) {
                layChangeMedicine.setVisibility(View.GONE);
                spinnerDrugName.setVisibility(View.INVISIBLE);
            }
        } else {
            tvAddDrugTitle.setText("Add Medication");
            btnAddDrugs.setText("Add Medication");
        }
    }

    //for checking validation before next step while adding drugs
    private boolean isvalid() {

        return true;
    }


    public void setInd() {
        ImageView cir1 = (ImageView) drugsDialog.findViewById(R.id.cir1);
//        ImageView cir2 = (ImageView) findViewById(R.id.cir2);
        ImageView cir3 = (ImageView) drugsDialog.findViewById(R.id.cir3);
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

    void setupViewFiliperAndTabs(int pos) {
        switch (pos) {
            case 0:
                tvStep1.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                //tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
                //tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvStep2.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                viewFlipper.setDisplayedChild(0);
                break;
            case 1:
                tvStep1.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                //tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
                //tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
                tvStep2.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                //tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
                viewFlipper.setDisplayedChild(1);
                break;
            default:
                break;
        }
    }
    //end


    public String getDateDifference(String startDate, String endDate) {
        int daysDiff = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);
            long diff = eDate.getTime() - sDate.getTime();

            DATA.print("--days: " + (int) (diff / (1000 * 60 * 60 * 24)));
            daysDiff = (int) (diff / (1000 * 60 * 60 * 24));
            daysDiff = daysDiff + 1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return daysDiff + " Days";
    }

    public String validateQuantity(String q) {
        long a = 0;
        float b = 0.0f;

        String result = "";


        String[] arr = q.split("\\.");

        //DATA.print("-- arr.length "+arr.length);
        //DATA.print("-- q = "+q);

        if (arr.length > 1) {

            a = Long.parseLong(arr[0]);
            b = Float.parseFloat("." + arr[1]);

            if (b > 0) {
                result = (a + b) + "";
            } else {
                result = a + "";
            }

        } else if (arr.length == 1) {
            DATA.print("-- Long.MAX_VALUE : " + Long.MAX_VALUE);
            a = Long.parseLong(arr[0]);

            result = a + "";
        }


        DATA.print("--result: " + result);
        return result;
    }


    ArrayList<DrugBean> drugBeans;

    public void getDrugs(String keyword, boolean isForEditDrug, DrugBean drugBeanEdit) {

        pd.setMessage("Please wait...    ");
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();
        params.put("keyword", keyword);
        //params.put("dosage_form", dosage_form);

        String reqURL = DATA.baseUrl + "/getDrugs";

        DATA.print("-- Request : " + reqURL);
        DATA.print("-- params : " + params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in getDrugs " + content);
                    try {
                        drugBeans = new ArrayList<DrugBean>();
                        DrugBean temp;

                        JSONArray jsonArray = new JSONObject(content).getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
                            String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
                            String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
                            String code = jsonArray.getJSONObject(i).getString("code");
                            String route = jsonArray.getJSONObject(i).getString("route");
                            String strength = jsonArray.getJSONObject(i).getString("strength");
                            String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
                            String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
                            String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
                            String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

                            String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
                            String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

                            temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc, potency_unit, potency_code);
                            drugBeans.add(temp);
                            temp = null;
                        }

                        //setData here

                        ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
                                activity,
                                R.layout.view_spinner_item,
                                drugBeans
                        );
                        spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDrugName.setAdapter(spDrugNameAdapter);

                        if (isForEditDrug) {
                            try {
                                for (int i = 0; i < drugBeans.size(); i++) {
                                    if (drugBeans.get(i).getDrug_descriptor_id().equalsIgnoreCase(drugBeanEdit.getDrug_descriptor_id())) {
                                        spinnerDrugName.setSelection(i);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Open the Spinner...
                            spinnerDrugName.performClick();
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

    }//end getDrugs

    //===========================Add drugs ends


    //=================send presription API starts==================
    public void sendPresscription(String doctor_id, String patient_id, String vitals, String diagnosis, String primaryDiagnosis, String secondaryDiagnosis, String treatment,
                                  String signFilePath, String live_checkup_id, String drugs, String quantity, String directions,
                                  String notes, String rxfillindicator, String potency_code, String refill, String start_date,
                                  String end_date) {
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        client.setTimeout(50000);
        RequestParams params = new RequestParams();
        params.put("doctor_id", doctor_id);
        params.put("patient_id", patient_id);
        params.put("vitals", vitals);
        params.put("diagnosis", diagnosis);
        params.put("diagnoses_codes_input_p", primaryDiagnosis);
        params.put("diagnoses_codes_input_s", secondaryDiagnosis);
        params.put("treatment", treatment);
        params.put("send_email", "1");

        //params.put("live_checkup_id", live_checkup_id);   removed by Maaz
        params.put("drugs", drugs);
        params.put("quantity", quantity);
        params.put("directions", directions);
        params.put("notes", notes);
        params.put("rxfillindicator", rxfillindicator);
        params.put("potency_code", potency_code);
        params.put("refill", refill);

        params.put("start_date", start_date);
        params.put("end_date", end_date);

        params.put("send_education_to_patient" , drugEducationCheckbox.isChecked() ? "1" : "0");

        try {
            params.put("signature", new File(signFilePath));
        } catch (FileNotFoundException e1) {
            DATA.print("-- sign file not found !");
            e1.printStackTrace();
        }
        //params.put("send_email", "1"); if dr want to email


        pd.setTitle("Please wait..");
        pd.setMessage("Sending prescriptions");
        pd.show();

//        String reqURL = DATA.baseUrl + "/savePrescriptions";
        String reqURL = DATA.baseUrl + "doctor/savePrescriptions";

        DATA.print("-- Request : " + reqURL);
        DATA.print("-- params : " + params.toString());

        //DATA.print("--params in savePrescriptions: "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try {
                    String content = new String(response);

                    DATA.print("--responce " + content);
                    //responce {"success":1,"message":"Saved"}
                    try {
                        JSONObject obj = new JSONObject(content);
                        String msg = obj.getString("message");

                        try {
                            String soapPrescTxt = "";
                            if (DATA.drugBeans != null) {
                                for (int i = 0; i < DATA.drugBeans.size(); i++) {
                                    soapPrescTxt = soapPrescTxt + (i + 1) + ": " + DATA.drugBeans.get(i).getDrug_name() + "\n"
                                            + "Usage Instruction: " + DATA.drugBeans.get(i).instructions + "\n";//+"\nStatus: Pending"
                                }
                            }
                            if (ActivitySoapNotesNew.etSOAPPrescription != null) {
                                ActivitySoapNotesNew.etSOAPPrescription.setText(soapPrescTxt);
                            }
                            if (ActivitySoapNotesEditNew.etSOAPPrescription != null) {
                                ActivitySoapNotesEditNew.etSOAPPrescription.setText(soapPrescTxt);
                            }
                            if (ActivitySoapNotesEmpty.etSOAPPrescription != null) {
                                ActivitySoapNotesEmpty.etSOAPPrescription.setText(soapPrescTxt);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (obj.has("success")) {

                            prescriptionsDialog.dismiss();

                            //showMessageBox(AfterCallDialog.this, "Success", "Prescriptions sent to patient. Remove patient from live care queue?");
                            //initRemoveDialog();

                            AlertDialog.Builder b = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme);
                            b.setMessage("Prescriptions has been successfully sent.").setPositiveButton("OK", null);
                            AlertDialog d = b.create();
                            d.setCanceledOnTouchOutside(false);

                            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface arg0) {
                                    // TODO Auto-generated method stub
                                    //startActivity(new Intent(activity,ActivityTelemedicineServices.class));
                                    //new GloabalMethods(activity).showAddSOAPDialog();
                                    if (activity instanceof AfterCallDialogEmcura) {
                                        ((AfterCallDialogEmcura) activity).proceedAfterSendPresc();
                                    }
                                }
                            });
                            d.show();
                        } else if (obj.has("error") && obj.has("message")) {
                            //{"error":1,"message":" Doctor not register in surescript.Surescript spi number missing.\n"}
                            AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                    .setMessage(obj.getString("message"))
                                    .setPositiveButton("Done", null).create();
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    AlertDialog callAlert = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                            .setMessage("Please call the patient's selected pharmacy for the prescription.")
                                            .setPositiveButton("Call Now", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                        callIntent.setData(Uri.parse("tel:" + Uri.encode(tvPrescPharmacyPhone.getText().toString())));
                                                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        activity.startActivity(callIntent);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Not Now", null)
                                            .create();
                                    callAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            if (activity instanceof AfterCallDialogEmcura) {
                                                ((AfterCallDialogEmcura) activity).proceedAfterSendPresc();
                                            } else {
                                                if (prescriptionsDialog != null) {
                                                    prescriptionsDialog.dismiss();
                                                }
                                            }
                                        }
                                    });
                                    callAlert.show();
                                }
                            });
                            alertDialog.show();
                        } else {
                            //showMessageBoxError(AfterCallDialog.this, "Error", msg);
                            Toast.makeText(activity, "Something went wrong. Presscription not saved. Please try again.", Toast.LENGTH_LONG).show();
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
    }

    //=================send presription API ends==================


    //============Electronic Signature starts
    private String signaturePath = "";
    Dialog signatureDialog;
    SignaturePad mSignaturePad;
    Button mClearButton;
    Button mSaveButton;

    public void initSignatureDialog(TextView tvSendPrescArg) {
        signatureDialog = new Dialog(activity);
        signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signatureDialog.setContentView(R.layout.dialog_signature);

        mSignaturePad = signatureDialog.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(activity, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = signatureDialog.findViewById(R.id.clear_button);
        mSaveButton = signatureDialog.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                String signPath = addSignatureToGallery(signatureBitmap);
                if (signPath != null) {
                    Toast.makeText(activity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    signaturePath = signPath;
                    signatureDialog.dismiss();
                    //ivSignature.setScaleType(ScaleType.CENTER_CROP);
                    ivSignature.setImageBitmap(BitmapFactory.decodeFile(signPath));

                    tvSendPrescArg.performClick();

                } else {
                    Toast.makeText(activity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signatureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        signatureDialog.show();
    }//end init

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public String addSignatureToGallery(Bitmap signature) {
        //boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("Online Care Dr"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);
            //result = true;
            return photo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //============Electronic Signature ends


    //=====================New Prescription template module========================================

    public void getPrescTemplates() {
        ApiManager apiManager = new ApiManager(ApiManager.GET_PRESC_TEMPLATES, "post", null, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void showAddTemplPrescDialog(ArrayList<PrescripTempletBean> prescripTempletBeans) {
        Dialog dialogAddTemplPresc = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogAddTemplPresc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //dialogAddTemplPresc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddTemplPresc.setContentView(R.layout.dialog_add_templ_presc);

        ImageView ivCancel = (ImageView) dialogAddTemplPresc.findViewById(R.id.ivCancel);
        Button btnAddTemplPresc = (Button) dialogAddTemplPresc.findViewById(R.id.btnAddTemplPresc);
        Button btnAddOtherMed = dialogAddTemplPresc.findViewById(R.id.btnAddOtherMed);


        FloatingGroupExpandableListView lvTemplPresc = dialogAddTemplPresc.findViewById(R.id.lvTemplPresc);
        //lvTemplPresc.setAdapter(new TemplPrescAdapter(activity, prescripTempletBeans));
        TemplPrescAdapter templPrescAdapter = new TemplPrescAdapter(activity, prescripTempletBeans);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(templPrescAdapter);
        lvTemplPresc.setAdapter(wrapperAdapter);

        btnAddTemplPresc.setOnClickListener(v -> {

            boolean isDrugAdded = false;
            for (int i = 0; i < prescripTempletBeans.size(); i++) {
                for (int j = 0; j < prescripTempletBeans.get(i).templateDrugBeans.size(); j++) {
                    PrescripTempletBean.TemplateDrugBean templateDrugBean = prescripTempletBeans.get(i).templateDrugBeans.get(j);
                    if (templateDrugBean.isSelected) {
                        DrugBean drugBean = new DrugBean(templateDrugBean.drug_descriptor_id, templateDrugBean.route_of_administration, templateDrugBean.drug_name,
                                templateDrugBean.code, templateDrugBean.route, templateDrugBean.strength, templateDrugBean.strength_unit_of_measure, templateDrugBean.dosage_form,
                                templateDrugBean.dfcode, templateDrugBean.dfdesc, templateDrugBean.potency_unit, templateDrugBean.potency_code);

                        drugBean.totalQuantity = templateDrugBean.qty;
                        drugBean.duration = templateDrugBean.no_of_days;
                        drugBean.refill = templateDrugBean.refill;
                        drugBean.instructions = templateDrugBean.instruction;
                        drugBean.instructionNote = templateDrugBean.instructionNote;
                        if (drugBean.rxfillIndicator == null) {
                            drugBean.rxfillIndicator = "";

                        } else {
                            drugBean.rxfillIndicator = templateDrugBean.rxfillindicator;
                        }
                        setStrtEndDates(drugBean);//to set start_date , end_date

                        drugBean.isTemplateDrug = true;//to diffrentiate it during edit


                        DATA.drugBeans.add(drugBean);

                        isDrugAdded = true;


                    }
                }
            }//

            if (isDrugAdded) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(activity.getResources().getString(R.string.app_name))
                        .setMessage("Selected medication(s) has been added successfully.")
                        .setPositiveButton("Done", null).create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        DrugsAdapter adapter = new DrugsAdapter(activity, PrescriptionModule.this);
                        lvDrugs.setAdapter(adapter);
                        lvDrugs.setExpanded(true);// This actually does the magic
                        //lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        dialogAddTemplPresc.dismiss();
                    }
                });
                alertDialog.show();
            } else {
                customToast.showToast("Please select at least one medication to add", 0, 0);
            }
        });

        btnAddOtherMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddTemplPresc.dismiss();
                initDrugsDialog(false, null, 0);
            }
        });

        ivCancel.setOnClickListener(v -> dialogAddTemplPresc.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddTemplPresc.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddTemplPresc.setCanceledOnTouchOutside(false);
        dialogAddTemplPresc.show();
        dialogAddTemplPresc.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogAddTemplPresc;
    }


    public void showAISuggestedPrescription(String aiText) {
        Dialog dialogAiSuggestedPresp = new Dialog(activity);
        dialogAiSuggestedPresp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAiSuggestedPresp.setContentView(R.layout.ai_suggested_medicines_dialog);


        actionEditText = (ActionEditText) dialogAiSuggestedPresp.findViewById(R.id.etAiAnswerPrescription);
        Button buttonSearch = (Button) dialogAiSuggestedPresp.findViewById(R.id.btnSearchMed);
        Button buttonNotNow = (Button) dialogAiSuggestedPresp.findViewById(R.id.btnNotNow);
        ImageView buttonCancel = (ImageView) dialogAiSuggestedPresp.findViewById(R.id.ivCancel);
        TextView buttonRefresh = (TextView) dialogAiSuggestedPresp.findViewById(R.id.refresh_btn);
        actionEditText.setText("");

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //callChatGPTAPIAiPresc("Write prescription/medication suggestions for a patient " + ActivityTcmDetails.ptFname + ActivityTcmDetails.ptLname + " having symptoms : " + DATA.selectedUserCallCondition + " Patient further told that : " + DATA.selectedUserCallDescription + ", Patient's DOB is "+DATA.selectedUserCallDOB+". Please do not add any information from your side.", true);
                GeneralAlertDialog.callAlert(activity,disclaimer,()->{
                    callChatGPTAPIAiPresc(queryForPrescription,true);
                },()->{

                },"OK","Not Now","Disclaimer");

            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAiSuggestedPresp.dismiss();
            }
        });

        buttonNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAiSuggestedPresp.dismiss();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDrugsDialog(false, null, 0);
                dialogAiSuggestedPresp.dismiss();
            }
        });


        StringBuilder sb = new StringBuilder();
        sb.append("Patient Name : ").append(DATA.selectedUserCallName).append(" ")
                .append("\nDOB : ").append(DATA.selectedUserCallDOB)
                .append("\n").append(aiText);

        actionEditText.setText(sb.toString());


        dialogAiSuggestedPresp.show();

        dialogAiSuggestedPresp.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAiSuggestedPresp.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAiSuggestedPresp.setCanceledOnTouchOutside(false);
        dialogAiSuggestedPresp.show();
        dialogAiSuggestedPresp.getWindow().setAttributes(lp);*/

        //dialogForDismiss = dialogAddTemplPresc;
    }


    public void setStrtEndDates(DrugBean drugBean) {

        int noOfDays = 1;
        try {
            noOfDays = Integer.parseInt(drugBean.duration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Date sDate = new Date();
//        String sDateStr = new SimpleDateFormat("yyyy-MM-dd").format(sDate);
//        btnHistoryTo.setTag(sDateStr);
//        btnHistoryTo.setText(new SimpleDateFormat("dd/MM/yyyy").format(sDate));
        String sDateStr = new SimpleDateFormat("MM/dd/yyyy").format(sDate);
        //btnHistoryTo.setTag(sDateStr);
        //btnHistoryTo.setText(sDateStr);

        drugBean.start_date = sDateStr;

        //int noOfDays = -7; //i.e one weeks
        //int noOfDays = -30; //i.e one month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);

        int noOfDays2 = noOfDays;

        if (noOfDays2 > 1) {
            noOfDays2 = noOfDays2 - 1;
        }

        calendar.add(Calendar.DAY_OF_YEAR, noOfDays2);
        Date afterWeekDate = calendar.getTime();
//        String toDateStr =  new SimpleDateFormat("yyyy-MM-dd").format(afterWeekDate);
//        btnHistoryFrom.setTag(toDateStr);
//        btnHistoryFrom.setText(new SimpleDateFormat("dd/MM/yyyy").format(afterWeekDate));
        String toDateStr = new SimpleDateFormat("MM/dd/yyyy").format(afterWeekDate);
        //btnHistoryFrom.setTag(toDateStr);
        //btnHistoryFrom.setText(toDateStr);


        drugBean.end_date = toDateStr;

        drugBean.duration = drugBean.duration + " Days";

    }


    ArrayList<PrescripTempletBean> prescripTempletBeans;

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_PRESC_TEMPLATES)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                Type listType = new TypeToken<ArrayList<PrescripTempletBean>>() {
                }.getType();
                prescripTempletBeans = new Gson().fromJson(data.toString(), listType);
                if (prescripTempletBeans != null) {
                    showAddTemplPrescDialog(prescripTempletBeans);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GET_ICD_CODES)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<IcdCodeBean>>() {
                }.getType();
                List<IcdCodeBean> icdCodeBeans = new Gson().fromJson(data.toString(), listType);

                pbAutoComplete.setVisibility(View.GONE);
                pbAutoCompleteSecondary.setVisibility(View.GONE);
                if (icdCodeBeans != null) {
                    //IMPORTANT: set data here and notify
                    icdCodesAdapter.setData(icdCodeBeans);
                    icdCodesAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        }
    }

}
