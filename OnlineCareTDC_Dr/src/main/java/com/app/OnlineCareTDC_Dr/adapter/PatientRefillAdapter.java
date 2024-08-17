package com.app.OnlineCareTDC_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.PatientRefillBean;

import java.util.ArrayList;

public class PatientRefillAdapter extends ArrayAdapter<PatientRefillBean> {

	Activity activity;
	ArrayList<PatientRefillBean> patientRefillBeens;

	public PatientRefillAdapter(Activity activity, ArrayList<PatientRefillBean> patientRefillBeens) {
		super(activity, R.layout.pres_detail_row, patientRefillBeens);

		this.activity = activity;
		this.patientRefillBeens = patientRefillBeens;
	}

	static class ViewHolder {

		TextView tvPresInd,tvPresName,tvPatientName,tvFromDate,tvToDate;
		TextView btnReqRifill;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.pres_detail_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvPresInd = (TextView) convertView.findViewById(R.id.tvPresInd);
			viewHolder.tvPresName = (TextView) convertView.findViewById(R.id.tvPresName);
			viewHolder.btnReqRifill = (TextView) convertView.findViewById(R.id.btnReqRifill);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvFromDate = (TextView) convertView.findViewById(R.id.tvFromDate);
			viewHolder.tvToDate = (TextView) convertView.findViewById(R.id.tvToDate);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPresInd, viewHolder.tvPresInd);
			convertView.setTag(R.id.tvPresName, viewHolder.tvPresName);
			convertView.setTag(R.id.btnReqRifill, viewHolder.btnReqRifill);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvFromDate, viewHolder.tvFromDate);
			convertView.setTag(R.id.tvToDate, viewHolder.tvToDate);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvPresInd.setText(position+1+"");
		viewHolder.tvPresName.setText(patientRefillBeens.get(position).drug_name);
		viewHolder.tvPatientName.setText(patientRefillBeens.get(position).first_name+" "+
				patientRefillBeens.get(position).last_name);
		viewHolder.tvFromDate.setText("Start: "+patientRefillBeens.get(position).start_date+"\nEnd: "+patientRefillBeens.get(position).end_date);
		viewHolder.tvToDate.setText("Pharmacy: "+patientRefillBeens.get(position).StoreName);

		return convertView;
	}
}
