package com.app.OnlineCareUS_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.model.FallRiskBean;

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

		viewHolder.tvDate.setText(fallRiskBeens.get(position).dateof);


		return convertView;
	}

}
