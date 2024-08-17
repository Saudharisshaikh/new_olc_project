package com.app.msu_dr;

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
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.msu_dr.R;
import com.app.msu_dr.adapter.CategoriesAdapter;
import com.app.msu_dr.api.ApiCallBack;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.CategoriesModel;
import com.app.msu_dr.permission.PermissionsActivity;
import com.app.msu_dr.permission.PermissionsChecker;
import com.app.msu_dr.reliance.therapist.ActivityReferedPatients;
import com.app.msu_dr.services.GPSTracker;
import com.app.msu_dr.util.ActionEditText;
import com.app.msu_dr.util.CallWebService;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.Database;
import com.app.msu_dr.util.DialogPatientInfo;
import com.app.msu_dr.util.GloabalMethods;
import com.app.msu_dr.util.GloabalSocket;
import com.app.msu_dr.util.HideShowKeypad;
import com.app.msu_dr.util.OpenActivity;
import com.app.msu_dr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class MainActivityNew extends AppCompatActivity implements ApiCallBack, GloabalSocket.SocketEmitterCallBack{

	private DrawerLayout mDrawerLayout;
	private ListView lvCategories;
	CharSequence mTitle = "";
	private ActionBarDrawerToggle mDrawerToggle;
	Toolbar	mToolbar;
	CustomToast customToast;
	OpenActivity openActivity;
	CheckInternetConnection checkInternetConnection;
	CallWebService callWebService;
	HideShowKeypad hideShowKeypad;

	LinearLayout tvLiveCare;
	TextView  tvAppointments, tvDrProfile, tvLogout,tvDrSchedule,
			tvDoctoDoc,tvHomeHealth,tvPresscriptions,tvMessages,tvTransactions,
			tvRatting,tvRefills,tvTCM,tvBilling,tvSupport,tvGroups,tvAddDoctor,tvEMS,tvSoapRef,tvMyPatients,tvRefrals,
			tvInstantConnect,tvAppInvite,tvReffredPt;
	TextView tvMessagesBadge,tvLiveCareBadge,tvAppointmentBadge,tvPrescriptionsBadge,tvRefillsBadge,
			tvGroupsBadge,tvAddDoctorBadge,tvSoapRefBadge;
	CheckBox cbAvailableForeLiveCare;
	String is_available_onlinecare = "0";
	Button btnOffline;
	//boolean isOnline = true;

	LinearLayout layPermissions;
	Button btnReviewPerm,btnExitApp;

	ProgressDialog pd;
	CategoriesAdapter categoriesAdapter;
	SharedPreferences prefs;
	RelativeLayout mDrawer;
	Activity activity;
	public static MainActivityNew mainActivityNew;
	Database db;
	ApiCallBack apiCallBack;


	public void setBadge(Database database){//Activity activity
		//Database database = new Database(activity);
		/*ArrayList<String> allNotifications = db.getAllNotif();

		DATA.print("-- allNotifications : "+allNotifications);

		if(allNotifications != null){
			if(allNotifications.size() > 0){
				int badgeCount = allNotifications.size();
				ShortcutBadger.applyCount(this, badgeCount);
			}else{
				ShortcutBadger.removeCount(activity);
			}
		}else {
			ShortcutBadger.removeCount(activity);
		}*/
		final ArrayList<String> messages = database.getAllNotifByType(DATA.NOTIF_TYPE_MESSAGE);
		final ArrayList<String> refills = database.getAllNotifByType(DATA.NOTIF_TYPE_REFILL);
		final ArrayList<String> callInvites = database.getAllNotifByType(DATA.NOTIF_TYPE_CALLINVITE);//AddDoctor
		final ArrayList<String> appointments = database.getAllNotifByType(DATA.NOTIF_TYPE_NEW_APPOINTMENT);
		final ArrayList<String> liveCares = database.getAllNotifByType(DATA.NOTIF_TYPE_NEW_PATIENT);
		final ArrayList<String> groupMessages = database.getAllNotifByType(DATA.NOTIF_TYPE_GROUP_MESSAGE);
		final ArrayList<String> prescriptions = database.getAllNotifByType(DATA.NOTIF_TYPE_DOCTOR_PRESCRIPTION);
		final ArrayList<String> serviceReferrals = database.getAllNotifByType(DATA.NOTIF_TYPE_SERVICE_REFERRAL_REQ);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(tvMessagesBadge!=null){
					if(messages!= null){
						DATA.print("--messages size "+messages.size());
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

				if(tvRefillsBadge!=null){
					if(refills!= null){
						DATA.print("--refills size "+refills.size());
						if(refills.size() > 0){
							tvRefillsBadge.setText(refills.size() + "");
							//badge.setTextSize(12);
							tvRefillsBadge.setVisibility(View.VISIBLE);
						}else {
							tvRefillsBadge.setVisibility(View.GONE);
						}
					}else {
						tvRefillsBadge.setVisibility(View.GONE);
					}
				}

				if(tvAddDoctorBadge!=null){
					if(callInvites!= null){
						DATA.print("--callInvites size "+callInvites.size());
						if(callInvites.size() > 0){
							tvAddDoctorBadge.setText(callInvites.size() + "");
							//badge.setTextSize(12);
							tvAddDoctorBadge.setVisibility(View.VISIBLE);
						}else {
							tvAddDoctorBadge.setVisibility(View.GONE);
						}
					}else {
						tvAddDoctorBadge.setVisibility(View.GONE);
					}
				}


				if(tvAppointmentBadge!=null){
					if(appointments!= null){
						DATA.print("--appointments size "+appointments.size());
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


				if(tvLiveCareBadge!=null){
					if(liveCares!= null){
						DATA.print("--liveCares size "+liveCares.size());
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
						DATA.print("--groupMessages size "+groupMessages.size());
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


				if(tvPrescriptionsBadge!=null){
					if(prescriptions!= null){
						DATA.print("--prescriptions size "+prescriptions.size());
						if(prescriptions.size() > 0){
							tvPrescriptionsBadge.setText(prescriptions.size() + "");
							//badge.setTextSize(12);
							tvPrescriptionsBadge.setVisibility(View.VISIBLE);
						}else {
							tvPrescriptionsBadge.setVisibility(View.GONE);
						}
					}else {
						tvPrescriptionsBadge.setVisibility(View.GONE);
					}
				}

				if(tvSoapRefBadge!=null){
					if(serviceReferrals!= null){
						DATA.print("--serviceReferrals size "+serviceReferrals.size());
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
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();


		if(getSupportActionBar() != null){
			//getSupportActionBar().setTitle(prefs.getString("last_name", "")+", "+prefs.getString("first_name", ""));
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
			getSupportActionBar().setTitle(getResources().getString(R.string.app_name)+" "+sb.toString().toUpperCase());
		}

		setBadge(db);

		//DATA.visit_start_time = "";
		//DATA.visit_end_time = "";
		DATA.call_start_time = "";
		DATA.call_end_time = "";
		DATA.virtual_visit_id = "";
		DATA.elivecare_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		DATA.elivecare_end_time = "";

		DATA.selectedUserCallId = "";
		DATA.rndSessionId = "";
		DialogPatientInfo.patientIdGCM = "";
		ActivityTcmDetails.primary_patient_id = "";
		ActivityTcmDetails.family_is_online = "";

		DATA.isVideoCallFromLiveCare = false;
		DATA.isVideoCallFromRefPt = false;

		//layPermissions.setVisibility(new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS) ? View.VISIBLE:View.GONE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONSANDROID13))
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(0);
			}
			else
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(250);
			}
		}
		else
		{
			if (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS))
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(0);
			}
			else
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(250);
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			layPermissions.setVisibility(new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONSANDROID13) ? View.VISIBLE:View.GONE);
		}
		else {
			layPermissions.setVisibility(new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS) ? View.VISIBLE:View.GONE);
		}

//		if((!isMyServiceRunning(MessageReceiverService.class)) || (!isMyServiceRunning(VChatListener.class))) {
//			
//			DATA.print("--online care service started from MainActivityNew check...");
//
//			Intent intent1 = new Intent();
//			intent1.setAction("com.app.onlinecaredr.START_SERVICE");
//			sendBroadcast(intent1);

//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawer)) {
			mDrawerLayout.closeDrawer(mDrawer);
		} else {
			super.onBackPressed();
		}

	}

	GloabalSocket gloabalSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_new1);
		activity = MainActivityNew.this;
		mainActivityNew = MainActivityNew.this;
		apiCallBack = this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		exportDB();

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
		mToolbar = findViewById(R.id.toolbar);
		mToolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle(getResources().getString(R.string.app_name)+" "+sb.toString().toUpperCase());
		setSupportActionBar(mToolbar);


		DATA.print("-- "+sb.toString().toUpperCase());
		DATA.print("-- "+getResources().getString(R.string.app_name)+" "+sb.toString().toUpperCase());


		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Submitting...");
		pd.setCanceledOnTouchOutside(false);


		tvLiveCare = findViewById(R.id.tvLiveCare);
		tvAppointments = findViewById(R.id.tvAppointments);
		tvDrProfile = findViewById(R.id.tvDrProfile);
		tvLogout = findViewById(R.id.tvLogout);
		tvDoctoDoc = findViewById(R.id.tvDoctoDoc);
		tvHomeHealth = findViewById(R.id.tvHomeHealth);
		tvDrSchedule = findViewById(R.id.tvDrSchedule);
		tvPresscriptions = findViewById(R.id.tvPrescriptions);
		tvMessages = findViewById(R.id.tvMessages);
		tvTransactions = findViewById(R.id.tvTransactions);
		tvRatting = findViewById(R.id.tvRatting);
		tvRefills = findViewById(R.id.tvRefills);
		tvTCM = findViewById(R.id.tvTCM);
		tvBilling = findViewById(R.id.tvBilling);
		tvSupport = findViewById(R.id.tvSupport);
		tvGroups = findViewById(R.id.tvGroups);
		tvAddDoctor = findViewById(R.id.tvAddDoctor);
		tvEMS = findViewById(R.id.tvEMS);
		tvSoapRef = findViewById(R.id.tvSoapRef);
		tvMyPatients = findViewById(R.id.tvMyPatients);
		tvRefrals = findViewById(R.id.tvRefrals);
		tvInstantConnect = findViewById(R.id.tvInstantConnect);
		tvAppInvite = findViewById(R.id.tvAppInvite);
		tvReffredPt = findViewById(R.id.tvReffredPt);

		cbAvailableForeLiveCare = findViewById(R.id.cbAvailableForOLC);

		tvMessagesBadge = findViewById(R.id.tvMessagesBadge);
		tvLiveCareBadge = findViewById(R.id.tvLiveCareBadge);
		tvAppointmentBadge = findViewById(R.id.tvAppointmentBadge);
		tvPrescriptionsBadge = findViewById(R.id.tvPrescriptionsBadge);
		tvRefillsBadge = findViewById(R.id.tvRefillsBadge);
		tvGroupsBadge = findViewById(R.id.tvGroupsBadge);
		tvAddDoctorBadge = findViewById(R.id.tvAddDoctorBadge);
		tvSoapRefBadge = findViewById(R.id.tvSoapRefBadge);

		btnOffline = findViewById(R.id.btnOffline);
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

		//if (prefs.getString("id", "0").equalsIgnoreCase("84")) {
		//getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
		//mToolbar.setLogo(R.drawable.ic_launcher);
			/*tvLiveCare.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_live_care_tr , 0, 0);
			tvAppointments.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_my_appointment_tr , 0, 0);
			tvDrProfile.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_my_profile_tr , 0, 0);
			tvLogout.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_logout_tr , 0, 0);
			tvDoctoDoc.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_doc_to_doc_tr , 0, 0);
			tvHomeHealth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_call_history_tr , 0, 0);
			tvDrSchedule.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dr_schedule_tr , 0, 0);
			tvPresscriptions.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_my_prescriptions_tr , 0, 0);
			tvMessages.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_my_messages_tr , 0, 0);
			tvTransactions.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_my_transactions_tr , 0, 0);*/
		//}

		if(prefs.getString("is_available_onlinecare","").equalsIgnoreCase("1")){
			cbAvailableForeLiveCare.setChecked(true);
			is_available_onlinecare = "1";
		}else{
			cbAvailableForeLiveCare.setChecked(false);
			is_available_onlinecare = "0";
		}
		cbAvailableForeLiveCare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					is_available_onlinecare = "1";
				}else {
					is_available_onlinecare = "0";
				}
				RequestParams params = new RequestParams();
				params.put("doctor_id", prefs.getString("id", ""));
				params.put("is_available_onlinecare", is_available_onlinecare);

				ApiManager apiManager = new ApiManager(ApiManager.AVAILABLE_FOR_OLC,"post",params,apiCallBack, activity);
				apiManager.loadURL();
			}
		});
		tvSoapRef.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showServiceReferalDialog();
			}
		});
		tvEMS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				emsDirctCall();
			}
		});
		tvGroups.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityGroupChat.class,false);
			}
		});
		tvBilling.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityBilling.class,false);
			}
		});

		tvSupport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showSupportDialog();
			}
		});
		tvMyPatients.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(ActivityTCM.class, false);
			}
		});
		tvRefrals.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(ActivityRefferedPatients.class, false);
			}
		});

		tvTCM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//customToast.showToast("Under Development",0,1);
				openActivity.open(ActivityTCM.class, false);
				/*DATA.selectedUserCallId = "35";
				Intent i = new Intent(activity, ActivityFindNurse.class);
				i.putExtra("patientType","tcm,complex_care,home_health,nursing_home");
				//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);*/
			}
		});
		tvRefills.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(activity).setTitle("Prescriptions Refills")
						.setMessage("Please select refills by pharmacy or refills by patients.")
						.setPositiveButton("Pharmacy Refills", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								openActivity.open(ActivityRifills.class, false);
							}
						}).setNegativeButton("Patient Refills", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openActivity.open(ActivityPatientRefill.class, false);
					}
				}).show();

			}
		});
		tvRatting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity.open(CareRatings.class, false);

			}
		});
		tvTransactions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity.open(TransectionsActivity.class, false);

			}
		});
		tvAddDoctor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity.open(ActivityAddDoctor.class, false);//TransectionsActivity

			}
		});

		////////////////////////////////////////////////////////////////////////////////////////////////
		tvLiveCare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(checkInternetConnection.isConnectedToInternet()) {
					openActivity.open(LiveCare.class, false);
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
				}

			}
		});

		tvAppointments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//openActivity.open(SearchADoctor.class, false);
				openActivity.open(DrsAppointments.class, false);
				//openActivity.open(GCMActivity.class, false);
			}
		});

		tvDrProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity.open(UpdateProfile.class, false);

			}
		});

		tvDoctoDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDocToDocDialog();
				/*DATA.referToSpecialist = false;
				//openActivity.open(DocToDoc.class, false);
				openActivity.open(DoctorsList.class, false);*/
			}
		});
		tvHomeHealth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

