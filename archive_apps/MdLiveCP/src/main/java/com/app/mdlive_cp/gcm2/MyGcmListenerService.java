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

/*package com.app.mdlive_cp.gcm2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.app.mdlive_cp.ActivityRifills;
import com.app.mdlive_cp.ConversationsActivity;
import com.app.mdlive_cp.DoctorsList;
import com.app.mdlive_cp.DrsAppointments;
import com.app.mdlive_cp.Login;
import com.app.mdlive_cp.MainActivityNew;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.ShowGroupMessagePopup;
import com.app.mdlive_cp.ShowLiveCarePopup;
import com.app.mdlive_cp.ShowMessagePopupActivity;
import com.app.mdlive_cp.SupportMessagesActivity;
import com.app.mdlive_cp.VCallModule;
import com.app.mdlive_cp.ViewConversationActivity;
import com.app.mdlive_cp.model.ConversationBean;
import com.app.mdlive_cp.model.SupportMessageBean;
import com.app.mdlive_cp.services.IncomingCallResponse;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DialogPatientInfo;
import com.app.mdlive_cp.util.EasyAES;
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
    private void generateAction(String response,Context context) {
    	System.out.println("-- GCM msg "+response);
        // notifies user


		try {
			System.out.println("--json" + response);
			jsonObject = new JSONObject(response);

			String current_time = jsonObject.getString("current_time");
			String future_time = jsonObject.getString("future_time");

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message_delete")) {
				String message_id = jsonObject.getJSONObject("data").getString("message_id");
				//you also can remove messagebean from local array on behalf of this ID
				//at this time I m reloading service
				Intent intent = new Intent();
				intent.setAction(ViewConversationActivity.REFRESH_CHAT_ACTION_MSG_DEL);
				context.sendBroadcast(intent);

				return;
			}

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

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment")) {

				sendNotification(jsonObject.getJSONObject("data").getString("message"), MainActivityNew.class);
				return;
			}
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment_cancel")) {

				sendNotification("Patient has cancelled the appointment", MainActivityNew.class);
				return;
			}
			if (jsonObject.has("response_type") && (jsonObject.getString("response_type").equalsIgnoreCase("coordinator_message")
			|| jsonObject.getString("response_type").equalsIgnoreCase("ems_message"))) {

				String messageSupport = jsonObject.getJSONObject("data").getString("message");
				String from = jsonObject.getJSONObject("data").getString("from");
				String image = jsonObject.getJSONObject("data").getString("image");

				SupportMessageBean bean = new SupportMessageBean();
				bean.message = messageSupport;
				bean.image = image;
				bean.name = from;
				bean.type = "coordinator";
				if (SupportMessagesActivity.supportMsgList != null) {
					SupportMessagesActivity.supportMsgList.add(bean);
				}

				Intent intent = new Intent();
				intent.setAction(SupportMessagesActivity.COORDINATOR_MESSAGE);
				*//*intent.putExtra("message", messageSupport);
				intent.putExtra("from", from);
				intent.putExtra("image", image);*//*
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

			//DATA.msgTimeForPopup = current_time;
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("refill")) {
				DATA.refillIdInGCM = jsonObject.getJSONObject("data").getString("refill_id");
				sendNotification("You have new refill requests.", ActivityRifills.class);
				return;
			}
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_MESSAGE);

				DATA.msgTextForPopup = jsonObject.getJSONObject("data").getString("message");
				DATA.msgPatientNameForPopup = jsonObject.getJSONObject("data").getString("from");
				DATA.msgPatientImageForPopup = jsonObject.getJSONObject("data").getString("image");
				String to_id = "to_id";

				DATA.msgTextForPopup = EasyAES.decryptString(DATA.msgTextForPopup);
				
				if (jsonObject.getJSONObject("data").has("to_id")) {
					to_id = jsonObject.getJSONObject("data").getString("to_id"); 
				}
				DATA.selecetedBeanFromConversation = new ConversationBean(
						"",
						jsonObject.getJSONObject("data").getString("doctor_id"),
						jsonObject.getJSONObject("data").getString("patient_id"),
						DATA.msgTextForPopup, 
						jsonObject.getJSONObject("data").getString("send_from"),
						jsonObject.getJSONObject("data").getString("date"),
						DATA.msgPatientNameForPopup, "", 
						DATA.msgPatientImageForPopup,
						"to",
						jsonObject.getJSONObject("data").getString("patient_id"),//"specialityId"
						jsonObject.getJSONObject("data").getString("send_from"),
						to_id);
				DATA.msgTimeForPopup = jsonObject.getJSONObject("data").getString("date");
				if(DATA.shouldNotify){
				//generateMsgNotification(context, "New message from: "+DATA.msgPatientNameForPopup);
				sendNotification("New message from: "+DATA.msgPatientNameForPopup, ConversationsActivity.class);
				}else {
					Intent i =new Intent();
					i.setAction(ViewConversationActivity.REFRESH_CHAT_ACTION_NEW_MSG);
					context.sendBroadcast(i);	
				}
				
				Intent i =new Intent(context,ShowMessagePopupActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
			
			JSONObject data = jsonObject.getJSONObject("data");
			if (data.has("status") && data.getString("status").equalsIgnoreCase("logout")) {
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
			}
			if (data.has("status") && data.getString("status").equalsIgnoreCase("disconnected")) {
			    Intent intent = new Intent();
			    intent.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
			    context.sendBroadcast(intent);
			    return;
			   }
			
			if (data.has("status") && data.getString("status").equalsIgnoreCase("connected")) {
			    Intent intent = new Intent();
			    intent.setAction(VCallModule.INCOMMING_CALL_CONNECTED);
			    context.sendBroadcast(intent);
			    return;
			   }
			
			if (data.has("status") && data.getString("status").equalsIgnoreCase("disconnect_spe")) {
			    Intent intent = new Intent();
			    intent.setAction(VCallModule.DISCONNECT_SPECIALIST);
			    context.sendBroadcast(intent);
			    return;
			   }
			
			if (data.has("status") && data.getString("status").equalsIgnoreCase("newpatient")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_PATIENT);
				//generateNotificationForQueue(context, data.getString("message"));
				sendNotification(data.getString("message"), DoctorsList.class);
				Intent i =new Intent(context,ShowLiveCarePopup.class);
				i.putExtra("status", "newpatient");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			    return;
			   }

			if (data.has("status") && data.getString("status").equalsIgnoreCase("prescription_approved")) {
				sendNotification(data.getString("message"), MainActivityNew.class);
				return;
			}

			if (data.has("status") && data.getString("status").equalsIgnoreCase("approved_dme")) {
				sendNotification(data.getString("message"), MainActivityNew.class);
				return;
			}

			if (data.has("status") && data.getString("status").equalsIgnoreCase("newappointment")) {

				//generateNotificationForApptmnt(context, "You have new appointment request");
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);

				sendNotification("You have new appointment request", DrsAppointments.class);
				Intent i =new Intent(context,ShowLiveCarePopup.class);
				i.putExtra("status", "newappointment");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);

				return;
			}
			
			String response_type = jsonObject.getString("response_type");
			if (response_type.equalsIgnoreCase("outgoing")) {
				// this is outgoing responce from patient
				String status = jsonObject.getJSONObject("data").getString(
						"status");

				if (status.equals("accept")) {
					//DATA.incomingCall = false;
					DATA.isCallRejected = false;
					Intent intent1 = new Intent();
					intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
					sendBroadcast(intent1);

				} else if (status.equals("reject")) {

					DATA.isCallRejected = true;
					//DATA.incomingCall = false;

					Intent intent1 = new Intent();
					intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
					sendBroadcast(intent1);

				} else if (status.equals("noanswer")) {
					DATA.isCallRejected = true;
					//DATA.incomingCall = false;

					Intent intent1 = new Intent();
					intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
					sendBroadcast(intent1);
				}
			} else {
				jsonObject = jsonObject.getJSONObject("data");
				if (jsonObject.has("incoming_call_status")) {
					incoming_call_status = jsonObject
							.getInt("incoming_call_status");
					System.out.println("--incoming_call_status"
							+ jsonObject.getInt("incoming_call_status"));
				}*//*
				 * else{ incoming_call_status = 0; }
				 *//*
				if (incoming_call_status == 1) {
					System.out.println("--in incoming status 1");
					if (jsonObject.has("first_name")
							&& jsonObject.has("last_name")) {// this should be
																// added in
																// service
						DATA.incomingCallerName = jsonObject
								.getString("first_name")
								+ " "
								+ jsonObject.getString("last_name");

					}

					if (jsonObject.has("picture")) {
						DATA.incomingCallerImage = jsonObject
								.getString("picture");
					}
					if (jsonObject.has("session_id")) {
						DATA.incomingCallSessionId = jsonObject
								.getString("session_id");
					}
					if (jsonObject.has("call_id")) {
						*//*
						 * SharedPreferences.Editor ed = prefs.edit();
						 * ed.putString("callingIDPatient",
						 * jsonObject.getString("call_id")); ed.commit();
						 *//*
						DATA.incommingCallId = jsonObject.getString("call_id");

					}

					if (jsonObject.has("from_id")) {
						DATA.incomingCallUserId = jsonObject.getString("from_id");
					}

					DialogPatientInfo.patientIdGCM = "";
					if(jsonObject.has("selected_patient_id")){
						DialogPatientInfo.patientIdGCM = jsonObject.getString("selected_patient_id");
					}
					
					
					String pattern = "yyyy-MM-dd HH:mm:ss";
				     SimpleDateFormat format = new SimpleDateFormat(pattern);
				      Date callTime = format.parse(future_time);//"2016-01-28 19:44:31"
				      int compare = callTime.compareTo(new Date());
				      System.out.println("--compare dates "+compare);
				      
				      if (compare == 1) {

						  if(MultiPartyVideoCallFragment.isInVideoCall){//|| MainActivity.isOnVideoCallActivity
							  DATA.incomingCallResponce = "reject";
							  IncomingCallResponse incomingCallResponse = new IncomingCallResponse(context, DATA.incomingCallResponce);
							  incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
							  return;
						  }

				    	  DATA.incomingCall = true;
				    	  DATA.isFromDocToDoc = true;
							Intent i = new Intent(context,MainActivity.class);//SampleActivity.class
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
				      } else {
				      // generateNotification(context, "You have missed a live care call from "+DATA.incomingCallerName);
				       sendNotification("You have missed a live care call from "+DATA.incomingCallerName, MainActivityNew.class);
				      }
					
					
					
					
				}
			}

		} catch (JSONException e) {
			System.out.println("--in catch");
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
      
	}
    
    private void sendNotification(String msg,final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(this, activityToOpen);
		intent.putExtra("isFromMyDoctors", true);
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
