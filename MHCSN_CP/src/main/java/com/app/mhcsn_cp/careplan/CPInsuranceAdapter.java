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


public class CPInsuranceAdapter extends ArrayAdapter<CP_InsuranceBean> {

	Activity activity;
	ArrayList<CP_InsuranceBean> cp_insuranceBeans;

	public CPInsuranceAdapter(Activity activity, ArrayList<CP_InsuranceBean> cp_insuranceBeans) {
		super(activity, R.layout.cp_insurance_row, cp_insuranceBeans);

		this.activity = activity;
		this.cp_insuranceBeans = cp_insuranceBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_insuranceBeans.size();
	}
	static class ViewHolder {
		TextView tvInsCompany,tvInsNo,tvInsGroup,tvInsCode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_insurance_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvInsCompany = (TextView) convertView.findViewById(R.id.tvInsCompany);
			viewHolder.tvInsNo = (TextView) convertView.findViewById(R.id.tvInsNo);
			viewHolder.tvInsGroup = (TextView) convertView.findViewById(R.id.tvInsGroup);
			viewHolder.tvInsCode = (TextView) convertView.findViewById(R.id.tvInsCode);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvInsCompany, viewHolder.tvInsCompany);
			convertView.setTag(R.id.tvInsNo, viewHolder.tvInsNo);
			convertView.setTag(R.id.tvInsGroup, viewHolder.tvInsGroup);
			convertView.setTag(R.id.tvInsCode, viewHolder.tvInsCode);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvInsCompany.setText(cp_insuranceBeans.get(position).payer_name);
		viewHolder.tvInsNo.setText(cp_insuranceBeans.get(position).policy_number);
		viewHolder.tvInsGroup.setText(cp_insuranceBeans.get(position).insurance_group);
		viewHolder.tvInsCode.setText(cp_insuranceBeans.get(position).insurance_code);

		return convertView;
	}
}
