package com.app.mdlive_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_cp.adapters.CategoriesAdapter;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.CategoriesModel;
import com.app.mdlive_cp.permission.PermissionsActivity;
import com.app.mdlive_cp.permission.PermissionsChecker;
import com.app.mdlive_cp.reliance.mdlive.ActivityCompanyDoctors;
import com.app.mdlive_cp.reliance.therapist.ActivityReferedPatients;
import com.app.mdlive_cp.util.ActionEditText;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.Database;
import com.app.mdlive_cp.util.DialogPatientInfo;
import com.app.mdlive_cp.util.GPSTracker;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.GloabalSocket;
import com.app.mdlive_cp.util.OpenActivity;
import com.app.mdlive_cp.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import me.leolin.shortcutbadger.ShortcutBadger;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class MainActivityNew extends AppCompatActivity implements GloabalSocket.SocketEmitterCallBack{

    private DrawerLayout mDrawerLayout;
    private ListView lvCategories;
    CharSequence mTitle = "";
    private ActionBarDrawerToggle mDrawerToggle;
    RelativeLayout mDrawer;

    TextView tvSearchDoctor,tvMyDoctors,tvMessages,tvTCM,tvComplexCare,tvHomeHealth,
            tvNursingHome,tvMyProfile,tvLogout,tvSupport,tvCPs,tvFollowups,tvNurseAppointments,tvGroupChat,tvEMS,
            tvPendingNotes,tvSoapRef,tvDrSchedule,tvAppointments,
            tvMyPatients,tvLiveCare,tvReffredPt,
            tvInstantConnect,tvAppInvite, tvCallHistory;
    TextView tvMessagesBadge,tvLiveCareBadge,tvGroupsBadge,tvSoapRefBadge,tvAppointmentBadge;
    Button btnOffline;
    public static final String TAB_TCM = "tcm",TAB_CC = "complex_care",TAB_HH = "home_health",TAB_NH = "nursing_home",TAB_ALL = "all";//,TAB_KEY = "tab_key";

    LinearLayout layMainCP,layMainSup;

    LinearLayout layPermissions;
    Button btnReviewPerm;

    ProgressDialog pd;
    Activity activity;
    public static MainActivityNew mainActivityNew;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    Database db;


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
        } else {
            super.onBackPressed();
        }

    }
    public void setBadge(Database database){//Activity activity
        //Database database = new Database(activity);
        ArrayList<String> allNotifications = db.getAllNotif();
        if(allNotifications != null){
            if(allNotifications.size() > 0){
                int badgeCount = allNotifications.size();
                ShortcutBadger.applyCount(this, badgeCount);
            }else{
                ShortcutBadger.removeCount(activity);
            }
        }else {
            ShortcutBadger.removeCount(activity);
        }
        final ArrayList<String> messages = database.getAllNotifByType(DATA.NOTIF_TYPE_MESSAGE);
        final ArrayList<String> liveCares = database.getAllNotifByType(DATA.NOTIF_TYPE_NEW_PATIENT);
        final ArrayList<String> groupMessages = database.getAllNotifByType(DATA.NOTIF_TYPE_GROUP_MESSAGE);
        final ArrayList<String> serviceReferrals = database.getAllNotifByType(DATA.NOTIF_TYPE_SERVICE_REFERRAL_REQ);
        final ArrayList<String> appointments = database.getAllNotifByType(DATA.NOTIF_TYPE_NEW_APPOINTMENT);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(tvMessagesBadge!=null){
                    if(messages!= null){
                        System.out.println("--messages size "+messages.size());
                        if(messages.size() > 0){
                            tvMessagesBadge.setText(messages.size() + "");
                            //badge.setTextSize(12);
                            tvMessagesBadge.setVisibility(View.VISIBLE);
                        }else {
                            tvMessagesBadge.setVisibility(View.GONE);
                        }
                    }else {
                        tvMessagesBadge.setVisibility(View.GONE);
                    }
                }

                if(tvLiveCareBadge!=null){
                    if(liveCares!= null){
                        System.out.println("--liveCares size "+liveCares.size());
                        if(liveCares.size() > 0){
                            tvLiveCareBadge.setText(liveCares.size() + "");
                            //badge.setTextSize(12);
                            tvLiveCareBadge.setVisibility(View.VISIBLE);
                        }else {
                            tvLiveCareBadge.setVisibility(View.GONE);
                        }
                    }else {
                        tvLiveCareBadge.setVisibility(View.GONE);
                    }
                }


                if(tvGroupsBadge!=null){
                    if(groupMessages!= null){
                        System.out.println("--groupMessages size "+groupMessages.size());
                        if(groupMessages.size() > 0){
                            tvGroupsBadge.setText(groupMessages.size() + "");
                            //badge.setTextSize(12);
                            tvGroupsBadge.setVisibility(View.VISIBLE);
                        }else {
                            tvGroupsBadge.setVisibility(View.GONE);
                        }
                    }else {
                        tvGroupsBadge.setVisibility(View.GONE);
                    }
                }

                if(tvSoapRefBadge!=null){
                    if(serviceReferrals!= null){
                        System.out.println("--serviceReferrals size "+serviceReferrals.size());
                        if(serviceReferrals.size() > 0){
                            tvSoapRefBadge.setText(serviceReferrals.size() + "");
                            //badge.setTextSize(12);
                            tvSoapRefBadge.setVisibility(View.VISIBLE);
                        }else {
                            tvSoapRefBadge.setVisibility(View.GONE);
                        }
                    }else {
                        tvSoapRefBadge.setVisibility(View.GONE);
                    }
                }

                if(tvAppointmentBadge!=null){
                    if(appointments!= null){
                        System.out.println("--appointments size "+appointments.size());
                        if(appointments.size() > 0){
                            tvAppointmentBadge.setText(appointments.size() + "");
                            //badge.setTextSize(12);
                            tvAppointmentBadge.setVisibility(View.VISIBLE);
                        }else {
                            tvAppointmentBadge.setVisibility(View.GONE);
                        }
                    }else {
                        tvAppointmentBadge.setVisibility(View.GONE);
                    }
                }
            }
        });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setBadge(db);
        DATA.visit_start_time = "";
        DATA.visit_end_time = "";
        DATA.call_start_time = "";
        DATA.call_end_time = "";
        DATA.virtual_visit_id = "";
        DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        DATA.elivecare_end_time = "";

        DATA.selectedUserCallId = "";
        DialogPatientInfo.patientIdGCM = "";
        ActivityTcmDetails.primary_patient_id = "";
        ActivityTcmDetails.family_is_online = "";

        GloabalMethods.faxId = "";

        DATA.isVideoCallFromLiveCare = false;

        layPermissions.setVisibility(new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS) ? View.VISIBLE:View.GONE);

        /*
         * Intent intent1 = new Intent();
         * intent1.setAction("com.app.onlinecarespecialist.START_SERVICE");
         * activity.sendBroadcast(intent1);
         */

    }

    GloabalSocket gloabalSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        activity = MainActivityNew.this;
        mainActivityNew = MainActivityNew.this;
        db = new Database(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        // exportDB();
        customToast = new CustomToast(activity);

        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        gloabalSocket = new GloabalSocket(activity,this);
        JSONObject data = new JSONObject();
        try {
            data.put("id",prefs.getString("id",""));
            data.put("usertype","doctor");
            data.put("status","login");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //mSocket.emit("webmsg",data);
        gloabalSocket.emitSocket(data);

        StringBuilder sb = new StringBuilder();
        String fName = prefs.getString("first_name", "");
        String lName = prefs.getString("last_name", "");
        if(fName.length() > 0){
            sb.append(fName.charAt(0));
            sb.append(" ");
        }
        if(lName.length() > 0){
            sb.append(lName.charAt(0));
        }

        Toolbar mToolbar = findViewById(R.id.toolbar);
        // mToolbar.setLogo(R.drawable.ic_launcher);
        //mToolbar.setTitle(getResources().getString(R.string.app_name));
        mToolbar.setTitle("OLC "+prefs.getString("doctor_category","")+" "+sb.toString().toUpperCase());
        setSupportActionBar(mToolbar);

        openActivity = new OpenActivity(activity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            pd = new ProgressDialog(activity,
                    ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        } else {
            pd = new ProgressDialog(activity);
        }

        pd.setMessage("Submitting...");
        pd.setCanceledOnTouchOutside(false);



        mDrawer = findViewById(R.id.left_drawe);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        lvCategories = findViewById(R.id.lvCategories);

        mTitle = getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // mDrawerLayout.setDrawerListener(mDrawerToggle);
        lvCategories.setOnItemClickListener(new DrawerItemClickListener());

        // getActionBar().setDisplayHomeAsUpEnabled(true);


        layMainCP = findViewById(R.id.layMainCP);
        layMainSup = findViewById(R.id.layMainSup);

        DATA.allCategories = new ArrayList<CategoriesModel>();

        if(prefs.getString("doctor_category","").equalsIgnoreCase("Supervisor")){
            layMainCP.setVisibility(View.GONE);
            layMainSup.setVisibility(View.VISIBLE);

            DATA.allCategories.add(new CategoriesModel("1", "Connect to CP",R.drawable.ic_drawer_nursing));
            DATA.allCategories.add(new CategoriesModel("2", "My Messages",R.drawable.ic_drawer_my_messages));
            DATA.allCategories.add(new CategoriesModel("3", "Group Chat",R.drawable.ic_drawer_group_chat));
            DATA.allCategories.add(new CategoriesModel("4", "My Profile",R.drawable.ic_drawer_my_profile));
            DATA.allCategories.add(new CategoriesModel("5", "Logout",R.drawable.ic_drawer_logout));

            tvCPs = findViewById(R.id.tvCPs1);
            tvMessages = findViewById(R.id.tvMessages1);
            tvGroupChat = findViewById(R.id.tvGroupChat1);
            tvMyProfile = findViewById(R.id.tvMyProfile1);
            tvLogout = findViewById(R.id.tvLogout1);

            tvMessagesBadge = findViewById(R.id.tvMessagesBadge1);
            tvGroupsBadge = findViewById(R.id.tvGroupsBadge1);

        }else {
            layMainCP.setVisibility(View.VISIBLE);
            layMainSup.setVisibility(View.GONE);

            //DATA.allCategories.add(new CategoriesModel("1", "Search Doctors",R.drawable.ic_drawer_doc_to_doc));
			/*DATA.allCategories.add(new CategoriesModel("1", "My Doctors",R.drawable.ic_drawer_live_care));
			DATA.allCategories.add(new CategoriesModel("2", "My Patients",R.drawable.ic_drawer_mypatients));
			DATA.allCategories.add(new CategoriesModel("3", "Connect to CP",R.drawable.ic_drawer_nursing));
			DATA.allCategories.add(new CategoriesModel("4", "My Messages",R.drawable.ic_drawer_my_messages));
			DATA.allCategories.add(new CategoriesModel("5", "Group Chat",R.drawable.ic_drawer_group_chat));
			DATA.allCategories.add(new CategoriesModel("6", "Followups",R.drawable.ic_drawer_dr_schedule));
			DATA.allCategories.add(new CategoriesModel("7", "My Schedules",R.drawable.ic_drawer_my_appointment));
			DATA.allCategories.add(new CategoriesModel("8", "NEMT",R.drawable.ic_drawer_ems));
			DATA.allCategories.add(new CategoriesModel("9", "Support",R.drawable.ic_drawer_support));
            DATA.allCategories.add(new CategoriesModel("10", "Service Referral",R.drawable.ic_drawer_service_ref));
            DATA.allCategories.add(new CategoriesModel("11", "Pending Notes",R.drawable.icon_drawer_reports));
			DATA.allCategories.add(new CategoriesModel("12", "My Profile",R.drawable.ic_drawer_my_profile));
			DATA.allCategories.add(new CategoriesModel("13", "Immediate Care",R.drawable.ic_drawer_elivecare_n));
			DATA.allCategories.add(new CategoriesModel("14", "My Appointments",R.drawable.icon_drawer_appntmnts));
			DATA.allCategories.add(new CategoriesModel("15", "Time Schedule",R.drawable.ic_drawer_dr_schedule));
			DATA.allCategories.add(new CategoriesModel("16", "Referred Patients",R.drawable.ic_drawer_ref_pt));
			DATA.allCategories.add(new CategoriesModel("17", "Logout",R.drawable.ic_drawer_logout));*/

            //MdsLive CP
            //DATA.allCategories.add(new CategoriesModel("1", "Search Doctors",R.drawable.ic_drawer_doc_to_doc));
            //DATA.allCategories.add(new CategoriesModel("1", "My Doctors",R.drawable.ic_drawer_live_care));
            DATA.allCategories.add(new CategoriesModel("2", "Nursing Home",R.drawable.ic_drawer_nursing_home));
            DATA.allCategories.add(new CategoriesModel("3", "Connect to CP",R.drawable.ic_drawer_cp_to_cp));
            DATA.allCategories.add(new CategoriesModel("6", "Followups",R.drawable.ic_drawer_dr_schedule));
            DATA.allCategories.add(new CategoriesModel("11", "Pending Notes",R.drawable.icon_drawer_reports));
            DATA.allCategories.add(new CategoriesModel("10", "Service Referral",R.drawable.ic_drawer_service_ref));
            DATA.allCategories.add(new CategoriesModel("7", "My Schedules",R.drawable.ic_drawer_my_appointment));
            DATA.allCategories.add(new CategoriesModel("4", "My Messages",R.drawable.ic_drawer_my_messages));
            DATA.allCategories.add(new CategoriesModel("5", "Group Chat",R.drawable.ic_drawer_group_chat));
            DATA.allCategories.add(new CategoriesModel("12", "My Profile",R.drawable.ic_drawer_my_profile));
            DATA.allCategories.add(new CategoriesModel("22", "Instant Connect",R.drawable.ic_drawer_instant_connect));
            DATA.allCategories.add(new CategoriesModel("23", "App Invite",R.drawable.ic_drawer_app_invite));
            DATA.allCategories.add(new CategoriesModel("9", "Support",R.drawable.ic_drawer_support));
            DATA.allCategories.add(new CategoriesModel("13", "Logout",R.drawable.ic_drawer_logout));
            //DATA.allCategories.add(new CategoriesModel("8", "NEMT",R.drawable.ic_drawer_ems));

            tvCPs = findViewById(R.id.tvCPs);
            tvMessages = findViewById(R.id.tvMessages);
            tvGroupChat = findViewById(R.id.tvGroupChat);
            tvMyProfile = findViewById(R.id.tvMyProfile);
            tvLogout = findViewById(R.id.tvLogout);

            tvMessagesBadge = findViewById(R.id.tvMessagesBadge);
            tvGroupsBadge = findViewById(R.id.tvGroupsBadge);
        }

        lvCategories.setAdapter(new CategoriesAdapter(activity));

        tvSearchDoctor = findViewById(R.id.tvSearchDoctor);
        tvMyDoctors = findViewById(R.id.tvMyDoctors);
        tvTCM = findViewById(R.id.tvTCM);
        tvComplexCare = findViewById(R.id.tvComplexCare);
        tvHomeHealth = findViewById(R.id.tvHomeHealth);
        tvNursingHome = findViewById(R.id.tvNursingHome);
        tvSupport = findViewById(R.id.tvSupport);
        tvFollowups = findViewById(R.id.tvFollowups);
        tvNurseAppointments = findViewById(R.id.tvNurseAppointments);
        tvEMS = findViewById(R.id.tvEMS);
        tvPendingNotes = findViewById(R.id.tvPendingNotes);
        tvSoapRef = findViewById(R.id.tvSoapRef);
        tvDrSchedule = findViewById(R.id.tvDrSchedule);
        tvAppointments = findViewById(R.id.tvAppointments);
        tvMyPatients = findViewById(R.id.tvMyPatients);
        tvLiveCare = findViewById(R.id.tvLiveCare);
        tvReffredPt = findViewById(R.id.tvReffredPt);
        tvInstantConnect = findViewById(R.id.tvInstantConnect);
        tvAppInvite = findViewById(R.id.tvAppInvite);
        tvCallHistory = findViewById(R.id.tvCallHistory);

        //tvMessagesBadge = (TextView) findViewById(R.id.tvMessagesBadge);
        tvLiveCareBadge = findViewById(R.id.tvLiveCareBadge);
        //tvGroupsBadge = (TextView) findViewById(R.id.tvGroupsBadge);
        tvSoapRefBadge = findViewById(R.id.tvSoapRefBadge);
        tvAppointmentBadge = findViewById(R.id.tvAppointmentBadge);

        btnOffline = findViewById(R.id.btnOffline);

        btnOffline.setVisibility(View.GONE);//for mdslive

        String vacation_mode = prefs.getString("vacation_mode","0");
        if (vacation_mode.equals("1")) {
            btnOffline.setText("Offline");
            btnOffline.setBackgroundResource(R.drawable.btn_selector);
            showOfflineDialog();
        }else {
            btnOffline.setText("Online");
            btnOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
        }
        btnOffline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showOfflineDialog();
            }
        });



        tvCallHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityCallLogs.class, false);
            }
        });
        tvInstantConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityInstatntConnect.class,false);
            }
        });

        tvAppInvite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAppInvite.class, false);
            }
        });

        tvReffredPt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityReferedPatients.class,false);
            }
        });
        tvLiveCare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(LiveCare.class, false);
            }
        });

        tvMyPatients.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //openActivity.open(ActivityCompany.class, false);
                openActivity.open(ActivityCompanyDoctors.class, false);//MdsLive
                //openActivity.open(ActivityMyPatients.class, false);//GreatLakes
            }
        });
        tvLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog d = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Logout").setMessage("Are you sure? You want to logout.").
                        setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                if (checkInternetConnection.isConnectedToInternet()) {
                                    logout();
                                } else {
                                    Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                }).create();
                d.show();

            }
        });

        tvSoapRef.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
                    openActivity.open(ActivitySOAPReferralOT.class,false);
                }else {
                    openActivity.open(ActivitySOAPReferral.class,false);
                }
            }
        });
        tvEMS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //openActivity.open(ActivityFollowUps_4.class, false);
                emsDirectCall();
            }
        });
        tvDrSchedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(DoctorScheduleNew.class,false);
            }
        });
        tvAppointments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(DrsAppointments.class,false);
            }
        });
        tvGroupChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityGroupChat.class, false);
            }
        });
        tvPendingNotes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivitySOAP_Pending.class, false);
            }
        });
        tvNurseAppointments.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                openActivity.open(ActivityFollowUps_3.class, false);
            }
        });
        tvFollowups.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowupDialog();
            }
        });
        tvMyProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(UpdateProfile.class, false);
            }
        });

        tvCPs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityCareProviders.class, false);
            }
        });

        tvMessages.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ConversationsActivity.class, false);
            }
        });

        tvSearchDoctor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(DoctorsList.class, false);
            }
        });
        tvMyDoctors.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,DoctorsList.class);
                i.putExtra("isFromMyDoctors", true);
                startActivity(i);
            }
        });
        tvTCM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,DoctorsList.class);
                i.putExtra("isFromMyDoctors", true);
                DATA.selectedTabFromMain = TAB_TCM;
                startActivity(i);
            }
        });
        tvComplexCare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				/*Intent intent = new Intent(activity,ActivityTCM.class);
				intent.putExtra(TAB_KEY,TAB_CC);
				startActivity(intent);*/

                Intent i = new Intent(activity,DoctorsList.class);
                i.putExtra("isFromMyDoctors", true);
                DATA.selectedTabFromMain = TAB_CC;
                startActivity(i);
            }
        });
        tvHomeHealth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				/*Intent intent = new Intent(activity,ActivityTCM.class);
				intent.putExtra(TAB_KEY,TAB_HH);
				startActivity(intent);*/
                Intent i = new Intent(activity,DoctorsList.class);
                i.putExtra("isFromMyDoctors", true);
                DATA.selectedTabFromMain = TAB_HH;
                startActivity(i);
            }
        });
        tvNursingHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				/*Intent intent = new Intent(activity,ActivityTCM.class);
				intent.putExtra(TAB_KEY,TAB_NH);
				startActivity(intent);*/
                Intent i = new Intent(activity,DoctorsList.class);
                i.putExtra("isFromMyDoctors", true);
                DATA.selectedTabFromMain = TAB_NH;
                startActivity(i);
            }
        });

        tvSupport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showSupportDialog();
            }
        });

        // ==============================================GCM==========================================================

		/*if (new GloabalMethods(activity).checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }*/
        // ===========================================GCM=============================================================

        //==================FCM=========================
        GloabalMethods gloabalMethods = new GloabalMethods(activity);
        gloabalMethods.getFirebaseToken();

        //------------Create notifications channel for android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.app_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        //=================FCM==========================

        ImageView imgAdMain = findViewById(R.id.imgAdMain);
        imgAdMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DATA.addIntent(activity);
            }
        });


        if (checkInternetConnection.isConnectedToInternet()) {
            gloabalMethods.getSpecialties();
            gloabalMethods.loadSymtomsConditions();

            gloabalMethods.getAllCompany();//All Companies
            //gloabalMethods.getCompany();//companies in which CP assigned

            gloabalMethods.loadAppLabels();


            layPermissions = findViewById(R.id.layPermissions);
            btnReviewPerm = findViewById(R.id.btnReviewPerm);
            btnReviewPerm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivity.open(PermissionsActivity.class, false);
                }
            });
        }
    }// end oncreate

    @Override
    protected void onDestroy() {

        gloabalSocket.offSocket();

        super.onDestroy();
    }

    @Override
    public void onSocketCallBack(String emitterResponse) {

    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            if(prefs.getString("doctor_category","").equalsIgnoreCase("Supervisor")){
                switch (position) {
                    case 0:
                        openActivity.open(ActivityCareProviders.class, false);
                        break;
                    case 1:
                        openActivity.open(ConversationsActivity.class,false);
                        break;
                    case 2:
                        openActivity.open(ActivityGroupChat.class, false);
                        break;
                    case 3:
                        openActivity.open(UpdateProfile.class, false);
                        break;
                    case 4:
                        AlertDialog d = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Logout").setMessage("Are you sure? You want to logout.").
                                setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        if (checkInternetConnection.isConnectedToInternet()) {
                                            logout();
                                        } else {
                                            Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub

                            }
                        }).create();
                        d.show();
                        break;

                    default:
                        break;
                }
            }else {
                /*switch (position) {
                 *//*case 0:
				openActivity.open(DoctorsList.class, false);
				break;*//*
					case 0:
						Intent i = new Intent(activity,DoctorsList.class);
						i.putExtra("isFromMyDoctors", true);
						startActivity(i);
						break;
					case 1:
                        //openActivity.open(ActivityCompany.class, false);//MHCSN-Reliance
						openActivity.open(ActivityCompanyDoctors.class, false);//MdsLive
						//openActivity.open(ActivityMyPatients.class, false);//GreatLakes
						break;
					case 2:
						openActivity.open(ActivityCareProviders.class, false);
						break;
					case 3:
						openActivity.open(ConversationsActivity.class,false);

						break;
					case 4:
						openActivity.open(ActivityGroupChat.class, false);
						break;
					case 5:
						//openActivity.open(ActivityFollowUps.class, false);
						showFollowupDialog();
						break;
					case 6:
						openActivity.open(ActivityFollowUps_3.class, false);
						break;
					case 7:
						emsDirectCall();
						break;
					case 8:
						showSupportDialog();
						break;
                    case 9:
                        if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
                            openActivity.open(ActivitySOAPReferralOT.class,false);
                        }else {
                            openActivity.open(ActivitySOAPReferral.class,false);
                        }
                        break;
                    case 10:
                        openActivity.open(ActivitySOAP_Pending.class, false);
                        break;
					case 11:
						openActivity.open(UpdateProfile.class, false);
						break;
					case 12:
						tvLiveCare.performClick();
						break;
					case 13:
						tvAppointments.performClick();
						break;
					case 14:
						tvDrSchedule.performClick();
						break;
					case 15:
						tvReffredPt.performClick();
						break;
					case 17:
						AlertDialog d = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Logout").setMessage("Are you sure? You want to logout.").
								setPositiveButton("Logout", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										if (checkInternetConnection.isConnectedToInternet()) {
											logout();
										} else {
											Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
										}
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

							}
						}).create();
						d.show();
						break;

					default:
						break;
				}*/

                switch (position) {
			/*case 0:
				openActivity.open(DoctorsList.class, false);
				break;*/
					/*case 0:
						Intent i = new Intent(activity,DoctorsList.class);
						i.putExtra("isFromMyDoctors", true);
						startActivity(i);
						break;*/
                    case 0:
                        //openActivity.open(ActivityCompany.class, false);
                        openActivity.open(ActivityCompanyDoctors.class, false);
                        break;
                    case 1:
                        openActivity.open(ActivityCareProviders.class, false);
                        break;
                    case 2:
                        //openActivity.open(ActivityFollowUps.class, false);
                        showFollowupDialog();
                        break;
                    case 3:
                        openActivity.open(ActivitySOAP_Pending.class, false);
                        break;
                    case 4:
                        if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
                            openActivity.open(ActivitySOAPReferralOT.class,false);
                        }else {
                            openActivity.open(ActivitySOAPReferral.class,false);
                        }
                        break;
                    case 5:
                        openActivity.open(ActivityFollowUps_3.class, false);
                        break;
                    case 6:
                        openActivity.open(ConversationsActivity.class,false);
                        break;
                    case 7:
                        openActivity.open(ActivityGroupChat.class, false);
                        break;
                    case 8:
                        openActivity.open(UpdateProfile.class, false);
                        break;
                    case 9:
                        openActivity.open(ActivityInstatntConnect.class, false);
                        break;
                    case 10:
                        openActivity.open(ActivityAppInvite.class, false);
                        break;
                    case 11:
                        showSupportDialog();
                        break;
                    case 12:
                        AlertDialog d = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Logout").setMessage("Are you sure? You want to logout.").
                                setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        if (checkInternetConnection.isConnectedToInternet()) {
                                            logout();
                                        } else {
                                            Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub

                            }
                        }).create();
                        d.show();
                        break;
                    /*case 13:
                        emsDirectCall();
                        break;*/



                    default:
                        break;
                }
            }

            lvCategories.setItemChecked(position, true);

            // setTitle(patientArry[position]);

            mDrawerLayout.closeDrawer(mDrawer);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        // menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);

    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/



    public void logout() {
        pd.setMessage("Please wait...    ");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("id", prefs.getString("id", "0"));
        params.put("type", "doctor");

        client.post(DATA.baseUrl + "/logout", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in logout" + content);
                    // 01-06 22:09:54.586: I/System.out(2570): --reaponce in
                    // logout{"status":"success","msg":"Successfully Logged Out"}
                    try {
                        JSONObject jsonObject = new JSONObject(content);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject data = new JSONObject();
                            try {
                                data.put("id",prefs.getString("id",""));
                                data.put("usertype","doctor");
                                data.put("status","logout");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //mSocket.emit("webmsg",data);
                            gloabalSocket.emitSocket(data);

                            SharedPreferences.Editor ed = prefs.edit();
                            ed.putBoolean("HaveShownPrefs", false);
                            ed.putBoolean("subUserSelected", false);
                            ed.commit();

                            ed.clear();
                            ed.apply();
                            SharedPrefsHelper.getInstance().clearAllData();

                            //openActivity.open(Login.class, true);
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            /*
                             * Intent intent = new Intent();
                             * intent.setAction
                             * ("com.app.onlinecare.STOP_SERVICE");
                             * sendBroadcast(intent);
                             *
                             * SharedPreferences.Editor ed = prefs.edit();
                             * ed.putBoolean("HaveShownPrefs", false);
                             * ed.putBoolean("qbcalledonce", false);
                             * ed.commit();
                             *
                             * openActivity.open(Login.class, true);
                             */

                        } else {
                            customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                        }
                    } catch (JSONException e) {
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: logout, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.dismiss();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail logout: " + content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }// end logout



    public void vacation_mode(final String vacation_mode, String auto_message) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("id", prefs.getString("id", "0"));
        params.put("user_type", "doctor");
        params.put("vacation_mode", vacation_mode);
        params.put("auto_message", auto_message);

        client.post(DATA.baseUrl+"vacation_mode", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    System.out.println("--reaponce in vacation_mode: "+content);
                    //reaponce in vacation_mode: {"success":1,"message":"Saved"}
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.has("success")) {
                            prefs.edit().putString("vacation_mode",vacation_mode).commit();
                            if (vacation_mode.equals("1")) {
                                btnOffline.setText("Offline");
                                btnOffline.setBackgroundResource(R.drawable.btn_selector);
                                btnDialogOffline.setText("Go Online");
                                btnDialogOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
                                //ivCloseDialog.setVisibility(View.GONE);
                                etOfflineMessage.setVisibility(View.GONE);
                                tvDialogLabel.setText("You are in offline mode.");

                            }else {
                                btnOffline.setText("Online");
                                btnOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
                                btnDialogOffline.setText("Save and go offline");
                                btnDialogOffline.setBackgroundResource(R.drawable.btn_selector);
                                //ivCloseDialog.setVisibility(View.VISIBLE);
                                etOfflineMessage.setVisibility(View.VISIBLE);
                                tvDialogLabel.setText(getString(R.string.offline_label_txt));
                                dialog.dismiss();
                            }
                        }else {
                            customToast.showToast(jsonObject.getString("message"),0,1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: vacation_mode, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    System.out.println("--onfail vacation_mode: " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
    Dialog dialog;
    Button btnDialogOffline;
    ImageView ivCloseDialog;
    ActionEditText etOfflineMessage;
    TextView tvDialogLabel;
    public void showOfflineDialog(){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_onoff);
        dialog.setCancelable(false);

        etOfflineMessage = dialog.findViewById(R.id.etOfflineMessage);
        btnDialogOffline = dialog.findViewById(R.id.btnDialogOffline);
        ivCloseDialog = dialog.findViewById(R.id.ivCloseDialog);
        tvDialogLabel = dialog.findViewById(R.id.tvDialogLabel);

        if (prefs.getString("vacation_mode","0").equals("1")) {
            btnOffline.setText("Offline");
            btnOffline.setBackgroundResource(R.drawable.btn_selector);
            btnDialogOffline.setText("Go Online");
            btnDialogOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
            //ivCloseDialog.setVisibility(View.GONE);
            etOfflineMessage.setVisibility(View.GONE);
            tvDialogLabel.setText("You are in offline mode.");
        }else {
            btnOffline.setText("Online");
            btnOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
            btnDialogOffline.setText("Save and go offline");
            btnDialogOffline.setBackgroundResource(R.drawable.btn_selector);
            //ivCloseDialog.setVisibility(View.VISIBLE);
            etOfflineMessage.setVisibility(View.VISIBLE);
            tvDialogLabel.setText(getString(R.string.offline_label_txt));
        }
        btnDialogOffline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetConnection.isConnectedToInternet()) {
                    if (prefs.getString("vacation_mode","0").equals("1")) {
                        vacation_mode("0",etOfflineMessage.getText().toString());
                    }else{
                        if (!etOfflineMessage.getText().toString().isEmpty()) {
                            vacation_mode("1",etOfflineMessage.getText().toString());
                        }else {
                            etOfflineMessage.setError("Please type a message to go offline. Anyone who wants to contact you from OnlineCare will get your message.");
                        }

                    }
                }else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
                }

            }
        });
        ivCloseDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getString("vacation_mode","0").equals("1")) {
                    finish();
                }else {
                    dialog.dismiss();
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    final String SUPPORT_MESSAGE = "We are currently available from 9am to 5pm live on our website at www.onlinecare.com - You may also email us at: support@onlinecare.com";
    final String SUPPORT_EMAIL = "support@onlinecare.com";
    String support_email = SUPPORT_EMAIL;
    public void showSupportDialog(){

        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_support);

        Button btnTextSupport = dialogSupport.findViewById(R.id.btnTextSupport);
        Button btnCallSupport = dialogSupport.findViewById(R.id.btnCallSupport);

        TextView tvMsg = dialogSupport.findViewById(R.id.tvMsg);

        String support_text = prefs.getString("support_text", "");
        if(TextUtils.isEmpty(support_text)){
            support_text = SUPPORT_MESSAGE;
        }
        tvMsg.setText(support_text);

        support_email = prefs.getString("support_email", "");
        if(TextUtils.isEmpty(support_email)){
            support_email = SUPPORT_EMAIL;
        }

        btnTextSupport.setText(support_email);

        btnTextSupport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //openActivity.open(SupportMessagesActivity.class, false);

                String subject = getString(R.string.app_name)+" customer support";
                String[] addresses = {support_email};//"support@onlinecare.com"

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",support_email, null));//"support@onlinecare.com"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });
        btnCallSupport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                DATA.incomingCall = false;
                Intent i = new Intent(activity,MainActivity.class);
                i.putExtra("isFromCallToCoordinator", true);
                startActivity(i);
                //customToast.showToast("Under Development",0,1);
            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

    }

    public void emsDirectCall(){
        GPSTracker gpsTracker = new GPSTracker(activity);
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
            return;
        }
        new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("NEMT")
                .setMessage("Do you want to connect to the NEMT Command Center ?")
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityFollowUps_4.selectedFollowupBean = null;
                        DATA.incomingCall = false;
                        Intent i = new Intent(activity,MainActivity.class);
                        i.putExtra("isFromCallToEMS", true);
                        startActivity(i);
                    }
                }).setNegativeButton("Cancel",null).create().show();
    }


    //=========Followups dialog============
    public void showFollowupDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_followup);
        dialogSupport.setCancelable(false);

        Button btnScheduleFollowup = dialogSupport.findViewById(R.id.btnScheduleFollowup);
        Button btnScheduleCareVisit = dialogSupport.findViewById(R.id.btnScheduleCareVisit);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        btnScheduleFollowup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                openActivity.open(ActivityFollowUps.class, false);
            }
        });
        btnScheduleCareVisit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                openActivity.open(ActivityCareVisit.class,false);
                //activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }
}
