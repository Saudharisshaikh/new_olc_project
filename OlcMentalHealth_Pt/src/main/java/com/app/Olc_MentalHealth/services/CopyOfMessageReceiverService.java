//
//package com.app.onlinecare.services;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.jivesoftware.smack.XMPPException;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Binder;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.app.onlinecare.MainActivityNew;
//import com.app.onlinecare.VideoCallScreen;
//import com.app.onlinecare.util.AsyncReceiveMessage;
//import com.app.onlinecare.util.DATA;
//import com.app.onlinecare.util.Database;
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
//public class CopyOfMessageReceiverService extends Service {
//
//	JSONArray receviMsgsArray, receiveImgArray, isSendReturnArray,eventsNotifyArray,
//	webMsgsArray, eventsRemainderArray;
//	JSONObject jsonObject;
//	Timer t;
//	public int DELAY = 2000;
//	public int PERIOD = 9000;  // every 9 Secs
//	CopyOfMessageReceiverService mService;
//    boolean mBound = false;
//	public static int count  = 0;
//	QBUser opponentUser;
//	// Binder given to clients
//	private final IBinder mBinder = new LocalBinder();
//	/**
//	 * Class used for the client Binder.  Because we know this service always
//	 * runs in the same process as its clients, we don't need to deal with IPC.
//	 */
//	public class LocalBinder extends Binder {
//		public CopyOfMessageReceiverService getService() {
//			// Return this instance of LocalService so clients can call public methods
//			return CopyOfMessageReceiverService.this;
//		}
//	}
//
//	
//	private VideoChatConfig videoChatConfig;
//
//	String userStatus;
//	
//	int i = 0;
// 	NotificationCompat.Builder mBuilder;
//	public static final int NOTIFICATION_ID = 1;
//	public static NotificationManager mNotificationManager;
//	//NotificationCompat.Builder builder;
//
//	private int qBLogincount = 0;
//	Database db;
//
//	public static final String BROADCAST_ACTION = "com.app.onlinecare";
//	Intent intent;
//	SharedPreferences prefs;
//
//	Context ctx;
//	Handler handler;
//	TimerTask doAsynchronousTask;
//	
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return mBinder;
//}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();	
//
//		ctx = CopyOfMessageReceiverService.this;
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		
//		initChatService();
//		
////		this.startForeground(1, get)
//
//		db = new Database(getApplicationContext());
//		handler = new Handler();
//		t = new Timer();
//
////		broadcaster = LocalBroadcastManager.getInstance(ctx);		
//
//		SharedPreferences.Editor ed = prefs.edit();
//		ed.putBoolean("isServiceRunning", true);
//		ed.commit();
//
////        Intent intent = new Intent(ctx, VideoCallService.class);
////        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//
////		if(DATA.currentUser != null) {
////
////			// Set video chat listener
////			//
////			DATA.print("--online care in videocallservice DATA.currntUser not null");
////
////			try {
////				QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
////			} catch (XMPPException e) {
////				e.printStackTrace();
////			}
////		}
////		else {
////
////			DATA.print("--online care in videocallservice DATA.currntUser is null");
////
////			DATA.currentUser = new QBUser(prefs.getInt("qbUserId", 0));
////			DATA.currentUser.setPassword(prefs.getString("qbUserPassword", ""));
////
////			// Set video chat listener
////			//
////			try {
////				QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
////			} catch (XMPPException e) {
////				e.printStackTrace();
////			}
////		}
//
//		
//		doAsynchronousTask = new TimerTask() {       
//			@Override
//			public void run() {
//				handler.post(new Runnable() {
//
//					public void run() {
//       			
//						try {
//							
//							qBLogincount++;
//
//							DATA.print("--online care qbcalled once in service: "+prefs.getBoolean("qbcalledonce", false));
//
//							if(isNetworkConnected()) {
//								
//								if(DATA.currentUser != null) {
//
//									// Set video chat listener
//									//
//									DATA.print("--online care in videocallservice DATA.currntUser not null");
//
//									try {
//										QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//									} catch (XMPPException e) {
//										e.printStackTrace();
//									}
//								}
//								else {
//
//									DATA.print("--online care in videocallservice DATA.currntUser is null");
//
//									DATA.currentUser = new QBUser(prefs.getInt("qbUserId", 0));
//									DATA.currentUser.setPassword(prefs.getString("qbUserPassword", ""));
//
//									// Set video chat listener
//									//
//									try {
//										QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//									} catch (XMPPException e) {
//										e.printStackTrace();
//									}
//								}
//
//
//								if(!prefs.getBoolean("qbcalledonce", false) || DATA.currentUser == null){
//									
//									loginToQB();
//
//								}
//								
//								if(qBLogincount == 600) {									
//									
//									qBLogincount = 0;
//									
//									loginToQB();
//									
//								}
//								else {
//									
//									AsyncReceiveMessage asyncReceiveMessage = new AsyncReceiveMessage(ctx, prefs.getString("id", ""));
//									asyncReceiveMessage.execute("");
//								}
//							}
//							else {
//							}
//
//						} catch (Exception e) {
//						}	
//					}
//				});
//			}
//		};
//		t.schedule(doAsynchronousTask, DELAY, PERIOD); //execute in every 50000 ms
//
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		
//		if(videoChatConfig != null){
//			videoChatConfig = intent.getParcelableExtra(VideoChatConfig.class.getCanonicalName());			
//		}
//
//
//		// TODO Auto-generated method stub
//		return Service.START_REDELIVER_INTENT;
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
//		DATA.print("--online caresend camera data called");
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
//		DATA.print("--online care in videocallscreen before callFriend");
//
//		videoChatConfig = QBVideoChatController.getInstance().callFriend(opponentUser, CallType.VIDEO_AUDIO, null);
//
//		DATA.print("--online care in videocallscreen after callFriend");
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
//
//	@Override
//	@Deprecated
//	public void onStart(Intent intent, int startId) {
//		super.onStart(intent, startId);
//
//		//		DATA.print("--service started...in start");
//
//	}
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		SharedPreferences.Editor ed = prefs.edit();
//		ed.putBoolean("isServiceRunning", false);
//		ed.putBoolean("qbcalledonce", false);
//		ed.commit();
//
//		DATA.print("--online care service destroyed");
//		
//		t.cancel();
//		
//      if (mBound) {
//      unbindService(mConnection);
//      mBound = false;
//  }
//
//			}
//
//	
//	private boolean isNetworkConnected() {
//		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo ni = cm.getActiveNetworkInfo();
//		if (ni == null) {
//			// There are no active networks.
//			return false;
//		} else
//			return true;
//	}
//
//	private void sendNotification(String message, String from, String img_link,String status) {
//		mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//		PendingIntent contentIntent;
//
//			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivityNew.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//		mBuilder =new NotificationCompat.Builder(this);
//
//		if(status.equals("msg")) {
//
//			//			mBuilder.setLargeIcon(setIcon(img_link));
//			mBuilder.setContentText(message);
//			mBuilder.setSubText("New Message");
//			mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//			mBuilder.setContentTitle(from);
//
//		}
//	}
//
//	public void setResults(String result) {
//		
//		try {
//			JSONObject jObject = new JSONObject(result);
//			
//			int incoming_call_status = jObject.getInt("incoming_call_status");
//			
//			if(incoming_call_status == 1) {
//
////				Intent i = new Intent(ctx, VideoCallScreen.class);
////				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////				ctx.startActivity(i);
//			
//				
//			}
//			
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		
////						sendNotification(temp.msg, temp.fromfname, temp.img_link, "url");
////
////						intent = new Intent(BROADCAST_ACTION);   
////						intent.putExtra("fromID", temp.fromID);
////						intent.putExtra("msg", temp.msg);
////						intent.putExtra("dateTime", temp.dateTime);
////						sendBroadcast(intent);
////
////
////						db.insertMessages(temp.fromID, temp.msg, prefs.getString("id", ""), temp.dateTime, "1", "0", "0","1","");
//	}
//
//	public void loginToQB() {
//
//		QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//
//		initChatService();
//		
//		createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//		
////		createSession("aftab8", "75308266");
//
//	}
//	
//	private void initChatService(){
//		  QBChatService.setDebugEnabled(true);
//
//		  if (!QBChatService.isInitialized()) {
//		      Log.d("ActivityLogin", "InitChat");
//		      QBChatService.init(this);
//		  }else{
//		      Log.d("ActivityLogin", "InitChat not needed");
//		  }
//		}
//	
//	private void createSession(String login, final String password) {
//
//		QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
//			@Override
//			public void onSuccess(QBSession qbSession, Bundle bundle) {
//
//
//				DATA.print("--online care in logintoQB from service");
//				// Save current user
//				//
//				DATA.currentUser = new QBUser(qbSession.getUserId());
//				DATA.currentUser.setPassword(password);
//				
//				SharedPreferences.Editor ed = prefs.edit();
//				ed.putInt("qbUserId", qbSession.getUserId());
//				ed.putString("qbUserPassword", password);
//				ed.commit();
//
//				
//				// Login to Chat
//				//
//				QBChatService.getInstance().login(DATA.currentUser, new QBEntityCallbackImpl() {
//					@Override
//					public void onSuccess() {
//						try {
//
//							QBVideoChatController.getInstance().initQBVideoChatMessageListener();
//							
//							SharedPreferences.Editor ed = prefs.edit();
//							ed.putBoolean("qbcalledonce", true);
//							ed.commit();
//
//							DATA.print("--online care in initVideoChat in service");
//
//						} catch (XMPPException e) {
//
//							e.printStackTrace();
//						}
//
//						DATA.print("--online care after initVideoChat in service");
//
//					}
//
//					@Override
//					public void onError(List errors) {
//						Toast.makeText(ctx, "Error when login", Toast.LENGTH_SHORT).show();
//					}
//				});
//
//			}
//
//			@Override
//			public void onError(List<String> errors) {
//				//       progressDialog.dismiss();
//				Toast.makeText(ctx, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//
//	/** Defines callbacks for service binding, passed to bindService() */
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            LocalBinder binder = (LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//            
//            Toast.makeText(ctx, "service bound", 0).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//            
//            Toast.makeText(ctx, "service disconnected", 0).show();
//        }
//    };
//
//}
