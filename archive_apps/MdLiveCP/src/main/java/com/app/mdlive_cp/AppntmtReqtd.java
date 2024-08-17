package com.app.mdlive_cp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.OpenActivity;

public class AppntmtReqtd extends AppCompatActivity{

	Button btnAppReqOk;
	AppCompatActivity activity;
	OpenActivity openActivity;
	TextView tvApptnmt;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appointment_requested);
		
		tvApptnmt = (TextView) findViewById(R.id.tvApptnmt);
		//tvApptnmt.setText(DATA.requestedAppntDay+", "+DATA.requestedAppntDate+"\n"+DATA.requestedAppntTime);
		
		tvApptnmt.setText(DATA.requestedAppntDate);

		activity = AppntmtReqtd.this;
		openActivity = new OpenActivity(activity);

		btnAppReqOk = (Button) findViewById(R.id.btnAppReqOk);
		
		btnAppReqOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//openActivity.open(MainActivityNew.class, true);
				finish();
			}
		});
	}

}
