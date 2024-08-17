package com.app.OnlineCareTDC_Pt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.OnlineCareTDC_Pt.api.ApiCallBack;
import com.app.OnlineCareTDC_Pt.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Pt.util.CustomToast;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.GloabalMethods;
import com.app.OnlineCareTDC_Pt.util.OpenActivity;

public class ActivityGyantChat extends BaseActivity{


    Activity activity;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    OpenActivity openActivity;
    SharedPreferences prefs;

    Button btnYes, btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyant_chat);

        getSupportActionBar().hide();

        activity = ActivityGyantChat.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);


        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        btnNo.setOnClickListener(view ->
        {
            Intent intent = new Intent(getApplicationContext(),MainActivityNew.class);
            intent.putExtra("show_med_history_popup", true);
            startActivity(intent);
            finish();
        });

        btnYes.setOnClickListener(view ->
        {
            new GloabalMethods(this).showWebviewDialog("https://web.dev.gyantts.com/?org=tdc-fd&sessionToken=9afb88a97b21213cfd000a53a70f637b3fc368b53fc40a46","");
        });
    }
}