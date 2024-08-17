
package com.app.amnm_ma.services;

import java.util.Timer;
import java.util.TimerTask;

import com.app.amnm_ma.VCallModule;
import com.app.amnm_ma.util.AsyncReceiveMessage;
import com.app.amnm_ma.util.DATA;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

public class OutgoingCallService extends Service {

	Timer t;
	public long DELAY = 1000;
	public long PERIOD = 3000;  // every .5 hours
	
	
//	public long DELAY = 1000;
//	public long PERIOD = 2000;  
	//public static int count  = 0;
	SharedPreferences prefs;

	Context ctx;
	Handler handler;
	TimerTask doAsynchronousTask;

	@Override
	public IBinder onBind(Intent arg0) {
		throw new UnsupportedOperationException("not yet completed...");
	}

	@Override
	public void onCreate() {
		super.onCreate();	

		ctx = OutgoingCallService.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		

		handler = new Handler();
		t = new Timer();

		doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {

					public void run() {

						try {

							if(isNetworkConnected()) {
								
								AsyncReceiveMessage asyncReceiveMessage = new AsyncReceiveMessage(ctx, prefs.getString("callingID", ""));
								asyncReceiveMessage.execute("");

								System.out.println("--online care outgoing call service running");
								
							}

						} catch (Exception e) {
							System.out.println("--exception in outgoingCallService");
							e.printStackTrace();
						}	
					}
				});
			}
		};
		t.schedule(doAsynchronousTask, DELAY, PERIOD); //execute in every 50000 ms

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return 0;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		System.out.println("--online care dr outgoing call service destroyed");
		t.cancel();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}
	
	
	public void callrej() {
		DATA.isCallRejected = true;
		
		
//		Intent i = new Intent(OutgoingCallService.this , MainActivity.class);
//		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(i);
		//stopSelf();
		stopService(new Intent(OutgoingCallService.this , OutgoingCallService.class));
		
		Intent intent1 = new Intent();
		intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
		sendBroadcast(intent1);
		 
	}
	
	public void setResults() {//String response
		
//		DATA.isCallComming = true;
//		Intent i = new Intent(OutgoingCallService.this , MainActivity.class);
//		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(i);
		//stopSelf();
		stopService(new Intent(OutgoingCallService.this , OutgoingCallService.class));
		Intent intent1 = new Intent();
		intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
		sendBroadcast(intent1);
		
		

		/*
		try {
			JSONObject jsonObject = new JSONObject(response);
			
			if(jsonObject.has("respond_id")) {
				
				DATA.outgoingCallResponse = jsonObject.getString("respond_id");
				
				if(! DATA.outgoingCallResponse.equals("0")) {

					Intent i = new Intent();
					i.putExtra("callResponse", jsonObject.getString("respond_id"));
					i.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
					sendBroadcast(i);
					
//					DATA.qwqwqwqwqw = true;
//					Intent i = new Intent(OutgoingCallService.this, MainActivity.class);
//					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(i);
				
					
				
//					stopSelf();

				}
				

				
				
				

			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		
	}//end setResults
}
