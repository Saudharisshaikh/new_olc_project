package com.app.mdlive_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.DrScheduleBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomTimePickerDialog;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragmentBtn;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class DoctorScheduleNew extends BaseActivity implements OnClickListener,OnCheckedChangeListener{


	Activity activity;
	OpenActivity openActivity;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	SharedPreferences prefs;
	ProgressDialog pd;

	CheckBox cbMonday,cbTuesday,cbWednesday,cbThursday,cbFriday,cbSaturday,cbSunday,
			cbMondayMorning,cbMondayEvening,cbTuesdayMorning,cbTuesdayEvening,cbWednesdayMorning,cbWednesdayEvening,cbThursdayMorning,
			cbThursdayEvening,cbFridayMorning,cbFridayEvening,cbSaturdayMorning,cbSaturdayEvening,cbSundayMorning,cbSundayEvening;

	Button etFromMonday,etToMonday,etEveningFromMonday,etEveningToMonday,
			etFromTuesday,etToTuesday,etEveningFromTuesday,etEveningToTuesday,
			etFromWednesday,etToWednesday,etEveningFromWednesday,etEveningToWednesday,
			etFromThursday,etToThursday,etEveningFromThursday,etEveningToThursday,
			etFromFriday,etToFriday,etEveningFromFriday,etEveningToFriday,
			etFromSaturday,etToSaturday,etEveningFromSaturday,etEveningToSaturday,
			etFromSunday,etToSunday,etEveningFromSunday,etEveningToSunday,btnSaveSchedule,
			etFromDate,etToDate;

	ArrayList<DrScheduleBean> drScheduleBeansList;
	//ArrayList<DrScheduleBean> drScheduleBeansDefault;

	CheckBox cbAvailableForeLiveCare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_schedule_new);

		init();
		validate();
		if (checkInternetConnection.isConnectedToInternet()) {
			getDrSchedule(prefs.getString("id", ""));
		} else {
			Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
		}

	}

	public void init() {
		activity = DoctorScheduleNew.this;
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
		cbSunday = (CheckBox) findViewById(R.id.cbSunday);

		cbMondayMorning = (CheckBox) findViewById(R.id.cbMondayMorning);
		cbMondayEvening = (CheckBox) findViewById(R.id.cbMondayEvening);
		cbTuesdayMorning = (CheckBox) findViewById(R.id.cbTuesdayMorning);
		cbTuesdayEvening = (CheckBox) findViewById(R.id.cbTuesdayEvening);
		cbWednesdayMorning = (CheckBox) findViewById(R.id.cbWednesdayMorning);
		cbWednesdayEvening = (CheckBox) findViewById(R.id.cbWednesdayEvening);
		cbThursdayMorning = (CheckBox) findViewById(R.id.cbThursdayMorning);
		cbThursdayEvening = (CheckBox) findViewById(R.id.cbThursdayEvening);
		cbFridayMorning = (CheckBox) findViewById(R.id.cbFridayMorning);
		cbFridayEvening = (CheckBox) findViewById(R.id.cbFridayEvening);
		cbSaturdayMorning = (CheckBox) findViewById(R.id.cbSaturdayMorning);
		cbSaturdayEvening = (CheckBox) findViewById(R.id.cbSaturdayEvening);
		cbSundayMorning = (CheckBox) findViewById(R.id.cbSundayMorning);
		cbSundayEvening = (CheckBox) findViewById(R.id.cbSundayEvening);

		etFromMonday = (Button) findViewById(R.id.etFromMonday);
		etToMonday = (Button) findViewById(R.id.etToMonday);
		etEveningFromMonday = (Button) findViewById(R.id.etEveningFromMonday);
		etEveningToMonday = (Button) findViewById(R.id.etEveningToMonday);
		etFromTuesday = (Button) findViewById(R.id.etFromTuesday);
		etToTuesday = (Button) findViewById(R.id.etToTuesday);
		etEveningFromTuesday = (Button) findViewById(R.id.etEveningFromTuesday);
		etEveningToTuesday = (Button) findViewById(R.id.etEveningToTuesday);
		etFromWednesday = (Button) findViewById(R.id.etFromWednesday);
		etToWednesday = (Button) findViewById(R.id.etToWednesday);
		etEveningFromWednesday = (Button) findViewById(R.id.etEveningFromWednesday);
		etEveningToWednesday = (Button) findViewById(R.id.etEveningToWednesday);
		etFromThursday = (Button) findViewById(R.id.etFromThursday);
		etToThursday = (Button) findViewById(R.id.etToThursday);
		etEveningFromThursday = (Button) findViewById(R.id.etEveningFromThursday);
		etEveningToThursday = (Button) findViewById(R.id.etEveningToThursday);
		etFromFriday = (Button) findViewById(R.id.etFromFriday);
		etToFriday = (Button) findViewById(R.id.etToFriday);
		etEveningFromFriday = (Button) findViewById(R.id.etEveningFromFriday);
		etEveningToFriday = (Button) findViewById(R.id.etEveningToFriday);
		etFromSaturday = (Button) findViewById(R.id.etFromSaturday);
		etToSaturday = (Button) findViewById(R.id.etToSaturday);
		etEveningFromSaturday = (Button) findViewById(R.id.etEveningFromSaturday);
		etEveningToSaturday = (Button) findViewById(R.id.etEveningToSaturday);
		etFromSunday = (Button) findViewById(R.id.etFromSunday);
		etToSunday = (Button) findViewById(R.id.etToSunday);
		etEveningFromSunday = (Button) findViewById(R.id.etEveningFromSunday);
		etEveningToSunday = (Button) findViewById(R.id.etEveningToSunday);
		btnSaveSchedule = (Button) findViewById(R.id.btnSaveSchedule);
		etFromDate = (Button) findViewById(R.id.etFromDate);
		etToDate = (Button) findViewById(R.id.etToDate);

		etFromDate.setOnClickListener(this);
		etToDate.setOnClickListener(this);

		Date sDate = new Date();
		String sDateStr = new SimpleDateFormat("MM/dd/yyyy").format(sDate);
		etFromDate.setText(sDateStr);

		int noOfDays = 7; //i.e one weeks
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sDate);
		calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
		Date afterWeekDate = calendar.getTime();
		String toDateStr =  new SimpleDateFormat("MM/dd/yyyy").format(afterWeekDate);
		etToDate.setText(toDateStr);

		btnSaveSchedule.setOnClickListener(this);

		cbMonday.setOnCheckedChangeListener(this);
		cbTuesday.setOnCheckedChangeListener(this);
		cbWednesday.setOnCheckedChangeListener(this);
		cbThursday.setOnCheckedChangeListener(this);
		cbFriday.setOnCheckedChangeListener(this);
		cbSaturday.setOnCheckedChangeListener(this);
		cbSunday.setOnCheckedChangeListener(this);

		cbMondayMorning.setOnCheckedChangeListener(this);
		cbMondayEvening.setOnCheckedChangeListener(this);
		cbTuesdayMorning.setOnCheckedChangeListener(this);
		cbTuesdayEvening.setOnCheckedChangeListener(this);
		cbWednesdayMorning.setOnCheckedChangeListener(this);
		cbWednesdayEvening.setOnCheckedChangeListener(this);
		cbThursdayMorning.setOnCheckedChangeListener(this);
		cbThursdayEvening.setOnCheckedChangeListener(this);
		cbFridayMorning.setOnCheckedChangeListener(this);
		cbFridayEvening.setOnCheckedChangeListener(this);
		cbSaturdayMorning.setOnCheckedChangeListener(this);
		cbSaturdayEvening.setOnCheckedChangeListener(this);
		cbSundayMorning.setOnCheckedChangeListener(this);
		cbSundayEvening.setOnCheckedChangeListener(this);

		etFromMonday.setOnClickListener(this);
		etToMonday.setOnClickListener(this);
		etEveningFromMonday.setOnClickListener(this);
		etEveningToMonday.setOnClickListener(this);
		etFromTuesday.setOnClickListener(this);
		etToTuesday.setOnClickListener(this);
		etEveningFromTuesday.setOnClickListener(this);
		etEveningToTuesday.setOnClickListener(this);
		etFromWednesday.setOnClickListener(this);
		etToWednesday.setOnClickListener(this);
		etEveningFromWednesday.setOnClickListener(this);
		etEveningToWednesday.setOnClickListener(this);
		etFromThursday.setOnClickListener(this);
		etToThursday.setOnClickListener(this);
		etEveningFromThursday.setOnClickListener(this);
		etEveningToThursday.setOnClickListener(this);
		etFromFriday.setOnClickListener(this);
		etToFriday.setOnClickListener(this);
		etEveningFromFriday.setOnClickListener(this);
		etEveningToFriday.setOnClickListener(this);
		etFromSaturday.setOnClickListener(this);
		etToSaturday.setOnClickListener(this);
		etEveningFromSaturday.setOnClickListener(this);
		etEveningToSaturday.setOnClickListener(this);
		etFromSunday.setOnClickListener(this);
		etToSunday.setOnClickListener(this);
		etEveningFromSunday.setOnClickListener(this);
		etEveningToSunday.setOnClickListener(this);

		cbAvailableForeLiveCare = (CheckBox) findViewById(R.id.cbAvailableForeLiveCare);

		if(prefs.getString("is_available_onlinecare","").equalsIgnoreCase("1")){
			cbAvailableForeLiveCare.setChecked(true);
			is_available_onlinecare = "1";
		}else{
			cbAvailableForeLiveCare.setChecked(false);
			is_available_onlinecare = "0";
		}
		cbAvailableForeLiveCare.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					is_available_onlinecare = "1";
				}else {
					is_available_onlinecare = "0";
				}
			}
		});
	}
	String is_available_onlinecare = "0";
	/*public void loadDefList() {
		drScheduleBeansDefault = new ArrayList<DrScheduleBean>();
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Monday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Tuesday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Wednesday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Thursday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Friday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Saturday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		drScheduleBeansDefault.add(new DrScheduleBean("", prefs.getString("id", ""), "Sunday", "00 : 00", "00 : 00", "00 : 00", "00 : 00"));
		
	}*/


	public void getDrSchedule(String doctor_id) {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		/*RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);*/

		pd.show();
		client.get(DATA.baseUrl+"/getDoctorSchedule/"+doctor_id, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--responce in getDoctorSchedule: "+content);

					JSONObject mainObj;
					try {
						mainObj = new JSONObject(content);

						int success = mainObj.getInt("success");
						if (!mainObj.getString("schedule_start_date").contains("1970") ||
								!mainObj.getString("schedule_end_date").contains("1970")) {
							etFromDate.setText(mainObj.getString("schedule_start_date"));
							etToDate.setText(mainObj.getString("schedule_end_date"));
						}


						drScheduleBeansList = new ArrayList<DrScheduleBean>();
						DrScheduleBean drScheduleBean;

						JSONArray data = mainObj.getJSONArray("data");
						for (int i = 0; i < data.length(); i++) {
							JSONObject obj = data.getJSONObject(i);

							String id = obj.getString("id");
							String doctor_id= obj.getString("doctor_id");
							String day= obj.getString("day");
							String from_time= obj.getString("from_time");
							String to_time= obj.getString("to_time");
							String evening_from_time= obj.getString("evening_from_time");
							String evening_to_time= obj.getString("evening_to_time");
							String is_morning= obj.getString("is_morning");
							String is_evening= obj.getString("is_evening");

							drScheduleBean = new DrScheduleBean(id, doctor_id, day, from_time, to_time, evening_from_time, evening_to_time,is_morning,is_evening);
							drScheduleBeansList.add(drScheduleBean);

						}

						setData();
				/*DrScheduleAdapter drScheduleAdapter = new DrScheduleAdapter(activity,drScheduleBeansList);
				lvDrSchedule.setAdapter(drScheduleAdapter);*/

					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: getDoctorSchedule, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail getDoctorSchedule: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	public void setData() {
		if (drScheduleBeansList != null) {
			for (int i = 0; i < drScheduleBeansList.size(); i++) {
				String day = drScheduleBeansList.get(i).getDay();
				System.out.println("--day "+day);
				if (day.equalsIgnoreCase("Monday")) {
					cbMonday.setChecked(true);
					etFromMonday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToMonday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromMonday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToMonday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbMondayMorning.setChecked(true);
						etFromMonday.setEnabled(true);
						etToMonday.setEnabled(true);
					} else {
						cbMondayMorning.setChecked(false);
						etFromMonday.setEnabled(false);
						etToMonday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbMondayEvening.setChecked(true);
						etEveningFromMonday.setEnabled(true);
						etEveningToMonday.setEnabled(true);
					} else {
						cbMondayEvening.setChecked(false);
						etEveningFromMonday.setEnabled(false);
						etEveningToMonday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Tuesday")) {
					cbTuesday.setChecked(true);
					etFromTuesday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToTuesday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromTuesday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToTuesday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbTuesdayMorning.setChecked(true);
						etFromTuesday.setEnabled(true);
						etToTuesday.setEnabled(true);
					} else {
						cbTuesdayMorning.setChecked(false);
						etFromTuesday.setEnabled(false);
						etToTuesday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbTuesdayEvening.setChecked(true);
						etEveningFromTuesday.setEnabled(true);
						etEveningToTuesday.setEnabled(true);
					} else {
						cbTuesdayEvening.setChecked(false);
						etEveningFromTuesday.setEnabled(false);
						etEveningToTuesday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Wednesday")) {
					cbWednesday.setChecked(true);
					etFromWednesday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToWednesday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromWednesday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToWednesday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbWednesdayMorning.setChecked(true);
						etFromWednesday.setEnabled(true);
						etToWednesday.setEnabled(true);
					} else {
						cbWednesdayMorning.setChecked(false);
						etFromWednesday.setEnabled(false);
						etToWednesday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbWednesdayEvening.setChecked(true);
						etEveningFromWednesday.setEnabled(true);
						etEveningToWednesday.setEnabled(true);
					} else {
						cbWednesdayEvening.setChecked(false);
						etEveningFromWednesday.setEnabled(false);
						etEveningToWednesday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Thursday")) {
					cbThursday.setChecked(true);
					etFromThursday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToThursday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromThursday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToThursday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbThursdayMorning.setChecked(true);
						etFromThursday.setEnabled(true);
						etToThursday.setEnabled(true);
					} else {
						cbThursdayMorning.setChecked(false);
						etFromThursday.setEnabled(false);
						etToThursday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbThursdayEvening.setChecked(true);
						etEveningFromThursday.setEnabled(true);
						etEveningToThursday.setEnabled(true);
					} else {
						cbThursdayEvening.setChecked(false);
						etEveningFromThursday.setEnabled(false);
						etEveningToThursday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Friday")) {
					cbFriday.setChecked(true);
					etFromFriday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToFriday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromFriday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToFriday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbFridayMorning.setChecked(true);
						etFromFriday.setEnabled(true);
						etToFriday.setEnabled(true);
					} else {
						cbFridayMorning.setChecked(false);
						etFromFriday.setEnabled(false);
						etToFriday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbFridayEvening.setChecked(true);
						etEveningFromFriday.setEnabled(true);
						etEveningToFriday.setEnabled(true);
					} else {
						cbFridayEvening.setChecked(false);
						etEveningFromFriday.setEnabled(false);
						etEveningToFriday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Saturday")) {
					cbSaturday.setChecked(true);
					etFromSaturday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToSaturday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromSaturday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToSaturday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbSaturdayMorning.setChecked(true);
						etFromSaturday.setEnabled(true);
						etToSaturday.setEnabled(true);
					} else {
						cbSaturdayMorning.setChecked(false);
						etFromSaturday.setEnabled(false);
						etToSaturday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbSaturdayEvening.setChecked(true);
						etEveningFromSaturday.setEnabled(true);
						etEveningToSaturday.setEnabled(true);
					} else {
						cbSaturdayEvening.setChecked(false);
						etEveningFromSaturday.setEnabled(false);
						etEveningToSaturday.setEnabled(false);
					}
				}
				if (day.equalsIgnoreCase("Sunday")) {
					cbSunday.setChecked(true);
					etFromSunday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToSunday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromSunday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToSunday.setText(drScheduleBeansList.get(i).getEvening_to_time());

					if (drScheduleBeansList.get(i).getIs_morning().equalsIgnoreCase("1")) {
						cbSundayMorning.setChecked(true);
						etFromSunday.setEnabled(true);
						etToSunday.setEnabled(true);
					} else {
						cbSundayMorning.setChecked(false);
						etFromSunday.setEnabled(false);
						etToSunday.setEnabled(false);
					}
					if (drScheduleBeansList.get(i).getIs_evening().equalsIgnoreCase("1")) {
						cbSundayEvening.setChecked(true);
						etEveningFromSunday.setEnabled(true);
						etEveningToSunday.setEnabled(true);
					} else {
						cbSundayEvening.setChecked(false);
						etEveningFromSunday.setEnabled(false);
						etEveningToSunday.setEnabled(false);
					}
				}

				/*switch (day) {
				case "Monday":
					cbMonday.setChecked(true);
					etFromMonday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToMonday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromMonday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToMonday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Tuesday":
					cbTuesday.setChecked(true);
					etFromTuesday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToTuesday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromTuesday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToTuesday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Wednesday":
					cbWednesday.setChecked(true);
					etFromWednesday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToWednesday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromWednesday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToWednesday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Thursday":
					cbThursday.setChecked(true);
					etFromThursday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToThursday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromThursday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToThursday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Friday":
					cbFriday.setChecked(true);
					etFromFriday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToFriday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromFriday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToFriday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Saturday":
					cbSaturday.setChecked(true);
					etFromSaturday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToSaturday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromSaturday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToSaturday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;
					
				case "Sunday":
					cbSunday.setChecked(true);
					etFromSunday.setText(drScheduleBeansList.get(i).getFrom_time());
					etToSunday.setText(drScheduleBeansList.get(i).getTo_time());
					etEveningFromSunday.setText(drScheduleBeansList.get(i).getEvening_from_time());
					etEveningToSunday.setText(drScheduleBeansList.get(i).getEvening_to_time());
					break;

				default:
					break;
				}*/
			}
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.etFromMonday:
				//setTimeByTimePicker(etFromMonday);
				System.out.println("mins: "+Integer.parseInt(etFromMonday.getText().toString().toString().split(":")[0]));
				System.out.println("secs: "+Integer.parseInt(etFromMonday.getText().toString().toString().split(":")[1].split(" ")[0]));

				initCustomTimePicker(etFromMonday,Integer.parseInt(convertTo24Hour(etFromMonday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromMonday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;

			case R.id.etToMonday:
				//setTimeByTimePicker(etToMonday);
				initCustomTimePicker(etToMonday,Integer.parseInt(convertTo24Hour(etToMonday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToMonday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromMonday:
				initCustomTimePicker(etEveningFromMonday,Integer.parseInt(convertTo24Hour(etEveningFromMonday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromMonday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToMonday:
				initCustomTimePicker(etEveningToMonday,Integer.parseInt(convertTo24Hour(etEveningToMonday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToMonday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromTuesday:
				initCustomTimePicker(etFromTuesday,Integer.parseInt(convertTo24Hour(etFromTuesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromTuesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToTuesday:
				initCustomTimePicker(etToTuesday,Integer.parseInt(convertTo24Hour(etToTuesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToTuesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromTuesday:
				initCustomTimePicker(etEveningFromTuesday,Integer.parseInt(convertTo24Hour(etEveningFromTuesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromTuesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToTuesday:
				initCustomTimePicker(etEveningToTuesday,Integer.parseInt(convertTo24Hour(etEveningToTuesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToTuesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromWednesday:
				initCustomTimePicker(etFromWednesday,Integer.parseInt(convertTo24Hour(etFromWednesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromWednesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToWednesday:
				initCustomTimePicker(etToWednesday,Integer.parseInt(convertTo24Hour(etToWednesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToWednesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromWednesday:
				initCustomTimePicker(etEveningFromWednesday,Integer.parseInt(convertTo24Hour(etEveningFromWednesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromWednesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToWednesday:
				initCustomTimePicker(etEveningToWednesday,Integer.parseInt(convertTo24Hour(etEveningToWednesday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToWednesday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromThursday:
				initCustomTimePicker(etFromThursday,Integer.parseInt(convertTo24Hour(etFromThursday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromThursday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToThursday:
				initCustomTimePicker(etToThursday,Integer.parseInt(convertTo24Hour(etToThursday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToThursday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromThursday:
				initCustomTimePicker(etEveningFromThursday,Integer.parseInt(convertTo24Hour(etEveningFromThursday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromThursday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToThursday:
				initCustomTimePicker(etEveningToThursday,Integer.parseInt(convertTo24Hour(etEveningToThursday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToThursday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromFriday:
				initCustomTimePicker(etFromFriday,Integer.parseInt(convertTo24Hour(etFromFriday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromFriday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToFriday:
				initCustomTimePicker(etToFriday,Integer.parseInt(convertTo24Hour(etToFriday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToFriday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromFriday:
				initCustomTimePicker(etEveningFromFriday,Integer.parseInt(convertTo24Hour(etEveningFromFriday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromFriday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToFriday:
				initCustomTimePicker(etEveningToFriday,Integer.parseInt(convertTo24Hour(etEveningToFriday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToFriday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromSaturday:
				initCustomTimePicker(etFromSaturday,Integer.parseInt(convertTo24Hour(etFromSaturday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromSaturday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToSaturday:
				initCustomTimePicker(etToSaturday,Integer.parseInt(convertTo24Hour(etToSaturday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToSaturday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromSaturday:
				initCustomTimePicker(etEveningFromSaturday,Integer.parseInt(convertTo24Hour(etEveningFromSaturday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromSaturday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToSaturday:
				initCustomTimePicker(etEveningToSaturday,Integer.parseInt(convertTo24Hour(etEveningToSaturday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToSaturday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etFromSunday:
				initCustomTimePicker(etFromSunday,Integer.parseInt(convertTo24Hour(etFromSunday.getText().toString()).split(":")[0]),
						Integer.parseInt(etFromSunday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etToSunday:
				initCustomTimePicker(etToSunday,Integer.parseInt(convertTo24Hour(etToSunday.getText().toString()).split(":")[0]),
						Integer.parseInt(etToSunday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningFromSunday:
				initCustomTimePicker(etEveningFromSunday,Integer.parseInt(convertTo24Hour(etEveningFromSunday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningFromSunday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.etEveningToSunday:
				initCustomTimePicker(etEveningToSunday,Integer.parseInt(convertTo24Hour(etEveningToSunday.getText().toString()).split(":")[0]),
						Integer.parseInt(etEveningToSunday.getText().toString().toString().split(":")[1].split(" ")[0]));
				break;
			case R.id.btnSaveSchedule:

				if (checkInternetConnection.isConnectedToInternet()) {

					save_Schedule();

				} else {
					customToast.showToast("No internet connection. Can not save schedule. Please check internet connection and try again", 0, 1);
				}
			
			/*if (cbMonday.isChecked()) {
				
				if (etFromMonday.getText().toString().equalsIgnoreCase("00 : 00") || etToMonday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToMonday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromMonday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for monday", 0, 0);
				}
			} else
			if (cbTuesday.isChecked()) {
				
				if (etFromTuesday.getText().toString().equalsIgnoreCase("00 : 00") || etToTuesday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToTuesday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromTuesday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else
			if (cbWednesday.isChecked()) {
				
				if (etFromWednesday.getText().toString().equalsIgnoreCase("00 : 00") || etToWednesday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToWednesday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromWednesday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else
			if (cbThursday.isChecked()) {
				
				if (etFromThursday.getText().toString().equalsIgnoreCase("00 : 00") || etToThursday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToThursday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromThursday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else
			if (cbFriday.isChecked()) {
				
				if (etFromFriday.getText().toString().equalsIgnoreCase("00 : 00") || etToFriday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToFriday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromFriday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else
			if (cbSaturday.isChecked()) {
				
				if (etFromSaturday.getText().toString().equalsIgnoreCase("00 : 00") || etToSaturday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToSaturday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromSaturday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else
			if (cbSunday.isChecked()) {
				
				if (etFromSunday.getText().toString().equalsIgnoreCase("00 : 00") || etToSunday.getText().toString().equalsIgnoreCase("00 : 00")
						|| etEveningToSunday.getText().toString().equalsIgnoreCase("00 : 00") || etEveningFromSunday.getText().toString().equalsIgnoreCase("00 : 00")) {
					
				} else {
					customToast.showToast("Please set your schedule properly for tuesday", 0, 0);
				}
			} else{
			
			
			}*/


				break;

			case R.id.etFromDate:
				DialogFragment newFragment = new DatePickerFragmentBtn(etFromDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
				break;
			case R.id.etToDate:
				DialogFragment newFragment1 = new DatePickerFragmentBtn(etToDate);
				newFragment1.show(getSupportFragmentManager(), "datePicker");
				break;
			default:
				break;
		}
	}
	
	
	
	/*int hour;int minute;
	public void setTimeByTimePicker(final Button editText) {
		 Calendar mcurrentTime = Calendar.getInstance();
		 hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
         minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
         mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {
               // editText.setText( selectedHour + ":" + selectedMinute);
            	// TODO Auto-generated method stub
                hour = hourOfDay;
                minute = minute1;
                String timeSet = "";
                if (hour > 12) {
                hour -= 12;
                timeSet = "PM";
                } else if (hour == 0) {
                hour += 12;
                timeSet = "AM";
                } else if (hour == 12)
                timeSet = "PM";
                else
                timeSet = "AM";

                String min = "";
                if (minute < 10)
                min = "0" + minute ;
                else
                min = String.valueOf(minute);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hour).append(':')
                .append(min ).append(" ").append(timeSet).toString();
                editText.setText(aTime);

            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
	}*/





	public void save_Schedule() {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));

		params.put("schedule_start_date", etFromDate.getText().toString());
		params.put("schedule_end_date", etToDate.getText().toString());

		params.put("is_available_onlinecare",is_available_onlinecare);

		if (cbMonday.isChecked()) {

			if (!checkTime(etFromMonday.getText().toString(), etToMonday.getText().toString(), "Sorry you have entered the wrong time interval for monday morning")) {
				return;
			}
			if (!checkTime(etEveningFromMonday.getText().toString(), etEveningToMonday.getText().toString(), "Sorry you have entered the wrong time interval for monday evening")) {
				return;
			}

			params.put("days[Monday]", "Monday");
			params.put("from_time[Monday]", etFromMonday.getText().toString());
			params.put("to_time[Monday]", etToMonday.getText().toString());
			params.put("evening_from_time[Monday]", etEveningFromMonday.getText().toString());
			params.put("evening_to_time[Monday]",  etEveningToMonday.getText().toString());

			if (cbMondayMorning.isChecked()) {
				params.put("is_morning[Monday]", "1");
			} else {
				params.put("is_morning[Monday]", "0");
			}
			if (cbMondayEvening.isChecked()) {
				params.put("is_evening[Monday]", "1");
			} else {
				params.put("is_evening[Monday]", "0");
			}
		}
		if (cbTuesday.isChecked()) {

			if (!checkTime(etFromTuesday.getText().toString(), etToTuesday.getText().toString(), "Sorry you have entered the wrong time interval for tuesday morning")) {
				return;
			}
			//System.out.println("-- function returened "+checkTime(etEveningFromTuesday.getText().toString(), etEveningToTuesday.getText().toString(), "Sorry you have entered the wrong time interval for tuesday evening"));
			if (!checkTime(etEveningFromTuesday.getText().toString(), etEveningToTuesday.getText().toString(), "Sorry you have entered the wrong time interval for tuesday evening")) {
				return;
			}

			params.put("days[Tuesday]", "Tuesday");
			params.put("from_time[Tuesday]", etFromTuesday.getText().toString());
			params.put("to_time[Tuesday]", etToTuesday.getText().toString());
			params.put("evening_from_time[Tuesday]", etEveningFromTuesday.getText().toString());
			params.put("evening_to_time[Tuesday]",  etEveningToTuesday.getText().toString());

			if (cbTuesdayMorning.isChecked()) {
				params.put("is_morning[Tuesday]", "1");
			} else {
				params.put("is_morning[Tuesday]", "0");
			}
			if (cbTuesdayEvening.isChecked()) {
				params.put("is_evening[Tuesday]", "1");
			} else {
				params.put("is_evening[Tuesday]", "0");
			}
		}
		if (cbWednesday.isChecked()) {

			if (!checkTime(etFromWednesday.getText().toString(), etToWednesday.getText().toString(), "Sorry you have entered the wrong time interval for wednesday morning")) {
				return;
			}
			if (!checkTime(etEveningFromWednesday.getText().toString(), etEveningToWednesday.getText().toString(), "Sorry you have entered the wrong time interval for wednesday evening")) {
				return;
			}

			params.put("days[Wednesday]", "Wednesday");
			params.put("from_time[Wednesday]", etFromWednesday.getText().toString());
			params.put("to_time[Wednesday]", etToWednesday.getText().toString());
			params.put("evening_from_time[Wednesday]", etEveningFromWednesday.getText().toString());
			params.put("evening_to_time[Wednesday]",  etEveningToWednesday.getText().toString());

			if (cbWednesdayMorning.isChecked()) {
				params.put("is_morning[Wednesday]", "1");
			} else {
				params.put("is_morning[Wednesday]", "0");
			}
			if (cbWednesdayEvening.isChecked()) {
				params.put("is_evening[Wednesday]", "1");
			} else {
				params.put("is_evening[Wednesday]", "0");
			}
		}
		if (cbThursday.isChecked()) {

			if (!checkTime(etFromThursday.getText().toString(), etToThursday.getText().toString(), "Sorry you have entered the wrong time interval for thursday morning")) {
				return;
			}
			if (!checkTime(etEveningFromThursday.getText().toString(), etEveningToThursday.getText().toString(), "Sorry you have entered the wrong time interval for thursday evening")) {
				return;
			}

			params.put("days[Thursday]", "Thursday");
			params.put("from_time[Thursday]", etFromThursday.getText().toString());
			params.put("to_time[Thursday]", etToThursday.getText().toString());
			params.put("evening_from_time[Thursday]", etEveningFromThursday.getText().toString());
			params.put("evening_to_time[Thursday]",  etEveningToThursday.getText().toString());

			if (cbThursdayMorning.isChecked()) {
				params.put("is_morning[Thursday]", "1");
			} else {
				params.put("is_morning[Thursday]", "0");
			}
			if (cbThursdayEvening.isChecked()) {
				params.put("is_evening[Thursday]", "1");
			} else {
				params.put("is_evening[Thursday]", "0");
			}
		}
		if (cbFriday.isChecked()) {

			if (!checkTime(etFromFriday.getText().toString(), etToFriday.getText().toString(), "Sorry you have entered the wrong time interval for friday morning")) {
				return;
			}
			if (!checkTime(etEveningFromFriday.getText().toString(), etEveningToFriday.getText().toString(), "Sorry you have entered the wrong time interval for friday evening")) {
				return;
			}

			params.put("days[Friday]", "Friday");
			params.put("from_time[Friday]", etFromFriday.getText().toString());
			params.put("to_time[Friday]", etToFriday.getText().toString());
			params.put("evening_from_time[Friday]", etEveningFromFriday.getText().toString());
			params.put("evening_to_time[Friday]",  etEveningToFriday.getText().toString());

			if (cbFridayMorning.isChecked()) {
				params.put("is_morning[Friday]", "1");
			} else {
				params.put("is_morning[Friday]", "0");
			}
			if (cbFridayEvening.isChecked()) {
				params.put("is_evening[Friday]", "1");
			} else {
				params.put("is_evening[Friday]", "0");
			}
		}
		if (cbSaturday.isChecked()) {

			if (!checkTime(etFromSaturday.getText().toString(), etToSaturday.getText().toString(), "Sorry you have entered the wrong time interval for saturday morning")) {
				return;
			}
			if (!checkTime(etEveningFromSaturday.getText().toString(), etEveningToSaturday.getText().toString(), "Sorry you have entered the wrong time interval for saturday evening")) {
				return;
			}

			params.put("days[Saturday]", "Saturday");
			params.put("from_time[Saturday]", etFromSaturday.getText().toString());
			params.put("to_time[Saturday]", etToSaturday.getText().toString());
			params.put("evening_from_time[Saturday]", etEveningFromSaturday.getText().toString());
			params.put("evening_to_time[Saturday]",  etEveningToSaturday.getText().toString());

			if (cbSaturdayMorning.isChecked()) {
				params.put("is_morning[Saturday]", "1");
			} else {
				params.put("is_morning[Saturday]", "0");
			}
			if (cbSaturdayEvening.isChecked()) {
				params.put("is_evening[Saturday]", "1");
			} else {
				params.put("is_evening[Saturday]", "0");
			}
		}
		if (cbSunday.isChecked()) {

			if (!checkTime(etFromSunday.getText().toString(), etToSunday.getText().toString(), "Sorry you have entered the wrong time interval for sunday morning")) {
				return;
			}
			if (!checkTime(etEveningFromSunday.getText().toString(), etEveningToSunday.getText().toString(), "Sorry you have entered the wrong time interval for sunday evening")) {
				return;
			}

			params.put("days[Sunday]", "Sunday");
			params.put("from_time[Sunday]", etFromSunday.getText().toString());
			params.put("to_time[Sunday]", etToSunday.getText().toString());
			params.put("evening_from_time[Sunday]", etEveningFromSunday.getText().toString());
			params.put("evening_to_time[Sunday]",  etEveningToSunday.getText().toString());

			if (cbSundayMorning.isChecked()) {
				params.put("is_morning[Sunday]", "1");
			} else {
				params.put("is_morning[Sunday]", "0");
			}
			if (cbSundayEvening.isChecked()) {
				params.put("is_evening[Sunday]", "1");
			} else {
				params.put("is_evening[Sunday]", "0");
			}
		}

		System.out.println("-- Params in save_Schedule: "+params.toString());
		pd.show();
		client.post(DATA.baseUrl+"/save_Schedule/", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--responce save_Schedule"+content);
					//--responce save_Schedule{"success":1,"messsage":"Saved.."}

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Schedule saved successfully", 0, 1);

							prefs.edit().putString("is_available_onlinecare",is_available_onlinecare).commit();

							if (checkInternetConnection.isConnectedToInternet()) {
								getDrSchedule(prefs.getString("id", ""));
							} else {
								customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
							}

						} else {
							// --responce save_Schedule{"error":1,"message":"System does not allow to change your schedule, your appointment already fixed."}

							customToast.showToast(jsonObject.getString("message"), 0, 1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: save_Schedule, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail save_Schedule "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (checkBox.getId()) {
			case R.id.cbMonday:
				if (isChecked) {
				/*etFromMonday.setEnabled(true);
				etToMonday.setEnabled(true);
				etEveningFromMonday.setEnabled(true);
				etEveningToMonday.setEnabled(true);*/
				} else {
					cbMondayMorning.setChecked(false);
					cbMondayEvening.setChecked(false);
				/*etFromMonday.setEnabled(false);
				etToMonday.setEnabled(false);
				etEveningFromMonday.setEnabled(false);
				etEveningToMonday.setEnabled(false);*/
				/*etFromMonday.setText("00 : 00");
				etToMonday.setText("00 : 00");
				etEveningFromMonday.setText("00 : 00");
				etEveningToMonday.setText("00 : 00");*/
				}

				break;

			case R.id.cbTuesday:
				if (isChecked) {
				/*etFromTuesday.setEnabled(true);
				etToTuesday.setEnabled(true);
				etEveningFromTuesday.setEnabled(true);
				etEveningToTuesday.setEnabled(true);*/
				} else {
					cbTuesdayMorning.setChecked(false);
					cbTuesdayEvening.setChecked(false);
				/*etFromTuesday.setEnabled(false);
				etToTuesday.setEnabled(false);
				etEveningFromTuesday.setEnabled(false);
				etEveningToTuesday.setEnabled(false);*/
				/*etFromTuesday.setText("00 : 00");
				etToTuesday.setText("00 : 00");
				etEveningFromTuesday.setText("00 : 00");
				etEveningToTuesday.setText("00 : 00");*/
				}

				break;
			case R.id.cbWednesday:
				if (isChecked) {
				/*etFromWednesday.setEnabled(true);
				etToWednesday.setEnabled(true);
				etEveningFromWednesday.setEnabled(true);
				etEveningToWednesday.setEnabled(true);*/
				} else {
					cbWednesdayMorning.setChecked(false);
					cbWednesdayEvening.setChecked(false);
				/*etFromWednesday.setEnabled(false);
				etToWednesday.setEnabled(false);
				etEveningFromWednesday.setEnabled(false);
				etEveningToWednesday.setEnabled(false);*/
				/*etFromWednesday.setText("00 : 00");
				etToWednesday.setText("00 : 00");
				etEveningFromWednesday.setText("00 : 00");
				etEveningToWednesday.setText("00 : 00");*/
				}

				break;
			case R.id.cbThursday:
				if (isChecked) {
				/*etFromThursday.setEnabled(true);
				etToThursday.setEnabled(true);
				etEveningFromThursday.setEnabled(true);
				etEveningToThursday.setEnabled(true);*/
				} else {
					cbThursdayMorning.setChecked(false);
					cbThursdayEvening.setChecked(false);
				/*etFromThursday.setEnabled(false);
				etToThursday.setEnabled(false);
				etEveningFromThursday.setEnabled(false);
				etEveningToThursday.setEnabled(false);*/
				/*etFromThursday.setText("00 : 00");
				etToThursday.setText("00 : 00");
				etEveningFromThursday.setText("00 : 00");
				etEveningToThursday.setText("00 : 00");*/
				}

				break;
			case R.id.cbFriday:
				if (isChecked) {
				/*etFromFriday.setEnabled(true);
				etToFriday.setEnabled(true);
				etEveningFromFriday.setEnabled(true);
				etEveningToFriday.setEnabled(true);*/
				} else {
					cbFridayMorning.setChecked(false);
					cbFridayEvening.setChecked(false);
				/*etFromFriday.setEnabled(false);
				etToFriday.setEnabled(false);
				etEveningFromFriday.setEnabled(false);
				etEveningToFriday.setEnabled(false);*/
				/*etFromFriday.setText("00 : 00");
				etToFriday.setText("00 : 00");
				etEveningFromFriday.setText("00 : 00");
				etEveningToFriday.setText("00 : 00");*/
				}

				break;
			case R.id.cbSaturday:
				if (isChecked) {
				/*etFromSaturday.setEnabled(true);
				etToSaturday.setEnabled(true);
				etEveningFromSaturday.setEnabled(true);
				etEveningToSaturday.setEnabled(true);*/
				} else {
					cbSaturdayMorning.setChecked(false);
					cbSaturdayEvening.setChecked(false);
				/*etFromSaturday.setEnabled(false);
				etToSaturday.setEnabled(false);
				etEveningFromSaturday.setEnabled(false);
				etEveningToSaturday.setEnabled(false);*/
				/*etFromSaturday.setText("00 : 00");
				etToSaturday.setText("00 : 00");
				etEveningFromSaturday.setText("00 : 00");
				etEveningToSaturday.setText("00 : 00");*/
				}

				break;
			case R.id.cbSunday:
				if (isChecked) {
				/*etFromSunday.setEnabled(true);
				etToSunday.setEnabled(true);
				etEveningFromSunday.setEnabled(true);
				etEveningToSunday.setEnabled(true);*/
				} else {
					cbSundayMorning.setChecked(false);
					cbSundayEvening.setChecked(false);
				/*etFromSunday.setEnabled(false);
				etToSunday.setEnabled(false);
				etEveningFromSunday.setEnabled(false);
				etEveningToSunday.setEnabled(false);*/
				/*etFromSunday.setText("00 : 00");
				etToSunday.setText("00 : 00");
				etEveningFromSunday.setText("00 : 00");
				etEveningToSunday.setText("00 : 00");*/
				}

				break;
			case R.id.cbMondayMorning:
				if (cbMonday.isChecked()) {
					if (isChecked) {
						etFromMonday.setEnabled(true);
						etToMonday.setEnabled(true);
					} else {
						etFromMonday.setEnabled(false);
						etToMonday.setEnabled(false);
					}
				} else {
					cbMondayMorning.setChecked(false);
					etFromMonday.setEnabled(false);
					etToMonday.setEnabled(false);
					Toast.makeText(activity, "Please set the monday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbMondayEvening:

				if (cbMonday.isChecked()) {
					if (isChecked) {
						etEveningFromMonday.setEnabled(true);
						etEveningToMonday.setEnabled(true);
					} else {
						etEveningFromMonday.setEnabled(false);
						etEveningToMonday.setEnabled(false);
					}
				} else {
					cbMondayEvening.setChecked(false);
					etEveningFromMonday.setEnabled(false);
					etEveningToMonday.setEnabled(false);
					Toast.makeText(activity, "Please set the monday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbTuesdayMorning:

				if (cbTuesday.isChecked()) {
					if (isChecked) {
						etFromTuesday.setEnabled(true);
						etToTuesday.setEnabled(true);
					} else {
						etFromTuesday.setEnabled(false);
						etToTuesday.setEnabled(false);
					}
				} else {
					cbTuesdayMorning.setChecked(false);
					etFromTuesday.setEnabled(false);
					etToTuesday.setEnabled(false);
					Toast.makeText(activity, "Please set the tuesday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbTuesdayEvening:

				if (cbTuesday.isChecked()) {
					if (isChecked) {
						etEveningFromTuesday.setEnabled(true);
						etEveningToTuesday.setEnabled(true);
					} else {
						etEveningFromTuesday.setEnabled(false);
						etEveningToTuesday.setEnabled(false);
					}
				} else {
					cbTuesdayEvening.setChecked(false);
					etEveningFromTuesday.setEnabled(false);
					etEveningToTuesday.setEnabled(false);
					Toast.makeText(activity, "Please set the tuesday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbWednesdayMorning:

				if (cbWednesday.isChecked()) {
					if (isChecked) {
						etFromWednesday.setEnabled(true);
						etToWednesday.setEnabled(true);
					} else {
						etFromWednesday.setEnabled(false);
						etToWednesday.setEnabled(false);
					}
				} else {
					cbWednesdayMorning.setChecked(false);
					etFromWednesday.setEnabled(false);
					etToWednesday.setEnabled(false);
					Toast.makeText(activity, "Please set the Wednesay to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbWednesdayEvening:

				if (cbWednesday.isChecked()) {
					if (isChecked) {
						etEveningFromWednesday.setEnabled(true);
						etEveningToWednesday.setEnabled(true);
					} else {
						etEveningFromWednesday.setEnabled(false);
						etEveningToWednesday.setEnabled(false);
					}
				} else {
					cbWednesdayEvening.setChecked(false);
					etEveningFromWednesday.setEnabled(false);
					etEveningToWednesday.setEnabled(false);
					Toast.makeText(activity, "Please set the Wednesday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbThursdayMorning:

				if (cbThursday.isChecked()) {
					if (isChecked) {
						etFromThursday.setEnabled(true);
						etToThursday.setEnabled(true);
					} else {
						etFromThursday.setEnabled(false);
						etToThursday.setEnabled(false);
					}
				} else {
					cbThursdayMorning.setChecked(false);
					etFromThursday.setEnabled(false);
					etToThursday.setEnabled(false);
					Toast.makeText(activity, "Please set the Thursday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbThursdayEvening:

				if (cbThursday.isChecked()) {
					if (isChecked) {
						etEveningFromThursday.setEnabled(true);
						etEveningToThursday.setEnabled(true);
					} else {
						etEveningFromThursday.setEnabled(false);
						etEveningToThursday.setEnabled(false);
					}
				} else {
					cbThursdayEvening.setChecked(false);
					etEveningFromThursday.setEnabled(false);
					etEveningToThursday.setEnabled(false);
					Toast.makeText(activity, "Please set the Thursday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbFridayMorning:

				if (cbFriday.isChecked()) {
					if (isChecked) {
						etFromFriday.setEnabled(true);
						etToFriday.setEnabled(true);
					} else {
						etFromFriday.setEnabled(false);
						etToFriday.setEnabled(false);
					}
				} else {
					cbFridayMorning.setChecked(false);
					etFromFriday.setEnabled(false);
					etToFriday.setEnabled(false);
					Toast.makeText(activity, "Please set the Friday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbFridayEvening:

				if (cbFriday.isChecked()) {
					if (isChecked) {
						etEveningFromFriday.setEnabled(true);
						etEveningToFriday.setEnabled(true);
					} else {
						etEveningFromFriday.setEnabled(false);
						etEveningToFriday.setEnabled(false);
					}
				} else {
					cbFridayEvening.setChecked(false);
					etEveningFromFriday.setEnabled(false);
					etEveningToFriday.setEnabled(false);
					Toast.makeText(activity, "Please set the Friday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbSaturdayMorning:

				if (cbSaturday.isChecked()) {
					if (isChecked) {
						etFromSaturday.setEnabled(true);
						etToSaturday.setEnabled(true);
					} else {
						etFromSaturday.setEnabled(false);
						etToSaturday.setEnabled(false);
					}
				} else {
					cbSaturdayMorning.setChecked(false);
					etFromSaturday.setEnabled(false);
					etToSaturday.setEnabled(false);
					Toast.makeText(activity, "Please set the Saturday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbSaturdayEvening:

				if (cbSaturday.isChecked()) {
					if (isChecked) {
						etEveningFromSaturday.setEnabled(true);
						etEveningToSaturday.setEnabled(true);
					} else {
						etEveningFromSaturday.setEnabled(false);
						etEveningToSaturday.setEnabled(false);
					}
				} else {
					cbSaturdayEvening.setChecked(false);
					etEveningFromSaturday.setEnabled(false);
					etEveningToSaturday.setEnabled(false);
					Toast.makeText(activity, "Please set the Saturday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbSundayMorning:

				if (cbSunday.isChecked()) {
					if (isChecked) {
						etFromSunday.setEnabled(true);
						etToSunday.setEnabled(true);
					} else {
						etFromSunday.setEnabled(false);
						etToSunday.setEnabled(false);
					}
				} else {
					cbSundayMorning.setChecked(false);
					etFromSunday.setEnabled(false);
					etToSunday.setEnabled(false);
					Toast.makeText(activity, "Please set the Sunday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.cbSundayEvening:

				if (cbSunday.isChecked()) {
					if (isChecked) {
						etEveningFromSunday.setEnabled(true);
						etEveningToSunday.setEnabled(true);
					} else {
						etEveningFromSunday.setEnabled(false);
						etEveningToSunday.setEnabled(false);
					}
				} else {
					cbSundayEvening.setChecked(false);
					etEveningFromSunday.setEnabled(false);
					etEveningToSunday.setEnabled(false);
					Toast.makeText(activity, "Please set the monday to on before select time", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
		}
	}



	public void validate() {
		if (cbMonday.isChecked()) {
			etFromMonday.setEnabled(true);
			etToMonday.setEnabled(true);
			etEveningFromMonday.setEnabled(true);
			etEveningToMonday.setEnabled(true);
		} else {
			etFromMonday.setEnabled(false);
			etToMonday.setEnabled(false);
			etEveningFromMonday.setEnabled(false);
			etEveningToMonday.setEnabled(false);
			/*etFromMonday.setText("00 : 00");
			etToMonday.setText("00 : 00");
			etEveningFromMonday.setText("00 : 00");
			etEveningToMonday.setText("00 : 00");*/
		}

		if (cbTuesday.isChecked()) {
			etFromTuesday.setEnabled(true);
			etToTuesday.setEnabled(true);
			etEveningFromTuesday.setEnabled(true);
			etEveningToTuesday.setEnabled(true);
		} else {
			etFromTuesday.setEnabled(false);
			etToTuesday.setEnabled(false);
			etEveningFromTuesday.setEnabled(false);
			etEveningToTuesday.setEnabled(false);
			/*etFromTuesday.setText("00 : 00");
			etToTuesday.setText("00 : 00");
			etEveningFromTuesday.setText("00 : 00");
			etEveningToTuesday.setText("00 : 00");*/
		}

		if (cbWednesday.isChecked()) {
			etFromWednesday.setEnabled(true);
			etToWednesday.setEnabled(true);
			etEveningFromWednesday.setEnabled(true);
			etEveningToWednesday.setEnabled(true);
		} else {
			etFromWednesday.setEnabled(false);
			etToWednesday.setEnabled(false);
			etEveningFromWednesday.setEnabled(false);
			etEveningToWednesday.setEnabled(false);
			/*etFromWednesday.setText("00 : 00");
			etToWednesday.setText("00 : 00");
			etEveningFromWednesday.setText("00 : 00");
			etEveningToWednesday.setText("00 : 00");*/
		}

		if (cbThursday.isChecked()) {
			etFromThursday.setEnabled(true);
			etToThursday.setEnabled(true);
			etEveningFromThursday.setEnabled(true);
			etEveningToThursday.setEnabled(true);
		} else {
			etFromThursday.setEnabled(false);
			etToThursday.setEnabled(false);
			etEveningFromThursday.setEnabled(false);
			etEveningToThursday.setEnabled(false);
			/*etFromThursday.setText("00 : 00");
			etToThursday.setText("00 : 00");
			etEveningFromThursday.setText("00 : 00");
			etEveningToThursday.setText("00 : 00");*/
		}

		if (cbFriday.isChecked()) {
			etFromFriday.setEnabled(true);
			etToFriday.setEnabled(true);
			etEveningFromFriday.setEnabled(true);
			etEveningToFriday.setEnabled(true);
		} else {
			etFromFriday.setEnabled(false);
			etToFriday.setEnabled(false);
			etEveningFromFriday.setEnabled(false);
			etEveningToFriday.setEnabled(false);
			/*etFromFriday.setText("00 : 00");
			etToFriday.setText("00 : 00");
			etEveningFromFriday.setText("00 : 00");
			etEveningToFriday.setText("00 : 00");*/
		}

		if (cbSaturday.isChecked()) {
			etFromSaturday.setEnabled(true);
			etToSaturday.setEnabled(true);
			etEveningFromSaturday.setEnabled(true);
			etEveningToSaturday.setEnabled(true);
		} else {
			etFromSaturday.setEnabled(false);
			etToSaturday.setEnabled(false);
			etEveningFromSaturday.setEnabled(false);
			etEveningToSaturday.setEnabled(false);
			/*etFromSaturday.setText("00 : 00");
			etToSaturday.setText("00 : 00");
			etEveningFromSaturday.setText("00 : 00");
			etEveningToSaturday.setText("00 : 00");*/
		}

		if (cbSunday.isChecked()) {
			etFromSunday.setEnabled(true);
			etToSunday.setEnabled(true);
			etEveningFromSunday.setEnabled(true);
			etEveningToSunday.setEnabled(true);
		} else {
			etFromSunday.setEnabled(false);
			etToSunday.setEnabled(false);
			etEveningFromSunday.setEnabled(false);
			etEveningToSunday.setEnabled(false);
			/*etFromSunday.setText("00 : 00");
			etToSunday.setText("00 : 00");
			etEveningFromSunday.setText("00 : 00");
			etEveningToSunday.setText("00 : 00");*/
		}

	}



	int hour;int minute;
	public void initCustomTimePicker(final Button et,int hh,int mm) {

		CustomTimePickerDialog customTimePickerDialog =
				new CustomTimePickerDialog(activity, new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {

						hour = hourOfDay;
						minute = minute1;
						String timeSet = "";
						if (hour > 12) {
							hour -= 12;
							timeSet = "PM";
						} else if (hour == 0) {
							hour += 12;
							timeSet = "AM";
						} else if (hour == 12)
							timeSet = "PM";
						else
							timeSet = "AM";

						String min = "";
						if (minute < 10)
							min = "0" + minute ;
						else
							min = String.valueOf(minute);

						// Append in a StringBuilder
						String aTime = new StringBuilder().append(hour).append(':')
								.append(min ).append(" ").append(timeSet).toString();
	                /*String aTime = "";
	                if (hour < 10) {
	                	 aTime = new StringBuilder().append("0").append(hour).append(':')
	        	                .append(min ).append(" ").append(timeSet).toString();
					} else {
						 aTime = new StringBuilder().append(hour).append(':')
				                .append(min ).append(" ").append(timeSet).toString();
					}*/
						et.setText(aTime);
					}
				}, hh , mm , false);//Calendar.HOUR_OF_DAY,Calendar.MINUTE
		customTimePickerDialog.setTitle("Select time");
		customTimePickerDialog.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_dr_shedule, menu);

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		//return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {
			if (checkInternetConnection.isConnectedToInternet()) {

				save_Schedule();

			} else {
				customToast.showToast("No internet connection. Can not save schedule. Please check internet connection and try again", 0, 1);
			}
		}
		return super.onOptionsItemSelected(item);
	}



	public boolean checkTime(String startTime,String endTime,String msg) {
		/*if (startTime.equalsIgnoreCase("00 : 00") || endTime.equalsIgnoreCase("00 : 00")) {
			Toast.makeText(activity, "Invalid time 00 : 00", 0).show();
			return false;		
		}*/

		try {
			SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
			Date date1 = format.parse(startTime);
			Date date2 = format.parse(endTime);
			long difference = date2.getTime() - date1.getTime();
			System.out.println("--date1 "+date1+" milis: "+date1.getTime());
			System.out.println("--date2 "+date2+" milis: "+date2.getTime());
			System.out.println("--time diff "+difference);
			if (difference < 0) {
				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						//.setTitle("Confirm")
						.setMessage(msg)
						.setPositiveButton("Ok", null)
						//.setNegativeButton("Not Now", null)
						.create()
						.show();
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public String convertTo24Hour(String am_pm_Time){
		SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.US);
		SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a",Locale.US);
		Date date = null;
		try {
			date = parseFormat.parse(am_pm_Time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
		return displayFormat.format(date);
	}
}
