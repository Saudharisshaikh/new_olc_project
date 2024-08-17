package com.app.mhcsn_spe.util;

import android.app.Activity;
import android.content.Intent;

public class OpenActivity {
	
	Activity activity;
	
	public OpenActivity(Activity currentActivity) {
		
		this.activity = currentActivity;
	}
	
	public void open(final Class<? extends Activity> activityToOpen, boolean shouldCallFinish) {
		
		
		
		if(shouldCallFinish) {
			
			activity.startActivity(new Intent(activity, activityToOpen));
			activity.finish();
			
		}
		else {
			
			activity.startActivity(new Intent(activity, activityToOpen));			
		}
	}
}


