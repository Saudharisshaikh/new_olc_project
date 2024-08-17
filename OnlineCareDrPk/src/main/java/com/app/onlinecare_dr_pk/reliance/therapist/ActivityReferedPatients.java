package com.app.onlinecare_dr_pk.reliance.therapist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.onlinecare_dr_pk.ActivityTcmDetails;
import com.app.onlinecare_dr_pk.BaseActivity;
import com.app.onlinecare_dr_pk.R;
import com.app.onlinecare_dr_pk.api.ApiManager;
import com.app.onlinecare_dr_pk.model.MyAppointmentsModel;
import com.app.onlinecare_dr_pk.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityReferedPatients extends BaseActivity {



    ListView lvReffPt, lvReffPt2, lvReffPt3;
    EditText etSearchPt,etSearchPt2,etSearchPt3;

    Button btnTabPending,btnTabApproved,btnTabDeclined;
    ViewFlipper vfRefredPatients;
    int selectedChild = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_refered_pts);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Referred Patients");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvReffPt = (ListView) findViewById(R.id.lvReffPt);
        etSearchPt = findViewById(R.id.etSearchPt);
        lvReffPt2 = (ListView) findViewById(R.id.lvReffPt2);
        etSearchPt2 = findViewById(R.id.etSearchPt2);
        lvReffPt3 = (ListView) findViewById(R.id.lvReffPt3);
        etSearchPt3 = findViewById(R.id.etSearchPt3);
        btnTabPending = findViewById(R.id.btnTabPending);
        btnTabApproved = findViewById(R.id.btnTabApproved);
        btnTabDeclined = findViewById(R.id.btnTabDeclined);
        vfRefredPatients = findViewById(R.id.vfRefredPatients);


        lvReffPt2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DATA.selectedUserQbid = "";
                DATA.selectedUserCallId = refferedPatientBeansApproved.get(position).patient_id;
                DATA.selectedUserCallName = refferedPatientBeansApproved.get(position).patient_name;
                DATA.selectedUserCallSympTom = "";
                DATA.selectedUserCallCondition = "";
                DATA.selectedUserCallDescription = "";
                DATA.selectedUserAppntID = "";

                DATA.selectedUserLatitude =  0;
                DATA.selectedUserLongitude =  0;

                DATA.selectedUserCallImage = refferedPatientBeansApproved.get(position).pimage;

                //filterReports(DATA.selectedUserCallId);
                DATA.allReportsFiltered = new ArrayList<>();

                DATA.isFromDocToDoc = false;

                DATA.selectedLiveCare = new MyAppointmentsModel();
                DATA.selectedLiveCare.id = refferedPatientBeansApproved.get(position).patient_id;
                DATA.selectedLiveCare.is_online = refferedPatientBeansApproved.get(position).is_online;
                DATA.selectedLiveCare.first_name = refferedPatientBeansApproved.get(position).patient_name;
                DATA.selectedLiveCare.last_name = "";//refferedPatientBeansApproved.get(position).last_name;
                DATA.selectedLiveCare.patient_phone = "-";//followupBeens.get(position).phone;

                Intent i = new Intent(activity, ActivityTcmDetails.class);
                startActivity(i);
                //activity.finish();
            }
        });

        etSearchPt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvReffPtAdapterPending != null){
                    lvReffPtAdapterPending.filter(s.toString());
                }
            }
        });
        etSearchPt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvReffPtAdapterApproved != null){
                    lvReffPtAdapterApproved.filter(s.toString());
                }
            }
        });
        etSearchPt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvReffPtAdapterDeclined != null){
                    lvReffPtAdapterDeclined.filter(s.toString());
                }
            }
        });



        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnTabPending:
                        btnTabPending.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabPending.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabApproved.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabApproved.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabDeclined.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabDeclined.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 0;
                        if (selectedChild > vfRefredPatients.getDisplayedChild()) {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_right);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_left);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfRefredPatients.getDisplayedChild() != selectedChild) {
                            vfRefredPatients.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnTabApproved:
                        btnTabApproved.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabApproved.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabPending.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabPending.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabDeclined.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabDeclined.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 1;
                        if (selectedChild > vfRefredPatients.getDisplayedChild()) {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_right);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_left);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfRefredPatients.getDisplayedChild() != selectedChild) {
                            vfRefredPatients.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnTabDeclined:
                        btnTabDeclined.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        btnTabDeclined.setTextColor(getResources().getColor(android.R.color.white));
                        btnTabApproved.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabApproved.setTextColor(getResources().getColor(R.color.theme_red));
                        btnTabPending.setBackgroundColor(getResources().getColor(android.R.color.white));
                        btnTabPending.setTextColor(getResources().getColor(R.color.theme_red));

                        selectedChild = 2;
                        if (selectedChild > vfRefredPatients.getDisplayedChild()) {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_right);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfRefredPatients.setInAnimation(activity, R.anim.in_left);
                            vfRefredPatients.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfRefredPatients.getDisplayedChild() != selectedChild) {
                            vfRefredPatients.setDisplayedChild(selectedChild);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        btnTabPending.setOnClickListener(onClickListener);
        btnTabApproved.setOnClickListener(onClickListener);
        btnTabDeclined.setOnClickListener(onClickListener);


        getReffredPatients("", true);
    }


    String statusRefer = "";
    private void getReffredPatients(String status, boolean showLoader){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        statusRefer = status;

        if(!TextUtils.isEmpty(status)){
            params.put("status",status);
        }
        ApiManager.shouldShowPD = showLoader;
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_REFFRED_PATIENTS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    protected void acceptRefered(String id){
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_APPROVE_REFERED,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
    protected void declineRefered(String id, String reason){
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("reason", reason);
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_DECLINE_REFERED,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<RefferedPatientBean> refferedPatientBeansPending = new ArrayList<>(), refferedPatientBeansApproved = new ArrayList<>(), refferedPatientBeansDeclined = new ArrayList<>();
    LvReffPtAdapter lvReffPtAdapterPending, lvReffPtAdapterApproved, lvReffPtAdapterDeclined;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_REFFRED_PATIENTS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                ArrayList<RefferedPatientBean> refferedPatientBeansAPI = gson.fromJson(data.toString(), new TypeToken<ArrayList<RefferedPatientBean>>() {}.getType());
                if(refferedPatientBeansAPI != null){
                    if(TextUtils.isEmpty(statusRefer)){
                        refferedPatientBeansPending.clear();
                        refferedPatientBeansPending.addAll(refferedPatientBeansAPI);
                        lvReffPtAdapterPending = new LvReffPtAdapter(activity, refferedPatientBeansPending);
                        lvReffPt.setAdapter(lvReffPtAdapterPending);

                        int vis = data.length() == 0 ? View.VISIBLE: View.GONE;
                        findViewById(R.id.tvNoData).setVisibility(vis);

                        getReffredPatients("Accept", false);

                    }else if(statusRefer.equalsIgnoreCase("Accept")){
                        refferedPatientBeansApproved.clear();
                        refferedPatientBeansApproved.addAll(refferedPatientBeansAPI);
                        lvReffPtAdapterApproved = new LvReffPtAdapter(activity, refferedPatientBeansApproved);
                        lvReffPt2.setAdapter(lvReffPtAdapterApproved);

                        int vis = data.length() == 0 ? View.VISIBLE: View.GONE;
                        findViewById(R.id.tvNoData2).setVisibility(vis);

                        getReffredPatients("Decline",false);

                    }else if(statusRefer.equalsIgnoreCase("Decline")){
                        refferedPatientBeansDeclined.clear();
                        refferedPatientBeansDeclined.addAll(refferedPatientBeansAPI);
                        lvReffPtAdapterDeclined = new LvReffPtAdapter(activity, refferedPatientBeansDeclined);
                        lvReffPt3.setAdapter(lvReffPtAdapterDeclined);

                        int vis = data.length() == 0 ? View.VISIBLE: View.GONE;
                        findViewById(R.id.tvNoData3).setVisibility(vis);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_APPROVE_REFERED) || apiName.equalsIgnoreCase(ApiManager.BHEALTH_DECLINE_REFERED)){
            //{"status":"success","message":"Patient Approved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Done",null)
                        .create();
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    if(dialogDecReasonD != null){
                        dialogDecReasonD.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getReffredPatients("", true);
                            /*if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_APPROVE_REFERED)){
                                getReffredPatients("Accept", true);
                            }else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_DECLINE_REFERED)){
                                getReffredPatients("Decline", true);
                            }*/
                        }
                    });
                }
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    Dialog dialogDecReasonD;
    protected void showDeclineReasonDialog(String reffId){
        Dialog dialogDecReason = new Dialog(activity);
        dialogDecReason.setCanceledOnTouchOutside(false);

        dialogDecReason.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDecReason.setContentView(R.layout.dialog_ref_decline_reason);


        EditText etReason  = (EditText) dialogDecReason.findViewById(R.id.etReason);
        TextView tvCancel  = (TextView) dialogDecReason.findViewById(R.id.tvCancel);
        TextView tvDone  = (TextView) dialogDecReason.findViewById(R.id.tvDone);
        ImageView ivClose = dialogDecReason.findViewById(R.id.ivClose);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivClose:
                        dialogDecReason.dismiss();
                        break;
                    case R.id.tvCancel:
                        dialogDecReason.dismiss();
                        break;
                    case R.id.tvDone:
                        String reason = etReason.getText().toString().trim();
                        if(TextUtils.isEmpty(reason)){
                            etReason.setError("");
                        }else {
                            declineRefered(reffId, reason);
                        }
                        break;
                }
            }
        };
        ivClose.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
        tvDone.setOnClickListener(onClickListener);

        dialogDecReason.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDecReason.show();

        dialogDecReasonD = dialogDecReason;
    }
}
