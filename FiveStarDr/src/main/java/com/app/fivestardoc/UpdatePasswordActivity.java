package com.app.fivestardoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.util.CheckInternetConnection;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdatePasswordActivity extends BaseActivity {

	EditText etCurrentPass,etNewPass,etConfirmPass;
	Button btnUpdatePass;
	SharedPreferences prefs;
	 ProgressDialog pd;
	AppCompatActivity activity;
	 CheckInternetConnection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_password);
		
		activity = UpdatePasswordActivity.this;
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false); 
		
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
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}
			}
		});
	}
	
	
	public void updatePassword(String currentPass,String newPass,String confirmPass) {

		pd.show();

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("old_password", currentPass);
		params.put("new_password", newPass);
		params.put("confirm_password", confirmPass);
		

		String reqURL = DATA.baseUrl+"/changedoctorPassword";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					//{"error":"1","msg":"Invalid Password"}
					//{"success":1,"message":"Password has been changed."}
					DATA.print("--responce in updatePassword: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("error")) {
							if (jsonObject.has("message")) {
								new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
										.setTitle("Error")
										.setMessage(jsonObject.getString("message"))
										.setPositiveButton("Ok", null)
										//.setNegativeButton("Not Now", null)
										.create()
										.show();
							}else if (jsonObject.has("msg")) {
								new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
										.setTitle("Error")
										.setMessage(jsonObject.getString("msg"))
										.setPositiveButton("Ok", null)
										//.setNegativeButton("Not Now", null)
										.create()
										.show(); }

						}else if (jsonObject.has("success")) {
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle("Success")
									.setMessage(jsonObject.getString("message"))
									.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									})
									//.setNegativeButton("Not Now", null)
									.create()
									.show();
						}else {
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle("Error")
									.setMessage(DATA.CMN_ERR_MSG)
									.setPositiveButton("Ok", null)
									//.setNegativeButton("Not Now", null)
									.create()
									.show();
						}
					} catch (JSONException e) {
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle("Error")
								.setMessage(DATA.JSON_ERROR_MSG)
								.setPositiveButton("Ok", null)
								//.setNegativeButton("Not Now", null)
								.create()
								.show();
						e.printStackTrace();
					}

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
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}

	
}
