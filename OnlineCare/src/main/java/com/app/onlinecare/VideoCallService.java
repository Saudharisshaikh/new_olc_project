//package com.app.onlinecare;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.AlertDialog;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Binder;
//import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.app.onlinecare.util.DATA;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.CallType;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//
//public class VideoCallService extends Service{
//
//	public static int count  = 0;
//	QBUser opponentUser;
//	NotificationCompat.Builder mBuilder;
//	public static final int NOTIFICATION_ID = 1;
//	public static NotificationManager mNotificationManager;
//	SharedPreferences prefs;
//
//	// Binder given to clients
//	private final IBinder mBinder = new LocalBinder();
//
//	/**
//	 * Class used for the client Binder.  Because we know this service always
//	 * runs in the same process as its clients, we don't need to deal with IPC.
//	 */
//	public class LocalBinder extends Binder {
//		public VideoCallService getService() {
//			// Return this instance of LocalService so clients can call public methods
//			return VideoCallService.this;
//		}
//	}
//
//	private VideoChatConfig videoChatConfig;
//	Context ctx;
//	private AlertDialog alertDialog;
//
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return mBinder;
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//
//		ctx = VideoCallService.this;
//
//		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//
//		System.out.println("--online care videocallservice started...");
//
//		initChatService();
//
//		if(DATA.currentUser != null) {
//
//			// Set video chat listener
//			//
//			System.out.println("--online care in videocallservice DATA.currntUser not null");
//
//			try {
//				QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//			} catch (XMPPException e) {
//				e.printStackTrace();
//			}
//		}
//		else {
//
//			System.out.println("--online care in videocallservice DATA.currntUser is null");
//
//			DATA.currentUser = new QBUser(prefs.getInt("qbUserId", 0));
//			DATA.currentUser.setPassword(prefs.getString("qbUserPassword", ""));
//
//			// Set video chat listener
//			//
//			try {
//				QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//			} catch (XMPPException e) {
//				e.printStackTrace();
//			}
//		}
//
//
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//
//		videoChatConfig = intent.getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//
//		Runnable runnable = new Runnable() {               
//
//            @Override
//            public void run() {
//
//    			try {
//    				QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//    			} catch (XMPPException e) {
//    				e.printStackTrace();
//    			}
//            };
//       };
//          new Thread(runnable).start();
//		return START_STICKY;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
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
//			//			opponentView.render(videoData);
//
//			Intent videoDATAIntent = new Intent();
//			videoDATAIntent.putExtra("videoData", videoData);
//			videoDATAIntent.setAction("ON_OPPONENT_VIDEO_RECEIVE");
//			DATA.videoData = videoData;
//
//
//			sendBroadcast(videoDATAIntent);			
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
//				Toast.makeText(getBaseContext(), "ON_CALL_START", Toast.LENGTH_SHORT).show();
//				
//				count = 0;
//
//				sendBroadcast(new Intent("ON_CALL_START"));
//				//				opponentView.setVisibility(View.VISIBLE);
//
//				//                    progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CANCELED_CALL:
//				Toast.makeText(getBaseContext(), "ON_CANCELED_CALL", Toast.LENGTH_SHORT).show();
//
//				sendBroadcast(new Intent("ON_CANCELED_CALL"));
//				count = 0;
//
//				videoChatConfig = null;
//
//				break;
//			case ON_CALL_END:
//				Toast.makeText(getBaseContext(), "ON_CALL_END", Toast.LENGTH_SHORT).show();
//
//				sendBroadcast(new Intent("ON_CALL_END"));
//				count = 0;
//
//				if(mNotificationManager != null) {
//
//					mNotificationManager.cancelAll();
//
//				}
//
//				// clear opponent view
//				//				opponentView.clear();
//				//				btnStopCallBtn.setText("CALL");
//				break;
//			case ACCEPT:
//				Toast.makeText(getBaseContext(), "ACCEPT", Toast.LENGTH_SHORT).show();
//
//				count++;
//
//				DATA.videoChatConfig = videoChatConfig;
//				Intent i = new Intent();
//				i.putExtra("videoCallConfigObj", videoChatConfig);
//				i.setAction("ACCEPT");
//
//				if(count == 1) {
//					sendBroadcast(i);					
//				}
//
//				//				showIncomingCallDialog();
//				break;
//			case ON_ACCEPT_BY_USER:
//				Toast.makeText(getBaseContext(), "ON_ACCEPT_BY_USER", Toast.LENGTH_SHORT).show();
//				count = 0;
//
//				QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);
//				break;
//			case ON_REJECTED_BY_USER:
//				Toast.makeText(getBaseContext(), "ON_REJECTED_BY_USER", Toast.LENGTH_SHORT).show();
////				count = 0;
//
//				sendBroadcast(new Intent("ON_REJECTED_BY_USER"));
//				//                progressBar.setVisibility(View.INVISIBLE);
//				break;
//			case ON_CONNECTED:
//				Toast.makeText(getBaseContext(), "ON_CONNECTED", Toast.LENGTH_SHORT).show();
//				count = 0;
//
//				DATA.videoChatConfig = videoChatConfig;
//
//				sendBroadcast(new Intent("ON_CONNECTED"));
//
//		//		sendNotification("Dr. Aftab Saraz");
//				//                  progressBar.setVisibility(View.INVISIBLE);
//
//				//				opponentView.setVisibility(View.VISIBLE);
//				//				btnStopCallBtn.setText("HANG UP");
//				break;
//			case ON_START_CONNECTING:
//				Toast.makeText(getBaseContext(), "ON_START_CONNECTING", Toast.LENGTH_SHORT).show();
//
//				break;
//			}
//		}
//	};
//
//
//
//
//	public void sendCameraData(byte[] data) {
//
//		System.out.println("--online caresend camera data called");
//
//		if (videoChatConfig != null && videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
//
//
//			return;
//
//		}
//		QBVideoChatController.getInstance().sendVideo(data);
//
//	}
//
//	public void callUser() {
//
//		initChatService();
//
//		Toast.makeText(ctx, "call user in service called", 0).show();
//
//		opponentUser = new QBUser();
//		
//		if(DATA.currentUser.getId() == DATA.junaid9Id) {
//
//			opponentUser.setId(DATA.aftab8Id);
//		}else {
//			opponentUser.setId(DATA.junaid9Id);
//
//		}
//
//
//	//	opponentUser.setId(2287774);
//
//		System.out.println("--online care in videocallscreen before callFriend");
//
//		videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.VIDEO_AUDIO, null);
//
//		System.out.println("--online care in videocallscreen after callFriend");
//	}
//
//	public void finishVideoCall() {
//
//		if(videoChatConfig != null) {
//
//			QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
//
//		}
//	}
//
//	private void sendNotification(String name) {
//		mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//		PendingIntent contentIntent;
//
//		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, VideoCallScreen.class), 0);
//		mBuilder =new NotificationCompat.Builder(this);
//
//		//			mBuilder.setLargeIcon(setIcon(img_link));
//		mBuilder.setContentText(name);
//		//		mBuilder.setSubText("New Message");
//		mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(name));
//		mBuilder.setContentTitle("Ongoing call");
//
//
//		mBuilder.setSmallIcon(R.drawable.ic_launcher);
//		mBuilder.setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission
//		//		mBuilder.setAutoCancel(true);
//		//mBuilder.setLights(argb, onMs, offMs);
//		mBuilder.setContentIntent(contentIntent);
//		mBuilder.addAction(R.drawable.ic_rotate_left, "Disconnect", contentIntent);
//		//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//		startForeground(NOTIFICATION_ID, mBuilder.build());
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
//}
