package com.app.fivestardoc;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fivestardoc.adapter.CareRequestAdapter;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.model.CareRequestBean;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.Database;
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
    BroadcastReceiver ptRemovedbroadCast;
    BroadcastReceiver ptAddedbroadCast;


    static boolean shouldReloadData = false;

    public static final String pt_removed = BuildConfig.APPLICATION_ID+".ptRemoved";
    public static final String pt_added = BuildConfig.APPLICATION_ID+".pt_added";


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
            getSupportActionBar().setTitle("Waiting Room");
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

        ptRemovedbroadCast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                loadListData();
            }
        };

        ptAddedbroadCast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                loadListData();
            }
        };
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
        if (apiName.contains(ApiManager.ACCEPT_LIVECARE_REQUEST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String title = jsonObject.getString("title");
                String msg = jsonObject.getString("message");
                if (status.equalsIgnoreCase("success"))
                {
                    openActivity.open(LiveCare.class , true);
                    customToast.showToast(msg,0,0);
                }
                else if (status.equalsIgnoreCase("error"))
                {
                    AlertDialog d = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle(title)
                            .setMessage(msg).
                            setPositiveButton("Done", (arg0, arg1) -> {
                                // TODO Auto-generated method stub
                                if (checkInternetConnection.isConnectedToInternet()) {
                                    loadListData();
                                } else {
                                    Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                                }
                            }).create();
                    d.show();
                }
              /*  else
                {
                    customToast.showToast(msg , 0 , 0);
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }



    public void acceptCareRequest(String livecareId){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("livecare_id" , livecareId);
        ApiManager apiManager = new ApiManager(ApiManager.ACCEPT_LIVECARE_REQUEST,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void onStart() {
        registerReceiver(ptRemovedbroadCast, new IntentFilter(ActivityCareRequestsList.pt_removed));
        registerReceiver(ptAddedbroadCast, new IntentFilter(ActivityCareRequestsList.pt_added));
        super.onStart();
    }

    @Override
    public void onStop() {
        unregisterReceiver(ptRemovedbroadCast);
        unregisterReceiver(ptAddedbroadCast);
        super.onStop();
    }
}
