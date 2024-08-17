package com.app.mhcsn_ma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.app.mhcsn_ma.adapter.CallLogsAdapter;
import com.app.mhcsn_ma.api.ApiManager;
import com.app.mhcsn_ma.model.CallLogBean;
import com.app.mhcsn_ma.util.CheckInternetConnection;
import com.app.mhcsn_ma.util.CustomToast;
import com.app.mhcsn_ma.util.DATA;
import com.app.mhcsn_ma.util.DialogCallDetails;
import com.app.mhcsn_ma.util.GloabalSocket;
import com.app.mhcsn_ma.util.OpenActivity;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCallLogs extends BaseActivity implements GloabalSocket.SocketEmitterCallBack{

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;

	TextView tvNoCalls;
	SwipeRefreshLayout srCallLog;
	PagingListView lvCallLogs;

	ArrayList<CallLogBean> callLogBeans = new ArrayList<>();
	CallLogsAdapter callLogsAdapter;
	//CallLogBean selectedBean;

	GloabalSocket gloabalSocket;

	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_logs);

		activity = ActivityCallLogs.this;
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		gloabalSocket = new GloabalSocket(activity,this);

		tvNoCalls =  findViewById(R.id.tvNoCalls);
		srCallLog =  findViewById(R.id.srCallLog);
		lvCallLogs =  findViewById(R.id.lvCallLogs);
		//callLogBeans = new ArrayList<CallLogBean>();



		lvCallLogs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				try {
					//selectedBean = callLogBeans.get(position);
					new DialogCallDetails(activity).initDetailsDialog(callLogBeans.get(position));
				}catch (Exception e){e.printStackTrace();}
			}
		});


		//===========Paging Listview starts===========================

		callLogsAdapter = new CallLogsAdapter(activity,callLogBeans);
		lvCallLogs.setAdapter(callLogsAdapter);

		lvCallLogs.setPagingableListener(new PagingListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {
				//DATA.print("-- pagging listener called at page :"+page);
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
		//params.put("doctor_id", prefs.getString("id", ""));
		ApiManager.shouldShowPD = false;
		ApiManager apiManager = new ApiManager(ApiManager.CALL_HISTORY+"/"+pageNo+"/"+prefs.getString("id", ""),"get",params,apiCallBack, activity);
		apiManager.loadURL();
	}//end getCallLogs




	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.contains(ApiManager.CALL_HISTORY)){


			try {

				srCallLog.setRefreshing(false);

				JSONObject jsonObject = new JSONObject(content);
				JSONArray call_logs = jsonObject.getJSONArray("call_logs");

				Type listType = new TypeToken<ArrayList<CallLogBean>>() {}.getType();
				ArrayList<CallLogBean> callLogBeansAPI = gson.fromJson(call_logs.toString(), listType);

				if(callLogBeansAPI != null){
					if(pageNoAllCalls == 0){
						callLogBeans.clear();
					}
					callLogBeans.addAll(callLogBeansAPI);
					callLogsAdapter.notifyDataSetChanged();


					//tell listview to load more
					boolean view_more = !callLogBeansAPI.isEmpty();//jsonObject.optBoolean("view_more");
					lvCallLogs.setHasMoreItems(view_more);
					lvCallLogs.onFinishLoading(view_more, callLogBeans);
				}


				int vis = callLogBeans.isEmpty() ? View.VISIBLE : View.GONE;
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




	@Override
	public void onSocketCallBack(String emitterResponse) {
		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("patient")){
				//Call logs list view
				if(callLogBeans != null){
					for (int i = 0; i < callLogBeans.size(); i++) {
						if(callLogBeans.get(i).getCallto().equalsIgnoreCase("patient") &&
								callLogBeans.get(i).getPatient_id().equalsIgnoreCase(id)){

							if(status.equalsIgnoreCase("login")){
								callLogBeans.get(i).is_online = "1";
							}else if(status.equalsIgnoreCase("logout")){
								callLogBeans.get(i).is_online = "0";
							}
						}else if(callLogBeans.get(i).getCallto().equalsIgnoreCase("doctor") &&
								callLogBeans.get(i).getPatient_id().equalsIgnoreCase(id)){

							if(status.equalsIgnoreCase("login")){
								callLogBeans.get(i).is_online = "1";
							}else if(status.equalsIgnoreCase("logout")){
								callLogBeans.get(i).is_online = "0";
							}
						}
					}
					if(callLogsAdapter != null){callLogsAdapter.notifyDataSetChanged();}
				}

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
