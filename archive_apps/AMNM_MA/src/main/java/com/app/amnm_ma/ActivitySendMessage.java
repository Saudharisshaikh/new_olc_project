package com.app.amnm_ma;

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

import com.app.amnm_ma.api.ApiManager;
import com.app.amnm_ma.util.CheckInternetConnection;
import com.app.amnm_ma.util.CustomToast;
import com.app.amnm_ma.util.DATA;
import com.app.amnm_ma.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ActivitySendMessage extends Activity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	ProgressDialog pd;
	CustomToast customToast;

	EditText etMsg;
	TextView btnMsgOK;
	TextView tvMsgPatientName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_message);

		activity = ActivitySendMessage.this;
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);



		tvMsgPatientName = (TextView) findViewById(R.id.tvMsgPatientName);
		etMsg = (EditText) findViewById(R.id.etMessage);

		//etMsg.setText(DATA.msgFromDoctor);
		tvMsgPatientName.setText("To: "+DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());

		btnMsgOK = (TextView) findViewById(R.id.btnMsgOK);


		btnMsgOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (checkInternetConnection.isConnectedToInternet()) {
					if (etMsg.getText().toString().isEmpty()) {
						Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
					} else {
						sendMsg(DATA.selecetedBeanFromConversation.getPatient_id(), etMsg.getText().toString());
					}
				} else {
					Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}


	public void sendMsg(String patientId,String msgText) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("patient_id", patientId);
		params.put("message_text", msgText);
		params.put("from", "doctor");

		String reqURL = DATA.baseUrl+"/sendmessage";

		System.out.println("-- Request : "+reqURL);
		System.out.println("-- params : "+params.toString());

		client.post(reqURL,params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--reaponce in sendMsg"+content);
					// tvDrsNotes.setText(content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {

							Toast.makeText(activity, "Message Sent", Toast.LENGTH_LONG).show();
							finish();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("--json eception sendMsg" +content);
					}

				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg


}
