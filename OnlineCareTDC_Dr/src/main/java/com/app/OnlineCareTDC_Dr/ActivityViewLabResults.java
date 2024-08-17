package com.app.OnlineCareTDC_Dr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.app.OnlineCareTDC_Dr.adapter.LabRequestAdapter;
import com.app.OnlineCareTDC_Dr.adapter.VVReportImagesAdapter2;
import com.app.OnlineCareTDC_Dr.api.ApiCallBack;
import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.model.LabReqDetailsModel;
import com.app.OnlineCareTDC_Dr.model.LabRequestModel;
import com.app.OnlineCareTDC_Dr.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.DialogPatientInfo;
import com.app.OnlineCareTDC_Dr.util.ExpandableHeightGridView;
import com.app.OnlineCareTDC_Dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ActivityViewLabResults extends BaseActivity {


    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    TextView tvNoLabReq;

    String file;
    ArrayList<String> vvImgs;

    ExpandableHeightGridView gvReportImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lab_results);

        activity = ActivityViewLabResults.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        openActivity = new OpenActivity(activity);

        gvReportImages = findViewById(R.id.gvReportImages);

        viewLabRes(LabRequestAdapter.labId);
    }


    public void viewLabRes(String id)
    {
        RequestParams params = new RequestParams();
        params.put("doctor_id" , prefs.getString("id", ""));
        params.put("id" , id);
        ApiManager apiManager = new ApiManager(ApiManager.VIEW_LAB_RESULT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
       if (apiName.contains(ApiManager.VIEW_LAB_RESULT))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    vvImgs = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        String id = data.getJSONObject(i).getString("id");
                        String lab_request_id = data.getJSONObject(i).getString("lab_request_id");
                        file = data.getJSONObject(i).getString("file");
                        String dateof = data.getJSONObject(i).getString("dateof");

                        vvImgs.add(data.getJSONObject(i).getString("file"));
                    }

                    gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
                    gvReportImages.setExpanded(true);
                    gvReportImages.setPadding(5,5,5,5);

                    gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}