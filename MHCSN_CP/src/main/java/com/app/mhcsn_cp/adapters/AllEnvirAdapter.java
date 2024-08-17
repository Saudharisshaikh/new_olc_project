package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.ActivityAllEnvir;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.EnvirBean;

import java.util.ArrayList;

public class AllEnvirAdapter extends ArrayAdapter<EnvirBean> {

	Activity activity;
	ArrayList<EnvirBean> envirBeen;

	public AllEnvirAdapter(Activity activity , ArrayList<EnvirBean> envirBeen) {
		super(activity, R.layout.all_envirnmnt_row,envirBeen);
		this.activity = activity;
		this.envirBeen = envirBeen;
	}

	static class ViewHolder {
		TextView tvDate,tvProvider, tvEdit;
		ImageView ivLock;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.all_envirnmnt_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvProvider = (TextView) convertView.findViewById(R.id.tvProvider);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDate, viewHolder.tvDate);
			convertView.setTag(R.id.tvProvider, viewHolder.tvProvider);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDate.setText(envirBeen.get(position).dateof);
		viewHolder.tvProvider.setText(envirBeen.get(position).doctor_name+" ("+envirBeen.get(position).doctor_category+")");

		viewHolder.tvEdit.setEnabled(! envirBeen.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = envirBeen.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityAllEnvir){
					((ActivityAllEnvir) activity).viewOrEditForm(position, false);
				}
			}
		});


		return convertView;
	}

}
