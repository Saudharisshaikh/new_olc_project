package com.app.msu_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.model.CategoriesModel;
import com.app.msu_cp.util.DATA;

public class CategoriesAdapter extends ArrayAdapter<CategoriesModel> {

	Activity activity;

	public CategoriesAdapter(Activity activity) {
		super(activity, R.layout.categories_list_row, DATA.allCategories);

		this.activity = activity;
	}

	static class ViewHolder {

		TextView tvCatName;
		ImageView imgCatIcon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.categories_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvCatName = (TextView) convertView.findViewById(R.id.tvCatName);
			viewHolder.imgCatIcon = (ImageView) convertView.findViewById(R.id.imgCatIcon);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvCatName);
			convertView.setTag(R.id.imgCatIcon, viewHolder.imgCatIcon);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvCatName.setText(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);
		

		viewHolder.tvCatName.setTag(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);


		return convertView;
	}
}
