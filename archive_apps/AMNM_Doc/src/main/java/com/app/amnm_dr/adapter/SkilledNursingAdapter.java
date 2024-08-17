package com.app.amnm_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.amnm_dr.R;
import com.app.amnm_dr.model.SkilledNursingCheckBean;

import java.util.ArrayList;

public class SkilledNursingAdapter extends ArrayAdapter<SkilledNursingCheckBean> {

	Activity activity;
	ArrayList<SkilledNursingCheckBean> skilledNursingCheckBeen;

	public SkilledNursingAdapter(Activity activity , ArrayList<SkilledNursingCheckBean> skilledNursingCheckBeen) {
		super(activity, R.layout.skilled_nursing_row,skilledNursingCheckBeen);
		this.activity = activity;
		this.skilledNursingCheckBeen = skilledNursingCheckBeen;
	}

	static class ViewHolder {
		TextView tvSkilledNursingLabel;
		CheckBox cbSkilledNursing;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.skilled_nursing_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvSkilledNursingLabel = (TextView) convertView.findViewById(R.id.tvSkilledNursingLabel);
			viewHolder.cbSkilledNursing = (CheckBox) convertView.findViewById(R.id.cbSkilledNursing);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvSkilledNursingLabel, viewHolder.tvSkilledNursingLabel);
			convertView.setTag(R.id.cbSkilledNursing,viewHolder.cbSkilledNursing);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		/*viewHolder.cbSkilledNursing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println("--checkchange listener ");
				skilledNursingCheckBeen.get(position).isChecked = isChecked;
			}
		});*/


		viewHolder.cbSkilledNursing.setChecked(skilledNursingCheckBeen.get(position).isChecked);
		viewHolder.tvSkilledNursingLabel.setText(skilledNursingCheckBeen.get(position).checkBoxLabel);
		return convertView;
	}

}
