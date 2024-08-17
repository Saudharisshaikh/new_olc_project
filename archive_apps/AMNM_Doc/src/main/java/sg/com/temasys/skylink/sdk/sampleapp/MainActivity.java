package sg.com.temasys.skylink.sdk.sampleapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.amnm_dr.ActivityTcmDetails;
import com.app.amnm_dr.AfterCallDialogEmcura;
import com.app.amnm_dr.BuildConfig;
import com.app.amnm_dr.MainActivityNew;
import com.app.amnm_dr.OnlineCareDr;
import com.app.amnm_dr.R;
import com.app.amnm_dr.VCallModule;
import com.app.amnm_dr.adapter.LvDoctorsAdapter;
import com.app.amnm_dr.adapter.NurseAdapter;
import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.fcm.MyFirebaseMessagingService;
import com.app.amnm_dr.model.DoctorsModel;
import com.app.amnm_dr.model.NurseBean;
import com.app.amnm_dr.model.SpecialityModel;
import com.app.amnm_dr.services.IncomingCallResponse;
import com.app.amnm_dr.util.CheckInternetConnection;
import com.app.amnm_dr.util.CustomToast;
import com.app.amnm_dr.util.DATA;
import com.app.amnm_dr.util.Database;
import com.app.amnm_dr.util.DialogPatientInfo;
import com.app.amnm_dr.util.GloabalMethods;
import com.app.amnm_dr.util.GloabalSocket;
import com.app.amnm_dr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import io.agora.openlive.activities.LiveActivity;
import io.agora.openlive.rtc.EngineConfig;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.Config;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.ConfigFragment;

