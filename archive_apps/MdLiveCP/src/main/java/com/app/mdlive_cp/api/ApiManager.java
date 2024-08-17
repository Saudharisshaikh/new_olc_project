package com.app.mdlive_cp.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;

import static com.app.mdlive_cp.util.DATA.SIGNUP_URL;
import static com.app.mdlive_cp.util.DATA.baseUrl;


public class ApiManager {

	/*try {
		      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		      trustStore.load(null, null);
		      MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
		      sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		      client.setSSLSocketFactory(sf);
		    }
		    catch (Exception e) {
		    	System.out.println("-- exception ");
		    	e.printStackTrace();
		    }*/

	//public static final String BASE_URL = "https://onlinecare.com/dev/index.php/app/";

	public static final String API_STATUS_SUCCESS = "onSuccess";
	public static final String API_STATUS_ERROR = "onFailure";

	//public static final String API_LOGIN = "login";

	public static final String HOSPITALS = "hospitals";
	public static final String DOCTOR_SIGNUP = "doctors/signup";//"doctorSignup";
	public static final String DOCTOR_LOGIN = "doctors/login";
	public static final String CHECK_USERNAME_AVAILABLITY = "patients/checkAvailability";

	public static final String API_GET_OTNOTES = "doctor/getotnotes";

	public static final String API_GET_ROOMS = "groupchat/getrooms";
	public static final String CREATE_GROUP = "groupchat/creategroup";
	public static final String ADD_TO_GROUP = "groupchat/adduser";
	public static final String GET_GROUP_MESSAGES = "groupchat/getmessages";
	public static final String SEND_GROUP_MESSAGE = "groupchat/sendmessage";
	public static final String REMOVE_GROUP_USER = "groupchat/removeuser";
	public static final String NURSE_PATIENTS = "groupchat/nursepatients";

