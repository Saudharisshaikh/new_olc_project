/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*package com.app.mhcsn_uc.gcm2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.app.mhcsn_uc.ConversationsActivity;
import com.app.mhcsn_uc.GetLiveCare;
import com.app.mhcsn_uc.Login;
import com.app.mhcsn_uc.MainActivityNew;
import com.app.mhcsn_uc.MyAppointments;
import com.app.mhcsn_uc.R;
import com.app.mhcsn_uc.ShowGroupMessagePopup;
import com.app.mhcsn_uc.ShowMessagePopupActivity;
import com.app.mhcsn_uc.SupportMessagesActivity;
import com.app.mhcsn_uc.VCallModule;
import com.app.mhcsn_uc.ViewConversationActivity;
import com.app.mhcsn_uc.model.ConversationBean;
import com.app.mhcsn_uc.model.SupportMessageBean;
import com.app.mhcsn_uc.util.DATA;
import com.app.mhcsn_uc.util.EasyAES;
import com.app.mhcsn_uc.util.IncomingCallResponse;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    *//**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     *//*
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

		DATA.print("-- all bundle: "+data.toString());

        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        *//**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         *//*

        *//**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         *//*
        //sendNotification(message);
        generateAction(message, this);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    *//**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     *//*
    int incoming_call_status = 0;
	JSONObject jsonObject = null;
    private void generateAction(String response, final Context context) {
    	DATA.print("-- GCM msg "+response);
        // notifies user


		
		try {
			jsonObject = new JSONObject(response);
			
			String current_time = jsonObject.getString("current_time");
			String future_time = jsonObject.getString("future_time");

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("group_add_user")) {
				sendNotification(jsonObject.getJSONObject("data").getString("message"),MainActivityNew.class);
				return;
			}

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("group_message")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_GROUP_MESSAGE);

				JSONObject data = jsonObject.getJSONObject("data");
				sendNotification("New group message from: "+data.getString("from"),MainActivityNew.class);

				ShowGroupMessagePopup.messageFrom = data.getString("from");
				ShowGroupMessagePopup.messageText = data.getString("message");
				ShowGroupMessagePopup.messageTime = data.getString("date");
				ShowGroupMessagePopup.messageImageURL = data.getString("image");
				Intent intent = new Intent(context,ShowGroupMessagePopup.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return;
			}

			//DATA.msgTimeForPopup = current_time;
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_MESSAGE);

				//DATA.msgFromDoctor = jsonObject.getJSONObject("data").getString("message");
				//DATA.msgDoctorName = jsonObject.getJSONObject("data").getString("from");
				DATA.msgTextForPopup = jsonObject.getJSONObject("data").getString("message");
				DATA.msgPatientNameForPopup = jsonObject.getJSONObject("data").getString("from");
				DATA.msgPatientImageForPopup = jsonObject.getJSONObject("data").getString("image");

				DATA.msgTextForPopup = EasyAES.decryptString(DATA.msgTextForPopup);

				String to_id = "to_id";
				if (jsonObject.getJSONObject("data").has("to_id")) {
					to_id = jsonObject.getJSONObject("data").getString("to_id");
				}
				
				DATA.selecetedBeanFromConversation = new ConversationBean("", 
						jsonObject.getJSONObject("data").getString("doctor_id"),
						jsonObject.getJSONObject("data").getString("patient_id"),
						DATA.msgTextForPopup, 
						jsonObject.getJSONObject("data").getString("send_from"),
						jsonObject.getJSONObject("data").getString("date"),
						DATA.msgPatientNameForPopup, "", 
						DATA.msgPatientImageForPopup,
						"to",//"to"
						jsonObject.getJSONObject("data").getString("doctor_id"),//"specialist_id"
						jsonObject.getJSONObject("data").getString("send_from"),
						to_id);//"user_type"
				//this value is for view conv from popup
				DATA.msgTimeForPopup = jsonObject.getJSONObject("data").getString("date");
				Intent i =new Intent(context,ShowMessagePopupActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				//generateMsgNotification(context, "New message from: "+jsonObject.getJSONObject("data").getString("from"));
				sendNotification("New message from: "+jsonObject.getJSONObject("data").getString("from"), ConversationsActivity.class);
			return;
			}
			
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);//apptmnt confirmed
				//generateApptmntNotification(context, jsonObject.getJSONObject("data").getString("message"));
				sendNotification(jsonObject.getJSONObject("data").getString("message") , MyAppointments.class);
			return;	
			}
			
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment_refered")) {
				//generateApptmntNotification(context, jsonObject.getJSONObject("data").getString("message"));
				sendNotification(jsonObject.getJSONObject("data").getString("message") , MyAppointments.class);
				//alert(context, "Appointment Reffered", jsonObject.getJSONObject("data").getString("message"));
			return;	
			}
			
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("referred")) {
				//generateNotification(context, jsonObject.getJSONObject("data").getString("message"));
				sendNotification(jsonObject.getJSONObject("data").getString("message"), MainActivityNew.class);
			return;	
			}
			
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("coordinator_message")) {
				
				String message = jsonObject.getJSONObject("data").getString("message");
				String from = jsonObject.getJSONObject("data").getString("from");
				String image = jsonObject.getJSONObject("data").getString("image");

				SupportMessageBean bean = new SupportMessageBean();
				bean.message = message;
				bean.image = image;
				bean.name = from;
				bean.type = "coordinator";
				if (SupportMessagesActivity.supportMsgList != null) {
					SupportMessagesActivity.supportMsgList.add(bean);
				}

				Intent intent = new Intent();
				intent.setAction("coordinator_message");
				intent.putExtra("message", message);
				intent.putExtra("from", from);
				intent.putExtra("image", image);
				context.sendBroadcast(intent);

				sendNotification("New message from support", SupportMessagesActivity.class);
				return;
			}
			
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("coordinator")) {
				
				String status = jsonObject.getJSONObject("data").getString("status");
				if (status.equalsIgnoreCase("accept")) {
					Intent intent = new Intent();
					intent.setAction(VCallModule.COORDINATOR_CALL_ACCEPTED);
					context.sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.setAction(VCallModule.COORDINATOR_CALL_REJECTED);
					context.sendBroadcast(intent);
				}
			return;	
			}
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message_delete")) {
				String message_id = jsonObject.getJSONObject("data").getString("message_id");
				//you also can remove messagebean from local array on behalf of this ID
				//at this time I m reloading service
				Intent intent = new Intent();
				intent.setAction(ViewConversationActivity.REFRESH_CHAT_ACTION_MSG_DEL);
				context.sendBroadcast(intent);

				return;
			}
			
			jsonObject = jsonObject.getJSONObject("data");
			if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("disconnected")) {
				Intent intent = new Intent();
				intent.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
				context.sendBroadcast(intent);
			}else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("connected")) {
				Intent intent = new Intent();
				intent.setAction(VCallModule.COORDINATOR_CALL_CONNECTED);
				context.sendBroadcast(intent);
				//Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
			}else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("disconnect_spe")) {
				Intent intent = new Intent();
				intent.setAction(VCallModule.DISCONNECT_SPECIALIST);
				context.sendBroadcast(intent);
			}else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("assigned_doctor")) {
				sendNotification("Your eLiveCare has been assigned to the Doctor. Please tap to check your eLiveCare status.", GetLiveCare.class);

				final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
				setMessage("Your eLiveCare has been assigned to the Doctor. Please tap to check your eLiveCare status.").
					setPositiveButton("View Details", null);

				try {
					new Thread() {
						@Override
						public void run() {
							Looper.prepare();
							final AlertDialog alertDialog = builder.create();
							alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialog) {
									Intent intent = new Intent(context,GetLiveCare.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent);
								}
							});
							alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
							alertDialog.show();
							Looper.loop();

						}
					}.start();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("logout")) {
				SharedPreferences prefs = getApplicationContext().getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
				SharedPreferences.Editor ed = prefs.edit();
				*//*ed.putBoolean("HaveShownPrefs", false);
				ed.putBoolean("subUserSelected", false);
				ed.commit();*//*
				ed.clear();
				ed.apply();

				Intent intent = new Intent(getApplicationContext(), Login.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

				return;
			}else if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("newappointment")) {
				String msg = jsonObject.getString("message");
				sendNotification(msg, MyAppointments.class);
				return;
			}else
			 incoming_call_status = jsonObject.getInt("incoming_call_status");
			 
			 if(incoming_call_status == 1) {
				 DATA.print("--in incoming status 1");
				 if(jsonObject.has("first_name") && jsonObject.has("last_name")) {
					 DATA.incomingCallerName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
					
				 }
				 	
			     if(jsonObject.has("picture")) {
			    	 DATA.incomingCallerImage = jsonObject.getString("picture");
			     }
			     if(jsonObject.has("session_id")) {
			    	 DATA.incomingCallSessionId = jsonObject.getString("session_id");
			     }
			     if(jsonObject.has("id")) {
			    	 DATA.incomingCallUserId = jsonObject.getString("id");
			     }
			     if(jsonObject.has("increment_id")) {
						*//*SharedPreferences.Editor ed = prefs.edit();
						ed.putString("callingIDPatient", jsonObject.getString("increment_id"));
						ed.commit();*//*
			    	 DATA.incommingCallId = jsonObject.getString("increment_id");
			     }

					
			        String pattern = "yyyy-MM-dd HH:mm:ss";
					SimpleDateFormat format = new SimpleDateFormat(pattern);
						Date callTime = format.parse(future_time);//"2016-01-28 19:44:31"
						int compare = callTime.compareTo(new Date());
						DATA.print("--compare dates "+compare);
						
						if (compare == 1) {

							if(MultiPartyVideoCallFragment.isInVideoCall){
								IncomingCallResponse incomingCallResponse = new IncomingCallResponse(context, "reject");
								incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
								return;
							}

							Intent i = new Intent(context, MainActivity.class);//SampleActivity.class //changed context here from GCMintentservice.this back activity was closed due to that
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							context.startActivity(i);//added context.
						} else {
							//generateNotification(context, "You have missed a live care call from "+DATA.incomingCallerName);
							sendNotification("You have missed a live care call from "+DATA.incomingCallerName, MainActivityNew.class);
						}
				
					
				 }

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
    
    private void sendNotification(String msg,final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(this, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
				.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
    }
}*/
