package com.app.fivestaruc.b_health;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.fivestaruc.R;

import java.util.ArrayList;

public class DepInvListAdapter extends ArrayAdapter<DepInvListBean> {

	Activity activity;
	ArrayList<DepInvListBean> depInvListBeans;

	public DepInvListAdapter(Activity activity , ArrayList<DepInvListBean> depInvListBeans) {
		super(activity, R.layout.dep_inv_row,depInvListBeans);
		this.activity = activity;
		this.depInvListBeans = depInvListBeans;
	}

	static class ViewHolder {
		TextView tvScore, tvDate;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.dep_inv_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvScore = convertView.findViewById(R.id.tvScore);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvScore, viewHolder.tvScore);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvScore.setText(depInvListBeans.get(position).score);
		viewHolder.tvDate.setText(depInvListBeans.get(position).dateof);

		return convertView;
	}

}
