package com.app.emcurama.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurama.R;
import com.app.emcurama.model.CallLogBean;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class CallLogsAdapter2 extends ArrayAdapter<CallLogBean> {

	Activity activity;
	ArrayList<CallLogBean> callLogBeans;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	int pageNo = 0;

	public CallLogsAdapter2(Activity activity, ArrayList<CallLogBean> callLogBeans) {
		super(activity, R.layout.lv_call_logs_row2, callLogBeans);

		this.activity = activity;
		this.callLogBeans = callLogBeans;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
	}

	static class ViewHolder {
		CircularImageView ivCallLog;
		TextView tvCallLogName,tvCallLogTime,tvDocOrPatient;
		TextView btnCallLogLoadMore;
		ImageView ivIsonline;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_call_logs_row2, null);

			viewHolder = new ViewHolder();

			viewHolder.ivCallLog = (CircularImageView) convertView.findViewById(R.id.ivCallLog);
			viewHolder.tvDocOrPatient = (TextView) convertView.findViewById(R.id.tvDocOrPatient);
			viewHolder.tvCallLogName = (TextView) convertView.findViewById(R.id.tvCallLogName);
			viewHolder.tvCallLogTime= (TextView) convertView.findViewById(R.id.tvCallLogTime);
			viewHolder.btnCallLogLoadMore = (TextView) convertView.findViewById(R.id.btnCallLogLoadMore);
			viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.ivCallLog , viewHolder.ivCallLog);
			convertView.setTag(R.id.tvDocOrPatient , viewHolder.tvDocOrPatient);
			convertView.setTag(R.id.tvCallLogName, viewHolder.tvCallLogName);
			convertView.setTag(R.id.tvCallLogTime, viewHolder.tvCallLogTime);
			convertView.setTag(R.id.btnCallLogLoadMore, viewHolder.btnCallLogLoadMore);
			convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}


		DATA.loadImageFromURL( callLogBeans.get(position).pimage, R.drawable.icon_call_screen,viewHolder.ivCallLog);
		//viewHolder.tvCallLogName.setText(callLogBeans.get(position).getFirst_name()+" "+callLogBeans.get(position).getLast_name());

		viewHolder.tvCallLogName.setText(callLogBeans.get(position).patient_name);

		viewHolder.tvCallLogTime.setText("Dated : "+callLogBeans.get(position).getDateof());

		viewHolder.tvDocOrPatient.setText("Called by : "+callLogBeans.get(position).doctor_name);

		//viewHolder.tvDocOrPatient.setText(Html.fromHtml("<font color='#aaa'>Called by : </font>"+ callLogBeans.get(position).doctor_name));
		//viewHolder.tvDocOrPatient.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Report Date : </font>"+ fallListBeans.get(position).dateof));


		int isOnlineRes = callLogBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		viewHolder.ivIsonline.setImageResource(isOnlineRes);

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
						Toast.makeText(activity, "No internet connection", 0).show();
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
		//show loader
		DATA.showLoader(activity);
		client.get(DATA.baseUrl+"/call_history/"+pageNo+"/"+prefs.getString("id", ""), new AsyncHttpResponseHandler() {
			@Override

			public void onSuccess(int statusCode, String content) {
				DATA.dismissLoader();
				DATA.print("--reaponce in getCallLogs "+content);
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

						bean = new CallLogBean(id, doctor_id, patient_id, response, dateof, first_name, last_name,image,callto,callto1);
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
				}

			}

			@Override

			public void onFailure(Throwable error, String content) {
				DATA.dismissLoader();
				DATA.print("--onFailure in getCallLogs "+content);
				//Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			}
		});

	}//end getCallLogs*/
}
