package com.app.Olc_MentalHealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Olc_MentalHealth.MapActivity;
import com.app.Olc_MentalHealth.R;
import com.app.Olc_MentalHealth.model.HospitalsBean;
import com.app.Olc_MentalHealth.util.DATA;

import java.util.ArrayList;

public class NearbyHospitalsAdapter extends ArrayAdapter<HospitalsBean> {

	Activity activity;
	ArrayList<HospitalsBean> allHospitals;
	Button btnRequest,btnDetails;

	public NearbyHospitalsAdapter(Activity activity , ArrayList<HospitalsBean> allHospitals) {
		super(activity, R.layout.lv_livecare_doctors_row, allHospitals);

		this.activity = activity;
		this.allHospitals = allHospitals;
	}

	static class ViewHolder {

		TextView tvDrName;
		TextView tvVicinity;
		ImageView ivDr;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_livecare_doctors_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvDrName = (TextView) convertView.findViewById(R.id.tvDrNameLivecare);
			viewHolder.tvVicinity = (TextView) convertView.findViewById(R.id.tvHospitalVicinity);
			viewHolder.ivDr = (ImageView) convertView.findViewById(R.id.ivDrLivecare);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDrNameLivecare, viewHolder.tvDrName);
			convertView.setTag(R.id.tvHospitalVicinity, viewHolder.tvVicinity);
			convertView.setTag(R.id.ivDrLivecare, viewHolder.ivDr);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvDrName.setText(allHospitals.get(position).getName());
		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivDr, allHospitals.get(position).getIcon(), R.drawable.ic_launcher);
		DATA.loadImageFromURL(allHospitals.get(position).getIcon(), R.drawable.ic_launcher, viewHolder.ivDr);
		viewHolder.tvVicinity.setVisibility(View.VISIBLE);
		viewHolder.tvVicinity.setText(allHospitals.get(position).getVicinity());

		viewHolder.tvDrName.setTag(allHospitals.get(position).getName());
		viewHolder.tvVicinity.setTag(allHospitals.get(position).getVicinity());

		
		btnRequest = (Button) convertView.findViewById(R.id.btnRequest);
		btnRequest.setVisibility(View.GONE);
		btnDetails= (Button) convertView.findViewById(R.id.btnDetails);
		btnDetails.setVisibility(View.GONE);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//Toast.makeText(activity, "fdffdf", 0).show();
			DATA.print("--context "+activity.toString()+" LocalClassName(): "+activity.getLocalClassName());
			if (activity.getLocalClassName().equalsIgnoreCase("MapActivity")) {
				HospitalsBean currentbean = allHospitals.get(position);
				ArrayList<HospitalsBean> curentbeanlist = new ArrayList<HospitalsBean>();
				curentbeanlist.add(currentbean);
				
				((MapActivity)activity).updateMapHospital(curentbeanlist);
			} else {
				/*HospitalsBean currentbean = allHospitals.get(position);
				ArrayList<HospitalsBean> curentbeanlist = new ArrayList<HospitalsBean>();
				curentbeanlist.add(currentbean);
				
				((DoctorsList)activity).updateMapHospital(curentbeanlist);*/
			}
			
				
			}
		});

		return convertView;
	}
}
