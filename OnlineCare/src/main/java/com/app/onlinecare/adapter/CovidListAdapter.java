package com.app.onlinecare.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare.R;
import com.app.onlinecare.model.CovidFormListBean;
import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.GloabalMethods;

import java.util.List;

public class CovidListAdapter extends ArrayAdapter<CovidFormListBean> {

	Activity activity;
	List<CovidFormListBean> covidFormListBeans;

	public CovidListAdapter(Activity activity , List<CovidFormListBean> covidFormListBeans) {
		super(activity, R.layout.lv_covid_list_row, covidFormListBeans);
		this.activity = activity;
		this.covidFormListBeans = covidFormListBeans;
	}

	static class ViewHolder {
		TextView tvPatientName, tvDate, tvViewForm,tvCareAct,tvResultStatus;
		ImageView ivResultStatus;
	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_covid_list_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvPatientName = convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvViewForm = convertView.findViewById(R.id.tvViewForm);
			viewHolder.tvCareAct = convertView.findViewById(R.id.tvCareAct);
			viewHolder.tvResultStatus = convertView.findViewById(R.id.tvResultStatus);
			viewHolder.ivResultStatus = convertView.findViewById(R.id.ivResultStatus);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvPatientName , viewHolder.tvPatientName);
			convertView.setTag(R.id.tvDate , viewHolder.tvDate);
			convertView.setTag(R.id.tvViewForm , viewHolder.tvViewForm);
			convertView.setTag(R.id.tvCareAct , viewHolder.tvCareAct);
			convertView.setTag(R.id.tvResultStatus , viewHolder.tvResultStatus);
			convertView.setTag(R.id.ivResultStatus , viewHolder.ivResultStatus);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPatientName.setText(covidFormListBeans.get(position).patient_name);
		viewHolder.tvDate.setText(covidFormListBeans.get(position).dateof);

		if(TextUtils.isEmpty(covidFormListBeans.get(position).result)){
			viewHolder.tvResultStatus.setText("Processing");
			viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_processing);
			//viewHolder.tvSendResults.setEnabled(true);
		}else if(covidFormListBeans.get(position).result.equalsIgnoreCase("Positive")){
			viewHolder.tvResultStatus.setText(covidFormListBeans.get(position).result);
			viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_positvie);
			//viewHolder.tvSendResults.setEnabled(false);
		}else if(covidFormListBeans.get(position).result.equalsIgnoreCase("Negative")){
			viewHolder.tvResultStatus.setText(covidFormListBeans.get(position).result);
			viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_negatvie);
			//viewHolder.tvSendResults.setEnabled(false);
		}

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvViewForm:
						new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view/"+covidFormListBeans.get(position).id+"?platform=mobile" , "Covid Testing");
						break;
					case R.id.tvCareAct:
						new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view_careact/"+covidFormListBeans.get(position).id+"?platform=mobile" , "COVID Data Elements Reporting Sheet (CARES ACT)");
						break;
				}
			}
		};
		viewHolder.tvViewForm.setOnClickListener(onClickListener);
		viewHolder.tvCareAct.setOnClickListener(onClickListener);

		return convertView;
	}

}
