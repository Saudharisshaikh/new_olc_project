package com.app.fivestaruc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.fivestaruc.adapter.MyAppointmentsAdapter;
import com.app.fivestaruc.api.ApiManager;
import com.app.fivestaruc.model.MyAppointmentsModel;
import com.app.fivestaruc.util.CheckInternetConnection;
import com.app.fivestaruc.util.CustomToast;
import com.app.fivestaruc.util.DATA;
import com.app.fivestaruc.util.Database;
import com.app.fivestaruc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MyAppointments extends BaseActivity {
	
	AppCompatActivity activity;
	MyAppointmentsAdapter myAppointmentsAdapter;
	ListView lvMyAppointments;
	TextView tvNoAppointments;
	MyAppointmentsModel temp;

	SharedPreferences prefs;
	JSONObject jsonObject;
	JSONArray appointmentsArray;
	AsyncHttpClient client;
	String msg, status;

	ProgressDialog pd;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	
	@Override
	protected void onResume() {
		super.onResume();

		if(checkInternetConnection.isConnectedToInternet()) {
			
			getMyAppointmentsCall();
		}
		else {
			customToast.showToast("Not connected to Internet, Can't get your appointments",  0, 0);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_appointments);
		
		activity = MyAppointments.this;

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_NEW_APPOINTMENT);//Apptmnt confirmed

		tvNoAppointments = (TextView) findViewById(R.id.tvNoAppointments);
		lvMyAppointments = (ListView) findViewById(R.id.lvMyAppointments);
		
		pd = new ProgressDialog(activity);
		pd.setMessage("Loading your appointments...");
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast =  new CustomToast(activity);

		lvMyAppointments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				Intent i = new Intent(activity, MyAppointmentDetails.class);
				i.putExtra("aptDrName", DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
				i.putExtra("aptDrUrl", DATA.allAppointments.get(position).dr_image);
				i.putExtra("aptSymp", DATA.allAppointments.get(position).symptom_name);
				i.putExtra("aptCond", DATA.allAppointments.get(position).condition_name);
				i.putExtra("aptdesc", DATA.allAppointments.get(position).description);
				i.putExtra("aptStatus", DATA.allAppointments.get(position).status);
				i.putExtra("aptDate", DATA.allAppointments.get(position).appointment_date);//date
				i.putExtra("aptId", DATA.allAppointments.get(position).appointment_id);
				
				i.putExtra("aptFromTime", DATA.allAppointments.get(position).from_time);
				i.putExtra("aptToTime", DATA.allAppointments.get(position).to_time);
				
				i.putExtra("aptID", DATA.allAppointments.get(position).appointment_id);
				
				i.putExtra("aptSlotID", DATA.allAppointments.get(position).slot_id);

				i.putExtra("aptReason", DATA.allAppointments.get(position).reason);
				i.putExtra("aptDrCat", DATA.allAppointments.get(position).doctor_category);
								
				/*if(DATA.allAppointments.get(position).time.equals("morning")) {
					
					i.putExtra("aptFromTime", DATA.allAppointments.get(position).morningFromTime);
					i.putExtra("aptToTime", DATA.allAppointments.get(position).morningToTime);
				}
				else {
					i.putExtra("aptFromTime", DATA.allAppointments.get(position).eveningFromTime);
					i.putExtra("aptToTime", DATA.allAppointments.get(position).eveningToTime);
				}*/
				
				startActivity(i);
			}
		});

		//============tab layout apptmnt========================================
		TextView tvBookAppt = findViewById(R.id.tvBookAppt);
		TextView tvMyAppt = findViewById(R.id.tvMyAppt);
		tvBookAppt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openActivity.open(SearchADoctor.class, true);
			}
		});
	}

	private void getMyAppointmentsCall() {
		
		pd.show();

		client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();
		
		params.put("patient_id", prefs.getString("id", ""));//patient id
		//params.put("sub_patient_id", prefs.getString("subPatientID", ""));//sub patient id

		DATA.print("-- params in getMyAppointment: "+params.toString());

		client.post(DATA.baseUrl+"getMyAppointment", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in api getMyAppointment: "+content);

					try {

						jsonObject = new JSONObject(content);

						status = jsonObject.getString("status");
						//			msg = jsonObject.getString("msg");

						if(status.equals("success")) {

							String foldrstr = jsonObject.getString("appointments");

							appointmentsArray = new JSONArray(foldrstr);
							if (appointmentsArray.length() == 0) {
								tvNoAppointments.setVisibility(View.VISIBLE);

								DATA.allAppointments = new ArrayList<MyAppointmentsModel>();
								myAppointmentsAdapter = new MyAppointmentsAdapter(activity);
								lvMyAppointments.setAdapter(myAppointmentsAdapter);
							} else {
								tvNoAppointments.setVisibility(View.GONE);

								DATA.allAppointments = new ArrayList<MyAppointmentsModel>();

								MyAppointmentsModel temp ;

								for (int i = 0; i < appointmentsArray.length(); i++) {

							/*temp = new MyAppointmentsModel();

							temp.id = appointmentsArray.getJSONObject(i).getString("appointment_id");
							temp.date = appointmentsArray.getJSONObject(i).getString("appointment_date");
							temp.drName = appointmentsArray.getJSONObject(i).getString("first_name");
							temp.status = appointmentsArray.getJSONObject(i).getString("status");
							temp.time = appointmentsArray.getJSONObject(i).getString("time");
							temp.symptomName = appointmentsArray.getJSONObject(i).getString("symptom_name");
							temp.conditionName = appointmentsArray.getJSONObject(i).getString("condition_name");
							temp.description = appointmentsArray.getJSONObject(i).getString("description");
							temp.date = appointmentsArray.getJSONObject(i).getString("appointment_date");
							temp.drImageUrl = appointmentsArray.getJSONObject(i).getString("dr_image");

							temp.eveningFromTime = appointmentsArray.getJSONObject(i).getString("evening_from_time");
							temp.eveningToTime = appointmentsArray.getJSONObject(i).getString("evening_to_time");
							temp.morningFromTime = appointmentsArray.getJSONObject(i).getString("from_time");
							temp.morningToTime = appointmentsArray.getJSONObject(i).getString("to_time");

							DATA.allAppointments.add(temp);

							temp = null;*/

									String appointment_id = appointmentsArray.getJSONObject(i).getString("appointment_id");
									String appointment_date = appointmentsArray.getJSONObject(i).getString("appointment_date");
									String first_name = appointmentsArray.getJSONObject(i).getString("first_name");
									String last_name = appointmentsArray.getJSONObject(i).getString("last_name");
									String dr_image = appointmentsArray.getJSONObject(i).getString("dr_image");
									String status = appointmentsArray.getJSONObject(i).getString("status");
									String time = appointmentsArray.getJSONObject(i).getString("time");
									String from_time = appointmentsArray.getJSONObject(i).getString("from_time");
									String to_time = appointmentsArray.getJSONObject(i).getString("to_time");
									String symptom_name = appointmentsArray.getJSONObject(i).getString("symptom_name");
									String condition_name = appointmentsArray.getJSONObject(i).getString("condition_name");
									String description = appointmentsArray.getJSONObject(i).getString("description");
									String date = appointmentsArray.getJSONObject(i).getString("date");
									String slot_id = appointmentsArray.getJSONObject(i).getString("slot_id");
									String reason = appointmentsArray.getJSONObject(i).getString("reason");
									String doctor_category = appointmentsArray.getJSONObject(i).getString("doctor_category");
									if(reason.isEmpty()){
										reason = "None";
									}
									temp = new MyAppointmentsModel(appointment_id, appointment_date, first_name, last_name,
											dr_image, status, time, from_time, to_time, symptom_name, condition_name, description,
											date,slot_id,reason,doctor_category);
									DATA.allAppointments.add(temp);
									temp = null;
								}

								myAppointmentsAdapter = new MyAppointmentsAdapter(activity);
								lvMyAppointments.setAdapter(myAppointmentsAdapter);
							}
						} else {
							customToast.showToast("No appoinements found", 0, 0);
						}


					} catch (JSONException e) {
						DATA.print("--Exception in login: "+e);

						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getMyAppointment, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- getMyAppointment o failure : "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}
}
