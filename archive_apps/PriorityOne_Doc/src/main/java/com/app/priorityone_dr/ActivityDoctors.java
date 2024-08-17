package com.app.priorityone_dr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.priorityone_dr.adapter.DoctorInviteAdapter;
import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.model.DoctorsModel;
import com.app.priorityone_dr.util.CheckInternetConnection;
import com.app.priorityone_dr.util.CustomToast;
import com.app.priorityone_dr.util.DATA;
import com.app.priorityone_dr.util.HideShowKeypad;
import com.app.priorityone_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDoctors extends AppCompatActivity implements ApiCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvDoctor;
    TextView tvNoDoctor;
    Button btnDoctors,btnSpecialist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        activity = ActivityDoctors.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        tvNoDoctor = (TextView) findViewById(R.id.tvNoDoctor);
        lvDoctor = (ListView) findViewById(R.id.lvDoctor);
        btnDoctors = (Button) findViewById(R.id.btnDoctors);
        btnSpecialist = (Button) findViewById(R.id.btnSpecialist);

        lvDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("user_id", doctorsModels.get(position).id);
                params.put("user_type", "doctor");
                params.put("group_id",ActivityGroupChat.selectedGroupBean.id);

                ApiManager apiManager = new ApiManager(ApiManager.ADD_TO_GROUP,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        btnDoctors.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                    btnDoctors.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnDoctors.setTextColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));

                    callApi("doctor");
            }
        });

        btnSpecialist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                    btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setTextColor(getResources().getColor(android.R.color.white));

                    callApi("specialist");
            }
        });

        callApi("doctor");
    }

    public void callApi(String userType){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("user_type", userType);

        ApiManager apiManager = new ApiManager(ApiManager.SEARCHALLDOCTORS_,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<DoctorsModel> doctorsModels;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if (apiName.equals(ApiManager.SEARCHALLDOCTORS_)){
            try {
                JSONArray data = new JSONArray(content);
                doctorsModels = new ArrayList<DoctorsModel>();
                DoctorsModel temp = null;
                DoctorInviteAdapter doctorInviteAdapter;

                if (data.length() == 0) {
                    //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    tvNoDoctor.setVisibility(View.VISIBLE);
                    doctorInviteAdapter = new DoctorInviteAdapter(activity,doctorsModels);
                    lvDoctor.setAdapter(doctorInviteAdapter);
                }else{
                    tvNoDoctor.setVisibility(View.GONE);

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

                        doctorsModels.add(temp);
                        temp = null;
                    }

                    doctorInviteAdapter = new DoctorInviteAdapter(activity,doctorsModels);
                    lvDoctor.setAdapter(doctorInviteAdapter);

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equals(ApiManager.ADD_TO_GROUP)){
            //result: {"status":"success","message":"User has been added."}
            try {
                JSONObject jsonObject = new JSONObject(content);

                customToast.showToast(jsonObject.getString("message"),0,0);
                String s = jsonObject.getString("status");
                if (s.equalsIgnoreCase("success")) {
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
