package com.app.priorityone_dr.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.priorityone_dr.ActivityAddDoctor;
import com.app.priorityone_dr.R;
import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.api.CustomSnakeBar;
import com.app.priorityone_dr.model.DoctorInviteBean;
import com.app.priorityone_dr.util.DATA;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvitesByDoctorsAdapter extends ArrayAdapter<DoctorInviteBean> implements ApiCallBack{

	Activity activity;
	ApiCallBack apiCallBack;
	ArrayList<DoctorInviteBean> doctorInviteBeans;
	CustomSnakeBar customSnakeBar;

	public InvitesByDoctorsAdapter(Activity activity,ArrayList<DoctorInviteBean> doctorInviteBeans) {
		super(activity, R.layout.invites_by_doctors_row, doctorInviteBeans);

		this.activity = activity;
		apiCallBack = this;
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
		this.doctorInviteBeans = doctorInviteBeans;
	}

	static class ViewHolder {
		CircularImageView ivDoctor;
		TextView tvDoctorName,tvDoctorDesig;
		TextView tvAccept,tvReject;
		//ImageView ivIsonline;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.invites_by_doctors_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivDoctor = (CircularImageView) convertView.findViewById(R.id.ivDoctor);
			viewHolder.tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
			viewHolder.tvDoctorDesig = (TextView) convertView.findViewById(R.id.tvDoctorDesig);
			viewHolder.tvAccept = (TextView) convertView.findViewById(R.id.tvAccept);
			viewHolder.tvReject = (TextView) convertView.findViewById(R.id.tvReject);
			//viewHolder.ivIsonline = (ImageView) convertView.findViewById(ivIsonline);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDoctorName, viewHolder.tvDoctorName);
			convertView.setTag(R.id.tvDoctorDesig, viewHolder.tvDoctorDesig);
			convertView.setTag(R.id.ivDoctor, viewHolder.ivDoctor);
			convertView.setTag(R.id.tvReject, viewHolder.tvReject);
			convertView.setTag(R.id.tvReject, viewHolder.tvReject);
			//convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
			
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		DATA.loadImageFromURL(doctorInviteBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivDoctor);
		
		viewHolder.tvDoctorName.setText(doctorInviteBeans.get(position).first_name+" "+doctorInviteBeans.get(position).last_name);
		viewHolder.tvDoctorDesig.setText(doctorInviteBeans.get(position).dateof);

		viewHolder.tvDoctorName.setTag(doctorInviteBeans.get(position).first_name+" "+doctorInviteBeans.get(position).last_name);
		viewHolder.tvDoctorDesig.setTag(doctorInviteBeans.get(position).dateof);


		/*if (DATA.allDoctors.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}*/

		viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inviteResponce(doctorInviteBeans.get(position).id,"accept");
			}
		});
		viewHolder.tvReject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inviteResponce(doctorInviteBeans.get(position).id,"reject");
			}
		});
		return convertView;
	}

	private void inviteResponce(final String invite_id, final String status){
		AlertDialog alertDialog =
				new AlertDialog.Builder(activity).setTitle("Confirm")
						.setMessage("Are you sure? Do you want to "+status+" the doctor invite")
						.setPositiveButton(status, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								RequestParams params = new RequestParams();
								params.put("invite_id", invite_id);
								params.put("status", status);

								ApiManager apiManager = new ApiManager(ApiManager.INVITE_RESPONCE,"post",params,apiCallBack, activity);
								apiManager.loadURL();
							}
						}).setNegativeButton("Cancel",null).create();
		alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {

			}
		});
		alertDialog.show();
	}


	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.INVITE_RESPONCE)){
			//result: {"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					((ActivityAddDoctor)activity).getInvites();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
