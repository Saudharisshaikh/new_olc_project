package com.onlinecare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.onlinecare.adapter.FoldersAdapter;
import com.covacard.api.ApiManager;
import com.covacard.BaseActivity;
import com.covacard.R;
import com.onlinecare.model.ReportsModel;
import com.covacard.util.CheckInternetConnection;
import com.covacard.util.CustomToast;
import com.covacard.util.DATA;
import com.covacard.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportFolders extends BaseActivity {

	//AbstractUploadServiceReceiver uploadReceiver;


	// Stores names of traversed directories
	/*ArrayList<String> str = new ArrayList<String>();

	private Boolean firstLvl = true;

	private static final String TAG = "F_PATH";

	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;
	private int FILE_SELECT_CODE = 4347;*/

	ListAdapter adapter;


	Activity activity;

	FoldersAdapter foldersAdapter;
	GridView gridView;
	TextView tvNoFolders;
	SharedPreferences prefs;
	JSONObject jsonObject;
	AsyncHttpClient client;
	String msg, status;
	//ProgressDialog pd;
	TextView tvReportsNewFolder;
	ImageView imgAdReportsfolder;

	JSONArray foldersArray, reportsArray;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	OpenActivity openActivity;

	public static boolean reloadRepoerts = false;

	@Override
	protected void onPause() {
		super.onPause();
		//uploadReceiver.unregister(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(reloadRepoerts){
			reloadRepoerts = false;
			getReportsCall();
		}
		/*if (DATA.isImageCaptured) {//this block will not be used any more
			DATA.selectedReprtPath = DATA.imagePath;
			DATA.selectedReprtExtension = DATA.selectedReprtPath.substring(DATA.selectedReprtPath.lastIndexOf("."));
			DATA.isImageCaptured = false;
			DATA.imagePath = "";
			DATA.isFromReportsFolder = true;
			openActivity.open(GiveNameReportDialog.class, false);
		} else {
			if(reloadRepoerts){
				reloadRepoerts = false;
				getReportsCall();
			}
		}*/

		//uploadReceiver.register(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports_folders);

		activity = ReportFolders.this;
		gridView = (GridView) findViewById(R.id.gridFolders);
		tvNoFolders = findViewById(R.id.tvNoFolders);

		openActivity = new OpenActivity(activity);

		//pd = new ProgressDialog(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		//pd.setMessage("Loading Reports...");

		tvReportsNewFolder = (TextView) findViewById(R.id.tvReportsNewFolder);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast =  new CustomToast(activity);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				DATA.selectedReprtFoldrId = DATA.allFolders.get(position).folder_id;

				filterReports(DATA.selectedReprtFoldrId);
				openActivity.open(Reports.class, false);

				/*final Dialog dialog = new Dialog(activity);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_report_folder);
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);
				Button btnOpenFolder = (Button) dialog.findViewById(R.id.btnOpenFolder);
				Button btnDeleteFolder = (Button) dialog.findViewById(R.id.btnDeleteFolder);
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


		tvReportsNewFolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				/*loadFileList();
				showDialog(DIALOG_LOAD_FILE);
				Log.d(TAG, path.getAbsolutePath());*/
				//openActivity.open(ChoosePictureDialog.class, false);

				createFolderDialog();
			}
		});
		
		 /*uploadReceiver =
				    new AbstractUploadServiceReceiver() {

				        @Override
				        public void onProgress(String uploadId, int progress) {
//				            Log.i(TAG, "The progress of the upload with ID "
//				                       + uploadId + " is: " + progress);
				            
				            System.out.println("--online care: The progress of the upload with ID "
				                       + uploadId + " is: " + progress);
				        }

				        @Override
				        public void onError(String uploadId, Exception exception) {
//				            Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
//				                       + exception.getLocalizedMessage(), exception);
				            
				            System.out.println("--online care: Error in upload with ID: " + uploadId + ". "
				                       + exception.getLocalizedMessage()+ exception);
				        }

				        @Override
				        public void onCompleted(String uploadId,
				                                int serverResponseCode,
				                                String serverResponseMessage) {
//				            Log.i(TAG, "Upload with ID " + uploadId
//				                       + " has been completed with HTTP " + serverResponseCode
//				                       + ". Response from server: " + serverResponseMessage);

				            System.out.println("--online care: Upload with ID " + uploadId
				                       + " has been completed with HTTP " + serverResponseCode
				                       + ". Response from server: " + serverResponseMessage);
				            //If your server responds with a JSON, you can parse it
				            //from serverResponseMessage string using a library 
				            //such as org.json (embedded in Android) or google's gson
				        }
				    };*/



		imgAdReportsfolder = (ImageView) findViewById(R.id.imgAdReportsfolder);
		imgAdReportsfolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DATA.addIntent(activity);
			}
		});



		getReportsCall();

	}//end onCreate

	public void confirmDeleteFolderDialog(String folderID){

		new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
				.setTitle("Confirm")
				.setMessage("Are you sure? Do you want to delete this folder? Please make sure as all medical report files under this folder will not be accessible anymore.")
				.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkInternetConnection.isConnectedToInternet()) {
							deleteReportFolder(folderID);
						} else {
							customToast.showToast("No internet connection", 0, 0);
						}
					}
				})
				.setNegativeButton("Not Now", null)
				.create()
				.show();

