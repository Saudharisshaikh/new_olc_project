package com.app.onlinecare.devices.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare.R;
import com.app.onlinecare.devices.beanclasses.DeviceBean;
import com.app.onlinecare.util.DATA;

import java.util.ArrayList;

public class MyDevicesAdapter1 extends ArrayAdapter<DeviceBean> {

	Activity activity;
	ArrayList<DeviceBean> deviceBeans;
	
	public MyDevicesAdapter1(Activity activity, ArrayList<DeviceBean> deviceBeans) {
		super(activity, R.layout.item_my_devices);

		this.activity = activity;
		this.deviceBeans = deviceBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return deviceBeans.size();
	}
	static class ViewHolder {

		TextView tvDeviceName;
		ImageView imgDeviceThumb;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.item_my_devices, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvDeviceName= (TextView) convertView.findViewById(R.id.tvDeviceName);
			viewHolder.imgDeviceThumb = (ImageView) convertView.findViewById(R.id.imgDeviceThumb);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDeviceName, viewHolder.tvDeviceName);
			convertView.setTag(R.id.imgDeviceThumb, viewHolder.imgDeviceThumb);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvDeviceName.setText(deviceBeans.get(position).getDeviceName());
		/*Glide.with(activity).load(deviceBeans.get(position).getDeviceImageUrl())
        .placeholder(R.drawable.place_holder)
        .crossFade()
        .fitCenter()
        .into(viewHolder.imgDeviceThumb);*/

		DATA.loadImageFromURL(deviceBeans.get(position).getDeviceImageUrl(), R.drawable.place_holder, viewHolder.imgDeviceThumb);

		viewHolder.tvDeviceName.setTag(deviceBeans.get(position).getDeviceName());
		
		


		return convertView;
	}
}
