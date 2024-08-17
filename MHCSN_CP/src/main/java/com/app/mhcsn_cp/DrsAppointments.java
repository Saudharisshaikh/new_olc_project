package com.app.mhcsn_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mhcsn_cp.adapters.DrAppointmentsAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DrAppointmentModel;
import com.app.mhcsn_cp.model.ReportsModel;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.Database;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.SpinnerCustom;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DrsAppointments extends Activity {

	ViewFlipper vfDrAppointments;
	ListView lvDrApptments,lvDrAwaitingAppointments;
	TextView tvNoAppointment,tvAvaitingAppoitments,tvAllAppoitments;
	SharedPreferences prefs;
	ProgressDialog pd;
	Activity activity;
	DrAppointmentsAdapter drAppointmentsAdapter;
	DrAppointmentsAdapter drAwaitingAppointmentsAdapter;
	CheckInternetConnection connection;
	CustomToast customToast;

	JSONArray reportsArray;
	RelativeLayout spinnerCont;
	SpinnerCustom spDates;
	ArrayList<String> apptmntDates;

	CheckBox cbRefer;
	Button btnReferAppointments,btnReferAppointmentsToAdmin;

	int selectedChild = 0;

	@Override
	protected void onResume() {
		if (connection.isConnectedToInternet()) {
			getDrsAppointments(prefs.getString("id", ""),"");
		} else {
			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drs_appointments);
		activity  = DrsAppointments.this;
		connection=new CheckInternetConnection(activity);
		prefs =    getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Loading...");
		pd.setCanceledOnTouchOutside(false);
		customToast = new CustomToast(activity);

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_NEW_APPOINTMENT);

		vfDrAppointments = (ViewFlipper) findViewById(R.id.vfDrAppointments);
		lvDrAwaitingAppointments = (ListView) findViewById(R.id.lvDrAwaitingAppointments);
		lvDrApptments = (ListView) findViewById(R.id.lvDrAppointments);
		tvNoAppointment = (TextView) findViewById(R.id.tvNoAppointment);
		tvAvaitingAppoitments = (TextView) findViewById(R.id.tvAvaitingAppoitments);
		tvAllAppoitments = (TextView) findViewById(R.id.tvAllAppoitments);
		cbRefer = (CheckBox) findViewById(R.id.cbRefer);
//		/prefs.getString("id", "")
		spinnerCont = (RelativeLayout) findViewById(R.id.spinnerCont);
		spDates = (SpinnerCustom) findViewById(R.id.spDates);
		btnReferAppointments = (Button) findViewById(R.id.btnReferAppointments);
		btnReferAppointmentsToAdmin = (Button) findViewById(R.id.btnReferAppointmentsToAdmin);

		lvDrApptments.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (cbRefer.isChecked()) {
					//lvDrApptments.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					//lvDrApptments.setItemChecked(position, true);
					if (DATA.drsApptmentsList.get(position).isSelected) {
						DATA.drsApptmentsList.get(position).isSelected = false;
					} else {
						DATA.drsApptmentsList.get(position).isSelected = true;
					}
					DATA.print("-- isselected "+DATA.drsApptmentsList.get(position).isSelected);
					drAppointmentsAdapter.notifyDataSetChanged();
				} else {
					DATA.drAppointmentModel = DATA.drsApptmentsList.get(position);

					DATA.selectedUserCallId = DATA.drAppointmentModel.getPatient_id();//for patient detail api GM

					//for show on assess list header - therapist apptmnt
					DATA.selectedUserCallName = DATA.drAppointmentModel.getFirst_name()+" "+DATA.drAppointmentModel.getLast_name();
					DATA.selectedUserCallImage = DATA.drAppointmentModel.getImage();
					//for show on assess list header - therapist apptmnt

					//filterReports(DATA.drAppointmentModel.getId());
					DATA.allReportsFiltered = DATA.drAppointmentModel.sharedReports;
					startActivity(new Intent(activity,DrApointmentDetail.class));
				}

			}
		});
		lvDrAwaitingAppointments.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (cbRefer.isChecked()) {
					//lvDrApptments.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					//lvDrApptments.setItemChecked(position, true);
					if (DATA.awaitingAppointments.get(position).isSelected) {
						DATA.awaitingAppointments.get(position).isSelected = false;
					} else {
						DATA.awaitingAppointments.get(position).isSelected = true;
					}
					DATA.print("-- isselected DATA.awaitingAppointments "+DATA.awaitingAppointments.get(position).isSelected);
					drAwaitingAppointmentsAdapter.notifyDataSetChanged();
				} else {
					DATA.drAppointmentModel = DATA.awaitingAppointments.get(position);

					DATA.selectedUserCallId = DATA.drAppointmentModel.getPatient_id();//for patient detail api GM

					//filterReports(DATA.drAppointmentModel.getId());
					DATA.allReportsFiltered = DATA.drAppointmentModel.sharedReports;
					startActivity(new Intent(activity,DrApointmentDetail.class));
				}

			}
		});

		cbRefer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				// TODO Auto-generated method stub
				try {
					if (selectedChild == 0) {
						drAppointmentsAdapter = new DrAppointmentsAdapter(activity, DATA.drsApptmentsList,ischecked);
						lvDrApptments.setAdapter(drAppointmentsAdapter);
						if (ischecked) {
							btnReferAppointments.setVisibility(View.VISIBLE);
							btnReferAppointmentsToAdmin.setVisibility(View.VISIBLE);
						} else {
							btnReferAppointments.setVisibility(View.GONE);
							btnReferAppointmentsToAdmin.setVisibility(View.GONE);
						}
					} else {
						drAwaitingAppointmentsAdapter = new DrAppointmentsAdapter(activity, DATA.awaitingAppointments,ischecked);
						lvDrAwaitingAppointments.setAdapter(drAwaitingAppointmentsAdapter);
						if (ischecked) {
							btnReferAppointments.setVisibility(View.VISIBLE);
							btnReferAppointmentsToAdmin.setVisibility(View.VISIBLE);
						} else {
							btnReferAppointments.setVisibility(View.GONE);
							btnReferAppointmentsToAdmin.setVisibility(View.GONE);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});


		spDates.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3,boolean isUserSelect) {
				// TODO Auto-generated method stub
				if(isUserSelect){
					if (connection.isConnectedToInternet()) {
						getDrsAppointments(prefs.getString("id", ""),apptmntDates.get(pos));
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		btnReferAppointments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (connection.isConnectedToInternet()) {

					if (selectedChild == 0) {
						DATA.selectedApptmntIdsForRefer = new ArrayList<String>();
						for (DrAppointmentModel model : DATA.drsApptmentsList) {
							if (model.isSelected) {
								DATA.selectedApptmntIdsForRefer.add(model.getId());
							}
						}
						if (!DATA.selectedApptmntIdsForRefer.isEmpty()) {
							startActivity(new Intent(activity,ReferAppointmentActivity.class));
						} else {
							customToast.showToast("Please select at least one appointment to refer",0,0);
						}
					} else {
						DATA.selectedApptmntIdsForRefer = new ArrayList<String>();
						for (DrAppointmentModel model : DATA.awaitingAppointments) {
							if (model.isSelected) {
								DATA.selectedApptmntIdsForRefer.add(model.getId());
							}
						}
						if (!DATA.selectedApptmntIdsForRefer.isEmpty()) {
							startActivity(new Intent(activity,ReferAppointmentActivity.class));
						} else {
							customToast.showToast("Please select at least one appointment to refer",0,0);
						}
					}


				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});


		btnReferAppointmentsToAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (connection.isConnectedToInternet()) {

					if (selectedChild == 0) {
						DATA.selectedApptmntIdsForRefer = new ArrayList<String>();
						for (DrAppointmentModel model : DATA.drsApptmentsList) {
							if (model.isSelected) {
								DATA.selectedApptmntIdsForRefer.add(model.getId());
							}
						}
						if (!DATA.selectedApptmntIdsForRefer.isEmpty()) {

							confirmDialog(activity, "Confirm Refer", "Are you sure? You want to refer "+DATA.selectedApptmntIdsForRefer.size()
									+" appointments to admin ?");
						} else {
							customToast.showToast("Please select at least one appointment to refer",0,0);
						}
					} else {
						DATA.selectedApptmntIdsForRefer = new ArrayList<String>();
						for (DrAppointmentModel model : DATA.awaitingAppointments) {
							if (model.isSelected) {
								DATA.selectedApptmntIdsForRefer.add(model.getId());
							}
						}
						if (!DATA.selectedApptmntIdsForRefer.isEmpty()) {

							confirmDialog(activity, "Confirm Refer", "Are you sure? You want to refer "+DATA.selectedApptmntIdsForRefer.size()
									+" appointments to admin ?");
						} else {
							customToast.showToast("Please select at least one appointment to refer",0,0);
						}
					}

				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});


		tvAvaitingAppoitments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tvAvaitingAppoitments.setBackgroundColor(getResources().getColor(R.color.theme_red));
				tvAvaitingAppoitments.setTextColor(getResources().getColor(android.R.color.white));
				tvAllAppoitments.setBackgroundColor(getResources().getColor(android.R.color.white));
				tvAllAppoitments.setTextColor(getResources().getColor(R.color.theme_red));

				selectedChild = 1;
				if (selectedChild > vfDrAppointments.getDisplayedChild()) {

					vfDrAppointments.setInAnimation(activity, R.anim.in_right);
					vfDrAppointments.setOutAnimation(activity, R.anim.out_left);
				} else {

					vfDrAppointments.setInAnimation(activity, R.anim.in_left);
					vfDrAppointments.setOutAnimation(activity, R.anim.out_right);
				}
				if (vfDrAppointments.getDisplayedChild() != selectedChild) {
					vfDrAppointments.setDisplayedChild(selectedChild);
				}


				if (cbRefer.isChecked()) {
					cbRefer.setChecked(false);
				}
			}
		});
		tvAllAppoitments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tvAvaitingAppoitments.setBackgroundColor(getResources().getColor(android.R.color.white));
				tvAvaitingAppoitments.setTextColor(getResources().getColor(R.color.theme_red));
				tvAllAppoitments.setBackgroundColor(getResources().getColor(R.color.theme_red));
				tvAllAppoitments.setTextColor(getResources().getColor(android.R.color.white));


				selectedChild = 0;
				if (selectedChild > vfDrAppointments.getDisplayedChild()) {
					vfDrAppointments.setInAnimation(activity, R.anim.in_right);
					vfDrAppointments.setOutAnimation(activity, R.anim.out_left);
				} else {
					vfDrAppointments.setInAnimation(activity, R.anim.in_left);
					vfDrAppointments.setOutAnimation(activity, R.anim.out_right);
				}
				if (vfDrAppointments.getDisplayedChild() != selectedChild) {
					vfDrAppointments.setDisplayedChild(selectedChild);
				}

				if (cbRefer.isChecked()) {
					cbRefer.setChecked(false);
				}
			}
		});

	}


	private void getDrsAppointments(String drId,final String apptmntDate) {

		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();
		params.put("doctor_id", drId);
		if (!apptmntDate.isEmpty()) {
			params.put("appointment_date", apptmntDate);
		}

		DATA.print("-- params in getDoctorAppointment "+params.toString());

		client.post(DATA.baseUrl+"getDoctorAppointment", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in getDrsAppointments : "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						String	stats = jsonObject.getString("status");
//					msg = jsonObject.getString("msg");
						DATA.drsApptmentsList = new ArrayList<DrAppointmentModel>();
						DATA.awaitingAppointments = new ArrayList<DrAppointmentModel>();
						DrAppointmentModel appointmentModel;
						if(stats.equals("success")){
							JSONArray drApptmentsArray = jsonObject.getJSONArray("appointments");

							if (drApptmentsArray.length() == 0) {
								tvNoAppointment.setVisibility(View.VISIBLE);
							} else {
								tvNoAppointment.setVisibility(View.GONE);
							}

							for (int i = 0; i < drApptmentsArray.length(); i++) {
								JSONObject apptmntObj= drApptmentsArray.getJSONObject(i);

								String id=apptmntObj.getString("id");
								//String dr_schedule_id=apptmntObj.getString("dr_schedule_id");
								String patient_id=apptmntObj.getString("patient_id");
								String sub_patient_id=apptmntObj.getString("sub_patient_id");
								String time=apptmntObj.getString("time");
								String appointment_date=apptmntObj.getString("appointment_date");
								String date=apptmntObj.getString("date");
								String payment_method=apptmntObj.getString("payment_method");
								String free_reason=apptmntObj.getString("free_reason");
								String symptom_id=apptmntObj.getString("symptom_id");
								String condition_id=apptmntObj.getString("condition_id");
								String description=apptmntObj.getString("description");
								String diagnosis=apptmntObj.getString("diagnosis");
								String treatment=apptmntObj.getString("treatment");
								String doctor_notification=apptmntObj.getString("doctor_notification");
								String patient_notification=apptmntObj.getString("patient_notification");
								String status=apptmntObj.getString("status");
								String first_name=apptmntObj.getString("first_name");
								String last_name=apptmntObj.getString("last_name");
								String symptom_name=apptmntObj.getString("symptom_name");
								String condition_name=apptmntObj.getString("condition_name");
								String image=apptmntObj.getString("image");
								String day=apptmntObj.getString("day");
								String from_time=apptmntObj.getString("from_time");
								String to_time=apptmntObj.getString("to_time");
								//  String patient_image=apptmntObj.getString("patient_image");

								String birthdate=apptmntObj.getString("birthdate");
								String gender=apptmntObj.getString("gender");
								String residency=apptmntObj.getString("residency");
								String phone=apptmntObj.getString("phone");
								String StoreName=apptmntObj.getString("StoreName");
								String PhonePrimary=apptmntObj.getString("PhonePrimary");

								appointmentModel = new DrAppointmentModel(id, patient_id, sub_patient_id, time, appointment_date, date,
										payment_method, free_reason, symptom_id, condition_id, description, diagnosis, treatment, doctor_notification,
										patient_notification, status, first_name, last_name, symptom_name, condition_name, image, image,
										day,from_time,to_time,birthdate,gender,residency,phone,StoreName,PhonePrimary);


								String reprts = apptmntObj.getString("reports");
								if (reprts.equalsIgnoreCase("null") || reprts.isEmpty()) {
									appointmentModel.sharedReports = new ArrayList<ReportsModel>();
								} else {
									reportsArray = new JSONArray(reprts);

									appointmentModel.sharedReports = new ArrayList<ReportsModel>();

									ReportsModel temp1;

									for(int j = 0; j<reportsArray.length(); j++) {

										temp1 = new ReportsModel();

										temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
										temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
										temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id
										temp1.userId = reportsArray.getJSONObject(j).getString("appointment_id");//patient_id

										appointmentModel.sharedReports.add(temp1);

										temp1 = null;
									}
								}

								DATA.drsApptmentsList.add(appointmentModel);
								if (appointmentModel.getStatus().equals("Pending")) {
									DATA.awaitingAppointments.add(appointmentModel);
								}

							}


						/*String reprts = jsonObject.getString("reports");

						reportsArray = new JSONArray(reprts);

						DATA.allReports = new ArrayList<ReportsModel>();

						ReportsModel temp1;

						for(int j = 0; j<reportsArray.length(); j++) {

							temp1 = new ReportsModel();

							temp1.name = reportsArray.getJSONObject(j).getString("file_display_name");
							temp1.url = reportsArray.getJSONObject(j).getString("report_name");//DATA.imageBaseUrl+
							temp1.patientID = reportsArray.getJSONObject(j).getString("patient_id");//patient_id
							temp1.userId = reportsArray.getJSONObject(j).getString("appointment_id");//patient_id

							DATA.allReports.add(temp1);

							temp1 = null;
						}*/

							drAppointmentsAdapter = new DrAppointmentsAdapter(activity, DATA.drsApptmentsList,false);
							lvDrApptments.setAdapter(drAppointmentsAdapter);
							cbRefer.setChecked(false);

							drAwaitingAppointmentsAdapter = new DrAppointmentsAdapter(activity, DATA.awaitingAppointments,false);
							lvDrAwaitingAppointments.setAdapter(drAwaitingAppointmentsAdapter);

							apptmntDates = new ArrayList<String>();
							//apptmntDates.add("Select date to view appointments");
							JSONArray appointment_dates = jsonObject.getJSONArray("appointment_dates");
							if (appointment_dates.length() == 0) {
								spinnerCont.setVisibility(View.GONE);
							} else {
								spinnerCont.setVisibility(View.VISIBLE);
							}
							for (int i = 0; i < appointment_dates.length(); i++) {
								String appointment_date = appointment_dates.getJSONObject(i).getString("appointment_date");
								apptmntDates.add(appointment_date);
							}

							if (apptmntDate.isEmpty()) {
								spDates.mUserActionOnSpinner = false;
								ArrayAdapter<String> spDatesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, apptmntDates);
								spDates.setAdapter(spDatesAdapter);

								for (int i = 0; i < apptmntDates.size(); i++) {
									if (!DATA.drsApptmentsList.isEmpty()) {
										if (DATA.drsApptmentsList.get(0).getAppointment_date().equalsIgnoreCase(apptmntDates.get(i))) {
											spDates.setSelection(i);
										}
									}
								}
							}

						}else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}

					} catch (JSONException e) {
						DATA.print("--online care dr exception in getDrsAppointments: "+e);
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getDoctorAppointment, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- on failure getDoctorAppointment: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}

	public void filterReports(String apptmntID) {

		DATA.allReportsFiltered = new ArrayList<ReportsModel>();
		ReportsModel temp;

		for(int i = 0; i<DATA.allReports.size(); i++) {

			temp = new ReportsModel();

			if(DATA.allReports.get(i).userId.equals(apptmntID)) {

				DATA.print("item added");

				temp.name = DATA.allReports.get(i).name;
				temp.url = DATA.allReports.get(i).url;
				temp.patientID = DATA.allReports.get(i).patientID;

				DATA.allReportsFiltered.add(temp);

				temp = null;
			}

		}

	}



	public void refferToAdmin(ArrayList<String> appointment_ids) {
		pd.show();
		String ids = "";
		for (int i = 0; i < appointment_ids.size(); i++) {
			ids = ids + appointment_ids.get(i) + ",";
		}
		ids = ids.substring(0, (ids.length()-1));

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();
		params.put("appointment_ids", ids);

		DATA.print("--params in refertoAdmin "+params.toString());

		client.post(DATA.baseUrl+"/refertoAdmin/",params ,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce in refertoAdmin: "+content);
					//{"status":"success","msg":"2 patient has been refered."}

					try {
						JSONObject jsonObject = new JSONObject(content);
						customToast.showToast(jsonObject.getString("msg"), 0, 1);
						finish();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: refertoAdmin, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure refertoAdmin: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end refferToAdmin



	public void confirmDialog(final Context context, String tittle , String content) {
		new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (connection.isConnectedToInternet()) {
							refferToAdmin(DATA.selectedApptmntIdsForRefer);
						} else {
							customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
						}
					}
				})
				.setNegativeButton("Cancel", null)
				.create()
				.show();

	}
}
