package com.app.fivestaruc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fivestaruc.ActivityGroupChat;
import com.app.fivestaruc.ActivityGroupMessages;
import com.app.fivestaruc.R;
import com.app.fivestaruc.model.GroupMemberBean;
import com.app.fivestaruc.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class GroupMemberAdapter extends ArrayAdapter<GroupMemberBean> {

	Activity activity;
	ArrayList<GroupMemberBean> groupMemberBeens;

	public GroupMemberAdapter(Activity activity,ArrayList<GroupMemberBean> groupMemberBeens) {
		super(activity, R.layout.lv_group_member_row, groupMemberBeens);

		this.activity = activity;
		this.groupMemberBeens = groupMemberBeens;
	}

	static class ViewHolder {
		CircularImageView ivDoctor;
		TextView tvDoctorName,tvDoctorDesig;
		//Button btnConnect;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_group_member_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivDoctor = (CircularImageView) convertView.findViewById(R.id.ivDoctor);
			viewHolder.tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
			viewHolder.tvDoctorDesig = (TextView) convertView.findViewById(R.id.tvDoctorDesig);
			//viewHolder.btnConnect = (Button) convertView.findViewById(R.id.btnConnect);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDoctorName, viewHolder.tvDoctorName);
			convertView.setTag(R.id.tvDoctorDesig, viewHolder.tvDoctorDesig);
			convertView.setTag(R.id.ivDoctor, viewHolder.ivDoctor);
			//convertView.setTag(R.id.btnConnect, viewHolder.btnConnect);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(!groupMemberBeens.get(position).image.isEmpty()){
			DATA.loadImageFromURL(groupMemberBeens.get(position).image, R.drawable.icon_call_screen ,viewHolder.ivDoctor);
		}
		
		viewHolder.tvDoctorName.setText(groupMemberBeens.get(position).first_name+" "+groupMemberBeens.get(position).last_name);
		viewHolder.tvDoctorName.setTag(groupMemberBeens.get(position).first_name+" "+groupMemberBeens.get(position).last_name);

		if(ActivityGroupChat.selectedGroupBean.created_by.equals(groupMemberBeens.get(position).user_id) &&
				ActivityGroupMessages.groupParticepentsDoctorIds.contains(groupMemberBeens.get(position).user_id)){
			viewHolder.tvDoctorDesig.setText("Group Admin");
			viewHolder.tvDoctorDesig.setTag("Group Admin");
		}else{
			viewHolder.tvDoctorDesig.setText(groupMemberBeens.get(position).type);
			viewHolder.tvDoctorDesig.setTag(groupMemberBeens.get(position).type);
		}

		if(groupMemberBeens.get(position).is_online.equals("1")){
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}

		return convertView;
	}
}
