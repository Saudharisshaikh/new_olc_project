package com.app.mdlive_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_uc.R;
import com.app.mdlive_uc.model.PressDetailBean;

import java.util.ArrayList;

public class PresDetailAdapter extends ArrayAdapter<PressDetailBean> {

	Activity activity;
	ArrayList<PressDetailBean> pressDetailBeens;

	public PresDetailAdapter(Activity activity,ArrayList<PressDetailBean> pressDetailBeens) {
		super(activity, R.layout.pres_detail_row, pressDetailBeens);

		this.activity = activity;
		this.pressDetailBeens = pressDetailBeens;
	}

	static class ViewHolder {

		TextView tvPresInd,tvPresName;
		TextView btnReqRifill;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.pres_detail_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvPresInd = (TextView) convertView.findViewById(R.id.tvPresInd);
			viewHolder.tvPresName = (TextView) convertView.findViewById(R.id.tvPresName);
			viewHolder.btnReqRifill = (TextView) convertView.findViewById(R.id.btnReqRifill);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPresInd, viewHolder.tvPresInd);
			convertView.setTag(R.id.tvPresName, viewHolder.tvPresName);
			convertView.setTag(R.id.btnReqRifill, viewHolder.btnReqRifill);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvPresInd.setText(position+1+"");
		viewHolder.tvPresName.setText(pressDetailBeens.get(position).drug_name);

		if (pressDetailBeens.get(position).refill.equals("0")) {
			viewHolder.btnReqRifill.setVisibility(View.GONE);
		}else{
			viewHolder.btnReqRifill.setVisibility(View.VISIBLE);
		}

		return convertView;
	}
}
