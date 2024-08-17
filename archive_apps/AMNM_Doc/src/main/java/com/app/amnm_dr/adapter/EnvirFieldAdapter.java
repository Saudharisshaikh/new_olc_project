package com.app.amnm_dr.adapter;

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

import com.app.amnm_dr.R;
import com.app.amnm_dr.model.EnvirFieldBean;

import java.util.ArrayList;

public class EnvirFieldAdapter extends ArrayAdapter<EnvirFieldBean> {

	Activity activity;
	ArrayList<EnvirFieldBean> envirFieldBeens;

	public EnvirFieldAdapter(Activity activity , ArrayList<EnvirFieldBean> envirFieldBeens) {
		super(activity, R.layout.envir_field_row,envirFieldBeens);
		this.activity = activity;
		this.envirFieldBeens = envirFieldBeens;
	}

	static class ViewHolder {
		TextView tvEnvRow;
		RadioGroup rgEnvRow;
		RadioButton radioNA,radioYes,radioNo;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.envir_field_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvEnvRow= (TextView) convertView.findViewById(R.id.tvEnvRow);
			viewHolder.rgEnvRow = (RadioGroup) convertView.findViewById(R.id.rgEnvRow);
			viewHolder.radioNA = (RadioButton) convertView.findViewById(R.id.radioNA);
			viewHolder.radioYes = (RadioButton) convertView.findViewById(R.id.radioYes);
			viewHolder.radioNo = (RadioButton) convertView.findViewById(R.id.radioNo);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvEnvRow, viewHolder.tvEnvRow);
			convertView.setTag(R.id.rgEnvRow,viewHolder.rgEnvRow);
			convertView.setTag(R.id.radioNA,viewHolder.radioNA);
			convertView.setTag(R.id.radioYes,viewHolder.radioYes);
			convertView.setTag(R.id.radioNo,viewHolder.radioNo);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvEnvRow.setText(envirFieldBeens.get(position).value);

		viewHolder.rgEnvRow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Log.d("chk", "id" + checkedId);
						if (checkedId == R.id.radioNA) {
							envirFieldBeens.get(position).radioValue = "2";
						} else if (checkedId == R.id.radioNo) {
							envirFieldBeens.get(position).radioValue = "0";
						}else if (checkedId == R.id.radioYes) {
							envirFieldBeens.get(position).radioValue = "1";
						}
					}

				});

		if (envirFieldBeens.get(position).radioValue.equalsIgnoreCase("0")){
			viewHolder.radioNo.setChecked(true);
		}else if (envirFieldBeens.get(position).radioValue.equalsIgnoreCase("1")){
			viewHolder.radioYes.setChecked(true);
		}else if (envirFieldBeens.get(position).radioValue.equalsIgnoreCase("2")){
			viewHolder.radioNA.setChecked(true);
		}

		return convertView;
	}

}
