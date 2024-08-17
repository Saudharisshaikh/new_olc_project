package com.app.msu_uc.api;

import static com.app.msu_uc.util.DATA.baseUrl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.msu_uc.util.CheckInternetConnection;
import com.app.msu_uc.util.CustomToast;
import com.app.msu_uc.util.DATA;
import com.app.msu_uc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class ApiManager {

	//public static final String BASE_URL = "https://onlinecare.com/dev/index.php/app/";

	public static final String API_STATUS_SUCCESS = "onSuccess";
	public static final String API_STATUS_ERROR = "onFailure";

	public static final String GET_HOSPITAL = "hospitals/gethospital";
	public static final String HOSPITALS = "hospitals";
	public static final String PATIENT_SIGNUP = "patient/patientSignup";
	public static final String PATIENT_LOGIN = "patient/login";
	public static final String PATIENT__SOCIAL_LOGIN = "patients/social_login";
	public static final String CHECK_USERNAME_AVAILABLITY = "patients/checkAvailability";

	//public static final String PATIENT_LOGIN = "login";

	//public static final String API_GET_OTNOTES = "doctor/getotnotes";
	public static final String API_GET_ROOMS = "groupchat/getrooms";
	//public static final String SEARCHALLDOCTORS_ = "searchAllDoctors_";
	//public static final String CREATE_GROUP = "groupchat/creategroup";
	//public static final String ADD_TO_GROUP = "groupchat/adduser";
	public static final String GET_GROUP_MESSAGES = "groupchat/getmessages";
	public static final String SEND_GROUP_MESSAGE = "groupchat/sendmessage";
	public static final String REMOVE_GROUP_USER = "groupchat/removeuser";
	public static final String GET_URGENT_CARES = "doctor/getUrgentcares";
	public static final String DOCTORS_BY_URGENTCARE = "doctor/doctors_by_urgentcare";
	public static final String SEARCH_DOCTOR_BY_ZIPCODE2 = "searchDoctorByZipCode2";
	public static final String CREATE_TOKEN = "auth/createToken";
	public static final String SAVE_TRANSACTION = "saveTransaction";
	public static final String ADD_LIVE_CHECKUP = "addLiveCheckup";
	public static final String CHECK_PATIENT_QUEUE = "checkPatientqueue";
	public static final String CHECK_URGENT_CARE_PATIENT_QUEUE = "patient/checkUrgentPatientqueue";
	public static final String GET_MEDICAL_HISTORY = "getMedicalHistory";
	public static final String SAVE_MEDICAL_HISTORY = "saveMedicalHistory";
	public static final String ACCEPT_FORMS = "patient/accept_forms";
	public static final String PATIENT_FORM_STATUS = "patientFormStatus";
	public static final String FORGOT_PASSWORD = "patient/forgotPassword";
	public static final String FORGOT_PASSWORD_REQUEST = "forgetpassword/forget_request";
	public static final String FORGOT_PASSWORD_VERIFY_OTP = "forgetpassword/verifyOtp";
	public static final String FORGOT_PASSWORD_CHANGE_PASSWORD = "forgetpassword/updatePassword";
	public static final String GET_ALL_DOCTORS = "getAllDoctors";
	public static final String ADD_PRIMARY_CARE = "addPrimaryCare";
    public static final String GET_PRIMARY_CARE = "getPrimaryCare";
	public static final String LIST_PROVIDERS = "patient/list_providers";
	public static final String GET_PATIENT_TRANSACTIONS = "getpatientTransactions";
	public static final String UPDATE_PROFILE = "updateProfile";
	public static final String GET_PRESCRIPTIONS = "getPrescriptions";
	public static final String SAVE_PATIENT_REFILL_REQUEST = "savePatientsRefillRequest";
	public static final String GET_PATIENT_PRESC_DETAILS = "getPatientsPrescriptionDetails";
	public static final String CHANGE_PATIENT_PASS = "changepatientPassword";
	public static final String GET_MESSAGES_CONVERSATIONS = "getMessagesConversation/patient/";
	public static final String VIEW_CONVERSATIONS = "viewConversation/patient/";
	public static final String VIEW_CONVERSATIONS_SPECIALIST = "viewConversationspecialist/patient/";
	public static final String SAVE_INSURANCE_INFO = "saveInsuranceInfo";
	public static final String GET_PAYERS = "getPayers";
	public static final String GET_MY_INSURANCE = "patient/getMyInsurance";
	public static final String SET_DEFAULT_INSURANCE = "patient/setDefaultInsurance";
	public static final String DELETE_INSURANCE = "patient/deleteInsurance";
	public static final String VARIFY_INSURANCE = "patient/getInsuranceData";//this is varify
	public static final String UPDATE_INSURANCE = "patient/updateInsurane";
	//public static final String SEND_MESSAGE_TO_DOCTOR = "sendmessagetoDoctor";
	public static final String SUB_PATIENTS = "subpatients";
	public static final String ADD_SUB_PATIENTS = "addSubPatients";
	public static final String ADD_PRIMARY_PATIENT = "patient/primary_patient";
	public static final String DELETE_MESSAGE = "messages/delete_message";
	public static final String LOGOUT = "logout";
	public static final String PCP_REQUEST = "patient/pcp_request";
	public static final String SYMP_COND = "general/symptoms_conditions";
	public static final String GET_DRUGS = "getDrugs";
	public static final String GET_WAITING_PATIENTS = "getWaitingPatients";
	public static final String SND_MSG_OM = "messages/sendMessage_toOfficeManager";
	public static final String TIMER_ALERT = "patient/timerAlert";
	public static final String SIMPLE_STATES = "doctor/simple_states";
	public static final String MY_FAMILY_MEMBERS = "patient/myfamilymembers";
	public static final String CALLING_TO_FAMILY = "calling/callingToFamily";


	public static final String GET_REPORTS = "getReports";
	public static final String CREATE_FOLDER = "create_folder";
	public static final String DELETE_FOLDER = "deleteFolder/";
	public static final String DELETE_FILE = "deleteFile/";
	public static final String GET_REPORTS_BY_FOLDER_ID = "getreportsByfolder/";
	public static final String UPLOAD_REPORT = "uploadReports";

	public static final String GET_ICD_CODES = "doctor/getIcdCodes";

	public static final String GET_HOSPITALS_DATA = "hospitals/getHospitalData";
	public static final String GET_HOSPITALS_DATA_BY_ID = "hospitals/getHospitalDataById";//param: hospital_id //for apps with no clinical code screen

	public static final String GET_ALL_DEVICES = "getAllDevices";
	public static final String GET_MY_DEVICES = "myDevices";

	public static final String GET_DOCTOR_SLOTS2 = "doctor/getDoctorsSlots";
	public static final String GET_DOCTOR_SLOTS = "getDoctorsSlots";

	public static final String CHECK_CLINIC_TIMING = "patient/check_clinic_timing/";
	public static final String CHECK_FREE_CODE = "patient/check_free_code";
	public static final String SEARCH_DOCTOR = "searchDoctor/";
	public static final String STRIPE_CHARGE = "payments/stripe_charge";
	public static final String END_CALL = "endcall";
	public static final String PAYMENT_CREDS = "payments/credentials";

	public static final String BHEALTH_IMMCARE = "bhealth/immediatecare";
	public static final String BHEALTH_GET_WAITING_PATIENTS = "bhealth/getWaitingPatients/";
	public static final String BHEALTH_EXIT_ROOM = "bhealth/exitroom";
	public static final String BHEALTH_GET_THERAPIST_DOC = "bhealth/getTherapists_doctors";//Symptom Based Doctors
	public static final String BHEALTH_GET_THERAPIST_DOC_OLD = "bhealth/getTherapistDoctors";
	public static final String BHEALTH_BOOK_APPOINTMENT = "bhealth/bookAppointment";
	public static final String BHEALTH_SAVE_RATING = "bhealth/saveRating";

	//================Behavioural Healthth/ Emotional Welness assessments APIs start===============================
	public static final String SAVE_DEP_INV = "patient/depression_inventory";
	public static final String DEP_INV_LIST = "patient/depression_inventory_list/";

	public static final String SAVE_OCD_FORM = "assessments/ocd_form";
	public static final String SAVE_FCSAS_FORM = "assessments/fcsas_form";
	public static final String SAVE_PANIC_ATT_FORM = "assessments/panic_attack_form";
	public static final String SAVE_SCOFF_FORM = "assessments/scoff_form";
	public static final String OCD_LIST = "assessments/ocd_list";
	public static final String FCSAS_LIST = "assessments/fcsas_list";
	public static final String PANIC_ATT_LIST = "assessments/panic_attack_list";
	public static final String SCOFF_LIST = "assessments/scoff_list";
	public static final String SAVE_STRESS_QUES_FORM = "assessments/stress_form";
	public static final String STRESS_QUES_LIST = "assessments/stress_list";

	public static final String SAVE_PHQ_FORM = "assessments/phq_form";
	public static final String PHQ_LIST = "assessments/phq_list";
	public static final String GAD_LIST = "assessments/gad_list";
	public static final String GAD_FORM_SAVE = "assessments/gad_form";
	public static final String DAST_LIST = "assessments/dast_list";
	public static final String DAST_FORM_SAVE = "assessments/dast_form";

	public static final String LOAD_EMO_WELL_OPTIONS = "bhealth/eoptions";
	public static final String BHEALTH_JOIN_APTMNT = "bhealth/join_appointment";
	//==============================Behavioural Healthth/ Emotional Welness assessments APIs ends=================

	public static final String MESSAGES_CHECK_BLOCKPATIENT = "messages/checkblock_patient";
	public static final String MESSAGES_BLOCK_PATIENT = "messages/blockpatient";
	public static final String MESSAGES_UNBLOCK_PATIENT = "messages/unblock_patient";

	public static final String SYMP_COND_BHELTH = "general/emotional_symptoms_conditions";
	public static final String SYMP_COND_ALL = "general/all_symptoms_conditions";


	public static final String BRAINTREE_PAYMENT_PROCESS = "patient/braintree_payment_process";

	public static final String PATIENT_AUTH = "patient/patient_authorization";


	//otp verify apis
	public static final String OTP_VERIFY = "patient_registration/verifyOtp";
	public static final String RESEND_OTP_CODE = "patient_registration/resendOtp";
	public static final String SOCIAL_SENDOTP = "patient_registration/social_otp";
	public static final String OTP_VERIFY_SOCIAL = "patient_registration/verifySocialOtp";

	//Delete Account Api's
	public static final String DEL_ACC_OTPREQUEST = "delete_account/otp_request";
	public static final String DEL_ACC_OTP_VERIFY = "delete_account/verifyotp";

	//get states
	public static final String GET_STATES = "general/getstates";

	public static final String JSON_BASE_URL = DATA.ROOT_Url+"jsonfiles/";
	public static final String APP_LABELS_JSON = "app_labels.json";
	public static final String PREF_APP_LBL_KEY = "app_labels_json";

	public static final String DEP_INV_JSON = "depression_inventory.json";
	public static final String OCD_FORM = "ocd.json";
	public static final String FCSAS_FORM = "fcsas.json";
	public static final String PANIC_ATTACK_FORM = "panic_attack.json";
	public static final String SCOFF_FORM = "scoff.json";
	public static final String STRESS_QUES_FORM = "stress_questionnaire.json";

	public static final String PHQ_FORM = "phq_form.json";
	public static final String GAD_FORM = "gad_form.json";
	public static final String DAST_FORM = "dast_form.json";




	private String api = "";
	private String getOrPost = "";
	private RequestParams params;
	private ApiCallBack apiCallBack;
	private Activity activity;
	private SharedPreferences prefs;

	String customURL = "";

	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	CustomSnakeBar customSnakeBar;
	//CustomProgressDialog customProgressDialog;
	Dialog_CustomProgress customProgressDialog;
	public static boolean shouldShowLoader = true;

	public ApiManager(String api, String getOrPost, RequestParams params, ApiCallBack apiCallBack, Activity activity) {
		// TODO Auto-generated constructor stub
		this.api = api;
		this.getOrPost = getOrPost;
		this.params = params;
		this.apiCallBack = apiCallBack;
		this.activity = activity;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
		//customProgressDialog = CustomProgressDialog.getCustomProgressDialogInstance(activity);
		customProgressDialog = new Dialog_CustomProgress(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		if(api.equals(PATIENT_LOGIN)){
			//customURL = DATA.SIGNUP_URL+PATIENT_LOGIN;
			customURL = baseUrl+api;
		}else if(api.equals(FORGOT_PASSWORD)){
			customURL = DATA.SIGNUP_URL+FORGOT_PASSWORD;
		}else if(api.equals(PATIENT__SOCIAL_LOGIN)){
			//customURL = SIGNUP_URL+PATIENT__SOCIAL_LOGIN;// changed by zohaib bhai 13-02-2023
			customURL = baseUrl+api;
		}else if(api.equals(HOSPITALS)){
			customURL = DATA.SIGNUP_URL+HOSPITALS;
		}else if(api.equals(GET_HOSPITAL)){
			customURL = DATA.SIGNUP_URL+GET_HOSPITAL;
		}else if(api.equals(PATIENT_SIGNUP)){
			//customURL = DATA.SIGNUP_URL + PATIENT_SIGNUP;
			customURL = baseUrl+api;//DATA.SIGNUP_URL + PATIENT_SIGNUP;
		}else if(api.equals(CHECK_USERNAME_AVAILABLITY)){
			customURL = DATA.SIGNUP_URL + CHECK_USERNAME_AVAILABLITY;
		}else if(api.equals(GET_HOSPITALS_DATA)){
			customURL = DATA.SIGNUP_URL + GET_HOSPITALS_DATA;
		}else if(api.endsWith(".json")){
			customURL = JSON_BASE_URL+api;
		}else{
			customURL = baseUrl+api;
		}
		//customURL = baseUrl+api;
		DATA.print("-- customURL in apimanager contructor: "+customURL);
		if(params != null){
			DATA.print("-- params in : "+api+" : "+params.toString());

			String p = params.toString();
			p = p.replaceAll("&", "\n");
			p = p.replaceAll("=", ":");

			System.out.println("-- Postman Params : "+p);
		}
	}


	public void loadURL() {

		if (! checkInternetConnection.isConnectedToInternet()){
			//customToast.showToast("No network found. Please check your internet connection and try again.", 0, 1);
			customSnakeBar.showToast(DATA.NO_NETWORK_MESSAGE);
			return;
		}
		 //DATA.showLoaderDefault(activity, "Please wait . . .");
		customProgressDialog.showProgressDialog();
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(1000000000);

		DATA.print("-- token: "+"Bearer "+prefs.getString("access_token",""));
		client.addHeader("Oauthtoken","Bearer "+prefs.getString("access_token",""));

		 if (getOrPost.equalsIgnoreCase("post")) {

				 client.post(customURL,params, new AsyncHttpResponseHandler() {
				 	@Override
					 public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						 // called when response HTTP status is "200 OK"
						 customProgressDialog.dismissProgressDialog();
						 try{
							 String content = new String(response);
							 DATA.print("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content);
							 apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);
						 }catch (Exception e){
							 e.printStackTrace();
							 DATA.print("-- responce onsuccess: "+api+", http status code: "+statusCode+" Byte responce: "+response);
							 customSnakeBar.showToast(DATA.CMN_ERR_MSG);
						 }
					 }

					 @Override
					 public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
						 // called when response HTTP status is "4XX" (eg. 401, 403, 404)
						 customProgressDialog.dismissProgressDialog();
						 try {
							 String content = new String(errorResponse);
							 DATA.print("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content);
							 //apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
							 if(api.contains(ApiManager.GET_WAITING_PATIENTS)){DATA.shouldLiveCareWatingRefresh = 1;}//for livecarewaitingarea
							 customSnakeBar.showToast(DATA.CMN_ERR_MSG);
							 new GloabalMethods(activity).checkLogin(content , statusCode);
						 }catch (Exception e1){
							 e1.printStackTrace();
							 customSnakeBar.showToast(DATA.CMN_ERR_MSG);
						 }
					 }
				 });

		} else {
			client.get(customURL,params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					customProgressDialog.dismissProgressDialog();
					try{
						String content = new String(response);
						DATA.print("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content);
						apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);

					}catch (Exception e){
						e.printStackTrace();
						DATA.print("-- responce onsuccess: "+api+", http status code: "+statusCode+" Byte responce: "+response);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					customProgressDialog.dismissProgressDialog();
					try {
						String content = new String(errorResponse);
						DATA.print("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content);
						//apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
						new GloabalMethods(activity).checkLogin(content , statusCode);
					}catch (Exception e1){
						e1.printStackTrace();
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
					}
				}
			});
		}
		 


	 }


	public static void addHeader(Context context,AsyncHttpClient client){
		SharedPreferences prefs = context.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		DATA.print("-- token: "+"Bearer "+prefs.getString("access_token",""));
		client.addHeader("Oauthtoken","Bearer "+prefs.getString("access_token",""));
	}
}
