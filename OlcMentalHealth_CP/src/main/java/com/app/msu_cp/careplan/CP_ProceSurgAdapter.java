package com.app.msu_cp.careplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;


public class CP_ProceSurgAdapter extends ArrayAdapter<CP_ProceSurgBean> implements ApiCallBack {

	Activity activity;
	ArrayList<CP_ProceSurgBean> cp_proceSurgBeans;
	FragmentCPProcSurg fragmentCPProcSurg;
	CustomSnakeBar customSnakeBar;

	public CP_ProceSurgAdapter(Activity activity, ArrayList<CP_ProceSurgBean> cp_proceSurgBeans,FragmentCPProcSurg fragmentCPProcSurg) {
		super(activity, R.layout.cp_proce_surg_row, cp_proceSurgBeans);

		this.activity = activity;
		this.cp_proceSurgBeans = cp_proceSurgBeans;
		this.fragmentCPProcSurg = fragmentCPProcSurg;
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_proceSurgBeans.size();
	}

	static class ViewHolder {
		TextView tvCPPS_Hosp,tvCPPS_Proc_Surg,tvCPPS_Date,tvDelete,tvEdit;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_proce_surg_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPPS_Hosp = (TextView) convertView.findViewById(R.id.tvCPPS_Hosp);
			viewHolder.tvCPPS_Proc_Surg = (TextView) convertView.findViewById(R.id.tvCPPS_Proc_Surg);
			viewHolder.tvCPPS_Date = (TextView) convertView.findViewById(R.id.tvCPPS_Date);
			viewHolder.tvDelete = (TextView) convertView.findViewById(R.id.tvDelete);
			viewHolder.tvEdit = (TextView) convertView.findViewById(R.id.tvEdit);


			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPPS_Hosp, viewHolder.tvCPPS_Hosp);
			convertView.setTag(R.id.tvCPPS_Proc_Surg, viewHolder.tvCPPS_Proc_Surg);
			convertView.setTag(R.id.tvCPPS_Date, viewHolder.tvCPPS_Date);
			convertView.setTag(R.id.tvDelete, viewHolder.tvDelete);
			convertView.setTag(R.id.tvEdit, viewHolder.tvEdit);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPPS_Hosp.setText(cp_proceSurgBeans.get(position).hospital);
		viewHolder.tvCPPS_Proc_Surg.setText(cp_proceSurgBeans.get(position).procedures_surgeries);
		viewHolder.tvCPPS_Date.setText(cp_proceSurgBeans.get(position).date);

		View.OnClickListener onClickListener = view -> {
            switch (view.getId()){
                case R.id.tvEdit:
                    fragmentCPProcSurg.showAddProcDialog(cp_proceSurgBeans.get(position));
                    break;
                case R.id.tvDelete:
                	posRemove = position;
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity).setTitle("Confirm")
									.setMessage("Are you sure ? Do you want to remove this Procedure/Surgery ?")
									.setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											RequestParams params = new RequestParams();
											params.put("id", cp_proceSurgBeans.get(position).id);

											ApiManager apiManager = new ApiManager(ApiManager.DELETE_PROC_SURG,"post",params,CP_ProceSurgAdapter.this::fetchDataCallback, activity);
											apiManager.loadURL();
										}
									})
									.setNegativeButton("Now Now",null)
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
		viewHolder.tvDelete.setOnClickListener(onClickListener);
		viewHolder.tvEdit.setOnClickListener(onClickListener);

		return convertView;
	}


	int posRemove;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.DELETE_PROC_SURG)){
			//{"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					customSnakeBar.showToast("Procedure/Surgery has been removed");
					cp_proceSurgBeans.remove(posRemove);
					notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
