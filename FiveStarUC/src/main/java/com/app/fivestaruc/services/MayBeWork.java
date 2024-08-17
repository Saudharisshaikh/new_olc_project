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
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.app.onlinecare.VideoCallScreen;
//import com.quickblox.auth.QBAuth;
//import com.quickblox.auth.model.QBSession;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//
//public class MayBeWork extends Service {
//
//    QBUser qbUser;
//	SharedPreferences prefs;
//
//	boolean flag = false;
//	private void createSession(String login, final String password) {
//		QBAuth.createSession(login, password,
//				new QBEntityCallbackImpl<QBSession>() {
//					@Override
//					public void onSuccess(QBSession qbSession, Bundle bundle) {
//
//						// Save current user
//						//
//						Toast.makeText(getApplicationContext(),
//								"current user: " + qbSession.getUserId(), 0)
//								.show();
//						qbUser = new QBUser(qbSession.getUserId());
//						qbUser.setPassword(password);
//
//						// Login to Chat
//						//
//						QBChatService.getInstance().login(qbUser,
//								new QBEntityCallbackImpl() {
//									@Override
//									public void onSuccess() {
//										try {
//											QBVideoChatController
//													.getInstance()
//													.initQBVideoChatMessageListener();
//										} catch (XMPPException e) {
//											e.printStackTrace();
//										}
//										// show next activity
//										try {
//											System.out
//													.println("-----: Timer is Running!");
//
//											QBVideoChatController
//													.getInstance()
//													.setQBVideoChatListener(
//															qbUser,
//															qbVideoChatListener);
//										} catch (XMPPException e) {
//											e.printStackTrace();
//										}
//
//										// t.scheduleAtFixedRate(new TimerTask()
//										// {
//										//
//										// @Override
//										// public void run() {
//										// h.post(new Runnable() {
//										//
//										// @Override
//										// public void run() {
//										// // TODO Auto-generated
//										// // method stub
//										// try {
//										// DATA.print("-----: Timer is Running!");
//										// VideoChatApplication app =
//										// (VideoChatApplication)
//										// getApplication();
//										//
//										// QBVideoChatController
//										// .getInstance()
//										// .setQBVideoChatListener(
//										// app.getCurrentUser(),
//										// qbVideoChatListener);
//										// } catch (XMPPException e) {
//										// e.printStackTrace();
//										// }
//										// }
//										// });
//										//
//										// }
//										// }, 1000, 2000);
//
//									}
//
//									@Override
//									public void onError(List errors) {
//										Toast.makeText(getApplicationContext(),
//												"Error when login",
//												Toast.LENGTH_SHORT).show();
//									}
//								});
//
//					}
//
//					@Override
//					public void onError(List<String> errors) {
//
//						Toast.makeText(
//								getApplicationContext(),
//								"Error when login, check test users login and password",
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//	}
//
//	private void initChatService() {
//		QBChatService.setDebugEnabled(true);
//
//		if (!QBChatService.isInitialized()) {
//			Log.d("ActivityLogin", "InitChat");
//			QBChatService.init(this);
//		} else {
//			Log.d("ActivityLogin", "InitChat not needed");
//		}
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
//
//		}
//
//		@Override
//		public void onOpponentAudioDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().playAudio(audioData);
//		}
//
//		@Override
//		public void onProgress(boolean progress) {
//			// progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
//		}
//
//		@Override
//		public void onVideoChatStateChange(CallState callState,
//				VideoChatConfig receivedVideoChatConfig) {
//
//			switch (callState) {
//			case ON_CALL_START:
//				Toast.makeText(getBaseContext(), "ON_CALL_START",
//						Toast.LENGTH_SHORT).show();
//
//				break;
//			case ON_CANCELED_CALL:
//				Toast.makeText(getBaseContext(), "ON_CANCELED_CALL",
//						Toast.LENGTH_SHORT).show();
//
//				break;
//			case ON_CALL_END:
//				Toast.makeText(getBaseContext(), "ON_CALL_END",
//						Toast.LENGTH_SHORT).show();
//
//				// clear opponent view
//
//				break;
//			case ACCEPT:
//				Toast.makeText(getBaseContext(), "Bhaii Call Agayi hai",
//						Toast.LENGTH_SHORT).show();
//				flag = true;
//
//				break;
//			case ON_ACCEPT_BY_USER:
//
//				break;
//			case ON_REJECTED_BY_USER:
//
//				break;
//			case ON_CONNECTED:
//
//				break;
//			case ON_START_CONNECTING:
//				Toast.makeText(getBaseContext(), "ON_START_CONNECTING",
//						Toast.LENGTH_SHORT).show();
//				break;
//			}
//		}
//	};
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	Timer t = new Timer();
//	Handler h = new Handler();
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//
//		initChatService();
//
//		createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//		
//
//
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//
//		Toast.makeText(getApplicationContext(), "Service Is destroyed!", 0)
//				.show();
//
//	}
//
//	Timer t2 = new Timer();
//	Handler h2 = new Handler();
//
//	@Override
//	public void onStart(Intent intent, int startId) {
//		// TODO Auto-generated method stub
//		
//		
//		t.scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				h2.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						if (flag) {
//							Toast.makeText(getApplicationContext(), "Trying to open Activity", 0).show();
//							Intent dialogIntent = new Intent(getBaseContext(), VideoCallScreen.class);
//							dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							getApplication().startActivity(dialogIntent);
//							flag = false;
//							t2.cancel();
//						}
//						
//					}
//				});
//			}
//		}, 1000, 1000);
//	}
//}
