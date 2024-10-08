package sg.com.temasys.skylink.sdk.sampleapp;

import android.Manifest;
import android.app.Activity;
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
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.emcurauc.AfterCallDialog;
import com.app.emcurauc.MainActivityNew;
import com.app.emcurauc.OnlineCare;
import com.app.emcurauc.R;
import com.app.emcurauc.VCallModule;
import com.app.emcurauc.fcm.MyFirebaseMessagingService;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.IncomingCallResponse;
import com.app.emcurauc.util.RingtonePlayer;
import com.mikhaellopez.circularimageview.CircularImageView;

import io.agora.openlive.activities.LiveActivity;
import io.agora.openlive.rtc.EngineConfig;

import static sg.com.temasys.skylink.sdk.sampleapp.Utils.getNumRemotePeers;


public class MainActivity extends AppCompatActivity {

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
    //private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //=============================OLC==============================================================
    Activity activity;
    SharedPreferences prefs;
    CustomToast customToast;
    VCallModule vCallModule;
    RelativeLayout incomingCallLayout,outgoingCallLayout;
    TextView tvIncomingCallName;
    CircularImageView imgIncomingCallImage;
    Button btnIncomingCallAccept,btnIncomingCallReject;
    //ImageView ivEndCall;

    //public Timer timerNoAnswer;
    BroadcastReceiver callDisconnectBroadcast;

    boolean isFromCallToCoordinator = false;
    BroadcastReceiver coordinatorCallAccceptBrodcast;
    BroadcastReceiver coordinatorCallConnectBrodcast;

    BroadcastReceiver disconnectSpecialistBroadcast;

    TextView tvOutgoingCallConnecting;
    Button btnEndCall;


    //Emcura new patient to family call
    ImageButton btnEndCall_2;
    Button btnConnectToConsult;
    LinearLayout callButtons;

