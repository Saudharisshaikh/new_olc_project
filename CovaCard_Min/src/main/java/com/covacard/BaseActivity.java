package com.covacard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.covacard.api.ApiCallBack;
import com.covacard.api.CustomSnakeBar;
import com.covacard.permission.PermissionsChecker;
import com.covacard.util.CheckInternetConnection;
import com.covacard.util.CustomToast;
import com.covacard.util.DATA;
import com.covacard.util.HideShowKeypad;
import com.covacard.util.OpenActivity;
import com.covacard.util.SharedPrefsHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by Engr G M on 10/3/2017.
 */

public class BaseActivity extends AppCompatActivity implements  ApiCallBack{

    public Activity activity;
    public AppCompatActivity appCompatActivity;
    public CheckInternetConnection checkInternetConnection;
    public CustomToast customToast;
    public OpenActivity openActivity;
    public HideShowKeypad hideShowKeypad;
    //public Database database;
    public CustomSnakeBar customSnakeBar;
    public SharedPreferences prefs;
    public ApiCallBack apiCallBack;
    public SharedPrefsHelper sharedPrefsHelper;
    public Gson gson;

    public PermissionsChecker permissionsChecker;

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
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        //database = new Database(activity);
        apiCallBack = this;
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        gson = new GsonBuilder().create();

        permissionsChecker = new PermissionsChecker(activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.WHITE);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

    }


    // Activity code here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_home) {

            Intent intent = new Intent(getApplicationContext(), ActivityCovacardHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else*/
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }






    /*private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(activity);

            System.out.println("-- heightDiff = "+heightDiff + " , contentViewTop = "+contentViewTop);
            if(heightDiff <= contentViewTop){
                onHideKeyboard();
                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);
                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };


    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {}

    protected void onHideKeyboard() {}

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }
        rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }*/
}
