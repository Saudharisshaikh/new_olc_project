package com.app.msu_cp.services;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncReceiveMessage{
	
	Context ctx;
	String url,appntID, response,numPatients = "", stats = "";
	
	public AsyncReceiveMessage(Context ctx, String appntID) {

		this.ctx = ctx;
		this.appntID = appntID;
	}


	public void executeAsyncReceiveMessage() {
		url = DATA.baseUrl+"getWaitingPatients/"+appntID;

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(ctx,client);
		RequestParams params = new RequestParams();

		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				// Perform any actions before the request starts
			}

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				try {

					JSONObject jsonObject = new JSONObject(response);

					String patients = jsonObject.getString("patients");

					JSONObject patientObject = new JSONObject(patients);

					stats = patientObject.getString("status");
					numPatients = patientObject.getString("total_patient");


				} catch (JSONException e) {
					// TODO Auto-generated catch block
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

/*	@Override
	protected void onPreExecute() {
		super.onPreExecute();


		url = DATA.baseUrl+"getWaitingPatients/"+appntID;

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

				String patients = jsonObject.getString("patients");

				JSONObject patientObject = new JSONObject(patients);

				 stats = patientObject.getString("status");
				 numPatients = patientObject.getString("total_patient");


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		//	((LiveCareWaitingService1)ctx).setResults(numPatients, stats);
		}
	}*/

}
