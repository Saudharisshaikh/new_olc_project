package com.app.mdlive_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_dr.R;
import com.app.mdlive_dr.model.DepressionBean;

import java.util.ArrayList;

public class AllDepressionAdapter extends ArrayAdapter<DepressionBean> {

	Activity activity;
	ArrayList<DepressionBean> depressionBeens;

	public AllDepressionAdapter(Activity activity , ArrayList<DepressionBean> depressionBeens) {
		super(activity, R.layout.all_fall_risk_row,depressionBeens);
		this.activity = activity;
		this.depressionBeens = depressionBeens;
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

		viewHolder.tvDate.setText(depressionBeens.get(position).dateof);


		return convertView;
	}

}
