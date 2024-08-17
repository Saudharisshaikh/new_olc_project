package com.covacard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.covacard.R;
import com.covacard.api.ApiManager;
import com.covacard.adapter.InvoiceAdapter;
import com.covacard.model.InvoiceBean;
import com.covacard.util.DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityInvoices extends ActivityBaseDrawer {


    ListView lvCardsList;
    TextView tvNoData;
    SwipeRefreshLayout swiperefresh;

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
        //setContentView(R.layout.activity_id_cards_list);
        getLayoutInflater().inflate(R.layout.activity_invoices, container_frame);

        lvCardsList = findViewById(R.id.lvCardsList);
        tvNoData = findViewById(R.id.tvNoData);

        /*lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);

                selectedCardBean = cardBeans.get(position);
            }
        });*/

        /*setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ID Cards");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddIdCard.class, false);
            }
        });*/

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        tvToolbarTitle.setText("Payment Invoices");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/


        /*lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityCardDetail.class, false);
                ActivityCardDetail.cardBeanSelected = cardBeans.get(position);
                Intent intent = new Intent(activity, ActivityCardDetail.class);
                intent.putExtra("isFromIdCard", true);
                startActivity(intent);
            }
        });*/


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


        loadListData();


        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }

    public void loadListData(){
        String all_invoices_json = sharedPrefsHelper.get("all_invoices_json", "");
        if(!TextUtils.isEmpty(all_invoices_json)){
            parseData(all_invoices_json);
            ApiManager.shouldShowLoader = false;
        }
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.INVOICES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<InvoiceBean> invoiceBeans;
    InvoiceAdapter invoiceAdapter;

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.INVOICES)){
            swiperefresh.setRefreshing(false);
            parseData(content);
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

            Type listType = new TypeToken<ArrayList<InvoiceBean>>() {}.getType();
            invoiceBeans = new Gson().fromJson(data.toString(), listType);

            invoiceAdapter = new InvoiceAdapter(activity, invoiceBeans);
            lvCardsList.setAdapter(invoiceAdapter);

            sharedPrefsHelper.save("all_invoices_json",content);

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
