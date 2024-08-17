package com.app.emcurama;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.app.emcurama.adapter.ReportsAdapter;
import com.app.emcurama.model.ReportsModel;
import com.app.emcurama.util.CallWebService;
import com.app.emcurama.util.ChoosePictureDialog;
import com.app.emcurama.util.DATA;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class Reports extends BaseActivity {

	ListView lvReports;
	ReportsAdapter reportsAdapter;
	ReportsModel temp;
	Button btnUploadReport;	
	private String selectedFolderId;
	SharedPreferences prefs;

	CallWebService callWebService;
	
	ProgressDialog pd;
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		DATA.isFromUploadReport = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		RequestParams params = new RequestParams();
		params.put("user_id", prefs.getString("id", ""));
		params.put("patient_id", prefs.getString("subPatientID", ""));		
		callWebService.postData("getReports", params);
		
		if(DATA.isFromUploadReport && DATA.isImageCaptured) {
			
		}
		
		
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);

		activity = Reports.this;
		
		pd = new ProgressDialog(activity);
		pd.setMessage("Loading Reports...");
		
		callWebService = new CallWebService(activity, pd);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		lvReports = (ListView) findViewById(R.id.lvReports);		
		btnUploadReport = (Button) findViewById(R.id.btnUploadReport);

		btnUploadReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final CharSequence[] items = {"Blood Suger Reports", "Ultrasounds","Other"};

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Choose Folder");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
						
						DATA.isFromUploadReport = true;
						
						Intent intent = new Intent(activity,ChoosePictureDialog.class);
						startActivity(intent);
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		//		registerForContextMenu(btnUploadReport);

		DATA.allReports = new ArrayList<ReportsModel>();

		temp = new ReportsModel();
		temp.setDateName("01/12/2015", "Blood Suger");
		DATA.allReports.add(temp);
		temp = null;

		temp = new ReportsModel();
		temp.setDateName("02/12/2015", "LFT, Liver Profile");
		DATA.allReports.add(temp);
		temp = null;

		temp = new ReportsModel();
		temp.setDateName("03/12/2015", "Abdomen Ultrasound");
		DATA.allReports.add(temp);
		temp = null;

		temp = new ReportsModel();
		temp.setDateName("05/12/2015", "Blood Suger");
		DATA.allReports.add(temp);
		temp = null;

		temp = new ReportsModel();
		temp.setDateName("06/12/2015", "LFT, Liver Profile");
		DATA.allReports.add(temp);
		temp = null;

		temp = new ReportsModel();
		temp.setDateName("07/12/2015", "Abdomen Ultrasound");
		DATA.allReports.add(temp);
		temp = null;

		reportsAdapter = new ReportsAdapter(appCompatActivity);
		lvReports.setAdapter(reportsAdapter);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Choose Folder");    
		menu.add(0, 0, 0, "Blood Suger Reports");//groupId, itemId, order, title   
		menu.add(0, 1, 0, "Ultrasounds");
		menu.add(0, 2, 0, "Other");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		int selectedItemId = item.getItemId();

		Toast.makeText(activity, ""+selectedItemId, 0).show();

		return true;


	}
}
