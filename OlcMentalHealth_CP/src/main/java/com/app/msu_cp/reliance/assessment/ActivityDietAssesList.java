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

public class ActivityDietAssesList extends BaseActivity {


    ListView lvDietAssesList;
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
        setContentView(R.layout.activity_dietasses_list);

        lvDietAssesList = findViewById(R.id.lvDietAssesList);
        tvNoData = findViewById(R.id.tvNoData);

        lvDietAssesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                viewForm(position);

                //openActivity.open(ActivityBecksDepression.class, false);

                /*selectedDietitaryAssesListBean = dietitaryAssesListBeans.get(position);
                Intent intent = new Intent(activity,ActivityDietAssesForm.class);
                intent.putExtra("isEdit",true);
                startActivity(intent);*/

                //assessments/sdsh_form_view/15?platform=mobile
                //String formURL = DATA.baseUrl + "assessments/dietitary_assessment_view/"+dietitaryAssesListBeans.get(position).id+"?platform=mobile";
                //new GloabalMethods(activity).showWebviewDialog(formURL, "Dietitary Assessment");
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dietitary Assessment");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityDietAssesForm.class, false);
            }
        });


        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesList = activity;

        loadListData();
    }


    public void editForm(int position){
        selectedDietitaryAssesListBean = dietitaryAssesListBeans.get(position);

        Intent intent = new Intent(activity,ActivityDietAssesForm.class);
        intent.putExtra("isEdit",true);
        startActivity(intent);
    }
    public void viewForm(int position){
        String formURL = DATA.baseUrl + "assessments/dietitary_assessment_view/"+dietitaryAssesListBeans.get(position).id+"?platform=mobile";
        new GloabalMethods(activity).showWebviewDialog(formURL, "Dietitary Assessment");
    }

    public void loadListData(){
        RequestParams params = new RequestParams("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.DIET_ASSES_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<DietitaryAssesListBean> dietitaryAssesListBeans;
    public static DietitaryAssesListBean selectedDietitaryAssesListBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.DIET_ASSES_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<DietitaryAssesListBean>>() {}.getType();
                dietitaryAssesListBeans = new Gson().fromJson(data.toString(), listType);

                lvDietAssesList.setAdapter(new DietAssesListAdapter(activity, dietitaryAssesListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
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
