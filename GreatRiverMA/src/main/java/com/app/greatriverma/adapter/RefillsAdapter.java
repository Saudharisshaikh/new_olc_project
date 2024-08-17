package com.app.greatriverma.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.greatriverma.R;
import com.app.greatriverma.model.RefillBean;
import com.app.greatriverma.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class RefillsAdapter extends ArrayAdapter<RefillBean> {

	Activity activity;
	ArrayList<RefillBean> refillBeans;
	//SharedPreferences prefs;

	public RefillsAdapter(Activity activity , ArrayList<RefillBean> refillBeans) {
		super(activity, R.layout.lv_refills_row, refillBeans);

		this.activity = activity;
		this.refillBeans = refillBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {

		TextView tvRefillPatientName,tvRefillDrugName,tvRefillDate;
		CircularImageView ivRefillPatientImage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_refills_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvRefillPatientName = (TextView) convertView.findViewById(R.id.tvRefillPatientName);
			viewHolder.tvRefillDrugName = (TextView) convertView.findViewById(R.id.tvRefillDrugName);
			viewHolder.tvRefillDate = (TextView) convertView.findViewById(R.id.tvRefillDate);
			viewHolder.ivRefillPatientImage = (CircularImageView) convertView.findViewById(R.id.ivRefillPatientImage);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvRefillPatientName, viewHolder.tvRefillPatientName);
			convertView.setTag(R.id.tvRefillDrugName, viewHolder.tvRefillDrugName);
			convertView.setTag(R.id.tvRefillDate, viewHolder.tvRefillDate);
			convertView.setTag(R.id.ivRefillPatientImage, viewHolder.ivRefillPatientImage);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvRefillPatientName.setText(refillBeans.get(position).getFirst_name()+" "+refillBeans.get(position).getLast_name());
		viewHolder.tvRefillDrugName.setText(refillBeans.get(position).getDrug_name());
		viewHolder.tvRefillDate.setText(refillBeans.get(position).getDateof());

		DATA.loadImageFromURL(refillBeans.get(position).getImage(), R.drawable.icon_call_screen, viewHolder.ivRefillPatientImage);
		
		if (!DATA.refillIdInGCM.isEmpty()) {
			if (DATA.refillIdInGCM.equals(refillBeans.get(position).getId())) {
				convertView.setBackgroundColor(Color.parseColor("#E0E0E0"));
			} else {
				convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}
		}
		
		return convertView;
	}

}
