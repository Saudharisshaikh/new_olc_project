package com.app.OnlineCareUS_MA.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareUS_MA.R;
import com.app.OnlineCareUS_MA.model.NotesBean;

import java.util.ArrayList;

public class NotesAdapter extends ArrayAdapter<NotesBean> {

	Activity activity;
	ArrayList<NotesBean> notesBeans;

	public NotesAdapter(Activity activity,ArrayList<NotesBean> notesBeans) {
		super(activity, R.layout.lv_soap_notes_row, notesBeans);

		this.activity = activity;
		this.notesBeans = notesBeans;
		
	}

	static class ViewHolder {

		//TextView tvHistory,tvPlan,tvObjective,tvFamily,tvSubjective,tvAssesment,tvDate;
		
		TextView tvNoteDate,tvNoteDate1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_soap_notes_row, null);
			
			viewHolder = new ViewHolder();
			
			/*viewHolder.tvHistory = (TextView) convertView.findViewById(R.id.tvHistory);
			viewHolder.tvPlan = (TextView) convertView.findViewById(R.id.tvPlan);
			viewHolder.tvObjective = (TextView) convertView.findViewById(R.id.tvObjective);
			viewHolder.tvFamily = (TextView) convertView.findViewById(R.id.tvFamily);
			viewHolder.tvSubjective = (TextView) convertView.findViewById(R.id.tvSubjective);
			viewHolder.tvAssesment = (TextView) convertView.findViewById(R.id.tvAssesment);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvHistory, viewHolder.tvHistory);
			convertView.setTag(R.id.tvPlan, viewHolder.tvPlan);
			convertView.setTag(R.id.tvObjective, viewHolder.tvObjective);
			convertView.setTag(R.id.tvFamily, viewHolder.tvFamily);
			convertView.setTag(R.id.tvSubjective, viewHolder.tvSubjective);
			convertView.setTag(R.id.tvAssesment, viewHolder.tvAssesment);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);*/
			
			viewHolder.tvNoteDate = (TextView) convertView.findViewById(R.id.tvNoteDate);
			viewHolder.tvNoteDate1 = (TextView) convertView.findViewById(R.id.tvNoteDate1);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNoteDate, viewHolder.tvNoteDate);
			convertView.setTag(R.id.tvNoteDate1, viewHolder.tvNoteDate1);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		/*viewHolder.tvHistory.setText(notesBeans.get(position).getHistory());
		viewHolder.tvPlan.setText(notesBeans.get(position).getPlan());
		viewHolder.tvObjective.setText(notesBeans.get(position).getObjective());
		viewHolder.tvFamily.setText(notesBeans.get(position).getFamily());
		viewHolder.tvSubjective.setText(notesBeans.get(position).getSubjective());
		viewHolder.tvAssesment.setText(notesBeans.get(position).getAssesment());
		viewHolder.tvDate.setText(notesBeans.get(position).getNotes_date());

		viewHolder.tvHistory.setTag(notesBeans.get(position).getHistory());
		viewHolder.tvPlan.setTag(notesBeans.get(position).getPlan());
		viewHolder.tvObjective.setTag(notesBeans.get(position).getObjective());
		viewHolder.tvFamily.setTag(notesBeans.get(position).getFamily());
		viewHolder.tvSubjective.setTag(notesBeans.get(position).getSubjective());
		viewHolder.tvAssesment.setTag(notesBeans.get(position).getAssesment());
		viewHolder.tvDate.setTag(notesBeans.get(position).getNotes_date());*/
		
		viewHolder.tvNoteDate.setText("Added on: "+notesBeans.get(position).notes_date);
		viewHolder.tvNoteDate1.setText(notesBeans.get(position).notes_date);
		
		viewHolder.tvNoteDate.setTag("Added on: "+notesBeans.get(position).notes_date);
		viewHolder.tvNoteDate1.setTag(notesBeans.get(position).notes_date);

		return convertView;
	}
}
