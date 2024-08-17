package com.app.fivestaruc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fivestaruc.R;
import com.app.fivestaruc.model.CategoriesModel;
import com.app.fivestaruc.util.DATA;
import com.app.fivestaruc.util.SharedPrefsHelper;

public class CategoriesAdapter extends ArrayAdapter<CategoriesModel> {

	Activity activity;

	SharedPrefsHelper sharedPrefsHelper;

	public CategoriesAdapter(Activity activity) {
		super(activity, R.layout.categories_list_row, DATA.allCategories);

		this.activity = activity;
	}

	static class ViewHolder {

		TextView tvCatName;
		ImageView imgCatIcon;
		TextView prescriptionBadge;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		sharedPrefsHelper = SharedPrefsHelper.getInstance();

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.categories_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvCatName = (TextView) convertView.findViewById(R.id.tvCatName);
			viewHolder.imgCatIcon = (ImageView) convertView.findViewById(R.id.imgCatIcon);
			viewHolder.prescriptionBadge= (TextView)convertView.findViewById(R.id.tvPrescriptionBadge);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvCatName);
			convertView.setTag(R.id.imgCatIcon, viewHolder.imgCatIcon);
			convertView.setTag(R.id.tvPrescriptionBadge, viewHolder.prescriptionBadge);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvCatName.setText(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);

		if(DATA.allCategories.get(position).catName.equals("My Prescriptions")){
			int presBadgeCount = sharedPrefsHelper.get("pres_badge_count", 0);
			viewHolder.prescriptionBadge.setText(String.valueOf(presBadgeCount));
			int vis = presBadgeCount == 0 ? View.GONE : View.VISIBLE;
			viewHolder.prescriptionBadge.setVisibility(vis);
		}else {
			viewHolder.prescriptionBadge.setVisibility(View.GONE);
		}



		viewHolder.tvCatName.setTag(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);


		return convertView;
	}
}
