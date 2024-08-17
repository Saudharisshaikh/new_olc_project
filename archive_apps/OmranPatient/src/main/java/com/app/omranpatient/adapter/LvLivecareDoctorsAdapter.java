package com.app.omranpatient.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.omranpatient.ActivityDoctorInfo;
import com.app.omranpatient.ActivityUrgentCareDoc;
import com.app.omranpatient.MapActivity;
import com.app.omranpatient.R;
import com.app.omranpatient.model.NearbyDoctorBean;
import com.app.omranpatient.paypal.PaymentLiveCare;
import com.app.omranpatient.util.CheckInternetConnection;
import com.app.omranpatient.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class LvLivecareDoctorsAdapter extends ArrayAdapter<NearbyDoctorBean> {

	Activity activity;
	ArrayList<NearbyDoctorBean> allLatlongs, allLatlongsOrig;

	CheckInternetConnection connection;

	SharedPreferences prefs;

	public LvLivecareDoctorsAdapter(Activity activity , ArrayList<NearbyDoctorBean> allLatlongs) {
		super(activity, R.layout.lv_livecare_doctors_row, allLatlongs);

		this.activity = activity;
		this.allLatlongs = allLatlongs;

		this.allLatlongsOrig = new ArrayList<>();
		this.allLatlongsOrig.addAll(allLatlongs);

		connection = new CheckInternetConnection(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
	}

	static class ViewHolder {

		TextView tvDrName,tvDrDesignation;
		ImageView ivDr;
		Button btnRequest, btnDetails;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_livecare_doctors_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvDrName = (TextView) convertView.findViewById(R.id.tvDrNameLivecare);
			viewHolder.tvDrDesignation = (TextView) convertView.findViewById(R.id.tvHospitalVicinity);
			viewHolder.ivDr = (ImageView) convertView.findViewById(R.id.ivDrLivecare);

			viewHolder.btnRequest = (Button) convertView.findViewById(R.id.btnRequest);
			viewHolder.btnDetails = (Button) convertView.findViewById(R.id.btnDetails);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDrNameLivecare, viewHolder.tvDrName);
			convertView.setTag(R.id.tvHospitalVicinity, viewHolder.tvDrDesignation);
			convertView.setTag(R.id.ivDrLivecare, viewHolder.ivDr);
			convertView.setTag(R.id.btnRequest, viewHolder.btnRequest);
			convertView.setTag(R.id.btnDetails, viewHolder.btnDetails);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvDrName.setText(allLatlongs.get(position).getFirst_name()+" "+allLatlongs.get(position).getLast_name());
		//viewHolder.imgCatIcon.setImageResource(DATA.allCategories.get(position).catIcon);


		viewHolder.tvDrName.setTag(allLatlongs.get(position).getFirst_name()+" "+allLatlongs.get(position).getLast_name());
		//	viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);

		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivDr, allLatlongs.get(position).getImage(), R.drawable.icon_call_screen);

		DATA.loadImageFromURL(allLatlongs.get(position).getImage(), R.drawable.icon_call_screen, viewHolder.ivDr);


		/*if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
			btnRequest.setVisibility(View.GONE);
		}else{
			btnRequest.setVisibility(View.VISIBLE);
		}*/

		viewHolder.btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NearbyDoctorBean bean = allLatlongs.get(position);
				DATA.doctorIdForLiveCare = bean.getId();
				activity.startActivity(new Intent(activity,PaymentLiveCare.class));
				activity.finish();
			}
		});

		viewHolder.btnDetails.setVisibility(View.GONE);
		viewHolder.btnDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (connection.isConnectedToInternet()) {
					Intent intent = new Intent(activity,ActivityDoctorInfo.class);
					intent.putExtra("doctor_id", allLatlongs.get(position).getId());
					activity.startActivity(intent);
				} else {
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
				}

			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(activity, "fdffdf", 0).show();

				NearbyDoctorBean currentbean = allLatlongs.get(position);
				ArrayList<NearbyDoctorBean> curentbeanlist = new ArrayList<NearbyDoctorBean>();
				curentbeanlist.add(currentbean);


				if (activity instanceof MapActivity) {
					((MapActivity)activity).updateMap(curentbeanlist);
				} else if (activity instanceof ActivityUrgentCareDoc){
					((ActivityUrgentCareDoc)activity).updateMap(curentbeanlist);
				}

			}
		});
		viewHolder.tvDrDesignation.setText("OnlineCare Physician");
		viewHolder.tvDrDesignation.setTextColor(activity.getResources().getColor(R.color.theme_red));
		return convertView;
	}


	public void filter(String filterText) {
		allLatlongs.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		System.out.println("-- allLatlongsOrig size: "+allLatlongsOrig.size());
		if(filterText.length() == 0) {
			allLatlongs.addAll(allLatlongsOrig);
		} else {
			for(NearbyDoctorBean temp : allLatlongsOrig) {
				if(temp.getFirst_name().toLowerCase(Locale.getDefault()).contains(filterText) || temp.getLast_name().toLowerCase(Locale.getDefault()).contains(filterText)) {
					allLatlongs.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}
