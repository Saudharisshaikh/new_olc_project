package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.List;

public class SdhsListAdapter extends ArrayAdapter<SDHSlistBean> {

	Activity activity;
	List<SDHSlistBean> sdhSlistBeans;

	public SdhsListAdapter(Activity activity , List<SDHSlistBean> sdhSlistBeans) {
		super(activity, R.layout.lv_sdhslist_row, sdhSlistBeans);
		this.activity = activity;
		this.sdhSlistBeans = sdhSlistBeans;
	}

	static class ViewHolder {
		TextView tvDate, tvSeverity,tvProvider, tvEdit;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_sdhslist_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvSeverity = convertView.findViewById(R.id.tvSeverity);
			viewHolder.tvProvider = convertView.findViewById(R.id.tvProvider);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			convertView.setTag(R.id.tvSeverity , viewHolder.tvSeverity);
			convertView.setTag(R.id.tvProvider , viewHolder.tvProvider);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvDate.setText(sdhSlistBeans.get(position).dateof);
		viewHolder.tvSeverity.setText(sdhSlistBeans.get(position).is_issue);
		viewHolder.tvProvider.setText(sdhSlistBeans.get(position).doctor_name);//+" ("+sdhSlistBeans.get(position).doctor_category+")"


		viewHolder.tvEdit.setEnabled(! sdhSlistBeans.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = sdhSlistBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivitySDHSList){
					((ActivitySDHSList) activity).viewOrEditForm(position);
				}
			}
		});

		return convertView;
	}

}
