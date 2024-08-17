//
//package com.app.onlinecare.services;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.quickblox.auth.QBAuth;
//import com.quickblox.auth.model.QBSession;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//
//public class MessageReceiverService extends Service {
//
//	Timer t;
//	public long DELAY = 1300000;
//	public long PERIOD = 1300000;  // every .5 hours
//	public static int count  = 0;
//	SharedPreferences prefs;
//
//	Context ctx;
//	Handler handler;
//	TimerTask doAsynchronousTask;
//
//	@Override
//	public IBinder onBind(Intent arg0) {
//		throw new UnsupportedOperationException("not yet completed...");
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();	
//
//		ctx = MessageReceiverService.this;
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		
//		if(isNetworkConnected()){
//			
//
//			loginToQB();
//			System.out.println("--online care login to qb in oncreate of msgService");			
//		}
//
////				this.startForeground(1, getMyActivityNotification(""));
//
//		handler = new Handler();
//		t = new Timer();
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
//							if(isNetworkConnected()) {
//
//								System.out.println("--online care login to qb in timer run");
//								
//									loginToQB();
//									
////									Intent intent1 = new Intent();
////									intent1.setAction("com.app.onlinecare.START_SERVICE");
////									sendBroadcast(intent1);
//
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
//		return START_STICKY;
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		SharedPreferences.Editor ed = prefs.edit();
//		ed.putBoolean("isServiceRunning", false);
//		ed.putBoolean("qbcalledonce", false);
//		ed.commit();
//
//		System.out.println("--online care service destroyed");
//
//		t.cancel();
//
//	}
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
//	public void loginToQB() {
//
//		QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//
//		initChatService();
//
//		createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//
//			//	createSession("aftab8", "75308266");
////
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
//	private void createSession(String login, final String password) {
//
//		QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
//			@Override
//			public void onSuccess(QBSession qbSession, Bundle bundle) {
//
//				System.out.println("--online care in logintoQB from service");
//				// Save current user
//				//
//				QBUser qbUssr = new QBUser(qbSession.getUserId());
//				qbUssr.setPassword(password);
//
//				SharedPreferences.Editor ed = prefs.edit();
//				ed.putInt("qbUserId", qbSession.getUserId());
//				ed.putString("qbUserPassword", password);
//				ed.commit();
//
//
//				// Login to Chat
//				//
//				QBChatService.getInstance().login(qbUssr, new QBEntityCallbackImpl() {
//					@Override
//					public void onSuccess() {
//						try {
//
//							QBVideoChatController.getInstance().initQBVideoChatMessageListener();
//
//							System.out.println("--online care in initVideoChat in service");
//
//						} catch (XMPPException e) {
//
//							e.printStackTrace();
//						}
//
//						System.out.println("--online care after initVideoChat in service");
//
//					}
//
//					@Override
//					public void onError(List errors) {
//					}
//				});
//
//			}
//
//			@Override
//			public void onError(List<String> errors) {
//				//       progressDialog.dismiss();
//			}
//		});
//	}
//
//}
