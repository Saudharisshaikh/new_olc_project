package com.app.OnlineCareTDC_Pt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.app.OnlineCareTDC_Pt.api.ApiCallBack;
import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.util.CustomToast;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class GiveNameReportDialog extends Activity implements ApiCallBack {
	
	Activity activity;
	ApiCallBack apiCallBack;
	SharedPreferences prefs;

	CustomToast customToast;

	TextView tvReportNameOK,tvDialog1;
	
	EditText etReportName,etFolderName;
	
	String createdFolderName = "";

	@Override
	protected void onResume() {
		super.onResume();


		/*if(DATA.isFromReportsFolder) {
			tvDialog1.setText("Create new folder");
			etFolderName.setVisibility(View.VISIBLE);
		} else {
			tvDialog1.setText("Give name to your report");
			etFolderName.setVisibility(View.GONE);
		}*/
		tvDialog1.setText("Give name to your report");
		etFolderName.setVisibility(View.GONE);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setFinishOnTouchOutside(false);

		setContentView(R.layout.give_name_to_report);
		
		activity = GiveNameReportDialog.this;
		apiCallBack = this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		//UploadService.NAMESPACE = "com.app.onlinecare";

		etReportName = (EditText) findViewById(R.id.etReportName);
		etFolderName = (EditText) findViewById(R.id.etFolderName);

		tvReportNameOK = (TextView) findViewById(R.id.tvReportNameOK);
		tvDialog1 = (TextView) findViewById(R.id.tvDialog1);
		tvReportNameOK.setText("Save and upload");			
		
		tvReportNameOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String selectedReprtName = etReportName.getText().toString().trim();
				if(! selectedReprtName.isEmpty()) {
					//uploadFile(getBaseContext());
					//finish();
					uploadReport(selectedReprtName);
				} else {
					customToast.showToast("Please enter report name", 0, 0);
				}

				/*if(DATA.isFromReportsFolder) {
					if((etReportName.getText().toString().isEmpty())) {
						customToast.showToast("Please enter report name", 0, 0);
					} else if (etFolderName.getText().toString().isEmpty()) {
						customToast.showToast("You must give a folder name", 0, 0);
					} else {
						DATA.selectedReprtName = etReportName.getText().toString();
						createdFolderName = etFolderName.getText().toString();
						//uploadFile(getBaseContext());
						//finish();
						uploadReport();
					}
				} else {
					if(!(etReportName.getText().toString().isEmpty())) {
						DATA.selectedReprtName = etReportName.getText().toString();
						//uploadFile(getBaseContext());
						//finish();
						uploadReport();
					} else {
						customToast.showToast("Please enter report name", 0, 0);
					}
				}*/
			}
		});



		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		//lp.gravity = Gravity.BOTTOM;
		//lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		this.getWindow().setAttributes(lp);*/
		
	}
