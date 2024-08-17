package com.onlinecare.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.covacard.util.AsyncReceiveMessage;
import com.covacard.util.CheckInternetConnection;
import com.covacard.util.DATA;

import java.util.Timer;
import java.util.TimerTask;

public class LiveCareWaitingService1 extends Service {
	
	Timer t;
	Intent intent;
	SharedPreferences prefs;

	Context ctx;
	CheckInternetConnection chkIntrnet;
 	NotificationCompat.Builder mBuilder;
	public static final int NOTIFICATION_ID = 1;
	public static NotificationManager mNotificationManager;


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		System.out.println("--online care service onCreate");
		
		ctx = LiveCareWaitingService1.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		t = new Timer();
		
		t.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				if(isConnectedToInternet() && DATA.shouldLiveCareWatingRefresh == 1) {
					DATA.shouldLiveCareWatingRefresh = 0;
					AsyncReceiveMessage as = new AsyncReceiveMessage(ctx, prefs.getString("getLiveCareApptID", ""));
					as.execute("");
					
					System.out.println("--online care livecareappointment id: "+prefs.getString("getLiveCareApptID", ""));
				}

				
			}
		}, 1000,6000);
		
		}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		t.cancel();
	}
	
  /*private void showNotification(){
  // The PendingIntent to launch our activity if the user selects
  // this notification
	  
		mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

  PendingIntent contentIntent = PendingIntent.getActivity(this,0, new Intent(this,GetLiveCare.class), 0);

	mBuilder =new NotificationCompat.Builder(this);
	
	mBuilder.setContentTitle("Live Care")
          .setContentText("We are sorry!")
          .setSmallIcon(R.drawable.ic_launcher)
          .setSubText("One hour is passed and we could not contact.\nYou can apply for live care again.")
          .setContentIntent(contentIntent).
          setDefaults(Notification.DEFAULT_ALL);
  		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
}*/

  public boolean isConnectedToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}
  
  public void setResults(String yourNo, String totalPatients, String status) {
	  
	  if(status.equals("1") || status.equals("")) {
		  
		  SharedPreferences.Editor ed = prefs.edit();
		  ed.putBoolean("livecareTimerRunning", false);
		  ed.commit();

		  Intent i = new Intent();
		  i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
		  sendBroadcast(i);
		  
		  stopSelf();
		  
		  
	  }
	  else {
		  Intent i = new Intent();
		  i.setAction("LIVE_CARE_WAITING_PATIENTS");
		  i.putExtra("patients", totalPatients);
		  i.putExtra("yourNo", yourNo);
		  sendBroadcast(i);
	  }
	  
	 
  }
}
