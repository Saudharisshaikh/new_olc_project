package com.app.emcurama.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcurama.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChoosePictureDialog extends AppCompatActivity {

	Activity activity;

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_SELECT_FROM_GALLERY = 2;
	static final int REQUEST_CODE_CROP_IMAGE = 2121;
	//public static boolean isFromBlusGlry = false;
	Uri outputFileUri;
	//SharedPreferences prefs;

	public static boolean isFromGlry = false;

	TextView tvDialog;

	ListView lvChooseImage;

	public String listItems[];

	boolean isForProfilePic = false;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom);
		DATA.isImageCaptured = false;
		DATA.imagePath = "";
		isFromGlry = false;

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lay_choose_pic_dialog);

		/*if(getSupportActionBar() != null){getSupportActionBar().hide();}
		if(getActionBar() != null){getActionBar().hide();}*/

		isForProfilePic = getIntent().getBooleanExtra("isForProfilePic", false);//for squire crop

		overridePendingTransition(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom);

		this.setFinishOnTouchOutside(false);

		lvChooseImage = (ListView) findViewById(R.id.lvChooseImage);
		//prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		tvDialog = (TextView) findViewById(R.id.tvDialog);
		tvDialog.setText("Choose Source");
		activity = ChoosePictureDialog.this;

		listItems = new String[3];

		listItems[0] = "Open Gallery";
		listItems[1] = "Capture";
		listItems[2] = "Cancel";

		ArrayAdapter<String> picAdapter = new ArrayAdapter<String>(this,R.layout.lay_simple_list_item,  listItems);
		lvChooseImage.setAdapter(picAdapter);

		lvChooseImage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				selectItem(position);
			}
		});

	}

	private void selectItem(int position) {

		switch(position) {
			case 0:
				if (checkPermission()) { dispatchChooseFromGallery();}
				else { requestPermissions(); }
				break;
			case 1:
				if (checkPermission()) { dispatchTakePictureIntent();}
				else { requestPermissions(); }
				break;
			case 2:
				//finish();
				onBackPressed();
				break;
		}
	}

	/*============ Check Permission Code ===================*/
	private String[] PERMISSIONS = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private String[] PERMISSIONSANDROID13 = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_MEDIA_VIDEO,
			Manifest.permission.READ_MEDIA_IMAGES,
	};

	private static final int PERMISSION_REQ_CODE = 1 << 4;
	private boolean checkPermission() {
		boolean granted = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
		}
		else {
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
		}
		return granted;
	}

	private boolean permissionGranted(String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	private void requestPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ActivityCompat.requestPermissions(activity, PERMISSIONSANDROID13, PERMISSION_REQ_CODE);
		}else {
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


	String imgFilePathTemp = "";//Jugaaar
	private void dispatchTakePictureIntent() {

		long tim = System.currentTimeMillis();
		String imgName = "image_"+tim+".jpg";

		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File op = new File(dir, imgName);

		//outputFileUri = Uri.fromFile(op);//get uri from file provider  ...Android 26 GM
		outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", op);
		imgFilePathTemp = op.getAbsolutePath();

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		//===============android19
		List<ResolveInfo> resolvedIntentActivities = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
			String packageName = resolvedIntentInfo.activityInfo.packageName;
			grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			//context.revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		//==============android19

		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	private void dispatchChooseFromGallery() {

		Intent photoPickerIntent = new Intent();
		photoPickerIntent.setType("image/*");
		//photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setAction(Intent.ACTION_PICK);
		startActivityForResult(photoPickerIntent, REQUEST_SELECT_FROM_GALLERY);

	}

	//coming back from camera app take picture
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

			//DATA.isImageCaptured = true;//calling activity onresume called in new OS if this true

			//DATA.imagePath = getRealPathFromURI(outputFileUri);
			if (activity == null || outputFileUri == null) {
				DATA.print("activity: " + activity + " outputFileUri: " + outputFileUri);
				return;
			}
			//DATA.imagePath = ImageFilePath.getPath(activity, outputFileUri);//GM commented.. file provider issue android O

			DATA.imagePath = imgFilePathTemp;// temp solution....GM

			DATA.print("--online care output image path: " + DATA.imagePath);
			DATA.print("--online care output file uri: " + outputFileUri);

			runCropImage(DATA.imagePath);

		} else if (requestCode == REQUEST_SELECT_FROM_GALLERY && resultCode == RESULT_OK) {

			//DATA.isImageCaptured = true;//calling activity onresume called in new OS if this true

			Uri uri = null;
			if (data != null) {

				uri = data.getData();
				//File file = new File(ImageFilePath.getPath(activity, uri));
				File file = new File(UriUtilGM.getRealPathFromUri(activity, getContentResolver(), uri));

				DATA.imagePath = file.toString();

				isFromGlry = true;
				runCropImage(DATA.imagePath);

			}
		}
		/*else if (requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_OK) {

			DATA.isImageCaptured = true;

			//DATA.imagePath = getRealPathFromURI(outputFileUri);
			DATA.imagePath = data.getStringExtra(CropImage.IMAGE_PATH);

			DATA.print("--online care output image path after crop: " + DATA.imagePath);
			DATA.print("--online care output file uri after crop: " + outputFileUri);

			//ByteArrayOutputStream bytes = new ByteArrayOutputStream();

			//note : this is compress removed by GM. it lowers img quality too much
			*//*long tim = System.currentTimeMillis();
			String imgName = "/image_"+tim+".jpg";

			Bitmap bp = decodeSampledBitmapFromPath(DATA.imagePath, 260, 260);

			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File op = new File(dir, imgName);
			DATA.imagePath = dir+imgName;
			DATA.print("----image path after scaling: "+DATA.imagePath);

			try {

				op.createNewFile();

				//write the bytes in file
				FileOutputStream fo = new FileOutputStream(op);
				bp.compress(Bitmap.CompressFormat.JPEG, 90, fo);
				//				fo.write(bytes.toByteArray());

				// remember close de FileOutput

				fo.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*//*


			finish();

		}*/
		else if (requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_CANCELED) {

			DATA.imagePath = "";
			DATA.isImageCaptured = false;
			finish();

		} else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {

			final Uri resultUri = UCrop.getOutput(data);
			String imagePath = resultUri.getPath();

			DATA.print("-- Image path after crop in onActivityResult : " + imagePath);

			/*String uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", new File(imagePath)).toString();
			final String decoded = Uri.decode(uri);
			DATA.loadImageFromURL(decoded, ivProfileImg, R.drawable.ic_default_user_squire);*/

			DATA.isImageCaptured = true;
			//DATA.imagePath = getRealPathFromURI(outputFileUri);
			DATA.imagePath = cropedImgPath;
			finish();

		} else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_CANCELED) {
			cropedImgPath = "";

			DATA.imagePath = "";
			DATA.isImageCaptured = false;
			finish();
		}


		super.onActivityResult(requestCode, resultCode, data);
	}



	/*private void runCropImageG() {

		// create explicit intent
		Intent intent = new Intent(activity, CropImage.class);
		// tell CropImage activity to look for image to crop
		String filePath = DATA.imagePath;
		intent.putExtra(CropImage.IMAGE_PATH, filePath);
		// allow CropImage activity to rescale image
		intent.putExtra(CropImage.SCALE, false);
		// if the aspect ratio is fixed to ratio 3/2
		if(isForProfilePic){
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
		}else {
			intent.putExtra(CropImage.ASPECT_X, 0);
			intent.putExtra(CropImage.ASPECT_Y, 0);
		}
		// start activity CropImage with certain request code and listen
		// for result
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}*/




	String cropedImgPath;
	private void runCropImage(String imagePathSource){

		/*File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		long tim = System.currentTimeMillis();
		String imgName = "/image_"+tim+".jpg";
		String destinationFileName = dir+imgName;*/

		//String destinationFileName = "Img"+System.currentTimeMillis()+".jpg";

		//Uri uri = Uri.fromFile(new File(imagePath));
		Uri uriSource = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", new File(imagePathSource));


		//File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +getResources().getString(R.string.app_name));//Formera Mobile App/Video
		//File folder = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() +"/" +getResources().getString(R.string.app_name));
		File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) +"/" +getResources().getString(R.string.app_name));
		if(!folder.exists()){
			boolean created = folder.mkdir();
			DATA.print("-- folder created : "+created);
		}
		String timeStr = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.ENGLISH).format(new Date());
		String imgFilePath = folder.getAbsolutePath()+"/"+"Img_"+timeStr+".jpg";

		File imgFile = new File(imgFilePath);
		//outputFileUri = Uri.fromFile(op);//Android O

		//Uri outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", imgFile);
		Uri outputFileUri = Uri.fromFile(imgFile);//note : here get uri from file provider not works.

		cropedImgPath = imgFile.getAbsolutePath();

		DATA.print("-- image path generated before startActivityForResult call "+ cropedImgPath);



		UCrop uCrop = UCrop.of(uriSource, outputFileUri);//Uri.fromFile(new File(getCacheDir(), destinationFileName)) //getCacheDir()  dir
		uCrop = basisConfig(uCrop);
		uCrop = advancedConfig(uCrop);
		uCrop.start(ChoosePictureDialog.this);
	}

	private UCrop basisConfig(@NonNull UCrop uCrop) {

		//uCrop = uCrop.useSourceImageAspectRatio();
		if(isForProfilePic){
			uCrop = uCrop.withAspectRatio(1, 1);
		}
		//uCrop = uCrop.withAspectRatio(0, 0);
		// do nothing  for dynamic aspact ratio
        /*try {
            float ratioX = Float.valueOf(mEditTextRatioX.getText().toString().trim());
            float ratioY = Float.valueOf(mEditTextRatioY.getText().toString().trim());
            if (ratioX > 0 && ratioY > 0) {
                uCrop = uCrop.withAspectRatio(ratioX, ratioY);
            }
        } catch (NumberFormatException e) {
            Log.i(TAG, String.format("Number please: %s", e.getMessage()));
        }*/

        /*if (mCheckBoxMaxSize.isChecked()) {
            try {
                int maxWidth = Integer.valueOf(mEditTextMaxWidth.getText().toString().trim());
                int maxHeight = Integer.valueOf(mEditTextMaxHeight.getText().toString().trim());
                if (maxWidth > 0 && maxHeight > 0) {
                    uCrop = uCrop.withMaxResultSize(maxWidth, maxHeight);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Number please", e);
            }
        }*/

		return uCrop;
	}
	private UCrop advancedConfig(@NonNull UCrop uCrop) {
		UCrop.Options options = new UCrop.Options();

		//options.setCompressionFormat(Bitmap.CompressFormat.PNG);
		options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
		options.setCompressionQuality(100);//90
		options.setHideBottomControls(false);
		options.setFreeStyleCropEnabled(false);//false


		// Color palette
		options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
		options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
		options.setActiveControlsWidgetColor(ContextCompat.getColor(this, android.R.color.white));
		options.setToolbarWidgetColor(ContextCompat.getColor(this, android.R.color.white));
		//options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));
		//options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
		/*options.setCropFrameColor(ContextCompat.getColor(this, R.color.red));
		options.setCropGridColor(ContextCompat.getColor(this, R.color.app_blue_color));
		options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.green));*/


        /*
        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */


       /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));

       */

		return uCrop.withOptions(options);
	}














	//generate image path from given image uri
	/*private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) { // Source is some local file path
			return contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}

	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inPurgeable = true;
		options.inDensity = 1;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		//Bitmap bmp = BitmapFactory.decodeFile(path, options);

		//	bmp = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, false);
		return BitmapFactory.decodeFile(path, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}*/

}
