package com.app.mdlive_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.ExpandableHeightListView;
import com.app.mdlive_cp.util.GloabalMethods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.app.mdlive_cp.PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText;

public class ActivitySDHS_Form extends BaseActivity implements AssessSubmit{

	ExpandableHeightListView lvSDHSForm;
	TextView tvFormDesc;//, tvTotalScore
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit;

	String start_time, end_time;

	public static final String QUES_TYPE_CHAECKBOX = "Think about where you live.  Do you have any problems with any of the following?";

	@Override
	protected void onDestroy() {
		LvSDHSquestionAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sdhs_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Social Determinents of Health Short Form");
		}
		tvFormDesc.setText("Social Determinents of Health Short Form");

		lvSDHSForm = findViewById(R.id.lvSDHSForm);
		svForm = findViewById(R.id.svForm);
		//tvTotalScore = findViewById(R.id.tvTotalScore);

		/*if(isEdit){
			btnSubmitForm.setText("Done");
		}*/
		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				if(! validateDASTForm()){
					if(lvSDHSfieldAdapter != null){
						LvSDHSquestionAdapter.validateFlag = true;
						lvSDHSfieldAdapter.notifyDataSetChanged();
					}
					customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

					return;
				}


				String assesTittle = getResources().getString(R.string.app_name);
				if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
					assesTittle = getSupportActionBar().getTitle().toString();
				}
				new GloabalMethods(activity).showConfSaveAssesDialog(ActivitySDHS_Form.this, assesTittle);
			}
		});

		loadSDHSForm();


		preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================SDHS Form starts======================
	public void loadSDHSForm(){
		String savedJSON = prefs.getString("sdhs_form_json", "");
		if(!savedJSON.isEmpty()){
			parseSDHSFormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.SDHS_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	ArrayList<SDHSfieldBean> sdhSfieldBeans;
	LvSDHSfieldAdapter lvSDHSfieldAdapter;
	public void parseSDHSFormData(String content){
		try {
			JSONArray jsonArray = new JSONArray(content);
			/*String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);*/

			prefs.edit().putString("sdhs_form_json", content).apply();

			Type listType = new TypeToken<ArrayList<SDHSfieldBean>>() {}.getType();
			sdhSfieldBeans = new Gson().fromJson(jsonArray+"", listType);

			System.out.println("-- parsed size: "+sdhSfieldBeans.size());


			//edit part starts
			if(isEdit){
				if(sdhSfieldBeans != null){

					try {
						JSONArray categoriesJA = new JSONArray(ActivitySDHSList.selectedSDHSlistBean.form_data);

						for (int i = 0; i < sdhSfieldBeans.size(); i++) {
							for (int j = 0; j < sdhSfieldBeans.get(i).questions.size(); j++) {
								for (int k = 0; k < sdhSfieldBeans.get(i).questions.get(j).options.size(); k++) {

									System.out.println("-- i = "+i+" j = "+j+" k = "+k);
									//System.out.println("-- isSelected" + sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected);


									JSONArray questionsJA = categoriesJA.getJSONArray(i);

									if(sdhSfieldBeans.get(i).questions.get(j).question.contains(QUES_TYPE_CHAECKBOX)){//special case--> checkboxes/multiple selection
										//params.add("ans["+i+"]["+j+"][single][]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
										List<String> selectedCheckboxs = Arrays.asList(questionsJA.getJSONObject(j).getString("single").split(", "));
										if(selectedCheckboxs.contains(sdhSfieldBeans.get(i).questions.get(j).options.get(k).key)){
											sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected = true;//for send data to api
											sdhSfieldBeans.get(i).questions.get(j).isAnswered = true;//for validation
										}

									}else{

										if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).key.equalsIgnoreCase(questionsJA.getJSONObject(j).getString("single"))){
											sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected = true;//for send data to api
											sdhSfieldBeans.get(i).questions.get(j).isAnswered = true;//for validation
										}
										//params.add("ans["+i+"]["+j+"][single]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
										if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).is_multi == 1 && questionsJA.getJSONObject(j).has("multi")){
											//params.add("ans["+i+"]["+j+"][multi]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).selectedSpinnerValue);
											sdhSfieldBeans.get(i).questions.get(j).options.get(k).selectedSpinnerValue = questionsJA.getJSONObject(j).getString("multi");
										}
									}



									/*if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected){
										//System.out.println("-- SDHS form key : ans["+i+"]["+j+"]" + "-- Value : "+sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
										if(sdhSfieldBeans.get(i).questions.get(j).question.contains(QUES_TYPE_CHAECKBOX)){//special case--> checkboxes/multiple selection
											params.add("ans["+i+"]["+j+"][single][]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
										}else{
											params.add("ans["+i+"]["+j+"][single]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
											if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).is_multi == 1){
												params.add("ans["+i+"]["+j+"][multi]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).selectedSpinnerValue);
											}
										}
									}*/
								}//for k ends
							}//for j ends
						}//for i ends

					}catch (Exception e){
						e.printStackTrace();
					}

				}else {System.out.println("-- list sdhSfieldBeans is Null");}

			}
			//edit part ends

			lvSDHSfieldAdapter = new LvSDHSfieldAdapter(activity, sdhSfieldBeans);
			lvSDHSForm.setAdapter(lvSDHSfieldAdapter);
			lvSDHSForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	public void submitForm(String is_lock){

		end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		RequestParams params = new RequestParams();
		params.add("patient_id", DATA.selectedUserCallId);
		params.add("author_id", prefs.getString("id", ""));
		params.add("start_time", start_time);
		params.add("end_time", end_time);
		params.put("is_lock", is_lock);

		if(isEdit){
			params.put("id", ActivitySDHSList.selectedSDHSlistBean.id);//this will edit the form
		}

		if(sdhSfieldBeans != null){
			for (int i = 0; i < sdhSfieldBeans.size(); i++) {
				for (int j = 0; j < sdhSfieldBeans.get(i).questions.size(); j++) {
					for (int k = 0; k < sdhSfieldBeans.get(i).questions.get(j).options.size(); k++) {
						System.out.println("-- i = "+i+" j = "+j+" k = "+k);
						//System.out.println("-- isSelected" + sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected);
						if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).isSelected){
							System.out.println("-- SDHS form key : ans["+i+"]["+j+"]" + "-- Value : "+sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
							if(sdhSfieldBeans.get(i).questions.get(j).question.contains(QUES_TYPE_CHAECKBOX)){//special case--> checkboxes/multiple selection
								params.add("ans["+i+"]["+j+"][single][]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
							}else{
								params.add("ans["+i+"]["+j+"][single]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).key);
								if(sdhSfieldBeans.get(i).questions.get(j).options.get(k).is_multi == 1){
									params.add("ans["+i+"]["+j+"][multi]", sdhSfieldBeans.get(i).questions.get(j).options.get(k).selectedSpinnerValue);
								}
							}
						}
					}

				}
			}
		}else {System.out.println("-- list sdhSfieldBeans is Null");}

		ApiManager apiManager = new ApiManager(ApiManager.SDSH_FORM_SAVE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validateDASTForm(){
		boolean validated = true;
		if(sdhSfieldBeans != null){
			for (int i = 0; i < sdhSfieldBeans.size(); i++) {
				for (int j = 0; j < sdhSfieldBeans.get(i).questions.size(); j++) {
					if(! sdhSfieldBeans.get(i).questions.get(j).isAnswered){
						validated = false;
					}
				}
			}
		}else {
			System.out.println("-- list sdhSfieldBeans is Null");
		}

		return validated;
	}

	//========================SDHS form end===============================

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.SDHS_FORM)){
			parseSDHSFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.SDSH_FORM_SAVE)){
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
							ActivitySDHSList.shoulRefresh = true;
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


	//score sup up
	/*int totalScore = 0;
	public void sumUpScore(){
		totalScore = 0;
		for (int i = 0; i < dasTfieldBeans.size(); i++) {
			totalScore = totalScore + dasTfieldBeans.get(i).scoreDerived;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}*/

}
