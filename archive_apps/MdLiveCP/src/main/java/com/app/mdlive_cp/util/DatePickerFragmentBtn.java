package com.app.mdlive_cp.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.reliance.preschistory.ActivityPrescHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public  class DatePickerFragmentBtn extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
	
	
	Button editText;
	public DatePickerFragmentBtn(Button editText) {
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
		editText.setTag(DATA.date);

		try {
			if(editText.getId() == R.id.btnHistoryTo || editText.getId() == R.id.btnHistoryFrom){
				if(getActivity() instanceof ActivityPrescHistory){
					((ActivityPrescHistory) getActivity()).setDatesAndCallLoadPrescHistory();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
}