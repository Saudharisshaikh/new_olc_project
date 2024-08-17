package com.app.mhcsn_cp.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.app.mhcsn_cp.UpdateProfile;
import com.app.mhcsn_cp.api.Dialog_CustomProgress;

public class UploadImageCall extends AsyncTask<String, String, Boolean> {

	Activity activity;
	//ProgressDialog pd;
	Dialog_CustomProgress dialog_customProgress;
	CustomToast customToast;

	String value, errorType = "", url;
	static JSONObject jsonObject = null,userInfoObject = null;

	SharedPreferences prefs;
	String msg, status;

	String imageName;

	public UploadImageCall(Activity activity) {
		this.activity = activity;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		dialog_customProgress = new Dialog_CustomProgress(activity);
		customToast = new CustomToast(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		/*pd = new ProgressDialog(activity);
//		pd.setTitle("Uploading image on server");
		//pd.setIcon(R.drawable.ic_launcher);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("Updating profile image...");
		pd.show();*/

		dialog_customProgress.showProgressDialog();

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
				connection.setRequestMethod("POST");//doctor   specialist

				imageName = prefs.getString("id", "")+"_"+prefs.getString("subPatientID", "0") + "_doctor_image.jpg";
				DATA.print("--online care IMAGE NAME: "+imageName);

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


			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
		}
		else if(errorType.equals("j")) {
			DATA.imagePath = "";
			DATA.isImageCaptured = false;
			customToast.showToast(DATA.CMN_ERR_MSG,0,0);
		}
		else {
			//Toast.makeText(activity, "Image uploaded...", 0).show();
			((UpdateProfile)activity).setResultsFromImageUpload();
		}
		dialog_customProgress.dismissProgressDialog();
	}

}
