package com.app.fivestardoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.fivestardoc.R;
import com.app.fivestardoc.model.SoapReferralBean;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewSkilledNursingAdapter extends ArrayAdapter<SoapReferralBean> {

	Activity activity;
	ArrayList<SoapReferralBean> soapReferralBeens;

	public ViewSkilledNursingAdapter(Activity activity , ArrayList<SoapReferralBean> soapReferralBeens) {
		super(activity, R.layout.dme_ref_row,soapReferralBeens);
		this.activity = activity;
		this.soapReferralBeens = soapReferralBeens;
	}

	static class ViewHolder {
		TextView tvSoapRefBy,tvSoapRefDetail,tvSoapRefStatus;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.dme_ref_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvSoapRefBy = (TextView) convertView.findViewById(R.id.tvSoapRefBy);
			viewHolder.tvSoapRefDetail = (TextView) convertView.findViewById(R.id.tvSoapRefDetail);
			viewHolder.tvSoapRefStatus = (TextView) convertView.findViewById(R.id.tvSoapRefStatus);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvSoapRefBy, viewHolder.tvSoapRefBy);
			convertView.setTag(R.id.tvSoapRefDetail,viewHolder.tvSoapRefDetail);
			convertView.setTag(R.id.tvSoapRefStatus,viewHolder.tvSoapRefStatus);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvSoapRefBy.setText("SOAP Notes Requested by:\n"
				+soapReferralBeens.get(position).first_name+" "+soapReferralBeens.get(position).last_name
				+", on "+soapReferralBeens.get(position).notes_date);

		StringBuilder details = new StringBuilder();
		if(!soapReferralBeens.get(position).skilled_nursing.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(soapReferralBeens.get(position).skilled_nursing);
				Iterator<String> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						Object value = jsonObject.get(key);
						key = key.replace("_"," ");
						//key = key.substring(0,1).toUpperCase() + key.substring(1).toLowerCase();
						key = WordUtils.capitalize(key);
						if(value.toString().equalsIgnoreCase("1")){
							value = "Yes";
						}else if(value.toString().equalsIgnoreCase("0")){
							value = "No";
						}
						details.append(key+" : "+value+"\n");
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		viewHolder.tvSoapRefDetail.setText(details);
		viewHolder.tvSoapRefStatus.setText("Status: "+soapReferralBeens.get(position).skilled_nursing_status);
		return convertView;
	}

}
