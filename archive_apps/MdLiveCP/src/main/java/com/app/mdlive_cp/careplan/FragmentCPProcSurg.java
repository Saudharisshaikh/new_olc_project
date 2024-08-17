package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPProcSurg extends Fragment implements ApiCallBack{

    //xml layout is same of medication fragment    === G.M

    Activity parentActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(parentActivity == null){
            parentActivity = getActivity();
        }
    }

    ListView lvCPMed;
    TextView tvNoData;
    Button btnAddProcSurg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = rootView.findViewById(R.id.lvCPMed);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        btnAddProcSurg = rootView.findViewById(R.id.btnAddMedication);

        btnAddProcSurg.setText("Add New Procedures and Surgeries");

        lvCPMed.setAdapter(new CP_ProceSurgAdapter(parentActivity,ActivityCarePlanDetail.cp_proceSurgBeans,FragmentCPProcSurg.this));

        if(ActivityCarePlanDetail.cp_proceSurgBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }

        btnAddProcSurg.setOnClickListener(view -> showAddProcDialog(null));


        return rootView;
    }


    Dialog dialogAddProc;
    public void showAddProcDialog(CP_ProceSurgBean cp_proceSurgBeanEdit){
        dialogAddProc = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddProc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddProc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddProc.setContentView(R.layout.dialog_add_proc_surg);

        ImageView ivCancel = dialogAddProc.findViewById(R.id.ivCancel);
        Button btnAddProcSurg = dialogAddProc.findViewById(R.id.btnAddProcSurg);

        EditText etHospital = dialogAddProc.findViewById(R.id.etHospital);
        EditText etProceduresSurgeries = dialogAddProc.findViewById(R.id.etProceduresSurgeries);
        EditText etDate = dialogAddProc.findViewById(R.id.etDate);

        etDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDate);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });

        if(cp_proceSurgBeanEdit != null){
            etHospital.setText(cp_proceSurgBeanEdit.hospital);
            etProceduresSurgeries.setText(cp_proceSurgBeanEdit.procedures_surgeries);
            etDate.setText(cp_proceSurgBeanEdit.date);
        }


        btnAddProcSurg.setOnClickListener(v -> {


            String hospital = etHospital.getText().toString();
            String procedures_surgeries = etProceduresSurgeries.getText().toString();
            String date = etDate.getText().toString();

            RequestParams params = new RequestParams();
            if(cp_proceSurgBeanEdit != null){
                params.put("id",cp_proceSurgBeanEdit.id);//this will made api as edit
            }
            params.put("hospital",hospital);
            params.put("procedures_surgeries",procedures_surgeries);
            params.put("date",date);

            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_CARE_PLAN_PROC,"post",params,FragmentCPProcSurg.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddProc.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddProc.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddProc.setCanceledOnTouchOutside(false);
        dialogAddProc.show();
        dialogAddProc.getWindow().setAttributes(lp);

    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ADD_CARE_PLAN_PROC)){
            //{"status":"success","message":"Procedures Surgeries Added"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Procedures/Surgeries has been added",0,0);
                    if(dialogAddProc != null){
                        dialogAddProc.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 7,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(7));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }
}
