package com.app.emcurama;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcurama.adapter.OTNotesAdapter;
import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.OTNoteBean;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.HideShowKeypad;
import com.app.emcurama.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityViewOTNotes extends AppCompatActivity implements ApiCallBack {

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvOTNotes;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_otnotes);

        activity = ActivityViewOTNotes.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        lvOTNotes = (ListView) findViewById(R.id.lvOTNotes);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        if(checkInternetConnection.isConnectedToInternet()){
            //getotnotes();
            RequestParams params = new RequestParams();
            params.put("patient_id", DATA.selectedUserCallId);

            ApiManager apiManager = new ApiManager(ApiManager.API_GET_OTNOTES,"post",params,apiCallBack, activity);
            apiManager.loadURL();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }
    }


    /*public void getotnotes() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("patient_id", DATA.selectedUserCallId);

        client.post(DATA.baseUrl+"doctor/getotnotes", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--reaponce in getotnotes"+content);

                try {
                    JSONObject jsonObject = new JSONObject(content);

                    ArrayList<OTNoteBean> otNoteBeens = new ArrayList<OTNoteBean>();
                    OTNoteBean bean;
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject note_data = new JSONObject(data.getJSONObject(i).getString("note_data"));

                        String id = data.getJSONObject(i).getString("id");
                        String author_by = data.getJSONObject(i).getString("author_by");
                        String patient_id = data.getJSONObject(i).getString("patient_id");
                        String dateof = data.getJSONObject(i).getString("dateof");
                        String first_name = data.getJSONObject(i).getString("first_name");
                        String last_name = data.getJSONObject(i).getString("last_name");
                        String image = data.getJSONObject(i).getString("image");
                        //"note_data" json object contains:
                        String ot_dmeneed = note_data.getString("ot_dmeneed");
                        String ot_date = note_data.getString("ot_date");
                        String ot_iadl = note_data.getString("ot_iadl");
                        String ot_respirations = note_data.getString("ot_respirations");
                        String ot_adl = note_data.getString("ot_adl");
                        String ot_hr = note_data.getString("ot_hr");
                        String ot_plan = note_data.getString("ot_plan");
                        String ot_bp = note_data.getString("ot_bp");
                        String ot_mobility = note_data.getString("ot_mobility");
                        String ot_subjective = note_data.getString("ot_subjective");
                        String ot_saturation = note_data.getString("ot_saturation");
                        String ot_timein = note_data.getString("ot_timein");
                        String ot_timeout = note_data.getString("ot_timeout");

                        bean = new OTNoteBean(id,author_by,patient_id,dateof,first_name,last_name,image,ot_dmeneed,
                                ot_date,ot_iadl,ot_respirations,ot_adl,ot_hr,ot_plan,ot_bp,ot_mobility,ot_subjective,
                                ot_saturation,ot_timein,ot_timeout);
                        otNoteBeens.add(bean);
                        bean = null;
                    }
                    lvOTNotes.setAdapter(new OTNotesAdapter(activity,otNoteBeens));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable error, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--onfail getotnotes" +content);
                Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
            }
        });

    }//getotnotes*/

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if (status.equals(ApiManager.API_STATUS_SUCCESS)){
            if (apiName.equals(ApiManager.API_GET_OTNOTES)){
                try {
                    JSONObject jsonObject = new JSONObject(content);

                    ArrayList<OTNoteBean> otNoteBeens = new ArrayList<OTNoteBean>();
                    OTNoteBean bean;
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length() == 0){tvNoData.setVisibility(View.VISIBLE);}else{tvNoData.setVisibility(View.GONE);}
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject note_data = new JSONObject(data.getJSONObject(i).getString("note_data"));

                        String id = data.getJSONObject(i).getString("id");
                        String author_by = data.getJSONObject(i).getString("author_by");
                        String patient_id = data.getJSONObject(i).getString("patient_id");
                        String dateof = data.getJSONObject(i).getString("dateof");
                        String first_name = data.getJSONObject(i).getString("first_name");
                        String last_name = data.getJSONObject(i).getString("last_name");
                        String image = data.getJSONObject(i).getString("image");
                        String num_images = data.getJSONObject(i).getString("num_images");

                        String dme_referral = data.getJSONObject(i).getString("dme_referral");

                        //"note_data" json object contains:
                        /*String ot_dmeneed = note_data.getString("ot_dmeneed");
                        String ot_date = note_data.getString("ot_date");
                        String ot_iadl = note_data.getString("ot_iadl");
                        String ot_respirations = note_data.getString("ot_respirations");
                        String ot_adl = note_data.getString("ot_adl");
                        String ot_hr = note_data.getString("ot_hr");
                        String ot_plan = note_data.getString("ot_plan");
                        String ot_bp = note_data.getString("ot_bp");
                        String ot_mobility = note_data.getString("ot_mobility");
                        String ot_subjective = "";
                        if(note_data.has("ot_subjective")){
                            ot_subjective = note_data.getString("ot_subjective");
                        }
                        String ot_saturation = note_data.getString("ot_saturation");
                        String ot_timein = note_data.getString("ot_timein");
                        String ot_timeout = note_data.getString("ot_timeout");*/
                        String ot_dmeneed = "";
                        if(note_data.has("ot_dmeneed")){
                            ot_dmeneed = note_data.getString("ot_dmeneed");
                        }
                        String ot_date = "";
                        if(note_data.has("ot_date")){
                            ot_date = note_data.getString("ot_date");
                        }
                        String ot_iadl = "";
                        if(note_data.has("ot_iadl")){
                            ot_iadl = note_data.getString("ot_iadl");
                        }
                        String ot_respirations = "";
                        if(note_data.has("ot_respirations")){
                            ot_respirations = note_data.getString("ot_respirations");
                        }
                        String ot_adl = "";
                        if(note_data.has("ot_adl")){
                            ot_adl = note_data.getString("ot_adl");
                        }
                        String ot_hr = "";
                        if(note_data.has("ot_hr")){
                            ot_hr = note_data.getString("ot_hr");
                        }
                        String ot_plan = "";
                        if(note_data.has("ot_plan")){
                            ot_plan = note_data.getString("ot_plan");
                        }
                        String ot_bp = "";
                        if(note_data.has("ot_bp")){
                            ot_bp = note_data.getString("ot_bp");
                        }
                        String ot_mobility = "";
                        if(note_data.has("ot_mobility")){
                            ot_mobility = note_data.getString("ot_mobility");
                        }
                        String ot_subjective = "";
                        if(note_data.has("ot_subjective")){
                            ot_subjective = note_data.getString("ot_subjective");
                        }
                        String ot_saturation = "";
                        if(note_data.has("ot_saturation")){
                            ot_saturation = note_data.getString("ot_saturation");
                        }
                        String ot_timein = "";
                        if(note_data.has("ot_timein")){
                            ot_timein = note_data.getString("ot_timein");
                        }
                        String ot_timeout = "";
                        if(note_data.has("ot_timeout")){
                            ot_timeout = note_data.getString("ot_timeout");
                        }

                        String ot_blood_sugar = "";

                        if(note_data.has("ot_blood_sugar")){
                            ot_blood_sugar = note_data.getString("ot_blood_sugar");
                            if(ot_blood_sugar.equalsIgnoreCase("null")){
                                ot_blood_sugar = "";
                            }
                        }

                        String ot_temperature = "";
                        if(note_data.has("ot_temperature")){
                            ot_temperature = note_data.getString("ot_temperature");
                            if(ot_temperature.equalsIgnoreCase("null")){
                                ot_temperature = "";
                            }
                        }

                        String ot_generic_assessment = "";
                        if(note_data.has("ot_generic_assessment")){
                            ot_generic_assessment = note_data.getString("ot_generic_assessment");
                            if(ot_generic_assessment.equalsIgnoreCase("null")){
                                ot_generic_assessment = "";
                            }
                        }

                        bean = new OTNoteBean(id,author_by,patient_id,dateof,first_name,last_name,image,ot_dmeneed,
                                ot_date,ot_iadl,ot_respirations,ot_adl,ot_hr,ot_plan,ot_bp,ot_mobility,ot_subjective,
                                ot_saturation,ot_timein,ot_timeout,ot_blood_sugar,num_images,ot_temperature,ot_generic_assessment,dme_referral);
                        otNoteBeens.add(bean);
                        bean = null;
                    }
                    lvOTNotes.setAdapter(new OTNotesAdapter(activity,otNoteBeens));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
