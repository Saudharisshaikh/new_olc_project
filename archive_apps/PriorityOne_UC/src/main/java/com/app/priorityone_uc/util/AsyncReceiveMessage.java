package com.app.priorityone_uc.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.priorityone_uc.services.LiveCareWaitingService1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class AsyncReceiveMessage extends AsyncTask<String, String, Boolean> {
	
	Context ctx;
	String url,appntID, response,numPatients = "", stats = "",your_number;
	SharedPreferences prefs;
	
	public AsyncReceiveMessage(Context ctx, String appntID) {
		
		this.ctx = ctx;
		this.appntID = appntID;
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
		url = DATA.baseUrl+"getWaitingPatients/"+appntID+"/"+prefs.getString("docId_eLiveCare", "");

	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			System.out.println("--url in asyncRecevieMessage: "+url);
			
		HttpClient httpClient  = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(url);
		response  = httpClient.execute(httpPost,responseHandler);
		
		System.out.println("--online care result in asyncgetMessages: "+response);
		
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

}
