package com.app.msu_uc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.msu_uc.api.ApiCallBack;
import com.app.msu_uc.api.ApiManager;
import com.app.msu_uc.model.StateBean;
import com.app.msu_uc.util.CheckInternetConnection;
import com.app.msu_uc.util.ChoosePictureDialog;
import com.app.msu_uc.util.CustomToast;
import com.app.msu_uc.util.DATA;
import com.app.msu_uc.util.DatePickerFragment;
import com.app.msu_uc.util.GloabalMethods;
import com.app.msu_uc.util.OpenActivity;
import com.app.msu_uc.util.UploadImageCall;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateProfile extends BaseActivity{

	Activity activity;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	OpenActivity openActivity;
	SharedPreferences prefs;

	EditText etUpdateProfileFname, etUpdateProfileLname, etUpdateProfilBday, etUpdateProfilePhone, etUpdateProfileAddress,
			 etUpdateProfileCountry, etUpdateProfileZipcode, etUpdateProfileCity;//etUpdateProfileOccupation,
	
	Spinner spUpdateProfileState;//spinner is hidden in UI, for update state uncommit this
	String userState = "MI";
	EditText etUpdateProfileState;
	
	RadioGroup rgEditProfilGender,rgMaritalStatus;
	RadioButton radioMale,radioFemale,radioSingle,radioMarried,radioDivorced,radioWidowed;
	ImageView imgChangeProfileImg;

	Button btnUpdtPrflSbmt,btnUpdtPrflPwd,btnAddPrimaryCare , btnDeleteAccount;

	String gender, maritalStatus;

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	protected void onResume() {
		super.onResume();

		DATA.print("--online care update profile onResume");
		if(DATA.isImageCaptured) {
			DATA.print("--online care update profile DATA.isImageCaptured onResume");
			try {
				UploadImageCall upldImg = new UploadImageCall(activity);
				//upldImg.execute("");
				upldImg.executeUploadImg();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			DATA.print("-- image url "+prefs.getString("image", ""));
			//UrlImageViewHelper.setUrlDrawable(imgChangeProfileImg, prefs.getString("image", ""), R.drawable.icon_call_screen);
			DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, imgChangeProfileImg);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_profile);

		activity = UpdateProfile.this;
		apiCallBack = this;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, MODE_PRIVATE);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setTitle("Medical History");
		Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
		btnToolbar.setText("My Providers");
		btnToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity,ActivityMyProviders.class));
			}
		});

		etUpdateProfileFname = (EditText) findViewById(R.id.etUpdateProfileFname);
		etUpdateProfileLname = (EditText) findViewById(R.id.etUpdateProfileLname);
		etUpdateProfilBday = (EditText) findViewById(R.id.etUpdateProfilBday);

		etUpdateProfilePhone = (EditText) findViewById(R.id.etUpdateProfilePhone);
		etUpdateProfileAddress = (EditText) findViewById(R.id.etUpdateProfileAddress);
		etUpdateProfileCountry = (EditText) findViewById(R.id.etUpdateProfileCountry);
		//etUpdateProfileOccupation = (EditText) findViewById(R.id.etUpdateProfileOccupation);
		etUpdateProfileZipcode = (EditText) findViewById(R.id.etUpdateProfileZipcode);
		etUpdateProfileCity = (EditText) findViewById(R.id.etUpdateProfileCity);
		spUpdateProfileState = (Spinner) findViewById(R.id.spUpdateProfileState);



		etUpdateProfileState = findViewById(R.id.etUpdateProfileState);

		imgChangeProfileImg  = (ImageView) findViewById(R.id.imgChangeProfileImg);

		radioMale = (RadioButton) findViewById(R.id.radioMale);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);
		radioSingle = (RadioButton) findViewById(R.id.radioSingle);
		radioMarried = (RadioButton) findViewById(R.id.radioMarried);
		radioDivorced = (RadioButton) findViewById(R.id.radioDivorced);
		radioWidowed = (RadioButton) findViewById(R.id.radioWidowed);
		
		etUpdateProfileFname.setText(prefs.getString("first_name", ""));
		etUpdateProfileLname.setText(prefs.getString("last_name", ""));
		etUpdateProfilBday.setText(prefs.getString("birthdate", ""));
		etUpdateProfileAddress.setText(prefs.getString("address", ""));
		etUpdateProfileCountry.setText(prefs.getString("country", ""));
		//etUpdateProfileOccupation.setText(prefs.getString("occupation", ""));
		etUpdateProfileZipcode.setText(prefs.getString("zipcode", ""));
		etUpdateProfilePhone.setText(prefs.getString("phone", ""));
		etUpdateProfileCity.setText(prefs.getString("city", ""));
		//etUpdateProfileState.setText(prefs.getString("state", ""));


		//spUpdateProfileState.setVisibility(View.GONE);
		etUpdateProfileState.setVisibility(View.GONE);
		
		final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
		ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(
		        activity,
		        R.layout.spinner_item_lay,
		        stateBeans
		);
		spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUpdateProfileState.setAdapter(spStateAdapter);
		
		for (int i = 0; i < stateBeans.size(); i++) {
			if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) {
				spUpdateProfileState.setSelection(i);
			}
		}
		
		spUpdateProfileState.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				userState = stateBeans.get(pos).getAbbreviation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		if (prefs.getString("gender", "1").equals("0")) {
			radioFemale.setChecked(true);
		} else {
			radioMale.setChecked(true);
		}
		if (prefs.getString("marital_status", "0").equals("1")) {
			radioMarried.setChecked(true);
		} else if (prefs.getString("marital_status", "0").equals("0")) {
			radioSingle.setChecked(true);
		}else if (prefs.getString("marital_status", "0").equals("2")) {
			radioDivorced.setChecked(true);
		}else if (prefs.getString("marital_status", "0").equals("3")) {
			radioWidowed.setChecked(true);
		}
		btnUpdtPrflPwd = (Button) findViewById(R.id.btnUpdtPrflPwd);
		btnUpdtPrflSbmt = (Button) findViewById(R.id.btnUpdtPrflSbmt);
		btnAddPrimaryCare = (Button) findViewById(R.id.btnAddPrimaryCare);
		btnDeleteAccount = (Button) findViewById(R.id.btnDeleteAccount);

		Button btnAddInsurance = (Button) findViewById(R.id.btnAddInsurance);
		btnAddInsurance.setOnClickListener(arg0 -> {
            // TODO Auto-generated method stub
            //initInsuranceDialog();
            openActivity.open(ActivityInsurance.class, false);
        });

		btnDeleteAccount.setOnClickListener(view ->
		{
			openActivity.open(ActivityDeleteMyAccount.class , false);
		});
		
		rgEditProfilGender = (RadioGroup) findViewById(R.id.rgEditProfilGender);
		rgEditProfilGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.radioMale:
					
					gender = "1";
					
					break;
				case R.id.radioFemale:
					gender = "0";
					
					break;
				default:
					break;
				}
			}
		});

		rgMaritalStatus = (RadioGroup) findViewById(R.id.rgMaritalStatus);
		rgMaritalStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.radioSingle:

						maritalStatus = "0";
						break;

					case R.id.radioMarried:

						maritalStatus = "1";
						break;

					case R.id.radioDivorced:

						maritalStatus = "2";
						break;

					case R.id.radioWidowed:

						maritalStatus = "3";
						break;
					default:
						break;
				}
			}
		});

