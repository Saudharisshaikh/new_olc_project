package com.app.omrandr.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.omrandr.DoctorsList;
import com.app.omrandr.api.ApiManager;
import com.app.omrandr.model.DoctorsModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CallWebService {

	Activity activity;
	AsyncHttpClient client;
	CustomToast customToast;
	String respons = "";
	ProgressDialog pd;
	OpenActivity openActivity;
	JSONObject jsonObject;//, user_info;
	JSONArray doctorsArray;
	Database db;
	SharedPreferences prefs;

	//JSONArray symptomsArray, conditionsArray,specialityArray, subUsersArray,
	//String msg ="";

	public CallWebService(Activity activity, ProgressDialog pd) {

		this.activity = activity;
		client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		db = new Database(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		this.pd = pd;
	}

	public void postData(final String methodname, RequestParams params) {

		try {

			String reqURL = DATA.baseUrl+methodname;

			DATA.print("-- Request : "+reqURL);
			DATA.print("-- params : "+params.toString());

			client.post(reqURL, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					//customProgressDialog.dismissProgressDialog();
					try{
						String content = new String(response);

						DATA.print("--Response: "+response);

						//============================================================
						if(methodname.equals("getOnlineDoctors")) { //if called from mainscreen get online doctors

							try {
								jsonObject = new JSONObject(content);

								String status = jsonObject.getString("status");

								if(status.equals("success")) {

									String drsStr = jsonObject.getString("doctors_info");

									doctorsArray = new JSONArray(drsStr);

									DATA.allDoctors = new ArrayList<DoctorsModel>();

									DoctorsModel temp;

									for(int i = 0; i<doctorsArray.length(); i++) {

										temp = new DoctorsModel();


										temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
										temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
										temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
										temp.email = doctorsArray.getJSONObject(i).getString("email");
										temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
										temp.image = doctorsArray.getJSONObject(i).getString("image");
										temp.careerData = doctorsArray.getJSONObject(i).getString("career_data");

										DATA.print("--online care callwebservice getOnline Doctors fname: "+temp.fName);

										DATA.allDoctors.add(temp);

										temp = null;
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}


							openActivity.open(DoctorsList.class, false);

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

								DATA.print("--online care liveCheckup response: "+response);


							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}


						pd.dismiss();


					}catch (Exception e){
						e.printStackTrace();
						DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
						customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					pd.dismiss();
					try {
						String content = new String(errorResponse);
						DATA.print("-- onfailure : "+reqURL+content);
						new GloabalMethods(activity).checkLogin(content);
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
