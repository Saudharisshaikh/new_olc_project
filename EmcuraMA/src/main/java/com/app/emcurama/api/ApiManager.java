package com.app.emcurama.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;

import static com.app.emcurama.util.DATA.SIGNUP_URL;
import static com.app.emcurama.util.DATA.baseUrl;


public class ApiManager {

	/*if(apiName.equalsIgnoreCase(ApiManager.REMOVE_REFERRED)){
		try {
			JSONObject jsonObject = new JSONObject(content);
		} catch (JSONException e) {
			e.printStackTrace();
			customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
		}
	}*/
	/*RequestParams params = new RequestParams();
        params.put("user_id", prefs.getString("id", ""));
        params.put("user_type", "doctor");

	ApiManager apiManager = new ApiManager(ApiManager.API_GET_ROOMS,"post",params,apiCallBack, activity);
        apiManager.loadURL();*/

	//{"error":"expired_token","error_description":"The access token provided has expired"}  fix this in all apps
	//public static final String BASE_URL = "https://onlinecare.com/dev/index.php/app/";
	/*try {
		      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		      trustStore.load(null, null);
		      MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
		      sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		      client.setSSLSocketFactory(sf);
		    }
		    catch (Exception e) {
		    	DATA.print("-- exception ");
		    	e.printStackTrace();
		    }*/

	public static final String API_STATUS_SUCCESS = "onSuccess";
	public static final String API_STATUS_ERROR = "onFailure";

	public static final String GET_HOSPITAL = "hospitals/gethospital";
	public static final String HOSPITALS = "hospitals";
	public static final String DOCTOR_SIGNUP = "doctors/signup";//"doctorSignup";
	public static final String DOCTOR_LOGIN = "doctors/login";
	//public static final String DOCTOR_LOGIN = "login";

