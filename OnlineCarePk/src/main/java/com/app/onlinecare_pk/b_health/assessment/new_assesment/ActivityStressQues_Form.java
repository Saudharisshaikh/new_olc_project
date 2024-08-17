package com.app.onlinecare_pk.b_health.assessment.new_assesment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.b_health.assessment.AssessSubmit;
import com.app.onlinecare_pk.b_health2.GetLiveCareFormBhealth;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.ExpandableHeightListView;
import com.app.onlinecare_pk.util.GloabalMethods;
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

import static com.app.onlinecare_pk.MedicalHistory1.preventScrollViewFromScrollingToEdiText;

public class ActivityStressQues_Form extends BaseActivity implements AssessSubmit{

	ExpandableHeightListView lvStressQFormS1,lvStressQFormS2,lvStressQFormS3,lvStressQFormS4;
	TextView tvFormDesc, tvTotalScore;
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit,isReadOnly;

	//public static int formFlag = 1;//1 = OCD,  2 = fcsas_form, 3 = Panic_attack, 4 = scoff

	String start_time, end_time;

    /*public static final String OCD_FORM_NAME = "Obsessive-Compulsive Disorder";
    public static final String FCSAS_FORM_NAME = "Focus and Concentration Self Assessment Survey";
    public static final String PANIC_ATACK_FORM_NAME = "Panic Attack Assessment";
    public static final String SCOFF_FORM_NAME = "SCOFF Assessment"; //Scoff*/

