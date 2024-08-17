package com.app.mhcsn_uc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import com.app.mhcsn_uc.adapter.TransectionAdapter;
import com.app.mhcsn_uc.api.ApiCallBack;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.model.TransectionBean;
import com.app.mhcsn_uc.util.CheckInternetConnection;
import com.app.mhcsn_uc.util.CustomToast;
import com.app.mhcsn_uc.util.DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransectionsActivity extends AppCompatActivity implements ApiCallBack {
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection connection;
	CustomToast customToast;
	ApiCallBack apiCallBack;
	ListView lvTransections;
	ArrayList<TransectionBean> transectionBeans;
	TransectionBean selectedTransectionBean;
	Dialog dialogTransactionDetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transections);

		activity = TransectionsActivity.this;
		apiCallBack = this;
		connection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		lvTransections = (ListView) findViewById(R.id.lvTransections);

		ApiManager apiManager = new ApiManager(ApiManager.GET_PATIENT_TRANSACTIONS+"/"+prefs.getString("id", "0"),"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	
	/*public void initDialog(TransectionBean bean) {
		dialogTransactionDetail = new Dialog(activity);
		dialogTransactionDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogTransactionDetail.setContentView(R.layout.dialog_transection);
		
		TextView tvTransactionPurpose,tvTransactionDate,tvTransactionAmount,tvTransactionID, tvTransactionGetWay;
		tvTransactionPurpose = (TextView) dialogTransactionDetail.findViewById(R.id.tvTransactionPurpose);
		tvTransactionAmount = (TextView) dialogTransactionDetail.findViewById(R.id.tvTransactionAmount);
		tvTransactionDate = (TextView) dialogTransactionDetail.findViewById(R.id.tvTransactionDate);
		tvTransactionID = (TextView) dialogTransactionDetail.findViewById(R.id.tvTransactionID);
		tvTransactionGetWay = (TextView) dialogTransactionDetail.findViewById(R.id.tvTransactionGetWay);
		
		if (bean.getLivecare_id().equalsIgnoreCase("null") ) {
			tvTransactionPurpose.setText("Online care donation");
		} else {
			tvTransactionPurpose.setText("Live care payment");	
		}
		
		tvTransactionAmount.setText("$ "+bean.getAmount());
		tvTransactionDate.setText(bean.getDateof());
		tvTransactionID.setText(bean.getTransaction_id());
		tvTransactionGetWay.setText(bean.getPayment_method());
		dialogTransactionDetail.show();
		
		Button btnOk = (Button) dialogTransactionDetail.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialogTransactionDetail.dismiss();
				
			}
		});
	}*/

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.contains(ApiManager.GET_PATIENT_TRANSACTIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				transectionBeans = new ArrayList<TransectionBean>();
				TransectionBean bean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String patient_id = data.getJSONObject(i).getString("patient_id");
					String dateof = data.getJSONObject(i).getString("dateof");
					String amount = data.getJSONObject(i).getString("amount");
					String livecare_id = data.getJSONObject(i).getString("livecare_id");
					String payment_method = data.getJSONObject(i).getString("payment_method");
					String donation_id = data.getJSONObject(i).getString("donation_id");
					String transaction_id = data.getJSONObject(i).getString("transaction_id");
					bean = new TransectionBean(id, patient_id, dateof, amount, livecare_id, payment_method, donation_id, transaction_id);
					transectionBeans.add(bean);
					bean = null;
				}
				TransectionAdapter adapter = new TransectionAdapter(activity, transectionBeans);
				lvTransections.setAdapter(adapter);

				/*lvTransections.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
						initDialog(transectionBeans.get(arg2));

					}
				});*/

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
