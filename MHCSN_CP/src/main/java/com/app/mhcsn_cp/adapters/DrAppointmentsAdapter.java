package com.app.mhcsn_cp.adapters;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.DrAppointmentModel;

import java.util.ArrayList;

public class DrAppointmentsAdapter extends ArrayAdapter<DrAppointmentModel> {

	Activity activity;
	ArrayList<DrAppointmentModel> drAppointmentsList;
	boolean iscbVisible;

	public DrAppointmentsAdapter(Activity activity, ArrayList<DrAppointmentModel> drAppointmentsList, boolean iscbVisible) {
		super(activity, R.layout.lv_dr_appointments_row, drAppointmentsList);

		this.activity = activity;
		this.drAppointmentsList = drAppointmentsList;
		this.iscbVisible = iscbVisible;
	}

	static class ViewHolder {

		TextView tvMyappntDate,tvMyappntPateint,tvMyappntStts;
		CheckBox cbRefer;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_dr_appointments_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvMyappntDate = (TextView) convertView.findViewById(R.id.tvMyappntDate);
			viewHolder.tvMyappntPateint = (TextView) convertView.findViewById(R.id.tvMyappntPateint);
			viewHolder.tvMyappntStts = (TextView) convertView.findViewById(R.id.tvMyappntStts);
			viewHolder.cbRefer = (CheckBox) convertView.findViewById(R.id.cbRefer);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMyappntDate, viewHolder.tvMyappntDate);
			convertView.setTag(R.id.tvMyappntPateint, viewHolder.tvMyappntPateint);
			convertView.setTag(R.id.tvMyappntStts, viewHolder.tvMyappntStts);
			convertView.setTag(R.id.cbRefer, viewHolder.cbRefer);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvMyappntDate.setText(drAppointmentsList.get(position).getAppointment_date()+"\n"+drAppointmentsList.get(position).getFrom_time());//+" To "+drAppointmentsList.get(position).getTo_time()
		viewHolder.tvMyappntDate.setTag(drAppointmentsList.get(position).getAppointment_date()+"\n"+drAppointmentsList.get(position).getFrom_time());//+" To "+drAppointmentsList.get(position).getTo_time()

		viewHolder.tvMyappntPateint.setText(drAppointmentsList.get(position).getFirst_name()+" "+drAppointmentsList.get(position).getLast_name());
		viewHolder.tvMyappntPateint.setTag(drAppointmentsList.get(position).getFirst_name()+" "+drAppointmentsList.get(position).getLast_name());
		
		if(drAppointmentsList.get(position).getStatus().equals("Pending")) {

			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_waiting_drawable);
			viewHolder.tvMyappntStts.setText("Awaiting");
			viewHolder.tvMyappntStts.setTextColor(activity.getResources().getColor(R.color.text_color));
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_awaiting);
		}
		else if(drAppointmentsList.get(position).getStatus().equals("Confirmed")) {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			viewHolder.tvMyappntStts.setText("Confirmed");
			viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));//3A7D00
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_success);
		}
		else if(drAppointmentsList.get(position).getStatus().equals("Declined")) {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
			viewHolder.tvMyappntStts.setText("Canceled");
			viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
			//viewHolder.imgMyappntStts.setTag(R.drawable.icon_cancelled);
		}else if (drAppointmentsList.get(position).getStatus().equals("Cancelled")) {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable_red);
			viewHolder.tvMyappntStts.setText(drAppointmentsList.get(position).getStatus());
			viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
		} else {
			viewHolder.tvMyappntStts.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
			viewHolder.tvMyappntStts.setText(drAppointmentsList.get(position).getStatus());
			viewHolder.tvMyappntStts.setTextColor(Color.parseColor("#FFFFFF"));
		}
		
		if (iscbVisible) {
			viewHolder.cbRefer.setVisibility(View.VISIBLE);
			
			viewHolder.cbRefer.setChecked(drAppointmentsList.get(position).isSelected);
			
			viewHolder.cbRefer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
					// TODO Auto-generated method stub
					drAppointmentsList.get(position).isSelected = ischecked;
				}
			});
		} else {
			viewHolder.cbRefer.setVisibility(View.GONE);
		}
		


		return convertView;
	}
}
