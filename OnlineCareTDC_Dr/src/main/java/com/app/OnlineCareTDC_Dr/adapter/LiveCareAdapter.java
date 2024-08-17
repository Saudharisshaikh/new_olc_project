package com.app.OnlineCareTDC_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.OnlineCareTDC_Dr.LiveCare;
import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.DoctorsModel;
import com.app.OnlineCareTDC_Dr.model.MyAppointmentsModel;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class LiveCareAdapter extends ArrayAdapter<MyAppointmentsModel> {

	Activity activity;
	ArrayList<MyAppointmentsModel> allAppointmentsOrig;

	public LiveCareAdapter(Activity activity , ArrayList<MyAppointmentsModel> allAppointmentsOrig) {
		super(activity, R.layout.my_appointments_row, allAppointmentsOrig);

		this.activity = activity;
		this.allAppointmentsOrig = new ArrayList<MyAppointmentsModel>();
		this.allAppointmentsOrig.addAll(allAppointmentsOrig);


		/*allAppointmentsOrig = new ArrayList<>();
		allAppointmentsOrig.addAll(DATA.allAppointments);*/
	}

	static class ViewHolder {

		TextView tvMyappntDate,tvPatientQueue;
		CircularImageView imgPetientLivecare;
		ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.my_appointments_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvMyappntDate = (TextView) convertView.findViewById(R.id.tvMyappntDate);
			viewHolder.tvPatientQueue = (TextView) convertView.findViewById(R.id.tvPatientQueue);
			viewHolder.imgPetientLivecare = (CircularImageView) convertView.findViewById(R.id.imgPetientLivecare);
			viewHolder.ivIsonline= (ImageView) convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvMyappntDate, viewHolder.tvMyappntDate);
			convertView.setTag(R.id.tvPatientQueue, viewHolder.tvPatientQueue);
			convertView.setTag(R.id.imgPetientLivecare, viewHolder.imgPetientLivecare);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvMyappntDate.setText(allAppointmentsOrig.get(position).first_name+" "+allAppointmentsOrig.get(position).last_name);
		viewHolder.tvMyappntDate.setTag(allAppointmentsOrig.get(position).first_name+" "+allAppointmentsOrig.get(position).last_name);

		viewHolder.tvPatientQueue.setText(""+(position+1));
		viewHolder.tvPatientQueue.setTag(""+(position+1));

		int vis = activity instanceof LiveCare ? View.VISIBLE : View.INVISIBLE;
		viewHolder.tvPatientQueue.setVisibility(vis);

		//UrlImageViewHelper.setUrlDrawable(viewHolder.imgPetientLivecare, DATA.allAppointments.get(position).image, R.drawable.icon_call_screen);
		//Picasso.with(activity).load(DATA.allAppointments.get(position).image).placeholder(R.drawable.icon_call_screen).into(viewHolder.imgPetientLivecare);
		DATA.loadImageFromURL(allAppointmentsOrig.get(position).image, R.drawable.icon_call_screen,viewHolder.imgPetientLivecare);

		if (allAppointmentsOrig.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}
//		viewHolder.imgMyappntStts.setImageResource(DATA.allAppointments.get(position).icon);
//		viewHolder.imgMyappntStts.setTag(DATA.allAppointments.get(position).icon);

//		viewHolder.imgMyappntStts.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//
//				DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
//				DATA.selectedUserCallId = DATA.allAppointments.get(position).id;
//				DATA.selectedUserCallName = DATA.allAppointments.get(position).first_name;
//
//				Intent i = new Intent(activity, VideoCallScreen.class);
//				activity.startActivity(i);
//				activity.finish();
//
////				getLiveCarePatiensList();
//			}
//		});

		return convertView;
	}


	public void filter(String filterText) {

		try {
			DATA.allAppointments.clear();
			filterText = filterText.toLowerCase(Locale.getDefault());

			DATA.print("---doctorsModelsOrg size: "+allAppointmentsOrig.size());
			if(filterText.length() == 0) {
				DATA.allAppointments.addAll(allAppointmentsOrig);
			}

			else {

				for(MyAppointmentsModel temp :allAppointmentsOrig) {

					if(temp.first_name.toLowerCase(Locale.getDefault()).startsWith(filterText) ||
							temp.last_name.toLowerCase(Locale.getDefault()).startsWith(filterText)) {

						DATA.allAppointments.add(temp);
					}

				}
			}

		/*if (allPosts.size()==0) {
			DATA.noRecordingFound = true;
		} else {
			DATA.noRecordingFound = false;
		}*/
			notifyDataSetChanged();
		}catch (Exception e){
			e.printStackTrace();
		}

	}//end filter
}
