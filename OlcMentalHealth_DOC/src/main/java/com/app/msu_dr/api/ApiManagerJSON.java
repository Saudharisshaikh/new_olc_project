package com.app.msu_dr.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.msu_dr.util.CheckInternetConnection;
import com.app.msu_dr.util.CustomToast;
import com.app.msu_dr.util.DATA;
import com.app.msu_dr.util.GloabalMethods;
import com.app.msu_dr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONObject;

import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.app.msu_dr.util.DATA.baseUrl;


public class ApiManagerJSON {

	/*try {
		JSONObject params = new JSONObject();
		params.put("username", email);
		params.put("password", pass);
		params.put("fcmToken", sharedPrefsHelper.get("fcm_token","none"));
		//params.put("platform", "andorid");
		ApiManager apiManager = new ApiManager(ApiManager.LOGIN,"post",params,apiCallBack,activity);
		apiManager.loadURL();
	} catch (JSONException e) {
		e.printStackTrace();
	}*/

	//public static final String BASE_URL_FIXED = "http://13.95.108.74/FormEra.Api/api/";
	//public static final String BASE_URL_FIXED = "https://formera.xyz/FormEra.Api/api/";
	//public static final String BASE_URL_FIXED = "https://formera.xyz/api/";
	//public static String BASE_URL = "";

//	public static final String sURL = "http://13.95.108.74/dynamicData/api/services/app/FormApi/Submit";
//	public static final String uploadFilesURL = "http://13.95.108.74/dynamicData/api/services/app/FormApi/uploadFiles";

	//public static final String sURL = "https://formera.xyz/api/services/app/FormApi/Submit";
	//public static final String uploadFilesURL = "https://formera.xyz/api/services/app/FormApi/uploadFiles";

	//public static final String BASE_URL_DEFAULT = "https://webapp.formera.xyz";//webapp.formera.xyz   /api/

	public static final String API_STATUS_SUCCESS = "onSuccess";
	public static final String API_STATUS_ERROR = "onFailure";

	/*public static final String LOGIN = "TokenAuth/Authenticate";
	//public static final String GET_ALL = "services/app/Form/GetAll";
	public static final String GET_ALL = "services/app/Form/GetAllActive";
	public static final String GET_FORM_BY_ID = "services/app/Form/Get?Id=";
	public static final String SUBMIT_BULK = "services/app/Form/SubmitBulk";
	public static final String GET_SUBMISSIONS = "services/app/Report/GetSubmissionsForLookup?";
	public static final String SUBMIT = "services/app/Form/Submit";
	public static final String GET_ALL_LOOKUPS = "services/app/Report/GetAllSubmissionsForLookups";
	public static final String CHANGE_PASS = "services/app/Account/ChangePassword";

	public static final String SUBMIT_WITH_FILE = "services/app/FormApi/Submit";
	public static final String UPLOAD_FILES = "services/app/FormApi/uploadFiles";*/



	private String api = "";
	private String getOrPost = "";
	//private RequestParams params;
	private JSONObject jsonParams;
	private ApiCallBack apiCallBack;
	private Activity activity;

	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	CustomSnakeBar customSnakeBar;
	//CustomProgressDialog customProgressDialog;
	Dialog_CustomProgress customProgressDialog;
	//Dialog_CircularProgress dialogCircularProgress;
	SharedPrefsHelper sharedPrefsHelper;
	//public static boolean shouldShowPD = true;
	SharedPreferences prefs;
	String customURL = "";

	public ApiManagerJSON(String api, String getOrPost, JSONObject jsonParams, ApiCallBack apiCallBack, Activity activity) {
		// TODO Auto-generated constructor stub
		this.api = api;
		this.getOrPost = getOrPost;
		this.jsonParams = jsonParams;
		this.apiCallBack = apiCallBack;
		this.activity = activity;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
		//customProgressDialog = CustomProgressDialog.getCustomProgressDialogInstance(activity);
		customProgressDialog = new Dialog_CustomProgress(activity);
		//dialogCircularProgress = new Dialog_CircularProgress(activity);
		sharedPrefsHelper = SharedPrefsHelper.getInstance();
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//this.BASE_URL = sharedPrefsHelper.get("base_url",ApiManager.BASE_URL_FIXED);

		//BASE_URL = getBaseURL();

		customURL = baseUrl+api;

		DATA.print("-- url in ApiManager constructor: "+customURL);

		if(jsonParams != null){
			DATA.print("-- params in : "+api+" : "+jsonParams.toString());
		}
	}


