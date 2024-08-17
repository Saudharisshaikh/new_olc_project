//package com.app.onlinecaredr;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager.LayoutParams;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecaredr.util.CustomToast;
//import com.app.onlinecaredr.util.DATA;
//import com.app.onlinecaredr.util.OpenActivity;
//import com.app.onlinecaredr.util.OpponentSurfaceView1;
//import com.app.onlinecaredr.util.OwnSurfaceView1;
//import com.quickblox.chat.QBChatService;
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
//	RelativeLayout videoCallLayoutDr,outgoingCallLayout,incomingCallLayout;
//
//	TextView tvOutgoingCallName;
//
//	Button btnStopCall,btnOutgoingCallCancel,btnPrescription,
//	btnIncomingCallAccept,btnIncomingCallReject,
//	btnMuteMic,btnSwitchCamera,btnSpeakerOnOff;
//
//	public static int count  = 0; 
//	MediaPlayer mMediaPlayer;
//	private VideoChatConfig videoChatConfig;
//	QBUser qbUser, opponentUser;
//	private OwnSurfaceView1 myView;
//	private  OpponentSurfaceView1 opponentView;
//
//	private boolean isMicMuted = false;
//	AudioManager audioManager;
//	CustomToast customToast;
//
//	OpenActivity openActivity;
//	SharedPreferences prefs;
//
//	PowerManager pm;
//	PowerManager.WakeLock wl;
//	private Window wind;
//
//	Timer t;
//	int counter = 0;
//	Handler handler;
//	
//	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//
//		videoChatConfig = null;
//
//		SharedPreferences.Editor ed1 = prefs.edit();
//		ed1.putBoolean("isCallConnecteddr", false);
//		ed1.commit();
//
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//
//
//		if(mMediaPlayer != null) {
//
//			mMediaPlayer.stop();
//			mMediaPlayer = null;
//
//		}
//
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		DATA.print("--isCallConnectedDr: "+prefs.getBoolean("isCallConnecteddr", false));
//
//		if(prefs.getBoolean("isCallConnecteddr", false)){
//
//			videoCallLayoutDr.setVisibility(View.VISIBLE);
//			outgoingCallLayout.setVisibility(View.GONE);
//
//			t.cancel();
//
//		}
//		else if(DATA.isOutgoingCall) {
//
//			playRingtone();
//
//			initChatService();
//
//			videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.VIDEO_AUDIO, null);
//
//			videoCallLayoutDr.setVisibility(View.GONE);
//			outgoingCallLayout.setVisibility(View.VISIBLE);
//		}
//		else if(!(DATA.isOutgoingCall)) {
//
//			videoCallLayoutDr.setVisibility(View.GONE);
//			outgoingCallLayout.setVisibility(View.GONE);
//			incomingCallLayout.setVisibility(View.VISIBLE);
//			
//		}
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
//
//		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//		audioManager.setSpeakerphoneOn(true);
//		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
//
//		openActivity = new OpenActivity(activity);
//
//		t = new Timer();
//
//		customToast = new CustomToast(activity);
//		handler = new Handler();
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
//						if(counter == 20) {
//
//							if(videoChatConfig != null) {
//
//								QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//								QBVideoChatController.getInstance().stopCalling();
//
//
//							}
//							customToast.showToast("No answer", 0, 0);
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
//		opponentUser = new QBUser();
//		opponentUser.setId(Integer.parseInt(DATA.selectedUserQbid));
//
//		DATA.print("--online care selected user qb id: "+DATA.selectedUserQbid);
//
//		videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//		opponentView = (OpponentSurfaceView1) findViewById(R.id.opponentView);
//		myView = (OwnSurfaceView1) findViewById(R.id.ownCameraView);
//		myView.setZOrderOnTop(true);
//
//		videoCallLayoutDr = (RelativeLayout) findViewById(R.id.videoCallLayoutDr);
//		outgoingCallLayout = (RelativeLayout) findViewById(R.id.outgoingCallLayout);
//		incomingCallLayout = (RelativeLayout) findViewById(R.id.incomingCallLayout);
//		//
//		btnStopCall = (Button) findViewById(R.id.btnStopCallDr);
//		btnOutgoingCallCancel = (Button) findViewById(R.id.btnOutgoingCallCancel);
//		btnMuteMic = (Button) findViewById(R.id.btnMuteMic);
//		btnSwitchCamera = (Button) findViewById(R.id.btnSwitchCamera);
//		btnPrescription = (Button) findViewById(R.id.btnPrescription);
//		btnSpeakerOnOff = (Button) findViewById(R.id.btnSpeakerOnOff);
//				
//		btnIncomingCallAccept = (Button) findViewById(R.id.btnIncomingCallAccept);
//		btnIncomingCallReject = (Button) findViewById(R.id.btnIncomingCallReject);
//
//		
//		tvOutgoingCallName = (TextView) findViewById(R.id.tvOutgoingCallName);
//		tvOutgoingCallName.setText(DATA.selectedUserCallName);
//
//		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//
//
//		myView.setCameraDataListener(new OwnSurfaceView1.CameraDataListener() {
//			@Override
//			public void onCameraDataReceive(byte[] data) {
//
//				//mService.sendCameraData(data);					
//
//				if (videoChatConfig != null && videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
//					return;
//				}
//				QBVideoChatController.getInstance().sendVideo(data);
//				//									DATA.print("--online care send video listener");
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
//					QBVideoChatController.getInstance().stopCalling();
//				}
//
//				opponentView.clear();
//				openActivity.open(AfterCallDialog.class, true);
//
//			}
//		});
//
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
//			}
//		});
//
//		btnOutgoingCallCancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if(videoChatConfig != null) {
//
//					QBVideoChatController.getInstance().stopCalling();
//					QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//				}
//
//				opponentView.clear();
//
//				mMediaPlayer.stop();
////				mMediaPlayer = null;
//
//				videoChatConfig = null;
//				qbVideoChatListener = null;
//				
//				t.cancel();
//				finish();
//
//			}
//		});
//
//		btnPrescription.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				openActivity.open(SendPrescription.class, false);
//			}
//		});
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
//				videoCallLayoutDr.setVisibility(View.VISIBLE);
//				outgoingCallLayout.setVisibility(View.GONE);
//				incomingCallLayout.setVisibility(View.GONE);
//
//			}
//		});
//
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
//				//				Toast.makeText(getBaseContext(), "ON_CALL_START", Toast.LENGTH_SHORT).show();
//
//
//				break;
//			case ON_CANCELED_CALL:
//				//				Toast.makeText(getBaseContext(), "ON_CANCELED_CALL", Toast.LENGTH_SHORT).show();
//
//				SharedPreferences.Editor ed2 = prefs.edit();
//				ed2.putBoolean("isCallConnecteddr", false);
//				ed2.commit();
//				videoChatConfig = null;
//				finish();
//
//				break;
//			case ON_CALL_END:
//								Toast.makeText(getBaseContext(), "ON_CALL_END", Toast.LENGTH_SHORT).show();
//
//				SharedPreferences.Editor ed1 = prefs.edit();
//				ed1.putBoolean("isCallConnecteddr", false);
//				ed1.commit();
//
//				openActivity.open(AfterCallDialog.class, true);
//
//				break;
//			case ACCEPT:
//				//				Toast.makeText(getBaseContext(), "ACCEPT", Toast.LENGTH_SHORT).show();
//
//				break;
//			case ON_ACCEPT_BY_USER:
////				Toast.makeText(getBaseContext(), "ON_ACCEPT_BY USER", Toast.LENGTH_SHORT).show();
//				t.cancel();
//
//				if(mMediaPlayer.isPlaying() && mMediaPlayer != null) {
//
//					mMediaPlayer.stop();
//
//				}
//
//				QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);
//				break;
//			case ON_REJECTED_BY_USER:
//								Toast.makeText(getBaseContext(), "REJECTED BY PATIENT", Toast.LENGTH_SHORT).show();
//				//				count = 0;
//
//								if(mMediaPlayer.isPlaying() && mMediaPlayer != null) {
//
//									mMediaPlayer.stop();
//
//								}
//
//				t.cancel();
//				SharedPreferences.Editor ed = prefs.edit();
//				ed.putBoolean("isCallConnecteddr", false);
//				ed.commit();
//
//				finish();
//
//				break;
//			case ON_CONNECTED:
//				Toast.makeText(getBaseContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
//
//
//				SharedPreferences.Editor ed3= prefs.edit();
//				ed3.putBoolean("isCallConnecteddr", true);
//				ed3.commit();
//
//				break;
//			case ON_START_CONNECTING:
//				//				Toast.makeText(getBaseContext(), "ON_START_CONNECTING", Toast.LENGTH_SHORT).show();
//
//				videoCallLayoutDr.setVisibility(View.VISIBLE);
//				outgoingCallLayout.setVisibility(View.GONE);
//
//				break;
//				
//				case ON_DID_NOT_ANSWERED:
//
//					customToast.showToast("No answer", 0, 0);
//
//					videoChatConfig = null;
//					finish();
//					
//					break;
//					
//			}
//		}
//	};
//
//	public void playRingtone() {
//
//		//			Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//		try {
//			mMediaPlayer = MediaPlayer.create(activity, R.raw.outgoingcall_ringtone);
//			mMediaPlayer.prepare();
//			mMediaPlayer.setLooping(true);
//			mMediaPlayer.start();
//
//
//		}catch(Exception e) {};
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
//	@Override
//	public void onBackPressed() {
//	}
//
//
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
//
//}