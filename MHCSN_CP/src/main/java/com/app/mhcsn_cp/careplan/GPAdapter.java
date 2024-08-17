package com.app.mhcsn_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.CheckInternetConnection;

import java.util.ArrayList;

public class GPAdapter extends BaseExpandableListAdapter{

	private Activity context;
	CheckInternetConnection checkInternetConnection;
	private ArrayList<GoalProgressBean> data;


	public GPAdapter(Activity context, ArrayList<GoalProgressBean> data) {
		//		super(context, R.layout.custom_supplier_past_tour_detail_attendee_list_item, objects);

		this.context = context;
		this.data = data;

		checkInternetConnection = new CheckInternetConnection(context);
	}


	static class ViewHolder{
		TextView tvProgressName;
		TextView tvCPG_Progress,tvCPG_ExpProgress,tvCPG_Barriers,tvCPG_Interv,tvCPG_SelfMgt,tvCPG_RefMade,tvCPG_WhoWasRef,
				tvCPG_EmBackupPlan,tvCPG_AreRevNeeded,tvCPG_WhtRevMade;
	}

	@Override
	public GoalProgressBean getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return data.get(arg0).goalProgressBeansSub.get(arg1);
	}


	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}


	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean arg2, View convertView, ViewGroup arg4) {



		GoalProgressAdapter.ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.goal_progress_row, null);

			viewHolder = new GoalProgressAdapter.ViewHolder();

			viewHolder.tvCPG_Progress = convertView.findViewById(R.id.tvCPG_Progress);
			viewHolder.tvCPG_ExpProgress = convertView.findViewById(R.id.tvCPG_ExpProgress);
			viewHolder.tvCPG_Barriers = convertView.findViewById(R.id.tvCPG_Barriers);
			viewHolder.tvCPG_Interv = convertView.findViewById(R.id.tvCPG_Interv);
			viewHolder.tvCPG_SelfMgt = convertView.findViewById(R.id.tvCPG_SelfMgt);
			viewHolder.tvCPG_RefMade = convertView.findViewById(R.id.tvCPG_RefMade);
			viewHolder.tvCPG_WhoWasRef = convertView.findViewById(R.id.tvCPG_WhoWasRef);
			viewHolder.tvCPG_EmBackupPlan = convertView.findViewById(R.id.tvCPG_EmBackupPlan);
			viewHolder.tvCPG_AreRevNeeded = convertView.findViewById(R.id.tvCPG_AreRevNeeded);
			viewHolder.tvCPG_WhtRevMade = convertView.findViewById(R.id.tvCPG_WhtRevMade);

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
			viewHolder = (GoalProgressAdapter.ViewHolder) convertView.getTag();
		}

		GoalProgressBean goalProgressBean =
				data.get(groupPosition).goalProgressBeansSub.get(childPosition);

		viewHolder.tvCPG_Progress.setText(goalProgressBean.progress);
		viewHolder.tvCPG_ExpProgress.setText(goalProgressBean.explain_progress);
		viewHolder.tvCPG_Barriers.setText(goalProgressBean.barriers);
		viewHolder.tvCPG_Interv.setText(goalProgressBean.interventions_to_overcome_barriers);
		viewHolder.tvCPG_SelfMgt.setText(goalProgressBean.self_management);
		viewHolder.tvCPG_RefMade.setText(goalProgressBean.referral_made);
		viewHolder.tvCPG_WhoWasRef.setText(goalProgressBean.referral_to);
		viewHolder.tvCPG_EmBackupPlan.setText(goalProgressBean.backup_plan);
		viewHolder.tvCPG_AreRevNeeded.setText(goalProgressBean.are_revision_needed);
		viewHolder.tvCPG_WhtRevMade.setText(goalProgressBean.what_revisions_were_made);

		return convertView;

		/*ViewHolder viewHolder;
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.gprogress_row_child, null);
		}

		viewHolder = new ViewHolder();
		viewHolder.tvChildCategoryData = (TextView) view.findViewById(R.id.tvChildCategoryData);
		viewHolder.cbSelectCategoryData = (CheckBox) view.findViewById(R.id.cbSelectCategoryData);

		viewHolder.tvChildCategoryData.setText(data.get(groupPosition).telemedicineCatDatas.get(childPosition).hcpcs_code+" - "+
				data.get(groupPosition).telemedicineCatDatas.get(childPosition).service_name);


		viewHolder.cbSelectCategoryData.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				data.get(groupPosition).telemedicineCatDatas.get(childPosition).isSelected = isChecked;
			}
		});
		viewHolder.cbSelectCategoryData.setChecked(data.get(groupPosition).telemedicineCatDatas.get(childPosition).isSelected);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				data.get(groupPosition).telemedicineCatDatas.get(childPosition).isSelected = !data.get(groupPosition).telemedicineCatDatas.get(childPosition).isSelected;
				notifyDataSetChanged();
			}
		});

		return view;*/
	}


	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0).goalProgressBeansSub.size();
	}


	@Override
	public GoalProgressBean getGroup(int arg0) {
		return data.get(arg0);
	}


	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return data.size();
	}


	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}


	@Override
	public View getGroupView(final int arg0, boolean arg1, View view, ViewGroup arg3) {

		ViewHolder viewHolder;

		GoalProgressBean headerInfo = getGroup(arg0);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.gprogress_row_parent, null);
		}
		viewHolder = new ViewHolder();

		viewHolder.tvProgressName = view.findViewById(R.id.tvProgressName);

		viewHolder.tvProgressName.setText("Progress : "+headerInfo.progress);//headerInfo.progress


		return view;
	}


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
