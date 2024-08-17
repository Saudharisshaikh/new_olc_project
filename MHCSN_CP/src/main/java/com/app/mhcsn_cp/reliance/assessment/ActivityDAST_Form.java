package com.app.mhcsn_cp.reliance.assessment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.ExpandableHeightListView;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.app.mhcsn_cp.PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText;

public class ActivityDAST_Form extends BaseActivity implements AssessSubmit{

	ExpandableHeightListView lvDASTForm;
	TextView tvFormDesc, tvTotalScore;
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit,isReadOnly;

	String start_time, end_time;

	@Override
	protected void onDestroy() {
		DastFormAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dast_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);

		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Network Drug Abuse Screening Test (DAST-10)");
		}

		lvDASTForm = findViewById(R.id.lvDASTForm);
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

					if(! validateDASTForm()){
						if(dastFormAdapter != null){
							DastFormAdapter.validateFlag = true;
							dastFormAdapter.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}

					String assesTittle = getResources().getString(R.string.app_name);
					if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
						assesTittle = getSupportActionBar().getTitle().toString();
					}
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivityDAST_Form.this, assesTittle);

				}
            }
        });

		loadDASTForm();


		preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================DAST Form starts======================
	public void loadDASTForm(){
		String savedJSON = prefs.getString("dast_form_json", "");
		if(!savedJSON.isEmpty()){
			parseDASTFormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.DAST_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	List<DASTfieldBean> dasTfieldBeans;
	DastFormAdapter dastFormAdapter;
	public void parseDASTFormData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);

			prefs.edit().putString("dast_form_json", content).apply();

			JSONArray questions = jsonObject.getJSONArray("questions");
			dasTfieldBeans = new ArrayList<>();
			DASTfieldBean dasTfieldBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				int Yes = questions.getJSONObject(i).getInt("Yes");
				int No = questions.getJSONObject(i).getInt("No");

				dasTfieldBean = new DASTfieldBean(question, Yes, No);
				if(isEdit){
					//view data part starts

                    //note: this block works if data comes as following
                    //{\"1\":\"0\",\"2\":\"0\",\"0\":\"0\",\"9\":\"0\",\"7\":\"0\",\"8\":\"0\",\"5\":\"0\",\"6\":\"0\",\"3\":\"0\",\"4\":\"0\"}
                    try {
                        JSONObject dataJSON = new JSONObject(ActivityDastList.selectedDastListBean.form_data);
                        if(dataJSON.has(i + "")){
                            dasTfieldBean.isGroupSelected = true;
                            dasTfieldBean.scoreDerived = Integer.parseInt(dataJSON.getString(i+""));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        dasTfieldBean.scoreDerived = 0;
                        dasTfieldBean.isGroupSelected = false;
                    }

                    //note: this block works if data comes as following
                    //[\"1\",\"1\",\"0\",\"1\",\"1\",\"1\",\"1\",\"1\",\"1\",\"1\"]
					/*try {
						JSONArray dataJSON = new JSONArray(ActivityDastList.selectedDastListBean.form_data);
						String answer = dataJSON.getString(i);
						dasTfieldBean.scoreDerived = Integer.parseInt(answer);
						dasTfieldBean.isGroupSelected = true;
					}catch (Exception e){
						e.printStackTrace();
						dasTfieldBean.scoreDerived = 0;
						dasTfieldBean.isGroupSelected = false;
					}*/
					//view data part ends
				}

				dasTfieldBeans.add(dasTfieldBean);
				dasTfieldBean = null;
			}
			dastFormAdapter = new DastFormAdapter(activity, dasTfieldBeans);
			lvDASTForm.setAdapter(dastFormAdapter);
			lvDASTForm.setExpanded(true);
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
			params.put("id", ActivityDastList.selectedDastListBean.id);//this will edit the form
		}

		for (int i = 0; i < dasTfieldBeans.size(); i++) {
			DATA.print("ans["+i+"]"   + " = "  + dasTfieldBeans.get(i).scoreDerived);
			if(dasTfieldBeans.get(i).isGroupSelected){
				params.put("ans["+i+"]", dasTfieldBeans.get(i).scoreDerived +"");
			}
		}

		ApiManager apiManager = new ApiManager(ApiManager.DAST_FORM_SAVE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validateDASTForm(){
		boolean validated = true;
		if(dasTfieldBeans != null){
			for (int i = 0; i < dasTfieldBeans.size(); i++) {
				if(! dasTfieldBeans.get(i).isGroupSelected){
					validated = false;
				}
			}
		}
		return validated;
	}

	//========================DAST form end===============================

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.DAST_FORM)){
			parseDASTFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.DAST_FORM_SAVE)){
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
							ActivityDastList.shoulRefresh = true;
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



	//score sup up
	int totalScore = 0;
	public void sumUpScore(){
		totalScore = 0;
		for (int i = 0; i < dasTfieldBeans.size(); i++) {
			totalScore = totalScore + dasTfieldBeans.get(i).scoreDerived;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
