package com.app.mhcsn_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.ArrayList;


public class CP_AllergyAdapter extends ArrayAdapter<CP_AllergyBean> {

	Activity activity;
	ArrayList<CP_AllergyBean> cp_allergyBeans;

	public CP_AllergyAdapter(Activity activity, ArrayList<CP_AllergyBean> cp_allergyBeans) {
		super(activity, R.layout.cp_allergy_row, cp_allergyBeans);

		this.activity = activity;
		this.cp_allergyBeans = cp_allergyBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_allergyBeans.size();
	}
	static class ViewHolder {
		TextView tvCPA_Substance,tvCPA_DateOccurred,tvCPA_Type,tvCPA_DocumentedBy,tvCPA_Reaction;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_allergy_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPA_Substance = (TextView) convertView.findViewById(R.id.tvCPA_Substance);
			viewHolder.tvCPA_DateOccurred = (TextView) convertView.findViewById(R.id.tvCPA_DateOccurred);
			viewHolder.tvCPA_Type = (TextView) convertView.findViewById(R.id.tvCPA_Type);
			viewHolder.tvCPA_DocumentedBy = (TextView) convertView.findViewById(R.id.tvCPA_DocumentedBy);
			viewHolder.tvCPA_Reaction = (TextView) convertView.findViewById(R.id.tvCPA_Reaction);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPA_Substance, viewHolder.tvCPA_Substance);
			convertView.setTag(R.id.tvCPA_DateOccurred, viewHolder.tvCPA_DateOccurred);
			convertView.setTag(R.id.tvCPA_Type, viewHolder.tvCPA_Type);
			convertView.setTag(R.id.tvCPA_DocumentedBy, viewHolder.tvCPA_DocumentedBy);
			convertView.setTag(R.id.tvCPA_Reaction, viewHolder.tvCPA_Reaction);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPA_Substance.setText(cp_allergyBeans.get(position).substance);
		viewHolder.tvCPA_DateOccurred.setText(cp_allergyBeans.get(position).date_occurred);
		viewHolder.tvCPA_Type.setText(cp_allergyBeans.get(position).type);
		viewHolder.tvCPA_DocumentedBy.setText(cp_allergyBeans.get(position).documented_by);
		viewHolder.tvCPA_Reaction.setText(cp_allergyBeans.get(position).reaction);

		return convertView;
	}
}
