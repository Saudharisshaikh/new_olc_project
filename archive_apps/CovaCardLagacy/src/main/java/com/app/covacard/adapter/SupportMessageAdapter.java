package com.app.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.covacard.R;
import com.app.covacard.model.SupportMessageBean;
import com.app.covacard.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class SupportMessageAdapter extends ArrayAdapter<SupportMessageBean> {

	Activity activity;
	ArrayList<SupportMessageBean> supportMessageBeans;
	SharedPreferences prefs;

	public SupportMessageAdapter(Activity activity , ArrayList<SupportMessageBean> supportMessageBeans) {
		super(activity, R.layout.lv_messages_row, supportMessageBeans);

		this.activity = activity;
		this.supportMessageBeans = supportMessageBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {
		RelativeLayout contMe;
		RelativeLayout contOther;
		TextView tvMsgTextMe,tvMsgTimeMe,tvMsgTextOther,tvMsgTimeOther;
		CircularImageView ivMsgOther,ivMsgMe;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_messages_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvMsgTextMe = (TextView) convertView.findViewById(R.id.tvMsgTextMe);
			viewHolder.tvMsgTimeMe = (TextView) convertView.findViewById(R.id.tvMsgTimeMe);
			viewHolder.tvMsgTextOther = (TextView) convertView.findViewById(R.id.tvMsgTextOther);
			viewHolder.tvMsgTimeOther = (TextView) convertView.findViewById(R.id.tvMsgTimeOther);
			viewHolder.ivMsgOther = (CircularImageView) convertView.findViewById(R.id.ivMsgOther);
			viewHolder.ivMsgMe = (CircularImageView) convertView.findViewById(R.id.ivMsgMe);
			
			viewHolder.contMe = (RelativeLayout) convertView.findViewById(R.id.contMe);
			viewHolder.contOther = (RelativeLayout) convertView.findViewById(R.id.contOther);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMsgTextMe, viewHolder.tvMsgTextMe);
			convertView.setTag(R.id.tvMsgTimeMe, viewHolder.tvMsgTimeMe);
			convertView.setTag(R.id.tvMsgTextOther, viewHolder.tvMsgTextOther);
			convertView.setTag(R.id.tvMsgTimeOther, viewHolder.tvMsgTimeOther);
			convertView.setTag(R.id.ivMsgOther, viewHolder.ivMsgOther);
			convertView.setTag(R.id.ivMsgMe, viewHolder.ivMsgMe);
			convertView.setTag(R.id.contMe, viewHolder.contMe);
			convertView.setTag(R.id.contOther, viewHolder.contOther);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		if (supportMessageBeans.get(position).type.equalsIgnoreCase("coordinator")) {
			viewHolder.contMe.setVisibility(View.GONE);
			viewHolder.contOther.setVisibility(View.VISIBLE);
			
			viewHolder.tvMsgTextOther.setText(supportMessageBeans.get(position).message);
			viewHolder.tvMsgTextOther.setTag(supportMessageBeans.get(position).message);
			
			//viewHolder.tvMsgTimeOther.setText(messageBeans.get(position).getDateof());
			//viewHolder.tvMsgTimeOther.setTag(messageBeans.get(position).getDateof());
			
			//UrlImageViewHelper.setUrlDrawable(viewHolder.ivMsgOther, supportMessageBeans.get(position).image, R.drawable.icon_call_screen);
			DATA.loadImageFromURL(supportMessageBeans.get(position).image, R.drawable.icon_call_screen,viewHolder.ivMsgOther);
		} else {
			viewHolder.contMe.setVisibility(View.VISIBLE);
			viewHolder.contOther.setVisibility(View.GONE);
			
			viewHolder.tvMsgTextMe.setText(supportMessageBeans.get(position).message);
			viewHolder.tvMsgTextMe.setTag(supportMessageBeans.get(position).message);
			
			//viewHolder.tvMsgTimeMe.setText(messageBeans.get(position).getDateof());
			//viewHolder.tvMsgTimeMe.setTag(messageBeans.get(position).getDateof());
			
			//UrlImageViewHelper.setUrlDrawable(viewHolder.ivMsgMe, prefs.getString("image", ""), R.drawable.doctor);

			DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, viewHolder.ivMsgMe);
		}
		
		return convertView;
	}

}
