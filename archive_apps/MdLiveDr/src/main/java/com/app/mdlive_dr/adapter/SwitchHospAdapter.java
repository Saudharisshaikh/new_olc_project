package com.app.mdlive_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_dr.R;
import com.app.mdlive_dr.model.HospitalBean;
import com.app.mdlive_dr.util.DATA;

import java.util.ArrayList;

public class SwitchHospAdapter extends ArrayAdapter<HospitalBean> {

	Activity activity;
	ArrayList<HospitalBean> hospitalBeans;
	SharedPreferences prefs;

	public SwitchHospAdapter(Activity activity , ArrayList<HospitalBean> hospitalBeans) {
		super(activity, R.layout.hospital_row, hospitalBeans);

		this.activity = activity;
		this.hospitalBeans = hospitalBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvHospName,tvHospZipcode,tvSelectHosp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.hospital_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvHospName = (TextView) convertView.findViewById(R.id.tvHospName);
			viewHolder.tvHospZipcode = (TextView) convertView.findViewById(R.id.tvHospZipcode);
			viewHolder.tvSelectHosp = (TextView) convertView.findViewById(R.id.tvSelectHosp);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvHospName, viewHolder.tvHospName);
			convertView.setTag(R.id.tvHospZipcode, viewHolder.tvHospZipcode);
			convertView.setTag(R.id.tvSelectHosp, viewHolder.tvSelectHosp);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvHospName.setText(hospitalBeans.get(position).hospital_name);
		viewHolder.tvHospZipcode.setText("Zipcode: "+hospitalBeans.get(position).hospital_name);

		if (hospitalBeans.get(position).folder_name.equalsIgnoreCase(prefs.getString("folder_name", ""))) {
			viewHolder.tvSelectHosp.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			viewHolder.tvSelectHosp.setText("Default");
		}else {
			viewHolder.tvSelectHosp.setBackgroundResource(R.drawable.btn_selector);
			viewHolder.tvSelectHosp.setText("Switch Hospital");
		}


		/*if (primaryCareBeens.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}*/
		
		return convertView;
	}

}