import static sg.com.temasys.skylink.sdk.sampleapp.Utils.getNumRemotePeers;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.toastLogLong;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,GloabalSocket.SocketEmitterCallBack {

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

    //===========================================================OLC=======================================================================================================
    AppCompatActivity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    CircularImageView imgPatientImage,imgIncomingCallImage;
    TextView tvOutgoingCallName,tvOutgoingCallConnecting,tvIncomingCallName;
    Button btnOutgoingCallCancel;
    Button btnConnectToConsult;
    BroadcastReceiver cBroadcast,callConnectBroadcast;
    BroadcastReceiver callDisconnectBroadcast;
    BroadcastReceiver disconnectSpecialistBroadcast;
    MediaPlayer mMediaPlayer;
    RelativeLayout outgoingCallLayout,incomingCallLayout;
    ImageButton btnEndCall;
    LinearLayout callButtons;
    Button btnIncomingCallAccept,btnIncomingCallReject;
    TextView tvVCallViewReports,tvVCallViewPtDetails, tvAppName;

    public static boolean isFromCallToCoordinator = false;
    public static boolean isFromCallToEMS = false;
    public static boolean isFromInstantConnect = false;
    BroadcastReceiver coordinatorCallAccceptBrodcast;
    BroadcastReceiver coordinatorCallConnectBrodcast;

    public static boolean isOnVideoCallActivity = false;

    boolean isFromCallToFamily = false;

    VCallModule vCallModule;

    @Override
    protected void onStart() {
        isOnVideoCallActivity = true;
        registerReceiver(cBroadcast, new IntentFilter(VCallModule.OUTGOING_CALL_RESPONSE));
        registerReceiver(callConnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_CONNECTED));
        registerReceiver(callDisconnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_DISCONNECTED));

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
        DATA.isSOAP_NotesSent = false;
        super.onStart();
    }

    @Override
    protected void onStop() {
        isOnVideoCallActivity = false;
        unregisterReceiver(cBroadcast);
        unregisterReceiver(callConnectBroadcast);
        unregisterReceiver(callDisconnectBroadcast);

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
        DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        DATA.endCall(activity);*/
        super.onStop();
    }


    GloabalSocket gloabalSocket;

    @Override
    protected void onDestroy() {
        gloabalSocket.offSocket();
        super.onDestroy();
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
    //===========================================================OLC=======================================================================================================

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
            OnlineCareDr app = (OnlineCareDr) getApplication();
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


    //===========================================================OLC=======================================================================================================

    public void initVideoCallActivity(){

        getSupportActionBar().hide();

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/

        isFromCallToFamily = getIntent().getBooleanExtra("isFromCallToFamily", false);

        DATA.isFromAppointment = getIntent().getBooleanExtra("isFromAppointment", false);//probelem on recall
        DATA.isFromCallHistoryOrMsgs = getIntent().getBooleanExtra("isFromCallHistoryOrMsgs", false);

        isFromCallToCoordinator = getIntent().getBooleanExtra("isFromCallToCoordinator", false);
        isFromCallToEMS = getIntent().getBooleanExtra("isFromCallToEMS", false);
        isFromInstantConnect = getIntent().getBooleanExtra("isFromInstantConnect", false);

        activity = MainActivity.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        vCallModule = new VCallModule(activity);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            pd = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
        }else{
            pd = new ProgressDialog(activity);
        }
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

        gloabalSocket = new GloabalSocket(activity,this);

        imgPatientImage = findViewById(R.id.imgPatientImage);
        tvOutgoingCallName = findViewById(R.id.tvOutgoingCallName);
        tvOutgoingCallConnecting = findViewById(R.id.tvOutgoingCallConnecting);
        btnOutgoingCallCancel = findViewById(R.id.btnOutgoingCallCancel);
        outgoingCallLayout = findViewById(R.id.outgoingCallLayout);
        btnEndCall = findViewById(R.id.btnEndCall);
        btnConnectToConsult = findViewById(R.id.btnConnectToConsult);
        callButtons = findViewById(R.id.callButtons);

        incomingCallLayout = findViewById(R.id.incomingCallLayout);
        tvIncomingCallName = findViewById(R.id.tvIncomingCallName);
        imgIncomingCallImage = findViewById(R.id.imgIncomingCallImage);
        btnIncomingCallAccept = findViewById(R.id.btnIncomingCallAccept);
        btnIncomingCallReject = findViewById(R.id.btnIncomingCallReject);

        tvVCallViewReports = findViewById(R.id.tvVCallViewReports);
        tvVCallViewPtDetails = findViewById(R.id.tvVCallViewPtDetails);
        tvAppName = findViewById(R.id.tvAppName);

        setupTopBar();

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
            Constants.ROOM_NAME_MULTI = DATA.incomingCallSessionId;
            incomingCallLayout.setVisibility(View.VISIBLE);
            outgoingCallLayout.setVisibility(View.GONE);
            DATA.loadImageFromURL(DATA.incomingCallerImage, R.drawable.icon_call_screen, imgIncomingCallImage);
            tvIncomingCallName.setText(DATA.incomingCallerName);
            vCallModule.incommingCallConnected(DATA.incomingCallUserId);
            vCallModule.unlockScreen();
            playRingtoneIncoming();
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
                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", Toast.LENGTH_SHORT).show();
                        } else {
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.outgoingCallSessionId, false);
                        }*/
                        outgoingCallLayout.setVisibility(View.GONE);
                        incomingCallLayout.setVisibility(View.GONE);
                        callButtons.setVisibility(View.VISIBLE);
                        onNavigationDrawerItemSelected(5);
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

            tvOutgoingCallName.setText("OnlineCare NEMT");


            if(DATA.selectedLiveCare != null){
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
                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", Toast.LENGTH_SHORT).show();
                        } else {
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.outgoingCallSessionId, false);
                        }*/
                        outgoingCallLayout.setVisibility(View.GONE);
                        incomingCallLayout.setVisibility(View.GONE);
                        callButtons.setVisibility(View.VISIBLE);
                        onNavigationDrawerItemSelected(5);
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
            onNavigationDrawerItemSelected(5);

        } else {
            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.VISIBLE);


            //==
            if(DATA.rndSessionId.equals("")){
                DATA.rndSessionId = vCallModule.randomStr();
            }
            Constants.ROOM_NAME_MULTI = DATA.rndSessionId;
            if(DATA.isFromDocToDoc){
                tvOutgoingCallName.setText(DATA.selectedDrName);
                DATA.loadImageFromURL(DATA.selectedDrImage, R.drawable.icon_call_screen, imgPatientImage);

                vCallModule.callingToDoctor();
            }else{
                tvOutgoingCallName.setText(DATA.selectedUserCallName);
                DATA.loadImageFromURL(DATA.selectedUserCallImage, R.drawable.icon_call_screen,imgPatientImage);

                if(isFromCallToFamily){
                    vCallModule.callingToPatient(ActivityTcmDetails.primary_patient_id);
                }else {
                    vCallModule.callingToPatient(DATA.selectedUserCallId);
                }
            }
            //==
            /*imgPatientImage = (ImageView) view.findViewById(R.id.imgPatientImage);
            btnOutgoingCallCancel = (Button) view.findViewById(R.id.btnOutgoingCallCancel);
            tvOutgoingCallName = (TextView) view.findViewById(R.id.tvOutgoingCallName);
            tvConnecting = (TextView) view.findViewById(R.id.tvOutgoingCallConnecting);
            pbConnecting = (ProgressBar) view.findViewById(pbConnecting);

            if(DATA.isFromDocToDoc){
                tvOutgoingCallName.setText(DATA.selectedDrName);
                UrlImageViewHelper.setUrlDrawable(imgPatientImage,  DATA.selectedDrImage, R.drawable.icon_dummy);
            }else{
                tvOutgoingCallName.setText(DATA.selectedUserCallName);
                UrlImageViewHelper.setUrlDrawable(imgPatientImage,DATA.selectedUserCallImage, R.drawable.icon_dummy);
            }

            if(DATA.rndSessionId.equals("")){
                DATA.rndSessionId = ((SampleActivity)getActivity()).randomStr();
            }
            if(DATA.isFromDocToDoc){
                ((SampleActivity)getActivity()).callUser1();
            }else{
                ((SampleActivity)getActivity()).callUser();
            }




            btnOutgoingCallCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (SampleActivity.mMediaPlayer != null) {
                        SampleActivity.mMediaPlayer.stop();
                    }

                    if (checkInternetConnection.isConnectedToInternet()) {
                        if (DATA.isFromDocToDoc) {
                            disconnectOutgoingCall(DATA.selectedDrId);
                        } else {
                            disconnectOutgoingCall(DATA.selectedUserCallId);
                        }

                    }

                }
            });*/
        }


        btnIncomingCallAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (checkInternetConnection.isConnectedToInternet()) {
                    pd.show();
                    MyFirebaseMessagingService.stopCallNotAnswerTimer();
                    DATA.incomingCallResponce = "accept";
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, DATA.incomingCallResponce);
                    incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                } else {
                    Toast.makeText(activity, "Please check internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnIncomingCallReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectedToInternet()) {
                    pd.show();
                    MyFirebaseMessagingService.stopCallNotAnswerTimer();
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
                /*if (MultiPartyVideoCallFragment.getNumPeers() >= 2) {
                    System.out.println("--disconnect broadcast sent to specialist");
                    vCallModule.disconnectSpecialist(DATA.selectedDrId, "doctor");
                }*/

                DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                DATA.endCall(activity);

                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                startActivity(new Intent(activity, AfterCallDialogEmcura.class));//AfterCallDialog
                finish();
            }
        });
        btnOutgoingCallCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                }
                if (checkInternetConnection.isConnectedToInternet()) {
                    if (isFromCallToCoordinator){
                        vCallModule.endcallCoordinator();
                    }else if (isFromCallToEMS){
                        //finish();
                        vCallModule.disconnectOutgoingCall(ems_id);
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

        //-------------------------------------------------------------------------------
        cBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()
                    mMediaPlayer.stop();
                }

                if(DATA.isCallRejected){
                    Toast.makeText(activity, "The person is not responding at the moment.", Toast.LENGTH_LONG).show();//Rejected by user
                    DATA.isCallRejected = false;
                    //outgoingCallLayout.setVisibility(View.GONE);
                    if (!MultiPartyVideoCallFragment.isInVideoCall) {
                        finish();
                    }

                }else{
                    setupTopBar();
                    //onJoinSession();
                    if (getNumRemotePeers() > 0){
                        return;
                    }
                    outgoingCallLayout.setVisibility(View.GONE);
                    incomingCallLayout.setVisibility(View.GONE);
                    callButtons.setVisibility(View.VISIBLE);
                    onNavigationDrawerItemSelected(5);
                    /*if (DATA.isSessionRunning) {
                        System.out.println("--DATA.isSessionRunning = true");
                    } else {
                        if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {

                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", Toast.LENGTH_SHORT).show();
                            BaseFragment current_fragment = AVChatSessionFragment.newInstance(null,
                                    null, null);//mSignalStrengthMenuItem
                            showFragment(current_fragment);
                            System.gc();
                            Runtime.getRuntime().gc();

                        } else {
                            DATA.isSessionRunning = true;
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.rndSessionId, false);
                        }
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
                playRingtone();
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


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            MyFirebaseMessagingService.sendStopBC(getBaseContext());
        }
    }


    public void playRingtone() {
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

    public void playRingtoneIncoming() {
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

    public void setResults() {// call is accepted/rejected by other user called from incommingCallResponce
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        if(pd != null){pd.dismiss();}
        //hideWaitingMessage();
        if (DATA.incomingCallResponce.equals("accept")) {

            outgoingCallLayout.setVisibility(View.GONE);
            incomingCallLayout.setVisibility(View.GONE);
            callButtons.setVisibility(View.VISIBLE);

            onNavigationDrawerItemSelected(5);
            /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {

                Toast.makeText(SampleActivity.this, "app().isInConference() is tru", Toast.LENGTH_SHORT).show();
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
            if(MultiPartyVideoCallFragment.isInVideoCall){
                return;
            }
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }

		/*if (DATA.incomingCall) {
			settingsToPersist.SessionID = DATA.incomingCallSessionId;
		} else {
			settingsToPersist.SessionID = DATA.rndSessionId;
		}*/

    }



    Dialog docToDocDialog;
    public void initDocToDocDialog() {
        docToDocDialog = new Dialog(activity);
        docToDocDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Database db = new Database(activity);

        docToDocDialog.setContentView(R.layout.doc_to_doc);

        ImageView imcCelv,imgHenry,imgKaled,imgMayo,imgRoswell;
        Spinner spinnerSpecialities;


        imcCelv = docToDocDialog.findViewById(R.id.imcCelv);
        imgHenry = docToDocDialog.findViewById(R.id.imgHenry);
        imgKaled = docToDocDialog.findViewById(R.id.imgKaled);
        imgMayo = docToDocDialog.findViewById(R.id.imgMayo);
        imgRoswell = docToDocDialog.findViewById(R.id.imgRoswell);

        spinnerSpecialities = docToDocDialog.findViewById(R.id.spinnerSpecility);

        docToDocDialog.show();

        DATA.allSpecialities = new ArrayList<SpecialityModel>();
        DATA.allSpecialities = SharedPrefsHelper.getInstance().getAllSpecialities();//db.getAllSpecialities();

        ArrayAdapter<SpecialityModel> specialitiesArray = new ArrayAdapter<SpecialityModel>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allSpecialities);
        spinnerSpecialities.setAdapter(specialitiesArray);

        spinnerSpecialities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //customToast.showToast(DATA.allSpecialities.get(position).specialityName, 0, 0);

                DATA.drSpecialityId = DATA.allSpecialities.get(position).specialityId;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });



        imcCelv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DATA.drClinicId = "1";

                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                }else{
                    getDoctors();

                }


            }
        });
        imgHenry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DATA.drClinicId = "2";
                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                }else{
                    getDoctors();

                }
            }
        });

        imgKaled.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DATA.drClinicId = "3";
                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                }else{
                    getDoctors();

                }
            }
        });

        imgMayo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DATA.drClinicId = "4";
                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                }else{
                    getDoctors();

                }

            }
        });

        imgRoswell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DATA.drClinicId = "5";
                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                }else{
                    getDoctors();

                }

            }
        });


    }

    //JSONObject jsonObject;
    JSONArray doctorsArray;
    ProgressDialog pd;
    public void getDoctors() {

        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();

        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();
        params.put("clinic_id",DATA.drClinicId);
        params.put("speciality_id",DATA.drSpecialityId);


        String reqURL = DATA.baseUrl+"getDoctorsByClinic";

        System.out.println("-- Request: "+reqURL);
        System.out.println("-- params : "+params.toString());


        //client.get(DATA.baseUrl+"searchDoctorByZipCode", params, new AsyncHttpResponseHandler() {
        client.get(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try{
                    String content = new String(response);

                    System.out.println("--Response in clinic drs: "+content);

                    // remove the progress bar view
//				refreshMenuItem.setActionView(null);
                    try {
                        JSONObject jsonObject = new JSONObject(content);

                        String status = jsonObject.getString("status");

                        if(status.equals("success")) {

                            String drsStr = jsonObject.getString("doctors");

                            doctorsArray = new JSONArray(drsStr);

                            DATA.allDoctors = new ArrayList<DoctorsModel>();

                            DoctorsModel temp;

                            for(int i = 0; i<doctorsArray.length(); i++) {

                                temp = new DoctorsModel();

                                temp.id = doctorsArray.getJSONObject(i).getString("id");
                                temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
                                temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
                                temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
                                temp.email = doctorsArray.getJSONObject(i).getString("email");
                                temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
                                temp.image = doctorsArray.getJSONObject(i).getString("image");
                                temp.careerData = doctorsArray.getJSONObject(i).getString("introduction");
                                temp.designation = doctorsArray.getJSONObject(i).getString("designation");
                                temp.is_online=doctorsArray.getJSONObject(i).getString("is_online");
                                temp.latitude =doctorsArray.getJSONObject(i).getString("latitude");
                                temp.longitude=doctorsArray.getJSONObject(i).getString("longitude");
                                temp.zip_code=doctorsArray.getJSONObject(i).getString("zip_code");
                                if (temp.latitude.equalsIgnoreCase("null")) {
                                    temp.latitude = "0.0";
                                }
                                if (temp.longitude.equalsIgnoreCase("null")) {
                                    temp.longitude = "0.0";
                                }
                                temp.speciality_id=doctorsArray.getJSONObject(i).getString("speciality_id");
                                temp.current_app=doctorsArray.getJSONObject(i).getString("current_app");
                                temp.speciality_name=doctorsArray.getJSONObject(i).getString("designation");//speciality_name is not in this api

                                System.out.println("--online care callwebservice getOnline Doctors fname: "+temp.fName);

                                DATA.allDoctors.add(temp);
                                System.out.println("--size "+DATA.allDoctors.size()+"");
                                temp = null;
                            }
                        } else {
                            customToast.showToast("Internal server error", 0, 0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                    //openActivity.open(DoctorsList.class, false);
                    docToDocDialog.dismiss();
                    initDoctorsDialog();

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.dismiss();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });
    }//end getDoctors
    public static Dialog doctorsDialog;
    public void initDoctorsDialog() {
        doctorsDialog = new Dialog(activity);
        doctorsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        doctorsDialog.setContentView(R.layout.doctors_dialog);

        ListView lvDialogDoctors = doctorsDialog.findViewById(R.id.lvDialogDoctors);
        TextView tvNoData = doctorsDialog.findViewById(R.id.tvNoData);
        ImageView ivClose = doctorsDialog.findViewById(R.id.ivClose);
        if(DATA.allDoctors.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            tvNoData.setVisibility(View.GONE);
        }

        lvDoctorsAdapter = new LvDoctorsAdapter(activity);
        lvDialogDoctors.setAdapter(lvDoctorsAdapter);
        lvDialogDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(DATA.allDoctors.get(arg2).is_online.equalsIgnoreCase("1")){
                    DATA.selectedDrId = DATA.allDoctors.get(arg2).id;
                    System.out.println("--onitemclick id "+DATA.selectedDrId);
                    vCallModule.callingToDoctor();
                }else {
                    customToast.showToast("Doctor is offline",0,1);
                }

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorsDialog.dismiss();
            }
        });
        doctorsDialog.show();
    }


    ListView lvDoctors;
    TextView tvNoData;
    public void initDoctorsDialogNew() {
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
                    System.out.println("--onitemclick id "+DATA.selectedDrId);
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

        getAllDrs("doctor");
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

        System.out.println("-- Request : "+reqURL);
        System.out.println("-- params : "+params.toString());

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

                    System.out.println("--responce in getAllDrs: "+content);

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
                    System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end getAllDrs


    ListView lvNurse;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabOM,tvTabSup;
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


        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (nurseBeens.get(position).is_online.equals("1")) {
                    dialog.dismiss();
                    DATA.selectedDrId = nurseBeens.get(position).id;
                    System.out.println("--onitemclick id "+DATA.selectedDrId);
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

        System.out.println("-- Request : "+reqURL);
        System.out.println("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in find_nurse "+content);

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
                            if (doctor_category.equalsIgnoreCase("Nurse")){
                                nurseBeens.add(bean);
                            }
                            nurseBeensOrig.add(bean);
                            bean = null;

                        }
                        nurseAdapter = new NurseAdapter(activity,nurseBeens);
                        lvNurse.setAdapter(nurseAdapter);

                        tvTabNurse.setBackgroundColor(getResources().getColor(R.color.theme_red));
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
                    System.out.println("-- responce onsuccess: find_nurse , http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- onfailure find_nurse : "+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }


    public static String ems_id = "";


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

                /*tvVCallViewReports.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ReportsDialog(MainActivity.this).showAllReportsDialog();
                    }
                });*/
                tvVCallViewReports.setText(getString(R.string.app_name));//Urgent Care Doc- Disable fucntion view reports
                tvAppName.setVisibility(View.GONE);//remove these 2 line and uncommit tvVCallViewReports clcick to revert

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
            /*tvVCallViewReports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ReportsDialog(activity).showAllReportsDialog();
                }
            });*/
            tvVCallViewReports.setText(getString(R.string.app_name));//Urgent Care Doc- Disable fucntion view reports
            tvAppName.setVisibility(View.GONE);//remove these 2 line and uncommit tvVCallViewReports clcick to revert

            tvVCallViewPtDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogPatientInfo(activity).showDialog();
                }
            });
        }
    }


    @Override
    public void onSocketCallBack(String emitterResponse) {

        try {
            JSONObject jsonObject = new JSONObject(emitterResponse);
            String id = jsonObject.getString("id");
            String usertype = jsonObject.getString("usertype");
            String status = jsonObject.getString("status");

            if(usertype.equalsIgnoreCase("doctor")){
                if(nurseBeens != null && nurseAdapter != null){
                    for (int i = 0; i < nurseBeens.size(); i++) {
                        if(nurseBeens.get(i).id.equalsIgnoreCase(id)){
                            if(status.equalsIgnoreCase("login")){
                                nurseBeens.get(i).is_online = "1";
                            }else if(status.equalsIgnoreCase("logout")){
                                nurseBeens.get(i).is_online = "0";
                            }
                        }
                    }
                    nurseAdapter.notifyDataSetChanged();
                }

                if(DATA.allDoctors != null && lvDoctorsAdapter != null){
                    for (int i = 0; i < DATA.allDoctors.size(); i++) {
                        if(DATA.allDoctors.get(i).id.equalsIgnoreCase(id)){
                            if(status.equalsIgnoreCase("login")){
                                DATA.allDoctors.get(i).is_online = "1";
                            }else if(status.equalsIgnoreCase("logout")){
                                DATA.allDoctors.get(i).is_online = "0";
                            }
                        }
                    }
                    lvDoctorsAdapter.notifyDataSetChanged();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
