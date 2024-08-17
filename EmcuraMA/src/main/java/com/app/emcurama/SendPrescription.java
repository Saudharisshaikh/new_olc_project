package com.app.emcurama;


import org.json.JSONException;
import org.json.JSONObject;

import com.app.emcurama.api.ApiManager;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cz.msebera.android.httpclient.Header;

public class SendPrescription extends Activity {
	
	Activity activity;
	TextView tvPrescDate,tvPrescPtName;
	EditText etPrescMedicine;
	Button btnSendPresc, btnPrescCancel;
	
	AsyncHttpClient client;	
	ProgressDialog pd;

	JSONObject jsonObject;
	String msg, status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_prescription);
		
		activity = SendPrescription.this;
		
		tvPrescDate = (TextView) findViewById(R.id.tvPrescDate);
		tvPrescPtName = (TextView) findViewById(R.id.tvPrescPtName);
		etPrescMedicine = (EditText) findViewById(R.id.etPrescMedicine);
		btnSendPresc = (Button) findViewById(R.id.btnSendPresc);
		btnPrescCancel = (Button) findViewById(R.id.btnPrescCancel);
		
		pd = new ProgressDialog(activity );
		pd.setTitle("Please wait..!");
		pd.setMessage("SENDING...");
		pd.setCanceledOnTouchOutside(false);

		
		btnSendPresc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				sendPrescription();
				
			}
		});

		btnPrescCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {


				finish();
			}
		});

	}

	private void sendPrescription() {

		pd.show();
		
		client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		
		RequestParams params = new RequestParams();
		params.put("type", "patient");

		String reqURL = DATA.baseUrl+"sendPrescription";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.get(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);


					DATA.print("--Response in login: "+content);

					// remove the progress bar view
//				refreshMenuItem.setActionView(null);

					try {

						jsonObject = new JSONObject(content);

						status = jsonObject.getString("status");
						msg = jsonObject.getString("msg");

						if(status.equals("success"))
						{




						}
					} catch (JSONException e) {
						DATA.print("--Exception in login: "+e);

						e.printStackTrace();
					}

					finish();

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
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
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}

}
