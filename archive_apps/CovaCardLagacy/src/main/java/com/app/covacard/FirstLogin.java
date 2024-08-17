package com.app.covacard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.covacard.api.ApiCallBack;
import com.app.covacard.api.ApiManager;
import com.app.covacard.util.CheckInternetConnection;
import com.app.covacard.util.CustomToast;
import com.app.covacard.util.DATA;
import com.app.covacard.util.Database;
import com.app.covacard.util.GloabalMethods;
import com.app.covacard.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class FirstLogin extends Activity implements ApiCallBack{

	Activity activity;
	ApiCallBack apiCallBack;

	static Button btnSkipp;

	Button btnOpenMedicalHistry,btnFillConcentForm,btnFillPatientConcentForm,btnFillMedPermission,btnAddPrmaryCare,btnSkipWithoutFill;
	OpenActivity openActivity;
	Database db;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	ProgressDialog pd;
	CustomToast customToast;

	RelativeLayout contFillConcentForm,contFillPatientConcentForm,contFillMedPermission,contAddInsurance,contOpenMedicalHistry,contAddPrmaryCare;
	ProgressBar pbFillConcentForm,pbFillPatientConcentForm,pbFillMedPermission,pbAddInsurance,pbOpenMedicalHistry,pbAddPrmaryCare;
	ImageView ivFillConcentFormStatus,ivFillPatientConcentFormStatus,ivFillMedPermissionStatus,ivAddInsuranceStatus,ivOpenMedicalHistryStatus,ivAddPrmaryCareStatus;

	AlertDialog d;
	AlertDialog.Builder builder;


	RelativeLayout contEndUserAgreement,contPrivacyPolicy,contPatientAuthorization;
	ImageView ivEndUserAgreementInfo,ivPrivacyPolicyInfo,ivPatientAuthorizationInfo,
			ivEndUserAgreement,ivPrivacyPolicy,ivPatientAuthorization;
	ProgressBar pbEndUserAgreement,pbPrivacyPolicy,pbPatientAuthorization;


	@Override
	protected void onDestroy() {

		btnSkipp = null;
		System.out.println("-- OnDestroy called on FirstLogin ");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		/*if (prefs.getBoolean("isConcentFilled", false)) {
			
			//btnFillConcentForm.setVisibility(View.GONE);
			//btnFillPatientConcentForm.setVisibility(View.GONE);
			//btnFillMedPermission.setVisibility(View.GONE);
			
			contFillPatientConcentForm.setVisibility(View.GONE);
			contFillConcentForm.setVisibility(View.GONE);
			contFillMedPermission.setVisibility(View.GONE);
		}else {
			//btnFillConcentForm.setVisibility(View.VISIBLE);
			//btnFillPatientConcentForm.setVisibility(View.VISIBLE);
			//btnFillMedPermission.setVisibility(View.VISIBLE);
			
			contFillConcentForm.setVisibility(View.VISIBLE);
			contFillPatientConcentForm.setVisibility(View.VISIBLE);
			contFillMedPermission.setVisibility(View.VISIBLE);
		}*/



		db.insertFirstLoginUser(prefs.getString("id", ""));
		//openActivity.open(MainActivityNew.class, true);
		gotoMain();

		/*if (checkInternetConnection.isConnectedToInternet()) {
			patientFormStatus();
		} else {

			builder = new AlertDialog.Builder(activity).setTitle("No Network").setMessage(getString(R.string.app_name)
					+" requires internet connection to proceed. Please check your internet connection and retry.")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (checkInternetConnection.isConnectedToInternet()) {
						patientFormStatus();
					} else {
						d = builder.create();
						d.setCancelable(false);
						d.show();
					}
				}
			}).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			d = builder.create();
			d.setCancelable(false);
			d.show();
		}*/
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_login);

		activity = FirstLogin.this;
		apiCallBack = this;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);
		db = new Database(activity);		
		db.createDatabase();
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		btnOpenMedicalHistry = (Button) findViewById(R.id.btnOpenMedicalHistry);
		btnSkipp = (Button) findViewById(R.id.btnSkipp);
		btnSkipWithoutFill = (Button) findViewById(R.id.btnSkipWithoutFill);
		btnFillConcentForm = (Button) findViewById(R.id.btnFillConcentForm);
		btnFillPatientConcentForm = (Button) findViewById(R.id.btnFillPatientConcentForm);
		btnFillMedPermission = (Button) findViewById(R.id.btnFillMedPermission);
		
		pbFillConcentForm = (ProgressBar) findViewById(R.id.pbFillConcentForm);
		pbFillConcentForm.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		ivFillConcentFormStatus = (ImageView) findViewById(R.id.ivFillConcentFormStatus);
		
	    contFillConcentForm = (RelativeLayout) findViewById(R.id.contFillConcentForm);
	    contFillPatientConcentForm = (RelativeLayout) findViewById(R.id.contFillPatientConcentForm);
	    contFillMedPermission = (RelativeLayout) findViewById(R.id.contFillMedPermission);
	    contAddInsurance = (RelativeLayout) findViewById(R.id.contAddInsurance);
	    contOpenMedicalHistry = (RelativeLayout) findViewById(R.id.contOpenMedicalHistry);
	    
		pbFillPatientConcentForm = (ProgressBar) findViewById(R.id.pbFillPatientConcentForm);
		pbFillPatientConcentForm.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		pbFillMedPermission = (ProgressBar) findViewById(R.id.pbFillMedPermission);
		pbFillMedPermission.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		pbAddInsurance = (ProgressBar) findViewById(R.id.pbAddInsurance);
		pbAddInsurance.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		pbOpenMedicalHistry = (ProgressBar) findViewById(R.id.pbOpenMedicalHistry);
		pbOpenMedicalHistry.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		
		ivFillPatientConcentFormStatus = (ImageView) findViewById(R.id.ivFillPatientConcentFormStatus);
		ivFillMedPermissionStatus = (ImageView) findViewById(R.id.ivFillMedPermissionStatus);
		ivAddInsuranceStatus = (ImageView) findViewById(R.id.ivAddInsuranceStatus);
		ivOpenMedicalHistryStatus = (ImageView) findViewById(R.id.ivOpenMedicalHistryStatus);
		
		btnAddPrmaryCare = (Button) findViewById(R.id.btnAddPrmaryCare);
		contAddPrmaryCare = (RelativeLayout) findViewById(R.id.contAddPrmaryCare);
		pbAddPrmaryCare = (ProgressBar) findViewById(R.id.pbAddPrmaryCare);
		pbAddPrmaryCare.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		ivAddPrmaryCareStatus = (ImageView) findViewById(R.id.ivAddPrmaryCareStatus);

		
		btnAddPrmaryCare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					//new GloabalMethods(activity).pimaryCareDialog();
					openActivity.open(ActivityPrimaryCareDoctors.class,false);
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
				}
			}
		});
		
		btnOpenMedicalHistry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//DATA.isFromFirstLogin = true;
				DATA.isFromFirstLogin = false;//for emcura app to restrict to go freecare screen from med history first time.
				openActivity.open(MedicalHistory1.class, false);

			}
		});

		btnSkipp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(isFormSaved){
					db.insertFirstLoginUser(prefs.getString("id", ""));
					//openActivity.open(MainActivityNew.class, true);
					gotoMain();
				}else {
					RequestParams params = new RequestParams();
					params.put("patient_id", prefs.getString("id", ""));
					params.put("is_accept_end_user", "1");
					params.put("is_accept_privacy", "1");
					params.put("is_accept_auth", "1");

					ApiManager apiManager = new ApiManager(ApiManager.ACCEPT_FORMS,"post",params,apiCallBack, activity);
					apiManager.loadURL();
				}

				/*if (patient_consent.equalsIgnoreCase("1")) {
					//prefs.edit().putBoolean("isConcentFilled", true).commit();
					db.insertFirstLoginUser(prefs.getString("id", ""));
					openActivity.open(MainActivityNew.class, true);
				}else {
					customToast.showToast("Please fill patient concent form to continue",0,1);
				}*/
				/*if (prefs.getBoolean("isConcentFilled", false)) {
					
				} else {
					Toast.makeText(activity, "Please fill up the Generic Medical Consent before proceed", Toast.LENGTH_LONG).show();
				}*/
			}
		});
		btnSkipWithoutFill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db.insertFirstLoginUser(prefs.getString("id", ""));
				//openActivity.open(MainActivityNew.class, true);

				gotoMain();
			}
		});

		btnFillConcentForm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(ConsentFormActivity.class, false);
				//openActivity.open(PatientConsentActivity.class, false);
			}
		});
		btnFillPatientConcentForm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(PatientConsentActivityNew.class, false);//PatientConsentActivity
			}
		});
		btnFillMedPermission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openActivity.open(MedicalPermissionForm.class, false);
			}
		});
		
		
		Button btnAddInsurance = (Button) findViewById(R.id.btnAddInsurance);
		btnAddInsurance.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//initInsuranceDialog();
				openActivity.open(ActivityInsurance.class, false);
			}
		});
		
		//-------------------------------------------------------------------------

		contEndUserAgreement = (RelativeLayout) findViewById(R.id.contEndUserAgreement);
		contPrivacyPolicy = (RelativeLayout) findViewById(R.id.contPrivacyPolicy);
		contPatientAuthorization = (RelativeLayout) findViewById(R.id.contPatientAuthorization);

		ivEndUserAgreementInfo = (ImageView) findViewById(R.id.ivEndUserAgreementInfo);
		ivPrivacyPolicyInfo = (ImageView) findViewById(R.id.ivPrivacyPolicyInfo);
		ivPatientAuthorizationInfo = (ImageView) findViewById(R.id.ivPatientAuthorizationInfo);
		ivEndUserAgreement = (ImageView) findViewById(R.id.ivEndUserAgreement);
		ivPrivacyPolicy = (ImageView) findViewById(R.id.ivPrivacyPolicy);
		ivPatientAuthorization = (ImageView) findViewById(R.id.ivPatientAuthorization);

		pbEndUserAgreement = (ProgressBar) findViewById(R.id.pbEndUserAgreement);
		pbEndUserAgreement.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		pbPrivacyPolicy = (ProgressBar) findViewById(R.id.pbPrivacyPolicy);
		pbPrivacyPolicy.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);
		pbPatientAuthorization = (ProgressBar) findViewById(R.id.pbPatientAuthorization);
		pbPatientAuthorization.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.theme_red), Mode.MULTIPLY);

		ivPrivacyPolicyInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(activity).showWebviewDialog(DATA.PRIVACY_POLICY_URL,"Privacy Policy");
			}
		});
		ivEndUserAgreementInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(activity).showWebviewDialog(DATA.USER_AGREEMENT_URL,"OnlineCare and its partner’s Virtual Care End User Agreement");
			}
		});

		ivPatientAuthorizationInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl+"/"+ ApiManager.PATIENT_AUTH, "Patient Authorization");
			}
		});

		contPrivacyPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_accept_privacy = "1";
				ivPrivacyPolicy.setImageResource(R.drawable.ic_success_2);
				enableBtn();
			}
		});
		contEndUserAgreement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_accept_end_user = "1";
				ivEndUserAgreement.setImageResource(R.drawable.ic_success_2);
				enableBtn();
			}
		});

		contPatientAuthorization.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_accept_auth = "1";
				ivPatientAuthorization.setImageResource(R.drawable.ic_success_2);
				enableBtn();
			}
		});
	}
	
	Dialog insDialog;
	public void initInsuranceDialog() {
		 insDialog = new Dialog(activity);
		insDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		insDialog.setContentView(R.layout.dialog_insurance);
		
		final EditText etInsurance = (EditText) insDialog.findViewById(R.id.etInsurance);
		final EditText etPolicynumber = (EditText) insDialog.findViewById(R.id.etPolicynumber);
		final EditText etGroup = (EditText) insDialog.findViewById(R.id.etGroup);
		final EditText etCode = (EditText) insDialog.findViewById(R.id.etCode);
		Button btnSubmitForm = (Button) insDialog.findViewById(R.id.btnSubmitForm);
		Button btnCancel = (Button) insDialog.findViewById(R.id.btnCancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				insDialog.dismiss();
			}
		});
		
		btnSubmitForm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String insurance = etInsurance.getText().toString();
				String p_no =  etPolicynumber.getText().toString();
				String group =  etGroup.getText().toString();
				String code =  etCode.getText().toString();
				
				if (insurance.isEmpty() || p_no.isEmpty() || group.isEmpty() || code.isEmpty()) {
					Toast.makeText(activity, "All fields are required", Toast.LENGTH_LONG).show();
				} else {
					
					if (checkInternetConnection.isConnectedToInternet()) {
						saveInsuranceInfo(insurance, p_no, group, code);
					} else {
						Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_LONG).show();
					}
					
				
				}
			}
		});
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(insDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    insDialog.show();
	    insDialog.getWindow().setAttributes(lp);
	}
	
	
	
	public void saveInsuranceInfo(String insurance, String p_no, String group, String code) {
		pd.show();
			  AsyncHttpClient client = new AsyncHttpClient();
			  RequestParams params = new RequestParams();
			  
			  params.put("patient_id", prefs.getString("id", "0"));
			  params.put("insurance", insurance);
			  params.put("policy_number", p_no);
			  params.put("group", group);
			  params.put("code", code);
			  
			  
			  
			  Editor ed = prefs.edit();
			  ed.putString("insurance", insurance);
			  ed.putString("policy_number", p_no);
			  ed.putString("group", group);
			  ed.putString("code", group);
			  ed.commit();
			 
			 System.out.println("--in saveInsuranceInfo params: "+params.toString());

			  client.post(DATA.baseUrl+"/saveInsuranceInfo", params, new AsyncHttpResponseHandler() {
				  @Override
				  public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					  // called when response HTTP status is "200 OK"
					  pd.dismiss();
					  try{
						  String content = new String(response);
						  System.out.println("--reaponce in saveInsuranceInfo "+content);
						  //--reaponce in saveInsuranceInfo {"success":1,"message":"Saved."}

						  try {
							  JSONObject jsonObject = new JSONObject(content);
							  int success = jsonObject.getInt("success");

							  if (success == 1) {
								  insDialog.dismiss();

								  customToast.showToast("Your information saved", 0, 1);
							  } else {
								  customToast.showToast(DATA.CMN_ERR_MSG, 0, 1);
							  }
						  } catch (JSONException e) {
							  // TODO Auto-generated catch block
							  Toast.makeText(activity, "Internal server error. Type JSON exception", Toast.LENGTH_LONG).show();
							  e.printStackTrace();
						  }
					  }catch (Exception e){
						  e.printStackTrace();
						  System.out.println("-- responce onsuccess: saveInsuranceInfo, http status code: "+statusCode+" Byte responce: "+response);
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }

				  @Override
				  public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					  // called when response HTTP status is "4XX" (eg. 401, 403, 404)
					  pd.dismiss();
					  try {
						  String content = new String(errorResponse);
						  System.out.println("--onFailure in saveInsuranceInfo "+content);
						  new GloabalMethods(activity).checkLogin(content);
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					  }catch (Exception e1){
						  e1.printStackTrace();
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }
			  });

			 }//end saveInsuranceInfo


	boolean isFormSaved = false;
	String patient_consent = "0";

	String is_accept_end_user = "0";
	String is_accept_privacy = "0";
	String is_accept_auth = "0";
	public void patientFormStatus() {
		patient_consent = "0";

		is_accept_end_user = "0";
		is_accept_privacy = "0";
		is_accept_auth = "0";

		ApiManager apiManager = new ApiManager(ApiManager.PATIENT_FORM_STATUS+"/"+prefs.getString("id", "0"),"post",null,apiCallBack, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();
	}


	void enableBtn(){
		if(is_accept_end_user.equalsIgnoreCase("1") && is_accept_privacy.equalsIgnoreCase("1") && is_accept_auth.equalsIgnoreCase("1")){
			btnSkipp.setEnabled(true);
		}else {
			btnSkipp.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_first_login, menu);

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_skip) {
			db.insertFirstLoginUser(prefs.getString("id", ""));
			//openActivity.open(MainActivityNew.class, true);

			gotoMain();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.ACCEPT_FORMS)){
			try {
				//{"status":"success"}
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					db.insertFirstLoginUser(prefs.getString("id", ""));
					//openActivity.open(MainActivityNew.class, true);

					gotoMain();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.contains(ApiManager.PATIENT_FORM_STATUS)){

			//DATA.dismissLoaderDefault();
			System.out.println("--reaponce in patientFormStatus "+content);

			try {
				JSONObject jsonObject = new JSONObject(content);

				String generic_consent = jsonObject.getJSONObject("generic_consent").getString("total");

				patient_consent = jsonObject.getJSONObject("patient_consent").getString("total");
				String medical_permission = jsonObject.getJSONObject("medical_permission").getString("total");
				String medical_history = jsonObject.getJSONObject("medical_history").getString("total");
				String primary_care = jsonObject.getJSONObject("primary_care").getString("total");
				String insurance = jsonObject.getJSONObject("insurance").getString("total");

				if (generic_consent.equalsIgnoreCase("1")) {
					pbFillConcentForm.setVisibility(View.INVISIBLE);
					ivFillConcentFormStatus.setVisibility(View.VISIBLE);
					ivFillConcentFormStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbFillConcentForm.setVisibility(View.INVISIBLE);
					ivFillConcentFormStatus.setVisibility(View.VISIBLE);
					ivFillConcentFormStatus.setImageResource(R.drawable.ic_error_2);
				}

				if (patient_consent.equalsIgnoreCase("1")) {
					pbFillPatientConcentForm.setVisibility(View.INVISIBLE);
					ivFillPatientConcentFormStatus.setVisibility(View.VISIBLE);
					ivFillPatientConcentFormStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbFillPatientConcentForm.setVisibility(View.INVISIBLE);
					ivFillPatientConcentFormStatus.setVisibility(View.VISIBLE);
					ivFillPatientConcentFormStatus.setImageResource(R.drawable.ic_error_2);
				}
				if (medical_permission.equalsIgnoreCase("1")) {
					pbFillMedPermission.setVisibility(View.INVISIBLE);
					ivFillMedPermissionStatus.setVisibility(View.VISIBLE);
					ivFillMedPermissionStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbFillMedPermission.setVisibility(View.INVISIBLE);
					ivFillMedPermissionStatus.setVisibility(View.VISIBLE);
					ivFillMedPermissionStatus.setImageResource(R.drawable.ic_error_2);
				}
				if (medical_history.equalsIgnoreCase("1")) {
					pbOpenMedicalHistry.setVisibility(View.INVISIBLE);
					ivOpenMedicalHistryStatus.setVisibility(View.VISIBLE);
					ivOpenMedicalHistryStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbOpenMedicalHistry.setVisibility(View.INVISIBLE);
					ivOpenMedicalHistryStatus.setVisibility(View.VISIBLE);
					ivOpenMedicalHistryStatus.setImageResource(R.drawable.ic_error_2);
				}
				if (insurance.equalsIgnoreCase("1")) {
					pbAddInsurance.setVisibility(View.INVISIBLE);
					ivAddInsuranceStatus.setVisibility(View.VISIBLE);
					ivAddInsuranceStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbAddInsurance.setVisibility(View.INVISIBLE);
					ivAddInsuranceStatus.setVisibility(View.VISIBLE);
					ivAddInsuranceStatus.setImageResource(R.drawable.ic_error_2);
				}

				if (primary_care.equalsIgnoreCase("1")) {
					pbAddPrmaryCare.setVisibility(View.INVISIBLE);
					ivAddPrmaryCareStatus.setVisibility(View.VISIBLE);
					ivAddPrmaryCareStatus.setImageResource(R.drawable.ic_success_2);
				} else {
					pbAddPrmaryCare.setVisibility(View.INVISIBLE);
					ivAddPrmaryCareStatus.setVisibility(View.VISIBLE);
					ivAddPrmaryCareStatus.setImageResource(R.drawable.ic_error_2);
				}


				is_accept_end_user = jsonObject.getJSONObject("is_accept_end_user").getString("total");
				is_accept_privacy = jsonObject.getJSONObject("is_accept_privacy").getString("total");
				is_accept_auth = jsonObject.getJSONObject("is_accept_auth").getString("total");

				enableBtn();

				if(is_accept_end_user.equalsIgnoreCase("1") && is_accept_privacy.equalsIgnoreCase("1") && is_accept_auth.equalsIgnoreCase("1")){
					isFormSaved = true;
				}else {
					isFormSaved = false;
				}

				if(is_accept_end_user.equalsIgnoreCase("1")){
					pbEndUserAgreement.setVisibility(View.INVISIBLE);
					ivEndUserAgreement.setVisibility(View.VISIBLE);
					ivEndUserAgreement.setImageResource(R.drawable.ic_success_2);
				}else{
					pbEndUserAgreement.setVisibility(View.INVISIBLE);
					ivEndUserAgreement.setVisibility(View.VISIBLE);
					ivEndUserAgreement.setImageResource(R.drawable.ic_error_2);
				}
				if(is_accept_privacy.equalsIgnoreCase("1")){
					pbPrivacyPolicy.setVisibility(View.INVISIBLE);
					ivPrivacyPolicy.setVisibility(View.VISIBLE);
					ivPrivacyPolicy.setImageResource(R.drawable.ic_success_2);
				}else{
					pbPrivacyPolicy.setVisibility(View.INVISIBLE);
					ivPrivacyPolicy.setVisibility(View.VISIBLE);
					ivPrivacyPolicy.setImageResource(R.drawable.ic_error_2);
				}
				if(is_accept_auth.equalsIgnoreCase("1")){
					pbPatientAuthorization.setVisibility(View.INVISIBLE);
					ivPatientAuthorization.setVisibility(View.VISIBLE);
					ivPatientAuthorization.setImageResource(R.drawable.ic_success_2);
				}else{
					pbPatientAuthorization.setVisibility(View.INVISIBLE);
					ivPatientAuthorization.setVisibility(View.VISIBLE);
					ivPatientAuthorization.setImageResource(R.drawable.ic_error_2);
				}


				//checkAllAcceptance(medical_history,primary_care,insurance);

			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
				e.printStackTrace();
			}



		}
	}


	void checkAllAcceptance(String medical_history, String primary_care, String insurance){
		if(is_accept_end_user.equalsIgnoreCase("1") && is_accept_privacy.equalsIgnoreCase("1") &&
				is_accept_auth.equalsIgnoreCase("1") && medical_history.equalsIgnoreCase("1") &&
				primary_care.equalsIgnoreCase("1") && insurance.equalsIgnoreCase("1")){
			btnSkipp.performClick();
		}
	}


	void gotoMain(){
		Intent intent = new Intent(getApplicationContext(),MainActivityNew.class);
		intent.putExtra("show_med_history_popup", true);
		startActivity(intent);
		finish();
	}
}
