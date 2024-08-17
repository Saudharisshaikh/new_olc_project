package com.app.mhcsn_spe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_spe.adapter.SwitchHospAdapter;
import com.app.mhcsn_spe.api.ApiManager;
import com.app.mhcsn_spe.model.HospitalBean;
import com.app.mhcsn_spe.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySwitchHosp extends BaseActivity {

    TextView tvNoHosp;
    ListView lvHospitals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_hosp);

        tvNoHosp = (TextView) findViewById(R.id.tvNoHosp);
        lvHospitals = (ListView) findViewById(R.id.lvHospitals);


        lvHospitals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                hospitalBeanSelected = hospitalBeens.get(position);

                RequestParams params = new RequestParams();
                params.put("doctor_id",prefs.getString("id",""));
                params.put("hospital_id",hospitalBeanSelected.id);
                ApiManager apiManager = new ApiManager(ApiManager.SWITCH_HOSPITAL,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });


        RequestParams params = new RequestParams("doctor_id",prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_MY_HOSPITALS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    ArrayList<HospitalBean> hospitalBeens;
    HospitalBean hospitalBeanSelected;
    SwitchHospAdapter hospitalAdapter;

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if(apiName.equals(ApiManager.GET_MY_HOSPITALS)){
            try {
                //JSONObject jsonObject = new JSONObject(content);
                //JSONArray data = jsonObject.getJSONArray("data");
                JSONArray data = new JSONArray(content);
                if(data.length() == 0){
                    tvNoHosp.setVisibility(View.VISIBLE);
                }else {
                    tvNoHosp.setVisibility(View.GONE);
                }
                hospitalBeens = new ArrayList<HospitalBean>();
                HospitalBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String hospital_name = data.getJSONObject(i).getString("hospital_name");
                    String folder_name = data.getJSONObject(i).getString("folder_name");

                    bean = new HospitalBean(id,hospital_name,folder_name);
                    hospitalBeens.add(bean);
                    bean = null;
                }

                hospitalAdapter = new SwitchHospAdapter(activity, hospitalBeens);
                lvHospitals.setAdapter(hospitalAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SWITCH_HOSPITAL)) {

            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(hospitalBeanSelected != null){
                        prefs.edit().putString("folder_name",hospitalBeanSelected.folder_name).apply();
                        DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;

                        AlertDialog alertDialog =
                                new AlertDialog.Builder(activity).setTitle(getString(R.string.app_name))
                                        .setMessage("Your hospital has been changed to "+hospitalBeanSelected.hospital_name+".")
                                        .setPositiveButton("Done",null).create();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(hospitalAdapter != null){
                                    hospitalAdapter.notifyDataSetChanged();
                                }
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                }else {
                    customToast.showToast("Hospital not changed. Please try again later",0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
