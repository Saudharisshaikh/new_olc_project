package com.app.mdlive_cp.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.app.mdlive_cp.util.DATA;

public class AsyncOutgoingCallRespons extends AsyncTask<String, String, Boolean> {
	
	Context ctx;
	String url,call_id, response;
	
	public AsyncOutgoingCallRespons(Context ctx, String call_id) {
		
		this.ctx = ctx;
		this.call_id = call_id;
		
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		url = DATA.baseUrl+"outgoingcallresponse";
		System.out.println("--user_id in async receive msg: "+call_id);

	}
	

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		    nameValuePairs.add(new BasicNameValuePair("call_id", call_id));

			
		HttpClient httpClient  = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		response  = httpClient.execute(httpPost,responseHandler);
		
		System.out.println("--online care dr result in outgoing service: "+response);
		
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
			
			//((OutgoingCallService)ctx).setResults(response);
			
			try {
				JSONObject jsonObject = new JSONObject(response);
				
				if(jsonObject.has("respond_id")) {
					
					DATA.outgoingCallResponse = jsonObject.getString("respond_id");
					
					if((! DATA.outgoingCallResponse.equals("0")) && DATA.outgoingCallResponse.equals("accept")) {
						//((MainActivity)ctx).setResults();
						//((OutgoingCallService)ctx).setResults();
						
					}else if(DATA.outgoingCallResponse.equals("reject")){
						
						//((OutgoingCallService)ctx).callrej();
						
					}
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			
			
			
			
			
			
			
			
			
			
		}
	}

}