//		ab = getSupportActionBar(); 
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4b6162"));     
//		ab.setBackgroundDrawable(colorDrawable);
		

//		UrlImageViewHelper.setUrlDrawable(imgChangeProfileImg, prefs.getString("id", ""), R.drawable.dummy_user_big);
		
		etUpdateProfilBday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

					//DATA.isDatePickerCallFromSignup = false;
					
					DialogFragment newFragment = new DatePickerFragment(etUpdateProfilBday);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				
			}
		});

		etUpdateProfileCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).showContrySelectionDialog(etUpdateProfileCountry);
			}
		});


		imgChangeProfileImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,ChoosePictureDialog.class);
				intent.putExtra("isForProfilePic", true);
				startActivity(intent);
			}
		});

		Button btnEditPicture = (Button) findViewById(R.id.btnEditPicture);
		btnEditPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(activity,ChoosePictureDialog.class);
				intent.putExtra("isForProfilePic", true);
				startActivity(intent);
				
			}
		});
		
		btnUpdtPrflSbmt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {


				if(etUpdateProfileFname.getText().toString().isEmpty() 
						|| etUpdateProfileLname.getText().toString().isEmpty()
						|| etUpdateProfilBday.getText().toString().isEmpty()
						|| etUpdateProfileAddress.getText().toString().isEmpty()
						|| etUpdateProfileCountry.getText().toString().isEmpty()
						|| etUpdateProfileCity.getText().toString().isEmpty()
						|| userState.isEmpty()
						|| etUpdateProfilePhone.getText().toString().isEmpty()
						|| etUpdateProfileZipcode.getText().toString().length() < 5)
				{				
					Toast.makeText(activity, "Infomplete form", Toast.LENGTH_LONG).show();
				}
				else {
					if (radioMale.isChecked()) {
						gender = "1";
					} else {
						gender = "0";
					}
					if (radioSingle.isChecked()) {
						maritalStatus = "0";
					} else if (radioMarried.isChecked()){
						maritalStatus = "1";
					}else if (radioDivorced.isChecked()){
						maritalStatus = "2";
					}else if (radioWidowed.isChecked()){
						maritalStatus = "3";
					}
					updateProfileNoFinish();

				}

			}
		});
		
		
		btnUpdtPrflPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(UpdateProfile.this,UpdatePasswordActivity.class));
			}
		});

		
		btnAddPrimaryCare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//new GloabalMethods(activity).pimaryCareDialog();
				openActivity.open(ActivityPrimaryCareDoctors.class,false);
			}
		});
	}
	
	


