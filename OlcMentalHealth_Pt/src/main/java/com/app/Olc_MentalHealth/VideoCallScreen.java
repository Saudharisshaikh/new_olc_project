//package com.app.onlinecare;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager.LayoutParams;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecare.quickblox.OpponentSurfaceView;
//import com.app.onlinecare.quickblox.OwnSurfaceView;
//import com.app.onlinecare.services.VChatListener;
//import com.app.onlinecare.util.OpenActivity;
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.CallType;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//
//public class VideoCallScreen extends Activity {
//
//	Activity activity;
//
//	RelativeLayout videoCallLayout,incomingCallLayout;
//
//	TextView tvIncomingCallName;
//	ImageView imgIncomingCallImage;
//
//	Button btnStopCall,btnIncomingCallAccept,btnIncomingCallReject,
//	btnMuteMic,btnSwitchCamera,btnSpeakerOnOff;
//	private boolean isMicMuted = false;
//	AudioManager audioManager;
//	Timer t;
//
//	public static int count  = 0;
//	MediaPlayer mMediaPlayer;
//
//	private VideoChatConfig videoChatConfig;
//	QBUser qbUser;
//	VChatListener vChatListener;
//	private OwnSurfaceView myView;
//	private OpponentSurfaceView opponentView;
//
//	OpenActivity openActivity;
//	SharedPreferences prefs;
//
//	PowerManager pm;
//	PowerManager.WakeLock wl;
//	private Window wind;
//	
//	Handler handler;
//	private int counter = 0;
//	
//	@Override
//	public void onBackPressed() {
//	}
//	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		videoChatConfig = null;
//		
//		
//		Intent intent1 = new Intent();
//		intent1.setAction("com.app.onlinecare.START_SERVICE");
//		sendBroadcast(intent1);
//
//		
//		SharedPreferences.Editor ed2 = prefs.edit();
//		ed2.putBoolean("isCallConnected", false);
//		ed2.commit();
//
//	}
//
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		Intent intent1 = new Intent();
//		intent1.setAction("com.app.onlinecare.STOP_VCHAT_SERVICE");
//		sendBroadcast(intent1);
//
//
//		if(prefs.getBoolean("isCallConnected", false)){
//
//			videoCallLayout.setVisibility(View.VISIBLE);
//			incomingCallLayout.setVisibility(View.GONE);
//
//		}
//		else {
//			
//			playRingtone();
//			videoCallLayout.setVisibility(View.GONE);
//			incomingCallLayout.setVisibility(View.VISIBLE);
//		}
//
//
//
//
//		wl.acquire();
//		myView.reuseCamera();
//
//		/******block is needed to raise the application if the lock is*********/
//		wind = this.getWindow();
//		wind.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
//		wind.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//		wind.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
//		/* ^^^^^^^block is needed to raise the application if the lock is*/
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		myView.closeCamera();
//		wl.release();
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.video_chat_new);
//
//		activity = VideoCallScreen.this;
//
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		openActivity = new OpenActivity(activity);
//		
//		videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//		opponentView = (OpponentSurfaceView) findViewById(R.id.opponentView);
//		myView = (OwnSurfaceView) findViewById(R.id.ownCameraView);
//		myView.setZOrderOnTop(true);
//		
//		videoCallLayout = (RelativeLayout) findViewById(R.id.videoCallLayout);
//		incomingCallLayout = (RelativeLayout) findViewById(R.id.incomingCallLayout);
//		
//
//		btnStopCall = (Button) findViewById(R.id.btnStopCall);
//		btnIncomingCallAccept = (Button) findViewById(R.id.btnIncomingCallAccept);
//		btnIncomingCallReject = (Button) findViewById(R.id.btnIncomingCallReject);
//		btnMuteMic = (Button) findViewById(R.id.btnMuteMic);
//		btnSwitchCamera = (Button) findViewById(R.id.btnSwitchCamera);
//		btnSpeakerOnOff = (Button) findViewById(R.id.btnSpeakerOnOff);
//
//		imgIncomingCallImage = (ImageView) findViewById(R.id.imgIncomingCallImage);
//		tvIncomingCallName = (TextView) findViewById(R.id.tvIncomingCallName);
//
//		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//		
//		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//		audioManager.setSpeakerphoneOn(true);
//		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
//	
//		handler = new Handler();
//		t = new Timer();
//
//		t.scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//
//				handler.post(new Runnable() {
//					public void run() {
//
//						counter++;
//
//						if(counter == 15) {
//
//							if(videoChatConfig != null) {
//
//								QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//								QBVideoChatController.getInstance().stopCalling();
//
//
//							}
//
//							t.cancel();
//							finish();
//						}
//
//					}
//				});
//
//
//			}
//		}, 1000	, 1000);
//
//		myView.setCameraDataListener(new OwnSurfaceView.CameraDataListener() {
//			@Override
//			public void onCameraDataReceive(byte[] data) {
//
//				//mService.sendCameraData(data);					
//
//				if (videoChatConfig != null && videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
//
//					return;
//
//				}
//				QBVideoChatController.getInstance().sendVideo(data);
////									DATA.print("--online care send video listener");
//			}
//		});
//
//		btnStopCall.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if(videoChatConfig != null) {
//
//					QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//				}
//
//				videoChatConfig = null;
//
////				opponentView.clear();
//				t.cancel();
//				counter = 0;
//
//				finish();
//
//			}
//		});
//		
//		btnSpeakerOnOff.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if(audioManager.isSpeakerphoneOn()) {
//					
//					audioManager.setSpeakerphoneOn(false);
//					btnSpeakerOnOff.setBackgroundResource(R.drawable.icon_earpiece);
//					
//				}
//				else {
//					audioManager.setSpeakerphoneOn(true);
//					btnSpeakerOnOff.setBackgroundResource(R.drawable.icon_speaker);
//
//				}
//			}
//		});
//
//
//		//accept incoming call
//		btnIncomingCallAccept.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				QBVideoChatController.getInstance().acceptCallByFriend(videoChatConfig, null);
//
//				mMediaPlayer.stop();
//				mMediaPlayer.reset();
//				mMediaPlayer = null;
//				t.cancel();
//				counter = 0;
//
//				videoCallLayout.setVisibility(View.VISIBLE);
//				incomingCallLayout.setVisibility(View.GONE);
//
//			}
//		});
//		
//		btnSwitchCamera.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				myView.switchCamera();
//			}
//		});
//
////		btnMuteMic.setVisibility(View.GONE);
//		btnMuteMic.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//
//				if(audioManager.isMicrophoneMute()) {
//
//					audioManager.setMicrophoneMute(false);
//					btnMuteMic.setBackgroundResource(R.drawable.icon_mic);
//					
//				}
//				else {
//
//					audioManager.setMicrophoneMute(true);
//					btnMuteMic.setBackgroundResource(R.drawable.icon_mic_mute);
//					
//				}
//				
//			}
//		});
//
//
//		//reject incoming call
//		btnIncomingCallReject.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				QBVideoChatController.getInstance().rejectCall(videoChatConfig);
//				mMediaPlayer.stop();
//				mMediaPlayer.reset();
//				mMediaPlayer = null;
//
//				videoChatConfig = null;
//
//				t.cancel();
//				counter = 0;
//
//				finish();
//
//			}
//		});
//
//
//		qbUser = new QBUser(prefs.getInt("qbUserId", 0));
//		qbUser.setPassword(prefs.getString("qbUserPassword", ""));
//
//		try {
//			QBVideoChatController.getInstance().setQBVideoChatListener(qbUser, qbVideoChatListener);
//		} catch (XMPPException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
//
//		@Override
//		public void onCameraDataReceive(byte[] videoData) {
//			//
//		}
//
//		@Override
//		public void onMicrophoneDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().sendAudio(audioData);
//		}
//
//		@Override
//		public void onOpponentVideoDataReceive(final byte[] videoData) {
//			opponentView.render(videoData);
//		}
//
//		@Override
//		public void onOpponentAudioDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().playAudio(audioData);
//		}
//
//		@Override
//		public void onProgress(boolean progress) {
//			//            progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
//		}
//
//		@Override
//		public void onVideoChatStateChange(CallState callState, VideoChatConfig receivedVideoChatConfig) {
//			videoChatConfig = receivedVideoChatConfig;
//
//			switch (callState) {
//			case ON_CALL_START:
////				Toast.makeText(getBaseContext(), "ON_CALL_START", Toast.LENGTH_SHORT).show();
//				
//				break;
//			case ON_CANCELED_CALL:
//				Toast.makeText(getBaseContext(), "ON_CANCELED_CALL", Toast.LENGTH_SHORT).show();
//
//				SharedPreferences.Editor ed2 = prefs.edit();
//				ed2.putBoolean("isCallConnected", false);
//				ed2.commit();
//				videoChatConfig = null;
//				finish();
//
//				break;
//			case ON_CALL_END:
//		//		Toast.makeText(getBaseContext(), "ON_CALL_END", Toast.LENGTH_SHORT).show();
//
//				SharedPreferences.Editor ed1 = prefs.edit();
//				ed1.putBoolean("isCallConnected", false);
//				ed1.commit();
//				finish();
//				
//				openActivity.open(AfterCallDialog.class, true);
//
//
//				break;
//			case ACCEPT:
////				Toast.makeText(getBaseContext(), "ACCEPT", Toast.LENGTH_SHORT).show();
//
//				//				count++;
//				//
//				//				DATA.videoChatConfig = videoChatConfig;
//				//				Intent i = new Intent();
//				//				i.putExtra("videoCallConfigObj", videoChatConfig);
//				//				i.setAction("ACCEPT");
//				//
//				//				if(count == 1) {
//				//					sendBroadcast(i);					
//				//				}
//
//				//				showIncomingCallDialog();
//				break;
//			case ON_ACCEPT_BY_USER:
////				Toast.makeText(getBaseContext(), "ON_ACCEPT_BY_USER", Toast.LENGTH_SHORT).show();
//
//				QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);
//				break;
//			case ON_REJECTED_BY_USER:
//				Toast.makeText(getBaseContext(), "CALL REJECTED BY USER", Toast.LENGTH_SHORT).show();
//				//				count = 0;
//
//				SharedPreferences.Editor ed = prefs.edit();
//				ed.putBoolean("isCallConnected", false);
//				ed.commit();
//
//				break;
//			case ON_CONNECTED:
//				Toast.makeText(getBaseContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
//
//				SharedPreferences.Editor ed3= prefs.edit();
//				ed3.putBoolean("isCallConnected", true);
//				ed3.commit();
//
//				break;
//			case ON_START_CONNECTING:
////				Toast.makeText(getBaseContext(), "ON_START_CONNECTING", Toast.LENGTH_SHORT).show();
//
//				break;
//			}
//		}
//	};
//
//	public void playRingtone() {
//
//		try {
//			Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//			mMediaPlayer = new MediaPlayer();
//			mMediaPlayer.setDataSource(this, alert);
//			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//			if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
//				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//				mMediaPlayer.setLooping(true);
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}   
//
//		/*  try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
//                    notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/
//	}
//}