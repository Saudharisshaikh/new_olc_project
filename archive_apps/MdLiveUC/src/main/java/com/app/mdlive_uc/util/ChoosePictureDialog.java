package com.app.mdlive_uc.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mdlive_uc.R;

import java.io.File;
import java.util.List;

import eu.janmuller.android.simplecropimage.CropImage;

public class ChoosePictureDialog extends Activity {
	
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

		isForProfilePic = getIntent().getBooleanExtra("isForProfilePic", false);//for squire crop
		
		overridePendingTransition(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom);
		
		this.setFinishOnTouchOutside(false);
		
		lvChooseImage = (ListView) findViewById(R.id.lvChooseImage);
		//prefs = getSharedPreferences("clothingPrefs", Context.MODE_PRIVATE);
		
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				selectItem(position);
				 
			}
		});

	}

	private void selectItem(int position) {
		switch(position) {
			case 0:
				dispatchChooseFromGallery();
				break;
			case 1:
				dispatchTakePictureIntent();
				break;
			case 2:
				//finish();
				onBackPressed();
				break;
		}
	}

	String imgFilePathTemp = "";//Jugaaar
	private void dispatchTakePictureIntent() {

		long tim = System.currentTimeMillis();
		String imgName = "image_"+tim+".jpg";

		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File op = new File(dir, imgName);

		//outputFileUri = Uri.fromFile(op);//get uri from file provider  ...Android 26 GM

		//outputFileUri = FileProvider.getUriForFile(getApplicationContext(), "com.app.emcurauc.fileprovider", op);
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
		photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT); 
		startActivityForResult(photoPickerIntent, REQUEST_SELECT_FROM_GALLERY);
	}
	
	//coming back from camera app take picture
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

			DATA.isImageCaptured = true;

			//DATA.imagePath = getRealPathFromURI(outputFileUri);
			//DATA.imagePath = ImageFilePath.getPath(activity, outputFileUri);//GM commented.. file provider issue androidN

			DATA.imagePath = imgFilePathTemp;// temp solution....GM
			
	//		Toast.makeText(activity, "path:"+DATA.imagePath+ "\nuri:"+outputFileUri,1).show();;
			
			System.out.println("--online care output image path: "+ DATA.imagePath);
			System.out.println("--online care output file uri: "+ outputFileUri);
	
				runCropImage();


		} else if(requestCode == REQUEST_SELECT_FROM_GALLERY && resultCode == RESULT_OK) {

			DATA.isImageCaptured = true;

			Uri uri = null;
			if(data != null) {

				uri = data.getData();
				File file = new File(ImageFilePath.getPath(activity, uri));
				
				DATA.imagePath = file.toString();
				
				isFromGlry = true;
				runCropImage();
			}
		} else if(requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_OK) {

			DATA.isImageCaptured = true;
			DATA.imagePath = data.getStringExtra(CropImage.IMAGE_PATH);
			
			System.out.println("--online care output image path after crop: "+ DATA.imagePath);
			System.out.println("--online care output file uri after crop: "+ outputFileUri);

			//DATA.imagePath = getRealPathFromURI(outputFileUri);
			//Toast.makeText(activity, "path:"+DATA.imagePath+ "\nuri:"+outputFileUri,1).show();;
			//ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			//note : this is compress removed by GM. it lowers img quality too much
			/*long tim = System.currentTimeMillis();
			String imgName = "/image_"+tim+".jpg";

			Bitmap bp = decodeSampledBitmapFromPath(DATA.imagePath, 260, 260);

			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			File op = new File(dir, imgName);
			DATA.imagePath = dir+imgName;
			System.out.println("----image path after scaling: "+DATA.imagePath);

			try {
				op.createNewFile();
				//write the bytes in file
				FileOutputStream fo = new FileOutputStream(op);
				bp.compress(Bitmap.CompressFormat.JPEG, 90, fo);
				//fo.write(bytes.toByteArray());
				// remember close de FileOutput
				fo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			finish();
		} else if(requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_CANCELED) {
			DATA.imagePath = "";
			DATA.isImageCaptured =false;
			finish();
		}
	}

	//generate image path from given image uri
	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) { // Source is some local file path
			return contentURI.getPath();
		} else { 
			cursor.moveToFirst(); 
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			return cursor.getString(idx); 
		}
	}

	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
			int reqHeight) {

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

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
		return inSampleSize;
	}

	private void runCropImage() {

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
	}

}
