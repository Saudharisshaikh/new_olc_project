package com.app.emcuradr.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.app.emcuradr.UpdateProfile;
import com.app.emcuradr.api.ApiManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class UploadImageCall{

	Activity activity;
	ProgressDialog pd;

	String value, errorType = "", url;
	static JSONObject jsonObject = null,userInfoObject = null;

	SharedPreferences prefs;
	String msg, status;

	String imageName;

	public UploadImageCall(Activity activity) {
		this.activity = activity;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

	}

	public void executeUploadImg() {
		pd = new ProgressDialog(activity);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("Updating profile image...");
		pd.show();

		url = DATA.baseUrl + "userProfileImg";

		try {
			File imageFile = new File(DATA.imagePath);
			imageName = prefs.getString("id", "") + "_" + prefs.getString("subPatientID", "0") + "_doctor_image.jpg";

			AsyncHttpClient client = new AsyncHttpClient();
			ApiManager.addHeader(activity,client);
			RequestParams params = new RequestParams();
			params.put("image", imageFile, "image/jpeg", imageName);

			client.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					super.onStart();
					// Perform any actions before the request starts
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					try {
						String response = new String(responseBody);
						jsonObject = new JSONObject(response);

						SharedPreferences.Editor ed = prefs.edit();
						ed.putString("image", jsonObject.getString("image"));
						ed.commit();

						// Image upload success, perform actions accordingly
						((UpdateProfile) activity).setResultsFromImageUpload();
					} catch (JSONException e) {
						errorType = "j";
						Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					errorType = "j";
					try {
						String content = new String(responseBody);
						DATA.print("-- onfailure : "+"Upload Image Profile"+content);
						Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
						new GloabalMethods(activity).checkLogin(content,statusCode);

					}catch (Exception e1){
						e1.printStackTrace();
						Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFinish() {
					super.onFinish();
					pd.dismiss();
				}
			});
		} catch (Exception e) {
			errorType = "j";
			Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}


	/*@Override
	protected void onPreExecute() {
		super.onPreExecute();

		pd = new ProgressDialog(activity);
//		pd.setTitle("Uploading image on server");
		//pd.setIcon(R.drawable.ic_launcher);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("Updating profile image...");
		pd.show();

			//url = "http://vegatechnologies.net/self/app2/lib/";
			//url = "http://vegatechnologies.net/self/app2/lib/";
	}



	@Override
	protected Boolean doInBackground(String... params) {



		url = DATA.baseUrl+"userProfileImg";

		try {

			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;

			String pathToOurFile = DATA.imagePath;
			DATA.print("--path: "+pathToOurFile);

			String urlServer = url;
			DATA.print("--urlServer: "+urlServer);

			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary =  "*****";

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;

			try
			{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");//http://3melements.com/onlineclinic/images/4779782887_0_doctor_user.jpg

			 imageName = prefs.getString("id", "")+"_"+prefs.getString("subPatientID", "0") + "_doctor_image.jpg";
				DATA.print("--online care dr IMAGE NAME: "+imageName);

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageName +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				DATA.print("--bytesRead: "+bytesRead);
				DATA.print("--bytesAvailable: "+bytesAvailable);

			outputStream.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
		 	int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			msg = serverResponseMessage;
			status = ""+serverResponseCode;

			DATA.print("--online care msg: "+msg);
			DATA.print("--online care status: "+status);

			if(msg.equals("OK")) {

				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                value = sb.toString();

                DATA.print("--value in image upload response "+value);

                jsonObject = new JSONObject(value);

				SharedPreferences.Editor ed = prefs.edit();
				ed.putString("image",jsonObject.getString("image"));
				ed.commit();


			}

			else {
				errorType = "j";
			}



			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			}
			catch (Exception ex)
			{
			//Exception handling
				DATA.print("--exception ex: "+ex);

			}
		} catch (Exception e) {
			errorType = "j";

			DATA.print("--exception: "+e);
			e.printStackTrace();
		}


		return true;


	}




	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);



		DATA.print("--errorType: "+errorType);
		DATA.print("--value in onPostExecute: "+value);

	if(errorType.equals("I")) {

		DATA.imagePath = "";
		DATA.isImageCaptured = false;


		Toast.makeText(activity, "No Internet connection found", 0).show();
	}
	else if(errorType.equals("j")) {

		DATA.imagePath = "";
		DATA.isImageCaptured = false;


		Toast.makeText(activity, "Something went wrong, please try again", 0).show();
	}

	else {

		//Toast.makeText(activity, "Image uploaded...", 0).show();

		((UpdateProfile)activity).setResultsFromImageUpload();

	}

		pd.dismiss();
	}*/

}
