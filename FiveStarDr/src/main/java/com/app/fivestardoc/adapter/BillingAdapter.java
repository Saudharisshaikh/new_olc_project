package com.app.fivestardoc.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.fivestardoc.R;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.model.BillingBean;
import com.app.fivestardoc.util.CheckInternetConnection;
import com.app.fivestardoc.util.CustomToast;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BillingAdapter extends ArrayAdapter<BillingBean> {

	Activity activity;
	ArrayList<BillingBean> billingBeens;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;

	public BillingAdapter(Activity activity, ArrayList<BillingBean> billingBeens) {
		super(activity, R.layout.lv_billing_row, billingBeens);

		this.activity = activity;
		this.billingBeens = billingBeens;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
	}

	static class ViewHolder {
		TextView tvBillingPatientName,tvBillingHistory,tvBillingServices,tvBillingObjective,tvBillingFamily,
				tvBillingSubjective,tvBillingAssesment,tvBillingApprove,tvBillingDrname,tvViewBill,tvBillingCarePlan;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_billing_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvBillingPatientName = (TextView) convertView.findViewById(R.id.tvBillingPatientName);
			viewHolder.tvBillingHistory = (TextView) convertView.findViewById(R.id.tvBillingHistory);
			viewHolder.tvBillingServices = (TextView) convertView.findViewById(R.id.tvBillingServices);
			viewHolder.tvBillingObjective = (TextView) convertView.findViewById(R.id.tvBillingObjective);
			viewHolder.tvBillingFamily = (TextView) convertView.findViewById(R.id.tvBillingFamily);
			viewHolder.tvBillingSubjective = (TextView) convertView.findViewById(R.id.tvBillingSubjective);
			viewHolder.tvBillingAssesment = (TextView) convertView.findViewById(R.id.tvBillingAssesment);
			viewHolder.tvBillingApprove = (TextView) convertView.findViewById(R.id.tvBillingApprove);
			viewHolder.tvBillingDrname = (TextView) convertView.findViewById(R.id.tvBillingDrname);
			viewHolder.tvViewBill = (TextView) convertView.findViewById(R.id.tvViewBill);
			viewHolder.tvBillingCarePlan = (TextView) convertView.findViewById(R.id.tvBillingCarePlan);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvBillingPatientName, viewHolder.tvBillingPatientName);
			convertView.setTag(R.id.tvBillingHistory, viewHolder.tvBillingHistory);
			convertView.setTag(R.id.tvBillingServices, viewHolder.tvBillingServices);
			convertView.setTag(R.id.tvBillingObjective, viewHolder.tvBillingObjective);
			convertView.setTag(R.id.tvBillingFamily, viewHolder.tvBillingFamily);
			convertView.setTag(R.id.tvBillingSubjective, viewHolder.tvBillingSubjective);
			convertView.setTag(R.id.tvBillingAssesment, viewHolder.tvBillingAssesment);
			convertView.setTag(R.id.tvBillingApprove, viewHolder.tvBillingApprove);
			convertView.setTag(R.id.tvBillingDrname, viewHolder.tvBillingDrname);
			convertView.setTag(R.id.tvViewBill, viewHolder.tvViewBill);
			convertView.setTag(R.id.tvBillingCarePlan, viewHolder.tvBillingCarePlan);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvBillingPatientName.setText(billingBeens.get(position).patient_name);
		viewHolder.tvBillingHistory.setText(billingBeens.get(position).history);
		viewHolder.tvBillingServices.setText(billingBeens.get(position).plan);
		viewHolder.tvBillingObjective.setText(billingBeens.get(position).objective);
		viewHolder.tvBillingFamily.setText(billingBeens.get(position).family);
		viewHolder.tvBillingSubjective.setText(billingBeens.get(position).subjective);
		viewHolder.tvBillingAssesment.setText(billingBeens.get(position).assesment);
		viewHolder.tvBillingCarePlan.setText(billingBeens.get(position).care_plan);
		viewHolder.tvBillingDrname.setText("SOAP Notes by: "+billingBeens.get(position).doctor_name+" on: "+billingBeens.get(position).notes_date);

		if (billingBeens.get(position).is_approved.equals("1")){
			viewHolder.tvBillingApprove.setText("Approved");
			viewHolder.tvBillingApprove.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
		}else {
			viewHolder.tvBillingApprove.setText("Approve");
			viewHolder.tvBillingApprove.setBackgroundResource(R.drawable.btn_selector);
			viewHolder.tvBillingApprove.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (billingBeens.get(position).is_approved.equals("1")){
						customToast.showToast("This bill is already approved",0,0);
					}else{
						final Dialog dialog = new Dialog(activity);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.lay_bill_options);

						final CheckBox cbSendBillingSummarytoBillers = (CheckBox) dialog.findViewById(R.id.cbSendBillingSummarytoBillers);
						final CheckBox cbProcessTheBillingProcessDirectly = (CheckBox) dialog.findViewById(R.id.cbProcessTheBillingProcessDirectly);
						TextView btnSubmitBill = (TextView) dialog.findViewById(R.id.btnSubmitBill);

						btnSubmitBill.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								String billing_process = "0", billing_summary = "0";
								if (cbSendBillingSummarytoBillers.isChecked()) {
									billing_summary = "1";
								}
								if (cbProcessTheBillingProcessDirectly.isChecked()) {
									billing_process = "1";
								}
								if (checkInternetConnection.isConnectedToInternet()){
									approve_note(billingBeens.get(position).id,position,billing_process,billing_summary);
								}else {
									customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
								}
							}
						});

						WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
						lp.copyFrom(dialog.getWindow().getAttributes());
						lp.width = WindowManager.LayoutParams.MATCH_PARENT;
						lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
						dialog.show();
						dialog.getWindow().setAttributes(lp);
					}
				}
			});
		}

		viewHolder.tvViewBill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new GloabalMethods(activity).
						showWebviewDialog(DATA.baseUrl+ApiManager.VIEW_BILL+"/"+billingBeens.get(position).id,"View Bill");
			}
		});

		return convertView;
	}

	public void approve_note(final String note_id, final int listPos,String billing_process,String billing_summary) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("note_id", note_id);
		params.put("billing_process", billing_process);
		params.put("billing_summary", billing_summary);

		String reqURL = DATA.baseUrl+"approve_note";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in approve_note "+content);
					//{"success":1,"message":"Saved"}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")){
							customToast.showToast("Bill Approved successfully.",0,1);
							billingBeens.get(listPos).is_approved = "1";
							notifyDataSetChanged();
						}else {
							customToast.showToast("Something went wrong.",0,1);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}

}
