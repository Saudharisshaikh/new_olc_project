package com.app.Olc_MentalHealth.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.Olc_MentalHealth.util.DATA;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class SaveGCM_TokenBroadcast extends BroadcastReceiver {

	SharedPreferences prefs;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		prefs = context.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		if(intent.getAction().equals("com.app.emcurauc.SAVE_GCM_TOKEN")){
			saveToken(DATA.gcm_token);
			
		}
	}
	
	
	
	public void saveToken(String device_token) {
		 
		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();*/
		
		/*if (device_token.contains("|ID|")) {
			device_token = device_token.substring(device_token.indexOf(":") +1);
		}*/
			  AsyncHttpClient client = new AsyncHttpClient();
			  RequestParams params = new RequestParams();
			  
			  params.put("patient_id", prefs.getString("id", "0"));
			  params.put("timezone", getDeviceTimeZone());
			  params.put("platform", "android");
			  params.put("device_token", device_token);
			 
			 DATA.print("--in params regId: "+device_token+" userid: "+ prefs.getString("id", "0")+" timezone "+getDeviceTimeZone());

			  client.post(DATA.baseUrl+"/saveToken", params, new AsyncHttpResponseHandler() {
				  @Override
				  public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					  // called when response HTTP status is "200 OK"
					  //customProgressDialog.dismissProgressDialog();
					  try{
						  String content = new String(response);
						  // pd.dismiss();
						  DATA.print("--reaponce in saveDeiceId"+content);
					  }catch (Exception e){
						  e.printStackTrace();
						  DATA.print("-- responce onsuccess: saveDeiceId, http status code: "+statusCode+" Byte responce: "+response);
						  //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }

				  @Override
				  public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					  // called when response HTTP status is "4XX" (eg. 401, 403, 404)
					  //customProgressDialog.dismissProgressDialog();
					  try {
						  String content = new String(errorResponse);
						  DATA.print("--reaponce in onfailure saveDeiceId: "+content);
						  // pd.dismiss();
						  //Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();

						  //new GloabalMethods(activity).checkLogin(content,statusCode);
						  //customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					  }catch (Exception e1){
						  e1.printStackTrace();
						  //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }
			  });

			 }//end signup

	
	private String getDeviceTimeZone() {
		/*Calendar cal = Calendar.getInstance();
		TimeZone tz = cal.getTimeZone();
		Log.d("Time zone","="+tz.getDisplayName());	
		DATA.print("--time zone "+tz.getDisplayName());
		DATA.print("--time zone from util "+TimeZone.getDefault());
		DATA.print("--time zone id from util "+TimeZone.getDefault().getID());*/
		return TimeZone.getDefault().getID();
	}

}
