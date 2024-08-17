package com.app.omrandr.adapter;

import java.util.ArrayList;
import java.util.Calendar;

import com.app.omrandr.R;
import com.app.omrandr.model.DrScheduleBean;
import com.app.omrandr.util.CheckInternetConnection;
import com.app.omrandr.util.DATA;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

public class SetDrScheduleAdapter extends ArrayAdapter<DrScheduleBean> {

	Activity activity;
	ArrayList<DrScheduleBean> drScheduleBeans;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;

	public SetDrScheduleAdapter(Activity activity,ArrayList<DrScheduleBean> drScheduleBeans) {
		super(activity, R.layout.set_dr_schedule_row, drScheduleBeans);

		this.activity = activity;
		this.drScheduleBeans = drScheduleBeans;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		CheckBox cbDay;
		//Button etFrom,etTo,etEveningFrom,etEveningTo;
	}

	Button etFrom,etTo,etEveningFrom,etEveningTo;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.set_dr_schedule_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.cbDay = (CheckBox) convertView.findViewById(R.id.cbDay);
			etFrom = (Button) convertView.findViewById(R.id.etFrom);
			etTo = (Button) convertView.findViewById(R.id.etTo);
			etEveningFrom = (Button) convertView.findViewById(R.id.etEveningFrom);
			etEveningTo = (Button) convertView.findViewById(R.id.etEveningTo);
			
			convertView.setTag(viewHolder);
			
			convertView.setTag(R.id.cbDay , viewHolder.cbDay);
			/*convertView.setTag(R.id.etFrom , viewHolder.etFrom);
			convertView.setTag(R.id.etTo, viewHolder.etTo);
			convertView.setTag(R.id.etEveningFrom, viewHolder.etEveningFrom);
			convertView.setTag(R.id.etEveningTo, viewHolder.etEveningTo);*/
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.cbDay.setText(drScheduleBeans.get(position).getDay());
		
		
		etFrom.setText(drScheduleBeans.get(position).getFrom_time());
		etTo.setText(drScheduleBeans.get(position).getEvening_from_time());
		etEveningFrom.setText(drScheduleBeans.get(position).getEvening_from_time());
		etEveningTo.setText(drScheduleBeans.get(position).getEvening_to_time());
		
		etFrom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTimeByTimePicker(etFrom , position);
			}
		});	
		
		etTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTimeByTimePicker(etTo , position);
			}
		});	
		etEveningFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTimeByTimePicker(etEveningFrom, position);
			}
		});	
		etEveningTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setTimeByTimePicker(etEveningTo,position);
			}
		});	
		return convertView;
	}
	
	
	
	
	
	int hour;int minute;
	public void setTimeByTimePicker(final Button editText,final int pos) {
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
               
                
                
                
                int id  = editText.getId();
                switch (id) {
                case R.id.etFrom:
                	drScheduleBeans.get(pos).setFrom_time(aTime);
                	break;
                case R.id.etTo:
                	drScheduleBeans.get(pos).setTo_time(aTime);
                	break;
                case R.id.etEveningFrom:
                	drScheduleBeans.get(pos).setEvening_from_time(aTime);
                	break;
                case R.id.etEveningTo:
                	drScheduleBeans.get(pos).setEvening_to_time(aTime);
                	break;

                default:
                	break;
                }
                editText.setText(aTime);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
	}
	
}
