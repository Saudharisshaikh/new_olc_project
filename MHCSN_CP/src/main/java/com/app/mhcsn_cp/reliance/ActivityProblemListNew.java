package com.app.mhcsn_cp.reliance;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.adapters.HistoryDiagAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.careplan.IcdCodeBean;
import com.app.mhcsn_cp.careplan.IcdCodesAdapter;
import com.app.mhcsn_cp.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ActivityProblemListNew extends BaseActivity{


    //new Diagnosis ILC_CODES field
    LinearLayout layDiagnosis;
    ProgressBar pbAutoComplete;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
    private Handler handler;
    private IcdCodesAdapter icdCodesAdapter;
    //EditText etMedHistrDiagnosis;

    public TextView tvNoData;
    ListView lvDiagnosis;
    List<String> diagnosisList;
    HistoryDiagAdapter historyDiagAdapter;
    //new Diagnosis ILC_CODES field
    Button btnSaveDiagnosis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_list_new);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Patient Problem List");
        }



        lvDiagnosis = findViewById(R.id.lvDiagnosis);
        tvNoData = findViewById(R.id.tvNoData);
        btnSaveDiagnosis = findViewById(R.id.btnSaveDiagnosis);

        //===============AutoComplete========================
        pbAutoComplete = (ProgressBar) findViewById(R.id.pbAutoComplete);
        pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final AppCompatAutoCompleteTextView autoCompleteTextView = (AppCompatAutoCompleteTextView) findViewById(R.id.auto_complete_edit_text);

        //Setting up the adapter for AutoSuggest
        icdCodesAdapter = new IcdCodesAdapter(activity, android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(icdCodesAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //selectedText.setText(icdCodesAdapter.getObject(position).toString());
                        String diagnosis = icdCodesAdapter.getObject(position).desc+ " ("+icdCodesAdapter.getObject(position).code+")";

                        /*String diag = etMedHistrDiagnosis.getText().toString();
						if(TextUtils.isEmpty(diag)){
							diag = diagnosis;
						}else {
							diag = diag + "\n"+diagnosis;//,
						}
						etMedHistrDiagnosis.setText(diag);*/

                        if(diagnosisList == null){
                            diagnosisList = new ArrayList<>();
                        }
                            diagnosisList.add(diagnosis);
                            historyDiagAdapter = new HistoryDiagAdapter(activity, diagnosisList);
                            lvDiagnosis.setAdapter(historyDiagAdapter);
                        //lvDiagnosis.setExpanded(true);

                        autoCompleteTextView.setText("");

                        hideShowKeypad.hideSoftKeyboard();

                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                        ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES,"post",params, apiCallBack, activity);
                        ApiManager.shouldShowPD = false;
                        apiManager.loadURL();
                    }
                }
                return false;
            }
        });
        //new Diagnosis ILC_CODES field Ends



        btnSaveDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String icd_codes = etMedHistrDiagnosis.getText().toString();
                String icd_codes = "";
                if(diagnosisList != null){
                    for (int i = 0; i < diagnosisList.size(); i++) {
                        icd_codes = icd_codes + diagnosisList.get(i);
                        if(i < (diagnosisList.size() - 1)){
                            icd_codes = icd_codes+"\n";
                        }
                    }
                }

                RequestParams params = new RequestParams();
                params.put("patient_id", DATA.selectedUserCallId);
                params.put("icd_codes", icd_codes);
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_ICD_CODES,"post",params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        getMedicalHistory(DATA.selectedUserCallId);

    }


    public void getMedicalHistory(String patient_id) {
        ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_HISTORY+"/"+patient_id,"get",null,apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);

        if(apiName.contains(ApiManager.GET_MEDICAL_HISTORY)){

            try {
                JSONObject jsonObject = new JSONObject(content);

                int success = jsonObject.getInt("success");
                String message = jsonObject.getString("message");
                JSONObject data = jsonObject.getJSONObject("data");

                if(! data.has("id")){
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Medical history not added on your profile. Please add your medical history now.")
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    return;
                }


                String icd_codes = data.optString("icd_codes");
                //etMedHistrDiagnosis.setText(icd_codes);

                diagnosisList = new ArrayList<>(Arrays.asList(icd_codes.split("\n")));
                if(diagnosisList != null){
                    historyDiagAdapter = new HistoryDiagAdapter(activity, diagnosisList);
                    lvDiagnosis.setAdapter(historyDiagAdapter);
                    //lvDiagnosis.setExpanded(true);
                    tvNoData.setVisibility(diagnosisList.isEmpty() ? View.VISIBLE:View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_ICD_CODES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<IcdCodeBean>>() {}.getType();
                List<IcdCodeBean> icdCodeBeans = gson.fromJson(data.toString(), listType);

                Iterator itr = icdCodeBeans.iterator();
                while(itr.hasNext()) {
                    IcdCodeBean icdCodeBean = (IcdCodeBean) itr.next();
                    if (diagnosisList.contains(icdCodeBean.desc+" ("+icdCodeBean.code+")")) {
                        itr.remove();
                    }
                }

                /*List<IcdCodeBean> icdCodeBeans = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    icdCodeBeans.add(gson.fromJson(data.getJSONObject(i)+"", IcdCodeBean.class));
                }*/

                pbAutoComplete.setVisibility(View.GONE);
                if(icdCodeBeans != null){
                    //IMPORTANT: set data here and notify
                    icdCodesAdapter.setData(icdCodeBeans);
                    icdCodesAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SAVE_ICD_CODES)){
            //{"success":1,"message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customSnakeBar.showToast(jsonObject.optString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
