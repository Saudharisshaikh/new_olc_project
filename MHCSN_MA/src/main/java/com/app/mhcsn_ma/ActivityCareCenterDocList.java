package com.app.mhcsn_ma;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_ma.adapter.CareCenterDocAdapter;
import com.app.mhcsn_ma.api.ApiManager;
import com.app.mhcsn_ma.model.CareCenterBean;
import com.app.mhcsn_ma.model.CareCenterDoctorBean;
import com.app.mhcsn_ma.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCareCenterDocList extends BaseActivity{


    ListView lvCareCenterDoc;
    TextView tvNoData;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_center_doc_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Doctor/Provider");
        }

        lvCareCenterDoc = findViewById(R.id.lvCareCenterDoc);
        tvNoData = findViewById(R.id.tvNoData);
        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(careCenterDocAdapter != null){
                    careCenterDocAdapter.filter(s.toString());
                }
            }
        });

        /*lvCovidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view/"+covidFormListBeans.get(position).id+"?platform=mobile" , "Covid Testing");
            }
        });*/


        loadListData();
    }

    static CareCenterBean careCenterBean;
    public void loadListData(){
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.DOCTORS_BY_URGENTCARE + careCenterBean.id,"get", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    public void assignLiveCareToDoc(int doctorListPos){
        RequestParams params = new RequestParams();
        params.put("id", ActivityCareCentersList.careRequestBean.live_checkup_id);
        params.put("doctor_id", careCenterDoctorBeans.get(doctorListPos).id);
        ApiManager apiManager = new ApiManager(ApiManager.ASSIGN_LIVECARE,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CareCenterDoctorBean> careCenterDoctorBeans;
    CareCenterDocAdapter careCenterDocAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.DOCTORS_BY_URGENTCARE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<CareCenterDoctorBean>>() {}.getType();
                careCenterDoctorBeans = gson.fromJson(data.toString(), listType);
                careCenterDocAdapter = new CareCenterDocAdapter(activity, careCenterDoctorBeans);
                lvCareCenterDoc.setAdapter(careCenterDocAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if (apiName.contains(ApiManager.ASSIGN_LIVECARE)){
            //{"status":"success"}
            try {
                JSONObject jsonObject = new JSONObject(content);

                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity).setTitle("Info")
                                    .setMessage("Care request has been assigned successfully.")
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ActivityCareRequestsList.shouldReloadData = true;
                            finish();
                        }
                    });
                    alertDialog.show();
                }else {
                    customToast.showToast(content, 0, 0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

}
