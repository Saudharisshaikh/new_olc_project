package com.app.priorityone_uc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.priorityone_uc.util.DATA;
import com.app.priorityone_uc.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ShowGroupMessagePopup extends Activity {

	OpenActivity openActivity;
	CircularImageView ivMsgPopup;
	TextView tvMsgPopupName,tvMsgPopupTime,tvMsgPopupMessage;
	Button btnView,btnCancel;

	public static String messageFrom = "",messageText = "",
			messageTime = "",messageImageURL = "none";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_message_popup);
		
		openActivity = new OpenActivity(ShowGroupMessagePopup.this);

		ivMsgPopup = (CircularImageView) findViewById(R.id.ivMsgPopup);
		tvMsgPopupName = (TextView) findViewById(R.id.tvMsgPopupName);
		tvMsgPopupTime = (TextView) findViewById(R.id.tvMsgPopupTime);
		tvMsgPopupMessage = (TextView) findViewById(R.id.tvMsgPopupMessage);
		
		
		tvMsgPopupName.setText(messageFrom);
		tvMsgPopupTime.setText(messageTime);
		tvMsgPopupMessage.setText(messageText);

		DATA.loadImageFromURL(messageImageURL, R.drawable.icon_call_screen, ivMsgPopup);
		
		btnView = (Button) findViewById(R.id.btnView);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setVisibility(View.GONE);
		btnView.setText("Done");
		btnView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//openActivity.open(ViewConversationActivity.class, true);
				finish();
				
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
