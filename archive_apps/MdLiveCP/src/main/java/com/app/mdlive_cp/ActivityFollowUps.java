package com.app.mdlive_cp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.FollowupAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.FollowupBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment2;
import com.app.mdlive_cp.util.HideShowKeypad;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityFollowUps extends BaseActivity implements View.OnClickListener,ApiCallBack{

    //this activity is for create followups
    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ListView lvFollowUps;
    TextView tvNoData;
    TextView tvTabTCM,tvTabCC,tvTabHomeHealth,tvTabNursingHome;

    public static String followUpDateTime = "";
    public static FollowupBean selectedFollowupBean;

    LinearLayout layTabs;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_ups);

        activity = ActivityFollowUps.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        lvFollowUps = (ListView) findViewById(R.id.lvFollowUps);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        tvTabTCM = (TextView) findViewById(R.id.tvTabTCM);
        tvTabCC = (TextView) findViewById(R.id.tvTabCC);
        tvTabHomeHealth = (TextView) findViewById(R.id.tvTabHomeHealth);
        tvTabNursingHome = (TextView) findViewById(R.id.tvTabNursingHome);

        layTabs = findViewById(R.id.layTabs);
        etSearch = findViewById(R.id.etSearch);

        layTabs.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);

        tvTabTCM.setOnClickListener(this);
        tvTabCC.setOnClickListener(this);
        tvTabHomeHealth.setOnClickListener(this);
        tvTabNursingHome.setOnClickListener(this);

        if (checkInternetConnection.isConnectedToInternet()) {
            followup_patients();
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }

        lvFollowUps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedFollowupBean = followupBeens.get(position);

                DialogFragment newFragment = new DatePickerFragment2(activity);
                newFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(followupAdapter != null){
                    followupAdapter.filter(s.toString());
                }
            }
        });
    }


    ArrayList<FollowupBean> followupBeens,followupBeensOrig;
    FollowupAdapter followupAdapter;
    public void followup_patients() {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));

        ApiManager apiManager = new ApiManager(ApiManager.FOLLOWUP_PATIENTS,"post",params,this, activity);
        apiManager.loadURL();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTabTCM:
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("tcm")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabCC:
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("complex_care")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabHomeHealth:
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("home_health")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
            case R.id.tvTabNursingHome:
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("nursing_home")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void setUpFollwUpDialog(String msg){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Confirm");
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkInternetConnection.isConnectedToInternet()) {
                    createfollowup();
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                }
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();*/
        final Dialog confirmDialog = new Dialog(activity);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(R.layout.dialog_followup_reason);

        TextView tvFollowup  = (TextView) confirmDialog.findViewById(R.id.tvFollowup);
        final EditText etReason  = (EditText) confirmDialog.findViewById(R.id.etReason);
        TextView tvFollowupCancel  = (TextView) confirmDialog.findViewById(R.id.tvFollowupCancel);
        TextView tvFollowupDone  = (TextView) confirmDialog.findViewById(R.id.tvFollowupDone);

        tvFollowup.setText(msg);


        tvFollowupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = etReason.getText().toString();
                if(reason.isEmpty()){
                    etReason.setError("Please enter an appointment reason");
                    return;
                }

                confirmDialog.dismiss();
                createfollowup(reason);
            }
        });
        tvFollowupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        confirmDialog.show();
    }

    public void createfollowup(String reason) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("patient_id", selectedFollowupBean.patient_id);
        params.put("dateof",followUpDateTime);
        params.put("patient_category",selectedFollowupBean.patient_category);
        params.put("reason",reason);

        if(!selectedFollowupBean.followup_id.isEmpty()){
            params.put("followup_id",selectedFollowupBean.followup_id);
        }

        ApiManager apiManager = new ApiManager(ApiManager.CREATE_FOLLOWUP,"post",params,this, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.FOLLOWUP_PATIENTS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                followupBeensOrig = new ArrayList<FollowupBean>();
                FollowupBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String first_name = data.getJSONObject(i).getString("first_name");
                    String last_name = data.getJSONObject(i).getString("last_name");
                    String image = data.getJSONObject(i).getString("image");
                    String patient_category = data.getJSONObject(i).getString("patient_category");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String doctor_nurses_id = data.getJSONObject(i).getString("doctor_nurses_id");
                    String followup_id = data.getJSONObject(i).getString("followup_id");
                    String dateof = data.getJSONObject(i).getString("dateof");
                    String is_online = data.getJSONObject(i).getString("is_online");

                    bean = new FollowupBean(first_name,last_name,image,patient_category,patient_id,doctor_nurses_id,followup_id,dateof,is_online);
                    followupBeensOrig.add(bean);
                }

                followupBeens = new ArrayList<FollowupBean>();
                for (FollowupBean temp : followupBeensOrig) {
                    if(temp.patient_category.equals("tcm")){
                        followupBeens.add(temp);
                    }
                }
                followupAdapter = new FollowupAdapter(activity,followupBeens);
                lvFollowUps.setAdapter(followupAdapter);

                if (followupBeens.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                tvTabTCM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tvTabCC.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabHomeHealth.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                tvTabNursingHome.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CREATE_FOLLOWUP)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equalsIgnoreCase("success")) {
                    selectedFollowupBean.followup_id = jsonObject.getString("followup_id");
                    followupAdapter.notifyDataSetChanged();
                    new AlertDialog.Builder(activity).setTitle("Info").
                            setMessage("You scheduled the follow up successfully").
                            setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (checkInternetConnection.isConnectedToInternet()) {
                                        followup_patients();
                                    }else {
                                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                                    }
                                }
                            }).show();
                }else {
                    customToast.showToast(message,0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
