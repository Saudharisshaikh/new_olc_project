package com.app.mhcsn_spe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.model.EnvirBean;

import java.util.ArrayList;

public class AllEnvirAdapter extends ArrayAdapter<EnvirBean> {

	Activity activity;
	ArrayList<EnvirBean> envirBeen;

	public AllEnvirAdapter(Activity activity , ArrayList<EnvirBean> envirBeen) {
		super(activity, R.layout.all_fall_risk_row,envirBeen);
		this.activity = activity;
		this.envirBeen = envirBeen;
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

		viewHolder.tvDate.setText(envirBeen.get(position).dateof);


		return convertView;
	}

}
