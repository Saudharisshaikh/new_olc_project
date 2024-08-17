package com.app.amnm_ma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import com.app.amnm_ma.adapter.BillingAdapter;
import com.app.amnm_ma.api.ApiManager;
import com.app.amnm_ma.model.BillingBean;
import com.app.amnm_ma.util.CheckInternetConnection;
import com.app.amnm_ma.util.CustomToast;
import com.app.amnm_ma.util.DATA;
import com.app.amnm_ma.util.GloabalMethods;
import com.app.amnm_ma.util.HideShowKeypad;
import com.app.amnm_ma.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityBilling extends AppCompatActivity {


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        activity = ActivityBilling.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        lvBilling = (ListView) findViewById(R.id.lvBilling);

        if (checkInternetConnection.isConnectedToInternet()){
            nursePendingNotes();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
    }

    ArrayList<BillingBean> billingBeens;
    public void nursePendingNotes() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));

        client.post(DATA.baseUrl+"nursePendingNotes", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in nursePendingNotes: "+content);

                    try {
                        billingBeens = new ArrayList<BillingBean>();
                        BillingBean bean;
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i<data.length(); i++){
                            String id = data.getJSONObject(i).getString("id");
                            String assesment = data.getJSONObject(i).getJSONObject("notes").getString("assesment");
                            String family = data.getJSONObject(i).getJSONObject("notes").getString("family");
                            String history = data.getJSONObject(i).getJSONObject("notes").getString("history");
                            //String objective = data.getJSONObject(i).getJSONObject("notes").getString("objective");
                            String objective = "";
                            String plan = data.getJSONObject(i).getJSONObject("notes").getString("plan");
                            String subjective = data.getJSONObject(i).getJSONObject("notes").getString("subjective");
                            String notes_date = data.getJSONObject(i).getString("notes_date");
                            String laid = data.getJSONObject(i).getString("laid");
                            String treatment_codes = data.getJSONObject(i).getString("treatment_codes");
                            String author_by = data.getJSONObject(i).getString("author_by");
                            String is_approved = data.getJSONObject(i).getString("is_approved");
                            String patient_name = data.getJSONObject(i).getString("patient_name");
                            String doctor_name = data.getJSONObject(i).getString("doctor_name");
                            String care_plan = "";
                            if(data.getJSONObject(i).getJSONObject("notes").has("care_plan")){
                                care_plan = data.getJSONObject(i).getJSONObject("notes").getString("care_plan");
                            }

                            bean = new BillingBean(id,assesment,family,history,objective,plan,subjective,notes_date,laid,
                                    treatment_codes,author_by,is_approved,patient_name,doctor_name,care_plan);
                            billingBeens.add(bean);
                            bean = null;
                        }
                        BillingAdapter billingAdapter = new BillingAdapter(activity,billingBeens);
                        lvBilling.setAdapter(billingAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: nursePendingNotes, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- onfailure nursePendingNotes : "+content);
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
