package com.app.msu_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.ActivityAllBradenScale;
import com.app.msu_cp.R;
import com.app.msu_cp.model.BradenScaleBean;

import java.util.ArrayList;

public class AllBradenScaleAdapter extends ArrayAdapter<BradenScaleBean> {

	Activity activity;
	ArrayList<BradenScaleBean> bradenScaleBeens;

	public AllBradenScaleAdapter(Activity activity , ArrayList<BradenScaleBean> bradenScaleBeens) {
		super(activity, R.layout.all_braden_scale_row,bradenScaleBeens);
		this.activity = activity;
		this.bradenScaleBeens = bradenScaleBeens;
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
			convertView = layoutInflater.inflate(R.layout.all_braden_scale_row, null);

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

		viewHolder.tvDate.setText(bradenScaleBeens.get(position).dateof);

		//viewHolder.tvEdit.setEnabled(! bradenScaleBeens.get(position).is_lock.equalsIgnoreCase("1"));
		//int drawableRes = bradenScaleBeens.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		//viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.ivLock.setVisibility(View.GONE);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityAllBradenScale){
					((ActivityAllBradenScale) activity).viewOrEditForm(position, false);
				}
			}
		});


		return convertView;
	}

}
