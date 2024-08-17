package com.app.onlinecare_pk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.model.PrimaryCareBean;
import com.app.onlinecare_pk.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class PrimaryCareAdapter extends ArrayAdapter<PrimaryCareBean> {

	Activity activity;
	ArrayList<PrimaryCareBean> primaryCareBeens,primaryCareBeensOrig;
	SharedPreferences prefs;

	public PrimaryCareAdapter(Activity activity , ArrayList<PrimaryCareBean> primaryCareBeens) {
		super(activity, R.layout.primary_care_row, primaryCareBeens);

		this.activity = activity;
		this.primaryCareBeens = primaryCareBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);

		primaryCareBeensOrig = new ArrayList<PrimaryCareBean>();
		primaryCareBeensOrig.addAll(primaryCareBeens);
	}

	static class ViewHolder {

		TextView tvNurseName,tvNurseType,tvAssign, tvPriEmail,tvPriMobile,tvPriAddress;
		CircularImageView ivNurse;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.primary_care_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNurseName = (TextView) convertView.findViewById(R.id.tvNurseName);
			viewHolder.tvNurseType = (TextView) convertView.findViewById(R.id.tvNurseType);
			viewHolder.tvAssign = (TextView) convertView.findViewById(R.id.tvAssign);
			viewHolder.ivNurse = (CircularImageView) convertView.findViewById(R.id.ivNurse);
			viewHolder.tvPriEmail = (TextView) convertView.findViewById(R.id.tvPriEmail);
			viewHolder.tvPriMobile = (TextView) convertView.findViewById(R.id.tvPriMobile);
			viewHolder.tvPriAddress = (TextView) convertView.findViewById(R.id.tvPriAddress);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNurseName, viewHolder.tvNurseName);
			convertView.setTag(R.id.tvNurseType, viewHolder.tvNurseType);
			convertView.setTag(R.id.tvAssign, viewHolder.tvAssign);
			convertView.setTag(R.id.ivNurse, viewHolder.ivNurse);
			convertView.setTag(R.id.tvPriEmail, viewHolder.tvPriEmail);
			convertView.setTag(R.id.tvPriMobile, viewHolder.tvPriMobile);
			convertView.setTag(R.id.tvPriAddress, viewHolder.tvPriAddress);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvNurseName.setText(primaryCareBeens.get(position).first_name+" "+primaryCareBeens.get(position).last_name);
		viewHolder.tvNurseName.setTag(primaryCareBeens.get(position).first_name+" "+primaryCareBeens.get(position).last_name);
		
		
		viewHolder.tvNurseType.setText("Desig: "+primaryCareBeens.get(position).current_app);
		viewHolder.tvNurseType.setTag("Desig: "+primaryCareBeens.get(position).current_app);

		viewHolder.tvPriEmail.setText("Email: "+primaryCareBeens.get(position).email);
		viewHolder.tvPriMobile.setText("Ofice Phone: "+primaryCareBeens.get(position).mobile);
		viewHolder.tvPriAddress.setText("Address: "+primaryCareBeens.get(position).address1);

		//viewHolder.tvPatientCat.setText("You are assigned as: "+nurseBeens.get(position).patient_category);
		//viewHolder.tvPatientCat.setTag("You are assigned as: "+nurseBeens.get(position).patient_category);

		
		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivConv, conversationBeans.get(position).getImage(), R.drawable.ic_launcher);
		/*Picasso.with(activity)
	    .load(primaryCareBeens.get(position).image)
	    .placeholder(R.drawable.icon_call_screen)
	    .error(R.drawable.icon_call_screen)
	    .into(viewHolder.ivNurse);*/

		DATA.loadImageFromURL(primaryCareBeens.get(position).image,R.drawable.icon_call_screen,viewHolder.ivNurse);

		if (primaryCareBeens.get(position).isMyPrimaryCare) {
			//viewHolder.tvAssign.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			viewHolder.tvAssign.setEnabled(false);
			viewHolder.tvAssign.setText("Selected as your PCP");//"Added as primary care"
		}else {
			//viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_selector);
			viewHolder.tvAssign.setEnabled(true);
			viewHolder.tvAssign.setText("Select as your PCP");//"Add Primary Care"
		}


		if (primaryCareBeens.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}
		
		return convertView;
	}




	public void filter(String filterText) {
		primaryCareBeens.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("-- primaryCareBeensOrig size: "+primaryCareBeensOrig.size());
		if(filterText.length() == 0) {
			primaryCareBeens.addAll(primaryCareBeensOrig);
		} else {
			for(PrimaryCareBean temp : primaryCareBeensOrig) {
				if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
					primaryCareBeens.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
