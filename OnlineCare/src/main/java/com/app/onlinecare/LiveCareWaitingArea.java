package com.app.onlinecare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare.api.ApiManager;
import com.app.onlinecare.util.CheckInternetConnection;
import com.app.onlinecare.util.CustomToast;
import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.GloabalMethods;
import com.app.onlinecare.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ss.com.bannerslider.views.BannerSlider;

public class LiveCareWaitingArea extends BaseActivity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	
	TextView tvLiveTimer,tvLiveCareTicker,tvDoctorName;
	ImageView imgAd;
	
	private BroadcastReceiver broadcastReceiver;

	long avtTimeMillis = 0;
	
	String hrs = "", minutes = "";
	Button btnFillForm,btnAddPrimaryCare;//btnCheckheartbeat
	OpenActivity openActivity;
	//TextView tvHeartbeat;
	SharedPreferences prefs;
	boolean isTimerRunning = false;
	Typeface timerfonts; 
	
	//Spinner spPharmacy;
	GloabalMethods gloabalMethods;
	/*LinearLayout layPharmacyInfo;
	TextView tvNoPharmacyAdded;
	TextView tvPharmacyName,tvPharmacyStreetAddress,tvPharmacyCity,tvPharmacyState,tvPharmacyZipcode,tvPharmacyPhone,tvPharmacyCategories;
	Button btnSelectPharmacy;*/
	Button btnPharmacy;
	TextView tvSelPharmacy;

	//public static final String ACTION_ASSIGNED_DOC = "com.app.emcurauc.assignedeLiveCaretoDoc";
	public static final String ACTION_ASSIGNED_DOC =  BuildConfig.APPLICATION_ID+".assignedeLiveCaretoDoc";
	
	@Override
	protected void onStart() {
		super.onStart();		
		
		IntentFilter filter = new IntentFilter();
		//filter.addAction("LIVE_CARE_WAITING_PATIENTS");
		//filter.addAction("LIVE_CARE_WAITING_TIMER_STOP");
		filter.addAction(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION);
		filter.addAction(ACTION_ASSIGNED_DOC);
		registerReceiver(broadcastReceiver, filter);

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		unregisterReceiver(broadcastReceiver);

	}


	@Override
	protected void onResume() {
		/*if (! prefs.getString("heartbeat", "").equals("")) {
			tvHeartbeat.setVisibility(View.VISIBLE);
			tvHeartbeat.setText("Your heart rate is: "+prefs.getString("heartbeat", "")+" bpm");
		}*/
		if (!prefs.getString("doctor_queue_msg", "").equals("")) {
			tvDoctorName.setVisibility(View.VISIBLE);
			tvDoctorName.setText(prefs.getString("doctor_queue_msg", ""));
		}
		super.onResume();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_live_care_waiting_area);
		
		timerfonts = Typeface.createFromAsset(getAssets(),"digital-7.ttf"); 
		
		activity = LiveCareWaitingArea.this;
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		
		tvLiveTimer = (TextView) findViewById(R.id.tvLiveTimer);
		tvLiveCareTicker = (TextView) findViewById(R.id.tvLiveCareTicker);
		tvLiveCareTicker.setTypeface(timerfonts);
		tvDoctorName = (TextView) findViewById(R.id.tvDoctorName);
		imgAd = (ImageView) findViewById(R.id.imgAd);
		
		
		/*btnSelectPharmacy = (Button) findViewById(btnSelectPharmacy);
		layPharmacyInfo = (LinearLayout) findViewById(layPharmacyInfo);
		tvNoPharmacyAdded = (TextView) findViewById(tvNoPharmacyAdded);
	    tvPharmacyName = (TextView) findViewById(tvPharmacyName);
	    tvPharmacyStreetAddress = (TextView) findViewById(tvPharmacyStreetAddress);
	    tvPharmacyCity = (TextView) findViewById(tvPharmacyCity);
	    tvPharmacyState = (TextView) findViewById(tvPharmacyState);
	    tvPharmacyZipcode = (TextView) findViewById(tvPharmacyZipcode);
	    tvPharmacyPhone = (TextView) findViewById(tvPharmacyPhone);
	    tvPharmacyCategories = (TextView) findViewById(tvPharmacyCategories);*/

		btnPharmacy = (Button) findViewById(R.id.btnPharmacy);
		tvSelPharmacy = (TextView) findViewById(R.id.tvSelPharmacy);

		imgAd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						DATA.addIntent(activity);
					}
				});	
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {

	//shutdown broadcast setup---Android 28 issues --- 2 jan 2019  GM
//				if(intent.getAction().equals("LIVE_CARE_WAITING_TIMER_STOP")) {
//					/*Intent i = new Intent(getBaseContext(), GetLiveCare.class);
//					startActivity(i);*/
//					finish();
//				} else if(intent.getAction().equals("LIVE_CARE_WAITING_PATIENTS")) {
//					String waitingPatients = intent.getStringExtra("patients");
//					String your_no = intent.getStringExtra("yourNo");;
//					int yourNum = Integer.parseInt(your_no);
//					/*if (yourNum > 1) {
//						yourNum = yourNum-1;
//					}*/
//					 avtTimeMillis =  (yourNum * 600000);//300000 milis = 5 minutes
//					if(yourNum >= 1) {
//						if (!isTimerRunning) {
//							countDownTimer(avtTimeMillis);
//						}
//
//						//tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
//
//						tvLiveTimer.setText("Patients in queue: "+waitingPatients+"\nYou are at number: " + (yourNum));
//					}else {
//						tvLiveTimer.setText("Be ready, our doctor will contact you soon. Thanks.");//waitingPatients+
//						tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
//
//					}
//				}else

					if (intent.getAction().equals(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION)) {

				    /*gloabalMethods.showPharmacyMap(GloabalMethods.selectedPharmacyBean);
				    
				    if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
				    	layPharmacyInfo.setVisibility(View.GONE);
				    	tvNoPharmacyAdded.setVisibility(View.VISIBLE);
					}else {
						layPharmacyInfo.setVisibility(View.VISIBLE);
						tvNoPharmacyAdded.setVisibility(View.GONE);
					    tvPharmacyName.setText(GloabalMethods.selectedPharmacyBean.StoreName);
					    tvPharmacyStreetAddress.setText(GloabalMethods.selectedPharmacyBean.address);
					    tvPharmacyCity.setText(GloabalMethods.selectedPharmacyBean.city);
					    tvPharmacyState.setText(GloabalMethods.selectedPharmacyBean.state);
					    tvPharmacyZipcode.setText(GloabalMethods.selectedPharmacyBean.zipcode);
					    tvPharmacyPhone.setText(GloabalMethods.selectedPharmacyBean.PhonePrimary);
					    tvPharmacyCategories.setText(GloabalMethods.selectedPharmacyBean.SpecialtyName);
					}*/

					if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
						btnPharmacy.setText("Select Pharmacy");
					}else {
						btnPharmacy.setText("Change Pharmacy");
						tvSelPharmacy.setText(Html.fromHtml("Selected Pharmacy: <b>"+GloabalMethods.selectedPharmacyBean.StoreName+"</b>"));
					}
				
				}else if(intent.getAction().equals(ACTION_ASSIGNED_DOC)){
						finish();
					}
			}
		};
		
	
	btnFillForm = (Button) findViewById(R.id.btnLiveFillForm);
	btnFillForm.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			DATA.isFromFirstLogin = false;
			openActivity.open(MedicalHistory1.class, false);
		}
	});
	btnAddPrimaryCare = (Button) findViewById(R.id.btnAddPrimaryCare);
	btnAddPrimaryCare.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//new GloabalMethods(activity).pimaryCareDialog();
			openActivity.open(ActivityPrimaryCareDoctors.class,false);
		}
	});
	//btnCheckheartbeat = (Button) findViewById(R.id.btnCheckHeartbeat);
	//tvHeartbeat = (TextView) findViewById(R.id.tvHeartbeat);
	/*btnCheckheartbeat.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DATA.isFromFirstLogin = false;
			//openActivity.open(HeartRateMonitor.class, false);
		}
	});*/

	gloabalMethods = new GloabalMethods(activity);
	//Note: no need to load pharmacy here as pharmacy button is hidden in UI--->2Jan2019--->GM
	 /*if (checkInternetConnection.isConnectedToInternet()) {
			gloabalMethods.getPharmacy("",false, "");
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}*/
		
		/*btnSelectPharmacy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (GloabalMethods.pharmacyBeans != null) {
					gloabalMethods.showPharmacyDialog();
				} else {
					System.out.println("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true);
					} else {
						customToast.showToast("Network not connected", 0, 0);
					}
				}
			}
		});*/
		btnPharmacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (GloabalMethods.pharmacyBeans != null) {
					gloabalMethods.showPharmacyDialog();
				} else {
					System.out.println("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true, "");
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
					}
				}
			}
		});

		BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);
		gloabalMethods.setupBannerSlider(bannerSlider,false);

		//Note: this check is impotant to update livecare asigned doc, in case if doc assigned push not recieved
		//Or patient refered to PCP by the doc, or also if patient refred to other doc/spe
		checkPatientqueue();

		if(timerAPIcall != null){
			timerAPIcall.cancel();
			timerAPIcall = null;
		}
		timerAPIcall = new Timer();
		timerAPIcall.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				if(checkInternetConnection.isConnectedToInternet() && DATA.shouldLiveCareWatingRefresh == 1) {

					System.out.println("-- this is API call timer");

					//Looper.prepare();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DATA.shouldLiveCareWatingRefresh = 0;
							getWaitingPatients();
						}
					});

					//AsyncReceiveMessage as = new AsyncReceiveMessage(ctx, prefs.getString("getLiveCareApptID", ""));
					//as.execute("");
					//System.out.println("--online care livecareappointment id: "+prefs.getString("getLiveCareApptID", ""));
				}


			}
		}, 1000,6000);
	}//end oncreate

	Timer timerAPIcall;
	CountDownTimer countDownTimer;

	@Override
	protected void onDestroy() {

		if(timerAPIcall != null){
			timerAPIcall.cancel();
		}
		if(countDownTimer != null){
			countDownTimer.cancel();
		}

		DATA.shouldLiveCareWatingRefresh = 1;

		System.out.println("-- LiveCareWaitingArea Activity onDestroy called");

		super.onDestroy();
	}

	private String convrtTime(long millis) {


//		long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);
        
		 //System.out.println("--online care waiting time millis: "+millis + "hrs: "+hours+" mins: "+minutes);

//		return hours+" hours, "+minutes+" minutes";

		String h = hours < 10 ? "0"+hours : ""+hours;
		String m = minutes < 10 ? "0"+minutes : ""+minutes;
		String s = seconds < 10 ? "0"+seconds : ""+seconds;
		 return h+":"+m+":"+s;
	}
	
	public void countDownTimer(long milis) {
		if(countDownTimer != null){
			countDownTimer.cancel();
			countDownTimer = null;
		}
		isTimerRunning = true;
		tvLiveCareTicker.setTypeface(timerfonts);
		tvLiveCareTicker.setTextSize(30.0f);
		countDownTimer = new CountDownTimer(milis, 1000) {

		    public void onTick(long millisUntilFinished) {
		    	//isTimerRunning = true;
		    	//tvLiveCareTicker.setText("seconds remaining: " + millisUntilFinished / 1000);
		    	//tvLiveCareTicker.setTypeface(timerfonts);
		    	//tvLiveCareTicker.setTextSize(30.0f);
				tvLiveCareTicker.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
		       //here you can have your logic to set text to edittext

				System.out.println("-- contdowntimer is running");
		    }

		    public void onFinish() {
		    	isTimerRunning = false;
		    	//tvLiveCareTicker.setTypeface(Typeface.DEFAULT);
		    	//tvLiveCareTicker.setTextSize(18.0f);
		    	//tvLiveCareTicker.setText("Its your trun. Be ready for live video checkup.");

				try {
					new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage("Sorry, it has been longer then the anticipated wait time. Office Manager has been informed and the provider will be with your shortly. Thank you for your patience.")
							.setPositiveButton("Done",null)
							.create().show();//causes nullpointer when activity not running

					timerAlert();
				}catch (Exception e){
					e.printStackTrace();
				}
		    }

		};
		countDownTimer.start();
	}


	public void getWaitingPatients(){
		RequestParams params = new RequestParams();

		String requestAPI = ApiManager.GET_WAITING_PATIENTS+"/"+prefs.getString("getLiveCareApptID", "")+"/"+prefs.getString("docId_eLiveCare", "");
		ApiManager apiManager = new ApiManager(requestAPI,"post",params,apiCallBack, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.contains(ApiManager.GET_WAITING_PATIENTS)){

			System.out.println("--in API: "+ApiManager.GET_WAITING_PATIENTS+" isTimerRunning = "+isTimerRunning);

			DATA.shouldLiveCareWatingRefresh = 1;

			try {
				JSONObject patients = new JSONObject(content).getJSONObject("patients");
				String status = patients.getString("status");
				String total_patient = patients.getString("total_patient");
				int your_number = patients.getInt("your_number");

				if(status.equals("1") || status.equals("")) {

					SharedPreferences.Editor ed = prefs.edit();
					ed.putBoolean("livecareTimerRunning", false);
					ed.commit();

					/*Intent i = new Intent();
					i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
					sendBroadcast(i);
					stopSelf();*/

					finish();


				} else {
					/*Intent i = new Intent();
					i.setAction("LIVE_CARE_WAITING_PATIENTS");
					i.putExtra("patients", totalPatients);
					i.putExtra("yourNo", yourNo);
					sendBroadcast(i);*/


					String waitingPatients = total_patient;

					int yourNum;
					if(prefs.getString("docId_eLiveCare", "").equalsIgnoreCase("0")) {//means doc not assigned by OM
						yourNum = 1;
					}else {
						yourNum = your_number;
					}
					/*if (yourNum > 1) {
						yourNum = yourNum-1;
					}*/
					avtTimeMillis =  (yourNum * 600000);//300000 milis = 5 minutes, 600000 = 10 minutes

					if(yourNum >= 1) {
						if (!isTimerRunning) {
							countDownTimer(avtTimeMillis);
						}
						//tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
						if(prefs.getString("docId_eLiveCare", "").equalsIgnoreCase("0")){//means doc not assigned by OM
							tvLiveTimer.setText("Patients in queue: 0");
						}else {
							tvLiveTimer.setText("Patients in queue: "+waitingPatients+"\nYou are at number: " + (yourNum));
						}
					}else {
						tvLiveTimer.setText("Be ready, our doctor will contact you soon. Thanks.");//waitingPatients+
						tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				//customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.CHECK_PATIENT_QUEUE+"/"+prefs.getString("id", "0"))){

			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success")) {
					String message = jsonObject.getString("message");
					String livecare_id = jsonObject.getString("livecare_id");

					String docId_eLiveCare = jsonObject.getString("doctor_id");
					prefs.edit().putString("docId_eLiveCare", docId_eLiveCare).commit();

					/*Intent intent1 = new Intent();
					intent1.setAction("LIVE_CARE_WAITING_TIMER");
					sendBroadcast(intent1);*/

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("getLiveCareApptID", livecare_id);
					ed.putBoolean("livecareTimerRunning", true);
					ed.putString("doctor_queue_msg", message);
					ed.commit();

					if (!prefs.getString("doctor_queue_msg", "").equals("")) {
						tvDoctorName.setVisibility(View.VISIBLE);
						tvDoctorName.setText(prefs.getString("doctor_queue_msg", ""));
					}

					//customToast.showToast(message,0,1);
					//openActivity.open(LiveCareWaitingArea.class, true);

				} else {
					System.out.println("--message "+jsonObject.getString("message"));
					//checkUrgentCarePatientqueue();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}


	public void timerAlert(){
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));

		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.TIMER_ALERT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	public void checkPatientqueue() {
		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_PATIENT_QUEUE+"/"+prefs.getString("id", "0"),"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}
}
