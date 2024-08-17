package io.agora.openlive.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.appcompat.app.AlertDialog;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.msu_cp.ActivityInstatntConnect;
import com.app.msu_cp.AfterCallDialog;
import com.app.msu_cp.CustomDialogActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.VCallModule;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DialogPatientInfo;
import com.app.msu_cp.util.ReportsDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.agora.openlive.stats.LocalStatsData;
import io.agora.openlive.stats.RemoteStatsData;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;


public class LiveActivity extends RtcBaseActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    VCallModule vCallModule;
    BroadcastReceiver disconnectSpecialistBroadcast;
    Button btnConnectToConsult;
    ImageButton btnEndCall;
    ImageView live_btn_more;
    TextView tvVCallViewReports,tvVCallViewPtDetails, tvAppName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.angora_activity_live_room);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //config().setChannelName(sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_MULTI);


        initUI();
        initData();
    }

    private void initUI() {
        //================================OLC code starts=========================================================
        activity = LiveActivity.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        vCallModule = new VCallModule(activity);
        btnConnectToConsult = findViewById(R.id.btnConnectToConsult);
        btnEndCall = findViewById(R.id.btnEndCall);
        live_btn_more = findViewById(R.id.live_btn_more);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);

        tvVCallViewReports = findViewById(R.id.tvVCallViewReports);
        tvVCallViewPtDetails = findViewById(R.id.tvVCallViewPtDetails);
        tvAppName = findViewById(R.id.tvAppName);

        setupTopBar();

        /*int vis = MainActivity.isFromInstantConnect ? View.GONE : View.VISIBLE;
        live_btn_more.setVisibility(vis);
        btnConnectToConsult.setVisibility(vis);*/

        btnConnectToConsult.setOnClickListener(vCallModule.getConnectBtnListener(mVideoGridContainer.getChildCount()));

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DATA.isFromDocToDoc) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else if (MainActivity.isFromCallToCoordinator) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else if (MainActivity.isFromCallToEMS) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else if(MainActivity.isFromInstantConnect && !ActivityInstatntConnect.isFromPatientProfile){
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else {
                    if (!DATA.isFromCallHistoryOrMsgs) {
                        DATA.isSOAP_NotesSent = false;
                        Intent i = new Intent(activity, AfterCallDialog.class);
                        startActivity(i);
                    }

                    /*if (MultiPartyVideoCallFragment.getNumPeers() >= 2){
                        DATA.print("--disconnect broadcast sent to specialist");
                        vCallModule.disconnectSpecialist(DATA.selectedDrId,"doctor");
                    }*/
                }
                rtcEngine().enableLocalVideo(true);
                finish();
            }
        });

        disconnectSpecialistBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*((ooVooSdkSampleShowApp) getApplication()).onEndOfCall();
                ((ooVooSdkSampleShowApp) getApplication()).sendEndOfCall();*/
		              /*  Intent i = new Intent(getActivity(), AfterCallDialog.class);
						startActivity(i);*/
                //getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();temasys_v0.11.0 end call
                rtcEngine().enableLocalVideo(true);
                finish();
            }
        };


        //================================OLC code ends=========================================================

        TextView roomName = findViewById(R.id.live_room_name);
        roomName.setText(config().getChannelName());
        roomName.setSelected(true);
        
        initUserIcon();

        int role = getIntent().getIntExtra(
                io.agora.openlive.Constants.KEY_CLIENT_ROLE,
                Constants.CLIENT_ROLE_AUDIENCE);
        boolean isBroadcaster =  (role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);

        //mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(role);
        if (isBroadcaster) startBroadcast();
    }

    private void initUserIcon() {
        Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.angora_fake_user_icon);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
        drawable.setCircular(true);
        ImageView iconView = findViewById(R.id.live_name_board_icon);
        iconView.setImageDrawable(drawable);
    }

    private void initData() {
        mVideoDimension = io.agora.openlive.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
        mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        mMuteAudioBtn.setActivated(false);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);

                //OLC Code onPeerLeft
                DATA.print("-- peer left remaining count : "+mVideoGridContainer.getChildCount());
                if(mVideoGridContainer.getChildCount() < 2){
                    btnEndCall.performClick();
                }
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {

        if(MainActivity.isFromInstantConnect){
            DATA.endCallInstantConnect(activity, ActivityInstatntConnect.call_id_instant_connect);
        }else {
            DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            DATA.endCall(activity);
        }

        super.finish();
        statsManager().clearAllData();
    }

    public void onLeaveClicked(View view) {
        finish();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void onMoreClicked(View view) {
        // Do nothing at the moment
        vCallModule.showDisconnectDialog();
    }

    public void onPushStreamClicked(View view) {
        // Do nothing at the moment
    }

    public void onMuteAudioClicked(View view) {
        //if (!mMuteVideoBtn.isActivated()) return;

        rtcEngine().muteLocalAudioStream(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    public void onMuteVideoClicked(View view) {
        /*if (view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }
        view.setActivated(!view.isActivated());*/

        //OLC code -- JUST mute own camera
        if (view.isActivated()) {
            rtcEngine().muteLocalVideoStream(true);
            rtcEngine().enableLocalVideo(false);
        } else {
            rtcEngine().enableLocalVideo(true);
            rtcEngine().muteLocalVideoStream(false);
        }
        view.setActivated(!view.isActivated());
    }




    //=========================OLC Code==============================================================================

    @Override
    public void onStart() {
        DATA.print("-- ## LiveActivity Angora onstart");
        MultiPartyVideoCallFragment.isInVideoCall = true;
        registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST));
        super.onStart();
    }

    @Override
    public void onStop() {
        DATA.print("-- ## LiveActivity Angora onstop");
        MultiPartyVideoCallFragment.isInVideoCall = false;
        unregisterReceiver(disconnectSpecialistBroadcast);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure ? Do you want to exit the video call")
                .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Not Now",null)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }



    public void setupTopBar(){
        if(DialogPatientInfo.patientIdGCM.isEmpty()){
            if(DATA.selectedUserCallId.isEmpty()){
                //tvVCallViewReports.setVisibility(View.GONE);
                tvVCallViewPtDetails.setVisibility(View.GONE);
                tvVCallViewReports.setText(getString(R.string.app_name));
                tvAppName.setVisibility(View.GONE);
            }else{
                DialogPatientInfo.patientIdGCM = DATA.selectedUserCallId;
                tvVCallViewReports.setVisibility(View.VISIBLE);
                tvVCallViewPtDetails.setVisibility(View.VISIBLE);
                tvAppName.setVisibility(View.VISIBLE);

                tvVCallViewReports.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ReportsDialog(LiveActivity.this).showAllReportsDialog();
                    }
                });
                //tvVCallViewReports.setText(getString(R.string.app_name));//Urgent Care Doc- Disable fucntion view reports
                //tvAppName.setVisibility(View.GONE);//remove these 2 line and uncommit tvVCallViewReports clcick to revert

                tvVCallViewPtDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogPatientInfo(LiveActivity.this).showDialog();
                    }
                });
            }
        }else{
            tvVCallViewReports.setVisibility(View.VISIBLE);
            tvVCallViewPtDetails.setVisibility(View.VISIBLE);
            tvAppName.setVisibility(View.VISIBLE);
            tvVCallViewReports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ReportsDialog(LiveActivity.this).showAllReportsDialog();
                }
            });
            //tvVCallViewReports.setText(getString(R.string.app_name));//Urgent Care Doc- Disable fucntion view reports
            //tvAppName.setVisibility(View.GONE);//remove these 2 line and uncommit tvVCallViewReports clcick to revert

            tvVCallViewPtDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogPatientInfo(LiveActivity.this).showDialog();
                }
            });
        }
    }
}
