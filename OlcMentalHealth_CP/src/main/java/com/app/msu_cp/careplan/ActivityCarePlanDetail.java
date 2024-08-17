package com.app.msu_cp.careplan;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityCarePlanDetail extends BaseActivity {

    Toolbar mToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    ListView lvDrawer;
    LinearLayout container_frame;
    LinearLayout left_drawer;

    RelativeLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawer = (ListView) findViewById(R.id.lvDrawer);


        container_frame = (LinearLayout) findViewById(R.id.container_frame);
        left_drawer = (LinearLayout) findViewById(R.id.left_drawer);

        fragmentContainer = (RelativeLayout) findViewById(R.id.fragmentContainer);

        //mToolbar.setLogo(R.drawable.top_logo);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Home Town");
        //mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //linearLayout.removeAllViews();
                //linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                // viewAnimator.showMenuContent();

                container_frame.setTranslationX(slideOffset * left_drawer.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        lvDrawer.setAdapter(new DrawerAdapter(activity, getDrawerItems()));
        lvDrawer.setOnItemClickListener(new DrawerItemClickListener());

        getCarePlanById();
    }

    public void getCarePlanById(){
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_BY_ID+"/"+ActivityCarePlan.slectedCareID,"get",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanGoals(){
        RequestParams params = new RequestParams();
        params.put("careplan_id", ActivityCarePlan.slectedCareID);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_GOALS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
    public void getCarePlanDiagnoses(){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_DIAG,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanAllergies(){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_ALLERGIES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanMed(String filterTxt){
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_MED+"/"+DATA.selectedUserCallId+"/"+filterTxt,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanHosp(){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_HOSP,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanProcSurg(){
        RequestParams params = new RequestParams();
        params.put("careplan_id", ActivityCarePlan.slectedCareID);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_PROC,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanImmunization(){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CARE_PLAN_IMMUN,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getCarePlanInsurance(){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.GET_CP_INSURANCE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            mDrawerLayout.closeDrawers();
            lvDrawer.setItemChecked(position, true);

            getSupportActionBar().setTitle("Care Plan > "+getDrawerItems().get(position).getItemName());

            switch (position) {

                case 0:
                    finish();
                    //onBackPressed();
                    //openActivity.open(ActivityIONS.class, false);
                    //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    break;
                case 1:
                    //openActivity.open(ActivityDealsAndCoupons.class, false);
                    //overridePendingTransition(R.anim.open_next, R.anim.close_next);

                    Fragment fragment = new FragmentCareTeam();
                    openFragment(fragment,"",activity);
                    break;
                case 2:
                    //openActivity.open(ActivityFindFriends.class, false);
                    //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    getCarePlanGoals();
                    break;

                case 3:
                    //openActivity.open(ActivityFindUs.class, false);
                    //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    getCarePlanDiagnoses();
                    break;
                case 4:
                    getCarePlanAllergies();
                    //openActivity.open(Profile.class, false);
                    //overridePendingTransition(R.anim.open_next, R.anim.close_next);
                    break;
                case 5:
                    //getCarePlanMed("");
                    Fragment fragment1 = new FragmentCPMedication();
                    openFragment(fragment1,"",activity);
                    break;
                case 6:
                    getCarePlanHosp();
                    break;
                case 7:
                    getCarePlanProcSurg();
                    break;
                case 8:
                    getCarePlanImmunization();
                    break;

                case 9:
                    getCarePlanInsurance();
                    break;

                default:
                    //customToast.showToast("This section is under development", 0, 0);
                    break;
            }


        }
    }//end DrawerItemClickListener


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START| Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }


    public ArrayList<DrawerItemBean> getDrawerItems() {
        ArrayList<DrawerItemBean> itemBeans = new ArrayList<>();
        itemBeans.add(new DrawerItemBean("Back", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Care Team", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Goals", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Diagnosis", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Allergies/In-tolerances", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Medications", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Hospitalization", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Procedures and Surgeries", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Immunizations", R.drawable.ic_launcher));
        itemBeans.add(new DrawerItemBean("Insurance Providers", R.drawable.ic_launcher));
        return itemBeans;
    }

    public static ArrayList<CareTeamBean> careTeamBeans;
    public static ArrayList<CP_GoalBean> cp_goalBeans;
    public static ArrayList<CP_DiagnosisBean> cp_diagnosisBeans;
    public static ArrayList<CP_MedBean> cp_medBeans;
    public static ArrayList<CP_AllergyBean> cp_allergyBeans;
    public static ArrayList<CP_HospBean> cp_hospBeans;
    public static ArrayList<CP_ProceSurgBean> cp_proceSurgBeans;
    public static ArrayList<CP_ImmunizationBean> cp_immunizationBeans;
    public static ArrayList<CP_InsuranceBean> cp_insuranceBeans;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.contains(ApiManager.GET_CARE_PLAN_BY_ID)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray care_team = jsonObject.getJSONArray("care_team");
                careTeamBeans = new ArrayList<>();
                CareTeamBean careTeamBean;

                for (int i = 0; i < care_team.length(); i++) {
                    careTeamBean = gson.fromJson(care_team.getJSONObject(i)+"",CareTeamBean.class);
                    careTeamBeans.add(careTeamBean);
                    careTeamBean = null;
                }

                //lvDrawer.setSelection(1);

                //lvDrawer.setItemChecked(1, true);
                lvDrawer.performItemClick(lvDrawer, 1, lvDrawer.getItemIdAtPosition(1));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_GOALS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cp_goalBeans = new ArrayList<>();
                CP_GoalBean cp_goalBean;
                for (int i = 0; i < data.length(); i++) {
                    cp_goalBean = gson.fromJson(data.getJSONObject(i)+"", CP_GoalBean.class);
                    cp_goalBeans.add(cp_goalBean);
                    cp_goalBean = null;
                }

                Fragment fragment = FragmentCareGoals.newInstance();
                openFragment(fragment,"",activity);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_DIAG)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray diagnosis = jsonObject.getJSONArray("diagnosis");
                cp_diagnosisBeans = new ArrayList<>();
                CP_DiagnosisBean cp_diagnosisBean;
                for (int i = 0; i < diagnosis.length(); i++) {
                    cp_diagnosisBean = gson.fromJson(diagnosis.getJSONObject(i)+"",CP_DiagnosisBean.class);
                    cp_diagnosisBeans.add(cp_diagnosisBean);
                    cp_diagnosisBean = null;
                }

                String medical_diagnosis = jsonObject.getString("medical_diagnosis");
                String[] medical_diagnosisArr = medical_diagnosis.split(",");
                for (int i = 0; i < medical_diagnosisArr.length; i++) {
                    cp_diagnosisBeans.add(new CP_DiagnosisBean("","-","-","-",
                            "-",medical_diagnosisArr[i],"-","-","-","-",
                            "-"));
                }

                String medical_history_other = jsonObject.getJSONObject("medical_history").getString("medical_history_other");
                cp_diagnosisBeans.add(new CP_DiagnosisBean("","-","-","-",
                        "-",medical_history_other,"-","-","-","-",
                        "-"));

                Fragment fragment = new FragmentCPDiagnosis();
                openFragment(fragment,"",activity);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.contains(ApiManager.GET_CARE_PLAN_MED)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cp_medBeans = new ArrayList<>();
                CP_MedBean cp_medBean;
                for (int i = 0; i < data.length(); i++) {
                    cp_medBean = gson.fromJson(data.getJSONObject(i)+"",CP_MedBean.class);
                    cp_medBeans.add(cp_medBean);
                    cp_medBean = null;
                }
                JSONArray prescription_medications = jsonObject.getJSONArray("prescription_medications");
                for (int i = 0; i < prescription_medications.length(); i++) {
                    cp_medBean = new CP_MedBean("","","",prescription_medications.getJSONObject(i).getString("dateof"),
                            prescription_medications.getJSONObject(i).getString("doctor_name"),
                            prescription_medications.getJSONObject(i).getString("drug_name"),prescription_medications.getJSONObject(i).getString("directions"),
                            "-","-","-","-","-","-","-");
                    cp_medBeans.add(cp_medBean);
                    cp_medBean = null;
                }

                try {//avoid json exception in case of filter = careplan
                    String medication_detail = jsonObject.getJSONObject("medical_history").getString("medication_detail");
                    String[] medication_detailArr = medication_detail.split("\\n");
                    for (int i = 0; i < medication_detailArr.length; i++) {
                        cp_medBean = new CP_MedBean("","","","-", "-",
                                medication_detailArr[i],"-", "-","-","-","-","-","-","-");
                        cp_medBeans.add(cp_medBean);
                        cp_medBean = null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                //Fragment fragment = new FragmentCPMedication();
                //openFragment(fragment,"",activity);
                try {//avoid nullpointer
                    FragmentCPMedication.lvCPMed.setAdapter(new CP_MedAdapter(activity,ActivityCarePlanDetail.cp_medBeans));
                    if(ActivityCarePlanDetail.cp_medBeans.isEmpty()){
                        FragmentCPMedication.tvNoData.setVisibility(View.VISIBLE);
                    }else {
                        FragmentCPMedication.tvNoData.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_ALLERGIES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray allergies = jsonObject.getJSONArray("allergies");
                cp_allergyBeans = new ArrayList<>();
                CP_AllergyBean cp_allergyBean;
                for (int i = 0; i < allergies.length() ; i++) {
                    cp_allergyBean = gson.fromJson(allergies.getJSONObject(i)+"", CP_AllergyBean.class);
                    cp_allergyBeans.add(cp_allergyBean);
                    cp_allergyBean = null;
                }
                String alergies_detail = jsonObject.getJSONObject("medical_history").getString("alergies_detail");
                String[] alergies_detailArr = alergies_detail.split("\\n");
                for (int i = 0; i < alergies_detailArr.length; i++) {
                    cp_allergyBean = new CP_AllergyBean("-","-","-","-",alergies_detailArr[i],"-","-","-","-","-",
                            "-");
                    cp_allergyBeans.add(cp_allergyBean);
                    cp_allergyBean = null;
                }

                Fragment fragment = new FragmentCPAllergies();
                openFragment(fragment,"",activity);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_HOSP)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray diagnosis = jsonObject.getJSONArray("diagnosis");
                cp_hospBeans = new ArrayList<>();
                CP_HospBean cp_hospBean;
                for (int i = 0; i < diagnosis.length(); i++) {
                    cp_hospBean = gson.fromJson(diagnosis.getJSONObject(i)+"", CP_HospBean.class);
                    cp_hospBeans.add(cp_hospBean);
                    cp_hospBean = null;
                }
                String hospitalize_detail = jsonObject.getJSONObject("medical_history").getString("hospitalize_detail");
                String[] hospDetailsArr = hospitalize_detail.split("\\n");
                for (int i = 0; i < hospDetailsArr.length; i++) {
                    cp_hospBean = new CP_HospBean("-","-","-","-",hospDetailsArr[i],"-","-","-","-");
                    cp_hospBeans.add(cp_hospBean);
                    cp_hospBean = null;
                }

                Fragment fragment = new FragmentCPHosp();
                openFragment(fragment,"",activity);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_PROC)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cp_proceSurgBeans = new ArrayList<>();
                CP_ProceSurgBean cp_proceSurgBean;
                for (int i = 0; i < data.length(); i++) {
                    cp_proceSurgBean = gson.fromJson(data.getJSONObject(i)+"", CP_ProceSurgBean.class);
                    cp_proceSurgBeans.add(cp_proceSurgBean);
                    cp_proceSurgBean = null;
                }
                Fragment fragment = new FragmentCPProcSurg();
                openFragment(fragment,"",activity);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CARE_PLAN_IMMUN)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cp_immunizationBeans = new ArrayList<>();
                CP_ImmunizationBean cp_immunizationBean;
                for (int i = 0; i < data.length(); i++) {
                    cp_immunizationBean = gson.fromJson(data.getJSONObject(i)+"", CP_ImmunizationBean.class);
                    cp_immunizationBeans.add(cp_immunizationBean);
                    cp_immunizationBean = null;
                }

                Fragment fragment = new FragmentCPImmunization();
                openFragment(fragment,"",activity);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_CP_INSURANCE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                cp_insuranceBeans = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    cp_insuranceBeans.add(gson.fromJson(data.getJSONObject(i)+"", CP_InsuranceBean.class));
                }
                Fragment fragment = new FragmentCPInsurance();
                openFragment(fragment,"",activity);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public static void openFragment(Fragment fragment, String title, Activity activity) {
        // Home.mDrawerLayout.closeDrawer(sv);
        //Fragment fragment = f;

        //Home.tv_activity_title.setText(title);
        //DATA.fragmentsList.add(fragment);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }
}
