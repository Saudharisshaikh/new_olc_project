package com.app.OnlineCareTDC_MA.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareTDC_MA.ActivityCovidAssignDocList;
import com.app.OnlineCareTDC_MA.R;
import com.app.OnlineCareTDC_MA.model.CovidAssignDocBean;
import com.app.OnlineCareTDC_MA.model.DoctorsModel;
import com.app.OnlineCareTDC_MA.util.DATA;
import com.app.OnlineCareTDC_MA.util.GloabalMethods;

import java.util.ArrayList;
import java.util.Locale;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class CovidAssignDocAdapter extends ArrayAdapter<CovidAssignDocBean> {

	Activity activity;
	ArrayList<CovidAssignDocBean> covidAssignDocBeans, covidAssignDocBeansOrig;

	public CovidAssignDocAdapter(Activity activity , ArrayList<CovidAssignDocBean> covidAssignDocBeans) {
		super(activity, R.layout.lv_covid_assign_doc_row, covidAssignDocBeans);
		this.activity = activity;
		this.covidAssignDocBeans = covidAssignDocBeans;

		covidAssignDocBeansOrig = new ArrayList<>();
		covidAssignDocBeansOrig.addAll(covidAssignDocBeans);
	}

	static class ViewHolder {
		ImageView ivDocImg,ivIsonline;
		TextView tvDocName, tvCall, tvMessage,tvAssignNow;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_covid_assign_doc_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivDocImg = convertView.findViewById(R.id.ivDocImg);
			viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);
			viewHolder.tvDocName = convertView.findViewById(R.id.tvDocName);
			viewHolder.tvCall = convertView.findViewById(R.id.tvCall);
			viewHolder.tvMessage = convertView.findViewById(R.id.tvMessage);
			viewHolder.tvAssignNow = convertView.findViewById(R.id.tvAssignNow);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivDocImg , viewHolder.ivDocImg);
			convertView.setTag(R.id.ivIsonline , viewHolder.ivIsonline);
			convertView.setTag(R.id.tvDocName , viewHolder.tvDocName);
			convertView.setTag(R.id.tvCall , viewHolder.tvCall);
			convertView.setTag(R.id.tvMessage , viewHolder.tvMessage);
			convertView.setTag(R.id.tvAssignNow , viewHolder.tvAssignNow);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDocName.setText(covidAssignDocBeans.get(position).first_name+ " "+covidAssignDocBeans.get(position).last_name);

		DATA.loadImageFromURL(covidAssignDocBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivDocImg);

		int drawableResId = covidAssignDocBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(drawableResId);

		viewHolder.tvCall.setEnabled(covidAssignDocBeans.get(position).is_online.equalsIgnoreCase("1"));

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvCall:
						DATA.selectedDrId = covidAssignDocBeans.get(position).id;
						//DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedUserCallId = ActivityCovidAssignDocList.covidFormListBean.patient_id;//careVistBeans.get(position).patient_id;
						DATA.selectedDrName = covidAssignDocBeans.get(position).first_name+" "+covidAssignDocBeans.get(position).last_name;
						DATA.selectedDrImage = covidAssignDocBeans.get(position).image;
						DATA.isFromDocToDoc = true;
						DATA.incomingCall = false;
						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = covidAssignDocBeans.get(position).current_app;

						Intent myIntent1 = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent1);
						break;
					case R.id.tvMessage:
						//DATA.selectedDrIdForNurse = careVistBeans.get(position).doctor_id;
						DATA.selectedDrId = covidAssignDocBeans.get(position).id;
						DATA.selectedDrName = covidAssignDocBeans.get(position).first_name+" "+covidAssignDocBeans.get(position).last_name;
						DATA.selectedDrImage = covidAssignDocBeans.get(position).image;
						DATA.isFromDocToDoc = true;

						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = covidAssignDocBeans.get(position).current_app;
						new GloabalMethods(activity).initMsgDialog();

						break;
					case R.id.tvAssignNow:
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
								.setMessage("Are you sure?  Do you want to assign covid care request by patient "+ ActivityCovidAssignDocList.covidFormListBean.patient_name+
										" to "+ covidAssignDocBeans.get(position).first_name+" "+covidAssignDocBeans.get(position).last_name+ "?")
								.setPositiveButton("Yes Assign", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										((ActivityCovidAssignDocList) activity).assignToProvider(position);
									}
								})
								.setNegativeButton("Not Now",null)
								.create().show();
						break;
				}
			}
		};
		viewHolder.tvCall.setOnClickListener(onClickListener);
		viewHolder.tvMessage.setOnClickListener(onClickListener);
		viewHolder.tvAssignNow.setOnClickListener(onClickListener);

		return convertView;
	}



	public void filter(String filterText) {
		covidAssignDocBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- covidAssignDocBeansOrig size: "+covidAssignDocBeansOrig.size());
		if(filterText.length() == 0) {
			covidAssignDocBeans.addAll(covidAssignDocBeansOrig);
		} else {
			for(CovidAssignDocBean temp : covidAssignDocBeansOrig) {
				if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					covidAssignDocBeans.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
