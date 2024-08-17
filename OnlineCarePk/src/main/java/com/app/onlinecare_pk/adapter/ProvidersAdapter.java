package com.app.onlinecare_pk.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.model.PatientProviderBean;
import com.app.onlinecare_pk.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

public class ProvidersAdapter extends ArrayAdapter<PatientProviderBean> {

	Activity activity;
	ArrayList<PatientProviderBean> patientProviderBeens;

	public ProvidersAdapter(Activity activity, ArrayList<PatientProviderBean> patientProviderBeens) {
		super(activity, R.layout.providers_list_row, patientProviderBeens);

		this.activity = activity;
		this.patientProviderBeens = patientProviderBeens;
	}

	static class ViewHolder {

		TextView tvProviderName,tvProviderDesig;
		ImageView ivProvider,ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.providers_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvProviderName= (TextView) convertView.findViewById(R.id.tvProviderName);
            viewHolder.tvProviderDesig = (TextView) convertView.findViewById(R.id.tvProviderDesig);
			viewHolder.ivProvider = (CircularImageView) convertView.findViewById(R.id.ivProvider);
            viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvProviderName, viewHolder.tvProviderName);
			convertView.setTag(R.id.tvProviderDesig, viewHolder.tvProviderDesig);
            convertView.setTag(R.id.ivProvider, viewHolder.ivProvider);
            convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tvProviderName.setText(patientProviderBeens.get(position).first_name+
        " "+patientProviderBeens.get(position).last_name);

        DATA.loadImageFromURL(patientProviderBeens.get(position).image,R.drawable.icon_call_screen,viewHolder.ivProvider);

		if(patientProviderBeens.get(position).current_app.equalsIgnoreCase("nurse")){
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(patientProviderBeens.get(position).doctor_category));
        }else{
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(patientProviderBeens.get(position).current_app));
        }

        if (patientProviderBeens.get(position).is_online.equals("1")) {
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
        }else{
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
        }


		return convertView;
	}
}
