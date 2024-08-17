package com.app.mhcsn_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_ma.ActivityCareCentersList;
import com.app.mhcsn_ma.ActivityTcmDetails;
import com.app.mhcsn_ma.R;
import com.app.mhcsn_ma.model.CareRequestBean;
import com.app.mhcsn_ma.util.DATA;
import com.app.mhcsn_ma.util.GloabalMethods;

import java.util.ArrayList;
import java.util.Locale;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class CareRequestAdapter extends ArrayAdapter<CareRequestBean> {

	Activity activity;
	ArrayList<CareRequestBean> careRequestBeans, careRequestBeansOrig;

	public CareRequestAdapter(Activity activity , ArrayList<CareRequestBean> careRequestBeans) {
		super(activity, R.layout.lv_care_request_row, careRequestBeans);
		this.activity = activity;
		this.careRequestBeans = careRequestBeans;

		careRequestBeansOrig = new ArrayList<>();
		careRequestBeansOrig.addAll(careRequestBeans);
	}

	static class ViewHolder {
		ImageView ivPatientImg,ivIsonline;
		TextView tvPatientName, tvCall, tvMessage,tvAssignToDoc;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_care_request_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivPatientImg = convertView.findViewById(R.id.ivPatientImg);
			viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);
			viewHolder.tvPatientName = convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvCall = convertView.findViewById(R.id.tvCall);
			viewHolder.tvMessage = convertView.findViewById(R.id.tvMessage);
			viewHolder.tvAssignToDoc = convertView.findViewById(R.id.tvAssignToDoc);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivPatientImg , viewHolder.ivPatientImg);
			convertView.setTag(R.id.ivIsonline , viewHolder.ivIsonline);
			convertView.setTag(R.id.tvPatientName , viewHolder.tvPatientName);
			convertView.setTag(R.id.tvCall , viewHolder.tvCall);
			convertView.setTag(R.id.tvMessage , viewHolder.tvMessage);
			convertView.setTag(R.id.tvAssignToDoc , viewHolder.tvAssignToDoc);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPatientName.setText(careRequestBeans.get(position).first_name+ " "+careRequestBeans.get(position).last_name);

		DATA.loadImageFromURL(careRequestBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatientImg);

		int drawableResId = careRequestBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(drawableResId);

		viewHolder.tvCall.setEnabled(careRequestBeans.get(position).is_online.equalsIgnoreCase("1"));

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvCall:
						DATA.selectedUserCallId = careRequestBeans.get(position).id;
						DATA.selectedUserCallName = careRequestBeans.get(position).first_name+" "+careRequestBeans.get(position).last_name;
						DATA.selectedUserCallSympTom = careRequestBeans.get(position).symptom_name;
						DATA.selectedUserCallCondition = careRequestBeans.get(position).condition_name;
						DATA.selectedUserCallDescription = careRequestBeans.get(position).description;
						DATA.selectedUserCallImage = careRequestBeans.get(position).image;
						DATA.isFromDocToDoc = false;
						DATA.incomingCall = false;
						ActivityTcmDetails.primary_patient_id = "";//careVistBeans.get(position).primary_patient_id;
						ActivityTcmDetails.family_is_online = "0";//"1";//not good but api limitation
						Intent myIntent = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent);
						break;

					case R.id.tvMessage:
						DATA.isFromDocToDoc = false;
						DATA.selectedUserCallId = careRequestBeans.get(position).id;
						DATA.selectedUserCallName = careRequestBeans.get(position).first_name+" "+careRequestBeans.get(position).last_name;
						DATA.selectedUserCallImage = careRequestBeans.get(position).image;

						ActivityTcmDetails.primary_patient_id = "";//careVistBeans.get(position).primary_patient_id;
						ActivityTcmDetails.family_is_online = "0";//"1";//not good but api limitation
						new GloabalMethods(activity).initMsgDialog();
						break;

					case R.id.tvAssignToDoc:
						ActivityCareCentersList.careRequestBean = careRequestBeans.get(position);
						activity.startActivity(new Intent(activity, ActivityCareCentersList.class));
						break;
				}
			}
		};
		viewHolder.tvCall.setOnClickListener(onClickListener);
		viewHolder.tvMessage.setOnClickListener(onClickListener);
		viewHolder.tvAssignToDoc.setOnClickListener(onClickListener);

		return convertView;
	}



	public void filter(String filterText) {
		careRequestBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- careRequestBeansOrig size: "+careRequestBeansOrig.size());
		if(filterText.length() == 0) {
			careRequestBeans.addAll(careRequestBeansOrig);
		} else {
			for(CareRequestBean temp : careRequestBeansOrig) {
				if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					careRequestBeans.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
