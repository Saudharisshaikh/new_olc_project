package com.app.msu_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.R;

import java.util.ArrayList;


public class GoalProgressAdapter extends ArrayAdapter<GoalProgressBean> {

	Activity activity;
	ArrayList<GoalProgressBean> goalProgressBeans;

	public GoalProgressAdapter(Activity activity, ArrayList<GoalProgressBean> goalProgressBeans) {
		super(activity, R.layout.goal_progress_row, goalProgressBeans);

		this.activity = activity;
		this.goalProgressBeans = goalProgressBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goalProgressBeans.size();
	}
	static class ViewHolder {
		TextView tvCPG_Progress,tvCPG_ExpProgress,tvCPG_Barriers,tvCPG_Interv,tvCPG_SelfMgt,tvCPG_RefMade,tvCPG_WhoWasRef,
				tvCPG_EmBackupPlan,tvCPG_AreRevNeeded,tvCPG_WhtRevMade;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.goal_progress_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPG_Progress = (TextView) convertView.findViewById(R.id.tvCPG_Progress);
			viewHolder.tvCPG_ExpProgress = (TextView) convertView.findViewById(R.id.tvCPG_ExpProgress);
			viewHolder.tvCPG_Barriers = (TextView) convertView.findViewById(R.id.tvCPG_Barriers);
			viewHolder.tvCPG_Interv = (TextView) convertView.findViewById(R.id.tvCPG_Interv);
			viewHolder.tvCPG_SelfMgt = (TextView) convertView.findViewById(R.id.tvCPG_SelfMgt);
			viewHolder.tvCPG_RefMade = (TextView) convertView.findViewById(R.id.tvCPG_RefMade);
			viewHolder.tvCPG_WhoWasRef = (TextView) convertView.findViewById(R.id.tvCPG_WhoWasRef);
			viewHolder.tvCPG_EmBackupPlan = (TextView) convertView.findViewById(R.id.tvCPG_EmBackupPlan);
			viewHolder.tvCPG_AreRevNeeded = (TextView) convertView.findViewById(R.id.tvCPG_AreRevNeeded);
			viewHolder.tvCPG_WhtRevMade = (TextView) convertView.findViewById(R.id.tvCPG_WhtRevMade);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPG_Progress, viewHolder.tvCPG_Progress);
			convertView.setTag(R.id.tvCPG_ExpProgress, viewHolder.tvCPG_ExpProgress);
			convertView.setTag(R.id.tvCPG_Barriers, viewHolder.tvCPG_Barriers);
			convertView.setTag(R.id.tvCPG_Interv, viewHolder.tvCPG_Interv);
			convertView.setTag(R.id.tvCPG_SelfMgt, viewHolder.tvCPG_SelfMgt);
			convertView.setTag(R.id.tvCPG_RefMade, viewHolder.tvCPG_RefMade);
			convertView.setTag(R.id.tvCPG_WhoWasRef, viewHolder.tvCPG_WhoWasRef);
			convertView.setTag(R.id.tvCPG_EmBackupPlan, viewHolder.tvCPG_EmBackupPlan);
			convertView.setTag(R.id.tvCPG_AreRevNeeded, viewHolder.tvCPG_AreRevNeeded);
			convertView.setTag(R.id.tvCPG_WhtRevMade, viewHolder.tvCPG_WhtRevMade);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPG_Progress.setText(goalProgressBeans.get(position).progress);
		viewHolder.tvCPG_ExpProgress.setText(goalProgressBeans.get(position).explain_progress);
		viewHolder.tvCPG_Barriers.setText(goalProgressBeans.get(position).barriers);
		viewHolder.tvCPG_Interv.setText(goalProgressBeans.get(position).interventions_to_overcome_barriers);
		viewHolder.tvCPG_SelfMgt.setText(goalProgressBeans.get(position).self_management);
		viewHolder.tvCPG_RefMade.setText(goalProgressBeans.get(position).referral_made);
		viewHolder.tvCPG_WhoWasRef.setText(goalProgressBeans.get(position).referral_to);
		viewHolder.tvCPG_EmBackupPlan.setText(goalProgressBeans.get(position).backup_plan);
		viewHolder.tvCPG_AreRevNeeded.setText(goalProgressBeans.get(position).are_revision_needed);
		viewHolder.tvCPG_WhtRevMade.setText(goalProgressBeans.get(position).what_revisions_were_made);

		return convertView;
	}
}
