package com.app.mdlive_dr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.mdlive_dr.adapter.CallLogsEncounterAdapter;
import com.app.mdlive_dr.api.ApiManager;
import com.app.mdlive_dr.model.CallLogBean;
import com.app.mdlive_dr.model.CallLogEncounterBean;
import com.app.mdlive_dr.util.CheckInternetConnection;
import com.app.mdlive_dr.util.CustomToast;
import com.app.mdlive_dr.util.DATA;
import com.app.mdlive_dr.util.DialogCallDetails;
import com.app.mdlive_dr.util.OpenActivity;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCallLogsEncounters extends BaseActivity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;

	TextView tvNoCalls;
	SwipeRefreshLayout srCallLog;
	PagingListView lvCallLogs;

	ArrayList<CallLogEncounterBean> callLogEncounterBeans = new ArrayList<>();
	CallLogsEncounterAdapter callLogsEncounterAdapter;
	//CallLogBean selectedBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_logs);

		if(getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("Encounters");
			//GetLiveCareFormBhealth.assessmentsAdded.add(getSupportActionBar().getTitle()+"");
		}

		activity = ActivityCallLogsEncounters.this;
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		tvNoCalls =  findViewById(R.id.tvNoCalls);
		srCallLog =  findViewById(R.id.srCallLog);
		lvCallLogs =  findViewById(R.id.lvCallLogs);
		//callLogBeans = new ArrayList<CallLogBean>();



		lvCallLogs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				try {
					//selectedBean = callLogBeans.get(position);

					CallLogEncounterBean callLogEncounterBean = callLogEncounterBeans.get(position);

					CallLogBean callLogBean = new CallLogBean();

					try {callLogBean.first_name = callLogEncounterBean.patient_name.split("\\s+")[0];}catch (Exception e){e.printStackTrace();}
					try {callLogBean.last_name = callLogEncounterBean.patient_name.split("\\s+")[1];}catch (Exception e){e.printStackTrace();}
					callLogBean.patient_id = callLogEncounterBean.patient_id;
					callLogBean.image = callLogEncounterBean.image;
					callLogBean.callto = "patient";//callLogEncounterBean.callto1;
					callLogBean.callto1 = "patient";//callLogEncounterBean.callto1;
					callLogBean.phone = callLogEncounterBean.phone;
					callLogBean.residency = callLogEncounterBean.residency;
					callLogBean.city = callLogEncounterBean.city;
					callLogBean.state = callLogEncounterBean.state;
					callLogBean.id = callLogEncounterBean.call_id;

					callLogBean.dateof = callLogEncounterBean.dateof;
					callLogBean.dateof2 = callLogEncounterBean.dateof2;
					callLogBean.end_time = callLogEncounterBean.dateof2;
					callLogBean.duration = callLogEncounterBean.duration;
					callLogBean.last_seen = callLogEncounterBean.last_seen;
					callLogBean.is_online = callLogEncounterBean.is_online;

					new DialogCallDetails(activity).initDetailsDialog(callLogBean);
				}catch (Exception e){e.printStackTrace();}
			}
		});


		//===========Paging Listview starts===========================

		callLogsEncounterAdapter = new CallLogsEncounterAdapter(activity,callLogEncounterBeans);
		lvCallLogs.setAdapter(callLogsEncounterAdapter);

		lvCallLogs.setPagingableListener(new PagingListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {
				//System.out.println("-- pagging listener called at page :"+page);
				pageNoAllCalls = pageNoAllCalls+1;
				getCallLogs(pageNoAllCalls);
			}
		});

		//=============Paging Listview Ends============================


		//======================swip to refresh==================================
		//mySwipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh);
		int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
		srCallLog.setColorSchemeColors(colorsArr);
		srCallLog.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.i("--", "onRefresh called from SwipeRefreshLayout");
						if(!checkInternetConnection.isConnectedToInternet()){
							srCallLog.setRefreshing(false);
						}else {
							//toggleViews(true);
						}
						getCallLogs(0);
					}
				}
		);
		//======================swip to refresh ends=============================



		if (checkInternetConnection.isConnectedToInternet()) {
			getCallLogs(0);
		} else {
			customToast.showToast("Please check internet connection", 0, 0);
		}
	}


	int pageNoAllCalls = 0;
	private void getCallLogs(int pageNo) {
		pageNoAllCalls = pageNo;
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		ApiManager.shouldShowPD = false;
		//ApiManager apiManager = new ApiManager(ApiManager.CALL_HISTORY+"/"+pageNo+"/"+prefs.getString("id", ""),"get",params,apiCallBack, activity);
		ApiManager apiManager = new ApiManager(ApiManager.CALL_HISTORY_ENCOUNTERS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}//end getCallLogs




	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.contains(ApiManager.CALL_HISTORY_ENCOUNTERS)){


			try {

				srCallLog.setRefreshing(false);

				JSONObject jsonObject = new JSONObject(content);
				JSONArray call_logs = jsonObject.getJSONArray("data");

				Type listType = new TypeToken<ArrayList<CallLogEncounterBean>>() {}.getType();
				ArrayList<CallLogEncounterBean> callLogBeansAPI = gson.fromJson(call_logs.toString(), listType);

				if(callLogBeansAPI != null){
					if(pageNoAllCalls == 0){
						callLogEncounterBeans.clear();
					}
					callLogEncounterBeans.addAll(callLogBeansAPI);
					callLogsEncounterAdapter.notifyDataSetChanged();


					//tell listview to load more
					boolean view_more = false;//!callLogBeansAPI.isEmpty();//jsonObject.optBoolean("view_more");
					lvCallLogs.setHasMoreItems(view_more);
					lvCallLogs.onFinishLoading(view_more, callLogEncounterBeans);
				}


				int vis = callLogEncounterBeans.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoCalls.setVisibility(vis);

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}

			//============================================Lagacy Code==============================================================
			/*try {
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
				CallLogsAdapter adapter = new CallLogsAdapter(activity, callLogBeans);
				lvCallLogs.setAdapter(adapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
}
