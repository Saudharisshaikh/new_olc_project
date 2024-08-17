package com.app.omranpatient.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.omranpatient.ActivityCovidFormList;
import com.app.omranpatient.R;
import com.app.omranpatient.model.CovidFormListBean;
import com.app.omranpatient.util.DATA;
import com.app.omranpatient.util.GloabalMethods;

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
		//ImageView ivResultStatus;
		TextView tvSwabSendOut,tvInHouseSwab,tvStrepTest,tvRapidFlu,tvDnaFlu,tvCulture;
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
			//viewHolder.ivResultStatus = convertView.findViewById(R.id.ivResultStatus);
			viewHolder.tvSwabSendOut = convertView.findViewById(R.id.tvSwabSendOut);
			viewHolder.tvInHouseSwab = convertView.findViewById(R.id.tvInHouseSwab);
			viewHolder.tvStrepTest = convertView.findViewById(R.id.tvStrepTest);
			viewHolder.tvRapidFlu = convertView.findViewById(R.id.tvRapidFlu);
			viewHolder.tvDnaFlu = convertView.findViewById(R.id.tvDnaFlu);
			viewHolder.tvCulture = convertView.findViewById(R.id.tvCulture);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvPatientName , viewHolder.tvPatientName);
			convertView.setTag(R.id.tvDate , viewHolder.tvDate);
			convertView.setTag(R.id.tvViewForm , viewHolder.tvViewForm);
			convertView.setTag(R.id.tvCareAct , viewHolder.tvCareAct);
			convertView.setTag(R.id.tvResultStatus , viewHolder.tvResultStatus);
			//convertView.setTag(R.id.ivResultStatus , viewHolder.ivResultStatus);
			convertView.setTag(R.id.tvSwabSendOut , viewHolder.tvSwabSendOut);
			convertView.setTag(R.id.tvInHouseSwab , viewHolder.tvInHouseSwab);
			convertView.setTag(R.id.tvStrepTest , viewHolder.tvStrepTest);
			convertView.setTag(R.id.tvRapidFlu , viewHolder.tvRapidFlu);
			convertView.setTag(R.id.tvDnaFlu , viewHolder.tvDnaFlu);
			convertView.setTag(R.id.tvCulture , viewHolder.tvCulture);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPatientName.setText(covidFormListBeans.get(position).patient_name);
		viewHolder.tvDate.setText(covidFormListBeans.get(position).dateof);

		/*if(TextUtils.isEmpty(covidFormListBeans.get(position).result)){
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
		}*/


		viewHolder.tvSwabSendOut.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).swab_send_out.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvInHouseSwab.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).in_house_swab.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvStrepTest.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).strep_test.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvRapidFlu.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).rapid_flu.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvDnaFlu.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).dna_flu.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvCulture.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).culture.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
		viewHolder.tvCulture.setText(covidFormListBeans.get(position).culture.equalsIgnoreCase("1") ? "Strep Culture : Yes" : "Strep Culture : No");

		if(TextUtils.isEmpty(covidFormListBeans.get(position).rapid_covid_result) && TextUtils.isEmpty(covidFormListBeans.get(position).pcr_covid_result) &&
				TextUtils.isEmpty(covidFormListBeans.get(position).rapid_strep_result) && TextUtils.isEmpty(covidFormListBeans.get(position).strep_culture_result) &&
				TextUtils.isEmpty(covidFormListBeans.get(position).rapid_flu_result)   && TextUtils.isEmpty(covidFormListBeans.get(position).dna_flu_result)){
			viewHolder.tvResultStatus.setEnabled(false);
		}else {
			viewHolder.tvResultStatus.setEnabled(true);
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
					case R.id.tvResultStatus:
						if(activity instanceof ActivityCovidFormList){
							((ActivityCovidFormList) activity).showCovidViewDialog(position);
						}
						break;
				}
			}
		};
		viewHolder.tvViewForm.setOnClickListener(onClickListener);
		viewHolder.tvCareAct.setOnClickListener(onClickListener);
		viewHolder.tvResultStatus.setOnClickListener(onClickListener);

		return convertView;
	}

}
