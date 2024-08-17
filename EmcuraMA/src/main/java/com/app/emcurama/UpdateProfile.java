package com.app.emcurama;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.StateBean;
import com.app.emcurama.util.ChoosePictureDialog;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.DatePickerFragment;
import com.app.emcurama.util.GloabalMethods;
import com.app.emcurama.util.UploadImageCall;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UpdateProfile extends BaseActivity {

	Activity activity;

	EditText etUpdateProfileFname,
			 etUpdateProfileLname,
			 etUpdateProfilBday,
			 etUpdateProfilePhone,
			 etUpdateProfileAddress,
			 etUpdateProfileCountry,
			 etUpdateProfileOccupation,
			 etUpdateProfileDrCity,
			 etUpdateProfileIntroduction,
			 etUpdateProfileQualification,
			 etUpdateProfileCareerData,
			 etUpdateProfileZipcode;
	//etUpdateProfileState,
	RadioGroup rgEditProfilGender,rgMaritalStatus;
	RadioButton radioMale,radioFemale;
	CheckBox cbAvailableForeLiveCare;
	String is_available_onlinecare = "0";
	ImageView imgChangeProfileImg;

	Spinner spUpdateProfileState;
	String userState = "";

	AsyncHttpClient client;	
	ProgressDialog pd;

	JSONObject jsonObject, user_info;

	String msg, status, gender = "1", maritalStatus;
	SharedPreferences prefs;
	ActionBar ab;
	
	Button btnUpdtPrflSbmt,btnUpdtPrflPwd;
	
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
		
		}
		else {
			DATA.loadImageFromURL( prefs.getString("image", ""), R.drawable.icon_call_screen , imgChangeProfileImg);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_profile);

		activity = UpdateProfile.this;

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("UPDATING PROFILE...");
		pd.setCanceledOnTouchOutside(false); 
		//pd.setTitle("Please wait..!");

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		client = new AsyncHttpClient();

		etUpdateProfileFname = (EditText) findViewById(R.id.etUpdateProfileFname);
		etUpdateProfileLname = (EditText) findViewById(R.id.etUpdateProfileLname);
		etUpdateProfilBday = (EditText) findViewById(R.id.etUpdateProfilBday);

		etUpdateProfilePhone = (EditText) findViewById(R.id.etUpdateProfilePhone);
		etUpdateProfileAddress = (EditText) findViewById(R.id.etUpdateProfileAddress);
		etUpdateProfileCountry = (EditText) findViewById(R.id.etUpdateProfileCountry);
		etUpdateProfileOccupation = (EditText) findViewById(R.id.etUpdateProfileOccupation);
		
		etUpdateProfileDrCity = (EditText) findViewById(R.id.etUpdateProfileDrCity);
		//etUpdateProfileState = (EditText) findViewById(etUpdateProfileState);
		etUpdateProfileIntroduction = (EditText) findViewById(R.id.etUpdateProfileIntroduction);
		etUpdateProfileQualification = (EditText) findViewById(R.id.etUpdateProfileQualification);
		etUpdateProfileCareerData = (EditText) findViewById(R.id.etUpdateProfileCareerData);
		
		etUpdateProfileZipcode = (EditText) findViewById(R.id.etUpdateProfileZipcode);

		imgChangeProfileImg  = (ImageView) findViewById(R.id.imgChangeProfileImg);

		etUpdateProfileFname.setText(prefs.getString("first_name", ""));
		etUpdateProfileLname.setText(prefs.getString("last_name", ""));
		etUpdateProfilBday.setText(prefs.getString("birthdate", ""));
		etUpdateProfileAddress.setText(prefs.getString("address", ""));
		etUpdateProfileCountry.setText(prefs.getString("country", ""));
		etUpdateProfileOccupation.setText(prefs.getString("designation", ""));
		etUpdateProfilePhone.setText(prefs.getString("mobile", ""));
		etUpdateProfileDrCity.setText(prefs.getString("city", ""));
		//etUpdateProfileState.setText(prefs.getString("state", ""));
		etUpdateProfileIntroduction.setText(prefs.getString("introduction", ""));
		etUpdateProfileZipcode.setText(prefs.getString("zipcode", ""));
		etUpdateProfileQualification.setText(prefs.getString("qualification", ""));
		etUpdateProfileCareerData.setText(prefs.getString("career_data", ""));
	
		btnUpdtPrflPwd = (Button) findViewById(R.id.btnUpdtPrflPwd);
		btnUpdtPrflSbmt = (Button) findViewById(R.id.btnUpdtPrflSbmt);
		
		rgEditProfilGender = (RadioGroup) findViewById(R.id.rgEditProfilGender);
		radioMale = (RadioButton) findViewById(R.id.radioMale);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);

		cbAvailableForeLiveCare = (CheckBox) findViewById(R.id.cbAvailableForeLiveCare);

		spUpdateProfileState = (Spinner) findViewById(R.id.spUpdateProfileState);

		final ArrayList<StateBean> stateBeans = DATA.loadStates(activity);
		ArrayAdapter<StateBean> spStateAdapter = new ArrayAdapter<StateBean>(
				activity,
				R.layout.spinner_item_lay2,
				stateBeans
		);
		spStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUpdateProfileState.setAdapter(spStateAdapter);

		for (int i = 0; i < stateBeans.size(); i++) {
			if (prefs.getString("state", "").equalsIgnoreCase(stateBeans.get(i).getAbbreviation())) {
				spUpdateProfileState.setSelection(i);
			}
		}

		spUpdateProfileState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
		
		etUpdateProfilBday.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus) {

					//DATA.isDatePickerCallFromSignup = false;
					
					DialogFragment newFragment = new DatePickerFragment(etUpdateProfilBday);
					newFragment.show(getSupportFragmentManager(), "datePicker");

				}
		
			}
		});
		etUpdateProfilBday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etUpdateProfilBday);
				newFragment.show(getSupportFragmentManager(), "datePicker");
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
		
		switch (Integer.parseInt(prefs.getString("gender", "1"))) {
		case 1:
			radioMale.setChecked(true);
			radioFemale.setChecked(false);
			break;
		case 0:
			radioMale.setChecked(false);
			radioFemale.setChecked(true);
			break;
		default:
			break;
		}

		is_available_onlinecare = prefs.getString("is_available_onlinecare","0");
		if(is_available_onlinecare.equals("1")){
			cbAvailableForeLiveCare.setChecked(true);
		}else {
			cbAvailableForeLiveCare.setChecked(false);
		}
		cbAvailableForeLiveCare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					is_available_onlinecare = "1";
				}else {
					is_available_onlinecare = "0";
				}
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
						|| etUpdateProfileOccupation.getText().toString().isEmpty()
						|| etUpdateProfilePhone.getText().toString().isEmpty()
						|| etUpdateProfileDrCity.getText().toString().isEmpty()
						|| userState.isEmpty()
						|| etUpdateProfileZipcode.getText().toString().length() < 5)
				{				
					Toast.makeText(activity, "Infomplete form", Toast.LENGTH_LONG).show();
				}
				else {

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
		
		
		etUpdateProfileCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).showContrySelectionDialog(etUpdateProfileCountry);
			}
		});
	}//end oncreate


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

		pd.show();

		ApiManager.addHeader(activity,client);
		
		RequestParams params = new RequestParams();
		params.put("id",prefs.getString("id", ""));
		params.put("first_name", etUpdateProfileFname.getText().toString());
		params.put("last_name", etUpdateProfileLname.getText().toString());
		params.put("address", etUpdateProfileAddress.getText().toString());
		params.put("country", etUpdateProfileCountry.getText().toString());
		params.put("phone", etUpdateProfilePhone.getText().toString());
		params.put("birthdate", etUpdateProfilBday.getText().toString());
		params.put("city", etUpdateProfileDrCity.getText().toString());
		params.put("state", userState);
		params.put("introduction", etUpdateProfileIntroduction.getText().toString());
		params.put("designation", etUpdateProfileOccupation.getText().toString());
		params.put("zipcode", etUpdateProfileZipcode.getText().toString());
		params.put("qualification", etUpdateProfileQualification.getText().toString());
		params.put("career_data", etUpdateProfileCareerData.getText().toString());
		params.put("gender", gender);
		params.put("type", "doctor");
		params.put("is_available_onlinecare",is_available_onlinecare);



		String reqURL = DATA.baseUrl+"updateProfile";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);


					DATA.print("--Response in updte profile: "+response);

					// remove the progress bar view
