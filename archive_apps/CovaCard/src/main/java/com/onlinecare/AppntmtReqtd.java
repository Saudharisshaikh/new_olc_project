package com.onlinecare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.covacard.util.DATA;
import com.covacard.util.OpenActivity;
import com.covacard.R;

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
				//finish();

				Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

}
