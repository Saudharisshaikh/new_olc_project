package com.app.priorityone_uc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
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

import com.app.priorityone_uc.api.ApiCallBack;
import com.app.priorityone_uc.api.ApiManager;
import com.app.priorityone_uc.model.HospitalBean;
import com.app.priorityone_uc.model.StateBean;
import com.app.priorityone_uc.util.CheckInternetConnection;
import com.app.priorityone_uc.util.CustomToast;
import com.app.priorityone_uc.util.DATA;
import com.app.priorityone_uc.util.DatePickerFragment;
import com.app.priorityone_uc.util.GloabalMethods;
import com.app.priorityone_uc.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.app.priorityone_uc.api.ApiManager.GET_HOSPITAL;

public class Signup extends AppCompatActivity implements ApiCallBack,CompoundButton.OnCheckedChangeListener{

	Activity activity;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;

	EditText etSignupFname,etSignupLname,etSignupEmail,etSignupPhone,etSignupBirthdate,etSignupUsername,etSignupZipcode,etSignupPass,etSignupConfrmPass,etSignupAddress,
			etSignupCity, etSignupCountry;
	Spinner spSignupState;
	String userState = "MI";
	Button btnSubmitSignup;
	Spinner spSignupHospital,spSignupDoctor;
	RadioGroup rgSignupGender;

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

		etSignupCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).showContrySelectionDialog(etSignupCountry);
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
				String phone = etSignupPhone.getText().toString().trim();
				String birthDate = etSignupBirthdate.getText().toString().trim();
				String address = etSignupAddress.getText().toString().trim();
				String zipcode = etSignupZipcode.getText().toString().trim();

				String city = etSignupCity.getText().toString().trim();
				String country = etSignupCountry.getText().toString();

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
				if(TextUtils.isEmpty(username)){
					etSignupUsername.setError("This field is required");
					customToast.showToast("Please enter username",0,0);
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
				}
				if(!isGenderSelected){
					customToast.showToast("Please select gender",0,0);
					return;
				}


				if(!etSignupPass.getText().toString().equals(etSignupConfrmPass.getText().toString())){
					customToast.showToast("Password don't matched", 0, 0);
				}

				/*else if (!cbPrivacy.isChecked()) {
					customToast.showToast("Please agree to our Privacy Policy", 0, 0);
				} else if (!cbUserAgreement.isChecked()) {
					customToast.showToast("Please agree to the End User Agreement", 0, 0);
				}*/

				else if (!GloabalMethods.isValidEmail(etSignupEmail.getText())) {
					customToast.showToast("The email adderss should look like an email address.", 0, 0);
				} else {

					RequestParams params = new RequestParams();
					params.put("first_name", etSignupFname.getText().toString().trim());
					params.put("last_name", etSignupLname.getText().toString().trim());
					params.put("email", etSignupEmail.getText().toString().trim());
					params.put("gender", selectedGender);
					params.put("phone", etSignupPhone.getText().toString().trim());
					params.put("birthdate", etSignupBirthdate.getText().toString().trim());
					params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);//selectedHospitalId
					params.put("username", etSignupUsername.getText().toString().trim());
					params.put("zipcode", etSignupZipcode.getText().toString().trim());
					params.put("password", etSignupPass.getText().toString().trim());

					params.put("residency",etSignupAddress.getText().toString().trim());

					params.put("city", city);
					params.put("state", userState);//params.put("state", "MI");
					params.put("country", country);

					params.put("current_app", Login.CURRENT_APP);

					signupUsername = etSignupUsername.getText().toString().trim();
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

				if(status.equals("success")) {

					openActivity.open(Login.class, true);
					customToast.showToast(msg, 0, 1);//"Account created successfully.\nYour username and password are sent to your email address."

					is_signup_successfull = true;

				} else {

					customToast.showToast(msg, 0, 1);
				}


			} catch (JSONException e) {
				e.printStackTrace();
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
		}else if(apiName.equalsIgnoreCase(ApiManager.CHECK_USERNAME_AVAILABLITY)){
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
