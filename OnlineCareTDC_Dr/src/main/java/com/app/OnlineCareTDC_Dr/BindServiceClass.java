//package com.app.onlinecaredr;
//
//import android.app.Application;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//import android.widget.Toast;
//
//import com.app.onlinecaredr.VideoCallService.LocalBinder;
//
//public class BindServiceClass extends Application {
//
//	VideoCallService mService;
//	
//	Context ctx;
//	
//	public BindServiceClass(Context ctx) {
//		
//		this.ctx = ctx;
//		
//	    Intent intent = new Intent(ctx, VideoCallService.class);
//	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//
//	}	
//
//	/** Defines callbacks for service binding, passed to bindService() */
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            LocalBinder binder = (LocalBinder) service;
//            mService = binder.getService();
//            
//            Toast.makeText(getApplicationContext(), "service bound", 0).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            
//            Toast.makeText(getApplicationContext(), "service disconnected", 0).show();
//        }
//    };
//
//}
