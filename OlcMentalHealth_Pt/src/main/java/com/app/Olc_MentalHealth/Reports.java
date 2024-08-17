package com.app.Olc_MentalHealth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.app.Olc_MentalHealth.adapter.ReportsGridAdapter;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.model.ReportsModel;
import com.app.Olc_MentalHealth.util.CheckInternetConnection;
import com.app.Olc_MentalHealth.util.ChoosePictureDialog;
import com.app.Olc_MentalHealth.util.CustomToast;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.OpenActivity;
import com.app.Olc_MentalHealth.util.UriUtilGM;
import com.app.Olc_MentalHealth.R;
import com.braver.tool.picker.BraverDocPathUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;


public class Reports extends BaseActivity {

	// Stores names of traversed directories
	//ArrayList<String> str = new ArrayList<String>();

	// Check if the first level of the directory structure is the one showing
	//private Boolean firstLvl = true;

	//private static final String TAG = "F_PATH";

	//private Item[] fileList;
	//private File path = new File(Environment.getExternalStorageDirectory() + "");
	//private String chosenFile;
	//private static final int DIALOG_LOAD_FILE = 1000;

	//ListAdapter adapter;

	AppCompatActivity activity;

	//ListView lvReports;
	//static ReportsAdapter reportsAdapter;
	ReportsModel temp;
	Button btnUploadReport;
	SharedPreferences prefs;
	String msg, status;

	private String tempFileAbsolutePath = "";

	JSONObject jsonObject;
	//AsyncHttpClient client;

	//private int FILE_SELECT_CODE = 2342;

	//CallWebService callWebService;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	//ProgressDialog pd;
	OpenActivity openActivity;

	ReportsGridAdapter reportsGridAdapter;
	GridView gridView;
	TextView tvNoReport;

	public static boolean isNewReportUloaded = false;

	public static final int IMAGE_PICKER_CODE13 = 420;
	public static final int PICKFILE_REQUEST_CODE13 = 4290;

	public static boolean imagefor13 = false;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//DATA.isFromUploadReport = false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		//note : this code will not exicute as report is now not slected via ChoosePictureDialog . GM
		/*if (DATA.isImageCaptured) {
			DATA.selectedReprtPath = DATA.imagePath;
			DATA.selectedReprtExtension = DATA.selectedReprtPath.substring(DATA.selectedReprtPath.lastIndexOf("."));
			
			DATA.isImageCaptured = false;
			DATA.imagePath = "";
			
			DATA.isFromReportsFolder = false;
			openActivity.open(GiveNameReportDialog.class, false);
		}else*/

		if(isNewReportUloaded){
			isNewReportUloaded = false;
			getreportsByfolder(DATA.selectedReprtFoldrId);
		}
		System.out.println("-- imagefor13 status " + imagefor13);
		//ahmer work for android 12+
		if (imagefor13) {
			openActivity.open(GiveNameReportDialog.class, false);
		}
		//if(DATA.isFromUploadReport && DATA.isImageCaptured) {}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);

		activity = Reports.this;

		//pd = new ProgressDialog(activity);
		//pd.setMessage("Uploading your report...");

		//callWebService = new CallWebService(activity, pd);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast =  new CustomToast(activity);
		openActivity = new OpenActivity(activity);

		//lvReports = (ListView) findViewById(R.id.lvReports);		

		btnUploadReport = (Button) findViewById(R.id.btnUploadReport);
		tvNoReport = findViewById(R.id.tvNoReport);
		gridView = findViewById(R.id.lvReports);
		reportsGridAdapter = new ReportsGridAdapter(activity);
		gridView.setAdapter(reportsGridAdapter);
//		reportsAdapter = new ReportsAdapter(activity);

		int vis = DATA.allReportsFiltered.isEmpty() ? View.VISIBLE : View.GONE;
		tvNoReport.setVisibility(vis);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				DATA.selctdReprtURLForVu = DATA.allReportsFiltered.get(position).report_url;

				String extention = DATA.selctdReprtURLForVu.substring(DATA.selctdReprtURLForVu.lastIndexOf("."));

