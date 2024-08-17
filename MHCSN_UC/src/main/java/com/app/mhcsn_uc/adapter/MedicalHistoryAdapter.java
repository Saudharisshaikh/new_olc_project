/*package com.app.onlinecare.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare.R;
import com.app.onlinecare.model.ReportsModel;
import com.app.onlinecare.util.DATA;

public class MedicalHistoryAdapter extends ArrayAdapter<String> {
	
	Activity activity;
	
	int getPosition;
	
	String[] deseaseNames;

	public MedicalHistoryAdapter(Activity activity , String[] deseaseNames) {
		super(activity, R.layout.lay_select_reports_row,deseaseNames);
		this.activity = activity;
		this.deseaseNames = deseaseNames;
	}

	static class ViewHolder {
		protected TextView tvDiseaseNameRow;
		protected CheckBox checkSelectDisease;

}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lay_select_reports_row, null);

			 viewHolder = new ViewHolder();
			 viewHolder.tvDiseaseNameRow = (TextView) convertView.findViewById(R.id.tvUsersRowName);
			 viewHolder.checkSelectDisease = (CheckBox) convertView.findViewById(R.id.checkSelectContact);
							 
			 
			 
			 viewHolder.checkSelectDisease.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					 getPosition = (Integer) buttonView.getTag(); // Here we get the position that we have set for the checkbox using setTag.
					// DATA.allReports.get(getPosition).setSelected(buttonView.isChecked());// Set the value of checkbox to maintain its state.

					if(buttonView.isChecked()) {
						
						//DATA.allReports.get(getPosition).status = "1";
						//DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory + deseaseNames[position]+",";
						
						DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory + position+",";
						
					}
					else {
						//DATA.allReports.get(getPosition).status = "0";
						//DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory.replace(deseaseNames[position]+",", "");
						
						DATA.desaesenamesFromHistory= DATA.desaesenamesFromHistory.replace(position+",", "");
					}	
					
					DATA.print("--value of history from adapter "+DATA.desaesenamesFromHistory);
				}
			});

				convertView.setTag(viewHolder);
				convertView.setTag(R.id.tvUsersRowName, viewHolder.tvDiseaseNameRow);
				convertView.setTag(R.id.checkSelectContact,viewHolder.checkSelectDisease);

				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
//			viewHolder.checkSelectContact.setOnCheckedChangeListener(null);
		}

		viewHolder.checkSelectDisease.setTag(position); // This line is important.

		viewHolder.tvDiseaseNameRow.setText(deseaseNames[position]);

		//viewHolder.checkSelectDisease.setChecked(DATA.allReports.get(position).isSelected());			
	
		if (DATA.selectedMedicalHistoryPositions.contains(position)) {
			// DATA.print("--inside true adapter position "+position);
			 viewHolder.checkSelectDisease.setChecked(true);
		} else {
			//DATA.print("--inside false adapter position "+position);
			 viewHolder.checkSelectDisease.setChecked(false);
		}
		return convertView;
	}

}
*/