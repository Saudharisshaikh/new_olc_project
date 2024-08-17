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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityAdlForm extends BaseActivity implements AssessSubmit {

	ExpandableHeightListView lvAdlForm;
	TextView tvFormDesc, tvTotalScore;
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit, isReadOnly;

	public static int formFlagA = 1;//1 = ADL form,  2 = IADL form

	String start_time, end_time;

	@Override
	protected void onDestroy() {
		LvADLformAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adl_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);
		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			if(formFlagA == 1){
				getSupportActionBar().setTitle("Katz Index of Independence in Activities of Daily Living");
			}else if(formFlagA == 2){
				getSupportActionBar().setTitle("The Lawton Instrumental Activities of Daily Living Scale");
				//tvFormDesc.setText("Generalized Anxiety Disorder 7-item (GAD-7) scale");
			}
		}

		lvAdlForm = findViewById(R.id.lvAdlForm);
		svForm = findViewById(R.id.svForm);
		tvTotalScore = findViewById(R.id.tvTotalScore);

		if(isEdit){
			if(isReadOnly){
				btnSubmitForm.setText("Done");
			}
			try {
				totalScore  = Integer.parseInt(ActivityAdlList.selectedAdlListBean.score);
			}catch (Exception e){
				e.printStackTrace();
			}
			tvTotalScore.setText("Total Score : "+ActivityAdlList.selectedAdlListBean.score);
		}
		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadOnly){
                	finish();
				}else {

					if(! validateADLForm()){
						if(lvADLformAdapter != null){
							LvADLformAdapter.validateFlag = true;
							lvADLformAdapter.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}

                	String assesTittle = getResources().getString(R.string.app_name);
                	if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
                		assesTittle = getSupportActionBar().getTitle().toString();
					}
                	new GloabalMethods(activity).showConfSaveAssesDialog(ActivityAdlForm.this, assesTittle);
				}
            }
        });

		if(formFlagA == 1){
			loadAdlForm();
		} else if(formFlagA == 2){
			loadIadlForm();
		}


		PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================PHQ9 Form starts======================
	public void loadAdlForm(){
		String savedJSON = prefs.getString("adl_form_json", "");
		if(!savedJSON.isEmpty()){
			parseAdlFormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.ADL_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	LvADLformAdapter lvADLformAdapter;
	public void parseAdlFormData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			if(! form_text.isEmpty()){
				tvFormDesc.setText(form_text);
			}else {
				tvFormDesc.setText(form_title);
			}

			prefs.edit().putString("adl_form_json", content).apply();

			JSONArray questions = jsonObject.getJSONArray("questions");
			adlFormBeans = new ArrayList<>();
			AdlFormBean adlFormBean;
			for (int i = 0; i < questions.length(); i++) {

				String question = questions.getJSONObject(i).getString("question");

				List<AdlFormBean.ADLoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");

				for (int j = 0; j < options.length(); j++) {
					AdlFormBean.ADLoptionBean adLoptionBean = new AdlFormBean().new ADLoptionBean(options.getString(j));//new AdlFormBean.ADLoptionBean(options.getString(j))
					optionsList.add(adLoptionBean);
				}

				List<Integer> scores = new ArrayList<>();
				if(questions.getJSONObject(i).has("scores")){
					JSONArray scoresJA = questions.getJSONObject(i).getJSONArray("scores");
					for (int j = 0; j < scoresJA.length(); j++) {
						scores.add(scoresJA.getInt(i));
					}
				}

				adlFormBean = new AdlFormBean(question, optionsList, scores);

				if(isEdit){
					//view data part starts
					try {
						JSONObject dataJSON = new JSONObject(ActivityAdlList.selectedAdlListBean.form_data);
						if(dataJSON.has(i + "")){
							adlFormBean.isAnswered = true;
							adlFormBean.score = dataJSON.getInt(i+"");

							for (int j = 0; j < adlFormBean.options.size(); j++) {
								if(j == adlFormBean.score){
									adlFormBean.options.get(j).isSelected = true;
								}
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				adlFormBeans.add(adlFormBean);
				adlFormBean = null;
			}
			lvADLformAdapter = new LvADLformAdapter(activity, adlFormBeans);
			lvAdlForm.setAdapter(lvADLformAdapter);
			lvAdlForm.setExpanded(true);
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
			params.put("id", ActivityAdlList.selectedAdlListBean.id);//this will edit the form
		}

		for (int i = 0; i < adlFormBeans.size(); i++) {
			DATA.print("ans["+i+"]"   + " = "  + adlFormBeans.get(i).score);
			if(adlFormBeans.get(i).isAnswered){
				if(formFlagA == 1){
					params.put("ans["+i+"]", adlFormBeans.get(i).score +"");
				}else if(formFlagA == 2){
					params.put("ans["+i+"]", adlFormBeans.get(i).selectedAns);
				}
			}
		}

		String apiName = ApiManager.SAVE_ADL_FORM;
		if(ActivityAdlForm.formFlagA == 1){
			apiName = ApiManager.SAVE_ADL_FORM;
		}else if(ActivityAdlForm.formFlagA == 2){
			apiName = ApiManager.SAVE_IADL_FORM;
		}

		ApiManager apiManager = new ApiManager(apiName,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validateADLForm(){
		boolean validated = true;
		if(adlFormBeans != null){
			for (int i = 0; i < adlFormBeans.size(); i++) {
				if(! adlFormBeans.get(i).isAnswered){
					validated = false;
				}
			}
		}
		return validated;
	}

	//========================ADL form end===============================


	ArrayList<AdlFormBean> adlFormBeans;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.ADL_FORM)){
			parseAdlFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.IADL_FORM)){
			parseIadlFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_ADL_FORM) || apiName.equalsIgnoreCase(ApiManager.SAVE_IADL_FORM)){
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
							ActivityAdlList.shoulRefresh = true;
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



	//IADL Form starts

	public void loadIadlForm(){
		String savedJSON = prefs.getString("iadl_form_json", "");
		if(!savedJSON.isEmpty()){
			parseIadlFormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.IADL_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void parseIadlFormData(String content){
		try {
			/*JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);*/

			//JSONArray questions = jsonObject.getJSONArray("questions");

			tvFormDesc.setText("The Lawton Instrumental Activities of Daily Living Scale");

			JSONArray questions = new JSONArray(content);

			prefs.edit().putString("iadl_form_json", content).apply();

			adlFormBeans = new ArrayList<>();
			AdlFormBean adlFormBean;
			for (int i = 0; i < questions.length(); i++) {

				String question = questions.getJSONObject(i).getString("question");

				List<AdlFormBean.ADLoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");

				for (int j = 0; j < options.length(); j++) {
					AdlFormBean.ADLoptionBean adLoptionBean = new AdlFormBean().new ADLoptionBean(options.getString(j));//new AdlFormBean.ADLoptionBean(options.getString(j))
					optionsList.add(adLoptionBean);
				}

				List<Integer> scores = new ArrayList<>();
				if(questions.getJSONObject(i).has("scores")){
					JSONArray scoresJA = questions.getJSONObject(i).getJSONArray("scores");
					for (int j = 0; j < scoresJA.length(); j++) {
						scores.add(scoresJA.getInt(j));
					}
				}

				adlFormBean = new AdlFormBean(question, optionsList, scores);

				if(isEdit){
					//view data part starts
					try {
						JSONArray dataJArr = new JSONArray(ActivityAdlList.selectedAdlListBean.form_data);

						Type listType = new TypeToken<ArrayList<String>>() {}.getType();
						ArrayList<String> form_data_list = new Gson().fromJson(dataJArr.toString(), listType);
						//options loop starts
						for (int j = 0; j < adlFormBean.options.size(); j++) {
							if(form_data_list.contains(adlFormBean.options.get(j).optionTxt)){//means this is given answer
								adlFormBean.options.get(j).isSelected = true;//for show selection
								adlFormBean.isAnswered = true;//for validation
								adlFormBean.selectedAns = adlFormBean.options.get(j).optionTxt;//for submission iadl form save api
								try {
									adlFormBean.score = adlFormBean.scores.get(j);//for total score
									//adlFormBean.options and adlFormBean.scores size are same phir b double check
								}catch (Exception e){
									e.printStackTrace();
								}
							}
						}
						//options loop ends


					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				adlFormBeans.add(adlFormBean);
				adlFormBean = null;
			}
			lvADLformAdapter = new LvADLformAdapter(activity, adlFormBeans);
			lvAdlForm.setAdapter(lvADLformAdapter);
			lvAdlForm.setExpanded(true);

		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}


	//score sup up
	int totalScore = 0;
	public void sumUpScore(){
		totalScore = 0;
		for (int i = 0; i < adlFormBeans.size(); i++) {
			totalScore = totalScore + adlFormBeans.get(i).score;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
