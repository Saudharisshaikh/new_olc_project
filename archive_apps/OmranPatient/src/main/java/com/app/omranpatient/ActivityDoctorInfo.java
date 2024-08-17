package com.app.omranpatient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.omranpatient.api.ApiManager;
import com.app.omranpatient.model.DrInfoBean;
import com.app.omranpatient.paypal.PaymentLiveCare;
import com.app.omranpatient.util.CustomToast;
import com.app.omranpatient.util.DATA;
import com.app.omranpatient.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ActivityDoctorInfo extends Activity {
	
	
	Activity activity;
	TextView tvDrInfoName,tvDrInfoDesignation, tvDrInfoEmail,tvDrInfoMobile,tvDrInfoAdress,tvDrInfoZipcode,tvDrInfoMemberSince;
	ImageView ivDrInfoImage;
	Button btnOk,btnDrInfoLiveCheckup;
	CustomToast customToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_doctor_info);
		
		activity = ActivityDoctorInfo.this;
		
		ivDrInfoImage = (CircularImageView) findViewById(R.id.ivDrInfoImage);
		tvDrInfoName = (TextView) findViewById(R.id.tvDrInfoName);
		tvDrInfoDesignation = (TextView) findViewById(R.id.tvDrInfoDesignation);
		tvDrInfoEmail = (TextView) findViewById(R.id.tvDrInfoEmail);
		tvDrInfoMobile = (TextView) findViewById(R.id.tvDrInfoMobile);
		tvDrInfoAdress = (TextView) findViewById(R.id.tvDrInfoAddress);
		tvDrInfoZipcode = (TextView) findViewById(R.id.tvDrInfoZipcode);
		tvDrInfoMemberSince = (TextView) findViewById(R.id.tvDrInfoMemberSince);
		btnOk = (Button) findViewById(R.id.btnDrInfoOK);
		btnDrInfoLiveCheckup = (Button) findViewById(R.id.btnDrInfoLiveCheckup);

		customToast = new CustomToast(activity);
		
		
		String doctorId = getIntent().getStringExtra("doctor_id");
		getDoctorInfo(doctorId);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			finish();
				
			}
		});
		
		btnDrInfoLiveCheckup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (drInfoBean != null) {
					DATA.doctorIdForLiveCare = drInfoBean.getId();
					Intent i = new Intent(activity,PaymentLiveCare.class);
					 startActivity(i);
					 if (MapActivity.activity != null) {
						 MapActivity.activity.finish();
					}
					if (ActivityUrgentCareDoc.activity != null) {
						ActivityUrgentCareDoc.activity.finish();
					}
	                 finish();
				} else {
					System.out.println("-- drInfoBean is null");
				}
				
			}
		});




		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        //dialogSupport.show();
        this.getWindow().setAttributes(lp);
	}//oncreate

	
	DrInfoBean drInfoBean;
	public void getDoctorInfo(String doctorId) {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
//		RequestParams params = new RequestParams();
//		params.put("patient_id", patient_id);


		DATA.showLoaderDefault(activity,  "Please wait...    ");

		client.get(DATA.baseUrl+"/doctorInfo/"+doctorId, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--responce in doctorInfo: "+content);

					try {

						JSONObject data = new JSONObject(content).getJSONObject("data");
						String id = data.getString("id");
						String first_name = data.getString("first_name");
						String last_name = data.getString("last_name");
						String sex = data.getString("sex");
						String birthdate = data.getString("birthdate");
						String address1 = data.getString("address1");
						String address2 = data.getString("address2");
						String state = data.getString("state");
						String city = data.getString("city");
						String mobile = data.getString("mobile");
						String email = data.getString("email");
						String username = data.getString("username");
						String password = data.getString("password");
						String is_online = data.getString("is_online");
						String image = data.getString("image");
						String introduction = data.getString("introduction");
						String qualification = data.getString("qualification");
						String career_data = data.getString("career_data");
						String designation = data.getString("designation");
						String country = data.getString("country");
						String qb_id = data.getString("qb_id");
						String reg_date = data.getString("reg_date");
						String speciality_id = data.getString("speciality_id");
						String zip_code = data.getString("zip_code");
						String live_care = data.getString("live_care");
						String live_care_status = data.getString("live_care_status");
						String clinic_id = data.getString("clinic_id");
						String calling_status = data.getString("calling_status");
						String calling_doctor_id = data.getString("calling_doctor_id");
						String session_id = data.getString("session_id");
						String latitude = data.getString("latitude");
						String longitude = data.getString("longitude");
						String is_approved = data.getString("is_approved");
						String marital_status = data.getString("marital_status");
						String timezone = data.getString("timezone");
						String platform = data.getString("platform");
						String device_token = data.getString("device_token");


						drInfoBean = new DrInfoBean(id, first_name, last_name, sex, birthdate, address1, address2, state, city, mobile, email, username, password, is_online, image, introduction, qualification, career_data, designation, country, qb_id, reg_date, speciality_id, zip_code, live_care, live_care_status, clinic_id, calling_status, calling_doctor_id, session_id, latitude, longitude, is_approved, marital_status, timezone, platform, device_token);


						DATA.loadImageFromURL(image,R.drawable.icon_call_screen,ivDrInfoImage);
						tvDrInfoName.setText(first_name+" "+last_name);
						tvDrInfoDesignation.setText(designation);
						tvDrInfoEmail.setText(email);
						tvDrInfoMobile.setText(mobile);
						tvDrInfoAdress.setText(address1);
						tvDrInfoZipcode.setText(zip_code);
						tvDrInfoMemberSince.setText(reg_date);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: doctorInfo , http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure doctorInfo: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end getNearByDrs
	
}
