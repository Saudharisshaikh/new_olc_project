package com.app.Olc_MentalHealth.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.Olc_MentalHealth.R;
import com.app.Olc_MentalHealth.model.GroupMessageBean;
import com.app.Olc_MentalHealth.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;


public class GroupMessageAdapter extends ArrayAdapter<GroupMessageBean> {

	Activity activity;
	ArrayList<GroupMessageBean> groupMessageBeens;
	SharedPreferences prefs;

	public GroupMessageAdapter(Activity activity , ArrayList<GroupMessageBean> groupMessageBeens) {
		super(activity, R.layout.lv_messages_row, groupMessageBeens);

		this.activity = activity;
		this.groupMessageBeens = groupMessageBeens;
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


		if (groupMessageBeens.get(position).user_type.equalsIgnoreCase("patient") &&
				groupMessageBeens.get(position).user_id.equals(prefs.getString("id",""))) {
			viewHolder.contMe.setVisibility(View.VISIBLE);
			viewHolder.contOther.setVisibility(View.GONE);

			viewHolder.tvMsgTextMe.setText(groupMessageBeens.get(position).message_text);
			viewHolder.tvMsgTextMe.setTag(groupMessageBeens.get(position).message_text);

			viewHolder.tvMsgTimeMe.setText(groupMessageBeens.get(position).dateof);
			viewHolder.tvMsgTimeMe.setTag(groupMessageBeens.get(position).dateof);

			DATA.loadImageFromURL(groupMessageBeens.get(position).image, R.drawable.icon_call_screen, viewHolder.ivMsgMe);

		} else {
			viewHolder.contMe.setVisibility(View.GONE);
			viewHolder.contOther.setVisibility(View.VISIBLE);

			viewHolder.tvMsgTextOther.setText(groupMessageBeens.get(position).message_text);
			viewHolder.tvMsgTextOther.setTag(groupMessageBeens.get(position).message_text);

			viewHolder.tvMsgTimeOther.setText(groupMessageBeens.get(position).uname+" at: "+groupMessageBeens.get(position).dateof);
			viewHolder.tvMsgTimeOther.setTag(groupMessageBeens.get(position).uname+" at: "+groupMessageBeens.get(position).dateof);

			DATA.loadImageFromURL(groupMessageBeens.get(position).image, R.drawable.icon_call_screen, viewHolder.ivMsgOther);
		}
		
		return convertView;
	}

}
