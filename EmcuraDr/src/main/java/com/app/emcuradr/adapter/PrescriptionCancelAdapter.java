package com.app.emcuradr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcuradr.R;
import com.app.emcuradr.model.PrescriptionCancelBean;
import com.app.emcuradr.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;


public class PrescriptionCancelAdapter extends ArrayAdapter<PrescriptionCancelBean> {

    Activity activity;
    ArrayList<PrescriptionCancelBean> prescriptionCancelBeans;

    public PrescriptionCancelAdapter(Activity activity , ArrayList<PrescriptionCancelBean> prescriptionCancelBeans) {
        super(activity, R.layout.dr_prescriptioncancel_row, prescriptionCancelBeans);

        this.activity = activity;
        this.prescriptionCancelBeans = prescriptionCancelBeans;
//		filter(DATA.selectedDrId);
    }

    static class ViewHolder {

        TextView tvpressDrName,tvPressDate,tvPressVitals,tvPressDiagnoses,tvPressTreatments,tvPresscribedBy;
        CircularImageView ivPressDr;
        ImageView ivSign;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PrescriptionCancelAdapter.ViewHolder viewHolder = null;

        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dr_prescriptioncancel_row, null);

            viewHolder = new PrescriptionCancelAdapter.ViewHolder();

            viewHolder.tvpressDrName = (TextView) convertView.findViewById(R.id.tvPressDrName);
            viewHolder.tvPressDate = (TextView) convertView.findViewById(R.id.tvPressDate);
            viewHolder.ivPressDr = (CircularImageView) convertView.findViewById(R.id.ivPressDr);
            viewHolder.tvPressVitals = (TextView) convertView.findViewById(R.id.tvPressVitals);
            viewHolder.tvPressDiagnoses = (TextView) convertView.findViewById(R.id.tvPressDiagnoses);
            viewHolder.tvPressTreatments = (TextView) convertView.findViewById(R.id.tvPressTreatments);
            viewHolder.tvPresscribedBy = (TextView) convertView.findViewById(R.id.tvPresscribedBy);
            viewHolder.ivSign = (ImageView) convertView.findViewById(R.id.ivSign);
            // viewHolder.btnCanelPrescription = (TextView) convertView.findViewById(R.id.btnCanelPrescription);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvPressDrName, viewHolder.tvpressDrName);
            convertView.setTag(R.id.tvPressDate, viewHolder.tvPressDate);
            convertView.setTag(R.id.ivPressDr, viewHolder.ivPressDr);
            convertView.setTag(R.id.tvPressVitals, viewHolder.tvPressVitals);
            convertView.setTag(R.id.tvPressDiagnoses, viewHolder.tvPressDiagnoses);
            convertView.setTag(R.id.tvPressTreatments, viewHolder.tvPressTreatments);
            convertView.setTag(R.id.ivSign, viewHolder.ivSign);
            // convertView.setTag(R.id.btnCanelPrescription, viewHolder.btnCanelPrescription);
        }
        else {

            viewHolder = (PrescriptionCancelAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.tvpressDrName.setText(prescriptionCancelBeans.get(position).getFirst_name()+" "+prescriptionCancelBeans.get(position).getLast_name());
        viewHolder.tvpressDrName.setTag(prescriptionCancelBeans.get(position).getFirst_name()+" "+prescriptionCancelBeans.get(position).getLast_name());

        viewHolder.tvPressDate.setText("Sent on "+prescriptionCancelBeans.get(position).getDateof());
        viewHolder.tvPressDate.setTag("Sent on "+prescriptionCancelBeans.get(position).getDateof());

        viewHolder.tvPressVitals.setText(prescriptionCancelBeans.get(position).getDrug_name());
        viewHolder.tvPressVitals.setTag(prescriptionCancelBeans.get(position).getDrug_name());

        viewHolder.tvPressDiagnoses.setText(prescriptionCancelBeans.get(position).getQuantity());
        viewHolder.tvPressDiagnoses.setTag(prescriptionCancelBeans.get(position).getQuantity());

        viewHolder.tvPressTreatments.setText(prescriptionCancelBeans.get(position).getDirections());
        viewHolder.tvPressTreatments.setTag(prescriptionCancelBeans.get(position).getDirections());

	/*UrlImageViewHelper.setUrlDrawable(viewHolder.ivPressDr, prescriptionBeans.get(position).getImage(), R.drawable.icon_dummy);
	UrlImageViewHelper.setUrlDrawable(viewHolder.ivSign, prescriptionBeans.get(position).getSignature(), R.drawable.ic_signature);*/

        DATA.loadImageFromURL(prescriptionCancelBeans.get(position).getImage(), R.drawable.icon_call_screen, viewHolder.ivPressDr);

        DATA.loadImageFromURL(prescriptionCancelBeans.get(position).getSignature(), R.drawable.ic_signature, viewHolder.ivSign);


        viewHolder.tvPresscribedBy.setVisibility(View.GONE);
	  /*if(! prescriptionBeans.get(position).prescribed_by.isEmpty()){
		  viewHolder.tvPresscribedBy.setVisibility(View.VISIBLE);
		  viewHolder.tvPresscribedBy.setText("Prescribed By: "+prescriptionBeans.get(position).prescribed_by);
		  viewHolder.tvPresscribedBy.setTag("Prescribed By: "+prescriptionBeans.get(position).prescribed_by);
	  }else {
		  viewHolder.tvPresscribedBy.setVisibility(View.GONE);
	  }*/
        return convertView;
    }

}
