package com.app.covacard.adapter;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.covacard.R;
import com.app.covacard.model.ReportsModel;
import com.app.covacard.util.DATA;

public class ReportsAdapter extends ArrayAdapter<ReportsModel> {

	AppCompatActivity activity;

	public ReportsAdapter(AppCompatActivity activity) {
		super(activity, R.layout.reports_row, DATA.allReportsFiltered);

		this.activity = activity;
	}

	static class ViewHolder {

		TextView tvReportsDate,tvReportsName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.reports_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvReportsDate = (TextView) convertView.findViewById(R.id.tvReportsDate);
			viewHolder.tvReportsName = (TextView) convertView.findViewById(R.id.tvReportsName);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvReportsDate, viewHolder.tvReportsDate);
			convertView.setTag(R.id.tvReportsName, viewHolder.tvReportsName);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvReportsDate.setText(DATA.allReportsFiltered.get(position).date);
		viewHolder.tvReportsDate.setTag(DATA.allReportsFiltered.get(position).date);

		viewHolder.tvReportsName.setText(DATA.allReportsFiltered.get(position).name);
		viewHolder.tvReportsName.setTag(DATA.allReportsFiltered.get(position).name);

		return convertView;
	}
}