    @Override
    protected void onStart() {
        if(callDisconnectBroadcast != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(callDisconnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_DISCONNECTED), Context.RECEIVER_NOT_EXPORTED);
            }else{
                registerReceiver(callDisconnectBroadcast, new IntentFilter(VCallModule.INCOMMING_CALL_DISCONNECTED));
            }
        }
        if (coordinatorCallAccceptBrodcast != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(VCallModule.COORDINATOR_CALL_ACCEPTED);
            intentFilter.addAction(VCallModule.COORDINATOR_CALL_REJECTED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(coordinatorCallAccceptBrodcast, intentFilter, RECEIVER_NOT_EXPORTED);
            }else {
                registerReceiver(coordinatorCallAccceptBrodcast, intentFilter);
            }

        }
        if (coordinatorCallConnectBrodcast != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(coordinatorCallConnectBrodcast, new IntentFilter(VCallModule.COORDINATOR_CALL_CONNECTED), RECEIVER_NOT_EXPORTED);
            }else {
                registerReceiver(coordinatorCallConnectBrodcast, new IntentFilter(VCallModule.COORDINATOR_CALL_CONNECTED));
            }
        }
        if(disconnectSpecialistBroadcast != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST), RECEIVER_NOT_EXPORTED);
            }else {
                registerReceiver(disconnectSpecialistBroadcast, new IntentFilter(VCallModule.DISCONNECT_SPECIALIST));
            }
        }
        super.onStart();
    }
    @Override
    protected void onStop() {
        if(callDisconnectBroadcast != null){
            unregisterReceiver(callDisconnectBroadcast);
        }
        if (coordinatorCallAccceptBrodcast != null) {
            unregisterReceiver(coordinatorCallAccceptBrodcast);
        }
        if (coordinatorCallConnectBrodcast != null) {
            unregisterReceiver(coordinatorCallConnectBrodcast);
        }
        if(disconnectSpecialistBroadcast != null){
            unregisterReceiver(disconnectSpecialistBroadcast);
        }
        /*if (new GloabalMethods(activity).checkPlayServices()) {  //this code moved to aftercalldialog
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }*/
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

    //=============================OLC==============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));*/

        // Load selected App key details
      /*  Config.loadSelectedAppKey(this);
        Config.loadRoomUserNames(this);*/


        //==========OLC init video call actitivty
        initVideoCallActivity();

        //==================ahmer work start from here======================
      /*  if (getIntent().hasExtra("fromNotification")) {
            btnIncomingCallAccept.performClick();
        }
        if (getIntent().hasExtra("fromNotificationReject"))
        {
            btnIncomingCallReject.performClick();
        }*/
        //==============end===============
    }

   // @Override
    public void onNavigationDrawerItemSelected(int position) {


        try {
            OnlineCare app = (OnlineCare) getApplication();
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
/*        FragmentManager fragmentManager = getSupportFragmentManager();
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


    /*@Override
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
    }*/

    /**
     * returns fragment
     *
     * @param //position
     * @return fragment to launch based on the item clicked on the navigation drawer
     */
  /*  public Fragment getFragmentToLaunch(int position) {
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
    }*/


    //===================================OLC============================================

    public void initVideoCallActivity() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //ahmer add this line to remove notification
        NotificationManagerCompat.from(MainActivity.this).cancelAll();

        isFromCallToCoordinator = getIntent().getBooleanExtra("isFromCallToCoordinator", false);

        activity = MainActivity.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);

        vCallModule = new VCallModule(activity);
        incomingCallLayout = (RelativeLayout) findViewById(R.id.incomingCallLayout);
        tvIncomingCallName = (TextView) findViewById(R.id.tvIncomingCallName);
        imgIncomingCallImage = (CircularImageView) findViewById(R.id.imgIncomingCallImage);
        btnIncomingCallAccept = (Button) findViewById(R.id.btnIncomingCallAccept);
        btnIncomingCallReject = (Button) findViewById(R.id.btnIncomingCallReject);
        //ivEndCall = (ImageView) findViewById(R.id.ivEndCall);

        outgoingCallLayout = (RelativeLayout) findViewById(R.id.outgoingCallLayout);
        tvOutgoingCallConnecting = (TextView) findViewById(R.id.tvOutgoingCallConnecting);
        btnEndCall = findViewById(R.id.btnEndCall);

        /*ivEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                startActivity(new Intent(activity, AfterCallDialog.class));
                finish();
            }
        });*/

        if (isFromCallToCoordinator) {
            incomingCallLayout.setVisibility(View.GONE);
            outgoingCallLayout.setVisibility(View.VISIBLE);

            btnEndCall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                    }
                    stopCallNotification();
                    vCallModule.endcallCoordinator();
                }
            });

            vCallModule.coordinatorCall();
            coordinatorCallAccceptBrodcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                    }
                    stopCallNotification();
                    if (intent.getAction().equals(VCallModule.COORDINATOR_CALL_ACCEPTED)) {
                        /*if (((ooVooSdkSampleShowApp) getApplication()).isInConference()) {
                            Toast.makeText(SampleActivity.this, "app().isInConference() is tru", 0).show();
                        } else {
                            ((ooVooSdkSampleShowApp) getApplication()).join(DATA.outgoingCallSessionId, false);
                        }*/

                        /*callButtons.setVisibility(View.VISIBLE);
                        outgoingCallLayout.setVisibility(View.GONE);
                        onNavigationDrawerItemSelected(5);*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            checkPermissionAndroid13();
                        }else {
                            checkPermission();
                        }

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
                        playOutgoingRingtone();
                    }
                }
            };
        }else {
            incomingCallLayout.setVisibility(View.VISIBLE);
            outgoingCallLayout.setVisibility(View.GONE);

            tvIncomingCallName.setText(DATA.incomingCallerName);
            DATA.loadImageFromURL(DATA.incomingCallerImage,R.drawable.icon_call_screen,imgIncomingCallImage);
            Constants.ROOM_NAME_MULTI = DATA.incomingCallSessionId;

            btnIncomingCallAccept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    //pd.show();
                    //timerNoAnswer.cancel();
                    MyFirebaseMessagingService.stopCallNotAnswerTimer();
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, "accept");
                    //incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    incomingCallResponse.sendResponse();
                    if (mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                    }
                    stopCallNotification();
                    /*callButtons.setVisibility(View.VISIBLE);
                    incomingCallLayout.setVisibility(View.GONE);
                    onNavigationDrawerItemSelected(5);*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkPermissionAndroid13();
                    }else {
                        checkPermission();
                    }
                /*if (app().isInConference()) {
                    Toast.makeText(getActivity(), "app().isInConference() is tru", 0).show();
                    BaseFragment current_fragment = AVChatSessionFragment.newInstance(null,
                            null, null);//mSignalStrengthMenuItem
                    ((SampleActivity)getActivity()).showFragment(current_fragment);
                    System.gc();
                    Runtime.getRuntime().gc();
                } else {
                    app().join(DATA.incomingCallSessionId, false);
                }*/

                }
            });

            btnIncomingCallReject.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //timerNoAnswer.cancel();
                    MyFirebaseMessagingService.stopCallNotAnswerTimer();
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, "reject");
                    //incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    incomingCallResponse.sendResponse();
                    if (mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                    }
                    stopCallNotification();
                    if(android.os.Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                }
            });


            callDisconnectBroadcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mMediaPlayer != null) {// && mMediaPlayer.isPlaying()
                        mMediaPlayer.stop();
                    }
                    stopCallNotification();
                    vCallModule.sendNotification( "You have missed a live care call from "+ DATA.incomingCallerName,MainActivityNew.class);
                    if(MultiPartyVideoCallFragment.isInVideoCall){
                        return;
                    }
                    startActivity(new Intent(activity, AfterCallDialog.class));
                    finish();
                }
            };
            disconnectSpecialistBroadcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                /*((ooVooSdkSampleShowApp) getApplication()).onEndOfCall();
                ((ooVooSdkSampleShowApp) getApplication()).sendEndOfCall();*/
		              /*  Intent i = new Intent(getActivity(), AfterCallDialog.class);
						startActivity(i);*/

                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                    finish();
                }
            };
            playRingtone();
            vCallModule.unlockScreen();
            vCallModule.incommingCallConnected(DATA.incomingCallUserId);
            /*timerNoAnswer = new Timer();
            timerNoAnswer.schedule(new TimerTask() {
                public void run() {
                    // when the task active then close the dialog
                    DATA.print("------40 seconds");
                    //callResponse = "noanswer";
                    //showWaitingMessage();
                    IncomingCallResponse incomingCallResponse = new IncomingCallResponse(activity, "noanswer");
                    incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    if (mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                    }
                    timerNoAnswer.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                    sendNotification("You have missed a live care call from "+DATA.incomingCallerName,MainActivityNew.class);
                    finish();

                }
            }, 40000);*/ // after 2 second (or 2000 miliseconds), the task will be active.
        }


        //Emcura new patient to patient/family call
        btnEndCall_2 = findViewById(R.id.btnEndCall_2);
        btnConnectToConsult = findViewById(R.id.btnConnectToConsult);
        callButtons = findViewById(R.id.callButtons);

        btnConnectToConsult.setOnClickListener(vCallModule.getConnectBtnListener(getNumRemotePeers()));

        btnEndCall_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                /*if (MultiPartyVideoCallFragment.getNumPeers() >= 2) {
                    DATA.print("--disconnect broadcast sent to specialist");
                    vCallModule.disconnectSpecialist(DATA.selectedDrId, "doctor");
                }*/

                /*DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                DATA.endCall(activity);

                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                startActivity(new Intent(activity, AfterCallDialogEmcura.class));//AfterCallDialog
                finish();*/

                DATA.endCall(activity);

                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                startActivity(new Intent(activity, AfterCallDialog.class));
                finish();

            }
        });


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            MyFirebaseMessagingService.sendStopBC(getBaseContext());
        }
    }

    public MediaPlayer mMediaPlayer;
    public void playRingtone() {
        startCallNotification();
        /*if (mMediaPlayer != null) {
            //if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            //}
        }
        //Uri mediaUri = createUri(getBaseContext(), R.raw.outgoingcall_ringtone); // Audiofile in raw folder  com.app.onlinecare
        Uri mediaUri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.incomingcall);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(activity, mediaUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }catch(Exception e) {
            e.printStackTrace();
        };*/
    }

    public void playOutgoingRingtone() {
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
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }catch(Exception e) {
            e.printStackTrace();
        };
    }





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

    private String[] PERMISSIONSADROID13 = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
    };

    private void checkPermissionAndroid13() {
        boolean granted = true;
        for (String per : PERMISSIONSADROID13) {
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
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, PERMISSIONSADROID13, PERMISSION_REQ_CODE);
        }
        else
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                resetLayoutAndForward();
            } else {
                customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
            }
        }
    }

    private void resetLayoutAndForward() {
        callButtons.setVisibility(View.VISIBLE);
        incomingCallLayout.setVisibility(View.GONE);
        outgoingCallLayout.setVisibility(View.GONE);
        onNavigationDrawerItemSelected(5);
    }

    //GM BHAI CODE FOR RINGTONE PLAYER
    private static RingtonePlayer ringtonePlayer;
    private static Vibrator vibrator;

    private void startCallNotification() {
        //Log.d(TAG, "startCallNotification()");

        stopCallNotification();

        ringtonePlayer = new RingtonePlayer(this);
        ringtonePlayer.play(false);

      /*  Handler handler = new Handler();
        handler.postDelayed(() -> {

            runOnUiThread(() -> {
                vibrator = (Vibrator) MultiVideosActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                long[] vibrationCycle = {0, 1000, 1000};
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(vibrationCycle, 1);
                }
            });

        }, 500); // 3000 milliseconds = 3 seconds*/

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            runOnUiThread(() -> {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null && audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long[] vibrationCycle = {0, 1000, 1000};
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(vibrationCycle, 1);
                    }
                }
            });
        }, 500); // 500 milliseconds = 0.5 seconds


    }

    private void stopCallNotification() {
        //Log.d(TAG, "stopCallNotification()");

        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            ringtonePlayer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();

            vibrator = null;
        }
    }
    //END
}
