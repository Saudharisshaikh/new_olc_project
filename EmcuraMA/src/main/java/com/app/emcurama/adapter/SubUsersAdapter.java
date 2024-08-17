package com.app.emcurama.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurama.R;
import com.app.emcurama.model.SubUsersModel;
import com.app.emcurama.util.DATA;

public class SubUsersAdapter extends ArrayAdapter<SubUsersModel> {
	
	Activity activity;
	
	TextView tvUsersRowStatus1,tvUsersRowName1;
	
	ImageView imgUsersRow1,imgUserStatus1;
	CheckBox checkSelectContact1;


	public SubUsersAdapter(Activity activity) {
		super(activity, R.layout.subusers_list_row, DATA.allSubUsers);
		this.activity = activity;
	}	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.subusers_list_row, null);

		tvUsersRowStatus1 = (TextView) convertView.findViewById(R.id.tvUsersRowStatus1);
		tvUsersRowName1 = (TextView) convertView.findViewById(R.id.tvUsersRowName1);

		imgUserStatus1 = (ImageView) convertView.findViewById(R.id.imgUserStatus1);
		imgUsersRow1 = (ImageView) convertView.findViewById(R.id.imgUsersRow1);

		DATA.loadImageFromURL(DATA.allSubUsers.get(position).image, R.drawable.icon_call_screen, imgUsersRow1);
		
		tvUsersRowName1.setText(DATA.allSubUsers.get(position).firstName + " " + DATA.allSubUsers.get(position).lastName);
		tvUsersRowStatus1.setText(DATA.allSubUsers.get(position).relationship);

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
