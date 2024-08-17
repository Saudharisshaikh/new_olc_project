package com.app.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.covacard.R;
import com.app.covacard.model.PrescriptionBean;
import com.app.covacard.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class PrescriptionAdapter extends ArrayAdapter<PrescriptionBean> {

	Activity activity;
	ArrayList<PrescriptionBean> prescriptionBeans;

	public PrescriptionAdapter(Activity activity , ArrayList<PrescriptionBean> prescriptionBeans) {
		super(activity, R.layout.dr_prescription_row, prescriptionBeans);

		this.activity = activity;
		this.prescriptionBeans = prescriptionBeans;
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvpressDrName,tvPressDate,tvPressVitals,tvPressDiagnoses,tvPressTreatments,tvSentOn;
		CircularImageView ivPressDr;
		ImageView ivSign;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.dr_prescription_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvpressDrName = (TextView) convertView.findViewById(R.id.tvPressDrName);
			viewHolder.tvPressDate = (TextView) convertView.findViewById(R.id.tvPressDate);
			viewHolder.ivPressDr = (CircularImageView) convertView.findViewById(R.id.ivPressDr);
			viewHolder.tvPressVitals = (TextView) convertView.findViewById(R.id.tvPressVitals);
			viewHolder.tvPressDiagnoses = (TextView) convertView.findViewById(R.id.tvPressDiagnoses);
			viewHolder.tvPressTreatments = (TextView) convertView.findViewById(R.id.tvPressTreatments);
			viewHolder.ivSign = (ImageView) convertView.findViewById(R.id.ivSign);
			viewHolder.tvSentOn = (TextView) convertView.findViewById(R.id.tvSentOn);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPressDrName, viewHolder.tvpressDrName);
			convertView.setTag(R.id.tvPressDate, viewHolder.tvPressDate);
			convertView.setTag(R.id.ivPressDr, viewHolder.ivPressDr);
			convertView.setTag(R.id.tvPressVitals, viewHolder.tvPressVitals);
			convertView.setTag(R.id.tvPressDiagnoses, viewHolder.tvPressDiagnoses);
			convertView.setTag(R.id.tvPressTreatments, viewHolder.tvPressTreatments);
			convertView.setTag(R.id.ivSign, viewHolder.ivSign);
			convertView.setTag(R.id.tvSentOn, viewHolder.tvSentOn);

		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvpressDrName.setText(prescriptionBeans.get(position).getFirst_name()+" "+prescriptionBeans.get(position).getLast_name());
		viewHolder.tvpressDrName.setTag(prescriptionBeans.get(position).getFirst_name()+" "+prescriptionBeans.get(position).getLast_name());

		viewHolder.tvPressDate.setText("Dated: "+prescriptionBeans.get(position).getDateof());
		viewHolder.tvPressDate.setTag("Dated: "+prescriptionBeans.get(position).getDateof());
		
		viewHolder.tvPressVitals.setText(prescriptionBeans.get(position).getVitals());
		viewHolder.tvPressVitals.setTag(prescriptionBeans.get(position).getVitals());
		
		viewHolder.tvPressDiagnoses.setText(prescriptionBeans.get(position).getDiagnosis());
		viewHolder.tvPressDiagnoses.setTag(prescriptionBeans.get(position).getDiagnosis());
		
		viewHolder.tvPressTreatments.setText(prescriptionBeans.get(position).getTreatment());
		viewHolder.tvPressTreatments.setTag(prescriptionBeans.get(position).getTreatment());

		viewHolder.tvSentOn.setText("Sent on: "+prescriptionBeans.get(position).getDateof());
		viewHolder.tvSentOn.setTag("Sent on: "+prescriptionBeans.get(position).getDateof());

		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivPressDr, prescriptionBeans.get(position).getImage(), R.drawable.doctor);
		if (!prescriptionBeans.get(position).getImage().isEmpty()) {
			DATA.loadImageFromURL(prescriptionBeans.get(position).getImage(), R.drawable.icon_call_screen, viewHolder.ivPressDr);
		}
		if (!prescriptionBeans.get(position).getSignature().isEmpty()) {
			DATA.loadImageFromURL(prescriptionBeans.get(position).getSignature(), R.drawable.ic_signature, viewHolder.ivSign);
		}
		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivSign, prescriptionBeans.get(position).getSignature(), R.drawable.ic_signature);
		return convertView;
	}

}
