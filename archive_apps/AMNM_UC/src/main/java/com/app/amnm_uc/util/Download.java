package com.app.amnm_uc.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.app.amnm_uc.ViewReportImage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Download extends AsyncTask<String, Void, String> 
{
    ProgressDialog mProgressDialog;

    Context context;
    String urlDownload;

    String errorType;
    
    String filename;
        
    File folder;
    File file;
    int totalSize;
    int downloadedSize = 0;
    int size1;
    CustomToast customToast;
    
    public Download(Activity context,String url, String filename) 
    {
        this.context = context;
        this.urlDownload=url;
        this.filename = filename;
        customToast = new CustomToast(context);
    }

    protected void onPreExecute() 
    {
        mProgressDialog = new ProgressDialog(context); 
        mProgressDialog.setTitle("Downloading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setMax(totalSize);
//        mProgressDialog.setIcon(R.drawable.ic_launcher);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage(filename);
        mProgressDialog.show();

        Log.v("DOWNLOAD", "Wait for downloading url : "+totalSize + urlDownload);
    }

    protected String doInBackground(String... params) 
    {
        try 
        {
            URL url = new URL(urlDownload);

            Log.w( "DOWNLOAD" , "URL TO CALL : " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //set up some things on the connection
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            folder = new File(android.os.Environment.getExternalStorageDirectory()+ "/Online Care Reports/") ;

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }

            file = new File(folder,filename);

            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file
             totalSize = urlConnection.getContentLength();
             mProgressDialog.setMax(totalSize);
             //mProgressDialog.show();
            //variable to store total downloaded bytes

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer

            //now, read through the input buffer and write the contents to the file
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                //add the data in the buffer to the file in the file output stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                //this is where you would do something to report the prgress, like this maybe
                //updateProgress(downloadedSize, totalSize);
                Log.w( "DOWNLOAD" , "progress " + downloadedSize + " / " + totalSize);
                mProgressDialog.setProgress(downloadedSize);
                size1 = downloadedSize;

            }
            //close the output stream when done
            fileOutput.close();

        //catch some possible errors...
        } 
        catch (MalformedURLException e) 
        {
            Log.e( "DOWNLOAD" , "ERROR : " + e );
            errorType = "U";
            return "fail";
        } 
 
        catch (FileNotFoundException e) {
            Log.e( "DOWNLOAD" , "ERROR File not Found : " + e );
            errorType = "F";

            return "fail";
        }
        catch (IOException e) 
        {
            Log.e( "DOWNLOAD" , "ERROR Internet : " + e );
            errorType = "I";

            return "fail";
        }
 
    	mProgressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
		         if(size1 != totalSize) {
		        	 
		         	if(file.exists()) {
		        		file.delete();
		        		System.out.println("--file deleted on cancel");
		        	}

		        	Toast.makeText(context, "Not Downloaded Properly", Toast.LENGTH_LONG).show();
		        }	
			}
		});

        return "done";
    }

    private void publishProgress( int i )
    {
        Log.v("DOWNLOAD", "PROGRESS ... " + i);
//        mProgressDialog.setProgress(downloadedSize);

        
    }

    protected void onPostExecute(String result) 
    {
    	
    	
        if (result.equals("done") && size1 == totalSize) 
        {
            customToast.showToast("Report "+filename+" downloaded and saved to sdcard", 0, 0);

    		MediaScannerConnection.scanFile(context,new String[] { file.toString() }, null,
  		          new MediaScannerConnection.OnScanCompletedListener() {
  		      public void onScanCompleted(String path, Uri uri) {

  		    	  Log.i("ExternalStorage", "Scanned " + path + ":");
  		          Log.i("ExternalStorage", "-> uri=" + uri);
  		      }
  		 });


            ((ViewReportImage)context).setResults(filename);
            
        }
        else if (result.equals("fail")){
        	
        	if(errorType.equals("I")) {
            	Toast.makeText(context, "Internet Connection Error", Toast.LENGTH_LONG).show();        		
                customToast.showToast("Not connected to Internet", 0, 0);

        	}
        	
        	else if(errorType.equals("U")) {
            	if(file.exists()) {
            		file.delete();
            	}
               customToast.showToast("Sorry, download failed, please try again", 0, 0);

        	}
        	
        	else if(errorType.equals("F")) {
                customToast.showToast("Sorry, file not found", 0, 0);
        	}
        	
        }
        else if(size1 != totalSize) {
        	
        	if(file.exists()) {
        		file.delete();
        	}
        	
            customToast.showToast("Sorry, the file could not download properly, download again.", 0, 0);
        }
        

        mProgressDialog.dismiss();

    }
    
   
}