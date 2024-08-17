package com.app.mhcsn_cp.reliance.preschistory;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragmentBtn;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityPrescHistory extends BaseActivity{


    PagingListView lvPrescHistory;
    TextView tvNoData;

    Button btnHistoryFrom,btnHistoryTo;

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
        setContentView(R.layout.activity_presc_history);


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Prescription History");
        }

        lvPrescHistory = findViewById(R.id.lvPrescHistory);
        tvNoData = findViewById(R.id.tvNoData);


        btnHistoryFrom = (Button) findViewById(R.id.btnHistoryFrom);
        btnHistoryTo = (Button) findViewById(R.id.btnHistoryTo);


        Date sDate = new Date();
//        String sDateStr = new SimpleDateFormat("yyyy-MM-dd").format(sDate);
//        btnHistoryTo.setTag(sDateStr);
//        btnHistoryTo.setText(new SimpleDateFormat("dd/MM/yyyy").format(sDate));
        String sDateStr = new SimpleDateFormat("MM/dd/yyyy").format(sDate);
        btnHistoryTo.setTag(sDateStr);
        btnHistoryTo.setText(sDateStr);

        //int noOfDays = -7; //i.e one weeks
        int noOfDays = -30; //i.e one month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date afterWeekDate = calendar.getTime();
//        String toDateStr =  new SimpleDateFormat("yyyy-MM-dd").format(afterWeekDate);
//        btnHistoryFrom.setTag(toDateStr);
//        btnHistoryFrom.setText(new SimpleDateFormat("dd/MM/yyyy").format(afterWeekDate));
        String toDateStr =  new SimpleDateFormat("MM/dd/yyyy").format(afterWeekDate);
        btnHistoryFrom.setTag(toDateStr);
        btnHistoryFrom.setText(toDateStr);


        btnHistoryFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragmentBtn(btnHistoryFrom);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        btnHistoryTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragmentBtn(btnHistoryTo);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });



        lvPrescHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*try {

                }catch (Exception e){e.printStackTrace();}*/

                //viewForm(position);

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



        prescHistoryAdapter = new PrescHistoryAdapter(activity, prescHistoryBeans);
        lvPrescHistory.setAdapter(prescHistoryAdapter);

        lvPrescHistory.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {


                //DATA.print("-- pagging listener called at page :"+page);

                if(!prescHistoryBeans.isEmpty()){
                    try {
                        end_date = prescHistoryBeans.get(prescHistoryBeans.size() - 1).last_fill_date;
                        loadPrescHistory(false);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                            /*if (page < finalTotalPages) {
                                loadChat(page + 1);
                            }*/


                            /*int totalPages = 0;
                            try {
                                totalPages = (int) Math.ceil(pages_count/page_size);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            DATA.print("-- total pages: "+totalPages+" , page : "+page);

                            if (page < totalPages) {
                                loadChat(page + 1);

                                lvMessages.onFinishLoading(true, messageBeansAPI);
                            } else {
                                lvMessages.onFinishLoading(false, null);
                            }*/
            }
        });

        setDatesAndCallLoadPrescHistory();
    }

    /*public void viewForm(int position){
        //String formURL = DATA.baseUrl + "assessments/dietitary_assessment_view/"+assessmentsSummaryBeans.get(position).id+"?platform=mobile";

        String formURL = DATA.baseUrl + assessmentsSummaryBeans.get(position).url+"?platform=mobile";
        new GloabalMethods(activity).showWebviewDialog(formURL, assessmentsSummaryBeans.get(position).form_name);
    }*/

    String end_date = "",start_date = "";
    public void setDatesAndCallLoadPrescHistory(){
        start_date = btnHistoryFrom.getTag().toString();
        end_date = btnHistoryTo.getTag().toString();

        try {
            int compare = new SimpleDateFormat("MM/dd/yyyy").parse(end_date).compareTo(new SimpleDateFormat("MM/dd/yyyy").parse(start_date));
            DATA.print("-- compare: "+compare);
            if(compare < 0){
                btnHistoryTo.setError("To date cant't be less than from date");
            }else{
                btnHistoryTo.setError(null);

                prescHistoryBeans.clear();
                loadPrescHistory(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    public void loadPrescHistory(boolean showLoader){

        RequestParams params = new RequestParams();
        String pId = DATA.selectedUserCallId;
        if(pId.equalsIgnoreCase("17")){// for testing -- only jack 17 :P
            pId = "448";
        }
        params.put("patient_id", pId);//"448"
        params.put("end_date", end_date);
        params.put("start_date", start_date);
        ApiManager apiManager = new ApiManager(ApiManager.API_MEDHISTORY, "post", params, apiCallBack, activity);
        ApiManager.shouldShowPD = showLoader;
        apiManager.loadURL();
    }

    List<PrescHistoryBean> prescHistoryBeans = new ArrayList<>();
    PrescHistoryAdapter prescHistoryAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.API_MEDHISTORY)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    JSONArray medicine = jsonObject.getJSONArray("medicine");

                /*if(medicine.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }*/

                    Type listType = new TypeToken<ArrayList<PrescHistoryBean>>() {}.getType();
                    List<PrescHistoryBean> prescHistoryBeansAPI = new Gson().fromJson(medicine.toString(), listType);

                    if(prescHistoryBeansAPI != null){
                        //lvPrescHistory.setAdapter(new PrescHistoryAdapter(activity, prescHistoryBeans));

                        ArrayList<PrescHistoryBean> beansToRemove = new ArrayList<>();
                        for (int i = 0; i < prescHistoryBeans.size(); i++) {
                            if(prescHistoryBeans.get(i).last_fill_date.equalsIgnoreCase(end_date)){
                                beansToRemove.add(prescHistoryBeans.get(i));
                            }
                        }
                        boolean isRemoved = prescHistoryBeans.removeAll(beansToRemove);
                        DATA.print("-- beansToRemove size: "+beansToRemove.size()+ "  isRemoved : "+isRemoved);


                        prescHistoryBeans.addAll(prescHistoryBeansAPI);
                    }
                    prescHistoryAdapter.notifyDataSetChanged();

                    int vis = prescHistoryBeans.isEmpty() ? View.VISIBLE : View.GONE;
                    tvNoData.setVisibility(vis);

                    boolean view_more = jsonObject.optBoolean("view_more");
                    lvPrescHistory.setHasMoreItems(view_more);
                    lvPrescHistory.onFinishLoading(view_more, prescHistoryBeans);

                }else {
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(jsonObject.optString("msg"));

                    lvPrescHistory.setHasMoreItems(false);
                    lvPrescHistory.onFinishLoading(false, prescHistoryBeans);
                    //lvPrescHistory.setIsLoading(false);
                }

            } catch (Exception e) {
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
