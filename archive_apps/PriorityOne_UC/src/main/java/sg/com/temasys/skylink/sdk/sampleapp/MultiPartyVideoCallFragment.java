package sg.com.temasys.skylink.sdk.sampleapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.priorityone_uc.AfterCallDialog;
import com.app.priorityone_uc.R;
import com.app.priorityone_uc.util.DATA;

import org.webrtc.SurfaceViewRenderer;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sg.com.temasys.skylink.sdk.listener.LifeCycleListener;
import sg.com.temasys.skylink.sdk.listener.MediaListener;
import sg.com.temasys.skylink.sdk.listener.OsListener;
import sg.com.temasys.skylink.sdk.listener.RecordingListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.listener.StatsListener;
import sg.com.temasys.skylink.sdk.rtc.Errors;
import sg.com.temasys.skylink.sdk.rtc.Info;
import sg.com.temasys.skylink.sdk.rtc.SkylinkCaptureFormat;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.UserInfo;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.Config;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.TOP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static sg.com.temasys.skylink.sdk.sampleapp.MainActivity.ARG_SECTION_NUMBER;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.getNumRemotePeers;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.getTotalInRoom;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.isConnectingOrConnected;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.permQReset;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.permQResume;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.toastLog;
import static sg.com.temasys.skylink.sdk.sampleapp.Utils.toastLogLong;

/**
 * Created by janidu on 3/3/15.
 */
