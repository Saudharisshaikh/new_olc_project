package com.app.mhcsn_cp.reliance.assessment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityAssessmentsSummary extends BaseActivity{


    ListView lvAssessmentSummary;
    TextView tvNoData;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_summary);


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recent Summary");//Assessments Summary
        }

        lvAssessmentSummary = findViewById(R.id.lvAssessmentSummary);
        tvNoData = findViewById(R.id.tvNoData);

        lvAssessmentSummary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        /*setSupportActionBar(findViewById(R.id.toolbar));
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
        GloabalMethods.activityAssesList = activity;*/

        loadListData();
    }

    public void viewForm(int position){
        //String formURL = DATA.baseUrl + "assessments/dietitary_assessment_view/"+assessmentsSummaryBeans.get(position).id+"?platform=mobile";

        String formURL = DATA.baseUrl + assessmentsSummaryBeans.get(position).url+"?platform=mobile";
        new GloabalMethods(activity).showWebviewDialog(formURL, assessmentsSummaryBeans.get(position).form_name);
    }

    public void loadListData(){
        //RequestParams params = new RequestParams("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.ASSESSMENT_SUMMARY+DATA.selectedUserCallId, "post", null, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<AssessmentsSummaryBean> assessmentsSummaryBeans;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.ASSESSMENT_SUMMARY)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<AssessmentsSummaryBean>>() {}.getType();
                assessmentsSummaryBeans = new Gson().fromJson(data.toString(), listType);

                if(assessmentsSummaryBeans != null){
                    lvAssessmentSummary.setAdapter(new AssesSummaryAdapter(activity, assessmentsSummaryBeans));
                }

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
