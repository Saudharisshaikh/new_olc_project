package com.app.priorityone_dr.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.priorityone_dr.ActivityTcmDetails;
import com.app.priorityone_dr.DocToDoc;
import com.app.priorityone_dr.R;
import com.app.priorityone_dr.adapter.NotesAdapter2;
import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.model.CallLogBean;
import com.app.priorityone_dr.model.DoctorsModel;
import com.app.priorityone_dr.model.NotesBean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class DialogCallDetails implements ApiCallBack, DialogAddEncNoteFromCallHist.RefreshEncounterNotesInterface {


    Activity activity;

    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    OpenActivity openActivity;
    ApiCallBack apiCallBack;

    public DialogCallDetails(Activity activity) {
        this.activity = activity;

        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;
    }

    Dialog dialogCallDetails;
    TextView tvNoNotes;
    ListView lvNotes;
    CallLogBean callLogBeanGloabal;
    public void initDetailsDialog(CallLogBean callLogBean) {

        callLogBeanGloabal = callLogBean;

        dialogCallDetails = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogCallDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCallDetails.setContentView(R.layout.dialog_call_drtails);

        ImageView ivCallLog = (ImageView) dialogCallDetails.findViewById(R.id.ivCallLog);
        TextView tvCallerName = (TextView) dialogCallDetails.findViewById(R.id.tvCallerName);
        TextView tvCallAddress = (TextView) dialogCallDetails.findViewById(R.id.tvCallAddress);
        TextView tvCallPhone = (TextView) dialogCallDetails.findViewById(R.id.tvCallPhone);
        TextView tvCallTime = (TextView) dialogCallDetails.findViewById(R.id.tvCallTime);
        TextView tvCallEndTime = (TextView) dialogCallDetails.findViewById(R.id.tvCallEndTime);
        TextView tvCallDuration = (TextView) dialogCallDetails.findViewById(R.id.tvCallDuration);
        tvNoNotes = (TextView) dialogCallDetails.findViewById(R.id.tvNoNotes);

        DATA.loadImageFromURL(callLogBean.getImage(),R.drawable.icon_call_screen, ivCallLog);
        tvCallerName.setText(callLogBean.getFirst_name()+" "+callLogBean.getLast_name());

        tvCallAddress.setText(callLogBean.residency+", "+callLogBean.city+", "+callLogBean.state);
        tvCallPhone.setText(Html.fromHtml("<font color='#2979ff'><u>"+callLogBean.phone+"</u></font>"));
        tvCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode(callLogBean.phone)));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(callIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        String stTime = "-", eTime = "-", dur = "-";
        try {
            Date callStrtDate = null, callEndDate = null;
            if(!TextUtils.isEmpty(callLogBean.getDateof())){//04/02/2020 12:03
                stTime = callLogBean.getDateof();
                callStrtDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(stTime);
                stTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(callStrtDate);

                //for accurate duration use dateof2 with seconds
                if(!TextUtils.isEmpty(callLogBean.dateof2)){//"2020-03-25 16:40:15"
                    try {
                        callStrtDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(callLogBean.dateof2);
                        stTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(callStrtDate);
                    }catch (Exception e){e.printStackTrace();}
                }
            }

            if(! TextUtils.isEmpty(callLogBean.end_time)){//2020-04-03 12:03:07
                eTime = callLogBean.end_time;
                callEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(eTime);
                eTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(callEndDate);
            }

            if(callStrtDate != null && callEndDate != null){
                long totalDiffMillis = callEndDate.getTime() - callStrtDate.getTime();
                try {
                    dur = String.format(Locale.ENGLISH, "%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(totalDiffMillis),
                            TimeUnit.MILLISECONDS.toSeconds(totalDiffMillis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDiffMillis))
                    );
					/*int seconds = (int) (milliseconds / 1000) % 60 ;
					int minutes = (int) ((milliseconds / (1000*60)) % 60);
					int hours   = (int) ((milliseconds / (1000*60*60)) % 24);*/
                }catch (Exception e){e.printStackTrace();}
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        tvCallTime.setText(stTime);
        tvCallEndTime.setText(eTime);
        if(!TextUtils.isEmpty(callLogBean.duration)){
            tvCallDuration.setText(callLogBean.duration);
        }else {
            tvCallDuration.setText(dur);
        }

        lvNotes = (ListView) dialogCallDetails.findViewById(R.id.lvNotes);
        RelativeLayout notesCont = (RelativeLayout) dialogCallDetails.findViewById(R.id.notesCont);
        LinearLayout layBottom = (LinearLayout) dialogCallDetails.findViewById(R.id.layBottom);
        Button btnRefer = (Button) dialogCallDetails.findViewById(R.id.btnRefer);
        Button btnAddSOAP = (Button) dialogCallDetails.findViewById(R.id.btnAddSOAP);
        Button btnrecall = (Button) dialogCallDetails.findViewById(R.id.btnRecall);
        Button btnSendMsg = (Button) dialogCallDetails.findViewById(R.id.btnSendMsg);

        LinearLayout layB2 = dialogCallDetails.findViewById(R.id.layB2);
        Button btnSendPres = dialogCallDetails.findViewById(R.id.btnSendPres);
        Button btnSendEncNotes = dialogCallDetails.findViewById(R.id.btnSendEncNotes);

        btnrecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(callLogBean.is_online.equalsIgnoreCase("0")){
                    customToast.showToast("This user can not be connected, as the user is not online right now.", 0, 0);
                    return;
                }


                dialogCallDetails.dismiss();

                if (callLogBean.getCallto().equalsIgnoreCase("doctor")) {
                    DATA.isFromDocToDoc = true;
                    DATA.selectedDrId = callLogBean.getPatient_id();
                    DATA.selectedDrName = callLogBean.getFirst_name()+" "+callLogBean.getLast_name();
                    DATA.selectedDrImage = callLogBean.getImage();

					/*Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(myIntent);
				finish();*/
                    DATA.incomingCall = false;
                    Intent myIntent = new Intent(activity.getBaseContext(), MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    myIntent.putExtra("isFromCallHistoryOrMsgs", true);
                    activity.startActivity(myIntent);
                    activity.finish();

                } else {
                    DATA.incomingCall = false;
                    DATA.isFromDocToDoc = false;
                    DATA.selectedUserCallId = callLogBean.getPatient_id();
                    DATA.selectedUserCallName = callLogBean.getFirst_name()+" "+callLogBean.getLast_name();
                    DATA.selectedUserCallImage = callLogBean.getImage();

                    Intent myIntent = new Intent(activity.getBaseContext(), MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if(callLogBean.current_app.equalsIgnoreCase("family")){
                        myIntent.putExtra("isFromCallHistoryOrMsgs", true);
                        myIntent.putExtra("isFromCallToFamily",true);
                        ActivityTcmDetails.primary_patient_id = callLogBean.getPatient_id();
                        DATA.selectedUserCallId = "";
                    }else{
                        DATA.selectedUserCallId = callLogBean.getPatient_id();
                    }

                    activity.startActivity(myIntent);
                    activity.finish();


                    new GloabalMethods(activity).getPatientDetails(false);
                    //this service called here to refresh selected patient livecare details for prescrption view after call
                }

            }
        });



        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //dialogCallDetails.dismiss();

                if (callLogBean.getCallto().equalsIgnoreCase("doctor")) {
                    DATA.isFromDocToDoc = true;
                    DATA.selectedDrId = callLogBean.getPatient_id();
                    DATA.selectedDrName = callLogBean.getFirst_name()+" "+callLogBean.getLast_name();
                    DATA.selectedDrImage = callLogBean.getImage();

                    DATA.selectedDoctorsModel = new DoctorsModel();//GM added in mdlive copy to all - used in GloabalMethods(activity).initMsgDialog()
                    DATA.selectedDoctorsModel.current_app = callLogBean.getCallto();//"specialist";

                    /*DATA.incomingCall = false;
                    Intent myIntent = new Intent(activity.getBaseContext(), MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    myIntent.putExtra("isFromCallHistoryOrMsgs", true);
                    activity.startActivity(myIntent);
                    activity.finish();*/

                } else {
                    DATA.isFromDocToDoc = false;
                    DATA.selectedUserCallId = callLogBean.getPatient_id();
                    DATA.selectedUserCallName = callLogBean.getFirst_name()+" "+callLogBean.getLast_name();
                    DATA.selectedUserCallImage = callLogBean.getImage();

                    /*Intent myIntent = new Intent(activity.getBaseContext(), MainActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if(callLogBean.current_app.equalsIgnoreCase("family")){
                        myIntent.putExtra("isFromCallHistoryOrMsgs", true);
                        myIntent.putExtra("isFromCallToFamily",true);
                        ActivityTcmDetails.primary_patient_id = callLogBean.getPatient_id();
                        DATA.selectedUserCallId = "";
                    }else{
                        DATA.selectedUserCallId = callLogBean.getPatient_id();
                    }
                    activity.startActivity(myIntent);
                    activity.finish();
                    new GloabalMethods(activity).getPatientDetails(false);*/
                    //this service called here to refresh selected patient livecare details for prescrption view after call
                }

                new GloabalMethods(activity).initMsgDialog();

            }
        });

        btnRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection.isConnectedToInternet()){
                    getLivecareId(callLogBean.getPatient_id());
                }else{
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                }
            }
        });

        ImageView ivClose = (ImageView) dialogCallDetails.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialogCallDetails.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCallDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogCallDetails.show();
        dialogCallDetails.getWindow().setAttributes(lp);

        if (callLogBean.getCallto().equalsIgnoreCase("doctor")) {
            notesCont.setVisibility(View.GONE);
            btnAddSOAP.setVisibility(View.GONE);
            //dialogCallDetails.findViewById(R.id.div1).setVisibility(View.GONE);
            layB2.setVisibility(View.GONE);
        } else {
            notesCont.setVisibility(View.VISIBLE);
            btnAddSOAP.setVisibility(View.VISIBLE);
            //dialogCallDetails.findViewById(R.id.div1).setVisibility(View.VISIBLE);

            //int visi = activity instanceof ActivityCallLogs ? View.GONE : View.VISIBLE;//bc also come from ActivityTCM - Emcura Patients Tab
            layB2.setVisibility(View.VISIBLE);//visi

            btnSendEncNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogAddEncNoteFromCallHist(activity, callLogBean, DialogCallDetails.this).showTelemedicineDialog();
                }
            });

            btnAddSOAP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCallDetails.dismiss();
                    DATA.selectedUserCallId = callLogBean.getPatient_id();
                    //ActivitySOAP.addSoapFlag = 2;
                    //startActivity(new Intent(activity,ActivityTelemedicineServices.class));
                    new GloabalMethods(activity).showAddSOAPDialog();
                }
            });
            btnSendPres.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DATA.isFromAppointment = false;//used on prescription dialog to show info from apptmnt or livecare.
                    DATA.selectedUserCallId = callLogBean.getPatient_id();
                    new GloabalMethods(activity).getPatientDetails(true);
                }
            });

            btnrecall.setEnabled(!TextUtils.isEmpty(callLogBean.last_seen));
            btnSendMsg.setEnabled(!TextUtils.isEmpty(callLogBean.last_seen));
            btnSendPres.setEnabled(!TextUtils.isEmpty(callLogBean.last_seen));

            if (checkInternetConnection.isConnectedToInternet()) {
                getNotes(callLogBean.getPatient_id());
            } else {
                customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
            }
        }
    }

    public void getLivecareId(String patientId) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);

        //System.out.println("-- url in getLivecareId: "+DATA.baseUrl+"getLivecareId/"+patientId+"/"+prefs.getString("id",""));
        //getLivecareId/{patient_id}/{doctor_id}

        String reqURL = DATA.baseUrl+"getLivecareId/"+patientId+"/"+prefs.getString("id","");

        System.out.println("-- Request : "+reqURL);
        //System.out.println("-- params : "+params.toString());

        client.post(reqURL , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in getLivecareId: "+content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if(jsonObject.has("success")){
                            DATA.selectedUserAppntID = jsonObject.getString("livecare_id");//will be used for referToSpecialist by going DocToDoc---->DoctorsList

                            DATA.referToSpecialist = true;
                            openActivity.open(DocToDoc.class, false);
                        }else {
                            customToast.showToast(jsonObject.getString("message"),0,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }




    ArrayList<NotesBean> notesBeans;
    public void getNotes(String patientID) {
        RequestParams params = new RequestParams();
        params.put("patient_id", patientID);
        params.put("doctor_id",prefs.getString("id",""));
		/*if(!search_doctor_id.isEmpty()){
			params.put("search_doctor_id", search_doctor_id);
		}
		if(!search_date.isEmpty()){
			params.put("search_date", search_date);
		}*/

        //ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_NOTES,"post",params,apiCallBack, activity);
        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_ENCOUNTER_NOTES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
        //Note : changed this API for urgent care doc to show encounter notes on call detail page.
        //not showing SOAP notes. GM
    }//end getCallLogs


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_PATIENT_ENCOUNTER_NOTES)){//ApiManager.GET_PATIENT_NOTES

            try {

                JSONObject jsonObject = new JSONObject(content);

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                notesBeans = new ArrayList<NotesBean>();
                if (jsonArray.length() == 0) {
                    tvNoNotes.setVisibility(View.VISIBLE);
                    NotesAdapter2 adapter = new NotesAdapter2(activity, notesBeans);
                    lvNotes.setAdapter(adapter);
                } else {
                    tvNoNotes.setVisibility(View.GONE);

                    NotesBean bean;
                    for (int i = 0; i < jsonArray.length(); i++) {


                        String history = jsonArray.getJSONObject(i).getJSONObject("notes").optString("history");
                        String plan = jsonArray.getJSONObject(i).getJSONObject("notes").optString("plan");
                        //String objective = jsonArray.getJSONObject(i).getJSONObject("notes").getString("objective");
                        String objective = "";
                        String family = "";
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("family")){
                            family = jsonArray.getJSONObject(i).getJSONObject("notes").getString("family");
                        }
                        String subjective = jsonArray.getJSONObject(i).getJSONObject("notes").optString("subjective");
                        String assesment = jsonArray.getJSONObject(i).getJSONObject("notes").optString("assesment");
                        String patient_id = DATA.selectedUserCallId;//jsonArray.getJSONObject(i).getString("patient_id");
                        String notes_date = jsonArray.getJSONObject(i).getString("notes_date");
                        String dr_name = jsonArray.getJSONObject(i).getString("dr_name");
                        String care_plan = "";
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("care_plan")){
                            care_plan = jsonArray.getJSONObject(i).getJSONObject("notes").getString("care_plan");
                        }
                        String author_by = "";
                        if(jsonArray.getJSONObject(i).has("author_by")){
                            author_by = jsonArray.getJSONObject(i).getString("author_by");
                        }
                        String amend_btn = "0";
                        if(jsonArray.getJSONObject(i).has("amend_btn")){
                            amend_btn = jsonArray.getJSONObject(i).getString("amend_btn");
                        }
                        String is_amended = "0";
                        if(jsonArray.getJSONObject(i).has("is_amended")){
                            is_amended = jsonArray.getJSONObject(i).getString("is_amended");
                        }

                        bean = new NotesBean(history, plan, objective, family, subjective, assesment, patient_id, notes_date,
                                dr_name,care_plan,author_by,amend_btn,is_amended);

                        bean.id = jsonArray.getJSONObject(i).getString("id");
                        bean.treatment_codes = jsonArray.getJSONObject(i).getString("treatment_codes");

                        String complain = "";
                        String pain_where = "";
                        String pain_severity = "";
                        String prescription = "";
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("complain")){
                            complain = jsonArray.getJSONObject(i).getJSONObject("notes").getString("complain");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("pain_where")){
                            pain_where = jsonArray.getJSONObject(i).getJSONObject("notes").getString("pain_where");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("pain_severity")){
                            pain_severity = jsonArray.getJSONObject(i).getJSONObject("notes").getString("pain_severity");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("prescription")){
                            prescription = jsonArray.getJSONObject(i).getJSONObject("notes").getString("prescription");
                        }

                        bean.complain = complain;
                        bean.pain_where = pain_where;
                        bean.pain_severity = pain_severity;
                        bean.prescription = prescription;

                        String ot_data = "";
                        if(jsonArray.getJSONObject(i).has("ot_data")){
                            ot_data = jsonArray.getJSONObject(i).getString("ot_data");
                        }
                        String ot_date = "Not Added";
                        String ot_timein = "Not Added";
                        String ot_timeout = "Not Added";
                        String ot_bp = "Not Added";
                        String ot_hr = "Not Added";
                        String ot_respirations = "Not Added";
                        String ot_saturation = "Not Added";
                        String ot_blood_sugar = "Not Added";

                        String ot_temperature = "Not Added";
                        String ot_height = "Not Added";
                        String ot_weight = "Not Added";
                        String ot_bmi = "Not Added";

                        if(!ot_data.isEmpty()){
                            JSONObject ot_dataJSON = new JSONObject(ot_data);

                            if(ot_dataJSON.has("ot_date")){
                                ot_date = ot_dataJSON.getString("ot_date");
                            }
                            if(ot_dataJSON.has("ot_timein")){
                                ot_timein = ot_dataJSON.getString("ot_timein");
                            }
                            if(ot_dataJSON.has("ot_timeout")){
                                ot_timeout = ot_dataJSON.getString("ot_timeout");
                            }
                            if(ot_dataJSON.has("ot_bp")){
                                ot_bp = ot_dataJSON.getString("ot_bp");
                            }
                            if(ot_dataJSON.has("ot_hr")){
                                ot_hr = ot_dataJSON.getString("ot_hr");
                            }
                            if(ot_dataJSON.has("ot_respirations")){
                                ot_respirations = ot_dataJSON.getString("ot_respirations");
                            }
                            if(ot_dataJSON.has("ot_saturation")){
                                ot_saturation = ot_dataJSON.getString("ot_saturation");
                            }
                            if(ot_dataJSON.has("ot_blood_sugar")){
                                ot_blood_sugar = ot_dataJSON.getString("ot_blood_sugar");
                            }

                            if(ot_dataJSON.has("ot_temperature")){
                                ot_temperature = ot_dataJSON.getString("ot_temperature");
                            }
                            if(ot_dataJSON.has("ot_height")){
                                ot_height = ot_dataJSON.getString("ot_height");
                            }
                            if(ot_dataJSON.has("ot_weight")){
                                ot_weight = ot_dataJSON.getString("ot_weight");
                            }
                            if(ot_dataJSON.has("ot_bmi")){
                                ot_bmi = ot_dataJSON.getString("ot_bmi");
                            }
                        }
                        bean.ot_date = ot_date;
                        bean.ot_timein = ot_timein;
                        bean.ot_timeout = ot_timeout;
                        bean.ot_bp = ot_bp;
                        bean.ot_hr = ot_hr;
                        bean.ot_respirations = ot_respirations;
                        bean.ot_saturation = ot_saturation;
                        bean.ot_blood_sugar = ot_blood_sugar;
                        bean.ot_temperature = ot_temperature;
                        bean.ot_height = ot_height;
                        bean.ot_weight = ot_weight;
                        bean.ot_bmi = ot_bmi;


                        if(jsonArray.getJSONObject(i).has("examination")){
                            bean.examination = jsonArray.getJSONObject(i).getString("examination");
                        }
                        if(jsonArray.getJSONObject(i).has("dme_referral")){
                            bean.dme_referral = jsonArray.getJSONObject(i).getString("dme_referral");
                        }
                        if(jsonArray.getJSONObject(i).has("skilled_nursing")){
                            bean.skilled_nursing = jsonArray.getJSONObject(i).getString("skilled_nursing");
                        }
                        if(jsonArray.getJSONObject(i).has("homecare_referral")){
                            bean.homecare_referral = jsonArray.getJSONObject(i).getString("homecare_referral");
                        }

                        bean.submit_type = jsonArray.getJSONObject(i).getString("submit_type");
                        bean.patient_name = jsonArray.getJSONObject(i).getString("patient_name");


                        bean.note_text = jsonArray.getJSONObject(i).getJSONObject("notes").optString("note_text", "-");

                        String visit_start_time = jsonArray.getJSONObject(i).getJSONObject("notes").optString("visit_start_time");//12:42:22
                        String visit_end_time = jsonArray.getJSONObject(i).getJSONObject("notes").optString("visit_end_time");//12:42:22


                        String stTime = "-", eTime = "-", dur = "-";
                        try {
                            Date callStrtDate = null, callEndDate = null;
                            if(!TextUtils.isEmpty(visit_start_time)){//12:42:22
                                stTime = visit_start_time;
                                callStrtDate = new SimpleDateFormat("HH:mm:ss").parse(stTime);
                                stTime = new SimpleDateFormat("hh:mm:ss a").format(callStrtDate);//MM/dd/yyyy
                            }

                            if(! TextUtils.isEmpty(visit_end_time)){//12:42:22
                                eTime = visit_end_time;
                                callEndDate = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(eTime);
                                eTime = new SimpleDateFormat("hh:mm:ss a").format(callEndDate);//MM/dd/yyyy
                            }

                            if(callStrtDate != null && callEndDate != null){
                                long totalDiffMillis = callEndDate.getTime() - callStrtDate.getTime();
                                try {
                                    /*dur = String.format(Locale.ENGLISH, "%02d min, %02d sec",
                                            TimeUnit.MILLISECONDS.toMinutes(totalDiffMillis),
                                            TimeUnit.MILLISECONDS.toSeconds(totalDiffMillis) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDiffMillis))
                                    );*/

                                    int seconds = (int) (totalDiffMillis / 1000) % 60 ;
                                    int minutes = (int) ((totalDiffMillis / (1000*60)) % 60);
                                    int hours   = (int) ((totalDiffMillis / (1000*60*60)) % 24);

                                    dur = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);

                                }catch (Exception e){e.printStackTrace();}
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        bean.visit_start_time = stTime;
                        bean.visit_end_time = eTime;
                        bean.callDuration = dur;

                        notesBeans.add(bean);
                        bean = null;

                    }
                    NotesAdapter2 adapter = new NotesAdapter2(activity, notesBeans);
                    lvNotes.setAdapter(adapter);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }
        }
    }


    @Override
    public void refreshNotes() {
        if (checkInternetConnection.isConnectedToInternet()) {
            try {
                getNotes(callLogBeanGloabal.getPatient_id());
            }catch (Exception e){e.printStackTrace();}
        } else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }
    }
}
