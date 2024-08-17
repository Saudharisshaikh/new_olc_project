package com.app.emcurauc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.emcurauc.model.SubUsersModel;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GetUsers {
	
	AsyncHttpClient client;
	JSONObject jsonObject, user_info;
	JSONArray symptomsArray, conditionsArray,specialityArray, subUsersArray,
			  doctorsArray;
	SharedPreferences prefs;
	String msg, status;
	
	Activity activity;
	CustomToast customToast;
	
	ProgressDialog pd;

	public GetUsers(Activity activity) {
		
		this.activity = activity;

		customToast = new CustomToast(activity);
		
		client = new AsyncHttpClient();
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		
		pd = new ProgressDialog(activity);
		pd.setMessage("Refreshing family members list");
	}
	
	public void getUsers() {
		
		pd.show();
		
		try {

			RequestParams params = new RequestParams();
			params.put("username",prefs.getString("qb_username", ""));
			params.put("password", prefs.getString("qb_password", ""));
			params.put("type", "patient");

			
			client.post(DATA.baseUrl+"login", params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					pd.dismiss();
					try{
						String content = new String(response);

						DATA.print("--Response: "+content);

						try {
							jsonObject = new JSONObject(content);
							String status = jsonObject.getString("status");
							if(jsonObject.has("msg")) {
								msg = jsonObject.getString("msg");
							}
							if(status.equals("success")) {

								String userStr = jsonObject.getString("user");
								user_info = new JSONObject(userStr);

								DATA.allSubUsers = new ArrayList<SubUsersModel>();

								SubUsersModel temp1 = new SubUsersModel();

								temp1.id = user_info.getString("id");
								temp1.firstName = user_info.getString("first_name");
								temp1.lastName = user_info.getString("last_name");;
								temp1.image = user_info.getString("image");
								temp1.occupation = user_info.getString("occupation");
								temp1.marital_status = user_info.getString("marital_status");

								temp1.insurance = user_info.getString("insurance");

								temp1.relationship = "Parent user";

								DATA.allSubUsers.add(temp1);

								SharedPreferences.Editor ed = prefs.edit();
								ed.putString("id", user_info.getString("id"));
								ed.putString("first_name", user_info.getString("first_name"));
								ed.putString("last_name", user_info.getString("last_name"));
								ed.putString("email", user_info.getString("email"));
								ed.putString("username", user_info.getString("username"));
								ed.putString("gender", user_info.getString("gender"));
								ed.putString("birthdate", user_info.getString("birthdate"));
								ed.putString("phone", user_info.getString("phone"));
								ed.putString("image", user_info.getString("image"));
								ed.putString("qb_id", user_info.getString("qb_id"));
								ed.putString("reg_date", user_info.getString("reg_date"));
								ed.putString("is_online", user_info.getString("is_online"));
								ed.putString("occupation", user_info.getString("occupation"));
								ed.putString("marital_status", user_info.getString("marital_status"));
								ed.putBoolean("HaveShownPrefs",true);
								ed.putString("DrOrPatient", Login.DrOrPatient);
								ed.commit();

								String subUsrs = jsonObject.getString("sub_users");

								subUsersArray = new JSONArray(subUsrs);


								SubUsersModel temp;

								for(int j = 0; j<subUsersArray.length(); j++) {

									temp = new SubUsersModel();

									temp.id = subUsersArray.getJSONObject(j).getString("id");
									temp.firstName = subUsersArray.getJSONObject(j).getString("first_name");
									temp.lastName = subUsersArray.getJSONObject(j).getString("last_name");
									temp.patient_id = subUsersArray.getJSONObject(j).getString("patient_id");
									temp.image = subUsersArray.getJSONObject(j).getString("image");
									temp.gender = subUsersArray.getJSONObject(j).getString("gender");
									temp.dob = subUsersArray.getJSONObject(j).getString("dob");
									temp.marital_status = subUsersArray.getJSONObject(j).getString("marital_status");
									temp.relationship = subUsersArray.getJSONObject(j).getString("relationship");
									temp.occupation = subUsersArray.getJSONObject(j).getString("occupation");

									temp.insurance = subUsersArray.getJSONObject(j).getString("insurance");

									temp.is_primary = subUsersArray.getJSONObject(j).getString("is_primary");

									DATA.allSubUsers.add(temp);

									temp  = null;
								}

								Intent intent = new Intent(activity,SubUsersList.class);
								activity.startActivity(intent);
								activity.finish();

							} else if(status.equals("error")) {
								DATA.print("--online care error in get users.");
							}

						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}catch (Exception e){
						e.printStackTrace();
						DATA.print("-- responce onsuccess: login, http status code: "+statusCode+" Byte responce: "+response);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					pd.dismiss();
					try {
						String content = new String(errorResponse);

						DATA.print("--statusCode"+statusCode);
						DATA.print("--error"+e);
						DATA.print("--content"+content);

						  new GloabalMethods(activity).checkLogin(content , statusCode);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					}catch (Exception e1){
						e1.printStackTrace();
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}
			});

		}catch(Exception e) {
			DATA.print("--exception in call service: "+e);

		}
		
	}

}
