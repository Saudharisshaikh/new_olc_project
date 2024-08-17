package com.app.mhcsn_cp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.adapters.DrScheduleAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DrSheduleModel;
import com.app.mhcsn_cp.model.SlotBean;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DrSchedule extends AppCompatActivity {

	AppCompatActivity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	DrScheduleAdapter drScheduleAdapter;
	ListView lvDrSchedule;
	DrSheduleModel temp;

	TextView tvSelectedDate;
	SharedPreferences prefs;
	Button btnOtherDoctor,btnChangeDate,btnProceed;
	
	ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dr_schedule);

		activity = DrSchedule.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		tvSelectedDate = (TextView) findViewById(R.id.tvSelectedDate);

		lvDrSchedule = (ListView) findViewById(R.id.lvDrSchedule);

		

		tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));

		btnOtherDoctor = (Button) findViewById(R.id.btnOtherDoctor);
		btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
		btnProceed = (Button) findViewById(R.id.btnProceed);

		/*btnOtherDoctor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.goBack = true;
				onBackPressed();
			}
		});
		btnChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.goBackToForm = true;
				onBackPressed();
			}
		});
		btnProceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.shouldProceed = true;
				onBackPressed();
			}
		});*/

		btnProceed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(DATA.selectedSlotIdForAppointment.isEmpty()){
					customToast.showToast("Please select a time slot for the appointment",0,0);
					return;
				}
				openActivity.open(BookAppointment.class, true);
			}
		});
		
		
		if (checkInternetConnection.isConnectedToInternet()) {
			getDoctorsSlots(DATA.doctorsModelForApptmnt.id, prefs.getString("apptmntDate", ""));
		} else {
			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
		}

		
		
		lvDrSchedule.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				DATA.print("-------click");
				String dt = prefs.getString("apptmntDate", "");
				String timetoreplace = dt.split(" ")[1]+" "+dt.split(" ")[2];
				dt = dt.replace(timetoreplace, slotBeans.get(arg2).getFrom_time());
				prefs.edit().putString("apptmntDate", dt).commit();
				
				DATA.print("-------dt "+dt);
				drScheduleAdapter.notifyDataSetChanged();
				tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));
				
				DATA.selectedSlotIdForAppointment = slotBeans.get(arg2).getId();
				DATA.print("-- slot id Re_assigned "+ DATA.selectedSlotIdForAppointment);
			}
		});
	}
	
	ArrayList<SlotBean> slotBeans;
	public void getDoctorsSlots(String drId,String aptmntDate) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("doctor_id", drId);
		params.put("appointment_date", aptmntDate);

		DATA.print("-- params in getDoctorsSlots: "+params.toString());

		pd.show();
		client.post(DATA.baseUrl+"/getDoctorsSlots/",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce in getDoctorsSlots: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						//String status = jsonObject.getString("status");
						JSONArray slots = jsonObject.getJSONArray("slots");

						slotBeans = new ArrayList<SlotBean>();
						SlotBean temp = null;

						if (slots.length() == 0) {
							Toast.makeText(activity, "No shedule added", Toast.LENGTH_SHORT).show();
							AlertDialog alertDialog =
									new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle(getResources().getString(R.string.app_name))
											.setMessage("Doctor has not added schedule. Please try again with a different date/time")//ask the doctor to add his schedule or
											.setPositiveButton("Done",null).create();
							alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialog) {
									finish();
								}
							});
							alertDialog.show();
						} else {
							for (int i = 0; i < slots.length(); i++) {
								String id = slots.getJSONObject(i).getString("id");
								String day = slots.getJSONObject(i).getString("day");
								String from_time = slots.getJSONObject(i).getString("from_time");
								String to_time = slots.getJSONObject(i).getString("to_time");
								String status = slots.getJSONObject(i).getString("status");

								temp = new SlotBean(id, day, from_time, to_time, status);
								slotBeans.add(temp);
								temp = null;
							}
							drScheduleAdapter = new DrScheduleAdapter(activity,slotBeans);
							lvDrSchedule.setAdapter(drScheduleAdapter);
						}


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getDoctorsSlots, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure getDoctorsSlots: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getDoctorsSlots

}
