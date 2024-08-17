package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.ActivityAllFallRisk;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.FallRiskBean;

import java.util.ArrayList;

public class AllFallRiskAdapter extends ArrayAdapter<FallRiskBean> {

	Activity activity;
	ArrayList<FallRiskBean> fallRiskBeens;

	public AllFallRiskAdapter(Activity activity , ArrayList<FallRiskBean> fallRiskBeens) {
		super(activity, R.layout.all_fall_risk_row,fallRiskBeens);
		this.activity = activity;
		this.fallRiskBeens = fallRiskBeens;
	}

	static class ViewHolder {
		TextView tvDate,tvProvider, tvEdit;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.all_fall_risk_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvProvider = (TextView) convertView.findViewById(R.id.tvProvider);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			convertView.setTag(R.id.tvProvider, viewHolder.tvProvider);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDate.setText(fallRiskBeens.get(position).dateof);
		viewHolder.tvProvider.setText(fallRiskBeens.get(position).doctor_name+" ("+fallRiskBeens.get(position).doctor_category+")");

		viewHolder.tvEdit.setEnabled(! fallRiskBeens.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = fallRiskBeens.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityAllFallRisk){
					((ActivityAllFallRisk) activity).viewOrEditForm(position, false);
				}
			}
		});


		return convertView;
	}

}
