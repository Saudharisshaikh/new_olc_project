package com.digihealthcard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.digihealthcard.api.ApiCallBack;
import com.digihealthcard.api.CustomSnakeBar;
import com.digihealthcard.permission.PermissionsChecker;
import com.digihealthcard.util.CheckInternetConnection;
import com.digihealthcard.util.CustomToast;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.HideShowKeypad;
import com.digihealthcard.util.OpenActivity;
import com.digihealthcard.util.SharedPrefsHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;


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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.WHITE);
            }catch (Exception e){e.printStackTrace();}
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if(!isDarkTheme()){
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar , getTheme()));
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public boolean isDarkTheme() {
        boolean isDarkThemeOn = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)  == Configuration.UI_MODE_NIGHT_YES;
        return isDarkThemeOn;
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


    // access modifiers changed from: protected /
    /*public void attachBaseContext(Context context) {
        super.attachBaseContext(updateBaseContextLocale(context));
    }*/

    private Context updateBaseContextLocale(Context context) {
        Locale locale = new Locale(("en"));
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= 24) {
            return updateResourcesLocale(context, locale);
        }
        return updateResourcesLocaleLegacy(context, locale);
    }

    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
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
