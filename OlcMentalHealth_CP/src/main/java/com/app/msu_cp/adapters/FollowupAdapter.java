package com.app.msu_cp.adapters;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.model.FollowupBean;
import com.app.msu_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class FollowupAdapter extends ArrayAdapter<FollowupBean> {

	AppCompatActivity activity;
	ArrayList<FollowupBean> followupBeens, followupBeansOrig;

	public FollowupAdapter(AppCompatActivity activity, ArrayList<FollowupBean> followupBeens) {
		super(activity, R.layout.followup_row, followupBeens);

		this.activity = activity;
		this.followupBeens = followupBeens;

		followupBeansOrig = new ArrayList<>();
		followupBeansOrig.addAll(followupBeens);
	}

	static class ViewHolder {
		CircularImageView ivPetient;
		TextView tvPatientName,tvSchedule,tvPatientCat,tvFollowupDate;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.followup_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivPetient = (CircularImageView) convertView.findViewById(R.id.ivPetient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvSchedule = (TextView) convertView.findViewById(R.id.tvSchedule);
			viewHolder.tvPatientCat = (TextView) convertView.findViewById(R.id.tvPatientCat);
			viewHolder.tvFollowupDate = (TextView) convertView.findViewById(R.id.tvFollowupDate);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPetient, viewHolder.ivPetient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvSchedule, viewHolder.tvSchedule);
			convertView.setTag(R.id.tvPatientCat, viewHolder.tvPatientCat);
			convertView.setTag(R.id.tvFollowupDate, viewHolder.tvFollowupDate);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPatientName.setText(followupBeens.get(position).first_name+" "+
				followupBeens.get(position).last_name);
		//viewHolder.tvPatientCat.setText(followupBeens.get(position).patient_category);
		String patientCat = "";
		if(followupBeens.get(position).patient_category.equalsIgnoreCase("tcm")){
			patientCat = "TCM";
		}else if(followupBeens.get(position).patient_category.equalsIgnoreCase("complex_care")){
			patientCat = "Complex Care";
		}else if(followupBeens.get(position).patient_category.equalsIgnoreCase("home_health")){
			patientCat = "Home Health";
		}else if(followupBeens.get(position).patient_category.equalsIgnoreCase("nursing_home")){
			patientCat = "Nursing Home";
		}
		viewHolder.tvPatientCat.setText(patientCat);

		viewHolder.tvFollowupDate.setText(followupBeens.get(position).dateof);

		DATA.loadImageFromURL(followupBeens.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPetient);

		if (followupBeens.get(position).followup_id.isEmpty()) {
			viewHolder.tvSchedule.setText("Schedule");
		}else {
			viewHolder.tvSchedule.setText("Re-Schedule");
		}

		if(followupBeens.get(position).is_online.equals("1")){
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}

		return convertView;
	}


	public void filter(String filterText) {
		followupBeens.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- doctorBeansOrig size: "+followupBeansOrig.size());
		if(filterText.length() == 0) {
			followupBeens.addAll(followupBeansOrig);
		} else {
			for(FollowupBean temp : followupBeansOrig) {
				if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					followupBeens.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter
}
