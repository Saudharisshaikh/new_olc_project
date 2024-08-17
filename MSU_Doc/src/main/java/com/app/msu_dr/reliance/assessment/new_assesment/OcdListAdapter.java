package com.app.msu_dr.reliance.assessment.new_assesment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_dr.R;

import java.util.List;

public class OcdListAdapter extends ArrayAdapter<OCDlistBean> {

	Activity activity;
	List<OCDlistBean> ocDlistBeans;

	public OcdListAdapter(Activity activity , List<OCDlistBean> ocDlistBeans) {
		super(activity, R.layout.lv_ocdlist_row, ocDlistBeans);
		this.activity = activity;
		this.ocDlistBeans = ocDlistBeans;
	}

	static class ViewHolder {
		TextView tvScore, tvDate, tvSeverity,tvProvider,tvEdit;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_ocdlist_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvScore = convertView.findViewById(R.id.tvScore);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvSeverity = convertView.findViewById(R.id.tvSeverity);
			viewHolder.tvProvider = convertView.findViewById(R.id.tvProvider);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvScore , viewHolder.tvScore);
			convertView.setTag(R.id.tvDate , viewHolder.tvDate);
			convertView.setTag(R.id.tvSeverity , viewHolder.tvSeverity);
			convertView.setTag(R.id.tvProvider , viewHolder.tvProvider);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvScore.setText(ocDlistBeans.get(position).score);
		viewHolder.tvDate.setText(ocDlistBeans.get(position).dateof);
		viewHolder.tvSeverity.setText(ocDlistBeans.get(position).severity);
		//viewHolder.tvProvider.setText(ocDlistBeans.get(position).doctor_name+" ("+ocDlistBeans.get(position).doctor_category+")");


		viewHolder.tvEdit.setEnabled(! ocDlistBeans.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = ocDlistBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityOCD_List){
					((ActivityOCD_List) activity).viewOrEditForm(position, false);
				}
			}
		});

		return convertView;
	}

}
