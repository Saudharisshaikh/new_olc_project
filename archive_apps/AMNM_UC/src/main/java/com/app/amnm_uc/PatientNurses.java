package com.app.amnm_uc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.amnm_uc.adapter.NurseAdapter;
import com.app.amnm_uc.api.ApiManager;
import com.app.amnm_uc.model.NurseBean;
import com.app.amnm_uc.util.CheckInternetConnection;
import com.app.amnm_uc.util.CustomToast;
import com.app.amnm_uc.util.DATA;
import com.app.amnm_uc.util.GloabalMethods;
import com.app.amnm_uc.util.HideShowKeypad;
import com.app.amnm_uc.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PatientNurses extends AppCompatActivity implements View.OnClickListener{

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvNurses;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_nurses);

        activity = PatientNurses.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        lvNurses = (ListView) findViewById(R.id.lvNurses);

        tvTabNurse = (TextView) findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = (TextView) findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = (TextView) findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = (TextView) findViewById(R.id.tvTabDietitian);
        tvTabOT = (TextView) findViewById(R.id.tvTabOT);


        tvTabNurse.setOnClickListener(this);
        tvTabNursePractitioner.setOnClickListener(this);
        tvTabSocialWorker.setOnClickListener(this);
        tvTabDietitian.setOnClickListener(this);
        tvTabOT.setOnClickListener(this);

        lvNurses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_nurse_msg);

                Button btn_call911 = (Button) dialog.findViewById(R.id.btn_call911);
                Button btnCallForCare = (Button) dialog.findViewById(R.id.btnCallForCare);

                btn_call911.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                });

                btnCallForCare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkInternetConnection.isConnectedToInternet()){
                            dialog.dismiss();
                            sendmessagetoDoctor(nurseBeens.get(position).my_id,"Call For Care");
                            sendmessagetoDoctor1(nurseBeens.get(position).doctor_id,"Call For Care");
                        }else {
                            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                        }
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });
        if (checkInternetConnection.isConnectedToInternet()){
            patientNurses();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
    }

    ArrayList<NurseBean> nurseBeens,nurseBeensOrig;
    NurseAdapter  nurseAdapter;
    public void patientNurses() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));

        client.post(DATA.baseUrl+"patientNurses", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    System.out.println("--reaponce in patientNurses: "+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray data = jsonObject.getJSONArray("data");
                        nurseBeens = new ArrayList<NurseBean>();
                        nurseBeensOrig = new ArrayList<NurseBean>();
                        NurseBean bean;
                        for (int i = 0; i < data.length() ; i++) {
                            String id = data.getJSONObject(i).getString("id");
                            String my_id = data.getJSONObject(i).getString("my_id");
                            String doctor_id = data.getJSONObject(i).getString("doctor_id");
                            String patient_id = data.getJSONObject(i).getString("patient_id");
                            String patient_category = data.getJSONObject(i).getString("patient_category");
                            String first_name = data.getJSONObject(i).getString("first_name");
                            String last_name = data.getJSONObject(i).getString("last_name");
                            String doctor_category = data.getJSONObject(i).getString("doctor_category");
                            String image = data.getJSONObject(i).getString("image");
                            String is_online = data.getJSONObject(i).getString("is_online");

                            bean = new NurseBean(id,my_id,doctor_id,patient_id,patient_category,first_name,last_name,doctor_category,image,is_online);
                            if (doctor_category.equalsIgnoreCase("Nurse")) {
                                nurseBeens.add(bean);
                            }
                            nurseBeensOrig.add(bean);
                            bean = null;
                        }
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        nurseAdapter = new NurseAdapter(activity,nurseBeens);
                        lvNurses.setAdapter(nurseAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: patientNurses, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail patientNurses: " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }

    public void sendmessagetoDoctor(String doctor_id,String text) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));
        params.put("from", "patient");
        params.put("to", "specialist");
        params.put("doctor_id", doctor_id);
        params.put("message_text", text);

        client.post(DATA.baseUrl+"sendmessagetoDoctor", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    System.out.println("--reaponce in sendmessagetoDoctor: "+content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("success")){
                            customToast.showToast("Your message has been sent",0,1);
                        }else {
                            customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: sendmessagetoDoctor, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail sendmessagetoDoctor: " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }


    public void sendmessagetoDoctor1(String doctor_id,String text) {

        //DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));
        params.put("from", "patient");
        params.put("to", "doctor");
        params.put("doctor_id", doctor_id);
        params.put("message_text", text);

        client.post(DATA.baseUrl+"sendmessagetoDoctor", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                // DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    System.out.println("--reaponce in sendmessagetoDoctor: "+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("success")){
                            customToast.showToast("Your message has been sent",0,1);
                        }else {
                            customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: sendmessagetoDoctor, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail sendmessagetoDoctor: " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabNurse:
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurses.setAdapter(nurseAdapter);
                }

                break;

            case R.id.tvTabNursePractitioner:
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurses.setAdapter(nurseAdapter);
                }
                break;
            case R.id.tvTabSocialWorker:
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurses.setAdapter(nurseAdapter);
                }
                break;
            case R.id.tvTabDietitian:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurses.setAdapter(nurseAdapter);
                }
                break;

            case R.id.tvTabOT:
                tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red));

                if (nurseBeensOrig !=null){
                    nurseBeens = new ArrayList<>();
                    for (int i = 0; i<nurseBeensOrig.size();i++){
                        if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                            nurseBeens.add(nurseBeensOrig.get(i));
                        }
                    }
                    nurseAdapter = new NurseAdapter(activity,nurseBeens);
                    lvNurses.setAdapter(nurseAdapter);
                }
                break;
            default:
                break;
        }
    }
}
