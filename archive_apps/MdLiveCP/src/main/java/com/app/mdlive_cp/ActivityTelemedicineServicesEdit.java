package com.app.mdlive_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.mdlive_cp.adapters.TelemedicineAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.TelemedicineCatData;
import com.app.mdlive_cp.model.TelemedicineCategories;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.OpenActivity;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ActivityTelemedicineServicesEdit extends AppCompatActivity implements OnClickListener, ApiCallBack{

	Activity activity;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;

	FloatingGroupExpandableListView lvTelemedicineData;
	StringBuilder sbSelectedTMSCodes,sbSelectedTMSCodesWithNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telemedicine_services);

		activity = ActivityTelemedicineServicesEdit.this;
		apiCallBack = this;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);


		sbSelectedTMSCodes = new StringBuilder();
		sbSelectedTMSCodesWithNames = new StringBuilder();

		lvTelemedicineData = (FloatingGroupExpandableListView) findViewById(R.id.lvTelemedicineData);

		if (checkInternetConnection.isConnectedToInternet()) {
			getTelemedicineServices();
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
		}

		Button btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(this);

	}

	public void getTelemedicineServices() {
		RequestParams params = new RequestParams();
		params.put("doctor_id",prefs.getString("id",""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_soap_notes, menu);

		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

		return true;
	}
	public static String tmsCodes = "",tmsCodesWithNames = "";
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {
			doneProceed();
		}else if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		System.out.println("-- onclick id: "+arg0.getId());
		if (arg0.getId() == R.id.btnDone) {
			doneProceed();
		}
	}


	public void doneProceed() {

		if (telemedicineCategories != null) {
			sbSelectedTMSCodes = new StringBuilder();
			sbSelectedTMSCodesWithNames = new StringBuilder();
			for (int i = 0; i < telemedicineCategories.size(); i++) {
				for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

					if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
						sbSelectedTMSCodes.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code);
						sbSelectedTMSCodes.append(",");

						sbSelectedTMSCodesWithNames.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code
								+" - "+telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_name);
						sbSelectedTMSCodesWithNames.append(",");
						sbSelectedTMSCodesWithNames.append("\n");
					}
				}
			}

			tmsCodes = sbSelectedTMSCodes.toString();
			tmsCodesWithNames = sbSelectedTMSCodesWithNames.toString();
			if (tmsCodes.isEmpty()) {
				//customToast.showToast("Please select telemedicine services", 0, 1);
				new AlertDialog.Builder(activity).setTitle("Confirm").
						setMessage("You do not selected any service. Do you want to skip ?")
						.setPositiveButton("Skip", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								tmsCodes = "";
								tmsCodesWithNames = "";
								System.out.println("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);
								//openActivity.open(ActivitySoapNotes.class, true);
								finish();
							}
						}).setNegativeButton("No",null).create().show();
			} else {
				tmsCodes = tmsCodes.substring(0, tmsCodes.length()-1);
				tmsCodesWithNames = tmsCodesWithNames.substring(0, tmsCodesWithNames.length()-1);
				System.out.println("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);
				//openActivity.open(ActivitySoapNotes.class, true);
				finish();
			}

		} else {
			System.out.println("-- telemedicineCategories list is null !");
		}

	}

	ArrayList<TelemedicineCategories> telemedicineCategories;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.GET_TELEMEDICINE_SERVICES)){

			telemedicineCategories = new ArrayList<>();
			TelemedicineCategories temp;

			try {
				JSONArray jsonArray = new JSONArray(content);

				for (int i = 0; i < jsonArray.length(); i++) {

					ArrayList<TelemedicineCatData> catData = new ArrayList<>();
					TelemedicineCatData telemedicineCatDataTEMP;

					String category_name = jsonArray.getJSONObject(i).getString("category_name");
					JSONArray data = jsonArray.getJSONObject(i).getJSONArray("data");

					for (int j = 0; j < data.length(); j++) {
						String category_name1 = data.getJSONObject(j).getString("category_name");
						String hcpcs_code = data.getJSONObject(j).getString("hcpcs_code");
						String service_name = data.getJSONObject(j).getString("service_name");
						String category_id = data.getJSONObject(j).getString("category_id");
						String non_fac_fee = data.getJSONObject(j).getString("non_fac_fee");
						String service_id = data.getJSONObject(j).getString("service_id");

						boolean isSelected = false;
						if(ActivitySoapNotesEditNew.selectedNotesBean.treatment_codes.contains(hcpcs_code)){
							isSelected = true;
						}/*else{
								isSelected = false;
							}*/
						telemedicineCatDataTEMP = new TelemedicineCatData(category_name1, hcpcs_code, service_name, category_id,
								non_fac_fee,isSelected,service_id);
						catData.add(telemedicineCatDataTEMP);
						telemedicineCatDataTEMP = null;
					}

					temp = new TelemedicineCategories(category_name, catData);
					telemedicineCategories.add(temp);

				}
				TelemedicineAdapter adapter = new TelemedicineAdapter(activity, telemedicineCategories);
				WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
				lvTelemedicineData.setAdapter(wrapperAdapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
				e.printStackTrace();
			}
		}
	}
}
