package com.app.OnlineCareUS_Dr.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.OnlineCareUS_Dr.MainActivityNew;
import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.app.OnlineCareUS_Dr.util.SharedPrefsHelper;
import com.google.firebase.messaging.RemoteMessage;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class BackgroundTaskJava extends Worker {
    public BackgroundTaskJava(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("-- one time req WorkM "," Background call");
        //sendNotification("Background Task","Succcessfully done");
        callNotification();
        return Result.success();
    }

    void sendNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",
                    "android",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("WorkManger");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    //New call notification forground code for android 10 - Q- cant open activities from service anymore. GM

    long[] vibrationPattern = {1000, 1000};
    public void callNotification() {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        int tempId = sharedPrefsHelper.get("notif_temp_id",0);
        tempId = tempId+1;
        sharedPrefsHelper.save("notif_temp_id", tempId);
        DATA.print("-- notification id: "+tempId);


        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notif_incomming_call);

        Intent fullScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent fullScreenPendingIntent;
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), tempId, fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE);
       /* }
        else {
            fullScreenPendingIntent = PendingIntent.getActivity(this, tempId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }*/
//        /*============================ Ahmer work =================================*/
//        //Intent for call Accept
//        Intent callAcceptIntent = new Intent(this, MultiVideosActivity.class);
//        callAcceptIntent.putExtra("fromNotification", true);
//        PendingIntent callAcceptPendingIntent = PendingIntent.getActivity(this, 1, callAcceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //Intent for call reject
//        Intent callDeclineIntent = new Intent(this, MultiVideosActivity.class);
//        callDeclineIntent.putExtra("fromNotificationReject", false);
//        PendingIntent callDeclinePendingIntent = PendingIntent.getActivity(this, 0, callDeclineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        /*============================ Ahmer work end =================================*/

        String channelId = getApplicationContext().getString(R.string.app_name)+"_call_channel";//getString(R.string.default_notification_channel_id);
        Uri notifSoundUri = getNotification(); //Uri.parse("android.resource://"+ getApplicationContext().getPackageName() +"/" + R.raw.incoming_call);
        //Uri notifSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(),  channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setCustomContentView(notificationView)
                        .setOngoing(true)
//                        .setContentTitle(getResources().getString(R.string.app_name))
//                        .setContentText("New incoming video call from "+DATA.incomingCallerName)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setAutoCancel(true)
                        .setSound(notifSoundUri)
                        .setVibrate(vibrationPattern)


                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);




        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, getApplicationContext().getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(notifSoundUri, att);
            channel.setVibrationPattern(vibrationPattern);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = notificationBuilder.build();
        notification.flags |= NotificationCompat.FLAG_INSISTENT;
        notificationManager.notify(tempId , notification);
        //notificationManager.notify(tempId , notificationBuilder.build());
        //Notification incomingCallNotification = notificationBuilder.build();

        /*incomingCallNotification.contentView = notificationView;
        //notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, fullScreenPendingIntent);
        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(this, CallNotificationBroadcast.class);
        switchIntent.setAction("com.app.emcuradr.acceptcall");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btnAcceptCall, pendingSwitchIntent);*/
    }
    private Uri getNotification() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        if (notification == null) {
            // notification is null, using backup
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (notification == null) {
                // notification backup is null, using 2nd backup
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }
        return notification;
    }
}
