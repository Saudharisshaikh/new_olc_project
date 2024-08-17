//package com.app.onlinecare;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import com.app.onlinecare.services.VChatListener;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.objects.CallType;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//
//
//
//public class CallActivity extends Activity {
//    private ProgressDialog progressDialog;
//    private VideoChatConfig videoChatConfig;
//    private VChatListener vChatListener;
//    QBUser qbuser;
//    MediaPlayer mMediaPlayer;
//	SharedPreferences prefs;
//	
//	@Override
//	public void onBackPressed() {
//	}
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_call);
//        
//    	prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//
//        ImageButton b1 = (ImageButton) findViewById(R.id.button1);
//        ImageButton b2 = (ImageButton) findViewById(R.id.button2);
//
//        playRingtone();
//
//        /*while(!b1.isPressed() || !b2.isPressed()){
//        playRingtone();
//        }*/
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Please Wait");
//        progressDialog
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        // TODO add stopCalling here, send Cancel message if
//                        // need
//                        // XMPPSender.sendCancelCallMsg(videoChatConfig);
//                        // QBVideoChatController.getInstance().stopCalling();
//                    }
//                });
//
//        qbuser = new QBUser(prefs.getInt("qbUserId", 0));
//        qbuser.setPassword(prefs.getString("qbUsersPassword", ""));
//        
//        videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//
//        
//        b1.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
//
//                onAcceptCallClick();
//                mMediaPlayer.stop();
//            }
//            
//
//            private void onAcceptCallClick() {
//                //mMediaPlayer.stop();
//                QBVideoChatController.getInstance().acceptCallByFriend(
//                        videoChatConfig, null);
//                startVideoChatActivity();
//
//            }
//
//            private void startVideoChatActivity() {
//                if (videoChatConfig.getCallType() == CallType.VIDEO_AUDIO) {
//                    Intent intent = new Intent(getBaseContext(),
//                            VideoCallScreen.class);
//                    intent.putExtra(VideoChatConfig.class.getCanonicalName(),
//                            videoChatConfig);
//                    startActivity(intent);
//                    finish();
//                    
//
//                	Toast.makeText(getApplicationContext(), "Video Call Activity start", 0).show();
//                } else if(videoChatConfig.getCallType() == CallType.AUDIO) {
////                    Intent intent = new Intent(getBaseContext(),
////                            ActivityAudioChat.class);
////                    intent.putExtra(VideoChatConfig.class.getCanonicalName(),
////                            videoChatConfig);
////                    startActivity(intent);
//                	Toast.makeText(getApplicationContext(), "Audio Call Activity start", 0).show();
//
//
//                }
//            }
//
//        });
//        
//        b2.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				end(getCurrentFocus());
//			}
//		});
//
//
//    }
//
//     public void end(View view)
//        {
//    	 
//         QBVideoChatController.getInstance().rejectCall(videoChatConfig);
//
//         mMediaPlayer.stop();
////            QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
////           // XMPPSender.sendCancelCallMsg(videoChatConfig);
////            QBVideoChatController.getInstance().stopCalling(); 
////            super.onDestroy();
//         
//			Intent intent1 = new Intent();
//			intent1.setAction("com.app.onlinecare.START_SERVICE");
//			sendBroadcast(intent1);
//
//            finish();
//        }
//
//    public void playRingtone() {
//
//        try {
//               Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//              mMediaPlayer = new MediaPlayer();
//              mMediaPlayer.setDataSource(this, alert);
//              final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//             if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
//             mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//             mMediaPlayer.setLooping(true);
//             mMediaPlayer.prepare();
//             mMediaPlayer.start();
//            }
//            } catch(Exception e) {
//                e.printStackTrace();
//            }   
//
//    /*  try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
//                    notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/
//    }
//
//    
//}