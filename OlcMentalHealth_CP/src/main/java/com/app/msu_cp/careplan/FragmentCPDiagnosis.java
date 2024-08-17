package com.app.msu_cp.careplan;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPDiagnosis extends Fragment implements ApiCallBack {

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

    ListView lvCareDiag;
    TextView tvNoData;
    Button btnAddDiag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_care_diag, container, false);

        lvCareDiag = (ListView) rootView.findViewById(R.id.lvCareDiag);
        tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);
        btnAddDiag = (Button) rootView.findViewById(R.id.btnAddDiag);

        lvCareDiag.setAdapter(new CPDiagAdapter(parentActivity,ActivityCarePlanDetail.cp_diagnosisBeans));

        if(ActivityCarePlanDetail.cp_diagnosisBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }

        btnAddDiag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDiagDialog();
            }
        });


        return rootView;
    }


    Dialog dialogAddDiag;
    ProgressBar pbAutoComplete;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
    private Handler handler;
    private IcdCodesAdapter icdCodesAdapter;
    String diagnosis = "";
    public void showAddDiagDialog(){
        dialogAddDiag = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddDiag.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddDiag.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddDiag.setContentView(R.layout.dialog_add_diag);

        ImageView ivCancel = (ImageView) dialogAddDiag.findViewById(R.id.ivCancel);
        Button btnAddDiag = (Button) dialogAddDiag.findViewById(R.id.btnAddDiag);

        EditText etAddDiagDesc = (EditText) dialogAddDiag.findViewById(R.id.etAddDiagDesc);
        EditText etAddDiagDate = (EditText) dialogAddDiag.findViewById(R.id.etAddDiagDate);
        EditText etAddDiagBy = (EditText) dialogAddDiag.findViewById(R.id.etAddDiagBy);
        EditText etAddDiagComments = (EditText) dialogAddDiag.findViewById(R.id.etAddDiagComments);

        etAddDiagDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddDiagDate);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });

        //===============AutoComplete========================
        pbAutoComplete = (ProgressBar) dialogAddDiag.findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final AppCompatAutoCompleteTextView autoCompleteTextView = (AppCompatAutoCompleteTextView) dialogAddDiag.findViewById(R.id.auto_complete_edit_text);

        //Setting up the adapter for AutoSuggest
        icdCodesAdapter = new IcdCodesAdapter(parentActivity, android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(icdCodesAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        diagnosis = icdCodesAdapter.getObject(position).toString();//.desc
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
                        ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES,"post",params,FragmentCPDiagnosis.this::fetchDataCallback, parentActivity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    }
                }
                return false;
            }
        });


        btnAddDiag.setOnClickListener(v -> {

            if(diagnosis.isEmpty()){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select diagnosis",0,0);
                autoCompleteTextView.setError("");
                return;
            }
            String description = etAddDiagDesc.getText().toString();
            String date_diagnosed = etAddDiagDate.getText().toString();
            String diagnosed_by = etAddDiagBy.getText().toString();
            String comments = etAddDiagComments.getText().toString();


            RequestParams params = new RequestParams();
            params.put("diagnosis",diagnosis);
            params.put("description",description);
            params.put("date_diagnosed",date_diagnosed);
            params.put("diagnosed_by",diagnosed_by);
            params.put("comments",comments);
            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_CP_DIAG,"post",params,FragmentCPDiagnosis.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddDiag.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddDiag.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddDiag.setCanceledOnTouchOutside(false);
        dialogAddDiag.show();
        dialogAddDiag.getWindow().setAttributes(lp);

    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_ICD_CODES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                /*List<IcdCodeBean> icdCodeBeans = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    icdCodeBeans.add(gson.fromJson(data.getJSONObject(i)+"", IcdCodeBean.class));
                }*/
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<IcdCodeBean>>() {}.getType();
                List<IcdCodeBean> icdCodeBeans = gson.fromJson(data.toString(), listType);

                pbAutoComplete.setVisibility(View.GONE);
                if(icdCodeBeans != null){
                    //IMPORTANT: set data here and notify
                    icdCodesAdapter.setData(icdCodeBeans);
                    icdCodesAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_CP_DIAG)){
            //{"status":"success","message":"Diagnosis Added"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast(jsonObject.getString("message"),0,0);
                    if(dialogAddDiag != null){
                        dialogAddDiag.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 3,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(3));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
