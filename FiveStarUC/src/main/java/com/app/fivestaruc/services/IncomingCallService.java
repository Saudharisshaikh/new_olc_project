//package com.app.onlinecare.services;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Notification;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.app.onlinecare.R;
//import com.app.onlinecare.util.DATA;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//import com.quickblox.videochat.model.utils.Debugger;
//
//public class IncomingCallService extends Service {
//    private VideoChatConfig videoChatConfig;
//    private CallState state;
//    QBUser a=DATA.currentUser;
//    /* private AlertDialog alertDialog;*/
//    private static final int notif_id=1;
//    private OnQBVideoChatListener qbVideoChatListener;
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        // TODO Auto-generated method stub
//
//        this.startForeground();
//        
//
//    }
//
//    private void startForeground() {
//            startForeground(notif_id, getMyActivityNotification(""));
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // TODO Auto-generated method stub
//        //startForeground(FOREGROUND_ID, notification);
//
//         Runnable runnable = new Runnable() {               
//
//              @Override
//              public void run() {
//
//
//                  try {
//                	  
//                      qbVideoChatListener = new OnQBVideoChatListener() {
//
//                          @Override
//                          public void onVideoChatStateChange(CallState state, VideoChatConfig receivedVideoChatConfig) {
//                              Debugger.logConnection("onVideoChatStateChange: " + state);
//                              videoChatConfig = receivedVideoChatConfig;
//                              
//                              DATA.print("-- online care callstate listenere");
//                              /*if (progressDialog != null && progressDialog.isShowing()) {
//                                  progressDialog.dismiss();
//                              }*/
//
//                              Log.i("service state",""+state.toString());
//                              switch (state) {
//                                  case ACCEPT:
////                                      Intent intent = new Intent(VChatListener.this, CallActivity.class);
////                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                      intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
////                                      startActivity(intent);
//                                  	
//                                  	Toast.makeText(getApplicationContext(), "incoming call", 0).show();
//                                  	
//                                     // showCallDialog();
//                                      Log.i("service state 1",""+state.toString());
//
//                                      break;
//                                  case ON_ACCEPT_BY_USER:
//                                      Log.i("service state 2",""+state.toString());
//
//                                     /* QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);*/
//
//                                      //startVideoChatActivity();
//                                      break;
//                                  case ON_REJECTED_BY_USER:
//                                      Log.i("service state 3",""+state.toString());
//                                         /*Toast.makeText(VChatListener.this, "Rejected by user", Toast.LENGTH_SHORT).show();*/
//                                      break;
//                                  case ON_DID_NOT_ANSWERED:
//                                      Log.i("service state 4",""+state.toString());
//
//                                     /* Toast.makeText(VChatListener.this, "User did not answer", Toast.LENGTH_SHORT).show();*/
//                                      break;
//                                  case ON_CANCELED_CALL:
//                                      videoChatConfig = null;
//                                      Log.i("service state 5","nothing");
//
//                                      /*if (alertDialog != null && alertDialog.isShowing()){
//                                          alertDialog.dismiss();
//                                      }
//                                      autoCancelHandler.removeCallbacks(autoCancelTask);*/
//                                      break;
//                              }
//                          }
//                      };
//
//	                    DATA.print("--online care Service Call user Here*****now");
//	                    if(DATA.currentUser!=null){
//		                    DATA.print("--online care  Service testing***"+DATA.currentUser);
//	                    }else{
//		                    DATA.print("--online care  Service testing user****nothing");
//	                    }
//	                    DATA.print("--online care Still****running****");
//	                      QBVideoChatController.getInstance().setQBVideoChatListener(DATA.currentUser, qbVideoChatListener);
//	                  } catch (NullPointerException ex) {
//	                      ex.printStackTrace();
//	                  } catch (XMPPException e) {
//	                      e.printStackTrace();
//	                  }
//	              };
//	         };
//	            new Thread(runnable).start();
//
//
//            return Service.START_REDELIVER_INTENT;
//    }
//
//
//    public void onDestroyed(){
//        super.onDestroy();
//    }
//
//    private Notification getMyActivityNotification(String text){
//        // The PendingIntent to launch our activity if the user selects
//        // this notification
//      /*  PendingIntent contentIntent = PendingIntent.getActivity(this,0, new Intent(this,Appointment.class), 0);*/
//
//        return new Notification.Builder(this)
//                .setContentTitle("Video Call")
//                .setContentText(text)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .getNotification();     
//}
//
//
//}