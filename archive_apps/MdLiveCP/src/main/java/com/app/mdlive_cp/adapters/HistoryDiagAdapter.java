package com.app.mdlive_cp.adapters;

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

import com.app.mdlive_cp.PatientMedicalHistoryNew;
import com.app.mdlive_cp.R;

import java.util.List;


public class HistoryDiagAdapter extends ArrayAdapter<String> {

	Activity activity;
	List<String> diagnosisList;

	public HistoryDiagAdapter(Activity activity, List<String> diagnosisList) {
		super(activity, R.layout.history_medi_row, diagnosisList);

		this.activity = activity;
		this.diagnosisList = diagnosisList;

		try {
			((PatientMedicalHistoryNew) activity).tvViewDiag.setText("View ("+diagnosisList.size()+")");
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
			
		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvMedName.setText(diagnosisList.get(position));
		viewHolder.ivDeleteMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				diagnosisList.remove(position);
				notifyDataSetChanged();
			}
		});

		viewHolder.ivEditMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editDiagDialog(position);
			}
		});

		return convertView;
	}


	public void editDiagDialog(final int listPos){
		final Dialog diagDialog = new Dialog(activity);
		diagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		diagDialog.setContentView(R.layout.dialog_edit_diag);
		diagDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = diagDialog.findViewById(R.id.tvDTittle);
		final EditText etAddDiag = (EditText) diagDialog.findViewById(R.id.etAddDiag);
		etAddDiag.setText(diagnosisList.get(listPos));
		etAddDiag.setSelection(etAddDiag.getText().toString().length());

		Button btnSaveDiag = diagDialog.findViewById(R.id.btnSaveDiag);

		tvDTittle.setText("Edit diagnosis");

		btnSaveDiag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String diag = etAddDiag.getText().toString();
				if(diag.isEmpty()){
					etAddDiag.setError("Please enter the diagnosis");
					return;
				}
				diagnosisList.remove(listPos);
				diagnosisList.add(listPos,diag);
				diagDialog.dismiss();

				/*AlertDialog alertDialog =
						new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
								.setMessage("Medication has been edited successfully.")
								.setPositiveButton("Done",null).create();

				alertDialog.show();
				alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/


				notifyDataSetChanged();
			}
		});

		diagDialog.findViewById(R.id.btnCancelDiag).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				diagDialog.dismiss();
			}
		});
		diagDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				diagDialog.dismiss();
			}
		});



		diagDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(diagDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		diagDialog.show();
		diagDialog.getWindow().setAttributes(lp);
	}
}
