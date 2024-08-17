package com.app.amnm_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.amnm_ma.R;
import com.app.amnm_ma.model.DoctorsModel;
import com.app.amnm_ma.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import static com.app.amnm_ma.R.id.ivIsonline;

public class LvDoctorsAdapter extends ArrayAdapter<DoctorsModel> {

	Activity activity;

	public LvDoctorsAdapter(Activity activity) {
		super(activity, R.layout.lv_doctors_row, DATA.allDoctors);

		this.activity = activity;
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
			 convertView = layoutInflater.inflate(R.layout.lv_doctors_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivDoctor = (CircularImageView) convertView.findViewById(R.id.ivDoctor);
			viewHolder.tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
			viewHolder.tvDoctorDesig = (TextView) convertView.findViewById(R.id.tvDoctorDesig);
			//viewHolder.btnConnect = (Button) convertView.findViewById(R.id.btnConnect);
			viewHolder.ivIsonline = (ImageView) convertView.findViewById(ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDoctorName, viewHolder.tvDoctorName);
			convertView.setTag(R.id.tvDoctorDesig, viewHolder.tvDoctorDesig);
			convertView.setTag(R.id.ivDoctor, viewHolder.ivDoctor);
			//convertView.setTag(R.id.btnConnect, viewHolder.btnConnect);
			convertView.setTag(ivIsonline, viewHolder.ivIsonline);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL(DATA.allDoctors.get(position).image, R.drawable.icon_call_screen, viewHolder.ivDoctor);
		
		viewHolder.tvDoctorName.setText(DATA.allDoctors.get(position).fName+" "+DATA.allDoctors.get(position).lName);
		//viewHolder.tvDoctorDesig.setTag(DATA.allDoctors.get(position).designation);

		viewHolder.tvDoctorName.setTag(DATA.allDoctors.get(position).fName+" "+DATA.allDoctors.get(position).lName);
		//viewHolder.tvDoctorDesig.setTag(DATA.allDoctors.get(position).designation);

		if (DATA.allDoctors.get(position).current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
			viewHolder.tvDoctorDesig.setText("Doctor");
		} else {
			viewHolder.tvDoctorDesig.setText(DATA.allDoctors.get(position).speciality_name);
		}
		if (DATA.allDoctors.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}
		return convertView;
	}
}
