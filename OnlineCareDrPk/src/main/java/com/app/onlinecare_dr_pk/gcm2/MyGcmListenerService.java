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

/*package com.app.onlinecare_dr_pk.gcm2;

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

import com.app.onlinecare_dr_pk.ActivityAddDoctor;
import com.app.onlinecare_dr_pk.ActivityRifills;
import com.app.onlinecare_dr_pk.ActivityTelemedicineServices;
import com.app.onlinecare_dr_pk.ConversationsActivity;
import com.app.onlinecare_dr_pk.DrsAppointments;
import com.app.onlinecare_dr_pk.LiveCare;
import com.app.onlinecare_dr_pk.Login;
import com.app.onlinecare_dr_pk.MainActivityNew;
import com.app.onlinecare_dr_pk.PresscriptionsActivity;
import com.app.onlinecare_dr_pk.R;
import com.app.onlinecare_dr_pk.ShowGroupMessagePopup;
import com.app.onlinecare_dr_pk.ShowLiveCarePopup;
import com.app.onlinecare_dr_pk.ShowMessagePopupActivity;
import com.app.onlinecare_dr_pk.SupportMessagesActivity;
import com.app.onlinecare_dr_pk.VCallModule;
import com.app.onlinecare_dr_pk.ViewConversationActivity;
import com.app.onlinecare_dr_pk.model.ConversationBean;
import com.app.onlinecare_dr_pk.model.SupportMessageBean;
import com.app.onlinecare_dr_pk.services.IncomingCallResponse;
import com.app.onlinecare_dr_pk.util.DATA;
import com.app.onlinecare_dr_pk.util.DialogPatientInfo;
import com.app.onlinecare_dr_pk.util.EasyAES;
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
    private void generateAction(String message,Context context) {
    	DATA.print("-- GCM msg "+message);
        // notifies user
       
      //  setResults(message);
        
//{"current_time":"2016-01-05 22:28:38","future_time":"2016-01-05 22:30:38","data":{"status":"accept"}} 
        try {
			JSONObject jsonObject = new JSONObject(message);
			String current_time = jsonObject.getString("current_time");
			String future_time = jsonObject.getString("future_time");

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("doctor_prescription")) {
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_DOCTOR_PRESCRIPTION);
				sendNotification(jsonObject.getJSONObject("data").getString("message"),PresscriptionsActivity.class);
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

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("refill_request_by_patient")) {

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_REFILL);

				sendNotification(jsonObject.getJSONObject("data").getString("message"),MainActivityNew.class);
				//DATA.showAlert(context,"Refill request",
				//		jsonObject.getJSONObject("data").getString("message"),MainActivityNew.class);
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
				intent.setAction("coordinator_message");
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

			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message_delete")) {
				String message_id = jsonObject.getJSONObject("data").getString("message_id");
				//you also can remove messagebean from local array on behalf of this ID
				//at this time I m reloading service
				Intent intent = new Intent();
				intent.setAction(ViewConversationActivity.REFRESH_CHAT_ACTION_MSG_DEL);
				context.sendBroadcast(intent);

				return;
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
			if (data.has("status") && data.getString("status").equalsIgnoreCase("disconnect_spe")) {
				Intent intent = new Intent();
				intent.setAction(VCallModule.DISCONNECT_SPECIALIST);
				context.sendBroadcast(intent);
				return;
			}
			//DATA.msgTimeForPopup = current_time;
			if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("message")) {
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_MESSAGE);

				DATA.msgTextForPopup = jsonObject.getJSONObject("data").getString("message");
				DATA.msgPatientNameForPopup = jsonObject.getJSONObject("data").getString("from");
				DATA.msgPatientImageForPopup = jsonObject.getJSONObject("data").getString("image");

				DATA.msgTextForPopup = EasyAES.decryptString(DATA.msgTextForPopup);
				
				DATA.selecetedBeanFromConversation = new ConversationBean("", jsonObject.getJSONObject("data").getString("doctor_id"),
						jsonObject.getJSONObject("data").getString("patient_id"), DATA.msgTextForPopup, 
						jsonObject.getJSONObject("data").getString("send_from"),
						jsonObject.getJSONObject("data").getString("date"),
						DATA.msgPatientNameForPopup, "", 
						DATA.msgPatientImageForPopup,
						"to",
						jsonObject.getJSONObject("data").getString("patient_id"),
						jsonObject.getJSONObject("data").getString("send_from"),
						"to_id");
				DATA.msgTimeForPopup = jsonObject.getJSONObject("data").getString("date");
				if(DATA.shouldNotify){
				
				}else {
					Intent i =new Intent();
					i.setAction("com.app.onlinecaredr.refreshmsges");
					context.sendBroadcast(i);	
				}
				//generateMsgNotification(context, "New message from: "+DATA.msgPatientNameForPopup);
				sendNotification("New message from: "+DATA.msgPatientNameForPopup, ConversationsActivity.class);
				Intent i =new Intent(context,ShowMessagePopupActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("incoming")) {

				jsonObject = jsonObject.getJSONObject("data");
								
				
				if (jsonObject.has("incoming_call_status")) {
					incoming_call_status = jsonObject.getInt("incoming_call_status");
					DATA.print("--incoming_call_status"+ jsonObject.getInt("incoming_call_status"));
				}*//*
				 * else{ incoming_call_status = 0; }
				 *//*
				if (incoming_call_status == 1) {
					DATA.print("--in incoming status 1");
					if (jsonObject.has("first_name") && jsonObject.has("last_name")) {// this should be added in service														 
						DATA.incomingCallerName = jsonObject.getString("first_name")+ " "+ jsonObject.getString("last_name");
					}
					if (jsonObject.has("picture")) {
						DATA.incomingCallerImage = jsonObject.getString("picture");
					}
					if (jsonObject.has("session_id")) {
						DATA.incomingCallSessionId = jsonObject.getString("session_id");
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
				      DATA.print("--compare dates "+compare);
				      
				      if (compare == 1) {
						  if(MultiPartyVideoCallFragment.isInVideoCall){//|| MainActivity.isOnVideoCallActivity
							  DATA.incomingCallResponce = "reject";
							  IncomingCallResponse incomingCallResponse = new IncomingCallResponse(context, DATA.incomingCallResponce);
							  incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
							  return;
						  }
				    	  DATA.incomingCall = true;
				    	  DATA.isFromDocToDoc = true;
							Intent i = new Intent(this, MainActivity.class);//SampleActivity.class
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
				      } else {
				      // generateNotification(context, "You have missed a live care call from "+DATA.incomingCallerName);
				    	  sendNotification("You have missed a live care call from "+DATA.incomingCallerName , MainActivityNew.class);
				      }
					
					
					
					
				}
			return;
			}else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment_cancel")) {
				String pName = jsonObject.getJSONObject("data").getJSONObject("patient_info").getString("first_name")+" "+
						jsonObject.getJSONObject("data").getJSONObject("patient_info").getString("last_name");
				//generateNotificationForApptmnt(context, "Patient "+pName+" has cancelled the appointment");
				sendNotification("Patient "+pName+" has cancelled the appointment", DrsAppointments.class);
				
				return;
			}else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("appointment_refered")){
				String referMsg = jsonObject.getJSONObject("data").getString("message");
				//generateNotificationForApptmnt(context, "You have new reffered appointments. "+referMsg);
				sendNotification("You have new reffered appointments. "+referMsg, DrsAppointments.class);
				return;
			}else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("refill")){
				DATA.refillIdInGCM = jsonObject.getJSONObject("data").getString("refill_id");
				sendNotification("You have new refill requests.", ActivityRifills.class);

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_REFILL);
				return;
			}else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("callinvite")){
				sendNotification(jsonObject.getJSONObject("data").getString("message"), ActivityAddDoctor.class);

				new DATA().insertNotification(context,DATA.NOTIF_TYPE_CALLINVITE);
				return;
			}
			
			
			
			//String status = jsonObject.getJSONObject("data").getString("status");
			String status = jsonObject.getJSONObject("data").optString("status");
			DATA.print("-- status: "+status);
			if (status.equalsIgnoreCase("connected")) {
				Intent i = new Intent();
				i.setAction(VCallModule.INCOMMING_CALL_CONNECTED);
				context.sendBroadcast(i);
			}else if (status.equals("disconnected")) {
				
				 Intent intent1 = new Intent();
				 intent1.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
				    context.sendBroadcast(intent1);
				    DATA.print("--broadcast sent");
				    //return;
				
			}else if(status.equals("accept")) {
				if(jsonObject.getJSONObject("data").has("patient_id")){
					DialogPatientInfo.patientIdGCM = jsonObject.getJSONObject("data").getString("patient_id");
				}
				DATA.isCallRejected = false;
				Intent intent1 = new Intent();
				intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
				sendBroadcast(intent1);
				
			}else if(status.equals("reject")){
				
				DATA.isCallRejected = true;
				
				Intent intent1 = new Intent();
				intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
				sendBroadcast(intent1);
				
			}else if(status.equals("noanswer")){
				DATA.isCallRejected = true;
				
				Intent intent1 = new Intent();
				intent1.setAction(VCallModule.OUTGOING_CALL_RESPONSE);
				sendBroadcast(intent1);	
			}else if(status.equals("newpatient")){
				//generateNotificationForQueue(context, "New patient added to your live care queue");
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_PATIENT);

				sendNotification("New patient added to your live care queue", LiveCare.class);
				Intent i =new Intent(context,ShowLiveCarePopup.class);
				i.putExtra("status", "newpatient");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}else if(status.equals("newappointment")){
				//generateNotificationForApptmnt(context, "You have new appointment request");
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);

				sendNotification("You have new appointment request", DrsAppointments.class);
				Intent i =new Intent(context,ShowLiveCarePopup.class);
				i.putExtra("status", "newappointment");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}else if(status.equalsIgnoreCase("note_fill_request")){

				String messageForAddNote = jsonObject.getJSONObject("data").getString("message");
				String patient_id = jsonObject.getJSONObject("data").getString("patient_id");

				DATA.isFromDocToDoc = true;
				*//*DATA.selectedLiveCare = new MyAppointmentsModel();
				DATA.selectedLiveCare.id = patient_id;*//*

				DATA.selectedUserCallId = patient_id;

				sendNotification(messageForAddNote, ActivityTelemedicineServices.class);
			}else if(status.equalsIgnoreCase("referral_request")){

				sendNotification(jsonObject.getJSONObject("data").getString("message"), MainActivityNew.class);////ActivitySOAPReferral.class  because we have 2 types referrals 1)soap 2)ot
				new DATA().insertNotification(context,DATA.NOTIF_TYPE_SERVICE_REFERRAL_REQ);

			}else if(status.equalsIgnoreCase("referred_doctor")){

				sendNotification(jsonObject.getJSONObject("data").getString("message"), MainActivityNew.class);
				//insert notif here to show badge
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
