package com.app.mhcsn_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.ActionEditText;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.app.mhcsn_cp.util.OpenActivity;
import com.app.mhcsn_cp.util.PrescriptionsPopup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ActivitySoapNotes extends AppCompatActivity implements OnClickListener{

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	HideShowKeypad hideShowKeypad;
	SharedPreferences prefs;

	ViewFlipper vfSoapNotes;
	TextView tvSubjective,tvObjective,tvASSESMENT,tvPlan;

	TextView tvSOAPPatientName,tvSOAPDOB,tvSOAPHistory,tvSOAPAllergies;//tvSOAPComplain
	EditText etSOAPDescription,etSOAPVitalSigns,etSOAPPrescription,etSOAPFamilyHistory,etSOAPSummaryofProblem,etSOAPPlanDescription,
			etSOAPCarePlan,etSOAPComplain,etSOAPPainWhere;//etSOAPReferredto,etSOAPRefills,
	Spinner spPainSeverity;
	Button btnSoapConfirm,btnAddOTNotes;

	public static String vitals = "",diagnosis = "";
	String soapPainSeverity = "";

	ActionEditText etOTDate,etOTTimeIn,etOTTimeout,etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,
			etOTAdditionalSigns;

	Button btnPharmacy,btnPrescription;
	GloabalMethods gloabalMethods;
	BroadcastReceiver showSelectedPharmacyBroadcast;

	final String[] painSeverity = {"No Pain","Mild","Moderate","Severe","Very Severe","Worst pain possible"};

	@Override
	protected void onStart() {
		registerReceiver(showSelectedPharmacyBroadcast, new IntentFilter(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION));
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(showSelectedPharmacyBroadcast);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soap_notes);

		activity = ActivitySoapNotes.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		openActivity = new OpenActivity(activity);

		vfSoapNotes = (ViewFlipper) findViewById(R.id.vfSoapNotes);
		tvSubjective = (TextView) findViewById(R.id.tvSubjective);
		tvObjective = (TextView) findViewById(R.id.tvObjective);
		tvASSESMENT = (TextView) findViewById(R.id.tvASSESMENT);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		btnSoapConfirm = (Button) findViewById(R.id.btnSoapConfirm);
		btnAddOTNotes = (Button) findViewById(R.id.btnAddOTNotes);

		if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
			btnAddOTNotes.setVisibility(View.VISIBLE);
		}else{
			btnAddOTNotes.setVisibility(View.GONE);
		}
		tvSubjective.setOnClickListener(this);
		tvObjective.setOnClickListener(this);
		tvASSESMENT.setOnClickListener(this);
		tvPlan.setOnClickListener(this);
		btnSoapConfirm.setOnClickListener(this);
		btnAddOTNotes.setOnClickListener(this);

		tvSOAPPatientName = (TextView) findViewById(R.id.tvSOAPPatientName);
		tvSOAPDOB = (TextView) findViewById(R.id.tvSOAPDOB);
		//tvSOAPComplain = (TextView) findViewById(R.id.tvSOAPComplain);
		tvSOAPHistory = (TextView) findViewById(R.id.tvSOAPHistory);
		tvSOAPAllergies = (TextView) findViewById(R.id.tvSOAPAllergies);

		etSOAPDescription = (EditText) findViewById(R.id.etSOAPDescription);
		etSOAPVitalSigns = (EditText) findViewById(R.id.etSOAPVitalSigns);
		etSOAPPrescription = (EditText) findViewById(R.id.etSOAPPrescription);
		etSOAPFamilyHistory = (EditText) findViewById(R.id.etSOAPFamilyHistory);
		etSOAPSummaryofProblem = (EditText) findViewById(R.id.etSOAPSummaryofProblem);
		//etSOAPReferredto = (EditText) findViewById(R.id.etSOAPReferredto);
		//etSOAPRefills = (EditText) findViewById(R.id.etSOAPRefills);
		etSOAPPlanDescription = (EditText) findViewById(R.id.etSOAPPlanDescription);
		etSOAPCarePlan  = (EditText) findViewById(R.id.etSOAPCarePlan);
		etSOAPComplain = (EditText) findViewById(R.id.etSOAPComplain);
		etSOAPPainWhere = (EditText) findViewById(R.id.etSOAPPainWhere);
		spPainSeverity = (Spinner) findViewById(R.id.spPainSeverity);

		etOTDate = (ActionEditText) findViewById(R.id.etOTDate);
		etOTTimeIn = (ActionEditText) findViewById(R.id.etOTTimeIn);
		etOTTimeout = (ActionEditText) findViewById(R.id.etOTTimeout);
		etOTBP = (ActionEditText) findViewById(R.id.etOTBP);
		etOTHR = (ActionEditText) findViewById(R.id.etOTHR);
		etOTRespirations = (ActionEditText) findViewById(R.id.etOTRespirations);
		etOTO2Saturations = (ActionEditText) findViewById(R.id.etOTO2Saturations);
		etOTBloodSugar = (ActionEditText) findViewById(R.id.etOTBloodSugar);
		etOTAdditionalSigns = (ActionEditText) findViewById(R.id.etSOAPVitalSigns);//etOTAdditionalSigns  nternally same as etSOAPVitalSigns

		etOTDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		etOTTimeIn.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
		//etOTTimeout.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		etOTDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(etOTDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		etOTTimeIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.setTimeByTimePicker(activity,etOTTimeIn);
			}
		});
		etOTTimeout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.setTimeByTimePicker(activity,etOTTimeout);
			}
		});


		spPainSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				soapPainSeverity = painSeverity[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, painSeverity);
		spPainSeverity.setAdapter(adapter);

		if (checkInternetConnection.isConnectedToInternet()) {
			if(DATA.isFromDocToDoc){
				getNoteDataByPatientID(DATA.selectedUserCallId,"livecare");//ActivityFollowUps_2.selectedFollowupBean.patient_id
			}else{
				//getNoteData(DATA.selectedUserCallId, DATA.selectedUserAppntID,"livecare");//commented this
				getNoteDataByPatientID(DATA.selectedUserCallId,"livecare");
			}
			/*if (DATA.isFromAppointment) {
				getNoteData(DATA.selectedUserCallId, DATA.drAppointmentModel.getId() ,"appointment");
			} else {
				getNoteData(DATA.selectedUserCallId, DATA.selectedUserAppntID,"livecare");
			}*/

		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}


		btnPharmacy = (Button) findViewById(R.id.btnPharmacy);
		btnPrescription = (Button) findViewById(R.id.btnPrescription);

		btnPrescription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PrescriptionsPopup(ActivitySoapNotes.this).initDoubleVerficationDialog();
			}
		});

		gloabalMethods = new GloabalMethods(activity);

		if (checkInternetConnection.isConnectedToInternet()) {
			gloabalMethods.getPharmacy("",false);
		} else {
			customToast.showToast("Network not connected", 0, 0);
		}

		btnPharmacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (GloabalMethods.pharmacyBeans != null) {
					gloabalMethods.showPharmacyDialog();
				} else {
					DATA.print("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true);
					} else {
						customToast.showToast("Network not connected", 0, 0);
					}
				}
			}
		});

		showSelectedPharmacyBroadcast = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION)) {


			    /*gloabalMethods.showPharmacyMap(GloabalMethods.selectedPharmacyBean);

			    if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
			    	layPharmacyInfo.setVisibility(View.GONE);
			    	tvNoPharmacyAdded.setVisibility(View.VISIBLE);
				}else {
					layPharmacyInfo.setVisibility(View.VISIBLE);
					tvNoPharmacyAdded.setVisibility(View.GONE);
				    tvPharmacyName.setText(GloabalMethods.selectedPharmacyBean.StoreName);
				    tvPharmacyStreetAddress.setText(GloabalMethods.selectedPharmacyBean.address);
				    tvPharmacyCity.setText(GloabalMethods.selectedPharmacyBean.city);
				    tvPharmacyState.setText(GloabalMethods.selectedPharmacyBean.state);
				    tvPharmacyZipcode.setText(GloabalMethods.selectedPharmacyBean.zipcode);
				    tvPharmacyPhone.setText(GloabalMethods.selectedPharmacyBean.PhonePrimary);
				    tvPharmacyCategories.setText(GloabalMethods.selectedPharmacyBean.SpecialtyName);
				}*/

					if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
						btnPharmacy.setText("Select Pharmacy");
					}else {
						btnPharmacy.setText("Change Pharmacy");
					}
				}
			}
		};

	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.tvSubjective:
				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(0);
				break;
			case R.id.tvObjective:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvObjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(1);
				break;
			case R.id.tvASSESMENT:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(2);
				break;
			case R.id.tvPlan:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvPlan.setTextColor(Color.parseColor("#FFFFFF"));

				vfSoapNotes.setDisplayedChild(3);
				break;
			case R.id.btnSoapConfirm:
				submitSOAP_Notes();
				break;
			case R.id.btnAddOTNotes:
				openActivity.open(ActivityOTPT_Notes.class,false);
				break;
			default:
				break;
		}
	}



	public void getNoteData(String patientId, String liveCareId,String type) {


		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);

		DATA.print("-- url in getNoteData: "+DATA.baseUrl+"getNoteData/"+patientId+"/"+liveCareId+"/"+type);


		client.post(DATA.baseUrl+"getNoteData/"+patientId+"/"+liveCareId+"/"+type, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getNoteData "+content);

					try {
						JSONObject data = new JSONObject(content).getJSONObject("data");
						String first_name = data.getString("first_name");
						String last_name = data.getString("last_name");
						String birthdate = data.getString("birthdate");
						String symptom_name = data.getString("symptom_name");
						String condition_name = data.getString("condition_name");
						String medical_history = data.getString("medical_history");
						String is_smoke = data.getString("is_smoke");
						String smoke_detail = data.getString("smoke_detail");
						String is_drink = data.getString("is_drink");
						String drink_detail = data.getString("drink_detail");
						String is_drug = data.getString("is_drug");
						String drug_detail = data.getString("drug_detail");
						String is_alergies = data.getString("is_alergies");
						String alergies_detail = data.getString("alergies_detail");
						String treatment = data.getString("treatment");
						String description = data.getString("description");
						String phistory = data.getString("phistory");

						String familyhistory = data.getString("familyhistory");

						//SoapNoteDataBean soapNoteDataBean = new SoapNoteDataBean(first_name, last_name, birthdate, symptom_name, condition_name, medical_history, is_smoke, smoke_detail, is_drink, drink_detail, is_drug, drug_detail, is_alergies, alergies_detail, treatment, description, phistory);

						if (is_smoke.equalsIgnoreCase("1")) {
							if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
								String[] smokeArr = smoke_detail.split("/");
								if (smokeArr.length < 2) {

								}else {
									phistory = phistory + "\n" + "Smoke Details: "+smokeArr[0] + "\n" + smokeArr[1];
								}
							}
						}

						if (is_drink.equalsIgnoreCase("1")) {
							if ((!drink_detail.isEmpty()) || (!drink_detail.equalsIgnoreCase("null"))) {
								String[] drinkArr = drink_detail.split("/");
								if (drinkArr.length < 2) {

								}else {
									phistory = phistory + "\n" + "Drink Details: "+drinkArr[0] + "\n" + drinkArr[1];
								}
							}
						}

						if (is_drug.equalsIgnoreCase("1")) {
							if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
								phistory = phistory + "\nDrugs details: "+drug_detail;
							}
						}

						tvSOAPPatientName.setText(first_name+" "+last_name);
						tvSOAPDOB.setText(birthdate);
						//tvSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);

						tvSOAPHistory.setText(phistory);
						tvSOAPAllergies.setText(alergies_detail);

						etSOAPDescription.setText(description);
						//etSOAPVitalSigns.setText("");
						//etSOAPPrescription.setText(treatment);
						etSOAPFamilyHistory.setText(familyhistory);
						//etSOAPSummaryofProblem.setText("");
						//etSOAPReferredto.setText("");
						//etSOAPRefills.setText("");
						etSOAPPlanDescription.setText(ActivityTelemedicineServices.tmsCodesWithNames);

					/*etSOAPVitalSigns.setText(vitals);
					etSOAPSummaryofProblem.setText(diagnosis);
					String medicationsWithInstructions = "";
					if (DATA.drugBeans != null) {
						for (int i = 0; i < DATA.drugBeans.size(); i++) {
							medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
							medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
						}
					}

					etSOAPPrescription.setText(medicationsWithInstructions);*/

						if(data.has("ot_data")&& !data.getString("ot_data").isEmpty()){
							JSONObject ot_data = new JSONObject(data.getString("ot_data"));
						/*if(ot_data.has("ot_date")){
							String ot_date = ot_data.getString("ot_date");
							etOTDate.setText(ot_date);
						}
						if(ot_data.has("ot_timein")){
							String ot_timein = ot_data.getString("ot_timein");
							etOTTimeIn.setText(ot_timein);
						}
						if(ot_data.has("ot_timeout")){
							String ot_timeout = ot_data.getString("ot_timeout");
							etOTTimeout.setText(ot_timeout);
						}*/
							if(ot_data.has("ot_respirations")){
								String ot_respirations = ot_data.getString("ot_respirations");
								etOTRespirations.setText(ot_respirations);
							}
							if(ot_data.has("ot_blood_sugar")){
								String ot_blood_sugar = ot_data.getString("ot_blood_sugar");
								etOTBloodSugar.setText(ot_blood_sugar);
							}
							if(ot_data.has("ot_hr")){
								String ot_hr = ot_data.getString("ot_hr");
								etOTHR.setText(ot_hr);
							}
							if(ot_data.has("ot_bp")){
								String ot_bp = ot_data.getString("ot_bp");
								etOTBP.setText(ot_bp);
							}
							if(ot_data.has("ot_saturation")){
								String ot_saturation = ot_data.getString("ot_saturation");
								etOTO2Saturations.setText(ot_saturation);
							}
							if(ot_data.has("ot_additional_vitals")){
								String ot_additional_vitals = ot_data.getString("ot_additional_vitals");
								//etOTAdditionalSigns.setText(ot_additional_vitals);
							}
						}

						JSONObject notes = new JSONObject(data.getString("notes"));
						if(notes.has("objective")){
							etSOAPVitalSigns.setText(notes.getString("objective"));
						}
						if(notes.has("assesment")){
							etSOAPSummaryofProblem.setText(notes.getString("assesment"));
						}
						if(notes.has("prescription")){
							etSOAPPrescription.setText(notes.getString("prescription"));
						}else {
							etSOAPPrescription.setText(treatment);
						}
						if(notes.has("complain")){
							etSOAPComplain.setText(notes.getString("complain"));
						}else{
							etSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
						}

					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getNoteData, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("--onFailure in getNoteData "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getNoteData


	public void saveDrNotes(String soapPainWhere, String soapPrescription,String soapComplain,String notes_history,String notes_subjective_description,String prescription,String notes_family,
							String notes_assesment_summary,String notes_plan_description,String notes_objective_careplan,String livecareId) {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("notes[history]", notes_history);
		params.put("notes[subjective]", notes_subjective_description);
		params.put("notes[objective]", prescription);
		params.put("notes[family]", notes_family);
		params.put("notes[assesment]", notes_assesment_summary);
		params.put("notes[plan]", notes_plan_description);
		params.put("notes[care_plan]", notes_objective_careplan);
		params.put("notes[prescription]", soapPrescription);
		params.put("notes[complain]", soapComplain);
		params.put("notes[pain_where]", soapPainWhere);
		params.put("notes[pain_severity]", soapPainSeverity);
		params.put("id", livecareId);

		params.put("treatment_codes", ActivityTelemedicineServices.tmsCodes);

		params.put("doctor_id", prefs.getString("id",""));

		params.put("ot_date" , etOTDate.getText().toString());
		params.put("ot_timein" , etOTTimeIn.getText().toString());
		params.put("ot_timeout" , etOTTimeout.getText().toString());
		params.put("ot_bp" , etOTBP.getText().toString());
		params.put("ot_hr" , etOTHR.getText().toString());
		params.put("ot_respirations" , etOTRespirations.getText().toString());
		params.put("ot_saturation" , etOTO2Saturations.getText().toString());
		params.put("ot_blood_sugar" , etOTBloodSugar.getText().toString());
		params.put("ot_additional_vitals" , etOTAdditionalSigns.getText().toString());

		params.put("patient_id", DATA.selectedUserCallId);
		if(!DATA.incomingCall){
			params.put("i_am_nurse", "1");
			params.put("calling_doctor_id", DATA.selectedDrId);
		}
		if(!PrescriptionsPopup.prescription_id.isEmpty()){
			params.put("prescription_id",PrescriptionsPopup.prescription_id);
		}


		DATA.print("--in saveNote params "+params.toString());
		client.post(DATA.baseUrl+"/saveNotes", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveDrNotes"+content);

					//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Your note saved successfully",0,0);
							DATA.isSOAP_NotesSent = true;
							PrescriptionsPopup.prescription_id = "";
							finish();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						DATA.print("--saveNotes json exception");
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveDrNotes, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail saveDrNotes" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDrNotes


	/*public void saveAppointmentNotes(String notes_history,String notes_subjective_description,String prescription,String notes_family,
									 String notes_assesment_summary,String notes_plan_description,String apptmentId) {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		params.put("notes[history]", notes_history);
		params.put("notes[subjective]", notes_subjective_description);
		params.put("notes[objective]", prescription);
		params.put("notes[family]", notes_family);
		params.put("notes[assesment]", notes_assesment_summary);
		params.put("notes[plan]", notes_plan_description);
		params.put("id", apptmentId);

		params.put("treatment_codes", ActivityTelemedicineServices.tmsCodes);

		DATA.print("--in saveAppointmentNotes params "+params.toString());
		client.post(DATA.baseUrl+"/saveAppointmentNotes", params, new AsyncHttpResponseHandler() {
			@Override

			public void onSuccess(int statusCode, String content) {
				DATA.dismissLoaderDefault();

				DATA.print("--reaponce in saveAppointmentNotes: "+content);


				//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
				try {
					JSONObject jsonObject = new JSONObject(content);
					if (jsonObject.has("success")) {
						Toast.makeText(activity, "Your note saved successfully", Toast.LENGTH_LONG).show();
						DATA.isSOAP_NotesSent = true;
						finish();
					} else {
						Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					DATA.print("--saveAppointmentNotes json exception");
					Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}

			@Override

			public void onFailure(Throwable error, String content) {
				DATA.dismissLoaderDefault();
				DATA.print("--onfail saveAppointmentNotes: " +content);
				Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			}
		});

	}//end saveAppointmentNotes*/


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_soap_notes, menu);

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {
			submitSOAP_Notes();
		}else if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private void submitSOAP_Notes() {
		// TODO Auto-generated method stub

		if (checkInternetConnection.isConnectedToInternet()) {

			new AlertDialog.Builder(activity).setTitle("Confirm").
					setMessage("Are you sure? Do you want to continue or edit").
					setPositiveButton("Continue", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String notes_history = tvSOAPHistory.getText().toString();
							String notes_subjective_description = etSOAPDescription.getText().toString();
							//String prescription = etSOAPPrescription.getText().toString();
							String prescription = etSOAPVitalSigns.getText().toString();//this is vitals under objective param in savenotes
							String notes_family = etSOAPFamilyHistory.getText().toString();
							String notes_assesment_summary = etSOAPSummaryofProblem.getText().toString();
							String notes_plan_description = etSOAPPlanDescription.getText().toString();
							String notes_objective_careplan = etSOAPCarePlan.getText().toString();

							String soapPrescription = etSOAPPrescription.getText().toString();
							String soapComplain = etSOAPComplain.getText().toString();
							String soapPainWhere = etSOAPPainWhere.getText().toString();


			/*if (DATA.isFromAppointment) {
				saveAppointmentNotes(notes_history, notes_subjective_description, prescription, notes_family, notes_assesment_summary, notes_plan_description, DATA.drAppointmentModel.getId());
			} else {*/
							if(DATA.isFromDocToDoc){
								saveDrNotesDocToDoc(soapPainWhere, soapPrescription,soapComplain, notes_history, notes_subjective_description, prescription, notes_family,
										notes_assesment_summary, notes_plan_description,notes_objective_careplan, DATA.selectedUserCallId);//ActivityFollowUps_2.selectedFollowupBean.patient_id
							}else {
								saveDrNotes(soapPainWhere, soapPrescription,soapComplain, notes_history, notes_subjective_description, prescription, notes_family,
										notes_assesment_summary, notes_plan_description,notes_objective_careplan, DATA.selectedUserAppntID);
							}
							//}
						}
					}).setNegativeButton("Edit",null).show();
		} else {
			customToast.showToast("No internet connection. Can not save notes. Please check internet connection and try again", 0, 1);
		}

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(activity,ActivityTelemedicineServices.class));
		super.onBackPressed();
	}


	//---------------------------------------------DOC TO DOC--------------------------------------------------------------------
	public void getNoteDataByPatientID(String patientId,String type) {


		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		DATA.print("-- url in getNoteDataByPatientID: "+DATA.baseUrl+"getNoteDataByPatientID/"+patientId+"/"+type);


		client.post(DATA.baseUrl+"getNoteDataByPatientID/"+patientId+"/"+type, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getNoteDataByPatientID "+content);

					try {
						JSONObject data = new JSONObject(content).getJSONObject("data");
						String first_name = data.getString("first_name");
						String last_name = data.getString("last_name");
						String birthdate = data.getString("birthdate");
						String symptom_name = data.getString("symptom_name");
						String condition_name = data.getString("condition_name");
						String medical_history = data.getString("medical_history");
						String is_smoke = data.getString("is_smoke");
						String smoke_detail = data.getString("smoke_detail");
						String is_drink = data.getString("is_drink");
						String drink_detail = data.getString("drink_detail");
						String is_drug = data.getString("is_drug");
						String drug_detail = data.getString("drug_detail");
						String is_alergies = data.getString("is_alergies");
						String alergies_detail = data.getString("alergies_detail");
						String treatment = data.getString("treatment");
						String description = data.getString("description");
						String phistory = data.getString("phistory");

						String familyhistory = data.getString("familyhistory");

						PrescriptionsPopup.dob = birthdate;
						PrescriptionsPopup.gender = data.getString("gender");
						PrescriptionsPopup.phone = data.getString("phone");
						PrescriptionsPopup.address = data.getString("residency");
						PrescriptionsPopup.pharmacyName = data.getString("StoreName");
						PrescriptionsPopup.pharmacyPhone = data.getString("PhonePrimary");

						//SoapNoteDataBean soapNoteDataBean = new SoapNoteDataBean(first_name, last_name, birthdate, symptom_name, condition_name, medical_history, is_smoke, smoke_detail, is_drink, drink_detail, is_drug, drug_detail, is_alergies, alergies_detail, treatment, description, phistory);

						if (is_smoke.equalsIgnoreCase("1")) {
							if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
								String[] smokeArr = smoke_detail.split("/");
								if (smokeArr.length < 2) {

								}else {
									phistory = phistory + "\n" + "Smoke Details: "+smokeArr[0] + "\n" + smokeArr[1];
								}
							}
						}

						if (is_drink.equalsIgnoreCase("1")) {
							if ((!drink_detail.isEmpty()) || (!drink_detail.equalsIgnoreCase("null"))) {
								String[] drinkArr = drink_detail.split("/");
								if (drinkArr.length < 2) {

								}else {
									phistory = phistory + "\n" + "Drink Details: "+drinkArr[0] + "\n" + drinkArr[1];
								}
							}
						}

						if (is_drug.equalsIgnoreCase("1")) {
							if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
								phistory = phistory + "\nDrugs details: "+drug_detail;
							}
						}

						tvSOAPPatientName.setText(first_name+" "+last_name);
						tvSOAPDOB.setText(birthdate);
						//tvSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
						//etSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
						tvSOAPHistory.setText(phistory);
						tvSOAPAllergies.setText(alergies_detail);

						//etSOAPDescription.setText(description);
						//etSOAPVitalSigns.setText("");
						//etSOAPPrescription.setText(treatment);
						etSOAPFamilyHistory.setText(familyhistory);
						//etSOAPSummaryofProblem.setText("");
						//etSOAPReferredto.setText("");
						//etSOAPRefills.setText("");
						etSOAPPlanDescription.setText(ActivityTelemedicineServices.tmsCodesWithNames);

					/*etSOAPVitalSigns.setText(vitals);
					etSOAPSummaryofProblem.setText(diagnosis);
					String medicationsWithInstructions = "";
					if (DATA.drugBeans != null) {
						for (int i = 0; i < DATA.drugBeans.size(); i++) {
							medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
							medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
						}
					}

					etSOAPPrescription.setText(medicationsWithInstructions);*/

						if(data.has("virtual_ot_data")&& !data.getString("virtual_ot_data").isEmpty()){
							JSONObject virtual_ot_data = new JSONObject(data.getString("virtual_ot_data"));
							if(virtual_ot_data.has("ot_respirations")){
								String ot_respirations = virtual_ot_data.getString("ot_respirations");
								etOTRespirations.setText(ot_respirations);
							}
							if(virtual_ot_data.has("ot_blood_sugar")){
								String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
								etOTBloodSugar.setText(ot_blood_sugar);
							}
							if(virtual_ot_data.has("ot_hr")){
								String ot_hr = virtual_ot_data.getString("ot_hr");
								etOTHR.setText(ot_hr);
							}
							if(virtual_ot_data.has("ot_bp")){
								String ot_bp = virtual_ot_data.getString("ot_bp");
								etOTBP.setText(ot_bp);
							}
							if(virtual_ot_data.has("ot_saturation")){
								String ot_saturation = virtual_ot_data.getString("ot_saturation");
								etOTO2Saturations.setText(ot_saturation);
							}
						}

						if(data.has("ot_data")&& !data.getString("ot_data").isEmpty()){
							JSONObject ot_data = new JSONObject(data.getString("ot_data"));
						/*if(ot_data.has("ot_date")){
							String ot_date = ot_data.getString("ot_date");
							etOTDate.setText(ot_date);
						}
						if(ot_data.has("ot_timein")){
							String ot_timein = ot_data.getString("ot_timein");
							etOTTimeIn.setText(ot_timein);
						}
						if(ot_data.has("ot_timeout")){
							String ot_timeout = ot_data.getString("ot_timeout");
							etOTTimeout.setText(ot_timeout);
						}*/
						/*if(ot_data.has("ot_respirations")){
							String ot_respirations = ot_data.getString("ot_respirations");
							etOTRespirations.setText(ot_respirations);
						}
						if(ot_data.has("ot_blood_sugar")){
							String ot_blood_sugar = ot_data.getString("ot_blood_sugar");
							etOTBloodSugar.setText(ot_blood_sugar);
						}
						if(ot_data.has("ot_hr")){
							String ot_hr = ot_data.getString("ot_hr");
							etOTHR.setText(ot_hr);
						}
						if(ot_data.has("ot_bp")){
							String ot_bp = ot_data.getString("ot_bp");
							etOTBP.setText(ot_bp);
						}
						if(ot_data.has("ot_saturation")){
							String ot_saturation = ot_data.getString("ot_saturation");
							etOTO2Saturations.setText(ot_saturation);
						}*/
							if(ot_data.has("ot_additional_vitals")){
								String ot_additional_vitals = ot_data.getString("ot_additional_vitals");
								//etOTAdditionalSigns.setText(ot_additional_vitals);
							}
						}

						if(!data.getString("notes").isEmpty()){
							JSONObject notes = new JSONObject(data.getString("notes"));

							if(notes.has("objective")){
								etSOAPVitalSigns.setText(notes.getString("objective"));
							}
							if(notes.has("assesment")){
								etSOAPSummaryofProblem.setText(notes.getString("assesment"));
							}
						/*if(notes.has("prescription")){
							etSOAPPrescription.setText(notes.getString("prescription"));
						}else {
							etSOAPPrescription.setText(treatment);
						}*/
							etSOAPPrescription.setText(treatment);

							if(notes.has("complain")){
								etSOAPComplain.setText(notes.getString("complain"));
							}else{
								etSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
							}
						}

						String additional_data = data.getString("additional_data");
						if(!additional_data.isEmpty()){
							JSONObject additional_dataJSON = new JSONObject(additional_data);
							etSOAPPainWhere.setText(additional_dataJSON.getString("pain_where"));
							String pain_severity = additional_dataJSON.getString("pain_severity");
							for (int i = 0; i < painSeverity.length; i++) {
								if(pain_severity.equalsIgnoreCase(painSeverity[i])){
									spPainSeverity.setSelection(i);
								}
							}
						}

						etSOAPDescription.setText(data.getString("virtual_visit_description"));


					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getNoteDataByPatientID, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("--onFailure in getNoteDataByPatientID "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getNoteData

	public void saveDrNotesDocToDoc(String soapPainWhere, String soapPrescription,String soapComplain,String notes_history,String notes_subjective_description,String prescription,String notes_family,
							String notes_assesment_summary,String notes_plan_description,String notes_objective_careplan,String patientId) {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("notes[history]", notes_history);
		params.put("notes[subjective]", notes_subjective_description);
		params.put("notes[objective]", prescription);
		params.put("notes[family]", notes_family);
		params.put("notes[assesment]", notes_assesment_summary);
		params.put("notes[plan]", notes_plan_description);
		params.put("notes[care_plan]", notes_objective_careplan);
		params.put("notes[prescription]", soapPrescription);
		params.put("notes[complain]", soapComplain);
		params.put("notes[pain_where]", soapPainWhere);
		params.put("notes[pain_severity]", soapPainSeverity);
		params.put("id", "");
		params.put("patient_id", patientId);

		params.put("treatment_codes", ActivityTelemedicineServices.tmsCodes);

		params.put("doctor_id", prefs.getString("id",""));

		params.put("ot_date" , etOTDate.getText().toString());
		params.put("ot_timein" , etOTTimeIn.getText().toString());
		params.put("ot_timeout" , etOTTimeout.getText().toString());
		params.put("ot_bp" , etOTBP.getText().toString());
		params.put("ot_hr" , etOTHR.getText().toString());
		params.put("ot_respirations" , etOTRespirations.getText().toString());
		params.put("ot_saturation" , etOTO2Saturations.getText().toString());
		params.put("ot_blood_sugar" , etOTBloodSugar.getText().toString());
		params.put("ot_additional_vitals" , etOTAdditionalSigns.getText().toString());

		if(!DATA.incomingCall){
			params.put("i_am_nurse", "1");
			params.put("calling_doctor_id", DATA.selectedDrId);
		}

		if(!PrescriptionsPopup.prescription_id.isEmpty()){
			params.put("prescription_id",PrescriptionsPopup.prescription_id);
		}

		DATA.print("--in saveNote params "+params.toString());
		client.post(DATA.baseUrl+"/saveNotes", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveDrNotes"+content);

					//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Your note saved successfully",0,0);
							DATA.isSOAP_NotesSent = true;
							PrescriptionsPopup.prescription_id = "";
							finish();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						DATA.print("--saveNotes json exception");
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveDrNotes, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail saveDrNotes" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDrNotes

	public void clearComplain(View v){
		AlertDialog alertDialog = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm")
				.setMessage("Complain will be cleared. Are you sure ?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etSOAPComplain.setText("");
					}
				}).setNegativeButton("No",null).create();

		alertDialog.show();
	}

	/*public void appendNewPrescriptionInSOAP(){
		String medicationsWithInstructions = "";
		if (DATA.drugBeans != null) {
			for (int i = 0; i < DATA.drugBeans.size(); i++) {
				medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
				medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
			}
		}
		etSOAPPrescription.append(medicationsWithInstructions);
	}*/
}
