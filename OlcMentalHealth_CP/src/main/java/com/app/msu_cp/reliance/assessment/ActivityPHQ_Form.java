package com.app.msu_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.ExpandableHeightListView;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.PatientMedicalHistoryNew;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityPHQ_Form extends BaseActivity implements AssessSubmit{

	ExpandableHeightListView lvPHQForm;
	TextView tvFormDesc, tvTotalScore;
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit,isReadOnly;

	public static int formFlag = 1;//1 = PHQ9,  2 = gad7_form

	String start_time, end_time;

	@Override
	protected void onDestroy() {
		LvPHQformAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phq_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);
		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			if(formFlag == 1){
				getSupportActionBar().setTitle("Patient Health Questionnaire (PHQ-9)");
			}else if(formFlag == 2){
				getSupportActionBar().setTitle("Generalized Anxiety Disorder 7-item (GAD-7) scale");
				tvFormDesc.setText("Generalized Anxiety Disorder 7-item (GAD-7) scale");
			}
		}

		lvPHQForm = findViewById(R.id.lvPHQForm);
		svForm = findViewById(R.id.svForm);
		tvTotalScore = findViewById(R.id.tvTotalScore);

		if(isReadOnly){
			btnSubmitForm.setText("Done");
		}
		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadOnly){
                	finish();
				}else {

					if(! validatePHQ9Form()){
						if(lvPHQformAdapter != null){
							LvPHQformAdapter.validateFlag = true;
							lvPHQformAdapter.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}

					String assesTittle = getResources().getString(R.string.app_name);
					if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
						assesTittle = getSupportActionBar().getTitle().toString();
					}
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivityPHQ_Form.this, assesTittle);
				}
            }
        });

		if(formFlag == 1){
			loadPHQ9Form();
		}else if(formFlag == 2){
			loadGAD7Form();
		}


		PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================PHQ9 Form starts======================
	public void loadPHQ9Form(){
		String savedJSON = prefs.getString("phq9_form_json", "");
		if(!savedJSON.isEmpty()){
			parsePHQ9FormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.PHQ_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	LvPHQformAdapter lvPHQformAdapter;
	public void parsePHQ9FormData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);

			prefs.edit().putString("phq9_form_json", content).apply();

			JSONArray questions = jsonObject.getJSONArray("questions");
			phQfieldBeans = new ArrayList<>();
			PHQfieldBean phQfieldBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				List<String> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(options.getString(j));
				}
				phQfieldBean = new PHQfieldBean(question, optionsList);

				if(isEdit){
					//view data part starts
					try {
						JSONObject dataJSON = new JSONObject(ActivityPhqList.selectedPHQlistBean.form_data);
						if(dataJSON.has(i + "")){
							phQfieldBean.isGroupSelected = true;
							phQfieldBean.score = dataJSON.getInt(i+"");
							phQfieldBean.seletedRadioIndex = dataJSON.getInt(i+"");
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				phQfieldBeans.add(phQfieldBean);
				phQfieldBean = null;
			}
			lvPHQformAdapter = new LvPHQformAdapter(activity, phQfieldBeans);
			lvPHQForm.setAdapter(lvPHQformAdapter);
			lvPHQForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	public void submitForm(String is_lock){
		end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		RequestParams params = new RequestParams();
		params.put("patient_id", DATA.selectedUserCallId);
		params.put("author_id", prefs.getString("id", ""));
		params.put("start_time", start_time);
		params.put("end_time", end_time);
		params.put("score", totalScore+"");
		params.put("is_lock", is_lock);

		if(isEdit){
			params.put("id", ActivityPhqList.selectedPHQlistBean.id);//this will edit the form
		}


		for (int i = 0; i < phQfieldBeans.size(); i++) {
			DATA.print("ans["+i+"]"   + " = "  + phQfieldBeans.get(i).score);
			if(phQfieldBeans.get(i).isGroupSelected){
				params.put("ans["+i+"]", phQfieldBeans.get(i).score +"");
			}
		}

		String apiName = ApiManager.SAVE_PHQ_FORM;
		if(ActivityPHQ_Form.formFlag == 1){
			apiName = ApiManager.SAVE_PHQ_FORM;
		}else if(ActivityPHQ_Form.formFlag == 2){
			apiName = ApiManager.GAD_FORM_SAVE;
		}

		ApiManager apiManager = new ApiManager(apiName,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validatePHQ9Form(){
		boolean validated = true;
		if(phQfieldBeans != null){
			for (int i = 0; i < phQfieldBeans.size(); i++) {
				if(! phQfieldBeans.get(i).isGroupSelected){
					validated = false;
				}
			}
		}
		return validated;
	}

	//========================PHQ9 form end===============================


	List<PHQfieldBean> phQfieldBeans;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.PHQ_FORM)){
			parsePHQ9FormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.GAD_FORM)){
			parseGAD7FormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_PHQ_FORM) || apiName.equalsIgnoreCase(ApiManager.GAD_FORM_SAVE)){
			//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					//customToast.showToast("",0,0);
					String severity = jsonObject.optString("severity", "-");
					String msgAssess =  "Total Score : "+totalScore+"\nSeverity : "+severity+"\n\n"+"Information has been saved successfully.";
					AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(msgAssess)
							.setPositiveButton("Ok, Done",null)
							.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							ActivityPhqList.shoulRefresh = true;
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



	//JAD 7 Form starts

	public void loadGAD7Form(){
		String savedJSON = prefs.getString("gad7_form_json", "");
		if(!savedJSON.isEmpty()){
			parseGAD7FormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.GAD_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void parseGAD7FormData(String content){
		try {
			/*JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);*/

			//JSONArray questions = jsonObject.getJSONArray("questions");

			JSONArray questions = new JSONArray(content);

			prefs.edit().putString("gad7_form_json", content).apply();

			phQfieldBeans = new ArrayList<>();
			PHQfieldBean phQfieldBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				List<String> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(options.getString(j));
				}
				phQfieldBean = new PHQfieldBean(question, optionsList);

				if(isEdit){
					//view data part starts
					try {
						JSONObject dataJSON = new JSONObject(ActivityPhqList.selectedPHQlistBean.form_data);
						if(dataJSON.has(i + "")){
							phQfieldBean.isGroupSelected = true;
							phQfieldBean.score = dataJSON.getInt(i+"");
							phQfieldBean.seletedRadioIndex = dataJSON.getInt(i+"");
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				phQfieldBeans.add(phQfieldBean);
				phQfieldBean = null;
			}
			lvPHQformAdapter = new LvPHQformAdapter(activity, phQfieldBeans);
			lvPHQForm.setAdapter(lvPHQformAdapter);
			lvPHQForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}


	//score sup up
	int totalScore = 0;
	public void sumUpScore(){
		totalScore = 0;
		for (int i = 0; i < phQfieldBeans.size(); i++) {
			totalScore = totalScore + phQfieldBeans.get(i).score;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
