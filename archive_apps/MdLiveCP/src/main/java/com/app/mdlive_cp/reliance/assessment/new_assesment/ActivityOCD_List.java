package com.app.mdlive_cp.reliance.assessment.new_assesment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityOCD_List extends BaseActivity{


    ListView lvOcdList;
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
        setContentView(R.layout.activity_ocd_list);

        lvOcdList = findViewById(R.id.lvOcdList);
        tvNoData = findViewById(R.id.tvNoData);

        lvOcdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //openActivity.open(ActivityBecksDepression.class, false);

                //viewOrEditForm(position, true);//this is read only form but disabled. lisa wants se only anwers not the whole assess tool


                String formTittle = "", formURL = "";
                if(ActivityOCD_Form.formFlag == 1){
                    formTittle = ActivityOCD_Form.OCD_FORM_NAME;
                    formURL = DATA.baseUrl + "assessments/view_ocd/"+ocDlistBeans.get(position).id+"?platform=mobile";
                }else if(ActivityOCD_Form.formFlag == 2){
                    formTittle = ActivityOCD_Form.FCSAS_FORM_NAME;
                    formURL = DATA.baseUrl + "assessments/view_fcsas/"+ocDlistBeans.get(position).id+"?platform=mobile";
                }else if(ActivityOCD_Form.formFlag == 3){
                    formTittle = ActivityOCD_Form.PANIC_ATACK_FORM_NAME;
                    formURL = DATA.baseUrl + "assessments/panic_attack_view/"+ocDlistBeans.get(position).id+"?platform=mobile";
                }else if(ActivityOCD_Form.formFlag == 4){
                    formTittle = ActivityOCD_Form.SCOFF_FORM_NAME;
                    formURL = DATA.baseUrl + "assessments/scoff_view/"+ocDlistBeans.get(position).id+"?platform=mobile";
                }else if(ActivityOCD_Form.formFlag == 5){
                    formTittle = ActivityOCD_Form.STRESS_QUES_FORM_NAME;
                    formURL = DATA.baseUrl + "assessments/stress_view/"+ocDlistBeans.get(position).id+"?platform=mobile";
                }
                new GloabalMethods(activity).showWebviewDialog(formURL, formTittle);


                /*String formTittle = "", formURL = "";
                //assessments/phq_view/15
                //assessments/gad_view/9
                if(ActivityPHQ_Form.formFlag == 1){
                    //getSupportActionBar().setTitle("Patient Health Questionnaire (PHQ-9)");
                    formTittle = "Patient Health Questionnaire (PHQ-9)";
                    formURL = DATA.baseUrl + "assessments/phq_view/"+phQlistBeans.get(position).id+"?platform=mobile";

                }else if(ActivityPHQ_Form.formFlag == 2){
                    //getSupportActionBar().setTitle("Generalized Anxiety Disorder 7-item (GAD-7) scale");
                    formTittle = "Generalized Anxiety Disorder 7-item (GAD-7) scale";
                    formURL = DATA.baseUrl + "assessments/gad_view/"+phQlistBeans.get(position).id+"?platform=mobile";

                }

                //String formURL = DATA.baseUrl + "assessments/sdsh_form_view/"+sdhSlistBeans.get(position).id+"?platform=mobile";
                new GloabalMethods(activity).showWebviewDialog(formURL, formTittle);*/
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if(ActivityOCD_Form.formFlag == 1){
                getSupportActionBar().setTitle(ActivityOCD_Form.OCD_FORM_NAME);
            }else if(ActivityOCD_Form.formFlag == 2){
                getSupportActionBar().setTitle(ActivityOCD_Form.FCSAS_FORM_NAME);
            }else if(ActivityOCD_Form.formFlag == 3){
                getSupportActionBar().setTitle(ActivityOCD_Form.PANIC_ATACK_FORM_NAME);
            }else if(ActivityOCD_Form.formFlag == 4){
                getSupportActionBar().setTitle(ActivityOCD_Form.SCOFF_FORM_NAME);
            }else if(ActivityOCD_Form.formFlag == 5){
                getSupportActionBar().setTitle(ActivityOCD_Form.STRESS_QUES_FORM_NAME);
            }
        }

        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityOCD_Form.formFlag == 5){
                    openActivity.open(ActivityStressQues_Form.class, false);
                }else {
                    openActivity.open(ActivityOCD_Form.class, false);
                }
            }
        });

        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesList = activity;

        loadListData();
    }

    public void loadListData(){
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        String apiName = ApiManager.OCD_LIST;
        if(ActivityOCD_Form.formFlag == 1){
            apiName = ApiManager.OCD_LIST;
        }else if(ActivityOCD_Form.formFlag == 2){
            apiName = ApiManager.FCSAS_LIST;
        }else if(ActivityOCD_Form.formFlag == 3){
            apiName = ApiManager.PANIC_ATT_LIST;
        }else if(ActivityOCD_Form.formFlag == 4){
            apiName = ApiManager.SCOFF_LIST;
        }else if(ActivityOCD_Form.formFlag == 5){
            apiName = ApiManager.STRESS_QUES_LIST;
        }
        ApiManager apiManager = new ApiManager(apiName,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<OCDlistBean> ocDlistBeans;
    public static OCDlistBean selectedOCDlistBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.OCD_LIST) || apiName.equalsIgnoreCase(ApiManager.FCSAS_LIST) ||
                apiName.contains(ApiManager.PANIC_ATT_LIST) || apiName.equalsIgnoreCase(ApiManager.SCOFF_LIST) ||
                apiName.equalsIgnoreCase(ApiManager.STRESS_QUES_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<OCDlistBean>>() {}.getType();
                ocDlistBeans = new Gson().fromJson(data.toString(), listType);

                lvOcdList.setAdapter(new OcdListAdapter(activity, ocDlistBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void viewOrEditForm(int position, boolean isReadOnly){

        selectedOCDlistBean = ocDlistBeans.get(position);

        Intent intent;

        if(ActivityOCD_Form.formFlag == 5){
            intent = new Intent(activity,ActivityStressQues_Form.class);
        }else {
            intent = new Intent(activity,ActivityOCD_Form.class);
        }

        intent.putExtra("isEdit",true);
        intent.putExtra("isReadOnly", isReadOnly);

        startActivity(intent);
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
