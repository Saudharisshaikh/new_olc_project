package com.app.OnlineCareTDC_MA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareTDC_MA.api.ApiCallBack;
import com.app.OnlineCareTDC_MA.api.ApiManager;
import com.app.OnlineCareTDC_MA.model.CallParticipant;
import com.app.OnlineCareTDC_MA.services.GPSTracker;
import com.app.OnlineCareTDC_MA.util.CustomToast;
import com.app.OnlineCareTDC_MA.util.DATA;
import com.app.OnlineCareTDC_MA.util.ExpandableHeightListView;
import com.app.OnlineCareTDC_MA.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import io.agora.openlive.activities.LiveActivity;
import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;

/**
 * Created by Engr G M on 2/12/2018.
 */

public class VCallModule implements ApiCallBack{

    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomToast customToast;


    //call broadcast actions
    public static final String OUTGOING_CALL_RESPONSE = BuildConfig.APPLICATION_ID+".Outgoing_call_response";
    public static final String INCOMMING_CALL_CONNECTED = BuildConfig.APPLICATION_ID+".incomming_call_connected";
    public static final String INCOMMING_CALL_DISCONNECTED = BuildConfig.APPLICATION_ID+".incomming_call_disconnected";
    public static final String DISCONNECT_SPECIALIST = BuildConfig.APPLICATION_ID+".disconnect_specialist";
    public static final String COORDINATOR_CALL_ACCEPTED = BuildConfig.APPLICATION_ID+".coordinator_call_accepted";
    public static final String COORDINATOR_CALL_REJECTED = BuildConfig.APPLICATION_ID+".coordinator_call_rejected";
    public static final String COORDINATOR_CALL_CONNECTED = BuildConfig.APPLICATION_ID+".coordinator_call_connected";


    public static final String CALLING_TO_PATIENT = "callingToPatient";
    public static final String CALLING_TO_DOCTOR = "callingToDoctor";
    public static final String SEND_PUSH = "sendpush";

    public VCallModule(Activity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        apiCallBack = this;
        customToast = new CustomToast(activity);
    }

