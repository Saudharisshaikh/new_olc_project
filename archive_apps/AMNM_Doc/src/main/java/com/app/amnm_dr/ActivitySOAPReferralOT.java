package com.app.amnm_dr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.amnm_dr.adapter.DmeRefAdapter;
import com.app.amnm_dr.api.ApiCallBack;
import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.model.SoapReferralBean;
import com.app.amnm_dr.util.CheckInternetConnection;
import com.app.amnm_dr.util.CustomToast;
import com.app.amnm_dr.util.DATA;
import com.app.amnm_dr.util.Database;
import com.app.amnm_dr.util.HideShowKeypad;
import com.app.amnm_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySOAPReferralOT extends BaseActivity implements ApiCallBack{

    //note: this activity shows only dme referrals requeted by OT/PT in the OT Notes
    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    //Button btnDmeReff,btnSkilledN,btnHomeCareRef;
    ListView lvSoapReferrals;

    //static int dmeOrSkilled = 0;// 0= DME, 1= Skilled nursing,  2 = homecare ref

    @Override
    protected void onResume() {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.PENDING_OT_REFERRALS,"post",params,apiCallBack, activity);
        apiManager.loadURL();

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soapreferral);

        activity = ActivitySOAPReferralOT.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_SERVICE_REFERRAL_REQ);

        findViewById(R.id.tabOptions).setVisibility(View.GONE);//tab bar options not required

        lvSoapReferrals = (ListView) findViewById(R.id.lvSoapReferrals);

        /*btnDmeReff = (Button) findViewById(R.id.btnDmeRef);
        btnSkilledN = (Button) findViewById(R.id.btnSkilledN);
        btnHomeCareRef = (Button) findViewById(R.id.btnHomeCareRef);

        btnDmeReff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setupTabs(0);

                if(soapReferralBeansOrig != null){
                    soapReferralBeen = new ArrayList<>();
                    for (int i = 0; i < soapReferralBeansOrig.size(); i++) {
                        if(!soapReferralBeansOrig.get(i).dme_referral.isEmpty()){
                            soapReferralBeen.add(soapReferralBeansOrig.get(i));
                        }
                    }
                    lvSoapReferrals.setAdapter(new DmeRefAdapter(activity,soapReferralBeen));
                }
            }
        });
        btnSkilledN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setupTabs(1);
                if(soapReferralBeansOrig != null){
                    soapReferralBeen = new ArrayList<>();
                    for (int i = 0; i < soapReferralBeansOrig.size(); i++) {
                        if(!soapReferralBeansOrig.get(i).skilled_nursing.isEmpty()){
                            soapReferralBeen.add(soapReferralBeansOrig.get(i));
                        }
                    }
                    lvSoapReferrals.setAdapter(new ViewSkilledNursingAdapter(activity,soapReferralBeen));
                }
            }
        });

        btnHomeCareRef.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                setupTabs(2);
                if(soapReferralBeansOrig != null){
                    soapReferralBeen = new ArrayList<>();
                    for (int i = 0; i < soapReferralBeansOrig.size(); i++) {
                        if(!soapReferralBeansOrig.get(i).homecare_referral.isEmpty()){
                            soapReferralBeen.add(soapReferralBeansOrig.get(i));
                        }
                    }
                    lvSoapReferrals.setAdapter(new HomecareAdapter(activity,soapReferralBeen));//change to homecareadapter
                }
            }
        });*/


        lvSoapReferrals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivitySOAPReferral.selectedSoapReferralBean = soapReferralBeen.get(position);
                Intent intent = new Intent(activity,ActivityDmeRefferalApprove.class);
                intent.putExtra("isForOTApproval",true);
                startActivity(intent);

                /*if(dmeOrSkilled == 0){
                    openActivity.open(ActivityDmeRefferalApprove.class,false);
                }else if(dmeOrSkilled == 1){
                    openActivity.open(ActivitySkilledNursingApprove.class,false);
                }else if(dmeOrSkilled == 2){
                    openActivity.open(ActivityHomeCareFormApprove.class,false);
                }*/
            }
        });


    }

    ArrayList<SoapReferralBean> soapReferralBeen,soapReferralBeansOrig;
    //public static SoapReferralBean selectedSoapReferralBean;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.PENDING_OT_REFERRALS)){
            try {
                //soapReferralBeen = new ArrayList<>();
                soapReferralBeansOrig = new ArrayList<>();
                SoapReferralBean bean;
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String notes_date = data.getJSONObject(i).getString("notes_date");
                    String dme_referral = data.getJSONObject(i).getString("dme_referral");
                    String skilled_nursing = "";//data.getJSONObject(i).getString("skilled_nursing");
                    String dme_status = data.getJSONObject(i).getString("dme_status");
                    String skilled_nursing_status = "";//data.getJSONObject(i).getString("skilled_nursing_status");
                    String first_name = data.getJSONObject(i).getString("first_name");
                    String last_name = data.getJSONObject(i).getString("last_name");

                    String homecare_referral = "";//data.getJSONObject(i).getString("homecare_referral");
                    String homecare_status = "";//data.getJSONObject(i).getString("homecare_status");

                    String patient_firstname = data.getJSONObject(i).getString("patient_firstname");
                    String patient_lastname = data.getJSONObject(i).getString("patient_lastname");
                    String patient_birthdate = data.getJSONObject(i).getString("patient_birthdate");
                    String patient_address = data.getJSONObject(i).getString("patient_address");
                    String patient_zipcode = data.getJSONObject(i).getString("patient_zipcode");
                    String patient_phone = data.getJSONObject(i).getString("patient_phone");

                    bean = new SoapReferralBean(id,notes_date,dme_referral,skilled_nursing,dme_status,skilled_nursing_status,
                            first_name,last_name,homecare_referral,homecare_status,
                            patient_firstname,patient_lastname,patient_birthdate,patient_address,patient_zipcode,patient_phone);
                    //soapReferralBeen.add(bean);
                    soapReferralBeansOrig.add(bean);
                    bean = null;
                }
                soapReferralBeen = new ArrayList<>();
                for (int i = 0; i < soapReferralBeansOrig.size(); i++) {
                    if(!soapReferralBeansOrig.get(i).dme_referral.isEmpty()){
                        soapReferralBeen.add(soapReferralBeansOrig.get(i));
                    }
                }
                lvSoapReferrals.setAdapter(new DmeRefAdapter(activity,soapReferralBeen));

                //setupTabs(0);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }


    /*public void setupTabs(int position){
        switch (position){
            case 0:
                dmeOrSkilled = 0;
                btnDmeReff.setBackgroundColor(getResources().getColor(R.color.theme_red));
                btnDmeReff.setTextColor(getResources().getColor(android.R.color.white));
                btnSkilledN.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnSkilledN.setTextColor(getResources().getColor(R.color.theme_red));
                btnHomeCareRef.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnHomeCareRef.setTextColor(getResources().getColor(R.color.theme_red));
                break;
            case 1:
                dmeOrSkilled = 1;
                btnDmeReff.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnDmeReff.setTextColor(getResources().getColor(R.color.theme_red));
                btnSkilledN.setBackgroundColor(getResources().getColor(R.color.theme_red));
                btnSkilledN.setTextColor(getResources().getColor(android.R.color.white));
                btnHomeCareRef.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnHomeCareRef.setTextColor(getResources().getColor(R.color.theme_red));
                break;
            case 2:
                dmeOrSkilled = 2;
                btnDmeReff.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnDmeReff.setTextColor(getResources().getColor(R.color.theme_red));

                btnHomeCareRef.setBackgroundColor(getResources().getColor(R.color.theme_red));
                btnHomeCareRef.setTextColor(getResources().getColor(android.R.color.white));

                btnSkilledN.setBackgroundColor(getResources().getColor(android.R.color.white));
                btnSkilledN.setTextColor(getResources().getColor(R.color.theme_red));
                break;
            default:
                break;
        }
    }*/
}
