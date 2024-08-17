package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.DoctorsModel;
import com.app.mdlive_cp.model.DrugBean;
import com.app.mdlive_cp.util.CustomAnimations;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPMedication extends Fragment implements ApiCallBack{

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

    ArrayList<CT_TypeBean> getMedFilters(){
        ArrayList<CT_TypeBean> ct_typeBeans = new ArrayList<>();
        ct_typeBeans.add(new CT_TypeBean("all","All Medications"));
        ct_typeBeans.add(new CT_TypeBean("prescription","Medications by Prescription"));
        ct_typeBeans.add(new CT_TypeBean("history","Medications from History"));
        ct_typeBeans.add(new CT_TypeBean("careplan","Medications from Careplan"));
        return ct_typeBeans;
    }

    static ListView lvCPMed;
    static TextView tvNoData;
    Button btnAddMedication;
    Spinner spMedFilter;
    ArrayList<CT_TypeBean> medFilters = getMedFilters();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = rootView.findViewById(R.id.lvCPMed);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        btnAddMedication = rootView.findViewById(R.id.btnAddMedication);
        spMedFilter = rootView.findViewById(R.id.spMedFilter);

        spMedFilter.setVisibility(View.VISIBLE);
        ArrayAdapter<CT_TypeBean> spFiltersAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, medFilters);
        spMedFilter.setAdapter(spFiltersAdapter);
        spMedFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((ActivityCarePlanDetail)parentActivity).getCarePlanMed(medFilters.get(i).kay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*lvCPMed.setAdapter(new CP_MedAdapter(parentActivity,ActivityCarePlanDetail.cp_medBeans));

        if(ActivityCarePlanDetail.cp_medBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }*/


        if(notesDoctors == null){
            getNotesDoctors();
        }

        btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMedDialog();
            }
        });

        return rootView;
    }



    Dialog dialogAddMed;
    ProgressBar pbAutoComplete;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
    private Handler handler;
    private DrugsAdapter drugsAdapter;
    String medication_name = "";
    Spinner spPrescribedBy;
    public void showAddMedDialog(){
        dialogAddMed = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddMed.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddMed.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddMed.setContentView(R.layout.dialog_add_med);

        ImageView ivCancel = dialogAddMed.findViewById(R.id.ivCancel);
        Button btnAddMed = dialogAddMed.findViewById(R.id.btnAddMed);

        EditText etStartDate = dialogAddMed.findViewById(R.id.etStartDate);
        EditText etDirections = dialogAddMed.findViewById(R.id.etDirections);
        EditText etUse = dialogAddMed.findViewById(R.id.etUse);
        EditText etOTC = dialogAddMed.findViewById(R.id.etOTC);
        EditText etComments = dialogAddMed.findViewById(R.id.etComments);

        spPrescribedBy = dialogAddMed.findViewById(R.id.spPrescribedBy);

        CheckBox cbB = dialogAddMed.findViewById(R.id.cbB);
        CheckBox cbL = dialogAddMed.findViewById(R.id.cbL);
        CheckBox cbD = dialogAddMed.findViewById(R.id.cbD);
        CheckBox cbN = dialogAddMed.findViewById(R.id.cbN);

        etStartDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etStartDate);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });


        if(notesDoctors != null){
            ArrayAdapter<DoctorsModel> spPtencyUnitAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, notesDoctors);
            spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spPrescribedBy.setAdapter(spPtencyUnitAdapter);
        }else {
            getNotesDoctors();
        }

        //===============AutoComplete========================
        pbAutoComplete = dialogAddMed.findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final AppCompatAutoCompleteTextView autoCompleteTextView =  dialogAddMed.findViewById(R.id.auto_complete_edit_text);

        //Setting up the adapter for AutoSuggest
        drugsAdapter = new DrugsAdapter(parentActivity, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(drugsAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        medication_name = drugsAdapter.getObject(position).getDrug_name();
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        pbAutoComplete.setVisibility(View.VISIBLE);
                        RequestParams params = new RequestParams();
                        params.put("keyword", autoCompleteTextView.getText().toString());
                        ApiManager apiManager = new ApiManager(ApiManager.GET_DRUGS,"post",params,FragmentCPMedication.this::fetchDataCallback, parentActivity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    }
                }
                return false;
            }
        });


        btnAddMed.setOnClickListener(v -> {
            if(spPrescribedBy.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select prescriber",0,0);
                new CustomAnimations().shakeAnimate(spPrescribedBy, 1000, spPrescribedBy);
                return;
            }
            if(medication_name.isEmpty()){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select medication name",0,0);
                autoCompleteTextView.setError("");
                return;
            }

            String start_date = etStartDate.getText().toString();
            String prescribe_by = notesDoctors.get(spPrescribedBy.getSelectedItemPosition()).id;
            String direction = etDirections.getText().toString();
            String use = etUse.getText().toString();
            String otc = etOTC.getText().toString();
            String b = cbB.isChecked() ? "1":"0";
            String l = cbL.isChecked() ? "1":"0";
            String d = cbD.isChecked() ? "1":"0";
            String n = cbN.isChecked() ? "1":"0";
            String comments = etComments.getText().toString();


            RequestParams params = new RequestParams();
            params.put("medication_name", medication_name);
            params.put("start_date", start_date);
            params.put("prescribe_by", prescribe_by);
            params.put("direction", direction);
            params.put("use", use);
            params.put("otc", otc);
            params.put("b", b);
            params.put("l", l);
            params.put("d", d);
            params.put("n", n);
            params.put("comments", comments);

            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            //params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.SAVE_CP_MEDICATION,"post",params,FragmentCPMedication.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddMed.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddMed.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddMed.setCanceledOnTouchOutside(false);
        dialogAddMed.show();
        dialogAddMed.getWindow().setAttributes(lp);

    }

    public void getNotesDoctors(){
        ApiManager apiManager = new ApiManager(ApiManager.NOTES_DOCTORS,"post",null,FragmentCPMedication.this, parentActivity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }

    static ArrayList<DoctorsModel> notesDoctors;
    List<DrugBean> drugBeans;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.NOTES_DOCTORS)){

            try {
                JSONArray data = new JSONArray(content);
                notesDoctors = new ArrayList<DoctorsModel>();
                DoctorsModel temp = null;

                temp = new DoctorsModel();
                temp.fName="Select ";
                temp.lName="Prescriber";
                notesDoctors.add(temp);
                temp = null;


                if (data.length() == 0) {
                    //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    /*tvNoData.setVisibility(View.VISIBLE);
                    usersAdapter = new UsersAdapter(activity);
                    lvUsersList.setAdapter(usersAdapter);*/
                }else{

                    for (int i = 0; i < data.length(); i++) {
                        temp = new DoctorsModel();
                        JSONObject object = data.getJSONObject(i);
                        temp.id = object.getString("id");
                        temp.latitude =object.getString("latitude");
                        temp.longitude=object.getString("longitude");
                        temp.zip_code=object.getString("zip_code");
                        temp.fName=object.getString("first_name");
                        temp.lName=object.getString("last_name");
                        temp.is_online=object.getString("is_online");
                        temp.image=object.getString("image");
                        temp.designation=object.getString("designation");


                        if (temp.latitude.equalsIgnoreCase("null")) {
                            temp.latitude = "0.0";
                        }
                        if (temp.longitude.equalsIgnoreCase("null")) {
                            temp.longitude = "0.0";
                        }

                        temp.speciality_id=object.getString("speciality_id");
                        temp.current_app=object.getString("current_app");
                        temp.speciality_name=object.getString("speciality_name");

                        if(temp.current_app.equalsIgnoreCase("nurse")){
                            temp.lName = temp.lName+" ("+object.getString("doctor_category")+")";
                        }else {
                            temp.lName = temp.lName+" ("+temp.current_app+")";
                        }

                        notesDoctors.add(temp);
                        temp = null;
                    }

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                    /*UsersAdapter usersAdapter = new UsersAdapter(activity);
                    spSOAPDoc.setAdapter(usersAdapter);*/

                    //R.layout.spinner_item_lay
                    ArrayAdapter<DoctorsModel> spPtencyUnitAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, notesDoctors);
                    spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    if(spPrescribedBy != null){
                        spPrescribedBy.setAdapter(spPtencyUnitAdapter);
                    }

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }else if(apiName.equalsIgnoreCase(ApiManager.GET_DRUGS)){

            try {
                drugBeans = new ArrayList<>();
                DrugBean temp;

                JSONArray jsonArray = new JSONObject(content).getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
                    String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
                    String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
                    String code = jsonArray.getJSONObject(i).getString("code");
                    String route = jsonArray.getJSONObject(i).getString("route");
                    String strength = jsonArray.getJSONObject(i).getString("strength");
                    String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
                    String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
                    String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
                    String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

                    String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
                    String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

                    temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc,potency_unit,potency_code);
                    drugBeans.add(temp);
                    temp = null;
                }

                ((ActivityCarePlanDetail)parentActivity).hideShowKeypad.hideSoftKeyboard();
                //setData here

                pbAutoComplete.setVisibility(View.GONE);
                //IMPORTANT: set data here and notify
                drugsAdapter.setData(drugBeans);
                drugsAdapter.notifyDataSetChanged();

                /*ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
                        activity,
                        R.layout.view_spinner_item,
                        drugBeans
                );
                spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDrugName.setAdapter(spDrugNameAdapter);*/

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SAVE_CP_MEDICATION)){
            //{"success":1,"message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getInt("success") == 1){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Medication has been added",0,0);
                    if(dialogAddMed != null){
                        dialogAddMed.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 5,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(5));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }
}
