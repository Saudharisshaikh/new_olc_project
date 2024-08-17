package com.app.OnlineCareUS_MA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;

import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class SetDrShedule extends BaseActivity implements OnCheckedChangeListener {

	ProgressDialog pd;
	SharedPreferences prefs;

	Button etFrom,etTo,etEveningFrom,etEveningTo;
	CheckBox cbMonday,cbTuesday,cbWednesday,cbThursday,
			cbFriday,cbSatureday,cbSunday;
	Button btnSubmit;

	String days="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_dr_shedule);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		etFrom = (Button) findViewById(R.id.etFrom);
		etTo = (Button) findViewById(R.id.etTo);
		etEveningFrom = (Button) findViewById(R.id.etEveningFrom);
		etEveningTo = (Button) findViewById(R.id.etEveningTo);
		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSatureday = (CheckBox) findViewById(R.id.cbSaterday);
		cbSunday = (CheckBox) findViewById(R.id.cbSunday);

		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		//spShift = (Spinner) findViewById(R.id.spShift);

		cbMonday.setOnCheckedChangeListener(this);
		cbTuesday.setOnCheckedChangeListener(this);
		cbWednesday.setOnCheckedChangeListener(this);
		cbThursday.setOnCheckedChangeListener(this);
		cbFriday.setOnCheckedChangeListener(this);
		cbSatureday.setOnCheckedChangeListener(this);
		cbSunday.setOnCheckedChangeListener(this);



		
		/*etFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus) {
				setTimeByTimePicker(etFrom);
				}
			}
		});*/
		etFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setTimeByTimePicker(etFrom);

			}
		});
		
		/*etTo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus) {
				setTimeByTimePicker(etTo);
				}
			}
		});*/
		etTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setTimeByTimePicker(etTo);

			}
		});
	
		/*etEveningFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus) {
				setTimeByTimePicker(etEveningFrom);
				}
				
			}
		});*/
		etEveningFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setTimeByTimePicker(etEveningFrom);

			}
		});
		 
		/*etEveningTo.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus) {
				setTimeByTimePicker(etEveningTo);
				}
				
			}
		});*/
		etEveningTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setTimeByTimePicker(etEveningTo);

			}
		});


		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String doctor_id= prefs.getString("id", "");
				String from_time = etFrom.getText().toString();
				String to_time = etTo.getText().toString();
				String evening_from_time= etEveningFrom.getText().toString();
				String evening_to_time= etEveningTo.getText().toString();


				sendShedule(doctor_id, days, from_time, to_time, evening_from_time, evening_to_time);



			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_dr_shedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save_schedule) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}




	public void sendShedule(String doctor_id,String days,String from_time,String to_time,String evening_from_time,String evening_to_time) {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);
		params.put("days", days);
		params.put("from_time", from_time);
		params.put("to_time", to_time);
		params.put("evening_from_time", evening_from_time);
		params.put("evening_to_time", evening_to_time);

		pd.show();

		String reqURL = DATA.baseUrl+"/saveSchedule";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {



			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce "+content);
					//--responce {"error":1,"message":"Already added."}
					//--responce {"success":1,"messsage":"Saved.."}
					try {
						JSONObject obj = new JSONObject(content);
						String msg = "";
						if (obj.has("messsage")) {
							msg = obj.getString("messsage");
						} else if(obj.has("message")) {
							msg = obj.getString("message");
						}

						if (obj.has("error")) {
							new AlertDialog.Builder(SetDrShedule.this, R.style.CustomAlertDialogTheme)
									.setTitle("Error")
									.setMessage(msg)
									.setPositiveButton("Ok", null)
									//.setNegativeButton("Not Now", null)
									.create()
									.show();

						} else {
							new AlertDialog.Builder(SetDrShedule.this, R.style.CustomAlertDialogTheme)
									.setTitle("Success")
									.setMessage(msg)
									.setPositiveButton("Ok", null)
									//.setNegativeButton("Not Now", null)
									.create()
									.show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
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
				pd.dismiss();
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
	}

	@Override
	public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
		switch (checkBox.getId()) {
			case R.id.cbMonday:
				if (isChecked) {
					days= days+"Monday,";
				}else {
					days = days.replace("Monday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbTuesday:
				if (isChecked) {
					days= days+"Tuesday,";
				}else {
					days = days.replace("Tuesday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbWednesday:
				if (isChecked) {
					days= days+"Wednesday,";
				}else {
					days = days.replace("Wednesday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbThursday:
				if (isChecked) {
					days= days+"Thursday,";
				}else {
					days = days.replace("Thursday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbFriday:
				if (isChecked) {
					days= days+"Friday,";
				}else {
					days = days.replace("Friday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbSaterday:
				if (isChecked) {
					days= days+"Saterday,";
				}else {
					days = days.replace("Saterday,", "");
				}
				DATA.print("days "+days);
				break;
			case R.id.cbSunday:
				if (isChecked) {
					days= days+"Sunday,";
				}else {
					days = days.replace("Sunday,", "");
				}
				DATA.print("days "+days);
				break;

			default:
				break;
		}

	}


	/*public void setTimeByTimePicker(final EditText editText) {
		 Calendar mcurrentTime = Calendar.getInstance();
         int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
         int minute = mcurrentTime.get(Calendar.MINUTE);
         TimePickerDialog mTimePicker;
          mTimePicker = new TimePickerDialog(SetDrShedule.this, new TimePickerDialog.OnTimeSetListener() {
             @Override
             public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                 editText.setText( selectedHour + ":" + selectedMinute);
             }
         }, hour, minute, true);//Yes 24 hour time
         mTimePicker.setTitle("Select Time");
         mTimePicker.show();
	}*/
	int hour;int minute;
	public void setTimeByTimePicker(final Button editText) {
		Calendar mcurrentTime = Calendar.getInstance();
		hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(SetDrShedule.this, new TimePickerDialog.OnTimeSetListener() {
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
	}


}