	/*public static String getBaseURL(){
		return SharedPrefsHelper.getInstance().get("base_url",ApiManager.BASE_URL_DEFAULT)+"/api/";
	}*/


	public void loadURL() {

		if (! checkInternetConnection.isConnectedToInternet()){
			//customToast.showToast("No network found. Please check your internet connection and try again.", 0, 1);
			customSnakeBar.showToast(DATA.NO_NETWORK_MESSAGE);
			return;
		}
		 //DATA.showLoaderDefault(activity, "Please wait . . .");

		customProgressDialog.showProgressDialog();

		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(1000000000);

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			client.setSSLSocketFactory(sf);
		}
		catch (Exception e) {
			DATA.print("-- exception ");
			e.printStackTrace();
		}

		client.addHeader("Content-Type","application/json");

		DATA.print("-- token: "+"Bearer "+prefs.getString("access_token",""));
		client.addHeader("Oauthtoken","Bearer "+prefs.getString("access_token",""));

		//client.addHeader("Accept-Charset", "UTF-8");
		//Accept-Language: en
		//client.addHeader("Accept-Language",sharedPrefsHelper.get("lang_key","en"));
        //client.addHeader("X-App-Version", String.valueOf(BuildConfig.VERSION_CODE));


		//client.addHeader("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IjEwMDExIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZSI6InVzZXIiLCJBc3BOZXQuSWRlbnRpdHkuU2VjdXJpdHlTdGFtcCI6ImRiZWMzYTQyLWJkMTItNDc1MC05MmRiLTkxZGJkMGUzOTVkNSIsImh0dHA6Ly93d3cuYXNwbmV0Ym9pbGVycGxhdGUuY29tL2lkZW50aXR5L2NsYWltcy90ZW5hbnRJZCI6IjQiLCJzdWIiOiIxMDAxMSIsImp0aSI6IjYyMGY5MTFmLTBiM2ItNDI3ZS1iNzBiLWIwNWQ1YTBkMGYwOSIsImlhdCI6MTUyODcyMjkzMSwibmJmIjoxNTI4NzIyOTMxLCJleHAiOjE1Mjg4MDkzMzEsImlzcyI6IkR5bmFtaWNEYXRhIiwiYXVkIjoiRHluYW1pY0RhdGEifQ.pLVA2iIlkqvp6Zq83sClIA8Vu7WkqYLn2Eo5FQdQS3U");
		//if(sharedPrefsHelper.get("isLogin",false)){
			//client.addHeader("Authorization","Bearer "+sharedPrefsHelper.getUser().accessToken);
			//DATA.print("-- authToken : "+"Bearer "+sharedPrefsHelper.getUser().accessToken);
		//}else {
			//client.addHeader("Authorization","Basic aG90c3BvdDphZG1pbjEyMyFAIy4=");
		//}

