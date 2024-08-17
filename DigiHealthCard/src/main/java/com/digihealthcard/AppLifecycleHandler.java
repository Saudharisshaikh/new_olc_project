package com.digihealthcard;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.digihealthcard.api.ApiManager;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.SharedPrefsHelper;


public final class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private boolean appInForeground;
    private final LifeCycleDelegate lifeCycleDelegate;



    public AppLifecycleHandler(LifeCycleDelegate lifeCycleDelegate) {//@NotNull
        //Intrinsics.checkParameterIsNotNull(lifeCycleDelegate, "lifeCycleDelegate");
        super();
        this.lifeCycleDelegate = lifeCycleDelegate;
    }

    public void onActivityPaused(@Nullable Activity p0) {
    }

    public void onActivityResumed(@Nullable Activity p0) {
        if (!this.appInForeground) {
            this.appInForeground = true;
            this.lifeCycleDelegate.onAppForegrounded();
        }

    }

    public void onActivityStarted(@Nullable Activity p0) {
    }

    public void onActivityDestroyed(@Nullable Activity p0) {
    }

    public void onActivitySaveInstanceState(@Nullable Activity p0, @Nullable Bundle p1) {
    }

    public void onActivityStopped(@Nullable Activity p0) {
    }

    public void onActivityCreated(@Nullable Activity activity, @Nullable Bundle p1) {

        //OLC Code
        try {
            //SharedPreferences prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            //DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
            DATA.baseUrl = SharedPrefsHelper.getInstance().get("baseURL", ApiManager.COVACARD_BASE_URL);
            if(SharedPrefsHelper.getInstance().get("debug_logs", false)){
                System.out.println("--onActivityCreated "+activity.getClass().getName());
                System.out.println("-- DATA.baseUrl in Application Class "+DATA.baseUrl);
            }
            // new activity created; force its orientation to portrait
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch (Exception e){
            e.printStackTrace();
        }
        //OLC Code
    }

    public void onLowMemory() {
    }

    public void onConfigurationChanged(@Nullable Configuration p0) {
    }

    public void onTrimMemory(int level) {
        if (level == 20) {
            this.appInForeground = false;
            this.lifeCycleDelegate.onAppBackgrounded();
        }

    }





    public interface LifeCycleDelegate {
        void onAppBackgrounded();

        void onAppForegrounded();
    }
}


