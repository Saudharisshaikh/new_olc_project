package com.covacard;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Engr G M on 2/20/2017.
 */

public class App extends Application  implements AppLifecycleHandler.LifeCycleDelegate {

    private static App instance;
    SharedPreferences prefs;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //initImageLoader(getApplicationContext());


        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);




        AppLifecycleHandler lifeCycleHandler = new AppLifecycleHandler((AppLifecycleHandler.LifeCycleDelegate)this);
        this.registerLifecycleHandler(lifeCycleHandler);



        // register to be informed of activities starting up
        /*registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                System.out.println("--onActivityCreated "+activity.getClass().getName());
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
                DATA.baseUrl = DATA.ROOT_Url+prefs.getString("folder_name","no_folder_recieved_in_login")+DATA.POST_FIX;
                System.out.println("-- DATA.baseUrl in Application Class "+DATA.baseUrl);
            }
            @Override
            public void onActivityDestroyed(Activity arg0) {}
            @Override
            public void onActivityPaused(Activity arg0) {}
            @Override
            public void onActivityResumed(Activity arg0) {}
            @Override
            public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {}
            @Override
            public void onActivityStarted(Activity arg0) {}
            @Override
            public void onActivityStopped(Activity arg0) {}
        });*/






        Fresco.initialize(this);
    }


    public static synchronized App getInstance() {
        return instance;
    }

    /*public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    //final String CHAT_SERVER_URL = "http://ec2-18-220-180-242.us-east-2.compute.amazonaws.com:3000";
    //final String CHAT_SERVER_URL = "https://telelivecare.com:3000/";
    final String CHAT_SERVER_URL = "https://onlinecare.com:3000/";


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }






    public static boolean isAppInForground = false;
    @Override
    public void onAppBackgrounded() {
        Log.d("-- App OLC lifeCicle: ", "App in background");
        isAppInForground = false;
    }
    @Override
    public void onAppForegrounded() {
        Log.d("-- App OLC lifeCicle: ", "App in foreground");
        isAppInForground = true;
    }

    private final void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        this.registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks)lifeCycleHandler);
        this.registerComponentCallbacks((ComponentCallbacks)lifeCycleHandler);
    }

}
