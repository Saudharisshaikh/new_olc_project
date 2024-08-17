package com.app.emcurama.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurama.R;
import com.app.emcurama.model.DrugBean;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.PrescriptionModule;

public class DrugsAdapter extends ArrayAdapter<DrugBean> {

	Activity activity;
	//ArrayList<DrugBean> drugBeans;
	PrescriptionModule prescriptionModule;

	public DrugsAdapter(Activity activity, PrescriptionModule prescriptionModule) {
		super(activity, R.layout.drug_list_row, DATA.drugBeans);

		this.activity = activity;
		this.prescriptionModule = prescriptionModule;
		//this.drugBeans = drugBeans;
	}

	static class ViewHolder {

		TextView tvDrugName,tvDrugStrength,tvDrugDosageForm,tvDrugQuantity,tvDrugDuration,tvDrugPotencyCode,tvDrugInstructions,tvDrugRefils,tvDrugNum;
		ImageView ivDeleteDrug, ivEditDrug;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.drug_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvDrugName = (TextView) convertView.findViewById(R.id.tvDrugName);
			viewHolder.ivDeleteDrug = (ImageView) convertView.findViewById(R.id.ivDeleteDrug);
			viewHolder.ivEditDrug = (ImageView) convertView.findViewById(R.id.ivEditDrug);
			viewHolder.tvDrugStrength = (TextView) convertView.findViewById(R.id.tvDrugStrength);
			viewHolder.tvDrugDosageForm = (TextView) convertView.findViewById(R.id.tvDrugDosageForm);
			viewHolder.tvDrugQuantity = (TextView) convertView.findViewById(R.id.tvDrugQuantity);
			viewHolder.tvDrugDuration = (TextView) convertView.findViewById(R.id.tvDrugDuration);
			viewHolder.tvDrugPotencyCode = (TextView) convertView.findViewById(R.id.tvDrugPotencyCode);
			viewHolder.tvDrugInstructions = (TextView) convertView.findViewById(R.id.tvDrugInstructions);
			viewHolder.tvDrugRefils = (TextView) convertView.findViewById(R.id.tvDrugRefils);
			viewHolder.tvDrugNum = (TextView) convertView.findViewById(R.id.tvDrugNum);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDrugName, viewHolder.tvDrugName);
			convertView.setTag(R.id.ivDeleteDrug, viewHolder.ivDeleteDrug);
			convertView.setTag(R.id.ivEditDrug, viewHolder.ivEditDrug);
			convertView.setTag(R.id.tvDrugStrength, viewHolder.tvDrugStrength);
			convertView.setTag(R.id.tvDrugDosageForm, viewHolder.tvDrugDosageForm);
			convertView.setTag(R.id.tvDrugQuantity, viewHolder.tvDrugQuantity);
			convertView.setTag(R.id.tvDrugDuration, viewHolder.tvDrugDuration);
			convertView.setTag(R.id.tvDrugPotencyCode, viewHolder.tvDrugPotencyCode);
			convertView.setTag(R.id.tvDrugInstructions, viewHolder.tvDrugInstructions);
			convertView.setTag(R.id.tvDrugRefils, viewHolder.tvDrugRefils);
			convertView.setTag(R.id.tvDrugNum, viewHolder.tvDrugNum);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvDrugNum.setText((position+1)+". ");
		viewHolder.tvDrugName.setText(DATA.drugBeans.get(position).getDrug_name());
		//viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);
		

		viewHolder.tvDrugName.setTag(DATA.drugBeans.get(position).getDrug_name());
		//viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);
		
		
		viewHolder.tvDrugStrength.setText(DATA.drugBeans.get(position).getStrength()+" "+DATA.drugBeans.get(position).getStrength_unit_of_measure());
		viewHolder.tvDrugDosageForm.setText(DATA.drugBeans.get(position).getDosage_form());
		viewHolder.tvDrugQuantity.setText(DATA.drugBeans.get(position).totalQuantity);
		viewHolder.tvDrugDuration.setText(DATA.drugBeans.get(position).duration);
		viewHolder.tvDrugPotencyCode.setText(DATA.drugBeans.get(position).getPotency_unit());
		viewHolder.tvDrugInstructions.setText(DATA.drugBeans.get(position).instructions);
		viewHolder.tvDrugRefils.setText(DATA.drugBeans.get(position).refill);

		viewHolder.ivDeleteDrug.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				new AlertDialog.Builder(activity).setTitle("Confirm").
				setMessage("Are you sure? You want to remove this medication from the list.").
				setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						DATA.drugBeans.remove(position);
						notifyDataSetChanged();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				}).show();
			}
		});

		viewHolder.ivEditDrug.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					DrugBean drugBean = DATA.drugBeans.get(position);
					//PrescriptionModule prescriptionModule = new PrescriptionModule((AppCompatActivity) activity, false);
					prescriptionModule.initDrugsDialog(true, drugBean, position);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

		return convertView;
	}
}
