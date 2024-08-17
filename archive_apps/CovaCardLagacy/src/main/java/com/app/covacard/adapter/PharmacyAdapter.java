package com.app.covacard.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.covacard.R;
import com.app.covacard.model.PharmacyBean;
import com.app.covacard.util.GloabalMethods;

import java.util.ArrayList;

public class PharmacyAdapter extends ArrayAdapter<PharmacyBean> {

	Activity activity;
	ArrayList<PharmacyBean> pharmacyBeans;

	public PharmacyAdapter(Activity activity,ArrayList<PharmacyBean> pharmacyBeans) {
		super(activity, R.layout.lv_pharmacy_row, pharmacyBeans);

		this.activity = activity;
		this.pharmacyBeans = pharmacyBeans;
	}

	static class ViewHolder {

		TextView tvPharmacyName,tvPharmacyCityState,tvPharmacyAddress,
				tvPharmacyLocation,tvSelectPharmacy;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_pharmacy_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvPharmacyName = (TextView) convertView.findViewById(R.id.tvPharmacyName);
			viewHolder.tvPharmacyCityState = (TextView) convertView.findViewById(R.id.tvPharmacyCityState);
			viewHolder.tvPharmacyAddress = (TextView) convertView.findViewById(R.id.tvPharmacyAddress);
			viewHolder.tvPharmacyLocation = (TextView) convertView.findViewById(R.id.tvPharmacyLocation);
			viewHolder.tvSelectPharmacy = (TextView) convertView.findViewById(R.id.tvSelectPharmacy);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPharmacyName, viewHolder.tvPharmacyName);
			convertView.setTag(R.id.tvPharmacyCityState, viewHolder.tvPharmacyCityState);
			convertView.setTag(R.id.tvPharmacyAddress, viewHolder.tvPharmacyAddress);
			convertView.setTag(R.id.tvPharmacyLocation, viewHolder.tvPharmacyLocation);
			convertView.setTag(R.id.tvSelectPharmacy, viewHolder.tvSelectPharmacy);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvPharmacyName.setText(pharmacyBeans.get(position).StoreName);
		viewHolder.tvPharmacyCityState.setText(pharmacyBeans.get(position).address+", "+pharmacyBeans.get(position).city+", "+
				pharmacyBeans.get(position).state+", "+pharmacyBeans.get(position).zipcode);
		viewHolder.tvPharmacyAddress.setText(pharmacyBeans.get(position).SpecialtyName);

		/*viewHolder.tvPharmacyName.setTag(pharmacyBeans.get(position).StoreName);
		viewHolder.tvPharmacyCityState.setTag(pharmacyBeans.get(position).city+" ("+pharmacyBeans.get(position).state+")");
		viewHolder.tvPharmacyAddress.setTag(pharmacyBeans.get(position).address+", "+pharmacyBeans.get(position).zipcode);*/

		viewHolder.tvPharmacyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new GloabalMethods(activity).showPharmacyMap(pharmacyBeans.get(position));

				showPharmacyDetailsDialog(pharmacyBeans.get(position));
			}
		});


		return convertView;
	}




	public void showPharmacyDetailsDialog(PharmacyBean pharmacyBean){
		final Dialog dialogPharmacyDetails = new Dialog(activity);
		dialogPharmacyDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogPharmacyDetails.setContentView(R.layout.dialog_pharmacy_details);
		dialogPharmacyDetails.setCancelable(false);


		Button btnCancel = dialogPharmacyDetails.findViewById(R.id.btnCancel);

		TextView tvPharmacyName = dialogPharmacyDetails.findViewById(R.id.tvPharmacyName);
		TextView tvPharmacyAddress = dialogPharmacyDetails.findViewById(R.id.tvPharmacyAddress);
		TextView tvPharmacyCity = dialogPharmacyDetails.findViewById(R.id.tvPharmacyCity);
		TextView tvPharmacyState = dialogPharmacyDetails.findViewById(R.id.tvPharmacyState);
		TextView tvPharmacyZipcode = dialogPharmacyDetails.findViewById(R.id.tvPharmacyZipcode);
		TextView tvPharmacyPhone = dialogPharmacyDetails.findViewById(R.id.tvPharmacyPhone);
		TextView tvPharmacyType = dialogPharmacyDetails.findViewById(R.id.tvPharmacyType);


		tvPharmacyName.setText(pharmacyBean.StoreName);
		tvPharmacyAddress.setText(pharmacyBean.address);
		tvPharmacyCity.setText(pharmacyBean.city);
		tvPharmacyState.setText(pharmacyBean.state);
		tvPharmacyZipcode.setText(pharmacyBean.zipcode);
		tvPharmacyPhone.setText(pharmacyBean.PhonePrimary);
		tvPharmacyType.setText(pharmacyBean.SpecialtyName);


		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogPharmacyDetails.dismiss();
			}
		});
		dialogPharmacyDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogPharmacyDetails.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogPharmacyDetails.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogPharmacyDetails.show();
        dialogPharmacyDetails.getWindow().setAttributes(lp);*/
	}
}
