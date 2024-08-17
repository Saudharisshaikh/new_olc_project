package com.app.onlinecare_pk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.onlinecare_pk.adapter.PresDetailAdapter;
import com.app.onlinecare_pk.adapter.PrescriptionAdapter;
import com.app.onlinecare_pk.api.ApiCallBack;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.model.PrescriptionBean;
import com.app.onlinecare_pk.model.PressDetailBean;
import com.app.onlinecare_pk.util.CheckInternetConnection;
import com.app.onlinecare_pk.util.CustomToast;
import com.app.onlinecare_pk.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PresscriptionActivity extends AppCompatActivity implements ApiCallBack{

	ListView lvPress;
	TextView tvNoPress;
	AppCompatActivity activity;
	ApiCallBack apiCallBack;
	SharedPreferences prefs;
	CheckInternetConnection connection;
	CustomToast customToast;
	
	ArrayList<PrescriptionBean> prescriptionBeans;
	PrescriptionBean selectedPrescriptionBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presscription);
		
		activity = PresscriptionActivity.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		connection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		apiCallBack = this;
		
		lvPress = (ListView) findViewById(R.id.lvPress);
		tvNoPress = (TextView) findViewById(R.id.tvNoPress);
		
		/*lvPress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialogPrescDetails = new Dialog(activity);
				dialogPrescDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogPrescDetails.setContentView(R.layout.dial_pres_detail);
				lvPrescDrugsOnDialog = (ListView) dialogPrescDetails.findViewById(R.id.lvPresDetails);
				ImageView ivClose = (ImageView) dialogPrescDetails.findViewById(R.id.ivClose);
				ivClose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogPrescDetails.dismiss();
					}
				});

				selectedPrescriptionBean = prescriptionBeans.get(position);

				getPatientsPrescriptionDetails(selectedPrescriptionBean);
			}
		});*/
		getPresscriptions();
		/*lvPress.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 PrescriptionBean bean =prescriptionBeans.get(arg2);
				 final Dialog dial = new Dialog(activity);
				 dial.requestWindowFeature(Window.FEATURE_NO_TITLE);
				 dial.setContentView(R.layout.dialog_prescription);
				 
				 
				 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				    lp.copyFrom(dial.getWindow().getAttributes());
				    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				    dial.show();
				    dial.getWindow().setAttributes(lp);
				 
				 
				 TextView tvDrName = (TextView) dial.findViewById(R.id.tvDrName);
				 TextView tvDate = (TextView) dial.findViewById(R.id.tvDate);
				 TextView tvVitals = (TextView) dial.findViewById(R.id.tvVitals);
				 TextView tvDiagnoses= (TextView) dial.findViewById(R.id.tvDiagnoses);
				 TextView tvPresscriptions = (TextView) dial.findViewById(R.id.tvPresscrptions);
				 
				CircularImageView ivDr = (CircularImageView) dial.findViewById(R.id.ivPressDr);
				
				SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
				try {
				   Date formatedDate = input.parse(bean.getDateof());                 // parse input 
				    tvDate.setText(output.format(formatedDate));
				} catch (ParseException e) {
				    e.printStackTrace();
				}
				
				
				tvDrName.setText("Dr "+bean.getFirst_name()+" "+bean.getLast_name());
				tvVitals.setText(bean.getVitals());
				tvDiagnoses.setText(bean.getDiagnosis());
				tvPresscriptions.setText(bean.getTreatment());
				UrlImageViewHelper.setUrlDrawable(ivDr, bean.getImage(), R.drawable.doctor);
				Button btnok = (Button) dial.findViewById(R.id.btnOk);
				btnok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
					dial.dismiss();
						
					}
				});
			}
		});*/
		
	}

	public void getPresscriptions() {
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("type", "patient");

		ApiManager apiManager = new ApiManager(ApiManager.GET_PRESCRIPTIONS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	ArrayList<PressDetailBean> pressDetailBeens;
	public void getPatientsPrescriptionDetails(final PrescriptionBean prescriptionBean) {

		RequestParams params = new RequestParams();
		params.put("prescription_id", prescriptionBean.getId());

		ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_PRESC_DETAILS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void savePatientsRefillRequest(String doctor_id,String prescription_id,String id) {

		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("doctor_id", doctor_id);
		params.put("prescription_id", prescription_id);
		params.put("id", id);

		ApiManager apiManager = new ApiManager(ApiManager.SAVE_PATIENT_REFILL_REQUEST,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	Dialog dialogPrescDetails;
	ListView lvPrescDrugsOnDialog;
	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.GET_PRESCRIPTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					tvNoPress.setVisibility(View.VISIBLE);
				} else {
					tvNoPress.setVisibility(View.GONE);
				}

				prescriptionBeans = new ArrayList<PrescriptionBean>();
				PrescriptionBean prescriptionBean = null;
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					String id = object.getString("id");
					String  patient_id= object.getString("patient_id");
					String  doctor_id= object.getString("doctor_id");
					String  vitals= object.getString("vitals");
					String  diagnosis= object.getString("diagnosis");
					String  treatment= object.getString("treatment");
					String  dateof= object.getString("dateof");
					String  first_name= object.getString("first_name");
					String  last_name= object.getString("last_name");
					String  image= object.getString("image");
					String signature = object.getString("signature");

					prescriptionBean = new PrescriptionBean(id, patient_id, doctor_id, vitals, diagnosis, treatment, dateof, first_name, last_name, image,signature);
					prescriptionBeans.add(prescriptionBean);
					prescriptionBean = null;
				}

				PrescriptionAdapter adapter = new PrescriptionAdapter(activity, prescriptionBeans);
				lvPress.setAdapter(adapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0 , 0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.GET_PATIENT_PRESC_DETAILS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				pressDetailBeens = new ArrayList<PressDetailBean>();
				PressDetailBean bean;
				for (int i = 0; i < data.length(); i++) {
					String prescription_id = data.getJSONObject(i).getString("prescription_id");
					String id = data.getJSONObject(i).getString("id");
					String drug_name = data.getJSONObject(i).getString("drug_name");
					String refill = data.getJSONObject(i).getString("refill");

					bean = new PressDetailBean(prescription_id,id,drug_name,refill);
					pressDetailBeens.add(bean);
					bean = null;
				}

				lvPrescDrugsOnDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
						if (pressDetailBeens.get(position).refill.equals("0")) {
							new AlertDialog.Builder(activity).setTitle("No Rifills")
									.setMessage("No refills from doctor for this medication")
									.setPositiveButton("Ok", null).show();
						}else {
							new AlertDialog.Builder(activity).setTitle("Confirm")
									.setMessage("Are you sure? Would you like to send refill request to your doctor for this prescription")
									.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (connection.isConnectedToInternet()) {
												savePatientsRefillRequest(selectedPrescriptionBean.getDoctor_id(),pressDetailBeens.get(position).prescription_id,
														pressDetailBeens.get(position).id);
											}else {
												customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0 , 0);
											}
										}
									}).setNegativeButton("No", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
						}


					}
				});


				PresDetailAdapter adapter = new PresDetailAdapter(activity,pressDetailBeens);
				lvPrescDrugsOnDialog.setAdapter(adapter);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialogPrescDetails.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				dialogPrescDetails.show();
				dialogPrescDetails.getWindow().setAttributes(lp);
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0 , 0);
			}

		}else if(apiName.equalsIgnoreCase(ApiManager.SAVE_PATIENT_REFILL_REQUEST)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				String message = jsonObject.getString("message");
				new AlertDialog.Builder(activity).setTitle(jsonObject.getString("status"))
						.setMessage(message)
						.setPositiveButton("Ok", null).show();
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0 , 0);
			}
		}
	}
}
