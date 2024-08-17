package com.app.onlinecare_pk.b_health.assessment.new_assesment;

import android.os.Bundle;
import android.text.Html;
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
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.app.onlinecare_pk.MedicalHistory1.preventScrollViewFromScrollingToEdiText;

public class ActivityOCD_Form extends BaseActivity implements AssessSubmit{

	ExpandableHeightListView lvOCDForm;
	TextView tvFormDesc, tvTotalScore;
	ScrollView svForm;
	Button btnSubmitForm;

	public boolean isEdit,isReadOnly;

	public static int formFlag = 1;//1 = OCD,  2 = fcsas_form, 3 = Panic_attack, 4 = scoff

	String start_time, end_time;

    public static final String OCD_FORM_NAME = "Obsessive-Compulsive Disorder";
    public static final String FCSAS_FORM_NAME = "Focus and Concentration Self Assessment Survey";
    public static final String PANIC_ATACK_FORM_NAME = "Panic Attack Assessment";
    public static final String SCOFF_FORM_NAME = "SCOFF Assessment"; //Scoff
	public static final String STRESS_QUES_FORM_NAME = "Stress Questionnaire"; //Scoff

	@Override
	protected void onDestroy() {
		LvOCDformAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ocd_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);
		isReadOnly = getIntent().getBooleanExtra("isReadOnly",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		tvFormDesc = findViewById(R.id.tvFormDesc);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			if(formFlag == 1){
				getSupportActionBar().setTitle("OCD");
				tvFormDesc.setText(Html.fromHtml("<b>"+OCD_FORM_NAME+"</b>"));
			}else if(formFlag == 2){
				getSupportActionBar().setTitle("FCSAS");
				tvFormDesc.setText(Html.fromHtml("<b>"+FCSAS_FORM_NAME+"</b>"));
			}else if(formFlag == 3){
				getSupportActionBar().setTitle(PANIC_ATACK_FORM_NAME);
				tvFormDesc.setText(Html.fromHtml("<b>"+PANIC_ATACK_FORM_NAME+"</b>"));
			}else if(formFlag == 4){
				getSupportActionBar().setTitle(SCOFF_FORM_NAME);
				tvFormDesc.setText(Html.fromHtml("<b>"+SCOFF_FORM_NAME+"</b>"));
			}

			//GetLiveCareFormBhealth.assessmentsAdded.add(getSupportActionBar().getTitle()+"");
		}

		lvOCDForm = findViewById(R.id.lvOCDForm);
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
						if(lvOCDformAdapter != null){
							LvOCDformAdapter.validateFlag = true;
							lvOCDformAdapter.notifyDataSetChanged();
						}
						customToast.showToast("Please make sure you have filled the required fields.", 0, 0);

