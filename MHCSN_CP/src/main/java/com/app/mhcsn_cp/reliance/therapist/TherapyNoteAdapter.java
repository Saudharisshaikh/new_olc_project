package com.app.mhcsn_cp.reliance.therapist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class TherapyNoteAdapter extends ArrayAdapter<TherapyNoteBean> {

	Activity activity;
	ArrayList<TherapyNoteBean> therapyNoteBeans, therapyNoteBeansOrig;
	//SharedPreferences prefs;

	public TherapyNoteAdapter(Activity activity, ArrayList<TherapyNoteBean> therapyNoteBeans) {
		super(activity, R.layout.lv_therapynote_row, therapyNoteBeans);

		this.activity = activity;
		this.therapyNoteBeans = therapyNoteBeans;
		this.therapyNoteBeansOrig = new ArrayList<>();
		this.therapyNoteBeansOrig.addAll(therapyNoteBeans);
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		TextView tvTherapyNoteDate,tvTherapyNoteStartTime,tvTherapyNoteEndTime,tvTherapyNoteDiagnosis,tvTherapyNoteSummary,tvTherapyNoteEducation,tvTherapyNoteGoals;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_therapynote_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvTherapyNoteDate = convertView.findViewById(R.id.tvTherapyNoteDate);
			viewHolder.tvTherapyNoteStartTime = convertView.findViewById(R.id.tvTherapyNoteStartTime);
			viewHolder.tvTherapyNoteEndTime = convertView.findViewById(R.id.tvTherapyNoteEndTime);
			viewHolder.tvTherapyNoteDiagnosis = convertView.findViewById(R.id.tvTherapyNoteDiagnosis);
			viewHolder.tvTherapyNoteSummary = convertView.findViewById(R.id.tvTherapyNoteSummary);
			viewHolder.tvTherapyNoteEducation = convertView.findViewById(R.id.tvTherapyNoteEducation);
			viewHolder.tvTherapyNoteGoals = convertView.findViewById(R.id.tvTherapyNoteGoals);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvTherapyNoteDate, viewHolder.tvTherapyNoteDate);
			convertView.setTag(R.id.tvTherapyNoteStartTime, viewHolder.tvTherapyNoteStartTime);
			convertView.setTag(R.id.tvTherapyNoteEndTime, viewHolder.tvTherapyNoteEndTime);
			convertView.setTag(R.id.tvTherapyNoteDiagnosis, viewHolder.tvTherapyNoteDiagnosis);
			convertView.setTag(R.id.tvTherapyNoteSummary, viewHolder.tvTherapyNoteSummary);
			convertView.setTag(R.id.tvTherapyNoteEducation, viewHolder.tvTherapyNoteEducation);
			convertView.setTag(R.id.tvTherapyNoteGoals, viewHolder.tvTherapyNoteGoals);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvTherapyNoteDate.setText(therapyNoteBeans.get(position).date);
		viewHolder.tvTherapyNoteStartTime.setText(therapyNoteBeans.get(position).start_time);
		viewHolder.tvTherapyNoteEndTime.setText(therapyNoteBeans.get(position).end_time);
		viewHolder.tvTherapyNoteDiagnosis.setText(therapyNoteBeans.get(position).diagnosis);
		viewHolder.tvTherapyNoteSummary.setText(therapyNoteBeans.get(position).summary);
		viewHolder.tvTherapyNoteEducation.setText(therapyNoteBeans.get(position).education);

		viewHolder.tvTherapyNoteGoals.setText(therapyNoteBeans.get(position).goal);



		return convertView;
	}



	public void filter(String filterText) {
		therapyNoteBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- therapyNoteBeansOrig size: "+therapyNoteBeansOrig.size());
		if(filterText.length() == 0) {
			therapyNoteBeans.addAll(therapyNoteBeansOrig);
		} else {
			for(TherapyNoteBean temp : therapyNoteBeansOrig) {
				if(temp.diagnosis.toLowerCase(Locale.getDefault()).contains(filterText) || temp.education.toLowerCase(Locale.getDefault()).contains(filterText)
						|| temp.summary.toLowerCase(Locale.getDefault()).contains(filterText)) {
					therapyNoteBeans.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
