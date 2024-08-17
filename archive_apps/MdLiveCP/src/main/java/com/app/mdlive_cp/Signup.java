package com.app.mdlive_cp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.HospitalBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.app.mdlive_cp.api.ApiManager.HOSPITALS;

public class Signup extends AppCompatActivity implements ApiCallBack{

	Activity activity;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;

	EditText etSignupFname, etSignupLname, etSignupEmail, etSignupUsername,etSignupPhone, etSignupBirthdate;
	Spinner spSignupHospital,spSignupRole;
	Button btnSubmitSignup;
	RadioGroup rgSignupGender;//rgNurseType

	private boolean isGenderSelected = false;
	private String selectedGender = "";
	String doctor_category = "";

	String selectedHospitalId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		activity = Signup.this;
		apiCallBack = this;
		checkInternet = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		hideSoftKeyboard();

		etSignupFname = (EditText) findViewById(R.id.etSignupFname);
		etSignupLname = (EditText) findViewById(R.id.etSignupLname);
		etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
		etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
		spSignupHospital = (Spinner) findViewById(R.id.spSignupHospital);
		rgSignupGender = (RadioGroup) findViewById(R.id.rgSignupGender);
		etSignupPhone = (EditText) findViewById(R.id.etSignupPhone);
		etSignupBirthdate = (EditText) findViewById(R.id.etSignupBirthdate);
		//rgNurseType = (RadioGroup) findViewById(R.id.rgNurseType);
		spSignupRole = (Spinner) findViewById(R.id.spSignupRole);
		btnSubmitSignup = (Button) findViewById(R.id.btnSubmitSignup);

		btnSubmitSignup.bringToFront();

		spSignupHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedHospitalId = hospitalBeens.get(position).id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				selectedHospitalId = "";
			}
		});

		final String [] nurseTypes = {"Select Role","Nurse","Dietitian","Social Worker","Nurse Practitioner","OT","Pharmacist","MA","Supervisor"};
		spSignupRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					doctor_category = "";
				}else {
					doctor_category = nurseTypes[position];
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				doctor_category = "";
			}
		});

		ArrayAdapter<String> nurseTypeAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, nurseTypes);
		spSignupRole.setAdapter(nurseTypeAdapter);

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
				if (hasFocus) {
					//DATA.isDatePickerCallFromSignup = true;
					DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				}

			}
		});
		etSignupBirthdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etSignupBirthdate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		btnSubmitSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

					if (etSignupFname.getText().toString().isEmpty()
							|| etSignupLname.getText().toString().isEmpty()
							|| etSignupEmail.getText().toString().isEmpty()
							|| !isGenderSelected
							|| etSignupPhone.getText().toString().isEmpty()
							|| etSignupBirthdate.getText().toString().isEmpty()
							||	selectedHospitalId.isEmpty()
							||etSignupUsername.getText().toString().isEmpty()) {

						customToast.showToast("Incomplete Information", 0, 0);

					} else if (!isValidEmail(etSignupEmail.getText())) {
						customToast.showToast("Please enter your valid email address", 0, 0);
					} else if(doctor_category.isEmpty()){
						customToast.showToast("Please select a Care Provider Role",0,1);
					} else {
						/*String doctor_category = "";
						int selectedNurseType = rgNurseType.getCheckedRadioButtonId();
						switch (selectedNurseType){
							case R.id.radioNurse:
								doctor_category = "Nurse";
								break;
							case R.id.radioSocialWorker:
								doctor_category = "Social Worker";
								break;
							case R.id.radioDietitian:
								doctor_category = "Dietitian";
								break;
							case R.id.radioPractitioner:
								doctor_category = "Practitioner Nurse";
								break;
							case R.id.radioOT:
								doctor_category = "OT";
								break;
							case R.id.radioPharmacist:
								doctor_category = "Pharmacist";
								break;
							case R.id.radioMedicalAssistant:
								doctor_category = "MA";
								break;
						}*/

						RequestParams params = new RequestParams();
						params.put("first_name", etSignupFname.getText().toString());
						params.put("last_name", etSignupLname.getText().toString());
						params.put("email", etSignupEmail.getText().toString());
						params.put("gender", selectedGender);
						params.put("phone", etSignupPhone.getText().toString());
						params.put("birthdate", etSignupBirthdate.getText().toString());
						params.put("doctor_category",doctor_category);
						params.put("hospital_id", selectedHospitalId);
						params.put("username", etSignupUsername.getText().toString());

						ApiManager apiManager = new ApiManager(ApiManager.DOCTOR_SIGNUP,"post",params,apiCallBack, activity);
						apiManager.loadURL();
					}
			}
		});

		ApiManager apiManager = new ApiManager(HOSPITALS,"post",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	ArrayList<HospitalBean> hospitalBeens;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equals(ApiManager.DOCTOR_SIGNUP)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");

				if (status.equals("success")) {
					if (jsonObject.has("msg")) {
						customToast.showToast(jsonObject.getString("msg"), 0, 0);
					} else {
						customToast.showToast("Your account has been created. Please check email to get account details", 0, 0);
					}
					openActivity.open(Login.class, true);
				} else {
					if (jsonObject.has("msg")) {
						customToast.showToast(jsonObject.getString("msg"), 0, 0);
					} else {
						customToast.showToast("Something went wrong, please try again", 0, 0);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equals(HOSPITALS)){
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
		}
	}
}
