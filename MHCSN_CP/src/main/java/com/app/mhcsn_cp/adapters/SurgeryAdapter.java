package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.PatientMedicalHistoryNew;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.FaxHistBean;
import com.app.mhcsn_cp.model.SurguryBean;

import java.util.ArrayList;

public class SurgeryAdapter extends ArrayAdapter<SurguryBean> {

	Activity activity;
	ArrayList<SurguryBean> surguryBeans;

	public SurgeryAdapter(Activity activity, ArrayList<SurguryBean> surguryBeans) {
		super(activity, R.layout.lv_surgery_row, surguryBeans);

		this.activity = activity;
		this.surguryBeans = surguryBeans;
	}

	static class ViewHolder {
		TextView tvLocation,tvTypeOfSurg,tvDocName,tvDate,tvDeleteSurg, tvRowNo;
	}

	@Override
	public int getCount() {
		return surguryBeans.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_surgery_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
			viewHolder.tvTypeOfSurg = (TextView) convertView.findViewById(R.id.tvTypeOfSurg);
			viewHolder.tvDocName = (TextView) convertView.findViewById(R.id.tvDocName);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvDeleteSurg = (TextView) convertView.findViewById(R.id.tvDeleteSurg);
			viewHolder.tvRowNo = convertView.findViewById(R.id.tvRowNo);
			
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvLocation.setText(surguryBeans.get(position).location);
		viewHolder.tvTypeOfSurg.setText(surguryBeans.get(position).type_of_surgery);
		viewHolder.tvDocName.setText(surguryBeans.get(position).doctor_name);
		viewHolder.tvDate.setText(surguryBeans.get(position).dateof);
		viewHolder.tvRowNo.setText(String.valueOf(position+1));

		viewHolder.tvDeleteSurg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this surgery ?")
								.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if(activity instanceof PatientMedicalHistoryNew){
											((PatientMedicalHistoryNew)activity).deleteSurgery(position);
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
