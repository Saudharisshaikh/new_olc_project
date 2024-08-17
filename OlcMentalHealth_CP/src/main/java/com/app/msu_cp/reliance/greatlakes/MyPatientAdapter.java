package com.app.msu_cp.reliance.greatlakes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class MyPatientAdapter extends ArrayAdapter<MyPatientBean>{

	Activity activity;
	ArrayList<MyPatientBean> myPatientBeans, myPatientBeansOrig;
	CustomToast customToast;

	public MyPatientAdapter(Activity activity, ArrayList<MyPatientBean> myPatientBeans) {
		super(activity, R.layout.lv_mypatient_row, myPatientBeans);

		this.activity = activity;
		customToast = new CustomToast(activity);

		this.myPatientBeans = myPatientBeans;

		myPatientBeansOrig = new ArrayList<>();
		myPatientBeansOrig.addAll(myPatientBeans);
	}

	static class ViewHolder {
		CircularImageView ivPatient;
		TextView tvPatientName;      //,tvActive;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_mypatient_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			//viewHolder.tvActive = (TextView) convertView.findViewById(R.id.tvActive);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			//convertView.setTag(R.id.tvActive, viewHolder.tvActive);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(myPatientBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatient);

		viewHolder.tvPatientName.setText(myPatientBeans.get(position).first_name+" "+myPatientBeans.get(position).last_name);

		//String activeTxt = myPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? "De Activate" : "Activate";
		//int resIDBtn = myPatientBeans.get(position).is_active.equalsIgnoreCase("1") ? R.drawable.btn_green : R.drawable.btn_grey;
		int resID = myPatientBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		//viewHolder.tvActive.setText(activeTxt);
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
			myPatientBeans.clear();
			filterText = filterText.toLowerCase(Locale.getDefault());
			DATA.print("---doctorsModelsOrg size: "+myPatientBeansOrig.size());
			if(filterText.length() == 0) {
				myPatientBeans.addAll(myPatientBeansOrig);
			} else {
				for(MyPatientBean temp : myPatientBeansOrig) {
					if(temp.first_name.toLowerCase(Locale.getDefault()).startsWith(filterText) ||
							temp.last_name.toLowerCase(Locale.getDefault()).startsWith(filterText)) {
						myPatientBeans.add(temp);
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
