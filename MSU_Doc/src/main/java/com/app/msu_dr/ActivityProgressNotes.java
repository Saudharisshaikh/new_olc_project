package com.app.msu_dr;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.msu_dr.R;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.ProgressNoteBean;
import com.app.msu_dr.util.ActionEditText;
import com.app.msu_dr.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityProgressNotes extends BaseActivity{


	EditText etPtName,etProviderName,etDate,etTime,etSessionTime,etSymptom,etCondition,etExpNotes,etInterventions,etPtRes,etCarePlan;
	EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBMI;
	TextView tvVitalsDate;
	Button btnSubmit;

	boolean isForEdit = false;
	public static ProgressNoteBean progressNoteBean;

	boolean isFromListScreen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_notes);

		isForEdit = getIntent().getBooleanExtra("isForEdit",false);
		isFromListScreen= getIntent().getBooleanExtra("isFromListScreen",false);


		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setTitle("Medical History");
		Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
		/*if(DATA.isFromDocToDoc){
			btnToolbar.setVisibility(View.GONE);
		}*/
		btnToolbar.setText("Review Previous Note");
		btnToolbar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(ActivityProgressNotes.class,false);
				getPreviousNote();
			}
		});

		etPtName = (EditText) findViewById(R.id.etPtName);
		etProviderName = (EditText) findViewById(R.id.etProviderName);
		etDate = (EditText) findViewById(R.id.etDate);
		etTime = (EditText) findViewById(R.id.etTime);
		etSessionTime = (EditText) findViewById(R.id.etSessionTime);
		etSymptom = (EditText) findViewById(R.id.etSymptom);
		etCondition = (EditText) findViewById(R.id.etCondition);
		etExpNotes = (EditText) findViewById(R.id.etExpNotes);
		etInterventions = (EditText) findViewById(R.id.etInterventions);
		etPtRes = (EditText) findViewById(R.id.etPtRes);
		etCarePlan = (EditText) findViewById(R.id.etCarePlan);

		etOTBP = (ActionEditText) findViewById(R.id.etOTBP);
		etOTHR = (ActionEditText) findViewById(R.id.etOTHR);
		etOTRespirations = (ActionEditText) findViewById(R.id.etOTRespirations);
		etOTO2Saturations = (ActionEditText) findViewById(R.id.etOTO2Saturations);
		etOTBloodSugar = (ActionEditText) findViewById(R.id.etOTBloodSugar);
		etOTTemperature = (ActionEditText) findViewById(R.id.etOTTemperature);
		etOTHeight = (ActionEditText) findViewById(R.id.etOTHeight);
		etOTWeight = (ActionEditText) findViewById(R.id.etOTWeight);
		etOTBMI = (ActionEditText) findViewById(R.id.etOTBMI);
		tvVitalsDate = (TextView) findViewById(R.id.tvVitalsDate);

		btnSubmit = (Button) findViewById(R.id.btnSubmit);


		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(isForEdit){
					finish();
				}else {
					String callId = "";
					if(isFromListScreen){
						callId  = "0";
					}else {
						if(DATA.incomingCall){
							callId = DATA.incommingCallId;
						}else{
							callId = prefs.getString("callingID","");
						}
					}

					RequestParams params = new RequestParams();
					params.put("patient_id",DATA.selectedUserCallId);
					params.put("author_id", prefs.getString("id",""));
					params.put("call_id",callId);
					params.put("explanatory_notes",etExpNotes.getText().toString());
					params.put("interventions",etInterventions.getText().toString());
					params.put("feedback",etPtRes.getText().toString());
					params.put("care_plan",etCarePlan.getText().toString());
					params.put("virtual_visit_id",virtualVisitID);
					params.put("ddate",etDate.getText().toString());
					params.put("ttime",etTime.getText().toString());
					String sLength = etSessionTime.getText().toString().replace(" Min","");
					if(sLength.isEmpty()){
						sLength = "0";
					}
					params.put("session_length", sLength);

					ApiManager apiManager = new ApiManager(ApiManager.SAVE_PROGRESS_NOTE,"post",params,apiCallBack, activity);
					//ApiManager.shouldShowPD = false;
					apiManager.loadURL();
				}
			}
		});



		if(isForEdit){
			btnSubmit.setText("Done");
			RequestParams params = new RequestParams();
			params.put("id",progressNoteBean.id);
			ApiManager apiManager = new ApiManager(ApiManager.SINGLE_PROGRESS_NOTE,"post",params,apiCallBack, activity);
			//ApiManager.shouldShowPD = false;
			apiManager.loadURL();
		}else {
			btnSubmit.setText("Submit");
			getPatientDetails();
		}
	}//end oncreate



	public void getPatientDetails(){
		if(! DATA.selectedUserCallId.isEmpty()){
			ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+DATA.selectedUserCallId,"get",null,apiCallBack, activity);
			//ApiManager.shouldShowPD = false;
			apiManager.loadURL();
		}
	}

	public void getPreviousNote(){
		RequestParams params = new RequestParams();
		params.put("patient_id",DATA.selectedUserCallId);
		ApiManager apiManager = new ApiManager(ApiManager.SINGLE_PREVIOUS_PROGRESS_NOTE,"post",params,apiCallBack, activity);
		//ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}


	static String virtualVisitID = "0";
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.contains(ApiManager.PATIENT_DETAIL)){
			try {
				JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

				etPtName.setText(jsonObject.getJSONObject("patient_data").getString("first_name")+" "+
						jsonObject.getJSONObject("patient_data").getString("last_name"));
				etProviderName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));

				if(!jsonObject.getString("virtual_visit").isEmpty()){
					JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");

					if(virtual_visit.has("ddate")){
						etDate.setText(virtual_visit.getString("ddate"));
					}
					if(virtual_visit.has("ttime")){
						etTime.setText(virtual_visit.getString("ttime"));
					}
					if(virtual_visit.has("session_length")){
						etSessionTime.setText(virtual_visit.getString("session_length")+" Min");
					}
					if(virtual_visit.has("symptom_name")){
						etSymptom.setText(virtual_visit.getString("symptom_name"));
					}
					if(virtual_visit.has("condition_name")){
						etCondition.setText(virtual_visit.getString("condition_name"));
					}

					if(virtual_visit.has("dateof")){
						tvVitalsDate.setText("Date: "+virtual_visit.getString("dateof"));
					}
					if(virtual_visit.has("id")){
						virtualVisitID = virtual_visit.getString("id");
					}
					if(virtual_visit.has("ot_data")&& !virtual_visit.getString("ot_data").isEmpty()){
						JSONObject virtual_ot_data = new JSONObject(virtual_visit.getString("ot_data"));
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

						if(virtual_ot_data.has("ot_height")){
							String ot_height = virtual_ot_data.getString("ot_height");
							etOTHeight.setText(ot_height);
						}
						if(virtual_ot_data.has("ot_temperature")){
							String ot_temperature = virtual_ot_data.getString("ot_temperature");
							etOTTemperature.setText(ot_temperature);
						}
						if(virtual_ot_data.has("ot_weight")){
							String ot_weight = virtual_ot_data.getString("ot_weight");
							etOTWeight.setText(ot_weight);
						}

						if(virtual_ot_data.has("ot_bmi")){
							String bmi = virtual_ot_data.getString("ot_bmi");
							etOTBMI.setText(bmi);
						}
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				//customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.SINGLE_PROGRESS_NOTE)){
			try {
				JSONObject data = new JSONObject(content).getJSONObject("data");

				etPtName.setText(data.getString("patient_name"));
				etProviderName.setText(data.getString("first_name")+" "+data.getString("last_name"));
				etDate.setText(data.getString("ddate"));
				etTime.setText(data.getString("ttime"));
				etSessionTime.setText(data.getString("session_length")+" Min");
				etSymptom.setText(data.getString("symptom_name"));
				etCondition.setText(data.getString("condition_name"));
				etExpNotes.setText(data.getString("explanatory_notes"));
				etInterventions.setText(data.getString("interventions"));
				etPtRes.setText(data.getString("feedback"));
				etCarePlan.setText(data.getString("care_plan"));

				String ot_dataStr = data.getString("ot_data");
				if(! ot_dataStr.isEmpty()){

					JSONObject virtual_ot_data = new JSONObject(ot_dataStr);
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

					if(virtual_ot_data.has("ot_height")){
						String ot_height = virtual_ot_data.getString("ot_height");
						etOTHeight.setText(ot_height);
					}
					if(virtual_ot_data.has("ot_temperature")){
						String ot_temperature = virtual_ot_data.getString("ot_temperature");
						etOTTemperature.setText(ot_temperature);
					}
					if(virtual_ot_data.has("ot_weight")){
						String ot_weight = virtual_ot_data.getString("ot_weight");
						etOTWeight.setText(ot_weight);
					}

					if(virtual_ot_data.has("ot_bmi")){
						String bmi = virtual_ot_data.getString("ot_bmi");
						etOTBMI.setText(bmi);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_PROGRESS_NOTE)){//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					customToast.showToast("Progress notes has been saved successfully",0,1);

					ActivityProgressNotesView.shouldRefreshPrNotes = true;
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.SINGLE_PREVIOUS_PROGRESS_NOTE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("data").isEmpty()){
					customToast.showToast("No progress note added",0,1);

					return;
				}
				JSONObject data = jsonObject.getJSONObject("data");

				String id = data.getString("id");
				String dateof = data.getString("dateof");
				String patient_id = data.getString("patient_id");
				String author_id = data.getString("author_id");
				String call_id = data.getString("call_id");
				String virtual_visit_id = data.getString("virtual_visit_id");
				String explanatory_notes = data.getString("explanatory_notes");
				String interventions = data.getString("interventions");
				String feedback = data.getString("feedback");
				String ddate = data.getString("ddate");
				String ttime = data.getString("ttime");
				String session_length = data.getString("session_length");
				String first_name = data.getString("first_name");
				String last_name = data.getString("last_name");
				String patient_name = data.getString("patient_name");
				String ot_data = data.getString("ot_data");
				String symptom_name = data.getString("symptom_name");
				String condition_name = data.getString("condition_name");
				String care_plan = data.getString("care_plan");

				ProgressNoteBean progressNoteBean = new ProgressNoteBean(id,dateof,patient_id,author_id,call_id,virtual_visit_id,explanatory_notes,interventions
						,feedback,ddate,ttime,session_length,first_name,last_name,patient_name,ot_data,symptom_name,condition_name,care_plan);

				showPreviousNoteDialog(progressNoteBean);
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}




	Dialog dialogPreviousNote;
	public void showPreviousNoteDialog(final ProgressNoteBean progressNoteBean) {
		dialogPreviousNote = new Dialog(activity);
		dialogPreviousNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogPreviousNote.setContentView(R.layout.dialog_prev_prg_note);

		EditText etPtName = (EditText) dialogPreviousNote.findViewById(R.id.etPtName),
				etProviderName = (EditText) dialogPreviousNote.findViewById(R.id.etProviderName),
				etDate = (EditText) dialogPreviousNote.findViewById(R.id.etDate),
				etTime = (EditText) dialogPreviousNote.findViewById(R.id.etTime),
				etSessionTime = (EditText) dialogPreviousNote.findViewById(R.id.etSessionTime),
				etSymptom = (EditText) dialogPreviousNote.findViewById(R.id.etSymptom),
				etCondition = (EditText) dialogPreviousNote.findViewById(R.id.etCondition),
				etExpNotes = (EditText) dialogPreviousNote.findViewById(R.id.etExpNotes),
				etInterventions = (EditText) dialogPreviousNote.findViewById(R.id.etInterventions),
				etPtRes = (EditText) dialogPreviousNote.findViewById(R.id.etPtRes),
				etCarePlan = (EditText) dialogPreviousNote.findViewById(R.id.etCarePlan);

		EditText etOTBP = (EditText) dialogPreviousNote.findViewById(R.id.etOTBP),
				etOTHR = (EditText) dialogPreviousNote.findViewById(R.id.etOTHR),
				etOTRespirations = (EditText) dialogPreviousNote.findViewById(R.id.etOTRespirations),
				etOTO2Saturations = (EditText) dialogPreviousNote.findViewById(R.id.etOTO2Saturations),
				etOTBloodSugar = (EditText) dialogPreviousNote.findViewById(R.id.etOTBloodSugar),
				etOTTemperature = (EditText) dialogPreviousNote.findViewById(R.id.etOTTemperature),
				etOTHeight = (EditText) dialogPreviousNote.findViewById(R.id.etOTHeight),
				etOTWeight = (EditText) dialogPreviousNote.findViewById(R.id.etOTWeight),
				etOTBMI = (EditText) dialogPreviousNote.findViewById(R.id.etOTBMI);
		TextView tvVitalsDate = (TextView) dialogPreviousNote.findViewById(R.id.tvVitalsDate);

		Button btnYes = (Button) dialogPreviousNote.findViewById(R.id.btnYes);
		Button btnNo = (Button) dialogPreviousNote.findViewById(R.id.btnNo);

		dialogPreviousNote.findViewById(R.id.ivClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPreviousNote.dismiss();
			}
		});

		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPreviousNote.dismiss();
			}
		});

		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPreviousNote.dismiss();

				ActivityProgressNotes.this.etExpNotes.setText(progressNoteBean.explanatory_notes);
				ActivityProgressNotes.this.etInterventions.setText(progressNoteBean.interventions);
				ActivityProgressNotes.this.etPtRes.setText(progressNoteBean.feedback);
				ActivityProgressNotes.this.etCarePlan.setText(progressNoteBean.care_plan);
			}
		});


		etPtName.setText(progressNoteBean.patient_name);
		etProviderName.setText(progressNoteBean.first_name+" "+progressNoteBean.last_name);
		etDate.setText(progressNoteBean.ddate);
		etTime.setText(progressNoteBean.ttime);
		etSessionTime.setText(progressNoteBean.session_length+" Min");
		etSymptom.setText(progressNoteBean.symptom_name);
		etCondition.setText(progressNoteBean.condition_name);
		etExpNotes.setText(progressNoteBean.explanatory_notes);
		etInterventions.setText(progressNoteBean.interventions);
		etPtRes.setText(progressNoteBean.feedback);
		etCarePlan.setText(progressNoteBean.care_plan);

		if(!progressNoteBean.ot_data.isEmpty()){
			try {

				JSONObject virtual_ot_data = new JSONObject(progressNoteBean.ot_data);
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

				if(virtual_ot_data.has("ot_height")){
					String ot_height = virtual_ot_data.getString("ot_height");
					etOTHeight.setText(ot_height);
				}
				if(virtual_ot_data.has("ot_temperature")){
					String ot_temperature = virtual_ot_data.getString("ot_temperature");
					etOTTemperature.setText(ot_temperature);
				}
				if(virtual_ot_data.has("ot_weight")){
					String ot_weight = virtual_ot_data.getString("ot_weight");
					etOTWeight.setText(ot_weight);
				}

				if(virtual_ot_data.has("ot_bmi")){
					String bmi = virtual_ot_data.getString("ot_bmi");
					etOTBMI.setText(bmi);
				}

			}catch (Exception e){
				e.printStackTrace();
			}
		}

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogPreviousNote.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogPreviousNote.show();
		dialogPreviousNote.getWindow().setAttributes(lp);
	}
}
