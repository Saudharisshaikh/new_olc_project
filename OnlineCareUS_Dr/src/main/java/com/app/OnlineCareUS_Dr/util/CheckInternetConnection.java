package com.app.OnlineCareUS_Dr.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnection {
	
	Activity a;
	
	public CheckInternetConnection(Activity a) {
		
		this.a = a;
	}
	
	public boolean isConnectedToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
