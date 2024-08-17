package com.app.OnlineCareUS_Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.OnlineCareUS_Pt.adapter.CategoriesAdapter;
import com.app.OnlineCareUS_Pt.api.ApiCallBack;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.b_health2.GetLiveCareFormBhealth;
import com.app.OnlineCareUS_Pt.devices.MyDevices;
import com.app.OnlineCareUS_Pt.model.CategoriesModel;
import com.app.OnlineCareUS_Pt.model.EmoWelOptionBean;
import com.app.OnlineCareUS_Pt.permission.PermissionsActivity;
import com.app.OnlineCareUS_Pt.permission.PermissionsChecker;
import com.app.OnlineCareUS_Pt.services.GPSTracker;
import com.app.OnlineCareUS_Pt.util.ActionEditText;
import com.app.OnlineCareUS_Pt.util.CheckInternetConnection;
import com.app.OnlineCareUS_Pt.util.CustomToast;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.Database;
import com.app.OnlineCareUS_Pt.util.EasyAES;
import com.app.OnlineCareUS_Pt.util.GloabalMethods;
import com.app.OnlineCareUS_Pt.util.GloabalSocket;
import com.app.OnlineCareUS_Pt.util.HideShowKeypad;
import com.app.OnlineCareUS_Pt.util.LiveCareInsurance;
import com.app.OnlineCareUS_Pt.util.OpenActivity;
import com.app.OnlineCareUS_Pt.util.SharedPrefsHelper;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class MainActivityNew extends AppCompatActivity implements GloabalSocket.SocketEmitterCallBack, ApiCallBack {

	//private static final String TAG = "AndroidUploadService";
	//AbstractUploadServiceReceiver uploadReceiver;
	
	/*private String[] patientArry = {
			"Urgent Care",
			"Book Appointment",
			"My Appointments",
			"Medical History",
			"My Messages",
			"My Prescriptions",
			"My Profile",
			"Logout"
	};
	private int[] drawrIconsArry = {
			R.drawable.ic_drawer_urgent_care,
			R.drawable.ic_drawer_book_appointment,
			R.drawable.ic_drawer_my_appointment,

			R.drawable.ic_drawer_medical_history,
			R.drawable.ic_drawer_my_messages,
			R.drawable.ic_drawer_my_prescriptions,

			R.drawable.ic_drawer_my_profile,
			R.drawable.ic_drawer_logout
	};//R.drawable.ic_heart,*/

	private String[] patientArry = {
			"Clinical Care",
			/*"Appointments",*/
			"Medical History",
			"My Messages",
			//"Family Members",
			"Insurance",
			"My Prescriptions",
			//"Profile Barcode"
			"My Profile",
			"Support",
			"Logout"
	};
	private int[] drawrIconsArry = {
			R.drawable.ic_drawer_urgent_care,
			/*R.drawable.ic_drawer_appt,*/
			R.drawable.ic_drawer_medical_history,
			R.drawable.ic_drawer_my_messages,
//			R.drawable.ic_drawer_add_family_members,
			R.drawable.ic_drawer_insuracne,
			R.drawable.ic_drawer_my_prescriptions,
			//R.drawable.ic_drawer_barcode,
			R.drawable.ic_drawer_my_profile,
			R.drawable.ic_drawer_support,
			R.drawable.ic_drawer_logout
	};//R.drawable.ic_heart,     icon_drawer_add_family_m

	AlertDialog.Builder builder;


//	private String[] patientArryMembers = {"Search a Doctor","Medical History", "Profile", "Appointments", "Reports","Switch User", "Logout"};
//	private int[] drawrIconsArryMembers = {R.drawable.icon_drawer_search_a_dr, R.drawable.icon_drawer_med_histry, 
//			R.drawable.icon_drawer_profile, R.drawable.icon_drawer_appntmnts, R.drawable.icon_drawer_reports, R.drawable.icon_drawer_add_family_m, R.drawable.icon_drawer_profile};

	private DrawerLayout mDrawerLayout;
	private ListView lvCategories;
	CharSequence mTitle = "";
	private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mDrawerTitle;
	//String sympArr[];
	CustomToast customToast;
	OpenActivity openActivity;
	CheckInternetConnection checkInternetConnection;
	//CallWebService callWebService;
	HideShowKeypad hideShowKeypad;

	TextView tvSetAppointment,
			tvAppointments,
			tvMedicalHistory,
			tvReports,
			tvProfile,
			tvAddFamilyMembers,
			tvSwitchUser,
			//tvPrescriptions,
			tvDonation,
			tvTransections,
			tvMessages,
			tvMedDevice,
			tvHomeHelth,
			tvSupport,
			tvTCM,
			tvLogout,
			tvGroupChat,
			tvMyPrescription,
			tvCovidTestPast,
			tvCovidTest;
	ImageView tvWellness;
	//tvGetLiveCare
	LinearLayout liveCareCont;

	TextView tvMessagesBadge,tvAppointmentBadge,tvGroupsBadge,tvMedHistoryBadge;

	Button btnOffline;

	LinearLayout layPermissions;
	Button btnReviewPerm,btnExitApp;

	ProgressDialog pd;

	CategoriesAdapter categoriesAdapter;
	SharedPreferences prefs;

	RelativeLayout mDrawer;

	Activity activity;
	public static MainActivityNew mainActivityNew;
	ImageView imgMainAd;
	//TextView tvMainNoInternet;

	GloabalMethods gloabalMethods;

	Database db;
	//	private BroadcastReceiver broadcastReceiver;
//	double latitude,longitude;
//	LocationManager lMgr;

	AlertDialog d;

	public static boolean medicalhistoryDone = false;
	@Override
	protected void onResume() {
		super.onResume();

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(prefs.getString("last_name", "")+", "+prefs.getString("first_name", ""));
		}

		setBadge(db);
		//uploadReceiver.register(this);

		if (prefs.getString("address", "").isEmpty() ||
				prefs.getString("state", "").isEmpty() ||
				prefs.getString("country", "").isEmpty() ||
				prefs.getString("city", "").isEmpty() ||
				prefs.getString("zipcode", "").isEmpty() ||
				prefs.getString("image", "").isEmpty() ||
				prefs.getString("image", "").equalsIgnoreCase("null")) {

			openActivity.open(UpdateProfile.class, false);

			/*AlertDialog.Builder b = new Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getString(R.string.app_name))
					.setMessage("Your profile information seems to be incomplete. Please complete your profile so that we can serve you the best.")
					.setPositiveButton("Done", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if (d != null) {
								d.dismiss();
							}
							openActivity.open(UpdateProfile.class, false);
						}
					}).setNegativeButton("Not Now", null);
			d = b.create();
			d.setCanceledOnTouchOutside(false);
			d.show();*/
		}

		//Forst login page removed in mhcsn by sir jamal
		//First login page removed by sir Jamal for OLC US app
		/*if(db.alreadyLoggedIn(prefs.getString("id", ""))) {
		} else {
			openActivity.open(FirstLogin.class, true);//Removed by Jamal
			//openActivity.open(MedicalPermissionForm.class, true);
		}*/

		/*if (prefs.getString("subPatientID", "").equals("0")) {

		} else {
			if (! prefs.getBoolean("isMedPermissionFormFilled", false)) {
				openActivity.open(MedicalPermissionForm.class, true);
			}

		}*/

		GPSTracker gpsTracker = new GPSTracker(activity);
		if (gpsTracker.canGetLocation()) {
			prefs.edit().putString("userLatitude", gpsTracker.getLatitude()+"").commit();
			prefs.edit().putString("userLongitude", gpsTracker.getLongitude()+"").commit();
			DATA.print("-- mainActivity on resume : lat: "+gpsTracker.getLatitude()+" lng: "+gpsTracker.getLongitude());
			gpsTracker.stopUsingGPS();
		}


		//layPermissions.setVisibility(new PermissionsChecker(activity).lacksPermissions(PermissionsChecker.PERMISSIONS) ? View.VISIBLE:View.GONE);
//android 13 new permission code by ahmer
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
		else {
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
		//end

		if (medicalhistoryDone)
		{
			getMedicalHistory(prefs.getString("id", "0"));
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		//uploadReceiver.unregister(this);
		//overridePendingTransition(R.anim.hold, R.anim.pull_out);
		//overridePendingTransition(R.anim.slide_down, R.anim.slide_down);//activity exit from top  to bottom slowly
	}

	@Override
	protected void onStart() {
		super.onStart();
		//		registerReceiver(broadcastReceiver, new IntentFilter(MessageReceiverService.BROADCAST_ACTION));
	}

	@Override
	protected void onStop() {
		super.onStop();

		//	unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawer)) {
			mDrawerLayout.closeDrawer(mDrawer);
		} else {
			super.onBackPressed();
			overridePendingTransition(R.anim.slide_down, R.anim.slide_down);//activity exit from top  to bottom slowly
		}

	}


	public void setBadge(Database database){//Activity activity
		//Database database = new Database(activity);
		/*ArrayList<String> allNotifications = db.getAllNotif();
		if(allNotifications != null){
			if(allNotifications.size() > 0){
				int badgeCount = allNotifications.size();
				ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
			}else{
				ShortcutBadger.removeCount(getApplicationContext());
			}
		}else {
			ShortcutBadger.removeCount(getApplicationContext());
		}*/
		final ArrayList<String> messages = database.getAllNotifByType(DATA.NOTIF_TYPE_MESSAGE);
		final ArrayList<String> appointments = database.getAllNotifByType(DATA.NOTIF_TYPE_NEW_APPOINTMENT);
		final ArrayList<String> groupMessages = database.getAllNotifByType(DATA.NOTIF_TYPE_GROUP_MESSAGE);

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
			}
		});

	}

	GloabalSocket gloabalSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		overridePendingTransition(R.anim.pull_in, R.anim.hold);
		setContentView(R.layout.main_screen_new2);

		activity = MainActivityNew.this;
		mainActivityNew = MainActivityNew.this;
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		db = new Database(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		mDrawer = findViewById(R.id.left_drawe);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		lvCategories = findViewById(R.id.lvCategories);

		mTitle = getTitle();

		Toolbar	mToolbar = findViewById(R.id.toolbar);
		mToolbar.setLogo(R.drawable.ic_launcher);
		//mToolbar.setTitle(getResources().getString(R.string.app_name));
		mToolbar.setTitle(prefs.getString("last_name", "")+", "+prefs.getString("first_name", ""));
		setSupportActionBar(mToolbar);
//	        mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
//	        mDrawerLayout.setDrawerListener(mDrawerToggle);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Submitting...");
		pd.setCanceledOnTouchOutside(false);



		new LiveCareInsurance(activity).getMyInsurance(false);

		gloabalSocket = new GloabalSocket(activity,this);
		JSONObject data = new JSONObject();
		try {
			data.put("id",prefs.getString("id",""));
			data.put("usertype","patient");
			data.put("status","login");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//mSocket.emit("webmsg",data);
		gloabalSocket.emitSocket(data);


		tvAddFamilyMembers = findViewById(R.id.tvAddFamilyMembers);
		tvSwitchUser = findViewById(R.id.tvSwitchUser);
		liveCareCont = findViewById(R.id.liveCareCont);
		tvSetAppointment = findViewById(R.id.tvSetAppointment);
		tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
		tvProfile = findViewById(R.id.tvProfile);
		tvAppointments = findViewById(R.id.tvAppointments);
		tvReports = findViewById(R.id.tvReports);
		//tvPrescriptions = findViewById(R.id.tvHomeHealth);
		tvDonation = findViewById(R.id.tvDonation);
		tvTransections = findViewById(R.id.tvTransections);
		tvMessages = findViewById(R.id.tvMessages);
		tvMedDevice = findViewById(R.id.tvMedDevice);
		tvHomeHelth = findViewById(R.id.tvHomeHelth);
		tvSupport = findViewById(R.id.tvSupport);
		tvTCM = findViewById(R.id.tvTCM);
		tvLogout = findViewById(R.id.tvLogout);
		tvGroupChat = findViewById(R.id.tvGroupChat);
		tvMyPrescription = findViewById(R.id.tvMyPrescription);
		tvWellness = findViewById(R.id.tvWellness);
		tvCovidTest = findViewById(R.id.tvCovidTest);
		tvCovidTestPast = findViewById(R.id.tvCovidTestPast);

		tvMessagesBadge = findViewById(R.id.tvMessagesBadge);
		tvAppointmentBadge = findViewById(R.id.tvAppointmentBadge);
		tvGroupsBadge = findViewById(R.id.tvGroupsBadge);
		tvMedHistoryBadge = findViewById(R.id.tvMedHistoryBadge);

		//HomeScreenItem item = DATA.loadLanguage(activity);
		
		/*tvAddFamilyMembers.setText(item.getAdd_family_members());
		tvSwitchUser.setText(item.getSwitch_user());
		tvGetLiveCare.setText(item.getGet_elivecare());
		tvSetAppointment.setText(item.getBook_appointment());
		tvMedicalHistory.setText(item.getMedical_history());
		tvProfile.setText(item.getMy_profile());
		tvAppointments.setText(item.getMy_appointment());
		tvReports.setText(item.getMy_reports());
		tvPrescriptions.setText(item.getMy_prescriptions());
		tvDonation.setText(item.getDonate_for_care());
		tvTransections.setText(item.getTransactions());
		tvMessages.setText(item.getMy_messages());
		tvMedDevice.setText(item.getMedical_devices());
		tvHomeHelth.setText(item.getHome_health());
		tvLogout.setText(item.getLogout());*/

		imgMainAd = findViewById(R.id.imgMainAd);
		imgMainAd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DATA.addIntent(activity);
			}
		});


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

		builder = new Builder(activity, R.style.CustomAlertDialogTheme);
		builder.setTitle(getResources().getString(R.string.app_name));
		//builder.setMessage("Your live care appointments will be cancelled.");
		builder.setMessage("Are you sure ? Do you want to logout.");
		builder.setPositiveButton("Yes, LOGOUT", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (checkInternetConnection.isConnectedToInternet()) {
					logout();
				} else {
					Toast.makeText(activity, "Please checl internet connection", Toast.LENGTH_SHORT).show();
				}

			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		////////////////////////////////////////////////////////////////////////////////////////////////


		tvMyPrescription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivity.open(PresscriptionActivity.class, false);
			}
		});
		tvGroupChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(ActivityGroupChat.class, false);
				customToast.showToast("Feature will be available in near future",0,1);
			}
		});
		tvTCM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(PatientNurses.class, false);
				customToast.showToast("Feature will be available in near future",0,1);
			}
		});
		tvAddFamilyMembers.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(AddFamilyMember.class, false);
			}
		});

		tvSwitchUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(SubUsersList.class,true);
				//GetUsers getUsers = new GetUsers(activity);
				//getUsers.getUsers();
			}
		});



		liveCareCont.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//urgentCareClick();
				showAskLiveCareDialog();
			}
		});

		tvSetAppointment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(SearchADoctor.class, false);
			}
		});

		tvMedicalHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.isFromFirstLogin = false;
				openActivity.open(MedicalHistory1.class, false);
			}
		});

		tvProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(UpdateProfile.class, false);
			}
		});

		tvAppointments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(MyAppointments.class, false);
				//openActivity.open(SearchADoctor.class, false);//tab based navigation added 1 icon for both book and my apptmnt
				openActivity.open(ActivityAppntmtOptions.class, false);
				//showApptmntMsgDialog();
			}
		});

		tvReports.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog verDialog = new Dialog(activity);
				verDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				verDialog.setContentView(R.layout.dialog_verification);
				verDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

				final TextView tvMessage = verDialog.findViewById(R.id.tvMessage);
				final EditText etPincode = verDialog.findViewById(R.id.etPincode);
				Button btnEnterPincode = verDialog.findViewById(R.id.btnEnterPincode);
				Button btnForgotPincode = verDialog.findViewById(R.id.btnForgotPincode);

				btnEnterPincode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (etPincode.getText().toString().isEmpty()) {
							customToast.showToast("Please enter your pincode",0,0);
						} else {
							if (prefs.getString("pincode", "1234").equals(etPincode.getText().toString())) {
								verDialog.dismiss();
								openActivity.open(ReportFolders.class, false);
							} else {
								tvMessage.setText("Incorrect pincode. Possible typing mistake?");
							}
						}
					}
				});

				btnForgotPincode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						verDialog.dismiss();
						//initForgotPincodeDialogDialog();
					}
				});

				verDialog.show();
			}
		});

		/*tvPrescriptions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				openActivity.open(ActivityInsurance.class, false);
				openActivity.open(PresscriptionActivity.class, false);
			}
		});*/

		tvDonation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*openActivity.open(DonateForCare.class, false);*/
				//openActivity.open(PaymentPaypalActivity.class, false);
				customToast.showToast("Feature will be available in near future",0,1);
			}
		});
		tvTransections.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openActivity.open(TransectionsActivity.class, false);
			}
		});
		tvMessages.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openActivity.open(ConversationsActivity.class, false);
			}
		});
		tvMedDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//customToast.showToast("To be linked with provider organization", 0, 1);
				openActivity.open(MyDevices.class, false);
			}
		});
		tvHomeHelth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				customToast.showToast("Feature will be available in near future",0,1);
			}
		});
		tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		tvSupport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showSupportDialog();
			}
		});

		//////////////////////////////////////////////////////////////////////////////

		//  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		/*mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); 
			}
		};*/

		mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		//mDrawerLayout.setDrawerListener(mDrawerToggle);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

		//set drawer items according to user logged in
		DATA.allCategories = new ArrayList<CategoriesModel>();
		CategoriesModel temp;
		for(int i = 0; i<patientArry.length;i++) {
			temp = new CategoriesModel();
			temp.catName = patientArry[i];
			temp.catIcon = drawrIconsArry[i];
			DATA.allCategories.add(temp);
			temp = null;
		}

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
		gloabalMethods = new GloabalMethods(activity);
		//gloabalMethods.getFirebaseToken();//moved down

		//------------Create notifications channel for android O
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create channel to show notifications.
			String channelId  = getString(R.string.default_notification_channel_id);
			String channelName = getString(R.string.app_name);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
		}
		//=================FCM==========================


		tvWellness.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showWellnessOptionsDialog();
				//gloabalMethods.showAskEmoWellDialog();
				loadEmoWellOptions();
			}
		});

		tvCovidTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gloabalMethods.showAskCovidDialog(false);
			}
		});
		tvCovidTestPast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gloabalMethods.showAskCovidDialog(true);
			}
		});

		setupNoNetworkLabel();


		gloabalMethods.loadSymtomsConditions();
		gloabalMethods.loadSymtomsConditionsBhealth();
		gloabalMethods.loadAppLabels();
		//gloabalMethods.loadSymtomsCovid();
		gloabalMethods.loadHospitalData();
		gloabalMethods.loadStates();

		//this cond will true on firstlogin forms screen . Its removed in emcura app
		/*if(db.alreadyLoggedIn(prefs.getString("id", ""))) {
			gloabalMethods.showAskMedHistoryDialog();
		}*/

		//boolean isAlreadyLoggedin = db.alreadyLoggedIn(prefs.getString("id", ""));
		boolean isAlreadyLoggedin = true;
		if(isAlreadyLoggedin){
			gloabalMethods.getFirebaseToken();

            if(Signup.covidOrVV == 1){
                Signup.covidOrVV = 0;
                openActivity.open(ActivityCovidTest.class, false);
            }else if(Signup.covidOrVV == 2){
                Signup.covidOrVV = 0;
                urgentCareClick();
            }
		}
		if(getIntent().getBooleanExtra("show_med_history_popup", false)){
			//gloabalMethods.showAskMedHistoryDialog();
			if(isAlreadyLoggedin) {
				//gloabalMethods.showAskMedHistoryDialog();
			}
		}


		//Reset Variables
		GetLiveCareFormBhealth.ocd_form = "";
		GetLiveCareFormBhealth.fcsas_form = "";
		GetLiveCareFormBhealth.panic_attack_form = "";
		GetLiveCareFormBhealth.scoff_form = "";
		GetLiveCareFormBhealth.stress_form = "";
		GetLiveCareFormBhealth.phq_form = "";
		GetLiveCareFormBhealth.gad_form = "";
		GetLiveCareFormBhealth.dast_form = "";
		DATA.print("-- MainActivityNew OnCreate reset variables - behealth assess filled form ids");



		TextView tvAppNameCopyRight = findViewById(R.id.tvAppNameCopyRight);
		TextView tvVersionName = findViewById(R.id.tvVersionName);

		String appname = "© "+ Calendar.getInstance().get(Calendar.YEAR)+ " " + getResources().getString(R.string.app_name)+"  Mobile App.";
		tvAppNameCopyRight.setText(appname);
		tvVersionName.setText("Version "+BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")");


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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSocketCallBack(String emitterResponse) {

	}


	/*"Immediate Care",
            "Medical History",
            //"Appointments",
            "Messages",
            "Support",
            //"Family Members",
            "Insurance",
            //"My Prescriptions",
            "Profile",
            "Logout"*/
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {

			/*switch (position){
				case 0:
					urgentCareClick();
					break;
				case 1:
					openActivity.open(SearchADoctor.class, false);
					break;
				case 2:
					openActivity.open(MyAppointments.class, false);
					break;
				case 3:
					openActivity.open(MedicalHistory1.class, false);
					break;
				case 4:
					openActivity.open(ConversationsActivity.class, false);
					break;
				case 5:
					openActivity.open(PresscriptionActivity.class, false);
					break;
				case 6:
					openActivity.open(UpdateProfile.class, false);
					break;
				case 7:
					AlertDialog dialog = builder.create();
					dialog.show();
					break;
				default:
					break;
			}*/

			switch (position){
				case 0:
					urgentCareClick();
					break;
				/*case 1:
					//openActivity.open(SearchADoctor.class, false);
					openActivity.open(ActivityAppntmtOptions.class, false);
					//showApptmntMsgDialog();
					break;*/
				case 1:
					openActivity.open(MedicalHistory1.class, false);
					//openActivity.open(ActivityCreditCardStripe.class, false);
					break;
				case 2:
					openActivity.open(ConversationsActivity.class, false);
					break;
				case 3:
					openActivity.open(ActivityInsurance.class, false);
					break;
//				case 5:
//					//openActivity.open(SubUsersList.class,false);
//                    Intent intent = new Intent(activity, SubUsersList.class);
//                    intent.putExtra("isFromHome", true);
//                    startActivity(intent);
//					break;
				case 4:
					openActivity.open(PresscriptionActivity.class, false);
					break;

				/*case 7:
					String barcode_image = prefs.getString("barcode_image", "");
					if(TextUtils.isEmpty(barcode_image)){
						RequestParams params = new RequestParams();
						params.put("patient_id", prefs.getString("id", ""));
						ApiManager apiManager = new ApiManager(ApiManager.PROFILE_QR,"post",params,MainActivityNew.this, activity);
						apiManager.loadURL();
					}else {
						showProfileQRDialog(barcode_image);
					}

					break;*/
				case 5:
					openActivity.open(UpdateProfile.class, false);
					break;

				case 6:
					showSupportDialog();
					break;

				case 7:
					AlertDialog dialog = builder.create();
					dialog.show();
					break;

				default:
					break;
			}

			lvCategories.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawer);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);

	}

	public void logout() {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("id", prefs.getString("id", "0"));
		params.put("type", "patient");


		client.post(DATA.baseUrl+"/logout", params, new AsyncHttpResponseHandler() {
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

						if (jsonObject.has("status")&& jsonObject.getString("status").equalsIgnoreCase("success")) {
							JSONObject data = new JSONObject();
							boolean debug_logs = SharedPrefsHelper.getInstance().get("debug_logs", false);
							try {
								data.put("id",prefs.getString("id",""));
								data.put("usertype","patient");
								data.put("status","logout");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							//mSocket.emit("webmsg",data);
							gloabalSocket.emitSocket(data);

						/*Intent intent = new Intent();
						intent.setAction("com.app.onlinecare.STOP_SERVICE");
						sendBroadcast(intent);*/

						/*Intent i = new Intent();
						i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
						sendBroadcast(i);*/

							String hospitalID = prefs.getString(Login.HOSP_ID_PREFS_KEY, "");
							String support_text = prefs.getString("support_text", "");

							SharedPreferences.Editor ed = prefs.edit();
							ed.putBoolean("HaveShownPrefs", false);
							ed.putBoolean("subUserSelected", false);
							ed.putBoolean("livecareTimerRunning", false);
							ed.putBoolean("isConcentFilled", false);

							ed.putBoolean("isInsuranceInfoAdded", false);
							ed.putString("insurance", "");
							ed.putString("policy_number", "");
							ed.putString("group", "");
							ed.putString("code", "");

							ed.commit();

							ed.clear();
							ed.apply();

							new Database(activity).emptyFirstLoginUser();

							//save hospital id and support_text on logout
							ed = prefs.edit();
							ed.putString(Login.HOSP_ID_PREFS_KEY, hospitalID);
							ed.putString("support_text", support_text);
							ed.commit();


							SharedPrefsHelper.getInstance().clearAllData();
							SharedPrefsHelper.getInstance().save("debug_logs", debug_logs);

							//openActivity.open(Login.class, true);
							Intent intent1 = new Intent(getApplicationContext(), Login.class);
							intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent1);

						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: logout, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- responce in logout: "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
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
		params.put("user_type", "patient");
		params.put("vacation_mode", vacation_mode);
		params.put("auto_message", auto_message);

		DATA.print("-- params in vacation_mode : "+params.toString());

		client.post(DATA.baseUrl+"vacation_mode", params, new AsyncHttpResponseHandler() {
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
					DATA.print("-- responce onsuccess: vacation_mode, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail vacation_mode: " +content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
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
				AlertDialog dialog = builder.create();
				dialog.show();
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



	//==============================
	public void setupNoNetworkLabel(){
		TextView tvMainNoInternet = findViewById(R.id.tvMainNoInternet);

		if(!checkInternetConnection.isConnectedToInternet()) {
			tvMainNoInternet.setVisibility(View.VISIBLE);

			//tvMainNoInternet.setAlpha(0.0f);
			// Start the animation
			//tvMainNoInternet.animate().translationY(tvMainNoInternet.getHeight()).alpha(1.0f);
		}else{
			tvMainNoInternet.setVisibility(View.GONE);
		}
		tvMainNoInternet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!checkInternetConnection.isConnectedToInternet()) {
					tvMainNoInternet.setVisibility(View.VISIBLE);

					//tvMainNoInternet.setAlpha(0.0f);
					// Start the animation
					//tvMainNoInternet.animate().translationY(tvMainNoInternet.getHeight()).alpha(1.0f);
				} else {
					tvMainNoInternet.setVisibility(View.GONE);
				}
			}
		});
	}



	String support_email = DATA.SUPPORT_EMAIL;
	public void showSupportDialog(){

		// TODO Auto-generated method stub
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_support);

		Button btnTextSupport = dialogSupport.findViewById(R.id.btnTextSupport);
		Button btnCallSupport = dialogSupport.findViewById(R.id.btnCallSupport);
		Button btnCallSupportOM = dialogSupport.findViewById(R.id.btnCallSupportOM);
		Button btnTextSupportOM = dialogSupport.findViewById(R.id.btnTextSupportOM);

		TextView tvMsg = dialogSupport.findViewById(R.id.tvMsg);

		//String styledText = "For Technical support, either email us at " + "<u><font color='#2979ff'>support@onlinecare.com</font></u>"+", or send us a text message by clicking the link below.";
		//tvMsg.setText(Html.fromHtml(styledText));

		//String txt = "Dear valued customer, currently we are available to support you from Monday till Friday between 09:00 AM - 07:00 PM EST, for any query you may call us at the number " + "<u><font color='#2979ff'>(810)-406-8466</font></u>"+" for support, you may also send an email us at :";
		//tvMsg.setText(Html.fromHtml(txt));

		String support_text = prefs.getString("support_text","");
		if(TextUtils.isEmpty(support_text)){
			support_text = DATA.SUPPORT_MESSAGE;
		}
		tvMsg.setText(support_text);

		support_email = prefs.getString("support_email", "");
		if(TextUtils.isEmpty(support_email)){
			support_email = DATA.SUPPORT_EMAIL;
		}

		btnTextSupport.setText(support_email);

		/*tvMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+Uri.encode("(810)-406-8466")));
					callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(callIntent);
				}catch (Exception  e){e.printStackTrace();}
			}
		});*/

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
				/*dialogSupport.dismiss();

				Intent i = new Intent(activity,MainActivity.class);
				i.putExtra("isFromCallToCoordinator", true);
				startActivity(i);*/

				customToast.showToast("Not available for right now",0,1);
			}
		});
		btnCallSupportOM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialogSupport.dismiss();
				customToast.showToast("Not available for right now",0,1);
			}
		});
		btnTextSupportOM.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSupport.dismiss();

				initMsgDialog();
			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogSupport.show();
	}

	public void sendMessageToOM(String message_text){
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		params.put("message_text", EasyAES.encryptString(message_text));

		ApiManager apiManager = new ApiManager(ApiManager.SND_MSG_OM,"post",params,MainActivityNew.this, activity);
		apiManager.loadURL();
	}


	public void getMedicalHistory(String patient_id) {

		ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_HISTORY+"/"+patient_id,"get",null,MainActivityNew.this, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.SND_MSG_OM)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success")) {
					dialogSendMessage.dismiss();
					customToast.showToast("Your message has been sent",0,1);
				}else if (jsonObject.has("error")) {
					dialogSendMessage.dismiss();
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Info").setMessage(jsonObject.getString("message")).
							setPositiveButton("OK",null).show();
				} else {
					Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DATA.print("--json eception sendMsg" +content);
			}
		}
		else if(apiName.contains(ApiManager.CHECK_CLINIC_TIMING)){
			//{"status":"success","message":"Available"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				if(status.equalsIgnoreCase("success")){
					searchDoctors();
				}else {
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(message)
							.setPositiveButton("Done", null)
							.create().show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0,0);
			}
		}
		else if(apiName.contains(ApiManager.SEARCH_DOCTOR)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				if (data.length() == 0) {
					//showMessageBoxForAllDrs(activity, "We are sorry", "Currently no doctors available near by you. Do you want to connect to other doctor ?");
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(DATA.NO_DOC_MESSAGE)
							.setPositiveButton("Done", null)
							.create();
					/*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							Intent intent1 = new Intent(getApplicationContext(), MainActivityNew.class);
							intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent1);
							finish();
						}
					});*/
					alertDialog.show();
				}else {
					openActivity.open(GetLiveCare.class, false);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
		else if(apiName.equalsIgnoreCase(ApiManager.LOAD_EMO_WELL_OPTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.optJSONArray("data");
				if(data != null){
					ArrayList<EmoWelOptionBean> emoWelOptionBeans = new GsonBuilder().create().fromJson(data.toString(), new TypeToken<ArrayList<EmoWelOptionBean>>() {}.getType());
					if(emoWelOptionBeans != null){
						gloabalMethods.showAskEmoWellDialog(emoWelOptionBeans);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
		else if(apiName.equalsIgnoreCase(ApiManager.PROFILE_QR)){
			/*{
				"status": "success",
					"data": {
				"patient_id": "35",
						"barcode_image": "https://www.onlinecare.com/onlinecare_newdesign/barcodes/20210121151.png"
			}
			}*/
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.optString("status").equalsIgnoreCase("success")){
					String barcode_image = jsonObject.getJSONObject("data").getString("barcode_image");
					prefs.edit().putString("barcode_image", barcode_image).apply();
					showProfileQRDialog(barcode_image);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
		else if(apiName.contains(ApiManager.GET_MEDICAL_HISTORY)){

			try {
				JSONObject jsonObject = new JSONObject(content);
				//==============================================================================
				JSONArray diseases = jsonObject.getJSONArray("diseases");
				JSONObject data = jsonObject.getJSONObject("data");
				medicalhistoryDone = false;

				if(!data.has("id")){tvMedHistoryBadge.setVisibility(View.VISIBLE);}
				else {tvMedHistoryBadge.setVisibility(View.GONE);}

				String id = data.getString("id");
				String patient_id = data.getString("patient_id");

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void urgentCareClick(){
		if(prefs.getBoolean("livecareTimerRunning", false)) {
			openActivity.open(LiveCareWaitingArea.class, false);
		} else {
			openActivity.open(GetLiveCare.class, false);
			//checkClinicTimings();
		}
	}

	public void checkClinicTimings(){

		ApiManager apiManager = new ApiManager(ApiManager.CHECK_CLINIC_TIMING+Login.HOSPITAL_ID_EMCURA,"post",null,MainActivityNew.this, activity);
		apiManager.loadURL();
	}

	public void searchDoctors(){
		ApiManager apiManager = new ApiManager(ApiManager.SEARCH_DOCTOR+prefs.getString("zipcode", "1"),"post",null,MainActivityNew.this, activity);
		apiManager.loadURL();
	}

	public void loadEmoWellOptions(){
		ApiManager apiManager = new ApiManager(ApiManager.LOAD_EMO_WELL_OPTIONS,"post",null,MainActivityNew.this, activity);
		apiManager.loadURL();
	}


	Dialog dialogSendMessage;
	public void initMsgDialog() {
		dialogSendMessage = new Dialog(activity);
		dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSendMessage.setContentView(R.layout.dialog_send_msg);
		final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
		TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);
		TextView btnSendMsgToFamily = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsgToFamily);

		TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);

		tvMsgPatientName.setText("To: Office Manager");
		btnSendMsgToFamily.setVisibility(View.GONE);


		btnSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etMsg.getText().toString().trim().isEmpty()) {
					customToast.showToast("Please type your message",0,0);
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						sendMessageToOM(EasyAES.encryptString(etMsg.getText().toString().trim()));
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}

			}
		});

		/*btnSendMsgToFamily.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etMsg.getText().toString().isEmpty()) {
					Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						sendMsg(ActivityTcmDetails.primary_patient_id, EasyAES.encryptString(etMsg.getText().toString()));
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}

			}
		});*/

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogSendMessage.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogSendMessage.show();
		dialogSendMessage.getWindow().setAttributes(lp);
	}



	public void showApptmntMsgDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_appointment_msg);

		TextView tvCallNow = (TextView) dialogSupport.findViewById(R.id.tvCallNow);
		TextView tvNOTNOW = (TextView) dialogSupport.findViewById(R.id.tvNOTNOW);

		TextView tvApptMsg = dialogSupport.findViewById(R.id.tvApptMsg);

		String styledText = "To make an appointment, please call our clinic directly. You can reach us at " + "<font color='#2979ff'><u>"+DATA.EMCURA_BLOOMFIELD_PHONE+"</u></font>";
		tvApptMsg.setText(Html.fromHtml(styledText));



		tvCallNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+Uri.encode(DATA.EMCURA_BLOOMFIELD_PHONE)));
					callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(callIntent);
				}catch (Exception e){
					e.printStackTrace();
				}

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




	public void showProfileQRDialog(String qrcodeURL){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_profile_qrcode);

		dialogSupport.setCanceledOnTouchOutside(false);

		ImageView ivClose = dialogSupport.findViewById(R.id.ivClose);
		ImageView ivQRcode = dialogSupport.findViewById(R.id.ivQRcode);

		TextView tvPatientName = dialogSupport.findViewById(R.id.tvPatientName),
				tvSelPtPhone = dialogSupport.findViewById(R.id.tvSelPtPhone),
				tvSelPtEmail = dialogSupport.findViewById(R.id.tvSelPtEmail),
				tvSelPtAddress = dialogSupport.findViewById(R.id.tvSelPtAddress),
				tvSelPtDOB = dialogSupport.findViewById(R.id.tvSelPtDOB);

		String fullName = prefs.getString("first_name","") + " " + prefs.getString("last_name", "");
		tvPatientName.setText(fullName);

		String residency = prefs.getString("address", "");
		//String address2 = patient_data.getString("address2");
		String city = prefs.getString("city", "");
		String state = prefs.getString("state", "");
		String zipcode = prefs.getString("zipcode", "");

		StringBuilder sbAddress = new StringBuilder();
		//sbAddress.append("<font color='"+DATA.APP_THEME_RED_COLOR+"'>Address : </font>");
		if(!TextUtils.isEmpty(residency)){
			sbAddress.append(residency);
			sbAddress.append("\n");
		}
		if(!TextUtils.isEmpty(city)){
			sbAddress.append(city);
			sbAddress.append(", ");
		}
		if(!TextUtils.isEmpty(state)){
			sbAddress.append(state);
			sbAddress.append(", ");
		}
		if(!TextUtils.isEmpty(zipcode)){
			sbAddress.append(zipcode);
		}
		//tvSelPtAddress.setText(Html.fromHtml(sbAddress.toString()));
		tvSelPtAddress.setText(sbAddress.toString());

		//tvSelPtDOB.setText(Html.fromHtml("<font color='"+DATA.APP_THEME_RED_COLOR+"'>DOB : </font>"+patient_data.getString("birthdate")+"  <b>|</b>  <font color='"+DATA.APP_THEME_RED_COLOR+"'>Age : </font>"+selectedUserCallAge));//DATA.selectedUserCallAge
		//tvSelPtDOB.setText(Html.fromHtml(prefs.getString("birthdate", "")+"  &nbsp;&nbsp;<b>|</b>&nbsp;&nbsp;  <font color='"+DATA.APP_THEME_RED_COLOR+"'>Age : </font>"+selectedUserCallAge));//DATA.selectedUserCallAge
		tvSelPtDOB.setText(prefs.getString("birthdate", ""));

		DATA.loadImageFromURL(qrcodeURL, R.drawable.ic_placeholder_3, ivQRcode);

		tvSelPtPhone.setText(prefs.getString("phone", ""));

		ImageView ivPatient = dialogSupport.findViewById(R.id.ivPatient);


		DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.ic_placeholder_3, ivPatient);


		ivClose.setOnClickListener(new View.OnClickListener() {

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
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);
	}

	public void showAskLiveCareDialog(){
		final Dialog dialogLiveCareOptions = new Dialog(activity);
		dialogLiveCareOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLiveCareOptions.setContentView(R.layout.dialog_asklivecare);
		dialogLiveCareOptions.setCancelable(false);

		Button btnGotoUrgentCare = dialogLiveCareOptions.findViewById(R.id.btnGotoUrgentCare);
		Button btnEmotionWellness = dialogLiveCareOptions.findViewById(R.id.btnEmotionWellness);
		Button btnCancel = dialogLiveCareOptions.findViewById(R.id.btnCancel);

		TextView tvMsg = dialogLiveCareOptions.findViewById(R.id.tvMsg);


		btnGotoUrgentCare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogLiveCareOptions.dismiss();
				// TODO Auto-generated method stub
				urgentCareClick();
			}
		});

		btnEmotionWellness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogLiveCareOptions.dismiss();
				// TODO Auto-generated method stub
				loadEmoWellOptions();
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogLiveCareOptions.dismiss();
			}
		});
		dialogLiveCareOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogLiveCareOptions.show();

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