		 if (getOrPost.equalsIgnoreCase("post")) {

			 StringEntity entity = null;
			 //try {
			     //entity = new StringEntity(jsonParams.toString();//here charset was not defined. causes issue in sending arabic language
				 entity = new StringEntity(jsonParams.toString(), "UTF-8");
			 /*} catch (UnsupportedEncodingException e) {
				 e.printStackTrace();
			 }*/

			 //==================API logs=================================
			 /*final StringBuilder stringBuilder = new StringBuilder();
			 stringBuilder.append("\n");stringBuilder.append("\n");
			 stringBuilder.append("Request time: "+SharedPrefsHelper.getDT_Fmt().format(new Date()));
			 stringBuilder.append("\n");
			 stringBuilder.append("Request URL: " + BASE_URL+api);
			 stringBuilder.append("\n");
			 stringBuilder.append("Request Body (JSON payload) : "+jsonParams.toString());*/
			 //==================API logs=================================

				 client.post(activity,customURL,null,entity,"application/json", new AsyncHttpResponseHandler() {
			        //client.post(BASE_URL+api,params, new AsyncHttpResponseHandler() {
                     @Override
                     public void onStart() {
                         // called before request is started
                     }

                     @Override
                     public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                         // called when response HTTP status is "200 OK"
						 customProgressDialog.dismissProgressDialog();
						 try{
							 if(response != null){
								 String content = new String(response);
								 DATA.print("-- in activity "+apiCallBack.getClass().getName().replace(activity.getPackageName(),"")+": status: "+
										 API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content+"\nstatusCode: "+statusCode);
								 apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);


								 //==================API logs=================================
								 /*stringBuilder.append("\n");
								 stringBuilder.append("Server Responce:\n");
								 stringBuilder.append("StatusCode: "+statusCode+"\n");
								 stringBuilder.append("Server Responce Body:\n");
								 stringBuilder.append(content);

								 new GloabalMethods(activity).saveToFile(stringBuilder.toString());*/
								 //==================API logs=================================
							 }else{
								 DATA.print("-- responce onsuccess: "+api+" :Error status code: "+statusCode+" Byte responce: "+response);
							 }
						 }catch (Exception e){
							 e.printStackTrace();
						 }
                     }

                     @Override
                     public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                         // called when response HTTP status is "4XX" (eg. 401, 403, 404)
						 customProgressDialog.dismissProgressDialog();
						 try {
							 String content = new String(errorResponse);
							 DATA.print("-- in activity "+apiCallBack.getClass().getName().replace(activity.getPackageName(),"")+": status: "+
									 API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content+"\nstatusCode: "+statusCode);


							 //==================API logs=================================
							 /*stringBuilder.append("\n");
							 stringBuilder.append("Server Responce:\n");
							 stringBuilder.append("StatusCode: "+statusCode+"\n");
							 stringBuilder.append("Server Responce Body:\n");
							 stringBuilder.append(content);

							 new GloabalMethods(activity).saveToFile(stringBuilder.toString());*/
							 //==================API logs=================================

							 new GloabalMethods(activity).checkLogin(content , statusCode);
							 customSnakeBar.showToast(DATA.CMN_ERR_MSG);

							 //{"result":null,"targetUrl":null,"success":false,"error":{"code":0,"message":"Login failed!","details":"Invalid user name or password",
							 // "validationErrors":null},"unAuthorizedRequest":false,"__abp":true}
							 /*JSONObject jsonObject = new JSONObject(content);
							 if(api.equalsIgnoreCase(ApiManager.LOGIN) && jsonObject.has("error")){
								 customToast.showToast(jsonObject.getJSONObject("error").optString("message")+" " +
										 jsonObject.getJSONObject("error").optString("details"),0,1);

								 return;
							 }*/
							 /*if(jsonObject.has("message")){
							 	customToast.showToast(jsonObject.getString("message"),0,0);
							 }*/

							 //apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
							 //customSnakeBar.showToast("Opps! Some thing went wrong please try again");
                             //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						 }catch (Exception e1){
							 e1.printStackTrace();
							 //customSnakeBar.showToast("Opps! Some thing went wrong please try again");
						 }
					 }

                     @Override
                     public void onRetry(int retryNo) {
                         // called when request is retried
                     }