//				openActivity.open(HomeHealth.class, false);
				openActivity.open(ActivityCallLogs.class, false);
			}
		});

		tvLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAskLogoutDialog();
			}
		});


		tvDrSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//openActivity.open(DrSchedule.class, false);
				openActivity.open(DoctorScheduleNew.class, false);

			}
		});

		tvPresscriptions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(activity,PresscriptionsActivity.class);
				intent.putExtra("isForMyPrescriptions", true);
				startActivity(intent);
				//openActivity.open(PresscriptionsActivity.class, false);
				/*AlertDialog alertDialog = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
						setTitle(getString(R.string.app_name))
								.setMessage("Please select an option to view Prescriptions")
								.setPositiveButton("My Prescriptions", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(activity,PresscriptionsActivity.class);
										intent.putExtra("isForMyPrescriptions", true);
										startActivity(intent);
									}
								})
						.setNegativeButton("Prescriptions by CP", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								openActivity.open(PresscriptionsActivity.class, false);

							}
						}).create();

				alertDialog.show();*/

			}
		});

		tvMessages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity.open(ConversationsActivity.class, false);

			}
		});

		tvInstantConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityInstatntConnect.class, false);
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
				openActivity.open(ActivityReferedPatients.class, false);
			}
		});

		//////////////////////////////////////////////////////////////////////////////
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		db = new Database(activity);

		mDrawer = findViewById(R.id.left_drawe);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		lvCategories = findViewById(R.id.lvCategories);

		mTitle  = getTitle();//= mDrawerTitle
		//  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
