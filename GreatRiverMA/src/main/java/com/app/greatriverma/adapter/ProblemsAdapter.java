package com.app.greatriverma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.greatriverma.R;
import com.app.greatriverma.model.ProblemBean;

import java.util.ArrayList;


public class ProblemsAdapter extends ArrayAdapter<ProblemBean> {

	Activity activity;
	ArrayList<ProblemBean> problemBeens;

	public ProblemsAdapter(Activity activity,ArrayList<ProblemBean> problemBeens) {
		super(activity, R.layout.problem_list_row, problemBeens);

		this.activity = activity;
		this.problemBeens = problemBeens;
	}

	static class ViewHolder {
		TextView tvSymptoms,tvConditions,tvDate;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.problem_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvSymptoms = (TextView) convertView.findViewById(R.id.tvSymptoms);
			viewHolder.tvConditions = (TextView) convertView.findViewById(R.id.tvConditions);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvSymptoms, viewHolder.tvSymptoms);
			convertView.setTag(R.id.tvConditions, viewHolder.tvConditions);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvSymptoms.setText(problemBeens.get(position).symptom_name);
		viewHolder.tvConditions.setText(problemBeens.get(position).condition_name);
		viewHolder.tvDate.setText(problemBeens.get(position).dateof);

		return convertView;
	}
}
