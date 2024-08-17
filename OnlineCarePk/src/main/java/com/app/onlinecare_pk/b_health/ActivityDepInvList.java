package com.app.onlinecare_pk.b_health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.util.DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDepInvList extends BaseActivity{


    ListView lvDepInv;
    TextView tvNoData;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;
            ApiManager apiManager = new ApiManager(ApiManager.DEP_INV_LIST + prefs.getString("id", ""),"post", null, apiCallBack, activity);
            apiManager.loadURL();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dep_inv_list);

        lvDepInv = findViewById(R.id.lvDepInv);
        tvNoData = findViewById(R.id.tvNoData);

        lvDepInv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //openActivity.open(ActivityBecksDepression.class, false);

                selectedDepInvListBean = depInvListBeans.get(position);

                Intent intent = new Intent(activity,ActivityBecksDepression.class);
                intent.putExtra("isEdit",true);
                startActivity(intent);
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityBecksDepression.class, false);
            }
        });


        ApiManager apiManager = new ApiManager(ApiManager.DEP_INV_LIST + prefs.getString("id", ""),"post", null, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<DepInvListBean> depInvListBeans;
    public static DepInvListBean selectedDepInvListBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.DEP_INV_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                depInvListBeans = new ArrayList<>();

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                for (int i = 0; i < data.length(); i++) {
                    depInvListBeans.add(gson.fromJson(data.getJSONObject(i)+"", DepInvListBean.class));
                }


                lvDepInv.setAdapter(new DepInvListAdapter(activity, depInvListBeans));


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
