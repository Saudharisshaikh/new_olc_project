package com.app.msu_cp.adapters;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.model.FollowupBean;
import com.app.msu_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class FollowupAdapter2 extends ArrayAdapter<FollowupBean> {

	AppCompatActivity activity;
	ArrayList<FollowupBean> followupBeens;

	public FollowupAdapter2(AppCompatActivity activity, ArrayList<FollowupBean> followupBeens) {
		super(activity, R.layout.followup_row_2, followupBeens);

		this.activity = activity;
		this.followupBeens = followupBeens;
	}

	static class ViewHolder {
		CircularImageView ivPetient;
		TextView tvPatientName,tvSchedule,tvPatientCat;//tvFollowupDate
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.followup_row_2, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivPetient = (CircularImageView) convertView.findViewById(R.id.ivPetient);
			viewHolder.tvPatientName = (TextView) convertView.findViewById(R.id.tvPatientName);
			viewHolder.tvSchedule = (TextView) convertView.findViewById(R.id.tvSchedule);
			viewHolder.tvPatientCat = (TextView) convertView.findViewById(R.id.tvPatientCat);
			//viewHolder.tvFollowupDate = (TextView) convertView.findViewById(R.id.tvFollowupDate);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivPetient, viewHolder.ivPetient);
			convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
			convertView.setTag(R.id.tvSchedule, viewHolder.tvSchedule);
			convertView.setTag(R.id.tvPatientCat, viewHolder.tvPatientCat);
			//convertView.setTag(R.id.tvFollowupDate, viewHolder.tvFollowupDate);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvPatientName.setText(followupBeens.get(position).first_name+" "+
				followupBeens.get(position).last_name);
		viewHolder.tvPatientCat.setText(followupBeens.get(position).patient_category);
		//viewHolder.tvFollowupDate.setText(followupBeens.get(position).dateof);


		DATA.loadImageFromURL(followupBeens.get(position).image , R.drawable.icon_call_screen , viewHolder.ivPetient);

		/*if (followupBeens.get(position).followup_id.isEmpty()) {
			viewHolder.tvSchedule.setText("Schedule");
		}else {
			viewHolder.tvSchedule.setText("Re-Schedule");
		}*/

		return convertView;
	}
}
