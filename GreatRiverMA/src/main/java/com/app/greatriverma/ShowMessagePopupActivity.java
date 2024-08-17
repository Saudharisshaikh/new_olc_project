package com.app.greatriverma;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.greatriverma.util.DATA;
import com.app.greatriverma.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ShowMessagePopupActivity extends Activity {

	OpenActivity openActivity;
	CircularImageView ivMsgPopup;
	TextView tvMsgPopupName,tvMsgPopupTime,tvMsgPopupMessage;
	Button btnView,btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_message_popup);
		
		openActivity = new OpenActivity(ShowMessagePopupActivity.this);
		
		ivMsgPopup = (CircularImageView) findViewById(R.id.ivMsgPopup);
		tvMsgPopupName = (TextView) findViewById(R.id.tvMsgPopupName);
		tvMsgPopupTime = (TextView) findViewById(R.id.tvMsgPopupTime);
		tvMsgPopupMessage = (TextView) findViewById(R.id.tvMsgPopupMessage);
		
		
		tvMsgPopupName.setText(DATA.msgPatientNameForPopup);
		tvMsgPopupTime.setText(DATA.msgTimeForPopup);
		tvMsgPopupMessage.setText(DATA.msgTextForPopup);
		//UrlImageViewHelper.setUrlDrawable(ivMsgPopup, DATA.msgPatientImageForPopup, R.drawable.ic_my_profile);
		

		DATA.loadImageFromURL(DATA.msgPatientImageForPopup, R.drawable.icon_call_screen, ivMsgPopup);
		
		btnView = (Button) findViewById(R.id.btnView);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		btnView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				openActivity.open(ViewConversationActivity.class, true);
				
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
	}

	
}
