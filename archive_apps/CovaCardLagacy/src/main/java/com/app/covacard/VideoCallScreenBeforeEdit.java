//package com.app.onlinecare;
//
//import java.util.List;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.PowerManager;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecare.quickblox.DialogHelper;
//import com.app.onlinecare.quickblox.OnCallDialogListener;
//import com.app.onlinecare.quickblox.OpponentSurfaceView;
//import com.app.onlinecare.quickblox.OwnSurfaceView;
//import com.app.onlinecare.util.DATA;
//import com.app.onlinecare.util.OpenActivity;
//import com.quickblox.auth.QBAuth;
//import com.quickblox.auth.model.QBSession;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.CallType;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//
//public class VideoCallScreenBeforeEdit extends ActionBarActivity {
//
//	ActionBarActivity activity;
//	ImageView imgVideoCallUsrImg;
//	TextView tvVideoCallUsrName;
//	Button btnStopCallBtn,btnSwitchCamera;
//	MediaPlayer mMediaPlayer;
//
//	private AlertDialog alertDialog;
//	OpenActivity openActivity;
//	SharedPreferences prefs;
//
//	private OwnSurfaceView myView;
//	private OpponentSurfaceView opponentView;
//
//	private VideoChatConfig videoChatConfig;
//	//	final VideoChatApplication app = (VideoChatApplication)getApplication();
//	PowerManager pm;
//	PowerManager.WakeLock wl;
//	AudioManager audioManager;
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//
//		initChatService();
//
//		createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//
//
//		wl.acquire();
//
//		myView.reuseCamera();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		myView.closeCamera();
//
//		wl.release();
//	}
//
//
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.video_chat_new);
//
//		activity = VideoCallScreenBeforeEdit.this;
//
//		//		initChatService();		
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//
//		audioManager = ((AudioManager)getSystemService(Context.AUDIO_SERVICE));
//		audioManager.setMode(AudioManager.MODE_NORMAL);
////        audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
//        
//
//		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//
//
//		// VideoChat settings
//		videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//
//		//		createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//
//		opponentView = (OpponentSurfaceView) findViewById(R.id.opponentView);
//		myView = (OwnSurfaceView) findViewById(R.id.ownCameraView);
//		myView.setZOrderOnTop(true);
//
//		opponentView.setVisibility(View.GONE);
//
//		//		Display display = getWindowManager().getDefaultDisplay();
//		//		Point size = new Point();
//		//		display.getSize(size);
//		//		int width = size.x;
//		//		int height = size.y;
//		//
//		//		System.out.println("--online care screen width "+width );
//		//		System.out.println("--online care screen height "+height );
//		//
//		//		int  viewWidth = (int) (width * 0.25) + 5;
//		//		float aspectRatio = (float) width/height;
//		//		int viewHeight = (int) (viewWidth / aspectRatio);
//		//		
//		//		System.out.println("--online care screen aspectRatio "+aspectRatio );
//		//		System.out.println("--online care screen width "+viewWidth );
//		//		System.out.println("--online care screen width "+viewHeight );
//		//		
//		//
//		//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth,viewHeight);		
//		//		params.setMargins(20, 5, 5, 10);
//		//		params.addRule(RelativeLayout.ABOVE, R.id.bottoml) ;
//		//		myView.setLayoutParams(params);
//
//
////		imgVideoCallUsrImg = (ImageView) findViewById(R.id.imgVideoCallUsrImg);
////		tvVideoCallUsrName = (TextView) findViewById(R.id.tvVideoCallUsrName);
////		btnStopCallBtn = (Button) findViewById(R.id.btnStopCallBtn);
////		btnSwitchCamera = (Button) findViewById(R.id.btnSwitchCamera);
//
//		btnStopCallBtn.setText("CALL");
//
//		btnSwitchCamera.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				myView.switchCamera();
//
//			}
//		});
//
//		myView.setCameraDataListener(new OwnSurfaceView.CameraDataListener() {
//			@Override
//			public void onCameraDataReceive(byte[] data) {
//				if (videoChatConfig != null && videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
//					return;
//				}
//				QBVideoChatController.getInstance().sendVideo(data);
//			}
//		});
//
//
//		btnStopCallBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if(btnStopCallBtn.getText().toString().equals("CALL")) {
//
//
//					//					Toast.makeText(activity, "CAlling", 0).show();
//					callUser();
//
//				}
//				else {
//
//					if(videoChatConfig != null) {
//						btnStopCallBtn.setText("CALL");
//
//						QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//
//					}
//
//					opponentView.clear();
//
//				}
//
//			}
//		});
//
//		QBUser qbUser = new QBUser(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//
//		// Set video chat listener
//		//
//		try {
//			QBVideoChatController.getInstance().setQBVideoChatListener(qbUser, qbVideoChatListener);
//		} catch (XMPPException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	@Override
//	protected void onStop() {
//
//		if(videoChatConfig != null) {
//
//			QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//
//		}
//		super.onStop();
//	}
//
//	OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
//
//		@Override
//		public void onCameraDataReceive(byte[] videoData) {
//			//
//			
//		}
//		@Override
//		public void onMicrophoneDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().sendAudio(audioData);
//			
//			QBVideoChatController.getInstance().setSpeaking(true);
//			
////			audioManager.setMicrophoneMute(false);
////	        audioManager.setMode(AudioManager.MODE_IN_CALL);
////			
//		}
//
//		@Override
//		public void onOpponentVideoDataReceive(final byte[] videoData) {
//			opponentView.render(videoData);
//		}
//
//		@Override
//		public void onOpponentAudioDataReceive(byte[] audioData) {
//			
//			QBVideoChatController.getInstance().setSpeaking(false);
//
//			QBVideoChatController.getInstance().playAudio(audioData);
//			
//	//		audioManager.setMicrophoneMute(true);
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
//				//			Toast.makeText(getBaseContext(), "ON_CALL_START", Toast.LENGTH_SHORT).show();
//
//				opponentView.setVisibility(View.VISIBLE);
//
//				//                    progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CANCELED_CALL:
//				//		Toast.makeText(getBaseContext(), "ON_CANCELED_CALL", Toast.LENGTH_SHORT).show();
//
//				videoChatConfig = null;
//				if (alertDialog != null && alertDialog.isShowing()){
//					alertDialog.dismiss();
//				}
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//
//				break;
//			case ON_CALL_END:
//				//	Toast.makeText(getBaseContext(), "ON_CALL_END", Toast.LENGTH_SHORT).show();
//
////				finish();
//
//				// clear opponent view
//				opponentView.clear();
//				btnStopCallBtn.setText("CALL");
//				break;
//			case ACCEPT:
//				//				Toast.makeText(getBaseContext(), "ACCEPT", Toast.LENGTH_SHORT).show();
//
//				showIncomingCallDialog();
//				break;
//			case ON_ACCEPT_BY_USER:
//				//			Toast.makeText(getBaseContext(), "ON_ACCEPT_BY_USER", Toast.LENGTH_SHORT).show();
//
//				QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);
//				break;
//			case ON_REJECTED_BY_USER:
//				//				Toast.makeText(getBaseContext(), "ON_REJECTED_BY_USER", Toast.LENGTH_SHORT).show();
//
//				//                progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CONNECTED:
//				//			Toast.makeText(getBaseContext(), "ON_CONNECTED", Toast.LENGTH_SHORT).show();
//
//				//                  progressBar.setVisibility(View.INVISIBLE);
//
//				opponentView.setVisibility(View.VISIBLE);
//				btnStopCallBtn.setText("HANG UP");
//				break;
//			case ON_START_CONNECTING:
//				//				Toast.makeText(getBaseContext(), "ON_START_CONNECTING", Toast.LENGTH_SHORT).show();
//				break;
//			}
//		}
//	};
//
//	private Handler autoCancelHandler = new Handler(Looper.getMainLooper());
//	private Runnable autoCancelTask = new Runnable() {
//		@Override
//		public void run() {
//			if (alertDialog != null && alertDialog.isShowing()){
//				alertDialog.dismiss();
//			}
//		}
//	};
//
//
//	private void showIncomingCallDialog() {
//		
//		playRingtone();
//		
//		alertDialog = DialogHelper.showCallDialog(this, new OnCallDialogListener() {
//			@Override
//			public void onAcceptCallClick() {
//				//                progressBar.setVisibility(View.VISIBLE);
//
//				QBVideoChatController.getInstance().acceptCallByFriend(videoChatConfig, null);
//				
//				mMediaPlayer.stop();
//
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//			}
//
//			@Override
//			public void onRejectCallClick() {
//				QBVideoChatController.getInstance().rejectCall(videoChatConfig);
//				mMediaPlayer.stop();
//
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//			}
//		});
//
//		autoCancelHandler.postDelayed(autoCancelTask, 30000);
//	}
//
//
//	private void createSession(String login, final String password) {
//
//		QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
//			@Override
//			public void onSuccess(QBSession qbSession, Bundle bundle) {
//
//
//				System.out.println("--online care in videocallscreen createSession onSuccess");
//				// Save current user
//				//
//				DATA.currentUser = new QBUser(qbSession.getUserId());
//				DATA.currentUser.setPassword(password);
//
//				//		          VideoChatApplication app = (VideoChatApplication)getApplicationContext();
//				//		          app.setCurrentUser(qbSession.getUserId(), password);
//
//				// Login to Chat
//				//
//				QBChatService.getInstance().login(DATA.currentUser, new QBEntityCallbackImpl() {
//					@Override
//					public void onSuccess() {
//						try {
//							System.out.println("--online care in videocallscreen login onSuccess");
//
//							QBVideoChatController.getInstance().initQBVideoChatMessageListener();
//							System.out.println("--online care in videocallscreen login onSuccess after getInstance.initQBVideoChatMessageListener");
//
//						} catch (XMPPException e) {
//							System.out.println("--online care in videocallscreen login onSuccess after getInstance.initQBVideoChatMessageListener exception "+e);
//
//
//							e.printStackTrace();
//						}
//						// show next activity
//						//		                  showCallUserActivity();
//						//		          		openActivity.open(VideoCallScreen.class, false);
//
//						// Toast.makeText(activity, "Success in chat service init...", 0).show();
//
//						QBUser opponentUser = new QBUser();
//
//						//						if(DATA.currentUser.getId() == DATA.user1Id) {
//						//
//						//							opponentUser.setId(DATA.user2Id);
//						//						}else {
//						//							opponentUser.setId(DATA.user1Id);
//						//
//						//						}
//						System.out.println("--online care in videocallscreen before callFriend");
//
//						System.out.println("--online care in my ID / opponent ID "+DATA.currentUser.getId() + " / "+opponentUser.getId());
//
//						//		        		opponentUser.setId((DATA.currentUser.getId() == VideoChatApplication.FIRST_USER_ID ? VideoChatApplication.SECOND_USER_ID : VideoChatApplication.FIRST_USER_ID));
//						videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.AUDIO, null);
//
//						System.out.println("--online care in videocallscreen after callFriend");
//
//					}
//
//					@Override
//					public void onError(List errors) {
//						Toast.makeText(activity, "Error when login", Toast.LENGTH_SHORT).show();
//					}
//				});
//
//			}
//
//			@Override
//			public void onError(List<String> errors) {
//				//       progressDialog.dismiss();
//				Toast.makeText(activity, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
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
//	private void callUser() {
//
//		QBUser opponentUser = new QBUser();
//
//		//		if(DATA.currentUser.getId() == DATA.user1Id) {
//		//
//		//			opponentUser.setId(DATA.user2Id);
//		//		}else {
//		//			opponentUser.setId(DATA.user1Id);
//		//
//		//		}
//		System.out.println("--online care in videocallscreen before callFriend");
//
//		//		System.out.println("--online care in my ID / opponent ID "+DATA.currentUser.getId() + " / "+opponentUser.getId());
//
//		//		        		opponentUser.setId((DATA.currentUser.getId() == VideoChatApplication.FIRST_USER_ID ? VideoChatApplication.SECOND_USER_ID : VideoChatApplication.FIRST_USER_ID));
//		videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.VIDEO_AUDIO, null);
//
//		System.out.println("--online care in videocallscreen after callFriend");
//	}
//
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
//	}
//	
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		
//		mMediaPlayer.stop();
//
//	}
//	
//	// sampleRate = 44100 
//
//	public static int calculate(int sampleRate, byte [] audioData){
//
//	    int numSamples = audioData.length;
//	    int numCrossing = 0;
//	    for (int p = 0; p < numSamples-1; p++)
//	    {
//	        if ((audioData[p] > 0 && audioData[p + 1] <= 0) || 
//	            (audioData[p] < 0 && audioData[p + 1] >= 0))
//	        {
//	            numCrossing++;
//	        }
//	    }
//
//	    float numSecondsRecorded = (float)numSamples/(float)sampleRate;
//	    float numCycles = numCrossing/2;
//	    float frequency = numCycles/numSecondsRecorded;
//
//	    return (int)frequency;
//	}	
//}
