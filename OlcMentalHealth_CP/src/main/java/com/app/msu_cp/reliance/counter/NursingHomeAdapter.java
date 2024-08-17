package com.app.msu_cp.reliance.counter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.DATA;

import java.util.ArrayList;


public class NursingHomeAdapter extends ArrayAdapter<NursingHomeListBean> {

	Activity activity;
	ArrayList<NursingHomeListBean> nursingHomeListBeans;

	public NursingHomeAdapter(Activity activity, ArrayList<NursingHomeListBean> nursingHomeListBeans) {
		super(activity, R.layout.lv_nurshomelist_row, nursingHomeListBeans);

		this.activity = activity;
		this.nursingHomeListBeans = nursingHomeListBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nursingHomeListBeans.size();
	}
	static class ViewHolder {
		TextView tvNHDate,tvNHNameFacility,tvNHAdmDate,tvNHDischargeDate, tvNHDiagnosis,tvNHDischargeSummary,tvNHAdmissionSummary;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_nurshomelist_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNHDate = (TextView) convertView.findViewById(R.id.tvNHDate);
			viewHolder.tvNHNameFacility = (TextView) convertView.findViewById(R.id.tvNHNameFacility);
			viewHolder.tvNHAdmDate = (TextView) convertView.findViewById(R.id.tvNHAdmDate);
			viewHolder.tvNHDischargeDate = (TextView) convertView.findViewById(R.id.tvNHDischargeDate);
			viewHolder.tvNHDiagnosis = (TextView) convertView.findViewById(R.id.tvNHDiagnosis);
			viewHolder.tvNHDischargeSummary = (TextView) convertView.findViewById(R.id.tvNHDischargeSummary);
			viewHolder.tvNHAdmissionSummary = (TextView) convertView.findViewById(R.id.tvNHAdmissionSummary);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvNHDate, viewHolder.tvNHDate);
			convertView.setTag(R.id.tvNHNameFacility, viewHolder.tvNHNameFacility);
			convertView.setTag(R.id.tvNHAdmDate, viewHolder.tvNHAdmDate);
			convertView.setTag(R.id.tvNHDischargeDate, viewHolder.tvNHDischargeDate);
			convertView.setTag(R.id.tvNHDiagnosis, viewHolder.tvNHDiagnosis);
			convertView.setTag(R.id.tvNHDischargeSummary, viewHolder.tvNHDischargeSummary);
			convertView.setTag(R.id.tvNHAdmissionSummary, viewHolder.tvNHAdmissionSummary);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvNHDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Date : </font>"+ nursingHomeListBeans.get(position).date_reported));
		viewHolder.tvNHNameFacility.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Name of Facility : </font>"+ nursingHomeListBeans.get(position).name_of_facility));
		viewHolder.tvNHAdmDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Admission Date : </font>"+ nursingHomeListBeans.get(position).admit));
		viewHolder.tvNHDischargeDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Discharge Date : </font>"+ nursingHomeListBeans.get(position).discharge));
		viewHolder.tvNHDiagnosis.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Diagnosis : </font>"+ nursingHomeListBeans.get(position).diagnosis));
		viewHolder.tvNHDischargeSummary.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Discharge Summary : </font>"+ nursingHomeListBeans.get(position).discharge_summary));
		viewHolder.tvNHAdmissionSummary.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Admission Summary : </font>"+ nursingHomeListBeans.get(position).admission_summary));

		return convertView;
	}
}
