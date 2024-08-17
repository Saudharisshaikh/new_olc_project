package com.app.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.covacard.R;
import com.app.covacard.model.DoctorsModel;
import com.app.covacard.util.DATA;

public class UsersAdapter extends ArrayAdapter<DoctorsModel> {
	
	Activity activity;
	
	TextView tvUsersRowStatus,tvUsersRowName;
	
	ImageView imgUsersRow,imgUserStatus;
	CheckBox checkSelectContact;


	public UsersAdapter(Activity activity) {
		super(activity, R.layout.doctors_list_row, DATA.allDoctors);
		this.activity = activity;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.doctors_list_row, null);

		tvUsersRowStatus = (TextView) convertView.findViewById(R.id.tvUsersRowStatus);
		tvUsersRowName = (TextView) convertView.findViewById(R.id.tvUsersRowName);

		imgUserStatus = (ImageView) convertView.findViewById(R.id.imgUserStatus);
		imgUsersRow = (ImageView) convertView.findViewById(R.id.imgUsersRow);
		
		//UrlImageViewHelper.setUrlDrawable(imgUsersRow, DATA.allDoctors.get(position).image, R.drawable.ic_launcher);

		DATA.loadImageFromURL(DATA.allDoctors.get(position).image, R.drawable.icon_call_screen, imgUsersRow);
		
		tvUsersRowName.setText(DATA.allDoctors.get(position).fName + " "+DATA.allDoctors.get(position).lName);
		tvUsersRowStatus.setText(DATA.allDoctors.get(position).designation);

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

}