//		new SweetAlertDialog(ReportFolders.this, SweetAlertDialog.WARNING_TYPE)
//				.setTitleText("Are you sure?")
//				.setContentText("You will not be able to recover this folder")
//				.setCancelText("Cancel")
//				.setConfirmText("Delete")
//				.showCancelButton(true)
//				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//					@Override
//					public void onClick(SweetAlertDialog sDialog) {
//						// reuse previous dialog instance, keep widget user state, reset them if you need
//						sDialog.setTitleText("Cancelled")
//								.setContentText("Your medical reports folder not deleted")
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
//	                               /* sDialog.setTitleText("Deleted!")
//	                                        .setContentText("Your imaginary file has been deleted!")
//	                                        .setConfirmText("OK")
//	                                        .showCancelButton(false)
//	                                        .setCancelClickListener(null)
//	                                        .setConfirmClickListener(null)
//	                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
//						if (checkInternetConnection.isConnectedToInternet()) {
//							sDialog.dismiss();
//							deleteReportFolder(DATA.selectedReprtFoldrId);
//						} else {
//							sDialog.dismiss();
//							customToast.showToast("No internet connection", 0, 0);
//						}
//					}
//				})
//				.show();
	}


	private void getReportsCall() {

		String savedData = sharedPrefsHelper.get("patient_medical_reports_folders", "");

		if(!TextUtils.isEmpty(savedData)){
			parseReportsData(savedData);
			ApiManager.shouldShowLoader = false;
		}

		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));//patient id
		//params.put("sub_patient_id", prefs.getString("subPatientID", ""));//sub patient id   this causes issue no folder shown 8/11/2018

		System.out.println("--URL: "+DATA.baseUrl+"getReports");
		System.out.println("--params "+params.toString());

		ApiManager apiManager = new ApiManager(ApiManager.GET_REPORTS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void deleteReportFolder(String folderId) {
		ApiManager apiManager = new ApiManager(ApiManager.DELETE_FOLDER+folderId ,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	Dialog createFolderDialog;
	public void createFolderDialog() {
		createFolderDialog = new Dialog(activity);
		createFolderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		createFolderDialog.setContentView(R.layout.dialog_create_folder);
		createFolderDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);
		final EditText etFolderName = (EditText) createFolderDialog.findViewById(R.id.etFolderName);
		Button btnCreateFolder = (Button) createFolderDialog.findViewById(R.id.btnCreateFolder);


		btnCreateFolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					String folderName = etFolderName.getText().toString();
					if (folderName.isEmpty()) {
						customToast.showToast("please enter folder name", 0, 0);
					} else {
						create_folder(folderName);
					}
				} else {
					customToast.showToast("No internet connection", 0, 0);
				}
			}
		});
		createFolderDialog.show();
	}


	public void create_folder(String folderName) {
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("folder_name", folderName);

		ApiManager apiManager = new ApiManager(ApiManager.CREATE_FOLDER,"post",params,apiCallBack, activity);
		apiManager.loadURL();

	}//end create_folder


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.GET_REPORTS)){
			parseReportsData(content);
		}else if(apiName.equalsIgnoreCase(ApiManager.CREATE_FOLDER)){
			//{"success":"Created","reload":1}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success") && jsonObject.getString("success").equalsIgnoreCase("Created")) {

					createFolderDialog.dismiss();
					getReportsCall();

				} else {
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.contains(ApiManager.DELETE_FOLDER)){
			/*new SweetAlertDialog(ReportFolders.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Deleted!")
                 .setContentText("Your imaginary file has been deleted!")
                 .setConfirmText("OK")
                 .showCancelButton(false)
                 .setCancelClickListener(null)
                 .setConfirmClickListener(null)
                 .show();//changeAlertType(SweetAlertDialog.SUCCESS_TYPE)*/

			//-- responce in deleteReportFolder {"success":1,"message":"Deleted."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
					customToast.showToast("Folder deleted successfully", 0, 0);
					getReportsCall();
				} else {
					customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
				}
			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
				e.printStackTrace();
			}
		}
	}

	public void parseReportsData(String content){

		try {
			jsonObject = new JSONObject(content);
			status = jsonObject.getString("status");
			//			msg = jsonObject.getString("msg");
			if(status.equals("success")) {

				String foldrstr = jsonObject.getString("folders");

				foldersArray = new JSONArray(foldrstr);
				DATA.allFolders = new ArrayList<ReportsModel>();

				ReportsModel temp;

				for (int i = 0; i < foldersArray.length(); i++) {

					temp = new ReportsModel();
					temp.folder_id = foldersArray.getJSONObject(i).getString("id");
					temp.folder_name = foldersArray.getJSONObject(i).getString("folder_name");

					DATA.allFolders.add(temp);

					temp = null;
				}

				int vis = DATA.allFolders.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoFolders.setVisibility(vis);


				String filesStr = jsonObject.getString("files");

				reportsArray = new JSONArray(filesStr);

				ReportsModel temp1;
				DATA.allReports = new ArrayList<ReportsModel>();

				for (int i = 0; i < reportsArray.length(); i++) {

					temp1 = new ReportsModel();

					temp1.id = reportsArray.getJSONObject(i).getString("id");
					temp1.name = reportsArray.getJSONObject(i).getString("file_display_name");
					temp1.report_url = reportsArray.getJSONObject(i).getString("report_url");
					temp1.report_thumb = reportsArray.getJSONObject(i).getString("report_thumb");
					temp1.folder_id = reportsArray.getJSONObject(i).getString("folder_id");
					temp1.date = reportsArray.getJSONObject(i).getString("dateof");

					DATA.allReports.add(temp1);

					temp1 = null;

				}

				foldersAdapter = new FoldersAdapter(activity, DATA.allFolders);
				gridView.setAdapter(foldersAdapter);


				sharedPrefsHelper.save("patient_medical_reports_folders", content);

			} else {
				customToast.showToast("No reports uploaded yet", 0, 0);
				tvNoFolders.setVisibility(View.VISIBLE);
			}

		} catch (JSONException e) {
			System.out.println("--Exception in getReports : "+e);
			e.printStackTrace();
			customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
		}
	}

	public void filterReports(String filterText) {

		DATA.allReportsFiltered = new ArrayList<ReportsModel>();
		ReportsModel temp;

		for(int i = 0; i<DATA.allReports.size(); i++) {

			temp = new ReportsModel();

			if(DATA.allReports.get(i).folder_id.equals(filterText)) {

				System.out.println("item added");

				temp.id = DATA.allReports.get(i).id;
				temp.name = DATA.allReports.get(i).name;
				temp.date = DATA.allReports.get(i).date;
				temp.report_url = DATA.allReports.get(i).report_url;
				temp.report_thumb = DATA.allReports.get(i).report_thumb;

				DATA.allReportsFiltered.add(temp);

				temp = null;
			}

		}

	}






	
	/*private void loadFileList() {
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

	}//end loadFileList()

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
							
							DATA.isFromReportsFolder = true;

							openActivity.open(GiveNameReportDialog.class, false);
			
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
	}*/

}
