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

public class FragmentCPAllergies extends Fragment implements ApiCallBack{

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
    Button btnAddAllergy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = (ListView) rootView.findViewById(R.id.lvCPMed);
        tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);
        btnAddAllergy = (Button) rootView.findViewById(R.id.btnAddMedication);

        btnAddAllergy.setText("Add New Allergies");
        btnAddAllergy.setOnClickListener(v -> {
            showAddAllergiesDialog();
        });

        lvCPMed.setAdapter(new CP_AllergyAdapter(parentActivity,ActivityCarePlanDetail.cp_allergyBeans));

        if(ActivityCarePlanDetail.cp_allergyBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }


        return rootView;
    }


    Dialog dialogAddAllergies;
    public void showAddAllergiesDialog(){
        dialogAddAllergies = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddAllergies.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddAllergies.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddAllergies.setContentView(R.layout.dialog_add_allergy);

        ImageView ivCancel = (ImageView) dialogAddAllergies.findViewById(R.id.ivCancel);
        Button btnAddAllergies = (Button) dialogAddAllergies.findViewById(R.id.btnAddAllergies);

        EditText etSubstance = (EditText) dialogAddAllergies.findViewById(R.id.etSubstance);
        EditText etDateOccurred = (EditText) dialogAddAllergies.findViewById(R.id.etDateOccurred);
        EditText etType = (EditText) dialogAddAllergies.findViewById(R.id.etType);
        EditText etDocumentedBy = (EditText) dialogAddAllergies.findViewById(R.id.etDocumentedBy);
        EditText etReaction = (EditText) dialogAddAllergies.findViewById(R.id.etReaction);

        etDateOccurred.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDateOccurred);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });


        btnAddAllergies.setOnClickListener(v -> {


            String substance = etSubstance.getText().toString();
            String date_occurred = etDateOccurred.getText().toString();
            String type = etType.getText().toString();
            String documented_by = etDocumentedBy.getText().toString();
            String reaction = etReaction.getText().toString();

            RequestParams params = new RequestParams();
            params.put("substance",substance);
            params.put("date_occurred",date_occurred);
            params.put("type",type);
            params.put("documented_by",documented_by);
            params.put("reaction",reaction);

            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_CP_ALLERGIES,"post",params,FragmentCPAllergies.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddAllergies.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddAllergies.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddAllergies.setCanceledOnTouchOutside(false);
        dialogAddAllergies.show();
        dialogAddAllergies.getWindow().setAttributes(lp);

    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ADD_CP_ALLERGIES)){
            //{"status":"success","message":"Allergies Added","hide_modal":1,"modal_id":"add_careplan_allergies"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast(jsonObject.getString("message"),0,0);
                    if(dialogAddAllergies != null){
                        dialogAddAllergies.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 4,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(4));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
