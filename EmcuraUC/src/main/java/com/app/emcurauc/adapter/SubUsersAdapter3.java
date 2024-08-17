package com.app.emcurauc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurauc.R;
import com.app.emcurauc.model.SubUsersModel;
import com.app.emcurauc.util.DATA;

import java.util.ArrayList;

public class SubUsersAdapter3 extends ArrayAdapter<SubUsersModel> {

	Activity activity;
	ArrayList<SubUsersModel> subUsersModels;

	public SubUsersAdapter3(Activity activity, ArrayList<SubUsersModel> subUsersModels) {
		super(activity, R.layout.lv_subusers_row, subUsersModels);

		this.activity = activity;
		this.subUsersModels = subUsersModels;
	}

	static class ViewHolder {
		ImageView ivSubUser;
		TextView tvSubUserName, tvSubUserRelation;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_subusers_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivSubUser = convertView.findViewById(R.id.ivSubUser);
			viewHolder.tvSubUserName = convertView.findViewById(R.id.tvSubUserName);
			viewHolder.tvSubUserRelation = convertView.findViewById(R.id.tvSubUserRelation);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivSubUser, viewHolder.ivSubUser);
			convertView.setTag(R.id.tvSubUserName, viewHolder.tvSubUserName);
			convertView.setTag(R.id.tvSubUserRelation, viewHolder.tvSubUserRelation);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(subUsersModels.get(position).image ,R.drawable.icon_call_screen, viewHolder.ivSubUser);

		viewHolder.tvSubUserName.setText(subUsersModels.get(position).firstName+" "+subUsersModels.get(position).lastName);
		viewHolder.tvSubUserRelation.setText(subUsersModels.get(position).relationship);


		return convertView;
	}
}
