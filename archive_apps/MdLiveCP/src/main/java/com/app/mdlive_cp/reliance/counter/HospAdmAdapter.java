package com.app.mdlive_cp.reliance.counter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.util.DATA;

import java.util.ArrayList;


public class HospAdmAdapter extends ArrayAdapter<HospAdmListBean> {

	Activity activity;
	ArrayList<HospAdmListBean> hospAdmListBeans;

	public HospAdmAdapter(Activity activity, ArrayList<HospAdmListBean> hospAdmListBeans) {
		super(activity, R.layout.hosp_adm_row, hospAdmListBeans);

		this.activity = activity;
		this.hospAdmListBeans = hospAdmListBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hospAdmListBeans.size();
	}
	static class ViewHolder {
		TextView tvHospAdmDate,tvHospAdmAdmit,tvHospAdmDischarge,tvHospAdmHosp, tvHospAdmDiagnosis,tvHospAdmDescription,tvHospAdmDeschSumm;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.hosp_adm_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvHospAdmDate = (TextView) convertView.findViewById(R.id.tvHospAdmDate);
			viewHolder.tvHospAdmAdmit = (TextView) convertView.findViewById(R.id.tvHospAdmAdmit);
			viewHolder.tvHospAdmDischarge = (TextView) convertView.findViewById(R.id.tvHospAdmDischarge);
			viewHolder.tvHospAdmHosp = (TextView) convertView.findViewById(R.id.tvHospAdmHosp);
			viewHolder.tvHospAdmDiagnosis = (TextView) convertView.findViewById(R.id.tvHospAdmDiagnosis);
			viewHolder.tvHospAdmDescription = (TextView) convertView.findViewById(R.id.tvHospAdmDescription);
			viewHolder.tvHospAdmDeschSumm = (TextView) convertView.findViewById(R.id.tvHospAdmDeschSumm);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvHospAdmDate, viewHolder.tvHospAdmDate);
			convertView.setTag(R.id.tvHospAdmAdmit, viewHolder.tvHospAdmAdmit);
			convertView.setTag(R.id.tvHospAdmDischarge, viewHolder.tvHospAdmDischarge);
			convertView.setTag(R.id.tvHospAdmHosp, viewHolder.tvHospAdmHosp);
			convertView.setTag(R.id.tvHospAdmDiagnosis, viewHolder.tvHospAdmDiagnosis);
			convertView.setTag(R.id.tvHospAdmDescription, viewHolder.tvHospAdmDescription);
			convertView.setTag(R.id.tvHospAdmDeschSumm, viewHolder.tvHospAdmDeschSumm);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvHospAdmDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Report Date : </font>"+ hospAdmListBeans.get(position).dateof));
		viewHolder.tvHospAdmAdmit.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Admission Date : </font>"+ hospAdmListBeans.get(position).admit));
		viewHolder.tvHospAdmDischarge.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Discharge Date : </font>"+ hospAdmListBeans.get(position).discharge));
		viewHolder.tvHospAdmHosp.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Hospital : </font>"+ hospAdmListBeans.get(position).hospital));
		viewHolder.tvHospAdmDiagnosis.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Diagnosis : </font>"+ hospAdmListBeans.get(position).diagnosis));
		viewHolder.tvHospAdmDescription.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Admission Description : </font>"+ hospAdmListBeans.get(position).description));
		viewHolder.tvHospAdmDeschSumm.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Discharge Summary : </font>"+ hospAdmListBeans.get(position).discharge_summary));

		return convertView;
	}
}
