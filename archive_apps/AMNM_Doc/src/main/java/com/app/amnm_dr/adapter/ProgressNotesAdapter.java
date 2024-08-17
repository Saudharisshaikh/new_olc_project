package com.app.amnm_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.app.amnm_dr.R;
import com.app.amnm_dr.model.ProgressNoteBean;
import com.app.amnm_dr.util.ActionEditText;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProgressNotesAdapter extends ArrayAdapter<ProgressNoteBean> {

	Activity activity;
	ArrayList<ProgressNoteBean> progressNoteBeans;

	public ProgressNotesAdapter(Activity activity , ArrayList<ProgressNoteBean> progressNoteBeans) {
		super(activity, R.layout.lv_pr_note_row,progressNoteBeans);
		this.activity = activity;
		this.progressNoteBeans = progressNoteBeans;
	}

	static class ViewHolder {
		EditText etPtName,etProviderName,etDate,etTime,etSessionTime,etSymptom,etCondition,etExpNotes,etInterventions,etPtRes,etCarePlan;
		EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBMI;
		TextView tvVitalsDate;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_pr_note_row, null);

			viewHolder = new ViewHolder();
			viewHolder.etPtName = (EditText) convertView.findViewById(R.id.etPtName);
			viewHolder.etProviderName = (EditText) convertView.findViewById(R.id.etProviderName);
			viewHolder.etDate = (EditText) convertView.findViewById(R.id.etDate);
			viewHolder.etTime = (EditText) convertView.findViewById(R.id.etTime);
			viewHolder.etSessionTime = (EditText) convertView.findViewById(R.id.etSessionTime);
			viewHolder.etSymptom = (EditText) convertView.findViewById(R.id.etSymptom);
			viewHolder.etCondition = (EditText) convertView.findViewById(R.id.etCondition);
			viewHolder.etExpNotes = (EditText) convertView.findViewById(R.id.etExpNotes);
			viewHolder.etInterventions = (EditText) convertView.findViewById(R.id.etInterventions);
			viewHolder.etPtRes = (EditText) convertView.findViewById(R.id.etPtRes);
			viewHolder.etCarePlan = (EditText) convertView.findViewById(R.id.etCarePlan);

			viewHolder.etOTBP = (ActionEditText) convertView.findViewById(R.id.etOTBP);
			viewHolder.etOTHR = (ActionEditText) convertView.findViewById(R.id.etOTHR);
			viewHolder.etOTRespirations = (ActionEditText) convertView.findViewById(R.id.etOTRespirations);
			viewHolder.etOTO2Saturations = (ActionEditText) convertView.findViewById(R.id.etOTO2Saturations);
			viewHolder.etOTBloodSugar = (ActionEditText) convertView.findViewById(R.id.etOTBloodSugar);
			viewHolder.etOTTemperature = (ActionEditText) convertView.findViewById(R.id.etOTTemperature);
			viewHolder.etOTHeight = (ActionEditText) convertView.findViewById(R.id.etOTHeight);
			viewHolder.etOTWeight = (ActionEditText) convertView.findViewById(R.id.etOTWeight);
			viewHolder.etOTBMI = (ActionEditText) convertView.findViewById(R.id.etOTBMI);
			viewHolder.tvVitalsDate = (TextView) convertView.findViewById(R.id.tvVitalsDate);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.etPtName, viewHolder.etPtName);
			convertView.setTag(R.id.etProviderName, viewHolder.etProviderName);
			convertView.setTag(R.id.etDate, viewHolder.etDate);
			convertView.setTag(R.id.etTime, viewHolder.etTime);
			convertView.setTag(R.id.etSessionTime, viewHolder.etSessionTime);
			convertView.setTag(R.id.etSymptom, viewHolder.etSymptom);
			convertView.setTag(R.id.etCondition, viewHolder.etCondition);
			convertView.setTag(R.id.etExpNotes, viewHolder.etExpNotes);
			convertView.setTag(R.id.etInterventions, viewHolder.etInterventions);
			convertView.setTag(R.id.etPtRes, viewHolder.etPtRes);
			convertView.setTag(R.id.etCarePlan, viewHolder.etCarePlan);

			convertView.setTag(R.id.etOTBP, viewHolder.etOTBP);
			convertView.setTag(R.id.etOTHR, viewHolder.etOTHR);
			convertView.setTag(R.id.etOTRespirations, viewHolder.etOTRespirations);
			convertView.setTag(R.id.etOTO2Saturations, viewHolder.etOTO2Saturations);
			convertView.setTag(R.id.etOTBloodSugar, viewHolder.etOTBloodSugar);
			convertView.setTag(R.id.etOTTemperature, viewHolder.etOTTemperature);
			convertView.setTag(R.id.etOTHeight, viewHolder.etOTHeight);
			convertView.setTag(R.id.etOTWeight, viewHolder.etOTWeight);
			convertView.setTag(R.id.etOTBMI, viewHolder.etOTBMI);
			convertView.setTag(R.id.tvVitalsDate, viewHolder.tvVitalsDate);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.etPtName.setText(progressNoteBeans.get(position).patient_name);
		viewHolder.etProviderName.setText(progressNoteBeans.get(position).first_name+" "+progressNoteBeans.get(position).last_name);
		viewHolder.etDate.setText(progressNoteBeans.get(position).ddate);
		viewHolder.etTime.setText(progressNoteBeans.get(position).ttime);
		viewHolder.etSessionTime.setText(progressNoteBeans.get(position).session_length+" Min");
		viewHolder.etSymptom.setText(progressNoteBeans.get(position).symptom_name);
		viewHolder.etCondition.setText(progressNoteBeans.get(position).condition_name);
		viewHolder.etExpNotes.setText(progressNoteBeans.get(position).explanatory_notes);
		viewHolder.etInterventions.setText(progressNoteBeans.get(position).interventions);
		viewHolder.etPtRes.setText(progressNoteBeans.get(position).feedback);
		viewHolder.etCarePlan.setText(progressNoteBeans.get(position).care_plan);

		if(!progressNoteBeans.get(position).ot_data.isEmpty()){
			try {

				JSONObject virtual_ot_data = new JSONObject(progressNoteBeans.get(position).ot_data);
				if(virtual_ot_data.has("ot_respirations")){
					String ot_respirations = virtual_ot_data.getString("ot_respirations");
					viewHolder.etOTRespirations.setText(ot_respirations);
				}
				if(virtual_ot_data.has("ot_blood_sugar")){
					String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
					viewHolder.etOTBloodSugar.setText(ot_blood_sugar);
				}
				if(virtual_ot_data.has("ot_hr")){
					String ot_hr = virtual_ot_data.getString("ot_hr");
					viewHolder.etOTHR.setText(ot_hr);
				}
				if(virtual_ot_data.has("ot_bp")){
					String ot_bp = virtual_ot_data.getString("ot_bp");
					viewHolder.etOTBP.setText(ot_bp);
				}
				if(virtual_ot_data.has("ot_saturation")){
					String ot_saturation = virtual_ot_data.getString("ot_saturation");
					viewHolder.etOTO2Saturations.setText(ot_saturation);
				}

				if(virtual_ot_data.has("ot_height")){
					String ot_height = virtual_ot_data.getString("ot_height");
					viewHolder.etOTHeight.setText(ot_height);
				}
				if(virtual_ot_data.has("ot_temperature")){
					String ot_temperature = virtual_ot_data.getString("ot_temperature");
					viewHolder.etOTTemperature.setText(ot_temperature);
				}
				if(virtual_ot_data.has("ot_weight")){
					String ot_weight = virtual_ot_data.getString("ot_weight");
					viewHolder.etOTWeight.setText(ot_weight);
				}

				if(virtual_ot_data.has("ot_bmi")){
					String bmi = virtual_ot_data.getString("ot_bmi");
					viewHolder.etOTBMI.setText(bmi);
				}

			}catch (Exception e){
				e.printStackTrace();
			}
		}

		return convertView;
	}

}
