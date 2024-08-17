package com.app.mdlive_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.app.mdlive_cp.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.mdlive_cp.PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText;

public class ActivitySelfMgtForm extends BaseActivity implements AssessSubmit{


	ScrollView svForm;
	Button btnSubmitForm;
	TextView tvSmgLbl1,tvSmgLbl2,tvSmgLbl3;
	EditText etAddSMGptName,etAddSMGdate,etAddSMGstafName, etAddSMGstafRole, etAddSMGstafCntact, etAddSMGgoal,etAddSMGgoalDes,etAddSMGhow,etAddSMGwhere,etAddSMGwhen,
			etAddSMGfreq,etAddSMGchallenges,etAddSMGsupport,etAddSMGfollowup;
	EditText[] editTexts;
	SeekBar sbAddSMG1,sbAddSMG2;
	SeekBar[] seekBars;
	TextView tvSB1, tvSB2;

	public boolean isEdit,isReadOnly;
	String start_time, end_time;

	@Override
	protected void onDestroy() {
		//DastFormAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selfmgt_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);

		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Self- Management Action Plan");
		}


		btnSubmitForm = findViewById(R.id.btnSubmitForm);
		svForm = findViewById(R.id.svForm);
		tvSmgLbl1 = findViewById(R.id.tvSmgLbl1);
		tvSmgLbl2 = findViewById(R.id.tvSmgLbl2);
		tvSmgLbl3 = findViewById(R.id.tvSmgLbl3);

		etAddSMGptName = findViewById(R.id.etAddSMGptName);
		etAddSMGdate = findViewById(R.id.etAddSMGdate);
		etAddSMGstafName = findViewById(R.id.etAddSMGstafName);
		etAddSMGstafRole = findViewById(R.id.etAddSMGstafRole);
		etAddSMGstafCntact = findViewById(R.id.etAddSMGstafCntact);
		etAddSMGgoal = findViewById(R.id.etAddSMGgoal);
		etAddSMGgoalDes = findViewById(R.id.etAddSMGgoalDes);
		etAddSMGhow = findViewById(R.id.etAddSMGhow);
		etAddSMGwhere = findViewById(R.id.etAddSMGwhere);
		etAddSMGwhen = findViewById(R.id.etAddSMGwhen);
		etAddSMGfreq = findViewById(R.id.etAddSMGfreq);
		etAddSMGchallenges = findViewById(R.id.etAddSMGchallenges);
		etAddSMGsupport = findViewById(R.id.etAddSMGsupport);
		etAddSMGfollowup = findViewById(R.id.etAddSMGfollowup);
		sbAddSMG1 = findViewById(R.id.sbAddSMG1);
		sbAddSMG2 = findViewById(R.id.sbAddSMG2);
		tvSB1 = findViewById(R.id.tvSB1);
		tvSB2 = findViewById(R.id.tvSB2);

		editTexts = new EditText[]{etAddSMGdate,etAddSMGstafName, etAddSMGstafRole, etAddSMGstafCntact, etAddSMGgoal,etAddSMGgoalDes,etAddSMGhow,etAddSMGwhere,etAddSMGwhen,
				etAddSMGfreq,etAddSMGchallenges,etAddSMGsupport,etAddSMGfollowup};
		seekBars = new SeekBar[]{sbAddSMG1,sbAddSMG2};

