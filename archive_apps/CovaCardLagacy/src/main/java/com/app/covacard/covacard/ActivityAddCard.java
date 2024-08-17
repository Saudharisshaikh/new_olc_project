package com.app.covacard.covacard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.app.covacard.BaseActivity;
import com.app.covacard.R;
import com.app.covacard.api.ApiManager;
import com.app.covacard.util.DATA;
import com.app.covacard.util.LiveCareInsuranceCardhelper;
import com.app.covacard.util.LiveCareInsuranceInterface;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class ActivityAddCard extends BaseActivity implements LiveCareInsuranceInterface {


	ImageView ivVCfrontImg,ivDeleteVCfrontImg,ivVCbackImg,ivDeleteVCbackImg;
	EditText etName,etRelationship;
	Button btnSaveCard,btnNotNow;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_covacard_add);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Scan Card");
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		ivVCfrontImg = findViewById(R.id.ivVCfrontImg);
		ivDeleteVCfrontImg = findViewById(R.id.ivDeleteVCfrontImg);
		ivVCbackImg = findViewById(R.id.ivVCbackImg);
		ivDeleteVCbackImg = findViewById(R.id.ivDeleteVCbackImg);
		etName = findViewById(R.id.etName);
		etRelationship = findViewById(R.id.etRelationship);
		btnSaveCard = findViewById(R.id.btnSaveCard);
		btnNotNow = findViewById(R.id.btnNotNow);


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

	}



	private String vacCardFrontImgPath = "", vacCardBackImgPath = "";
	//idCardFrontImgPath = "", idCardBackImgPath = "";

	@Override
	public void displayICfrontImg(String imgPath) {
		try {
			vacCardFrontImgPath = imgPath;
			ivDeleteVCfrontImg.setVisibility(View.VISIBLE);

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
									.setPositiveButton("Done", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									})
									.create();
					alertDialog.show();
				}


			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
