package com.app.emcuradr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.emcuradr.adapter.PrescriptionAdapter;
import com.app.emcuradr.adapter.PrescriptionCPAdapter;
import com.app.emcuradr.adapter.PrescriptionCancelAdapter;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.model.PrescriptionBean;
import com.app.emcuradr.model.PrescriptionCPBean;
import com.app.emcuradr.model.PrescriptionCancelBean;
import com.app.emcuradr.util.CheckInternetConnection;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.Database;
import com.app.emcuradr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PresscriptionsActivity extends BaseActivity implements ApiCallBack{

	ViewFlipper vfPresc;
	ListView lvPress,lvPressCP , lvCancelpress ;
	TextView tvNoPress,tvMyPresc,tvPrescCP , tvpresCancel , tvNocancelpress ;

	AppCompatActivity activity;
	SharedPreferences prefs;
	CheckInternetConnection connection;
	OpenActivity openActivity;
	CustomToast customToast;
	ApiCallBack apiCallBack;

	ArrayList<PrescriptionBean> prescriptionBeans;
	//PrescriptionBean selectedPrescriptionBean;

	ArrayList<PrescriptionCPBean> prescriptionCPBeens;

	ArrayList<PrescriptionCancelBean> prescriptionCancelBeans;


	int selectedChild = 0;

	boolean isForMyPrescriptions = false;

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (ActivityCancelPrescription.isShouldreloadDate == true)
		{
			getPresscriptions();
			ActivityCancelPrescription.isShouldreloadDate = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presscriptions);


		isForMyPrescriptions = getIntent().getBooleanExtra("isForMyPrescriptions",false);

		activity = PresscriptionsActivity.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		connection = new CheckInternetConnection(activity);
		apiCallBack = this;
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_DOCTOR_PRESCRIPTION);

		lvPress = (ListView) findViewById(R.id.lvPress);
		tvNoPress = (TextView) findViewById(R.id.tvNoPress);
		tvNocancelpress = (TextView) findViewById(R.id.tvNocancelpress);
		tvMyPresc = (TextView) findViewById(R.id.tvMyPresc);
		tvPrescCP = (TextView) findViewById(R.id.tvPrescCP);
		vfPresc = (ViewFlipper) findViewById(R.id.vfPresc);
		lvPressCP = (ListView) findViewById(R.id.lvPressCP);
		lvCancelpress = (ListView) findViewById(R.id.lvCancelpress);
		tvpresCancel = findViewById(R.id.tvpresCancel);

		getPresscriptions();

		lvPressCP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ActivityPrescDetailCP.prescriptionCPBean = prescriptionCPBeens.get(position);
				openActivity.open(ActivityPrescDetailCP.class,false);
				/*if(! isForMyPrescriptions){

				}*/
			}
		});

		View.OnClickListener tabsClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvMyPresc:
						setUpTabs(0);
						break;
					case R.id.tvPrescCP:
						setUpTabs(1);
						break;
					case R.id.tvpresCancel:
						setUpTabs(2);
						break;
					default:
						break;
				}
			}
		};

		tvMyPresc.setOnClickListener(tabsClick);
		tvPrescCP.setOnClickListener(tabsClick);
		tvpresCancel.setOnClickListener(tabsClick);
		
		
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
				
				
				tvDrName.setText(bean.getFirst_name()+" "+bean.getLast_name());
				tvVitals.setText(bean.getVitals());
				tvDiagnoses.setText(bean.getDiagnosis());
				tvPresscriptions.setText(bean.getTreatment());
			UrlImageViewHelper.setUrlDrawable(ivDr, bean.getImage(), R.drawable.icon_dummy);
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

	public void setUpTabs(int index){
		if(index == 0){
			tvMyPresc.setBackgroundColor(getResources().getColor(R.color.theme_red));
			tvMyPresc.setTextColor(getResources().getColor(android.R.color.white));
			tvPrescCP.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvPrescCP.setTextColor(getResources().getColor(R.color.theme_red));
			tvpresCancel.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvpresCancel.setTextColor(getResources().getColor(R.color.theme_red));

			selectedChild = 0;
			if (selectedChild > vfPresc.getDisplayedChild()) {

				vfPresc.setInAnimation(activity, R.anim.in_right);
				vfPresc.setOutAnimation(activity, R.anim.out_left);
			} else {

				vfPresc.setInAnimation(activity, R.anim.in_left);
				vfPresc.setOutAnimation(activity, R.anim.out_right);
			}
			if (vfPresc.getDisplayedChild() != selectedChild) {
				vfPresc.setDisplayedChild(selectedChild);
			}
		}else if(index == 1){
			tvMyPresc.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvMyPresc.setTextColor(getResources().getColor(R.color.theme_red));
			tvpresCancel.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvpresCancel.setTextColor(getResources().getColor(R.color.theme_red));
			tvPrescCP.setBackgroundColor(getResources().getColor(R.color.theme_red));
			tvPrescCP.setTextColor(getResources().getColor(android.R.color.white));



			selectedChild = 1;
			if (selectedChild > vfPresc.getDisplayedChild()) {
				vfPresc.setInAnimation(activity, R.anim.in_right);
				vfPresc.setOutAnimation(activity, R.anim.out_left);
			} else {
				vfPresc.setInAnimation(activity, R.anim.in_left);
				vfPresc.setOutAnimation(activity, R.anim.out_right);
			}
			if (vfPresc.getDisplayedChild() != selectedChild) {
				vfPresc.setDisplayedChild(selectedChild);
			}
		}
		else if(index == 2){
			tvMyPresc.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvMyPresc.setTextColor(getResources().getColor(R.color.theme_red));
			tvPrescCP.setBackgroundColor(getResources().getColor(android.R.color.white));
			tvPrescCP.setTextColor(getResources().getColor(R.color.theme_red));
			tvpresCancel.setBackgroundColor(getResources().getColor(R.color.theme_red));
			tvpresCancel.setTextColor(getResources().getColor(android.R.color.white));

			selectedChild = 2;
			if (selectedChild > vfPresc.getDisplayedChild()) {
				vfPresc.setInAnimation(activity, R.anim.in_right);
				vfPresc.setOutAnimation(activity, R.anim.out_left);
			} else {
				vfPresc.setInAnimation(activity, R.anim.in_left);
				vfPresc.setOutAnimation(activity, R.anim.out_right);
			}
			if (vfPresc.getDisplayedChild() != selectedChild) {
				vfPresc.setDisplayedChild(selectedChild);
			}
		}
	}

	public void getPresscriptions() {
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("type", "doctor");
		if(isForMyPrescriptions){
			setUpTabs(0);
		}else {
			setUpTabs(1);
		}

		ApiManager apiManager = new ApiManager(ApiManager.GET_PRESCRIPTIONS,"post",params,apiCallBack, activity);
		apiManager.loadURL();

		//ApiManager apiManager1 = new ApiManager(ApiManager.GET_NURSE_PRESCRIPTIONS,"post",params,apiCallBack, activity);
		//apiManager1.loadURL();

		RequestParams params1 = new RequestParams();
		params1.put("doctor_id", prefs.getString("id", "0"));
		ApiManager apiManager2 = new ApiManager(ApiManager.GET_CANCEL_PRESCRIPTIONS,"post",params1,apiCallBack, activity);
		apiManager2.loadURL();
	}

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.GET_PRESCRIPTIONS)){

			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				DATA.print(data.toString());

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
					String  patient_id= object.optString("patient_id");
					String  doctor_id= object.optString("doctor_id");
					String  vitals= object.optString("vitals");
					String  diagnosis= object.optString("diagnosis");
					String  treatment= object.optString("treatment");
					String  dateof= object.getString("dateof");
					String  first_name= object.getString("first_name");
					String  last_name= object.getString("last_name");
					String  image= object.getString("image");
					String  signature= object.getString("signature");


					prescriptionBean = new PrescriptionBean(id, patient_id, doctor_id, vitals, diagnosis, treatment,
							dateof, first_name, last_name, image,signature);


					prescriptionBean.quantity = object.getString("quantity");
					prescriptionBean.directions = object.getString("directions");
					 prescriptionBean.status = object.getString("status");
					 prescriptionBean.drug_name = object.getString("drug_name");

					prescriptionBeans.add(prescriptionBean);
					prescriptionBean = null;
				}

				PrescriptionAdapter adapter = new PrescriptionAdapter(activity, prescriptionBeans);
				lvPress.setAdapter(adapter);

			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
				e.printStackTrace();
			}
		}
		else if(apiName.equalsIgnoreCase(ApiManager.GET_NURSE_PRESCRIPTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					findViewById(R.id.tvNoCPPress).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.tvNoCPPress).setVisibility(View.GONE);
				}

				prescriptionCPBeens = new ArrayList<>();
				PrescriptionCPBean prescriptionCPBean = null;
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);

					String first_name = object.getString("first_name");
					String last_name = object.getString("last_name");
					String image = object.getString("image");
					String signature = object.getString("signature");
					String dateof = object.getString("dateof");
					String prescribed_by = object.getString("prescribed_by");
					String drug_name = object.getString("drug_name");
					String ppstatus = object.getString("ppstatus");
					String id = object.getString("id");
					String directions = object.getString("directions");
					String quantity = object.getString("quantity");
					String refill = object.getString("refill");

					prescriptionCPBean = new PrescriptionCPBean(first_name,last_name,image,signature,dateof,prescribed_by,drug_name
							,ppstatus,id,directions,quantity,refill);
					prescriptionCPBeens.add(prescriptionCPBean);
					prescriptionCPBean = null;
				}

				PrescriptionCPAdapter adapter = new PrescriptionCPAdapter(activity, prescriptionCPBeens);
				lvPressCP.setAdapter(adapter);

			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
				e.printStackTrace();
			}
		}
		else if(apiName.equalsIgnoreCase(ApiManager.GET_CANCEL_PRESCRIPTIONS)){

			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					tvNocancelpress.setVisibility(View.VISIBLE);
				} else {
					tvNocancelpress.setVisibility(View.GONE);
				}

				prescriptionCancelBeans = new ArrayList<PrescriptionCancelBean>();
				PrescriptionCancelBean prescriptionCancelBean = null;
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					String id = object.getString("id");
					String  patient_id= object.getString("patient_id");
					String  doctor_id= object.getString("patient_id");
					String  vitals= object.getString("vitals");
					String  diagnosis= object.getString("diagnosis");
					String  treatment= object.optString("treatment");
					String  dateof= object.getString("dateof");
					String  first_name= object.getString("first_name");
					String  last_name= object.getString("last_name");
					String  image= object.getString("image");
					String  signature= object.getString("signature");

					prescriptionCancelBean = new PrescriptionCancelBean(id, patient_id, doctor_id, vitals, diagnosis, treatment, dateof, first_name, last_name, image,signature);
					prescriptionCancelBean.quantity = object.getString("quantity");
					prescriptionCancelBean.directions = object.getString("directions");
					prescriptionCancelBean.status = object.getString("status");
					prescriptionCancelBean.drug_name = object.getString("drug_name");
					prescriptionCancelBeans.add(prescriptionCancelBean);
					prescriptionCancelBean = null;
				}

				PrescriptionCancelAdapter adapter = new PrescriptionCancelAdapter(activity, prescriptionCancelBeans);
				lvCancelpress.setAdapter(adapter);

			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
				e.printStackTrace();
			}
		}
	}

}
