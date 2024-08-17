package com.app.mhcsn_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.ArrayList;


public class CP_ImmunizationAdapter extends ArrayAdapter<CP_ImmunizationBean> {

	Activity activity;
	ArrayList<CP_ImmunizationBean> cp_immunizationBeans;

	public CP_ImmunizationAdapter(Activity activity, ArrayList<CP_ImmunizationBean> cp_immunizationBeans) {
		super(activity, R.layout.cp_immuniz_row, cp_immunizationBeans);

		this.activity = activity;
		this.cp_immunizationBeans = cp_immunizationBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_immunizationBeans.size();
	}
	static class ViewHolder {
		TextView tvCPI_Vaccine,tvCPI_SubVaccine,tvCPI_Dose,tvCPI_Date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_immuniz_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPI_Vaccine = (TextView) convertView.findViewById(R.id.tvCPI_Vaccine);
			viewHolder.tvCPI_SubVaccine = (TextView) convertView.findViewById(R.id.tvCPI_SubVaccine);
			viewHolder.tvCPI_Dose = (TextView) convertView.findViewById(R.id.tvCPI_Dose);
			viewHolder.tvCPI_Date = (TextView) convertView.findViewById(R.id.tvCPI_Date);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPI_Vaccine, viewHolder.tvCPI_Vaccine);
			convertView.setTag(R.id.tvCPI_SubVaccine, viewHolder.tvCPI_SubVaccine);
			convertView.setTag(R.id.tvCPI_Dose, viewHolder.tvCPI_Dose);
			convertView.setTag(R.id.tvCPI_Date, viewHolder.tvCPI_Date);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPI_Vaccine.setText(cp_immunizationBeans.get(position).vaccine_name);
		viewHolder.tvCPI_SubVaccine.setText(cp_immunizationBeans.get(position).sub_vaccine_name);
		viewHolder.tvCPI_Dose.setText(cp_immunizationBeans.get(position).dose);
		viewHolder.tvCPI_Date.setText(cp_immunizationBeans.get(position).date);

		return convertView;
	}
}
