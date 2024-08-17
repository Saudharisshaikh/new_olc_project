package com.digihealthcard;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.digihealthcard.R;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.adapter.TestResultAdapter;
import com.digihealthcard.model.TestResultBean;
import com.digihealthcard.util.DATA;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityTestResultsList extends ActivityBaseDrawer{


    ListView lvCardsList;
    TextView tvNoData;
    SwipeRefreshLayout swiperefresh;
    Button btnSyncEmail,btnDone;
    TextView tvEmailInfo;

    static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;
            loadListData();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test_result_list);
        getLayoutInflater().inflate(R.layout.activity_test_result_list, container_frame);

        lvCardsList = findViewById(R.id.lvCardsList);
        tvNoData = findViewById(R.id.tvNoData);
        btnSyncEmail = findViewById(R.id.btnSyncEmail);
        tvEmailInfo = findViewById(R.id.tvEmailInfo);
        btnDone = findViewById(R.id.btnDone);


        /*String info = "Please make sure that you have to send your test results from the email registerd with your COVACARD Health Wallet acount. Your registered email is : "
                +prefs.getString("email", "");*/
        tvEmailInfo.setText(prefs.getString("email", ""));

        lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);

                selectedTestResultBean = testResultBeans.get(position);
            }
        });

        /*setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Test Results");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddTestResults.class, false);
            }
        });*/

        tvToolbarTitle.setText("Test Results");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.VISIBLE);
        btnToolbarAdd.setText("Add New");
        btnToolbarAdd.setOnClickListener(v -> {
            openActivity.open(ActivityAddTestResults.class, false);
        });

        String testReslutsEmail = sharedPrefsHelper.get("test_results_email","testresults@digihealthcard.com");
        TextView tvEmail = findViewById(R.id.tvEmail);
        String styledText = "<font color='#2979ff'><u>"+testReslutsEmail+"</u></font>";
        tvEmail.setText(Html.fromHtml(styledText));
        tvEmail.setOnClickListener(v -> {
            String subject = getString(R.string.app_name)+" Test Results";
            String[] addresses = {testReslutsEmail};//"support@onlinecare.com"

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",testReslutsEmail, null));//"support@onlinecare.com"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });


        //======================swip to refresh==================================
        swiperefresh = findViewById(R.id.swiperefresh);
        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        swiperefresh.setColorSchemeColors(colorsArr);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!checkInternetConnection.isConnectedToInternet()){
                            swiperefresh.setRefreshing(false);
                        }
                        loadListData();
                    }
                }
        );
        //======================swip to refresh ends=============================


        btnSyncEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
                ApiManager apiManager = new ApiManager(ApiManager.FETCH_EMAIL, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        loadListData();

        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }

    public void loadListData(){
        String all_test_results = sharedPrefsHelper.get("all_test_results", "");
        if(!TextUtils.isEmpty(all_test_results)){
            parseData(all_test_results);
            ApiManager.shouldShowLoader = false;
        }
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_RESULT, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<TestResultBean> testResultBeans;
    TestResultAdapter testResultAdapter;
    public static TestResultBean selectedTestResultBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_RESULT)){
            swiperefresh.setRefreshing(false);
            parseData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.FETCH_EMAIL)){
            //{"success":1,"message":"Fetched"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast("Email has been fetched successfully. We are loading your test results now.",0,0);
                    loadListData();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.TEST_RESULT_DELETE)){
            //{"status":"success","message":"Deleted."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    testResultBeans.remove(listPos);
                    testResultAdapter.notifyDataSetChanged();
                    loadListData();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    private void parseData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray data = jsonObject.getJSONArray("data");

            if(data.length() == 0){
                tvNoData.setVisibility(View.VISIBLE);
            }else {
                tvNoData.setVisibility(View.GONE);
            }

            Type listType = new TypeToken<ArrayList<TestResultBean>>() {}.getType();
            testResultBeans = new Gson().fromJson(data.toString(), listType);

            testResultAdapter = new TestResultAdapter(activity, testResultBeans);
            lvCardsList.setAdapter(testResultAdapter);

            sharedPrefsHelper.save("all_test_results", content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }



    /*public void viewOrEditForm(int position, boolean isReadOnly){

        selectedDastListBean = dastListBeans.get(position);

        Intent intent = new Intent(activity,ActivityDAST_Form.class);
        intent.putExtra("isEdit",true);

        intent.putExtra("isReadOnly", isReadOnly);

        startActivity(intent);
    }*/


    int listPos;
    public void deleteCard(int pos){
        //customToast.showToast("Feature will be available in the near future", 0,0);
        listPos = pos;
        RequestParams params = new RequestParams();
        params.put("id", testResultBeans.get(pos).id);
        ApiManager apiManager = new ApiManager(ApiManager.TEST_RESULT_DELETE,"post",params,this, activity);
        //ApiManager.shouldShowLoader = showLoader;
        apiManager.loadURL();
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityBradenScale.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }*/
}
