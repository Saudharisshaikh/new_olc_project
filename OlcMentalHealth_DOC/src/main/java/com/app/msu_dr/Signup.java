package com.app.msu_dr;

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

import com.app.msu_dr.R;
import com.app.msu_dr.api.ApiCallBack;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.HospitalBean;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.DatePickerFragment;
import com.app.msu_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.app.msu_dr.api.ApiManager.HOSPITALS;


public class Signup extends AppCompatActivity implements ApiCallBack{
	
	Activity activity;
	CheckInternetConnection checkInternet;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	
	//TextView tvSignupCap;
	EditText etSignupFname,etSignupLname,etSignupEmail,etSignupPassword,etSignupPhone,etSignupBirthdate,etSignupUsername;
	Button btnSubmitSignup;
	Spinner spSignupHospital;
	RadioGroup rgSignupGender;
	
	//private final String gendrArr[] = {"Select Gender", "Male", "Female"};
	private  boolean isGenderSelected = false;
	private String selectedGender = "";

	String selectedHospitalId = "";
	
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

//		tvSignupCap = (TextView) findViewById(R.id.tvSignupCap);
		
		etSignupFname = (EditText) findViewById(R.id.etSignupFname);
		etSignupLname = (EditText) findViewById(R.id.etSignupLname);
//		etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
		etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
		//etSignupPassword = (EditText) findViewById(R.id.etSignupPassword);
		spSignupHospital = (Spinner) findViewById(R.id.spSignupHospital);
		rgSignupGender = (RadioGroup) findViewById(R.id.rgSignupGender);
		etSignupPhone = (EditText) findViewById(R.id.etSignupPhone);
		etSignupBirthdate = (EditText) findViewById(R.id.etSignupBirthdate);
		etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
		
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
					
					if(etSignupFname.getText().toString().isEmpty() 
						||	etSignupLname.getText().toString().isEmpty()
						||	etSignupEmail.getText().toString().isEmpty()
		//				||	etSignupPassword.getText().toString().isEmpty()
						||	!isGenderSelected
						//||	etSignupUsername.getText().toString().isEmpty()
						||	etSignupPhone.getText().toString().isEmpty()
						||	etSignupBirthdate.getText().toString().isEmpty()
						||	selectedHospitalId.isEmpty()
						||etSignupUsername.getText().toString().isEmpty()) {
												
						customToast.showToast("Incomplete Information", 0, 0);

					} else if (!isValidEmail(etSignupEmail.getText())) {

						customToast.showToast("The email adderss should look like an email address.", 0, 0);
						
					}else {
						
						RequestParams params = new RequestParams();
						params.put("first_name",etSignupFname.getText().toString());
						params.put("last_name", etSignupLname.getText().toString());
			//			params.put("username", etSignupUsername.getText().toString());
						params.put("email", etSignupEmail.getText().toString());
				//		params.put("password", etSignupPassword.getText().toString());
						params.put("gender", selectedGender);
						params.put("phone", etSignupPhone.getText().toString());
						params.put("birthdate", etSignupBirthdate.getText().toString());
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
	    if(getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    }
	}

	/**
	 * Shows the soft keyboard
	 */
	public void showSoftKeyboard(View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	    view.requestFocus();
	    inputMethodManager.showSoftInput(view, 0);
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}

	ArrayList<HospitalBean> hospitalBeens;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equals(ApiManager.DOCTOR_SIGNUP)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				//String msg = jsonObject.getString("msg");
				if(status.equals("success")) {
					if (jsonObject.has("msg")) {
						customToast.showToast(jsonObject.getString("msg"), 0, 1);
					} else {
						customToast.showToast("Your account has been created. Please check email to get account details", 0, 1);
					}
					openActivity.open(Login.class, true);
				}else {
					if (jsonObject.has("msg")) {
						customToast.showToast(jsonObject.getString("msg"), 0, 1);
					} else {
						customToast.showToast("Something went wrong, please try again", 0, 0);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
