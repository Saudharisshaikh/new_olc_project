package com.app.mhcsn_dr.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.app.mhcsn_dr.R;
import com.app.mhcsn_dr.adapter.ReportsPinedAdapter;
import com.app.mhcsn_dr.api.ApiManager;
import com.app.mhcsn_dr.api.Dialog_CustomProgress;
import com.app.mhcsn_dr.model.FileBean;
import com.app.mhcsn_dr.model.FolderBean;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Engr G M on 12/20/2017.
 */

public class ReportsDialog {

    AppCompatActivity activity;

    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    OpenActivity openActivity;
    SharedPreferences prefs;
    Dialog_CustomProgress dialog_customProgress;


    public ReportsDialog(AppCompatActivity activity) {
        this.activity = activity;

        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        dialog_customProgress = new Dialog_CustomProgress(activity);
    }


    FloatingGroupExpandableListView lvPinnedReports;
    TextView tvNoData;

    public void showAllReportsDialog(){
        final Dialog reportsDialog = new Dialog(activity);
        reportsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportsDialog.setContentView(R.layout.dialog_all_reports);


        lvPinnedReports = (FloatingGroupExpandableListView) reportsDialog.findViewById(R.id.lvPinnedReports);
        tvNoData = (TextView) reportsDialog.findViewById(R.id.tvNoData);
        reportsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportsDialog.dismiss();
            }
        });


        lvPinnedReports.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                DATA.selectedPtReportUrl = folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url;
                //openActivity.open(ViewReportFull.class, false);

                showReportDialog();

                DATA.print("--click");
                return true;
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reportsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reportsDialog.show();
        reportsDialog.getWindow().setAttributes(lp);


        if (checkInternetConnection.isConnectedToInternet()) {
            patient_folders_files();
        } else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
        }
    }


    public void showReportDialog(){
        final Dialog reportDialog = new Dialog(activity);
        reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportDialog.setContentView(R.layout.dialog_view_report);

        PhotoView ivPhoto = (PhotoView) reportDialog.findViewById(R.id.ivPhoto);
        reportDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog.dismiss();
            }
        });
        WebView wvReport= (WebView) reportDialog.findViewById(R.id.wvReport);
        wvReport.getSettings().setJavaScriptEnabled(true);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reportDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reportDialog.show();
        reportDialog.getWindow().setAttributes(lp);

        if (DATA.selectedPtReportUrl.endsWith("pdf") || DATA.selectedPtReportUrl.endsWith("doc") || DATA.selectedPtReportUrl.endsWith("docx")) {
            wvReport.setVisibility(View.VISIBLE);
            ivPhoto.setVisibility(View.GONE);
            dialog_customProgress.showProgressDialog();
            wvReport.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    dialog_customProgress.dismissProgressDialog();
                    super.onPageFinished(view, url);
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                }
            });
            wvReport.loadUrl("https://docs.google.com/gview?embedded=true&url="+DATA.selectedPtReportUrl);
        }else{
            wvReport.setVisibility(View.GONE);
            ivPhoto.setVisibility(View.VISIBLE);
            DATA.loadImageFromURL(DATA.selectedPtReportUrl,R.drawable.ic_placeholder_2,ivPhoto);
        }
    }


    ArrayList<FolderBean> folderBeens;
    public void patient_folders_files() {

        dialog_customProgress.showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();
        params.put("patient_id",DialogPatientInfo.patientIdGCM);

        String reqURL = DATA.baseUrl+"patient_folders_files";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                dialog_customProgress.dismissProgressDialog();
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
                dialog_customProgress.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
}
