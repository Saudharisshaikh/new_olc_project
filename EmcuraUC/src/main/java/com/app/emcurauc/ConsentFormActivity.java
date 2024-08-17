package com.app.emcurauc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.util.CheckInternetConnection;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.DatePickerFragment;
import com.app.emcurauc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ConsentFormActivity extends AppCompatActivity {
	
	Activity activity;
	SharedPreferences prefs;
	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	ProgressDialog pd;
	
	EditText etSpecialmedicalproblems,etLasttetanusshot,etMedicationallergies,
	etFullName,etDOB,etInsurance,etPolicynumber,etGroup,etCode;
	RadioGroup rgHistoryofasthama,rgHistoryofheart,rgHistoryofsiezures,rgInsurance;
	RadioButton radioHistoryofasthamaYes,radioHistoryofasthamaNo,radioHistoryofheartYes,
	radioHistoryofheartNo,radioHistoryofsiezuresYes,radioHistoryofsiezuresNo,radioInsuranceYes,
	radioInsuranceNo;
	
	Button btnSubmitForm,btnSkipForm;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consent_form);
		
		activity = ConsentFormActivity.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Submitting...");
		pd.setCanceledOnTouchOutside(false);
		
		
		etSpecialmedicalproblems = (android.widget.EditText) findViewById(R.id.etSpecialmedicalproblems);
		etLasttetanusshot = (android.widget.EditText) findViewById(R.id.etLasttetanusshot);
		etMedicationallergies = (android.widget.EditText) findViewById(R.id.etMedicationallergies);
		etFullName = (android.widget.EditText) findViewById(R.id.etFullName);
		etDOB = (android.widget.EditText) findViewById(R.id.etDOB);
		etInsurance = (android.widget.EditText) findViewById(R.id.etInsurance);
		etPolicynumber = (android.widget.EditText) findViewById(R.id.etPolicynumber);
		etGroup = (android.widget.EditText) findViewById(R.id.etGroup);
		etCode = (android.widget.EditText) findViewById(R.id.etCode);
		
		rgHistoryofasthama = (RadioGroup) findViewById(R.id.rgHistoryofasthama);
		rgHistoryofheart = (RadioGroup) findViewById(R.id.rgHistoryofheart);
		rgHistoryofsiezures = (RadioGroup) findViewById(R.id.rgHistoryofsiezures);
		rgInsurance = (RadioGroup) findViewById(R.id.rgInsurance);
		
		radioHistoryofasthamaYes = (RadioButton) findViewById(R.id.radioHistoryofasthamaYes);
		radioHistoryofasthamaNo = (RadioButton) findViewById(R.id.radioHistoryofasthamaNo);
		radioHistoryofheartYes = (RadioButton) findViewById(R.id.radioHistoryofheartYes);
		radioHistoryofheartNo = (RadioButton) findViewById(R.id.radioHistoryofheartNo);
		radioHistoryofsiezuresYes = (RadioButton) findViewById(R.id.radioHistoryofsiezuresYes);
		radioHistoryofsiezuresNo = (RadioButton) findViewById(R.id.radioHistoryofsiezuresNo);
		radioInsuranceYes = (RadioButton) findViewById(R.id.radioInsuranceYes);
		radioInsuranceNo = (RadioButton) findViewById(R.id.radioInsuranceNo);
		
		etFullName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
		etDOB.setText(prefs.getString("birthdate", ""));
		etLasttetanusshot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etLasttetanusshot);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		
		
		
		btnSubmitForm = (Button) findViewById(R.id.btnSubmitForm);
		btnSkipForm = (Button) findViewById(R.id.btnSkipForm);
		btnSubmitForm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (etSpecialmedicalproblems.getText().toString().isEmpty() || etLasttetanusshot.getText().toString().isEmpty() ||
						etMedicationallergies.getText().toString().isEmpty() || etFullName.getText().toString().isEmpty() || 
						etDOB.getText().toString().isEmpty() || etInsurance.getText().toString().isEmpty() || etPolicynumber.getText().toString().isEmpty() || 
						etGroup.getText().toString().isEmpty() || etCode.getText().toString().isEmpty()) {
					Toast.makeText(activity, "All fields are required", Toast.LENGTH_SHORT).show();
				} else {
					saveFormData();
				}
			}
		});
		
		btnSkipForm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(activity,PatientConsentActivityNew.class));//PatientConsentActivity
				finish();
			}
		});
		
		rgInsurance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radioInsuranceYes:
					etInsurance.setVisibility(View.VISIBLE);
					etPolicynumber.setVisibility(View.VISIBLE);
					etGroup.setVisibility(View.VISIBLE);
					etCode.setVisibility(View.VISIBLE);
					break;
				case R.id.radioInsuranceNo:
					etInsurance.setVisibility(View.GONE);
					etPolicynumber.setVisibility(View.GONE);
					etGroup.setVisibility(View.GONE);
					etCode.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			}
		});
		
		if (checkInternetConnection.isConnectedToInternet()) {
			getFormData();
		} else {
			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
		}
	}

	public void saveFormData() {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);*/
		Editor editor = prefs.edit();
		if (!(etInsurance.getText().toString().isEmpty() && etPolicynumber.getText().toString().isEmpty())) {
			editor.putBoolean("isInsuranceInfoAdded", true);
			editor.putString("insurance", etInsurance.getText().toString());
			editor.putString("policy_number", etPolicynumber.getText().toString());
			editor.putString("group", etGroup.getText().toString());
			editor.putString("code", etCode.getText().toString());
			editor.commit();
			pd.show();
		}		
		
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));
		params.put("special_problems", etSpecialmedicalproblems.getText().toString());
		params.put("last_tetanus_shot", etLasttetanusshot.getText().toString());
		params.put("medication_allergies", etMedicationallergies.getText().toString());
		params.put("insurance", etInsurance.getText().toString());
		params.put("policy_number", etPolicynumber.getText().toString());
		params.put("group", etGroup.getText().toString());
		params.put("code", etCode.getText().toString());
		
		int rgAsthamaValue = rgHistoryofasthama.getCheckedRadioButtonId();
		if (rgAsthamaValue == R.id.radioHistoryofasthamaYes) {
			params.put("history_asthma", "1");
		} else {
			params.put("history_asthma", "0");
		}
		
		int rgHistoryofheartValue = rgHistoryofheart.getCheckedRadioButtonId();
		if (rgHistoryofheartValue == R.id.radioHistoryofheartYes) {
			params.put("heart_history", "1");
		} else {
			params.put("heart_history", "0");
		}
		int rgHistoryofsiezuresValue = rgHistoryofsiezures.getCheckedRadioButtonId();
		if (rgHistoryofsiezuresValue == R.id.radioHistoryofsiezuresYes) {
			params.put("history_seizures", "1");
		} else {
			params.put("history_seizures", "0");
		}
		

		client.post(DATA.baseUrl+"/genericConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);
					DATA.print("--reaponce in saveFormData "+content);
//06-09 16:59:14.591: I/System.out(18945): --reaponce in saveFormData {"success":1,"message":"Saved."}
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(content);

						if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {

							//prefs.edit().putBoolean("isConcentFilled", true).commit();

							Toast.makeText(activity, "You information saved successfully", Toast.LENGTH_LONG).show();
							startActivity(new Intent(activity,PatientConsentActivityNew.class));//PatientConsentActivity
							finish();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
						}

					} catch (JSONException e) {
						Toast.makeText(activity, "Internal server error. Type: json exception", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveFormData, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- saveFormData on fail "+content);
					  new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end signup
	
	
	public void getFormData() {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);*/
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));
		


		client.post(DATA.baseUrl+"/getGenericConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getGenericConsent "+content);
					try {
						JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

						String id = jsonObject.getString("id");
						String patient_id = jsonObject.getString("patient_id");
						String special_problems = jsonObject.getString("special_problems");
						String  last_tetanus_shot = jsonObject.getString("last_tetanus_shot");
						String  medication_allergies = jsonObject.getString("medication_allergies");
						String history_asthma = jsonObject.getString("history_asthma");
						String history_seizures = jsonObject.getString("history_seizures");
						String heart_history = jsonObject.getString("heart_history");
						String insurance = jsonObject.getString("insurance");
						String policy_number = jsonObject.getString("policy_number");
						String group = jsonObject.getString("group");
						String code = jsonObject.getString("code");


						etSpecialmedicalproblems.setText(special_problems);
						etLasttetanusshot.setText(last_tetanus_shot);
						etMedicationallergies.setText(medication_allergies);
//					etFullName.setText("");
//					etDOB.setText("");
						etInsurance.setText(insurance);
						etPolicynumber.setText(policy_number);
						etGroup.setText(group);
						etCode.setText(code);

						if (history_asthma.equalsIgnoreCase("1")) {
							radioHistoryofasthamaYes.setChecked(true);
						} else {
							radioHistoryofasthamaNo.setChecked(true);
						}

						if (history_seizures.equalsIgnoreCase("1")) {
							radioHistoryofsiezuresYes.setChecked(true);
						} else {
							radioHistoryofsiezuresNo.setChecked(false);
						}

						if (heart_history.equalsIgnoreCase("1")) {
							radioHistoryofheartYes.setChecked(true);
						} else {
							radioHistoryofheartNo.setChecked(true);
						}

						Editor editor = prefs.edit();
						editor.putBoolean("isInsuranceInfoAdded", true);
						editor.putString("insurance", insurance);
						editor.putString("policy_number", policy_number);
						editor.putString("group", group);
						editor.putString("code", code);
						editor.commit();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getGenericConsent, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- getGenericConsent on fail "+content);
					  new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getFormData
}
