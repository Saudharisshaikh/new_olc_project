
package com.app.OnlineCareUS_MA.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.model.MyAppointmentsModel;
import com.app.OnlineCareUS_MA.model.ReportsModel;
import com.app.OnlineCareUS_MA.util.DATA;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class LiveCareRefreshService extends Service {

	Timer t;
	public long DELAY = 1000;
	public long PERIOD = 5000;  // every .5 hours
	Context ctx;
	Handler handler;
	TimerTask doAsynchronousTask;



	@Override
	public IBinder onBind(Intent arg0) {
		throw new UnsupportedOperationException("not yet completed...");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		ctx = LiveCareRefreshService.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		client = new AsyncHttpClient();

		handler = new Handler();
		t = new Timer();

		doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {

					public void run() {
						DATA.print("--online care LiveCareRefreshService running");
						try {

							if(isNetworkConnected()) {

								getLiveCarePatiensList();

								DATA.print("--online care LiveCareRefreshService running");

							}

						} catch (Exception e) {
							e.printStackTrace();
							DATA.print("--online care LiveCareRefreshService exception");
						}
					}
				});
			}
		};
		t.schedule(doAsynchronousTask, DELAY, PERIOD); //execute in every 50000 ms

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		DATA.print("--online care LiveCareRefreshService destroyed");
		t.cancel();
	}




	private String randomStr()
	{
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		StringBuilder sb = new StringBuilder( 8 );
		for( int i = 0; i < 8; i++ )
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();
	}

	JSONObject jsonObject;
	JSONArray appointmentsArray, reportsArray;
	AsyncHttpClient client;
	SharedPreferences prefs;
	MyAppointmentsModel temp;
	private void getLiveCarePatiensList() {

		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));

		String reqURL = DATA.baseUrl+"getMyLiveCheckups?rnd="+randomStr();

		ApiManager.addHeader(ctx, client);

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.get(reqURL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					//DATA.print("--url in getmylivecheckups "+DATA.baseUrl+"getMyLiveCheckups");
					DATA.print("--online care response in get livecare patients: "+content);

					try {

						jsonObject = new JSONObject(content);

						String	status = jsonObject.getString("status");
//					msg = jsonObject.getString("msg");

						if(status.equals("success"))
						{

							String appts = jsonObject.getString("livecheckups");
							appointmentsArray = new JSONArray(appts);

							if (appointmentsArray.length() == 0) {
								//empty queue
								DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
								//adapter set here
								Intent intent = new Intent();
								intent.setAction("refresh_live_care_queue");
								ctx.sendBroadcast(intent);
							} else {

								DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
								temp = new MyAppointmentsModel();

								for(int i = 0; i<appointmentsArray.length(); i++) {


									temp = new MyAppointmentsModel();
									temp.id = appointmentsArray.getJSONObject(i).getString("id");
									temp.liveCheckupId = appointmentsArray.getJSONObject(i).getString("live_checkup_id");
									temp.first_name = appointmentsArray.getJSONObject(i).getString("first_name");
									temp.last_name = appointmentsArray.getJSONObject(i).getString("last_name");
									temp.symptom_name = appointmentsArray.getJSONObject(i).getString("symptom_name");
									temp.condition_name = appointmentsArray.getJSONObject(i).getString("condition_name");
									temp.description = appointmentsArray.getJSONObject(i).getString("description");
									temp.patients_qbid = appointmentsArray.getJSONObject(i).getString("patients_qbid");
									temp.datetime = appointmentsArray.getJSONObject(i).getString("datetime");

									temp.latitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("latitude"));
									temp.longitude = Double.parseDouble(appointmentsArray.getJSONObject(i).getString("longitude"));
									if (appointmentsArray.getJSONObject(i).has("image")) {
										temp.image = appointmentsArray.getJSONObject(i).getString("image");
									} else {
										temp.image = "";
									}

									temp.birthdate = appointmentsArray.getJSONObject(i).getString("birthdate");
									temp.gender = appointmentsArray.getJSONObject(i).getString("gender");
									temp.residency = appointmentsArray.getJSONObject(i).getString("residency");
									temp.patient_phone = appointmentsArray.getJSONObject(i).getString("patient_phone");
									temp.StoreName = appointmentsArray.getJSONObject(i).getString("StoreName");
									temp.PhonePrimary = appointmentsArray.getJSONObject(i).getString("PhonePrimary");

									if (appointmentsArray.getJSONObject(i).has("reports")) {
										String rp = appointmentsArray.getJSONObject(i).getString("reports");
										JSONArray reports = new JSONArray(rp);
										temp.sharedReports = new ArrayList<ReportsModel>();
										//ReportsModel reportsModel;
										for (int j = 0; j < reports.length(); j++) {
											ReportsModel reportsModel = new ReportsModel();

											reportsModel.name = reports.getJSONObject(j).getString("file_display_name");
											reportsModel.url = reports.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
											reportsModel.patientID = reports.getJSONObject(j).getString("patient_id");//patient_id

											temp.sharedReports.add(reportsModel);

											//reportsModel = null;
										}
									} else {
										temp.sharedReports = new ArrayList<ReportsModel>();
									}

									DATA.allAppointments.add(temp);

									temp = null;
								}

						/*String reprts = jsonObject.getString("reports");

						reportsArray = new JSONArray(reprts);

						DATA.allReports = new ArrayList<ReportsModel>();

						ReportsModel temp1;

						for(int j = 0; j<reportsArray.length(); j++) {

							temp1 = new ReportsModel();

							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id

							DATA.allReports.add(temp1);

							temp1 = null;
						}*/

								//adapter set here
								Intent intent = new Intent();
								intent.setAction("refresh_live_care_queue");
								ctx.sendBroadcast(intent);
								DATA.print("--broadcast sent");
							}
						}else {

							//Toast.makeText(activity, "Something went wrong, please try again.", 0).show();
						}


					} catch (JSONException e) {
						DATA.print("--online care exception in getlivecare patients: "+e);

						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					//new GloabalMethods(activity).checkLogin(content,statusCode);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}//getLiveCarePatiensList

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

}

