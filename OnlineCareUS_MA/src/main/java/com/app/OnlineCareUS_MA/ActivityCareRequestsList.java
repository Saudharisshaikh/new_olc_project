package com.app.OnlineCareUS_MA;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.OnlineCareUS_MA.adapter.CareRequestAdapter;
import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.model.CareRequestBean;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.Database;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCareRequestsList extends BaseActivity{


    ListView lvCareRequests;
    TextView tvNoData;
    EditText etSearch;


    static boolean shouldReloadData = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldReloadData){
            shouldReloadData = false;
            loadListData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_requests_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Care Requests");
        }

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_CARE_REQ);

        lvCareRequests = findViewById(R.id.lvCareRequests);
        tvNoData = findViewById(R.id.tvNoData);
        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(careRequestAdapter != null){
                    careRequestAdapter.filter(s.toString());
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
        RequestParams params = new RequestParams("doctor_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_LIVECARE_REQUESTS,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CareRequestBean> careRequestBeans;
    CareRequestAdapter careRequestAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.GET_LIVECARE_REQUESTS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<CareRequestBean>>() {}.getType();
                careRequestBeans = gson.fromJson(data.toString(), listType);
                careRequestAdapter = new CareRequestAdapter(activity, careRequestBeans);
                lvCareRequests.setAdapter(careRequestAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

}
