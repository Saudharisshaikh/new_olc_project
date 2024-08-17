package com.app.mdlive_cp.reliance.mdlive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.reliance.CompanyPatientBean;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class CompanyPatientAdapter2 extends ArrayAdapter<CompanyPatientBean> {

	Activity activity;
	ArrayList<CompanyPatientBean> companyPatientBeans, companyPatientBeansOrig;
	CustomToast customToast;

	public CompanyPatientAdapter2(Activity activity, ArrayList<CompanyPatientBean> companyPatientBeans) {
		super(activity, R.layout.lv_companypatient_row2, companyPatientBeans);

		this.activity = activity;
		customToast = new CustomToast(activity);

		this.companyPatientBeans = companyPatientBeans;

		companyPatientBeansOrig = new ArrayList<>();
		companyPatientBeansOrig.addAll(companyPatientBeans);
	}

	static class ViewHolder {
		CircularImageView ivPatient;
		TextView tvPatientName,tvActive,tvPatientCompany;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_companypatient_row2, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvActive = (TextView) convertView.findViewById(R.id.tvActive);
			viewHolder.tvPatientCompany = (TextView) convertView.findViewById(R.id.tvPatientCompany);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvActive, viewHolder.tvActive);
			convertView.setTag(R.id.tvPatientCompany, viewHolder.tvPatientCompany);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(companyPatientBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatient);

		viewHolder.tvPatientName.setText(companyPatientBeans.get(position).first_name+" "+companyPatientBeans.get(position).last_name);

		viewHolder.tvPatientCompany.setText(companyPatientBeans.get(position).company_name);

		String activeTxt = companyPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? "De Activate" : "Activate";
		//int resIDBtn = companyPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? R.drawable.btn_green : R.drawable.btn_grey;
		int resID = companyPatientBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.tvActive.setText(activeTxt);
		//viewHolder.tvActive.setBackgroundResource(resIDBtn);
		viewHolder.ivIsonline.setImageResource(resID);


		/*viewHolder.tvActive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeStatus(position);
			}
		});*/


		return convertView;
	}


	public void filter(String filterText) {

		try {
			companyPatientBeans.clear();

			filterText = filterText.toLowerCase(Locale.getDefault());

			System.out.println("---doctorsModelsOrg size: "+companyPatientBeansOrig.size());

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