	public static final String API_GET_OTNOTES = "doctor/getotnotes";
	public static final String API_GET_ROOMS = "groupchat/getrooms";
	public static final String SEARCHALLDOCTORS_ = "searchAllDoctors_";
	public static final String CREATE_GROUP = "groupchat/creategroup";
	public static final String ADD_TO_GROUP = "groupchat/adduser";
	public static final String GET_GROUP_MESSAGES = "groupchat/getmessages";
	public static final String SEND_GROUP_MESSAGE = "groupchat/sendmessage";
	public static final String REMOVE_GROUP_USER = "groupchat/removeuser";
	public static final String GET_PATIENT_BY_CATEGORY = "getPatientBycategory";
	public static final String FIND_NURSE = "find_nurse";
	public static final String GET_MY_LIVE_CHECKUPS = "doctor/getAllLiveCheckups";//"getMyLiveCheckups";
	public static final String MATCHREFEREDSLOTS = "matchreferedSlots";
	public static final String SCREENING_TOOL = "patient/screeningtool";
	public static final String VIEW_BILL = "viewBill";
	public static final String GET_DOCTOR_BY_CLINIC = "getDoctorsByClinic";
	public static final String INVITE_DOCTOR = "doctor/invitedoctor";
	public static final String GET_INVITES = "doctor/getinvites";
	public static final String INVITE_RESPONCE = "doctor/inviteresponse";
	public static final String GET_PATIENT_NOTES = "getPatientNotes";
	public static final String NOTES_DOCTORS = "doctor/notesdoctors";
	public static final String AVAILABLE_FOR_OLC = "doctor/availableforolc";
	public static final String GET_PRESCRIPTIONS = "getPrescriptions";
	public static final String GET_NURSE_PRESCRIPTIONS = "prescription/getPrescriptions";
	public static final String APPROVE_PRESCRIPTION = "prescription/approvePrescription";
	public static final String PENDING_REFERRALS = "doctor/pending_referrals";
	public static final String APPROVE_REFERRAL = "doctor/approve_referral";
	public static final String PATIENT_DETAIL = "patient/patient_detail";
	public static final String PATIENT_DETAIL_URGENTCARE = "patient/patient_detail_urgentcare";
	public static final String GET_TELEMEDICINE_SERVICES = "getTelemedicineServices";
	public static final String SAVE_FAVOURITE_SERVICES = "doctor/save_favorite_services";
	public static final String REMOVE_FAVOURITE_SERVICES = "doctor/remove_favorite_services";
	public static final String DEPRESSION_SCALE = "doctor/depression_scale";
	public static final String BRADEN_SCALE = "doctor/braden_sacle";
	public static final String FALL_RISK_ASSESMENT = "doctor/fall_risk_assessment";
	public static final String ENVIR_ASSESMENT = "doctor/environmental_assessment";
	public static final String ALL_FALL_RISK_ASSESMENT = "patient/fall_risk_assessment";//for all list data
	public static final String ALL_BRADEN_SCALE = "patient/braden_scale";//for all list data
	public static final String ALL_DEPRESSION_SCALE = "patient/depression_scale";//for all list data
	public static final String ALL_ENVIR_ASSES = "patient/environmental_assesment";
	public static final String SCREENINGTOOL_REPORT = "patient/screeningtoolreport";
	public static final String SAVE_ANWERS = "patient/saveanswers";
	public static final String GET_FAX_HISTORY = "doctor/getfaxhistory";
	public static final String FORGOT_PASSWORD = "doctors/forgotPassword";
	public static final String GET_DOCTOR_TRANSACTIONS = "getdoctorTransactions";
	public static final String CREATE_TOKEN = "auth/createToken";
	public static final String GET_MESSAGES_CONVERSATIONS = "getMessagesConversation/doctor/";
	public static final String GET_PATIENT_REFILL_REQUESTS = "getPatientsRefillRequests";
	public static final String PATIENT_REFILL_REQUEST_RESPONSE = "patientsRefillRequestResponse";
	public static final String CALL_HISTORY = "call_history";
	public static final String END_CALL = "endcall";
	public static final String CALLING_ENDCALL = "calling/endcall/";
	public static final String GET_OT_REPORTS = "patient/getOtReports";
	public static final String SAVE_PROGRESS_NOTE = "patient/save_progress_note";
	public static final String GET_PROGRESS_NOTE = "patient/getProgressNotes";
	public static final String SINGLE_PROGRESS_NOTE = "patient/singleProgressNote";
	public static final String SINGLE_PREVIOUS_PROGRESS_NOTE = "patient/singlePreviousProgressNote";
	public static final String REFERRED_PATIENTS = "doctor/referredPatients";
	public static final String REMOVE_REFERRED = "doctor/remove_referred";
	public static final String SAVE_PRESCRIPTION = "savePrescriptions";
	public static final String PENDING_OT_REFERRALS = "doctor/pending_ot_referrals";
	public static final String APPROVE_OT_REFERRAL = "doctor/approve_ot_referral";
	public static final String JOINED_MEMBERS = "calling/joined_members";
	public static final String GET_MY_HOSPITALS = "doctor/getmyhospitals";
	public static final String SWITCH_HOSPITAL = "doctor/switch_hospital";
	public static final String DELETE_MESSAGE = "messages/delete_message";
	public static final String LOGOUT = "logout";
	public static final String BILL_WITHOUT_NOTE = "doctor/billWithoutNote";
	public static final String BILL_WITHOUT_NOTE_CALL_HISTORY = "notes/billWithoutNote";
	public static final String PATIENT_DETAIL_APPTMNT = "patient/patient_detail_appointment/";
	public static final String GET_MEDICAL_HISTORY = "getMedicalHistory/";
	public static final String REFER_TO_PCP = "referToPCP";
	public static final String SEND_MEDICAL_NOTE = "doctor/sendMedicalNote";
	public static final String GET_MEDICAL_NOTE_TXT = "doctor/getMedicalNoteText";
	public static final String GET_PRESC_TEMPLATES = "prescription_templates/get_templates";
	public static final String ALL_CALLS = "doctor/all_calls";
	public static final String GET_LABS = "patient/getLabs";
	public static final String SAVE_LABS = "patient/save_lab_request";
	public static final String SAVE_FAV_LABS = "doctor/save_favorite_labcode";
	public static final String REM_FAV_LABS = "doctor/remove_favorite_labcode";
	public static final String GET_PATIENT_ENCOUNTER_NOTES = "notes/getEncounterNotes";
	public static final String SMS_GET_APPS = "sms/getapps";
	public static final String SMS_SEND_INVITE = "sms/sendinvite";
	public static final String INSTANT_PATIENT = "patient/instantPatient";
	public static final String CALLING_INVITE_CALL = "calling/invite_call";

