package com.app.mhcsn_cp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.mhcsn_cp.util.OpenActivity;

public class ShowLiveCarePopup extends Activity {

	OpenActivity openActivity;
	TextView tvReferralPopupMessage;
	Button btnView,btnCancel;

	String status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_live_care_popup);

		status = getIntent().getStringExtra("status");
		
		openActivity = new OpenActivity(ShowLiveCarePopup.this);
		tvReferralPopupMessage = (TextView) findViewById(R.id.tvReferralPopupMessage);
		
		btnView = (Button) findViewById(R.id.btnView);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		if(status != null){
			if (status.equalsIgnoreCase("newappointment")) {
				tvReferralPopupMessage.setText("You have new appointment request");
			}
		}else {
			status = "";
		}
		
		btnView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (status.equalsIgnoreCase("newappointment")) {
					openActivity.open(DrsAppointments.class,false);
				}else {
					//openActivity.open(LiveCare.class, true);
					Intent i = new Intent(ShowLiveCarePopup.this,DoctorsList.class);
					i.putExtra("isFromMyDoctors", true);
					startActivity(i);
				}
				
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
