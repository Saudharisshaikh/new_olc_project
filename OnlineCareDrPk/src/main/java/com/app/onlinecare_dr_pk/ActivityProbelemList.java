package com.app.onlinecare_dr_pk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.onlinecare_dr_pk.adapter.ProblemsAdapter;
import com.app.onlinecare_dr_pk.api.ApiManager;
import com.app.onlinecare_dr_pk.model.ProblemBean;
import com.app.onlinecare_dr_pk.util.CheckInternetConnection;
import com.app.onlinecare_dr_pk.util.CustomToast;
import com.app.onlinecare_dr_pk.util.DATA;
import com.app.onlinecare_dr_pk.util.GloabalMethods;
import com.app.onlinecare_dr_pk.util.HideShowKeypad;
import com.app.onlinecare_dr_pk.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityProbelemList extends BaseActivity {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvProbelems;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probelem_list);

        activity = ActivityProbelemList.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        lvProbelems = (ListView) findViewById(R.id.lvProbelems);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        if (checkInternetConnection.isConnectedToInternet()){
            getPatientsProblemsList();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
    }


    public void getPatientsProblemsList() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();

        params.put("patient_id", DATA.selectedUserCallId);

        String reqURL = DATA.baseUrl+"getPatientsProblemsList";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in getPatientsProblemsList: "+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data.length() == 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            tvNoData.setVisibility(View.GONE);
                        }
                        ArrayList<ProblemBean> problemBeens = new ArrayList<ProblemBean>();
                        ProblemBean bean;
                        for (int i = 0; i < data.length(); i++) {
                            String dateof = data.getJSONObject(i).getString("dateof");
                            String symptom_name = data.getJSONObject(i).getString("symptom_name");
                            String condition_name = data.getJSONObject(i).getString("condition_name");

                            bean = new ProblemBean(dateof,symptom_name,condition_name);
                            problemBeens.add(bean);
                            bean = null;
                        }
                        lvProbelems.setAdapter(new ProblemsAdapter(activity,problemBeens));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
}
