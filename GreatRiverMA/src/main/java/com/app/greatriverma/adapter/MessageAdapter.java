package com.app.greatriverma.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.greatriverma.R;
import com.app.greatriverma.model.MessageBean;
import com.app.greatriverma.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<MessageBean> {

	Activity activity;
	ArrayList<MessageBean> messageBeans;
	SharedPreferences prefs;

	public MessageAdapter(Activity activity , ArrayList<MessageBean> messageBeans) {
		super(activity, R.layout.lv_messages_row, messageBeans);

		this.activity = activity;
		this.messageBeans = messageBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {
		RelativeLayout contMe;
		RelativeLayout contOther;
		TextView tvMsgTextMe,tvMsgTimeMe,tvMsgTextOther,tvMsgTimeOther;
		CircularImageView ivMsgOther,ivMsgMe;
		ImageView ivMsgImgMe,ivMsgImgOther;
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

			viewHolder.ivMsgImgMe = (ImageView) convertView.findViewById(R.id.ivMsgImgMe);
			viewHolder.ivMsgImgOther = (ImageView) convertView.findViewById(R.id.ivMsgImgOther);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMsgTextMe, viewHolder.tvMsgTextMe);
			convertView.setTag(R.id.tvMsgTimeMe, viewHolder.tvMsgTimeMe);
			convertView.setTag(R.id.tvMsgTextOther, viewHolder.tvMsgTextOther);
			convertView.setTag(R.id.tvMsgTimeOther, viewHolder.tvMsgTimeOther);
			convertView.setTag(R.id.ivMsgOther, viewHolder.ivMsgOther);
			convertView.setTag(R.id.ivMsgMe, viewHolder.ivMsgMe);
			convertView.setTag(R.id.contMe, viewHolder.contMe);
			convertView.setTag(R.id.contOther, viewHolder.contOther);
			convertView.setTag(R.id.ivMsgImgMe, viewHolder.ivMsgImgMe);
			convertView.setTag(R.id.ivMsgImgOther, viewHolder.ivMsgImgOther);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		if (messageBeans.get(position).getFrom().equalsIgnoreCase("patient")) {
			viewHolder.contMe.setVisibility(View.GONE);
			viewHolder.contOther.setVisibility(View.VISIBLE);
			
			viewHolder.tvMsgTextOther.setText(messageBeans.get(position).getMessage_text());
			viewHolder.tvMsgTextOther.setTag(messageBeans.get(position).getMessage_text());
			
			viewHolder.tvMsgTimeOther.setText(messageBeans.get(position).getDateof());
			viewHolder.tvMsgTimeOther.setTag(messageBeans.get(position).getDateof());

			DATA.loadImageFromURL(messageBeans.get(position).getImage(),R.drawable.icon_call_screen,viewHolder.ivMsgOther);

			if(messageBeans.get(position).msg_type.equalsIgnoreCase("file")){
				viewHolder.ivMsgImgOther.setVisibility(View.VISIBLE);
				if(messageBeans.get(position).getMessage_text().isEmpty()){
					viewHolder.tvMsgTextOther.setVisibility(View.GONE);
				}else {
					viewHolder.tvMsgTextOther.setVisibility(View.VISIBLE);
				}
				DATA.loadImageFromURL(messageBeans.get(position).files, R.drawable.ic_placeholder_2, viewHolder.ivMsgImgOther);
			}else {
				viewHolder.ivMsgImgOther.setVisibility(View.GONE);
				viewHolder.tvMsgTextOther.setVisibility(View.VISIBLE);
			}
		} else {
			viewHolder.contMe.setVisibility(View.VISIBLE);
			viewHolder.contOther.setVisibility(View.GONE);
			
			viewHolder.tvMsgTextMe.setText(messageBeans.get(position).getMessage_text());
			viewHolder.tvMsgTextMe.setTag(messageBeans.get(position).getMessage_text());
			
			viewHolder.tvMsgTimeMe.setText(messageBeans.get(position).getDateof());
			viewHolder.tvMsgTimeMe.setTag(messageBeans.get(position).getDateof());

			DATA.loadImageFromURL(prefs.getString("image", ""),R.drawable.icon_call_screen,viewHolder.ivMsgMe);

			if(messageBeans.get(position).msg_type.equalsIgnoreCase("file")){
				viewHolder.ivMsgImgMe.setVisibility(View.VISIBLE);
				if(messageBeans.get(position).getMessage_text().isEmpty()){
					viewHolder.tvMsgTextMe.setVisibility(View.GONE);
				}else {
					viewHolder.tvMsgTextMe.setVisibility(View.VISIBLE);
				}
				DATA.loadImageFromURL(messageBeans.get(position).files, R.drawable.ic_placeholder_2, viewHolder.ivMsgImgMe);
			}else {
				viewHolder.ivMsgImgMe.setVisibility(View.GONE);
				viewHolder.tvMsgTextMe.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}

}
