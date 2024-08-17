package com.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.covacard.R;
import com.covacard.model.InvoiceBean;

import java.util.ArrayList;

public class InvoiceAdapter extends ArrayAdapter<InvoiceBean> {

	Activity activity;
	ArrayList<InvoiceBean> invoiceBeans;


	public InvoiceAdapter(Activity activity , ArrayList<InvoiceBean> invoiceBeans) {
		super(activity, R.layout.lv_invoice_row, invoiceBeans);

		this.activity = activity;
		this.invoiceBeans = invoiceBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		TextView tvDate,tvAmount,tvMsg;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_invoice_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvAmount = convertView.findViewById(R.id.tvAmount);
			viewHolder.tvMsg = convertView.findViewById(R.id.tvMsg);

			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvDate.setText(invoiceBeans.get(position).dateof);
		viewHolder.tvAmount.setText("US$ "+invoiceBeans.get(position).amount);
		viewHolder.tvMsg.setText(invoiceBeans.get(position).message);


		return convertView;
	}



}

