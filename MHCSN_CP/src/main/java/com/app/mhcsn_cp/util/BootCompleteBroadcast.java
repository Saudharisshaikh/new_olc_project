
package com.app.mhcsn_cp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 

public class BootCompleteBroadcast extends BroadcastReceiver {


	Context ctx ;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.ctx = context;

		
		if(intent.getAction().equals("com.app.onlinecarespecialist.START_SERVICE") || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			DATA.print("--bootbrodcast");
			//context.startService(new Intent(ctx,IncomingCallServiceNew.class));//after gcm

		}
		else if(intent.getAction().equals("com.app.onlinecarespecialist.STOP_SERVICE")) {
			
			//context.stopService(new Intent(ctx, IncomingCallServiceNew.class));//after gcm

//			context.stopService(new Intent(ctx,MessageReceiverService.class));
//			context.stopService(new Intent(ctx,VChatListener.class));

		}
		
		

	}
	
}

