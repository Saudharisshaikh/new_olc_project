package com.app.emcurauc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcurauc.adapter.PatientPrescriptionAdapter;
import com.app.emcurauc.adapter.PrescriptionDetailAdapter;
import com.app.emcurauc.api.ApiCallBack;
import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.api.Dialog_CustomProgress;
import com.app.emcurauc.model.ConversationBean;
import com.app.emcurauc.model.MyPrescriptionModel;
import com.app.emcurauc.model.PrescriptionDetailModel;
import com.app.emcurauc.util.ActionEditText;
import com.app.emcurauc.util.CheckInternetConnection;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.GeneralAlertDialog;
import com.app.emcurauc.util.OpenActivity;
import com.app.emcurauc.util.PrescriptionDetailListener;
import com.app.emcurauc.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActivityPrescriptionDetails extends BaseActivity implements PrescriptionDetailListener {

	Activity activity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;

	LinearLayout noPrescriptionLayout;

	RecyclerView prescriptionRv;

	final String API_URL = "https://api.openai.com/v1/completions";
	 String API_SECRET_KEY = "";

	TextView tvNoConv;

	ListView lvConversations;
	ArrayList<ConversationBean> conversationBeans;

	ActionEditText actionEditText;

	Dialog_CustomProgress customProgressDialog;

	SharedPrefsHelper sharedPrefsHelper;

	//String drugEduQuery = jsonObject.getJSONObject("data").getString("drug_edu_query");

	PrescriptionDetailAdapter prescriptionDetailAdapter;

	ArrayList<PrescriptionDetailModel> prescriptionDetailModelArrayList;

	String prescriptionId,sendEducation;

	@Override
	protected void onResume() {

		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prescription_details);
		
		activity = ActivityPrescriptionDetails.this;
		apiCallBack = this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);

		customProgressDialog = new Dialog_CustomProgress(activity);

		sharedPrefsHelper = SharedPrefsHelper.getInstance();
		prescriptionId = getIntent().getStringExtra("prescription_id");
		sendEducation = getIntent().getStringExtra("send_education");

		prescriptionRv = findViewById(R.id.prescription_details_rv);
		noPrescriptionLayout = findViewById(R.id.no_prescription_layout);
		prescriptionRv.setLayoutManager(new LinearLayoutManager(this));
		prescriptionDetailModelArrayList = new ArrayList<>();
		prescriptionDetailAdapter = new PrescriptionDetailAdapter(this,prescriptionDetailModelArrayList,this);
		prescriptionRv.setAdapter(prescriptionDetailAdapter);

		getPrescriptionDetails();

		
	}//oncreate



	public void showDrugEducationDialog(String aiText,String drugNameText) {

		Dialog dialogAiSuggestedPresp = new Dialog(activity);
		dialogAiSuggestedPresp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAiSuggestedPresp.setContentView(R.layout.patient_drug_education);


		actionEditText = (ActionEditText) dialogAiSuggestedPresp.findViewById(R.id.etAiAnswerPrescription);

		Button buttonDone = (Button) dialogAiSuggestedPresp.findViewById(R.id.btnDone);

		TextView drugName = (TextView) dialogAiSuggestedPresp.findViewById(R.id.drug_name);
        drugName.setText(drugNameText);




		buttonDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialogAiSuggestedPresp.dismiss();
			}
		});






		actionEditText.setText(aiText);


		dialogAiSuggestedPresp.show();

		dialogAiSuggestedPresp.getWindow()
				.setBackgroundDrawable(new ColorDrawable(activity.getResources()
						.getColor(android.R.color.transparent)));

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialogAiSuggestedPresp.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

      //  lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

//        dialogAiSuggestedPresp.setCanceledOnTouchOutside(false);
//        dialogAiSuggestedPresp.show();
//        dialogAiSuggestedPresp.getWindow().setAttributes(lp);

		//dialogForDismiss = dialogAddTemplPresc;
	}



	public void getPrescriptionDetails() {
		RequestParams params = new RequestParams();
		params.put("type","patient");
		params.put("id", prescriptionId);
		ApiManager apiManager = new ApiManager(ApiManager.PATIENT_PRESCRIPTION_DETAILS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {


		if(apiName.equalsIgnoreCase(ApiManager.PATIENT_PRESCRIPTION_DETAILS)){


			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");



				if (data.length() == 0) {
					noPrescriptionLayout.setVisibility(View.VISIBLE);
				} else {
					noPrescriptionLayout.setVisibility(View.GONE);
				}


				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					String id = object.optString("id");
					String  drug_name= object.optString("drug_name");
					String  directions= object.optString("directions");
					String  quantity= object.optString("quantity");
					String  refill= object.optString("refill");
					String  notes= object.optString("notes");



					PrescriptionDetailModel  myPrescriptionModel = new PrescriptionDetailModel();
					myPrescriptionModel.setId(id);
					myPrescriptionModel.setDrug_name(drug_name);
					myPrescriptionModel.setDirections(directions);
					myPrescriptionModel.setQuantity(quantity);
					myPrescriptionModel.setRefill(refill);
					myPrescriptionModel.setNotes(notes);
					myPrescriptionModel.setSend_education(sendEducation);


					prescriptionDetailModelArrayList.add(myPrescriptionModel);
				}
				prescriptionDetailAdapter.notifyDataSetChanged();


				//
			}
			catch (Exception e){
				e.printStackTrace();
			}

		}
}

	private void callChatGPTDrugEducation(String query,String drugName) {

		customProgressDialog.showProgressDialog();
		AsyncHttpClient client = new AsyncHttpClient();
		JSONObject params = new JSONObject();
		try {
			params.put("model", "gpt-3.5-turbo-instruct");
			params.put("prompt", query);
			params.put("max_tokens", 1000);

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		DATA.print("-- query " + query);

		StringEntity entity = new StringEntity(params.toString(), "UTF-8");

		client.addHeader("Content-Type", "application/json");
		client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

		client.post(activity, API_URL, entity, "application/json", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
					DATA.print("-- GPT RESPONSE " + text);
					// Remove first word before "\n"
					//clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
					// Remove both instances of "\n"
					//clearedResponse = clearedResponse.replaceAll("\\n", "");
					DATA.print("-- cleanedResponse frm chatgpt : " + text);
					DATA.print("-- response frm chatgpt : " + response);
					customProgressDialog.dismissProgressDialog();
					showDrugEducationDialog(text,drugName);


				} catch (Exception e) {
					customToast.showToast(""+e.getMessage(),0,0);
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				customProgressDialog.dismissProgressDialog();
			}
		});

	}



	@Override
	public void prescriptionDetail(String drugName) {

		String labels = sharedPrefsHelper.get("app_general_labels");
        try {
            JSONObject jsonObject = new JSONObject(labels);
			JSONObject data = jsonObject.getJSONObject("data");
			String drugEduQuery = data.getString("drug_edu_query");
			API_SECRET_KEY = data.getString("chatgpt_key");
 			String disclaimer = data.optString("disclaimer");

             GeneralAlertDialog.callAlert(ActivityPrescriptionDetails.this,disclaimer,()->{
				 String personalizedDrugEduQuery = drugEduQuery.replace("[drugname]", drugName);
				 callChatGPTDrugEducation(personalizedDrugEduQuery,drugName);
			 },()->{

             },"OK","Not Now","Disclaimer");



		} catch (JSONException e) {
           e.printStackTrace();
        }




	}
}
