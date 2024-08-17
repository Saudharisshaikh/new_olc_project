package com.app.emcuradr.reliance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcuradr.R;
import com.app.emcuradr.adapter.NotesAdapter2;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.NotesBean;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DialogAllEncNotes implements ApiCallBack {

    Activity activity;
    SharedPreferences prefs;
    CustomToast customToast;

    public DialogAllEncNotes(Activity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);
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
        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_ENCOUNTER_NOTES,"post",params, DialogAllEncNotes.this, activity);
        apiManager.loadURL();
        //Note : changed this API for urgent care doc to show encounter notes on call detail page.
        //not showing SOAP notes. GM
    }//end getCallLogs


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_PATIENT_ENCOUNTER_NOTES)){//ApiManager.GET_PATIENT_NOTES

            showAllEncNotesDialog(content);

        }
    }




    private void showAllEncNotesDialog(String content) {

        Dialog dialogAllEncNotes = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogAllEncNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAllEncNotes.setContentView(R.layout.dialog_all_enc_notes);

        TextView tvNoNotes = (TextView) dialogAllEncNotes.findViewById(R.id.tvNoNotes);
        ListView lvNotes = (ListView) dialogAllEncNotes.findViewById(R.id.lvNotes);

        ImageView ivClose = (ImageView) dialogAllEncNotes.findViewById(R.id.ivClose);
        Button btnDone =  dialogAllEncNotes.findViewById(R.id.btnDone);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAllEncNotes.dismiss();
            }
        };
        ivClose.setOnClickListener(onClickListener);
        btnDone.setOnClickListener(onClickListener);



        //=================================Parse API json data==================================
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
        //==================Parse data API ends=====================================================



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAllEncNotes.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogAllEncNotes.show();
        dialogAllEncNotes.getWindow().setAttributes(lp);

    }
}
