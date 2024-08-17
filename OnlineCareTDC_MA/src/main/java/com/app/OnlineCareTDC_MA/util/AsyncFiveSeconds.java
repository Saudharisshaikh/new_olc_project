package com.app.OnlineCareTDC_MA.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.OnlineCareTDC_MA.api.ApiManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
//import com.app.onlinecare.services.LiveCareWaitingService1;

public class AsyncFiveSeconds{
	
	Context ctx;
	String url,appntID, response = "", stats = "";
	int incoming_call_status = 0;
	SharedPreferences prefs;

	public AsyncFiveSeconds(Context ctx, String appntID) {
		
		this.ctx = ctx;
		this.appntID = appntID;
		
	}

	public void executeAsyncFiveSeconds() {
		url = DATA.baseUrl + "fiveSecondService";
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(ctx,client);
		RequestParams params = new RequestParams();
		params.put("id", appntID);
		params.put("type", "doctor");

		client.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				// Perform any actions before the request starts
			}

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				try {
					String response = new String(responseBody);
					JSONObject jsonObject = new JSONObject(response);
					stats = jsonObject.getString("status");
					incoming_call_status = jsonObject.getInt("incoming_call_status");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				try {
					String content = new String(responseBody);
					DATA.print("-- onfailure : "+url+content);
					new GloabalMethods((Activity) ctx).checkLogin(content, statusCode);

				}catch (Exception e1){
					e1.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				// Perform any actions after the request completes
				// Call the appropriate method to handle the response
				// ((IncomingCallServiceNew) ctx).setResults(response);
			}
		});
	}
}
	
	/*@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
		url = DATA.baseUrl+"fiveSecondService?id="+appntID+"&type=doctor";
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			DATA.print("--url in asyncRecevieMessage: "+url);
			
		HttpClient httpClient  = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(url);
		response  = httpClient.execute(httpPost,responseHandler);
		
		DATA.print("--online care result in asyncgetMessages: "+response);
		
		return true;
		}
		catch(Exception e) {
			
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(result) {
			
			try {
				
				JSONObject jsonObject = new JSONObject(response);
				
				//String patients = jsonObject.getString("patients");
				
				//JSONObject patientObject = new JSONObject(patients);
				
				 stats = jsonObject.getString("status");
				 incoming_call_status = jsonObject.getInt("incoming_call_status");
	
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//	((IncomingCallServiceNew)ctx).setResults(response);
		}
	}

}*/
