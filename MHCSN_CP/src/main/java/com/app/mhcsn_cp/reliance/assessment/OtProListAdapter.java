package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.List;

public class OtProListAdapter extends ArrayAdapter<OtProListBean> {

	Activity activity;
	List<OtProListBean> otProListBeans;

	public OtProListAdapter(Activity activity , List<OtProListBean> otProListBeans) {
		super(activity, R.layout.lv_ot_pro_list_row, otProListBeans);
		this.activity = activity;
		this.otProListBeans = otProListBeans;
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
			convertView = layoutInflater.inflate(R.layout.lv_ot_pro_list_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvProvider = convertView.findViewById(R.id.tvProvider);
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


		viewHolder.tvDate.setText(otProListBeans.get(position).dateof);
		viewHolder.tvProvider.setText(otProListBeans.get(position).doctor_name);//+" ("+otProListBeans.get(position).doctor_category+")"


		viewHolder.tvEdit.setEnabled(! otProListBeans.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = otProListBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityOtProfileList){
					((ActivityOtProfileList) activity).viewOrEditForm(position);
				}
			}
		});

		return convertView;
	}

}
