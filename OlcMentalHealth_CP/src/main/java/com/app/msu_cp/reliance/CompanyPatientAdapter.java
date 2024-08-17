package com.app.msu_cp.reliance;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class CompanyPatientAdapter extends ArrayAdapter<CompanyPatientBean> implements ApiCallBack {

	Activity activity;
	ArrayList<CompanyPatientBean> companyPatientBeans, companyPatientBeansOrig;
	CustomToast customToast;

	public CompanyPatientAdapter(Activity activity, ArrayList<CompanyPatientBean> companyPatientBeans) {
		super(activity, R.layout.lv_companypatient_row, companyPatientBeans);

		this.activity = activity;
		customToast = new CustomToast(activity);

		this.companyPatientBeans = companyPatientBeans;

		companyPatientBeansOrig = new ArrayList<>();
		companyPatientBeansOrig.addAll(companyPatientBeans);
	}

	static class ViewHolder {
		CircularImageView ivPatient;
		TextView tvPatientName,tvActive;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_companypatient_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvActive = (TextView) convertView.findViewById(R.id.tvActive);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvActive, viewHolder.tvActive);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(companyPatientBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatient);

		viewHolder.tvPatientName.setText(companyPatientBeans.get(position).first_name+" "+companyPatientBeans.get(position).last_name);

		String activeTxt = companyPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? "De Activate" : "Activate";
		//int resIDBtn = companyPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? R.drawable.btn_green : R.drawable.btn_grey;
		int resID = companyPatientBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.tvActive.setText(activeTxt);
		//viewHolder.tvActive.setBackgroundResource(resIDBtn);
		viewHolder.ivIsonline.setImageResource(resID);


		viewHolder.tvActive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeStatus(position);
			}
		});


		return convertView;
	}

	public void changeStatus(int pos){
		RequestParams params = new RequestParams();
		params.put("list_id", companyPatientBeans.get(pos).list_id);
		String active = companyPatientBeans.get(pos).is_active!=null && companyPatientBeans.get(pos).is_active.equalsIgnoreCase("1") ? "0" : "1";
		params.put("status", active);
		ApiManager apiManager = new ApiManager(ApiManager.CHANGE_PATIENT_STATUS,"post",params,this, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.CHANGE_PATIENT_STATUS)){
			//{"status":"success","reload":1}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					customToast.showToast("Patient status has been changed",0,1);

					if(activity instanceof ActivityCompany){
						((ActivityCompany) activity).loadPatientsByCompany(ActivityCompany.selectedCompanyID);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}

	public void filter(String filterText) {

		try {
			companyPatientBeans.clear();

			filterText = filterText.toLowerCase(Locale.getDefault());

			DATA.print("---doctorsModelsOrg size: "+companyPatientBeansOrig.size());

			if(filterText.length() == 0) {
				companyPatientBeans.addAll(companyPatientBeansOrig);
			}

			else {

				for(CompanyPatientBean temp :companyPatientBeansOrig) {

					if(temp.first_name.toLowerCase(Locale.getDefault()).startsWith(filterText) ||
							temp.last_name.toLowerCase(Locale.getDefault()).startsWith(filterText)) {

						companyPatientBeans.add(temp);
					}

				}
			}

		/*if (allPosts.size()==0) {
			DATA.noRecordingFound = true;
		} else {
			DATA.noRecordingFound = false;
		}*/
			notifyDataSetChanged();
		}catch (Exception e){
			e.printStackTrace();
		}

	}//end filter
	
}
