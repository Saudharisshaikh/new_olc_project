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
import android.widget.Toast;

import com.app.msu_uc.api.ApiManager;
import com.app.msu_uc.util.CheckInternetConnection;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MedicalPermissionForm extends AppCompatActivity {

	Activity activity;
	SharedPreferences prefs;
	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	ProgressDialog pd;

	ImageView ivMotherSignature,ivFatherSignature,ivSignOfNotary;
	EditText etNameofmother,etTelNoofMother,etNameoffather,etTelNoofFather,etMotherSignatureDate,etFatherSignatureDate,
	etNameOfNotary,etTreatmentDate,etteacher_or_babysitter_name;//etNameofchild,etAgeofchild,
	Button btnSubmitForm,btnSkipForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medical_permission_form);

		initUI();
		
		Date date = new Date();
		String fDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
		etMotherSignatureDate.setText(fDate);
		etFatherSignatureDate.setText(fDate);

		ivMotherSignature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initSignatureDialog(ivMotherSignature);
			}
		});

		ivFatherSignature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initSignatureDialog(ivFatherSignature);
			}
		});

		ivSignOfNotary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initSignatureDialog(ivSignOfNotary);
			}
		});
		
		etTreatmentDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etTreatmentDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
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
				prefs.edit().putBoolean("isMedPermissionFormFilled", true).commit();
				startActivity(new Intent(activity, MainActivityNew.class));
				finish();
			}
		});
		
		if (checkInternetConnection.isConnectedToInternet()) {
			getFormData();
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
		}

	}

	public void initUI() {
		activity = MedicalPermissionForm.this;
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

		ivMotherSignature = (ImageView) findViewById(R.id.ivMotherSignature);
		ivFatherSignature = (ImageView) findViewById(R.id.ivFatherSignature);
		ivSignOfNotary = (ImageView) findViewById(R.id.ivSignOfNotary);

		//etNameofchild = (EditText) findViewById(R.id.etNameofchild);
		//etAgeofchild = (EditText) findViewById(R.id.etAgeofchild);
		etNameofmother = (EditText) findViewById(R.id.etNameofmother);
		etTelNoofMother = (EditText) findViewById(R.id.etTelNoofMother);
		etNameoffather = (EditText) findViewById(R.id.etNameoffather);
		etTelNoofFather = (EditText) findViewById(R.id.etTelNoofFather);
		etMotherSignatureDate = (EditText) findViewById(R.id.etMotherSignatureDate);
		etFatherSignatureDate = (EditText) findViewById(R.id.etFatherSignatureDate);
		etNameOfNotary = (EditText) findViewById(R.id.etNameOfNotary);
		etTreatmentDate = (EditText) findViewById(R.id.etTreatmentDate);
		etteacher_or_babysitter_name = (EditText) findViewById(R.id.etteacher_or_babysitter_name);
		btnSubmitForm = (Button) findViewById(R.id.btnSubmitForm);
		btnSkipForm = (Button) findViewById(R.id.btnSkipForm);
		
	}

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
			MedicalPermissionForm.this.sendBroadcast(mediaScanIntent);
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
		params.put("name_of_mother", etNameofmother.getText().toString());
		params.put("mother_tel", etTelNoofMother.getText().toString());
		params.put("name_of_father", etNameoffather.getText().toString());
		params.put("father_tel", etTelNoofFather.getText().toString());
		params.put("mother_sig_date", etMotherSignatureDate.getText().toString());
		params.put("father_sig_date", etFatherSignatureDate.getText().toString());

		params.put("treatment_date", etTreatmentDate.getText().toString());
		params.put("teacher_or_babysitter_name", etteacher_or_babysitter_name.getText().toString());
		params.put("notary_name", etNameOfNotary.getText().toString());

		try {
			params.put("mother_sig", new File(ivMotherSignature.getTag().toString()));
			params.put("father_sig",  new File(ivFatherSignature.getTag().toString()));
			params.put("notary_sig",  new File(ivSignOfNotary.getTag().toString()));

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DATA.print("-- params in medicalPermission: "+params.toString());

		client.post(DATA.baseUrl+"/medicalPermission", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in medicalPermission "+content);
					//06-09 16:59:14.591: I/System.out(18945): --reaponce in saveFormData {"success":1,"message":"Saved."}
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(content);

						if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {

							prefs.edit().putBoolean("isMedPermissionFormFilled", true).commit();
							Toast.makeText(activity, "You information saved successfully", Toast.LENGTH_LONG).show();
							startActivity(new Intent(activity, MainActivityNew.class));
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
					DATA.print("-- responce onsuccess: medicalPermission, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- medicalPermission on fail "+content);
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
		


		client.post(DATA.baseUrl+"/getMedicalPermission", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getMedicalPermission "+content);

					try {
						JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
						String id = jsonObject.getString("id");
						String patient_id = jsonObject.getString("patient_id");
						String name_of_mother = jsonObject.getString("name_of_mother");
						String mother_tel = jsonObject.getString("mother_tel");
						String name_of_father = jsonObject.getString("name_of_father");
						String father_tel = jsonObject.getString("father_tel");
						String mother_sig = jsonObject.getString("mother_sig");
						String mother_sig_date = jsonObject.getString("mother_sig_date");
						String father_sig = jsonObject.getString("father_sig");
						String father_sig_date = jsonObject.getString("father_sig_date");
						String teacher_or_babysitter_name = jsonObject.getString("teacher_or_babysitter_name");
						String notary_name = jsonObject.getString("notary_name");
						String notary_sig = jsonObject.getString("notary_sig");
						String treatment_date = jsonObject.getString("treatment_date");


						//etNameofchild.setText(nam);
						//etAgeofchild.setText("");
						etNameofmother.setText(name_of_mother);
						etTelNoofMother.setText(mother_tel);
						etNameoffather.setText(name_of_father);
						etTelNoofFather.setText(father_tel);
						etMotherSignatureDate.setText(mother_sig_date);
						etFatherSignatureDate.setText(father_sig_date);
						etNameOfNotary.setText(notary_name);
						etTreatmentDate.setText(treatment_date);
						etteacher_or_babysitter_name.setText(teacher_or_babysitter_name);

						DATA.loadImageFromURL(mother_sig, R.drawable.ic_signature, ivMotherSignature);
						ivMotherSignature.setTag(mother_sig);

						DATA.loadImageFromURL(father_sig, R.drawable.ic_signature, ivFatherSignature);
						ivFatherSignature.setTag(father_sig);

						DATA.loadImageFromURL(notary_sig, R.drawable.ic_signature, ivSignOfNotary);
						ivSignOfNotary.setTag(notary_sig);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getMedicalPermission, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);

					DATA.print("-- getMedicalPermission on fail "+content);

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
