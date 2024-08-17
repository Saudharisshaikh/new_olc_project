package com.app.mdlive_dr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mdlive_dr.adapter.NotesAdapter2;
import com.app.mdlive_dr.api.ApiCallBack;
import com.app.mdlive_dr.api.ApiManager;
import com.app.mdlive_dr.model.DoctorsModel;
import com.app.mdlive_dr.model.NotesBean;
import com.app.mdlive_dr.util.CheckInternetConnection;
import com.app.mdlive_dr.util.CustomToast;
import com.app.mdlive_dr.util.DATA;
import com.app.mdlive_dr.util.DatePickerFragment;
import com.app.mdlive_dr.util.GloabalMethods;
import com.app.mdlive_dr.util.HideShowKeypad;
import com.app.mdlive_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySOAP extends AppCompatActivity implements ApiCallBack{


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvBilling;
    TextView tvNoNotes;
    EditText etSOAPDate;
    Spinner spSOAPDoc;

    public static int addSoapFlag = 1;//1 = virtual visit data, 2 = new soapnote,3 = last note data, 4 = billWithoutNote, 5=MedicalNote
    //(Note: billWithoutNote is new option for emcura. un this case Doc will not enter soap notes, but he will
    // select billing services and give a short note ie.textbox, save and exit)

    @Override
    protected void onResume() {
        getNotes("","");
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap);

        activity = ActivitySOAP.this;
        apiCallBack = this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
        if(DATA.isFromDocToDoc){
            btnToolbar.setVisibility(View.GONE);
        }
        btnToolbar.setText("Add SOAP Notes");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GloabalMethods(activity).showAddSOAPDialog();
            }
        });

        etSOAPDate = (EditText) findViewById(R.id.etSOAPDate);
        spSOAPDoc = (Spinner) findViewById(R.id.spSOAPDoc);
        lvBilling = (ListView) findViewById(R.id.lvBilling);
        tvNoNotes = (TextView) findViewById(R.id.tvNoNotes);

        etSOAPDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etSOAPDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        spSOAPDoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){

                    selectedProviderID = notesDoctors.get(position).id;
                    getData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ApiManager apiManager = new ApiManager(ApiManager.NOTES_DOCTORS,"post",null,apiCallBack, activity);
        apiManager.loadURL();
    }
    static String selectedProviderID = "";
    public void getData(){
        String date = "";
        if(!etSOAPDate.getText().toString().equalsIgnoreCase("According to Date")){
            date = etSOAPDate.getText().toString();
        }
        getNotes(selectedProviderID,date);
    }

    ArrayList<NotesBean> notesBeans;
    ArrayList<DoctorsModel> notesDoctors;
    public void getNotes(String search_doctor_id,String search_date) {
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("doctor_id",prefs.getString("id",""));//DATA.selectedDoctorsModel.id
        if(!search_doctor_id.isEmpty()){
            params.put("search_doctor_id", search_doctor_id);
        }
        if(!search_date.isEmpty()){
            params.put("search_date", search_date);
        }

        ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_NOTES,"post",params,apiCallBack, activity);
        apiManager.loadURL();

    }//end getCallLogs

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_PATIENT_NOTES)){

            try {

                JSONObject jsonObject = new JSONObject(content);

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                notesBeans = new ArrayList<NotesBean>();
                if (jsonArray.length() == 0) {
                    tvNoNotes.setVisibility(View.VISIBLE);
                    NotesAdapter2 adapter = new NotesAdapter2(activity, notesBeans);
                    lvBilling.setAdapter(adapter);
                } else {
                    tvNoNotes.setVisibility(View.GONE);

                    NotesBean bean;
                    for (int i = 0; i < jsonArray.length(); i++) {


                        String history = jsonArray.getJSONObject(i).getJSONObject("notes").getString("history");
                        String plan = jsonArray.getJSONObject(i).getJSONObject("notes").getString("plan");
                        //String objective = jsonArray.getJSONObject(i).getJSONObject("notes").getString("objective");
                        String objective = "";
                        String family = "";
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("family")){
                            family = jsonArray.getJSONObject(i).getJSONObject("notes").getString("family");
                        }
                        String subjective = jsonArray.getJSONObject(i).getJSONObject("notes").getString("subjective");
                        String assesment = jsonArray.getJSONObject(i).getJSONObject("notes").getString("assesment");
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
                        String pain_related = "";
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("complain")){
                            complain = jsonArray.getJSONObject(i).getJSONObject("notes").getString("complain");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("pain_where")){
                            pain_where = jsonArray.getJSONObject(i).getJSONObject("notes").getString("pain_where");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("pain_severity")){
                            pain_severity = jsonArray.getJSONObject(i).getJSONObject("notes").getString("pain_severity");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("pain_related")){
                            pain_related = jsonArray.getJSONObject(i).getJSONObject("notes").getString("pain_related");
                        }
                        if(jsonArray.getJSONObject(i).getJSONObject("notes").has("prescription")){
                            prescription = jsonArray.getJSONObject(i).getJSONObject("notes").getString("prescription");
                        }

                        bean.complain = complain;
                        bean.pain_where = pain_where;
                        bean.pain_severity = pain_severity;
                        bean.pain_related = pain_related;
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

                        notesBeans.add(bean);
                        bean = null;

                    }
                    NotesAdapter2 adapter = new NotesAdapter2(activity, notesBeans);
                    lvBilling.setAdapter(adapter);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }



        }else if(apiName.equalsIgnoreCase(ApiManager.NOTES_DOCTORS)){

            try {
                JSONArray data = new JSONArray(content);
                notesDoctors = new ArrayList<DoctorsModel>();
                DoctorsModel temp = null;

                temp = new DoctorsModel();
                temp.fName="According to Provider";
                temp.lName="";
                notesDoctors.add(temp);
                temp = null;


                if (data.length() == 0) {
                    //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    /*tvNoData.setVisibility(View.VISIBLE);
                    usersAdapter = new UsersAdapter(activity);
                    lvUsersList.setAdapter(usersAdapter);*/
                }else{

                    for (int i = 0; i < data.length(); i++) {
                        temp = new DoctorsModel();
                        JSONObject object = data.getJSONObject(i);
                        temp.id = object.getString("id");
                        temp.latitude =object.getString("latitude");
                        temp.longitude=object.getString("longitude");
                        temp.zip_code=object.getString("zip_code");
                        temp.fName=object.getString("first_name");
                        temp.lName=object.getString("last_name");
                        temp.is_online=object.getString("is_online");
                        temp.image=object.getString("image");
                        temp.designation=object.getString("designation");


                        if (temp.latitude.equalsIgnoreCase("null")) {
                            temp.latitude = "0.0";
                        }
                        if (temp.longitude.equalsIgnoreCase("null")) {
                            temp.longitude = "0.0";
                        }

                        temp.speciality_id=object.getString("speciality_id");
                        temp.current_app=object.getString("current_app");
                        temp.speciality_name=object.getString("speciality_name");

                        if(temp.current_app.equalsIgnoreCase("nurse")){
                            temp.lName = temp.lName+" ("+object.getString("doctor_category")+")";
                        }else {
                            temp.lName = temp.lName+" ("+temp.current_app+")";
                        }
                        notesDoctors.add(temp);
                        temp = null;
                    }

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                    /*UsersAdapter usersAdapter = new UsersAdapter(activity);
                    spSOAPDoc.setAdapter(usersAdapter);*/

                    //R.layout.spinner_item_lay
                    ArrayAdapter<DoctorsModel> spPtencyUnitAdapter = new ArrayAdapter<>(
                            activity,
                            R.layout.sp_soap_item,
                            notesDoctors
                    );
                    spPtencyUnitAdapter.setDropDownViewResource(R.layout.sp_soap_item);
                    spSOAPDoc.setAdapter(spPtencyUnitAdapter);

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_soap_notes, menu);

        menu.getItem(0).setTitle("Add SOAP Note");
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {
            /*spSOAPDoc.setSelection(0);
            etSOAPDate.setText("Select Date");
            getNotes("","");*/
            startActivity(new Intent(activity,ActivityTelemedicineServices.class));
            //finish();
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
