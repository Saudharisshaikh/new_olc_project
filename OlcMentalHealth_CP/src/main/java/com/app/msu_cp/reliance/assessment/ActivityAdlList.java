package com.app.msu_cp.reliance.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityAdlList extends BaseActivity {


    ListView lvAdlList;
    TextView tvNoData;

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
        setContentView(R.layout.activity_adl_list);

        lvAdlList = findViewById(R.id.lvAdlList);
        tvNoData = findViewById(R.id.tvNoData);

        lvAdlList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //openActivity.open(ActivityBecksDepression.class, false);

                viewOrEditForm(position, true);
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(ActivityAdlForm.formFlagA == 1){
            getSupportActionBar().setTitle("Katz Index of ADLs");
        }else if(ActivityAdlForm.formFlagA == 2){
            getSupportActionBar().setTitle("Lawton IADL Scale");
        }
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAdlForm.class, false);
            }
        });


        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesList = activity;

        loadListData();
    }

    public void loadListData(){
        RequestParams params = new RequestParams("patient_id", DATA.selectedUserCallId);
        String apiName = ApiManager.ADL_FORM_LIST;
        if(ActivityAdlForm.formFlagA == 1){
            apiName = ApiManager.ADL_FORM_LIST;
        } else if(ActivityAdlForm.formFlagA == 2){
            apiName = ApiManager.IADL_FORM_LIST;
        }
        ApiManager apiManager = new ApiManager(apiName,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<AdlListBean> adlListBeans;
    public static AdlListBean selectedAdlListBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.ADL_FORM_LIST) || apiName.equalsIgnoreCase(ApiManager.IADL_FORM_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<AdlListBean>>() {}.getType();
                adlListBeans = new Gson().fromJson(data.toString(), listType);

                lvAdlList.setAdapter(new AdlListAdapter(activity, adlListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void viewOrEditForm(int position, boolean isReadOnly){

        if(ActivityAdlForm.formFlagA == 1){
            selectedAdlListBean = adlListBeans.get(position);
            Intent intent = new Intent(activity,ActivityAdlForm.class);
            intent.putExtra("isEdit",true);

            intent.putExtra("isReadOnly", isReadOnly);

            startActivity(intent);
        }else if(ActivityAdlForm.formFlagA == 2){


            if(isReadOnly){
                String formURL = DATA.baseUrl + "assessments/view_iadl/"+adlListBeans.get(position).id+"?platform=mobile";
                new GloabalMethods(activity).showWebviewDialog(formURL, "The Lawton Instrumental Activities of Daily Living Scale");
            }else {

                selectedAdlListBean = adlListBeans.get(position);
                Intent intent = new Intent(activity,ActivityAdlForm.class);
                intent.putExtra("isEdit",true);

                intent.putExtra("isReadOnly", isReadOnly);

                startActivity(intent);

            }
        }
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
