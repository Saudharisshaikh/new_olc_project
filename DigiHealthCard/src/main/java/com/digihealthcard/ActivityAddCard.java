package com.digihealthcard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.digihealthcard.permission.PermissionsChecker;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.digihealthcard.R;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.model.SubscriptionPlanBean;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.LiveCareInsuranceCardhelper;
import com.digihealthcard.util.LiveCareInsuranceInterface;

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

		tvToolbarTitle.setText("Scan Card");
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

		ArrayAdapter<String> spPurposeAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, purposeArray);
		spPurpose.setAdapter(spPurposeAdapter);

		String covacard_cardtypes = sharedPrefsHelper.get("covacard_cardtypes", "");
		cardTypesList = new ArrayList<>();
		if(! TextUtils.isEmpty(covacard_cardtypes)){
			cardTypesList = gson.fromJson(covacard_cardtypes, new TypeToken<ArrayList<String>>() {}.getType());
		}
		System.out.println("-- list size : "+cardTypesList.size());
		ArrayAdapter<String> spCardTypeAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, cardTypesList);
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
					if (checkPermission()) {
						callPicCardImgMethod(1, ActivityAddCard.this); }
					else { requestPermissions(); }
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
					if (checkPermission()) {
						callPicCardImgMethod(2, ActivityAddCard.this);}
					else { requestPermissions(); }
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

	static final int REQUEST_SELECT_FROM_GALLERY13 = 13;


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



	/*============ Check Permission Code ===================*/
	private String[] PERMISSIONS = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	public static final String[] PERMISSIONSANDROID13 = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_MEDIA_IMAGES,
			Manifest.permission.READ_MEDIA_VIDEO,};

	private static final int PERMISSION_REQ_CODE = 1 << 4;
	private boolean checkPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			boolean granted = true;
			for (String per : PERMISSIONSANDROID13) {
				if (!permissionGranted(per)) {
					granted = false;
					break;
				}
			}
			if (granted) {

			} else {
				requestPermissions();
			}
			return granted;
		}else {
			boolean granted = true;
			for (String per : PERMISSIONS) {
				if (!permissionGranted(per)) {
					granted = false;
					break;
				}
			}
			if (granted) {

			} else {
				requestPermissions();
			}
			return granted;
		}
	}

	private boolean permissionGranted(String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestPermissions() {
		ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQ_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQ_CODE) {
			boolean granted = true;
			for (int result : grantResults) {
				granted = (result == PackageManager.PERMISSION_GRANTED);
				if (!granted) break;
			}

			if (granted) {

			} else {
				showDeniedResponse(grantResults);
				// customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
			}
		}
	}


	private void showDeniedResponse(int[] grantResults) {

		boolean shouldShowDialog = false;
		String msg = getString(R.string.allow_camera_permission_msg);

		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				//Toast.makeText(activity, "Permission not granted for: "+permissionFeatures.values()[i], Toast.LENGTH_SHORT).show();
				//msg = msg + "\n* "+permissionFeatures.values()[i];
				shouldShowDialog = true;
			}
		}


		if(shouldShowDialog){
			new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
					//.setTitle("Permission ")
					.setMessage(msg)
					.setNegativeButton("Allow Permissions", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							openAppSettings();
							/*checkPermissions();*/
						}
					})
					.setPositiveButton("Not Now", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					})
					.create().show();
		}
	}

	private void openAppSettings(){
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);
		startActivity(intent);
	}
	/*============ Check Permission Code ===================*/
}
