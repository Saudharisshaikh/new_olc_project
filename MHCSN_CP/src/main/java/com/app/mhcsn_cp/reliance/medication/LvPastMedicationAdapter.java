package com.app.mhcsn_cp.reliance.medication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.ArrayList;

public class LvPastMedicationAdapter extends ArrayAdapter<MedicationBean> {

	Activity activity;
	ArrayList<MedicationBean> medicationBeans;

	public LvPastMedicationAdapter(Activity activity , ArrayList<MedicationBean> medicationBeans) {
		super(activity, R.layout.lv_past_med_row, medicationBeans);
		this.activity = activity;
		this.medicationBeans = medicationBeans;
	}

	static class ViewHolder {
		TextView tvMedName,tvMedStrength,tvMedInstructions,tvViewMed;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_past_med_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvMedName = convertView.findViewById(R.id.tvMedName);
			viewHolder.tvMedStrength = convertView.findViewById(R.id.tvMedStrength);
			viewHolder.tvMedInstructions = convertView.findViewById(R.id.tvMedInstructions);
            viewHolder.tvViewMed = convertView.findViewById(R.id.tvViewMed);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvMedName, viewHolder.tvMedName);
			convertView.setTag(R.id.tvMedStrength, viewHolder.tvMedStrength);
			convertView.setTag(R.id.tvMedInstructions, viewHolder.tvMedInstructions);
            convertView.setTag(R.id.tvViewMed, viewHolder.tvViewMed);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvMedName.setText(medicationBeans.get(position).name);
		viewHolder.tvMedStrength.setText(medicationBeans.get(position).strength);
		viewHolder.tvMedInstructions.setText(medicationBeans.get(position).directions);

		viewHolder.tvViewMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMedicationList) activity).showMedicationDetailDialog(medicationBeans.get(position),1);
            }
        });

		return convertView;
	}

}
