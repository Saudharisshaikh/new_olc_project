package sg.com.temasys.skylink.sdk.sampleapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_cp.ActivityFollowUps_4;
import com.app.mdlive_cp.ActivityTcmDetails;
import com.app.mdlive_cp.AfterCallDialog;
import com.app.mdlive_cp.BuildConfig;
import com.app.mdlive_cp.CustomDialogActivity;
import com.app.mdlive_cp.MainActivityNew;
import com.app.mdlive_cp.OnlineCareNurse;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.VCallModule;
import com.app.mdlive_cp.adapters.VVReportImagesAdapter;
import com.app.mdlive_cp.fcm.MyFirebaseMessagingService;
import com.app.mdlive_cp.model.TimeDiff;
import com.app.mdlive_cp.services.IncomingCallResponse;
import com.app.mdlive_cp.util.ActionSheetPopup;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DialogPatientInfo;
import com.app.mdlive_cp.util.DialogPatientVV;
import com.app.mdlive_cp.util.ReportsDialog;
import com.darsh.multipleimageselect.models.Image;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.agora.openlive.activities.LiveActivity;
import io.agora.openlive.rtc.EngineConfig;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.Config;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.ConfigFragment;

import static sg.com.temasys.skylink.sdk.sampleapp.Utils.getNumRemotePeers;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.toastLogLong;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final int CASE_SECTION_AUDIO_CALL = 1;
    private static final int CASE_SECTION_VIDEO_CALL = 2;
    private static final int CASE_SECTION_CHAT = 3;
    private static final int CASE_SECTION_FILE_TRANSFER = 4;
    private static final int CASE_SECTION_DATA_TRANSFER = 5;
    private static final int CASE_SECTION_MULTI_PARTY_VIDEO_CALL = 6;

    private static final int CASE_FRAGMENT_AUDIO_CALL = 0;
    private static final int CASE_FRAGMENT_VIDEO_CALL = 1;
    private static final int CASE_FRAGMENT_CHAT = 2;
    private static final int CASE_FRAGMENT_FILE_TRANSFER = 3;
    private static final int CASE_FRAGMENT_DATA_TRANSFER = 4;
    private static final int CASE_FRAGMENT_MULTI_PARTY_VIDEO_CALL = 5;
    private static final int CASE_FRAGMENT_CONFIG = 6;
    private static final String TAG = MainActivity.class.getName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //========================================================================OLC==============================================================================

    Activity activity;
    AppCompatActivity appCompatActivity;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    VCallModule vCallModule;
    RelativeLayout incomingCallLayout,outgoingCallLayout;
    TextView tvIncomingCallName,tvOutgoingCallName,tvOutgoingCallConnecting;
    CircularImageView imgIncomingCallImage,imgOutgoingCallImage;
    Button btnIncomingCallAccept,btnIncomingCallReject,btnOutgoingCallCancel;
    //ImageView ivEndCall;
    TextView tvVCallViewReports,tvVCallViewPtDetails,tvVCallTime,tvAppName;

    LinearLayout callButtons;
    Button btnConnectToConsult;
    ImageButton btnEndCall;

    BroadcastReceiver cBroadcast;
    BroadcastReceiver callDisconnectBroadcast;
    BroadcastReceiver callConnectBroadcast;
    BroadcastReceiver disconnectSpecialistBroadcast;

    MediaPlayer mMediaPlayer;
    ProgressDialog pd;

    public static boolean isFromCallToCoordinator = false;
    public static boolean isFromCallToEMS = false;
    public static boolean isFromInstantConnect = false;
    BroadcastReceiver coordinatorCallAccceptBrodcast;
    BroadcastReceiver coordinatorCallConnectBrodcast;

    public static boolean isOnVideoCallActivity = false;

    boolean isFromCallToFamily = false;

    @Override
    protected void onStart() {
        isOnVideoCallActivity = true;
        registerReceiver(cBroadcast, new IntentFilter(VCallModule.OUTGOING_CALL_RESPONSE));
        registerReceiver(callDisconnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_DISCONNECTED));
        registerReceiver(callConnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_CONNECTED));
        registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST));

        if (coordinatorCallAccceptBrodcast != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(VCallModule.COORDINATOR_CALL_ACCEPTED);
            intentFilter.addAction(VCallModule.COORDINATOR_CALL_REJECTED);
            registerReceiver(coordinatorCallAccceptBrodcast, intentFilter);
        }
        if (coordinatorCallConnectBrodcast != null) {//this action not sent from fcm service because it already sent onther bc working for it remove this bc in future
            registerReceiver(coordinatorCallConnectBrodcast, new IntentFilter(VCallModule.COORDINATOR_CALL_CONNECTED));
        }
        DATA.call_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        /*runnable = new CountDownRunner();
        myThread= new Thread(runnable);
        myThread.start();*/
        DATA.isSOAP_NotesSent = false;
        super.onStart();
    }

    @Override
    protected void onStop() {
        isOnVideoCallActivity = false;
        unregisterReceiver(cBroadcast);
        unregisterReceiver(callDisconnectBroadcast);
        unregisterReceiver(callConnectBroadcast);
        unregisterReceiver(disconnectSpecialistBroadcast);
        if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()
            mMediaPlayer.stop();
        }
        if (coordinatorCallAccceptBrodcast != null) {
            unregisterReceiver(coordinatorCallAccceptBrodcast);
        }
        if (coordinatorCallConnectBrodcast != null) {
            unregisterReceiver(coordinatorCallConnectBrodcast);
        }

        /*if (new GloabalMethods(activity).checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());//end call moved to btnendcall click and onremotepeerleave
        DATA.endCall(activity);*/
        //stopTimers();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
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
    //========================================================================OLC==============================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                findViewById(R.id.drawer_layout));

        // Load selected App key details
        Config.loadSelectedAppKey(this);
        Config.loadRoomUserNames(this);

        //==========OLC init video call actitivty
        initVideoCallActivity();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        try {
            OnlineCareNurse app = (OnlineCareNurse) getApplication();
            EngineConfig engineConfig = app.engineConfig();
            engineConfig.setChannelName(sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_MULTI);

            Intent intent = new Intent(getApplicationContext(), LiveActivity.class);
            intent.putExtra(io.agora.openlive.Constants.KEY_CLIENT_ROLE,  io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
            //intent.setClass(getApplicationContext(), LiveActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }


        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragmentToLaunch = getFragmentToLaunch(position);
        fragmentManager.beginTransaction().replace(R.id.container, fragmentToLaunch).commit();*/
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case CASE_SECTION_AUDIO_CALL:
                mTitle = getString(R.string.title_section1);
                break;
            case CASE_SECTION_VIDEO_CALL:
                mTitle = getString(R.string.title_section2);
                break;
            case CASE_SECTION_CHAT:
                mTitle = getString(R.string.title_section3);
                break;
            case CASE_SECTION_FILE_TRANSFER:
                mTitle = getString(R.string.title_section4);
                break;
            case CASE_SECTION_DATA_TRANSFER:
                mTitle = getString(R.string.title_section5);
                break;
            case CASE_SECTION_MULTI_PARTY_VIDEO_CALL:
                mTitle = getString(R.string.title_section6);
                break;
            default:
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_build_info) {
            String log = "SDK Version: " + sg.com.temasys.skylink.sdk.BuildConfig.VERSION_NAME
                    + "\n" + "Sample application version: " + BuildConfig.VERSION_NAME;
            toastLogLong(TAG, this, log);
            return true;
        } else if (id == R.id.action_configuration) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            ConfigFragment fragment = new ConfigFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * returns fragment
     *
     * @param position
     * @return fragment to launch based on the item clicked on the navigation drawer
     */
    public Fragment getFragmentToLaunch(int position) {
        Fragment fragmentToLaunch = null;
        switch (position) {
            case CASE_FRAGMENT_AUDIO_CALL:
                fragmentToLaunch = new AudioCallFragment();
                break;
            case CASE_FRAGMENT_VIDEO_CALL:
                fragmentToLaunch = new VideoCallFragment();
                break;
            case CASE_FRAGMENT_CHAT:
                fragmentToLaunch = new ChatFragment();
                break;
            case CASE_FRAGMENT_FILE_TRANSFER:
                fragmentToLaunch = new FileTransferFragment();
                break;
            case CASE_FRAGMENT_DATA_TRANSFER:
                fragmentToLaunch = new DataTransferFragment();
                break;
            case CASE_FRAGMENT_MULTI_PARTY_VIDEO_CALL:
                fragmentToLaunch = new MultiPartyVideoCallFragment();
                break;
            case CASE_FRAGMENT_CONFIG:
                fragmentToLaunch = new ConfigFragment();
                break;
            default:
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position + 1);
        fragmentToLaunch.setArguments(args);

        return fragmentToLaunch;
    }


    //========================================================================OLC==============================================================================

    public void initVideoCallActivity(){

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        isFromCallToFamily = getIntent().getBooleanExtra("isFromCallToFamily", false);

        DATA.isFromAppointment = getIntent().getBooleanExtra("isFromAppointment", false);//probelem on recall
        DATA.isFromCallHistoryOrMsgs = getIntent().getBooleanExtra("isFromCallHistoryOrMsgs", false);

        isFromCallToCoordinator = getIntent().getBooleanExtra("isFromCallToCoordinator", false);
        isFromCallToEMS = getIntent().getBooleanExtra("isFromCallToEMS", false);
        isFromInstantConnect = getIntent().getBooleanExtra("isFromInstantConnect", false);

        activity = MainActivity.this;
        appCompatActivity = MainActivity.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        vCallModule = new VCallModule(activity);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
        }else{
            pd = new ProgressDialog(activity);
        }
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        incomingCallLayout = findViewById(R.id.incomingCallLayout);
        tvIncomingCallName = findViewById(R.id.tvIncomingCallName);
        imgIncomingCallImage = findViewById(R.id.imgIncomingCallImage);
        btnIncomingCallAccept = findViewById(R.id.btnIncomingCallAccept);
        btnIncomingCallReject = findViewById(R.id.btnIncomingCallReject);
        //ivEndCall = (ImageView) findViewById(ivEndCall);

        outgoingCallLayout = findViewById(R.id.outgoingCallLayout);
        imgOutgoingCallImage = findViewById(R.id.imgOutgoingCallImage);
        tvOutgoingCallName = findViewById(R.id.tvOutgoingCallName);
        tvOutgoingCallConnecting = findViewById(R.id.tvOutgoingCallConnecting);
        btnOutgoingCallCancel = findViewById(R.id.btnOutgoingCallCancel);

        callButtons = findViewById(R.id.callButtons);
        btnConnectToConsult = findViewById(R.id.btnConnectToConsult);
        btnEndCall = findViewById(R.id.btnEndCall);

        tvVCallViewReports = findViewById(R.id.tvVCallViewReports);
        tvVCallViewPtDetails = findViewById(R.id.tvVCallViewPtDetails);
        tvVCallTime = findViewById(R.id.tvVCallTime);
        tvAppName = findViewById(R.id.tvAppName);

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
                        new ReportsDialog(MainActivity.this).showAllReportsDialog();
                    }
                });
                tvVCallViewPtDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogPatientInfo(appCompatActivity).showDialog();
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
                    new ReportsDialog(MainActivity.this).showAllReportsDialog();
                }
            });
            tvVCallViewPtDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogPatientInfo(appCompatActivity).showDialog();
                }
            });
        }

        btnOutgoingCallCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                }
                if (checkInternetConnection.isConnectedToInternet()) {
                    if (isFromCallToCoordinator){
                        vCallModule.endcallCoordinator();
                    }else if (isFromCallToEMS){
                        //finish();
                        vCallModule.disconnectOutgoingCall(VCallModule.ems_id);
                    }else {
                        if (DATA.isFromDocToDoc) {
                            vCallModule.disconnectOutgoingCall(DATA.selectedDrId);
                        } else {
                            vCallModule.disconnectOutgoingCall(DATA.selectedUserCallId);
                        }
                    }
                }
            }
        });

        btnIncomingCallAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkInternetConnection.isConnectedToInternet()) {

                    MyFirebaseMessagingService.stopCallNotAnswerTimer();

                    //pd.show();
                    DATA.incomingCallResponce = "accept";
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, DATA.incomingCallResponce);
                    incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                    }
                    callButtons.setVisibility(View.VISIBLE);
                    incomingCallLayout.setVisibility(View.GONE);
                    outgoingCallLayout.setVisibility(View.GONE);
                    //onNavigationDrawerItemSelected(5);
                    checkPermission();
                } else {
                    Toast.makeText(activity, "Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnIncomingCallReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectedToInternet()) {

                    MyFirebaseMessagingService.stopCallNotAnswerTimer();

                    pd.show();
                    DATA.incomingCallResponce = "reject";
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, DATA.incomingCallResponce);
                    incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                } else {
                    Toast.makeText(activity, "Please check internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnConnectToConsult.setOnClickListener(vCallModule.getConnectBtnListener(getNumRemotePeers()));

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();

                if(activity != null){
                    DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    DATA.endCall(activity);
                }

                if (DATA.isFromDocToDoc) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else if (isFromCallToCoordinator) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                }else if (isFromCallToEMS) {
                    Intent i = new Intent(activity, CustomDialogActivity.class);
                    startActivity(i);
                } else {
                    if (!DATA.isFromCallHistoryOrMsgs) {
                        DATA.isSOAP_NotesSent = false;
                        Intent i = new Intent(activity, AfterCallDialog.class);
                        startActivity(i);
                    }

                    /*if (MultiPartyVideoCallFragment.getNumPeers() >= 2){
                        System.out.println("--disconnect broadcast sent to specialist");
                        vCallModule.disconnectSpecialist(DATA.selectedDrId,"doctor");
                    }*/
                }
                finish();
            }
        });
        cBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()

                    mMediaPlayer.stop();
                }

                if (DATA.isCallRejected) {

                    Toast.makeText(activity, "The person is not responding at the moment.", Toast.LENGTH_LONG)
                            .show();
                    DATA.isCallRejected = false;
                    //outgoingCallLayout.setVisibility(View.GONE);
                    if (!MultiPartyVideoCallFragment.isInVideoCall) {
                        finish();
                    }
                } else {
                    //ivEndCall.setVisibility(View.VISIBLE);
                    //ivEndCall.bringToFront();
                    callButtons.setVisibility(View.VISIBLE);
                    if (getNumRemotePeers() > 0){
                        return;
                    }
                    incomingCallLayout.setVisibility(View.GONE);
                    outgoingCallLayout.setVisibility(View.GONE);
                    //onNavigationDrawerItemSelected(5);
                    checkPermission();
                    /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {

                        Toast.makeText(SampleActivity.this, "app().isInConference() is tru", 0).show();
                        BaseFragment current_fragment = AVChatSessionFragment.newInstance(null,
                                null, null);//mSignalStrengthMenuItem
                        showFragment(current_fragment);
                        System.gc();
                        Runtime.getRuntime().gc();

                    }else {

                        ((ooVooSdkSampleShowApp) getApplication()).join(DATA.rndSessionId, false);
                    }*/
                }
            }
        };
        callDisconnectBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()
                    mMediaPlayer.stop();
                }
                vCallModule.sendNotification("You have missed a live care call from "+DATA.incomingCallerName, MainActivityNew.class);
                if(MultiPartyVideoCallFragment.isInVideoCall){
                    return;
                }
                finish();
            }
        };

        callConnectBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                tvOutgoingCallConnecting.setText("Ringing");
                playRingtoneOutgoing();
            }
        };

        disconnectSpecialistBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                /*((ooVooSdkSampleShowApp) getApplication()).onEndOfCall();
                ((ooVooSdkSampleShowApp) getApplication()).sendEndOfCall();*/
		              /*  Intent i = new Intent(getActivity(), AfterCallDialog.class);
						startActivity(i);*/

                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                finish();
            }
        };

        /*if (DATA.isFromDocToDoc) {
            btnConnectToConsult.setVisibility(View.GONE);
        } else {
            btnConnectToConsult.setVisibility(View.VISIBLE);
        }*/
        /*if (DATA.incomingCall) {
            btnConnectToConsult.setVisibility(View.GONE);
        } else {
            btnConnectToConsult.setVisibility(View.VISIBLE);
        }*/

        if (DATA.incomingCall) {
            incomingCallLayout.setVisibility(View.VISIBLE);
            outgoingCallLayout.setVisibility(View.GONE);
            DATA.loadImageFromURL(DATA.incomingCallerImage, R.drawable.icon_call_screen,imgIncomingCallImage);
            tvIncomingCallName.setText(DATA.incomingCallerName);
            Constants.ROOM_NAME_MULTI = DATA.incomingCallSessionId;

            playRingtone();
            vCallModule.incommingCallConnected(DATA.incomingCallUserId);
            vCallModule.unlockScreen();
        }else if(isFromCallToCoordinator){

            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.VISIBLE);

            tvOutgoingCallName.setText("OnlineCare Support");

            vCallModule.coordinatorCall();
            coordinatorCallAccceptBrodcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                    }

                    if (intent.getAction().equals(VCallModule.COORDINATOR_CALL_ACCEPTED)) {
                        /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {
                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", 0).show();
                        } else {
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.outgoingCallSessionId, false);
                        }*/
                        outgoingCallLayout.setVisibility(View.GONE);
                        incomingCallLayout.setVisibility(View.GONE);
                        callButtons.setVisibility(View.VISIBLE);
                        //onNavigationDrawerItemSelected(5);
                        checkPermission();
                    } else {
                        Toast.makeText(activity, "Coordinator is bussy right now. Please try again latter", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            };

            coordinatorCallConnectBrodcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    // TODO Auto-generated method stub
                    if (tvOutgoingCallConnecting != null) {
                        tvOutgoingCallConnecting.setText("Ringing . . .");
                        playRingtone();
                    }
                }
            };

        }else if(isFromCallToEMS){
            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.VISIBLE);

            tvOutgoingCallName.setText("OnlineCare EMS");

            if(ActivityFollowUps_4.selectedFollowupBean != null){
                vCallModule.emscall(false);
            }else{
                vCallModule.emsdirectCall();
            }

            coordinatorCallAccceptBrodcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                    }

                    if (intent.getAction().equals(VCallModule.COORDINATOR_CALL_ACCEPTED)) {
                        /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {
                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", 0).show();
                        } else {
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.outgoingCallSessionId, false);
                        }*/
                        outgoingCallLayout.setVisibility(View.GONE);
                        incomingCallLayout.setVisibility(View.GONE);
                        callButtons.setVisibility(View.VISIBLE);
                        //onNavigationDrawerItemSelected(5);
                        checkPermission();
                    } else {
                        Toast.makeText(activity, "Coordinator is bussy right now. Please try again latter", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            };

        }else if(isFromInstantConnect){

            outgoingCallLayout.setVisibility(View.GONE);
            incomingCallLayout.setVisibility(View.GONE);
            callButtons.setVisibility(View.VISIBLE);
            //onNavigationDrawerItemSelected(5);
            checkPermission();

        }else {
            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.VISIBLE);
            if (DATA.isFromDocToDoc){
                //UrlImageViewHelper.setUrlDrawable(imgOutgoingCallImage, DATA.selectedDrImage, R.drawable.icon_dummy);
                tvOutgoingCallName.setText(DATA.selectedDrName);
                DATA.loadImageFromURL(DATA.selectedDrImage,R.drawable.icon_call_screen,imgOutgoingCallImage);
            }else {
                //UrlImageViewHelper.setUrlDrawable(imgOutgoingCallImage, DATA.selectedUserCallImage, R.drawable.icon_dummy);
                tvOutgoingCallName.setText(DATA.selectedUserCallName);
                DATA.loadImageFromURL(DATA.selectedUserCallImage,R.drawable.icon_call_screen,imgOutgoingCallImage);
            }


            if(DATA.rndSessionId.equals("")){
                DATA.rndSessionId = vCallModule.randomStr();
                DATA.rndSessionId = DATA.rndSessionId + "_nurse";
            }
            Constants.ROOM_NAME_MULTI = DATA.rndSessionId;
            if(DATA.isFromDocToDoc){

                vCallModule.callingToDoctor();

                /*if (DATA.selectedDoctorsModel.current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
                    vCallModule.callFromSpecialist();
                    //Toast.makeText(getActivity(), "CALLING to doctor", 0).show();
                } else {
                    callUser1();
                    //Toast.makeText(getActivity(), "CALLING to specialist", 0).show();
                }*/
            }else {
                System.out.println("--DATA.incomingCall = false");
                if(isFromCallToFamily){
                    vCallModule.callingToPatient(ActivityTcmDetails.primary_patient_id);
                }else {
                    vCallModule.callingToPatient(DATA.selectedUserCallId);
                }
                //Toast.makeText(getActivity(), "CALLING to patient", 0).show();
            }

        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            MyFirebaseMessagingService.sendStopBC(getBaseContext());
        }
    }


    public void playRingtone() {
        if (mMediaPlayer != null) {
            //if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            //}
        }
        //Uri mediaUri = createUri(getBaseContext(), R.raw.outgoingcall_ringtone); // Audiofile in raw folder
        Uri mediaUri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.incoming_call);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getBaseContext(), mediaUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void playRingtoneOutgoing() {
        if(MultiPartyVideoCallFragment.isInVideoCall){
            return;
        }
        if (mMediaPlayer != null) {
            //if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            //}
        }
        //Uri mediaUri = createUri(getBaseContext(), R.raw.outgoingcall_ringtone); // Audiofile in raw folder
        Uri mediaUri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.outgoingcall_ringtone);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getBaseContext(), mediaUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setResults() {// call is accepted/rejected on by other user called from incommingCallResponce
        if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()
            mMediaPlayer.stop();
        }
        pd.dismiss();
        //hideWaitingMessage();
        if (DATA.incomingCallResponce.equals("accept")) {
            //ivEndCall.setVisibility(View.VISIBLE);
            //ivEndCall.bringToFront();
            /*callButtons.setVisibility(View.VISIBLE);
            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.GONE);
            onNavigationDrawerItemSelected(5);*/

            /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {

                Toast.makeText(SampleActivity.this, "app().isInConference() is tru", 0).show();
                BaseFragment current_fragment = AVChatSessionFragment.newInstance(null,
                        null, null);//mSignalStrengthMenuItem
                showFragment(current_fragment);
                System.gc();
                Runtime.getRuntime().gc();

            } else {
                ((ooVooSdkSampleShowApp) getApplication()).join(DATA.incomingCallSessionId, false);
            }*/

        } else if (DATA.incomingCallResponce.equals("reject")) {

            Toast.makeText(getBaseContext(), "Call Rejected", Toast.LENGTH_SHORT).show();
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }
    }


    Thread myThread = null;
    Runnable runnable;

    public TimeDiff getTimeDiff(Date startTime, Date endTime){
        // d1, d2 are dates
        long diff = endTime.getTime() - startTime.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        /*System.out.println("-- Time diffrence");
        System.out.print("-- "+diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");*/

        return new TimeDiff(diffSeconds,diffMinutes,diffHours,diffDays);
    }

    public void stopTimers(){
        if(myThread != null){
            myThread.interrupt();
            myThread = null;
            runnable = null;
        }
    }

    public void displayTimer() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date tempEndTime = new Date();
                    Date startTime  = new SimpleDateFormat("HH:mm:ss").parse(DATA.call_start_time);
                    TimeDiff timeDiff = getTimeDiff(startTime,tempEndTime);

                    long mm = timeDiff.diffMinutes, ss = timeDiff.diffSeconds;
                    StringBuilder sb = new StringBuilder();
                    if(mm < 10){
                        sb.append("0"+String.valueOf(mm));
                    }else {
                        sb.append(String.valueOf(mm));
                    }
                    sb.append(":");
                    if(ss < 10){
                        sb.append("0"+String.valueOf(ss));
                    }else {
                        sb.append(String.valueOf(ss));
                    }

                    tvVCallTime.setText(sb.toString());


                }catch (Exception e) {}
            }
        });
    }


    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    //System.out.println("countdown running from: "+activity.getClass().getSimpleName());
                    displayTimer();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }



    //Add Virtual Visit During Call-- Select Repoert Images :
    final int REQUEST_IMAGE_CAPTURE = 1;
    public DialogPatientVV dialogPatientVV;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(dialogPatientVV != null){

            if (requestCode == com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

                if(dialogPatientVV.images == null){
                    dialogPatientVV.images = new ArrayList<>();
                }
                ArrayList<Image> imagesFromGal = data.getParcelableArrayListExtra(com.darsh.multipleimageselect.helpers.Constants.INTENT_EXTRA_IMAGES);
                dialogPatientVV.images.addAll(imagesFromGal);

                dialogPatientVV.vvReportImagesAdapter = new VVReportImagesAdapter(activity,dialogPatientVV.images);
                dialogPatientVV.gvReportImages.setAdapter(dialogPatientVV.vvReportImagesAdapter);
                dialogPatientVV.gvReportImages.setExpanded(true);
                dialogPatientVV.gvReportImages.setPadding(5,5,5,5);
            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/
            }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                //String imgPath = ImageFilePath.getPath(activity, ActionSheetPopup.outputFileUriVV);//Android O issue, file provider,  GM
                String imgPath = ActionSheetPopup.capturedImagePath;
                if(dialogPatientVV.images == null){
                    dialogPatientVV.images = new ArrayList<>();
                }
                dialogPatientVV.images.add(new Image(0,"",imgPath,true));

                dialogPatientVV.vvReportImagesAdapter = new VVReportImagesAdapter(activity,dialogPatientVV.images);
                dialogPatientVV.gvReportImages.setAdapter(dialogPatientVV.vvReportImagesAdapter);
                dialogPatientVV.gvReportImages.setExpanded(true);
                dialogPatientVV.gvReportImages.setPadding(5,5,5,5);

            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/

            }
        }
    }
    //Add Virtual Visit During Call-- Select Repoert Images : Ends



    //Check for RunTime Permissions
    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQ_CODE = 1 << 4;
    private void checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        if (granted) {
            resetLayoutAndForward();
        } else {
            requestPermissions();
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                resetLayoutAndForward();
            } else {
                customToast.showToast(getResources().getString(R.string.need_necessary_permissions), 0, 0);
            }
        }
    }

    private void resetLayoutAndForward() {
        onNavigationDrawerItemSelected(5);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_soap_notes, menu);

        if(DialogPatientInfo.patientIdGCM.isEmpty()){
            menu.getItem(0).setVisible(false);
        }else{
            menu.getItem(0).setVisible(true);
            menu.getItem(0).setTitle("Patient Details");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            new DialogPatientInfo(activity).showDialog();

        }else if(item.getItemId() == android.R.id.home){
            //onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
