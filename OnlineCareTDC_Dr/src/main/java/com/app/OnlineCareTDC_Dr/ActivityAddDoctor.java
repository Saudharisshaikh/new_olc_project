package com.app.OnlineCareTDC_Dr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.OnlineCareTDC_Dr.adapter.InvitesByDoctorsAdapter;
import com.app.OnlineCareTDC_Dr.adapter.UsersAdapter;
import com.app.OnlineCareTDC_Dr.api.ApiCallBack;
import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.api.CustomSnakeBar;
import com.app.OnlineCareTDC_Dr.model.DoctorInviteBean;
import com.app.OnlineCareTDC_Dr.model.DoctorsModel;
import com.app.OnlineCareTDC_Dr.model.HospitalBean;
import com.app.OnlineCareTDC_Dr.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Dr.util.CustomToast;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.Database;
import com.app.OnlineCareTDC_Dr.util.GloabalSocket;
import com.app.OnlineCareTDC_Dr.util.HideShowKeypad;
import com.app.OnlineCareTDC_Dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.app.OnlineCareTDC_Dr.api.ApiManager.GET_HOSPITAL;

public class ActivityAddDoctor extends BaseActivity implements ApiCallBack, View.OnClickListener, GloabalSocket.SocketEmitterCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomSnakeBar customSnakeBar;

    Button btnPrimaryCare,btnSpecialist,btnOnlineCare;
    Spinner spHospital;
    ListView lvDoctors;
    TextView tvNoDoc;

    ViewFlipper vfAddDoctor;
    TextView tvAddDoctor,tvDoctorInvites;
    ListView lvDoctorsInvites;
    TextView tvNoInvites;

    GloabalSocket gloabalSocket;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        activity = ActivityAddDoctor.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_CALLINVITE);

        gloabalSocket = new GloabalSocket(activity,this);

        btnPrimaryCare = (Button) findViewById(R.id.btnPrimaryCare);
        btnSpecialist = (Button) findViewById(R.id.btnSpecialist);
        btnOnlineCare = (Button) findViewById(R.id.btnOnlineCare);
        spHospital = (Spinner) findViewById(R.id.spHospital);
        lvDoctors = (ListView) findViewById(R.id.lvDoctors);
        tvNoDoc = (TextView) findViewById(R.id.tvNoDoc);

        vfAddDoctor = (ViewFlipper) findViewById(R.id.vfAddDoctor);
        tvAddDoctor = (TextView) findViewById(R.id.tvAddDoctor);
        tvDoctorInvites = (TextView) findViewById(R.id.tvDoctorInvites);
        lvDoctorsInvites = (ListView) findViewById(R.id.lvDoctorsInvites);
        tvNoInvites = (TextView) findViewById(R.id.tvNoInvites);

        tvAddDoctor.setOnClickListener(this);
        tvDoctorInvites.setOnClickListener(this);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("category_id",v.getTag().toString());
                //params.put("zipcode", etSignupZipcode.getText().toString());
                ApiManager apiManager = new ApiManager(GET_HOSPITAL,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        };
        btnPrimaryCare.setOnClickListener(onClickListener);
        btnSpecialist.setOnClickListener(onClickListener);
        btnOnlineCare.setOnClickListener(onClickListener);

        String textPlaceholder = "Please tap <font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+btnPrimaryCare.getText().toString()+"</font>, <font color='"+ DATA.APP_THEME_RED_COLOR +"'>"
                +btnSpecialist.getText().toString()+"</font> or <font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+btnOnlineCare.getText().toString()+"</font> to view doctors.";
        tvNoDoc.setText(Html.fromHtml(textPlaceholder));

        spHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("hospital_id", hospitalBeens.get(position).id);

               //params.put("hospital_id", "1");//odev doctors

                ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTOR_BY_CLINIC,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(activity).setTitle("Confirm")
                                .setMessage("Do you want to send add invitation to doctor ?")
                                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RequestParams params = new RequestParams();
                                        params.put("doctor_from", prefs.getString("id",""));
                                        params.put("doctor_to", DATA.allDoctors.get(position).id);

                                        ApiManager apiManager = new ApiManager(ApiManager.INVITE_DOCTOR,"post",params,apiCallBack, activity);
                                        apiManager.loadURL();
                                    }
                                }).setNegativeButton("Cancel",null).create();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                alertDialog.show();
            }
        });


        getInvites();
    }

    public void getInvites(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_INVITES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<HospitalBean> hospitalBeens;
    UsersAdapter usersAdapter;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equals(GET_HOSPITAL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                hospitalBeens = new ArrayList<HospitalBean>();
                HospitalBean bean;
                if(data.length() == 0){
                    customToast.showToast("No doctors found under the zipcode OR hospital",0,0);
                }
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String hospital_name = data.getJSONObject(i).getString("hospital_name");
                    String folder_name = data.getJSONObject(i).getString("folder_name");
                    String category_id = data.getJSONObject(i).getString("folder_name");
                    String hospital_zipcode = data.getJSONObject(i).getString("folder_name");

                    bean = new HospitalBean(id,hospital_name,folder_name,category_id,hospital_zipcode);
                    hospitalBeens.add(bean);
                    bean = null;
                }

                ArrayAdapter<HospitalBean> hospitalAdapter = new ArrayAdapter<HospitalBean>(activity, R.layout.spinner_item_center, hospitalBeens);
                spHospital.setAdapter(hospitalAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equals(ApiManager.GET_DOCTOR_BY_CLINIC)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");

                if(status.equals("success")) {

                    String drsStr = jsonObject.getString("doctors");

                    JSONArray doctorsArray = new JSONArray(drsStr);

                    if(doctorsArray.length() == 0){
                        tvNoDoc.setVisibility(View.VISIBLE);
                        tvNoDoc.setText("No doctors found in the hospital");
                    }else {
                        tvNoDoc.setVisibility(View.GONE);
                    }

                    DATA.allDoctors = new ArrayList<DoctorsModel>();

                    DoctorsModel temp;

                    for(int i = 0; i<doctorsArray.length(); i++) {

                        temp = new DoctorsModel();

                        temp.id = doctorsArray.getJSONObject(i).getString("id");
                        temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
                        temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
                        temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
                        temp.email = doctorsArray.getJSONObject(i).getString("email");
                        temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
                        temp.image = doctorsArray.getJSONObject(i).getString("image");
                        temp.careerData = doctorsArray.getJSONObject(i).getString("introduction");
                        temp.designation = doctorsArray.getJSONObject(i).getString("designation");

                        temp.current_app=doctorsArray.getJSONObject(i).getString("current_app");
                        temp.zip_code=doctorsArray.getJSONObject(i).getString("zip_code");

                        temp.is_online=doctorsArray.getJSONObject(i).getString("is_online");

                        DATA.print("--online care callwebservice getOnline Doctors fname: "+temp.fName);

                        DATA.allDoctors.add(temp);
                        DATA.print("--size "+DATA.allDoctors.size()+"");
                        temp = null;

                    }

                    usersAdapter = new UsersAdapter(activity);
                    lvDoctors.setAdapter(usersAdapter);//LvDoctorsAdapter

                } else {
                    customToast.showToast("Internal server error", 0, 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equals(ApiManager.INVITE_DOCTOR)){
            //result: {"status":"success","message":"Saved"}

            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast("Invitation has been sent",0,1);
                }else {
                    customToast.showToast(jsonObject.getString("message"),0,1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }

        }else if(apiName.equals(ApiManager.GET_INVITES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if(data.length() == 0){
                    tvNoInvites.setVisibility(View.VISIBLE);
                }else {
                    tvNoInvites.setVisibility(View.GONE);
                }
                ArrayList<DoctorInviteBean> doctorInviteBeans = new ArrayList<>();
                DoctorInviteBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String doctor_from = data.getJSONObject(i).getString("doctor_from");
                    String doctor_to = data.getJSONObject(i).getString("doctor_to");
                    String dateof = data.getJSONObject(i).getString("dateof");
                    String status = data.getJSONObject(i).getString("status");
                    String first_name = data.getJSONObject(i).getString("first_name");
                    String last_name = data.getJSONObject(i).getString("last_name");
                    String image = data.getJSONObject(i).getString("image");

                    bean = new DoctorInviteBean(id,doctor_from,doctor_to,dateof,status,first_name,last_name,image);
                    doctorInviteBeans.add(bean);
                    bean = null;
                }
                lvDoctorsInvites.setAdapter(new InvitesByDoctorsAdapter(activity,doctorInviteBeans));
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    int selectedChild = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvAddDoctor:
                tvAddDoctor.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvAddDoctor.setTextColor(getResources().getColor(android.R.color.white));
                tvDoctorInvites.setBackgroundColor(getResources().getColor(android.R.color.white));
                tvDoctorInvites.setTextColor(getResources().getColor(R.color.theme_red));

                selectedChild = 0;
                if (selectedChild > vfAddDoctor.getDisplayedChild()) {

                    vfAddDoctor.setInAnimation(activity, R.anim.in_right);
                    vfAddDoctor.setOutAnimation(activity, R.anim.out_left);
                } else {

                    vfAddDoctor.setInAnimation(activity, R.anim.in_left);
                    vfAddDoctor.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfAddDoctor.getDisplayedChild() != selectedChild) {
                    vfAddDoctor.setDisplayedChild(selectedChild);
                }
                break;
            case R.id.tvDoctorInvites:
                tvAddDoctor.setBackgroundColor(getResources().getColor(android.R.color.white));
                tvAddDoctor.setTextColor(getResources().getColor(R.color.theme_red));
                tvDoctorInvites.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvDoctorInvites.setTextColor(getResources().getColor(android.R.color.white));


                selectedChild = 1;
                if (selectedChild > vfAddDoctor.getDisplayedChild()) {
                    vfAddDoctor.setInAnimation(activity, R.anim.in_right);
                    vfAddDoctor.setOutAnimation(activity, R.anim.out_left);
                } else {
                    vfAddDoctor.setInAnimation(activity, R.anim.in_left);
                    vfAddDoctor.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfAddDoctor.getDisplayedChild() != selectedChild) {
                    vfAddDoctor.setDisplayedChild(selectedChild);
                }
                break;
            default:
                break;
        }
    }



    @Override
    public void onSocketCallBack(String emitterResponse) {

        try {
            JSONObject jsonObject = new JSONObject(emitterResponse);
            String id = jsonObject.getString("id");
            String usertype = jsonObject.getString("usertype");
            String status = jsonObject.getString("status");

            if(usertype.equalsIgnoreCase("doctor")){
                for (int i = 0; i < DATA.allDoctors.size(); i++) {
                    if(DATA.allDoctors.get(i).id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            DATA.allDoctors.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            DATA.allDoctors.get(i).is_online = "0";
                        }
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
