package com.app.amnm_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.amnm_uc.R;
import com.app.amnm_uc.model.PharmacyBean;
import com.app.amnm_uc.util.GloabalMethods;

import java.util.ArrayList;

public class PharmacyAdapter extends ArrayAdapter<PharmacyBean> {

	Activity activity;
	ArrayList<PharmacyBean> pharmacyBeans;

	public PharmacyAdapter(Activity activity,ArrayList<PharmacyBean> pharmacyBeans) {
		super(activity, R.layout.lv_pharmacy_row, pharmacyBeans);

		this.activity = activity;
		this.pharmacyBeans = pharmacyBeans;
	}

	static class ViewHolder {

		TextView tvPharmacyName,tvPharmacyCityState,tvPharmacyAddress,
				tvPharmacyLocation,tvSelectPharmacy;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_pharmacy_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvPharmacyName = (TextView) convertView.findViewById(R.id.tvPharmacyName);
			viewHolder.tvPharmacyCityState = (TextView) convertView.findViewById(R.id.tvPharmacyCityState);
			viewHolder.tvPharmacyAddress = (TextView) convertView.findViewById(R.id.tvPharmacyAddress);
			viewHolder.tvPharmacyLocation = (TextView) convertView.findViewById(R.id.tvPharmacyLocation);
			viewHolder.tvSelectPharmacy = (TextView) convertView.findViewById(R.id.tvSelectPharmacy);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPharmacyName, viewHolder.tvPharmacyName);
			convertView.setTag(R.id.tvPharmacyCityState, viewHolder.tvPharmacyCityState);
			convertView.setTag(R.id.tvPharmacyAddress, viewHolder.tvPharmacyAddress);
			convertView.setTag(R.id.tvPharmacyLocation, viewHolder.tvPharmacyLocation);
			convertView.setTag(R.id.tvSelectPharmacy, viewHolder.tvSelectPharmacy);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvPharmacyName.setText(pharmacyBeans.get(position).StoreName);
		viewHolder.tvPharmacyCityState.setText(pharmacyBeans.get(position).city+" ("+pharmacyBeans.get(position).state+")");
		viewHolder.tvPharmacyAddress.setText(pharmacyBeans.get(position).address+", "+pharmacyBeans.get(position).zipcode);

		viewHolder.tvPharmacyName.setTag(pharmacyBeans.get(position).StoreName);
		viewHolder.tvPharmacyCityState.setTag(pharmacyBeans.get(position).city+" ("+pharmacyBeans.get(position).state+")");
		viewHolder.tvPharmacyAddress.setTag(pharmacyBeans.get(position).address+", "+pharmacyBeans.get(position).zipcode);

		viewHolder.tvPharmacyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new GloabalMethods(activity).showPharmacyMap(pharmacyBeans.get(position));
			}
		});


		return convertView;
	}
}
