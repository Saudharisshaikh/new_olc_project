package com.app.amnm_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.amnm_uc.R;
import com.app.amnm_uc.SubUsersList;
import com.app.amnm_uc.api.ApiCallBack;
import com.app.amnm_uc.api.ApiManager;
import com.app.amnm_uc.model.SubUsersModel;
import com.app.amnm_uc.util.DATA;
import com.app.amnm_uc.util.SharedPrefsHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class SubUsersAdapter extends ArrayAdapter<SubUsersModel> implements ApiCallBack{
	
	Activity activity;

	SharedPrefsHelper sharedPrefsHelper;
	
	TextView tvUsersRowStatus1,tvUsersRowName1,tvSetPrimary,tvContinue;
	
	ImageView imgUsersRow1,imgUserStatus1;



	public SubUsersAdapter(Activity activity) {
		super(activity, R.layout.subusers_list_row, DATA.allSubUsers);
		this.activity = activity;
		this.sharedPrefsHelper = SharedPrefsHelper.getInstance();
	}	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.subusers_list_row, null);

		tvUsersRowStatus1 = (TextView) convertView.findViewById(R.id.tvUsersRowStatus1);
		tvUsersRowName1 = (TextView) convertView.findViewById(R.id.tvUsersRowName1);
		tvSetPrimary = (TextView) convertView.findViewById(R.id.tvSetPrimary);
		tvContinue = (TextView) convertView.findViewById(R.id.tvContinue);
		imgUserStatus1 = (ImageView) convertView.findViewById(R.id.imgUserStatus1);
		imgUsersRow1 = (ImageView) convertView.findViewById(R.id.imgUsersRow1);

		DATA.loadImageFromURL(DATA.allSubUsers.get(position).image, R.drawable.icon_call_screen, imgUsersRow1);
		tvUsersRowName1.setText(DATA.allSubUsers.get(position).firstName + " " + DATA.allSubUsers.get(position).lastName);
		tvUsersRowStatus1.setText(DATA.allSubUsers.get(position).relationship);

		if(activity instanceof SubUsersList){
			if(position == 0){
				tvSetPrimary.setVisibility(View.GONE);

				if(((SubUsersList) activity).isFromHome){
					tvContinue.setVisibility(View.GONE);
				}else {
					tvContinue.setVisibility(View.VISIBLE);
				}
			}else {
				tvSetPrimary.setVisibility(View.VISIBLE);
				tvContinue.setVisibility(View.GONE);
			}
		}else{
			tvSetPrimary.setVisibility(View.GONE);
			tvContinue.setVisibility(View.VISIBLE);
		}
		if(DATA.allSubUsers.get(position).is_primary.equalsIgnoreCase("1")){
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
				params.put("primary_patient_id", DATA.allSubUsers.get(position).id);

				ApiManager apiManager = new ApiManager(ApiManager.ADD_PRIMARY_PATIENT,"post",params,SubUsersAdapter.this, activity);
				apiManager.loadURL();
			}
		});
		
		return convertView;
	}

	@Override
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
	}
}
