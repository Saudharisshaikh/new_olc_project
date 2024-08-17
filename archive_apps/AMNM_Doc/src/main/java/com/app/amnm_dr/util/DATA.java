package com.app.amnm_dr.util;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.app.amnm_dr.MainActivityNew;
import com.app.amnm_dr.api.ApiCallBack;
import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.api.Dialog_CustomProgress;
import com.app.amnm_dr.model.CategoriesModel;
import com.app.amnm_dr.model.ConditionsModel;
import com.app.amnm_dr.model.ConversationBean;
import com.app.amnm_dr.model.CountryBean;
import com.app.amnm_dr.model.DoctorsModel;
import com.app.amnm_dr.model.DrAppointmentModel;
import com.app.amnm_dr.model.DrugBean;
import com.app.amnm_dr.model.MyAppointmentsModel;
import com.app.amnm_dr.model.PastHistoryBean;
import com.app.amnm_dr.model.RefillBean;
import com.app.amnm_dr.model.RelativeHadBean;
import com.app.amnm_dr.model.ReportsModel;
import com.app.amnm_dr.model.SpecialityModel;
import com.app.amnm_dr.model.StateBean;
import com.app.amnm_dr.model.SubUsersModel;
import com.app.amnm_dr.model.SymptomsModel;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class DATA {

	//public static final String SIGNUP_URL = "https://onlinecare.com/onlinecare_accounts/index.php/app/";
	//public static final String ROOT_Url = "https://onlinecare.com/";
	//public static final String SIGNUP_URL = "https://telelivecare.com/onlinecare_accounts/index.php/app/";
	//public static final String ROOT_Url = "https://telelivecare.com/";

	public static final String ROOT_Url = "https://www.onlinecare.com/";
	public static final String SIGNUP_URL = ROOT_Url + "onlinecare_accounts/index.php/app/";
	public static final String POST_FIX = "/index.php/app/";

	public static String baseUrl = "";

	//public static final String baseUrl = "https://onlinecare.com/odev/index.php/app/";
	//public static final String baseUrl = "https://onlinecare.com/dfkhan/index.php/app/";
	//public static final String baseUrl = "https://telelivecare.com/dev/index.php/app/";

	//public static String baseUrl = "https://onlinecare.com/index.php/app/";

	//public static String baseUrl = "http://www.elivecare.com/dev/index.php/app/";

	//public static String baseUrl = "https://onlinecare.azurewebsites.net/dev/index.php/app2/";

	//public static String baseUrl = "http://www.onlinecare.com/dev/app/";
	//public static String baseUrl = "http://67.227.193.186/~onlinecare/dev2/app/";


	public static final String NO_NETWORK_MESSAGE = "Unable to the connect. Please try again.";
	public static final String JSON_ERROR_MSG = "Internal server error. JSON Exception";
	public static final String CMN_ERR_MSG = "Unexpected error just occurred, please try again later";

	public static final String APP_THEME_RED_COLOR = "#C69500";

	// Google project id
	public static final String GOOGLE_PROJECT_NO = "522574798528";
	public static String gcm_token = "";

	public static ConversationBean selecetedBeanFromConversation;
	public static String msgPatientImageForPopup;
	public static String msgPatientNameForPopup;
	public static String msgTimeForPopup;
	public static String msgTextForPopup;
	public static boolean shouldNotify = true;

	//public static boolean isSessionRunning = false;

	public static boolean isSOAP_NotesSent = false;
	/*public static String baseUrl = "https://onlinecare.com/dev/index.php/app/";
	public static String profileImgUrl ="https://onlinecare.com/dev/images/";
	public static String imageBaseUrl = "https://onlinecare.com/dev/patient_reports/";*/
	
	/*public static String baseUrl = "http://cbuystore.com/onlineclinic/index.php/app/";
	public static String profileImgUrl = "http://cbuystore.com/onlineclinic/images/";
	public static String imageBaseUrl = "http://cbuystore.com/onlineclinic/patient_reports/";*/

	//public static String gcmRegId = "";

	public static ArrayList<CategoriesModel> allCategories;
	public static ArrayList<SymptomsModel> allSymptoms;
	public static ArrayList<ConditionsModel> allConditions;
	public static ArrayList<DoctorsModel> allDoctors;
	public static ArrayList<SubUsersModel> allSubUsers;
	public static ArrayList<SpecialityModel> allSpecialities;
	//public static ArrayList<DrSheduleModel> allSchedule;
	public static ArrayList<MyAppointmentsModel> allAppointments;
	public static ArrayList<ReportsModel> allReports;
	public static ArrayList<ReportsModel> allReportsFiltered;

	public static boolean isDateSelected = false;
	//public static boolean isDatePickerCallFromSignup = false;
	//public static boolean isDatPckrCallFromAddMember = false;
	public static boolean isFromUploadReport = false;

	public static String date = "";

	public static String imagePath = "";
	public static boolean isImageCaptured = false;
	
	/*public static String patientNameForMsg = "";
	public static String patientIdForMsg = "";*/

	public static String selectedDrId = "";
	public static String selectedDrName = "";
	public static String selectedDrDesignation = "";
	public static String selectedDrImage = "";
	public static String selectedDrQbId = "";
	public static String selectedDrQualification = "";
	public static DoctorsModel selectedDoctorsModel;

	public static boolean isFromDrApmtUploadReports = false;

//	public static QBUser currentUser;

	public static final int junaid9Id = 2308236;
	public static final int aftab8Id = 2281690;

//	public static VideoChatConfig videoChatConfig;

	//public static byte[] videoData;

	public static String selectedUserQbid = "";
	public static String selectedUserCallId = "";
	public static String selectedUserCallName = "";
	public static String selectedUserCallSympTom = "";
	public static String selectedUserCallCondition = "";
	public static String selectedUserCallDescription = "";
	public static String selectedUserCallImage = "";
	public static String selectedUserAppntID = "";
	public static double selectedUserLatitude = 0.0;
	public static double selectedUserLongitude = 0.0;
	public static MyAppointmentsModel selectedLiveCare;

	//public static boolean isCallConnected =false;

	public static String selectedPtReportUrl = "";

	public static boolean isFromDocToDoc = false;
	//public static boolean isConfirence = false;

	//public static boolean isOutgoingCall = false;

	public static boolean isFromAppointment = false;
	public static boolean isFromCallHistoryOrMsgs = false;

	public static String outgoingCallResponse = "";

	public static boolean incomingCall= false;//isCallAccepted
	public static boolean isCallRejected= false;


	public static boolean isVideoCallFromLiveCare = false;
	public static boolean isVideoCallFromRefPt = false;


	public static String incomingCallerName = "";
	public static String incomingCallerImage = "";
	public static String incomingCallSessionId = "";
	public static String incommingCallId = "";
	public static String incomingCallUserId = "";
	public static String incomingCallResponce = "";

	public static String drSpecialityId = "";
	public static String drClinicId = "";


	public static String rndSessionId = "";

	//public static boolean isThirdCallToDoc = true;

	//public static boolean isDoctar = false;

	public static boolean referToSpecialist = false;

	public static String onlyReports = "0";
	public static ArrayList<DrAppointmentModel> awaitingAppointments;
	public static ArrayList<DrAppointmentModel>drsApptmentsList;
	public static DrAppointmentModel drAppointmentModel;
	public static ArrayList<String> selectedApptmntIdsForRefer = new ArrayList<String>();



	//public static String desaesenamesFromHistory = "";
	public static int isSmoke = 0;
	public static int isDrunk = 0;
	public static int isDrug = 0;
	public static int isMedication = 0;
	public static int isAlergies = 0;
	public static int isHospitalized = 0;
	public static ArrayList<Integer> selectedMedicalHistoryPositions= new ArrayList<Integer>();
	public static ArrayList<RelativeHadBean> relativeHadBeans = new ArrayList<RelativeHadBean>();
	public static ArrayList<PastHistoryBean> pastHistoryBeans;


	public static final String SHARED_PREFS_NAME = "doctoronlinecarePrefs";

	public static ArrayList<DrugBean> drugBeans = null;
	//public static ArrayList<DrugRefillBean> drugRefillBeans = null;
	public static RefillBean selectedRefillBean;

	public static String refillIdInGCM = "";


	public static ArrayList<CountryBean> loadCountries(Context context) {
		ArrayList<CountryBean>countryBeans = new ArrayList<CountryBean>();
		CountryBean bean = null;

		InputStream is;
		try {
			is = context.getAssets().open("countries.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);

			//convert string to JSONArray
			JSONArray jsonArray = new JSONArray(bufferString);
			//parse an Object from a random index in the JSONArray
			for (int i = 0; i < jsonArray.length(); i++) {
				String contName = jsonArray.getJSONObject(i).getString("name");
				String contCode = jsonArray.getJSONObject(i).getString("code");

				bean = new CountryBean(contName, contCode);
				countryBeans.add(bean);
				bean = null;
			}


		} catch (Exception e) {
			System.out.println("--exception in load countries");
			e.printStackTrace();
		}



		return countryBeans;
	}

	public static ArrayList<StateBean> loadStates(Context context) {
		ArrayList<StateBean>  stateBeans = new ArrayList<StateBean>();
		StateBean bean = null;

		InputStream is;
		try {
			is = context.getAssets().open("us_states.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);

			//convert string to JSONArray
			JSONArray jsonArray = new JSONArray(bufferString);
			//parse an Object from a random index in the JSONArray
			for (int i = 0; i < jsonArray.length(); i++) {
				String name = jsonArray.getJSONObject(i).getString("name");
				String abbreviation = jsonArray.getJSONObject(i).getString("abbreviation");

				bean = new StateBean(name, abbreviation);
				stateBeans.add(bean);
				bean = null;
			}


		} catch (Exception e) {
			System.out.println("--exception in load states");
			e.printStackTrace();
		}



		return stateBeans;
	}


	public static void addIntent(Activity activity){
		//String url = "http://www.womenshealthlink.org/";
		//String url = "https://onlinecare.com/";
		String url = "https://www.amnm.com/";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		activity.startActivity(i);
	}


	//static ProgressDialog pd;
	static Dialog_CustomProgress dialog_customProgress;
	public static void showLoaderDefault(Activity context, String message) {
		try {
			dialog_customProgress = new Dialog_CustomProgress(context);
			dialog_customProgress.showProgressDialog();
		}catch (Exception e){
			e.printStackTrace();
		}

		/*if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(context,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(context);
		}
		if (message.isEmpty()) {
			pd.setMessage("Please wait...    ");
		} else {
			pd.setMessage(message);
		}
		pd.setCanceledOnTouchOutside(false);
		pd.show();*/
	}

	public static void dismissLoaderDefault() {
		try {
			if(dialog_customProgress != null){
				dialog_customProgress.dismissProgressDialog();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		/*if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}*/
	}

	/*public static void showAlert(final Context context, String tittle, String msg, final Class<? extends Activity> activityToOpen){
		new AlertDialog.Builder(context).setTitle(tittle)
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.startActivity(new Intent(context,activityToOpen));
					}
				}).create().show();
	}*/


	static int hour;static  int minute;
	public static void setTimeByTimePicker(Activity activity, final EditText editText) {
		Calendar mcurrentTime = Calendar.getInstance();
		hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {
				// editText.setText( selectedHour + ":" + selectedMinute);
				// TODO Auto-generated method stub
				hour = hourOfDay;
				minute = minute1;
				String timeSet = "";
				if (hour > 12) {
					hour -= 12;
					timeSet = "PM";
				} else if (hour == 0) {
					hour += 12;
					timeSet = "AM";
				} else if (hour == 12)
					timeSet = "PM";
				else
					timeSet = "AM";

				String min = "";
				if (minute < 10)
					min = "0" + minute ;
				else
					min = String.valueOf(minute);

				// Append in a StringBuilder
				String aTime = new StringBuilder().append(hour).append(':')
						.append(min ).append(" ").append(timeSet).toString();
				editText.setText(aTime);

			}
		}, hour, minute, false);//Yes 24 hour time
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}

	public static void loadImageFromURL(String imageURL,int placeholder, final ImageView iv) {
		try {
			ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.cacheOnDisc(true)
					.showImageOnLoading(placeholder)
					.showImageForEmptyUri(placeholder)
					.showImageOnFail(placeholder)
					.resetViewBeforeLoading(true).build();

			imageLoader.displayImage(imageURL, iv, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static final String NOTIF_TYPE_MESSAGE = "message";
	public static final String NOTIF_TYPE_REFILL = "refill";
	public static final String NOTIF_TYPE_CALLINVITE = "callinvite";
	public static final String NOTIF_TYPE_NEW_APPOINTMENT = "newappointment";
	public static final String NOTIF_TYPE_NEW_PATIENT = "newpatient";
	public static final String NOTIF_TYPE_GROUP_MESSAGE = "group_message";
	public static final String NOTIF_TYPE_DOCTOR_PRESCRIPTION = "doctor_prescription";
	public static final String NOTIF_TYPE_SERVICE_REFERRAL_REQ = "referral_request";
	public void insertNotification(Context context,String notificType){
		Database database = new Database(context);
		database.insertNotif(notificType);

		if (MainActivityNew.mainActivityNew != null){
			MainActivityNew.mainActivityNew.setBadge(database);
		}
	}


	//public static String visit_start_time = "";
	//public static String visit_end_time = "";
	public static String call_start_time = "";
	public static String call_end_time = "";
	public static String virtual_visit_id = "";
	public static String elivecare_start_time = "";
	public static String elivecare_end_time = "";

	public static void endCall(Activity activity){
		SharedPreferences prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		ApiCallBack apiCallBack = new ApiCallBack() {
			@Override
			public void fetchDataCallback(String httpStatus, String apiName, String content) {

			}
		};

		String callId = "0";
		if(DATA.incomingCall){
			callId = DATA.incommingCallId;
		}else{
			callId = prefs.getString("callingID","");
		}

		RequestParams params = new RequestParams();
		params.put("call_start_time",DATA.call_start_time);
		params.put("call_end_time", DATA.call_end_time);
		params.put("virtual_visit_id", DATA.virtual_visit_id);

		ApiManager apiManager = new ApiManager(ApiManager.END_CALL+"/"+callId,"get",params,apiCallBack, activity);
		ApiManager.shouldShowPD = false;
		apiManager.loadURL();

	}
	public static void endCallInstantConnect(Activity activity, String callID){
		ApiCallBack apiCallBack = new ApiCallBack() {
			@Override
			public void fetchDataCallback(String httpStatus, String apiName, String content) {

			}
		};
		ApiManager apiManager = new ApiManager(ApiManager.CALLING_ENDCALL+"/"+callID,"get",null,apiCallBack, activity);
		ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}

		
		/*	public static String baseUrl = "http://3melements.com/onlineclinic/app/";
		public static String imageBaseUrl = "http://3melements.com/onlineclinic/patient_reports/";*/

		
	/*	public static String baseUrl = "http://107.172.10.113/~econnect/onlineclinic/app/";
		public static String imageBaseUrl = "http://107.172.10.113/~econnect/onlineclinic/patient_reports/";*/
		
	/*	public static String profileImgUrl ="http://cbuystore.com/onlineclinic/images//";
		public static String baseUrl = "http://cbuystore.com/onlineclinic/app/";
		public static String imageBaseUrl = "http://cbuystore.com/onlineclinic/patient_reports/";*/
		
		
		
		/*public static String baseUrl = "http://67.227.193.186/~onlinecare/index.php/app/";
		public static String profileImgUrl ="http://67.227.193.186/~onlinecare/images/";
		public static String imageBaseUrl = "http://67.227.193.186/~onlinecare/patient_reports/";*/
		
		
		/*public static String baseUrl = "http://67.227.193.186/~onlinecare/dev/index.php/app/";
		public static String profileImgUrl ="http://67.227.193.186/~onlinecare/dev/images/";
		public static String imageBaseUrl = "http://67.227.193.186/~onlinecare/dev/patient_reports/";*/


	//public static String imageBaseUrl = "https://onlinecare.com/patient_reports/";
	/*public static String imageBaseUrl = "https://onlinecare.com/dev/patient_reports/";*/
		 
		 
		 
		 /*public void callService() {

			DATA.showLoaderDefault(activity, "");
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();

			params.put("id", prefs.getString("id", "0"));
			params.put("type", "doctor");

			client.post(DATA.baseUrl+"/logout", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, String content) {
					DATA.dismissLoaderDefault();
					System.out.println("--reaponce in logout"+content);


				}

				@Override
				public void onFailure(Throwable error, String content) {
					DATA.dismissLoaderDefault();
					System.out.println("--onfail logout" +content);
					Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
				}
			});

		}*/


	/*AlertDialog alertDialog =
					new AlertDialog.Builder(activity).setTitle("Info")
					.setMessage("Information saved successfully.")
					.setPositiveButton("Done",null).create();
			alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {

				}
			});
			alertDialog.show();*/

	/*ArrayAdapter<PotencyUnitBean> spPtencyUnitAdapter = new ArrayAdapter<PotencyUnitBean>(
					        activity,
					        R.layout.spinner_item_lay,
					        potencyUnitBeans
					);
					spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner_potency_unit.setAdapter(spPtencyUnitAdapter);*/


	/*new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
			.setTitle("Confirm")
                .setMessage("Are you sure? Do you want to delete this medical report?")
                .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

		}
	})
			.setNegativeButton("Not Now", null)
                .create()
                .show();*/





	/*static SweetAlertDialog pDialog;
		static CountDownTimer countDownTimer;
		private static int i = -1;
		 public static void showLoader(final Context context) {
		    	  pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
		    	.setTitleText("Loading");
		    	pDialog.show();
		    	pDialog.setCanceledOnTouchOutside(false);
		    	countDownTimer = new CountDownTimer(800 * 7, 800) {
		    		public void onTick(long millisUntilFinished) {
		    			// you can change the progress bar color by ProgressHelper every 800 millis
		    			i++;

		    			System.out.println("--value of i before switch "+i);
		    			switch (i){
		    			case 0:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
		    				break;
		    			case 1:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_deep_teal_50));
		    				break;
		    			case 2:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.success_stroke_color));
		    				break;
		    			case 3:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_deep_teal_20));
		    				break;
		    			case 4:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_blue_grey_80));
		    				break;
		    			case 5:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.warning_stroke_color));
		    				break;
		    			case 6:
		    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.success_stroke_color));
		    				break;
		    			}
		    		}

		    		public void onFinish() {
		    			i = -1;
//		    			pDialog.setTitleText("Success!")
//		    			.setConfirmText("OK")
//		    			.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
		    			this.start();
		    		}
		    	};
		    	countDownTimer.start();
		    }


		 public static void dismissLoader() {
			if (pDialog.isShowing()) {
				pDialog.dismiss();
				countDownTimer.cancel();
			}
		}*/

}
