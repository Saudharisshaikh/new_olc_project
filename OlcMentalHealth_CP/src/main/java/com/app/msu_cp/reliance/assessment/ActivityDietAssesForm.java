package com.app.msu_cp.reliance.assessment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.PastHistoryBean;
import com.app.msu_cp.util.ActionEditText;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.PatientMedicalHistoryNew;
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

public class ActivityDietAssesForm extends BaseActivity implements AssessSubmit{


	ScrollView svForm;
	Button btnSubmitForm;
	LinearLayout laySection1, laySection2, laySection3, laySection4, laySection5, laySection6, laySection7, laySection8;
	//CheckBox cbIsLocked;//not used anymore

	public boolean isEdit;

	String start_time, end_time;

	@Override
	protected void onDestroy() {
		DastFormAdapter.validateFlag = false;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_diet_asses_form);

		isEdit = getIntent().getBooleanExtra("isEdit",false);

		start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());


		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Complex Care Nutrition Assessment");
		}

		svForm = findViewById(R.id.svForm);

		laySection1 = findViewById(R.id.laySection1);
		laySection2 = findViewById(R.id.laySection2);
		laySection3 = findViewById(R.id.laySection3);
		laySection4 = findViewById(R.id.laySection4);
		laySection5 = findViewById(R.id.laySection5);
		laySection6 = findViewById(R.id.laySection6);
		laySection7 = findViewById(R.id.laySection7);
		laySection8 = findViewById(R.id.laySection8);

		//cbIsLocked = findViewById(R.id.cbIsLocked);
		btnSubmitForm = findViewById(R.id.btnSubmitForm);

		/*if(isEdit){
			btnSubmitForm.setText("Done");
		}*/

		btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


				if(false){//isReadOnly   // not used for now
					finish();
				}else {

					/*if(! validateDASTForm()){  // put validation here if required in future
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
					new GloabalMethods(activity).showConfSaveAssesDialog(ActivityDietAssesForm.this, assesTittle);

				}


				//submitForm();
                /*if(isEdit){
                	finish();
				}else {
					submitForm();
				}*/
            }
        });

		loadDietAssesForm();


		PatientMedicalHistoryNew.preventScrollViewFromScrollingToEdiText(svForm);

		new GloabalMethods(activity).setAssesListHeader();
		GloabalMethods.activityAssesForm = activity;
	}


	//=====================dietitary_assessment Form starts======================
	public void loadDietAssesForm(){
		String savedJSON = prefs.getString("dietitary_assessment_form_json", "");
		if(!savedJSON.isEmpty()){
			parseDietAssesFormData(savedJSON);
			ApiManager.shouldShowPD = false;
		}
		ApiManager apiManager = new ApiManager(ApiManager.DIET_ASSES_FORM,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	List<DietitaryAssesFormBean> dietitaryAssesFormBeans = new ArrayList<>(),
			dietitaryAssesFormBeansAll = new ArrayList<>();
	//DastFormAdapter dastFormAdapter;
	public void parseDietAssesFormData(String content){
		try {
			JSONObject jsonObject = new JSONObject(content);

			prefs.edit().putString("dietitary_assessment_form_json", content).apply();
			
			dietitaryAssesFormBeans = new ArrayList<>();
			DietitaryAssesFormBean dietitaryAssesFormBean;
			for (int i = 1; i < 100; i++) {//32
				String formFieldKey = "code_"+i;
				if(jsonObject.has(formFieldKey)){
					JSONObject formField = jsonObject.getJSONObject(formFieldKey);
					String label = formField.getString("label");
					String key = formField.getString("key");
					String type = "";
					ArrayList<String> options = new ArrayList<>();

					if(formFieldKey.equalsIgnoreCase("code_2")){//this is diagnosis from medical history
						ArrayList<PastHistoryBean> pastHistoryBeans = sharedPrefsHelper.getPastHistoryDiag();
						for (int j = 0; j < pastHistoryBeans.size(); j++) {
							options.add(pastHistoryBeans.get(j).getDiseases());
						}
						type = "checkbox";
					}else {
						if(formField.has("type")){
							type = formField.getString("type");
						}
						if(formField.has("options")){
							JSONArray optionsJArray = formField.getJSONArray("options");
							for (int j = 0; j < optionsJArray.length(); j++) {
								options.add(optionsJArray.getString(j));
							}
						}
					}

					dietitaryAssesFormBean = new DietitaryAssesFormBean(label, key, type, options, formFieldKey);
					dietitaryAssesFormBeans.add(dietitaryAssesFormBean);
					dietitaryAssesFormBean = null;
				}

				//for _a keys
				formFieldKey = formFieldKey+"_a";
				if(jsonObject.has(formFieldKey)){
					JSONObject formField = jsonObject.getJSONObject(formFieldKey);
					String label = formField.getString("label");
					String key = formField.getString("key");
					String type = "";
					ArrayList<String> options = new ArrayList<>();

					if(formFieldKey.equalsIgnoreCase("code_2")){//this is diagnosis from medical history
						ArrayList<PastHistoryBean> pastHistoryBeans = sharedPrefsHelper.getPastHistoryDiag();
						for (int j = 0; j < pastHistoryBeans.size(); j++) {
							options.add(pastHistoryBeans.get(j).getDiseases());
						}
						type = "checkbox";
					}else {
						if(formField.has("type")){
							type = formField.getString("type");
						}
						if(formField.has("options")){
							JSONArray optionsJArray = formField.getJSONArray("options");
							for (int j = 0; j < optionsJArray.length(); j++) {
								options.add(optionsJArray.getString(j));
							}
						}
					}

					dietitaryAssesFormBean = new DietitaryAssesFormBean(label, key, type, options, formFieldKey);
					dietitaryAssesFormBeans.add(dietitaryAssesFormBean);
					dietitaryAssesFormBean = null;
				}

				//for _b keys
				formFieldKey = formFieldKey.replace("_a","")+"_b";
				if(jsonObject.has(formFieldKey)){
					JSONObject formField = jsonObject.getJSONObject(formFieldKey);
					String label = formField.getString("label");
					String key = formField.getString("key");
					String type = "";
					ArrayList<String> options = new ArrayList<>();

					if(formFieldKey.equalsIgnoreCase("code_2")){//this is diagnosis from medical history
						ArrayList<PastHistoryBean> pastHistoryBeans = sharedPrefsHelper.getPastHistoryDiag();
						for (int j = 0; j < pastHistoryBeans.size(); j++) {
							options.add(pastHistoryBeans.get(j).getDiseases());
						}
						type = "checkbox";
					}else {
						if(formField.has("type")){
							type = formField.getString("type");
						}
						if(formField.has("options")){
							JSONArray optionsJArray = formField.getJSONArray("options");
							for (int j = 0; j < optionsJArray.length(); j++) {
								options.add(optionsJArray.getString(j));
							}
						}
					}

					dietitaryAssesFormBean = new DietitaryAssesFormBean(label, key, type, options, formFieldKey);
					dietitaryAssesFormBeans.add(dietitaryAssesFormBean);
					dietitaryAssesFormBean = null;
				}
			}//end service loop


			//new code
			dietitaryAssesFormBeansAll.clear();
			dietitaryAssesFormBeansAll.addAll(dietitaryAssesFormBeans);//make other list to re arange original list

			dietitaryAssesFormBeans.clear();

			DietitaryAssesFormBean bean2 = getFieldByCode("code_2");
			if(bean2 != null){
				dietitaryAssesFormBeans.add(bean2);
			}
			DietitaryAssesFormBean bean3 = getFieldByCode("code_3");
			if(bean3 != null){
				dietitaryAssesFormBeans.add(bean3);
			}
			DietitaryAssesFormBean bean32 = getFieldByCode("code_32");
			if(bean32 != null){
				dietitaryAssesFormBeans.add(bean32);
			}
			DietitaryAssesFormBean bean5 = getFieldByCode("code_5");
			if(bean5 != null){
				dietitaryAssesFormBeans.add(bean5);
			}
			DietitaryAssesFormBean bean33 = getFieldByCode("code_33");
			if(bean33 != null){
				dietitaryAssesFormBeans.add(bean33);
			}
			DietitaryAssesFormBean bean33_a = getFieldByCode("code_33_a");
			if(bean33_a != null){
				dietitaryAssesFormBeans.add(bean33_a);
			}
			DietitaryAssesFormBean bean33_b = getFieldByCode("code_33_b");
			if(bean33_b != null){
				dietitaryAssesFormBeans.add(bean33_b);
			}
			DietitaryAssesFormBean bean6 = getFieldByCode("code_6");
			if(bean6 != null){
				dietitaryAssesFormBeans.add(bean6);
			}
			DietitaryAssesFormBean bean35 = getFieldByCode("code_35");
			if(bean35 != null){
				dietitaryAssesFormBeans.add(bean35);
			}
			DietitaryAssesFormBean bean36 = getFieldByCode("code_36");
			if(bean36 != null){
				dietitaryAssesFormBeans.add(bean36);
			}
			DietitaryAssesFormBean bean37 = getFieldByCode("code_37");
			if(bean37 != null){
				dietitaryAssesFormBeans.add(bean37);
			}
			DietitaryAssesFormBean bean38 = getFieldByCode("code_38");
			if(bean38 != null){
				dietitaryAssesFormBeans.add(bean38);
			}
			DietitaryAssesFormBean bean7 = getFieldByCode("code_7");
			if(bean7 != null){
				dietitaryAssesFormBeans.add(bean7);
			}
			DietitaryAssesFormBean bean8 = getFieldByCode("code_8");
			if(bean8 != null){
				dietitaryAssesFormBeans.add(bean8);
			}
			DietitaryAssesFormBean bean61 = getFieldByCode("code_61");
			if(bean61 != null){
				dietitaryAssesFormBeans.add(bean61);
			}
			DietitaryAssesFormBean bean9 = getFieldByCode("code_9");
			if(bean9 != null){
				dietitaryAssesFormBeans.add(bean9);
			}
			DietitaryAssesFormBean bean10 = getFieldByCode("code_10");
			if(bean10 != null){
				dietitaryAssesFormBeans.add(bean10);
			}
			DietitaryAssesFormBean bean45 = getFieldByCode("code_45");
			if(bean45 != null){
				dietitaryAssesFormBeans.add(bean45);
			}
			DietitaryAssesFormBean bean46 = getFieldByCode("code_46");
			if(bean46 != null){
				dietitaryAssesFormBeans.add(bean46);
			}
			DietitaryAssesFormBean bean47 = getFieldByCode("code_47");
			if(bean47 != null){
				dietitaryAssesFormBeans.add(bean47);
			}
			DietitaryAssesFormBean bean49 = getFieldByCode("code_49");
			if(bean49 != null){
				dietitaryAssesFormBeans.add(bean49);
			}
			DietitaryAssesFormBean bean11 = getFieldByCode("code_11");
			if(bean11 != null){
				dietitaryAssesFormBeans.add(bean11);
			}
			DietitaryAssesFormBean bean51 = getFieldByCode("code_51");
			if(bean51 != null){
				dietitaryAssesFormBeans.add(bean51);
			}
			DietitaryAssesFormBean bean12 = getFieldByCode("code_12");
			if(bean12 != null){
				dietitaryAssesFormBeans.add(bean12);
			}
			DietitaryAssesFormBean bean13 = getFieldByCode("code_13");
			if(bean13 != null){
				dietitaryAssesFormBeans.add(bean13);
			}
			DietitaryAssesFormBean bean14 = getFieldByCode("code_14");
			if(bean14 != null){
				dietitaryAssesFormBeans.add(bean14);
			}
			DietitaryAssesFormBean bean15 = getFieldByCode("code_15");
			if(bean15 != null){
				dietitaryAssesFormBeans.add(bean15);
			}
			DietitaryAssesFormBean bean16 = getFieldByCode("code_16");
			if(bean16 != null){
				dietitaryAssesFormBeans.add(bean16);
			}
			DietitaryAssesFormBean bean17 = getFieldByCode("code_17");
			if(bean17 != null){
				dietitaryAssesFormBeans.add(bean17);
			}
			DietitaryAssesFormBean bean18 = getFieldByCode("code_18");
			if(bean18 != null){
				dietitaryAssesFormBeans.add(bean18);
			}
			DietitaryAssesFormBean bean19 = getFieldByCode("code_19");
			if(bean19 != null){
				dietitaryAssesFormBeans.add(bean19);
			}
			DietitaryAssesFormBean bean20 = getFieldByCode("code_20");
			if(bean20 != null){
				dietitaryAssesFormBeans.add(bean20);
			}
			DietitaryAssesFormBean bean21 = getFieldByCode("code_21");
			if(bean21 != null){
				dietitaryAssesFormBeans.add(bean21);
			}
			DietitaryAssesFormBean bean22 = getFieldByCode("code_22");
			if(bean22 != null){
				dietitaryAssesFormBeans.add(bean22);
			}
			DietitaryAssesFormBean bean23 = getFieldByCode("code_23");
			if(bean23 != null){
				dietitaryAssesFormBeans.add(bean23);
			}
			DietitaryAssesFormBean bean53 = getFieldByCode("code_53");
			if(bean53 != null){
				dietitaryAssesFormBeans.add(bean53);
			}
			DietitaryAssesFormBean bean54 = getFieldByCode("code_54");
			if(bean54 != null){
				dietitaryAssesFormBeans.add(bean54);
			}
			DietitaryAssesFormBean bean55 = getFieldByCode("code_55");
			if(bean55 != null){
				dietitaryAssesFormBeans.add(bean55);
			}
			DietitaryAssesFormBean bean24 = getFieldByCode("code_24");
			if(bean24 != null){
				dietitaryAssesFormBeans.add(bean24);
			}
			DietitaryAssesFormBean bean57 = getFieldByCode("code_57");
			if(bean57 != null){
				dietitaryAssesFormBeans.add(bean57);
			}
			DietitaryAssesFormBean bean58 = getFieldByCode("code_58");
			if(bean58 != null){
				dietitaryAssesFormBeans.add(bean58);
			}
			DietitaryAssesFormBean bean25 = getFieldByCode("code_25");
			if(bean25 != null){
				dietitaryAssesFormBeans.add(bean25);
			}
			DietitaryAssesFormBean bean26 = getFieldByCode("code_26");
			if(bean26 != null){
				dietitaryAssesFormBeans.add(bean26);
			}
			DietitaryAssesFormBean bean27 = getFieldByCode("code_27");
			if(bean27 != null){
				dietitaryAssesFormBeans.add(bean27);
			}
			DietitaryAssesFormBean bean28 = getFieldByCode("code_28");
			if(bean28 != null){
				dietitaryAssesFormBeans.add(bean28);
			}
			DietitaryAssesFormBean bean29 = getFieldByCode("code_29");
			if(bean29 != null){
				dietitaryAssesFormBeans.add(bean29);
			}
			DietitaryAssesFormBean bean30 = getFieldByCode("code_30");
			if(bean30 != null){
				dietitaryAssesFormBeans.add(bean30);
			}
			DietitaryAssesFormBean bean31 = getFieldByCode("code_31");
			if(bean31 != null){
				dietitaryAssesFormBeans.add(bean31);
			}
			//new code


			laySection1.removeAllViews();
			laySection2.removeAllViews();
			laySection3.removeAllViews();
			laySection4.removeAllViews();
			laySection5.removeAllViews();
			laySection6.removeAllViews();
			laySection7.removeAllViews();
			laySection8.removeAllViews();
			for (int i = 0; i < dietitaryAssesFormBeans.size(); i++) {

				DietitaryAssesFormBean dafBean = dietitaryAssesFormBeans.get(i);

				//Edit part starts
				if(isEdit){
					try {
						JSONObject formDataJSON = new JSONObject(ActivityDietAssesList.selectedDietitaryAssesListBean.form_data);
						if(formDataJSON.has(dafBean.code)){
							dafBean.inputValue = formDataJSON.getString(dafBean.code);

                            if(dafBean.type.equalsIgnoreCase("dropdown")){
                                //containerLay = getSpinner(dafBean);
                                for (int j = 0; j < dafBean.options.size(); j++) {
                                    if(dafBean.options.get(j).equalsIgnoreCase(dafBean.inputValue)){
                                        dafBean.spPosition = j;
                                    }
                                }
                            }
                            /*else if(dafBean.type.equalsIgnoreCase("checkbox")){
                                //containerLay = getCheckboxGroup(dafBean);
                                //this part of code is done in getCheckboxGroup Method
                            }*/
						}
					}catch (JSONException e){
						e.printStackTrace();
					}
				}
				//Edit part Ends

				LinearLayout containerLay = null;

				if(dafBean.type.equalsIgnoreCase("dropdown")){
					containerLay = getSpinner(dafBean);
				}else if(dafBean.type.equalsIgnoreCase("radio")){
					containerLay = getRadioGroup(dafBean);
				}else if(dafBean.type.equalsIgnoreCase("checkbox")){
					containerLay = getCheckboxGroup(dafBean);
				}else{
					containerLay = getEditText(dafBean);
				}

				if(i <= 1){
					laySection1.addView(containerLay);
				}else if(i > 1  && i <= 25){
					laySection2.addView(containerLay);
				}else if(i > 25 && i <=32){
					laySection3.addView(containerLay);
				}else if(i > 32 && i <=33){
					laySection4.addView(containerLay);
				} else if(i > 33 && i <=42){
					laySection5.addView(containerLay);
				}else if(i > 42 && i<=45){
					laySection6.addView(containerLay);
				}else if(i > 45 && i<=46){
					laySection7.addView(containerLay);
				}else if(i > 46 && i<=47){
					laySection8.addView(containerLay);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	public DietitaryAssesFormBean getFieldByCode(String code){
		DietitaryAssesFormBean bean = null;
		for (int i = 0; i < dietitaryAssesFormBeansAll.size(); i++) {
			if(dietitaryAssesFormBeansAll.get(i).code.equalsIgnoreCase(code)){
				bean = dietitaryAssesFormBeansAll.get(i);
				return bean;
			}
		}
		return bean;
	}

	public void submitForm(String is_lock){
		end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		RequestParams params = new RequestParams();

		if(isEdit){
			params.add("id", ActivityDietAssesList.selectedDietitaryAssesListBean.id);
		}
		//String is_lock = cbIsLocked.isChecked() ? "1" : "0";
		params.add("is_lock", is_lock);
		params.add("patient_id", DATA.selectedUserCallId);
		params.add("author_id", prefs.getString("id", ""));
		params.add("start_time", start_time);
		params.add("end_time", end_time);
		//params.put("score", totalScore+"");
		for (int i = 0; i < dietitaryAssesFormBeans.size(); i++) {
			if(dietitaryAssesFormBeans.get(i).type.equalsIgnoreCase("checkbox")){
				if(dietitaryAssesFormBeans.get(i).cbValueBeans != null){
					for (int j = 0; j < dietitaryAssesFormBeans.get(i).cbValueBeans.size(); j++) {
						if(dietitaryAssesFormBeans.get(i).cbValueBeans.get(j).selectedByUSer){
							params.add(dietitaryAssesFormBeans.get(i).key,  dietitaryAssesFormBeans.get(i).cbValueBeans.get(j).value);
						}
					}
				}
			}else {
				params.add(dietitaryAssesFormBeans.get(i).key, dietitaryAssesFormBeans.get(i).inputValue);
			}
		}

		ApiManager apiManager = new ApiManager(ApiManager.DIET_ASSES_FORM_SAVE,"post",params,apiCallBack, activity);

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

	//========================dietitary_assessment form end===============================

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.DIET_ASSES_FORM)){
			parseDietAssesFormData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.DIET_ASSES_FORM_SAVE)){
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
							ActivityDietAssesList.shoulRefresh = true;
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


	LinearLayout lay_code_33_a,lay_code_33_b;

	//make form fields
	public static int dpToPx(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float)dp * density);
	}
	LinearLayout.LayoutParams mainLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	public LinearLayout getEditText(final DietitaryAssesFormBean dietitaryAssesFormBean){

		final LinearLayout containerLay = new LinearLayout (activity);
		containerLay.setOrientation(LinearLayout.VERTICAL); // set orientation
		//containerLay.setBackgroundResource(R.drawable.cust_border_white); // set background
		containerLay.setLayoutParams(mainLP);
		((LinearLayout.LayoutParams)containerLay.getLayoutParams()).setMargins(0,0,0,10);
		//containerLay.setElevation(2);
		//containerLay.setPadding(dpToPx(activity, 10),dpToPx(activity, 10),dpToPx(activity, 10),dpToPx(activity, 10));
		containerLay.setPadding(dpToPx(activity, 10),dpToPx(activity, 0),dpToPx(activity, 10),dpToPx(activity, 0));



		//set layout
		TextView textView = new TextView(this);
		textView.setLayoutParams(mainLP);
		textView.setText(dietitaryAssesFormBean.label);
		//textView.setTextSize(20);
		textView.setTextAppearance(activity,android.R.style.TextAppearance_Small);
		textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_light)));
		textView.setTypeface(null, Typeface.BOLD);
		/*if(formItemBean.required){
			textView.append(" *");
		}*/

		containerLay.addView(textView);

		//---------------make edittext
		final EditText editText = new ActionEditText(activity);
		editText.setLayoutParams(mainLP);
		editText.setBackgroundResource(R.drawable.cust_border_white_outline);
		editText.setPadding(dpToPx(activity, 10),dpToPx(activity, 10),dpToPx(activity, 10),dpToPx(activity, 10));
		editText.setGravity(Gravity.TOP | Gravity.START);
		editText.setTextAppearance(activity,android.R.style.TextAppearance_Small);
		editText.setTextColor(getResources().getColor(R.color.black));

		editText.setHint("Please enter here");//dietitaryAssesFormBean.label

		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		//editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		//editText.setMinLines(4);
		//editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		//OnlineCare
		if(dietitaryAssesFormBean.key.equalsIgnoreCase("form_data[code_1]")){
			dietitaryAssesFormBean.inputValue = DATA.selectedUserCallAge;
			editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}else if(dietitaryAssesFormBean.key.equalsIgnoreCase("form_data[code_14]")){

			dietitaryAssesFormBean.inputValue = DATA.selectedUserCallHistoryMed;

			//DATA.print("-- value DATA.selectedUserCallHistoryMed = "+DATA.selectedUserCallHistoryMed+" key = "+dietitaryAssesFormBean.key);

			editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editText.setMinLines(2);

		}else if(dietitaryAssesFormBean.key.equalsIgnoreCase("form_data[code_15]")){
			dietitaryAssesFormBean.inputValue = DATA.selectedUserCallHeight;
			editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_PHONE);
		}else if(dietitaryAssesFormBean.key.equalsIgnoreCase("form_data[code_16]")){
			dietitaryAssesFormBean.inputValue = DATA.selectedUserCallWeight;
			editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_PHONE);
		}else if(dietitaryAssesFormBean.key.equalsIgnoreCase("form_data[code_17]")){
			dietitaryAssesFormBean.inputValue = DATA.selectedUserCallBMI;
			editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_PHONE);
		}
		//DATA.print("-- key: "+dietitaryAssesFormBean.key+ " value : "+dietitaryAssesFormBean.inputValue);
		editText.setText(dietitaryAssesFormBean.inputValue);
		//OnlineCare




		containerLay.addView(editText);

		addTxtChangeListener(editText, dietitaryAssesFormBean);


		if(dietitaryAssesFormBean.code.equalsIgnoreCase("code_33_a")){
			lay_code_33_a = containerLay;
			lay_code_33_a.setVisibility(View.GONE);
		}else if(dietitaryAssesFormBean.code.equalsIgnoreCase("code_33_b")){
			lay_code_33_b = containerLay;
			lay_code_33_b.setVisibility(View.GONE);
		}

		return containerLay;
	}


	public void addTxtChangeListener(final EditText editText, final DietitaryAssesFormBean dietitaryAssesFormBean){
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				dietitaryAssesFormBean.inputValue = editable.toString();
			}
		});
	}



	public LinearLayout getSpinner(final DietitaryAssesFormBean dietitaryAssesFormBean){

		LinearLayout containerLay = new LinearLayout (activity);
		containerLay.setOrientation(LinearLayout.VERTICAL); // set orientation
		//containerLay.setBackgroundResource(R.drawable.cust_border_white); // set background
		containerLay.setLayoutParams(mainLP);
		((LinearLayout.LayoutParams)containerLay.getLayoutParams()).setMargins(0,0,0,10);
		//containerLay.setElevation(2);
		containerLay.setPadding(dpToPx(activity, 10),dpToPx(activity, 0),dpToPx(activity, 10),dpToPx(activity, 0));



		//set layout
		TextView textView = new TextView(this);
		textView.setLayoutParams(mainLP);
		textView.setText(dietitaryAssesFormBean.label);
		//textView.setTextSize(20);
		textView.setTextAppearance(activity,android.R.style.TextAppearance_Small);
		textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_light)));
		textView.setTypeface(null, Typeface.BOLD);
		/*if(formItemBean.required){
			textView.append(" *");
		}*/

		containerLay.addView(textView);
		ArrayList<String> spinnerOptions = dietitaryAssesFormBean.options;

		final Spinner spinner = new Spinner(activity);
		spinner.setLayoutParams(mainLP);
		spinner.setBackgroundResource(R.drawable.spinner_bg);

		ArrayAdapter<String> spAdapter = new ArrayAdapter<>(activity, R.layout.sp_item, spinnerOptions);
		spAdapter.setDropDownViewResource(R.layout.sp_item);
		spinner.setAdapter(spAdapter);
		spinner.setTag(spinnerOptions);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				//important: set values of current formItemBean input by user start
				ArrayList<String> data = (ArrayList<String>) adapterView.getTag();
				//DATA.print("--sp label "+data.get(i));
				dietitaryAssesFormBean.inputValue = data.get(i);
				dietitaryAssesFormBean.spPosition = i;
				//important: set values of current formItemBean input by user end
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		spinner.setSelection(dietitaryAssesFormBean.spPosition);

		containerLay.addView(spinner);

		//====================== Drop Down List Dependencies======================================
		//spinners.add(spinner);
		//spinner.setTag(R.id.formitembean_toview_tag, formItemBean);

		return containerLay;
	}


	public LinearLayout getRadioGroup(final DietitaryAssesFormBean dietitaryAssesFormBean){

		LinearLayout containerLay = new LinearLayout (activity);
		containerLay.setOrientation(LinearLayout.VERTICAL); // set orientation
		//containerLay.setBackgroundResource(R.drawable.cust_border_white); // set background
		containerLay.setLayoutParams(mainLP);
		((LinearLayout.LayoutParams)containerLay.getLayoutParams()).setMargins(0,0,0,10);
		//containerLay.setElevation(2);
		containerLay.setPadding(dpToPx(activity, 10),dpToPx(activity, 0),dpToPx(activity, 10),dpToPx(activity, 0));



		//set layout
		TextView textView = new TextView(this);
		textView.setLayoutParams(mainLP);
		textView.setText(dietitaryAssesFormBean.label);
		//textView.setTextSize(20);
		textView.setTextAppearance(activity,android.R.style.TextAppearance_Small);
		textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_light)));
		textView.setTypeface(null, Typeface.BOLD);
		/*if(formItemBean.required){
			textView.append(" *");
		}*/

		containerLay.addView(textView);

		RadioGroup rg = new RadioGroup(this); //create the RadioGroup
		rg.setLayoutParams(mainLP);
		rg.setOrientation(RadioGroup.VERTICAL);//RadioGroup.VERTICAL or RadioGroup.HORIZONTAL

		ArrayList<String> radioOptions = dietitaryAssesFormBean.options;
		for (int i = 0; i < radioOptions.size(); i++) {

			RadioButton radioButton = new RadioButton(activity);
			radioButton.setLayoutParams(mainLP);
			radioButton.setText(radioOptions.get(i));
			radioButton.setId(i);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.theme_red)));
			}

			radioButton.setTag(radioOptions.get(i));
			rg.addView(radioButton);

			if(radioOptions.get(i).equalsIgnoreCase(dietitaryAssesFormBean.inputValue)){
				radioButton.setChecked(true);
			}
		}

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//int checkedRadioButtonId = rg.getCheckedRadioButtonId();
				RadioButton radioButton = group.findViewById(group.getCheckedRadioButtonId());
				//DATA.print("--rb_tag"+radioButton.getTag());
				String radioValue = (String) radioButton.getTag();
				dietitaryAssesFormBean.inputValue = radioValue;

				if(dietitaryAssesFormBean.code.equalsIgnoreCase("code_33")){
					int vis = radioValue.equalsIgnoreCase("yes") ? View.VISIBLE : View.GONE;
					if(lay_code_33_a != null){
						lay_code_33_a.setVisibility(vis);
					}
					if(lay_code_33_b != null){
						lay_code_33_b.setVisibility(vis);
					}
				}
			}
		});

		containerLay.addView(rg);
		//root_layout.addView(containerLay);
		return containerLay;
	}



	public LinearLayout getCheckboxGroup(DietitaryAssesFormBean dietitaryAssesFormBean){

		LinearLayout containerLay = new LinearLayout (activity);
		containerLay.setOrientation(LinearLayout.VERTICAL); // set orientation
		//containerLay.setBackgroundResource(R.drawable.cust_border_white); // set background
		containerLay.setLayoutParams(mainLP);
		((LinearLayout.LayoutParams)containerLay.getLayoutParams()).setMargins(0,0,0,10);
		//containerLay.setElevation(2);
		containerLay.setPadding(dpToPx(activity, 10),dpToPx(activity, 0),dpToPx(activity, 10),dpToPx(activity, 0));



		//set layout
		TextView textView = new TextView(this);
		textView.setLayoutParams(mainLP);
		textView.setText(dietitaryAssesFormBean.label);
		//textView.setTextSize(20);
		textView.setTextAppearance(activity,android.R.style.TextAppearance_Small);
		textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_light)));
		textView.setTypeface(null, Typeface.BOLD);
		/*if(formItemBean.required){
			textView.append(" *");
		}*/

		containerLay.addView(textView);


		if(dietitaryAssesFormBean.options != null){

			dietitaryAssesFormBean.cbValueBeans = new ArrayList<>();

			//edit form part starts
            ArrayList<String> checkedValuesArrList = null;
            if(isEdit){
                try {
                    JSONArray checkedValues = new JSONArray(dietitaryAssesFormBean.inputValue);
                    Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                    checkedValuesArrList = gson.fromJson(checkedValues.toString(), listType);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            //edit form part ends

			for (int i = 0; i < dietitaryAssesFormBean.options.size(); i++) {

				String value = dietitaryAssesFormBean.options.get(i);

				CBValueBean cbValueBean = new CBValueBean(value);
                //edit form part starts
				if(isEdit && checkedValuesArrList!=null){
				    if(checkedValuesArrList.contains(cbValueBean.value)){
				        cbValueBean.selectedByUSer = true;
                    }
                }
                //edit form part ends
				dietitaryAssesFormBean.cbValueBeans.add(cbValueBean);

				CheckBox checkBox  = new CheckBox(activity);
				checkBox.setText(cbValueBean.value);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.theme_red)));
				}
				checkBox.setId(i);
				checkBox.setTag(cbValueBean);
				checkBox.setLayoutParams(mainLP);

				checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						DATA.print("--cb checkchange tag: "+buttonView.getTag()+" ischecked: "+isChecked);
						cbValueBean.selectedByUSer = isChecked;
					}
				});
				checkBox.setChecked(cbValueBean.selectedByUSer);
				containerLay.addView(checkBox);
			}
		}

		return containerLay;
	}


	@Override
	public void submitAssessment(String is_lock_asses) {
		submitForm(is_lock_asses);
	}

}
