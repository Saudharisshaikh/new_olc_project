package com.app.mhcsn_spe;

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

import com.app.mhcsn_spe.adapter.TelemedicineAdapter;
import com.app.mhcsn_spe.api.ApiManager;
import com.app.mhcsn_spe.model.TelemedicineCatData;
import com.app.mhcsn_spe.model.TelemedicineCategories;
import com.app.mhcsn_spe.util.CheckInternetConnection;
import com.app.mhcsn_spe.util.CustomToast;
import com.app.mhcsn_spe.util.DATA;
import com.app.mhcsn_spe.util.GloabalMethods;
import com.app.mhcsn_spe.util.OpenActivity;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityTelemedicineServicesEdit extends AppCompatActivity implements OnClickListener {

	Activity activity;
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

	ArrayList<TelemedicineCategories> telemedicineCategories;
	public void getTelemedicineServices() {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		String reqURL = DATA.baseUrl+"getTelemedicineServices";

		System.out.println("-- Request : "+reqURL);
		//System.out.println("-- params : "+params.toString());

		client.post(reqURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--reaponce in getTelemedicineServices "+content);

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
								}else{
									isSelected = false;
								}
								telemedicineCatDataTEMP = new TelemedicineCatData(category_name1, hcpcs_code, service_name, category_id, non_fac_fee,isSelected,service_id);
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
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
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

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_skip, menu);//menu_soap_notes

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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		System.out.println("-- onclick id: "+view.getId());
		if (view.getId() == R.id.btnDone) {
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
}
