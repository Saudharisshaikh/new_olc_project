/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.mdlive_dr.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.app.mdlive_dr.ActivityAddDoctor;
import com.app.mdlive_dr.ActivityRifills;
import com.app.mdlive_dr.ActivityTelemedicineServices;
import com.app.mdlive_dr.BuildConfig;
import com.app.mdlive_dr.ConversationsActivity;
import com.app.mdlive_dr.DrsAppointments;
import com.app.mdlive_dr.LiveCare;
import com.app.mdlive_dr.Login;
import com.app.mdlive_dr.MainActivityNew;
import com.app.mdlive_dr.OnlineCareDr;
import com.app.mdlive_dr.PresscriptionsActivity;
import com.app.mdlive_dr.R;
import com.app.mdlive_dr.ShowGroupMessagePopup;
import com.app.mdlive_dr.ShowLiveCarePopup;
import com.app.mdlive_dr.ShowMessagePopupActivity;
import com.app.mdlive_dr.SupportMessagesActivity;
import com.app.mdlive_dr.VCallModule;
import com.app.mdlive_dr.ViewConversationActivity;
import com.app.mdlive_dr.model.ConversationBean;
import com.app.mdlive_dr.model.SupportMessageBean;
import com.app.mdlive_dr.services.IncomingCallResponse;
import com.app.mdlive_dr.util.DATA;
import com.app.mdlive_dr.util.DialogPatientInfo;
import com.app.mdlive_dr.util.EasyAES;
import com.app.mdlive_dr.util.SharedPrefsHelper;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String message = remoteMessage.getData().get("message");

            generateAction(message, this);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    //======================OLC Code start==========================================
    //int incoming_call_status = 0;
    JSONObject jsonObject = null;
    private void generateAction(String message,Context context) {
        System.out.println("-- GCM msg "+message);
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

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCareDr.isAppInForground){
                        Intent intent = new Intent(context,ShowGroupMessagePopup.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        //app in background
                    }
                }else {
                    Intent intent = new Intent(context,ShowGroupMessagePopup.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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
				/*intent.putExtra("message", messageSupport);
				intent.putExtra("from", from);
				intent.putExtra("image", image);*/
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
				/*ed.putBoolean("HaveShownPrefs", false);
				ed.putBoolean("subUserSelected", false);
				ed.commit();*/
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

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCareDr.isAppInForground){
                        Intent i =new Intent(context,ShowMessagePopupActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else {
                        //app in background
                    }
                }else {
                    Intent i =new Intent(context,ShowMessagePopupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }else if (jsonObject.has("response_type") && jsonObject.getString("response_type").equalsIgnoreCase("incoming")) {

                jsonObject = jsonObject.getJSONObject("data");


                int incoming_call_status = 0;
                if (jsonObject.has("incoming_call_status")) {
                    incoming_call_status = jsonObject.getInt("incoming_call_status");
                    System.out.println("--incoming_call_status"+ jsonObject.getInt("incoming_call_status"));
                }/*
                 * else{ incoming_call_status = 0; }
                 */
                if (incoming_call_status == 1) {
                    System.out.println("--in incoming status 1");
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
                        /*
                         * SharedPreferences.Editor ed = prefs.edit();
                         * ed.putString("callingIDPatient",
                         * jsonObject.getString("call_id")); ed.commit();
                         */
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

                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if(OnlineCareDr.isAppInForground){
                                Intent i = new Intent(this, MainActivity.class);//SampleActivity.class
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }else {
                                callNotification();
                            }
                        }else {
                            Intent i = new Intent(this, MainActivity.class);//SampleActivity.class
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }

                        initCallNotAnswerTimer();

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
            System.out.println("-- status: "+status);
            if (status.equalsIgnoreCase("connected")) {
                Intent i = new Intent();
                i.setAction(VCallModule.INCOMMING_CALL_CONNECTED);
                context.sendBroadcast(i);
            }else if (status.equals("disconnected")) {

                Intent intent1 = new Intent();
                intent1.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
                context.sendBroadcast(intent1);
                System.out.println("--broadcast sent");
                //return;

                MyFirebaseMessagingService.stopCallNotAnswerTimer();

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    MyFirebaseMessagingService.sendStopBC(getBaseContext());
                }

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
                //sendNotificationWithBeep("New patient added to your live care queue", LiveCare.class);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCareDr.isAppInForground){
                        Intent i =new Intent(context,ShowLiveCarePopup.class);
                        i.putExtra("status", "newpatient");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else {
                        //app in background
                    }
                }else {
                    Intent i =new Intent(context,ShowLiveCarePopup.class);
                    i.putExtra("status", "newpatient");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }else if(status.equals("newappointment")){
                //generateNotificationForApptmnt(context, "You have new appointment request");
                new DATA().insertNotification(context,DATA.NOTIF_TYPE_NEW_APPOINTMENT);

                sendNotification("You have new appointment request", DrsAppointments.class);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if(OnlineCareDr.isAppInForground){
                        Intent i =new Intent(context,ShowLiveCarePopup.class);
                        i.putExtra("status", "newappointment");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else {
                        //app in background
                    }
                }else {
                    Intent i =new Intent(context,ShowLiveCarePopup.class);
                    i.putExtra("status", "newappointment");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }else if(status.equalsIgnoreCase("note_fill_request")){

                String messageForAddNote = jsonObject.getJSONObject("data").getString("message");
                String patient_id = jsonObject.getJSONObject("data").getString("patient_id");

                DATA.isFromDocToDoc = true;
				/*DATA.selectedLiveCare = new MyAppointmentsModel();
				DATA.selectedLiveCare.id = patient_id;*/

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
    //=======================OLC Code ends==============================================


    private void sendNotification(String messageBody, final Class<? extends Activity> activityToOpen) {

        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
        tempId = tempId+1;
        sharedPrefsHelper.save("notif_temp_id", tempId);
        System.out.println("-- notification id: "+tempId);


        Intent intent = new Intent(this, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, tempId /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        /*Uri defaultSoundUri;
        if(activityToOpen == LiveCare.class){
            defaultSoundUri = Uri.parse("android.resource://"+ getPackageName() +"/" + R.raw.beep2);
        }else {
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(tempId /* ID of notification */, notificationBuilder.build());
    }


//    private void sendNotificationWithBeep(String messageBody, final Class<? extends Activity> activityToOpen) {
//
//        System.out.println("-- Beeppp");
//
//        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
//        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
//        tempId = tempId+1;
//        sharedPrefsHelper.save("notif_temp_id", tempId);
//        System.out.println("-- notification id: "+tempId);
//
//
//        Intent intent = new Intent(this, activityToOpen);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, tempId /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//
//        /*Uri defaultSoundUri;
//        if(activityToOpen == LiveCare.class){
//            defaultSoundUri = Uri.parse("android.resource://"+ getPackageName() +"/" + R.raw.beep2);
//        }else {
//            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }*/
//
//        Uri defaultSoundUri = Uri.parse("android.resource://"+ getPackageName() +"/" + R.raw.beep2);
//
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent)
//                        .setPriority(Notification.PRIORITY_HIGH)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    getResources().getString(R.string.app_name),
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(tempId /* ID of notification */, notificationBuilder.build());
//    }



    //New call notification forground code for android 10 - Q- cant open activities from service anymore. GM
    public void callNotification(){

        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
        tempId = tempId+1;
        sharedPrefsHelper.save("notif_temp_id", tempId);
        System.out.println("-- notification id: "+tempId);


        //RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notif_incomming_call);


        Intent fullScreenIntent = new Intent(this, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, tempId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.app_name)+"_call_channel";//getString(R.string.default_notification_channel_id);
        Uri notifSoundUri = Uri.parse("android.resource://"+ getPackageName() +"/" + R.raw.incoming_call);
        //Uri notifSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,  channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("New incoming video call from "+DATA.incomingCallerName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        .setSound(notifSoundUri)


                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(notifSoundUri, att);

            notificationManager.createNotificationChannel(channel);
        }


        Notification incomingCallNotification = notificationBuilder.build();


        /*incomingCallNotification.contentView = notificationView;
        //notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, fullScreenPendingIntent);
        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(this, CallNotificationBroadcast.class);
        switchIntent.setAction("com.app.emcuradr.acceptcall");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, pendingSwitchIntent);*/



        // Provide a unique integer for the "notificationId" of each notification.
        startForeground(tempId, incomingCallNotification);

        //notificationManager.notify(tempId /* ID of notification */, incomingCallNotification);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).registerReceiver(stopJobReceiver , new IntentFilter(ACTION_STOP_JOB));
    }

    public static final String ACTION_STOP_JOB = BuildConfig.APPLICATION_ID+".actionStopCallNotification";

    private BroadcastReceiver stopJobReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction().equals(ACTION_STOP_JOB)) {
                Log.d("unregister"," job stop receiver");
            /*try {
                unregisterReceiver(this); //Unregister receiver to avoid receiver leaks exception
            }catch (Exception e){e.printStackTrace();}*/
                stopForeground(true);
            }
        }
    };



    public static void sendStopBC(Context context){
        Intent stopJobService = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stopJobService = new Intent(MyFirebaseMessagingService.ACTION_STOP_JOB);
            LocalBroadcastManager.getInstance(context).sendBroadcast(stopJobService);//getBaseContext()
        }else{
            //Toast.makeText(context,"yet to be coded - stop service",Toast.LENGTH_SHORT).show();
        }
    }




    private static Timer timerNoAnswer;
    public void initCallNotAnswerTimer(){
        timerNoAnswer = new Timer();
        timerNoAnswer.schedule(new TimerTask() {
            public void run() {
                // when the task active then close the dialog
                System.out.println("------40 seconds");
                //callResponse = "noanswer";
                //showWaitingMessage();
                IncomingCallResponse incomingCallResponse = new IncomingCallResponse(getApplicationContext(), "noanswer");
                incomingCallResponse.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                    /*if (mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                    }*/
                timerNoAnswer.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                //sendNotification("You have missed a live care call from "+DATA.incomingCallerName,MainActivityNew.class);
                //finish();

                Intent intent1 = new Intent();
                intent1.setAction(VCallModule.INCOMMING_CALL_DISCONNECTED);
                getApplicationContext().sendBroadcast(intent1);
                System.out.println("--broadcast sent");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    MyFirebaseMessagingService.sendStopBC(getBaseContext());
                }


            }
        }, 40000); //40000 // after 2 second (or 2000 miliseconds), the task will be active.
    }

    public static void stopCallNotAnswerTimer(){
        if(timerNoAnswer != null){
            timerNoAnswer.cancel();
            System.out.println("-- timerNoAnswer has been canceled - GM");
        }
    }

    //New call notification forground code for android 10 - Q- cant open activities from service anymore. GM
}