					 /*@Override
					 public void onProgress(long bytesWritten, long totalSize) {
						 super.onProgress(bytesWritten, totalSize);
						 DATA.print("-- onprogress: bytesWritten: "+bytesWritten+" totalSize: "+totalSize);
						 //DATA.print("-- Progress: "+((bytesWritten/totalSize)*100));

						 int progress = (int)((bytesWritten*100)/totalSize);
						 DATA.print("-- Progress: "+progress);

					 }*/
				 });

		} else {

			 //==================API logs=================================
			 /*final StringBuilder stringBuilder = new StringBuilder();
			 stringBuilder.append("\n");stringBuilder.append("\n");
			 stringBuilder.append("Request time: "+SharedPrefsHelper.getDT_Fmt().format(new Date()));
			 stringBuilder.append("\n");
			 stringBuilder.append("Request URL: " + BASE_URL+api);
			 stringBuilder.append("\n");
			 stringBuilder.append("Get Request");*/
			 //==================API logs=================================

			client.get(customURL,null, new AsyncHttpResponseHandler() {//params
				@Override
				public void onStart() {
					// called before request is started
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					// called when response HTTP status is "200 OK"
					customProgressDialog.dismissProgressDialog();
					try{
						if(response != null){
							String content = new String(response);
							DATA.print("-- in activity "+apiCallBack.getClass().getName().replace(activity.getPackageName(),"")+": status: "+
									API_STATUS_SUCCESS+"\napiName: "+api+"\nresult: "+content);
							apiCallBack.fetchDataCallback(API_STATUS_SUCCESS,api,content);


							//==================API logs=================================
							/*stringBuilder.append("\n");
							stringBuilder.append("Server Responce:\n");
							stringBuilder.append("StatusCode: "+statusCode+"\n");
							stringBuilder.append("Server Responce Body:\n");
							stringBuilder.append(content);

							new GloabalMethods(activity).saveToFile(stringBuilder.toString());*/
							//==================API logs=================================
						}else{
							DATA.print("-- responce onsuccess: "+api+" :Error status code: "+statusCode+" Byte responce: "+response);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					// called when response HTTP status is "4XX" (eg. 401, 403, 404)
					customProgressDialog.dismissProgressDialog();
					try {
						String content = new String(errorResponse);
						DATA.print("-- in activity "+apiCallBack.getClass().getName().replace(activity.getPackageName(),"")+": status: "+
								API_STATUS_ERROR+"\napiName: "+api+"\nresult: "+content);


						//==================API logs=================================
						/*stringBuilder.append("\n");
						stringBuilder.append("Server Responce:\n");
						stringBuilder.append("StatusCode: "+statusCode+"\n");
						stringBuilder.append("Server Responce Body:\n");
						stringBuilder.append(content);

						new GloabalMethods(activity).saveToFile(stringBuilder.toString());*/
						//==================API logs=================================

						new GloabalMethods(activity).checkLogin(content , statusCode);
						customSnakeBar.showToast(DATA.CMN_ERR_MSG);

						//{"code":403,"status":"error","message":"Invalid username or passowrd","time":1516001279.737,"result":"Invalid username or passowrd"}
						/*JSONObject jsonObject = new JSONObject(content);
						if(jsonObject.has("message")){
							customToast.showToast(jsonObject.getString("message"),0,0);

							return;
						}

						//apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
						//customSnakeBar.showToast("Opps! Some thing went wrong please try again");
                        customToast.showToast(DATA.COMMON_ERROR_MSG,0,0);

						if(api.equalsIgnoreCase(ApiManager.GET_ALL_LOOKUPS)){
                            apiCallBack.fetchDataCallback(API_STATUS_ERROR,api,content);
                        }*/
					}catch (Exception e1){
						e1.printStackTrace();
						//customSnakeBar.showToast("Opps! Some thing went wrong please try again");
					}
				}

				@Override
				public void onRetry(int retryNo) {
					// called when request is retried
				}
			});
		}
	 }


	/*public static void addHeaders(AsyncHttpClient client){
		client.addHeader("devicetoken",SharedPrefsHelper.getInstance().get("fcm_token", "1234567890"));
		client.addHeader("platform","android");
		client.addHeader("Content-Type","application/json");
		client.addHeader("Authorization","Bearer "+SharedPrefsHelper.getInstance().getUser().accessToken);
		client.addHeader("X-App-Version", String.valueOf(BuildConfig.VERSION_CODE));

		DATA.print("-- authToken : "+"Bearer "+SharedPrefsHelper.getInstance().getUser().accessToken);
	}*/
}
