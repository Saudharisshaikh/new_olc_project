
package com.app.OnlineCareUS_MA.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import com.app.onlinecare.services.LiveCareWaitingService1;

public class BootCompleteBroadcast extends BroadcastReceiver {


	Context ctx ;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.ctx = context;

		
		if(intent.getAction().equals("com.app.onlinecaredr.START_SERVICE") || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

//			context.startService(new Intent(ctx,MessageReceiverService.class));			
//			context.startService(new Intent(ctx,VChatListener.class));

//			DATA.print("--bootbrodcast");
//			context.startService(new Intent(ctx,IncomingCallServiceNew.class));

		}
		else if(intent.getAction().equals("com.app.onlinecaredr.STOP_SERVICE")) {

//			context.stopService(new Intent(ctx,MessageReceiverService.class));
//			context.stopService(new Intent(ctx,VChatListener.class));

		}
		
		
//		else if(intent.getAction().equals("LIVE_CARE_WAITING_TIMER")) {
//			
//			DATA.print("--online care live care waitnig service started");
//
//			//context.startService(new Intent(ctx,LiveCareWaitingService1.class));
//
//		}
//		else if(intent.getAction().equals("LIVE_CARE_WAITING_TIMER_STOP")) {
//
//			//context.stopService(new Intent(ctx,LiveCareWaitingService1.class));
//
//		}
//		else if(intent.getAction().equals("com.app.onlinecaredr.STOP_VCHAT_SERVICE")) {
//			
//			DATA.print("--online care stop vchat called");
//
////			context.stopService(new Intent(ctx,VChatListener.class));
//
//		}


	}
	
}






//package com.app.onlinecaredr.broadcasts;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.app.onlinecaredr.services.MessageReceiverService;
//import com.app.onlinecaredr.services.VChatListener;
//
//public class BootCompleteBroadcast extends BroadcastReceiver {
//
//
//	Context ctx ;
//
//	@Override
//	public void onReceive(Context context, Intent intent)
//	{
//		this.ctx = context;
//
//		
//		if(intent.getAction().equals("com.app.onlinecaredr.START_SERVICE") || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//			
//			DATA.print("--online care dr broadcast called startservice");
//
//			context.startService(new Intent(ctx,MessageReceiverService.class));
//			context.startService(new Intent(ctx,VChatListener.class));
//
////			if(!prefs.getBoolean("qbcalledonce", false)) {
////
////				loginToQB();
////
////			}
//
//		}
//		else if(intent.getAction().equals("com.app.onlinecaredr.STOP_SERVICE")) {
//
//			context.stopService(new Intent(ctx,MessageReceiverService.class));
//			context.stopService(new Intent(ctx,VChatListener.class));
//
//		}
////		else if(intent.getAction().equals("com.app.onlinecaredr.START_VCHAT_SERVICE")) {
//
//		//	context.stopService(new Intent(ctx,VChatListener.class));
//
//	//	}
//
//
//	}
//	
//}
