package com.app.mhcsn_spe.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_spe.R;

import java.util.List;

public class DastListAdapter extends ArrayAdapter<DastListBean> {

	Activity activity;
	List<DastListBean> dastListBeans;

	public DastListAdapter(Activity activity , List<DastListBean> dastListBeans) {
		super(activity, R.layout.lv_dastlist_row, dastListBeans);
		this.activity = activity;
		this.dastListBeans = dastListBeans;
	}

	static class ViewHolder {
		TextView tvScore, tvDate,tvProvider, tvEdit, tvSeverity;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dastlist_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvScore = convertView.findViewById(R.id.tvScore);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvProvider = convertView.findViewById(R.id.tvProvider);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);
			viewHolder.tvSeverity = convertView.findViewById(R.id.tvSeverity);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvScore, viewHolder.tvScore);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			convertView.setTag(R.id.tvProvider, viewHolder.tvProvider);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);
			convertView.setTag(R.id.tvSeverity , viewHolder.tvSeverity);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvScore.setText(dastListBeans.get(position).score);
		viewHolder.tvDate.setText(dastListBeans.get(position).dateof);
		viewHolder.tvSeverity.setText(dastListBeans.get(position).severity);
		viewHolder.tvProvider.setText(dastListBeans.get(position).doctor_name);//+" ("+dastListBeans.get(position).doctor_category+")"


		viewHolder.tvEdit.setEnabled(! dastListBeans.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = dastListBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityDastList){
					((ActivityDastList) activity).viewOrEditForm(position, false);
				}
			}
		});

		return convertView;
	}

}