	public static final String SEARCHALLDOCTORS_ = "searchAllDoctors_";
	public static final String GET_DOCTORS_BY_CLINIC = "getDoctorsByClinic";
	public static final String FIND_NURSE = "find_nurse";
	public static final String GET_PATIENT_NOTES = "getPatientNotes";
	public static final String GET_PENDING_NOTES = "patient/getPendingNotes";
	public static final String NOTES_DOCTORS = "doctor/notesdoctors";
	public static final String SCREENINGTOOL_REPORT = "patient/screeningtoolreport";
	public static final String SAVE_ANWERS = "patient/saveanswers";
	public static final String SAVE_VIRTUAL_VISIT = "patient/save_virtual_visit";
	public static final String GET_MEDICAL_HISTORY = "getMedicalHistory";
	public static final String SAVE_MEDICAL_HISTORY = "saveMedicalHistory";
	public static final String GET_TELEMEDICINE_SERVICES = "getTelemedicineServices";
	public static final String SAVE_FAVOURITE_SERVICES = "doctor/save_favorite_services";
	public static final String REMOVE_FAVOURITE_SERVICES = "doctor/remove_favorite_services";
	public static final String PATIENT_DETAIL = "patient/patient_detail";
	public static final String DEPRESSION_SCALE = "doctor/depression_scale";
	public static final String BRADEN_SCALE = "doctor/braden_sacle";
	public static final String FALL_RISK_ASSESMENT = "doctor/fall_risk_assessment";
	public static final String ENVIR_ASSESMENT = "doctor/environmental_assessment";
	public static final String ALL_FALL_RISK_ASSESMENT = "patient/fall_risk_assessment";//for all list data
	public static final String ALL_BRADEN_SCALE = "patient/braden_scale";//for all list data
	public static final String ALL_DEPRESSION_SCALE = "patient/depression_scale";//for all list data
	public static final String ALL_ENVIR_ASSES = "patient/environmental_assesment";
	public static final String GET_FAX_HISTORY = "doctor/getfaxhistory";
	public static final String FORGOT_PASSWORD = "doctors/forgotPassword";
	public static final String GET_PRESCRIPTIONS = "getPrescriptions";
	public static final String CREATE_TOKEN = "auth/createToken";
	public static final String GET_MESSAGES_CONVERSATIONS = "getMessagesConversation/specialist/";
	public static final String GET_PATIENT_BY_CATEGORY = "getPatientBycategory";
	public static final String MY_DOCTORS = "mydoctors";
	public static final String FOLLOWUP_PATIENTS = "followup_patients";
	public static final String CREATE_FOLLOWUP = "createfollowup";
	public static final String CAREPROVIDER_SCHEDULES = "careprovider_schedules";
	public static final String CAREPROVIDER_APPROVED_REFERRALS = "doctor/careprovider_approved_referrals";
	public static final String CAREPROVIDER_OT_REFERRALS = "doctor/careprovider_OT_referrals";
	public static final String ADD_NEW_PATIENT = "patient/createPatient_by_careprovider";
	public static final String END_CALL = "endcall";
	public static final String CALLING_ENDCALL = "calling/endcall/";
	public static final String GET_OT_REPORTS = "patient/getOtReports";
	public static final String GET_DRUGS = "getDrugs";
	public static final String SAVE_PROGRESS_NOTE = "patient/save_progress_note";
	public static final String GET_PROGRESS_NOTE = "patient/getProgressNotes";
	public static final String SINGLE_PROGRESS_NOTE = "patient/singleProgressNote";
	public static final String SINGLE_PREVIOUS_PROGRESS_NOTE = "patient/singlePreviousProgressNote";
	public static final String SEND_MESSAGE = "sendmessage";
	public static final String REFERED_PATIENT = "doctor/referedpatient";
	public static final String MATCHREFEREDSLOTS = "matchreferedSlots";
	public static final String JOINED_MEMBERS = "calling/joined_members";
	public static final String DELETE_MESSAGE = "messages/delete_message";
	public static final String GET_SCHEDULE_VISIT = "doctor/getScheduleVisit";
	//care plan APIs starts
	public static final String GET_CARE_PLAN = "careplan/getCarePlan";
	public static final String GET_CARE_PLAN_BY_ID = "careplan/getCarePlanById";
	public static final String ADD_CARE_PLAN = "doctor/addcareplan";
	public static final String CARE_TEAM_BY_TYPE = "careplan/careTeamByType/";
	public static final String ADD_TEAM_MEMBER = "careplan/addteammember/";
	public static final String GET_CARE_PLAN_GOALS = "careplan/getCarePlanGoals";
	public static final String ADD_GOAL = "careplan/addGoal";
	public static final String MARK_ACCOMPLISH = "careplan/markAccumplish";
	public static final String GET_GOAL_PROGRESS = "careplan/getGoalProgress/";
	public static final String ADD_GOAL_PROGRESS = "doctor/addGoalProgress";
	public static final String GET_CARE_PLAN_DIAG = "careplan/getCarePlanDiagnoses";
	public static final String GET_CARE_PLAN_ALLERGIES = "careplan/getCarePlanAllergies";
	public static final String GET_CARE_PLAN_MED = "careplan/getmedications/";
	public static final String GET_CARE_PLAN_HOSP = "careplan/getCarePlanHospitalization";
	public static final String GET_CARE_PLAN_PROC = "careplan/getCarePlanProcedures";
	public static final String GET_CARE_PLAN_IMMUN = "careplan/getCarePlanImmunizations";
	public static final String GET_ICD_CODES = "doctor/getIcdCodes";
	public static final String ADD_CP_DIAG = "careplan/addCareplanDiagnosis";//replace doctor with careplan
	public static final String ADD_CP_ALLERGIES = "careplan/addCareplanAllergies";
	public static final String SAVE_CP_MEDICATION = "doctor/saveCarePlanMedication";
	public static final String GET_CP_INSURANCE = "careplan/getCarePlanInsuranceProviders";
	public static final String DELETE_CARE_PLAN = "careplan/delete_careplan";
	public static final String ADD_CARE_PLAN_HOSP = "doctor/addCareplanHospitalization";
    public static final String ADD_CARE_PLAN_PROC = "doctor/addCarePlanProcedures";
	public static final String GET_VACCINES = "careplan/getVaccines";
	public static final String GET_SUB_VACCINES = "careplan/getSubVaccines/";
	public static final String ADD_CARE_PLAN_IMMUN = "careplan/addCarePlanImmunizations";
	public static final String DELETE_CARE_MEMBER = "careplan/delete_careplan_member";
	public static final String DELETE_PROC_SURG = "careplan/delete_careplan_procedure";
	//care plan APIs ends
	public static final String LOGOUT = "logout";
	public static final String DEP_INV_LIST = "patient/depression_inventory_list/";
	public static final String SAVE_DEP_INV = "patient/depression_inventory";
	public static final String SYMP_COND = "general/symptoms_conditions";


	public static final String GET_REPORTS = "getReports";
	public static final String CREATE_FOLDER = "create_folder";
	public static final String DELETE_FOLDER = "deleteFolder/";
	public static final String DELETE_FILE = "deleteFile/";
	public static final String GET_REPORTS_BY_FOLDER_ID = "getreportsByfolder/";
	public static final String UPLOAD_REPORT = "uploadReports";

