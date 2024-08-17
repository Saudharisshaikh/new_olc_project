package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_cp.R;

import java.util.ArrayList;


public class DrawerAdapter extends ArrayAdapter<DrawerItemBean> {

	Activity activity;
	ArrayList<DrawerItemBean> itemBeans;
	
	public DrawerAdapter(Activity activity,ArrayList<DrawerItemBean> itemBeans) {
		super(activity, R.layout.lv_drawer_row);

		this.activity = activity;
		this.itemBeans = itemBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemBeans.size();
	}
	static class ViewHolder {
		TextView tvItemName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_drawer_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
			//viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvItemName, viewHolder.tvItemName);
			//convertView.setTag(R.id.iv_icon, viewHolder.iv_icon);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvItemName.setText(itemBeans.get(position).getItemName());
		//viewHolder.iv_icon.setImageResource(itemBeans.get(position).getIconId());

		return convertView;
	}
}
