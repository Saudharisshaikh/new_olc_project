package com.app.msu_cp;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.adapters.MedSummaryAdapter;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.MedSummaryBean;
import com.app.msu_cp.util.DATA;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityMedicalSummary extends BaseActivity {

    /*============ Ahmer Work Start ==========*/
    ListView lvMedicalSummary;
    TextView tvNoData;
    MedSummaryAdapter medSummaryAdapter;
    SwipeRefreshLayout srMedSummary;
    Button btnAddMedSummary;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_summary);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Medical Summary");//Assessments Summary
        }

        lvMedicalSummary = findViewById(R.id.lvMedicalSummary);
        tvNoData = findViewById(R.id.tvNoData);
        srMedSummary = findViewById(R.id.srMedSummary);
        btnAddMedSummary = findViewById(R.id.btnAddMedSummary);

        loadMedSummary();

        //======================swip to refresh==================================
        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        srMedSummary.setColorSchemeColors(colorsArr);
        srMedSummary.setOnRefreshListener(
                () -> {
                    if(!checkInternetConnection.isConnectedToInternet()){srMedSummary.setRefreshing(false);}else {}
                    loadMedSummary();
                }
        );
        //======================swip to refresh ends=============================

        btnAddMedSummary.setOnClickListener(view ->
        {
            showAddMedSummaryDialog();
        });
    }

    Dialog dialogAddMedSummry;
    private void showAddMedSummaryDialog() {

        dialogAddMedSummry = new Dialog(activity);
        dialogAddMedSummry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddMedSummry.setContentView(R.layout.dialog_addmedsummary_ahmer);

        dialogAddMedSummry.setCanceledOnTouchOutside(false);

        EditText etMedSummaryDiag = dialogAddMedSummry.findViewById(R.id.etMedSummaryDiag);
        Button btnAddMedSummaryDiag = dialogAddMedSummry.findViewById(R.id.btnAddMedSummaryDiag);
        ImageView ivCloseAddMedSummary = dialogAddMedSummry.findViewById(R.id.ivCloseAddMedSummary);

        btnAddMedSummaryDiag.setOnClickListener(view -> {
            if (etMedSummaryDiag.getText().toString().isEmpty()) {
                etMedSummaryDiag.setError("Please enter summary");
                etMedSummaryDiag.requestFocus();
            } else {
                String summary = etMedSummaryDiag.getText().toString().trim();
                RequestParams params = new RequestParams();
                params.add("patient_id", DATA.selectedUserCallId);
                params.put("author_id", prefs.getString("id", ""));
                params.add("summary", summary);
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_MED_SUMMARY, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        ivCloseAddMedSummary.setOnClickListener(view -> {
            dialogAddMedSummry.dismiss();
        });

        dialogAddMedSummry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddMedSummry.show();
    }

    private void loadMedSummary() {
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_MED_SUMMARY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<MedSummaryBean> medSummaryBeans;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        srMedSummary.setRefreshing(false);
        if (apiName.contains(ApiManager.GET_MED_SUMMARY)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                int vis = data.length() > 0 ? View.GONE : View.VISIBLE;
                tvNoData.setVisibility(vis);

                medSummaryBeans = new Gson().fromJson(data.toString(), new TypeToken<ArrayList<MedSummaryBean>>() {}.getType());

                if (medSummaryBeans != null) {
                    // lvMedicalSummary.setAdapter(new MedSummaryAdapter(activity, medSummaryBeans));
                    medSummaryAdapter = new MedSummaryAdapter(activity, medSummaryBeans);
                    lvMedicalSummary.setAdapter(medSummaryAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.DELETE_MED_SUMMARY)) {
            //{"status":"success","message":"Deleted."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                    customSnakeBar.showToast(jsonObject.optString("message"));
                    medSummaryBeans.remove(posDeleteMedSummary);
                    medSummaryAdapter.notifyDataSetChanged();
                    //tvViewMedSummary.setText("View ("+medSummaryBeans.size()+")");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SAVE_MED_SUMMARY)) {
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                    customSnakeBar.showToast(jsonObject.optString("message"));
                    loadMedSummary();
                    dialogAddMedSummry.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }


    int posDeleteMedSummary;
    public void deleteMedSummary(int listPos){
        posDeleteMedSummary = listPos;
        RequestParams params = new RequestParams("id",medSummaryBeans.get(listPos).id);
        ApiManager apiManager = new ApiManager(ApiManager.DELETE_MED_SUMMARY, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }
}