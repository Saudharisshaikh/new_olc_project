package com.app.mhcsn_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.app.mhcsn_cp.PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText;

public class ActivityOtEvaForm extends BaseActivity implements AssessSubmit {


	ScrollView svForm;
	Button btnSubmitForm;

	EditText etOtEvaField1,etOtEvaField2,etOtEvaField3,etOtEvaField4,etOtEvaField5,etOtEvaField6,etOtEvaField7,etOtEvaField8,etOtEvaField9,etOtEvaField10,
	etOtEvaField11,etOtEvaField12,etOtEvaField13;
	CheckBox cbOtEvaField1,cbOtEvaField2,cbOtEvaField3,cbOtEvaField4,cbOtEvaField5;
	EditText[] editTexts;
	CheckBox[] checkBoxes;
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
		setContentView(R.layout.activity_ot_eva_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);
		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("INITIAL OCCUPATIONAL THERAPY EVALUATION REPORT");
		}


		btnSubmitForm = findViewById(R.id.btnSubmitForm);
		svForm = findViewById(R.id.svForm);

		etOtEvaField1 = findViewById(R.id.etOtEvaField1);
		etOtEvaField2 = findViewById(R.id.etOtEvaField2);
		etOtEvaField3 = findViewById(R.id.etOtEvaField3);
		etOtEvaField4 = findViewById(R.id.etOtEvaField4);
		etOtEvaField5 = findViewById(R.id.etOtEvaField5);
		etOtEvaField6 = findViewById(R.id.etOtEvaField6);
		etOtEvaField7 = findViewById(R.id.etOtEvaField7);
		etOtEvaField8 = findViewById(R.id.etOtEvaField8);
		etOtEvaField9 = findViewById(R.id.etOtEvaField9);
		etOtEvaField10 = findViewById(R.id.etOtEvaField10);
		etOtEvaField11 = findViewById(R.id.etOtEvaField11);
		etOtEvaField12 = findViewById(R.id.etOtEvaField12);
		etOtEvaField13 = findViewById(R.id.etOtEvaField13);
		editTexts = new EditText[]{etOtEvaField1,etOtEvaField2,etOtEvaField3,etOtEvaField4,etOtEvaField5,etOtEvaField6,etOtEvaField7,etOtEvaField8,etOtEvaField9,etOtEvaField10,
				etOtEvaField11,etOtEvaField12,etOtEvaField13};
		cbOtEvaField1 = findViewById(R.id.cbOtEvaField1);
		cbOtEvaField2 = findViewById(R.id.cbOtEvaField2);
		cbOtEvaField3 = findViewById(R.id.cbOtEvaField3);
		cbOtEvaField4 = findViewById(R.id.cbOtEvaField4);
		cbOtEvaField5 = findViewById(R.id.cbOtEvaField5);
		checkBoxes = new CheckBox[]{cbOtEvaField1,cbOtEvaField2,cbOtEvaField3,cbOtEvaField4,cbOtEvaField5};



		if(isEdit && ActivityOtEvaList.selectedOtEvaListBean != null){

			try {
				JSONObject dataJSON = new JSONObject(ActivityOtEvaList.selectedOtEvaListBean.form_data);
				for (int i = 0; i < editTexts.length; i++) {
					String etTag = editTexts[i].getTag().toString();
					etTag = etTag.replace("form_data[","");
					etTag = etTag.replace("]", "");

					if(dataJSON.has(etTag)){
						editTexts[i].setText(dataJSON.getString(etTag));
					}
				}

				JSONArray cbValuesJA = dataJSON.getJSONArray("current_visions_goals");//static key here
				Type listType = new TypeToken<ArrayList<String>>() {}.getType();
				ArrayList<String> cbValuesList = gson.fromJson(cbValuesJA.toString(), listType);

				for (int i = 0; i < checkBoxes.length; i++) {
					checkBoxes[i].setChecked(cbValuesList != null && cbValuesList.contains(checkBoxes[i].getText().toString()));
				}

			}catch (Exception e){
				e.printStackTrace();
			}

		}

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
				new GloabalMethods(activity).showConfSaveAssesDialog(ActivityOtEvaForm.this, assesTittle);
			}
		});


		preventScrollViewFromScrollingToEdiText(svForm);

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

		if(isEdit && ActivityOtEvaList.selectedOtEvaListBean != null){
			params.put("id", ActivityOtEvaList.selectedOtEvaListBean.id);//this will edit the form
		}

		for (int i = 0; i < editTexts.length; i++) {
			params.add(editTexts[i].getTag().toString(), editTexts[i].getText().toString().trim());
		}
		for (int i = 0; i < checkBoxes.length; i++) {
			if(checkBoxes[i].isChecked()){
				params.add(checkBoxes[i].getTag().toString(), checkBoxes[i].getText().toString());
			}
		}

		ApiManager apiManager = new ApiManager(ApiManager.OT_EVA_FORM_SAVE,"post",params,apiCallBack, activity);

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

		if(apiName.equalsIgnoreCase(ApiManager.OT_EVA_FORM_SAVE)){
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
							ActivityOtEvaList.shoulRefresh = true;
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
