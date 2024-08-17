package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_cp.R;

import java.util.ArrayList;


public class CP_HospAdapter extends ArrayAdapter<CP_HospBean> {

	Activity activity;
	ArrayList<CP_HospBean> cp_hospBeans;

	public CP_HospAdapter(Activity activity, ArrayList<CP_HospBean> cp_hospBeans) {
		super(activity, R.layout.cp_hosp_row, cp_hospBeans);

		this.activity = activity;
		this.cp_hospBeans = cp_hospBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_hospBeans.size();
	}
	static class ViewHolder {
		TextView tvCPH_Title,tvCPH_DateAdmitted,tvCPH_Comments;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_hosp_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPH_Title = (TextView) convertView.findViewById(R.id.tvCPH_Title);
			viewHolder.tvCPH_DateAdmitted = (TextView) convertView.findViewById(R.id.tvCPH_DateAdmitted);
			viewHolder.tvCPH_Comments = (TextView) convertView.findViewById(R.id.tvCPH_Comments);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPH_Title, viewHolder.tvCPH_Title);
			convertView.setTag(R.id.tvCPH_DateAdmitted, viewHolder.tvCPH_DateAdmitted);
			convertView.setTag(R.id.tvCPH_Comments, viewHolder.tvCPH_Comments);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPH_Title.setText(cp_hospBeans.get(position).description);
		viewHolder.tvCPH_DateAdmitted.setText(cp_hospBeans.get(position).date_admitted);
		viewHolder.tvCPH_Comments.setText(cp_hospBeans.get(position).comments);

		return convertView;
	}
}
