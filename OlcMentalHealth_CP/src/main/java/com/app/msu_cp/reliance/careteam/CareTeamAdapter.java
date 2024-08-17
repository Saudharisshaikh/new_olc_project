package com.app.msu_cp.reliance.careteam;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class CareTeamAdapter extends ArrayAdapter<CareTeamBean> {

	Activity activity;
	ArrayList<CareTeamBean> careTeamBeans;
	//SharedPreferences prefs;

	public CareTeamAdapter(Activity activity , ArrayList<CareTeamBean> careTeamBeans) {
		super(activity, R.layout.lv_careteam_row, careTeamBeans);

		this.activity = activity;
		this.careTeamBeans = careTeamBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvNurseName,tvNurseType,tvAssign;
		CircularImageView ivNurse;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_careteam_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNurseName = (TextView) convertView.findViewById(R.id.tvNurseName);
			viewHolder.tvNurseType = (TextView) convertView.findViewById(R.id.tvNurseType);
			viewHolder.tvAssign = (TextView) convertView.findViewById(R.id.tvAssign);
			viewHolder.ivNurse = (CircularImageView) convertView.findViewById(R.id.ivNurse);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNurseName, viewHolder.tvNurseName);
			convertView.setTag(R.id.tvNurseType, viewHolder.tvNurseType);
			convertView.setTag(R.id.tvAssign, viewHolder.tvAssign);
			convertView.setTag(R.id.ivNurse, viewHolder.ivNurse);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvNurseName.setText(careTeamBeans.get(position).first_name+" "+careTeamBeans.get(position).last_name);
		viewHolder.tvNurseType.setText(careTeamBeans.get(position).doctor_category);




		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivConv, conversationBeans.get(position).getImage(), R.drawable.ic_launcher);
		DATA.loadImageFromURL(careTeamBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivNurse);

		if (careTeamBeans.get(position).is_added.equals("1")){
			viewHolder.tvAssign.setText("Remove Patient");
			//viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_selector);
		}else if (careTeamBeans.get(position).is_added.equals("0")){
			viewHolder.tvAssign.setText("Assign Patient");
			/*if (careTeamBeans.get(position).is_online.equals("1")) {
				viewHolder.tvAssign.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			}else {
				viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_drawable_grey);
			}*/
		}
		/*else if (nurseBeens.get(position).is_added.equals("group_call")) {
			if (nurseBeens.get(position).is_online.equals("1")) {
				viewHolder.tvAssign.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
				viewHolder.tvAssign.setText("Join To Video Session");
			}else {
				viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_drawable_grey);
				viewHolder.tvAssign.setText("Offline");
			}
		}*/

		return convertView;
	}

}
