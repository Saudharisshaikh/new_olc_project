package com.app.OnlineCareUS_Dr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareUS_Dr.api.ApiManager;
import com.app.OnlineCareUS_Dr.util.CheckInternetConnection;
import com.app.OnlineCareUS_Dr.util.CustomToast;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.app.OnlineCareUS_Dr.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CareRatings extends AppCompatActivity {

	AppCompatActivity activity;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	SharedPreferences prefs;
	
	RatingBar ratingBar;
	TextView tvRatting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_care_ratings);
		activity = CareRatings.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
		tvRatting = (TextView) findViewById(R.id.tvRatting);
		
		if (checkInternetConnection.isConnectedToInternet()) {
			getRating();
		} else {
			customToast.showToast("No internet connection", 0, 0);
		}
		
	}

	public void getRating() {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();*/
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));

		String reqURL = DATA.baseUrl+"/getDoctorRating";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getRating "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);

						String ratting = jsonObject.getJSONObject("data").getString("rating");

						if (ratting.isEmpty() || ratting.equalsIgnoreCase("null")) {
							ratting = "0.0";
						}

						ratingBar.setRating(Float.parseFloat(ratting));

						tvRatting.setText(ratting+" out of 5");

					} catch (JSONException e) {
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}


				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end logout
}