						return;
					}

					String assesTittle = getResources().getString(R.string.app_name);
					if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null){
						assesTittle = getSupportActionBar().getTitle().toString();
					}
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivityOCD_Form.this, assesTittle);
				}
            }
        });


		if(formFlag == 1){
			loadOCDForm();
		}else if(formFlag == 2){
			loadFCSASForm();
		}else if(formFlag == 3){
			loadPanicAtackForm();
		}else if(formFlag == 4){
			loadScoffForm();
		}


		preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================OCD Form starts======================
	public void loadOCDForm(){
		String savedJSON = prefs.getString("ocd_form_json", "");
		if(!savedJSON.isEmpty()){
			parseOCDFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.OCD_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	LvOCDformAdapter lvOCDformAdapter;
	public void parseOCDFormData(String content){
		try {
			/*JSONObject jsonObject = new JSONObject(content);
			String form_title = jsonObject.getString("form_title");
			String form_text = jsonObject.getString("form_text");
			if(getSupportActionBar() != null){
				getSupportActionBar().setTitle(form_title);
			}
			tvFormDesc.setText(form_text);
			JSONArray questions = jsonObject.getJSONArray("questions");*/

			JSONArray questions = new JSONArray(content);

			prefs.edit().putString("ocd_form_json", content).apply();

			ocdFormBeans = new ArrayList<>();
			OCDFormBean ocdFormBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");

				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){
					//note: i am checking here form_data is JSONobj or JSONArray - Request Params order in loop  like ans[0] issue.
					Object json = new JSONTokener(ActivityOCD_List.selectedOCDlistBean.form_data).nextValue();
					if (json instanceof JSONObject){
						DATA.print("-- Form Data is of type JSONObject");
						//view data part starts
						try {
							JSONObject dataJSON = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data);
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
						}
						//view data part ends
					} else if (json instanceof JSONArray){
						DATA.print("-- Form Data is of type JSONArray");
						//view data part starts
						try {
							JSONArray dataJArr = new JSONArray(ActivityOCD_List.selectedOCDlistBean.form_data);

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
				}

				ocdFormBeans.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter = new LvOCDformAdapter(activity, ocdFormBeans);
			lvOCDForm.setAdapter(lvOCDformAdapter);
			lvOCDForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}


    /*public void submitForm2(String is_lock){

        try {
            JSONObject params = new JSONObject();

            end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

            params.put("patient_id", prefs.getString("id", ""));
            params.put("author_id", "0");
            params.put("start_time", start_time);
            params.put("end_time", end_time);
            params.put("score", totalScore+"");
            params.put("is_lock", is_lock);

            if(isEdit){
                params.put("id", ActivityOCD_List.selectedOCDlistBean.id);//this will edit the form
            }


            for (int i = 0; i < ocdFormBeans.size(); i++) {
                DATA.print("ans["+i+"]"   + " = "  + ocdFormBeans.get(i).score);
                if(ocdFormBeans.get(i).isAnswered){
                    params.put("ans["+i+"]", ocdFormBeans.get(i).score +"");
                }
            }

            String apiName = ApiManager.SAVE_OCD_FORM;
            if(ActivityOCD_Form.formFlag == 1){
                apiName = ApiManager.SAVE_OCD_FORM;
            }else if(ActivityOCD_Form.formFlag == 2){
                apiName = ApiManager.SAVE_FCSAS_FORM;
            }else if(ActivityOCD_Form.formFlag == 3){
                apiName = ApiManager.SAVE_PANIC_ATT_FORM;
            }else if(ActivityOCD_Form.formFlag == 4){
                apiName = ApiManager.SAVE_SCOFF_FORM;
            }

            ApiManagerJSON apiManagerJSON = new ApiManagerJSON(apiName,"post",params,apiCallBack, activity);
            apiManagerJSON.loadURL();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

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


		/*for (int i = 0; i < ocdFormBeans.size(); i++) {
			DATA.print("ans["+i+"]"   + " = "  + ocdFormBeans.get(i).score);
			if(ocdFormBeans.get(i).isAnswered){
				params.add("ans["+i+"]", ocdFormBeans.get(i).score +"");
			}
		}*/

		List<String> answersList = new ArrayList<>();
		for (int i = 0; i < ocdFormBeans.size(); i++) {
			DATA.print("ans["+i+"]"   + " = "  + ocdFormBeans.get(i).score);
			if(ocdFormBeans.get(i).isAnswered){
				//params.add("ans["+i+"]", ocdFormBeans.get(i).score +"");
				answersList.add(ocdFormBeans.get(i).score +"");
			}
		}
		params.put("ans", answersList);

		String apiName = ApiManager.SAVE_OCD_FORM;
		if(ActivityOCD_Form.formFlag == 1){
			apiName = ApiManager.SAVE_OCD_FORM;
		}else if(ActivityOCD_Form.formFlag == 2){
			apiName = ApiManager.SAVE_FCSAS_FORM;
		}else if(ActivityOCD_Form.formFlag == 3){
			apiName = ApiManager.SAVE_PANIC_ATT_FORM;
		}else if(ActivityOCD_Form.formFlag == 4){
			apiName = ApiManager.SAVE_SCOFF_FORM;
		}

		ApiManager apiManager = new ApiManager(apiName,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public boolean validateForm(){
		boolean validated = true;
		if(ocdFormBeans != null){
			for (int i = 0; i < ocdFormBeans.size(); i++) {
				if(! ocdFormBeans.get(i).isAnswered){
					validated = false;
				}
			}
		}
		return validated;
	}

	//========================OCD form end===============================


	ArrayList<OCDFormBean> ocdFormBeans;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.OCD_FORM)){
			parseOCDFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.FCSAS_FORM)){
			parseFCSASFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.PANIC_ATTACK_FORM)){
			parsePanicAtackFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.SCOFF_FORM)){
			parseScoffFormData(content);
		}

		else if(apiName.equalsIgnoreCase(ApiManager.SAVE_OCD_FORM) || apiName.equalsIgnoreCase(ApiManager.SAVE_FCSAS_FORM)
		|| apiName.equalsIgnoreCase(ApiManager.SAVE_PANIC_ATT_FORM) || apiName.equalsIgnoreCase(ApiManager.SAVE_SCOFF_FORM)){
			//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){

					if(apiName.equalsIgnoreCase(ApiManager.SAVE_OCD_FORM)){
						GetLiveCareFormBhealth.ocd_form = jsonObject.getString("id");
					}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_FCSAS_FORM)){
						GetLiveCareFormBhealth.fcsas_form = jsonObject.getString("id");
					}if(apiName.equalsIgnoreCase(ApiManager.SAVE_PANIC_ATT_FORM)){
						GetLiveCareFormBhealth.panic_attack_form = jsonObject.getString("id");
					}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_SCOFF_FORM)){
						GetLiveCareFormBhealth.scoff_form = jsonObject.getString("id");
					}

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



	//FCSAS Form starts

	public void loadFCSASForm(){
		String savedJSON = prefs.getString("fcsas_form_json", "");
		if(!savedJSON.isEmpty()){
			parseFCSASFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.FCSAS_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void parseFCSASFormData(String content){
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

			prefs.edit().putString("fcsas_form_json", content).apply();

			ocdFormBeans = new ArrayList<>();
			OCDFormBean ocdFormBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

                if(isEdit){

					//note: i am checking here form_data is JSONobj or JSONArray - Request Params order in loop  like ans[0] issue.
					Object json = new JSONTokener(ActivityOCD_List.selectedOCDlistBean.form_data).nextValue();
					if (json instanceof JSONObject){
						DATA.print("-- Form Data is of type JSONObject");
						//view data part starts
						try {
							JSONObject dataJSON = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data);
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
						}
						//view data part ends
					} else if (json instanceof JSONArray){
						DATA.print("-- Form Data is of type JSONArray");
						//view data part starts
						try {
							JSONArray dataJArr = new JSONArray(ActivityOCD_List.selectedOCDlistBean.form_data);

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

				}

				ocdFormBeans.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter = new LvOCDformAdapter(activity, ocdFormBeans);
			lvOCDForm.setAdapter(lvOCDformAdapter);
			lvOCDForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}
	//FCSAS Form ends



	//PanicAtack Form starts

	public void loadPanicAtackForm(){
		String savedJSON = prefs.getString("panic_atack_form_json", "");
		if(!savedJSON.isEmpty()){
			parsePanicAtackFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.PANIC_ATTACK_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void parsePanicAtackFormData(String content){
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

			prefs.edit().putString("panic_atack_form_json", content).apply();

			ocdFormBeans = new ArrayList<>();
			OCDFormBean ocdFormBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){
					//note: i am checking here form_data is JSONobj or JSONArray - Request Params order in loop  like ans[0] issue.
					Object json = new JSONTokener(ActivityOCD_List.selectedOCDlistBean.form_data).nextValue();
					if (json instanceof JSONObject){
						DATA.print("-- Form Data is of type JSONObject");
						//view data part starts
						try {
							JSONObject dataJSON = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data);
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
						}
						//view data part ends
					} else if (json instanceof JSONArray){
						DATA.print("-- Form Data is of type JSONArray");
						//view data part starts
						try {
							JSONArray dataJArr = new JSONArray(ActivityOCD_List.selectedOCDlistBean.form_data);

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
				}

				ocdFormBeans.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter = new LvOCDformAdapter(activity, ocdFormBeans);
			lvOCDForm.setAdapter(lvOCDformAdapter);
			lvOCDForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}
	//PanicAtack Form ends




	//Scoff Form starts

	public void loadScoffForm(){
		String savedJSON = prefs.getString("scoff_form_json", "");
		if(!savedJSON.isEmpty()){
			parseScoffFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.SCOFF_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void parseScoffFormData(String content){
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

			prefs.edit().putString("scoff_form_json", content).apply();

			ocdFormBeans = new ArrayList<>();
			OCDFormBean ocdFormBean;
			for (int i = 0; i < questions.length(); i++) {
				String question = questions.getJSONObject(i).getString("question");
				List<OCDFormBean.OCDoptionBean> optionsList = new ArrayList<>();
				JSONArray options = questions.getJSONObject(i).getJSONArray("options");
				for (int j = 0; j < options.length(); j++) {
					optionsList.add(new OCDFormBean().new OCDoptionBean(options.getString(j)));
				}
				ocdFormBean = new OCDFormBean(question, optionsList);

				if(isEdit){
					//note: i am checking here form_data is JSONobj or JSONArray - Request Params order in loop  like ans[0] issue.
					Object json = new JSONTokener(ActivityOCD_List.selectedOCDlistBean.form_data).nextValue();
					if (json instanceof JSONObject){
						DATA.print("-- Form Data is of type JSONObject");
						//view data part starts
						try {
							JSONObject dataJSON = new JSONObject(ActivityOCD_List.selectedOCDlistBean.form_data);
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
						}
						//view data part ends
					} else if (json instanceof JSONArray){
						DATA.print("-- Form Data is of type JSONArray");
						//view data part starts
						try {
							JSONArray dataJArr = new JSONArray(ActivityOCD_List.selectedOCDlistBean.form_data);

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

				}

				ocdFormBeans.add(ocdFormBean);
				ocdFormBean = null;
			}
			lvOCDformAdapter = new LvOCDformAdapter(activity, ocdFormBeans);
			lvOCDForm.setAdapter(lvOCDformAdapter);
			lvOCDForm.setExpanded(true);
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}
	//Scoff Form ends


	//score sup up
	int totalScore = 0;
	public void sumUpScore(){
		totalScore = 0;
		for (int i = 0; i < ocdFormBeans.size(); i++) {
			totalScore = totalScore + ocdFormBeans.get(i).score;
		}

		tvTotalScore.setText("Total Score : "+totalScore);
		//tvScoreCriteria.setText(getScoreCrieteria(totalScore));
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}
}
