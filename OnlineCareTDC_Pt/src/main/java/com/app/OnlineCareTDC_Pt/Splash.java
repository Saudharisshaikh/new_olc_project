package com.app.OnlineCareTDC_Pt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareTDC_Pt.util.CustomToast;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.GloabalMethods;

public class Splash extends Activity {
	
	TextView tvSplashContinue,tvSplashCAllninonon;
	SharedPreferences prefs;
	CheckBox privacycb;
	CustomToast customToast;
	
	@Override
	protected void onStart() {
		super.onStart();

	}
	@Override
	protected void onResume() {
		super.onResume();
		
		/*if((!isMyServiceRunning(IncomingCallServiceNew.class))) {
			
			DATA.print("--online care service started from splash check...");

			Intent intent1 = new Intent();
			intent1.setAction("com.app.onlinecare.START_SERVICE");
			sendBroadcast(intent1);

		}*/

		/*DATA.print("-- livecareTimerRunning: "+prefs.getBoolean("livecareTimerRunning", false));
		if(!(isMyServiceRunning(LiveCareWaitingService1.class)) && prefs.getBoolean("livecareTimerRunning", false)) {
			DATA.print("--online care liveCareWaitingService1 started from splash check...");
			Intent intent1 = new Intent();
			intent1.setAction("LIVE_CARE_WAITING_TIMER");
			sendBroadcast(intent1);
		}*/

//	loginToQB();

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		//new Login().checkforUpdates();
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(Splash.this);


		tvSplashContinue = (TextView) findViewById(R.id.tvSplashContinue);
		tvSplashCAllninonon = (TextView) findViewById(R.id.tvSplashCAllninonon);

		privacycb = findViewById(R.id.privacycb);

		boolean checkedvalue = prefs.getBoolean("checkedPrivacyPolicy" , false);


		if (checkedvalue == true)
		{
			privacycb.setChecked(true);
		}


		privacycb.setOnCheckedChangeListener((compoundButton, b) ->
		{
			if (compoundButton.isChecked())
			{
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("checkedPrivacyPolicy", true);
				ed.commit();
			}
		});


		tvSplashContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (!privacycb.isChecked())
				{
					customToast.showToast("Please check privacy policy and terms of service." , 0,1);
				}
				else if (privacycb.isChecked())
				{
					Intent intent = new Intent(getApplicationContext(),MainActivityNew.class);
					intent.putExtra("show_med_history_popup", true);
					startActivity(intent);
					finish();
				}

//				Intent intent = new Intent(getApplicationContext(),ActivityGyantChat.class);
//				startActivity(intent);
//				finish();

//				startActivity(new Intent(Splash.this, ActivityGyantChat.class));
	
			}
		});
		
		tvSplashCAllninonon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
					callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(callIntent);
				}catch (Exception  e){e.printStackTrace();}
			}
		});

		findViewById(R.id.tvPolicy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(Splash.this).showWebviewDialog(DATA.PRIVACY_POLICY_URL,"Privacy Policy");
			}
		});
		findViewById(R.id.tvAgreement).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(Splash.this).showWebviewDialog(DATA.USER_AGREEMENT_URL,"Terms of service.");
			}
		});
	}

	/*private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            DATA.print("--service already running");
	        	return true;
	        }
	    }
        DATA.print("--service not running");
	    return false;
	}*/
}
