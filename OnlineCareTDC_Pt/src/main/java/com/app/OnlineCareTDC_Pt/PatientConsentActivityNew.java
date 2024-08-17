package com.app.OnlineCareTDC_Pt;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Pt.util.CustomToast;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.DatePickerFragment;
import com.app.OnlineCareTDC_Pt.util.GloabalMethods;
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

public class PatientConsentActivityNew extends AppCompatActivity {

	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;

	EditText etPatientName,etRelationship,etSignatureDate;
	ImageView ivSignature;
	Button btnSubmitForm,btnSkipForm;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_consent_new);

		activity = PatientConsentActivityNew.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);

		etPatientName = (EditText) findViewById(R.id.etPatientName);
		etRelationship = (EditText) findViewById(R.id.etRelationship);
		etSignatureDate = (EditText) findViewById(R.id.etSignatureDate);
		btnSubmitForm = (Button) findViewById(R.id.btnSubmitForm);
		btnSkipForm = (Button) findViewById(R.id.btnSkipForm);
		ivSignature = (ImageView) findViewById(R.id.ivSignature);

		etPatientName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));

		etSignatureDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etSignatureDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		ivSignature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initSignatureDialog(ivSignature);
			}
		});

		btnSubmitForm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					saveFormData();
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
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
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
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

		mClearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSignaturePad.clear();
			}
		});

		mSaveButton.setOnClickListener(new OnClickListener() {
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
		DATA.showLoaderDefault(activity,"");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));
		params.put("sign_date", etSignatureDate.getText().toString());
		params.put("relationship", etRelationship.getText().toString());
		if(ivSignature.getTag() != null){
			try {
				params.put("sign",  new File(ivSignature.getTag().toString()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				//params.put("sign",  ivSignature.getTag().toString());
			}
		}

		DATA.print("-- params in patientConsent: "+params.toString());

		client.post(DATA.baseUrl+"patientConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveFormData: patientConsent: "+content);
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
					DATA.print("-- responce onsuccess: saveFormData, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- saveFormData on fail "+content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end signup
	
	
	public void getFormData() {

		DATA.showLoaderDefault(activity,"");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", "0"));

		client.post(DATA.baseUrl+"/getPatientConsent", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
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
						String relationship = jsonObject.getString("relationship");

						etSignatureDate.setText(sign_date);
						etRelationship.setText(relationship);

						DATA.loadImageFromURL(sign, R.drawable.ic_signature, ivSignature);
						//ivSignature.setTag(sign);
						ivSignature.setTag(null);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getFormData, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- getFormData on fail "+content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getFormData
}
