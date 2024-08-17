package com.app.priorityone_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.priorityone_uc.R;
import com.app.priorityone_uc.model.ReportsModel;
import com.app.priorityone_uc.util.DATA;

public class SelecReportsAdapter extends ArrayAdapter<ReportsModel> {
	
	Activity activity;
	
	//TextView tvUsersRowStatus,tvUsersRowName;
	
//	ImageView imgUsersRow;
//	public CheckBox checkSelectContact;
	
	int getPosition;

	public SelecReportsAdapter(Activity activity) {
		super(activity, R.layout.lay_select_reports_row, DATA.allReports);
		this.activity = activity;
	}

	static class ViewHolder {
		protected TextView tvReportName,tvReportDate;
		protected CheckBox checkSelectReport;	
		ImageView ivReport;

}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lay_select_reports_row, null);

			 viewHolder = new ViewHolder();
			 viewHolder.tvReportName = (TextView) convertView.findViewById(R.id.tvReportName);
			 viewHolder.tvReportDate = (TextView) convertView.findViewById(R.id.tvReportDate);
			 viewHolder.checkSelectReport = (CheckBox) convertView.findViewById(R.id.checkSelectReport);
			 viewHolder.ivReport = (ImageView) convertView.findViewById(R.id.ivReport);
							 
			 viewHolder.checkSelectReport.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					 getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
					 DATA.allReports.get(getPosition).setSelected(buttonView.isChecked());// Set the value of checkbox to maintain its state.

					if(buttonView.isChecked()) {
						
						DATA.allReports.get(getPosition).status = "1";
						
					}
					else {
						DATA.allReports.get(getPosition).status = "0";
					}					
				}
			});

				convertView.setTag(viewHolder);
				convertView.setTag(R.id.tvReportName, viewHolder.tvReportName);
				convertView.setTag(R.id.tvReportDate, viewHolder.tvReportDate);
				convertView.setTag(R.id.checkSelectReport,viewHolder.checkSelectReport);
				convertView.setTag(R.id.ivReport,viewHolder.ivReport);

				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//			viewHolder.checkSelectContact.setOnCheckedChangeListener(null);
		}

		viewHolder.checkSelectReport.setTag(position); // This line is important.

		viewHolder.tvReportName.setText(DATA.allReports.get(position).name);
		viewHolder.tvReportDate.setText(DATA.allReports.get(position).date);

		viewHolder.checkSelectReport.setChecked(DATA.allReports.get(position).isSelected());	


		DATA.loadImageFromURL(DATA.allReports.get(position).report_url, R.drawable.icon_drawer_reports, viewHolder.ivReport);
	
		return convertView;
	}

}
