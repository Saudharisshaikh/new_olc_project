package com.app.mdlive_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.DrugBean;
import com.app.mdlive_cp.model.DrugRefillBean;
import com.app.mdlive_cp.model.PotencyUnitBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ActivityRefillDetails extends AppCompatActivity {
	
	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	SharedPreferences prefs;
	CustomToast customToast;
	
	TextView tvPrescDate,tvPrescPtName,tvPrescPatientDOB,tvPrescPatientGender,tvPrescPatientPhone,tvPrescPatientAdress,
	tvPrescPharmacy,tvPrescPharmacyPhone,
	tvPrescriberDrugName,tvPrescriberDrugStrength,tvPrescriberDrugDosageForm,tvPrescriberDrugQuantity,tvPrescriberDrugDuration,tvPrescriberDrugPotencyCode,
	tvPrescriberDrugInstructions,tvPrescriberDrugRefils,
	tvPharmacyDrugName,tvPharmacyDrugStrength,tvPharmacyDrugDosageForm,tvPharmacyDrugQuantity,tvPharmacyDrugDuration,tvPharmacyDrugPotencyCode,
	tvPharmacyDrugInstructions,tvPharmacyDrugRefils,
	tvEditPharmacyDrug;
	
	EditText etVitals,etDiagnosis;
	
	Dialog dialogDenieReson;
	EditText etReason;
	String denial_reason_code = "";
	
	Button btnApprove,btnApproveWithChanges,btnDenie,btnDenieWithNewRX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refill_details);
		
		activity = ActivityRefillDetails.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		
		tvPrescDate = (TextView) findViewById(R.id.tvPrescDate);
		tvPrescPtName = (TextView) findViewById(R.id.tvPrescPtName);
		tvPrescPatientDOB = (TextView) findViewById(R.id.tvPrescPatientDOB);
		tvPrescPatientGender = (TextView) findViewById(R.id.tvPrescPatientGender);
		tvPrescPatientPhone = (TextView) findViewById(R.id.tvPrescPatientPhone);
		tvPrescPatientAdress = (TextView) findViewById(R.id.tvPrescPatientAdress);
		tvPrescPharmacy = (TextView) findViewById(R.id.tvPrescPharmacy);
		tvPrescPharmacyPhone = (TextView) findViewById(R.id.tvPrescPharmacyPhone);
		tvPrescriberDrugName = (TextView) findViewById(R.id.tvPrescriberDrugName);
		tvPrescriberDrugStrength = (TextView) findViewById(R.id.tvPrescriberDrugStrength);
		tvPrescriberDrugDosageForm = (TextView) findViewById(R.id.tvPrescriberDrugDosageForm);
		tvPrescriberDrugQuantity = (TextView) findViewById(R.id.tvPrescriberDrugQuantity);
		tvPrescriberDrugDuration = (TextView) findViewById(R.id.tvPrescriberDrugDuration);
		tvPrescriberDrugPotencyCode = (TextView) findViewById(R.id.tvPrescriberDrugPotencyCode);
		tvPrescriberDrugInstructions = (TextView) findViewById(R.id.tvPrescriberDrugInstructions);
		tvPrescriberDrugRefils = (TextView) findViewById(R.id.tvPrescriberDrugRefils);
		tvPharmacyDrugName = (TextView) findViewById(R.id.tvPharmacyDrugName);
		tvPharmacyDrugStrength = (TextView) findViewById(R.id.tvPharmacyDrugStrength);
		tvPharmacyDrugDosageForm = (TextView) findViewById(R.id.tvPharmacyDrugDosageForm);
		tvPharmacyDrugQuantity = (TextView) findViewById(R.id.tvPharmacyDrugQuantity);
		tvPharmacyDrugDuration = (TextView) findViewById(R.id.tvPharmacyDrugDuration);
		tvPharmacyDrugPotencyCode = (TextView) findViewById(R.id.tvPharmacyDrugPotencyCode);
		tvPharmacyDrugInstructions = (TextView) findViewById(R.id.tvPharmacyDrugInstructions);
		tvPharmacyDrugRefils = (TextView) findViewById(R.id.tvPharmacyDrugRefils);
		
		tvEditPharmacyDrug = (TextView) findViewById(R.id.tvEditPharmacyDrug);
		
		etVitals = (EditText) findViewById(R.id.etVitals);
		etDiagnosis = (EditText) findViewById(R.id.etDiagnosis);
		
		btnApprove = (Button) findViewById(R.id.btnApprove);
		btnApproveWithChanges = (Button) findViewById(R.id.btnApproveWithChanges);
		btnDenie = (Button) findViewById(R.id.btnDenie);
		btnDenieWithNewRX = (Button) findViewById(R.id.btnDenieWithNewRX);
		
		tvPrescDate.setText(DATA.selectedRefillBean.getDateof());
		tvPrescPtName.setText(DATA.selectedRefillBean.getFirst_name()+" "+DATA.selectedRefillBean.getLast_name());
		tvPrescPatientDOB.setText(DATA.selectedRefillBean.getBirthdate());
		if (DATA.selectedRefillBean.getGender().equals("1")) {
			tvPrescPatientGender.setText("Male");
		} else {
			tvPrescPatientGender.setText("Female");
		}
		
		tvPrescPatientPhone.setText(DATA.selectedRefillBean.getPhone());
		tvPrescPatientAdress.setText(DATA.selectedRefillBean.getResidency());
		tvPrescPharmacy.setText(DATA.selectedRefillBean.getStoreName());
		tvPrescPharmacyPhone.setText(DATA.selectedRefillBean.getPhonePrimary());
		etVitals.setText(DATA.selectedRefillBean.getVitals());
		etDiagnosis.setText(DATA.selectedRefillBean.getDiagnosis());
		
		if (checkInternetConnection.isConnectedToInternet()) {
			refillSummary(DATA.selectedRefillBean.getPrescription_id(), DATA.selectedRefillBean.getId());
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}
		
		
		tvEditPharmacyDrug.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if (checkInternetConnection.isConnectedToInternet()) {
					if (pharmacyRifillBean != null) {
						initDrugsDialog();
						getDrugs(pharmacyRifillBean.getDrug_name().split(" ")[0]);
					} else {
						customToast.showToast("pharmacyRifillBean is null", 0, 0);
					}
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
				}
			}
		});
		
		btnApprove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (pharmacyRifillBean.getRefill().equals("0")) {
					customToast.showToast("Refills should be greater than 0", 0, 1);
				} else {
					new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").setMessage("Are you sure? You want to send refill approval to pharmacy").
					setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (checkInternetConnection.isConnectedToInternet()) {
								sendRefillResponse("Approved", pharmacyRifillBean.getDrug_descriptor_id(), pharmacyRifillBean.getStart_date(),
										pharmacyRifillBean.getEnd_date(), pharmacyRifillBean.getRefill_id(), doctorRifillBean.getRefill_id(), pharmacyRifillBean.getQuantity(),
										pharmacyRifillBean.getDirections(), pharmacyRifillBean.getSelected_potency_code(), pharmacyRifillBean.getRefill());
							} else {
								customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
							}
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();
				}
				
			}
		});
		btnApproveWithChanges.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if (pharmacyRifillBean.getRefill().equals("0")) {
					customToast.showToast("Refills should be greater than 0", 0, 1);
				} else {
					new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").setMessage("Are you sure? You want to send Approved with changes to pharmacy").
					setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (checkInternetConnection.isConnectedToInternet()) {
								sendRefillResponse("Approved_With_Changes", pharmacyRifillBean.getDrug_descriptor_id(), pharmacyRifillBean.getStart_date(),
										pharmacyRifillBean.getEnd_date(), pharmacyRifillBean.getRefill_id(), doctorRifillBean.getRefill_id(), pharmacyRifillBean.getQuantity(),
										pharmacyRifillBean.getDirections(), pharmacyRifillBean.getSelected_potency_code(), pharmacyRifillBean.getRefill());
							} else {
								customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
							}
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();
				}
			}
		});
		btnDenie.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			    dialogDenieReson  = new Dialog(activity);
				dialogDenieReson.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogDenieReson.setContentView(R.layout.dialog_denie_reason);
				etReason = (EditText) dialogDenieReson.findViewById(R.id.etReason);
				Button btnSubmitDenieCode = (Button) dialogDenieReson.findViewById(R.id.btnSubmitDenieCode);
				
				LinearLayout layDenieWithNewRXReason = (LinearLayout) dialogDenieReson.findViewById(R.id.layDenieWithNewRXReason);
				layDenieWithNewRXReason.setVisibility(View.GONE);
				RelativeLayout lay_denial_reason_code = (RelativeLayout) dialogDenieReson.findViewById(R.id.lay_denial_reason_code);
				lay_denial_reason_code.setVisibility(View.VISIBLE);
				
				final ListView lv_denial_reason_code = (ListView) dialogDenieReson.findViewById(R.id.lv_denial_reason_code);		
				lv_denial_reason_code.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				lv_denial_reason_code.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						lv_denial_reason_code.setItemChecked(arg2, true);
					}
				});
				
				/*final String[] arr = {"Deniel code 1","Deniel code 2","Deniel code 3","Deniel code 4","Deniel code 5","Deniel code 6","Deniel code 7"
						,"Deniel code 8","Deniel code 9","Deniel code 10"};*/
				final ArrayList<DenielResonCodeBean> denielResonCodeBeans = new ArrayList<>();
				String denialReasonsAndCodes = prefs.getString("denialReasonsAndCodes", "");
				try {
					JSONArray jsonArray = new JSONArray(denialReasonsAndCodes);
					for (int i = 0; i < jsonArray.length(); i++) {
						 String code = jsonArray.getJSONObject(i).getString("code");
						 String reason = jsonArray.getJSONObject(i).getString("reason");
						 
						 denielResonCodeBeans.add(new DenielResonCodeBean(code, reason));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("-- exception in reason codes json");
					e.printStackTrace();
				}
				
				ArrayAdapter<DenielResonCodeBean> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, denielResonCodeBeans);
				lv_denial_reason_code.setAdapter(adapter);
				
				
				
				btnSubmitDenieCode.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						int selectedPosition =  lv_denial_reason_code.getCheckedItemPosition();
						System.out.println("-- selected denie code position "+selectedPosition);
						if (selectedPosition < 0) {
							customToast.showToast("Please select a code from the list to denie with newRX", 0, 1);
						} else {
							denial_reason_code = denielResonCodeBeans.get(selectedPosition).getCode();
							System.out.println("-- selected denial reason code: "+denial_reason_code);
							dialogDenieReson.dismiss();
							
							new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").setMessage("Are you sure? You want to Denie this refill request").
							setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if (checkInternetConnection.isConnectedToInternet()) {
										sendRefillResponse("Denied", pharmacyRifillBean.getDrug_descriptor_id(), pharmacyRifillBean.getStart_date(),
												pharmacyRifillBean.getEnd_date(), pharmacyRifillBean.getRefill_id(), doctorRifillBean.getRefill_id(), pharmacyRifillBean.getQuantity(),
												pharmacyRifillBean.getDirections(), pharmacyRifillBean.getSelected_potency_code(), pharmacyRifillBean.getRefill());
									} else {
										customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
									}
								}
							}).setNegativeButton("No", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}).show();
						}
						
					}
				});
				
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(dialogDenieReson.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			    dialogDenieReson.show();
			    dialogDenieReson.getWindow().setAttributes(lp);
			}
		});
		btnDenieWithNewRX.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialogDenieReson  = new Dialog(activity);
				dialogDenieReson.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogDenieReson.setContentView(R.layout.dialog_denie_reason);
				
				LinearLayout layDenieWithNewRXReason = (LinearLayout) dialogDenieReson.findViewById(R.id.layDenieWithNewRXReason);
				layDenieWithNewRXReason.setVisibility(View.VISIBLE);
				RelativeLayout lay_denial_reason_code = (RelativeLayout) dialogDenieReson.findViewById(R.id.lay_denial_reason_code);
				lay_denial_reason_code.setVisibility(View.GONE);
				
				etReason = (EditText) dialogDenieReson.findViewById(R.id.etReason);
				Button btnSubmitDenie = (Button) dialogDenieReson.findViewById(R.id.btnSubmitDenie);
				
				btnSubmitDenie.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (etReason.getText().toString().trim().isEmpty()) {
							etReason.setError("Please enter a reason for denie with newRX");
						} else {
							if (checkInternetConnection.isConnectedToInternet()) {
								new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").setMessage("Are you sure? You want to send Denie with newRX against this refill request").
								setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										if (checkInternetConnection.isConnectedToInternet()) {
											sendRefillResponse("Denied_With_NewRx", pharmacyRifillBean.getDrug_descriptor_id(), pharmacyRifillBean.getStart_date(),
													pharmacyRifillBean.getEnd_date(), pharmacyRifillBean.getRefill_id(), doctorRifillBean.getRefill_id(), pharmacyRifillBean.getQuantity(),
													pharmacyRifillBean.getDirections(), pharmacyRifillBean.getSelected_potency_code(), pharmacyRifillBean.getRefill());
										} else {
											customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
										}
									}
								}).setNegativeButton("No", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								}).show();
							} else {
								customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
							}
						}
					}
				});
				
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(dialogDenieReson.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			    dialogDenieReson.show();
			    dialogDenieReson.getWindow().setAttributes(lp);
				
			}
		});
	}//oncreate
	
	
	
	
	ArrayList<DrugRefillBean> drugRefillBeans;
	DrugRefillBean pharmacyRifillBean,pharmacyRifillBeanOriginal,
	doctorRifillBean;
	public void refillSummary(String prescription_id,String refill_id) {

		DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("prescription_id", prescription_id);
		params.put("refill_id", refill_id);

		client.post(DATA.baseUrl+"refillSummary", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--reaponce in refillSummary: "+content);

					try {
						JSONArray jsonArray = new JSONArray(content);
						drugRefillBeans = new ArrayList<>();
						DrugRefillBean drugRefillBean = null;

						for (int i = 0; i < jsonArray.length(); i++) {
							String refill_id = jsonArray.getJSONObject(i).getString("refill_id");
							String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
							String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
							String strength = jsonArray.getJSONObject(i).getString("strength");
							String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
							String refill = jsonArray.getJSONObject(i).getString("refill");
							String selected_potency_code = jsonArray.getJSONObject(i).getString("selected_potency_code");
							String quantity = jsonArray.getJSONObject(i).getString("quantity");
							String directions = jsonArray.getJSONObject(i).getString("directions");
							String start_date = jsonArray.getJSONObject(i).getString("start_date");
							String end_date = jsonArray.getJSONObject(i).getString("end_date");
							String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
							String potency_code = jsonArray.getJSONObject(i).getString("potency_code");
							String request_type = jsonArray.getJSONObject(i).getString("request_type");
							String prescribedBy = jsonArray.getJSONObject(i).getString("prescribedBy");
							String supply_days = jsonArray.getJSONObject(i).getString("supply_days");
							String selected_potency_unit = jsonArray.getJSONObject(i).getString("selected_potency_unit");

							String refill_qualifier = jsonArray.getJSONObject(i).getString("refill_qualifier");

							drugRefillBean = new DrugRefillBean(refill_id, drug_descriptor_id, drug_name, strength, dosage_form, refill, selected_potency_code, quantity, directions, start_date, end_date, potency_unit, potency_code, request_type, prescribedBy, supply_days, selected_potency_unit,refill_qualifier);
							drugRefillBeans.add(drugRefillBean);
							drugRefillBean = null;
						}

						for (int i = 0; i < drugRefillBeans.size(); i++) {
							if (drugRefillBeans.get(i).getPrescribedBy().equals("Pharmacy")) {
								pharmacyRifillBean = drugRefillBeans.get(i);
								pharmacyRifillBeanOriginal = drugRefillBeans.get(i);

								tvPharmacyDrugName.setText(pharmacyRifillBean.getDrug_name());
								tvPharmacyDrugStrength.setText(pharmacyRifillBean.getStrength());
								tvPharmacyDrugDosageForm.setText(pharmacyRifillBean.getDosage_form());
								tvPharmacyDrugQuantity.setText(pharmacyRifillBean.getQuantity());
								tvPharmacyDrugDuration.setText(doctorRifillBean.getSupply_days()+" Days");
								tvPharmacyDrugPotencyCode.setText(pharmacyRifillBean.getSelected_potency_unit());
								tvPharmacyDrugInstructions.setText(pharmacyRifillBean.getDirections());

								if (pharmacyRifillBean.getRefill().equals("0")) {

									if (pharmacyRifillBean.getRefill_qualifier().equalsIgnoreCase("prn")) {
										tvPharmacyDrugRefils.setText("As needed");
									} else {
										tvPharmacyDrugRefils.setText("");
									}

								} else {
									tvPharmacyDrugRefils.setText(pharmacyRifillBean.getRefill());
								}
							} else {
								doctorRifillBean = drugRefillBeans.get(i);
								tvPrescriberDrugName.setText(doctorRifillBean.getDrug_name());
								tvPrescriberDrugStrength.setText(doctorRifillBean.getStrength());
								tvPrescriberDrugDosageForm.setText(doctorRifillBean.getDosage_form());
								tvPrescriberDrugQuantity.setText(doctorRifillBean.getQuantity());
								tvPrescriberDrugDuration.setText(doctorRifillBean.getSupply_days()+" Days");
								tvPrescriberDrugPotencyCode.setText(doctorRifillBean.getSelected_potency_unit());
								tvPrescriberDrugInstructions.setText(doctorRifillBean.getDirections());
								tvPrescriberDrugRefils.setText(doctorRifillBean.getRefill());
							}
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: refillSummary, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail refillSummary: " +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}
	
	
	
	ArrayList<DrugBean> drugBeans;
	 public void getDrugs(String keyword) {

		DATA.showLoaderDefault(activity, "");
		 AsyncHttpClient client = new AsyncHttpClient();
		 ApiManager.addHeader(activity,client);
		 RequestParams params = new RequestParams();
		 params.put("keyword", keyword);
		 //params.put("dosage_form", dosage_form);

		 client.post(DATA.baseUrl+"/getDrugs", params, new AsyncHttpResponseHandler() {
		 	@Override
			 public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				 // called when response HTTP status is "200 OK"
				 DATA.dismissLoaderDefault();
				 try{
					 String content = new String(response);

					 System.out.println("--reaponce in getDrugs "+content);
					 try {
						 drugBeans = new ArrayList<DrugBean>();
						 DrugBean temp;

						 JSONArray jsonArray = new JSONObject(content).getJSONArray("data");

						 for (int i = 0; i < jsonArray.length(); i++) {
							 String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
							 String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
							 String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
							 String code = jsonArray.getJSONObject(i).getString("code");
							 String route = jsonArray.getJSONObject(i).getString("route");
							 String strength = jsonArray.getJSONObject(i).getString("strength");
							 String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
							 String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
							 String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
							 String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

							 String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
							 String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

							 temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc,potency_unit,potency_code);
							 drugBeans.add(temp);
							 temp = null;
						 }

						 //setData here

						 ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
								 activity,
								 R.layout.view_spinner_item,
								 drugBeans
						 );
						 spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						 spinnerDrugName.setAdapter(spDrugNameAdapter);


						 for (int i = 0; i < drugBeans.size(); i++) {
							 if (drugBeans.get(i).getDrug_descriptor_id().equals(pharmacyRifillBean.getDrug_descriptor_id())) {
								 spinnerDrugName.setSelection(i);
							 }
						 }

					 } catch (JSONException e) {
						 // TODO Auto-generated catch block
						 e.printStackTrace();
					 }
				 }catch (Exception e){
					 e.printStackTrace();
					 System.out.println("-- responce onsuccess: getDrugs, http status code: "+statusCode+" Byte responce: "+response);
					 customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				 }
			 }

			 @Override
			 public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				 // called when response HTTP status is "4XX" (eg. 401, 403, 404)
				 DATA.dismissLoaderDefault();
				 try {
					 String content = new String(errorResponse);
					 System.out.println("--onfail getDrugs " +content);
					 new GloabalMethods(activity).checkLogin(content);
					 customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				 }catch (Exception e1){
					 e1.printStackTrace();
					 customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				 }
			 }
		 });

	 }//end getDrugs
	
	
	
	
	 DrugBean selectedDrugBean;
	 Dialog drugsDialog;
	 Spinner spinnerDrugName;
	 //String dosage_form = "";
	 
	 
	 //String dosage = "",freq1 = "",freq2 = "";//dosage_formVal = "",
	 public void initDrugsDialog() {
		 drugsDialog = new Dialog(activity);
		 drugsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 drugsDialog.setContentView(R.layout.dialog_add_drugs);
		 drugsDialog.setCancelable(false);
		 
		 final EditText etSearchQuery = (EditText) drugsDialog.findViewById(R.id.etSearchQuery);
		 ImageView ivSearchQuery = (ImageView) drugsDialog.findViewById(R.id.ivSearchQuery); 
		 //final Spinner spinnerDrugForm = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugForm);
		 spinnerDrugName = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugName);
		 final Spinner spinnerRoute = (Spinner) drugsDialog.findViewById(R.id.spinner1);
		 final Spinner spinnerDosageForm = (Spinner) drugsDialog.findViewById(R.id.spinner2);
		 final Spinner spinnerStrength = (Spinner) drugsDialog.findViewById(R.id.spinner3);
		 final Spinner spinnerUnit = (Spinner) drugsDialog.findViewById(R.id.spinner4);
		 
		 
		 
		 /*Spinner spinnerDosage = (Spinner) drugsDialog.findViewById(R.id.spinner5);
		 Spinner spinnerFrequency1 = (Spinner) drugsDialog.findViewById(R.id.spinner6);
		 Spinner spinnerFrequency2 = (Spinner) drugsDialog.findViewById(R.id.spinner7);*/
		 final Spinner spinnerRefill = (Spinner) drugsDialog.findViewById(R.id.spinner8);
		 
		  final Spinner spinner_potency_unit = (Spinner) drugsDialog.findViewById(R.id.spinner_potency_unit);
		 final EditText etTotalQuantity = (EditText) drugsDialog.findViewById(R.id.etTotalQuantity);
		 
		 Button btnAddDrugs = (Button) drugsDialog.findViewById(R.id.btnAddDrugs);
		 btnAddDrugs.setText("Done");
		 Button btnAddDrugsCancel = (Button) drugsDialog.findViewById(R.id.btnAddDrugsCancel);
		 final EditText etStartDate = (EditText) drugsDialog.findViewById(R.id.etStartDate);
		 final EditText etEndtDate = (EditText) drugsDialog.findViewById(R.id.etEndtDate);
		 
		 final EditText etInstructions = (EditText) drugsDialog.findViewById(R.id.etInstructions);
		 ImageView ic_mike_Instructions = (ImageView) drugsDialog.findViewById(R.id.ic_mike_Instructions);
		 ic_mike_Instructions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startVoiceRecognitionActivity(etInstructions);
			}
		});
		 
		 etStartDate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					DialogFragment newFragment = new DatePickerFragment(etStartDate);
					newFragment.show(getSupportFragmentManager(), "datePicker");
				}
			});
		 etEndtDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etEndtDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		 
		 /*if (dosageFormsBeans != null) {
			 ArrayAdapter<DosageFormsBean> spDosageFormAdapter = new ArrayAdapter<DosageFormsBean>(
				        activity,
				        R.layout.view_spinner_item,
				        dosageFormsBeans
				);
			 spDosageFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 spinnerDrugForm.setAdapter(spDosageFormAdapter);
		} else {
			if (checkInternetConnection.isConnectedToInternet()) {
				getDosageForms();
			} else {
				Toast.makeText(activity, "No internet connection", 0).show();
			}
		}*/
		 
		 
		 /*spinnerDrugForm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dosage_form = dosageFormsBeans.get(arg2).getField_value();
				dosage_formVal = dosageFormsBeans.get(arg2).getDosage_form();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});*/
		 
		 //final String[] dosage={"1","2","3","4","5","6","7","8","9","10"};//,"11","12","13","14","15","16","17","18","19","20"
		 
		 /*ArrayAdapter<String> spDosageAdapter = new ArrayAdapter<String>(
			        this,
			         R.layout.spinner_item_lay,
			        dosage
			);
		 spDosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDosage.setAdapter(spDosageAdapter);
			
			
			//final String[] f1={"once","twice","thrice"};
			 ArrayAdapter<String> spFreq1Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f1
				);
			 spFreq1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency1.setAdapter(spFreq1Adapter);
			
			//final String[] f2={"day","week","month"};
			 ArrayAdapter<String> spFreq2Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f2
				);
			 spFreq2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency2.setAdapter(spFreq2Adapter);
		    
		    
		    spinnerDosage.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					AfterCallDialog.this.dosage = dosage[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		    spinnerFrequency1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq1 = f1[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		    spinnerFrequency2.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq2 = f2[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});*/
		    
		    ivSearchQuery.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//hideShowKeypad.hideSoftKeyboard();
					// TODO Auto-generated method stub
					if (etSearchQuery.getText().toString().isEmpty() || etSearchQuery.getText().toString().length()==1) {
						Toast.makeText(activity, "Please enter at least 2 characters of a medication name to search the medication", Toast.LENGTH_LONG).show();
					}else if (!checkInternetConnection.isConnectedToInternet()) {
						Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
					} else {
						
						getDrugs(etSearchQuery.getText().toString());
					}
				}
			});
		    
		    
		    spinnerDrugName.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					// TODO Auto-generated method stub
					  selectedDrugBean = drugBeans.get(pos);
					
					ArrayList<String> route = new ArrayList<String>();
					route.add(selectedDrugBean.getRoute());
					
					ArrayList<String> dosage_form = new ArrayList<String>();
					dosage_form.add(selectedDrugBean.getDosage_form());
					
					ArrayList<String> strength = new ArrayList<String>();
					strength.add(selectedDrugBean.getStrength());
					
					ArrayList<String> strength_unit_of_measure = new ArrayList<String>();
					strength_unit_of_measure.add(selectedDrugBean.getStrength_unit_of_measure());
					
					//------------------------potency_unit------------------------------------------------
					
					final ArrayList<PotencyUnitBean> potencyUnitBeans = new ArrayList<>();
					String[] pu = selectedDrugBean.getPotency_unit().split(",");
					String[] pc = selectedDrugBean.getPotency_code().split(",");
					
					for (int i = 0; i < pu.length; i++) {
						potencyUnitBeans.add(new PotencyUnitBean(pu[i], pc[i]));
					}
					
					spinner_potency_unit.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
							// TODO Auto-generated method stub
							selectedDrugBean.setPotency_code(potencyUnitBeans.get(pos).getPotency_code());
							selectedDrugBean.setPotency_unit(potencyUnitBeans.get(pos).getPotency_unit());
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					
					ArrayAdapter<PotencyUnitBean> spPtencyUnitAdapter = new ArrayAdapter<PotencyUnitBean>(
					        activity,
					        R.layout.spinner_item_lay,
					        potencyUnitBeans
					);
					spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner_potency_unit.setAdapter(spPtencyUnitAdapter);
					
					//--------------set Refill value for selectedDrugBean--------------------------
					final String[] refillArray={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"
							,"16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31",
							"32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50",
							"51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70",
							"71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88"
							,"89","90","91","92","93","94","95","96","97","98","99"};
					spinnerRefill.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							selectedDrugBean.refill = refillArray[arg2];
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
						}
					});
					
					ArrayAdapter<String> spRefillAdapter = new ArrayAdapter<String>(
					        activity,
					         R.layout.spinner_item_lay,
					         refillArray
					);
					spRefillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerRefill.setAdapter(spRefillAdapter);
					
					spinnerRefill.setSelection(Integer.parseInt(pharmacyRifillBean.getRefill()));
					
					//---------------------set Refill value for selectedDrugBean----------------------
					
					//------------------------potency_unit----------------------------------------------------
					ArrayAdapter<String> sprouteAdapter = new ArrayAdapter<String>(
					        activity,
					        R.layout.spinner_item_lay,
					        route
					);
					sprouteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerRoute.setAdapter(sprouteAdapter);
					
					ArrayAdapter<String> spdosage_formAdapter = new ArrayAdapter<String>(
					        activity,
					         R.layout.spinner_item_lay,
					        dosage_form
					);
					spdosage_formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerDosageForm.setAdapter(spdosage_formAdapter);
					
					ArrayAdapter<String> spstrengthAdapter = new ArrayAdapter<String>(
					        activity,
					         R.layout.spinner_item_lay,
					        strength
					);
					spstrengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerStrength.setAdapter(spstrengthAdapter);
					
					ArrayAdapter<String> spstrength_unit_of_measureAdapter = new ArrayAdapter<String>(
					        activity,
					         R.layout.spinner_item_lay,
					        strength_unit_of_measure
					);
					spstrength_unit_of_measureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerUnit.setAdapter(spstrength_unit_of_measureAdapter);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		    
		    etTotalQuantity.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					if (arg0.toString().startsWith(".")) {
						etTotalQuantity.setText("0"+arg0.toString());
						etTotalQuantity.setSelection(etTotalQuantity.getText().length());
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		    
		    etTotalQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View arg0, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus) {
						if (!etTotalQuantity.getText().toString().isEmpty()) {
							etTotalQuantity.setText(validateQuantity(etTotalQuantity.getText().toString()));
							Toast.makeText(activity, "Leading and trailing zeeros will be truncated from quantity.", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
		    btnAddDrugsCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					drugsDialog.dismiss();
				}
			});
		    btnAddDrugs.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					if (selectedDrugBean!=null) {
						
						if (etStartDate.getText().toString().equalsIgnoreCase("Start Date") ||
								etEndtDate.getText().toString().equalsIgnoreCase("End Date") || 
								etStartDate.getText().toString().isEmpty() || etEndtDate.getText().toString().isEmpty()) {
							Toast.makeText(activity, "Please add medicine start date and end date", Toast.LENGTH_LONG).show();
						}else if (etTotalQuantity.getText().toString().isEmpty()) {
							Toast.makeText(activity, "Please enter total quantity for the medicine", Toast.LENGTH_SHORT).show();
						}else if (etInstructions.getText().toString().trim().isEmpty()) {
							Toast.makeText(activity, "Please enter usage instructions for the medicine", Toast.LENGTH_SHORT).show();
						} else {						
						selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name());
						/*+"\n"+AfterCallDialog.this.dosage+" "+selectedDrugBean.getDfdesc()+" "+freq1+" a "+freq2
						+"\nFrom date: "+etStartDate.getText().toString()+" To date: "+etEndtDate.getText().toString()*/
						
						//this is total quantity for the medicine i.e sent in savePrescription
						//selectedDrugBean.setDosage_form(validateQuantity(etTotalQuantity.getText().toString()));//AfterCallDialog.this.dosage
						selectedDrugBean.totalQuantity = validateQuantity(etTotalQuantity.getText().toString());
						
						selectedDrugBean.start_date = etStartDate.getText().toString();
						selectedDrugBean.end_date = etEndtDate.getText().toString();
						
						if (!etInstructions.getText().toString().trim().isEmpty()) {
							/*selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name()
									+"\n\nUsage Instructions: "+etInstructions.getText().toString().trim()+"\n");*/
							
							selectedDrugBean.instructions = etInstructions.getText().toString().trim();
						}
						
						selectedDrugBean.duration = getDateDifference(etStartDate.getText().toString(), etEndtDate.getText().toString());
						
						/*DATA.drugBeans.add(selectedDrugBean);
						DrugsAdapter adapter = new DrugsAdapter(activity);
						lvDrugs.setAdapter(adapter);
						lvDrugs.setExpanded(true);*/// This actually does the magic
						//lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						
						
						pharmacyRifillBean = new DrugRefillBean(pharmacyRifillBeanOriginal.getRefill_id(), selectedDrugBean.getDrug_descriptor_id(), 
								selectedDrugBean.getDrug_name(), selectedDrugBean.getStrength(), selectedDrugBean.getDosage_form(), selectedDrugBean.refill, 
								selectedDrugBean.getPotency_code(), selectedDrugBean.totalQuantity, selectedDrugBean.instructions, selectedDrugBean.start_date, 
								selectedDrugBean.end_date, selectedDrugBean.getPotency_unit(), selectedDrugBean.getPotency_code(), 
								pharmacyRifillBeanOriginal.getRequest_type(), pharmacyRifillBeanOriginal.getPrescribedBy(), 
								selectedDrugBean.duration, selectedDrugBean.getPotency_unit(), pharmacyRifillBeanOriginal.getRefill_qualifier());
						
						
						drugsDialog.dismiss();
						
						tvPharmacyDrugName.setText(pharmacyRifillBean.getDrug_name());
						tvPharmacyDrugStrength.setText(pharmacyRifillBean.getStrength());
						tvPharmacyDrugDosageForm.setText(pharmacyRifillBean.getDosage_form());
						tvPharmacyDrugQuantity.setText(pharmacyRifillBean.getQuantity());
						tvPharmacyDrugDuration.setText(pharmacyRifillBean.getSupply_days());
						tvPharmacyDrugPotencyCode.setText(pharmacyRifillBean.getSelected_potency_unit());
						tvPharmacyDrugInstructions.setText(pharmacyRifillBean.getDirections());
						tvPharmacyDrugRefils.setText(pharmacyRifillBean.getRefill());
						
						if (pharmacyRifillBean.getDrug_descriptor_id().equals(pharmacyRifillBeanOriginal.getDrug_descriptor_id())) {
							if (pharmacyRifillBean.getRefill().equals(pharmacyRifillBeanOriginal.getRefill()) &&
									pharmacyRifillBean.getQuantity().equals(pharmacyRifillBeanOriginal.getQuantity()) && 
									pharmacyRifillBean.getDirections().equals(pharmacyRifillBeanOriginal.getDirections())) {
								btnApprove.setEnabled(true);
								btnApproveWithChanges.setEnabled(false);
								btnDenieWithNewRX.setEnabled(false);
							}else if (!pharmacyRifillBean.getDirections().equals(pharmacyRifillBeanOriginal.getDirections())) {
								btnApprove.setEnabled(false);
								btnApproveWithChanges.setEnabled(false);
								btnDenieWithNewRX.setEnabled(true);
							}else if (!pharmacyRifillBean.getQuantity().equals(pharmacyRifillBeanOriginal.getQuantity())) {
								btnApprove.setEnabled(false);
								btnApproveWithChanges.setEnabled(false);
								btnDenieWithNewRX.setEnabled(true);
							}else {
								btnApprove.setEnabled(false);
								btnApproveWithChanges.setEnabled(true);
								btnDenieWithNewRX.setEnabled(false);
							}
						} else {
							btnApprove.setEnabled(false);
							btnApproveWithChanges.setEnabled(false);
							btnDenieWithNewRX.setEnabled(true);
						}
						}
					} else {
						Toast.makeText(activity, "Please select a medicine to add", Toast.LENGTH_LONG).show();
					}
					
				}
			});
		    
		    etSearchQuery.setText(pharmacyRifillBean.getDrug_name().split(" ")[0]);
		    etSearchQuery.setSelection(etSearchQuery.getText().toString().length());
		    etTotalQuantity.setText(pharmacyRifillBean.getQuantity());
		    etInstructions.setText(pharmacyRifillBean.getDirections());
		    /*etStartDate.setText(pharmacyRifillBean.getStart_date());
		    etEndtDate.setText(pharmacyRifillBean.getEnd_date());*/
		    //etStartDate.setText(doctorRifillBean.getStart_date());
		    //etEndtDate.setText(doctorRifillBean.getEnd_date());
		    
		    
		    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(drugsDialog.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		    drugsDialog.show();
		    drugsDialog.getWindow().setAttributes(lp);
	}
	 
	 
	 public String getDateDifference(String startDate, String endDate) {
		 int daysDiff = 0;
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date sDate = sdf.parse(startDate);
			Date eDate = sdf.parse(endDate);
			long diff = eDate.getTime() - sDate.getTime();
			
			System.out.println("--days: "+(int) (diff / (1000*60*60*24)));
			daysDiff = (int) (diff / (1000*60*60*24));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return daysDiff+" Days";
	}
	 
	 public String validateQuantity(String q){
		 int a = 0;
		 float b = 0.0f;
		 
		 String result = "";
		 

			String[] arr = q.split("\\."); 
			
			//System.out.println("-- arr.length "+arr.length);
			//System.out.println("-- q = "+q);
			 
			 if (arr.length > 1) {
				 
				 a = Integer.parseInt(arr[0]);
				 b = Float.parseFloat("."+arr[1]);
				 
				 if (b > 0) {
					 result = (a+b)+"";
				} else {
					result = a+"";
				}
				
			} else if(arr.length == 1){
				a = Integer.parseInt(arr[0]);
				
				result = a+"";
			}
		
		 
		 
		 System.out.println("--result: "+result);
		 return result;
	 }
	 
	 
	 EditText editText;
		private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
		private void startVoiceRecognitionActivity(EditText editText) {
		    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    // identifying your application to the Google service
		    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		    // hint in the dialog
		    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
		    // hint to the recognizer about what the user is going to say
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		    // number of results
		    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		    // recognition language
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
		    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		    this.editText = editText;
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
		        ArrayList<String> matches = data.getStringArrayListExtra(
		                    RecognizerIntent.EXTRA_RESULTS);
		        // do whatever you want with the results
		        this.editText.setText(matches.get(0));
		    }
		    super.onActivityResult(requestCode, resultCode, data);
		}

	
		
		public void sendRefillResponse(String status,String drug_id,String start_date,String end_date,String refill_id,
				String old_refill_id,String quantity,String directions,String codes,String refill) {

			DATA.showLoaderDefault(activity, "");
			AsyncHttpClient client = new AsyncHttpClient();
			ApiManager.addHeader(activity,client);
			RequestParams params = new RequestParams();

			params.put("doctor_id", prefs.getString("id", "0"));
			params.put("status", status);
			params.put("prescription_id", DATA.selectedRefillBean.getPrescription_id());
			params.put("drug_id", drug_id);
			params.put("start_date", start_date);
			params.put("end_date", end_date);
			params.put("refill_id", refill_id);
			params.put("old_refill_id", old_refill_id);
			params.put("quantity", quantity);
			params.put("directions", directions);
			params.put("codes", codes);
			params.put("refill", refill);
			params.put("live_checkup_id", DATA.selectedRefillBean.getLive_checkup_id());
			params.put("patient_id", DATA.selectedRefillBean.getPatient_id());
			
			if (status.equals("Denied_With_NewRx")) {
				
				if (etReason != null) {
					params.put("denial_reason", etReason.getText().toString().trim());
				} else {
					System.err.println("-- etReason is null");
				}
			}
			
			if (status.equals("Denied")) {
				params.put("denial_reason_code", denial_reason_code);
			}
			
			System.out.println("-- params in sendRefillResponse: "+params.toString());

			client.post(DATA.baseUrl+"sendRefillResponse", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					DATA.dismissLoaderDefault();
					try{
						String content = new String(response);

						System.out.println("--reaponce in sendRefillResponse "+content);

						try {
							JSONObject jsonObject = new JSONObject(content);
							String status = jsonObject.getString("status");
							String msg = jsonObject.getString("msg");
							customToast.showToast(msg, 0, 1);

							if (status.equalsIgnoreCase("success")) {
								finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						}
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("-- responce onsuccess: sendRefillResponse, http status code: "+statusCode+" Byte responce: "+response);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					DATA.dismissLoaderDefault();
					try {
						String content = new String(errorResponse);
						System.out.println("--onfail sendRefillResponse " +content);
						new GloabalMethods(activity).checkLogin(content);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					}catch (Exception e1){
						e1.printStackTrace();
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}
			});

		}
		
		class DenielResonCodeBean{
			private String code;
			private String reason;
			public DenielResonCodeBean(String code, String reason) {
				super();
				this.code = code;
				this.reason = reason;
			}
			public String getCode() {
				return code;
			}
			public String getReason() {
				return reason;
			}
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return getReason();
			}
			
		}
}
