package com.app.OnlineCareUS_MA.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareUS_MA.R;
import com.app.OnlineCareUS_MA.model.CareCenterBean;
import com.app.OnlineCareUS_MA.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class CareCentersAdapter extends ArrayAdapter<CareCenterBean> {

	Activity activity;
	ArrayList<CareCenterBean> careCenterBeans, careCenterBeansOrig;

	public CareCentersAdapter(Activity activity , ArrayList<CareCenterBean> careCenterBeans) {
		super(activity, R.layout.lv_care_centers_row, careCenterBeans);
		this.activity = activity;
		this.careCenterBeans = careCenterBeans;

		careCenterBeansOrig = new ArrayList<>();
		careCenterBeansOrig.addAll(careCenterBeans);
	}

	static class ViewHolder {
		TextView tvCareCenterName, tvNumOfDoc;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_care_centers_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCareCenterName = convertView.findViewById(R.id.tvCareCenterName);
			viewHolder.tvNumOfDoc = convertView.findViewById(R.id.tvNumOfDoc);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCareCenterName , viewHolder.tvCareCenterName);
			convertView.setTag(R.id.tvNumOfDoc , viewHolder.tvNumOfDoc);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCareCenterName.setText(careCenterBeans.get(position).center_name);
		viewHolder.tvNumOfDoc.setText(careCenterBeans.get(position).total_doctors+" Doctors Available");


		return convertView;
	}



	public void filter(String filterText) {
		careCenterBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- careCenterBeansOrig size: "+careCenterBeansOrig.size());
		if(filterText.length() == 0) {
			careCenterBeans.addAll(careCenterBeansOrig);
		} else {
			for(CareCenterBean temp : careCenterBeansOrig) {
				if(temp.center_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					careCenterBeans.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
