package com.app.mdlive_dr.util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
//import com.app.onlinecare.services.LiveCareWaitingService1;

public class AsyncFiveSeconds extends AsyncTask<String, String, Boolean> {
	
	Context ctx;
	String url,appntID, response = "", stats = "";
	int incoming_call_status = 0;
	SharedPreferences prefs;

	public AsyncFiveSeconds(Context ctx, String appntID) {
		
		this.ctx = ctx;
		this.appntID = appntID;
		
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
		url = DATA.baseUrl+"fiveSecondService?id="+appntID+"&type=doctor";
		prefs = ctx.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

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

}
