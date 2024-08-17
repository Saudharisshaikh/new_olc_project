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


public class CPDiagAdapter extends ArrayAdapter<CP_DiagnosisBean> {

	Activity activity;
	ArrayList<CP_DiagnosisBean> cp_diagnosisBeans;

	public CPDiagAdapter(Activity activity, ArrayList<CP_DiagnosisBean> cp_diagnosisBeans) {
		super(activity, R.layout.lv_cp_diag_row, cp_diagnosisBeans);

		this.activity = activity;
		this.cp_diagnosisBeans = cp_diagnosisBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_diagnosisBeans.size();
	}
	static class ViewHolder {
		TextView tvCPDiag,tvCPD_Des,tvCPD_DateDiag,tvCPD_DiagBy,tvCPD_Comments;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_cp_diag_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPDiag = (TextView) convertView.findViewById(R.id.tvCPDiag);
			viewHolder.tvCPD_Des = (TextView) convertView.findViewById(R.id.tvCPD_Des);
			viewHolder.tvCPD_DateDiag = (TextView) convertView.findViewById(R.id.tvCPD_DateDiag);
			viewHolder.tvCPD_DiagBy = (TextView) convertView.findViewById(R.id.tvCPD_DiagBy);
			viewHolder.tvCPD_Comments = (TextView) convertView.findViewById(R.id.tvCPD_Comments);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCPDiag, viewHolder.tvCPDiag);
			convertView.setTag(R.id.tvCPD_Des, viewHolder.tvCPD_Des);
			convertView.setTag(R.id.tvCPD_DateDiag, viewHolder.tvCPD_DateDiag);
			convertView.setTag(R.id.tvCPD_DiagBy, viewHolder.tvCPD_DiagBy);
			convertView.setTag(R.id.tvCPD_Comments, viewHolder.tvCPD_Comments);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPDiag.setText(cp_diagnosisBeans.get(position).diagnosis);
		viewHolder.tvCPD_Des.setText(cp_diagnosisBeans.get(position).description);
		viewHolder.tvCPD_DateDiag.setText(cp_diagnosisBeans.get(position).date_diagnosed);
		viewHolder.tvCPD_DiagBy.setText(cp_diagnosisBeans.get(position).diagnosed_by);
		viewHolder.tvCPD_Comments.setText(cp_diagnosisBeans.get(position).comments);

		return convertView;
	}
}
