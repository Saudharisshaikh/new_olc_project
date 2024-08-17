package com.app.msu_uc.services;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.app.msu_uc.GetLiveCare;
import com.app.msu_uc.R;
import com.app.msu_uc.util.DATA;

public class LiveCareWaitingService extends Service {
	
	Timer t;
	Intent intent;
	SharedPreferences prefs;

	Context ctx;
	CountDownTimer cdt = null;
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
		
		DATA.print("--online care service onCreate");
		
		ctx = LiveCareWaitingService.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		
		cdt = new CountDownTimer(150000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
				DATA.print("countdown live care running: "+ (millisUntilFinished/1000));

				String time = String.format("%d min, %d sec", 
					    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
					    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - 
					    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
					);
				intent = new Intent();
				intent.setAction("LIVE_CARE_WAITING_TIMER");
				intent.putExtra("counter", time);
				sendBroadcast(intent);

			}
			
			@Override
			public void onFinish() {

				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("livecareTimerRunning", false);
				ed.commit();
				
				showNotification();
				
				intent = new Intent();
				intent.setAction("LIVE_CARE_WAITING_TIMER_STOP");
				
				sendBroadcast(intent);
	
			}
		};
		
		 cdt.start();
		}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		cdt.cancel();
	}
	
  private void showNotification(){
  // The PendingIntent to launch our activity if the user selects
  // this notification
	  
		mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

  PendingIntent contentIntent = PendingIntent.getActivity(this,0, new Intent(this, GetLiveCare.class), 0);

	mBuilder =new NotificationCompat.Builder(this);
	
	mBuilder.setContentTitle("Live Care")
          .setContentText("We are sorry!")
          .setSmallIcon(R.drawable.ic_launcher)
          .setSubText("One hour is passed and we could not contact.\nYou can apply for live care again.")
          .setContentIntent(contentIntent).
          setDefaults(Notification.DEFAULT_ALL);
  		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
}

}
