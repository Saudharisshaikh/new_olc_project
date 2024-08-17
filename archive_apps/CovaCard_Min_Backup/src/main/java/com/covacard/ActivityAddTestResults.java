package com.covacard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.covacard.api.ApiManager;
import com.covacard.util.DATA;
import com.covacard.util.LiveCareInsuranceCardhelper;
import com.covacard.util.LiveCareInsuranceInterface;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;

public class ActivityAddTestResults extends ActivityBaseDrawer {


	ImageView ivVCfrontImg,ivDeleteVCfrontImg;//,ivVCbackImg,ivDeleteVCbackImg;
	EditText etName,etRelationship;
	Button btnSaveCard,btnNotNow;
	Spinner spResult;
	LinearLayout layOther;
	EditText etOther;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_add_test_results);
		getLayoutInflater().inflate(R.layout.activity_add_test_results, container_frame);

		if(getSupportActionBar() != null){
			/*getSupportActionBar().setTitle("Add Test Results");
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
			getSupportActionBar().hide();
		}

		tvToolbarTitle.setText("Add Test Results");
		ivToolbarBack.setVisibility(View.VISIBLE);
		ivToolbarHome.setVisibility(View.VISIBLE);
		btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/

		ivVCfrontImg = findViewById(R.id.ivVCfrontImg);
		ivDeleteVCfrontImg = findViewById(R.id.ivDeleteVCfrontImg);
		//ivVCbackImg = findViewById(R.id.ivVCbackImg);
		//ivDeleteVCbackImg = findViewById(R.id.ivDeleteVCbackImg);
		etName = findViewById(R.id.etName);
		etRelationship = findViewById(R.id.etRelationship);
		btnSaveCard = findViewById(R.id.btnSaveCard);
		btnNotNow = findViewById(R.id.btnNotNow);
		spResult = findViewById(R.id.spResult);
		layOther = findViewById(R.id.layOther);
		etOther = findViewById(R.id.etOther);


		String[] resultArray  = {"Select Result", "Negative", "Psitive", "Other"};
		ArrayAdapter<String> spResultAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, resultArray);
		spResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spResult.setAdapter(spResultAdapter);

		spResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				layOther.setVisibility(resultArray[position].equalsIgnoreCase("Other") ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});


		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.ivVCfrontImg:
					//callPicCardImgMethod(1, ActivityAddTestResults.this);
					FilePickerBuilder.getInstance()
							.setMaxCount(1)
							.setSelectedFiles(photoPaths)
							.setActivityTheme(R.style.FilePickerTheme)
							.setActivityTitle("Please select a card picture")
							.enableVideoPicker(false)
							.enableCameraSupport(true)
							.showGifs(false)
							.showFolderView(false)
							.enableSelectAll(true)
							.enableImagePicker(true)
							.setCameraPlaceholder(R.drawable.custom_camera)
							.withOrientation(Orientation.PORTRAIT_ONLY)
							.pickPhoto(activity, DATA.CUSTOM_REQUEST_CODE);
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
				/*case R.id.ivVCbackImg:
					callPicCardImgMethod(2, ActivityAddTestResults.this);
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
					break;*/


				case R.id.btnSaveCard:

					String name = etName.getText().toString().trim();
					String relation = etRelationship.getText().toString().trim();
					String result = resultArray[spResult.getSelectedItemPosition()];

					if(layOther.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(etOther.getText().toString().trim())){
						result = result + " - "+etOther.getText().toString().trim();
					}

					boolean validate = true;
					if(TextUtils.isEmpty(name)){etName.setError("Required");validate =false;}
					if(TextUtils.isEmpty(relation)){etRelationship.setError("Required");validate =false;}
					if(TextUtils.isEmpty(vacCardFrontImgPath)){customToast.showToast("Please add a picture of your result.",0,0);validate =false;}
					if(spResult.getSelectedItemPosition() == 0){customToast.showToast("Please select the result",0,0);validate =false;}

					if(validate){
						RequestParams params = new RequestParams();
						params.put("patient_id",prefs.getString("id", ""));
						//params.put("id",prefs.getString("subPatientID", "0"));
						params.put("name", name);
						params.put("relation", relation);
						params.put("result", result);

						try {
							params.put("frontpic", new File(vacCardFrontImgPath));
							//params.put("back_pic", new File(vacCardFrontImgPath));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						ApiManager apiManager = new ApiManager(ApiManager.SAVE_RESULT,"post",params,apiCallBack, activity);
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
		//ivVCbackImg.setOnClickListener(onClickListener);
		//ivDeleteVCbackImg.setOnClickListener(onClickListener);
		btnSaveCard.setOnClickListener(onClickListener);
		btnNotNow.setOnClickListener(onClickListener);


		super.lockApp(sharedPrefsHelper.get("isAppLocked", false));

	}



	private String vacCardFrontImgPath = "";//, vacCardBackImgPath = "";
	//idCardFrontImgPath = "", idCardBackImgPath = "";






	//pick Insurance card image front + back on add new insurance
	LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
	public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
		liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
		liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(liveCareInsuranceCardhelper != null){
			//Insurance card image
			liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		// {"success":1,"message":"Saved","reload":1,"id":1}

		if (apiName.equalsIgnoreCase(ApiManager.SAVE_RESULT)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.has("success")){
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
									.setTitle("Info")
									.setMessage("Your test results has been saved successfully")
									.setPositiveButton("Done", null)
									.create();
					alertDialog.setOnDismissListener(dialog -> {
						ActivityTestResultsList.shoulRefresh = true;
						finish();
					});
					alertDialog.show();
				}else {
					//{"error":1,"message":"Please attach test picture"}
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




	private ArrayList<String> photoPaths = new ArrayList<>();
	private ArrayList<String> docPaths = new ArrayList<>();//not using doc yet
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		photoPaths = new ArrayList<>();
        docPaths = new ArrayList<>();
        switch (requestCode) {
            case DATA.CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }
        //addThemToView(photoPaths, docPaths);
        ArrayList<String> filePaths = new ArrayList<>();
        if (photoPaths != null) filePaths.addAll(photoPaths);
        if (docPaths != null) filePaths.addAll(docPaths);
        System.out.println("--total files: "+filePaths.size());
        if(!filePaths.isEmpty()){
            String filePath = filePaths.get(0);

            /*File rotatedImg = ImageRotator.Companion.prepareAndStoreImageFile(new File(filePath), new File(filePath));
			System.out.println("-- Image Rotated "+rotatedImg.getAbsolutePath());
            filePath = rotatedImg.getAbsolutePath();*/

			displayICfrontImg(filePath);

        }
		super.onActivityResult(requestCode, resultCode, data);
	}



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
}
