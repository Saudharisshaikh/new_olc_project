package com.app.mhcsn_ma;

import com.app.mhcsn_ma.util.OpenActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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

		openActivity = new OpenActivity(ShowLiveCarePopup.this);
		tvReferralPopupMessage = (TextView) findViewById(R.id.tvReferralPopupMessage);

		btnView = (Button) findViewById(R.id.btnView);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		status = getIntent().getStringExtra("status");

		if (status.equalsIgnoreCase("newpatient")) {
			tvReferralPopupMessage.setText("New patient added to your live care queue");
		} else {
			tvReferralPopupMessage.setText("You have new appointment request");
		}


		btnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (status.equalsIgnoreCase("newpatient")) {
					openActivity.open(LiveCare.class, true);
				} else {
					openActivity.open(DrsAppointments.class, true);
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
