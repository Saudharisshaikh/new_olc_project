//package com.app.onlinecaredr;
//
//import java.util.List;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecaredr.util.ChoosePictureDialog;
//import com.app.onlinecaredr.util.DATA;
//import com.app.onlinecaredr.util.OpenActivity;
//import com.quickblox.auth.QBAuth;
//import com.quickblox.auth.model.QBSession;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//
//public class AwaitingAppointments extends ActionBarActivity {
//
//	ImageView imgApntmtDrImage;
//	TextView tvApntmtDrName;
//	Button btnApnmtUploadReports,btnApnmntStartVideoCall;
//	OpenActivity openActivity;
//	SharedPreferences prefs;
//
//	ActionBarActivity activity;
//	
//	
//	
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		
//		DATA.isFromDrApmtUploadReports = false;
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//
//	}
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.awaiting_appointments);
//		
//		activity = AwaitingAppointments.this;
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		
//		initChatService();
//		
//		imgApntmtDrImage = (ImageView) findViewById(R.id.imgApntmtDrImage);
//		tvApntmtDrName = (TextView) findViewById(R.id.tvApntmtDrName);
//		btnApnmtUploadReports = (Button) findViewById(R.id.btnApnmtUploadReports);
//		btnApnmntStartVideoCall = (Button) findViewById(R.id.btnApnmtStartVideoCall);
//		
//		openActivity = new OpenActivity(activity);	
//
//		btnApnmtUploadReports.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				DATA.isFromDrApmtUploadReports = true;
//				
//				Intent intent = new Intent(activity,ChoosePictureDialog.class);
//				startActivity(intent);
//				
//			}
//		});
//		
//		btnApnmntStartVideoCall.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				DATA.print("--online care qb_username on videocallscreen: "+prefs.getString("qb_username", ""));
//				DATA.print("--online care qb_password on videocallscreen: "+prefs.getString("qb_password", ""));
//	
//	//			createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));
//
//				openActivity.open(VideoCallScreen.class, false);
//
//			}
//		});
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
//				DATA.print("--online care in videocallscreen createSession onSuccess");
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
//
//							QBVideoChatController.getInstance().initQBVideoChatMessageListener();
//
//						} catch (XMPPException e) {
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
////						QBUser opponentUser = new QBUser();
////
////						if(DATA.currentUser.getId() == DATA.user1Id) {
////
////							opponentUser.setId(DATA.user2Id);
////						}else {
////							opponentUser.setId(DATA.user1Id);
////
////						}
////						DATA.print("--online care in videocallscreen before callFriend");
////
////						DATA.print("--online care in my ID / opponent ID "+DATA.currentUser.getId() + " / "+opponentUser.getId());
//
//						//		        		opponentUser.setId((DATA.currentUser.getId() == VideoChatApplication.FIRST_USER_ID ? VideoChatApplication.SECOND_USER_ID : VideoChatApplication.FIRST_USER_ID));
//						
//						openActivity.open(VideoCallScreen.class, false);
//
////						DATA.print("--online care in videocallscreen after callFriend");
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
//}