//http://www.cbuystore.com/onlineclinic/index.php/app/uploadReports
	//DATA.baseUrl+"uploadReports"
	/*public void uploadFile(final Context context) {
	    final UploadRequest request = new UploadRequest(context, "1" , "http://www.cbuystore.com/onlineclinic/index.php/app/uploadReports");
           DATA.print("--url "+DATA.baseUrl+"uploadReports");
	    request.setMethod("POST");
	    
	     * parameter-name: is the name of the parameter that will contain file's data.
	     * Pass "uploaded_file" if you're using the test PHP script
	     *
	     * custom-file-name.extension: is the file name seen by the server.
	     * E.g. value of $_FILES["uploaded_file"]["name"] of the test PHP script
	     
	    if(DATA.selectedReprtExtension.equals(".png")) {

		    request.addFileToUpload(DATA.selectedReprtPath,
                    "file",
                    DATA.selectedReprtName+DATA.selectedReprtExtension,
                    ContentType.IMAGE_PNG);
	    }
	    else if(DATA.selectedReprtExtension.equals(".jpg") ||(DATA.selectedReprtExtension.equals(".jpeg"))) {

	    	request.addFileToUpload(DATA.selectedReprtPath,
                    "file",
                    DATA.selectedReprtName+DATA.selectedReprtExtension,
                    ContentType.IMAGE_JPEG);
	    }

    	DATA.print("--online care report path: "+DATA.selectedReprtPath);

//	    You can add your own custom headers
//	  request.addHeader("Content-Type", "application/x-www-form-urlencoded");
	    
	    if(DATA.isFromReportsFolder) {
	    	
	    	DATA.print("--online care selected folder id: "+DATA.selectedReprtFoldrId);
	    	DATA.print("--online care selected file name: "+DATA.selectedReprtName);

	    	DATA.print("--online care from folder");
		    //and parameters
		    request.addParameter("patient_id", prefs.getString("id", ""));
		    request.addParameter("sub_patient_id", prefs.getString("subPatientID", "0"));
		    request.addParameter("folder_id", "0");
		    request.addParameter("folder_name", createdFolderName);
		    request.addParameter("file_display_name", DATA.selectedReprtName);
		    
	    }
	    else {

	    	DATA.print("--online care from reports");
	    	
	    	DATA.print("--online care selected folder id: "+DATA.selectedReprtFoldrId);
	    	DATA.print("--online care selected file name: "+DATA.selectedReprtName);

		    //and parameters
		    request.addParameter("patient_id", prefs.getString("id", ""));
		    request.addParameter("sub_patient_id", prefs.getString("subPatientID", "0"));
		    request.addParameter("folder_id", DATA.selectedReprtFoldrId);
		    request.addParameter("file_display_name", DATA.selectedReprtName);

	    }


	 
	 	    //configure the notification
	    request.setNotificationConfig(android.R.drawable.ic_menu_upload,
	                                  "New Report Upload",
	                                  "upload in progress",
	                                  "upload completed successfully",
	                                  "something went wrong, please try again",
	                                  false);

	    try {
	        //Start upload service and display the notification
	        UploadService.startUpload(request);

	    } catch (Exception exc) {
	    	DATA.print("--exception in uploadreport");
	    	exc.printStackTrace();
	        //You will end up here only if you pass an incomplete UploadRequest
	        Log.e("AndroidUploadService", exc.getLocalizedMessage(), exc);
	    }
	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		//DATA.selectedReprtExtension = "";
		//DATA.selectedReprtName = "";
		DATA.selectedReprtPath = "";
	}
	
	
	
	 /*request.addParameter("patient_id", prefs.getString("id", ""));
	    request.addParameter("sub_patient_id", prefs.getString("subPatientID", "0"));
	    request.addParameter("folder_id", "0");
	    request.addParameter("folder_name", createdFolderName);
	    request.addParameter("file_display_name", DATA.selectedReprtName);
	    
	    * request.addFileToUpload(DATA.selectedReprtPath,
                    "file",
                    DATA.selectedReprtName+DATA.selectedReprtExtension,
                    ContentType.IMAGE_PNG);
	    */
	
	 public void uploadReport(String selectedReprtName) {
		 RequestParams params = new RequestParams();
		 /*if(DATA.isFromReportsFolder) {
			 DATA.print("--online care selected folder id: "+DATA.selectedReprtFoldrId);
			 DATA.print("--online care selected file name: "+DATA.selectedReprtName);
			 DATA.print("--online care from folder");
			 params.put("patient_id", prefs.getString("id", ""));
			 params.put("sub_patient_id", prefs.getString("subPatientID", "0"));
			 params.put("folder_id", "0");
			 params.put("folder_name", createdFolderName);
			 params.put("file_display_name",  DATA.selectedReprtName);
		 }else {
			 DATA.print("--online care from reports");
			 DATA.print("--online care selected folder id: "+DATA.selectedReprtFoldrId);
			 DATA.print("--online care selected file name: "+DATA.selectedReprtName);
			 //and parameters
			 params.put("patient_id", prefs.getString("id", ""));
			 params.put("sub_patient_id", prefs.getString("subPatientID", "0"));
			 params.put("folder_id", DATA.selectedReprtFoldrId);
			 params.put("file_display_name", DATA.selectedReprtName);
		 }*/
		 params.put("patient_id", prefs.getString("id", ""));
		 params.put("sub_patient_id", prefs.getString("subPatientID", "0"));
		 params.put("folder_id", DATA.selectedReprtFoldrId);
		 params.put("file_display_name", selectedReprtName);
		 try {
			 DATA.print("-- file path "+DATA.selectedReprtPath);
			 params.put("file", new File(DATA.selectedReprtPath));
		 } catch (FileNotFoundException e) {
			 e.printStackTrace();
		 }

		 ApiManager apiManager = new ApiManager(ApiManager.UPLOAD_REPORT,"post",params,apiCallBack, activity);
		 apiManager.loadURL();
			 
		}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.UPLOAD_REPORT)){
			//-- responce in uploadReport {"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success")) {

					customToast.showToast("Report Uploaded successfully",0,0);

					Reports.isNewReportUloaded = true;
				} else {
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
			finish();
		}
	}
}