//
//			public void onDrawerClosed(View view) {
//				super.onDrawerClosed(view);
//				getActionBar().setTitle(mTitle);
//				invalidateOptionsMenu();
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//				getActionBar().setTitle(mDrawerTitle);
//				invalidateOptionsMenu(); 
//			}
//		};

		mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
		mDrawerLayout.addDrawerListener(mDrawerToggle);
		//for drawer toggle icon setup


		//getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

		DATA.allCategories = new ArrayList<CategoriesModel>();
		//Emcura Like options
		/*DATA.allCategories.add(new CategoriesModel("1", "Immediate Care",R.drawable.ic_drawer_elivecare_n));
		DATA.allCategories.add(new CategoriesModel("2", "Patients",R.drawable.ic_drawer_mypatients));
		DATA.allCategories.add(new CategoriesModel("4", "Doc To Doc",R.drawable.ic_drawer_doc_todoc_n));

		DATA.allCategories.add(new CategoriesModel("7", "My Prescriptions",R.drawable.ic_drawer_my_prescriptions));
		DATA.allCategories.add(new CategoriesModel("8", "Refill Requests",R.drawable.ic_drawer_refills));

		DATA.allCategories.add(new CategoriesModel("12", "My Messages",R.drawable.ic_drawer_my_messages));
		DATA.allCategories.add(new CategoriesModel("14", "My Schedule",R.drawable.ic_drawer_dr_schedule));
		DATA.allCategories.add(new CategoriesModel("5", "My Appointments",R.drawable.ic_drawer_my_appointment));
		DATA.allCategories.add(new CategoriesModel("13", "Call History",R.drawable.ic_drawer_call_history));
		DATA.allCategories.add(new CategoriesModel("17", "Support",R.drawable.ic_drawer_support));
		DATA.allCategories.add(new CategoriesModel("20", "My Profile",R.drawable.ic_drawer_my_profile));
		DATA.allCategories.add(new CategoriesModel("21", "Logout",R.drawable.ic_drawer_logout));*/

		DATA.allCategories.add(new CategoriesModel("1", "Patient Care",R.drawable.ic_drawer_elivecare_n));
		DATA.allCategories.add(new CategoriesModel("4", "Doc To Doc",R.drawable.ic_drawer_doc_todoc_n));
		DATA.allCategories.add(new CategoriesModel("2", "My Patients",R.drawable.ic_drawer_mypatients));
		DATA.allCategories.add(new CategoriesModel("12", "My Messages",R.drawable.ic_drawer_my_messages));
		DATA.allCategories.add(new CategoriesModel("13", "Call History",R.drawable.ic_drawer_call_history));
		DATA.allCategories.add(new CategoriesModel("22", "Instant Connect",R.drawable.ic_drawer_instant_connect));
		DATA.allCategories.add(new CategoriesModel("23", "App Invite",R.drawable.ic_drawer_app_invite));
		//DATA.allCategories.add(new CategoriesModel("14", "My Schedule",R.drawable.ic_drawer_dr_schedule));
		//DATA.allCategories.add(new CategoriesModel("5", "My Appointments",R.drawable.ic_drawer_my_appointment));
		DATA.allCategories.add(new CategoriesModel("17", "Referred Patients",R.drawable.ic_drawer_ref_pt));
		DATA.allCategories.add(new CategoriesModel("17", "Support",R.drawable.ic_drawer_support));
		DATA.allCategories.add(new CategoriesModel("20", "My Profile",R.drawable.ic_drawer_my_profile));
		DATA.allCategories.add(new CategoriesModel("21", "Logout",R.drawable.ic_drawer_logout));


		/*DATA.allCategories.add(new CategoriesModel("3", "Care Management",R.drawable.ic_drawer_care_mgt));


		DATA.allCategories.add(new CategoriesModel("9", "Service Referral",R.drawable.ic_drawer_service_ref));
		DATA.allCategories.add(new CategoriesModel("10", "Add Doctor",R.drawable.ic_drawer_add_family_members));
		DATA.allCategories.add(new CategoriesModel("11", "NEMT",R.drawable.ic_drawer_ems));
		//Doctor Ratings
		DATA.allCategories.add(new CategoriesModel("15", "Group Chat",R.drawable.ic_drawer_group_chat));
		DATA.allCategories.add(new CategoriesModel("16", "Billing",R.drawable.ic_drawer_billing));
		DATA.allCategories.add(new CategoriesModel("18", "My Transactions",R.drawable.ic_drawer_my_transactions));
		DATA.allCategories.add(new CategoriesModel("6", "Referred Patients",R.drawable.ic_drawer_ref_pt));// My Refrals  icon_drawer_referals
		DATA.allCategories.add(new CategoriesModel("19", "Switch Hospital",R.drawable.ic_drawer_switch_user));*/



		categoriesAdapter = new CategoriesAdapter(activity);
		lvCategories.setAdapter(categoriesAdapter);

		lvCategories.setOnItemClickListener(new DrawerItemClickListener());

		//==============================================GCM==========================================================
		
        /*if (new GloabalMethods(activity).checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }*/

		//===========================================GCM=============================================================

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
			//getSpecialties();
			gloabalMethods.getSpecialties();

			gloabalMethods.loadAppLabels();
		}

		layPermissions = findViewById(R.id.layPermissions);
		btnReviewPerm = findViewById(R.id.btnReviewPerm);
		btnExitApp = findViewById(R.id.btnExitApp);
		btnReviewPerm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(PermissionsActivity.class, false);
			}
		});

		btnExitApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mToolbar.setVisibility(View.GONE);
				finishAffinity();
				System.exit(0);
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONSANDROID13))
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(0);
			}
		}
		else
		{
			if (new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS))
			{
				DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
				params.width = dp2px(0);
			}
		}
	}//oncreate

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	protected void onDestroy() {

		gloabalSocket.offSocket();

		super.onDestroy();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		//   menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
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
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.AVAILABLE_FOR_OLC)){
			//{"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					prefs.edit().putString("is_available_onlinecare",is_available_onlinecare).commit();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}

	@Override
	public void onSocketCallBack(String emitterResponse) {

	}
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/


	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			//position = position-1;for top panel in adapter
			switch (position) {

				case 0:
					if(checkInternetConnection.isConnectedToInternet()) {
						openActivity.open(LiveCare.class, false);
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
					}
					break;
				case 1:
					showDocToDocDialog();
					/*DATA.referToSpecialist = false;
					//openActivity.open(DocToDoc.class, false);
					openActivity.open(DoctorsList.class, false);*/
					break;
				case 2:
					openActivity.open(ActivityTCM.class, false);
					break;
				case 3:
					openActivity.open(ConversationsActivity.class, false);
					break;
				case 4:
					openActivity.open(ActivityCallLogs.class, false);
					break;
				case 5:
					openActivity.open(ActivityInstatntConnect.class, false);
					break;
				case 6:
					openActivity.open(ActivityAppInvite.class, false);
					break;
				case 7:
					tvReffredPt.performClick();
					break;
				case 8:
					showSupportDialog();
					break;
				case 9:
					openActivity.open(UpdateProfile.class, false);
					break;
				case 10:
					showAskLogoutDialog();
					break;


				/*case 0:
					if(checkInternetConnection.isConnectedToInternet()) {
						openActivity.open(LiveCare.class, false);
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
					}
					break;
				case 1:
					openActivity.open(ActivityTCM.class, false);
					break;
				case 2:
					showDocToDocDialog();
					break;
				case 3:
					Intent intent = new Intent(activity,PresscriptionsActivity.class);
					intent.putExtra("isForMyPrescriptions", true);
					startActivity(intent);
					break;
				case 4:
					new AlertDialog.Builder(activity).setTitle("Prescriptions Refills")
							.setMessage("Please select refills by pharmacy or refills by patients.")
							.setPositiveButton("Pharmacy Refills", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									openActivity.open(ActivityRifills.class, false);
								}
							}).setNegativeButton("Patient Refills", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							openActivity.open(ActivityPatientRefill.class, false);
						}
					}).show();
					break;
				case 5:
					openActivity.open(ConversationsActivity.class, false);
					break;
				case 6:
					openActivity.open(DoctorScheduleNew.class, false);
					break;
				case 7:
					openActivity.open(DrsAppointments.class, false);
					break;
				case 8:
					openActivity.open(ActivityCallLogs.class, false);
					break;
				case 9:
					showSupportDialog();
					break;
				case 10:
					openActivity.open(UpdateProfile.class, false);
					break;
				case 11:
					AlertDialog d = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage("Are you sure? You want to logout.").
									setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											if (checkInternetConnection.isConnectedToInternet()) {
												logout();
											} else {
												Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
											}
										}
									}).setNegativeButton("No", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub

								}
							}).create();
					d.show();
					break;*/

				default:
					break;
			}
			lvCategories.setItemChecked(position, true);

//				setTitle(patientArry[position]);								

			if (position!= -1) {
				mDrawerLayout.closeDrawer(mDrawer);
			}
		}
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

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}*/

	/*private void exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//com.app.onlinecare//databases//onlinecareDB.sqlite";
				String backupDBPath = "onlinecareDB.sqlite";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				//		        Toast.makeText(activity, backupDB.toString(), Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
		}
	}*/

	/*private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {

	            DATA.print("--service already running "+serviceClass.getName());

	        	return true;
	            
	        }
	    }
        DATA.print("--service not running "+serviceClass.getName());

	    return false;
	}*/

	/*public void saveToken(String device_token) {
		 
		if (device_token.contains("|ID|")) {
			device_token = device_token.substring(device_token.indexOf(":") +1);
		}
			  AsyncHttpClient client = new AsyncHttpClient();
			  RequestParams params = new RequestParams();
			  
			  params.put("doctor_id", prefs.getString("id", "0"));
			  params.put("timezone", getDeviceTimeZone());
			  params.put("platform", "android");
			  params.put("device_token", device_token);
			 
			 DATA.print("--in saveDoctorToken regId: "+device_token+" userid: "+ prefs.getString("id", "0")+" timezone "+getDeviceTimeZone());

			  client.post(DATA.baseUrl+"/saveDoctorToken", params, new AsyncHttpResponseHandler() {
			   @Override
			 
			   public void onSuccess(int statusCode, String content) {

				   DATA.print("--reaponce in saveDoctorToken "+content);
				  
				  
			   }

			   @Override

			   public void onFailure(Throwable error, String content) {
				   DATA.print("--onFailure in saveDoctorToken "+content);
		//Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			   }
			  });

			 }//end saveDoctorToken

	
	private String getDeviceTimeZone() {
		Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		Log.d("Time zone","="+tz.getDisplayName());	
		DATA.print("--time zone "+tz.getDisplayName());
		DATA.print("--time zone from util "+TimeZone.getDefault());
		DATA.print("--time zone id from util "+TimeZone.getDefault().getID());
		return TimeZone.getDefault().getID();
	}*/



	public void logout() {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("id", prefs.getString("id", "0"));
		params.put("type", "doctor");

		String reqURL = DATA.baseUrl+"/logout";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in logout"+content);
					//01-06 22:09:54.586: I/System.out(2570): --reaponce in logout{"status":"success","msg":"Successfully Logged Out"}
					try {
						JSONObject jsonObject = new JSONObject(content);
						boolean debug_logs = SharedPrefsHelper.getInstance().get("debug_logs", false);
						if (jsonObject.has("status")&& jsonObject.getString("status").equalsIgnoreCase("success")) {

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
							SharedPrefsHelper.getInstance().save("debug_logs", debug_logs);

							//openActivity.open(Login.class, true);
							Intent intent = new Intent(getApplicationContext(), Login.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);


						/*Intent intent = new Intent();
						intent.setAction("com.app.onlinecare.STOP_SERVICE");
						sendBroadcast(intent);

						SharedPreferences.Editor ed = prefs.edit();
						ed.putBoolean("HaveShownPrefs", false);
						ed.putBoolean("qbcalledonce", false);
						ed.commit();

						openActivity.open(Login.class, true);*/

						} else {
							Toast.makeText(activity, DATA.CMN_ERR_MSG, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

		});

	}//end logout


	public void vacation_mode(final String vacation_mode, String auto_message) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("id", prefs.getString("id", "0"));
		params.put("user_type", "doctor");
		params.put("vacation_mode", vacation_mode);
		params.put("auto_message", auto_message);

		String reqURL = DATA.baseUrl+"vacation_mode";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in vacation_mode: "+content);
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
								btnDialogLogout.setVisibility(View.VISIBLE);
							}else {
								btnOffline.setText("Online");
								btnOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
								btnDialogOffline.setText("Save and go offline");
								btnDialogOffline.setBackgroundResource(R.drawable.btn_selector);
								//ivCloseDialog.setVisibility(View.VISIBLE);
								etOfflineMessage.setVisibility(View.VISIBLE);
								tvDialogLabel.setText(getString(R.string.offline_label_txt));
								btnDialogLogout.setVisibility(View.GONE);
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
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
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
	Button btnDialogLogout;
	public void showOfflineDialog(){
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialog.setContentView(R.layout.dialog_app_onoff);
		dialog.setCancelable(false);

		etOfflineMessage = dialog.findViewById(R.id.etOfflineMessage);
		btnDialogOffline = dialog.findViewById(R.id.btnDialogOffline);
		ivCloseDialog = dialog.findViewById(R.id.ivCloseDialog);
		tvDialogLabel = dialog.findViewById(R.id.tvDialogLabel);
		btnDialogLogout = dialog.findViewById(R.id.btnDialogLogout);

		if (prefs.getString("vacation_mode","0").equals("1")) {
			btnOffline.setText("Offline");
			btnOffline.setBackgroundResource(R.drawable.btn_selector);
			btnDialogOffline.setText("Go Online");
			btnDialogOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			//ivCloseDialog.setVisibility(View.GONE);
			etOfflineMessage.setVisibility(View.GONE);
			tvDialogLabel.setText("You are in offline mode.");
			btnDialogLogout.setVisibility(View.VISIBLE);
		}else {
			btnOffline.setText("Online");
			btnOffline.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			btnDialogOffline.setText("Save and go offline");
			btnDialogOffline.setBackgroundResource(R.drawable.btn_selector);
			//ivCloseDialog.setVisibility(View.VISIBLE);
			etOfflineMessage.setVisibility(View.VISIBLE);
			tvDialogLabel.setText(getString(R.string.offline_label_txt));
			btnDialogLogout.setVisibility(View.GONE);
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


		btnDialogLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAskLogoutDialog();
			}
		});

		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.show();
		dialog.getWindow().setAttributes(lp);*/

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.show();
	}

	public void showAskLogoutDialog(){
		AlertDialog d = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
				.setTitle(getResources().getString(R.string.app_name))
				.setMessage("Are you sure? You want to logout.").
						setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								if (checkInternetConnection.isConnectedToInternet()) {
									logout();
								} else {
									Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).create();
		d.show();
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

	public void emsDirctCall(){
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
						DATA.selectedLiveCare = null;
						DATA.incomingCall = false;
						Intent i = new Intent(activity,MainActivity.class);
						i.putExtra("isFromCallToEMS", true);
						startActivity(i);
					}
				}).setNegativeButton("Cancel",null).create().show();
	}


	public void showServiceReferalDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_referal_options);
		//dialogSupport.setCancelable(false);
		dialogSupport.setCanceledOnTouchOutside(false);

		TextView tvRefByDoc = dialogSupport.findViewById(R.id.tvRefByDoc);
		TextView tvRefByCp = dialogSupport.findViewById(R.id.tvRefByCp);
		TextView tvCancel = dialogSupport.findViewById(R.id.tvCancel);


		tvRefByDoc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				openActivity.open(ActivitySOAPReferral.class,false);

			}
		});
		tvRefByCp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				openActivity.open(ActivitySOAPReferralOT.class,false);

			}
		});

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
		dialogSupport.show();
		dialogSupport.getWindow().setAttributes(lp);
	}


	public void showDocToDocDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_doctodoc_opt);

		TextView tvDocToDocDialog = (TextView) dialogSupport.findViewById(R.id.tvDocToDocDialog);
		TextView tvDocToCPDialog = (TextView) dialogSupport.findViewById(R.id.tvDocToCPDialog);
		TextView tvNOTNOW = (TextView) dialogSupport.findViewById(R.id.tvNOTNOW);

		tvDocToDocDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				DATA.referToSpecialist = false;
				//openActivity.open(DocToDoc.class, false);
				openActivity.open(DoctorsList.class, false);
			}
		});
		tvDocToCPDialog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				openActivity.open(ActivityCareProviders.class, false);
			}
		});

		tvNOTNOW.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogSupport.show();
	}

	/*new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Doc To Doc")
							.setMessage("Please select Doc to Doc or Doc to CP").
							setPositiveButton("Doc to Doc", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									DATA.referToSpecialist = false;
									//openActivity.open(DocToDoc.class, false);
									openActivity.open(DoctorsList.class, false);
								}
							}).setNegativeButton("Doc to CP", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							openActivity.open(ActivityCareProviders.class, false);
						}
					}).setNeutralButton("Not Now", null).create().show();*/


	/*new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Doc To Doc")
						.setMessage("Please select Doc to Doc or Doc to CP").
						setPositiveButton("Doc to Doc", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DATA.referToSpecialist = false;
								//openActivity.open(DocToDoc.class, false);
								openActivity.open(DoctorsList.class, false);
							}
						}).setNegativeButton("Doc to CP", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openActivity.open(ActivityCareProviders.class, false);
					}
				}).setNeutralButton("Not Now", null).create().show();*/
}
