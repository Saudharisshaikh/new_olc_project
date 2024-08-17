package com.app.Olc_MentalHealth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.model.MyInsuranceBean;
import com.app.Olc_MentalHealth.model.StateBean;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.DatePickerFragment;
import com.app.Olc_MentalHealth.util.GloabalMethods;
import com.app.Olc_MentalHealth.util.LiveCareInsurance;
import com.app.Olc_MentalHealth.util.LiveCareInsuranceCardhelper;
import com.app.Olc_MentalHealth.util.LiveCareInsuranceInterface;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AddFamilyMember extends BaseActivity {

	ImageView imgAdAdmembr;
	EditText etAdmemberFname,etAdmemberLname,etAdmemberOccupation,etAdmemberDob,etAdmemberRelationship,etAdmemberPass,etAdmemberConfirmPass,etAdmemberEmail,etAdmemberUsername;
	EditText etSignupAddress, etSignupCity, etSignupCountry,etSignupZipcode,etSignupPhone;
	Spinner spSignupState;
	String userState = "MI";

	Button btnAdmemberCreateUser;

	Spinner spAdmemberGender,spAdmemberMarital;

	private final String gendrArr[] = {"Select Gender", "Female",  "Male", "Other"};
	private final String maritalArr[] = {"Marital Status", "Single", "Married", "Divorced", "Widowed"};
	private  boolean isGenderSelected = false;
	private  boolean isMaritalSelected = false;
	private String selectedGender = "";
	private String selectedMarital = "";

	boolean isTyping = false;
	ProgressBar pbAutoComplete;
	ImageView ivClear;

	ArrayList<MyInsuranceBean> myInsuranceBeen;
	ArrayAdapter<MyInsuranceBean> spInsuranceAdapter;
	String insurance_id = "";
	public static MyInsuranceBean myInsuranceBeanNewIns;

	Spinner spSelectInsurance;
	Button btnAddInsurance;
	LinearLayout layNewInsurance;
	TextView tvPatientInsurance;
	CheckBox cbNewInsurance;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_famly_member);

		etAdmemberFname = (EditText) findViewById(R.id.etAdmemberFname);
		etAdmemberLname = (EditText) findViewById(R.id.etAdmemberLname);
		etAdmemberOccupation = (EditText) findViewById(R.id.etAdmemberOccupation);
		etAdmemberDob = (EditText) findViewById(R.id.etAdmemberDob);
		etAdmemberRelationship = (EditText) findViewById(R.id.etAdmemberRelationship);
		etAdmemberPass = (EditText) findViewById(R.id.etAdmemberPass);
		etAdmemberConfirmPass = (EditText) findViewById(R.id.etAdmemberConfirmPass);
		etAdmemberEmail = (EditText) findViewById(R.id.etAdmemberEmail);
		etAdmemberUsername = (EditText) findViewById(R.id.etAdmemberUsername);

		spAdmemberGender = (Spinner) findViewById(R.id.spAdmemberGender);
		spAdmemberMarital = (Spinner) findViewById(R.id.spAdmemberMarital);

		etSignupAddress = findViewById(R.id.etSignupAddress);
		etSignupCity = findViewById(R.id.etSignupCity);
		etSignupCountry = findViewById(R.id.etSignupCountry);
		etSignupZipcode = findViewById(R.id.etSignupZipcode);
		etSignupPhone = findViewById(R.id.etSignupPhone);
		spSignupState = findViewById(R.id.spSignupState);
		spSelectInsurance = findViewById(R.id.spSelectInsurance);
		btnAddInsurance = findViewById(R.id.btnAddInsurance);
		layNewInsurance = findViewById(R.id.layNewInsurance);
		tvPatientInsurance = findViewById(R.id.tvPatientInsurance);
		cbNewInsurance = findViewById(R.id.cbNewInsurance);

		btnAdmemberCreateUser = (Button) findViewById(R.id.btnAdmemberCreateUser);


		myInsuranceBeanNewIns = null;

		cbNewInsurance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					layNewInsurance.setVisibility(View.VISIBLE);
					spSelectInsurance.setVisibility(View.GONE);
				}else {
					layNewInsurance.setVisibility(View.GONE);
					spSelectInsurance.setVisibility(View.VISIBLE);
				}
			}
		});

		btnAddInsurance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LiveCareInsurance.familyFistName = etAdmemberFname.getText().toString().trim();
				LiveCareInsurance.familyLastName = etAdmemberLname.getText().toString().trim();
				LiveCareInsurance.familyDob = etAdmemberDob.getText().toString().trim();
				if(TextUtils.isEmpty(LiveCareInsurance.familyFistName)){
					customToast.showToast("Please enter first name", 0, 0);
				}else if(TextUtils.isEmpty(LiveCareInsurance.familyLastName)){
					customToast.showToast("Please enter last name", 0, 0);
				}else if(TextUtils.isEmpty(LiveCareInsurance.familyDob)){
					customToast.showToast("Please enter DOB", 0, 0);
				}else {
					new LiveCareInsurance(activity).getSimpleState(true);
				}
			}
		});


		etSignupAddress.setText(prefs.getString("address", ""));
		etSignupCity.setText(prefs.getString("city", ""));
		etSignupCountry.setText(prefs.getString("country", ""));
		etSignupZipcode.setText(prefs.getString("zipcode", ""));

		//===========State Spinner
		final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
		ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(activity, R.layout.spinner_item_lay, stateBeans);
		spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSignupState.setAdapter(spStateAdapter);

		for (int i = 0; i < stateBeans.size(); i++) { if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) { spSignupState.setSelection(i); } }

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


		myInsuranceBeen = sharedPrefsHelper.getPatientInrances();
		spSelectInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				insurance_id = myInsuranceBeen.get(position).id;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		if(myInsuranceBeen.isEmpty()){
			getMyInsurance();
		}else {
			//showInsuranceListDialog();
			spInsuranceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, myInsuranceBeen);
			spSelectInsurance.setAdapter(spInsuranceAdapter);
		}





		ArrayAdapter<String> gendrAdptr = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, gendrArr);
		spAdmemberGender.setAdapter(gendrAdptr);

		spAdmemberGender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if(position == 0) {
					selectedGender = "";
					isGenderSelected = false;
				} else {
					selectedGender = String.valueOf(position-1);
					isGenderSelected = true;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				isGenderSelected = false;
			}
		});

		ArrayAdapter<String> maritalAdptr = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, maritalArr);
		spAdmemberMarital.setAdapter(maritalAdptr);

		spAdmemberMarital.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position == 0) {
					selectedMarital = "";
					isMaritalSelected = false;
				}else {
					selectedMarital = String.valueOf(position-1);
					isMaritalSelected = true;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				isMaritalSelected = false;
			}
		});


		etAdmemberDob.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus) {
					//DATA.isDatPckrCallFromAddMember = true;
					DialogFragment newFragment = new DatePickerFragment(etAdmemberDob);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				}


			}
		});

		etAdmemberDob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogFragment newFragment = new DatePickerFragment(etAdmemberDob);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});


		btnAdmemberCreateUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String fname = etAdmemberFname.getText().toString().trim();
				String lname = etAdmemberLname.getText().toString().trim();
				String username = etAdmemberUsername.getText().toString().trim();
				String email = etAdmemberEmail.getText().toString().trim();
				String occupation = etAdmemberOccupation.getText().toString().trim();
				String dob = etAdmemberDob.getText().toString().trim();
				String relationShip = etAdmemberRelationship.getText().toString().trim();
				String password = etAdmemberPass.getText().toString().trim();
				String confirmPass = etAdmemberConfirmPass.getText().toString().trim();

				String residency = etSignupAddress.getText().toString().trim();
				String city = etSignupCity.getText().toString().trim();
				String country = etSignupCountry.getText().toString().trim();
				String zipcode = etSignupZipcode.getText().toString().trim();
				String phone = etSignupPhone.getText().toString().trim();


				/*if(TextUtils.isEmpty(fname)){ etAdmemberFname.setError("This field is required");customToast.showToast("Please enter first name",0,0);return;}
				if(TextUtils.isEmpty(lname)){etAdmemberLname.setError("This field is required");customToast.showToast("Please enter last name",0,0);return;}
				if(TextUtils.isEmpty(username)){etAdmemberUsername.setError("This field is required");customToast.showToast("Please enter username",0,0);return;}
				if(TextUtils.isEmpty(email)){etAdmemberEmail.setError("This field is required");customToast.showToast("Please enter email",0,0);return;}
				if (!GloabalMethods.isValidEmail(etAdmemberEmail.getText())) {customToast.showToast("The email adderss should look like an email address.", 0, 0);etAdmemberEmail.setError("Invalid email address");return;}
				if(TextUtils.isEmpty(occupation)){etAdmemberOccupation.setError("This field is required");customToast.showToast("Please enter occupation",0,0);return; }
				if(TextUtils.isEmpty(dob)){etAdmemberDob.setError("This field is required");customToast.showToast("Please enter birthdate",0,0);return;}
				if(TextUtils.isEmpty(relationShip)){etAdmemberRelationship.setError("This field is required");customToast.showToast("Please enter relationship",0,0);return;}
				if(TextUtils.isEmpty(password)){etAdmemberPass.setError("This field is required");customToast.showToast("Please enter password",0,0);return;}
				if(TextUtils.isEmpty(confirmPass)){ etAdmemberConfirmPass.setError("This field is required");customToast.showToast("Please retype password",0,0);return;}*/


				if(!emptyValidate()){
					customToast.showToast("Please enter the required information",0,0);
					return;
				}
				if(!GloabalMethods.isValidEmail(email)){
					customToast.showToast("Please enter a valid email address",0,0);
					return;
				}
				if(!password.equals(confirmPass)){
					customToast.showToast("Password not matched",0,0);
					return;
				}
				if(!isGenderSelected){
					customToast.showToast("Please select gender",0,0);
					return;
				}
				if(!isMaritalSelected){
					customToast.showToast("Please select marital status",0,0);
					return;
				}



				RequestParams params = new RequestParams();
				params.put("patient_id",prefs.getString("id", ""));
				params.put("first_name", fname);
				params.put("last_name", lname);
				params.put("username", username);
				params.put("email", email);
				params.put("occupation", occupation);
				params.put("dob", dob);
				params.put("relationship", relationShip);
				params.put("password", password);
				params.put("confirm_password", confirmPass);
				params.put("marital_status", selectedMarital);
				params.put("gender", selectedGender);
				params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);//selectedHospitalId
				params.put("current_app", Login.CURRENT_APP);

				params.put("residency", residency);
				params.put("city", city);
				params.put("state", userState);
				params.put("zipcode", zipcode);
				params.put("country", country);
				params.put("phone", phone);


				if(cbNewInsurance.isChecked()){
					if(myInsuranceBeanNewIns != null){
						//params.put("patient_id", prefs.getString("id", "0"));
						params.put("insurance", myInsuranceBeanNewIns.insurance);
						params.put("policy_number", myInsuranceBeanNewIns.policy_number);
						params.put("group", myInsuranceBeanNewIns.insurance_group);
						params.put("code", myInsuranceBeanNewIns.insurance_code);

						if(! TextUtils.isEmpty(myInsuranceBeanNewIns.inc_front)){
							try {params.put("inc_front", new File(myInsuranceBeanNewIns.inc_front));} catch (FileNotFoundException e) {e.printStackTrace();}
						}
						if(! TextUtils.isEmpty(myInsuranceBeanNewIns.inc_back)){
							try {params.put("inc_back", new File(myInsuranceBeanNewIns.inc_back));} catch (FileNotFoundException e) {e.printStackTrace();}
						}

						if(! TextUtils.isEmpty(myInsuranceBeanNewIns.id_front)){
							try {params.put("id_front", new File(myInsuranceBeanNewIns.id_front));} catch (FileNotFoundException e) {e.printStackTrace();}
						}
						if(! TextUtils.isEmpty(myInsuranceBeanNewIns.id_back)){
							try {params.put("id_back", new File(myInsuranceBeanNewIns.id_back));} catch (FileNotFoundException e) {e.printStackTrace();}
						}
					}
				}else {
					params.put("insurance_id", insurance_id);
				}



				ApiManager apiManager = new ApiManager(ApiManager.ADD_SUB_PATIENTS,"post",params,apiCallBack, activity);
				apiManager.loadURL();

			}
		});

		imgAdAdmembr = (ImageView) findViewById(R.id.imgAdAdmembr);
		imgAdAdmembr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DATA.addIntent(activity);
			}
		});


		//=====================Check Availablity=============================================

		pbAutoComplete = (ProgressBar) findViewById(R.id.pbAutoComplete);
		ivClear = (ImageView) findViewById(R.id.ivClear);
		//svMain = (ScrollView) findViewById(R.id.svMain);
		pbAutoComplete.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.theme_red), android.graphics.PorterDuff.Mode.MULTIPLY);


		etAdmemberUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//boolean isType = false;
                    /*if(etAdmemberUsername.getText().toString().isEmpty()){
                        isType = false;
                    }else{
                        isType = true;
                    }*/
					//GPSTracker gpsTracker = new GPSTracker(activity);
					//LatLng latLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
					checkUsername(etAdmemberUsername.getText().toString());
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
				DATA.print("-- srevice call here 1");
				pbAutoComplete.setVisibility(View.VISIBLE);

				checkUsername(etAdmemberUsername.getText().toString());
			}
		};

		etAdmemberUsername.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// reset the timeout
				timeoutHandler.removeCallbacks(typingTimeout);

				if (etAdmemberUsername.getText().toString().trim().length() > 2) {
					// schedule the timeout
					timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);

					if (!isTyping) {
						isTyping = true;
						//serviceCall();
						DATA.print("-- srevice call here 2");
					}
				} else {
					isTyping = false;
					//serviceCall();
					DATA.print("-- srevice call here 3");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}


	public void checkUsername(String username){
		RequestParams params = new RequestParams();
		params.put("username",username);

		ApiManager.shouldShowLoader = false;
		ApiManager apiManager = new ApiManager(ApiManager.CHECK_USERNAME_AVAILABLITY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.ADD_SUB_PATIENTS)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");

				if (jsonObject.has("msg")) {
					customToast.showToast(jsonObject.getString("msg"),0,1);
				}

				if(status.equals("success")) {
					SubUsersList.shouldLoadData = true;
					finish();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.CHECK_USERNAME_AVAILABLITY)){
			pbAutoComplete.setVisibility(View.GONE);
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					etAdmemberUsername.setError(null);
					btnAdmemberCreateUser.setEnabled(true);
				}else {
					etAdmemberUsername.setError("Username not available");
					btnAdmemberCreateUser.setEnabled(false);
					customToast.showToast("This username is not available. Please try a diffrent one",0,0);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(apiName.equals(ApiManager.GET_MY_INSURANCE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");


                /*if(data.length() == 0){
                    tvNoInsurance.setText(jsonObject.optString("message"));
                    layNoInsurance.setVisibility(View.VISIBLE);
                }else {
                    layNoInsurance.setVisibility(View.GONE);
                }*/

				myInsuranceBeen = new ArrayList<>();
				MyInsuranceBean myInsuranceBean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String patient_id = data.getJSONObject(i).getString("patient_id");
					String insurance = data.getJSONObject(i).getString("insurance");
					String policy_number = data.getJSONObject(i).getString("policy_number");
					String insurance_group = data.getJSONObject(i).getString("insurance_group");
					String insurance_code = data.getJSONObject(i).getString("insurance_code");
					String payer_name = data.getJSONObject(i).getString("payer_name");
					String copay_uc = data.getJSONObject(i).getString("copay_uc");

					String inc_front = data.getJSONObject(i).optString("inc_front");
					String inc_back = data.getJSONObject(i).optString("inc_back");
					String id_front = data.getJSONObject(i).optString("id_front");
					String id_back = data.getJSONObject(i).optString("id_back");

					myInsuranceBean = new MyInsuranceBean(id,patient_id,insurance,policy_number,insurance_group,insurance_code,payer_name,copay_uc,
							inc_front, inc_back, id_front, id_back);
					myInsuranceBeen.add(myInsuranceBean);
					myInsuranceBean = null;
				}

				spInsuranceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, myInsuranceBeen);
				spSelectInsurance.setAdapter(spInsuranceAdapter);

				sharedPrefsHelper.savePatientInrances(myInsuranceBeen);

				//btnToolbar.setVisibility( (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE);

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}



	public void getMyInsurance(){
		RequestParams params = new RequestParams();
		params.put("patient_id",prefs.getString("id",""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_MY_INSURANCE,"post",params,this, activity);
		apiManager.loadURL();
	}


	public void displayInsurance(){
		if(myInsuranceBeanNewIns != null){
			StringBuilder sbInsDisplay = new StringBuilder();
			sbInsDisplay.append("Payer Name : "+myInsuranceBeanNewIns.payer_name);
			sbInsDisplay.append("\n");
			sbInsDisplay.append("Policy/ID : "+myInsuranceBeanNewIns.policy_number);
			sbInsDisplay.append("\n");
			sbInsDisplay.append("Group ID : "+myInsuranceBeanNewIns.insurance_group);

			tvPatientInsurance.setText(sbInsDisplay.toString());
		}

		//new HideShowKeypad(activity).hidekeyboardOnDialog();
		hideShowKeypad.hidekeyboardOnDialog();
	}




	//pick Insurance card image front + back on add new insurance
	LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
	public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
		liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
		liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(liveCareInsuranceCardhelper != null){
			//Insurance card image
			liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}





	public boolean emptyValidate(){
		EditText[] editTexts = {etAdmemberFname,etAdmemberLname,etAdmemberOccupation,etAdmemberDob,etAdmemberRelationship,etAdmemberPass,etAdmemberConfirmPass,etAdmemberEmail,etAdmemberUsername,
				etSignupAddress, etSignupCity, etSignupCountry,etSignupZipcode,etSignupPhone};
		boolean validate = true;
		for (int i = 0; i < editTexts.length; i++) {
			if(editTexts[i].getText().toString().trim().isEmpty()){
				editTexts[i].setError("Required");
				validate = false;
			}else {
				editTexts[i].setError(null);
			}
		}
		return validate;
	}

}
