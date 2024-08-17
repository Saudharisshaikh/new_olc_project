package com.app.mdlive_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_uc.R;
import com.app.mdlive_uc.model.SubUsersModel;
import com.app.mdlive_uc.util.DATA;
import com.app.mdlive_uc.util.SharedPrefsHelper;

import java.util.ArrayList;

public class SubUsersAdapter2 extends ArrayAdapter<SubUsersModel>{

	Activity activity;

	SharedPrefsHelper sharedPrefsHelper;

	TextView tvUsersRowStatus1,tvUsersRowName1,tvSetPrimary,tvContinue;

	ImageView imgUsersRow1,imgUserStatus1;


	ArrayList<SubUsersModel> subUsersModels;

	public SubUsersAdapter2(Activity activity, ArrayList<SubUsersModel> subUsersModels) {
		super(activity, R.layout.subusers_list_row2, subUsersModels);
		this.activity = activity;
		this.sharedPrefsHelper = SharedPrefsHelper.getInstance();

		this.subUsersModels = subUsersModels;
	}	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.subusers_list_row2, null);

		tvUsersRowStatus1 = (TextView) convertView.findViewById(R.id.tvUsersRowStatus1);
		tvUsersRowName1 = (TextView) convertView.findViewById(R.id.tvUsersRowName1);
		tvSetPrimary = (TextView) convertView.findViewById(R.id.tvSetPrimary);
		tvContinue = (TextView) convertView.findViewById(R.id.tvContinue);
		imgUserStatus1 = (ImageView) convertView.findViewById(R.id.imgUserStatus1);
		imgUsersRow1 = (ImageView) convertView.findViewById(R.id.imgUsersRow1);

		DATA.loadImageFromURL(subUsersModels.get(position).image, R.drawable.icon_call_screen, imgUsersRow1);
		tvUsersRowName1.setText(subUsersModels.get(position).firstName + " " + subUsersModels.get(position).lastName);
		tvUsersRowStatus1.setText(subUsersModels.get(position).relationship);

		/*if(activity instanceof SubUsersList){
			if(position == 0){
				tvSetPrimary.setVisibility(View.GONE);
				tvContinue.setVisibility(View.VISIBLE);
			}else {
				tvSetPrimary.setVisibility(View.VISIBLE);
				tvContinue.setVisibility(View.GONE);
			}
		}else{
			tvSetPrimary.setVisibility(View.GONE);
			tvContinue.setVisibility(View.VISIBLE);
		}
		if(subUsersModels.get(position).is_primary.equalsIgnoreCase("1")){
			tvSetPrimary.setEnabled(false);
			tvSetPrimary.setText("Primary");
		}else {
			tvSetPrimary.setEnabled(true);
			tvSetPrimary.setText("Set as Primary");
		}

		tvSetPrimary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				params.put("patient_id", sharedPrefsHelper.getParentUser().id);
				params.put("primary_patient_id", subUsersModels.get(position).id);

				ApiManager apiManager = new ApiManager(ApiManager.ADD_PRIMARY_PATIENT,"post",params, SubUsersAdapter2.this, activity);
				apiManager.loadURL();
			}
		});*/
		
		return convertView;
	}

	/*@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.ADD_PRIMARY_PATIENT)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				//{"status":"success"}
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					if(activity instanceof SubUsersList){
						((SubUsersList)activity).loadSubPatients();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}*/
}
