/*
package com.app.onlinecaredr.services;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import com.app.onlinecaredr.oovoosample.Main.MainActivity;
import com.app.onlinecaredr.util.AsyncFiveSeconds;
import com.app.onlinecaredr.util.AsyncReceiveMessage;
import com.app.onlinecaredr.util.DATA;

public class IncomingCallServiceNew extends Service {

	Timer t;
	public long DELAY = 1000;
	public long PERIOD = 5000;  // every .5 hours
	public static int count  = 0;
	SharedPreferences prefs;
	int incoming_call_status = 0;
	JSONObject jsonObject = null;
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

		ctx = IncomingCallServiceNew.this;
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
								
								AsyncFiveSeconds asyncReceiveMessage = new AsyncFiveSeconds(ctx, prefs.getString("id", ""));
								asyncReceiveMessage.execute("");

								DATA.print("--online care incoming call service running");
								
							}

						} catch (Exception e) {
						}	
					}
				});
			}
		};
		t.schedule(doAsynchronousTask, DELAY, PERIOD); //execute in every 50000 ms

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		DATA.print("--online care service destroyed");
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
	
	public void setResults(String response) {

		
		try {
			jsonObject = new JSONObject(response);
			
			 incoming_call_status = jsonObject.getInt("incoming_call_status");
			 
			 if(incoming_call_status == 1) {
				 DATA.print("--in incoming status 1");
				 if(jsonObject.has("first_name") && jsonObject.has("last_name")) {
					 DATA.incomingCallerName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
					
				 }
				 	
			     if(jsonObject.has("image")) {
			    	 DATA.incomingCallerImage = jsonObject.getString("image");
			     }
			     if(jsonObject.has("session_id")) {
			    	 DATA.incomingCallSessionId = jsonObject.getString("session_id");
			     }
			     if(jsonObject.has("call_id")) {
						SharedPreferences.Editor ed = prefs.edit();
						ed.putString("callingIDPatient", jsonObject.getString("call_id"));
						ed.commit();

			     }

					
					Intent i = new Intent(ctx, MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				 }

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
			
			
		}
		
	}

*/