package com.app.mdlive_uc;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mdlive_uc.api.ApiManager;
import com.app.mdlive_uc.util.DATA;
import com.app.mdlive_uc.util.DatePickerFragment;
import com.app.mdlive_uc.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFamilyMember extends BaseActivity {

	ImageView imgAdAdmembr;
	EditText etAdmemberFname,etAdmemberLname,etAdmemberOccupation,etAdmemberDob,etAdmemberRelationship,etAdmemberPass,etAdmemberConfirmPass,etAdmemberEmail,etAdmemberUsername;
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

		btnAdmemberCreateUser = (Button) findViewById(R.id.btnAdmemberCreateUser);


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

				if(TextUtils.isEmpty(fname)){
					etAdmemberFname.setError("This field is required");
					customToast.showToast("Please enter first name",0,0);
					return;
				}
				if(TextUtils.isEmpty(lname)){
					etAdmemberLname.setError("This field is required");
					customToast.showToast("Please enter last name",0,0);
					return;
				}
				if(TextUtils.isEmpty(username)){
					etAdmemberUsername.setError("This field is required");
					customToast.showToast("Please enter username",0,0);
					return;
				}
				if(TextUtils.isEmpty(email)){
					etAdmemberEmail.setError("This field is required");
					customToast.showToast("Please enter email",0,0);
					return;
				}
				if (!GloabalMethods.isValidEmail(etAdmemberEmail.getText())) {
					customToast.showToast("The email adderss should look like an email address.", 0, 0);
					etAdmemberEmail.setError("Invalid email address");
					return;
				}
				if(TextUtils.isEmpty(occupation)){
					etAdmemberOccupation.setError("This field is required");
					customToast.showToast("Please enter occupation",0,0);
					return;
				}
				if(TextUtils.isEmpty(dob)){
					etAdmemberDob.setError("This field is required");
					customToast.showToast("Please enter birthdate",0,0);
					return;
				}
				if(TextUtils.isEmpty(relationShip)){
					etAdmemberRelationship.setError("This field is required");
					customToast.showToast("Please enter relationship",0,0);
					return;
				}
				if(TextUtils.isEmpty(password)){
					etAdmemberPass.setError("This field is required");
					customToast.showToast("Please enter password",0,0);
					return;
				}
				if(TextUtils.isEmpty(confirmPass)){
					etAdmemberConfirmPass.setError("This field is required");
					customToast.showToast("Please retype password",0,0);
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
				params.put("first_name",etAdmemberFname.getText().toString());
				params.put("last_name", etAdmemberLname.getText().toString());
				params.put("occupation", etAdmemberOccupation.getText().toString());
				params.put("relationship", etAdmemberRelationship.getText().toString());
				params.put("dob", etAdmemberDob.getText().toString());
				params.put("password", etAdmemberPass.getText().toString());
				params.put("confirm_password", etAdmemberConfirmPass.getText().toString());
				params.put("email",etAdmemberEmail.getText().toString());
				params.put("username", etAdmemberUsername.getText().toString());
				params.put("marital_status", selectedMarital);
				params.put("gender", selectedGender);
				params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);//selectedHospitalId
				params.put("current_app", Login.CURRENT_APP);

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
				System.out.println("-- srevice call here 1");
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
		}
	}

}
