package com.app.amnm_ma;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.net.URISyntaxException;

import io.agora.openlive.Constants;
import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PrefManager;
import io.agora.rtc.RtcEngine;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Engr G M on 2/20/2017.
 */

public class OnlineCareDr extends Application implements AppLifecycleHandler.LifeCycleDelegate{

    SharedPreferences prefs;

    private static OnlineCareDr instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initImageLoader(getApplicationContext());


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


        //===============Angora.io Starts============================================
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();
        //===============Angora.io Ends============================================
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static synchronized OnlineCareDr getInstance() {
        return instance;
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





    //===============Angora.io Starts============================================
    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    private void initConfig() {
        SharedPreferences pref = PrefManager.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
    //===============Angora.io Ends============================================
}
