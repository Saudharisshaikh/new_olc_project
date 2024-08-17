package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.List;

public class DietAssesListAdapter extends ArrayAdapter<DietitaryAssesListBean> {

	Activity activity;
	List<DietitaryAssesListBean> dietitaryAssesListBeans;

	public DietAssesListAdapter(Activity activity , List<DietitaryAssesListBean> dietitaryAssesListBeans) {
		super(activity, R.layout.lv_dietasses_list_row, dietitaryAssesListBeans);
		this.activity = activity;
		this.dietitaryAssesListBeans = dietitaryAssesListBeans;
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
			convertView = layoutInflater.inflate(R.layout.lv_dietasses_list_row, null);

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


		viewHolder.tvDate.setText(dietitaryAssesListBeans.get(position).dateof);
		viewHolder.tvProvider.setText(dietitaryAssesListBeans.get(position).doctor_name);//+" ("+dietitaryAssesListBeans.get(position).doctor_category+")"


		boolean enable = ((! TextUtils.isEmpty(dietitaryAssesListBeans.get(position).is_lock)) && dietitaryAssesListBeans.get(position).is_lock.equalsIgnoreCase("0"));
		viewHolder.tvEdit.setEnabled(enable);
		int drawableRes = dietitaryAssesListBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityDietAssesList){
					((ActivityDietAssesList) activity).editForm(position);
				}
			}
		});

		return convertView;
	}

}
