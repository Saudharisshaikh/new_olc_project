package com.app.emcurama.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import com.app.emcurama.R;
import com.app.emcurama.model.CovidBillingCodeBean;

import java.util.ArrayList;

public class CovidBillingCodesAdapter extends ArrayAdapter<CovidBillingCodeBean> {

	Activity activity;
	ArrayList<CovidBillingCodeBean> covidBillingCodeBeans, covidBillingCodeBeansOrig;

	public CovidBillingCodesAdapter(Activity activity , ArrayList<CovidBillingCodeBean> covidBillingCodeBeans) {
		super(activity, R.layout.lv_covid_billing_row, covidBillingCodeBeans);
		this.activity = activity;
		this.covidBillingCodeBeans = covidBillingCodeBeans;

		covidBillingCodeBeansOrig = new ArrayList<>();
		covidBillingCodeBeansOrig.addAll(covidBillingCodeBeans);
	}

	static class ViewHolder {
		RadioButton rbServiceName;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_covid_billing_row, null);

			viewHolder = new ViewHolder();

			viewHolder.rbServiceName = convertView.findViewById(R.id.rbServiceName);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.rbServiceName , viewHolder.rbServiceName);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.rbServiceName.setText(covidBillingCodeBeans.get(position).hcpcs_code+" - "+covidBillingCodeBeans.get(position).service_name);


		viewHolder.rbServiceName.setChecked(covidBillingCodeBeans.get(position).isChecked);

		return convertView;
	}



	/*public void filter(String filterText) {
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
	}//end filter*/

}
