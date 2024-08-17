package com.app.fivestaruc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.fivestaruc.util.CustomToast;
import com.app.fivestaruc.util.OpenActivity;

public class DonateForCare extends BaseActivity {

	Button btnDonate;
	CustomToast customToast;
	OpenActivity openActivity;
	Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_for_care);

		activity = DonateForCare.this;

		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		
		btnDonate = (Button) findViewById(R.id.btnDonate);
		
		btnDonate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				customToast.showToast("This section is under development.", 0, 0);
			}
		});
	}

}
