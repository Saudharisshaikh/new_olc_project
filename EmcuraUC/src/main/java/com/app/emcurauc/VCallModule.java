package com.app.emcurauc;

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
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.emcurauc.adapter.SubUsersAdapter2;
import com.app.emcurauc.api.ApiCallBack;
import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.model.SubUsersModel;
import com.app.emcurauc.util.CheckInternetConnection;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.Constants;

/**
 * Created by Engr G M on 5/7/2018.
 */

public class VCallModule implements ApiCallBack{

    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;


    //call broadcast actions
    //call broadcast actions
    //public static final String OUTGOING_CALL_RESPONSE = BuildConfig.APPLICATION_ID+".Outgoing_call_response";
    //public static final String INCOMMING_CALL_CONNECTED = BuildConfig.APPLICATION_ID+".incomming_call_connected";
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
        checkInternetConnection = new CheckInternetConnection(activity);
    }


    public void incommingCallConnected(String doctor_id) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", doctor_id);
        params.put("type", "doctor");
        params.put("message", "connected");

        ApiManager apiManager = new ApiManager(SEND_PUSH,"post",params,apiCallBack, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
        //no call back needed !

    }//end incommingCallConnected

    public void unlockScreen() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public final String COORDINATOR_CALL = "coordinatorCall";
    public final String END_CALL_COORDINATOR = "endcallCoordinator";
    public void coordinatorCall() {
        ApiManager apiManager = new ApiManager(COORDINATOR_CALL+"/"+prefs.getString("id", "0"),"get",null,apiCallBack, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }
    public void endcallCoordinator() {
        ApiManager apiManager = new ApiManager(END_CALL_COORDINATOR+"/"+DATA.outgoingCallSessionId,"get",null,apiCallBack, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }

    public void sendNotification(String msg,final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.contains(COORDINATOR_CALL)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                int success = jsonObject.getInt("success");
                if (success == 0) {
                    AlertDialog d;
                    AlertDialog.Builder b = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.app_name))
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
                    DATA.outgoingCallSessionId = jsonObject.getJSONObject("data").getString("call_id");
                    Constants.ROOM_NAME_MULTI = DATA.outgoingCallSessionId;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(apiName.contains(END_CALL_COORDINATOR)){
            activity.finish();
        }else if(apiName.equalsIgnoreCase(ApiManager.MY_FAMILY_MEMBERS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray sub_users = jsonObject.getJSONArray("sub_users");

                Type listType = new TypeToken<ArrayList<SubUsersModel>>() {}.getType();
                ArrayList<SubUsersModel> subUsersModels = new Gson().fromJson(sub_users.toString(), listType);

                if(subUsersModels!= null){
                    showSubusersDialog(subUsersModels);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CALLING_TO_FAMILY)){
            //{"status":"success","session_id":"1234567","participant_data":{"first_name":"Jack","last_name":"Denial"},"increment_id":21492}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast("Calling . . .",0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, DATA.JSON_ERROR_MSG,Toast.LENGTH_SHORT).show();
            }
        }
    }



    //emcura new patient to patient/family call
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

                Button btnFamilyMem =  (Button) dialog.findViewById(R.id.btnFamilyMem);


                btnFamilyMem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //((MultiVideosActivity) activity).loadSubPatients();
                        loadSubPatients();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                /*btn_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btn_ems.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new VCallModule(getActivity()).emscall(true);
                    }
                });
                btn_doctodoc.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //((MainActivity)getActivity()).initDocToDocDialog();
                        ((MainActivity)getActivity()).initDoctorsDialogNew();
                    }
                });
                sp_button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ((MainActivity)getActivity()).initDocToDocDialog();
                    }
                });

                cp_button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ((MainActivity)getActivity()).initCPDialog();
                    }
                });

                if(ActivityTcmDetails.primary_patient_id.isEmpty()){
                    btnFamily.setEnabled(false);
                }else{
                    btnFamily.setEnabled(true);
                    btnFamily.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            if(ActivityTcmDetails.family_is_online.equalsIgnoreCase("1")){
                                //DATA.isThirdCallToDoc = false;
                                new VCallModule(getActivity()).callingToPatient(ActivityTcmDetails.primary_patient_id);
                                Toast.makeText(getActivity(),"Connecting to the patient's primary family member",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getActivity(),"Patient's primary family member is offline and can't connected right now.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }*/



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


    //-------------Emcura new Patient to family calls 7 nov 2019

    public void loadSubPatients(){
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));

        //ApiManager.SUB_PATIENTS
        ApiManager apiManager = new ApiManager(ApiManager.MY_FAMILY_MEMBERS,"post",params, VCallModule.this, activity);
        apiManager.loadURL();
    }

    public void callingToFamily(String family_id){
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        params.put("family_id", family_id);
        params.put("session_id", DATA.incomingCallSessionId);

        //ApiManager.SUB_PATIENTS
        ApiManager apiManager = new ApiManager(ApiManager.CALLING_TO_FAMILY,"post",params,VCallModule.this, activity);
        apiManager.loadURL();
    }


    public void showSubusersDialog(ArrayList<SubUsersModel> subUsersModels){
        Dialog dialogSubusers = new Dialog(activity);//,R.style.TransparentThemeH4B
        dialogSubusers.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogSubusers.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogSubusers.setContentView(R.layout.dialog_subusers);

        ImageView ivCancel = (ImageView) dialogSubusers.findViewById(R.id.ivCancel);
        ListView lvSubusers = dialogSubusers.findViewById(R.id.lvSubusers);

        ivCancel.setOnClickListener(v -> dialogSubusers.dismiss());


        lvSubusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogSubusers.dismiss();
                callingToFamily(subUsersModels.get(position).id);
            }
        });
        lvSubusers.setAdapter(new SubUsersAdapter2(activity, subUsersModels));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSubusers.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogSubusers.setCanceledOnTouchOutside(false);
        dialogSubusers.show();
        dialogSubusers.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogSubusers;


        //loadHospitals(true);
    }
}
