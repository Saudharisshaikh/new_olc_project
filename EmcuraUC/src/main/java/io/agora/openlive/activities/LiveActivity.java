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
import android.os.Build;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.emcurauc.AfterCallDialog;
import com.app.emcurauc.R;
import com.app.emcurauc.VCallModule;
import com.app.emcurauc.api.Dialog_CustomProgress;
import com.app.emcurauc.util.AudioRecordTest;
import com.app.emcurauc.util.CheckInternetConnection;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import io.agora.openlive.stats.LocalStatsData;
import io.agora.openlive.stats.RemoteStatsData;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoEncoderConfiguration;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;

public class LiveActivity extends RtcBaseActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;

    AudioRecordTest audioRecorder;
    Dialog_CustomProgress customProgressDialog;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;


    Activity activity;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    VCallModule vCallModule;
    BroadcastReceiver disconnectSpecialistBroadcast;
    //Button btnConnectToConsult;
    ImageButton btnEndCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.angora_activity_live_room);

        audioRecorder = new AudioRecordTest(this);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //config().setChannelName(sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_MULTI);


        initUI();
        initData();
    }

    private void initUI() {
        //================================OLC code starts=========================================================
        activity = LiveActivity.this;
        customProgressDialog = new Dialog_CustomProgress(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        vCallModule = new VCallModule(activity);
        //btnConnectToConsult = findViewById(R.id.btnConnectToConsult);
        btnEndCall = findViewById(R.id.btnEndCall);

        //btnConnectToConsult.setOnClickListener(vCallModule.getConnectBtnListener(2));

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OLC code -- JUST un_mute own camera
                rtcEngine().enableLocalVideo(true);
                startActivity(new Intent(activity, AfterCallDialog.class));
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
                //OLC code -- JUST un_mute own camera
                rtcEngine().enableLocalVideo(true);
                startActivity(new Intent(activity, AfterCallDialog.class));
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
        boolean isBroadcaster = (role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
//        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
//                io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                new BeautyOptions());

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        //rtcEngine().setCloudProxy(Constants.TRANSPORT_TYPE_TCP_PROXY);

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
                DATA.print("-- peer left remaining count : " + mVideoGridContainer.getChildCount());
                if (mVideoGridContainer.getChildCount() < 2) {
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

        DATA.endCall(activity);

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
        //rtcEngine().setBeautyEffectOptions(view.isActivated(), io.agora.openlive.Constants.DEFAULT_BEAUTY_OPTIONS);

        rtcEngine().setBeautyEffectOptions(view.isActivated(), new BeautyOptions());
    }

    public void onMoreClicked(View view) {
        // Do nothing at the moment
        //vCallModule.showDisconnectDialog();
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


    //================================OLC======================================
    @Override
    public void onStart() {
        audioRecorder.startRecording();
        DATA.print("-- ## LiveActivity Angora onstart");
        MultiPartyVideoCallFragment.isInVideoCall = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST), RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST));
        }
        super.onStart();
    }

    //get file from file path.
    private File getFileFromPath(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            return new File(filePath);
        } else {
            return null; // Handle invalid file path if needed
        }
    }

    @Override
    public void onStop() {
        audioRecorder.stopRecording();
        String filePath = audioRecorder.getFilePath();
        Log.d("AudioRecordTest", "Audio file path: " + filePath);
        File audioFile = getFileFromPath(filePath);
        uploadFile(audioFile);
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
                        startActivity(new Intent(activity, AfterCallDialog.class));
                        finish();
                    }
                })
                .setNegativeButton("Not Now", null)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //KProgressHUD kProgressHUD;

    public void uploadFile(File audioFile) {

//        kProgressHUD = KProgressHUD.create(activity)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Please wait while the conversation is being uploaded.")
//                //.setDetailsLabel("Downloading data")
//                .setCancellable(true)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f);

        String callId = "0";
        callId = DATA.incommingCallId;

        String RequestUrl = DATA.baseUrl + "notes/call_conversation";
        RequestParams params = new RequestParams();
        params.put("type", "patient");
        params.put("call_id", callId);
        try {
            params.put("audio", audioFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //kProgressHUD.show();
        DATA.print("-- params in call_conversation " + params);
        DATA.print("-- RequestUrl " + RequestUrl);

        AsyncHttpClient client = new AsyncHttpClient();

        DATA.print("-- token: " + "Bearer " + prefs.getString("access_token", ""));
        client.addHeader("Oauthtoken", "Bearer " + prefs.getString("access_token", ""));

        client.post(RequestUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String content = new String(responseBody);
                    DATA.print("-- response in content " + content);
                    JSONObject jsonObject = new JSONObject(content);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        DATA.print("-- audio file upload : " + msg);
                        //kProgressHUD.dismiss();
                    }

                } catch (JSONException e) {
                    //kProgressHUD.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                DATA.print("-- call_conversation onFailure " + error.getMessage());
                DATA.print("-- call_conversation statusCode " + statusCode);
                //kProgressHUD.dismiss();
            }
        });
    }
}