				DATA.selctdReprtName = DATA.allReportsFiltered.get(position).name+extention;

				//DATA.selectedReportId = DATA.allReportsFiltered.get(position).id;

				openActivity.open(ViewReportImage.class, false);


//				if(extention.equals(".png")
//						|| extention.equals(".jpg")
//						|| extention.equals(".jpeg")) {
//
//
//					openActivity.open(ViewReportImage.class, false);
//				}
//				else if(extention.equals("doc")
//					|| extention.equals("docx")) {
//					
//				}
//				else if(extention.equals("pdf")) {
//						
//					}

				/*final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_report_folder);
				Button btnOpenFolder = (Button) dialog.findViewById(R.id.btnOpenFolder);
				Button btnDeleteFolder = (Button) dialog.findViewById(R.id.btnDeleteFolder);
				btnOpenFolder.setText("View Report");
				btnDeleteFolder.setText("Delete Report");
				
				btnOpenFolder.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) { dialog.dismiss(); }
				});
				btnDeleteFolder.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) { dialog.dismiss(); }
				});
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(dialog.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			    dialog.show();
			    dialog.getWindow().setAttributes(lp);*/

			}
		});

		btnUploadReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showPictureDialog();

				//openActivity.open(ChoosePictureDialog.class, false);

				/*loadFileList();
				showDialog(DIALOG_LOAD_FILE);
				Log.d(TAG, path.getAbsolutePath());*/

			}
		});


		findViewById(R.id.imgAdReports).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DATA.addIntent(activity);
			}
		});
	}//oncreate


	public void confirmDeleteReportDialog(String reportID){

		new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
				.setTitle("Confirm")
				.setMessage("Are you sure? Do you want to delete this medical report?")
				.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkInternetConnection.isConnectedToInternet()) {
							deleteReport(reportID);
						} else {
							customToast.showToast("No internet connection", 0, 0);
						}
					}
				})
				.setNegativeButton("Not Now", null)
				.create()
				.show();

