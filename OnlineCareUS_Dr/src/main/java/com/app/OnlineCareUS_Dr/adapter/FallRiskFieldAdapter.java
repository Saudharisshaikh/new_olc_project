package com.app.OnlineCareUS_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.OnlineCareUS_Dr.FallRiskForm;
import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.model.FallRiskFieldBean;

import java.util.ArrayList;

public class FallRiskFieldAdapter extends ArrayAdapter<FallRiskFieldBean> {

	Activity activity;
	ArrayList<FallRiskFieldBean> fallRiskFieldBeens;

	public FallRiskFieldAdapter(Activity activity , ArrayList<FallRiskFieldBean> fallRiskFieldBeens) {
		super(activity, R.layout.fallrisk_field_row,fallRiskFieldBeens);
		this.activity = activity;
		this.fallRiskFieldBeens = fallRiskFieldBeens;
	}

	static class ViewHolder {
		TextView tvFallRiskRow;
		RadioGroup rgFallRiskRow;
		RadioButton radioYes,radioNo;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.fallrisk_field_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvFallRiskRow= (TextView) convertView.findViewById(R.id.tvFallRiskRow);
			viewHolder.rgFallRiskRow = (RadioGroup) convertView.findViewById(R.id.rgFallRiskRow);
			viewHolder.radioYes = (RadioButton) convertView.findViewById(R.id.radioYes);
			viewHolder.radioNo = (RadioButton) convertView.findViewById(R.id.radioNo);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvFallRiskRow, viewHolder.tvFallRiskRow);
			convertView.setTag(R.id.rgFallRiskRow,viewHolder.rgFallRiskRow);
			convertView.setTag(R.id.radioYes,viewHolder.radioYes);
			convertView.setTag(R.id.radioNo,viewHolder.radioNo);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvFallRiskRow.setText(fallRiskFieldBeens.get(position).value);

		viewHolder.rgFallRiskRow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Log.d("chk", "id" + checkedId);
						if (checkedId == R.id.radioNo) {
							fallRiskFieldBeens.get(position).radioValue = "0";
						}else if (checkedId == R.id.radioYes) {
							fallRiskFieldBeens.get(position).radioValue = "1";
						}

						((FallRiskForm)activity).setScore();
					}

				});

		if (fallRiskFieldBeens.get(position).radioValue.equalsIgnoreCase("0")){
			viewHolder.radioNo.setChecked(true);
		}else if (fallRiskFieldBeens.get(position).radioValue.equalsIgnoreCase("1")){
			viewHolder.radioYes.setChecked(true);
		}

		return convertView;
	}

}
