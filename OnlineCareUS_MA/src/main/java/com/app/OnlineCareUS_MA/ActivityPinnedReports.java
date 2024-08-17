package com.app.OnlineCareUS_MA;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.app.OnlineCareUS_MA.adapter.ReportsPinedAdapter;
import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.model.FileBean;
import com.app.OnlineCareUS_MA.model.FolderBean;
import com.app.OnlineCareUS_MA.util.CheckInternetConnection;
import com.app.OnlineCareUS_MA.util.CustomToast;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.GloabalMethods;
import com.app.OnlineCareUS_MA.util.OpenActivity;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityPinnedReports extends AppCompatActivity {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    CustomToast customToast;
    OpenActivity openActivity;

    FloatingGroupExpandableListView lvPinnedReports;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned_reports);

        activity = ActivityPinnedReports.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);

        lvPinnedReports = (FloatingGroupExpandableListView) findViewById(R.id.lvPinnedReports);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        if (checkInternetConnection.isConnectedToInternet()) {
            patient_folders_files();
        } else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
        }

        lvPinnedReports.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                DATA.selectedPtReportUrl = folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url;
                openActivity.open(ViewReportFull.class, false);

                DATA.print("--click");
                return true;
            }
        });
    }


    ArrayList<FolderBean> folderBeens;
    public void patient_folders_files() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);

        String reqURL = DATA.baseUrl+"patient_folders_files";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL,params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in patient_folders_files "+content);

                    folderBeens = new ArrayList<>();
                    FolderBean temp;

                    try {
                        JSONArray jsonArray = new JSONObject(content).getJSONArray("reports");

                        if(jsonArray.length() == 0){
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            tvNoData.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

                            ArrayList<FileBean> fileBeens = new ArrayList<>();
                            FileBean fileBeanTemp;

                            String id = jsonArray.getJSONObject(i).getString("id");
                            String patient_id = jsonArray.getJSONObject(i).getString("patient_id");
                            String sub_patient_id = jsonArray.getJSONObject(i).getString("sub_patient_id");
                            String folder_name = jsonArray.getJSONObject(i).getString("folder_name");
                            String dateof = jsonArray.getJSONObject(i).getString("dateof");
                            String typeof = jsonArray.getJSONObject(i).getString("typeof");
                            String report_name = jsonArray.getJSONObject(i).getString("report_name");

                            if(jsonArray.getJSONObject(i).has("files")){
                                JSONArray files = jsonArray.getJSONObject(i).getJSONArray("files");

                                for (int j = 0; j < files.length(); j++) {
                                    String id1 = files.getJSONObject(j).getString("id");
                                    String patient_id1 = files.getJSONObject(j).getString("patient_id");
                                    String sub_patient_id1 = files.getJSONObject(j).getString("sub_patient_id");
                                    String folder_id = files.getJSONObject(j).getString("folder_id");
                                    String report_name1 = files.getJSONObject(j).getString("report_name");
                                    String file_display_name = files.getJSONObject(j).getString("file_display_name");
                                    String dateof1 = files.getJSONObject(j).getString("dateof");
                                    String typeof1 = files.getJSONObject(j).getString("typeof");
                                    String report_url = files.getJSONObject(j).getString("report_url");
                                    String report_thumb = files.getJSONObject(j).getString("report_thumb");

                                    fileBeanTemp = new FileBean(id1,patient_id1,sub_patient_id1,folder_id,report_name1,file_display_name
                                            ,dateof1,typeof1,report_url,report_thumb);
                                    fileBeens.add(fileBeanTemp);
                                    fileBeanTemp = null;
                                }
                            }

                            temp = new FolderBean(id,patient_id,sub_patient_id,folder_name,dateof,typeof,report_name,fileBeens);
                            folderBeens.add(temp);

                        }
                        ReportsPinedAdapter adapter = new ReportsPinedAdapter(activity, folderBeens);
                        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
                        lvPinnedReports.setAdapter(wrapperAdapter);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
}