//		new SweetAlertDialog(Reports.this, SweetAlertDialog.WARNING_TYPE)
//				.setTitleText("Are you sure?")
//				.setContentText("You will not be able to recover this file")
//				.setCancelText("Cancel")
//				.setConfirmText("Delete")
//				.showCancelButton(true)
//				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//					@Override
//					public void onClick(SweetAlertDialog sDialog) {
//						sDialog.dismiss();
//						// reuse previous dialog instance, keep widget user state, reset them if you need
//						sDialog.setTitleText("Cancelled")
//								.setContentText("Your file not deleted")
//								.setConfirmText("OK")
//								.showCancelButton(false)
//								.setCancelClickListener(null)
//								.setConfirmClickListener(null)
//								.changeAlertType(SweetAlertDialog.ERROR_TYPE);
//
//						// or you can new a SweetAlertDialog to show
//	                               /* sDialog.dismiss();
//	                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
//	                                        .setTitleText("Cancelled!")
//	                                        .setContentText("Your imaginary file is safe :)")
//	                                        .setConfirmText("OK")
//	                                        .show();*/
//					}
//				})
//				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//					@Override
//					public void onClick(SweetAlertDialog sDialog) {
//						sDialog.dismiss();
//	                               /* sDialog.setTitleText("Deleted!")
//	                                        .setContentText("Your imaginary file has been deleted!")
//	                                        .setConfirmText("OK")
//	                                        .showCancelButton(false)
//	                                        .setCancelClickListener(null)
//	                                        .setConfirmClickListener(null)
//	                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
//						if (checkInternetConnection.isConnectedToInternet()) {
//							deleteReport(DATA.selectedReportId);
//						} else {
//							customToast.showToast("No internet connection", 0, 0);
//						}
//					}
//				})
//				.show();
	}

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Choose Folder");    
		menu.add(0, 0, 0, "Blood Suger Reports");//groupId, itemId, order, title   
		menu.add(0, 1, 0, "Ultrasounds");
		menu.add(0, 2, 0, "Other");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int selectedItemId = item.getItemId();
		Toast.makeText(activity, ""+selectedItemId, Toast.LENGTH_SHORT).show();
		return true;
	}*/

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//	{
//		if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK)
//		{
//
//			try {
//				
//				DATA.selectedReprtPath = getRealPathFromURI( data.getData());
//				DATA.selectedReprtExtension = DATA.selectedReprtPath.substring(DATA.selectedReprtPath.lastIndexOf("."));
//
//				if(DATA.selectedReprtExtension.equals(".png")
//						|| DATA.selectedReprtExtension.equals(".jpg")
//						|| DATA.selectedReprtExtension.equals(".jpeg")
//						|| DATA.selectedReprtExtension.equals(".doc")
//						|| DATA.selectedReprtExtension.equals(".docx")
//						|| DATA.selectedReprtExtension.equals(".pdf")
//						) {
//
//
//					OpenActivity op = new OpenActivity(activity);
//					op.open(GiveNameReportDialog.class, true);
//	
//				}
//				else {
//					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//					builder.setTitle("Invalid file format");
//					builder.setMessage("Only file types .png, .jpg, .doc, .docx, or .pdf are acceptable.");
//					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//
//						}
//					});
//					AlertDialog alert = builder.create();
//					alert.show();
//
//
//					DATA.selectedReprtPath = "";
//					DATA.selectedReprtExtension = "";
//				}
//
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				Log.w("Error", e.toString());
//			}
//		}
//	}
//
//	public String getRealPathFromURI (Uri contentUri)
//	{
//		String path = null;
//		String[] proj = { MediaStore.MediaColumns.DATA };
//
//		if("content".equalsIgnoreCase(contentUri.getScheme ()))
//		{
//			Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//			if (cursor.moveToFirst()) {
//				int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//				path = cursor.getString(column_index);
//			}
//			cursor.close();
//			return path;
//		}
//		else if("file".equalsIgnoreCase(contentUri.getScheme()))
//		{
//			return contentUri.getPath();
//		}
//		return null;
//	}
//
//	public void openFile(String minmeType) {
//
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType(minmeType);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//		// special intent for Samsung file manager
//		Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
//		// if you want any file type, you can skip next line 
//		sIntent.putExtra("CONTENT_TYPE", minmeType); 
//		sIntent.addCategory(Intent.CATEGORY_DEFAULT);
//
//		Intent chooserIntent;
//		if (getPackageManager().resolveActivity(sIntent, 0) != null){
//			// it is device with samsung file manager
//			chooserIntent = Intent.createChooser(sIntent, "Open file");
//			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
//		}
//		else {
//			chooserIntent = Intent.createChooser(intent, "Open file");
//		}
//
//		try {
//			startActivityForResult(chooserIntent, FILE_SELECT_CODE);
//		} catch (android.content.ActivityNotFoundException ex) {
//			Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
//		}
//	}


