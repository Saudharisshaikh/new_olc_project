package com.app.mdlive_dr.adapter;

import java.util.ArrayList;

import com.app.mdlive_dr.R;
import com.app.mdlive_dr.model.UnknownPatientMedicationBean;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UnknownPatientMedictionAdapter extends ArrayAdapter<UnknownPatientMedicationBean> {

	Activity activity;
	ArrayList<UnknownPatientMedicationBean> data;

	public UnknownPatientMedictionAdapter(Activity activity,ArrayList<UnknownPatientMedicationBean> data) {
		super(activity, R.layout.lv_unknown_patient_medications, data);

		this.activity = activity;
		this.data = data;
	}

	static class ViewHolder {

		TextView tvMedName,tvMedStrength,tvMedDosageForm,tvMedPotencyUnit;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_unknown_patient_medications, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvMedName = (TextView) convertView.findViewById(R.id.tvMedName);
			viewHolder.tvMedStrength = (TextView) convertView.findViewById(R.id.tvMedStrength);
			viewHolder.tvMedDosageForm = (TextView) convertView.findViewById(R.id.tvMedDosageForm);
			viewHolder.tvMedPotencyUnit = (TextView) convertView.findViewById(R.id.tvMedPotencyUnit);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMedName, viewHolder.tvMedName);
			convertView.setTag(R.id.tvMedStrength, viewHolder.tvMedStrength);
			convertView.setTag(R.id.tvMedDosageForm, viewHolder.tvMedDosageForm);
			convertView.setTag(R.id.tvMedPotencyUnit, viewHolder.tvMedPotencyUnit);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvMedName.setText(data.get(position).drug_name);
		viewHolder.tvMedStrength.setText("Strength: "+data.get(position).strength);
		viewHolder.tvMedDosageForm.setText("Dosage Form: "+data.get(position).dosage_form);
		viewHolder.tvMedPotencyUnit.setText("Potency Unit: "+data.get(position).potency_unit);

		viewHolder.tvMedName.setTag(data.get(position).drug_name);
		viewHolder.tvMedStrength.setTag("Strength: "+data.get(position).strength);
		viewHolder.tvMedDosageForm.setTag("Dosage Form: "+data.get(position).dosage_form);
		viewHolder.tvMedPotencyUnit.setTag("Potency Unit: "+data.get(position).potency_unit);


		return convertView;
	}
}
