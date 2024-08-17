package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.ReportsModel;
import com.app.mhcsn_cp.util.DATA;

public class ViewReportsAdapter extends ArrayAdapter<ReportsModel> {

	Activity activity;

	public ViewReportsAdapter(Activity activity) {
		super(activity, R.layout.view_report_row, DATA.allReportsFiltered);

		this.activity = activity;
	}

	static class ViewHolder {

		TextView tvReportName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.view_report_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvReportName = (TextView) convertView.findViewById(R.id.tvReportName);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvReportName);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvReportName.setText(DATA.allReportsFiltered.get(position).name);
		

		viewHolder.tvReportName.setTag(DATA.allReportsFiltered.get(position).name);


		return convertView;
	}
}
