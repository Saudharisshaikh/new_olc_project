//package com.app.onlinecaredr;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//
//import com.app.onlinecaredr.util.DATA;
//import com.quickblox.videochat.core.QBVideoChatController;
//
//public class IncomingCallDialog extends Activity{
//	
//
//	Button btnAccept, btnReject;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.incoming_call_dialog);
//
//		btnAccept = (Button) findViewById(R.id.btnAccept);
//		btnReject = (Button) findViewById(R.id.btnReject);
//		
//		btnAccept.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				QBVideoChatController.getInstance().acceptCallByFriend(DATA.videoChatConfig, null);
//
//				Intent in = new Intent(getApplicationContext(), VideoCallScreen.class);
//				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(in);
//				finish();
//				
//			}
//		});
//		
//		btnReject.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				VideoCallService.count = 0;
//				
//				finish();
//			}
//		});
//
//
//
//	}
//
//
//	
//}
