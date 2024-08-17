package com.app.OnlineCareTDC_Dr.util;

import java.util.ArrayList;
import java.util.List;

/*import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;*/
import org.json.JSONException;
import org.json.JSONObject;

import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.services.OutgoingCallService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class AsyncReceiveMessage {
	
	Context ctx;
	String url,call_id, response;
	
	public AsyncReceiveMessage(Context ctx, String call_id) {
		
		this.ctx = ctx;
		this.call_id = call_id;
		
	}

	public void executeAsyncReceiveMessage() {
		url = DATA.baseUrl + "outgoingcallresponse";

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(ctx,client);
		RequestParams params = new RequestParams();
		params.put("call_id", call_id);

		client.post(url, params, new AsyncHttpResponseHandler() {
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

					if (jsonObject.has("respond_id")) {
						DATA.outgoingCallResponse = jsonObject.getString("respond_id");

						if ((!DATA.outgoingCallResponse.equals("0")) && DATA.outgoingCallResponse.equals("accept")) {
							((OutgoingCallService) ctx).setResults();
						} else if (DATA.outgoingCallResponse.equals("reject") || DATA.outgoingCallResponse.equals("noanswer")) {
							((OutgoingCallService) ctx).callrej();
						}
					}
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
	
	/*@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		url = DATA.baseUrl+"outgoingcallresponse";
		DATA.print("--call_id in async receive msg: "+call_id);

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
		
		DATA.print("--online care dr result in outgoing service: "+response);
		
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
						((OutgoingCallService)ctx).setResults();
						
					}else if(DATA.outgoingCallResponse.equals("reject")){
						
						((OutgoingCallService)ctx).callrej();
						
					}else if(DATA.outgoingCallResponse.equals("noanswer")){
						
						((OutgoingCallService)ctx).callrej();
						
					}
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}*/

}
