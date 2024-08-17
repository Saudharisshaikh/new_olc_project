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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPHosp extends Fragment implements ApiCallBack{

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
    Button btnAddHosp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = rootView.findViewById(R.id.lvCPMed);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        btnAddHosp = rootView.findViewById(R.id.btnAddMedication);

        btnAddHosp.setText("Add New Hospitalization");

        lvCPMed.setAdapter(new CP_HospAdapter(parentActivity,ActivityCarePlanDetail.cp_hospBeans));

        if(ActivityCarePlanDetail.cp_hospBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }

        btnAddHosp.setOnClickListener(view -> {
            showAddHospDialog();
        });

        return rootView;
    }


    Dialog dialogAddHosp;
    public void showAddHospDialog(){
        dialogAddHosp = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddHosp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddHosp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddHosp.setContentView(R.layout.dialog_add_hosp);

        ImageView ivCancel = dialogAddHosp.findViewById(R.id.ivCancel);
        Button btnAddHospitalization = dialogAddHosp.findViewById(R.id.btnAddHospitalization);

        EditText etDescription = dialogAddHosp.findViewById(R.id.etDescription);
        EditText etDateAdmitted = dialogAddHosp.findViewById(R.id.etDateAdmitted);
        EditText etComments = dialogAddHosp.findViewById(R.id.etComments);

        etDateAdmitted.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDateAdmitted);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });


        btnAddHospitalization.setOnClickListener(v -> {


            String description = etDescription.getText().toString();
            String date_admitted = etDateAdmitted.getText().toString();
            String comments = etComments.getText().toString();

            RequestParams params = new RequestParams();
            params.put("description",description);
            params.put("date_admitted",date_admitted);
            params.put("comments",comments);

            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_CARE_PLAN_HOSP,"post",params,FragmentCPHosp.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddHosp.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddHosp.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddHosp.setCanceledOnTouchOutside(false);
        dialogAddHosp.show();
        dialogAddHosp.getWindow().setAttributes(lp);

    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ADD_CARE_PLAN_HOSP)){
            //{"status":"success","message":"Hospitalization Added"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Hospitalization has been added",0,0);
                    if(dialogAddHosp != null){
                        dialogAddHosp.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 6,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(6));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }
}
