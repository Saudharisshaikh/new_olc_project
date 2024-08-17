package com.app.mhcsn_uc.b_health.assessment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_uc.BaseActivity;
import com.app.mhcsn_uc.R;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.util.DATA;
import com.app.mhcsn_uc.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityPhqList extends BaseActivity{


    ListView lvPhqList;
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
        setContentView(R.layout.activity_phq_list);

        lvPhqList = findViewById(R.id.lvPhqList);
        tvNoData = findViewById(R.id.tvNoData);

        lvPhqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //openActivity.open(ActivityBecksDepression.class, false);

                //viewOrEditForm(position, true);//this is read only form but disabled. lisa wants se only anwers not the whole assess tool


                String formTittle = "", formURL = "";
                //assessments/phq_view/15
                //assessments/gad_view/9
                if(ActivityPHQ_Form.formFlag == 1){

                    formTittle = ActivityPHQ_Form.PHQ9_FORM_TITTLE;
                    formURL = DATA.baseUrl + "assessments/phq_view/"+phQlistBeans.get(position).id+"?platform=mobile";

                }else if(ActivityPHQ_Form.formFlag == 2){

                    formTittle = ActivityPHQ_Form.GAD7_FORM_TITTLE;
                    formURL = DATA.baseUrl + "assessments/gad_view/"+phQlistBeans.get(position).id+"?platform=mobile";

                }

                //String formURL = DATA.baseUrl + "assessments/sdsh_form_view/"+sdhSlistBeans.get(position).id+"?platform=mobile";
                new GloabalMethods(activity).showWebviewDialog(formURL, formTittle);
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(ActivityPHQ_Form.formFlag == 1){
            getSupportActionBar().setTitle(ActivityPHQ_Form.PHQ9_FORM_TITTLE);
        }else if(ActivityPHQ_Form.formFlag == 2){
            getSupportActionBar().setTitle(ActivityPHQ_Form.GAD7_FORM_TITTLE);
        }
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityPHQ_Form.class, false);
            }
        });

        new GloabalMethods(activity).setAssesListHeader();
        GloabalMethods.activityAssesList = activity;

        loadListData();
    }

    public void loadListData(){
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        String apiName = ApiManager.PHQ_LIST;
        if(ActivityPHQ_Form.formFlag == 1){
            apiName = ApiManager.PHQ_LIST;
        }else if(ActivityPHQ_Form.formFlag == 2){
            apiName = ApiManager.GAD_LIST;
        }
        ApiManager apiManager = new ApiManager(apiName,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    List<PHQlistBean> phQlistBeans;
    public static PHQlistBean selectedPHQlistBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.PHQ_LIST) || apiName.equalsIgnoreCase(ApiManager.GAD_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<PHQlistBean>>() {}.getType();
                phQlistBeans = new Gson().fromJson(data.toString(), listType);

                lvPhqList.setAdapter(new PhqListAdapter(activity, phQlistBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void viewOrEditForm(int position, boolean isReadOnly){

        selectedPHQlistBean = phQlistBeans.get(position);

        Intent intent = new Intent(activity,ActivityPHQ_Form.class);
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
