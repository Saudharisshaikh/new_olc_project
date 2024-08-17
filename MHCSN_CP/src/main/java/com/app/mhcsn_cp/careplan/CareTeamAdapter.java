package com.app.mhcsn_cp.careplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DoctorsModel;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class CareTeamAdapter extends ArrayAdapter<CareTeamBean> implements ApiCallBack{

	Activity activity;
	ArrayList<CareTeamBean> careTeamBeans;

	public CareTeamAdapter(Activity activity, ArrayList<CareTeamBean> careTeamBeans) {
		super(activity, R.layout.careteam_row, careTeamBeans);
		this.activity = activity;
		this.careTeamBeans = careTeamBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return careTeamBeans.size();
	}


	static class ViewHolder {
		ImageView ivCT,ivIsOnline,ivCall,ivMsg,ivDeleteTM;
		TextView tvCTName,tvCTMobile;
		LinearLayout layCallMsg;
		//RelativeLayout layCall,layMsg;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.careteam_row, null);
			
			viewHolder = new ViewHolder();

			viewHolder.ivCT = convertView.findViewById(R.id.ivCT);
			viewHolder.ivIsOnline = convertView.findViewById(R.id.ivIsOnline);
			viewHolder.tvCTName = convertView.findViewById(R.id.tvCTName);
			viewHolder.tvCTMobile = convertView.findViewById(R.id.tvCTMobile);
			viewHolder.ivCall = convertView.findViewById(R.id.ivCall);
			viewHolder.ivMsg = convertView.findViewById(R.id.ivMsg);
			viewHolder.layCallMsg = convertView.findViewById(R.id.layCallMsg);
			viewHolder.ivDeleteTM = convertView.findViewById(R.id.ivDeleteTM);
			//viewHolder.layCall = (RelativeLayout) convertView.findViewById(R.id.layCall);
			//viewHolder.layMsg = (RelativeLayout) convertView.findViewById(R.id.layMsg);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.ivCT, viewHolder.ivCT);
			convertView.setTag(R.id.ivIsOnline, viewHolder.ivIsOnline);
			convertView.setTag(R.id.tvCTMobile, viewHolder.tvCTMobile);
			convertView.setTag(R.id.ivCall, viewHolder.ivCall);
			convertView.setTag(R.id.ivMsg, viewHolder.ivMsg);
			convertView.setTag(R.id.layCallMsg, viewHolder.layCallMsg);
			convertView.setTag(R.id.ivDeleteTM, viewHolder.ivDeleteTM);
			//convertView.setTag(R.id.layCall, viewHolder.layCall);
			//convertView.setTag(R.id.layMsg, viewHolder.layMsg);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		if(careTeamBeans.get(position).care_team_member_type.equalsIgnoreCase(FragmentCareTeam.CTM_TYPE_OTHER)){
			viewHolder.tvCTName.setText(careTeamBeans.get(position).careteam_member_name);
			viewHolder.tvCTMobile.setText(careTeamBeans.get(position).careteam_member_phone);
			DATA.loadImageFromURL(careTeamBeans.get(position).c_image, R.drawable.an_im_vc_place_holder, viewHolder.ivCT);//ic_placeholder_2

			viewHolder.layCallMsg.setVisibility(View.INVISIBLE);
			viewHolder.ivIsOnline.setVisibility(View.INVISIBLE);
		}else {
			viewHolder.tvCTName.setText(careTeamBeans.get(position).first_name+" "+careTeamBeans.get(position).last_name);
			viewHolder.tvCTMobile.setText(careTeamBeans.get(position).mobile);
			DATA.loadImageFromURL(careTeamBeans.get(position).dr_image, R.drawable.an_im_vc_place_holder, viewHolder.ivCT);//ic_placeholder_2

			if(careTeamBeans.get(position).doctor_id.equalsIgnoreCase(((ActivityCarePlanDetail)activity).prefs.getString("id",""))){
				viewHolder.layCallMsg.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.layCallMsg.setVisibility(View.VISIBLE);
			}
			viewHolder.ivIsOnline.setVisibility(View.VISIBLE);
			if(careTeamBeans.get(position).is_online.equalsIgnoreCase("1")){
				viewHolder.ivIsOnline.setImageResource(R.drawable.ic_online);
			}else {
				viewHolder.ivIsOnline.setImageResource(R.drawable.ic_offline);
			}
		}

		View.OnClickListener onClickListener = v -> {
            switch (v.getId()){
                case R.id.ivCall:
					if(careTeamBeans.get(position).is_online.equalsIgnoreCase("1")){
						DATA.selectedDrId = careTeamBeans.get(position).doctor_id;
						//DATA.selectedUserCallId = DATA.selectedUserCallId;
						DATA.selectedDrName = careTeamBeans.get(position).first_name+ " "+careTeamBeans.get(position).last_name;
						DATA.selectedDrImage = careTeamBeans.get(position).dr_image;
						DATA.isFromDocToDoc = true;
						DATA.incomingCall = false;
						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = careTeamBeans.get(position).current_app;

						Intent myIntent1 = new Intent(activity, MainActivity.class);//SampleActivity.class
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						myIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						activity.startActivity(myIntent1);
					}else {
						((ActivityCarePlanDetail)activity).customToast.showToast("Team member is offline and can't be connected right now",0,1);
					}
                    break;
                case R.id.ivMsg:
                    DATA.selectedDrIdForNurse = careTeamBeans.get(position).doctor_id;
                    DATA.selectedDrId = careTeamBeans.get(position).doctor_id;
                    DATA.selectedDrName = careTeamBeans.get(position).first_name+ " "+careTeamBeans.get(position).last_name;
                    DATA.selectedDrImage = careTeamBeans.get(position).image;
                    DATA.isFromDocToDoc = true;

                    DATA.selectedDoctorsModel = new DoctorsModel();
                    DATA.selectedDoctorsModel.current_app = careTeamBeans.get(position).current_app;
                    new GloabalMethods(activity).initMsgDialog();
                    break;
				case R.id.ivDeleteTM:
					listPosRemove = position;
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity).setTitle("Confirm")
									.setMessage("Are you sure ? Do you want to remove this team member from the care team ?")
									.setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											RequestParams params = new RequestParams();
											params.put("id",careTeamBeans.get(position).id);
											ApiManager apiManager = new ApiManager(ApiManager.DELETE_CARE_MEMBER,"post",params,CareTeamAdapter.this, activity);
											apiManager.loadURL();
										}
									})
									.setNegativeButton("Not Now",null)
									.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {

						}
					});
					alertDialog.show();
					break;
                default:
                    break;
            }
        };
		viewHolder.ivCall.setOnClickListener(onClickListener);
		viewHolder.ivMsg.setOnClickListener(onClickListener);
		viewHolder.ivDeleteTM.setOnClickListener(onClickListener);

		return convertView;
	}


	int listPosRemove;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.DELETE_CARE_MEMBER)){
			//{"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					careTeamBeans.remove(listPosRemove);
					notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
