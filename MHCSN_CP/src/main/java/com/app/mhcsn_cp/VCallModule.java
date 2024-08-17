package com.app.mhcsn_cp;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.app.mhcsn_cp.adapters.LvDoctorsAdapter;
import com.app.mhcsn_cp.adapters.NurseAdapter;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.CallParticipant;
import com.app.mhcsn_cp.model.DoctorsModel;
import com.app.mhcsn_cp.model.NurseBean;
import com.app.mhcsn_cp.model.SpecialityModel;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.Database;
import com.app.mhcsn_cp.util.ExpandableHeightListView;
import com.app.mhcsn_cp.util.GPSTracker;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.Constants;
import sg.com.temasys.skylink.sdk.sampleapp.MultiPartyVideoCallFragment;


/**
 * Created by Engr G M on 2/7/2018.
 */

public class VCallModule implements ApiCallBack{

    Activity activity;
    SharedPreferences prefs;
    SharedPrefsHelper sharedPrefsHelper;
    ApiCallBack apiCallBack;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;

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
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        apiCallBack = this;
        customToast = new CustomToast(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
    }




    public void callingToPatient(String patientId) {

        RequestParams params = new RequestParams();
        params.put("patient_id", patientId); // DATA.selectedUserCallId
        params.put("doctor_id", prefs.getString("id", "0")); // prefs.getString("id",																// "")
        params.put("session_id", DATA.rndSessionId);// randomSessionID

        ApiManager apiManager = new ApiManager(CALLING_TO_PATIENT,"get",params,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }// end callingToPatient


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

    }//end callingToDoctor


    public void disconnectSpecialist(String docID,String type) {//note: type = doctor, patient

        RequestParams params = new RequestParams();
        params.put("type", type);
        if(type.equalsIgnoreCase("patient")){
            params.put("patient_id", docID);
        }else {
            params.put("doctor_id", docID);
        }
        /*if(DATA.isThirdCallToDoc){
            params.put("doctor_id", DATA.selectedDrId);
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


    ListView lvDoctors;
    TextView tvNoData;
    Dialog doctorsDialog;
    ArrayList<DoctorsModel> doctorsModels;
    public void initDoctorsDialogNew() {
        doctorsDialog = new Dialog(activity);
        doctorsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        doctorsDialog.setContentView(R.layout.doctors_dialog_new);

        //doctorsDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        final Button btnDoctors = (Button) doctorsDialog.findViewById(R.id.btnDoctors);
        final Button btnSpecialist = (Button) doctorsDialog.findViewById(R.id.btnSpecialist);

        lvDoctors = (ListView) doctorsDialog.findViewById(R.id.lvDoctors);
        tvNoData = (TextView) doctorsDialog.findViewById(R.id.tvNoData);
        ImageView ivClose = (ImageView) doctorsDialog.findViewById(R.id.ivClose);

        btnDoctors.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (checkInternetConnection.isConnectedToInternet()) {
                    btnDoctors.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                    btnDoctors.setTextColor(activity.getResources().getColor(android.R.color.white));
                    btnSpecialist.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
                    btnSpecialist.setTextColor(activity.getResources().getColor(R.color.theme_red));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("doctor");
                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                }
            }
        });

        btnSpecialist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (checkInternetConnection.isConnectedToInternet()) {
                    btnDoctors.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
                    btnDoctors.setTextColor(activity.getResources().getColor(R.color.theme_red));
                    btnSpecialist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                    btnSpecialist.setTextColor(activity.getResources().getColor(android.R.color.white));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("specialist");
                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                }
            }
        });

        lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                DATA.selectedDrId = doctorsModels.get(arg2).id;
                DATA.print("--onitemclick id "+DATA.selectedDrId);
                //new VCallModule(activity).callingToDoctor();
                callingToDoctor();

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorsDialog.dismiss();
            }
        });
        doctorsDialog.show();

        getAllDrs("doctor");
    }

    LvDoctorsAdapter lvDoctorsAdapter;
    public void getAllDrs(String user_type) {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("user_type", user_type);
        params.put("selected_doctor_id",DATA.selectedDrIdForNurse);

        /*if (etZipcode.getText().toString().isEmpty()) {

		} else {
			params.put("zipcode", etZipcode.getText().toString());
		}*/

        ApiManager apiManager = new ApiManager(ApiManager.SEARCHALLDOCTORS_,"post",params,apiCallBack, activity);
        //ApiManager.shouldShowPD = false;
        apiManager.loadURL();

    }//end getAllDrs



