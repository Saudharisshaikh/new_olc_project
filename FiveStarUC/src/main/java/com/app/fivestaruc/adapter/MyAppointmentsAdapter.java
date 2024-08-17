package com.app.fivestaruc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.fivestaruc.R;
import com.app.fivestaruc.model.MyAppointmentsModel;
import com.app.fivestaruc.util.DATA;

public class MyAppointmentsAdapter extends ArrayAdapter<MyAppointmentsModel> {

	Activity activity;

	public MyAppointmentsAdapter(Activity activity) {
		super(activity, R.layout.my_appointments_row, DATA.allAppointments);

		this.activity = activity;
	}

	static class ViewHolder {
		TextView tvMyappntDate,tvMyappntDr,tvMyappntStts; 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.my_appointments_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvMyappntDate = (TextView) convertView.findViewById(R.id.tvMyappntDate);
			viewHolder.tvMyappntDr = (TextView) convertView.findViewById(R.id.tvMyappntDr);
			viewHolder.tvMyappntStts = (TextView) convertView.findViewById(R.id.tvMyappntStts);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMyappntDate, viewHolder.tvMyappntDate);
			convertView.setTag(R.id.tvMyappntDr, viewHolder.tvMyappntDr);
			convertView.setTag(R.id.tvMyappntStts, viewHolder.tvMyappntStts);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvMyappntDate.setText(DATA.allAppointments.get(position).appointment_date+"\n"+DATA.allAppointments.get(position).from_time);//date
		viewHolder.tvMyappntDate.setTag(DATA.allAppointments.get(position).appointment_date+"\n"+DATA.allAppointments.get(position).from_time);//date

		viewHolder.tvMyappntDr.setText(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
		viewHolder.tvMyappntDr.setTag(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
		
		if (DATA.allAppointments.get(position).slot_id.equals("0")) {
			viewHolder.tvMyappntDr.setText("Refferd to admin");
			viewHolder.tvMyappntDr.setTag("Refferd to admin");
		} else {
			viewHolder.tvMyappntDr.setText(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
			viewHolder.tvMyappntDr.setTag(DATA.allAppointments.get(position).first_name+" "+DATA.allAppointments.get(position).last_name);
		}
		
		if(DATA.allAppointments.get(position).status.equals("Pending")) {

			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_waiting_drawable);
			viewHolder.tvMyappntStts.setText("Awaiting");
			//viewHolder.tvMyappntStts.setTextColor(activity.getResources().getColor(R.color.text_color));
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_awaiting);
		}
		else if(DATA.allAppointments.get(position).status.equals("Confirmed")) {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);// icon_success
			viewHolder.tvMyappntStts.setText("Confirmed");
			//viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#3A7D00"));
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_success);
		}
		else if(DATA.allAppointments.get(position).status.equals("Declined")) {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
			viewHolder.tvMyappntStts.setText("Canceled");
			//viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_cancelled);
		}else if(DATA.allAppointments.get(position).status.equals("Cancelled")){
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable_red);
			viewHolder.tvMyappntStts.setText(DATA.allAppointments.get(position).status);
			//viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
		}else {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
			viewHolder.tvMyappntStts.setText(DATA.allAppointments.get(position).status);
			//viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
		}


		return convertView;
	}
}
