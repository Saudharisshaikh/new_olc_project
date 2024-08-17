package com.app.Olc_MentalHealth.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.Olc_MentalHealth.services.LiveCareWaitingService1;
import com.app.Olc_MentalHealth.util.DATA;

public class BootCompleteBroadcast extends BroadcastReceiver {


	//note: this broadcast reciever shut down bt GM on 2Jan2019
	//Android 28 issues with manifest broadcasts and services
	//this reciever was used to start/stop waiting patients (livacarewaitingarea) monitoring service
	Context ctx ;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.ctx = context;

		DATA.print("-- broadcast recieved");
		
		if(intent.getAction().equals("com.app.onlinecare.START_SERVICE") || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

//			context.startService(new Intent(ctx,MessageReceiverService.class));			
//			context.startService(new Intent(ctx,VChatListener.class));

			//context.startService(new Intent(ctx,IncomingCallServiceNew.class));   blocked after push notif

		}
		else if(intent.getAction().equals("com.app.onlinecare.STOP_SERVICE")) {

			//context.stopService(new Intent(ctx,IncomingCallServiceNew.class));
//			context.stopService(new Intent(ctx,MessageReceiverService.class));
//			context.stopService(new Intent(ctx,VChatListener.class));

		}
		else if(intent.getAction().equals("LIVE_CARE_WAITING_TIMER")) {
			
			DATA.print("--online care live care waitnig service started");

			context.startService(new Intent(ctx,LiveCareWaitingService1.class));

		}
		else if(intent.getAction().equals("LIVE_CARE_WAITING_TIMER_STOP")) {

			context.stopService(new Intent(ctx,LiveCareWaitingService1.class));

		}
		else if(intent.getAction().equals("com.app.onlinecare.STOP_VCHAT_SERVICE")) {
			
			DATA.print("--online care stop vchat called");

//			context.stopService(new Intent(ctx,VChatListener.class));

		}


	}
	
}
