package com.app.msu_cp.reliance.mdlive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class CompanyDoctorAdapter extends ArrayAdapter<CompanyDoctorBean> {

	Activity activity;
	ArrayList<CompanyDoctorBean> companyDoctorBeans, companyDoctorBeansOrig;
	//CustomToast customToast;

	public CompanyDoctorAdapter(Activity activity, ArrayList<CompanyDoctorBean> companyDoctorBeans) {
		super(activity, R.layout.lv_companydoctor_row, companyDoctorBeans);

		this.activity = activity;
		//customToast = new CustomToast(activity);

		this.companyDoctorBeans = companyDoctorBeans;

		companyDoctorBeansOrig = new ArrayList<>();
		companyDoctorBeansOrig.addAll(companyDoctorBeans);
	}

	static class ViewHolder {
		CircularImageView ivPatient;
		TextView tvPatientName;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_companydoctor_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(companyDoctorBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatient);

		viewHolder.tvPatientName.setText(companyDoctorBeans.get(position).first_name+" "+companyDoctorBeans.get(position).last_name);

		int resID = companyDoctorBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(resID);


		return convertView;
	}



	public void filter(String filterText) {

		try {
			companyDoctorBeans.clear();

			filterText = filterText.toLowerCase(Locale.getDefault());

			DATA.print("---doctorsModelsOrg size: "+companyDoctorBeansOrig.size());

			if(filterText.length() == 0) {
				companyDoctorBeans.addAll(companyDoctorBeansOrig);
			}

			else {

				for(CompanyDoctorBean temp :companyDoctorBeansOrig) {

					if(temp.first_name.toLowerCase(Locale.getDefault()).startsWith(filterText) ||
							temp.last_name.toLowerCase(Locale.getDefault()).startsWith(filterText)) {

						companyDoctorBeans.add(temp);
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