//	public void uploadFile(final Context context) {
//		final UploadRequest request = new UploadRequest(context, "1" , DATA.baseUrl+"uploadReports");
//
//		/*
//		 * parameter-name: is the name of the parameter that will contain file's data.
//		 * Pass "uploaded_file" if you're using the test PHP script
//		 *
//		 * custom-file-name.extension: is the file name seen by the server.
//		 * E.g. value of $_FILES["uploaded_file"]["name"] of the test PHP script
//		 */
//		request.addFileToUpload(path,
//				"file",
//				""+extention,
//				"*/*");
//
//		//You can add your own custom headers
//		request.addHeader("your-custom-header", "your-custom-value");
//
//		//and parameters
//		request.addParameter("patient_id", prefs.getString("id", ""));
//		request.addParameter("sub_patient_id", prefs.getString("subPatientID", ""));
//		request.addParameter("folder_id", DATA.selectedReprtFoldrId);
//
//
//		//configure the notification
//		request.setNotificationConfig(android.R.drawable.ic_menu_upload,
//				"Uploading Report",
//				"upload in progress",
//				"upload completed successfully",
//				"something went wrong, please try again",
//				false);
//
//		try {
//			//Start upload service and display the notification
//			UploadService.startUpload(request);
//
//		} catch (Exception exc) {
//			//You will end up here only if you pass an incomplete UploadRequest
//			Log.e("AndroidUploadService", exc.getLocalizedMessage(), exc);
//		}
//	}
	
	/*
	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card ");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())
							&& !sel.isHidden();

				}
			};

			String[] fList = path.list(filter);
			fileList = new Item[fList.length];
			for (int i = 0; i < fList.length; i++) {
				fileList[i] = new Item(fList[i], R.drawable.file_icon);

				// Convert into file path
				File sel = new File(path, fList[i]);

				// Set drawables
				if (sel.isDirectory()) {
					fileList[i].icon = R.drawable.directory_icon;
					Log.d("DIRECTORY", fileList[i].file);
				} else {
					Log.d("FILE", fileList[i].file);
				}
			}

			if (!firstLvl) {
				Item temp[] = new Item[fileList.length + 1];
				for (int i = 0; i < fileList.length; i++) {
					temp[i + 1] = fileList[i];
				}
				temp[0] = new Item("Up", R.drawable.directory_up);
				fileList = temp;
			}
		} else {
			Log.e(TAG, "path does not exist");
		}

		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1,
				fileList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
		};

	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose file to upload");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;
					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}
					// File picked
					else {
						
						try {
						
						DATA.selectedReprtPath = path + "/" + chosenFile;
						DATA.selectedReprtExtension = DATA.selectedReprtPath.substring(DATA.selectedReprtPath.lastIndexOf("."));
		
						if(DATA.selectedReprtExtension.equals(".png")
								|| DATA.selectedReprtExtension.equals(".jpg")
								|| DATA.selectedReprtExtension.equals(".jpeg")
//								|| DATA.selectedReprtExtension.equals(".doc")
//								|| DATA.selectedReprtExtension.equals(".docx")
//								|| DATA.selectedReprtExtension.equals(".pdf")
//								|| DATA.selectedReprtExtension.equals(".xls")
//								|| DATA.selectedReprtExtension.equals(".xlsx")
								) {
							
							DATA.isFromReportsFolder = false;
		
		
							OpenActivity op = new OpenActivity(activity);
							op.open(GiveNameReportDialog.class, true);
			
						}
						else {
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
							builder.setTitle("Invalid file format");
							builder.setMessage("Only file types .png, .jpeg and .jpg are acceptable.");
							builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		
								@Override
								public void onClick(DialogInterface dialog, int which) {
		
								}
							});
							AlertDialog alert = builder.create();
							alert.show();
		
		
							DATA.selectedReprtPath = "";
							DATA.selectedReprtExtension = "";
						}
		
		
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.w("Error", e.toString());
					}
					}

				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}

      */

	public void deleteReport(String reportId) {
		ApiManager apiManager = new ApiManager(ApiManager.DELETE_FILE+reportId,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	public void getreportsByfolder(String folderID) {
		ReportFolders.reloadRepoerts = true;// to reload reports as new report uploaded or deleted
		ApiManager apiManager = new ApiManager(ApiManager.GET_REPORTS_BY_FOLDER_ID+folderID,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.contains(ApiManager.DELETE_FILE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
					customToast.showToast("Report Deleted Successfully", 0, 0);
					//finish();
					getreportsByfolder(DATA.selectedReprtFoldrId);
				} else {
					customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
				}
			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
				e.printStackTrace();
			}
		}else if(apiName.contains(ApiManager.GET_REPORTS_BY_FOLDER_ID)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				ReportsModel temp1;
				DATA.allReportsFiltered = new ArrayList<ReportsModel>();
				for (int i = 0; i < data.length(); i++) {

					temp1 = new ReportsModel();

					temp1.id = data.getJSONObject(i).getString("id");
					temp1.name = data.getJSONObject(i).getString("file_display_name");
					temp1.report_url = data.getJSONObject(i).getString("report_name");
					temp1.report_thumb = data.getJSONObject(i).getString("report_name"); //= data.getJSONObject(i).getString("report_thumb");
					temp1.folder_id = data.getJSONObject(i).getString("folder_id");
					temp1.date = data.getJSONObject(i).getString("dateof");

					DATA.allReportsFiltered.add(temp1);

					temp1 = null;
				}

				Collections.reverse(DATA.allReportsFiltered);//GM added to show latest data first

				reportsGridAdapter = new ReportsGridAdapter(activity);
				gridView.setAdapter(reportsGridAdapter);

				int vis = DATA.allReportsFiltered.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoReport.setVisibility(vis);

				DATA.print("-- DATA.allReportsFiltered list size: "+DATA.allReportsFiltered.size());
			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
				e.printStackTrace();
			}
		}
	}


	//===========================Pick Image==========================================
	public void showPictureDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_pick_reort);
		dialogSupport.setCancelable(false);

		Button btnImage = dialogSupport.findViewById(R.id.btnGallery);
		Button btnFile = dialogSupport.findViewById(R.id.btnCapture);
		Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

		btnImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					if (checkPermission13()) {
						Intent intent = new Intent(activity, ChoosePictureDialog.class);
						intent.putExtra("isForProfilePic", true);
						startActivity(intent);
                        /*Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKER_CODE13);*/
					} else {
						requestPermissions();
					}
				} else {
					if (checkPermission()) {
						onPickPhoto();
					} else {
						requestPermissions();
					}
				}
				//start();//previous image picker

				//activity.finish();
                /*Intent intent = new Intent(activity, ActivityHome.class);//.getApplicationContext()
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);*/
			}
		});
		btnFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					if (checkPermission()) {
						openStorageAccess();
					} else {
						requestPermissions();
					}
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					if (checkPermission13()) {
						openStorageAccess();
					} else {
						requestPermissions();
					}
				}
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
					if (checkPermission()) {
						onPickDoc();
					} else {
						requestPermissions();
					}
				}

				//captureImage();//previous image picker
				//openActivity.open(ActivityCart.class,true);
				//activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialogSupport.show();

		/*String fileType = formItemBeanImg.fileType;
		DATA.print("-- filetype in showdialog method : "+fileType);
		if(!TextUtils.isEmpty(fileType)){
			if(fileType.equalsIgnoreCase("default")){
				dialogSupport.show();
			}else if(fileType.equalsIgnoreCase("image")){
				onPickPhoto();
			}else if(fileType.equalsIgnoreCase("doc") || fileType.equalsIgnoreCase("pdf") || fileType.equalsIgnoreCase("compressed")){
				onPickSpecificFileType(fileType);
			}
		}else {
			dialogSupport.show();
		}*/

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
	}


	public static final int CUSTOM_REQUEST_CODE = 532;
	private ArrayList<String> photoPaths = new ArrayList<>();
	private ArrayList<String> docPaths = new ArrayList<>();
	public void onPickPhoto() {

		FilePickerBuilder.getInstance()
				.setMaxCount(1)
				.setSelectedFiles(photoPaths)
				.setActivityTheme(R.style.FilePickerTheme)
				.setActivityTitle("Please select a report picture")
				.enableVideoPicker(false)
				.enableCameraSupport(true)
				.showGifs(false)
				.showFolderView(false)
				.enableSelectAll(true)
				.enableImagePicker(true)
				.setCameraPlaceholder(R.drawable.custom_camera)
				.withOrientation(Orientation.PORTRAIT_ONLY)
				.pickPhoto(this, CUSTOM_REQUEST_CODE);

        /*int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .enableVideoPicker(false)
                    .enableCameraSupport(true)
                    .showGifs(false)
                    .showFolderView(false)
                    .enableSelectAll(true)
                    .enableImagePicker(true)
                    .setCameraPlaceholder(R.drawable.custom_camera)
                    .withOrientation(Orientation.UNSPECIFIED)
                    .pickPhoto(this, CUSTOM_REQUEST_CODE);
        }*/
	}

	public void onPickDoc() {
		String[] zips = { ".zip", ".rar" };
		String[] pdfs = { ".pdf" };
		String[] doc = { ".doc", ".docx" };
		String[] ppt = { ".ppt", ".pptx" };
		String[] xlsx = {".xls", ".xlsx"};
		String[] txt = {".txt"};


		FilePickerBuilder.getInstance()
				.setMaxCount(1)
				.setSelectedFiles(docPaths)
				.setActivityTheme(R.style.FilePickerTheme)//DrawerTheme2
				.setActivityTitle("Please select a file")
				//.addFileSupport("ZIP", zips)
				.addFileSupport("PDF", pdfs, R.drawable.ic_pickfile_pdf)
				.addFileSupport("DOC", doc, R.drawable.ic_pickfile_docx)
				.addFileSupport("PPT", ppt, R.drawable.ic_pickfile_pptx)
				.addFileSupport("XLSX", xlsx, R.drawable.ic_pickfile_xlsx)
				//.addFileSupport("TXT", txt, R.drawable.ic_pickfile_txt)

				.enableDocSupport(false)
				.enableSelectAll(true)
				.sortDocumentsBy(SortingTypes.name)
				.withOrientation(Orientation.UNSPECIFIED)
				.pickFile(this);


        /*int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select a file")
                    .addFileSupport("ZIP", zips)
                    .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)

                    .addFileSupport("DOC", doc, R.drawable.baseline_file_copy_24)
                    .addFileSupport("PPT", ppt, R.drawable.baseline_file_copy_24)
                    .addFileSupport("XLSX", xlsx, R.drawable.baseline_file_copy_24)
                    .addFileSupport("TXT", txt, R.drawable.baseline_file_copy_24)

                    .enableDocSupport(false)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(Orientation.UNSPECIFIED)
                    .setActivityTheme(R.style.DrawerTheme2)
                    .pickFile(this);
        }*/
	}


	@Override
	protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        /*if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            //images = (ArrayList<Image>) ImagePicker.getImages(data);
            //printImages(images);
            Image image = ImagePicker.getFirstImageOrNull(data);
            //ivPreviewImg.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
            final String uri = Uri.fromFile(new File(image.getPath())).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, ivPreviewImg, R.drawable.ic_placeholder_2);
            formItemBeanImg.inputValue = image.getPath();

            return;
        }*/

		photoPaths = new ArrayList<>();
		docPaths = new ArrayList<>();
		switch (requestCode) {
			case CUSTOM_REQUEST_CODE:
				if (resultCode == Activity.RESULT_OK && data != null) {
					photoPaths = new ArrayList<>();
					photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
				}
				break;

			case FilePickerConst.REQUEST_CODE_DOC:
				if (resultCode == Activity.RESULT_OK && data != null) {
					docPaths = new ArrayList<>();
					docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
				}
				break;
			case IMAGE_PICKER_CODE13: {
				if (resultCode == activity.RESULT_OK && data != null) {
					Uri uri = null;
					if (data != null) {

						uri = data.getData();
						File file = new File(UriUtilGM.getRealPathFromUri(activity, getContentResolver(), uri));
						photoPaths = new ArrayList<>();
						DATA.print("-- photoPaths data 13 FIle  " + file);
						photoPaths.add(String.valueOf(file));
					}
				}
			}
			case PICKFILE_REQUEST_CODE13: {
				if (resultCode == activity.RESULT_OK && data != null) {
					try {
						Uri selectedDocUri = data.getData();
						tempFileAbsolutePath = BraverDocPathUtils.Companion.getSourceDocPath(Reports.this, selectedDocUri);
						DATA.print("-- " + tempFileAbsolutePath);
						docPaths = new ArrayList<>();
						docPaths.addAll(Collections.singleton(tempFileAbsolutePath));
					} catch (Exception e) {
						e.printStackTrace();
						//AppUtils.printLogConsole("activityResultLauncherForDocs", "Exception-------->" + e.getMessage());
					}
				}
			}
		}

		//addThemToView(photoPaths, docPaths);
		ArrayList<String> filePaths = new ArrayList<>();
		if (photoPaths != null) filePaths.addAll(photoPaths);

		if (docPaths != null) filePaths.addAll(docPaths);

		DATA.print("--total files: "+filePaths.size());

		if(!filePaths.isEmpty()){
			DATA.selectedReprtPath = filePaths.get(0);

			//DATA.selectedReprtExtension = DATA.selectedReprtPath.substring(DATA.selectedReprtPath.lastIndexOf("."));
			//DATA.isImageCaptured = false;
			//DATA.imagePath = "";
			//DATA.isFromReportsFolder = false;
			openActivity.open(GiveNameReportDialog.class, false);

			//formItemBeanImg.inputValue = filePaths.get(0);
			//tvFilePathGloabal.setText(filePaths.get(0));
			//ivPreviewImg.setImageResource(R.drawable.baseline_attachment_24);
            /*File tempFile = new File(filePaths.get(0));
            final String uri = Uri.fromFile(tempFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, ivPreviewImg, R.drawable.baseline_file_copy_24);


            try {
                long fileSizeMbs = (tempFile.length()/(1024))/1024;
                DATA.print("-- selected file size : "+fileSizeMbs);

                if(formItemBeanImg.fileSize > 0 &&   fileSizeMbs > formItemBeanImg.fileSize){
                    tvFilePathGloabal.setText("Please select a file within "+formItemBeanImg.fileSize+" MB");
                    tvFilePathGloabal.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_dark)));
                    btnSaveForm.setEnabled(false);
                }else {
                    tvFilePathGloabal.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));
                    btnSaveForm.setEnabled(true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }*/
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	/*============ Check Permission Code ===================*/
	private String[] PERMISSIONS = {
			android.Manifest.permission.READ_EXTERNAL_STORAGE,
			android.Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private String[] PERMISSIONSANDROID13 = {
			Manifest.permission.READ_MEDIA_IMAGES,
			android.Manifest.permission.CAMERA,
	};

	private static final int PERMISSION_REQ_CODE = 1 << 4;
	private boolean checkPermission () {
		boolean granted = true;
		for (String per : PERMISSIONS) {
			if (!permissionGranted(per)) {
				granted = false;
				break;
			}
		}
		if (granted) {

		} else {
			requestPermissions();
		}
		return granted;
	}

	private boolean checkPermission13 () {
		boolean granted = true;
		for (String per : PERMISSIONSANDROID13) {
			if (!permissionGranted(per)) {
				granted = false;
				break;
			}
		}
		if (granted) {

		} else {
			requestPermissions();
		}
		return granted;
	}

	private boolean permissionGranted(String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestPermissions () {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ActivityCompat.requestPermissions(activity, PERMISSIONSANDROID13, PERMISSION_REQ_CODE);
		} else {
			ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQ_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQ_CODE) {
			boolean granted = true;
			for (int result : grantResults) {
				granted = (result == PackageManager.PERMISSION_GRANTED);
				if (!granted) break;
			}

			if (granted) {

			} else {
				showDeniedResponse(grantResults);
				// customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
			}
		}
	}


	private void showDeniedResponse(int[] grantResults) {

		boolean shouldShowDialog = false;
		String msg = getString(R.string.allow_camera_permission_msg);

		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				//Toast.makeText(activity, "Permission not granted for: "+permissionFeatures.values()[i], Toast.LENGTH_SHORT).show();
				//msg = msg + "\n* "+permissionFeatures.values()[i];
				shouldShowDialog = true;
			}
		}


		if(shouldShowDialog){
			new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
					//.setTitle("Permission ")
					.setMessage(msg)
					.setNegativeButton("Allow Permissions", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							openAppSettings();
							/*checkPermissions();*/
						}
					})
					.setPositiveButton("Not Now", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					})
					.create().show();
		}
	}

	private void openAppSettings(){
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);
		startActivity(intent);
	}
	/*============ Check Permission Code ===================*/

	/*=======================  Ahmer work for filePicker Android 11 12 , 13 Issue ========================*/
	private void openStorageAccess() {
        /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");*/
		Intent addAttachment = new Intent(Intent.ACTION_GET_CONTENT);
		addAttachment.setType("*/*");
		//addAttachment.setAction(Intent.ACTION_GET_CONTENT);
		addAttachment.setAction(Intent.ACTION_OPEN_DOCUMENT);
		startActivityForResult(addAttachment, PICKFILE_REQUEST_CODE13);
	}
}
