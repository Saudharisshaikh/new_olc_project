package com.app.OnlineCareTDC_MA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.OnlineCareTDC_MA.adapter.CovidAssignDocAdapter;
import com.app.OnlineCareTDC_MA.api.ApiManager;
import com.app.OnlineCareTDC_MA.model.CovidAssignDocBean;
import com.app.OnlineCareTDC_MA.model.CovidFormListBean;
import com.app.OnlineCareTDC_MA.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCovidAssignDocList extends BaseActivity{


    ListView lvCareCenterDoc;
    TextView tvNoData;
    EditText etSearch;

    public static CovidFormListBean covidFormListBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_assign_doc_list);

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
                if(covidAssignDocAdapter != null){
                    covidAssignDocAdapter.filter(s.toString());
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


    public void loadListData(){
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.GET_ALL_DOC ,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    public void assignToProvider(int doctorListPos){
        RequestParams params = new RequestParams();
        params.put("id", covidFormListBean.id);
        params.put("doctor_id", covidAssignDocBeans.get(doctorListPos).id);
        ApiManager apiManager = new ApiManager(ApiManager.ASSIGN_PROVIDER,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CovidAssignDocBean> covidAssignDocBeans;
    CovidAssignDocAdapter covidAssignDocAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.GET_ALL_DOC)){
            try {
                //JSONObject jsonObject = new JSONObject(content);
                //JSONArray data = jsonObject.getJSONArray("data");

                JSONArray data = new JSONArray(content);

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<CovidAssignDocBean>>() {}.getType();
                covidAssignDocBeans = gson.fromJson(data.toString(), listType);
                covidAssignDocAdapter = new CovidAssignDocAdapter(activity, covidAssignDocBeans);
                lvCareCenterDoc.setAdapter(covidAssignDocAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if (apiName.contains(ApiManager.ASSIGN_PROVIDER)){
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
                            ActivityCovidFormList.shouldReloadData = true;
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
