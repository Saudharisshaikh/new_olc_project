package com.app.emcuradr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Splash extends Activity {

	TextView tvSplashContinue,tvSplashCAllninonon;
	//SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		/*RelativeLayout lay_splash = (RelativeLayout) findViewById(R.id.lay_splash);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		if (prefs.getString("id", "0").equalsIgnoreCase("84")) {
			lay_splash.setBackgroundResource(R.drawable.splash_tr);
		}else {
			lay_splash.setBackgroundResource(R.drawable.splash_doctor);
		}*/

		tvSplashContinue = (TextView) findViewById(R.id.tvSplashContinue);
		tvSplashContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),MainActivityNew.class);
				startActivity(intent);
				finish();

			}
		});

		tvSplashCAllninonon = (TextView) findViewById(R.id.tvSplashCAllninonon);
		tvSplashCAllninonon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(callIntent);  
			}
		});
	}

}