//	@Override
//	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
//
//		if (item.getItemId() == android.R.id.home) {
//			NavUtils.navigateUpFromSameTask(this);
//			return true;
//		}
//
//		switch (item.getItemId()) {
//
//		case R.id.action_change_status:
//
//			refreshMenuItem = item;
//
//			if(isConnectedToInternet()) {
//
//				//Toast.makeText(activity, DATA.isImageCaptured+"", 0).show();
//
//				if(DATA.isImageCaptured) {
//					
//
//					UploadImageCall uploadImage = new UploadImageCall(activity);
//					uploadImage.execute("");
//
//					if(etUpdateProfileFname.getText().toString().isEmpty() || etUpdateProfileLname.getText().toString().isEmpty())
//					{				
//						Toast.makeText(activity, "Empty first name/last name", 0).show();
//					}
//					else {
//
//						updateProfileNoFinish();
//
//					}
//
//					refreshMenuItem.setActionView(R.layout.action_progressbar);
//					pd.show();
//
//				}
//				else {
//
//					if(etUpdateProfileFname.getText().toString().isEmpty() || etUpdateProfileLname.getText().toString().isEmpty())
//					{				
//						Toast.makeText(activity, "Empty first name/last name", 0).show();
//					}
//					else {
//
//						updateProfile();
//						refreshMenuItem.setActionView(R.layout.action_progressbar);
//						pd.show();
//
//					}
//
//				}
//			}
//			else {
//				Toast.makeText(activity, "Not connected to Internet", 0).show();
//			}
//
//
//
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

	
	public void updateProfileNoFinish() {

		RequestParams params = new RequestParams();
		params.put("id",prefs.getString("id", ""));
	//	params.put("id",prefs.getString("subPatientID", "0"));
		params.put("first_name", etUpdateProfileFname.getText().toString());
		params.put("last_name", etUpdateProfileLname.getText().toString());
		params.put("address", etUpdateProfileAddress.getText().toString());
		params.put("country", etUpdateProfileCountry.getText().toString());
		params.put("phone", etUpdateProfilePhone.getText().toString());
		params.put("city", etUpdateProfileCity.getText().toString());
		params.put("state", userState);
		params.put("zipcode", etUpdateProfileZipcode.getText().toString());
		params.put("birthdate", etUpdateProfilBday.getText().toString());
		params.put("marital_status", maritalStatus);
		params.put("gender", gender);
		params.put("type", "patient");

		ApiManager apiManager = new ApiManager(ApiManager.UPDATE_PROFILE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	public void setResultsFromImageUpload() {

		// remove the progress bar view
		DATA.imagePath = "";
		DATA.isImageCaptured = false;
//		DATA.isFromUpdateProfile = false;
//		pd.dismiss();
		//UrlImageViewHelper.setUrlDrawable(imgChangeProfileImg, prefs.getString("image", ""), R.drawable.ic_launcher);

		DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, imgChangeProfileImg);
		
		customToast.showToast("Updated Successfully",0,1);
//		Intent intent = new Intent(activity,MainActivityNew.class);
//		startActivity(intent);
//		finish();
	}




	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.UPDATE_PROFILE)){

			try {

				JSONObject jsonObject = new JSONObject(content);

				String status = jsonObject.getString("status");
				String msg = jsonObject.getString("msg");

				if(status.equals("success")) {

					String userinfo = jsonObject.getString("user_info");

					JSONObject user_info = new JSONObject(userinfo);

					//update preferences
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("first_name", user_info.getString("first_name"));
					ed.putString("last_name", user_info.getString("last_name"));
					ed.putString("country", user_info.getString("country"));
					ed.putString("gender", user_info.getString("gender"));
					ed.putString("birthdate", user_info.getString("birthdate"));
					ed.putString("phone", user_info.getString("phone"));
					//ed.putString("occupation", user_info.getString("occupation"));
					ed.putString("marital_status", user_info.getString("marital_status"));

					ed.putString("city", user_info.getString("city"));
					ed.putString("state", user_info.getString("state"));

					ed.putString("zipcode", user_info.getString("zipcode"));
					if(prefs.getString("DrOrPatient", "").equals("patient")) {
						ed.putString("address", user_info.getString("residency"));

					} else {

						//	ed.putString("address", user_info.getString("address1"));   not use
					}
					ed.commit();

					//to remove after service becomes functioning
					customToast.showToast("Updated Successfully",0,1);
					finish();

//						DATA.imagePath = "";
//						DATA.isImageCaptured = false;

				} else {
					customToast.showToast(msg,0,1);
				}

				finish();


			} catch (JSONException e) {
				DATA.print("--Exception in login: "+e);

				e.printStackTrace();
			}
		}
	}
}