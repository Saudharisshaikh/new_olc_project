package com.app.msu_dr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.app.msu_dr.R;
import com.app.msu_dr.adapter.DrScheduleAdapter;
import com.app.msu_dr.api.ApiManager;
import com.app.msu_dr.model.DrScheduleBean;
import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.GloabalMethods;
import com.app.msu_dr.util.OpenActivity;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class DrSchedule extends BaseActivity {

	SwipeMenuListView lvDrSchedule;
	SwipeMenuCreator creator;
	ProgressDialog pd;
	SharedPreferences prefs;
	Button btnAddSchedule;
	OpenActivity openActivity;
	ArrayList<DrScheduleBean> drScheduleBeansList;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;

	@Override
	protected void onResume() {
		if (checkInternetConnection.isConnectedToInternet()) {
			getDrSchedule(prefs.getString("id", ""));
		} else {
			Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dr_schedule);

		activity = DrSchedule.this;
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, MODE_PRIVATE);
		lvDrSchedule = (SwipeMenuListView) findViewById(R.id.lvDrSchedule);
		btnAddSchedule = (Button) findViewById(R.id.btnAddSchedule);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);






		btnAddSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openActivity.open(SetDrShedule.class, false);

			}
		});


		//===============================================================
		// step 1. create a MenuCreator
		creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
	         /* // create "open" item
	          SwipeMenuItem openItem = new SwipeMenuItem(
	                  getApplicationContext());
	          // set item background
	          openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
	                  0xCE)));
	          // set item width
	          openItem.setWidth(dp2px(90));
	          // set item title
	          openItem.setTitle("Edit");
	          // set item title fontsize
	          openItem.setTitleSize(18);
	          // set item title font color
	          openItem.setTitleColor(Color.WHITE);
	          // add to menu
	          menu.addMenuItem(openItem);*/

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
	         /* deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
	                  0x3F, 0x25)));*/
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		lvDrSchedule.setMenuCreator(creator);


		// step 2. listener item click event
		lvDrSchedule.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
				//  Toast.makeText(activity, position+"", 0).show();
				//    ApplicationInfo item = mAppList.get(position);
				switch (index) {
					case 0:
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle("Confirm")
								.setMessage("Are you sure? You want to delete this schedule.")
								.setPositiveButton("Yes delete", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (checkInternetConnection.isConnectedToInternet()) {
											deleteSchedule(drScheduleBeansList.get(position).getId());
										} else {
											customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
										}
									}
								})
								.setNegativeButton("Not Now", null)
								.create()
								.show();
						break;
	             /* case 1:
	                  // delete
	              	break;*/
				}
				return false;
			}
		});

		// set SwipeListener
		lvDrSchedule.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		//=================================================================

	}//oncreate


	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

//	{
//	    "success": 1,
//	    "message": "Success",
//	    "data": [
//	        {
//	            "id": "7",
//	            "doctor_id": "7",
//	            "day": "Tuesday",
//	            "from_time": "09:00 am",
//	            "to_time": "12:00 pm",
//	            "evening_from_time": "09:00 pm",
//	            "evening_to_time": "12:00 am"
//	        },
//	        {


	public void getDrSchedule(String doctor_id) {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);

		pd.show();

		String reqURL = DATA.baseUrl+"/getDoctorSchedule/"+doctor_id;

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.get(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce "+content);

					JSONObject mainObj;
					try {
						mainObj = new JSONObject(content);

						int success = mainObj.getInt("success");

						drScheduleBeansList = new ArrayList<DrScheduleBean>();
						DrScheduleBean drScheduleBean;

						JSONArray data = mainObj.getJSONArray("data");
						for (int i = 0; i < data.length(); i++) {
							JSONObject obj = data.getJSONObject(i);

							String id = obj.getString("id");
							String doctor_id= obj.getString("doctor_id");
							String day= obj.getString("day");
							String from_time= obj.getString("from_time");
							String to_time= obj.getString("to_time");
							String evening_from_time= obj.getString("evening_from_time");
							String evening_to_time= obj.getString("evening_to_time");

							drScheduleBean = new DrScheduleBean(id, doctor_id, day, from_time, to_time, evening_from_time, evening_to_time,"","");
							drScheduleBeansList.add(drScheduleBean);

						}
						DrScheduleAdapter drScheduleAdapter = new DrScheduleAdapter(activity,drScheduleBeansList);
						lvDrSchedule.setAdapter(drScheduleAdapter);

					} catch (JSONException e) {
						pd.dismiss();
						e.printStackTrace();
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_SHORT).show();
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
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	public void deleteSchedule(String scheduleId) {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		/*RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);*/

		pd.show();

		String reqURL = DATA.baseUrl+"/deleteSchedule/"+scheduleId;

		DATA.print("-- Request : "+reqURL);
		//DATA.print("-- params : "+params.toString());

		client.get(reqURL , new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce in deleteSchedule "+content);
					// --responce in deleteSchedule {"success":1,"message":"Deleted."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {

							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									//.setTitle("Confirm")
									.setMessage("Your schedule has been deleted!")
									.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (checkInternetConnection.isConnectedToInternet()) {
												getDrSchedule(prefs.getString("id", "0"));
											} else {
												customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
											}
										}
									})
									//.setNegativeButton("Not Now", null)
									.create()
									.show();
						} else {
							customToast.showToast(jsonObject.optString("message") , 0, 0);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
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
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}

}
