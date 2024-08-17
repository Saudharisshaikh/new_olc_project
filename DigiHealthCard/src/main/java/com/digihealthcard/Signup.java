package com.digihealthcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.digihealthcard.model.HospitalBean;
import com.digihealthcard.model.StateBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.RequestParams;
import com.digihealthcard.api.ApiCallBack;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.adapter.LinkAdapter;
import com.digihealthcard.model.LinkBean;
import com.digihealthcard.R;

import com.digihealthcard.util.CheckInternetConnection;
import com.digihealthcard.util.CustomToast;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.DatePickerFragment;
import com.digihealthcard.util.ExpandableHeightListView;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.OpenActivity;
import com.digihealthcard.util.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.digihealthcard.api.ApiManager.GET_HOSPITAL;

public class Signup extends AppCompatActivity implements ApiCallBack,CompoundButton.OnCheckedChangeListener{

	Activity activity;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	SharedPrefsHelper sharedPrefsHelper;

	EditText etSignupFname,etSignupLname,etSignupEmail,etSignupPhone,etSignupBirthdate,etSignupUsername,etSignupZipcode,etSignupPass,etSignupConfrmPass,etSignupAddress,
			etSignupCity, etSignupCountry,etSignupState;
	CountryCodePicker ccp_CountryCode;
	Spinner spSignupState;
	String userState = "MI";
	Button btnSubmitSignup;
	Spinner spSignupHospital,spSignupDoctor;
	RadioGroup rgSignupGender,rgSignupCovidVV;

	ExpandableHeightListView lvPrivacy;

	public static int covidOrVV = 0;//1=new covid, 2 = new virtual Visit

	//private final String gendrArr[] = {"Select Gender", "Male", "Female"};
	private  boolean isGenderSelected = false;
	private String selectedGender = "";
	String selectedHospitalId = "";

	public CheckBox cbPrivacy,cbUserAgreement;


	boolean isTyping = false;
	ProgressBar pbAutoComplete;
	ImageView ivClear;

	public static String signupUsername = "", signupPassword = "";
	public static boolean is_signup_successfull = false;

	String phone , phoneInput;
	public static String fromSignUp = "0";//0=From social , 1= From normal signup, 2 = from home
	public static String fNameSignup , lNameSignup , emailSignup , phoneNumSignup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		activity = Signup.this;
		checkInternet = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		apiCallBack = this;
		sharedPrefsHelper = SharedPrefsHelper.getInstance();

		hideSoftKeyboard();

		etSignupFname = (EditText) findViewById(R.id.etSignupFname);
		etSignupLname = (EditText) findViewById(R.id.etSignupLname);
		etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
		spSignupHospital = (Spinner) findViewById(R.id.spSignupHospital);
		spSignupDoctor = (Spinner) findViewById(R.id.spSignupDoctor);
		rgSignupGender = (RadioGroup) findViewById(R.id.rgSignupGender);
		etSignupPhone = (EditText) findViewById(R.id.etSignupPhone);
		etSignupBirthdate = (EditText) findViewById(R.id.etSignupBirthdate);
		etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
		etSignupZipcode = (EditText) findViewById(R.id.etSignupZipcode);
		etSignupPass = (EditText) findViewById(R.id.etSignupPass);
		etSignupConfrmPass = (EditText) findViewById(R.id.etSignupConfrmPass);
		etSignupAddress = (EditText) findViewById(R.id.etSignupAddress);
		btnSubmitSignup = (Button) findViewById(R.id.btnSubmitSignup);
		cbPrivacy = (CheckBox) findViewById(R.id.cbPrivacy);
		cbUserAgreement = (CheckBox) findViewById(R.id.cbUserAgreement);
		etSignupCity =  findViewById(R.id.etSignupCity);
		etSignupCountry =  findViewById(R.id.etSignupCountry);
		spSignupState =  findViewById(R.id.spSignupState);
		rgSignupCovidVV = findViewById(R.id.rgSignupCovidVV);
		ccp_CountryCode = findViewById(R.id.ccp_CountryCode);
		lvPrivacy = findViewById(R.id.lvPrivacy);
		etSignupState = findViewById(R.id.etSignupState);


