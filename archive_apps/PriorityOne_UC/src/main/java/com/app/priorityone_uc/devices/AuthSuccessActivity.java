package com.app.priorityone_uc.devices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.app.priorityone_uc.R;
import com.app.priorityone_uc.devices.asynctasks.FetchData;
import com.app.priorityone_uc.devices.asynctasks.PostData;
import com.app.priorityone_uc.devices.customclasses.CommonConstants;
import com.app.priorityone_uc.devices.customclasses.LocalSharedPreferences;
import com.app.priorityone_uc.devices.customclasses.SessionIdentifierGenerator;
import com.app.priorityone_uc.devices.interfaces.FetchDataCallbackInterface;


import static com.app.priorityone_uc.devices.customclasses.CustomMethods.enc;
import static com.app.priorityone_uc.devices.customclasses.CustomMethods.generateHmacSHA1;

/**
 * Created by aftab on 08/08/2016.
 */

public class AuthSuccessActivity extends AppCompatActivity implements FetchDataCallbackInterface {

    private static final String TAG = "AuthSuccessActivity";

    private String token = "";
    private LocalSharedPreferences lsp;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_success);

        lsp = new LocalSharedPreferences(this);

        mProgressDialog = new ProgressDialog(AuthSuccessActivity.this);
        mProgressDialog.setMessage("Authenticating user...");
//        mProgressDialog.show();

        Uri uri = getIntent().getData();

        if (uri != null) {


            token = uri.getQueryParameter("oauth_token");
            generateOauthTokenAndSecret();

        }
        else {
            Toast.makeText(AuthSuccessActivity.this,"Sorry, not able to authenticate user.",Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void fetchDataCallback(String result) {

        if (result.contains("oauth_token=") && result.contains("oauth_token_secret=")) {

            result = result.replace("oauth_token=","");
            result = result.replace("oauth_token_secret=","");
            result = result.replace("userid=","");
            result = result.replace("deviceid=","");

            String sp[] = result.split("&");

            lsp.saveWithingsBpOAuthToken(sp[0]);
            lsp.saveWithingsBpOAuthTokenSecret(sp[1]);
            lsp.saveWithingsUserId(sp[2]);

            Log.e("tag", "--url result token= "+lsp.getWithingsBpOAuthToken());
            Log.e("tag", "--url result token secret= "+lsp.getWithingsBpOauthTokenSecret());
            Log.e("tag", "--url result user id= "+lsp.getWithingsUserId());



            new PostData(AuthSuccessActivity.this,CommonConstants.onlineCareMainUrl +"addUserDevice", AuthSuccessActivity.this).execute();

        }
        else if (result.contains("addUserDevice")) {

            Log.e("tag", "--response addUserDevice = "+result );

            result = result.replace("addUserDevice","");

            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equals("success")) {

                    Toast.makeText(AuthSuccessActivity.this, jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AuthSuccessActivity.this,MyDevices.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(AuthSuccessActivity.this, jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AuthSuccessActivity.this,AvailableDevicesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    finish();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(AuthSuccessActivity.this,"Sorry, not able to authenticate user.",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }

    }

    private void generateOauthTokenAndSecret() {

        String oauth_nounce = new SessionIdentifierGenerator().nextSessionId();
        String signature = "";
        long timeStamp = (long) System.currentTimeMillis()/1000;

        String base1 = "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Faccess_token&" +
                "oauth_consumer_key%3D" + CommonConstants.withingsCustomerKey+
                "%26oauth_nonce%3D"+oauth_nounce+
                "%26oauth_signature_method%3DHMAC-SHA1" +
                "%26oauth_timestamp%3D"+timeStamp+
                "%26oauth_token%3D"+token +
                "%26oauth_version%3D1.0";

        try {
            signature = generateHmacSHA1(base1, CommonConstants.withingsCustomerSecret + "&" + new LocalSharedPreferences(this).getWithingsBpOauthTokenSecret());

            String url = CommonConstants.withingsUserAccessTokenUrl +
                    "?oauth_consumer_key=" + CommonConstants.withingsCustomerKey +
                    "&oauth_nonce=" + oauth_nounce +
                    "&oauth_signature="+enc(signature)+
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp="+timeStamp+
                    "&oauth_token="+token +
                    "&oauth_version=1.0";

            Log.e("tag", "--url = "+url);

            new FetchData(url, AuthSuccessActivity.this).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            Toast.makeText(AuthSuccessActivity.this,"Sorry, not able to authenticate user.",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            Toast.makeText(AuthSuccessActivity.this,"Sorry, not able to authenticate user.",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();

        } catch (InvalidKeyException e) {
            e.printStackTrace();

            Toast.makeText(AuthSuccessActivity.this,"Sorry, not able to authenticate user.",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();

        }


    }
}