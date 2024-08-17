package com.app.mhcsn_cp.reliance.medication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.DoctorsModel;
import com.app.mhcsn_cp.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;


public class LvDoctorsAdapter2 extends ArrayAdapter<DoctorsModel> {

	Activity activity;
	ArrayList<DoctorsModel> doctorsModels,doctorsModelsOrg;

	public LvDoctorsAdapter2(Activity activity, ArrayList<DoctorsModel> doctorsModels) {
		super(activity, R.layout.lv_doctors_row2, doctorsModels);

		this.activity = activity;
		this.doctorsModels = doctorsModels;

		this.doctorsModelsOrg = new ArrayList<>();
		this.doctorsModelsOrg.addAll(doctorsModels);
	}

	static class ViewHolder {
		CircularImageView ivDoctor;
		TextView tvDoctorName,tvDoctorDesig;
		Button btnConnect;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_doctors_row2, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivDoctor = (CircularImageView) convertView.findViewById(R.id.ivDoctor);
			viewHolder.tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
			viewHolder.tvDoctorDesig = (TextView) convertView.findViewById(R.id.tvDoctorDesig);
			//viewHolder.btnConnect = (Button) convertView.findViewById(R.id.btnConnect);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDoctorName, viewHolder.tvDoctorName);
			convertView.setTag(R.id.tvDoctorDesig, viewHolder.tvDoctorDesig);
			convertView.setTag(R.id.ivDoctor, viewHolder.ivDoctor);
			//convertView.setTag(R.id.btnConnect, viewHolder.btnConnect);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		DATA.loadImageFromURL(doctorsModels.get(position).image, R.drawable.icon_call_screen, viewHolder.ivDoctor);
		
		viewHolder.tvDoctorName.setText(doctorsModels.get(position).fName+" "+doctorsModels.get(position).lName);
		//viewHolder.tvDoctorDesig.setTag(doctorsModels.get(position).designation);

		viewHolder.tvDoctorName.setTag(doctorsModels.get(position).fName+" "+doctorsModels.get(position).lName);
		//viewHolder.tvDoctorDesig.setTag(doctorsModels.get(position).designation);

		if (doctorsModels.get(position).current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
			viewHolder.tvDoctorDesig.setText("Doctor");
		} else {
			viewHolder.tvDoctorDesig.setText(doctorsModels.get(position).speciality_name);
		}
		if (doctorsModels.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}
		return convertView;
	}



	public void filter(String filterText) {
		doctorsModels.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("---doctorsModelsOrg size: "+doctorsModelsOrg.size());
		if(filterText.length() == 0) {
			doctorsModels.addAll(doctorsModelsOrg);
		} else {
			for(DoctorsModel temp :doctorsModelsOrg) {
				if(temp.fName.toLowerCase(Locale.getDefault()).contains(filterText) || temp.lName.toLowerCase(Locale.getDefault()).contains(filterText)) {
					doctorsModels.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter
}
