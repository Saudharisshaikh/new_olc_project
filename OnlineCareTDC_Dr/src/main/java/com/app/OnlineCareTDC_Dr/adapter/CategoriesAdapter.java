package com.app.OnlineCareTDC_Dr.adapter;

import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.CategoriesModel;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoriesAdapter extends ArrayAdapter<CategoriesModel> {

	Activity activity;
	
	CircularImageView ivDrawerProfile;
	TextView tvDrawerName,tvDrawerEmail,tvDrawerPhone;
	SharedPreferences prefs;

	public CategoriesAdapter(Activity activity) {
		super(activity, R.layout.categories_list_row, DATA.allCategories);

		this.activity = activity;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {

		TextView tvCatName;
		ImageView imgCatIcon;
		LinearLayout layDp,layItem;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return DATA.allCategories.size()+1;
		return DATA.allCategories.size();
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
			viewHolder.layDp = (LinearLayout) convertView.findViewById(R.id.layDp);
			viewHolder.layItem = (LinearLayout) convertView.findViewById(R.id.layItem);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvCatName);
			convertView.setTag(R.id.imgCatIcon, viewHolder.imgCatIcon);
			convertView.setTag(R.id.layDp, viewHolder.layDp);
			convertView.setTag(R.id.layItem, viewHolder.layItem);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*viewHolder.tvCatName.setText(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);
		

		viewHolder.tvCatName.setTag(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);*/
		
		/*if (position == 0) {
			viewHolder.layDp.setVisibility(View.VISIBLE);
			viewHolder.layItem.setVisibility(View.GONE);
			
			ivDrawerProfile = (CircularImageView) convertView.findViewById(R.id.ivDrawerProfile);
			tvDrawerName = (TextView) convertView.findViewById(R.id.tvDrawerName);
			tvDrawerEmail = (TextView) convertView.findViewById(R.id.tvDrawerEmail);
			tvDrawerPhone = (TextView) convertView.findViewById(R.id.tvDrawerPhone);
			
			UrlImageViewHelper.setUrlDrawable(ivDrawerProfile, prefs.getString("image", ""), R.drawable.icon_call_screen);
			tvDrawerName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
			tvDrawerEmail.setText(prefs.getString("email", ""));
			if (prefs.getString("mobile", "").isEmpty() || prefs.getString("mobile", "").equalsIgnoreCase("null")) {
				tvDrawerPhone.setVisibility(View.GONE);
			} else {
				tvDrawerPhone.setVisibility(View.VISIBLE);
				tvDrawerPhone.setText("Phone: "+prefs.getString("mobile", ""));
			}
		} else {
			viewHolder.layDp.setVisibility(View.GONE);
			viewHolder.layItem.setVisibility(View.VISIBLE);
			
			viewHolder.tvCatName.setText(DATA.allCategories.get(position-1).catName);
			viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position-1).catIcon);
			

			viewHolder.tvCatName.setTag(DATA.allCategories.get(position-1).catName);
			viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position-1).catIcon);
		}*/



		viewHolder.layDp.setVisibility(View.GONE);
		viewHolder.layItem.setVisibility(View.VISIBLE);

		viewHolder.tvCatName.setText(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);

		viewHolder.tvCatName.setTag(DATA.allCategories.get(position).catName);
		viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);




		return convertView;
	}
}
