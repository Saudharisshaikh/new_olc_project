package com.app.mhcsn_spe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.model.AttachSurveyBean;

import java.util.ArrayList;

public class GvAttachedSurveyAdapter extends ArrayAdapter<AttachSurveyBean> {

	Activity activity;
	ArrayList<AttachSurveyBean> attachSurveyBeans;

	public GvAttachedSurveyAdapter(Activity activity, ArrayList<AttachSurveyBean> attachSurveyBeans) {
		super(activity, R.layout.gv_attach_survey_row, attachSurveyBeans);

		this.activity = activity;
		this.attachSurveyBeans = attachSurveyBeans;
	}

	static class ViewHolder {
		ImageView ivIcon;
	}

	@Override
	public int getCount() {
		return attachSurveyBeans.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.gv_attach_survey_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivIcon = convertView.findViewById(R.id.ivIcon);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivIcon, viewHolder.ivIcon);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.ivIcon.setImageResource(attachSurveyBeans.get(position).surveyIconID);


		return convertView;
	}
}
