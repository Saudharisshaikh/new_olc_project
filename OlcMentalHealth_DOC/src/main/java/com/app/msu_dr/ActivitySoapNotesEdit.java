package com.app.msu_dr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.msu_dr.R;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.NotesBean;
import com.app.msu_dr.model.SoapNoteDataBean;
import com.app.msu_dr.util.ActionEditText;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.DatePickerFragment;
import com.app.msu_dr.util.GloabalMethods;
import com.app.msu_dr.util.HideShowKeypad;
import com.app.msu_dr.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ActivitySoapNotesEdit extends AppCompatActivity implements OnClickListener{

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
			etSOAPCarePlan,etSOAPComplain;//etSOAPReferredto,etSOAPRefills,
	Button btnSoapConfirm,btnEditServices;

	//public static String vitals = "",diagnosis = "";

	public static NotesBean selectedNotesBean;

	public static final String IS_FOR_EDIT_NOTE_KEY = "isForEditNote";
	boolean isForEditNote = false;

	ActionEditText etOTDate,etOTTimeIn,etOTTimeout,etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,
			etOTAdditionalSigns;

	@Override
	protected void onResume() {
		etSOAPPlanDescription.setText(ActivityTelemedicineServicesEdit.tmsCodesWithNames);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soap_notes_edit);

		isForEditNote = getIntent().getBooleanExtra(IS_FOR_EDIT_NOTE_KEY, false);

		activity = ActivitySoapNotesEdit.this;
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		vfSoapNotes = (ViewFlipper) findViewById(R.id.vfSoapNotes);
		tvSubjective = (TextView) findViewById(R.id.tvSubjective);
		tvObjective = (TextView) findViewById(R.id.tvObjective);
		tvASSESMENT = (TextView) findViewById(R.id.tvASSESMENT);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		btnSoapConfirm = (Button) findViewById(R.id.btnSoapConfirm);
		btnEditServices = (Button) findViewById(R.id.btnEditServices);

		tvSubjective.setOnClickListener(this);
		tvObjective.setOnClickListener(this);
		tvASSESMENT.setOnClickListener(this);
		tvPlan.setOnClickListener(this);
		btnSoapConfirm.setOnClickListener(this);
		btnEditServices.setOnClickListener(this);

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

		etOTDate = (ActionEditText) findViewById(R.id.etOTDate);
		etOTTimeIn = (ActionEditText) findViewById(R.id.etOTTimeIn);
		etOTTimeout = (ActionEditText) findViewById(R.id.etOTTimeout);
		etOTBP = (ActionEditText) findViewById(R.id.etOTBP);
		etOTHR = (ActionEditText) findViewById(R.id.etOTHR);
		etOTRespirations = (ActionEditText) findViewById(R.id.etOTRespirations);
		etOTO2Saturations = (ActionEditText) findViewById(R.id.etOTO2Saturations);
		etOTBloodSugar = (ActionEditText) findViewById(R.id.etOTBloodSugar);
		etOTAdditionalSigns = (ActionEditText) findViewById(R.id.etSOAPVitalSigns);//etOTAdditionalSigns  internaly same as etSOAPVitalSigns

		etOTDate.setText(selectedNotesBean.ot_date);
		etOTTimeIn.setText(selectedNotesBean.ot_timein);
		etOTTimeout.setText(selectedNotesBean.ot_timeout);
		etOTBP.setText(selectedNotesBean.ot_bp);
		etOTHR.setText(selectedNotesBean.ot_hr);
		etOTRespirations.setText(selectedNotesBean.ot_respirations);
		etOTO2Saturations.setText(selectedNotesBean.ot_saturation);
		etOTBloodSugar.setText(selectedNotesBean.ot_blood_sugar);
		//etOTAdditionalSigns.setText(selectedNotesBean.ot_additional_vitals);

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

		if (checkInternetConnection.isConnectedToInternet()) {

			getNoteByNoteID(selectedNotesBean.id);

		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}

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
			case R.id.btnEditServices:
				openActivity.open(ActivityTelemedicineServicesEdit.class,false);
				break;
			default:
				break;
		}
	}



	public void getNoteByNoteID(String noteId) {


		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		String reqURL = DATA.baseUrl+"getNoteByNoteID/"+noteId;

		DATA.print("-- Request : "+reqURL);
		//DATA.print("-- params : "+params.toString());

		client.post(reqURL, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getNoteByNoteID: "+content);

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

						SoapNoteDataBean soapNoteDataBean = new SoapNoteDataBean(first_name, last_name, birthdate, symptom_name, condition_name, medical_history, is_smoke, smoke_detail, is_drink, drink_detail, is_drug, drug_detail, is_alergies, alergies_detail, treatment, description, phistory);

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

						//etSOAPPlanDescription.setText(ActivityTelemedicineServices.tmsCodesWithNames);

						String plan = data.getJSONObject("notes").getString("plan");
						etSOAPPlanDescription.setText(plan);

						String care_plan = data.getJSONObject("notes").getString("care_plan");
						etSOAPCarePlan.setText(care_plan);

						//etSOAPVitalSigns.setText(vitals);
						//etSOAPSummaryofProblem.setText(diagnosis);

						//etSOAPPrescription.setText(data.getJSONObject("notes").getString("objective"));
						//etSOAPPrescription.setText(data.getString("treatment"));
						etSOAPSummaryofProblem.setText(data.getJSONObject("notes").getString("assesment"));
						etSOAPVitalSigns.setText(data.getJSONObject("notes").getString("objective"));

						if(data.getJSONObject("notes").has("complain")){
							etSOAPComplain.setText(data.getJSONObject("notes").getString("complain"));
						}
						if(data.getJSONObject("notes").has("prescription")){
							etSOAPPrescription.setText(data.getJSONObject("notes").getString("prescription"));
						}
					/*String medicationsWithInstructions = "";
					if (DATA.drugBeans != null) {
						for (int i = 0; i < DATA.drugBeans.size(); i++) {
							medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
							medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
						}
					}

					etSOAPPrescription.setText(medicationsWithInstructions);*/

					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getNoteByNoteID


	public void saveDrNotes(String soapPrescription,String soapComplain,String notes_history,String notes_subjective_description,String prescription,String notes_family,
							String notes_assesment_summary,String notes_plan_description,String notes_objective_careplan,
							String patientId,String note_id) {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

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
		//params.put("id", livecareId);
		params.put("patient_id", patientId);
		params.put("note_id", note_id);

		params.put("treatment_codes", ActivityTelemedicineServicesEdit.tmsCodes);

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

		String editNoteURL = "";
		if(isForEditNote){
			editNoteURL = DATA.baseUrl+"doctor/updateNotes";
		}else{
			editNoteURL = DATA.baseUrl+"saveNotes";
		}

		DATA.print("--in saveNote URL "+editNoteURL);
		DATA.print("--in saveNote params "+params.toString());

		final String reqURL = editNoteURL;

		client.post(editNoteURL, params, new AsyncHttpResponseHandler() {




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
							Toast.makeText(activity, "Your note saved successfully", Toast.LENGTH_LONG).show();
						/*DATA.isSOAP_NotesSent = true;
						finish();*/
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
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDrNotes


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
							String objective_vitals = etSOAPVitalSigns.getText().toString();
							String notes_family = etSOAPFamilyHistory.getText().toString();
							String notes_assesment_summary = etSOAPSummaryofProblem.getText().toString();
							String notes_plan_description = etSOAPPlanDescription.getText().toString();
							String notes_objective_careplan = etSOAPCarePlan.getText().toString();
							String soapPrescription = etSOAPPrescription.getText().toString();
							String soapComplain = etSOAPComplain.getText().toString();

							saveDrNotes(soapPrescription,soapComplain,notes_history, notes_subjective_description, objective_vitals, notes_family,
									notes_assesment_summary, notes_plan_description,notes_objective_careplan, DATA.selectedUserCallId,
									selectedNotesBean.id);

						}
					}).setNegativeButton("Edit",null).show();
		} else {
			customToast.showToast("No internet connection. Can not save schedule. Please check internet connection and try again", 0, 1);
		}

	}

	@Override
	public void onBackPressed() {
		//startActivity(new Intent(activity,ActivityTelemedicineServices.class));
		super.onBackPressed();
	}

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
}
