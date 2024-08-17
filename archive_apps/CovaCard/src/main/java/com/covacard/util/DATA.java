package com.covacard.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.RequestParams;
import com.onlinecare.MainActivityNew;
import com.covacard.OnlineCare;
import com.covacard.api.ApiCallBack;
import com.covacard.api.ApiManager;
import com.covacard.api.Dialog_CustomProgress;
import com.onlinecare.languagebeans.HomeScreenItem;
import com.onlinecare.model.CategoriesModel;
import com.onlinecare.model.ConditionsModel;
import com.onlinecare.model.ConversationBean;
import com.onlinecare.model.CountryBean;
import com.onlinecare.model.DoctorsModel;
import com.onlinecare.model.MyAppointmentsModel;
import com.onlinecare.model.PastHistoryBean;
import com.onlinecare.model.RelativeHadBean;
import com.onlinecare.model.ReportsModel;
import com.onlinecare.model.SpecialityModel;
import com.onlinecare.model.StateBean;
import com.covacard.model.SubUsersModel;
import com.onlinecare.model.SymptomsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class DATA {



	//public static final String SIGNUP_URL = "https://onlinecare.com/onlinecare_accounts/index.php/app/";
	//public static final String ROOT_Url = "https://onlinecare.com/";
	//public static final String SIGNUP_URL = "https://telelivecare.com/onlinecare_accounts/index.php/app/";
	//public static final String ROOT_Url = "https://telelivecare.com/";

	public static final String ROOT_Url = "https://www.onlinecare.com/";
	public static final String SIGNUP_URL = ROOT_Url + "onlinecare_accounts/index.php/app/";
	public static final String POST_FIX = "/index.php/app/";

	public static final String APP_THEME_RED_COLOR = "#2cbabb";

	public static String baseUrl = "";

	public static final String PRIVACY_POLICY_URL = ROOT_Url + "onlinecare_accounts/index.php/app/patients/privacy_policy";
	public static final String USER_AGREEMENT_URL = ROOT_Url + "onlinecare_accounts/index.php/app/patients/user_agreement";

	//public static final String baseUrl = "https://onlinecare.com/odev/index.php/app/";
	//public static final String baseUrl = "https://onlinecare.com/dfkhan/index.php/app/";
    //public static final String baseUrl = "https://telelivecare.com/dev/index.php/app/";

	public static final String NO_NETWORK_MESSAGE = "Unable to the connect. Please try again.";
	public static final String JSON_ERROR_MSG = "Internal server error. JSON Exception";
	public static final String CMN_ERR_MSG = "Unexpected error just occurred, please try again later";
	public static final String SUPPORT_MESSAGE = "We are currently available from 9am to 5pm live on our website at www.onlinecare.com - You may also email us at: support@onlinecare.com";
	public static final String SUPPORT_EMAIL = "support@onlinecare.com";
	public static final String NO_DOC_MESSAGE = "Sorry, currently no providers are available for virtual care, please contact out office for virtual care hours";

	public static final String EMCURA_BLOOMFIELD_PHONE = "(734)-956-6336";//"(810)-406-8466";//"248-885-8211"    //emcura_office = (734)-956-6336

	public static final String OLC_DATE_FMT = "MM/dd/yyyy";
	
	
	// Google project id
    public static final String GOOGLE_PROJECT_NO = "5379082267";
    public static String gcm_token = "";
	
	//public static String msgDoctorID = "";
	//public static String msgDoctorName = "";
	public static ConversationBean selecetedBeanFromConversation;
	public static String msgPatientImageForPopup;
	public static String msgPatientNameForPopup;
	public static String msgTimeForPopup;
	public static String msgTextForPopup;
	//public static boolean shouldNotify = true;
	
	public static int shouldLiveCareWatingRefresh = 1;
	
	//public static String gcmRegId = "";
	public static String incommingCallId = "";
	
	public static ArrayList<CategoriesModel> allCategories;
	public static ArrayList<SymptomsModel> allSymptoms;
	public static ArrayList<ConditionsModel> allConditions;
	public static ArrayList<DoctorsModel> allDoctors;
	public static ArrayList<SubUsersModel> allSubUsers;
	public static ArrayList<SpecialityModel> allSpecialities;
	//public static ArrayList<DrSheduleModel> allSchedule;
	//public static ArrayList<DrSheduleModel> allScheduleFiltered;
	public static ArrayList<MyAppointmentsModel> allAppointments;
	public static ArrayList<ReportsModel> allReports;
	public static ArrayList<ReportsModel> allReportsFiltered;
	public static ArrayList<ReportsModel> allFolders;

	//public static boolean isDateSelected = false;
	//public static boolean isDatePickerCallFromSignup = false;
	//public static boolean isDatPckrCallFromAddMember = false;
	//public static boolean isFromUploadReport = false;
	
//	public static QBUser currentUser;
	
	public static String date = "";
	
	public static String imagePath = "";
	public static boolean isImageCaptured = false;
	
	/*public static String selectedDrId = "";
	public static String selectedDrName = "";
	public static String selectedDrDesignation = "";
	public static String selectedDrImage = "";
	public static String selectedDrQbId = "";
	public static String selectedDrQualification = "";
	public static String selectedDrCareerData = "";*/
	public static DoctorsModel doctorsModelForApptmnt;
	
	public static boolean isFromDrApmtUploadReports = false;
	
	
	//public static final int junaid9Id = 2308236;
	//public static final int aftab8Id = 2281690;
	
//	public static VideoChatConfig videoChatConfig;
	
	//public static byte[] videoData;
	
	//public static String selectedDayIdForAppointment = "";
	//public static String selectedRGEveMor = "morning";
	public static String selectedSlotIdForAppointment = "";
	public static String selectedRGFreePaypal = "paypal";
	public static String selectedRGFreeOptions = "student";
	//public static boolean isAppointmentDaySelected = false;
	public static String selectedReportIdsForApntmt = "";
	public static boolean isReprtSelected = false;
	public static int NumOfReprtsSelected = 0;
	public static String requestedAppntDate = "";
	//public static String requestedAppntDay  = "";
	//public static String requestedAppntTime = "";

	public static String selectedReprtFoldrId = "";
	public static String selectedReprtPath = "";
	public static String selctdReprtURLForVu = "";
	public static String selctdReprtName = "";

	//public static String selectedReprtExtension = "";
	//public static String selectedReprtName = "";
	//public static String selectedReportId = "";
	//public static boolean isFromReportsFolder = false;

	public static boolean isFromFirstLogin = false;
	
	public static String incomingCallerName = "";
	public static String incomingCallerImage = "";
	public static String incomingCallSessionId = "";
	public static String incomingCallUserId = "";
	
	public static String outgoingCallSessionId = "";
	
	
	public static String desaesenamesFromHistory = "";
	public static int isSmoke = 0;
	public static int isDrunk = 0;
	public static int isDrug = 0;
	public static int isMedication = 0;
	public static int isAlergies = 0;
	public static int isHospitalized = 0;
	
	
	public static String zipCodeFromLiveCare = "";
	public static String doctorIdForLiveCare = "";
	public static boolean isLiveCarePaymentDone = false;
	public static String livecarePaymentPaypalInfo = "";
	public static int liveCareIdForPayment = 0;
	public static boolean isFreeCare = false;
	
	public static ArrayList<Integer> selectedMedicalHistoryPositions= new ArrayList<Integer>();
	
	public static ArrayList<PastHistoryBean> pastHistoryBeans;
//	public static ArrayList<String> relationHads= new ArrayList<String>();
//	public static ArrayList<String> relationHadNames= new ArrayList<String>();
	
	public static ArrayList<RelativeHadBean> relativeHadBeans = new ArrayList<RelativeHadBean>();
	
	
	
	
	
	/*params.put("patient_id",prefs.getString("id", ""));
	params.put("sub_patient_id",prefs.getString("subPatientID", ""));
	params.put("symptom_id",selectedSympId);
	params.put("condition_id",selectedConditionId);
	params.put("description",etLiveExtraInfo.getText().toString());
	params.put("report_ids", DATA.selectedReportIdsForApntmt);
	params.put("doctor_id", DATA.doctorIdForLiveCare);*/
	
	//public static String getLiveCarepatient_id;
	//public static String getLiveCaresub_patient_id;
	public static String getLiveCaresymptom_id;
	public static String getLiveCarecondition_id;
	public static String getLiveCaredescription;
	public static String getLiveCarereport_ids;
	public static String painWhere;
	public static String selectedPainSeverity;
	public static String selectedPainBodyPart;
	public static String ot_bp, ot_hr, ot_respirations, ot_saturation, ot_blood_sugar, ot_temperature, ot_height, ot_weight;
	//public static String getLiveCaredoctor_id;
	
	public static final String SHARED_PREFS_NAME = "onlinecarePrefs";
	
	public static String selected_dayForApptmnt = "";

	//public static final String urgentCareHospitalID = "6";   //6  = original ,   1=odev   removed in urgent care app
	//public static String pharmacy_id = "";
	//public static ArrayList<PharmacyBean> pharmacyBeans = new ArrayList<PharmacyBean>();
	
//	static SweetAlertDialog pDialog;
//	static CountDownTimer countDownTimer;
//	 static int i = -1;
//	 public static void showLoader(final Context context) {
//	    	  pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
//	    	.setTitleText("Loading");
//	    	pDialog.show();
//	    	pDialog.setCanceledOnTouchOutside(false);
//	    	countDownTimer = new CountDownTimer(800 * 7, 800) {
//	    		public void onTick(long millisUntilFinished) {
//	    			// you can change the progress bar color by ProgressHelper every 800 millis
//	    			i++;
//
//	    			System.out.println("--value of i before switch "+i);
//	    			switch (i){
//	    			case 0:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.blue_btn_bg_color));
//	    				break;
//	    			case 1:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_deep_teal_50));
//	    				break;
//	    			case 2:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.success_stroke_color));
//	    				break;
//	    			case 3:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_deep_teal_20));
//	    				break;
//	    			case 4:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.material_blue_grey_80));
//	    				break;
//	    			case 5:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.warning_stroke_color));
//	    				break;
//	    			case 6:
//	    				pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.success_stroke_color));
//	    				break;
//	    			}
//	    		}
//
//	    		public void onFinish() {
//	    			i = -1;
//	    			/*pDialog.setTitleText("Success!")
//	    			.setConfirmText("OK")
//	    			.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
//	    			this.start();
//	    		}
//	    	};
//	    	pDialog.setOnDismissListener(new OnDismissListener() {
//
//				@Override
//				public void onDismiss(DialogInterface arg0) {
//					// TODO Auto-generated method stub
//					countDownTimer.cancel();
//				}
//			});
//	    	countDownTimer.start();
//	    }
//
//
//	 public static void dismissLoader() {
//		if (pDialog.isShowing()) {
//			pDialog.dismiss();
//			countDownTimer.cancel();
//		}
//	}
	
	public static void addIntent(Activity activity){
		//String url = "http://www.womenshealthlink.org/";
		//String url = "https://onlinecare.com/";
		//String url = "http://www.mihcsn.com/";
		String url = "http://covacard.com/";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		activity.startActivity(i);
	}
	
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
	
	
	public static HomeScreenItem loadLanguage(Context context) {
		//ArrayList<CountryBean>countryBeans = new ArrayList<CountryBean>();
		HomeScreenItem item = null;
		
		InputStream is;
		try {
			is = context.getAssets().open("language.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);
			
			//convert string to JSONArray
			/*JSONArray jsonArray = new JSONArray(bufferString);
			//parse an Object from a random index in the JSONArray
			for (int i = 0; i < jsonArray.length(); i++) {
				String contName = jsonArray.getJSONObject(i).getString("name");
				String contCode = jsonArray.getJSONObject(i).getString("code");
				
				bean = new CountryBean(contName, contCode);
				countryBeans.add(bean);
				bean = null;
				
				
			}*/
			JSONObject jsonObject = new JSONObject(bufferString);
			jsonObject = jsonObject.getJSONObject("homeview");
			
			  String get_elivecare = jsonObject.getString("get_elivecare");
			  String book_appointment = jsonObject.getString("book_appointment");
			  String my_appointment = jsonObject.getString("my_appointment");
			  String medical_history = jsonObject.getString("medical_history");
			  String my_reports = jsonObject.getString("my_reports");
			  String my_prescriptions = jsonObject.getString("my_prescriptions");
			  String my_profile = jsonObject.getString("my_profile");
			  String add_family_members = jsonObject.getString("add_family_members");
			  String switch_user = jsonObject.getString("switch_user");
			  String transactions = jsonObject.getString("transactions");
			  String my_transactions = jsonObject.getString("my_transactions");
			  String my_messages = jsonObject.getString("my_messages");
			  String medical_devices = jsonObject.getString("medical_devices");
			  String home_health = jsonObject.getString("home_health");
			  String donate_for_care = jsonObject.getString("donate_for_care");
			  String logout = jsonObject.getString("logout");
		item = new HomeScreenItem(get_elivecare, book_appointment, my_appointment, medical_history, my_reports, my_prescriptions, my_profile, add_family_members, switch_user, transactions, my_transactions, my_messages, medical_devices, home_health, donate_for_care, logout);
		
		} catch (Exception e) {
			System.out.println("--exception in load countries");
			e.printStackTrace();
		}
		
		
		
		return item;
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


	public static void loadImageFromURL(String imageURL,int placeholder, final ImageView iv) {
		try {
			Glide.with(OnlineCare.getInstance().getApplicationContext()).setDefaultRequestOptions(new RequestOptions().placeholder(placeholder).error(placeholder)).load(imageURL).into(iv);
		}catch (Exception e){e.printStackTrace();}
		/*try {
			ImageLoader imageLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.cacheOnDisc(true)
					.showImageOnLoading(placeholder)
					.showImageForEmptyUri(placeholder)
					.showImageOnFail(placeholder)
					.resetViewBeforeLoading(true).build();

			*//*iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					imageLoader.displayImage(imageURL, iv, options);
				}
			});*//*
			imageLoader.displayImage(imageURL, iv, options);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}


	public static final String NOTIF_TYPE_MESSAGE = "message";
	public static final String NOTIF_TYPE_NEW_APPOINTMENT = "newappointment";
	public static final String NOTIF_TYPE_GROUP_MESSAGE = "group_message";
	public void insertNotification(Context context,String notificType){
		Database database = new Database(context);
		database.insertNotif(notificType);

		if (MainActivityNew.mainActivityNew != null){
			MainActivityNew.mainActivityNew.setBadge(database);
		}
	}


	public static void endCall(Activity activity){
		SharedPreferences prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		ApiCallBack apiCallBack = new ApiCallBack() {
			@Override
			public void fetchDataCallback(String httpStatus, String apiName, String content) {

			}
		};

		/*String callId = "0";
		if(DATA.incomingCall){
			callId = DATA.incommingCallId;
		}else{
			callId = prefs.getString("callingID","");
		}*/
		//Note : patient has only incomming call or he can connect his family during call - Calling this service
		//for incoming call only at this time
		String callId = DATA.incommingCallId;

		RequestParams params = new RequestParams();
		//params.put("call_start_time",DATA.call_start_time);
		//params.put("call_end_time", DATA.call_end_time);
		//params.put("virtual_visit_id", DATA.virtual_visit_id);

		ApiManager apiManager = new ApiManager(ApiManager.END_CALL+"/"+callId,"get",params,apiCallBack, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();

	}
	 
	
	
	/*Picasso.with(activity).load(allLatlongs.get(position).getImage()).placeholder(R.drawable.icon_call_screen).
	error(R.drawable.icon_call_screen).into(viewHolder.ivDr);*/
	
	
	
	/*public static String imageBaseUrl = "http://3melements.com/onlineclinic/patient_reports/";
	public static String baseUrl = "http://3melements.com/onlineclinic/app/";*/
	
	
/*	public static String baseUrl = "http://107.172.10.113/~econnect/onlineclinic/app/";
	public static String profileImgUrl ="http://107.172.10.113//~econnect//onlineclinic//images//";*/
	
/*	public static String baseUrl = "http://cbuystore.com/onlineclinic/app/";
	public static String profileImgUrl ="http://cbuystore.com/onlineclinic/images//";*/
	
	
	
	/*public static String baseUrl = "http://67.227.193.186/~onlinecare/index.php/app/";
	public static String profileImgUrl ="http://67.227.193.186/~onlinecare/images/";*/
	
	/*public static String baseUrl = "http://67.227.193.186/~onlinecare/dev/index.php/app/";
	public static String profileImgUrl ="http://67.227.193.186/~onlinecare/dev/images/";*/
	
	//public static String profileImgUrl = "https://onlinecare.com/images/";
		/*public static String profileImgUrl = "https://onlinecare.com/dev/images/";*/
		
		/*public static String baseUrl = "http://cbuystore.com/onlineclinic/index.php/app/";
		public static String profileImgUrl = "http://cbuystore.com/onlineclinic/images/";*/


	//public static String baseUrl = "https://onlinecare.com/index.php/app/";
	//public static String baseUrl = "http://www.elivecare.com/dev/index.php/app/";

	//public static String baseUrl = "https://onlinecare.com/dev/index.php/app2/";

	//public static String baseUrl = "https://onlinecare.azurewebsites.net/dev/index.php/app2/";
}
