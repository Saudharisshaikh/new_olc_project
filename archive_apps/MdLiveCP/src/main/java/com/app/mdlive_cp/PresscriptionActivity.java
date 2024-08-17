package com.app.mdlive_cp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_cp.adapters.PrescriptionAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.PrescriptionBean;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PresscriptionActivity extends BaseActivity implements ApiCallBack{

	ListView lvPress;
	TextView tvNoPress;
	ApiCallBack apiCallBack;
	AppCompatActivity activity;
	//SharedPreferences prefs;
	CheckInternetConnection connection;
	
	ArrayList<PrescriptionBean> prescriptionBeans;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presscriptions);
		
		activity = PresscriptionActivity.this;
		//prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);   name is inccorrect
		connection = new CheckInternetConnection(activity);
		
		lvPress = (ListView) findViewById(R.id.lvPress);
		tvNoPress = (TextView) findViewById(R.id.tvNoPress);
		apiCallBack = this;


		getPresscriptions(DATA.selectedUserCallId);
		
		
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

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.presscription, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	/*{
        "id": "3",
        "patient_id": "17",
        "doctor_id": "7",
        "vitals": "these are vitals",
        "diagnosis": "this is diagnosis",
        "treatment": "this is treatment",
        "dateof": "2015-11-17",
        "first_name": "Paul",
        "last_name": "Victor",
        "image": "1902350847_0_doctor_image.jpg"
    }*/
	public void getPresscriptions(String patientId) {

		RequestParams params = new RequestParams();
		params.put("patient_id", patientId);
		params.put("type", "patient");

		ApiManager apiManager = new ApiManager(ApiManager.GET_PRESCRIPTIONS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.GET_PRESCRIPTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					tvNoPress.setVisibility(View.VISIBLE);
					tvNoPress.setText("No prescriptions");
				} else {
					tvNoPress.setVisibility(View.GONE);
				}

				prescriptionBeans = new ArrayList<PrescriptionBean>();
				PrescriptionBean prescriptionBean = null;
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					String id = object.getString("id");
					String  patient_id= object.getString("patient_id");
					String  doctor_id= object.getString("patient_id");
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
			}
		}
	}
}
