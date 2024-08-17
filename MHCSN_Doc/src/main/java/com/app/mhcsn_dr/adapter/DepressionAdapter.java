package com.app.mhcsn_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.app.mhcsn_dr.ActivityDepressionForm;
import com.app.mhcsn_dr.R;
import com.app.mhcsn_dr.model.DepressionFieldBean;
import com.app.mhcsn_dr.util.DATA;

import java.util.ArrayList;

public class DepressionAdapter extends ArrayAdapter<DepressionFieldBean> {

	Activity activity;
	ArrayList<DepressionFieldBean> depressionFieldBeens;

	public DepressionAdapter(Activity activity , ArrayList<DepressionFieldBean> depressionFieldBeens) {
		super(activity, R.layout.depression_row,depressionFieldBeens);
		this.activity = activity;
		this.depressionFieldBeens = depressionFieldBeens;
	}

	static class ViewHolder {
		TextView tvDepressionLabel;
		CheckBox cbDepression;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.depression_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDepressionLabel = (TextView) convertView.findViewById(R.id.tvDepressionLabel);
			viewHolder.cbDepression = (CheckBox) convertView.findViewById(R.id.cbDepression);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDepressionLabel, viewHolder.tvDepressionLabel);
			convertView.setTag(R.id.cbDepression,viewHolder.cbDepression);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.cbDepression.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				DATA.print("--checkchange listener ");
				depressionFieldBeens.get(position).isChecked = isChecked; //!depressionFieldBeens.get(position).isChecked;
				((ActivityDepressionForm)activity).setScore();
			}
		});

		if (depressionFieldBeens.get(position).isChecked){
			viewHolder.cbDepression.setText("Yes");
		}else {
			viewHolder.cbDepression.setText("No");
		}
		viewHolder.cbDepression.setChecked(depressionFieldBeens.get(position).isChecked);
		viewHolder.tvDepressionLabel.setText(depressionFieldBeens.get(position).question);
		return convertView;
	}

}
