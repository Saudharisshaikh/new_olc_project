package com.app.priorityone_uc.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CallWebService {

	Activity activity;
	AsyncHttpClient client;
	CustomToast customToast;
	//String respons = "";
	ProgressDialog pd;
	OpenActivity openActivity;
	JSONObject jsonObject;//, user_info;
	//JSONArray doctorsArray;//symptomsArray, conditionsArray,specialityArray, subUsersArray, drSheduleArray;
	Database db;
	SharedPreferences prefs;
	//String msg ="";


	public CallWebService(Activity activity, ProgressDialog pd) {

		this.activity = activity;
		client = new AsyncHttpClient();
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		db = new Database(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		this.pd = pd;
	}

	public void postData(final String methodname, RequestParams params) {

		try {
			/*String url = "";
			if(methodname.equals("login")){
				url = DATA.SIGNUP_URL + PATIENT_LOGIN;
			}else{
				url = DATA.baseUrl+methodname;
			}
			System.out.println("-- url in callWebService: "+url);*/

			client.post(DATA.baseUrl+methodname, params, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					pd.dismiss();
					try{
						String content = new String(response);

						System.out.println("--Response: "+content);
						if (methodname.equals("login")) {
							//code shifted to Login
						}
						//============================================================
						else if(methodname.equals("searchDoctorByZipCode2")) { //if called from mainscreen set appointment

						}//==============if from getOnline doctors ends==========================


						//============================================================
						else if(methodname.equals("signup")) { //if called from signup

							try {
								jsonObject = new JSONObject(content);



								//							String userinfo = jsonObject.getString("user_info");
								//
								//							user_info = new JSONObject(userinfo);
								//
								//							db.deleteUser();
								//							db.insertMessage(jsonObject.getString("id"),
								//									user_info.getString("fname"),
								//									user_info.getString("lname"),
								//									user_info.getString("username"),
								//									user_info.getString("email"),
								//									user_info.getString("gender"),
								//									user_info.getString("dob"),
								//									"",
								//									user_info.getString("profile_img"),
								//									"",
								//									"");
								//
								//						} catch (JSONException e) {
								//							e.printStackTrace();
								//						}
								//
								//						SharedPreferences.Editor ed = prefs.edit();
								//						ed.putBoolean("shownPrefs", true);
								//						ed.commit();
								//
								//
								//						openActivity.open(MainScreen.class, true);

								//					}
								//============================================================



							} catch (JSONException e) {
								e.printStackTrace();
							}

						}//if signup ends...

						else if(methodname.equals("liveCheckup")) {

							try {

								jsonObject = new JSONObject(content);

								System.out.println("--online care liveCheckup response: "+content);


							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("-- responce onsuccess: "+methodname+", http status code: "+statusCode+" Byte responce: "+response);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					pd.dismiss();
					try {
						String content = new String(errorResponse);

						System.out.println("--statusCode"+statusCode);
						System.out.println("--error"+e);
						System.out.println("--content"+content);

						new GloabalMethods(activity).checkLogin(content);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					}catch (Exception e1){
						e1.printStackTrace();
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}
			});

		}catch(Exception e) {

			System.out.println("--exception in call service: "+e);

		}

	}

}
