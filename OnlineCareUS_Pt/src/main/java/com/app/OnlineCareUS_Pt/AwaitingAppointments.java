package com.app.OnlineCareUS_Pt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.util.ChoosePictureDialog;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.OpenActivity;

public class AwaitingAppointments extends BaseActivity {

	ImageView imgApntmtDrImage;
	TextView tvApntmtDrName;
	Button btnApnmtUploadReports,btnApnmntStartVideoCall;
	OpenActivity openActivity;
	SharedPreferences prefs;
	
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		DATA.isFromDrApmtUploadReports = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awaiting_appointments);
		
		activity = AwaitingAppointments.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		//initChatService();
		
		imgApntmtDrImage = (ImageView) findViewById(R.id.imgApntmtDrImage);
		tvApntmtDrName = (TextView) findViewById(R.id.tvApntmtDrName);
		btnApnmtUploadReports = (Button) findViewById(R.id.btnApnmtUploadReports);
		btnApnmntStartVideoCall = (Button) findViewById(R.id.btnApnmtStartVideoCall);
		
		openActivity = new OpenActivity(activity);	

		btnApnmtUploadReports.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DATA.isFromDrApmtUploadReports = true;
				
				Intent intent = new Intent(activity,ChoosePictureDialog.class);
				startActivity(intent);
				
			}
		});
		
		btnApnmntStartVideoCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DATA.print("--online care qb_username on videocallscreen: "+prefs.getString("qb_username", ""));
				DATA.print("--online care qb_password on videocallscreen: "+prefs.getString("qb_password", ""));
	
	//			createSession(prefs.getString("qb_username", ""), prefs.getString("qb_password", ""));

				//openActivity.open(VideoCallScreen.class, false);

			}
		});

	}
	
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

}
