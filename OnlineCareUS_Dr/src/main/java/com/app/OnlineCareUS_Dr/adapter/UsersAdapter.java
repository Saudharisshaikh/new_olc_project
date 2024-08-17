package com.app.OnlineCareUS_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareUS_Dr.R;
import com.app.OnlineCareUS_Dr.model.DoctorsModel;
import com.app.OnlineCareUS_Dr.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class UsersAdapter extends ArrayAdapter<DoctorsModel> {

	Activity activity;

	TextView tvUsersRowStatus,tvUsersRowName,tvDocOrSp,tvZipcode;

	ImageView imgUsersRow,imgUserStatus,ivIsonline;
	//ProgressBar progress;
	CheckBox checkSelectContact;

	/*DisplayImageOptions options;
	ImageLoader imageLoader;*/

	ArrayList<DoctorsModel> doctorsModelsOrg;

	public UsersAdapter(Activity activity) {
		super(activity, R.layout.doctors_list_row, DATA.allDoctors);
		this.activity = activity;
		doctorsModelsOrg = new ArrayList<DoctorsModel>();
		doctorsModelsOrg.addAll(DATA.allDoctors);
		
		/*options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_my_profile)
		.showImageForEmptyUri(R.drawable.ic_my_profile)
		.showImageOnFail(R.drawable.ic_my_profile)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		imageLoader = ImageLoader.getInstance();*/
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.doctors_list_row, null);

		tvUsersRowStatus = (TextView) convertView.findViewById(R.id.tvUsersRowStatus);
		tvUsersRowName = (TextView) convertView.findViewById(R.id.tvUsersRowName);
		tvDocOrSp = (TextView) convertView.findViewById(R.id.tvDocOrSp);
		tvZipcode = (TextView) convertView.findViewById(R.id.tvZipcode);

		imgUserStatus = (ImageView) convertView.findViewById(R.id.imgUserStatus);
		imgUsersRow = (ImageView) convertView.findViewById(R.id.imgUsersRow);
		ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
		//progress = (ProgressBar) convertView.findViewById(R.id.progress);

		//UrlImageViewHelper.setUrlDrawable(imgUsersRow, DATA.allDoctors.get(position).image, R.drawable.icon_dummy);
		
		/*imageLoader.displayImage(DATA.allDoctors.get(position).image, imgUsersRow, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progress.setProgress(0);
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progress.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current,
					int total) {
				progress.setProgress(Math.round(100.0f * current / total));
			}
		}
				);*/



		if(DATA.allDoctors!=null){

			DATA.loadImageFromURL(DATA.allDoctors.get(position).image,R.drawable.icon_call_screen,imgUsersRow);

			tvUsersRowName.setText(DATA.allDoctors.get(position).fName + " "+DATA.allDoctors.get(position).lName);

			if (DATA.allDoctors.get(position).current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
				tvUsersRowStatus.setText("Doctor");
			} else {
				tvUsersRowStatus.setText(DATA.allDoctors.get(position).speciality_name);
			}
			if (DATA.allDoctors.get(position).is_online.equals("1")) {
				ivIsonline.setImageResource(R.drawable.icon_online);
			}else{
				ivIsonline.setImageResource(R.drawable.icon_notification);
			}

			//tvUsersRowStatus.setText("Designation: "+DATA.allDoctors.get(position).designation);
			tvDocOrSp.setText(DATA.allDoctors.get(position).current_app);
			tvZipcode.setText("Zipcode: "+DATA.allDoctors.get(position).zip_code);
		}else{
			Toast.makeText(activity, "alldoc null", Toast.LENGTH_SHORT).show();
		}

//		if(DATA.ClientOrAgentArray.get(position).id.equals("0")) {
//			imgUserStatus.setVisibility(View.GONE);
//		}
//		else {
//			imgUserStatus.setVisibility(View.VISIBLE);
//			
//		}
//		
//		if(DATA.ClientOrAgentArray.get(position).isLoggedIn.equals("1")) {
//			
//			imgUserStatus.setImageResource(R.drawable.icon_online);
//		}
//		else {
//			imgUserStatus.setImageResource(R.drawable.icon_notification);			
//		}

//		imgUsersRow.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//			
//				DATA.selectedUserId = DATA.ClientOrAgentArray.get(position).id;
//				DATA.selectedUsePaymentInfo = DATA.ClientOrAgentArray.get(position).paymentInfo;
//				DATA.selectedUserFname = DATA.ClientOrAgentArray.get(position).fName;
//				DATA.selectedUserLname = DATA.ClientOrAgentArray.get(position).lName;
//				DATA.selectedUserImgLink = DATA.ClientOrAgentArray.get(position).imgLink;
//				DATA.selectedUserMood = DATA.ClientOrAgentArray.get(position).mood;
//
//				Intent intent = new Intent(activity, ProfileActivity.class);
//				activity.startActivity(intent);
//			}
//		});

		return convertView;
	}


	public void filter(String filterText) {

		DATA.allDoctors.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		DATA.print("---doctorsModelsOrg size: "+doctorsModelsOrg.size());
		if(filterText.length() == 0) {
			DATA.allDoctors.addAll(doctorsModelsOrg);
		} else {
			for(DoctorsModel temp :doctorsModelsOrg) {
				if(temp.zip_code.toLowerCase(Locale.getDefault()).contains(filterText) ||
						temp.fName.toLowerCase(Locale.getDefault()).contains(filterText)||
						temp.lName.toLowerCase(Locale.getDefault()).contains(filterText)) {
					DATA.allDoctors.add(temp);
				}
			}
		}
		notifyDataSetChanged();

	}//end filter

}
