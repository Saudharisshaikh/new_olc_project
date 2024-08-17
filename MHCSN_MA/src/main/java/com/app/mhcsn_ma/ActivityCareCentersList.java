package com.app.mhcsn_ma;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_ma.adapter.CareCentersAdapter;
import com.app.mhcsn_ma.api.ApiManager;
import com.app.mhcsn_ma.model.CareCenterBean;
import com.app.mhcsn_ma.model.CareRequestBean;
import com.app.mhcsn_ma.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCareCentersList extends BaseActivity{


    ListView lvCareCenters;
    TextView tvNoData;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_centers_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Care Centers");
        }

        lvCareCenters = findViewById(R.id.lvCareCenters);
        tvNoData = findViewById(R.id.tvNoData);
        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(careCentersAdapter != null){
                    careCentersAdapter.filter(s.toString());
                }
            }
        });

        lvCareCenters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityCareCenterDocList.careCenterBean = careCenterBeans.get(position);
                openActivity.open(ActivityCareCenterDocList.class, true);
            }
        });


        loadListData();
    }

    public static CareRequestBean careRequestBean;
    public void loadListData(){
        RequestParams params = new RequestParams("patient_id", careRequestBean.id);
        ApiManager apiManager = new ApiManager(ApiManager.GET_URGENT_CARE_CENTERS,"post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<CareCenterBean> careCenterBeans;
    CareCentersAdapter careCentersAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.contains(ApiManager.GET_URGENT_CARE_CENTERS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<CareCenterBean>>() {}.getType();
                careCenterBeans = gson.fromJson(data.toString(), listType);
                careCentersAdapter = new CareCentersAdapter(activity, careCenterBeans);
                lvCareCenters.setAdapter(careCentersAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

}
