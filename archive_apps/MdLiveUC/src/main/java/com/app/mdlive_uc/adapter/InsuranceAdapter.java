package com.app.mdlive_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_uc.ActivityInsurance;
import com.app.mdlive_uc.R;
import com.app.mdlive_uc.api.ApiCallBack;
import com.app.mdlive_uc.api.ApiManager;
import com.app.mdlive_uc.model.MyInsuranceBean;
import com.app.mdlive_uc.util.CustomToast;
import com.app.mdlive_uc.util.DATA;
import com.app.mdlive_uc.util.SharedPrefsHelper;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InsuranceAdapter extends ArrayAdapter<MyInsuranceBean> implements ApiCallBack{

	Activity activity;
	ArrayList<MyInsuranceBean> myInsuranceBeens;
	SharedPreferences prefs;
	CustomToast customToast;

	public InsuranceAdapter(Activity activity , ArrayList<MyInsuranceBean> myInsuranceBeens) {
		super(activity, R.layout.insurance_row, myInsuranceBeens);

		this.activity = activity;
		this.myInsuranceBeens = myInsuranceBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
//		filter(DATA.selectedDrId);
	}


	static class ViewHolder {
		TextView tvInsurance,tvPolicyNo,tvGroup,tvCode, tvAddPrimary,tvCopay,tvPolicyHolder;
		ImageView ivDeleteInsurance;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.insurance_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvInsurance = convertView.findViewById(R.id.tvInsurance);
			viewHolder.tvGroup = convertView.findViewById(R.id.tvGroup);
			viewHolder.tvPolicyNo = convertView.findViewById(R.id.tvPolicyNo);
			viewHolder.tvCode = convertView.findViewById(R.id.tvCode);
			viewHolder.tvAddPrimary = convertView.findViewById(R.id.tvAddPrimary);
			viewHolder.tvCopay = convertView.findViewById(R.id.tvCopay);
			viewHolder.tvPolicyHolder = convertView.findViewById(R.id.tvPolicyHolder);
			viewHolder.ivDeleteInsurance = convertView.findViewById(R.id.ivDeleteInsurance);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);
			convertView.setTag(R.id.tvGroup, viewHolder.tvGroup);
			convertView.setTag(R.id.tvPolicyNo, viewHolder.tvPolicyNo);
			convertView.setTag(R.id.tvCode, viewHolder.tvCode);
			convertView.setTag(R.id.tvAddPrimary, viewHolder.tvAddPrimary);
			convertView.setTag(R.id.tvCopay, viewHolder.tvCopay);
			convertView.setTag(R.id.tvPolicyHolder, viewHolder.tvPolicyHolder);
			convertView.setTag(R.id.ivDeleteInsurance, viewHolder.ivDeleteInsurance);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvInsurance.setText("Insurance : "+myInsuranceBeens.get(position).payer_name);
		viewHolder.tvGroup.setText("Group ID : "+myInsuranceBeens.get(position).insurance_group);
		viewHolder.tvPolicyNo.setText("Policy/ID number : "+myInsuranceBeens.get(position).policy_number);
		viewHolder.tvCode.setText("Code : "+myInsuranceBeens.get(position).insurance_code);
		viewHolder.tvCopay.setText("Copayment Amount : USD "+myInsuranceBeens.get(position).copay_uc);
		viewHolder.tvPolicyHolder.setText("Policy Holder : "+prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));

        viewHolder.tvCopay.setVisibility(View.GONE);

		System.out.println("-- prefs: "+prefs.getString("insurance","")+"  list val: "+myInsuranceBeens.get(position).insurance);

		if(activity instanceof ActivityInsurance){
			if (myInsuranceBeens.get(position).insurance.equalsIgnoreCase(prefs.getString("insurance",""))) {
				viewHolder.tvAddPrimary.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
				viewHolder.tvAddPrimary.setText("Primary Insurance");
			}else {
				viewHolder.tvAddPrimary.setBackgroundResource(R.drawable.btn_selector);
				viewHolder.tvAddPrimary.setText("Select as Primary Insurance");
			}

			viewHolder.ivDeleteInsurance.setVisibility(View.VISIBLE);
			viewHolder.ivDeleteInsurance.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity).setTitle("Confirm")
									.setMessage("Are you sure ? Do you want to delete this insurance ?")
									.setPositiveButton("Yes Delete", (dialogInterface, i) -> deleteInsurance(position))
									.setNegativeButton("Not Now",null)
									.create();
					alertDialog.show();
				}
			});
		}else {
			viewHolder.tvAddPrimary.setBackgroundResource(R.drawable.btn_green);
			viewHolder.tvAddPrimary.setText("Click to Validate");

			viewHolder.ivDeleteInsurance.setVisibility(View.GONE);
		}


		/*if (primaryCareBeens.get(position).is_online.equals("1")) {
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
		}*/
		
		return convertView;
	}


	int listPos = 0;
	private void deleteInsurance(int pos){

		listPos = pos;
		RequestParams params = new RequestParams();
		params.put("id", myInsuranceBeens.get(pos).id);
		ApiManager apiManager = new ApiManager(ApiManager.DELETE_INSURANCE,"post",params,this, activity);

		//ApiManager.shouldShowLoader = showLoader;

		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.DELETE_INSURANCE)){
			//{"success":1,"message":"Deleted."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.has("success")){
					customToast.showToast("Your insurance has been deleted",0,0);

					try {
						myInsuranceBeens.remove(listPos);
						notifyDataSetChanged();
						SharedPrefsHelper.getInstance().savePatientInrances(myInsuranceBeens);
						if(activity instanceof ActivityInsurance){
							((ActivityInsurance) activity).showNoDataLbls();
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
