package com.app.msu_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.ActivityAllDepression;
import com.app.msu_cp.R;

import java.util.ArrayList;

public class AllDepressionAdapter extends ArrayAdapter<DepressionBean> {

	Activity activity;
	ArrayList<DepressionBean> depressionBeens;

	public AllDepressionAdapter(Activity activity , ArrayList<DepressionBean> depressionBeens) {
		super(activity, R.layout.all_depr_scale_row,depressionBeens);
		this.activity = activity;
		this.depressionBeens = depressionBeens;
	}

	static class ViewHolder {
		TextView tvDate, tvEdit;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.all_depr_scale_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDate.setText(depressionBeens.get(position).dateof);


		/*viewHolder.tvEdit.setEnabled(! depressionBeens.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = depressionBeens.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);*/

		viewHolder.ivLock.setVisibility(View.GONE);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityAllDepression){
					((ActivityAllDepression) activity).viewOrEditForm(position, false);
				}
			}
		});


		return convertView;
	}

}
