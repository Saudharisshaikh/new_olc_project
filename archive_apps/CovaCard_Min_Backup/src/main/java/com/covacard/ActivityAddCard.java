package com.covacard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.core.content.FileProvider;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.covacard.R;
import com.covacard.api.ApiManager;
import com.covacard.model.SubscriptionPlanBean;
import com.covacard.util.DATA;
import com.covacard.util.LiveCareInsuranceCardhelper;
import com.covacard.util.LiveCareInsuranceInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ActivityAddCard extends ActivityBaseDrawer implements LiveCareInsuranceInterface {


	ImageView ivVCfrontImg,ivDeleteVCfrontImg,ivVCbackImg,ivDeleteVCbackImg;
	EditText etName,etRelationship, etAdditionalTypeOfCard;
	LinearLayout layAddiTypeCont;
	Spinner spCardType, spPurpose;
	Button btnSaveCard,btnNotNow, btnRetakeFrontImg, btnRetakeBackImg;

	ArrayList<String> cardTypesList;
	String[] purposeArray = {"For Personal Use"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_covacard_add);
		getLayoutInflater().inflate(R.layout.activity_covacard_add, container_frame);

		if(getSupportActionBar() != null){
			/*getSupportActionBar().setTitle("Scan Card");
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
			getSupportActionBar().hide();
		}

		tvToolbarTitle.setText("Add Card");
		ivToolbarBack.setVisibility(View.VISIBLE);
		ivToolbarHome.setVisibility(View.VISIBLE);
		btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/

		ivVCfrontImg = findViewById(R.id.ivVCfrontImg);
		ivDeleteVCfrontImg = findViewById(R.id.ivDeleteVCfrontImg);
		ivVCbackImg = findViewById(R.id.ivVCbackImg);
		ivDeleteVCbackImg = findViewById(R.id.ivDeleteVCbackImg);
		etName = findViewById(R.id.etName);
		etRelationship = findViewById(R.id.etRelationship);
		btnSaveCard = findViewById(R.id.btnSaveCard);
		btnNotNow = findViewById(R.id.btnNotNow);
		spCardType = findViewById(R.id.spCardType);
		spPurpose = findViewById(R.id.spPurpose);
		btnRetakeFrontImg = findViewById(R.id.btnRetakeFrontImg);
		btnRetakeBackImg = findViewById(R.id.btnRetakeBackImg);
		etAdditionalTypeOfCard = findViewById(R.id.etAdditionalTypeOfCard);
		layAddiTypeCont = findViewById(R.id.layAddiTypeCont);

		SubscriptionPlanBean planBean = sharedPrefsHelper.getSubscriptionPlan();
		if(planBean != null){
			if(planBean.pkg_mode.equalsIgnoreCase("Self")){
				purposeArray = new String[]{"For Personal Use"};
			}else if(planBean.pkg_mode.equalsIgnoreCase("Family")){
				purposeArray = new String[]{"For Personal Use", "For Family"};
			}
		}

		ArrayAdapter<String> spPurposeAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, purposeArray);
		spPurpose.setAdapter(spPurposeAdapter);

		String covacard_cardtypes = sharedPrefsHelper.get("covacard_cardtypes", "");
		cardTypesList = new ArrayList<>();
		if(! TextUtils.isEmpty(covacard_cardtypes)){
			cardTypesList = gson.fromJson(covacard_cardtypes, new TypeToken<ArrayList<String>>() {}.getType());
		}
		System.out.println("-- list size : "+cardTypesList.size());
		ArrayAdapter<String> spCardTypeAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, cardTypesList);
		spCardType.setAdapter(spCardTypeAdapter);

		spCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int vis = cardTypesList.get(position).equalsIgnoreCase("Other") ? View.VISIBLE : View.GONE;
				layAddiTypeCont.setVisibility(vis);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					etName.setText(prefs.getString("first_name", "") + " "+prefs.getString("last_name", ""));
					etName.setInputType(InputType.TYPE_NULL);
					etName.setFocusable(false);
					etName.setFocusableInTouchMode(false);
					etName.setClickable(false);
					etName.setLongClickable(false);
					etName.setEnabled(false);


					etRelationship.setText("Self");
					etRelationship.setInputType(InputType.TYPE_NULL);
					etRelationship.setFocusable(false);
					etRelationship.setFocusableInTouchMode(false);
					etRelationship.setClickable(false);
					etRelationship.setLongClickable(false);
					etRelationship.setEnabled(false);
				}else {
					etName.setText("");
					etName.setFocusable(true);
					etName.setFocusableInTouchMode(true);
					etName.setClickable(true);
					etName.setLongClickable(true);
					etName.setEnabled(true);
					etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

					etRelationship.setText("");
					etRelationship.setFocusable(true);
					etRelationship.setFocusableInTouchMode(true);
					etRelationship.setClickable(true);
					etRelationship.setLongClickable(true);
					etRelationship.setEnabled(true);
					etRelationship.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.ivVCfrontImg:
					callPicCardImgMethod(1, ActivityAddCard.this);
					break;
				case R.id.ivDeleteVCfrontImg:
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle("Confirm")
							.setMessage("Are you sure? Do you want to delete this file")
							.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									vacCardFrontImgPath = "";
									ivDeleteVCfrontImg.setVisibility(View.GONE);
									btnRetakeFrontImg.setVisibility(View.GONE);
									ivVCfrontImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
									ivVCfrontImg.setScaleType(ImageView.ScaleType.CENTER);
								}
							})
							.setNegativeButton("Not Now",null)
							.create().show();
					break;
				case R.id.ivVCbackImg:
					callPicCardImgMethod(2, ActivityAddCard.this);
					break;
				case R.id.ivDeleteVCbackImg:
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle("Confirm")
							.setMessage("Are you sure? Do you want to delete this file")
							.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									vacCardBackImgPath = "";
									ivDeleteVCbackImg.setVisibility(View.GONE);
									btnRetakeBackImg.setVisibility(View.GONE);
									ivVCbackImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
									ivVCbackImg.setScaleType(ImageView.ScaleType.CENTER);
								}
							})
							.setNegativeButton("Not Now",null)
							.create().show();
					break;


				case R.id.btnSaveCard:

					String name = etName.getText().toString().trim();
					String relation = etRelationship.getText().toString().trim();
					String card_type = cardTypesList.get(spCardType.getSelectedItemPosition());
					String card_for = purposeArray[spPurpose.getSelectedItemPosition()];
					String additional_card_type = etAdditionalTypeOfCard.getText().toString().trim();

					boolean validate = true;
					if(TextUtils.isEmpty(name)){etName.setError("Required");validate =false;}
					if(TextUtils.isEmpty(relation)){etRelationship.setError("Required");validate =false;}
					if(TextUtils.isEmpty(vacCardFrontImgPath)){customToast.showToast("Please add front picture of your health card",0,0);validate =false;}
					if(TextUtils.isEmpty(vacCardBackImgPath)){customToast.showToast("Please add back picture of your health card",0,0);validate =false;}

					if(validate){
						RequestParams params = new RequestParams();
						params.put("patient_id",prefs.getString("id", ""));
						//params.put("id",prefs.getString("subPatientID", "0"));
						params.put("name", name);
						params.put("relation", relation);
						params.put("card_type", card_type);
						params.put("card_for", card_for);
						if(layAddiTypeCont.getVisibility() == View.VISIBLE){
							params.put("additional_card_type", additional_card_type);
						}

						try {
							params.put("front_pic", new File(vacCardFrontImgPath));
							params.put("back_pic", new File(vacCardBackImgPath));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						ApiManager apiManager = new ApiManager(ApiManager.SAVE_CARD,"post",params,apiCallBack, activity);
						apiManager.loadURL();
					}

					break;

				case R.id.btnNotNow:
					finish();
					break;
				case R.id.btnRetakeFrontImg:
					ivVCfrontImg.performClick();
					break;
				case R.id.btnRetakeBackImg:
					ivVCbackImg.performClick();
					break;

				default:
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Comming soon")
							.setMessage("This feature will be available in the near future")
							.setPositiveButton("Done",null).create();
					alertDialog.show();
					break;
			}
		};
		ivVCfrontImg.setOnClickListener(onClickListener);
		ivDeleteVCfrontImg.setOnClickListener(onClickListener);
		ivVCbackImg.setOnClickListener(onClickListener);
		ivDeleteVCbackImg.setOnClickListener(onClickListener);
		btnSaveCard.setOnClickListener(onClickListener);
		btnNotNow.setOnClickListener(onClickListener);
		btnRetakeFrontImg.setOnClickListener(onClickListener);
		btnRetakeBackImg.setOnClickListener(onClickListener);


		super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
	}



	private String vacCardFrontImgPath = "", vacCardBackImgPath = "";
	//idCardFrontImgPath = "", idCardBackImgPath = "";

	@Override
	public void displayICfrontImg(String imgPath) {
		try {
			vacCardFrontImgPath = imgPath;
			ivDeleteVCfrontImg.setVisibility(View.VISIBLE);
			btnRetakeFrontImg.setVisibility(View.VISIBLE);

			File imageFile = new File(vacCardFrontImgPath);
			String uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", imageFile).toString();
			final String decoded = Uri.decode(uri);
			DATA.loadImageFromURL(decoded,R.drawable.ic_placeholder_2, ivVCfrontImg);
			ivVCfrontImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void displayICbackImg(String imgPath) {
		try {
			vacCardBackImgPath = imgPath;
			ivDeleteVCbackImg.setVisibility(View.VISIBLE);
			btnRetakeBackImg.setVisibility(View.VISIBLE);

			File imageFile = new File(vacCardBackImgPath);
			String uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", imageFile).toString();
			final String decoded = Uri.decode(uri);
			DATA.loadImageFromURL(decoded,R.drawable.ic_placeholder_2, ivVCbackImg);
			ivVCbackImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}catch (Exception e){
			e.printStackTrace();
		}
	}



	@Override
	public void displayIDcardFrontImg(String imgPath) {

	}

	@Override
	public void displayIDcardBackImg(String imgPath) {

	}






	//pick Insurance card image front + back on add new insurance
	LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
	public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
		liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
		liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(liveCareInsuranceCardhelper != null){
			//Insurance card image
			liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		// {"success":1,"message":"Saved","reload":1,"id":1}

		if (apiName.equalsIgnoreCase(ApiManager.SAVE_CARD)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.has("success")){
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
									.setTitle("Info")
									.setMessage("Your health card has been saved successfully")
									.setPositiveButton("Done", null)
									.create();
					alertDialog.setOnDismissListener(dialog -> {
						ActivityCardsList.shoulRefresh = true;
						finish();
					});
					alertDialog.show();
				}else {
					//{"error":1,"status":"error","message":"Please attach front card picture"}
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle("Error")
							.setMessage(jsonObject.optString("message", DATA.CMN_ERR_MSG))
							.setPositiveButton("Done", null)
							.create().show();
				}


			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
