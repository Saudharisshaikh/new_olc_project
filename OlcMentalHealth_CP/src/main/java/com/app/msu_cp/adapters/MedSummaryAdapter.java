package com.app.msu_cp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.ActivityMedicalSummary;
import com.app.msu_cp.PatientMedicalHistoryNew;
import com.app.msu_cp.R;
import com.app.msu_cp.model.MedSummaryBean;

import java.util.ArrayList;

public class MedSummaryAdapter extends ArrayAdapter<MedSummaryBean> {

	Activity activity;
	ArrayList<MedSummaryBean> medSummaryBeans;

	public MedSummaryAdapter(Activity activity, ArrayList<MedSummaryBean> medSummaryBeans) {
		super(activity, R.layout.lv_med_summary_row, medSummaryBeans);

		this.activity = activity;
		this.medSummaryBeans = medSummaryBeans;
	}

	static class ViewHolder {
		TextView tvMedSummary,tvDate,tvDeleteMedSummary,tvPostionSummary;
	}

	@Override
	public int getCount() {
		return medSummaryBeans.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_med_summary_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvMedSummary = (TextView) convertView.findViewById(R.id.tvMedSummary);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvDeleteMedSummary = (TextView) convertView.findViewById(R.id.tvDeleteMedSummary);
			viewHolder.tvPostionSummary = convertView.findViewById(R.id.tvPostionSummary);
			
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvMedSummary.setText(medSummaryBeans.get(position).summary);
		viewHolder.tvDate.setText(medSummaryBeans.get(position).dateof);
		viewHolder.tvPostionSummary.setText(String.valueOf(position+1));

		viewHolder.tvDeleteMedSummary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this medical summary ?")
								.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if(activity instanceof PatientMedicalHistoryNew){
											((PatientMedicalHistoryNew)activity).deleteMedSummary(position);
										}else if(activity instanceof ActivityMedicalSummary){
											((ActivityMedicalSummary)activity).deleteMedSummary(position);
										}
									}
								})
								.setNegativeButton("Not Now", null).create();
				alertDialog.show();
			}
		});

		return convertView;
	}
}
