package com.app.OnlineCareUS_MA.adapter;

import com.app.OnlineCareUS_MA.R;
import com.app.OnlineCareUS_MA.model.ReportsModel;
import com.app.OnlineCareUS_MA.util.DATA;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