		ccp_CountryCode.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
			@Override
			public void onCountrySelected() {
				System.out.println("-- onCountrySelected deligate");
				etSignupCountry.setText(ccp_CountryCode.getSelectedCountryName());
			}
		});


		cbPrivacy.setOnCheckedChangeListener(this);
		cbUserAgreement.setOnCheckedChangeListener(this);

		//===========State Spinner
		final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
		ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(activity, R.layout.spinner_item_lay, stateBeans);
		spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSignupState.setAdapter(spStateAdapter);

		//for (int i = 0; i < stateBeans.size(); i++) { if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) { spSignupState.setSelection(i); } }

		spSignupState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				userState = stateBeans.get(pos).getAbbreviation();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//========================State Spinner=======================================

		//etSignupCountry.setText("United States");

		etSignupCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).showContrySelectionDialog(etSignupCountry);
			}
		});

		etSignupCountry.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().contains("United States") || s.toString().contains("USA") || s.toString().contains("US")){
					spSignupState.setVisibility(View.VISIBLE);
					etSignupState.setVisibility(View.GONE);
				}else {
					spSignupState.setVisibility(View.GONE);
					etSignupState.setVisibility(View.VISIBLE);
				}
			}
		});

		btnSubmitSignup.bringToFront();

		spSignupDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedHospitalId = hospitalBeens.get(position).id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				selectedHospitalId = "";
			}
		});

		spSignupHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position != 0){
					if(etSignupZipcode.getText().toString().isEmpty()){
						etSignupZipcode.setError("Please enter your zipcode");
						return;
					}
					RequestParams params = new RequestParams();
					params.put("category_id",(position-1)+"");
					params.put("zipcode", etSignupZipcode.getText().toString());
					ApiManager apiManager = new ApiManager(GET_HOSPITAL,"post",params,apiCallBack, activity);
					apiManager.loadURL();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//selectedHospitalId = "";
			}
		});
		String doctor_category[] = {"Select Hospital", "My Primary Care", "My Specialist", "OnlineCare Doc"};
		ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, doctor_category);
		spSignupHospital.setAdapter(hospitalAdapter);

		rgSignupGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.radioMale:

						selectedGender = "1";
						isGenderSelected = true;

						break;
					case R.id.radioFemale:

						selectedGender = "0";
						isGenderSelected = true;

						break;
					case R.id.radioOther:

						selectedGender = "2";
						isGenderSelected = true;

						break;

					default:
						break;
				}
			}
		});

		etSignupBirthdate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus) {
					//DATA.isDatePickerCallFromSignup = true;
					DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				}
			}
		});
		etSignupBirthdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});


		btnSubmitSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String fname = etSignupFname.getText().toString().trim();
				String lname = etSignupLname.getText().toString().trim();
				String username = etSignupUsername.getText().toString().trim();
				String email = etSignupEmail.getText().toString().trim();
				String password = etSignupPass.getText().toString().trim();
				String confrimPass = etSignupConfrmPass.getText().toString().trim();
				//String phone = etSignupPhone.getText().toString().trim();
				String birthDate = etSignupBirthdate.getText().toString().trim();
				String address = etSignupAddress.getText().toString().trim();
				String zipcode = etSignupZipcode.getText().toString().trim();

				String countryCode = ccp_CountryCode.getSelectedCountryCodeWithPlus();
				phone = etSignupPhone.getText().toString();
				phoneInput = countryCode+"-"+phone;

				String city = etSignupCity.getText().toString().trim();
				String country = etSignupCountry.getText().toString();

				if(spSignupState.getVisibility() == View.VISIBLE){
					userState = stateBeans.get(spSignupState.getSelectedItemPosition()).getAbbreviation();
				}else {
					userState = etSignupState.getText().toString().trim();
				}

				if(rgSignupCovidVV.getCheckedRadioButtonId() == R.id.rbNewCovid){ covidOrVV = 1;}
				else if(rgSignupCovidVV.getCheckedRadioButtonId() == R.id.rbVirtualVisit){ covidOrVV = 2;}
				else{ covidOrVV = 0;}

				if(TextUtils.isEmpty(fname)){
					etSignupFname.setError("This field is required");
					customToast.showToast("Please enter first name",0,0);
					return;
				}
				if(TextUtils.isEmpty(lname)){
					etSignupLname.setError("This field is required");
					customToast.showToast("Please enter last name",0,0);
					return;
				}
				if(TextUtils.isEmpty(email)){
					etSignupEmail.setError("This field is required");
					customToast.showToast("Please enter email",0,0);
					return;
				}
				if(TextUtils.isEmpty(password)){
					etSignupPass.setError("This field is required");
					customToast.showToast("Please enter password",0,0);
					return;
				}
				if(TextUtils.isEmpty(confrimPass)){
					etSignupConfrmPass.setError("This field is required");
					customToast.showToast("Please retype password",0,0);
					return;
				}
				if(TextUtils.isEmpty(phone)){
					etSignupPhone.setError("This field is required");
					customToast.showToast("Please enter phone",0,0);
					return;
				}
				/*
				if(TextUtils.isEmpty(username)){
					etSignupUsername.setError("This field is required");
					customToast.showToast("Please enter username",0,0);
					return;
				}
				if(TextUtils.isEmpty(phone)){
					etSignupPhone.setError("This field is required");
					customToast.showToast("Please enter phone",0,0);
					return;
				}
				if(TextUtils.isEmpty(birthDate)){
					etSignupBirthdate.setError("This field is required");
					customToast.showToast("Please enter birthdate",0,0);
					return;
				}
				if(TextUtils.isEmpty(address)){
					etSignupAddress.setError("This field is required");
					customToast.showToast("Please enter address",0,0);
					return;
				}


				if(TextUtils.isEmpty(city)){
					etSignupCity.setError("This field is required");
					customToast.showToast("Please enter city",0,0);
					return;
				}
				if(TextUtils.isEmpty(userState)){
					//spSignupState.setError("This field is required");
					customToast.showToast("Please select state",0,0);
					return;
				}
				if(TextUtils.isEmpty(country)){
					etSignupCountry.setError("This field is required");
					customToast.showToast("Please enter country",0,0);
					return;
				}


				if(TextUtils.isEmpty(zipcode)){
					etSignupZipcode.setError("This field is required");
					customToast.showToast("Please enter zipcode",0,0);
					return;
				}*/
				/*if(!isGenderSelected){
					customToast.showToast("Please select gender",0,0);
					return;
				}*/
				if(!etSignupPass.getText().toString().equals(etSignupConfrmPass.getText().toString())){
					customToast.showToast("Password don't matched", 0, 0);
				}

				/*else if (!cbPrivacy.isChecked()) {
					customToast.showToast("Please agree to our Privacy Policy", 0, 0);
				} else if (!cbUserAgreement.isChecked()) {
					customToast.showToast("Please agree to the End User Agreement", 0, 0);
				}*/

				else if (!GloabalMethods.isValidEmail(etSignupEmail.getText())) {
					customToast.showToast("Please enter a valid email address", 0, 0);
				} else {

					boolean policyCheck = true;
					if(linkBeans != null){
						for (int i = 0; i < linkBeans.size(); i++) {
							if(!linkBeans.get(i).isChecked){
								policyCheck = false;
							}
						}
					}
					if(!policyCheck){
						customToast.showToast("Please agree with the necessary acceptance", 0,0);
						return;
					}

					phone = ccp_CountryCode.getSelectedCountryCodeWithPlus() + "-" + phone;

					RequestParams params = new RequestParams();
					params.put("first_name", etSignupFname.getText().toString().trim());
					params.put("last_name", etSignupLname.getText().toString().trim());
					params.put("email", etSignupEmail.getText().toString().trim());
					params.put("password", etSignupPass.getText().toString().trim());
					params.put("phone", phoneInput);

					/*params.put("gender", selectedGender);
					params.put("phone", phone);
					params.put("birthdate", etSignupBirthdate.getText().toString().trim());
					params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);//selectedHospitalId
					params.put("username", etSignupUsername.getText().toString().trim());
					params.put("zipcode", etSignupZipcode.getText().toString().trim());
					params.put("residency",etSignupAddress.getText().toString().trim());
					params.put("city", city);
					params.put("state", userState);//params.put("state", "MI");
					params.put("country", country);
					params.put("current_app", Login.CURRENT_APP);*/

					signupUsername = etSignupEmail.getText().toString();//etSignupUsername.getText().toString().trim();
					signupPassword = etSignupPass.getText().toString().trim();

					ApiManager apiManager = new ApiManager(ApiManager.PATIENT_SIGNUP, "post", params, apiCallBack, activity);
					apiManager.loadURL();
				}
			}
		});


		/*ApiManager apiManager = new ApiManager(HOSPITALS,"post",null,apiCallBack, activity);
		apiManager.loadURL();*/


		//=====================Check Availablity=============================================

		pbAutoComplete = (ProgressBar) findViewById(R.id.pbAutoComplete);
		ivClear = (ImageView) findViewById(R.id.ivClear);
		//svMain = (ScrollView) findViewById(R.id.svMain);
		pbAutoComplete.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.theme_red), android.graphics.PorterDuff.Mode.MULTIPLY);


		etSignupUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//boolean isType = false;
                    /*if(etSignupUsername.getText().toString().isEmpty()){
                        isType = false;
                    }else{
                        isType = true;
                    }*/
					//GPSTracker gpsTracker = new GPSTracker(activity);
					//LatLng latLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
					checkUsername(etSignupUsername.getText().toString());
					//gpsTracker.stopUsingGPS();
					//return true; return true not closes keyboard
					return false;
				}
				return false;
			}
		});

		final int TYPING_TIMEOUT = 1000; // 5 seconds timeout = 5000, 500=half second
		final Handler timeoutHandler = new Handler();
		final Runnable typingTimeout = new Runnable() {
			public void run() {
				isTyping = false;
				//serviceCall();
				System.out.println("-- srevice call here 1");
				pbAutoComplete.setVisibility(View.VISIBLE);

				checkUsername(etSignupUsername.getText().toString());
			}
		};

		etSignupUsername.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// reset the timeout
				timeoutHandler.removeCallbacks(typingTimeout);

				if (etSignupUsername.getText().toString().trim().length() > 2) {
					// schedule the timeout
					timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);

					if (!isTyping) {
						isTyping = true;
						//serviceCall();
						System.out.println("-- srevice call here 2");
					}
				} else {
					isTyping = false;
					//serviceCall();
					System.out.println("-- srevice call here 3");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		//=========password validation:
		etSignupConfrmPass.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

			@Override
			public void afterTextChanged(Editable editable) {
				if(! editable.toString().equals(etSignupPass.getText().toString())){
					etSignupConfrmPass.setError("Password does not match");
				}else {
					etSignupConfrmPass.setError(null);
				}
			}
		});

		cbPrivacy.setVisibility(View.GONE);
		cbUserAgreement.setVisibility(View.GONE);

		getLabels();
	}


	public void getLabels(){
		String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
		if(!TextUtils.isEmpty(app_general_labels)){
			parseLabelsData(app_general_labels);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.LABELS,"post",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	public void checkUsername(String username){
		RequestParams params = new RequestParams();
		params.put("username",username);

		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_USERNAME_AVAILABLITY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void hideSoftKeyboard() {
		if(getCurrentFocus()!=null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	ArrayList<HospitalBean> hospitalBeens;
	@Override
	public void fetchDataCallback(String htpStatus, String apiName, String content) {
		if(apiName.equals(ApiManager.PATIENT_SIGNUP)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");
				String msg = jsonObject.getString("msg");

				String userStr = jsonObject.getString("user");
				JSONObject user_info = new JSONObject(userStr);
				//Check user Verification status here
				String userVerifyStatus = user_info.getString("is_approved");

				if(status.equalsIgnoreCase("success") ) {
					/*openActivity.open(Login.class, true);
					customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."
					is_signup_successfull = true;*/
					if (userVerifyStatus.equals("0")){

						fromSignUp = "1";
						fNameSignup = etSignupFname.getText().toString().trim();
						lNameSignup = etSignupLname.getText().toString().trim();
						emailSignup = etSignupEmail.getText().toString().trim();
						phoneNumSignup = phoneInput;

						openActivity.open(ActivityVerification.class ,false);
						customToast.showToast(msg, 0, 1);
					}
					else if (userVerifyStatus.equals("1")) {
						AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle(getResources().getString(R.string.app_name))
								.setMessage("Please check your email to validate your CovaCard account. Post validation, sign back in to your 7 days free trail account.")
								.setNegativeButton("Done", null)
								.create();
						alertDialog.setOnDismissListener(dialog -> {
							finish();
						});
						alertDialog.show();
					}
				}
				else if (status.equalsIgnoreCase("error"))
				{
					customToast.showToast(msg , 0 , 1);
				}


			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equals(GET_HOSPITAL)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				hospitalBeens = new ArrayList<HospitalBean>();
				HospitalBean bean;
				if(data.length() == 0){
					customToast.showToast("No doctors found under the zipcode OR hospital",0,0);
				}
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String hospital_name = data.getJSONObject(i).getString("hospital_name");
					String folder_name = data.getJSONObject(i).getString("folder_name");
					String category_id = data.getJSONObject(i).getString("folder_name");
					String hospital_zipcode = data.getJSONObject(i).getString("folder_name");

					bean = new HospitalBean(id,hospital_name,folder_name,category_id,hospital_zipcode);
					hospitalBeens.add(bean);
					bean = null;
				}

				ArrayAdapter<HospitalBean> hospitalAdapter = new ArrayAdapter<HospitalBean>(activity, android.R.layout.simple_dropdown_item_1line, hospitalBeens);
				spSignupDoctor.setAdapter(hospitalAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
		else if(apiName.equalsIgnoreCase(ApiManager.CHECK_USERNAME_AVAILABLITY)){
			pbAutoComplete.setVisibility(View.GONE);
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					etSignupUsername.setError(null);
					btnSubmitSignup.setEnabled(true);
				}else {
					etSignupUsername.setError("Username not available");
					btnSubmitSignup.setEnabled(false);
					customToast.showToast("This username is not available. Please try a diffrent one",0,0);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if (apiName.equalsIgnoreCase(ApiManager.LABELS)){
			parseLabelsData(content);
		}
		/*else if(apiName.equals(HOSPITALS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				hospitalBeens = new ArrayList<HospitalBean>();
				HospitalBean bean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String hospital_name = data.getJSONObject(i).getString("hospital_name");
					String folder_name = data.getJSONObject(i).getString("folder_name");

					bean = new HospitalBean(id,hospital_name,folder_name);
					hospitalBeens.add(bean);
					bean = null;
				}

				ArrayAdapter<HospitalBean> hospitalAdapter = new ArrayAdapter<HospitalBean>(activity, android.R.layout.simple_dropdown_item_1line, hospitalBeens);
				spSignupHospital.setAdapter(hospitalAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}*/
	}

	ArrayList<LinkBean> linkBeans;
	LinkAdapter linkAdapter;
	private void parseLabelsData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray patient_registeration = jsonObject.getJSONObject("data").getJSONArray("patient_registeration");

			Type listType = new TypeToken<ArrayList<LinkBean>>() {}.getType();
			linkBeans = new Gson().fromJson(patient_registeration.toString(), listType);

			linkAdapter = new LinkAdapter(activity, linkBeans);
			lvPrivacy.setAdapter(linkAdapter);
			lvPrivacy.setExpanded(true);

			sharedPrefsHelper.save("app_general_labels", content);

			String testLocSearchURL = jsonObject.getJSONObject("data").getJSONObject("test_locations").getString("url");
			sharedPrefsHelper.save("testLocSearchURL", testLocSearchURL);

			String city_search =  jsonObject.getJSONObject("data").getJSONObject("test_locations").getString("city_search");
			sharedPrefsHelper.save("city_search", city_search);

			String test_results_email = jsonObject.getJSONObject("data").getJSONObject("testresults").getString("email");
			sharedPrefsHelper.save("test_results_email", test_results_email);

			if(sharedPrefsHelper.get("debug_logs", false)){
				System.out.println("-- Test Loc Search URL : "+testLocSearchURL);
				System.out.println("-- city search URL : "+city_search);
				System.out.println("-- test results email : "+test_results_email);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			if(buttonView.getId() == R.id.cbPrivacy){
				new GloabalMethods(activity).showWebviewDialog(DATA.PRIVACY_POLICY_URL,"Privacy Policy");
			}else if(buttonView.getId() == R.id.cbUserAgreement){
				new GloabalMethods(activity).showWebviewDialog(DATA.USER_AGREEMENT_URL,"OnlineCare and its partner’s Virtual Care End User Agreement");
			}
		}
	}
}
