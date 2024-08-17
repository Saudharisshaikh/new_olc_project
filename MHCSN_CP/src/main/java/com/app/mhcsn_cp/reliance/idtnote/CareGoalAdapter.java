package com.app.mhcsn_cp.reliance.idtnote;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;


public class CareGoalAdapter extends ArrayAdapter<CareGoalBean> {

	Activity activity;
	ArrayList<CareGoalBean> careGoalBeans;

	public CareGoalAdapter(Activity activity, ArrayList<CareGoalBean> careGoalBeans) {
		super(activity, R.layout.lv_caregoal_row, careGoalBeans);

		this.activity = activity;
		this.careGoalBeans = careGoalBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return careGoalBeans.size();
	}
	static class ViewHolder {
		TextView tvCareGoalRow,tvGoalPriorityLevel,tvDateGoalSet,tvGoalCaseManager;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_caregoal_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCareGoalRow = (TextView) convertView.findViewById(R.id.tvCareGoalRow);
			viewHolder.tvGoalPriorityLevel = (TextView) convertView.findViewById(R.id.tvGoalPriorityLevel);
			viewHolder.tvDateGoalSet = (TextView) convertView.findViewById(R.id.tvDateGoalSet);
			viewHolder.tvGoalCaseManager = (TextView) convertView.findViewById(R.id.tvGoalCaseManager);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCareGoalRow, viewHolder.tvCareGoalRow);
			convertView.setTag(R.id.tvGoalPriorityLevel, viewHolder.tvGoalPriorityLevel);
			convertView.setTag(R.id.tvDateGoalSet, viewHolder.tvDateGoalSet);
			convertView.setTag(R.id.tvGoalCaseManager, viewHolder.tvGoalCaseManager);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvCareGoalRow.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Goal : </font>"+ careGoalBeans.get(position).goal));
		viewHolder.tvGoalPriorityLevel.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Priority Level : </font>"+ careGoalBeans.get(position).goal_priority));
		viewHolder.tvDateGoalSet.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Date Goal Set : </font>"+ careGoalBeans.get(position).date_goal_set));
		viewHolder.tvGoalCaseManager.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Case Manager : </font>"+ careGoalBeans.get(position).caregiver_name));

		return convertView;
	}
}