		etAddSMGptName.setText(DATA.selectedUserCallName);
		etAddSMGdate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(new Date()));
		etAddSMGdate.setOnClickListener(v -> {
			DialogFragment newFragment = new DatePickerFragment(etAddSMGdate);
			newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
		});

		tvSmgLbl1.setText(Html.fromHtml("<b>Goal : </b>What is something you WANT to work on ?"));
		tvSmgLbl2.setText(Html.fromHtml("<b>Goal Description : </b>What am I going to do ?"));
		tvSmgLbl3.setText(Html.fromHtml("<b>Challenges : </b>What are barriers that could get in the way &amp; how will I overcome them ?"));

		SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(seekBar.getId() == R.id.sbAddSMG1){
					tvSB1.setText(progress+"/"+seekBar.getMax());
				}else if(seekBar.getId() == R.id.sbAddSMG2){
					tvSB2.setText(progress+"/"+seekBar.getMax());
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		};
		sbAddSMG1.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbAddSMG2.setOnSeekBarChangeListener(onSeekBarChangeListener);


		if(isEdit && ActivitySelfMgtList.selectedSelfMgtListBean != null){

			try {
				JSONObject dataJSON = new JSONObject(ActivitySelfMgtList.selectedSelfMgtListBean.form_data);
				for (int i = 0; i < editTexts.length; i++) {
					String etTag = editTexts[i].getTag().toString();
					etTag = etTag.replace("form_data[","");
					etTag = etTag.replace("]", "");

					if(dataJSON.has(etTag)){
						editTexts[i].setText(dataJSON.getString(etTag));
					}
				}

				for (int i = 0; i < seekBars.length; i++) {
					String sbTag = seekBars[i].getTag().toString();
					sbTag = sbTag.replace("form_data[","");
					sbTag = sbTag.replace("]", "");

					if(dataJSON.has(sbTag)){
						try {
							seekBars[i].setProgress(Integer.parseInt(dataJSON.getString(sbTag)));
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}

			}catch (Exception e){
				e.printStackTrace();
			}

		}

		if(isReadOnly){
			btnSubmitForm.setText("Done");
		}
		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isReadOnly){
					finish();
				}else {
					/*if(! validateDASTForm()){  add function if needed
						if(dastFormAdapter != null){
							DastFormAdapter.validateFlag = true;
							dastFormAdapter.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}*/

					String assesTittle = getResources().getString(R.string.app_name);
					if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
						assesTittle = getSupportActionBar().getTitle().toString();
					}
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivitySelfMgtForm.this, assesTittle);


				}
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


		params.add("is_lock", is_lock);

		if(isEdit){
			params.add("id", ActivitySelfMgtList.selectedSelfMgtListBean.id);//this will edit the form
		}

		for (int i = 0; i < editTexts.length; i++) {
			params.add(editTexts[i].getTag().toString(), editTexts[i].getText().toString().trim());
		}
		for (int i = 0; i < seekBars.length; i++) {
			params.add(seekBars[i].getTag().toString(), String.valueOf(seekBars[i].getProgress()));
		}


		/*String date = etAddSMGdate.getText().toString().trim();
		String stafName = etAddSMGstafName.getText().toString().trim();
		String stafRole = etAddSMGstafRole.getText().toString().trim();
		String stafContactInfo = etAddSMGstafCntact.getText().toString().trim();
		String goal = etAddSMGgoal.getText().toString().trim();
		String goalDesc = etAddSMGgoalDes.getText().toString().trim();
		String how = etAddSMGhow.getText().toString().trim();
		String where = etAddSMGwhere.getText().toString().trim();
		String when = etAddSMGwhen.getText().toString().trim();
		String freq = etAddSMGfreq.getText().toString().trim();
		String challenges = etAddSMGchallenges.getText().toString().trim();
		String support = etAddSMGsupport.getText().toString().trim();
		String followup = etAddSMGfollowup.getText().toString().trim();
		String howReady = String.valueOf(sbAddSMG1.getProgress());
		String howConfident = String.valueOf(sbAddSMG2.getProgress());

		params.add("form_data[dateof]", date);
		params.add("form_data[staff_name]", stafName);
		params.add("form_data[staff_role]", stafRole);
		params.add("form_data[staff_contact_info]", stafContactInfo);
		params.add("form_data[goal]", goal);
		params.add("form_data[goal_desc]", goalDesc);
		params.add("form_data[goal_how]", how);
		params.add("form_data[goal_where]", where);
		params.add("form_data[goal_when]", when);
		params.add("form_data[frequency]", freq);
		params.add("form_data[ready_goal]", howReady);
		params.add("form_data[challenges]", challenges);
		params.add("form_data[support_do_need]", support);
		params.add("form_data[follow_up_summary]", followup);
		params.add("form_data[how_confident]", howConfident);*/

		ApiManager apiManager = new ApiManager(ApiManager.SMG_FORM_SAVE,"post",params,apiCallBack, activity);

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

		if(apiName.equalsIgnoreCase(ApiManager.SMG_FORM_SAVE)){
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
							ActivitySelfMgtList.shoulRefresh = true;
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
