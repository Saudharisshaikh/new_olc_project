package com.app.mdlive_uc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.app.mdlive_uc.R.color;
import com.app.mdlive_uc.util.DATA;
import com.app.mdlive_uc.util.Database;

public class FreeCare extends Activity {

	Activity activity;

	RadioGroup rgFreeCareOptions, rgFreeCareYesNo;
	Button btnGetFreeCare, btnFreeCareSkip;

	Database db;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.free_care);

		activity = FreeCare.this;

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		db = new Database(activity);		

		rgFreeCareOptions = (RadioGroup) findViewById(R.id.rgFreeCareOptions);
		rgFreeCareYesNo = (RadioGroup) findViewById(R.id.rgFreeCareYesNo);

		btnGetFreeCare = (Button) findViewById(R.id.btnGetFreeCare);
		btnFreeCareSkip = (Button) findViewById(R.id.btnFreeCareSkip);

		rgFreeCareOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			}
		});

		rgFreeCareYesNo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.radioFreeCareNo:
					
					btnGetFreeCare.setBackgroundColor(color.dark_gray);
					btnGetFreeCare.setEnabled(false);
										
					break;

				case R.id.radioFreeCareYes:
					
					btnGetFreeCare.setBackgroundColor(Color.parseColor("#d04c26"));
					btnGetFreeCare.setEnabled(true);
	
					break;
				}

			}
		});
		
		btnGetFreeCare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				db.insertFirstLoginUser(prefs.getString("id", ""));

				Intent i = new Intent(activity, GetLiveCare.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);			
				finish();
				}
		});

		btnFreeCareSkip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				db.insertFirstLoginUser(prefs.getString("id", ""));

				Intent i = new Intent(activity, MainActivityNew.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();
			}
		});

	}

}