	//Reliance APIs starts here
	public static final String GET_ALL_COMPANIES = "patient/getAllCompanies";
	public static final String GET_COMPANIES = "patient/getCompanies";
	public static final String GET_ALL_DOCTORS = "doctor/getAllDoctors";
	public static final String GET_PATIENTS_BY_COMPANY = "patient/getPatients_byCompany";
	public static final String CHANGE_PATIENT_STATUS = "patient/changeStatus";
	public static final String SAVE_PHQ_FORM = "assessments/phq_form";
	public static final String PHQ_LIST = "assessments/phq_list";
	public static final String GAD_LIST = "assessments/gad_list";
	public static final String GAD_FORM_SAVE = "assessments/gad_form";
	public static final String DAST_LIST = "assessments/dast_list";
	public static final String DAST_FORM_SAVE = "assessments/dast_form";
	public static final String SDSH_FORM_SAVE = "assessments/sdsh_form";
	public static final String SDSH_LIST = "assessments/sdsh_list";
	public static final String DIET_ASSES_LIST = "assessments/dietitary_assessment_list";
	public static final String DIET_ASSES_FORM_SAVE = "assessments/Dietitary_Assessment_form";
	public static final String SMG_FORM_LIST = "assessments/self_management_goals_list";
	public static final String SMG_FORM_SAVE = "assessments/save_self_management";
	public static final String OT_EVA_FORM_SAVE = "assessments/ot_evaluation_form";
	public static final String OT_EVALUATION_LIST = "assessments/ot_evaluation_list";
	public static final String OT_PROFILE_FORM_SAVE = "assessments/ot_profile_form";
	public static final String OT_PROFILE_LIST = "assessments/ot_profile_list";
	public static final String ADL_FORM_LIST = "assessments/adl_list";
	public static final String SAVE_ADL_FORM = "assessments/adl_form";
	public static final String IADL_FORM_LIST = "assessments/iadl_list";
	public static final String SAVE_IADL_FORM = "assessments/iadl_form";
	public static final String ASSESSMENT_SUMMARY = "assessments/assessment_summary/";
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

	//Counter APIs starts
	public static final String FALL_LIST = "counter/fall_list";
	public static final String ADD_FALL = "counter/save_fall";
	public static final String HOSP_ADM_LIST = "counter/hospital_admission_list";
	public static final String ADD_HOSP_ADM = "counter/save_hospital_admission";
	public static final String EMERG_ROOM_LIST = "counter/emergency_room_list";
	public static final String ADD_EMERG_ROOM = "counter/save_emergency_room";
	public static final String NURSING_HOME_LIST = "counter/nursing_home_hospital_admission_list";
	public static final String ADD_NURSING_HOME = "counter/save_nursing_home_hospital_admission";
	public static final String SAVE_ENC_HOSP = "counter/save_encounter_hospital";
	public static final String GET_ENC_HOSP = "counter/get_encounter_hospital";
	//Counter APIs ends
	//IDT Notes APIs starts
	public static final String IDT_NOTE_LIST = "assessments/idt_note_list";
	public static final String VIEW_IDT = "assessments/view_idt/";
	public static final String ADD_IDT = "assessments/idt_form";
	public static final String IDT_NOTES_BY_DATE = "assessments/idt_notes_by_date";
	public static final String LATEST_CAREPLAN_GOALS = "careplan/latest_careplan_goals";
	public static final String IDT_UPDATE = "assessments/idt_update";
	//IDT Notes APIs ends
	//CareTeam
	public static final String CARE_TEAM = "doctor/care_team";
	public static final String ADD_CARETEAM = "request_to_add_nurse";
	public static final String DEL_CARETEAM = "delete_care_management";

	public static final String API_MEDHISTORY = "apimedhistory/view";

	//Reliance APIs ends here


	//MdLive CP Apis starts here
	public static final String GET_DOCTORS_BY_COMPANY = "doctor/getDoctors_byCompany";
	public static final String GET_PATIENTS_BY_CP_DOC = "patient/getPatients_byCpDoc";
	//MdLive CP Apis ends here

	//Greal Lakes Apis starts here
	public static final String MY_PATIENTS = "cp/myPatients/";
	//Greal Lakes Apis ends here

	public static final String SMS_GET_APPS = "sms/getapps";
	public static final String SMS_SEND_INVITE = "sms/sendinvite";
	public static final String INSTANT_PATIENT = "patient/instantPatient";
	public static final String CALLING_INVITE_CALL = "calling/invite_call";
	public static final String GET_MY_LIVE_CHECKUPS = "getMyLiveCheckups";
	public static final String PATIENT_DETAIL_URGENTCARE = "patient/patient_detail_urgentcare";

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