	public static final String BHEALTH_GETDIAG = "bhealth/getDiagnosis";
	public static final String BHEALTH_GETGOALS = "bhealth/getGoals";
	public static final String BHEALTH_SAVE_THERAPY_NOTE = "bhealth/saveTherapyNote";
	public static final String BHEALTH_GET_THERAPIST_DOC = "bhealth/getTherapistDoctors";
	public static final String BHEALTH_GET_PSYCHIATIST_DOC = "bhealth/getPsychiatristDoctors";
	public static final String BHEALTH_REFFRED_PATIENT = "bhealth/referedpatient";
	public static final String BHEALTH_REFFRED_PATIENTS = "bhealth/referredpatients";//Listing
	public static final String BHEALTH_APPROVE_REFERED = "bhealth/approve_referred";
	public static final String BHEALTH_DECLINE_REFERED = "bhealth/decline_referred";

	public static final String MESSAGES_CHECK_BLOCKPATIENT = "messages/checkblock_patient";
	public static final String MESSAGES_BLOCK_PATIENT = "messages/blockpatient";
	public static final String MESSAGES_UNBLOCK_PATIENT = "messages/unblock_patient";

	public static final String COVID_FORMS_LIST = "ma/ctesting";//"ctesting/get_doctor_listing";
	public static final String CHANGE_COVID_FORM_STATUS = "ctesting/change_form_status";
	public static final String COVID_BILLING_CODES = "ctesting/billingcodes";
	public static final String COVID_SEND_RESULTS = "ctesting/save_result";
	public static final String COVID_SAVE_VITALS = "ctesting/save_vitals";
	public static final String GET_ALL_DOC = "doctor/getAllDoctors";
	public static final String ASSIGN_PROVIDER = "ma/assign_provider";
	public static final String COVID_TEST_LOCATIONS = "ctesting/testlocations";
	public static final String COVID_REMOVE = "ctesting/remove";

	public static final String GET_LIVECARE_REQUESTS = "ma/getlivecareRequests";
	public static final String GET_URGENT_CARE_CENTERS = "doctor/getUrgentcares";
	public static final String DOCTORS_BY_URGENTCARE = "doctor/doctors_by_urgentcare/";
	public static final String ASSIGN_LIVECARE = "doctor/assinglivecare";


	public static final String JSON_BASE_URL = DATA.ROOT_Url+"jsonfiles/";
	public static final String DEPRESSION_FIELDS = "depression_fields.json";
	public static final String HOMECARE_FORM_FIELDS = "homecare_form_fields.json";
	public static final String ENVIR_FORM_FIELDS = "environment_fields.json";

	public static final String APP_LABELS_JSON = "app_labels.json";
	public static final String PREF_APP_LBL_KEY = "app_labels_json";


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
	public static boolean shouldShowPD = true;

	public ApiManager(String api, String getOrPost, RequestParams params, ApiCallBack apiCallBack, Activity activity) {
		// TODO Auto-generated constructor stub
		this.api = api;
		this.getOrPost = getOrPost;
		this.params = params;
		this.apiCallBack = apiCallBack;
		this.activity = activity;
		this.prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
		//customProgressDialog = CustomProgressDialog.getCustomProgressDialogInstance(activity);
		customProgressDialog = new Dialog_CustomProgress(activity);

		if(api.equals(DOCTOR_LOGIN)){
			customURL = SIGNUP_URL+DOCTOR_LOGIN;
		}else if(api.equals(FORGOT_PASSWORD)){
			customURL = SIGNUP_URL+FORGOT_PASSWORD;
		}else if(api.equals(HOSPITALS)){
			customURL = SIGNUP_URL+HOSPITALS;
		}else if(api.equals(GET_HOSPITAL)){
			customURL = SIGNUP_URL+GET_HOSPITAL;
		}else if(api.equals(DOCTOR_SIGNUP)){
			customURL = SIGNUP_URL + DOCTOR_SIGNUP;
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
						new GloabalMethods(activity).checkLogin(content,statusCode);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
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


	public void setupKeystores(AsyncHttpClient client){
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
	}
}