    //===================Specialists by clinic==========================================================
    Dialog docToDocDialog;

    public void initDocToDocDialog() {
        docToDocDialog = new Dialog(activity);
        docToDocDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Database db = new Database(activity);

        docToDocDialog.setContentView(R.layout.doc_to_doc);

        ImageView imcCelv = (ImageView) docToDocDialog.findViewById(R.id.imcCelv);
        ImageView imgHenry = (ImageView) docToDocDialog.findViewById(R.id.imgHenry);
        ImageView imgKaled = (ImageView) docToDocDialog.findViewById(R.id.imgKaled);
        ImageView imgMayo = (ImageView) docToDocDialog.findViewById(R.id.imgMayo);
        ImageView imgRoswell = (ImageView) docToDocDialog.findViewById(R.id.imgRoswell);

        Spinner spinnerSpecialities = (Spinner) docToDocDialog.findViewById(R.id.spinnerSpecility);

        docToDocDialog.show();

        //DATA.allSpecialities = new ArrayList<SpecialityModel>();
        DATA.allSpecialities = sharedPrefsHelper.getAllSpecialities();//db.getAllSpecialities();

        spinnerSpecialities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //customToast.showToast(DATA.allSpecialities.get(position).specialityName, 0, 0);
                DATA.drSpecialityId = DATA.allSpecialities.get(position).specialityId;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayAdapter<SpecialityModel> specialitiesArray = new ArrayAdapter<SpecialityModel>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allSpecialities);
        spinnerSpecialities.setAdapter(specialitiesArray);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DATA.drSpecialityId.equals("0")){
                    customToast.showToast("Please select speciality", 0, 0);
                    return;
                }
                switch (v.getId()){
                    case R.id.imcCelv:
                        DATA.drClinicId = "1";
                        getDoctors();
                        break;
                    case R.id.imgHenry:
                        DATA.drClinicId = "2";
                        getDoctors();
                        break;
                    case R.id.imgKaled:
                        DATA.drClinicId = "3";
                        getDoctors();
                        break;
                    case R.id.imgMayo:
                        DATA.drClinicId = "4";
                        getDoctors();
                        break;
                    case R.id.imgRoswell:
                        DATA.drClinicId = "5";
                        getDoctors();
                        break;
                    default:
                        break;
                }
            }
        };
        imcCelv.setOnClickListener(onClickListener);
        imgHenry.setOnClickListener(onClickListener);
        imgKaled.setOnClickListener(onClickListener);
        imgMayo.setOnClickListener(onClickListener);
        imgRoswell.setOnClickListener(onClickListener);
    }


    public void getDoctors() {

        RequestParams params = new RequestParams();
        params.put("clinic_id",DATA.drClinicId);
        params.put("speciality_id",DATA.drSpecialityId);

        ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTORS_BY_CLINIC,"get",params,apiCallBack, activity);
        //ApiManager.shouldShowPD = false;
        apiManager.loadURL();

    }//end getDoctors

    public void initDoctorsDialog() {
        doctorsDialog = new Dialog(activity);
        doctorsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        doctorsDialog.setContentView(R.layout.doctors_dialog);

        ListView lvDialogDoctors = (ListView) doctorsDialog.findViewById(R.id.lvDialogDoctors);
        TextView tvNoData = (TextView) doctorsDialog.findViewById(R.id.tvNoData);
        ImageView ivClose = (ImageView) doctorsDialog.findViewById(R.id.ivClose);
        if(doctorsModels.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            tvNoData.setVisibility(View.GONE);
        }
        lvDialogDoctors.setAdapter(new LvDoctorsAdapter(activity,doctorsModels));
        lvDialogDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DATA.selectedDrId = doctorsModels.get(arg2).id;
                DATA.print("--onitemclick id "+DATA.selectedDrId);
                //new VCallModule(activity).callingToDoctor();
                callingToDoctor();

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorsDialog.dismiss();
            }
        });
        doctorsDialog.show();
    }

    //===================Specialists by clinic==========================================================


    public void unlockScreen() {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public String randomStr() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


    public void disconnectOutgoingCall(String patient_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
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

        DATA.print("--params in disconnectOutgoingCall "+params.toString());
		 /* if (DATA.isFromDocToDoc){
			  params.put("doctor_id", patient_id);
			  params.put("type", "doctor");
			  params.put("message", "disconnected");
			}else{
				 params.put("patient_id", patient_id);
				  params.put("type", "patient");
				  params.put("message", "disconnected");
			}*/

        client.post(DATA.baseUrl+"/sendpush", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                try{
                    String content = new String(response);
                    DATA.print("--reaponce in disconnectOutgoingCall "+content);
                    try {
                        activity.finish();
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.err.println("-- null pointer in sendpush getActivity().finish()");
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: sendpush, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onFailure in disconnectOutgoingCall "+content);
                    //new GloabalMethods(activity).checkLogin(content);
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
    public static String ems_id = "";
    public void emscall(final boolean isThreeWay) {

        VCallModule.isThreeWay = isThreeWay;

        RequestParams params = new RequestParams();
        if(isThreeWay){
            params.put("doctor_id", prefs.getString("id", "0"));
            params.put("patient_id", DATA.selectedUserCallId);
            params.put("session_id",DATA.rndSessionId);
        }else {
            params.put("doctor_id", prefs.getString("id", "0"));
            params.put("patient_id", ActivityFollowUps_4.selectedFollowupBean.patient_id);
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


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(CALLING_TO_PATIENT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("msg");

                if (status.equals("success")) {

                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("callingID",jsonObject.getString("increment_id"));
                    ed.commit();
                    // t.cancel();
								/*
								 * Intent i = new Intent(getBaseContext(),
								 * OutgoingCallService.class); startService(i);
								 */

                }else if (status.equals("error")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Info").setMessage(msg).
                            setPositiveButton("OK", null);
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

                    // outgoingCallLayout.setVisibility(View.GONE);
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
                    ed.commit();

                    //						t.cancel();

						/*Intent i = new Intent(getBaseContext(), OutgoingCallService.class);
											startService(i);
						*/
                    if (doctorsDialog != null && doctorsDialog.isShowing()) {
                        doctorsDialog.dismiss();
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
                    //outgoingCallLayout.setVisibility(View.GONE);
                    customToast.showToast("Call has been ended",0,0);
                    activity.finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SEARCHALLDOCTORS_)){

            try {
                JSONArray data = new JSONArray(content);
                doctorsModels = new ArrayList<>();
                DoctorsModel temp = null;

                if (data.length() == 0) {
                    //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    tvNoData.setVisibility(View.VISIBLE);
                    lvDoctorsAdapter = new LvDoctorsAdapter(activity, doctorsModels);
                    lvDoctors.setAdapter(lvDoctorsAdapter);
                }else{
                    tvNoData.setVisibility(View.GONE);

                    for (int i = 0; i < data.length(); i++) {
                        temp = new DoctorsModel();
                        JSONObject object = data.getJSONObject(i);
                        temp.id = object.getString("id");
                        temp.latitude =object.getString("latitude");
                        temp.longitude=object.getString("longitude");
                        temp.zip_code=object.getString("zip_code");
                        temp.fName=object.getString("first_name");
                        temp.lName=object.getString("last_name");
                        temp.is_online=object.getString("is_online");
                        temp.image=object.getString("image");
                        temp.designation=object.getString("designation");


                        if (temp.latitude.equalsIgnoreCase("null")) {
                            temp.latitude = "0.0";
                        }
                        if (temp.longitude.equalsIgnoreCase("null")) {
                            temp.longitude = "0.0";
                        }

                        temp.speciality_id=object.getString("speciality_id");
                        temp.current_app=object.getString("current_app");
                        temp.speciality_name=object.getString("speciality_name");

                        doctorsModels.add(temp);
                        temp = null;
                    }

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                    lvDoctorsAdapter = new LvDoctorsAdapter(activity,doctorsModels);
                    lvDoctors.setAdapter(lvDoctorsAdapter);

                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_DOCTORS_BY_CLINIC)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                String status = jsonObject.getString("status");

                if(status.equals("success")) {

                    JSONArray doctorsArray = jsonObject.getJSONArray("doctors");

                    doctorsModels = new ArrayList<>();

                    DoctorsModel temp;

                    for(int i = 0; i<doctorsArray.length(); i++) {

                        temp = new DoctorsModel();

                        temp.id = doctorsArray.getJSONObject(i).getString("id");
                        temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
                        temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
                        temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
                        temp.email = doctorsArray.getJSONObject(i).getString("email");
                        temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
                        temp.image = doctorsArray.getJSONObject(i).getString("image");
                        temp.careerData = doctorsArray.getJSONObject(i).getString("introduction");
                        temp.designation = doctorsArray.getJSONObject(i).getString("designation");
                        temp.is_online=doctorsArray.getJSONObject(i).getString("is_online");
                        temp.latitude =doctorsArray.getJSONObject(i).getString("latitude");
                        temp.longitude=doctorsArray.getJSONObject(i).getString("longitude");
                        temp.zip_code=doctorsArray.getJSONObject(i).getString("zip_code");
                        if (temp.latitude.equalsIgnoreCase("null")) {
                            temp.latitude = "0.0";
                        }
                        if (temp.longitude.equalsIgnoreCase("null")) {
                            temp.longitude = "0.0";
                        }
                        temp.speciality_id=doctorsArray.getJSONObject(i).getString("speciality_id");
                        temp.current_app=doctorsArray.getJSONObject(i).getString("current_app");
                        temp.speciality_name=doctorsArray.getJSONObject(i).getString("designation");//speciality_name is not in this api


                        doctorsModels.add(temp);

                        temp = null;
                    }


                    if(doctorsModels.isEmpty()){
                        customToast.showToast("No specialist found. Please try again with a different Speciality/Clinic",0,1);
                    }else {
                        //openActivity.open(DoctorsList.class, false);
                        docToDocDialog.dismiss();
                        initDoctorsDialog();
                    }

                } else {
                    customToast.showToast("Internal server error", 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
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
                    //callParticipants.add(callParticipant);
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
                    VCallModule.ems_id = jsonObject.getJSONObject("data").getString("ems_id");
                    int increment_id = jsonObject.getJSONObject("data").getInt("increment_id");
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("callingID", increment_id+"");
                    ed.commit();

                    if(isThreeWay){
                        DATA.selectedDrId = VCallModule.ems_id;//test well plz
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
            //{"success":1,"data":{"call_id":"40905214","increment_id":12536,"ems_id":"254"}}
            try {
                JSONObject jsonObject = new JSONObject(content);
                int  success = jsonObject.getInt("success");

                if(success == 1){
                    Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");

                    VCallModule.ems_id = jsonObject.getJSONObject("data").getString("ems_id");
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
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.lay_sp_nurse_dialog);
                Button btnDoctors = (Button) dialog.findViewById(R.id.btnDoctors);
                Button btnSpecialists =  (Button) dialog.findViewById(R.id.btnSpecialists);
                Button cp_button = (Button) dialog.findViewById(R.id.btn_cp);
                Button btnFamily = (Button) dialog.findViewById(R.id.btnFamily);
                Button btn_ems = (Button) dialog.findViewById(R.id.btn_ems);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                btnDoctors.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //((MainActivity)getActivity()).initDocToDocDialog();
                        new VCallModule(activity).initDoctorsDialogNew();
                    }
                });
                btnSpecialists.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new VCallModule(activity).initDocToDocDialog();
                        //((MainActivity)getActivity()).initDoctorsDialogNew();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
                btn_ems.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new VCallModule(activity).emscall(true);
                    }
                });
                    /*if(DATA.selectedDrIdForNurse.isEmpty()){//this was single doctor button to call selected doc
                        sp_button.setVisibility(View.GONE);
                    }
                    sp_button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            DATA.selectedDrId = DATA.selectedDrIdForNurse;
                            ((MainActivity)getActivity()).callUser1();
                        }
                    });*/

                cp_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new VCallModule(activity).initCPDialog();
                    }
                });


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

                    /*if (getNumPeers() < 3) {
                        dialog.setCanceledOnTouchOutside(false);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        //DATA.isThirdCallToDoc = true;

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    }else {
                        Toast.makeText(getActivity(),"Maximum 4 particepants can be connected",Toast.LENGTH_SHORT).show();
                    }*/


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

        ExpandableHeightListView lvCallParticepents = (ExpandableHeightListView) dialogSupport.findViewById(R.id.lvCallParticepents);
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



    ListView lvNurse;
    TextView tvTabNurse,tvTabNursePractitioner,tvTabSocialWorker,tvTabDietitian,tvTabOT,tvTabPharmacist,tvTabMA,tvTabOM,tvTabSup,tvTabAll;
    public void initCPDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_find_nurse);

        LinearLayout layTcmOptions = dialog.findViewById(R.id.layTcmOptions);
        layTcmOptions.setVisibility(View.GONE);
        Button btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setText("Cancel");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        lvNurse = dialog.findViewById(R.id.lvNurse);
        tvTabNurse = dialog.findViewById(R.id.tvTabNurse);
        tvTabNursePractitioner = dialog.findViewById(R.id.tvTabNursePractitioner);
        tvTabSocialWorker = dialog.findViewById(R.id.tvTabSocialWorker);
        tvTabDietitian = dialog.findViewById(R.id.tvTabDietitian);
        tvTabOT = dialog.findViewById(R.id.tvTabOT);
        tvTabPharmacist = dialog.findViewById(R.id.tvTabPharmacist);
        tvTabMA = dialog.findViewById(R.id.tvTabMA);
        tvTabOM = dialog.findViewById(R.id.tvTabOM);
        tvTabSup = dialog.findViewById(R.id.tvTabSup);
        tvTabAll = dialog.findViewById(R.id.tvTabAll);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvTabNurse:
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;

                    case R.id.tvTabNursePractitioner:
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse Practitioner")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabSocialWorker:
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Social Worker")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabDietitian:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Dietitian")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabOT:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("ot") ||
                                        nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("pt")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabPharmacist:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Pharmacist")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;
                    case R.id.tvTabMA:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("MA")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabOM:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Office Manager")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabSup:
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Supervisor")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }
                        break;

                    case R.id.tvTabAll:
                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));

                        if (nurseBeensOrig !=null){
                            /*nurseBeens = new ArrayList<>();
                            for (int i = 0; i<nurseBeensOrig.size();i++){
                                if (nurseBeensOrig.get(i).doctor_category.equalsIgnoreCase("Nurse")){
                                    nurseBeens.add(nurseBeensOrig.get(i));
                                }
                            }*/
                            nurseBeens.addAll(nurseBeensOrig);
                            nurseAdapter = new NurseAdapter(activity,nurseBeens);
                            lvNurse.setAdapter(nurseAdapter);
                        }

                        break;
                    default:
                        break;
                }
            }
        };

        tvTabNurse.setOnClickListener(onClickListener);
        tvTabNursePractitioner.setOnClickListener(onClickListener);
        tvTabSocialWorker.setOnClickListener(onClickListener);
        tvTabDietitian.setOnClickListener(onClickListener);
        tvTabOT.setOnClickListener(onClickListener);
        tvTabPharmacist.setOnClickListener(onClickListener);
        tvTabMA.setOnClickListener(onClickListener);
        tvTabOM.setOnClickListener(onClickListener);
        tvTabSup.setOnClickListener(onClickListener);
        tvTabAll.setOnClickListener(onClickListener);

        lvNurse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                DATA.selectedDrId = nurseBeens.get(position).id;
                DATA.print("--onitemclick id "+DATA.selectedDrId);
                new VCallModule(activity).callingToDoctor();
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);


        if (checkInternetConnection.isConnectedToInternet()){
            find_nurse("a",DATA.selectedUserCallId,"0");
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
        }
    }

    ArrayList<NurseBean> nurseBeens;
    ArrayList<NurseBean> nurseBeensOrig;
    NurseBean bean;
    NurseAdapter nurseAdapter;
    public void find_nurse(String keyword,String patient_id,String patient_category) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);

        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("keyword", keyword);
        if(patient_id.isEmpty()){
            patient_id = "0";
        }
        params.put("patient_id", patient_id);
        params.put("patient_category", patient_category);

        DATA.print("-- params in find_nurse: "+params.toString());

        client.post(DATA.baseUrl+"find_nurse", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in find_nurse "+content);

                    try {
                        nurseBeensOrig = new ArrayList<NurseBean>();
                        nurseBeens = new ArrayList<NurseBean>();
                        JSONObject jsonObject = new JSONObject(content);
                        JSONArray nurses = jsonObject.getJSONArray("nurses");
                        for (int i = 0; i<nurses.length(); i++){
                            String id = nurses.getJSONObject(i).getString("id");
                            String first_name = nurses.getJSONObject(i).getString("first_name");
                            String last_name = nurses.getJSONObject(i).getString("last_name");
                            String image = nurses.getJSONObject(i).getString("image");
                            String doctor_category = nurses.getJSONObject(i).getString("doctor_category");
                            String is_added = "group_call";//= nurses.getJSONObject(i).getString("is_added");
                            //String rec_id = nurses.getJSONObject(i).getString("rec_id");

                            bean = new NurseBean(id,first_name,last_name,image,doctor_category, is_added);
                            if (!id.equals(prefs.getString("id",""))) {
                                //if (doctor_category.equalsIgnoreCase("Nurse")){//adding all
                                    nurseBeens.add(bean);
                                //}
                                nurseBeensOrig.add(bean);
                            }

                            bean = null;

                        }
                        nurseAdapter = new NurseAdapter(activity,nurseBeens);
                        lvNurse.setAdapter(nurseAdapter);

                        tvTabAll.setBackgroundColor(activity.getResources().getColor(R.color.theme_red));
                        tvTabNurse.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabNursePractitioner.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSocialWorker.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabPharmacist.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabMA.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOM.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSup.setBackgroundColor(activity.getResources().getColor(R.color.theme_red_opaque_60));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: find_nurse, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail find_nurse " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }



    public void callFromSpecialist() {
        DATA.showLoaderDefault(activity,"");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        }
        catch (Exception e) {
            DATA.print("-- exception ");
            e.printStackTrace();
        }

        RequestParams params = new RequestParams();
        params.put("to_doctor_id",DATA.selectedDrId );//
        params.put("from_doctor_id",prefs.getString("id", ""));
        params.put("session_id",DATA.rndSessionId);//randomSessionID

        if (DATA.selectedDoctorsModel.current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
            params.put("to_doctor_type" , "doctor");
        } else {
            params.put("to_doctor_type" , "specialist");
        }

        DATA.print("-- params in callFromSpecialist: "+params.toString());

        client.get(DATA.baseUrl+"callFromSpecialist", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--Response in callFromSpecialist : "+content);

                    try {
                        //
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
						/*if (doctorsDialog != null && doctorsDialog.isShowing()) {
							doctorsDialog.dismiss();
						}*/


                        }else if (status.equals("error")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity).setTitle("Info").setMessage(msg).
                                    setPositiveButton("OK", null);
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
                        } else {
                            //outgoingCallLayout.setVisibility(View.GONE);
                            Toast.makeText(activity, "status not success", Toast.LENGTH_SHORT).show();
                            //finish();
                        }

                    } catch (JSONException e) {
                        DATA.print("-- Exception in callFromSpecialist: "+e);
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: callFromSpecialist, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfailure callFromSpecialist: "+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });
    }//end callFromSpecialist



    /*public void emscallThreeWay() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("session_id",DATA.rndSessionId);

        client.post(DATA.baseUrl+"ems/emscall", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--reaponce in emscall: "+content);
                // --reaponce in emscall: {"success":1,"data":{"call_id":"607181533","increment_id":42}}
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int  success = jsonObject.getInt("success");

                    if(success == 1){
                        //Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");
                        //int increment_id = jsonObject.getJSONObject("data").getInt("increment_id");
                        String ems_id = jsonObject.getJSONObject("data").getString("ems_id");

                        DATA.selectedDrId = ems_id;//test well plz
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--onfail createfollowup: " +content);
                Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void emscall() {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("patient_id", ActivityFollowUps_4.selectedFollowupBean.patient_id);


        client.post(DATA.baseUrl+"ems/emscall", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--reaponce in emscall: "+content);
                // --reaponce in emscall: {"success":1,"data":{"call_id":"607181533","increment_id":42}}
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int  success = jsonObject.getInt("success");

                    if(success == 1){
                        Constants.ROOM_NAME_MULTI = jsonObject.getJSONObject("data").getString("call_id");
                        int increment_id = jsonObject.getJSONObject("data").getInt("increment_id");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                DATA.dismissLoaderDefault();
                DATA.print("--onfail createfollowup: " +content);
                Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
            }
        });

    }*/
}
