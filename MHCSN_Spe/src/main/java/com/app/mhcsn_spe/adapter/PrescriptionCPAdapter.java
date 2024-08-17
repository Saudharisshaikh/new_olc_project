package com.app.mhcsn_spe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.model.PrescriptionCPBean;
import com.app.mhcsn_spe.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class PrescriptionCPAdapter extends ArrayAdapter<PrescriptionCPBean> {

	Activity activity;
	ArrayList<PrescriptionCPBean> prescriptionCPBeens;

	public PrescriptionCPAdapter(Activity activity , ArrayList<PrescriptionCPBean> prescriptionCPBeens) {
		super(activity, R.layout.cp_prescription_row, prescriptionCPBeens);

		this.activity = activity;
		this.prescriptionCPBeens = prescriptionCPBeens;
	}

	static class ViewHolder {
		TextView tvPressPatientName,tvPressDate,tvPressMedication,tvPresReqBy;
		CircularImageView ivPresc;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_prescription_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivPresc = (CircularImageView) convertView.findViewById(R.id.ivPresc);
			viewHolder.tvPressPatientName = (TextView) convertView.findViewById(R.id.tvPressPatientName);
			viewHolder.tvPressDate = (TextView) convertView.findViewById(R.id.tvPressDate);
			viewHolder.tvPressMedication = (TextView) convertView.findViewById(R.id.tvPressMedication);
			viewHolder.tvPresReqBy = (TextView) convertView.findViewById(R.id.tvPresReqBy);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPressPatientName, viewHolder.tvPressPatientName);
			convertView.setTag(R.id.tvPressDate, viewHolder.tvPressDate);
			convertView.setTag(R.id.tvPressMedication, viewHolder.tvPressMedication);
			convertView.setTag(R.id.tvPresReqBy, viewHolder.tvPresReqBy);
			convertView.setTag(R.id.ivPresc, viewHolder.ivPresc);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvPressPatientName.setText(prescriptionCPBeens.get(position).first_name+" "+prescriptionCPBeens.get(position).last_name);
		viewHolder.tvPressDate.setText(prescriptionCPBeens.get(position).dateof);
		viewHolder.tvPressMedication.setText(prescriptionCPBeens.get(position).drug_name);
		viewHolder.tvPresReqBy.setText("Requested by: "+prescriptionCPBeens.get(position).prescribed_by);
		DATA.loadImageFromURL(prescriptionCPBeens.get(position).image,R.drawable.icon_call_screen,viewHolder.ivPresc);
		

		return convertView;
	}

}
