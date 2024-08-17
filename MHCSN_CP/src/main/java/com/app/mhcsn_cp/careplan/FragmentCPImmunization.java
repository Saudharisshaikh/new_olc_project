package com.app.mhcsn_cp.careplan;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.CustomAnimations;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPImmunization extends Fragment implements ApiCallBack{

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
    Button btnAddImmun;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = rootView.findViewById(R.id.lvCPMed);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        btnAddImmun = rootView.findViewById(R.id.btnAddMedication);

        btnAddImmun.setText("Add Immunizations");

        lvCPMed.setAdapter(new CP_ImmunizationAdapter(parentActivity,ActivityCarePlanDetail.cp_immunizationBeans));

        if(ActivityCarePlanDetail.cp_immunizationBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }

        btnAddImmun.setOnClickListener(view -> {
            showAddImmunDialog();
        });
        return rootView;
    }


    Dialog dialogAddImmun;
    Spinner spVaccine,spSubVaccine;
    public void showAddImmunDialog(){
        dialogAddImmun = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddImmun.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddImmun.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddImmun.setContentView(R.layout.dialog_add_immunizations);

        ImageView ivCancel = dialogAddImmun.findViewById(R.id.ivCancel);
        Button btnAddImmunizations = dialogAddImmun.findViewById(R.id.btnAddImmunizations);

        spVaccine = dialogAddImmun.findViewById(R.id.spVaccine);
        spSubVaccine = dialogAddImmun.findViewById(R.id.spSubVaccine);
        EditText etDose = dialogAddImmun.findViewById(R.id.etDose);
        EditText etDate = dialogAddImmun.findViewById(R.id.etDate);

        etDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDate);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });


        if(vaccineBeans != null){
            ArrayAdapter<VaccineBean> spVaccineAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, vaccineBeans);
            spVaccineAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spVaccine.setAdapter(spVaccineAdapter);
        }else {
            ApiManager.shouldShowPD = false;
            new ApiManager(ApiManager.GET_VACCINES,"post",null,
                    FragmentCPImmunization.this::fetchDataCallback, parentActivity).
                    loadURL();
        }

        spVaccine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ApiManager.shouldShowPD = false;
                new ApiManager(ApiManager.GET_SUB_VACCINES+vaccineBeans.get(i).id,"post",null,
                        FragmentCPImmunization.this::fetchDataCallback, parentActivity).
                        loadURL();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        btnAddImmunizations.setOnClickListener(v -> {

            HideShowKeypad.hideKeyboard(parentActivity);

            if(spVaccine.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select vaccine",0,0);
                new CustomAnimations().shakeAnimate(spVaccine, 1000, spVaccine);
                return;
            }
            if(spSubVaccine.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select sub vaccine",0,0);
                new CustomAnimations().shakeAnimate(spSubVaccine, 1000, spSubVaccine);
                return;
            }

            String vaccine = vaccineBeans.get(spVaccine.getSelectedItemPosition()).id;
            String sub_vaccine_id = subVaccineBeans.get(spSubVaccine.getSelectedItemPosition()).id;
            String dose = etDose.getText().toString();
            String date = etDate.getText().toString();

            RequestParams params = new RequestParams();
            params.put("vaccine",vaccine);
            params.put("sub_vaccine_id",sub_vaccine_id);
            params.put("dose",dose);
            params.put("date",date);

            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_CARE_PLAN_IMMUN,"post",params,FragmentCPImmunization.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddImmun.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddImmun.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddImmun.setCanceledOnTouchOutside(false);
        dialogAddImmun.show();
        dialogAddImmun.getWindow().setAttributes(lp);

    }

    static ArrayList<VaccineBean> vaccineBeans,subVaccineBeans;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_VACCINES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                vaccineBeans = new ArrayList<>();
                vaccineBeans.add(new VaccineBean("","-- Select --",""));
                for (int i = 0; i < data.length(); i++) {
                    vaccineBeans.add(((ActivityCarePlanDetail) parentActivity).gson.fromJson(data.getJSONObject(i).toString(),VaccineBean.class));
                }
                if(spVaccine != null){
                    ArrayAdapter<VaccineBean> spVaccineAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, vaccineBeans);
                    spVaccineAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spVaccine.setAdapter(spVaccineAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail) parentActivity).customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.contains(ApiManager.GET_SUB_VACCINES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                subVaccineBeans = new ArrayList<>();
                subVaccineBeans.add(new VaccineBean("","-- Select --",""));
                for (int i = 0; i < data.length(); i++) {
                    subVaccineBeans.add(((ActivityCarePlanDetail) parentActivity).gson.fromJson(data.getJSONObject(i).toString(),VaccineBean.class));
                }
                if(spSubVaccine != null){
                    ArrayAdapter<VaccineBean> spVaccineAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, subVaccineBeans);
                    spVaccineAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spSubVaccine.setAdapter(spVaccineAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail) parentActivity).customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_CARE_PLAN_IMMUN)){
            //{"status":"success","message":"Procedures Surgeries Added"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Procedures/Surgeries has been added",0,0);
                    if(dialogAddImmun != null){
                        dialogAddImmun.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 8,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(8));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }
}
