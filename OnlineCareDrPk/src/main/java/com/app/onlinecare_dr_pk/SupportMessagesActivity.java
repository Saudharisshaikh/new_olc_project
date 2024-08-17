package com.app.onlinecare_dr_pk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.app.onlinecare_dr_pk.adapter.SupportMessageAdapter;
import com.app.onlinecare_dr_pk.api.ApiManager;
import com.app.onlinecare_dr_pk.model.SupportMessageBean;
import com.app.onlinecare_dr_pk.util.CheckInternetConnection;
import com.app.onlinecare_dr_pk.util.CustomToast;
import com.app.onlinecare_dr_pk.util.DATA;
import com.app.onlinecare_dr_pk.util.GloabalMethods;
import com.app.onlinecare_dr_pk.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SupportMessagesActivity extends AppCompatActivity {
	
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	
	EditText etSendMessage;
	ImageButton ivSendMessage;
	//TextView tvMessage;
	
	BroadcastReceiver supportMsgBroadcast;
	//CircularImageView ivMsgOther;
	
	ListView lvMessages;
	SupportMessageAdapter adapter;
	
	public static ArrayList<SupportMessageBean> supportMsgList = new ArrayList<SupportMessageBean>();
	
	@Override
	protected void onStart() {
		registerReceiver(supportMsgBroadcast, new IntentFilter("coordinator_message"));
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		unregisterReceiver(supportMsgBroadcast);
		super.onStop();
	}

	static boolean isFromEmsMsg = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_messages);

		isFromEmsMsg = getIntent().getBooleanExtra("isFromEmsMsg", false);

		if(isFromEmsMsg && getSupportActionBar() != null){
			getSupportActionBar().setTitle("NEMT Messages");
		}

		activity = SupportMessagesActivity.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		
		etSendMessage = (EditText) findViewById(R.id.etSendMessage);
		ivSendMessage = (ImageButton) findViewById(R.id.ivSendMessage);
		//tvMessage = (TextView) findViewById(R.id.tvMessage);
		//ivMsgOther = (CircularImageView) findViewById(R.id.ivMsgOther);
		
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		
		ivSendMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (etSendMessage.getText().toString().isEmpty()) {
					etSendMessage.setError("Please type your message");
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						sendCoordinatormessage(etSendMessage.getText().toString());
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
					}
				}
			}
		});
		
		
		supportMsgBroadcast = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				/*String message = intent.getStringExtra("message");
				String from = intent.getStringExtra("from");
				String image = intent.getStringExtra("image");*/
				
				//tvMessage.setText(message);
				
				//Picasso.with(activity).load(image).placeholder(R.drawable.icon_call_screen).into(ivMsgOther);
				
				adapter = new SupportMessageAdapter(activity, supportMsgList);
				lvMessages.setAdapter(adapter);
			}
		};
		
	    adapter = new SupportMessageAdapter(activity, supportMsgList);
		lvMessages.setAdapter(adapter);
		
	}

	
	
	public void sendCoordinatormessage(final String message_text) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		String api = "";
		if(isFromEmsMsg){
			api = "ems/sendmessage";
			params.put("doctor_id", prefs.getString("id", "0"));
			params.put("message_text", message_text);
			params.put("patient_id",DATA.selectedLiveCare.id);
			params.put("from","doctor");
			params.put("to","ems");
		}else{
			api = "sendCoordinatormessage_doc";
			params.put("doctor_id", prefs.getString("id", "0"));
			params.put("message_text", message_text);
		}

		//DATA.print("-- api: "+api);
		//DATA.print("-- params: "+params.toString());

		String reqURL = DATA.baseUrl+api;

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in sendCoordinatormessage "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						int success = jsonObject.getInt("success");
						String message = jsonObject.getString("message");
						customToast.showToast(message, 0, 1);
						etSendMessage.setText("");
						SupportMessageBean bean = new SupportMessageBean();
						bean.message = message_text;
						bean.type = "me";
						supportMsgList.add(bean);
						adapter = new SupportMessageAdapter(activity, supportMsgList);
						lvMessages.setAdapter(adapter);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
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
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}
	
}
