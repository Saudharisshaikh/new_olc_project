package com.app.OnlineCareTDC_Pt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.OnlineCareTDC_Pt.adapter.ConversationAdapter;
import com.app.OnlineCareTDC_Pt.adapter.LabRequestAdapter;
import com.app.OnlineCareTDC_Pt.api.ApiCallBack;
import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.model.ConversationBean;
import com.app.OnlineCareTDC_Pt.model.LabReqDetailsModel;
import com.app.OnlineCareTDC_Pt.model.LabRequestModel;
import com.app.OnlineCareTDC_Pt.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.Database;
import com.app.OnlineCareTDC_Pt.util.EasyAES;
import com.app.OnlineCareTDC_Pt.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class ActivityLabRequests extends BaseActivity {


    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    TextView tvNoLabReq;

    ListView lvLabRequest;
    ArrayList<LabRequestModel> labRequestModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_requests);

        activity = ActivityLabRequests.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        openActivity = new OpenActivity(activity);

        tvNoLabReq = (TextView) findViewById(R.id.tvNoLabReq);
        lvLabRequest = (ListView) findViewById(R.id.lvLabRequest);

        getLabReq();
    }

    private void getLabReq()
    {
        RequestParams params = new RequestParams();
        params.put("patient_id" , prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_ALL_LAB_REQUEST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void viewLabReq(String id)
    {
        RequestParams params = new RequestParams();
        params.put("patient_id" , prefs.getString("id", ""));
        params.put("id" , id);
        ApiManager apiManager = new ApiManager(ApiManager.VIEW_LAB_REQUEST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.contains(ApiManager.GET_ALL_LAB_REQUEST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("success")) {
                    JSONArray data = jsonObject.getJSONArray("data");

                    if (data.length() == 0) {
                        tvNoLabReq.setVisibility(View.VISIBLE);
                    } else {
                        tvNoLabReq.setVisibility(View.GONE);
                    }

                    labRequestModels = new ArrayList<LabRequestModel>();
                    LabRequestModel bean;
                    for (int i = 0; i < data.length(); i++) {
                        String id = data.getJSONObject(i).getString("id");
                        String first_name = data.getJSONObject(i).getString("first_name");
                        String last_name = data.getJSONObject(i).getString("last_name");
                        String title = data.getJSONObject(i).optString("title");
                        String dateof = data.getJSONObject(i).getString("dateof");

                        bean = new LabRequestModel(id, title, first_name, last_name, dateof);
                        labRequestModels.add(bean);
                        bean = null;
                    }
                    lvLabRequest.setAdapter(new LabRequestAdapter(activity, labRequestModels));
                }
            } catch (JSONException e) {
                DATA.print("-- json exception getMessagesConversation");
                e.printStackTrace();
            }
        }

        else if (apiName.contains(ApiManager.VIEW_LAB_REQUEST))
        {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");

                LabReqDetailsModel labReqDetailsModel;
                String email_html = null;
                if (status.equalsIgnoreCase("success")) {

                    JSONObject data = jsonObject.getJSONObject("data");
                    String id = data.getString("id");
                    String doctor_id = data.getString("doctor_id");
                    String patient_id = data.getString("patient_id");
                    String diagnosis_desc = data.getString("diagnosis_desc");
                    email_html = data.optString("email_html");
                    String dateof = data.getString("dateof");

                    labReqDetailsModel = new LabReqDetailsModel(id, doctor_id, patient_id, diagnosis_desc, email_html , dateof);

                    if(!email_html.isEmpty()) {
                        //showWebviewWithHTMLDialog(testResultBeans.get(position).bodyhtml, "Report Preview");
                        String filePath = writeFileOnInternalStorage(activity,email_html);
                        if (filePath != null) {
                            File htmlFile = new File(filePath);
                            System.out.println("-- file exist " + htmlFile.exists() + " file size: " + htmlFile.length());
                            Uri fileURI = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".fileprovider", htmlFile);
                            //Uri fileURI = Uri.fromFile(new File(filePath));
                            System.out.println("-- file Path: " + filePath + " |||| File URI : " + fileURI);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setDataAndType(fileURI, "text/html");
                            //browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                            browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            activity.startActivity(browserIntent);
                        }
                    }
                    else if (email_html.isEmpty())
                    {

                    }
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String writeFileOnInternalStorage(Context context, String fileBody){
        System.out.println("-- showWebviewDialog htmlString : "+fileBody);
        String filePath = null;
        //File dir = new File(context.getFilesDir(), "tempHTML");
        File dir = new File(context.getCacheDir(), "tempHTML");
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            File gpxfile = new File(dir, "testReport.html");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(fileBody);
            writer.flush();
            writer.close();
            filePath = gpxfile.getAbsolutePath();
        } catch (Exception e){
            e.printStackTrace();
        }
        return filePath;
    }
}