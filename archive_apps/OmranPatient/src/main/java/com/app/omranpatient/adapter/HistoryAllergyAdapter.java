package com.app.omranpatient.adapter;

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

import com.app.omranpatient.MedicalHistory1;
import com.app.omranpatient.R;

import java.util.ArrayList;


public class HistoryAllergyAdapter extends ArrayAdapter<String> {

	Activity activity;
	ArrayList<String> allergyList;

	public HistoryAllergyAdapter(Activity activity, ArrayList<String> allergyList) {
		super(activity, R.layout.history_medi_row, allergyList);

		this.activity = activity;
		this.allergyList = allergyList;

		try {
			((MedicalHistory1) activity).tvViewAllergy.setText("View ("+allergyList.size()+")");
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

		viewHolder.tvMedName.setText(allergyList.get(position));
		viewHolder.ivDeleteMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				allergyList.remove(position);
				notifyDataSetChanged();
			}
		});

		viewHolder.ivEditMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editAllergiesDialog(position);
			}
		});

		return convertView;
	}


	public void editAllergiesDialog(final int listPos){
		final Dialog allergiesDialog = new Dialog(activity);
		allergiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		allergiesDialog.setContentView(R.layout.dialog_add_allergy);
		allergiesDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = allergiesDialog.findViewById(R.id.tvDTittle);
		final EditText etEditAllergy = (EditText) allergiesDialog.findViewById(R.id.etEditAllergy);
		etEditAllergy.setText(allergyList.get(listPos));
        etEditAllergy.setSelection(etEditAllergy.getText().toString().length());

		Button btnSaveAllergy = allergiesDialog.findViewById(R.id.btnSaveAllergy);

		tvDTittle.setText("Edit Allergies");

		btnSaveAllergy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String allegy = etEditAllergy.getText().toString();
				if(allegy.isEmpty()){
					etEditAllergy.setError("Please enter the allergies");
					return;
				}
				allergyList.remove(listPos);
				allergyList.add(listPos,allegy);
				allergiesDialog.dismiss();

				/*AlertDialog alertDialog =
						new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
								.setMessage("Medication has been edited successfully.")
								.setPositiveButton("Done",null).create();

				alertDialog.show();
				alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/


				notifyDataSetChanged();
			}
		});

		allergiesDialog.findViewById(R.id.btnCancelAllergy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				allergiesDialog.dismiss();
			}
		});
		allergiesDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				allergiesDialog.dismiss();
			}
		});



		allergiesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(allergiesDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		allergiesDialog.show();
		allergiesDialog.getWindow().setAttributes(lp);
	}
}