    public void callingToPatient(String patientId) {

        RequestParams params = new RequestParams();

        params.put("patient_id", patientId);
        params.put("doctor_id",prefs.getString("id", ""));
        params.put("session_id",DATA.rndSessionId);

        ApiManager apiManager = new ApiManager(CALLING_TO_PATIENT,"get",params,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }


    public void callingToDoctor() {
        RequestParams params = new RequestParams();
        params.put("to_doctor_id",DATA.selectedDrId );//
        params.put("from_doctor_id",prefs.getString("id", ""));
        //params.put("session_id",DATA.rndSessionId);//randomSessionID

        if(DATA.incomingCall){
            params.put("session_id",DATA.incomingCallSessionId);
        }else {
            params.put("session_id",DATA.rndSessionId);
        }

        if(! DATA.selectedUserCallId.isEmpty()){
            params.put("patient_id", DATA.selectedUserCallId);
        }

        ApiManager apiManager = new ApiManager(CALLING_TO_DOCTOR,"get",params,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }


    public void disconnectSpecialist(String docID,String type) {//note: type = doctor, patient

        RequestParams params = new RequestParams();

        params.put("type", type);
        if(type.equalsIgnoreCase("patient")){
            params.put("patient_id", docID);
        }else {
            params.put("doctor_id", docID);
        }
        /*if(DATA.isThirdCallToDoc){
            params.put("doctor_id", docID);//DATA.selectedDrId
            params.put("type", "doctor");
        }else {
            params.put("patient_id", ActivityTcmDetails.primary_patient_id);
            params.put("type", "patient");
        }*/
        params.put("message", "disconnect_spe");


        ApiManager apiManager = new ApiManager(SEND_PUSH,"post",params,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
        //no call back needed !
    }//end disconnectSpecialist


    public void incommingCallConnected(String doctor_id) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", doctor_id);
        params.put("type", "doctor");
        params.put("message", "connected");

        ApiManager apiManager = new ApiManager(SEND_PUSH,"post",params,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
        //no call back needed !

    }// end incommingCallConnected


    ExpandableHeightListView lvParticepants;
    Dialog dialogParticepants;
    public void joined_members(ExpandableHeightListView lvParticepants, Dialog dialogParticepants) {
        this.lvParticepants = lvParticepants;
        this.dialogParticepants = dialogParticepants;

        RequestParams params = new RequestParams();
        //params.put("session_id", DATA.rndSessionId);

        if(DATA.incomingCall){
            params.put("session_id",DATA.incomingCallSessionId);
        }else {
            params.put("session_id",DATA.rndSessionId);
        }

        ApiManager apiManager = new ApiManager(ApiManager.JOINED_MEMBERS,"post",params,apiCallBack, activity);
        //ApiManager.shouldShowPD = false;
        apiManager.loadURL();

    }// end joined_members


    public void unlockScreen() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void disconnectOutgoingCall(String patient_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);

        RequestParams params = new RequestParams();

        if (DATA.isFromDocToDoc){
            params.put("doctor_id", patient_id);
            params.put("type", "doctor");
            params.put("message", "disconnected");
            params.put("call_id", prefs.getString("callingID", ""));
        }else{
            params.put("patient_id", patient_id);
            params.put("type", "patient");
            params.put("message", "disconnected");
            params.put("call_id", prefs.getString("callingID", ""));
        }

        String reqURL = DATA.baseUrl+"/sendpush";

        DATA.print("-- Request : "+reqURL);
        DATA.print("-- params : "+params.toString());

        client.post(reqURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    String content = new String(response);
                    DATA.print("--reaponce in disconnectOutgoingCall "+content);
                    //if (getActivity() != null) {
                    activity.finish();
                    // }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure : "+reqURL+content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end disconnectOutgoingCall


    public final String COORDINATOR_CALL = "coordinatorCall";
    public final String END_CALL_COORDINATOR = "endcallCoordinator";
    public void coordinatorCall() {
        ApiManager apiManager = new ApiManager(COORDINATOR_CALL+"/"+prefs.getString("id", "0")+"/doctor","get",null,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }
    public void endcallCoordinator() {
        ApiManager apiManager = new ApiManager(END_CALL_COORDINATOR+"/"+ Constants.ROOM_NAME_MULTI,"get",null,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }

    public static final String EMS_CALL = "ems/emscall";
    public static final String EMS_DIRECT_CALL = "ems/emsdirectCall";
    static boolean isThreeWay = false;
    public void emscall(final boolean isThreeWay) {

        VCallModule.isThreeWay = isThreeWay;

        RequestParams params = new RequestParams();
        if(isThreeWay){
            params.put("doctor_id", prefs.getString("id", "0"));
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("session_id",DATA.rndSessionId);
        }else {
            params.put("doctor_id", prefs.getString("id", "0"));
            params.put("patient_id", DATA.selectedLiveCare.id);
        }

        DATA.print("-- params in emscall: "+params.toString()+" isThreeWay: "+isThreeWay);

        ApiManager apiManager = new ApiManager(EMS_CALL,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void emsdirectCall() {
        GPSTracker gpsTracker = new GPSTracker(activity);

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        //params.put("patient_id", ActivityFollowUps_4.selectedFollowupBean.patient_id);
        params.put("latitude", gpsTracker.getLatitude()+"");
        params.put("longitude", gpsTracker.getLongitude()+"");

        ApiManager apiManager = new ApiManager(EMS_DIRECT_CALL,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public String randomStr()
    {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder( 8 );
        for( int i = 0; i < 8; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public void sendNotification(String msg,final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(CALLING_TO_PATIENT)){
            try {
                //
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                if(status.equals("success")) {

                    SharedPreferences.Editor ed = prefs.edit();
                    //	ed.putString("callingID", jsonObject.getString("call_id"));
                    ed.putString("callingID", jsonObject.getString("increment_id"));
                    ed.commit();

                }else if (status.equals("error")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Info").setMessage(msg).setPositiveButton("OK", null);
                    AlertDialog d = builder.create();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (!MultiPartyVideoCallFragment.isInVideoCall) {
                                activity.finish();
                            }
                        }
                    });
                    d.show();
                }else {
                    customToast.showToast("Call has been ended",0,0);
                    activity.finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(CALLING_TO_DOCTOR)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                if(status.equals("success")) {
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("callingID", jsonObject.getString("call_id"));
                    //	ed.putString("callingID", jsonObject.getString("increment_id"));
                    ed.commit();

                    //						t.cancel();

						/*Intent i = new Intent(getBaseContext(), OutgoingCallService.class);
											startService(i);*/
                    if (MainActivity.doctorsDialog != null && MainActivity.doctorsDialog.isShowing()) {
                        MainActivity.doctorsDialog.dismiss();
                    }


                }else if (status.equals("error")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Info").setMessage(msg).setPositiveButton("OK", null);
                    AlertDialog d = builder.create();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (!MultiPartyVideoCallFragment.isInVideoCall) {
                                activity.finish();
                            }
                        }
                    });
                    d.show();
                }else {
                    customToast.showToast("Call has been ended",0,0);
                    //finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.JOINED_MEMBERS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                final ArrayList<CallParticipant> callParticipants = new ArrayList<>();
                CallParticipant callParticipant;
                for (int i = 0; i < data.length(); i++) {
                    String to_id = data.getJSONObject(i).getString("to_id");
                    String callto = data.getJSONObject(i).getString("callto");
                    String join_user = data.getJSONObject(i).getString("join_user");

                    callParticipant = new CallParticipant(to_id,callto,join_user);
                    if(callto.equalsIgnoreCase("doctor") && to_id.equalsIgnoreCase(prefs.getString("id",""))){
                        //not adding myself
                    }else {
                        callParticipants.add(callParticipant);
                    }
                    callParticipant = null;
                }

                if(lvParticepants!= null){
                    lvParticepants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dialogParticepants.dismiss();
                            disconnectSpecialist(callParticipants.get(position).to_id, callParticipants.get(position).callto);
                            Toast.makeText(activity, "Disconnect", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ArrayAdapter<CallParticipant> callParticipantArrayAdapter = new ArrayAdapter<CallParticipant>(activity
                            ,android.R.layout.simple_list_item_1,callParticipants);
                    lvParticepants.setAdapter(callParticipantArrayAdapter);
                    lvParticepants.setExpanded(true);
                }
                if(dialogParticepants != null){
                    dialogParticepants.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.contains(COORDINATOR_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                int success = jsonObject.getInt("success");
                if (success == 0) {
                    AlertDialog d;
                    AlertDialog.Builder b = new AlertDialog.Builder(activity).setTitle("Oopsss")
                            .setMessage(jsonObject.getString("message")).setPositiveButton("Ok", null);
                    d = b.create();
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface arg0) {
                            // TODO Auto-generated method stub
                            activity.finish();
                        }
                    });

                } else {
                    //DATA.outgoingCallSessionId = jsonObject.getJSONObject("data").getString("call_id");
                    Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(apiName.contains(END_CALL_COORDINATOR)){
            activity.finish();
        }else if(apiName.equalsIgnoreCase(EMS_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                int  success = jsonObject.getInt("success");

                if(success == 1){
                    MainActivity.ems_id = jsonObject.getJSONObject("data").getString("ems_id");
                    int increment_id = jsonObject.getJSONObject("data").getInt("increment_id");
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("callingID", increment_id+"");
                    ed.commit();

                    if(isThreeWay){
                        DATA.selectedDrId = MainActivity.ems_id;//test well plz
                    }else {
                        Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");
                    }
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(activity).setTitle("Info")
                                    .setMessage(jsonObject.getString("message"))
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!MultiPartyVideoCallFragment.isInVideoCall){
                                activity.finish();
                            }
                        }
                    });
                    alertDialog.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(EMS_DIRECT_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                int  success = jsonObject.getInt("success");

                if(success == 1){
                    Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");

                    MainActivity.ems_id = jsonObject.getJSONObject("data").getString("ems_id");
                    int increment_id = jsonObject.getJSONObject("data").getInt("increment_id");
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("callingID", increment_id+"");
                    ed.commit();
                }else{
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
                                    .setMessage(jsonObject.getString("message"))
                                    .setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new GloabalMethods(activity).initMsgDialogEMS();
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!MultiPartyVideoCallFragment.isInVideoCall){
                                        activity.finish();
                                    }
                                }
                            }).create();

                    alertDialog.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }




    public View.OnClickListener getConnectBtnListener(int totalInRoom){
        return new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //mConferenceManager.getActiveUsers();
				/*if(mConferenceManager.getActiveUsers().length < 2){
				DATA.isConfirence =true;
				startActivity(new Intent(VideoCallActivity.this,DocToDoc.class));
				}else{
					//Toast.makeText(VideoCallActivity.this,"You are already connected to consultant",Toast.LENGTH_SHORT).show();
				disconnectSpecialist(DATA.selectedDrId);
				}*/
                //DATA.isConfirence =true;
                //getActivity().startActivity(new Intent(getActivity(),DocToDoc.class));
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.lay_sp_nurse_dialog);
                Button btn_doctodoc =  (Button) dialog.findViewById(R.id.btn_doctodoc);
                Button sp_button = (Button) dialog.findViewById(R.id.btn_sp);
                Button cp_button = (Button) dialog.findViewById(R.id.btn_cp);
                Button btnFamily = (Button) dialog.findViewById(R.id.btnFamily);
                Button btn_ems = (Button) dialog.findViewById(R.id.btn_ems);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btn_ems.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new VCallModule(activity).emscall(true);
                    }
                });
                btn_doctodoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //((MainActivity)getActivity()).initDocToDocDialog();

                        if(activity instanceof MainActivity){
                            ((MainActivity)activity).initDoctorsDialogNew();
                        }else if(activity instanceof LiveActivity){
                            ((LiveActivity)activity).initDoctorsDialogNew(1);
                        }
                    }
                });
                sp_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if(activity instanceof MainActivity){
                            ((MainActivity)activity).initDocToDocDialog();
                        }else if(activity instanceof LiveActivity){
                            ((LiveActivity)activity).initDoctorsDialogNew(2);
                        }
                    }
                });

                cp_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if(activity instanceof MainActivity){
                            ((MainActivity)activity).initCPDialog();
                        }else if(activity instanceof LiveActivity){
                            ((LiveActivity)activity).initCPDialog();
                        }
                    }
                });

                if(ActivityTcmDetails.primary_patient_id.isEmpty()){
                    btnFamily.setEnabled(false);
                }else{
                    btnFamily.setEnabled(true);
                    btnFamily.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            if(ActivityTcmDetails.family_is_online.equalsIgnoreCase("1")){
                                //DATA.isThirdCallToDoc = false;
                                new VCallModule(activity).callingToPatient(ActivityTcmDetails.primary_patient_id);
                                Toast.makeText(activity,"Connecting to the patient's primary family member",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(activity,"Patient's primary family member is offline and can't connected right now.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }



                if (totalInRoom < 3) {
                    dialog.setCanceledOnTouchOutside(false);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    //DATA.isThirdCallToDoc = true;

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                }else {
                    Toast.makeText(activity,"Maximum 4 particepants can be connected",Toast.LENGTH_SHORT).show();
                }

                    /*if (getNumPeers() < 2) {
                        dialog.setCanceledOnTouchOutside(false);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        DATA.isThirdCallToDoc = true;

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    } else {
                        new VCallModule(getActivity()).disconnectSpecialist();
                        Toast.makeText(getActivity(), "Disconnect", Toast.LENGTH_SHORT).show();
                    }*/
            }
        };
    }


    public void showDisconnectDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_disconnect);
        //dialogSupport.setCancelable(false);
        dialogSupport.setCanceledOnTouchOutside(false);

        ExpandableHeightListView lvCallParticepents = dialogSupport.findViewById(R.id.lvCallParticepents);
        TextView tvCancel = (TextView) dialogSupport.findViewById(R.id.tvCancel);



        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogSupport.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        //dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);

        new VCallModule(activity).joined_members(lvCallParticepents,dialogSupport);
    }
}
