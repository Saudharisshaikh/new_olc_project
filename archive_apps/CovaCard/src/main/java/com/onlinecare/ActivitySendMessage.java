package com.onlinecare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.covacard.api.ApiManager;
import com.covacard.util.CheckInternetConnection;
import com.covacard.util.CustomToast;
import com.covacard.util.DATA;
import com.covacard.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.covacard.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ActivitySendMessage extends Activity {


	EditText etMsg;
	TextView btnMsgOK;
	TextView tvMsgDoctorName;

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	ProgressDialog pd;
	CustomToast customToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_message);

		activity = ActivitySendMessage.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		tvMsgDoctorName = (TextView) findViewById(R.id.tvMsgDoctorName);
		etMsg = (EditText) findViewById(R.id.etMessage);

		//etMsg.setText(DATA.msgFromDoctor);
		tvMsgDoctorName.setText("To: "+DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());

		btnMsgOK = (TextView) findViewById(R.id.btnMsgOK);


		btnMsgOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//	finish();
				if (checkInternetConnection.isConnectedToInternet()) {
					if (etMsg.getText().toString().isEmpty()) {
						Toast.makeText(activity, "Please type yout message", 0).show();
					} else {

						sendMsg(DATA.selecetedBeanFromConversation.getDoctor_id(), etMsg.getText().toString());
					}
				} else {
					Toast.makeText(activity, "No network connection", 0).show();
				}

			}
		});
	}



	public void sendMsg(String doctorId,String msgText) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("doctor_id", doctorId);
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("message_text", msgText);
		params.put("from", "patient");

		client.post(DATA.baseUrl + "/sendmessagetoDoctor", params, new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try {
					String content = new String(response);

					System.out.println("--reaponce in sendMsg" + content);
					//  tvDrsNotes.setText(content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							//dialogSendMessage.dismiss();
							Toast.makeText(activity, "Message Sent", Toast.LENGTH_LONG).show();
							finish();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("--json eception sendMsg" + content);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("-- responce onsuccess: sendMsg , http status code: " + statusCode + " Byte responce: " + response);
					customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail sendMsg" + content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

				} catch (Exception e1) {
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
				}
			}

		});
	}//end sendMsg
}
