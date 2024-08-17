package com.app.mdlive_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_dr.R;
import com.app.mdlive_dr.model.CallLogEncounterBean;
import com.app.mdlive_dr.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class CallLogsEncounterAdapter extends ArrayAdapter<CallLogEncounterBean> {

	Activity activity;
	ArrayList<CallLogEncounterBean> callLogEncounterBeans;
	//CheckInternetConnection checkInternetConnection;
	//SharedPreferences prefs;
	//CustomToast customToast;
	//int pageNo = 0;

	public CallLogsEncounterAdapter(Activity activity, ArrayList<CallLogEncounterBean> callLogEncounterBeans) {
		super(activity, R.layout.lv_call_logs_row, callLogEncounterBeans);

		this.activity = activity;
		this.callLogEncounterBeans = callLogEncounterBeans;
		//customToast = new CustomToast(activity);
		//checkInternetConnection = new CheckInternetConnection(activity);
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		CircularImageView ivCallLog;
		TextView tvCallLogName,tvCallLogTime,tvDocOrPatient;
		//TextView btnCallLogLoadMore;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_call_logs_row, null);

			viewHolder = new ViewHolder();

			viewHolder.ivCallLog = (CircularImageView) convertView.findViewById(R.id.ivCallLog);
			viewHolder.tvDocOrPatient = (TextView) convertView.findViewById(R.id.tvDocOrPatient);
			viewHolder.tvCallLogName = (TextView) convertView.findViewById(R.id.tvCallLogName);
			viewHolder.tvCallLogTime= (TextView) convertView.findViewById(R.id.tvCallLogTime);
			//viewHolder.btnCallLogLoadMore = (TextView) convertView.findViewById(R.id.btnCallLogLoadMore);
			viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivCallLog , viewHolder.ivCallLog);
			convertView.setTag(R.id.tvDocOrPatient , viewHolder.tvDocOrPatient);
			convertView.setTag(R.id.tvCallLogName, viewHolder.tvCallLogName);
			convertView.setTag(R.id.tvCallLogTime, viewHolder.tvCallLogTime);
			//convertView.setTag(R.id.btnCallLogLoadMore, viewHolder.btnCallLogLoadMore);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL( callLogEncounterBeans.get(position).image, R.drawable.icon_call_screen,viewHolder.ivCallLog);
		viewHolder.tvCallLogName.setText(callLogEncounterBeans.get(position).patient_name);
		viewHolder.tvCallLogTime.setText(callLogEncounterBeans.get(position).dateof);

		int isOnlineRes = callLogEncounterBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(isOnlineRes);


		viewHolder.tvDocOrPatient.setText("Patient");

		/*if (callLogBeans.get(position).getCallto1().equalsIgnoreCase("doctor")) {
			viewHolder.tvDocOrPatient.setText("Doctor");
		} else if (callLogBeans.get(position).getCallto1().equalsIgnoreCase("specialist")){
			String desig = "Specialist";
			if (!callLogBeans.get(position).doctor_category.isEmpty()) {
				desig = callLogBeans.get(position).doctor_category;
			}
			viewHolder.tvDocOrPatient.setText(desig);
		}else if (callLogBeans.get(position).getCallto1().equalsIgnoreCase("patients")){
			viewHolder.tvDocOrPatient.setText("Patient");
		}*/

		/*if (position == (getCount()-1)) {
			viewHolder.btnCallLogLoadMore.setVisibility(View.VISIBLE);

			viewHolder.btnCallLogLoadMore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (checkInternetConnection.isConnectedToInternet()) {
						pageNo = pageNo+1;
						getCallLogs(pageNo);
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}

				}
			});
		} else {
			viewHolder.btnCallLogLoadMore.setVisibility(View.GONE);
		}*/

		return convertView;
	}


	/*public void getCallLogs(int pageNo) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		//show loader
		DATA.showLoader(activity);

		String reqURL = DATA.baseUrl+"/call_history/"+pageNo+"/"+prefs.getString("id", "");

		System.out.println("-- Request : "+reqURL);
		//System.out.println("-- params : "+params.toString());

		client.get(reqURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseAPI) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoader();
				try{
					String content = new String(responseAPI);

					System.out.println("--reaponce in getCallLogs "+content);
					try {

						CallLogBean bean;
						JSONObject jsonObject = new JSONObject(content);
						JSONArray call_logs = jsonObject.getJSONArray("call_logs");
						for (int i = 0; i < call_logs.length(); i++) {
							String id = call_logs.getJSONObject(i).getString("id");
							String doctor_id = call_logs.getJSONObject(i).getString("doctor_id");
							String patient_id = call_logs.getJSONObject(i).getString("patient_id");
							String response = call_logs.getJSONObject(i).getString("response");
							String dateof = call_logs.getJSONObject(i).getString("dateof");
							String first_name = call_logs.getJSONObject(i).getString("first_name");
							String last_name = call_logs.getJSONObject(i).getString("last_name");
							String image = call_logs.getJSONObject(i).getString("image");
							String callto = call_logs.getJSONObject(i).getString("callto");
							String callto1 = call_logs.getJSONObject(i).getString("callto1");

							String doctor_category = call_logs.getJSONObject(i).getString("doctor_category");
							String is_online = call_logs.getJSONObject(i).getString("is_online");
							String current_app = call_logs.getJSONObject(i).getString("current_app");

							String end_time = call_logs.getJSONObject(i).optString("end_time");
							String dateof2 = call_logs.getJSONObject(i).optString("dateof2");

							bean = new CallLogBean(id, doctor_id, patient_id, response, dateof, first_name, last_name,image,callto,callto1, end_time, dateof2);
							bean.doctor_category = doctor_category;
							bean.is_online = is_online;
							bean.current_app = current_app;
							callLogBeans.add(bean);
							bean = null;
						}

						notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}

				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+responseAPI);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoader();
				try {
					String content = new String(errorResponse);
					System.out.println("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}*/
	//end getCallLogs
}
