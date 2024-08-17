package com.app.priorityone_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.priorityone_dr.R;
import com.app.priorityone_dr.model.BradenScaleBean;

import java.util.ArrayList;

public class AllBradenScaleAdapter extends ArrayAdapter<BradenScaleBean> {

	Activity activity;
	ArrayList<BradenScaleBean> bradenScaleBeens;

	public AllBradenScaleAdapter(Activity activity , ArrayList<BradenScaleBean> bradenScaleBeens) {
		super(activity, R.layout.all_fall_risk_row,bradenScaleBeens);
		this.activity = activity;
		this.bradenScaleBeens = bradenScaleBeens;
	}

	static class ViewHolder {
		TextView tvDate;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.all_fall_risk_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDate.setText(bradenScaleBeens.get(position).dateof);


		return convertView;
	}

}
