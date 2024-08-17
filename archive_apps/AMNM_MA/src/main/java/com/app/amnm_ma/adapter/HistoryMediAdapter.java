package com.app.amnm_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.amnm_ma.R;

import java.util.ArrayList;


public class HistoryMediAdapter extends ArrayAdapter<String> {

	Activity activity;
	ArrayList<String> medications;

	public HistoryMediAdapter(Activity activity, ArrayList<String> medications) {
		super(activity, R.layout.history_medi_row, medications);

		this.activity = activity;
		this.medications = medications;

		/*try {
			((MedicalHistory1) activity).tvViewMed.setText("View ("+medications.size()+")");
		}catch (Exception e){
			e.printStackTrace();
		}*/
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

		viewHolder.tvMedName.setText(medications.get(position));

		/*viewHolder.ivDeleteMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				medications.remove(position);
				notifyDataSetChanged();
			}
		});

		viewHolder.ivEditMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editMedDialog(position);
			}
		});*/

		return convertView;
	}


	/*public void editMedDialog(final int listPos){
		final Dialog medicationsDialog = new Dialog(activity);
		medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		medicationsDialog.setContentView(R.layout.dialog_edit_medi);
		medicationsDialog.setCanceledOnTouchOutside(false);

		final EditText etEditMed = (EditText) medicationsDialog.findViewById(R.id.etEditMed);
		etEditMed.setText(medications.get(listPos));
		etEditMed.setSelection(etEditMed.getText().toString().length());

		Button btnSaveMed = medicationsDialog.findViewById(R.id.btnSaveMed);

		btnSaveMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String med = etEditMed.getText().toString();
				if(med.isEmpty()){
					etEditMed.setError("Please enter the medication name");
					return;
				}
				medications.remove(listPos);
				medications.add(listPos,med);
				medicationsDialog.dismiss();

				notifyDataSetChanged();
			}
		});

		medicationsDialog.findViewById(R.id.btnCancelMed).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				medicationsDialog.dismiss();
			}
		});
		medicationsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				medicationsDialog.dismiss();
			}
		});



		medicationsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(medicationsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		medicationsDialog.show();
		medicationsDialog.getWindow().setAttributes(lp);
	}*/
}
