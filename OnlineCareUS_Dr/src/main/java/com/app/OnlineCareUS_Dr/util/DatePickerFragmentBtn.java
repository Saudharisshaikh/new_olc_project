package com.app.OnlineCareUS_Dr.util;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

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
		return new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_LIGHT, this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		month = month +1;

		DATA.isDateSelected = true;
		//DATA.date = year + "-" + month + "-" + day;
		DATA.date =  month + "/" + day + "/" + year;
		
		/*if(DATA.isDatePickerCallFromSignup) {

			((Signup) getActivity()).setETDate();

		}
		else if(DATA.isDatPckrCallFromAddMember) {
			((AddFamilyMember) getActivity()).setETDate();
		}

		else  {
			((UpdateProfile) getActivity()).setETDate();

		}*/
		
		
		editText.setText(DATA.date);

	}
}