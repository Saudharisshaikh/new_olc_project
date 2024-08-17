package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.List;

public class AdlListAdapter extends ArrayAdapter<AdlListBean> {

	Activity activity;
	List<AdlListBean> adlListBeans;

	public AdlListAdapter(Activity activity , List<AdlListBean> adlListBeans) {
		super(activity, R.layout.lv_adl_list_row, adlListBeans);
		this.activity = activity;
		this.adlListBeans = adlListBeans;
	}

	static class ViewHolder {
		TextView tvScore, tvDate, tvSeverity,tvProvider,tvEdit;
		LinearLayout laySev;
		ImageView ivLock;
	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_adl_list_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvScore = convertView.findViewById(R.id.tvScore);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvSeverity = convertView.findViewById(R.id.tvSeverity);
			viewHolder.tvProvider = convertView.findViewById(R.id.tvProvider);
			viewHolder.laySev = convertView.findViewById(R.id.laySev);
			viewHolder.tvEdit = convertView.findViewById(R.id.tvEdit);
			viewHolder.ivLock = convertView.findViewById(R.id.ivLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvScore , viewHolder.tvScore);
			convertView.setTag(R.id.tvDate , viewHolder.tvDate);
			convertView.setTag(R.id.tvSeverity , viewHolder.tvSeverity);
			convertView.setTag(R.id.tvProvider , viewHolder.tvProvider);
			convertView.setTag(R.id.laySev , viewHolder.laySev);
			convertView.setTag(R.id.tvEdit , viewHolder.tvEdit);
			convertView.setTag(R.id.ivLock , viewHolder.ivLock);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvScore.setText(adlListBeans.get(position).score);
		viewHolder.tvDate.setText(adlListBeans.get(position).dateof);
		viewHolder.tvSeverity.setText(adlListBeans.get(position).severity);
		viewHolder.tvProvider.setText(adlListBeans.get(position).doctor_name);//+" ("+adlListBeans.get(position).doctor_category+")"

		int vis = TextUtils.isEmpty(adlListBeans.get(position).severity) ? View.GONE : View.VISIBLE;
		viewHolder.laySev.setVisibility(vis);

		viewHolder.tvEdit.setEnabled(! adlListBeans.get(position).is_lock.equalsIgnoreCase("1"));
		int drawableRes = adlListBeans.get(position).is_lock.equalsIgnoreCase("1") ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		viewHolder.ivLock.setImageResource(drawableRes);

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(activity instanceof ActivityAdlList){
					((ActivityAdlList) activity).viewOrEditForm(position, false);
				}
			}
		});

		return convertView;
	}

}
