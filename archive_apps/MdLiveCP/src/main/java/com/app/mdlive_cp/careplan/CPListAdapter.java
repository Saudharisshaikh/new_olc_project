package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CPListAdapter extends ArrayAdapter<CPListBean> implements ApiCallBack {

	Activity activity;
	ArrayList<CPListBean> cpListBeans;

	public CPListAdapter(Activity activity, ArrayList<CPListBean> cpListBeans) {
		super(activity, R.layout.cp_list_row, cpListBeans);

		this.activity = activity;
		this.cpListBeans = cpListBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cpListBeans.size();
	}

	static class ViewHolder {
		TextView tvAddedBy,tvDated,tvViewDetail,tvDelete,tvPMC,tvMI,tvLTSService,tvLPA,tvIfNoLifePlan,tvOutcome,tvEduMat,tvNameMat,tvFollowupDisc;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_list_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvAddedBy = convertView.findViewById(R.id.tvAddedBy);
			viewHolder.tvDated = convertView.findViewById(R.id.tvDated);
			viewHolder.tvViewDetail = convertView.findViewById(R.id.tvViewDetail);
			viewHolder.tvDelete = convertView.findViewById(R.id.tvDelete);
			viewHolder.tvPMC = convertView.findViewById(R.id.tvPMC);
			viewHolder.tvMI = convertView.findViewById(R.id.tvMI);
			viewHolder.tvLTSService = convertView.findViewById(R.id.tvLTSService);
			viewHolder.tvLPA = convertView.findViewById(R.id.tvLPA);
			viewHolder.tvIfNoLifePlan = convertView.findViewById(R.id.tvIfNoLifePlan);
			viewHolder.tvOutcome = convertView.findViewById(R.id.tvOutcome);
			viewHolder.tvEduMat = convertView.findViewById(R.id.tvEduMat);
			viewHolder.tvNameMat = convertView.findViewById(R.id.tvNameMat);
			viewHolder.tvFollowupDisc = convertView.findViewById(R.id.tvFollowupDisc);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvAddedBy, viewHolder.tvAddedBy);
			convertView.setTag(R.id.tvDated, viewHolder.tvDated);
			convertView.setTag(R.id.tvViewDetail, viewHolder.tvViewDetail);
			convertView.setTag(R.id.tvDelete, viewHolder.tvDelete);
			convertView.setTag(R.id.tvPMC, viewHolder.tvPMC);
			convertView.setTag(R.id.tvMI, viewHolder.tvMI);
			convertView.setTag(R.id.tvLTSService, viewHolder.tvLTSService);
			convertView.setTag(R.id.tvLPA, viewHolder.tvLPA);
			convertView.setTag(R.id.tvIfNoLifePlan, viewHolder.tvIfNoLifePlan);
			convertView.setTag(R.id.tvOutcome, viewHolder.tvOutcome);
			convertView.setTag(R.id.tvEduMat, viewHolder.tvEduMat);
			convertView.setTag(R.id.tvNameMat, viewHolder.tvNameMat);
			convertView.setTag(R.id.tvFollowupDisc, viewHolder.tvFollowupDisc);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvAddedBy.setText(cpListBeans.get(position).first_name+" "+cpListBeans.get(position).last_name+" ("+cpListBeans.get(position).doctor_category+")");
		viewHolder.tvDated.setText(cpListBeans.get(position).created_at);

		viewHolder.tvPMC.setText(cpListBeans.get(position).method_of_communication);
		viewHolder.tvMI.setText(cpListBeans.get(position).method_input);
		viewHolder.tvLTSService.setText(cpListBeans.get(position).preferences_delivery_services);
		viewHolder.tvLPA.setText(cpListBeans.get(position).completed_planning_activities);
		viewHolder.tvIfNoLifePlan.setText(cpListBeans.get(position).life_planning_discussed);
		viewHolder.tvOutcome.setText(cpListBeans.get(position).outcome_of_discussion);
		viewHolder.tvEduMat.setText(cpListBeans.get(position).were_educational_materials_sent);
		viewHolder.tvNameMat.setText(cpListBeans.get(position).name_of_materials);
		viewHolder.tvFollowupDisc.setText(cpListBeans.get(position).discussion_scheduled);

		View.OnClickListener onClickListener = v -> {
            switch (v.getId()){
                //case R.id.tvAddedBy:
                //break;
                case R.id.tvDelete:
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm")
                                    .setMessage("Delete care plan? Are you sure?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
											RequestParams params = new RequestParams();
											params.put("id", cpListBeans.get(position).id);
											ApiManager apiManager = new ApiManager(ApiManager.DELETE_CARE_PLAN,"post",params,CPListAdapter.this::fetchDataCallback, activity);
											apiManager.loadURL();
                                        }
                                    })
                                    .setNegativeButton("Cancel",null)
                                    .create();
                    alertDialog.show();
                    break;
                default:
                    break;
            }
        };
		//viewHolder.tvViewDetail.setOnClickListener(onClickListener);
		viewHolder.tvDelete.setOnClickListener(onClickListener);

		return convertView;
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.DELETE_CARE_PLAN)){
			//{"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					((ActivityCarePlan)activity).customToast.showToast("Care plan has been deleted",0,0);
					((ActivityCarePlan)activity).loadCarePlan();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				((ActivityCarePlan)activity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
