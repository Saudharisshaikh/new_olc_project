package com.app.msu_cp.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.app.msu_cp.ActivityFollowUps_3;
import com.app.msu_cp.ActivityProgressNotesView;
import com.app.msu_cp.ActivitySOAP;
import com.app.msu_cp.R;
import com.app.msu_cp.reliance.idtnote.ActivityIDTnoteList;
import com.app.msu_cp.reliance.medication.ActivityMedicationList;
import com.app.msu_cp.reliance.therapist.ActivityDoctorSlots;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	
	
	EditText editText;
	public DatePickerFragment(EditText editText) {
		// TODO Auto-generated constructor stub
		this.editText = editText;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), R.style.DatePickerTheme, this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		month = month +1;

		DATA.isDateSelected = true;
		//DATA.date = year + "-" + month + "-" + day;
		DATA.date =  month + "/" + day + "/" + year;

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yyyy");
			Date d = simpleDateFormat.parse(DATA.date);
			//SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
			//SimpleDateFormat sdf = new SimpleDateFormat(sharedPrefsHelper.get("date_formate",Constants.DATE_FORMATE_DISPLAY));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
			DATA.date = sdf.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		editText.setText(DATA.date);

		if(getActivity() instanceof ActivitySOAP){
			((ActivitySOAP)getActivity()).getData();
		}else if(getActivity() instanceof ActivityProgressNotesView){
			((ActivityProgressNotesView)getActivity()).getPrNotes();
		}else if(getActivity() instanceof ActivityIDTnoteList){
			((ActivityIDTnoteList) getActivity()).loadIDTnoteByDate(DATA.date);
		}else if(getActivity() instanceof ActivityFollowUps_3){
			((ActivityFollowUps_3) getActivity()).careprovider_schedules(DATA.date);
		}else if(getActivity() instanceof ActivityDoctorSlots){
			((ActivityDoctorSlots) getActivity()).getDoctorsSlots();
		}else if(getActivity() instanceof ActivityMedicationList && editText.getId() == R.id.etStopMedicationID){
			((ActivityMedicationList) getActivity()).stopMedications(DATA.date);
		}

	}




	//AM / PM Time picker dialog
	static int hour; static int minute;
	public static void setTimeByTimePickerAmPm(final EditText editText, final Activity activity) {
		Calendar mcurrentTime = Calendar.getInstance();
		hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(activity, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
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
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
					aTime = new SimpleDateFormat("hh:mm a").format(sdf.parse(aTime));
				}catch (Exception e){
					e.printStackTrace();
				}
				//editText.append(" "+aTime);//in case of both date time
				editText.setText(aTime);
				editText.setTag(editText.getText().toString());


                /*DATA.print("-- 2 activity instanceof ActivityMap: "+(activity instanceof ActivityMap));
                if(activity instanceof ActivityMap){
                    DATA.print("activity instanceof ActivityMap: "+(activity instanceof ActivityMap));
                    ((ActivityMap)activity).loadData();
                }else if(activity instanceof ActivityLiveMap){
                    DATA.print("activity instanceof ActivityLiveMap: "+(activity instanceof ActivityLiveMap));
                    ((ActivityLiveMap)activity).getVehicleLatestState();
                }else if(activity instanceof ActivityVehicleEvents){
                    DATA.print("activity instanceof ActivityVehicleEvents: "+(activity instanceof ActivityVehicleEvents));
                    ((ActivityVehicleEvents)activity).loadData();
                }else if(activity instanceof ActivityDriverEvents){
                    DATA.print("activity instanceof ActivityDriverEvents: "+(activity instanceof ActivityDriverEvents));
                    ((ActivityDriverEvents)activity).loadData();
                }*/

			}
		}, hour, minute, false);//Yes 24 hour time
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}

	public static void setTimeByTimePicker24H(final EditText editText, final Activity activity) {
		Calendar mcurrentTime = Calendar.getInstance();
		hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(activity, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute1) {
				// editText.setText( selectedHour + ":" + selectedMinute);
				// TODO Auto-generated method stub
				hour = hourOfDay;
				minute = minute1;


				String min = "";
				if (minute < 10)
					min = "0" + minute ;
				else{
					min = String.valueOf(minute);
				}

				// Append in a StringBuilder
				String aTime = new StringBuilder().append(hour).append(':').append(min).toString();
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);//HH:mm  , hh:mm a
					aTime = new SimpleDateFormat("HH:mm").format(sdf.parse(aTime));
				}catch (Exception e){
					e.printStackTrace();
				}
				//editText.append(" "+aTime);//in case of both date time
				editText.setText(aTime);
				editText.setTag(editText.getText().toString());


                /*DATA.print("-- 2 activity instanceof ActivityMap: "+(activity instanceof ActivityMap));
                if(activity instanceof ActivityMap){
                    DATA.print("activity instanceof ActivityMap: "+(activity instanceof ActivityMap));
                    ((ActivityMap)activity).loadData();
                }else if(activity instanceof ActivityLiveMap){
                    DATA.print("activity instanceof ActivityLiveMap: "+(activity instanceof ActivityLiveMap));
                    ((ActivityLiveMap)activity).getVehicleLatestState();
                }else if(activity instanceof ActivityVehicleEvents){
                    DATA.print("activity instanceof ActivityVehicleEvents: "+(activity instanceof ActivityVehicleEvents));
                    ((ActivityVehicleEvents)activity).loadData();
                }else if(activity instanceof ActivityDriverEvents){
                    DATA.print("activity instanceof ActivityDriverEvents: "+(activity instanceof ActivityDriverEvents));
                    ((ActivityDriverEvents)activity).loadData();
                }*/

			}
		}, hour, minute, false);//Yes 24 hour time
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}

}