package com.app.amnm_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.amnm_dr.R;
import com.app.amnm_dr.model.TransectionBean;

import java.util.ArrayList;

public class TransectionAdapter extends ArrayAdapter<TransectionBean> {

	Activity activity;
	ArrayList<TransectionBean> transectionBeans;

	public TransectionAdapter(Activity activity , ArrayList<TransectionBean> transectionBeans) {
		super(activity, R.layout.lv_transections_row, transectionBeans);

		this.activity = activity;
		this.transectionBeans = transectionBeans;
	}

	static class ViewHolder {

		TextView tvTransectionType,tvTransectionDate,tvTransectionAmount,tvTransectionGetway,tvTransectionId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_transections_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvTransectionType = (TextView) convertView.findViewById(R.id.tvTransectionType);
			viewHolder.tvTransectionDate = (TextView) convertView.findViewById(R.id.tvTransectionDate);
			viewHolder.tvTransectionAmount = (TextView) convertView.findViewById(R.id.tvTransectionAmount);
			viewHolder.tvTransectionGetway = (TextView) convertView.findViewById(R.id.tvTransectionGetway);
			viewHolder.tvTransectionId = (TextView) convertView.findViewById(R.id.tvTransectionId);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvTransectionType, viewHolder.tvTransectionType);
			convertView.setTag(R.id.tvTransectionDate, viewHolder.tvTransectionDate);
			convertView.setTag(R.id.tvTransectionAmount, viewHolder.tvTransectionAmount);
			convertView.setTag(R.id.tvTransectionGetway, viewHolder.tvTransectionGetway);
			convertView.setTag(R.id.tvTransectionId, viewHolder.tvTransectionId);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		if (transectionBeans.get(position).getLivecare_id() .equalsIgnoreCase("null") ) {
			viewHolder.tvTransectionType.setText((position+1)+". Online Care Donation");
		} else {
			viewHolder.tvTransectionType.setText((position+1)+". eLiveCare Payment");
		}
		viewHolder.tvTransectionDate.setText("Dated: "+transectionBeans.get(position).getDateof());
		viewHolder.tvTransectionAmount.setText("Amount: "+"$"+transectionBeans.get(position).getAmount());
		viewHolder.tvTransectionGetway.setText(transectionBeans.get(position).getPayment_method());
		viewHolder.tvTransectionId.setText(transectionBeans.get(position).getTransaction_id());

		return convertView;
	}

}
