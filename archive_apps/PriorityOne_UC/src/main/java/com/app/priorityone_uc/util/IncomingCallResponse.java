package com.app.priorityone_uc.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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
		
		
		url = DATA.baseUrl+"incomingcallresponse?call_id="+DATA.incommingCallId + "&response="+callResponse;
//         prefs.getString("callingIDPatient", "")
	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			System.out.println("--online url in incomingCallREsponse: "+url);
			
		HttpClient httpClient  = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(url);
		response  = httpClient.execute(httpPost,responseHandler);
		
		System.out.println("--online care result in incomingCallREsponse: "+response);
		
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
			
			//((MainActivity)ctx).setResults();
		//}
	}

}
