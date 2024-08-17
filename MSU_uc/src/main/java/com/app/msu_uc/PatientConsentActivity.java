package com.app.msu_uc;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.app.msu_uc.api.ApiManager;
import com.app.msu_uc.util.CheckInternetConnection;
import com.app.msu_uc.util.ChoosePictureDialog;
import com.app.msu_uc.util.CustomToast;
import com.app.msu_uc.util.DATA;
import com.app.msu_uc.util.DatePickerFragment;
import com.app.msu_uc.util.GloabalMethods;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;

public class PatientConsentActivity extends AppCompatActivity {
	
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	ProgressDialog pd;
	CustomToast customToast;

	EditText etFullName,etBloodGroup,etInsurance,etInsuranceGroup,etSpecialmedicalproblems,
	etLasttetanusshot,etMedicationallergies,etNatureoftheproblem,etMedicalprocedure,etDoctor,
	etDoctordate,etSigndate;
	RadioGroup rgHistoryofasthama,rgHistoryofunconsciousness,rgHistoryofheart;
	RadioButton radioHistoryofasthamaYes,radioHistoryofasthamaNo,radioHistoryofunconsciousnessYes,
	radioHistoryofunconsciousnessNo,radioHistoryofheartYes,radioHistoryofheartNo;
	ImageView ivDoctorSignature,ivNotaryDocument;
	Button btnSubmitForm,btnSkipForm;

