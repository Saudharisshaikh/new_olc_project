package io.agora.openlive.activities;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareUS_MA.ActivityInstatntConnect;
import com.app.OnlineCareUS_MA.AfterCallDialogEmcura;
import com.app.OnlineCareUS_MA.R;
import com.app.OnlineCareUS_MA.VCallModule;
import com.app.OnlineCareUS_MA.adapter.LvDoctorsAdapter;
import com.app.OnlineCareUS_MA.adapter.NurseAdapter;
import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.model.DoctorsModel;
import com.app.OnlineCareUS_MA.model.NurseBean;
import com.app.OnlineCareUS_MA.util.CheckInternetConnection;
import com.app.OnlineCareUS_MA.util.CustomToast;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.DialogPatientInfo;
import com.app.OnlineCareUS_MA.util.GloabalMethods;
import com.app.OnlineCareUS_MA.util.ReportsDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import io.agora.openlive.stats.LocalStatsData;
import io.agora.openlive.stats.RemoteStatsData;
import io.agora.openlive.stats.StatsData;
import io.agora.openlive.ui.VideoGridContainer;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;

import static sg.com.temasys.skylink.sdk.sampleapp.MainActivity.doctorsDialog;

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

                startActivity(new Intent(activity, AfterCallDialogEmcura.class));
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
        if (!mMuteVideoBtn.isActivated()) return;

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
    ListView lvDoctors;
    TextView tvNoData;
    public void initDoctorsDialogNew(int docSp) {
        doctorsDialog = new Dialog(activity);
        doctorsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        doctorsDialog.setContentView(R.layout.doctors_dialog_new);

        //doctorsDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        final Button btnDoctors = doctorsDialog.findViewById(R.id.btnDoctors);
        final Button btnSpecialist = doctorsDialog.findViewById(R.id.btnSpecialist);

        lvDoctors = doctorsDialog.findViewById(R.id.lvDoctors);
        tvNoData = doctorsDialog.findViewById(R.id.tvNoData);
        ImageView ivClose = doctorsDialog.findViewById(R.id.ivClose);

        btnDoctors.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (checkInternetConnection.isConnectedToInternet()) {
                    btnDoctors.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnDoctors.setTextColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("doctor");
                } else {
                    Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSpecialist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (checkInternetConnection.isConnectedToInternet()) {
                    btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setTextColor(getResources().getColor(android.R.color.white));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("specialist");
                } else {
                    Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if(DATA.allDoctors.get(arg2).is_online.equalsIgnoreCase("1")){
                    DATA.selectedDrId = DATA.allDoctors.get(arg2).id;
                    DATA.print("--onitemclick id "+DATA.selectedDrId);
                    vCallModule.callingToDoctor();
                }else {
                    customToast.showToast("Doctor is offline",0,1);
                }
                /*CallParticipant.callParticipant = new CallParticipant();
                CallParticipant.callParticipant.id = DATA.selectedDrId;
                CallParticipant.callParticipant.name = DATA.allDoctors.get(arg2).fName+" "+DATA.allDoctors.get(arg2).lName;
                CallParticipant.callParticipant.current_app = DATA.allDoctors.get(arg2).current_app;*/

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorsDialog.dismiss();
            }
        });
        doctorsDialog.show();


        if(docSp == 1){
            btnDoctors.setBackgroundColor(getResources().getColor(R.color.theme_red));
            btnDoctors.setTextColor(getResources().getColor(android.R.color.white));
            btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
            btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));
            //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
            //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
            getAllDrs("doctor");
        }else if(docSp == 2){
            btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
            btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
            btnSpecialist.setBackgroundColor(getResources().getColor(R.color.theme_red));
            btnSpecialist.setTextColor(getResources().getColor(android.R.color.white));
            //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
            //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
            getAllDrs("specialist");
        }
    }

    LvDoctorsAdapter lvDoctorsAdapter;
    public void getAllDrs(String user_type) {

        AsyncHttpClient client = new AsyncHttpClient();
        DATA.showLoaderDefault(activity,"");

        ApiManager.addHeader(activity,client);

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("user_type", user_type);


        String reqURL = DATA.baseUrl+"/searchAllDoctors_/";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

		/*if (etZipcode.getText().toString().isEmpty()) {

		} else {
			params.put("zipcode", etZipcode.getText().toString());
		}*/

        client.post(reqURL, params, new AsyncHttpResponseHandler() {
            //client.get("https://onlinecare.com/dev/index.php/app/alldoctors", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--responce in getAllDrs: "+content);

                    try {
                        JSONArray data = new JSONArray(content);
                        DATA.allDoctors = new ArrayList<DoctorsModel>();
                        DoctorsModel temp = null;

                        if (data.length() == 0) {
                            //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                            tvNoData.setVisibility(View.VISIBLE);
                            lvDoctorsAdapter = new LvDoctorsAdapter(activity);
                            lvDoctors.setAdapter(lvDoctorsAdapter);
                        }else{
                            tvNoData.setVisibility(View.GONE);

                            for (int i = 0; i < data.length(); i++) {
                                temp = new DoctorsModel();
                                JSONObject object = data.getJSONObject(i);
                                temp.id = object.getString("id");
                                temp.latitude =object.getString("latitude");
                                temp.longitude=object.getString("longitude");
                                temp.zip_code=object.getString("zip_code");
                                temp.fName=object.getString("first_name");
                                temp.lName=object.getString("last_name");
                                temp.is_online=object.getString("is_online");
                                temp.image=object.getString("image");
                                temp.designation=object.getString("designation");


                                if (temp.latitude.equalsIgnoreCase("null")) {
                                    temp.latitude = "0.0";
                                }
                                if (temp.longitude.equalsIgnoreCase("null")) {
                                    temp.longitude = "0.0";
                                }

                                temp.speciality_id=object.getString("speciality_id");
                                temp.current_app=object.getString("current_app");
                                temp.speciality_name=object.getString("speciality_name");

                                DATA.allDoctors.add(temp);
                                temp = null;
                            }

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                            lvDoctorsAdapter = new LvDoctorsAdapter(activity);
                            lvDoctors.setAdapter(lvDoctorsAdapter);

                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
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
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end getAllDrs



    ListView lvNurse;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabOM,tvTabSup,tvTabAll;
    public void initCPDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_find_nurse);

        LinearLayout layTcmOptions = dialog.findViewById(R.id.layTcmOptions);
        layTcmOptions.setVisibility(View.GONE);
        Button btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setText("Cancel");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        lvNurse = dialog.findViewById(R.id.lvNurse);
        tvTabNurse = dialog.findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = dialog.findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = dialog.findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = dialog.findViewById(R.id.tvTabDietitian);
        tvTabOT = dialog.findViewById(R.id.tvTabOT);
        tvTabPharmacist = dialog.findViewById(R.id.tvTabPharmacist);
        tvTabMA = dialog.findViewById(R.id.tvTabMA);
        tvTabOM = dialog.findViewById(R.id.tvTabOM);
        tvTabSup = dialog.findViewById(R.id.tvTabSup);
        tvTabAll = dialog.findViewById(R.id.tvTabAll);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.tvTabNurse:
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;

                    case R.id.tvTabNursePractitioner:
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabSocialWorker:
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabDietitian:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabOT:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                        nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabPharmacist:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Pharmacist")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabMA:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("MA")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabOM:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Office Manager")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabSup:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Supervisor")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabAll:
                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            /*nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }*/
                            nurseBeens.addAll(nurseBeensOrig);
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;
                    default:
                        break;
                }
            }
        };

        tvTabNurse.setOnClickListener(onClickListener);
        tvTabNursePractitioner.setOnClickListener(onClickListener);
        tvTabSocialWorker.setOnClickListener(onClickListener);
        tvTabDietitian.setOnClickListener(onClickListener);
        tvTabOT.setOnClickListener(onClickListener);
        tvTabPharmacist.setOnClickListener(onClickListener);
        tvTabMA.setOnClickListener(onClickListener);
        tvTabOM.setOnClickListener(onClickListener);
        tvTabSup.setOnClickListener(onClickListener);
        tvTabAll.setOnClickListener(onClickListener);


        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (nurseBeens.get(position).is_online.equals("1")) {
                    dialog.dismiss();
                    DATA.selectedDrId = nurseBeens.get(position).id;
                    DATA.print("--onitemclick id "+DATA.selectedDrId);
                    vCallModule.callingToDoctor();

                    /*CallParticipant.callParticipant = new CallParticipant();
                    CallParticipant.callParticipant.id = DATA.selectedDrId;
                    CallParticipant.callParticipant.name = nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name;
                    CallParticipant.callParticipant.current_app = "Care Provider";*/

                }else {
                    customToast.showToast("Care provider is offline",0,0);
                }
            }
        });

        final EditText etSearchQuery = dialog.findViewById(R.id.etSearchQuery);
        ImageView ivSearchQuery = dialog.findViewById(R.id.ivSearchQuery);

        ivSearchQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearchQuery.getText().toString().isEmpty()) {
                    customToast.showToast("Please type care provider name to search",0,0);
                }else {
                    if (checkInternetConnection.isConnectedToInternet()){
                        find_nurse(etSearchQuery.getText().toString(),DATA.selectedUserCallId,"0");
                    }else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                    }
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);


        if (checkInternetConnection.isConnectedToInternet()){
            find_nurse("",DATA.selectedUserCallId,"0");
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
    }

    ArrayList<NurseBean> nurseBeens;
    ArrayList<NurseBean> nurseBeensOrig;
    NurseBean bean;
    NurseAdapter nurseAdapter;
    public void find_nurse(String keyword,String patient_id,String patient_category) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity,client);


        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        if (!keyword.isEmpty()){
            params.put("keyword", keyword);
        }
        if(patient_id.isEmpty()){
            patient_id = "0";
        }
        params.put("patient_id", patient_id);
        params.put("patient_category", patient_category);


        String reqURL = DATA.baseUrl+"find_nurse";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in find_nurse "+content);

                    try {
                        nurseBeensOrig = new ArrayList<NurseBean>();
                        nurseBeens = new ArrayList<NurseBean>();
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray nurses = jsonObject.getJSONArray("nurses");
                        for (int i = 0; i<nurses.length(); i++){
                            String id = nurses.getJSONObject(i).getString("id");
                            String first_name = nurses.getJSONObject(i).getString("first_name");
                            String last_name = nurses.getJSONObject(i).getString("last_name");
                            String image = nurses.getJSONObject(i).getString("image");
                            String doctor_category = nurses.getJSONObject(i).getString("doctor_category");
                            String is_added = "group_call";//= nurses.getJSONObject(i).getString("is_added");
                            String is_online = nurses.getJSONObject(i).getString("is_online");

                            bean = new NurseBean(id,first_name,last_name,image,doctor_category, is_added,is_online);
                            //if (doctor_category.equalsIgnoreCase("Nurse")){//adding all
                            nurseBeens.add(bean);
                            //}
                            nurseBeensOrig.add(bean);
                            bean = null;

                        }
                        nurseAdapter = new NurseAdapter(activity,nurseBeens);
                        lvNurse.setAdapter(nurseAdapter);

                        tvTabAll.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: find_nurse , http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure find_nurse : "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }





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
                        new DialogPatientInfo(activity).showDialog();
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
                    new DialogPatientInfo(activity).showDialog();
                }
            });
        }
    }
}
