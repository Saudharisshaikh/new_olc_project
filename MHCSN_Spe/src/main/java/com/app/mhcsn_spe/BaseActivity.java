package com.app.mhcsn_spe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.app.mhcsn_spe.api.ApiCallBack;
import com.app.mhcsn_spe.api.CustomSnakeBar;
import com.app.mhcsn_spe.util.CheckInternetConnection;
import com.app.mhcsn_spe.util.CustomToast;
import com.app.mhcsn_spe.util.DATA;
import com.app.mhcsn_spe.util.HideShowKeypad;
import com.app.mhcsn_spe.util.OpenActivity;
import com.app.mhcsn_spe.util.SharedPrefsHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Engr G M on 10/3/2017.
 */

public class BaseActivity extends AppCompatActivity implements ApiCallBack{

    // Activity code here

    public Activity activity;
    public AppCompatActivity appCompatActivity;
    public CheckInternetConnection checkInternetConnection;
    public CustomToast customToast;
    public OpenActivity openActivity;
    public HideShowKeypad hideShowKeypad;
    //Database database;
    public CustomSnakeBar customSnakeBar;
    //public SharedPrefsHelper sharedPrefsHelper;
    public SharedPreferences prefs;
    public ApiCallBack apiCallBack;
    public SharedPrefsHelper sharedPrefsHelper;
    public Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = BaseActivity.this;
        appCompatActivity = BaseActivity.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        //sharedPrefsHelper = SharedPrefsHelper.getInstance();
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        gson = new GsonBuilder().create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {

            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

    }
}
