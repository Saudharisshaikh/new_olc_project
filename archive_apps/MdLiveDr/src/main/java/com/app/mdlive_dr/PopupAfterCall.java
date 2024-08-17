package com.app.mdlive_dr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;



public class PopupAfterCall extends Activity {
	
	Activity activity;
	
	TextView tvOkDone,tvCallAgain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_after_call);
		
		activity = PopupAfterCall.this;
		
		tvCallAgain = (TextView) findViewById(R.id.tvCallAgain);
		tvOkDone = (TextView) findViewById(R.id.tvOkDone);
		
		tvOkDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				
				
			}
		});
		
		tvCallAgain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});

	}

}
