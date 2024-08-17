//package com.app.onlinecaredr;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.PowerManager;
//import android.support.v7.app.ActionBarActivity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecaredr.quickblox.DialogHelper;
//import com.app.onlinecaredr.quickblox.OnCallDialogListener;
//import com.app.onlinecaredr.util.DATA;
//import com.app.onlinecaredr.util.OpenActivity;
//import com.app.onlinecaredr.util.OpponentSurfaceView1;
//import com.app.onlinecaredr.util.OwnSurfaceView1;
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
//
//	private AlertDialog alertDialog;
//	OpenActivity openActivity;
//	AudioManager audioManager;
//	SharedPreferences prefs;
//
//	private OwnSurfaceView1 myView;
//	private OpponentSurfaceView1 opponentView;
//
//	private VideoChatConfig videoChatConfig;
//	//	final VideoChatApplication app = (VideoChatApplication)getApplication();
//	PowerManager pm;
//	PowerManager.WakeLock wl;
//
//	@Override
//	protected void onResume() {
//		super.onResume();
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
//		audioManager = ((AudioManager)getSystemService(Context.AUDIO_SERVICE));
//		audioManager.setMode(AudioManager.MODE_NORMAL);
////		audioManager.setSpeakerphoneOn(true);
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
//		opponentView = (OpponentSurfaceView1) findViewById(R.id.opponentView);
//		myView = (OwnSurfaceView1) findViewById(R.id.ownCameraView);
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
//		//		DATA.print("--online care screen width "+width );
//		//		DATA.print("--online care screen height "+height );
//		//
//		//		int  viewWidth = (int) (width * 0.25) + 5;
//		//		float aspectRatio = (float) width/height;
//		//		int viewHeight = (int) (viewWidth / aspectRatio);
//		//		
//		//		DATA.print("--online care screen aspectRatio "+aspectRatio );
//		//		DATA.print("--online care screen width "+viewWidth );
//		//		DATA.print("--online care screen width "+viewHeight );
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
//		btnStopCallBtn.setEnabled(true);
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
//		myView.setCameraDataListener(new OwnSurfaceView1.CameraDataListener() {
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
//
//		// Set video chat listener
//		//
//		try {
//			QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
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
//		}
//
//		@Override
//		public void onMicrophoneDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().sendAudio(audioData);
//
//	//		audioManager.setMicrophoneMute(false);
//			QBVideoChatController.getInstance().setSpeaking(true);
//
//		}
//
//		@Override
//		public void onOpponentVideoDataReceive(final byte[] videoData) {
//			opponentView.render(videoData);
//
//		}
//
//		@Override
//		public void onOpponentAudioDataReceive(byte[] audioData) {
//
//			QBVideoChatController.getInstance().setSpeaking(false);
//
//			QBVideoChatController.getInstance().playAudio(audioData);
//
//
////			audioManager.setMicrophoneMute(true);
//
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
//				opponentView.setVisibility(View.VISIBLE);
//
//				//                    progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CANCELED_CALL:
//				//			Toast.makeText(getBaseContext(), "ON_CANCELED_CALL", Toast.LENGTH_SHORT).show();
//
//				videoChatConfig = null;
//				if (alertDialog != null && alertDialog.isShowing()){
//					alertDialog.dismiss();
//				}
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//
//				break;
//			case ON_CALL_END:
//				//		Toast.makeText(getBaseContext(), "ON_CALL_END", Toast.LENGTH_SHORT).show();
//				btnStopCallBtn.setEnabled(true);
//
//				// clear opponent view
//				opponentView.clear();
//				btnStopCallBtn.setText("CALL");
//				break;
//			case ACCEPT:
//				//	Toast.makeText(getBaseContext(), "ACCEPT", Toast.LENGTH_SHORT).show();
//
//				showIncomingCallDialog();
//				break;
//			case ON_ACCEPT_BY_USER:
//				//		Toast.makeText(getBaseContext(), "ON_ACCEPT_BY_USER", Toast.LENGTH_SHORT).show();
//
//				QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);
//				break;
//			case ON_REJECTED_BY_USER:
//				Toast.makeText(getBaseContext(), "CALL REJECTED BY THE PATIENT", Toast.LENGTH_SHORT).show();
//
//				btnStopCallBtn.setEnabled(true);
//
//				//                progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CONNECTED:
//				//			Toast.makeText(getBaseContext(), "ON_CONNECTED", Toast.LENGTH_SHORT).show();
//
//				btnStopCallBtn.setEnabled(true);
//
//				//                  progressBar.setVisibility(View.INVISIBLE);
//
//				opponentView.setVisibility(View.VISIBLE);
//				btnStopCallBtn.setText("HANG UP");
//				break;
//			case ON_START_CONNECTING:
//				//		Toast.makeText(getBaseContext(), "ON_START_CONNECTING", Toast.LENGTH_SHORT).show();
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
//		alertDialog = DialogHelper.showCallDialog(this, new OnCallDialogListener() {
//			@Override
//			public void onAcceptCallClick() {
//				//                progressBar.setVisibility(View.VISIBLE);
//
//				QBVideoChatController.getInstance().acceptCallByFriend(videoChatConfig, null);
//
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//			}
//
//			@Override
//			public void onRejectCallClick() {
//				QBVideoChatController.getInstance().rejectCall(videoChatConfig);
//
//				autoCancelHandler.removeCallbacks(autoCancelTask);
//			}
//		});
//
//		autoCancelHandler.postDelayed(autoCancelTask, 30000);
//	}
//
//
//	private void callUser() {
//
//		btnStopCallBtn.setEnabled(false);
//		QBUser opponentUser = new QBUser();
//		opponentUser.setId(2281690);
//
//		//		if(DATA.currentUser.getId() == DATA.user1Id) {
//		//
//		//			opponentUser.setId(DATA.user2Id);
//		//		}else {
//		//			opponentUser.setId(DATA.user1Id);
//		//
//		//		}
//		DATA.print("--online care in videocallscreen before callFriend");
//
//		//		DATA.print("--online care in my ID / opponent ID "+DATA.currentUser.getId() + " / "+opponentUser.getId());
//
//		//		        		opponentUser.setId((DATA.currentUser.getId() == VideoChatApplication.FIRST_USER_ID ? VideoChatApplication.SECOND_USER_ID : VideoChatApplication.FIRST_USER_ID));
//		videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.VIDEO_AUDIO, null);
//
//		DATA.print("--online care in videocallscreen after callFriend");
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
//
//}
