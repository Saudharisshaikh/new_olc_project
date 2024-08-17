package com.app.msu_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.PatientMedicalHistoryNew;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityOtProfileForm extends BaseActivity implements AssessSubmit {


	ScrollView svForm;
	Button btnSubmitForm;

	EditText etOtProField1,etOtProField2,etOtProField3,etOtProField4,etOtProField5,etOtProField6,etOtProField7,etOtProField8,etOtProField9,etOtProField10,
			etOtProField11,etOtProField12,etOtProField13,etOtProField14,etOtProField15,etOtProField16,etOtProField17,etOtProField18;
	EditText[] editTexts;

	public boolean isEdit;
	String start_time, end_time;

	@Override
	protected void onDestroy() {
		//DastFormAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ot_profile_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("AOTA OCCUPATIONAL PROFILE");
		}


		btnSubmitForm = findViewById(R.id.btnSubmitForm);
		svForm = findViewById(R.id.svForm);

		etOtProField1 = findViewById(R.id.etOtProField1);
		etOtProField2 = findViewById(R.id.etOtProField2);
		etOtProField3 = findViewById(R.id.etOtProField3);
		etOtProField4 = findViewById(R.id.etOtProField4);
		etOtProField5 = findViewById(R.id.etOtProField5);
		etOtProField6 = findViewById(R.id.etOtProField6);
		etOtProField7 = findViewById(R.id.etOtProField7);
		etOtProField8 = findViewById(R.id.etOtProField8);
		etOtProField9 = findViewById(R.id.etOtProField9);
		etOtProField10 = findViewById(R.id.etOtProField10);
		etOtProField11 = findViewById(R.id.etOtProField11);
		etOtProField12 = findViewById(R.id.etOtProField12);
		etOtProField13 = findViewById(R.id.etOtProField13);
		etOtProField14 = findViewById(R.id.etOtProField14);
		etOtProField15 = findViewById(R.id.etOtProField15);
		etOtProField16 = findViewById(R.id.etOtProField16);
		etOtProField17 = findViewById(R.id.etOtProField17);
		etOtProField18 = findViewById(R.id.etOtProField18);
		editTexts = new EditText[]{etOtProField1,etOtProField2,etOtProField3,etOtProField4,etOtProField5,etOtProField6,etOtProField7,etOtProField8,etOtProField9,etOtProField10,
				etOtProField11,etOtProField12,etOtProField13,etOtProField14,etOtProField15,etOtProField16,etOtProField17,etOtProField18};



		//edit part starts
		if(isEdit  && ActivityOtProfileList.selectedOtProListBean != null){
			try {
				JSONObject dataJSON = new JSONObject(ActivityOtProfileList.selectedOtProListBean.form_data);
				for (int i = 0; i < editTexts.length; i++) {
					String etTag = editTexts[i].getTag().toString();
					etTag = etTag.replace("form_data[","");
					etTag = etTag.replace("]", "");

					if(dataJSON.has(etTag)){
						editTexts[i].setText(dataJSON.getString(etTag));
					}
				}

			}catch (Exception e){
				e.printStackTrace();
			}
		}
		//edit part ends


		/*etAddSMGptName.setText(DATA.selectedUserCallName);
		etAddSMGdate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(new Date()));
		etAddSMGdate.setOnClickListener(v -> {
			DialogFragment newFragment = new DatePickerFragment(etAddSMGdate);
			newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
		});

		tvSmgLbl1.setText(Html.fromHtml("<b>Goal : </b>What is something you WANT to work on ?"));
		tvSmgLbl2.setText(Html.fromHtml("<b>Goal Description : </b>What am I going to do ?"));
		tvSmgLbl3.setText(Html.fromHtml("<b>Challenges : </b>What are barriers that could get in the way &amp; how will I overcome them ?"));*/


		/*if(isEdit){
			btnSubmitForm.setText("Done");
		}*/
		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				String assesTittle = getResources().getString(R.string.app_name);
				if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
					assesTittle = getSupportActionBar().getTitle().toString();
				}
				new GloabalMethods(activity).showConfSaveAssesDialog(ActivityOtProfileForm.this, assesTittle);

				/*if(isEdit){
					finish();
				}else {
					submitForm();
				}*/
			}
		});


		PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}




	public void submitForm(String is_lock){
		end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		RequestParams params = new RequestParams();
		params.add("patient_id", DATA.selectedUserCallId);
		params.add("author_id", prefs.getString("id", ""));
		params.add("start_time", start_time);
		params.add("end_time", end_time);
		params.put("is_lock", is_lock);

		if(isEdit && ActivityOtProfileList.selectedOtProListBean != null){
			params.put("id", ActivityOtProfileList.selectedOtProListBean.id);//this will edit the form
		}

		for (int i = 0; i < editTexts.length; i++) {
			params.add(editTexts[i].getTag().toString(), editTexts[i].getText().toString().trim());
		}


		ApiManager apiManager = new ApiManager(ApiManager.OT_PROFILE_FORM_SAVE,"post",params,apiCallBack, activity);

		/*if(! validateDASTForm()){
			if(dastFormAdapter != null){
				DastFormAdapter.validateFlag = true;
				dastFormAdapter.notifyDataSetChanged();
			}
			customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

			return;
		}*/

		apiManager.loadURL();
	}

	/*public boolean validateDASTForm(){
		boolean validated = true;
		if(dasTfieldBeans != null){
			for (int i = 0; i < dasTfieldBeans.size(); i++) {
				if(! dasTfieldBeans.get(i).isGroupSelected){
					validated = false;
				}
			}
		}
		return validated;
	}*/

	//========================DAST form end===============================

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.OT_PROFILE_FORM_SAVE)){
			//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					//customToast.showToast("",0,0);
					AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage("Information has been saved successfully.")
							.setPositiveButton("Ok, Done",null)
							.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							ActivityOtProfileList.shoulRefresh = true;
							finish();
						}
					});
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
