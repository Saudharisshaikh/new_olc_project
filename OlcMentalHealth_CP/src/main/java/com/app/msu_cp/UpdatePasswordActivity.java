package com.app.msu_cp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdatePasswordActivity extends BaseActivity {
	EditText etCurrentPass, etNewPass, etConfirmPass;
	Button btnUpdatePass;
	SharedPreferences prefs;
	ProgressDialog pd;
	CheckInternetConnection connection;
	AppCompatActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_password);

		activity = UpdatePasswordActivity.this;
		getSupportActionBar().setTitle("Update Password");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			pd = new ProgressDialog(this,
					ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		} else {
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		connection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		etCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
		etNewPass = (EditText) findViewById(R.id.etNewPass);
		etConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
		btnUpdatePass = (Button) findViewById(R.id.btnUpdatePassword);

		btnUpdatePass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String currentPass = etCurrentPass.getText().toString();
				String newPass = etNewPass.getText().toString();
				String confirmPass = etConfirmPass.getText().toString();
				if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
					customToast.showToast("Please fill the form",0,0);
				} else if (!newPass.equals(confirmPass)) {
					customToast.showToast("Password does not match the confirm password.",0,0);
				} else {
					if (connection.isConnectedToInternet()) {
						updatePassword(currentPass, newPass, confirmPass);
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}
			}
		});
	}

	public void updatePassword(String currentPass, String newPass, String confirmPass) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("old_password", currentPass);
		params.put("new_password", newPass);
		params.put("confirm_password", confirmPass);

		final String reqURL = DATA.baseUrl + "/changedoctorPassword";

		DATA.print("-- url : " + reqURL);
		DATA.print("-- params in changedoctorPassword: "+params.toString());

		pd.show();
		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					// {"error":"1","msg":"Invalid Password"}
					// {"success":1,"message":"Password has been changed."}

					DATA.print("--responce in changedoctorPassword: " + content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("error")) {
							if (jsonObject.has("message")){
								showMessageBox(activity ,"Error", jsonObject.getString("message"));
							}else if (jsonObject.has("msg")){
								showMessageBox(activity,"Error", jsonObject.getString("msg"));
							}
						} else if (jsonObject.has("success")) {
							showMessageBox(activity, "Success", jsonObject.getString("message"));
						} else {
							showMessageBox(activity, "Error", DATA.CMN_ERR_MSG);
						}
					} catch (JSONException e) {
						showMessageBox(activity, "Error", DATA.JSON_ERROR_MSG);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: changedoctorPassword, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure changedoctorPassword: " + content);
					showMessageBox(activity, "Error", DATA.CMN_ERR_MSG);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}

	public void showMessageBox(Context context, final String tittle, String content) {

		new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (tittle.equals("Success")) {
							etCurrentPass.setText("");
							etNewPass.setText("");
							etConfirmPass.setText("");
							finish();
						}
					}
				})
				//.setNegativeButton("Not Now", null)
				.create()
				.show();
	}

}