	@Override
	protected void onResume() {
		if(DATA.isImageCaptured) {
			DATA.isImageCaptured = false;
			if (!DATA.imagePath.isEmpty()) {
				ivNotaryDocument.setImageBitmap(BitmapFactory.decodeFile(DATA.imagePath));
			}
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_consent);
		
		activity = PatientConsentActivity.this;
		customToast = new CustomToast(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}
		pd.setMessage("Submitting...");
		pd.setCanceledOnTouchOutside(false);
		
		etFullName = (EditText) findViewById(R.id.etFullName);
		etBloodGroup = (EditText) findViewById(R.id.etBloodGroup);
		etInsurance = (EditText) findViewById(R.id.etInsurance);
		etInsuranceGroup = (EditText) findViewById(R.id.etInsuranceGroup);
		etSpecialmedicalproblems = (EditText) findViewById(R.id.etSpecialmedicalproblems);
		etLasttetanusshot = (EditText) findViewById(R.id.etLasttetanusshot);
		etMedicationallergies = (EditText) findViewById(R.id.etMedicationallergies);
		etNatureoftheproblem = (EditText) findViewById(R.id.etNatureoftheproblem);
		etMedicalprocedure = (EditText) findViewById(R.id.etMedicalprocedure);
		etDoctor = (EditText) findViewById(R.id.etDoctor);
		etDoctordate = (EditText) findViewById(R.id.etDoctordate);
		etSigndate = (EditText) findViewById(R.id.etSigndate);
		
		rgHistoryofasthama = (RadioGroup) findViewById(R.id.rgHistoryofasthama);
		rgHistoryofunconsciousness = (RadioGroup) findViewById(R.id.rgHistoryofunconsciousness);
		rgHistoryofheart = (RadioGroup) findViewById(R.id.rgHistoryofheart);
		
		radioHistoryofasthamaYes = (RadioButton) findViewById(R.id.radioHistoryofasthamaYes);
		radioHistoryofasthamaNo = (RadioButton) findViewById(R.id.radioHistoryofasthamaNo);
		radioHistoryofunconsciousnessYes = (RadioButton) findViewById(R.id.radioHistoryofunconsciousnessYes);
		radioHistoryofunconsciousnessNo = (RadioButton) findViewById(R.id.radioHistoryofunconsciousnessNo);
		radioHistoryofheartYes = (RadioButton) findViewById(R.id.radioHistoryofheartYes);
		radioHistoryofheartNo = (RadioButton) findViewById(R.id.radioHistoryofheartNo);
		
		btnSubmitForm = (Button) findViewById(R.id.btnSubmitForm);
		btnSkipForm = (Button) findViewById(R.id.btnSkipForm);
		
		ivDoctorSignature = (ImageView) findViewById(R.id.ivDoctorSignature);
		ivNotaryDocument = (ImageView) findViewById(R.id.ivNotaryDocument);
		
		
		etFullName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
		
		etLasttetanusshot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etLasttetanusshot);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		etDoctordate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etDoctordate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		etSigndate.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		DialogFragment newFragment = new DatePickerFragment(etSigndate);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
		});
		ivDoctorSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initSignatureDialog(ivDoctorSignature);
			}
		});

		ivNotaryDocument.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity, ChoosePictureDialog.class));
			}
		});
		
		btnSubmitForm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					saveFormData();
				} else {
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		btnSkipForm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				prefs.edit().putBoolean("isConcentFilled", true).commit();
				finish();
			}
		});
		
		
		if (checkInternetConnection.isConnectedToInternet()) {
			getFormData();
		} else {
			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
		}
	}//oncreate

	
	
	
	
	Dialog signatureDialog;
	SignaturePad mSignaturePad;
	Button mClearButton;
	Button mSaveButton;
	public void initSignatureDialog(final ImageView iv) {
		signatureDialog = new Dialog(activity);
		signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		signatureDialog.setContentView(R.layout.dialog_signature);

		mSignaturePad = (SignaturePad) signatureDialog.findViewById(R.id.signature_pad);
		mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
			@Override
			public void onStartSigning() {
				Toast.makeText(activity, "OnStartSigning", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSigned() {
				mSaveButton.setEnabled(true);
				mClearButton.setEnabled(true);
			}

			@Override
			public void onClear() {
				mSaveButton.setEnabled(false);
				mClearButton.setEnabled(false);
			}
		});

		mClearButton = (Button) signatureDialog.findViewById(R.id.clear_button);
		mSaveButton = (Button) signatureDialog.findViewById(R.id.save_button);

		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSignaturePad.clear();
			}
		});

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
				String signPath = addSignatureToGallery(signatureBitmap);
				if(signPath != null) {
					Toast.makeText(activity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
					//signaturePath = signPath;
					signatureDialog.dismiss();
					//ivSignature.setScaleType(ScaleType.CENTER_CROP);
					iv.setImageBitmap(BitmapFactory.decodeFile(signPath));
					iv.setTag(signPath);
				} else {
					Toast.makeText(activity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
				}
			}
		});
		signatureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		signatureDialog.show();
	}//end init

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs()) {
			Log.e("SignaturePad", "Directory not created");
		}
		return file;
	}

	public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, null);
		OutputStream stream = new FileOutputStream(photo);
		newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		stream.close();
	}
	public String addSignatureToGallery(Bitmap signature) {
		//boolean result = false;
		try {
			File photo = new File(getAlbumStorageDir("Online Care"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
			saveBitmapToJPG(signature, photo);
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri contentUri = Uri.fromFile(photo);
			mediaScanIntent.setData(contentUri);
			activity.sendBroadcast(mediaScanIntent);
			//result = true;
			return photo.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	
	public void saveFormData() {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);*/
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));
		params.put("blood_group", etBloodGroup.getText().toString());
		params.put("insurance", etInsurance.getText().toString());
		params.put("insurance_group", etInsuranceGroup.getText().toString());
		params.put("special_medical_issues", etSpecialmedicalproblems.getText().toString());
		params.put("last_tetanus_shot", etLasttetanusshot.getText().toString());
		params.put("medication_allergies", etMedicationallergies.getText().toString());
		
		/*params.put("asthma", "0/1");
		params.put("unconsciousness", "0/1");
		params.put("heart_problem", "0/1");*/
		
		int rgAsthamaValue = rgHistoryofasthama.getCheckedRadioButtonId();
		if (rgAsthamaValue == R.id.radioHistoryofasthamaYes) {
			params.put("asthma", "1");
		} else {
			params.put("asthma", "0");
		}
		int rgUnconsciousnessValue = rgHistoryofunconsciousness.getCheckedRadioButtonId();
		if (rgUnconsciousnessValue == R.id.radioHistoryofunconsciousnessYes) {
			params.put("unconsciousness", "1");
		} else {
			params.put("unconsciousness", "0");
		}
		int rgheart_problemValue = rgHistoryofheart.getCheckedRadioButtonId();
		if (rgheart_problemValue == R.id.radioHistoryofheartYes) {
			params.put("heart_problem", "1");
		} else {
			params.put("heart_problem", "0");
		}

		params.put("nature_of_problem", etNatureoftheproblem.getText().toString());
		params.put("medical_procedure", etMedicalprocedure.getText().toString());
		params.put("doctor", etDoctor.getText().toString());
		params.put("doctor_date", etDoctordate.getText().toString());
		params.put("sign_date", etSigndate.getText().toString());

		try {
		
			params.put("sign",  new File(ivDoctorSignature.getTag().toString()));
			params.put("document",new File(DATA.imagePath));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			params.put("sign",  ivDoctorSignature.getTag().toString());
			params.put("document",ivNotaryDocument.getTag().toString());
		}


		client.post(DATA.baseUrl+"/patientConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);
					DATA.print("--reaponce in patientConsent "+content);
					//06-09 16:59:14.591: I/System.out(18945): --reaponce in saveFormData {"success":1,"message":"Saved."}
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(content);

						if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {

							//prefs.edit().putBoolean("isMedPermissionFormFilled", true).commit();
							prefs.edit().putBoolean("isConcentFilled", true).commit();
							Toast.makeText(activity, "You information saved successfully", Toast.LENGTH_LONG).show();
							//startActivity(new Intent(activity, MainActivityNew.class));
							finish();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}

					} catch (JSONException e) {
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: patientConsent, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- patientConsent on fail "+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end signup
	
	
	public void getFormData() {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);*/
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));

		client.post(DATA.baseUrl+"/getPatientConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getPatientConsent "+content);
					try {
						JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

						String id = jsonObject.getString("id");
						String patient_id = jsonObject.getString("patient_id");
						String blood_group = jsonObject.getString("blood_group");
						String insurance = jsonObject.getString("insurance");
						String insurance_group = jsonObject.getString("insurance_group");
						String special_medical_issues = jsonObject.getString("special_medical_issues");
						String  last_tetanus_shot = jsonObject.getString("last_tetanus_shot");
						String  medication_allergies = jsonObject.getString("medication_allergies");
						String asthma = jsonObject.getString("asthma");
						String unconsciousness = jsonObject.getString("unconsciousness");
						String heart_problem = jsonObject.getString("heart_problem");
						String nature_of_problem = jsonObject.getString("nature_of_problem");
						String medical_procedure = jsonObject.getString("medical_procedure");
						String doctor = jsonObject.getString("doctor");
						String doctor_date = jsonObject.getString("doctor_date");
						String sign_date = jsonObject.getString("sign_date");
						String sign = jsonObject.getString("sign");
						String document = jsonObject.getString("document");

						//etFullName.setText("");
						etBloodGroup.setText(blood_group);
						etInsurance.setText(insurance);
						etInsuranceGroup.setText(insurance_group);
						etSpecialmedicalproblems.setText(special_medical_issues);
						etLasttetanusshot.setText(last_tetanus_shot);
						etMedicationallergies.setText(medication_allergies);
						etNatureoftheproblem.setText(nature_of_problem);
						etMedicalprocedure.setText(medical_procedure);
						etDoctor.setText(doctor);
						etDoctordate.setText(doctor_date);
						etSigndate.setText(sign_date);

						DATA.loadImageFromURL(sign, R.drawable.ic_signature, ivDoctorSignature);
						ivDoctorSignature.setTag(sign);

						DATA.loadImageFromURL(document, R.drawable.icon_document, ivNotaryDocument);
						ivNotaryDocument.setTag(document);

						if (asthma.equalsIgnoreCase("1")) {
							radioHistoryofasthamaYes.setChecked(true);
						} else {
							radioHistoryofasthamaNo.setChecked(true);
						}

						if (unconsciousness.equalsIgnoreCase("1")) {
							radioHistoryofunconsciousnessYes.setChecked(true);
						} else {
							radioHistoryofunconsciousnessNo.setChecked(true);
						}

						if (heart_problem.equalsIgnoreCase("1")) {
							radioHistoryofheartYes.setChecked(true);
						} else {
							radioHistoryofheartNo.setChecked(true);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getPatientConsent, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- getFormData on fail "+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getFormData
}
