//package com.app.onlinecaredr;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.app.onlinecaredr.services.VChatListener;
//import com.app.onlinecaredr.util.DATA;
//import com.quickblox.chat.QBChatService;
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
//	TextView tvCallingName;
//	BroadcastReceiver broadcastReceiver;
//
//	
//	@Override
//	public void onBackPressed() {
//	}
//	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("ON_START_CONNECTING_DR");
//		
//		registerReceiver(broadcastReceiver, filter);
//		
//	}
//	
//	@Override
//    protected void onStop() {
//        super.onStop();
//        
//        unregisterReceiver(broadcastReceiver);
//    }
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
//        qbuser = new QBUser(prefs.getInt("qbUserId", 0));
//        qbuser.setPassword(prefs.getString("qbUsersPassword", ""));
//
//        tvCallingName = (TextView) findViewById(R.id.tvCallingName);
//        tvCallingName.setText(DATA.selectedUserCallName);
//
////        playRingtone();
//    	initChatService();
//        videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//
//
//        callUser();
//		
//        
//        b2.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				end();
//			}
//		});
//        
//        
//		broadcastReceiver = new BroadcastReceiver() {
//			
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				
//				if(intent.getAction().equals("ON_START_CONNECTING_DR")) {
//
//	                   Intent intent1 = new Intent(getBaseContext(), VideoCallScreen.class);
//	                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	                    intent1.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
//	                    startActivity(intent);
//	
//					
//					
//				}
//			}
//		};
//
//
//    }
//    
////    private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
////    	
////		@Override
////		public void onMicrophoneDataReceive(byte[] audioData) {
////			QBVideoChatController.getInstance().sendAudio(audioData);
////		}
////    	
////    	public void onCameraDataReceive(byte[] videoData) {
////    		
//////        	VideoCallScreen.opponentView.render(videoData);
////
////    	};
////
////		@Override
////		public void onOpponentAudioDataReceive(byte[] audioData) {
////			QBVideoChatController.getInstance().playAudio(audioData);
////		}
////
////        @Override
////        public void onVideoChatStateChange(CallState state, VideoChatConfig receivedVideoChatConfig) {
////            Debugger.logConnection("onVideoChatStateChange: " + state);
////            videoChatConfig = receivedVideoChatConfig;
////            /*if (progressDialog != null && progressDialog.isShowing()) {
////                progressDialog.dismiss();
////            }*/
////
////            Log.i("service state",""+state.toString());
////            switch (state) {
////                case ACCEPT:
//////                    Intent intent = new Intent(VChatListener.this, CallActivity.class);
//////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//////                    intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
//////                    startActivity(intent);
//////                   // showCallDialog();
//////                    Log.i("service state 1",""+state.toString());
////
////                    break;
////                case ON_ACCEPT_BY_USER:
////                    Log.i("service state 2",""+state.toString());
////
////                   /* QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);*/
////
////                    //startVideoChatActivity();
////                    break;
////                case ON_REJECTED_BY_USER:
////                    Log.i("service state 3",""+state.toString());
////                       /*Toast.makeText(VChatListener.this, "Rejected by user", Toast.LENGTH_SHORT).show();*/
////                    break;
////                case ON_DID_NOT_ANSWERED:
////                    Log.i("service state 4",""+state.toString());
////
////                   /* Toast.makeText(VChatListener.this, "User did not answer", Toast.LENGTH_SHORT).show();*/
////                    break;
////                case ON_CANCELED_CALL:
////                    videoChatConfig = null;
////                    Log.i("service state 5","nothing");
////
////                    /*if (alertDialog != null && alertDialog.isShowing()){
////                        alertDialog.dismiss();
////                    }
////                    autoCancelHandler.removeCallbacks(autoCancelTask);*/
////                    break;
////                    
////                case ON_CONNECTED:
////
//////                	VideoCallScreen.opponentView.setVisibility(View.VISIBLE);
////                	
////                	break;
////                
////                case ON_CALL_END:
////         
////  //              	VideoCallScreen.opponentView.clear();
////                	break;
////            }
////        }
////    };
//
//
//     public void end()
//        {
////         mMediaPlayer.stop();
////            QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//           // XMPPSender.sendCancelCallMsg(videoChatConfig);
//            QBVideoChatController.getInstance().stopCalling(); 
//  //          super.onDestroy();
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
//	private void initChatService(){
//		QBChatService.setDebugEnabled(true);
//
//		if (!QBChatService.isInitialized()) {
//			Log.d("ActivityLogin", "InitChat");
//			QBChatService.init(this);
//		}else{
//			Log.d("ActivityLogin", "InitChat not needed");
//		}
//	}
//	
//	private void callUser() {
//
//	
//        qbuser = new QBUser(Integer.parseInt(DATA.selectedUserQbid));
//		qbuser.setId(Integer.parseInt(DATA.selectedUserQbid));
//
//        //        qbuser.setPassword(prefs.getString("qbUsersPassword", ""));
//
//		videoChatConfig = QBVideoChatController.getInstance().callFriend(qbuser, CallType.VIDEO_AUDIO, null);
//
//	}
//
//}