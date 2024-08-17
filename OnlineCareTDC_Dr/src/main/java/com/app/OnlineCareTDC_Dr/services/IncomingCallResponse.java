package com.app.OnlineCareTDC_Dr.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class IncomingCallResponse {

	private Context ctx;
	private String callResponse;
	private SharedPreferences prefs;

	public IncomingCallResponse(Context ctx, String callResponse) {
		this.ctx = ctx;
		this.callResponse = callResponse;
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void sendResponse() {
		String url = DATA.baseUrl + "incomingcallresponse";
		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("call_id", DATA.incommingCallId);
		params.put("response", callResponse);

		// Add headers if needed
		//client.addHeader("HeaderName", "HeaderValue");
		ApiManager.addHeader(ctx, client);

		DATA.print("-- Request URL : " + url);
		DATA.print("-- Request Params : " + params);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					if (ctx instanceof MainActivity) {
						((MainActivity) ctx).setResults();
					} else {
						DATA.print("-- ctx is not MainActivity");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// Handle failure case
				try {
					String response = new String(responseBody);
					DATA.print("--online care result in incomingCallResponse failure: " + responseBody);
					new GloabalMethods((Activity) ctx).checkLogin(response, statusCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
}


/*
public class IncomingCallResponse extends AsyncTask<String, String, Boolean> {
	
	Context ctx;
	String url,callResponse, response,numPatients = "", stats = "";
	SharedPreferences prefs;

	public IncomingCallResponse(Context ctx, String callResponse) {
		
		this.ctx = ctx;
		this.callResponse = callResponse;
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
		//url = DATA.baseUrl+"incomingcallresponse?call_id="+prefs.getString("callingIDPatient", "") + "&response="+callResponse;
		url = DATA.baseUrl+"incomingcallresponse?call_id="+DATA.incommingCallId  + "&response="+callResponse;

	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			DATA.print("--online url in incomingCallREsponse: "+url);
			
		HttpClient httpClient  = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(url);
		response  = httpClient.execute(httpPost,responseHandler);
		
		DATA.print("--online care result in incomingCallREsponse: "+response);
		
		return true;
		}
		catch(Exception e) {
			
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
//		if(result) {
//			
//			try {
//				
//				JSONObject jsonObject = new JSONObject(response);
//				
//				String patients = jsonObject.getString("patients");
//				
//				JSONObject patientObject = new JSONObject(patients);
//				
//				 stats = patientObject.getString("status");
//				 numPatients = patientObject.getString("total_patient");
//				
//				
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		if (ctx instanceof MainActivity) {
			// handle activity case
			((MainActivity)ctx).setResults();
		}else {
			DATA.print("-- ctx is not MAinActivity");
		}

		//((SampleActivity)ctx).setResults();
		//}
	}

}
*/
