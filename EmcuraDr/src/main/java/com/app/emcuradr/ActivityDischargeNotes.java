package com.app.emcuradr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcuradr.adapter.DischargeNotesAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.DiscNoteBean;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDischargeNotes extends BaseActivity implements ApiCallBack {

    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;

    ArrayList<DiscNoteBean> discNoteBeans;
    ListView rvDiscNotes;
    TextView tvNoDisNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discharge_notes);

        activity = ActivityDischargeNotes.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        openActivity = new OpenActivity(activity);

        rvDiscNotes = findViewById(R.id.rvDiscNotes);
        tvNoDisNotes = findViewById(R.id.tvNoDisNotes);

        getDischargeNotes();
    }

    public void getDischargeNotes() {
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_DISCHARGE_NOTES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equals(ApiManager.GET_PATIENT_DISCHARGE_NOTES)) {
            try {

                JSONArray jsonArray = new JSONArray(content);

                DATA.print("JSON Response: " + jsonArray.length()); // Debugging statement

                discNoteBeans = new ArrayList<>(); // Create the ArrayList outside the loop

                if (jsonArray.length() == 0) {
                    tvNoDisNotes.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        DiscNoteBean bean = new DiscNoteBean();
                        bean.id = jsonObject.getString("id");
                        // Debugging statement
                        DATA.print("-- Notes text adap " + jsonObject.getString("notes"));

                        // Parse the nested 'notes' field
                        JSONObject notesObject = new JSONObject(jsonObject.getString("notes"));
                        DiscNoteBean.Notes notes = new DiscNoteBean.Notes();
                        notes.note_text = notesObject.getString("note_text");
                        bean.notes = notes;

                        bean.notes_date = jsonObject.getString("notes_date");
                        bean.laid = jsonObject.getString("laid");
                        bean.note_type = jsonObject.getString("note_type");
                        bean.treatment_codes = jsonObject.getString("treatment_codes");
                        bean.author_by = jsonObject.getString("author_by");
                        bean.is_approved = jsonObject.getString("is_approved");
                        bean.note_patient_id = jsonObject.getString("note_patient_id");
                        bean.is_amended = jsonObject.getString("is_amended");
                        bean.ot_data = jsonObject.getString("ot_data");
                        bean.note_status = jsonObject.getString("note_status");
                        bean.is_deleted = jsonObject.getString("is_deleted");
                        bean.prescription_id = jsonObject.getString("prescription_id");
                        bean.examination = jsonObject.getString("examination");
                        bean.dme_referral = jsonObject.getString("dme_referral");
                        bean.skilled_nursing = jsonObject.getString("skilled_nursing");
                        bean.dme_status = jsonObject.getString("dme_status");
                        bean.skilled_nursing_status = jsonObject.getString("skilled_nursing_status");
                        bean.homecare_referral = jsonObject.getString("homecare_referral");
                        bean.homecare_status = jsonObject.getString("homecare_status");
                        bean.submit_type = jsonObject.getString("submit_type");
                        bean.call_time = jsonObject.getString("call_time");
                        bean.is_visible = jsonObject.getString("is_visible");
                        bean.dr_name = jsonObject.getString("dr_name");
                        bean.patient_name = jsonObject.getString("patient_name");
                        discNoteBeans.add(bean);
                    }

                    // Debugging statement
                    for (DiscNoteBean noteBean : discNoteBeans) {
                        DATA.print("-- Notes text adap " + noteBean.notes.note_text);
                    }

                    DischargeNotesAdapter dischargeNotesAdapter = new DischargeNotesAdapter(activity, discNoteBeans);
                    rvDiscNotes.setAdapter(dischargeNotesAdapter);

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            }
        }
    }
}