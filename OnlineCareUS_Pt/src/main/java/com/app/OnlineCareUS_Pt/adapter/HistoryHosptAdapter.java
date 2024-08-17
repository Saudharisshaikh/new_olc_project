package com.app.OnlineCareUS_Pt.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.MedicalHistory1;
import com.app.OnlineCareUS_Pt.R;

import java.util.ArrayList;


public class HistoryHosptAdapter extends ArrayAdapter<String> {

	Activity activity;
	ArrayList<String> hospitalizationList;

	public HistoryHosptAdapter(Activity activity, ArrayList<String> hospitalizationList) {
		super(activity, R.layout.history_medi_row, hospitalizationList);

		this.activity = activity;
		this.hospitalizationList = hospitalizationList;

		try {
			((MedicalHistory1) activity).tvViewHosp.setText("View ("+hospitalizationList.size()+")");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	static class ViewHolder {

		TextView tvMedName;
		ImageView ivDeleteMed,ivEditMed;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.history_medi_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvMedName = (TextView) convertView.findViewById(R.id.tvMedName);
			viewHolder.ivDeleteMed = (ImageView) convertView.findViewById(R.id.ivDeleteMed);
			viewHolder.ivEditMed = (ImageView) convertView.findViewById(R.id.ivEditMed);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMedName, viewHolder.tvMedName);
			convertView.setTag(R.id.ivDeleteMed, viewHolder.ivDeleteMed);
			convertView.setTag(R.id.ivEditMed, viewHolder.ivEditMed);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvMedName.setText(hospitalizationList.get(position));
		viewHolder.ivDeleteMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hospitalizationList.remove(position);
				notifyDataSetChanged();
			}
		});

		viewHolder.ivEditMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editHospDialog(position);
			}
		});

		return convertView;
	}


	public void editHospDialog(final int listPos){
		final Dialog hospDialog = new Dialog(activity);
		hospDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		hospDialog.setContentView(R.layout.dialog_edit_hosp);
		hospDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = hospDialog.findViewById(R.id.tvDTittle);
		final EditText etAddHosp = (EditText) hospDialog.findViewById(R.id.etAddHosp);
		etAddHosp.setText(hospitalizationList.get(listPos));
		etAddHosp.setSelection(etAddHosp.getText().toString().length());

		Button btnSaveHosp = hospDialog.findViewById(R.id.btnSaveHosp);

		tvDTittle.setText("Edit Hospitalization");

		btnSaveHosp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String med = etAddHosp.getText().toString();
				if(med.isEmpty()){
					etAddHosp.setError("Please enter the medication name");
					return;
				}
				hospitalizationList.remove(listPos);
				hospitalizationList.add(listPos,med);
				hospDialog.dismiss();

				/*AlertDialog alertDialog =
						new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
								.setMessage("Medication has been edited successfully.")
								.setPositiveButton("Done",null).create();

				alertDialog.show();
				alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/


				notifyDataSetChanged();
			}
		});

		hospDialog.findViewById(R.id.btnCancelHosp).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hospDialog.dismiss();
			}
		});
		hospDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hospDialog.dismiss();
			}
		});



		hospDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(hospDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		hospDialog.show();
		hospDialog.getWindow().setAttributes(lp);
	}
}
