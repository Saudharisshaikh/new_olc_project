package com.app.mhcsn_cp.services;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.app.mhcsn_cp.util.DATA;

public class AsyncReceiveMessage extends AsyncTask<String, String, Boolean> {
	
	Context ctx;
	String url,appntID, response,numPatients = "", stats = "";
	
	public AsyncReceiveMessage(Context ctx, String appntID) {
		
		this.ctx = ctx;
		this.appntID = appntID;
		
	}
	
	@Override
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
	}

}
