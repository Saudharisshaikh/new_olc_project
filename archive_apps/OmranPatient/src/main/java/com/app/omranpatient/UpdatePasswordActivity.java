package com.app.omranpatient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.omranpatient.api.ApiCallBack;
import com.app.omranpatient.api.ApiManager;
import com.app.omranpatient.util.CheckInternetConnection;
import com.app.omranpatient.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePasswordActivity extends AppCompatActivity implements ApiCallBack{

	EditText etCurrentPass,etNewPass,etConfirmPass;
	Button btnUpdatePass;
	SharedPreferences prefs;
	CheckInternetConnection connection;
	AppCompatActivity activity;
	ApiCallBack apiCallBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_password);
		
		activity = UpdatePasswordActivity.this;
		apiCallBack = this;
		connection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		etCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
		etNewPass =(EditText) findViewById(R.id.etNewPass);
		etConfirmPass =(EditText) findViewById(R.id.etConfirmPass);
		btnUpdatePass = (Button) findViewById(R.id.btnUpdatePassword);
		
		
		btnUpdatePass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				String currentPass = etCurrentPass.getText().toString();
				String newPass = etNewPass.getText().toString();
				String confirmPass = etConfirmPass.getText().toString();
				if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
					Toast.makeText(activity, "Please fill the form", Toast.LENGTH_SHORT).show();
				}else {
					if (connection.isConnectedToInternet()) {
						updatePassword(currentPass, newPass, confirmPass);
					}else {
						Toast.makeText(activity, "Please check internet connection and try again", 0).show();
					}
				}
			}
		});
	}
	
	
	public void updatePassword(String currentPass,String newPass,String confirmPass) {

		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		params.put("old_password", currentPass);
		params.put("new_password", newPass);
		params.put("confirm_password", confirmPass);

		ApiManager apiManager = new ApiManager(ApiManager.CHANGE_PATIENT_PASS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}
	
	
	//MaterialDialog mMaterialDialog;
	public void showMessageBox(String tittle , String content) {
		new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Done", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.create()
				.show();

		/*mMaterialDialog = new MaterialDialog(context)
	    .setTitle(tittle)
	    .setMessage(content)
	    .setPositiveButton("OK", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
	            finish();
	           
	        }
	    });*/
	
		
		
//		.setNegativeButton("CANCEL", new View.OnClickListener() {
//	        @Override
//	        public void onClick(View v) {
//	            mMaterialDialog.dismiss();
//	         
//	        }
//	    })
	
	//mMaterialDialog.show();
	
	}


	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.CHANGE_PATIENT_PASS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("error")) {
					showMessageBox("Error", jsonObject.getString("msg"));
				}else if (jsonObject.has("success")) {
					showMessageBox("Success", jsonObject.getString("message"));
				}else {
					showMessageBox("Error", DATA.CMN_ERR_MSG);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
