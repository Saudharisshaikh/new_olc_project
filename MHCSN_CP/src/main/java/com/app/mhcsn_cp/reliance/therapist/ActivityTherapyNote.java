package com.app.mhcsn_cp.reliance.therapist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mhcsn_cp.ActivityTcmDetails;
import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.careplan.IcdCodeBean;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.ExpandableHeightListView;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityTherapyNote extends BaseActivity {



    TextView tvStartEndTime,tvPatientName,tvPatientDOB,tvPatientAddress,tvPatientInsurance,tvMedicalHistory;
    Spinner spGoal;//,spDiagnosis;
    //TagView tag_group_diagnosis;
    ExpandableHeightListView lvAddedDiagnosis;
    EditText etNewGoal,etSummary,etEduInst;
    Button btnSwitchGoalInput,btnDiagAdd,btnDiagSelectAllApply,btnTherapist,btnPsychiatrist,btnSubmit,btnNotNow,btnNextApptmnt;

    String strtTime = "", endTime = "";//if not come from video call

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_therapy_note);

        strtTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Therapy Note");
        }

        tvStartEndTime = findViewById(R.id.tvStartEndTime);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientDOB = findViewById(R.id.tvPatientDOB);
        tvPatientAddress = findViewById(R.id.tvPatientAddress);
        tvPatientInsurance = findViewById(R.id.tvPatientInsurance);
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
        spGoal = findViewById(R.id.spGoal);
        //spDiagnosis = findViewById(R.id.spDiagnosis);
        //tag_group_diagnosis = findViewById(R.id.tag_group_diagnosis);
        lvAddedDiagnosis = findViewById(R.id.lvAddedDiagnosis);
        btnDiagAdd = findViewById(R.id.btnDiagAdd);
        etNewGoal = findViewById(R.id.etNewGoal);
        etSummary = findViewById(R.id.etSummary);
        etEduInst = findViewById(R.id.etEduInst);
        btnSwitchGoalInput = findViewById(R.id.btnSwitchGoalInput);
        btnDiagSelectAllApply = findViewById(R.id.btnDiagSelectAllApply);
        btnTherapist = findViewById(R.id.btnTherapist);
        btnPsychiatrist = findViewById(R.id.btnPsychiatrist);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNotNow = findViewById(R.id.btnNotNow);
        btnNextApptmnt = findViewById(R.id.btnNextApptmnt);


        String todayDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        String callTimeStr = "Start : "+todayDate+ " "+DATA.call_start_time+" | End : "+todayDate+" "+DATA.call_end_time;
        tvStartEndTime.setText(callTimeStr);

        tvPatientName.setText(DATA.selectedUserCallName);//ActivityTcmDetails.ptFname+" "+ActivityTcmDetails.ptLname
        tvPatientDOB.setText(ActivityTcmDetails.ptDOB);
        tvPatientInsurance.setText(ActivityTcmDetails.ptInsurancePayerName);
        tvMedicalHistory.setText(ActivityTcmDetails.phistory);

        StringBuilder sbAddress = new StringBuilder();
        if(!TextUtils.isEmpty(ActivityTcmDetails.ptAddress)){
            sbAddress.append(ActivityTcmDetails.ptAddress);
            sbAddress.append(", ");
        }
        if(!TextUtils.isEmpty(ActivityTcmDetails.pt_city)){
            sbAddress.append(ActivityTcmDetails.pt_city);
            sbAddress.append(", ");
        }
        if(!TextUtils.isEmpty(ActivityTcmDetails.pt_state)){
            sbAddress.append(ActivityTcmDetails.pt_state);
            sbAddress.append(", ");
        }
        if(!TextUtils.isEmpty(ActivityTcmDetails.ptZipcode)){
            sbAddress.append(ActivityTcmDetails.ptZipcode);
        }
        tvPatientAddress.setText(sbAddress.toString());


        icdCodeBeansToSubmit.clear();
        expListDiagnosisAdapter = new ExpListDiagnosisAdapter(activity,icdCodeBeansToSubmit);
        lvAddedDiagnosis.setAdapter(expListDiagnosisAdapter);
        lvAddedDiagnosis.setExpanded(true);

        /*spDiagnosis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0){

                    IcdCodeBean icdCodeBean = icdCodeBeans.get(position);
                    icdCodeBeansToSubmit.add(icdCodeBean);
                    icdCodeBeans.remove(icdCodeBean);

                    Tag tag = new Tag(icdCodeBean.code);
                    tag.isDeletable = true;
                    tag_group_diagnosis.addTag(tag);

                    spDiagAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //set delete listener
        tag_group_diagnosis.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                tag_group_diagnosis.remove(position);
                try {
                    IcdCodeBean icdCodeBean = icdCodeBeansToSubmit.get(position);
                    icdCodeBeans.add(icdCodeBean);
                    icdCodeBeansToSubmit.remove(icdCodeBean);
                    spDiagAdapter.notifyDataSetChanged();
                }catch (Exception e){e.printStackTrace();}
            }
        });*/

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnSwitchGoalInput:
                        if(spGoal.getVisibility() == View.VISIBLE){
                            btnSwitchGoalInput.setText("Show List");
                            spGoal.setVisibility(View.GONE);
                            etNewGoal.setVisibility(View.VISIBLE);
                        }else {
                            btnSwitchGoalInput.setText("Add New");
                            spGoal.setVisibility(View.VISIBLE);
                            etNewGoal.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.btnDiagSelectAllApply:

                       /* try {
                            icdCodeBeans.remove(0);
                            for (IcdCodeBean icdCodeBean : icdCodeBeans) {
                                Tag tag = new Tag(icdCodeBean.code);
                                tag.isDeletable = true;
                                tag_group_diagnosis.addTag(tag);
                            }
                            icdCodeBeansToSubmit.addAll(icdCodeBeans);
                            icdCodeBeans.clear();
                            icdCodeBeans.add(0, new IcdCodeBean("0", "", "Select Diagnosis"));
                            spDiagAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/

                        if(icdCodeBeans != null){
                            icdCodeBeansToSubmit.clear();
                            icdCodeBeansToSubmit.addAll(icdCodeBeans);
                            expListDiagnosisAdapter.notifyDataSetChanged();
                        }

                        break;
                    case R.id.btnDiagAdd:
                        showDiagnosisDialog();
                        break;
                    case R.id.btnTherapist:
                        getTherapist();
                        break;
                    case R.id.btnPsychiatrist:
                        getPsyciatrist();
                        break;
                    case R.id.btnSubmit:
                        submitForm();
                        break;
                    case R.id.btnNotNow:
                        finish();
                        break;
                    case R.id.btnNextApptmnt:
                        openActivity.open(ActivityDoctorSlots.class, false);
                        break;

                }
            }
        };
        btnSwitchGoalInput.setOnClickListener(onClickListener);
        btnDiagSelectAllApply.setOnClickListener(onClickListener);
        btnTherapist.setOnClickListener(onClickListener);
        btnPsychiatrist.setOnClickListener(onClickListener);
        btnSubmit.setOnClickListener(onClickListener);
        btnNotNow.setOnClickListener(onClickListener);
        btnDiagAdd.setOnClickListener(onClickListener);
        btnNextApptmnt.setOnClickListener(onClickListener);


        getGoals();

        getDiagnosis();

    }


    private void getGoals(){
        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GETGOALS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
    private void getDiagnosis(){
        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GETDIAG,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    private void submitForm(){


        String new_goal = etNewGoal.getText().toString().trim();
        String summary = etSummary.getText().toString().trim();
        String education = etEduInst.getText().toString().trim();
        String goal = "";
        try {
            if(therapyGoalBeans != null){
                goal = therapyGoalBeans.get(spGoal.getSelectedItemPosition()).id;
            }
        }catch (Exception e){e.printStackTrace();}
        StringBuilder diagnosis = new StringBuilder();
        if(icdCodeBeansToSubmit != null){
            for (int i = 0; i < icdCodeBeansToSubmit.size(); i++) {
                diagnosis.append(icdCodeBeansToSubmit.get(i).code);
                if(i < (icdCodeBeansToSubmit.size()-1)){
                    diagnosis.append(",");
                }
            }
        }

        endTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);
        if(!TextUtils.isEmpty(DATA.call_start_time)){
            params.put("start_time", DATA.call_start_time);
        }else {
            params.put("start_time", strtTime);
        }
        if(!TextUtils.isEmpty(DATA.call_end_time)){
            params.put("end_time", DATA.call_end_time);
        }else {
            params.put("end_time", endTime);
        }
        params.put("summary", summary);
        params.put("education", education);
        if(spGoal.getVisibility() == View.VISIBLE){
            params.put("goal", goal);
        }
        if(etNewGoal.getVisibility() == View.VISIBLE){
            params.put("new_goal", new_goal);
        }
        params.put("diagnosis", diagnosis.toString());

        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_SAVE_THERAPY_NOTE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    private void getTherapist(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_THERAPIST_DOC,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    private void getPsyciatrist(){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_PSYCHIATIST_DOC, "post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    protected void referPatient(String toDocId){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("from_id", prefs.getString("id", ""));
        params.put("to_id", toDocId);
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_REFFRED_PATIENT, "post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<IcdCodeBean> icdCodeBeans, icdCodeBeansToSubmit = new ArrayList<>();
    //ArrayAdapter<IcdCodeBean> spDiagAdapter;
    LvDiagnosisAdapter lvDiagnosisAdapter;
    ExpListDiagnosisAdapter expListDiagnosisAdapter;

    ArrayList<TherapyGoalBean> therapyGoalBeans;
    ArrayAdapter<TherapyGoalBean> spGoalAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);

        if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GETDIAG)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                icdCodeBeans = gson.fromJson(data.toString(), new TypeToken<ArrayList<IcdCodeBean>>() {}.getType());
                if(icdCodeBeans != null && lvDiagnosis != null){
                    /*icdCodeBeans.add(0, new IcdCodeBean("0", "", "Select Diagnosis"));
                    spDiagAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, icdCodeBeans);
                    spDiagnosis.setAdapter(spDiagAdapter);*/

                    lvDiagnosisAdapter = new LvDiagnosisAdapter(activity, icdCodeBeans);
                    lvDiagnosis.setAdapter(lvDiagnosisAdapter);

                    if(srDiag != null){ srDiag.setRefreshing(false);}
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GETGOALS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                therapyGoalBeans = gson.fromJson(data.toString(), new TypeToken<ArrayList<TherapyGoalBean>>() {}.getType());
                if(therapyGoalBeans != null){
                    spGoalAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, therapyGoalBeans);
                    spGoal.setAdapter(spGoalAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_SAVE_THERAPY_NOTE)){
            //{"status":"success","message":"Note has been saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Done",null)
                        .create();
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            DATA.isSOAP_NotesSent = true;
                            ActivityTherapyNoteList.shoulRefresh = true;
                            finish();
                        }
                    });
                }
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPIST_DOC) || apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_PSYCHIATIST_DOC)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray doctors = jsonObject.getJSONArray("doctors");
                doctorBeans = gson.fromJson(doctors.toString(), new TypeToken<ArrayList<DoctorBean>>() {}.getType());
                if(doctorBeans != null){
                    String title = apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPIST_DOC) ? "Select a Therapist" : "Select a Psychiatrist";
                    showTherapistDialog(title);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_REFFRED_PATIENT)){
            //{"status":"success","message":"Patient Sucessfully refered."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("message"))
                        .setPositiveButton("Done",null)
                        .create();
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(dialogTherapistDis != null){
                                dialogTherapistDis.dismiss();
                            }
                        }
                    });
                }
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    Dialog dialogTherapistDis;
    ArrayList<DoctorBean> doctorBeans;
    LvTherapistAdapter lvTherapistAdapter;
    public void showTherapistDialog(String tittle){
        Dialog dialogTherapist = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogTherapist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogTherapist.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogTherapist.setContentView(R.layout.dialog_therapist);

        ImageView ivCancel = (ImageView) dialogTherapist.findViewById(R.id.ivCancel);
        Button btnDone = (Button) dialogTherapist.findViewById(R.id.btnDone);

        TextView tvTittle =  dialogTherapist.findViewById(R.id.tvTittle);
        EditText etSearchDoc = dialogTherapist.findViewById(R.id.etSearchDoc);
        ListView lvTherapist = dialogTherapist.findViewById(R.id.lvTherapist);
        TextView tvNoProvider = dialogTherapist.findViewById(R.id.tvNoProvider);

        tvTittle.setText(tittle);

        lvTherapistAdapter = new LvTherapistAdapter(activity, doctorBeans);
        lvTherapist.setAdapter(lvTherapistAdapter);

        int vis = doctorBeans.isEmpty() ? View.VISIBLE:View.GONE;
        tvNoProvider.setVisibility(vis);

        etSearchDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvTherapistAdapter != null){
                    lvTherapistAdapter.filter(s.toString());
                }
            }
        });

        btnDone.setOnClickListener(v -> {dialogTherapist.dismiss();});
        ivCancel.setOnClickListener(v -> dialogTherapist.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTherapist.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogTherapist.setCanceledOnTouchOutside(false);
        dialogTherapist.show();
        dialogTherapist.getWindow().setAttributes(lp);

        dialogTherapistDis= dialogTherapist;
    }




    ListView lvDiagnosis;
    SwipeRefreshLayout srDiag;
    public void showDiagnosisDialog(){
        final Dialog dialogDiagICDCodes= new Dialog(activity);
        dialogDiagICDCodes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDiagICDCodes.setContentView(R.layout.dialog_diag_icdcodes);
        dialogDiagICDCodes.setCanceledOnTouchOutside(false);

        EditText etSerchDiagnosis = dialogDiagICDCodes.findViewById(R.id.etSerchDiagnosis);
        lvDiagnosis = dialogDiagICDCodes.findViewById(R.id.lvDiagnosis);
        srDiag = dialogDiagICDCodes.findViewById(R.id.srDiag);
        Button btnAddDiag = dialogDiagICDCodes.findViewById(R.id.btnAddDiag);

        dialogDiagICDCodes.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDiagICDCodes.dismiss();
            }
        });

        btnAddDiag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = lvDiagnosis.getCheckedItemPositions();
                DATA.print("--checked: "+checked);
                DATA.print("--checked size: "+checked.size());
                int c = 0;
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position1 = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)){
                        DATA.print("--pos checked "+position1);
                        c++;

                        try {
                            IcdCodeBean icdCodeBean = icdCodeBeans.get(position1);
                            boolean added = false;
                            for (int j = 0; j < icdCodeBeansToSubmit.size(); j++) {
                                if(icdCodeBeansToSubmit.get(j).code.equalsIgnoreCase(icdCodeBean.code)){
                                    DATA.print("-- code already added : "+icdCodeBean.code);
                                    added = true;
                                    break;
                                }
                            }
                            if(!added){
                                icdCodeBeansToSubmit.add(icdCodeBean);
                                expListDiagnosisAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                /*if (c == 0) {
                    menu.collapse();
                    menu.setVisibility(View.GONE);
                } else {
                    ShowAyatOfSurah.menu.setVisibility(View.VISIBLE);
                    ShowAyatOfSurah.menu.expand();
                }*/

                dialogDiagICDCodes.dismiss();
            }
        });


        lvDiagnosis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SparseBooleanArray checked = lvDiagnosis.getCheckedItemPositions();
                DATA.print("--checked: "+checked);
                DATA.print("--checked size: "+checked.size());
                int c = 0;
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position1 = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)){
                        DATA.print("--pos checked "+position1);
                        c++;
                    }
                }
                if (c > 0) {
                    btnAddDiag.setText("Add Diagnosis ("+c+")");
                    btnAddDiag.setBackgroundResource(R.drawable.btn_green);
                } else {
                    btnAddDiag.setText("Done");
                    btnAddDiag.setBackgroundResource(R.drawable.btn_selector);
                }


                /*etCountryInput.setError(null);
                etCountryInput.setText(countryBeans.get(position).getName());
                new HideShowKeypad(activity).hidekeyboardOnDialog();
                dialogDiagICDCodes.dismiss();*/
            }
        });

        etSerchDiagnosis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvDiagnosisAdapter != null){
                    lvDiagnosisAdapter.filter(s.toString());
                }
            }
        });

        //dialogDiagICDCodes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogDiagICDCodes.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogDiagICDCodes.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);

        dialogDiagICDCodes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogDiagICDCodes.show();
        dialogDiagICDCodes.getWindow().setAttributes(lp);


        if(icdCodeBeans != null){
            lvDiagnosisAdapter = new LvDiagnosisAdapter(activity, icdCodeBeans);
            lvDiagnosis.setAdapter(lvDiagnosisAdapter);
        }else {
            getDiagnosis();
        }


        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        srDiag.setColorSchemeColors(colorsArr);
        srDiag.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!checkInternetConnection.isConnectedToInternet()){
                            srDiag.setRefreshing(false);
                        }else {}
                        ApiManager.shouldShowPD = false;
                        getDiagnosis();
                    }
                }
        );
    }
}
