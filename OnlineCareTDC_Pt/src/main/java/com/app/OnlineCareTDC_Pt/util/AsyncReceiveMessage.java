package com.app.OnlineCareTDC_Pt.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.services.LiveCareWaitingService1;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncReceiveMessage{
	
	Context ctx;
	String url,appntID, response,numPatients = "", stats = "",your_number;
	SharedPreferences prefs;
	
	public AsyncReceiveMessage(Context ctx, String appntID) {
		this.ctx = ctx;
		this.appntID = appntID;
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void executeAsyncReceiveMessage() {
		url = DATA.baseUrl + "getWaitingPatients/" + appntID + "/" + prefs.getString("docId_eLiveCare", "");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(ctx,client);
		client.post(url, null, new AsyncHttpResponseHandler() {
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

					String patients = jsonObject.getString("patients");

					JSONObject patientObject = new JSONObject(patients);

					stats = patientObject.getString("status");
					numPatients = patientObject.getString("total_patient");

					your_number = patientObject.getString("your_number");

					((LiveCareWaitingService1) ctx).setResults(your_number, numPatients, stats);
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
			}
		});
	}
}
	
	/*@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
		url = DATA.baseUrl+"getWaitingPatients/"+appntID+"/"+prefs.getString("docId_eLiveCare", "");

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
		DATA.shouldLiveCareWatingRefresh = 1;
		if(result) {
			
			try {
				
				JSONObject jsonObject = new JSONObject(response);
				
				String patients = jsonObject.getString("patients");
				
				JSONObject patientObject = new JSONObject(patients);
				
				 stats = patientObject.getString("status");
				 numPatients = patientObject.getString("total_patient");
				 
				  your_number = patientObject.getString("your_number");
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			((LiveCareWaitingService1)ctx).setResults(your_number, numPatients, stats);
		}
	}

}*/