	@Override
	protected void onDestroy() {
		LvOCDformAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stressques_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);
		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(ActivityOCD_Form.STRESS_QUES_FORM_NAME);
			//GetLiveCareFormBhealth.assessmentsAdded.add(getSupportActionBar().getTitle()+"");
		}

		lvStressQFormS1 = findViewById(R.id.lvStressQFormS1);
		lvStressQFormS2 = findViewById(R.id.lvStressQFormS2);
		lvStressQFormS3 = findViewById(R.id.lvStressQFormS3);
		lvStressQFormS4 = findViewById(R.id.lvStressQFormS4);


		svForm = findViewById(R.id.svForm);
		tvTotalScore = findViewById(R.id.tvTotalScore);


        if(isEdit){
            if(isReadOnly){
                btnSubmitForm.setText("Done");
            }

            if( ! TextUtils.isEmpty(ActivityOCD_List.selectedOCDlistBean.score)){//not comes in fcsas_list api
				try {
					totalScore  = Integer.parseInt(ActivityOCD_List.selectedOCDlistBean.score);
				}catch (Exception e){
					e.printStackTrace();
				}
				tvTotalScore.setText("Total Score : "+ActivityOCD_List.selectedOCDlistBean.score);
			}

        }

		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadOnly){
                	finish();
				}else {

					if(! validateForm()){
						LvOCDformAdapter.validateFlag = true;
						if(lvOCDformAdapter1 != null){
							lvOCDformAdapter1.notifyDataSetChanged();
						}
						if(lvOCDformAdapter2 != null){
							lvOCDformAdapter2.notifyDataSetChanged();
						}
						if(lvOCDformAdapter3 != null){
							lvOCDformAdapter3.notifyDataSetChanged();
						}
						if(lvOCDformAdapter4 != null){
							lvOCDformAdapter4.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}

					String assesTittle = getResources().getString(R.string.app_name);
					if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
						assesTittle = getSupportActionBar().getTitle().toString();
					}
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivityStressQues_Form.this, assesTittle);
				}
            }
        });


		loadStressQuesForm();


		preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================OCD Form starts======================
	public void loadStressQuesForm(){
		String savedJSON = prefs.getString("stress_quest_form_json", "");
		if(!savedJSON.isEmpty()){
			parseStressQuesFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.STRESS_QUES_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<OCDFormBean> ocdFormBeans1,ocdFormBeans2,ocdFormBeans3,ocdFormBeans4;
	LvOCDformAdapter lvOCDformAdapter1, lvOCDformAdapter2, lvOCDformAdapter3, lvOCDformAdapter4;
	public void parseStressQuesFormData(String content){
		try {
			/*JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);
			JSONArray questions = jsonObject.getJSONArray("questions");*/


			JSONObject jsonObject = new JSONObject(content);

			JSONArray section1 =  jsonObject.optJSONArray("section1");
			JSONArray section2 =  jsonObject.optJSONArray("section2");
			JSONArray section3 =  jsonObject.optJSONArray("section3");
			JSONArray section4 =  jsonObject.optJSONArray("section4");


			prefs.edit().putString("stress_quest_form_json", content).apply();


			OCDFormBean ocdFormBean;

			//section 1 starts
			ocdFormBeans1 = new ArrayList<>();
			for (int i = 0; i < section1.length(); i++) {
				String question = section1.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = section1.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){

					//view data part starts
					try {
						JSONArray dataJArr = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data).getJSONArray("section1");

						Type listType = new TypeToken<ArrayList<String>>() {}.getType();
						ArrayList<String> form_data_list = gson.fromJson(dataJArr.toString(), listType);
                        try {
                            ocdFormBean.score = Integer.parseInt(form_data_list.get(i));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
						//options loop starts
						for (int j = 0; j < ocdFormBean.options.size(); j++) {
							if(j == ocdFormBean.score){
								ocdFormBean.options.get(j).isSelected = true;//for show selection
								ocdFormBean.isAnswered = true;//for validation
								ocdFormBean.selectedAns = ocdFormBean.options.get(j).optionTxt;//this is not used yet but may be use in  future as its iadl form copy
							}
						}
						//options loop ends

					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends



					//view data part starts
					//this code works if form data come as json object
                    /*try {
                        JSONObject dataJSON = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data).getJSONObject("section1");
                        if(dataJSON.has(i + "")){
                            ocdFormBean.isAnswered = true;
                            ocdFormBean.score = dataJSON.getInt(i+"");

                            for (int j = 0; j < ocdFormBean.options.size(); j++) {
                                if(j == ocdFormBean.score){
                                    ocdFormBean.options.get(j).isSelected = true;
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }*/
                    //view data part ends
				}

				ocdFormBeans1.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter1 = new LvOCDformAdapter(activity, ocdFormBeans1);
			lvStressQFormS1.setAdapter(lvOCDformAdapter1);
			lvStressQFormS1.setExpanded(true);

			//section 1 ends




			//section 2 starts
			ocdFormBeans2 = new ArrayList<>();
			for (int i = 0; i < section2.length(); i++) {
				String question = section2.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = section2.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){
					//view data part starts
					try {
						JSONArray dataJArr = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data).getJSONArray("section2");

						Type listType = new TypeToken<ArrayList<String>>() {}.getType();
						ArrayList<String> form_data_list = gson.fromJson(dataJArr.toString(), listType);
                        try {
                            ocdFormBean.score = Integer.parseInt(form_data_list.get(i));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
						//options loop starts
						for (int j = 0; j < ocdFormBean.options.size(); j++) {
							if(j == ocdFormBean.score){
								ocdFormBean.options.get(j).isSelected = true;//for show selection
								ocdFormBean.isAnswered = true;//for validation
								ocdFormBean.selectedAns = ocdFormBean.options.get(j).optionTxt;//this is not used yet but may be use in  future as its iadl form copy
							}
						}
						//options loop ends

					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				ocdFormBeans2.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter2 = new LvOCDformAdapter(activity, ocdFormBeans2);
			lvStressQFormS2.setAdapter(lvOCDformAdapter2);
			lvStressQFormS2.setExpanded(true);

			//section 2 ends



			//section 3 starts
			ocdFormBeans3 = new ArrayList<>();
			for (int i = 0; i < section3.length(); i++) {
				String question = section3.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = section3.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){
					//view data part starts
					try {
						JSONArray dataJArr = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data).getJSONArray("section3");

						Type listType = new TypeToken<ArrayList<String>>() {}.getType();
						ArrayList<String> form_data_list = gson.fromJson(dataJArr.toString(), listType);
                        try {
                            ocdFormBean.score = Integer.parseInt(form_data_list.get(i));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
						//options loop starts
						for (int j = 0; j < ocdFormBean.options.size(); j++) {
							if(j == ocdFormBean.score){
								ocdFormBean.options.get(j).isSelected = true;//for show selection
								ocdFormBean.isAnswered = true;//for validation
								ocdFormBean.selectedAns = ocdFormBean.options.get(j).optionTxt;//this is not used yet but may be use in  future as its iadl form copy
							}
						}
						//options loop ends

					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				ocdFormBeans3.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter3 = new LvOCDformAdapter(activity, ocdFormBeans3);
			lvStressQFormS3.setAdapter(lvOCDformAdapter3);
			lvStressQFormS3.setExpanded(true);

			//section 3 ends


			//section 4 starts
			ocdFormBeans4 = new ArrayList<>();
			for (int i = 0; i < section4.length(); i++) {

				String question = section4.getJSONObject(i).getString("question");

				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = section4.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}

				//special case  -- section 4
				List<Integer> scores = new ArrayList<>();
				if(section4.getJSONObject(i).has("scores")){
					JSONArray scoresJA = section4.getJSONObject(i).getJSONArray("scores");
					for (int j = 0; j < scoresJA.length(); j++) {
						scores.add(scoresJA.getInt(j));
					}
				}
				//special case  -- section 4

				ocdFormBean = new OCDFormBean(question, optionsList, scores);

				if(isEdit){
					//view data part starts
					try {
						JSONArray dataJArr = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data).getJSONArray("section4");

						Type listType = new TypeToken<ArrayList<String>>() {}.getType();
						ArrayList<String> form_data_list = gson.fromJson(dataJArr.toString(), listType);
                        try {
                            ocdFormBean.score = Integer.parseInt(form_data_list.get(i));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
						//options loop starts
						for (int j = 0; j < ocdFormBean.options.size(); j++) {

							if(ocdFormBean.scores.get(j) == ocdFormBean.score){//ocdFormBean.options and ocdFormBean.scores size are same
								ocdFormBean.options.get(j).isSelected = true;//for show selection
								ocdFormBean.isAnswered = true;//for validation
								ocdFormBean.selectedAns = ocdFormBean.options.get(j).optionTxt;//this is not used yet but may be use in  future as its iadl form copy
							}

							/*if(form_data_list.contains(ocdFormBean.options.get(j).optionTxt)){//means this is given answer
								ocdFormBean.options.get(j).isSelected = true;//for show selection
								ocdFormBean.isAnswered = true;//for validation
								ocdFormBean.selectedAns = ocdFormBean.options.get(j).optionTxt;//for submission iadl form save api
								try {
									ocdFormBean.score = ocdFormBean.scores.get(j);//for total score
									//ocdFormBean.options and ocdFormBean.scores size are same phir b double check
								}catch (Exception e){
									e.printStackTrace();
								}
							}*/
						}
						//options loop ends

					}catch (Exception e){
						e.printStackTrace();
					}
					//view data part ends
				}

				ocdFormBeans4.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter4 = new LvOCDformAdapter(activity, ocdFormBeans4);
			lvStressQFormS4.setAdapter(lvOCDformAdapter4);
			lvStressQFormS4.setExpanded(true);

			//section 4 ends


		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	public void submitForm(String is_lock){
		end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		RequestParams params = new RequestParams();
		params.add("patient_id", prefs.getString("id", ""));
		params.add("author_id", "0");
		params.add("start_time", start_time);
		params.add("end_time", end_time);
		params.add("score", totalScore+"");
		params.add("is_lock", is_lock);

		if(isEdit){
			params.add("id", ActivityOCD_List.selectedOCDlistBean.id);//this will edit the form
		}


		for (int i = 0; i < ocdFormBeans1.size(); i++) {
			if(ocdFormBeans1.get(i).isAnswered){
				params.add("ans[section1]["+i+"]", ocdFormBeans1.get(i).score +"");
			}
		}
		for (int i = 0; i < ocdFormBeans2.size(); i++) {
			if(ocdFormBeans2.get(i).isAnswered){
				params.add("ans[section2]["+i+"]", ocdFormBeans2.get(i).score +"");
			}
		}
		for (int i = 0; i < ocdFormBeans3.size(); i++) {
			if(ocdFormBeans3.get(i).isAnswered){
				params.add("ans[section3]["+i+"]", ocdFormBeans3.get(i).score +"");
			}
		}
		for (int i = 0; i < ocdFormBeans4.size(); i++) {
			if(ocdFormBeans4.get(i).isAnswered){
				params.add("ans[section4]["+i+"]", ocdFormBeans4.get(i).score +"");
			}
		}

		ApiManager apiManager = new ApiManager(ApiManager.SAVE_STRESS_QUES_FORM,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validateForm(){
		boolean validated = true;
		if(ocdFormBeans1 != null){
			for (int i = 0; i < ocdFormBeans1.size(); i++) {
				if(! ocdFormBeans1.get(i).isAnswered){
					validated = false;
				}
			}
		}
		if(ocdFormBeans2 != null){
			for (int i = 0; i < ocdFormBeans2.size(); i++) {
				if(! ocdFormBeans2.get(i).isAnswered){
					validated = false;
				}
			}
		}
		if(ocdFormBeans3 != null){
			for (int i = 0; i < ocdFormBeans3.size(); i++) {
				if(! ocdFormBeans3.get(i).isAnswered){
					validated = false;
				}
			}
		}
		if(ocdFormBeans4 != null){
			for (int i = 0; i < ocdFormBeans4.size(); i++) {
				if(! ocdFormBeans4.get(i).isAnswered){
					validated = false;
				}
			}
		}
		return validated;
	}

	//========================OCD form end===============================



	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.STRESS_QUES_FORM)){
			parseStressQuesFormData(content);
		}

		else if(apiName.equalsIgnoreCase(ApiManager.SAVE_STRESS_QUES_FORM)){
			//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){

					GetLiveCareFormBhealth.stress_form = jsonObject.getString("id");

					String severity = jsonObject.optString("severity", "-");

					String self_text = jsonObject.optString("self_text", "-");

					ActivityOCD_List.shoulRefresh = true;
					new GloabalMethods(activity).showAssesSavedDialog(totalScore, severity, self_text);

					//customToast.showToast("",0,0);
					/*AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage("Information has been saved successfully.")
							.setPositiveButton("Ok, Done",null)
							.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							ActivityOCD_List.shoulRefresh = true;
							finish();
						}
					});
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();*/
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
		for (int i = 0; i < ocdFormBeans1.size(); i++) {
			totalScore = totalScore + ocdFormBeans1.get(i).score;
		}
		for (int i = 0; i < ocdFormBeans2.size(); i++) {
			totalScore = totalScore + ocdFormBeans2.get(i).score;
		}
		for (int i = 0; i < ocdFormBeans3.size(); i++) {
			totalScore = totalScore + ocdFormBeans3.get(i).score;
		}
		for (int i = 0; i < ocdFormBeans4.size(); i++) {
			totalScore = totalScore + ocdFormBeans4.get(i).score;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