	public static final String UPDATE_VIRTUAL_VISIT = "patient/update_virtual_visit";

	public static final String CALL_HISTORY = "call_history";
	public static final String GET_PATIENT_ENCOUNTER_NOTES = "notes/getEncounterNotes";
	public static final String BILL_WITHOUT_NOTE = "doctor/billWithoutNote";
	public static final String BILL_WITHOUT_NOTE_CALL_HISTORY = "notes/billWithoutNote";
	public static final String GET_MEDICAL_NOTE_TXT = "doctor/getMedicalNoteText";
	public static final String SEND_MEDICAL_NOTE = "doctor/sendMedicalNote";


	public static final String JSON_BASE_URL = DATA.ROOT_Url+"jsonfiles/";
	public static final String DEPRESSION_FIELDS = "depression_fields.json";
	public static final String HOMECARE_FORM_FIELDS = "homecare_form_fields.json";
	public static final String ENVIR_FORM_FIELDS = "environment_fields.json";
	public static final String DEP_INV_JSON = "depression_inventory.json";

	public static final String SDHS_FORM = "sdsh_form.json";
	public static final String PHQ_FORM = "phq_form.json";
	public static final String GAD_FORM = "gad_form.json";
	public static final String DAST_FORM = "dast_form.json";
	public static final String DIET_ASSES_FORM = "dietitary_assessment.json";
	public static final String IADL_FORM = "iadl.json";
	public static final String ADL_FORM = "adl.json";

	public static final String OCD_FORM = "ocd.json";
	public static final String FCSAS_FORM = "fcsas.json";
	public static final String PANIC_ATTACK_FORM = "panic_attack.json";
	public static final String SCOFF_FORM = "scoff.json";
	public static final String STRESS_QUES_FORM = "stress_questionnaire.json";

	public static final String APP_LABELS_JSON = "app_labels.json";
	public static final String PREF_APP_LBL_KEY = "app_labels_json";


	private String api = "";
	private String getOrPost = "";
	private RequestParams params;
	private ApiCallBack apiCallBack;
	private Activity activity;
	private SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	CustomSnakeBar customSnakeBar;
	//CustomProgressDialog customProgressDialog;
	Dialog_CustomProgress customProgressDialog;
	public static boolean shouldShowPD = true;

	String customURL = "";

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
		}else if(api.equals(DOCTOR_SIGNUP)){
			customURL = SIGNUP_URL + DOCTOR_SIGNUP;
		}else if(api.equals(CHECK_USERNAME_AVAILABLITY)){
			customURL = SIGNUP_URL + CHECK_USERNAME_AVAILABLITY;
		}else if(api.endsWith(".json")){
			customURL = JSON_BASE_URL+api;
		}else{
			customURL = baseUrl+api;
		}
		System.out.println("-- customURL in apimanager contructor: "+customURL);
		if(params != null){
			System.out.println("-- params in : "+api+" : "+params.toString());
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

		setupKeystores(client);

		System.out.println("-- token: "+"Bearer "+prefs.getString("access_token",""));
		client.addHeader("Oauthtoken","Bearer "+prefs.getString("access_token",""));

		 if (getOrPost.equalsIgnoreCase("post")) {

				 client.post(customURL,params, new AsyncHttpResponseHandler() {
				 	@Override
					 public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						 // called when response HTTP status is "200 OK"
						 customProgressDialog.dismissProgressDialog();
						 try{
							 String content = new String(response);
							 System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content);
							 apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);
						 }catch (Exception e){
							 e.printStackTrace();
							 System.out.println("-- responce onsuccess: "+api+", http status code: "+statusCode+" Byte responce: "+response);
							 customSnakeBar.showToast(DATA.CMN_ERR_MSG);
						 }
					 }

					 @Override
					 public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
						 // called when response HTTP status is "4XX" (eg. 401, 403, 404)
						 customProgressDialog.dismissProgressDialog();
						 try {
							 String content = new String(errorResponse);
							 System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content);
							 //apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
							 new GloabalMethods(activity).checkLogin(content);
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
						System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content);
						apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("-- responce onsuccess: "+api+", http status code: "+statusCode+" Byte responce: "+response);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					customProgressDialog.dismissProgressDialog();
					try {
						String content = new String(errorResponse);
						System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+": status: "+ API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content);
						//apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);
						new GloabalMethods(activity).checkLogin(content);
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
		 System.out.println("-- token: "+"Bearer "+prefs.getString("access_token",""));
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
			System.out.println("-- exception ");
			e.printStackTrace();
		}
	}
}
