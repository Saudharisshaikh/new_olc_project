package com.app.OnlineCareTDC_Dr.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareTDC_Dr.ActivityRefillDetails;
import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.model.IdentifyPatientBean;
import com.app.OnlineCareTDC_Dr.model.UnknownPatientMedicationBean;
import com.app.OnlineCareTDC_Dr.util.CustomToast;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LvIdentifyPatientAdapter extends ArrayAdapter<IdentifyPatientBean> {

	Activity activity;
	CustomToast customToast;
	ArrayList<IdentifyPatientBean> identifyPatientBeans;

	public LvIdentifyPatientAdapter(Activity activity,ArrayList<IdentifyPatientBean> identifyPatientBeans) {
		super(activity, R.layout.lv_identify_patient_row, identifyPatientBeans);

		this.activity = activity;
		customToast = new CustomToast(activity);
		this.identifyPatientBeans = identifyPatientBeans;
	}

	public static class ViewHolder {
		CircularImageView ivDoctor;
		TextView tvDoctorName,tvDoctorDesig;
		public static TextView btnConnect,btnViewMedication;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_identify_patient_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.ivDoctor = (CircularImageView) convertView.findViewById(R.id.ivDoctor);
			viewHolder.tvDoctorName = (TextView) convertView.findViewById(R.id.tvDoctorName);
			viewHolder.tvDoctorDesig = (TextView) convertView.findViewById(R.id.tvDoctorDesig);
			viewHolder.btnConnect = (TextView) convertView.findViewById(R.id.btnConnect);
			viewHolder.btnViewMedication = (TextView) convertView.findViewById(R.id.btnViewMedication);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDoctorName, viewHolder.tvDoctorName);
			convertView.setTag(R.id.tvDoctorDesig, viewHolder.tvDoctorDesig);
			convertView.setTag(R.id.ivDoctor, viewHolder.ivDoctor);
			convertView.setTag(R.id.btnConnect, viewHolder.btnConnect);
			convertView.setTag(R.id.btnViewMedication, viewHolder.btnViewMedication);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		DATA.loadImageFromURL(identifyPatientBeans.get(position).getPimage() , R.drawable.icon_call_screen, viewHolder.ivDoctor);
		
		viewHolder.tvDoctorName.setText(identifyPatientBeans.get(position).getFirst_name()+" "+identifyPatientBeans.get(position).getLast_name());
		viewHolder.tvDoctorDesig.setText(identifyPatientBeans.get(position).getResidency());

		viewHolder.tvDoctorName.setTag(identifyPatientBeans.get(position).getFirst_name()+" "+identifyPatientBeans.get(position).getLast_name());
		viewHolder.tvDoctorDesig.setTag(identifyPatientBeans.get(position).getResidency());
		
		viewHolder.btnViewMedication.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getPatientsPrescription(identifyPatientBeans.get(position));
			}
		});

		return convertView;
	}
	
	
	ArrayList<UnknownPatientMedicationBean> medicationBeans;
	public void getPatientsPrescription(final IdentifyPatientBean identifyPatientBean) {
		
		DATA.print("-- in getPatientsPrescription patient name "+identifyPatientBean.getFirst_name());
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_view_patient_medications);
		final ListView lvPatientMedications = (ListView) dialog.findViewById(R.id.lvPatientMedications);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("date", DATA.selectedRefillBean.getDateof());
		params.put("patient_id", identifyPatientBean.getId());

		String reqURL = DATA.baseUrl+"getPatientsPrescription";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getPatientsPrescription: "+content);

					try {
						medicationBeans = new ArrayList<>();
						UnknownPatientMedicationBean bean;
						JSONArray jsonArray = new JSONArray(content);
						for (int i = 0; i < jsonArray.length(); i++) {
							String prescription_id = jsonArray.getJSONObject(i).getString("prescription_id");
							String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
							String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
							String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
							String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
							String strength = jsonArray.getJSONObject(i).getString("strength");
							String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
							String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
							String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

							bean = new UnknownPatientMedicationBean(prescription_id, drug_descriptor_id, route_of_administration, strength_unit_of_measure, drug_name, strength, dosage_form, potency_unit, potency_code);
							medicationBeans.add(bean);
							bean = null;

						}

						UnknownPatientMedictionAdapter adapter = new UnknownPatientMedictionAdapter(activity, medicationBeans);
						lvPatientMedications.setAdapter(adapter);

						lvPatientMedications.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								String prescriptionIdUnknownPatient = medicationBeans.get(arg2).prescription_id;
								DATA.selectedRefillBean.setPatient_id(identifyPatientBean.getId());
								DATA.selectedRefillBean.setPrescription_id(prescriptionIdUnknownPatient);
								ActivityRefillDetails.isFromUnknownPatientUnknownMedicaton = true;
								dialog.dismiss();
								if (ActivityRefillDetails.dialogIdentifyPatient != null) {
									ActivityRefillDetails.dialogIdentifyPatient.dismiss();
								}
								if (ActivityRefillDetails.layIdentify != null) {
									ActivityRefillDetails.layIdentify.setVisibility(View.GONE);
								}

								if (ActivityRefillDetails.layApproveBtns != null) {
									ActivityRefillDetails.layApproveBtns.setVisibility(View.VISIBLE);
								}
								ActivityRefillDetails.isFromUnknownRefills = false;


								if (ActivityRefillDetails.tvPrescPatientAdress != null) {
									ActivityRefillDetails.tvPrescPatientAdress.setText(identifyPatientBean.getResidency());
								}

								if (ActivityRefillDetails.tvPrescPtName != null) {
									ActivityRefillDetails.tvPrescPtName.setText(identifyPatientBean.getFirst_name()+" "+identifyPatientBean.getLast_name());
								}
								if (ActivityRefillDetails.tvPrescPatientDOB != null) {
									ActivityRefillDetails.tvPrescPatientDOB.setText(identifyPatientBean.getBirthdate());
								}

								if (ActivityRefillDetails.tvPrescPatientGender != null) {
									if (identifyPatientBean.getGender().equalsIgnoreCase("1")) {
										ActivityRefillDetails.tvPrescPatientGender.setText("Male");
									} else {
										ActivityRefillDetails.tvPrescPatientGender.setText("Female");
									}
								}

							}
						});

						dialog.show();

					} catch (JSONException e) {
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
						e.printStackTrace();
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
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}
}
