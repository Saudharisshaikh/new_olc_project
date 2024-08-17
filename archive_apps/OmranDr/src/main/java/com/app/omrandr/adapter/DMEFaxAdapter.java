package com.app.omrandr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.omrandr.R;
import com.app.omrandr.model.FaxHistBean;

import java.util.ArrayList;

public class DMEFaxAdapter extends ArrayAdapter<FaxHistBean> {

	Activity activity;
	ArrayList<FaxHistBean> faxHistBeens;

	public DMEFaxAdapter(Activity activity, ArrayList<FaxHistBean> faxHistBeens) {
		super(activity, R.layout.lv_dmefax_row, faxHistBeens);

		this.activity = activity;
		this.faxHistBeens = faxHistBeens;
	}

	static class ViewHolder {
		TextView tvDMEName,tvDMEEmail,tvDMEFax;
	}

	@Override
	public int getCount() {
		return faxHistBeens.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_dmefax_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvDMEName = (TextView) convertView.findViewById(R.id.tvDMEName);
			viewHolder.tvDMEEmail = (TextView) convertView.findViewById(R.id.tvDMEEmail);
			viewHolder.tvDMEFax = (TextView) convertView.findViewById(R.id.tvDMEFax);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDMEName, viewHolder.tvDMEName);
			convertView.setTag(R.id.tvDMEEmail, viewHolder.tvDMEEmail);
			convertView.setTag(R.id.tvDMEFax, viewHolder.tvDMEFax);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDMEName.setText(faxHistBeens.get(position).name);
		viewHolder.tvDMEEmail.setText("Email : "+faxHistBeens.get(position).email);
		viewHolder.tvDMEFax.setText("Fax : "+faxHistBeens.get(position).fax_number);

		return convertView;
	}
}