//				refreshMenuItem.setActionView(null);
					try {

						jsonObject = new JSONObject(content);

						status = jsonObject.getString("status");
						msg = jsonObject.getString("msg");

						if(status.equals("success"))
						{


							String userinfo = jsonObject.getString("user_info");

							user_info = new JSONObject(userinfo);

							//update preferences
							SharedPreferences.Editor ed = prefs.edit();
							ed.putString("first_name", user_info.getString("first_name"));
							ed.putString("last_name", user_info.getString("last_name"));
							ed.putString("country", user_info.getString("country"));
							//ed.putString("gender", user_info.getString("gender"));
							ed.putString("gender", user_info.getString("sex"));
							ed.putString("birthdate", user_info.getString("birthdate"));
							ed.putString("mobile", user_info.getString("mobile"));
							ed.putString("designation", user_info.getString("designation"));
							ed.putString("city", user_info.getString("city"));
							ed.putString("state", user_info.getString("state"));
							ed.putString("address", user_info.getString("address1"));

							ed.putString("introduction", user_info.getString("introduction"));

							ed.putString("zipcode", user_info.getString("zip_code"));

							ed.putString("qualification", user_info.getString("qualification"));
							ed.putString("career_data", user_info.getString("career_data"));
							ed.putString("is_available_onlinecare", user_info.getString("is_available_onlinecare"));

//						if(prefs.getString("DrOrPatient", "").equals("patient")) {
//							ed.putString("address", user_info.getString("residency"));
//
//						}
							//	else {


							//}
							ed.commit();

//						DATA.imagePath = "";
//						DATA.isImageCaptured = false;
							customToast.showToast("Your profile has been updated successfully",0,0);
							finish();

						} else {
							customToast.showToast(msg,0,0);
						}

						finish();


					} catch (JSONException e) {
						DATA.print("--Exception in login: "+e);
						e.printStackTrace();
						customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
					}

					//to remove after service becomes functioning

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	public void setResultsFromImageUpload() {

		// remove the progress bar view
		DATA.imagePath = "";
		DATA.isImageCaptured = false;
//		DATA.isFromUpdateProfile = false;
//		pd.dismiss();
		//UrlImageViewHelper.setUrlDrawable(imgChangeProfileImg, prefs.getString("image", ""), R.drawable.ic_launcher);

		DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, imgChangeProfileImg);
		
		customToast.showToast("Your profile picture has been updated successfully",0,0);
//		Intent intent = new Intent(activity,MainActivityNew.class);
//		startActivity(intent);
//		finish();
	}

	public boolean isConnectedToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
				for (int i = 0; i < info.length; i++) 
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
			int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inPurgeable = true;
		options.inDensity = 1;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		//Bitmap bmp = BitmapFactory.decodeFile(path, options);
		
	//	bmp = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, false);
		return BitmapFactory.decodeFile(path, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
		return inSampleSize;
	}


}