public class MultiPartyVideoCallFragment extends Fragment implements
        LifeCycleListener, OsListener, MediaListener, RemotePeerListener, RecordingListener,
        StatsListener {

    public String ROOM_NAME;
    public String MY_USER_NAME;


    private static final String TAG = MultiPartyVideoCallFragment.class.getName();

    /**
     * List of Framelayouts for VideoViews
     */
    private static SkylinkConnection skylinkConnection;
    private static SkylinkConfig skylinkConfig;
    // Indicates if camera should be toggled after returning to app.
    // Generally, it should match whether it was toggled when moving away from app.
    // For e.g., if camera was already off, then it would not be toggled when moving away from app,
    // So toggleCamera would be set to false at onPause(), and at onCreateView,
    // it would not be toggled.
    private static boolean toggleCamera;
    private FrameLayout[] videoViewLayouts;
    // List that associate FrameLayout position with PeerId.
    // First position is for local Peer.
    private static String[] peerList = new String[4];
    private Context context;

    // Map with PeerId as key for boolean state
    // that indicates if currently getting WebRTC stats for Peer.
    private static ConcurrentHashMap<String, Boolean> isGettingWebrtcStats =
            new ConcurrentHashMap<String, Boolean>();


    //TextView peer_1_label,peer_2_label,peer_3_label;
    Dialog dialog_full_video;
    FrameLayout peer_full;
    LinearLayout layAllPeers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //ROOM_NAME = Config.ROOM_NAME_PARTY;
        //MY_USER_NAME = Config.USER_NAME_PARTY;
        Config.ROOM_NAME_PARTY = Constants.ROOM_NAME_MULTI;
        ROOM_NAME = Constants.ROOM_NAME_MULTI;
        MY_USER_NAME = "Patient";

        View rootView = inflater.inflate(R.layout.fragment_video_multiparty, container, false);

        FrameLayout selfLayout = (FrameLayout) rootView.findViewById(R.id.self_video);
        FrameLayout peer1Layout = (FrameLayout) rootView.findViewById(R.id.peer_1);
        FrameLayout peer2Layout = (FrameLayout) rootView.findViewById(R.id.peer_2);
        FrameLayout peer3Layout = (FrameLayout) rootView.findViewById(R.id.peer_3);

        videoViewLayouts = new FrameLayout[]{selfLayout, peer1Layout, peer2Layout, peer3Layout};

        peer_full = rootView.findViewById(R.id.peer_full);
        layAllPeers = rootView.findViewById(R.id.layAllPeers);

        toggleFullView();

        /*peer_1_label = (TextView) rootView.findViewById(R.id.peer_1_label);
        peer_2_label = (TextView) rootView.findViewById(R.id.peer_2_label);
        peer_3_label = (TextView) rootView.findViewById(R.id.peer_3_label);*/

        // Set OnClick actions for each Peer's UI.
        /*for (int peerIndex = 0; peerIndex < videoViewLayouts.length; ++peerIndex) {
            FrameLayout frameLayout = videoViewLayouts[peerIndex];
            if (frameLayout == selfLayout) {
                // Show room and self info, plus give option to
                // switch self view between different cameras (if any).
                *//*frameLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (skylinkConnection != null) {
                            String name = Utils.getRoomPeerIdNick(skylinkConnection, ROOM_NAME,
                                    skylinkConnection.getPeerId());
                            TextView selfTV = new TextView(context);
                            selfTV.setText(name);
                            selfTV.setTextIsSelectable(true);
                            AlertDialog.Builder selfDialogBuilder =
                                    new AlertDialog.Builder(context);
                            selfDialogBuilder.setView(selfTV);
                            selfDialogBuilder.setPositiveButton("OK", null);
                            // Get the input video resolution.
                            selfDialogBuilder.setPositiveButton("Input video resolution",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            skylinkConnection.getInputVideoResolution();
                                        }
                                    });

                            selfDialogBuilder.setNegativeButton("Switch Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            skylinkConnection.switchCamera();
                                        }
                                    });
                            selfDialogBuilder.show();
                        }
                    }
                });*//*
            } else {
                // Allow each remote Peer FrameLayout to be able to provide an action menu.
                //frameLayout.setOnClickListener(showMenuRemote(peerIndex));
                frameLayout.setOnClickListener(getPeerFramListener(frameLayout));
            }
        }*/

        // Check if it was an orientation change
        if (savedInstanceState != null) {
            // Resume previous permission request, if any.
            permQResume(context, this, skylinkConnection);

            // Toggle camera back to previous state if required.
            if (toggleCamera) {
                if (getVideoView(null) != null) {
                    skylinkConnection.toggleCamera();
                    toggleCamera = false;
                }
            }

            // Set again the listeners to receive callbacks when events are triggered
            setListeners();
        } else {
            // This is the start of this sample, reset permission request states.
            permQReset();

            // Set toggleCamera back to default state.
            toggleCamera = false;
        }

        // Try to connect to room if we have not tried connecting.
        if (!isConnectingOrConnected()) {
            connectToRoom();
        }

        // Set the appropriate UI.
        // Add all existing VideoViews to UI
        addViews();

        //=====================================OLC==============================================

        toggleAudioButton = (Button) rootView.findViewById(R.id.toggle_audio);
        toggleVideoButton = (Button) rootView.findViewById(R.id.toggle_video);
        toggleCameraButton = (Button) rootView.findViewById(R.id.toggle_camera);

        toggleAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If audio is enabled, mute audio and if audio is enabled, mute it
                audioMuted = !audioMuted;
                skylinkConnection.muteLocalAudio(audioMuted);

                // Set UI and Toast.
                setAudioBtnLabel(true);
            }
        });

        toggleVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If video is enabled, mute video and if video is enabled, mute it
                videoMuted = !videoMuted;
                skylinkConnection.muteLocalVideo(videoMuted);

                // Set UI and Toast.
                setVideoBtnLabel(true);
            }
        });

        toggleCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skylinkConnection.switchCamera();
                /*String toast = "Toggled camera ";
                if (getVideoView(null) != null) {
                    try {
                        skylinkConnection.toggleCamera();
                        if (skylinkConnection.isCameraOn()) {
                            toast += "to restarted!";
                        } else {
                            toast += "to stopped!";
                        }
                    } catch (SkylinkException e) {
                        toast += "but failed as " + e.getMessage();
                    }
                } else {
                    toast += "but failed as local video is not available!";
                }
                Toast.makeText(parentActivity, toast, Toast.LENGTH_SHORT).show();*/
            }
        });
        //====================================OLC===============================================

        return rootView;
    }

    //====================================OLC===============================================
    Button toggleAudioButton,toggleVideoButton,toggleCameraButton;
    private boolean audioMuted,videoMuted;


    private void setAudioBtnLabel(boolean doToast) {
        if (audioMuted) {
            //toggleAudioButton.setText(getString(R.string.enable_audio));
            toggleAudioButton.setBackgroundResource(R.drawable.sdk_ic_mic_selected);
            if (doToast) {
                Toast.makeText(context, getString(R.string.muted_audio),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //toggleAudioButton.setText(getString(R.string.mute_audio));
            toggleAudioButton.setBackgroundResource(R.drawable.sdk_ic_mic);
            if (doToast) {
                Toast.makeText(context, getString(R.string.enabled_audio),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setVideoBtnLabel(boolean doToast) {
        if (videoMuted) {
            //toggleVideoButton.setText(getString(R.string.enable_video));
            toggleVideoButton.setBackgroundResource(R.drawable.sdk_ic_camera_selected);
            if (doToast) {
                Toast.makeText(context, getString(R.string.muted_video),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            //toggleVideoButton.setText(getString(R.string.mute_video));
            toggleVideoButton.setBackgroundResource(R.drawable.sdk_ic_camera);
            if (doToast) {
                Toast.makeText(context, getString(R.string.enabled_video),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    //====================================OLC===============================================


    private void connectToRoom() {
        // Initialize the skylink connection
        initializeSkylinkConnection();

        // Create the Skylink connection string.
        // In production, the connection string should be generated by an external entity
        // (such as a secure App server that has the Skylink App Key secret), and sent to the App.
        // This is to avoid keeping the App Key secret within the application, for better security.
        String skylinkConnectionString = Utils.getSkylinkConnectionString(
                ROOM_NAME, new Date(), SkylinkConnection.DEFAULT_DURATION);

        // The skylinkConnectionString should not be logged in production,
        // as it contains potentially sensitive information like the Skylink App Key ID.

        boolean connectFailed;
        connectFailed = !skylinkConnection.connectToRoom(skylinkConnectionString, MY_USER_NAME);
        if (connectFailed) {
            String log = "Unable to connect to Room! Rotate device to try again later.";
            toastLogLong(TAG, context, log);
            Log.e(TAG, log);
            return;
        }

        // Initialize and use the Audio router to switch between headphone and headset
        AudioRouter.startAudioRouting(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow volume to be controlled using volume keys
        ((MainActivity) context).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Toggle camera back to previous state if required.
        if (toggleCamera) {
            if (getVideoView(null) != null) {
                skylinkConnection.toggleCamera();
                toggleCamera = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop local video source only if not changing orientation
        if (!((MainActivity) context).isChangingConfigurations()) {
            if (getVideoView(null) != null) {
                // Stop local video source if it's on.
                // Record if need to toggleCamera when resuming.
                toggleCamera = skylinkConnection.toggleCamera(false);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //update actionbar title
        ((MainActivity) context).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        this.context = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Remove all views from layouts.
        emptyLayout();
        // Close the room connection when this sample app is finished, so the streams can be closed.
        // I.e. already isConnected() and not changing orientation.
        if (!((MainActivity) context).isChangingConfigurations() && isConnectingOrConnected()) {
            skylinkConnection.disconnectFromRoom();
            AudioRouter.stopAudioRouting(context);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        Utils.onRequestPermissionsResultHandler(
                requestCode, permissions, grantResults, TAG, skylinkConnection);
    }


    //----------------------------------------------------------------------------------------------
    // Skylink helper methods
    //----------------------------------------------------------------------------------------------

    private SkylinkConfig getSkylinkConfig() {
        if (skylinkConfig != null) {
            return skylinkConfig;
        }

        skylinkConfig = new SkylinkConfig();
        // AudioVideo config options can be:
        // NO_AUDIO_NO_VIDEO | AUDIO_ONLY | VIDEO_ONLY | AUDIO_AND_VIDEO
        skylinkConfig.setAudioVideoSendConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        skylinkConfig.setAudioVideoReceiveConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        skylinkConfig.setHasPeerMessaging(true);
        skylinkConfig.setHasFileTransfer(true);
        skylinkConfig.setMirrorLocalView(true);

        // Allow only 3 remote Peers to join, due to current UI design.
        skylinkConfig.setMaxPeers(3);

        skylinkConfig.setAllowHost(false);
        skylinkConfig.setAllowStun(false);
        skylinkConfig.setAllowTurn(true);

        // Set some common configs.
        Utils.skylinkConfigCommonOptions(skylinkConfig);
        return skylinkConfig;
    }

    private void initializeSkylinkConnection() {
        skylinkConnection = SkylinkConnection.getInstance();
        //the app_key and app_secret is obtained from the temasys developer console.
        skylinkConnection.init(Config.getAppKey(), getSkylinkConfig(),
                context.getApplicationContext());
        // Set listeners to receive callbacks when events are triggered
        setListeners();
    }

    /**
     * Set listeners to receive callbacks when events are triggered.
     * SkylinkConnection instance must not be null or listeners cannot be set.
     * Do not set before {@link SkylinkConnection#init} as that will remove all existing Listeners.
     *
     * @return false if listeners could not be set.
     */
    private boolean setListeners() {
        if (skylinkConnection != null) {
            skylinkConnection.setLifeCycleListener(this);
            skylinkConnection.setMediaListener(this);
            skylinkConnection.setOsListener(this);
            skylinkConnection.setRecordingListener(this);
            skylinkConnection.setRemotePeerListener(this);
            skylinkConnection.setStatsListener(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get Video View of a given Peer using SkylinkConnection API.
     *
     * @param peerId null for self Peer.
     * @return Desired Video View or null if not present.
     */
    private SurfaceViewRenderer getVideoView(String peerId) {
        if (skylinkConnection == null) {
            return null;
        }
        return skylinkConnection.getVideoView(peerId);
    }


    private void refreshConnection(String peerId, boolean iceRestart) {
        String peer = "all Peers";
        if (peerId != null) {
            peer = "Peer " + Utils.getPeerIdNick(peerId);
        }
        String log = "Refreshing connection for " + peer;
        if (iceRestart) {
            log += " with ICE restart.";
        } else {
            log += ".";
        }
        toastLog(TAG, context, log);

        // Refresh connections and log errors if any.
        String[] failedPeers = skylinkConnection.refreshConnection(peerId, iceRestart);
        if (failedPeers != null) {
            log = "Unable to refresh ";
            if ("".equals(failedPeers[0])) {
                log += "as there is no Peer in the room!";
            } else {
                log += "for Peer(s): " + Arrays.toString(failedPeers) + "!";
            }
            toastLog(TAG, context, log);
        }
    }

    private boolean startRecording() {
        boolean success = skylinkConnection.startRecording();
        String log = "[SRS][SA] startRecording=" + success +
                ", isRecording=" + skylinkConnection.isRecording() + ".";
        toastLog(TAG, context, log);
        return success;
    }

    private boolean stopRecording() {
        boolean success = skylinkConnection.stopRecording();
        String log = "[SRS][SA] stopRecording=" + success +
                ", isRecording=" + skylinkConnection.isRecording() + ".";
        toastLog(TAG, context, log);
        return success;
    }

    /**
     * Toggle WebRTC Stats logging on or off for specific Peer.
     *
     * @param peerId
     */
    private void webrtcStatsToggle(String peerId) {
        Boolean gettingStats = isGettingWebrtcStats.get(peerId);
        if (gettingStats == null) {
            String log = "[SA][wStatsTog] Peer " + peerId +
                    " does not exist. Will not get WebRTC stats.";
            Log.e(TAG, log);
            return;
        }

        // Toggle the state of getting WebRTC stats to the opposite state.
        if (gettingStats) {
            gettingStats = false;
        } else {
            gettingStats = true;
        }
        isGettingWebrtcStats.put(peerId, gettingStats);
        getWStatsAll(peerId);
    }

    /**
     * Get Transfer speed in kbps of all media streams for specific Peer.
     *
     * @param peerId
     */
    private void getTransferSpeedAll(String peerId) {
        // Request to get media transfer speeds.
        skylinkConnection.getTransferSpeeds(peerId, Info.MEDIA_DIRECTION_BOTH, Info.MEDIA_ALL);
    }

    /**
     * Trigger getWebrtcStats for specific Peer in a loop if current state allows.
     * To stop loop, set {@link #isGettingWebrtcStats} to false.
     *
     * @param peerId
     */
    private void getWStatsAll(final String peerId) {
        Boolean gettingStats = isGettingWebrtcStats.get(peerId);
        if (gettingStats == null) {
            String log = "[SA][WStatsAll] Peer " + peerId +
                    " does not exist. Will not get WebRTC stats.";
            Log.e(TAG, log);
            return;
        }

        if (gettingStats) {
            // Request to get WebRTC stats.
            skylinkConnection.getWebrtcStats(peerId, Info.MEDIA_DIRECTION_BOTH, Info.MEDIA_ALL);

            // Wait for waitMs ms before requesting WebRTC stats again.
            final int waitMs = 1000;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(waitMs);
                    } catch (InterruptedException e) {
                        String error =
                                "[SA][WStatsAll] Error while waiting to call for WebRTC stats again: " +
                                        e.getMessage();
                        Log.e(TAG, error);
                    }
                    getWStatsAll(peerId);
                }
            }).start();

        }
    }


    //----------------------------------------------------------------------------------------------
    // UI helper methods
    //----------------------------------------------------------------------------------------------


    /**
     * Remove all videoViews from layouts.
     */
    private void emptyLayout() {
        int totalInRoom = getTotalInRoom();
        for (int i = 0; i < totalInRoom; i++) {
            Utils.removeViewFromParent(getVideoView(peerList[i]));
        }
    }

    /**
     * Set LayoutParams for a VideoView to fit within it's containing FrameLayout, in the center.
     *
     * @param videoView
     */
    private void setLayoutParams(SurfaceViewRenderer videoView) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, CENTER);
        videoView.setLayoutParams(params);
    }

    /**
     * Get the Peer index of a Peer, given it's PeerId.
     *
     * @param peerId PeerId of the Peer for whom to retrieve its Peer index
     * @return Peer index of a Peer, which is the it's index in peerList.
     * Return a negative number if PeerId could not be found.
     */
    private int getPeerIndex(String peerId) {
        if (peerId == null) {
            return -1;
        }
        for (int index = 0; index < peerList.length; ++index) {
            if (peerId.equals(peerList[index])) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Remove any existing VideoView of a Peer.
     *
     * @param peerId
     * @return The index at which Peer was located, or a negative int if Peer Id was not found.
     */
    private int removePeerView(String peerId) {
        int indexToRemove = getPeerIndex(peerId);
        // Safety check
        if (indexToRemove < 0 || indexToRemove > peerList.length) {
            return -1;
        }


        //OLC GM
        if(peer_full.getVisibility() == VISIBLE){
            peer_full.performClick();
        }
        //reset tags on frame from which videoview removed and added to new frame
        videoViewLayouts[indexToRemove].setTag(null);
        videoViewLayouts[indexToRemove].setTag(R.id.peerIndexTag, null);
        videoViewLayouts[indexToRemove].setTag(R.id.remotePeerIdTag, null);
        //OLC GM ends

        // Remove view
        videoViewLayouts[indexToRemove].removeAllViews();

        //removePeerTxt(indexToRemove);//OLC

        return indexToRemove;
    }

    /**
     * Add a remote Peer into peerList at the first available remote index, and return it's index.
     * Add to any other Peer maps.
     *
     * @param peerId
     * @return Peer Index added at, or a negative int if Peer could not be added.
     */
    private int addRemotePeer(String peerId) {
        if (peerId == null) {
            return -1;
        }
        for (int peerIndex = 1; peerIndex < peerList.length; ++peerIndex) {
            if (peerList[peerIndex] == null) {
                peerList[peerIndex] = peerId;
                // Add to other Peer maps
                isGettingWebrtcStats.put(peerId, false);
                return peerIndex;
            }
        }
        return -1;
    }

    /**
     * Remove a remote Peer from peerList, other Peer maps, and any video view from UI.
     *
     * @param peerId
     */
    private void removeRemotePeer(String peerId) {
        int index = getPeerIndex(peerId);
        if (index < 1 || index > videoViewLayouts.length) {
            return;
        }
        removePeerView(peerId);
        peerList[index] = null;
        isGettingWebrtcStats.remove(peerId);
        shiftUpRemotePeers();
    }

    /**
     * Shift remote Peers and their views up the peerList and UI, such that there are no empty
     * elements or UI between local Peer and the last remote Peer.
     */
    private void shiftUpRemotePeers() {
        int indexEmpty = 0;
        // Remove view from layout.
        for (int i = 1; i < videoViewLayouts.length; ++i) {
            if (peerList[i] == null) {
                indexEmpty = i;
                continue;
            }
            // Switch Peer to empty spot if there is any.
            if (indexEmpty > 0) {
                // Shift peerList.
                String peerId = peerList[i];
                peerList[i] = null;
                peerList[indexEmpty] = peerId;
                // Shift UI.
                FrameLayout peerFrameLayout = videoViewLayouts[i];
                // Put this view in the layout before it.
                SurfaceViewRenderer view = (SurfaceViewRenderer) peerFrameLayout.getChildAt(0);
                if (view != null) {
                    peerFrameLayout.removeAllViews();
                    setLayoutParams(view);
                    videoViewLayouts[indexEmpty].addView(view);


                    //OLC Code ********************* GM
                    videoViewLayouts[indexEmpty].setTag(view);
                    videoViewLayouts[indexEmpty].setTag(R.id.peerIndexTag, indexEmpty);
                    Object remotePeerId = peerFrameLayout.getTag(R.id.remotePeerIdTag);
                    if(remotePeerId != null){
                        videoViewLayouts[indexEmpty].setTag(R.id.remotePeerIdTag, remotePeerId);

                        try {
                            TextView textViewInfo = new TextView(context);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | TOP;

                            textViewInfo.setLayoutParams(layoutParams);
                            textViewInfo.setPadding(10,10,10,10);
                            textViewInfo.setTextColor(Color.WHITE);
                            textViewInfo.setBackgroundColor(Color.parseColor("#80000000"));
                            textViewInfo.setGravity(CENTER);
                            textViewInfo.setText(Utils.getUserDataString((String) remotePeerId));
                            videoViewLayouts[indexEmpty].addView(textViewInfo);
                            System.out.println("-- Code call GM + peerindex "+indexEmpty);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //reset tags on frame from which videoview removed and added to new frame
                    videoViewLayouts[i].setTag(null);
                    videoViewLayouts[i].setTag(R.id.peerIndexTag, null);
                    videoViewLayouts[i].setTag(R.id.remotePeerIdTag, null);
                    //OLC code ends GM
                }
                ++indexEmpty;
            }
        }
    }

    /**
     * Add or update remote Peer's VideoView into the app.
     *
     * @param remotePeerId
     */
    private void addRemoteView(String remotePeerId) {
        SurfaceViewRenderer videoView = getVideoView(remotePeerId);
        if (videoView == null) {
            return;
        }

        // Remove any existing Peer View.
        // This may sometimes be the case, for e.g. in screen sharing.
        removePeerView(remotePeerId);

        int index = getPeerIndex(remotePeerId);
        if (index < 1 || index > videoViewLayouts.length) {
            return;
        }
        setLayoutParams(videoView);
        videoViewLayouts[index].addView(videoView);

        //addPeerTxt(index, remotePeerId);//OLC

        //OLC Code ********************* GM
        try {
            TextView textViewInfo = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | TOP;

            textViewInfo.setLayoutParams(layoutParams);
            textViewInfo.setPadding(10,10,10,10);
            textViewInfo.setTextColor(Color.WHITE);
            textViewInfo.setBackgroundColor(Color.parseColor("#80000000"));
            textViewInfo.setGravity(CENTER);
            textViewInfo.setText(Utils.getUserDataString(remotePeerId));
            videoViewLayouts[index].addView(textViewInfo);
            System.out.println("-- Code call GM + peerindex "+index);

            videoViewLayouts[index].setTag(videoView);
            videoViewLayouts[index].setTag(R.id.peerIndexTag, index);
            videoViewLayouts[index].setTag(R.id.remotePeerIdTag, remotePeerId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addSelfView(SurfaceViewRenderer videoView) {
        if (videoView == null) {
            return;
        }

        // Remove video from previous parent, if any.
        Utils.removeViewFromParent(videoView);

        // Remove self view if its already added
        videoViewLayouts[0].removeAllViews();

        // Add self PeerId and view.
        String[] peers = skylinkConnection.getPeerIdList();
        if (peers != null) {
            peerList[0] = peers[0];
        }
        setLayoutParams(videoView);
        videoViewLayouts[0].addView(videoView);


        //OLC Code ********************* GM
        try {
            TextView textViewInfo = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | TOP;

            textViewInfo.setLayoutParams(layoutParams);
            textViewInfo.setPadding(10,10,10,10);
            textViewInfo.setTextColor(Color.WHITE);
            textViewInfo.setBackgroundColor(Color.parseColor("#80000000"));
            textViewInfo.setGravity(CENTER);
            textViewInfo.setText("Me");
            videoViewLayouts[0].addView(textViewInfo);

            videoViewLayouts[0].setTag(videoView);
            videoViewLayouts[0].setTag(R.id.peerIndexTag, 0);
            videoViewLayouts[0].setTag(R.id.remotePeerIdTag, peerList[0]);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Remove a specific VideoView.
     *
     * @param viewToRemove
     */
    private void removeVideoView(SurfaceViewRenderer viewToRemove) {
        // Remove view from layout.
        for (int peerIndex = 0; peerIndex < videoViewLayouts.length; ++peerIndex) {
            FrameLayout peerFrameLayout = videoViewLayouts[peerIndex];

            if (peerFrameLayout.getChildAt(0) == viewToRemove) {
                // Remove if view found.
                peerFrameLayout.removeView(viewToRemove);
            }
        }
    }

    /**
     * Add all videoViews onto layouts.
     */
    private void addViews() {
        // Add self VideoView
        addSelfView(getVideoView(null));

        // Add remote VideoView(s)
        int totalInRoom = getTotalInRoom();
        // Iterate over the remote Peers only (first Peer is self Peer)
        for (int i = 1; i < totalInRoom; i++) {
            addRemoteView(peerList[i]);
        }
    }

    /**
     * Create and return onClickListener that
     * show list of potential actions on clicking space for remote Peers.
     */
    /*private OnClickListener showMenuRemote(final int peerIndex) {
        // Get peerId
        return new OnClickListener() {
            @Override
            public void onClick(View v) {

                int totalInRoom = getTotalInRoom();
                // Do not allow action if no one is in the room.
                if (totalInRoom == 0) {
                    return;
                }

                String peerIdTemp = null;
                if (peerIndex < totalInRoom) {
                    peerIdTemp = peerList[peerIndex];
                }
                final String peerId = peerIdTemp;
                PopupMenu.OnMenuItemClickListener clickListener =
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.vid_res_sent:
                                        if (peerId == null || skylinkConnection == null) {
                                            return false;
                                        }
                                        skylinkConnection.getSentVideoResolution(peerId);
                                        return true;
                                    case R.id.vid_res_recv:
                                        if (peerId == null || skylinkConnection == null) {
                                            return false;
                                        }
                                        skylinkConnection.getReceivedVideoResolution(peerId);
                                        return true;
                                    case R.id.webrtc_stats:
                                        if (peerId == null) {
                                            return false;
                                        }
                                        webrtcStatsToggle(peerId);
                                        return true;
                                    case R.id.transfer_speed:
                                        if (peerId == null) {
                                            return false;
                                        }
                                        getTransferSpeedAll(peerId);
                                        return true;
                                    case R.id.recording_start:
                                        return startRecording();
                                    case R.id.recording_stop:
                                        return stopRecording();
                                    case R.id.restart:
                                        if (peerId == null) {
                                            return false;
                                        }
                                        refreshConnection(peerId, false);
                                        return true;
                                    case R.id.restart_all:
                                        refreshConnection(null, false);
                                        return true;
                                    case R.id.restart_ice:
                                        if (peerId == null) {
                                            return false;
                                        }
                                        refreshConnection(peerId, true);
                                        return true;
                                    case R.id.restart_all_ice:
                                        refreshConnection(null, true);
                                        return true;
                                    default:
                                        Log.e(TAG, "Unknown menu option: " + id + "!");
                                        return false;
                                }
                            }
                        };
                // Add room name to title
                String title = Utils.getRoomPeerIdNick(skylinkConnection, ROOM_NAME, peerId);

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(clickListener);

                popupMenu.getMenu().add(title);
                // Populate actions of Popup Menu.
                if (peerId != null) {
                    popupMenu.getMenu().add(0, R.id.vid_res_sent, 0, R.string.vid_res_sent);
                    popupMenu.getMenu().add(0, R.id.vid_res_recv, 0, R.string.vid_res_recv);
                }
                if (peerId != null) {
                    String statsStr = getString(R.string.webrtc_stats);
                    final Boolean gettingStats = isGettingWebrtcStats.get(peerId);
                    if ((gettingStats != null) && gettingStats) {
                        statsStr += " (ON)";
                    } else {
                        statsStr += " (OFF)";
                    }
                    popupMenu.getMenu().add(0, R.id.webrtc_stats, 0, statsStr);
                    popupMenu.getMenu().add(0, R.id.transfer_speed, 0, R.string.transfer_speed);
                }
                popupMenu.getMenu().add(0, R.id.recording_start, 0, R.string.recording_start);
                popupMenu.getMenu().add(0, R.id.recording_stop, 0, R.string.recording_stop);
                if (peerId != null) {
                    popupMenu.getMenu().add(0, R.id.restart, 0, R.string.restart);
                }
                popupMenu.getMenu().add(0, R.id.restart_all, 0, R.string.restart_all);
                if (peerId != null) {
                    popupMenu.getMenu().add(0, R.id.restart_ice, 0, R.string.restart_ice);
                }
                popupMenu.getMenu().add(0, R.id.restart_all_ice, 0, R.string.restart_all_ice);
                popupMenu.show();
            }
        };
    }*/

    //----------------------------------------------------------------------------------------------
    // Skylink Listeners
    //----------------------------------------------------------------------------------------------

    /***
     * Lifecycle Listener Callbacks -- triggered during events that happen during the SDK's
     * lifecycle
     */

    @Override
    public void onConnect(boolean isSuccessful, String message) {
        if (isSuccessful) {

            // start audio routing and turn on speaker
            AudioRouter.startAudioRouting(context.getApplicationContext());
            AudioRouter.turnOnSpeaker();

            String log = "Connected to room " + ROOM_NAME + " (" + skylinkConnection.getRoomId() +
                    ") as " + skylinkConnection.getPeerId() + " (" + MY_USER_NAME + ").";
            toastLogLong(TAG, context, log);

            peerList[0] = skylinkConnection.getPeerIdList()[0];
        } else {
            String log = "Skylink failed to connect!\nReason : " + message;
            toastLogLong(TAG, context, log);
        }
    }

    @Override
    public void onDisconnect(int errorCode, String message) {

        // turn off speaker to the normal state and stop audio routing
        AudioRouter.turnOffSpeaker();
        AudioRouter.stopAudioRouting(context.getApplicationContext());


        String log = "[onDisconnect] ";
        if (errorCode == Errors.DISCONNECT_FROM_ROOM) {
            log += "We have successfully disconnected from the room.";
        } else if (errorCode == Errors.DISCONNECT_UNEXPECTED_ERROR) {
            log += "WARNING! We have been unexpectedly disconnected from the room!";
        }
        log += " Server message: " + message;
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onLockRoomStatusChange(String remotePeerId, boolean lockStatus) {
        String log = "[SA] Peer " + remotePeerId + " changed Room locked status to "
                + lockStatus + ".";
        toastLog(TAG, context, log);
    }

    @Override
    public void onReceiveLog(int infoCode, String message) {
        Utils.handleSkylinkReceiveLog(infoCode, message, context, TAG);
    }

    @Override
    public void onWarning(int errorCode, String message) {
        Utils.handleSkylinkWarning(errorCode, message, context, TAG);
    }

    /**
     * Media Listeners Callbacks - triggered when receiving changes to Media Stream from the
     * remote peer
     */

    @Override
    public void onLocalMediaCapture(SurfaceViewRenderer videoView) {
        if (videoView == null) {
            return;
        }
        addSelfView(videoView);
    }

    @Override
    public void onInputVideoResolutionObtained(int width, int height, int fps, SkylinkCaptureFormat captureFormat) {
        String log = "[SA][VideoResInput] The current video input has width x height, fps: " +
                width + " x " + height + ", " + fps + " fps.\r\n";
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onReceivedVideoResolutionObtained(String peerId, int width, int height, int fps) {
        String log = "[SA][VideoResRecv] The current video received from Peer " + peerId +
                " has width x height, fps: " + width + " x " + height + ", " + fps + " fps.\r\n";
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onSentVideoResolutionObtained(String peerId, int width, int height, int fps) {
        String log = "[SA][VideoResSent] The current video sent to Peer " + peerId +
                " has width x height, fps: " + width + " x " + height + ", " + fps + " fps.\r\n";
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onVideoSizeChange(String peerId, Point size) {
        String peer = "Peer " + Utils.getPeerIdNick(peerId);
        // If peerId is null, this call is for our local video.
        if (peerId == null) {
            peer = "We've";
        }
        Log.d(TAG, peer + " got video size changed to: " + size.toString() + ".");
    }

    @Override
    public void onRemotePeerMediaReceive(String remotePeerId, SurfaceViewRenderer videoView) {
        addRemoteView(remotePeerId);
        String log = "Received new ";
        if (videoView != null) {
            log += "Video ";
        } else {
            log += "Audio ";
        }
        log += "from Peer " + Utils.getPeerIdNick(remotePeerId) + ".\r\n";

        UserInfo remotePeerUserInfo = skylinkConnection.getUserInfo(remotePeerId);

        log += "isAudioStereo:" + remotePeerUserInfo.isAudioStereo() + ".\r\n" +
                "video height:" + remotePeerUserInfo.getVideoHeight() + ".\r\n" +
                "video width:" + remotePeerUserInfo.getVideoHeight() + ".\r\n" +
                "video frameRate:" + remotePeerUserInfo.getVideoFps() + ".";
        toastLog(TAG, context, log);
    }

    @Override
    public void onRemotePeerAudioToggle(String remotePeerId, boolean isMuted) {
        String log = "Peer " + Utils.getPeerIdNick(remotePeerId) +
                " Audio mute status via:\r\nCallback: " + isMuted + ".";

        // It is also possible to get the mute status via the UserInfo.
        UserInfo userInfo = skylinkConnection.getUserInfo(remotePeerId);
        if (userInfo != null) {
            log += "\r\nUserInfo: " + userInfo.isAudioMuted() + ".";
        }
        toastLog(TAG, context, log);
    }

    @Override
    public void onRemotePeerVideoToggle(String remotePeerId, boolean isMuted) {
        String log = "Peer " + Utils.getPeerIdNick(remotePeerId) +
                " Video mute status via:\r\nCallback: " + isMuted + ".";

        // It is also possible to get the mute status via the UserInfo.
        UserInfo userInfo = skylinkConnection.getUserInfo(remotePeerId);
        if (userInfo != null) {
            log += "\r\nUserInfo: " + userInfo.isVideoMuted() + ".";
        }
        toastLog(TAG, context, log);
    }

    /**
     * OsListener Callbacks - triggered by Android OS related events.
     */
    @Override
    public void onPermissionRequired(
            final String[] permissions, final int requestCode, final int infoCode) {
        Utils.onPermissionRequiredHandler(permissions, requestCode, infoCode, TAG, context, this, skylinkConnection);
    }

    @Override
    public void onPermissionGranted(String[] permissions, int requestCode, int infoCode) {
        Utils.onPermissionGrantedHandler(permissions, infoCode, TAG);
    }

    @Override
    public void onPermissionDenied(String[] permissions, int requestCode, int infoCode) {
        Utils.onPermissionDeniedHandler(infoCode, context, TAG);
    }

    /**
     * Remote Peer Listener Callbacks - triggered during events that happen when data or connection
     * with remote peer changes
     */

    @Override
    public void onRemotePeerJoin(String remotePeerId, Object userData, boolean hasDataChannel) {
        addRemotePeer(remotePeerId);
        String log = "Your Peer " + Utils.getPeerIdNick(remotePeerId) + " connected.";
        toastLog(TAG, context, log);
    }

    @Override
    public void onRemotePeerLeave(String remotePeerId, String message, UserInfo userInfo) {
        int numRemotePeers = getNumRemotePeers();
        removeRemotePeer(remotePeerId);
        String log = "Your Peer " + Utils.getPeerIdNick(remotePeerId, userInfo) + " left: " +
                message + ". " + numRemotePeers + " remote Peer(s) left in the room.";
        toastLog(TAG, context, log);

        try {
            //removePeerView(remotePeerId); old code
            // Toast.makeText(getActivity(),"peer left: id: "+remotePeerId,0).show();
            Log.d(TAG, "onRemotePeerLeave: userdata "+skylinkConnection.getUserData(remotePeerId)+" no of peers: "+getNumRemotePeers());

            if (getNumRemotePeers() < 1){
                if(getActivity() != null){
                    //DATA.call_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    DATA.endCall(getActivity());
                }
                startActivity(new Intent(getActivity(), AfterCallDialog.class));
                getActivity().finish();
            }
        }catch (Exception e){
            System.out.println("-- Exception onRemotePeerLeave");
            e.printStackTrace();
        }
    }

    @Override
    public void onRemotePeerConnectionRefreshed(
            String remotePeerId, Object userData, boolean hasDataChannel, boolean wasIceRestarted) {
        String peer = "Skylink Media Relay server";
        if (remotePeerId != null) {
            peer = "Peer " + Utils.getPeerIdNick(remotePeerId);
        }
        String log = "Your connection with " + peer + " has just been refreshed";
        if (wasIceRestarted) {
            log += ", with ICE restarted.";
        } else {
            log += ".\r\n";
        }

        UserInfo remotePeerUserInfo = skylinkConnection.getUserInfo(remotePeerId);
        log += "isAudioStereo:" + remotePeerUserInfo.isAudioStereo() + ".\r\n" +
                "video height:" + remotePeerUserInfo.getVideoHeight() + ".\r\n" +
                "video width:" + remotePeerUserInfo.getVideoHeight() + ".\r\n" +
                "video frameRate:" + remotePeerUserInfo.getVideoFps() + ".";
        toastLog(TAG, context, log);
    }

    @Override
    public void onRemotePeerUserDataReceive(String remotePeerId, Object userData) {
        // If Peer has no userData, use an empty string for nick.
        String nick = "";
        if (userData != null) {
            nick = userData.toString();
        }
        String log = "[SA][onRemotePeerUserDataReceive] Peer " + Utils.getPeerIdNick(remotePeerId) +
                ":\n" + nick;
        toastLog(TAG, context, log);
    }

    @Override
    public void onOpenDataConnection(String remotePeerId) {
        Log.d(TAG, "onOpenDataConnection for Peer " + Utils.getPeerIdNick(remotePeerId) + ".");
    }

    /**
     * Recording Listener Callbacks - triggered during Recording events.
     */

    @Override
    public void onRecordingStart(String recordingId) {
        String log = "[SRS][SA] Recording Started! isRecording=" +
                skylinkConnection.isRecording() + ".";
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onRecordingStop(String recordingId) {
        String log = "[SRS][SA] Recording Stopped! isRecording=" +
                skylinkConnection.isRecording() + ".";
        toastLogLong(TAG, context, log);
    }

    @Override
    public void onRecordingVideoLink(String recordingId, String peerId, String videoLink) {
        String peer = " Mixin";
        if (peerId != null) {
            peer = " Peer " + Utils.getPeerIdNick(peerId) + "'s";
        }
        String msg = "Recording:" + recordingId + peer + " video link:\n" + videoLink;

        // Create a clickable video link.
        final SpannableString videoLinkClickable = new SpannableString(msg);
        Linkify.addLinks(videoLinkClickable, Linkify.WEB_URLS);

        // Create TextView for video link.
        final TextView msgTxtView = new TextView(context);
        msgTxtView.setText(videoLinkClickable);
        msgTxtView.setMovementMethod(LinkMovementMethod.getInstance());

        // Create AlertDialog to present video link.
        AlertDialog.Builder videoLinkDialogBuilder = new AlertDialog.Builder(context);
        videoLinkDialogBuilder.setTitle("Recording: " + recordingId + " Video link");
        videoLinkDialogBuilder.setView(msgTxtView);
        videoLinkDialogBuilder.setPositiveButton("OK", null);
        videoLinkDialogBuilder.show();
        Log.d(TAG, "[SRS][SA] " + msg);
    }

    @Override
    public void onRecordingError(String recordingId, int errorCode, String description) {
        String log = "[SRS][SA] Received Recording error with errorCode:" + errorCode +
                "! Error: " + description;
        toastLogLong(TAG, context, log);
        Log.e(TAG, log);
    }

    /**
     * Stats Listener Callbacks - triggered during statistics measuring events.
     */

    @Override
    public void onWebrtcStatsReceived(final String peerId, int mediaDirection, int mediaType, HashMap<String, String> stats) {
        // Log the WebRTC stats.
        StringBuilder log =
                new StringBuilder("[SA][WStatsRecv] Received for Peer " + peerId + ":\r\n");
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            log.append(entry.getKey()).append(": ").append(entry.getValue()).append(".\r\n");
        }
        Log.d(TAG, log.toString());
    }

    @Override
    public void onTransferSpeedReceived(String peerId, int mediaDirection, int mediaType, double transferSpeed) {
        String direction = "Send";
        if (Info.MEDIA_DIRECTION_RECV == mediaDirection) {
            direction = "Recv";
        }
        // Log the transfer speeds.
        String log = "[SA][TransSpeed] Transfer speed for Peer " + peerId + ": " +
                Info.getInfoString(mediaType) + " " + direction + " = " + transferSpeed + " kbps";
        Log.d(TAG, log);
    }


    //================================================OLC============================================================
    public OnClickListener getPeerFramListener(final FrameLayout peerFrameLayout){
        return  new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String peerId = peerFrameLayout.getTag().toString();
                    final SurfaceViewRenderer videoView = getVideoView(peerId);
                    if (videoView == null) {
                        return;
                    }
                    // Remove video from previous parent, if any.
                    dialog_full_video = new Dialog(getActivity());
                    dialog_full_video.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_full_video.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog_full_video.setContentView(R.layout.lay_full_video);
                    final FrameLayout peer_Full = (FrameLayout) dialog_full_video.findViewById(R.id.peer_Full);
                    Utils.removeViewFromParent(videoView);
                    peer_Full.addView(videoView);
                    peer_Full.setTag(peerId);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog_full_video.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog_full_video.show();
                    dialog_full_video.getWindow().setAttributes(lp);

                    dialog_full_video.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Utils.removeViewFromParent(videoView);
                            addRemoteView(peer_Full.getTag().toString());
                        }
                    });
                    peer_Full.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_full_video.dismiss();
                        }
                    });
                    //root_layout.setVisibility(View.GONE);
                    //peer_Full.setVisibility(View.VISIBLE);
                    //peer_Full.bringToFront();
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    /*public void removePeerTxt(int indexToRemove){
        try{
            if(videoViewLayouts[indexToRemove].getId() == R.id.peer_1){
                peer_1_label.setText("");
            }else if(videoViewLayouts[indexToRemove].getId() == R.id.peer_2){
                peer_2_label.setText("");
            }else if(videoViewLayouts[indexToRemove].getId() == R.id.peer_3){
                peer_3_label.setText("");
            }
        }catch (Exception e){
            System.out.println("-- Exception in removePeerView");
            e.printStackTrace();
        }
    }

    public void addPeerTxt(int index, String remotePeerId){
        try {
            videoViewLayouts[index].setTag(remotePeerId);
            if (videoViewLayouts[index].getId() == R.id.peer_1) {
                peer_1_label.setText(Utils.getUserDataString(remotePeerId));
            }else if (videoViewLayouts[index].getId() == R.id.peer_2) {
                peer_2_label.setText(Utils.getUserDataString(remotePeerId));
            }else if (videoViewLayouts[index].getId() == R.id.peer_3) {
                peer_3_label.setText(Utils.getUserDataString(remotePeerId));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/



    public void toggleFullView(){
        for (FrameLayout frameLayout: videoViewLayouts) {
            frameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Object framTag = frameLayout.getTag();
                        if(framTag instanceof SurfaceViewRenderer){
                            ViewGroup.LayoutParams layoutParamsAllPeers = layAllPeers.getLayoutParams();
                            layoutParamsAllPeers.height = 0;
                            layoutParamsAllPeers.width = 0;
                            layAllPeers.setLayoutParams(layoutParamsAllPeers);

                            peer_full.setVisibility(VISIBLE);


                            SurfaceViewRenderer remoteView = (SurfaceViewRenderer) framTag;
                            int peerIndex = (int) frameLayout.getTag(R.id.peerIndexTag);
                            String remotePeerId = (String) frameLayout.getTag(R.id.remotePeerIdTag);

                            peer_full.setTag(remoteView);
                            peer_full.setTag(R.id.peerIndexTag, peerIndex);
                            peer_full.setTag(R.id.remotePeerIdTag, remotePeerId);

                            Utils.removeViewFromParent(remoteView);
                            peer_full.removeAllViews();
                            setLayoutParams(remoteView);
                            peer_full.addView(remoteView);


                            try {
                                //SkylinkPeer newPeer = presenter.processGetPeerByIndex(peerIndex+1);
                                //Utils.getUserDataString(remotePeerId)

                                TextView textViewInfo = new TextView(context);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | TOP;

                                textViewInfo.setLayoutParams(layoutParams);
                                textViewInfo.setPadding(10,10,10,10);
                                textViewInfo.setTextColor(Color.WHITE);
                                textViewInfo.setBackgroundColor(Color.parseColor("#80000000"));
                                textViewInfo.setGravity(CENTER);
                                textViewInfo.setText(Utils.getUserDataString(remotePeerId));
                                peer_full.addView(textViewInfo);
                                System.out.println("-- Code call GM + peerindex "+peerIndex);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            System.out.println("-- Fram layout tag is null or not instance of surfaceviewranderer");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        peer_full.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Object framTag = peer_full.getTag();
                    if(framTag instanceof SurfaceViewRenderer){
                        peer_full.setVisibility(GONE);

                        ViewGroup.LayoutParams layoutParamsAllPeers = layAllPeers.getLayoutParams();
                        layoutParamsAllPeers.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParamsAllPeers.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layAllPeers.setLayoutParams(layoutParamsAllPeers);

                        SurfaceViewRenderer remoteView = (SurfaceViewRenderer) peer_full.getTag();
                        int peerIndex = (int) peer_full.getTag(R.id.peerIndexTag);
                        String remotePeerId = (String) peer_full.getTag(R.id.remotePeerIdTag);

                        // Remove video from previous parent, if any.
                        Utils.removeViewFromParent(remoteView);
                        videoViewLayouts[peerIndex].removeAllViews();
                        setLayoutParams(remoteView);
                        videoViewLayouts[peerIndex].addView(remoteView);

                        try {
                            //SkylinkPeer newPeer = presenter.processGetPeerByIndex(peerIndex+1);

                            TextView textViewInfo = new TextView(context);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | TOP;

                            textViewInfo.setLayoutParams(layoutParams);
                            textViewInfo.setPadding(10,10,10,10);
                            textViewInfo.setTextColor(Color.WHITE);
                            textViewInfo.setBackgroundColor(Color.parseColor("#80000000"));
                            textViewInfo.setGravity(CENTER);
                            textViewInfo.setText(Utils.getUserDataString(remotePeerId));
                            videoViewLayouts[peerIndex].addView(textViewInfo);
                            System.out.println("-- Code call GM + peerindex "+peerIndex);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("-- Fram layout peerFull tag is null or not instance of surfaceviewranderer");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public static boolean isInVideoCall = false;
    @Override
    public void onStart() {
        System.out.println("## fragment multiparty onstart");
        isInVideoCall = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        System.out.println("## fragment multiparty onstop");
        isInVideoCall = false;
        super.onStop();
    }

}