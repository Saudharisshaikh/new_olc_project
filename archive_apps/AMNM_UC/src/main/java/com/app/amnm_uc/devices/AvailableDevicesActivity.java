package com.app.amnm_uc.devices;

import static com.app.amnm_uc.devices.customclasses.CustomMethods.enc;
import static com.app.amnm_uc.devices.customclasses.CustomMethods.generateHmacSHA1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.amnm_uc.R;
import com.app.amnm_uc.devices.adapters.AvailableDevicesAdapter;
import com.app.amnm_uc.devices.asynctasks.FetchData;
import com.app.amnm_uc.devices.beanclasses.DeviceBean;
import com.app.amnm_uc.devices.customclasses.CommonConstants;
import com.app.amnm_uc.devices.customclasses.LocalSharedPreferences;
import com.app.amnm_uc.devices.customclasses.SessionIdentifierGenerator;
import com.app.amnm_uc.devices.interfaces.FetchDataCallbackInterface;
import com.app.amnm_uc.devices.parsers.AllDevicesParser;

public class AvailableDevicesActivity extends AppCompatActivity implements FetchDataCallbackInterface {

    private static final String TAG = "AvailDevicesActivity";

    //RecyclerView recyclerAvailableDevices;
    GridView gvAvailableDevices;
    AvailableDevicesAdapter availableDevicesAdapter;
    LocalSharedPreferences lsp;
    TextView tvNoAvailableDevice;
    ArrayList<DeviceBean> myDevicesList;

    @Override
    protected void onResume() {
        super.onResume();

        new FetchData(CommonConstants.onlineCareMainUrl + "getAllDevices", AvailableDevicesActivity.this).execute();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_devices);

        gvAvailableDevices = (GridView) findViewById(R.id.gvAvailableDevices);
        tvNoAvailableDevice = (TextView) findViewById(R.id.tvNoAvailableDevice);
        lsp = new LocalSharedPreferences(this);
        tvNoAvailableDevice.setVisibility(View.GONE);

//        List<DeviceBean> devicesList = new ArrayList<>();
//        DeviceBean availableDevicesBean = new DeviceBean();
//
//        availableDevicesBean.setDeviceId(1);
//        availableDevicesBean.setDeviceName("Withings Blood Pressue Monitor");
//        availableDevicesBean.setDeviceDrawableId(R.drawable.withings_bp);
//
//        devicesList.add(availableDevicesBean);
//
//        availableDevicesListAdapter = new AvailableDevicesListAdapter(this, devicesList);
//            recyclerAvailableDevices.setAdapter(availableDevicesListAdapter);

            //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            // First param is number of columns and second param is orientation i.e Vertical or Horizontal
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

           // recyclerAvailableDevices.setLayoutManager(layoutManager);



    }

    @Override
    public void fetchDataCallback(String result) {

        Log.e(TAG,"--response authenticate device = "+result);

        if (result != null) {

            if (result.contains("oauth_token=") && result.contains("oauth_token_secret=")) {

                result = result.replace("oauth_token=","");
                result = result.replace("oauth_token_secret=","");

                String sp[] = result.split("&");

                lsp.saveWithingsBpOAuthToken(sp[0]);
                lsp.saveWithingsBpOAuthTokenSecret(sp[1]);

                generateAndCallAuthorize();
            }
            else if (result.contains("company") && result.contains("onlinecare.com") && result.contains("type")) {

                AllDevicesParser allDevicesParser = new AllDevicesParser(result);
                myDevicesList = new ArrayList<DeviceBean>();
                allDevicesParser.getData(myDevicesList);

                availableDevicesAdapter = new AvailableDevicesAdapter(this, myDevicesList);
                gvAvailableDevices.setAdapter(availableDevicesAdapter);

                gvAvailableDevices.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub

                        switch (myDevicesList.get(position).getDeviceId()) {

                            case 1:

                                lsp.setSelectedDeviceIdForOauth(myDevicesList.get(position).getDeviceId());
                                authenticateWithingsBloodPressureDevice(); // withings blood pressure device
                                break;
                            default:
                                Toast.makeText(AvailableDevicesActivity.this,"Sorry, this device is not available this time.",Toast.LENGTH_SHORT).show();


                        }



                    
					}
				});
                /*availableDevicesListAdapter.setOnItemClickListener(new AvailableDevicesListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        switch (myDevicesList.get(position).getDeviceId()) {

                            case 1:

                                lsp.setSelectedDeviceIdForOauth(myDevicesList.get(position).getDeviceId());
                                authenticateWithingsBloodPressureDevice(); // withings blood pressure device
                                break;
                            default:
                                Toast.makeText(AvailableDevicesActivity.this,"Sorry, this device is not available this time.",Toast.LENGTH_SHORT).show();


                        }



                    }
                });*/


            }
        }
        else {

            Toast.makeText(AvailableDevicesActivity.this,"Something went wrong, please try again",Toast.LENGTH_SHORT).show();
        }



    }






    private void generateAndCallAuthorize() {

        String oauth_nounce = new SessionIdentifierGenerator().nextSessionId();
        String signature = "";
        long timeStamp = (long) System.currentTimeMillis()/1000;

        String base1 = "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Fauthorize&" +
                "oauth_consumer_key%3D" + CommonConstants.withingsCustomerKey+
                "%26oauth_nonce%3D"+oauth_nounce+
                "%26oauth_signature_method%3DHMAC-SHA1" +
                "%26oauth_timestamp%3D"+timeStamp+
                "%26oauth_token%3D"+lsp.getWithingsBpOAuthToken()+
                "%26oauth_version%3D1.0";

        try {
            signature =generateHmacSHA1(base1, CommonConstants.withingsCustomerSecret + "&" + lsp.getWithingsBpOauthTokenSecret());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        String url = CommonConstants.withingsUserAuthorizeUrl +
                "?oauth_consumer_key=" + CommonConstants.withingsCustomerKey +
                "&oauth_nonce=" + oauth_nounce +
                "&oauth_signature="+enc(signature)+
                "&oauth_signature_method=HMAC-SHA1" +
                "&oauth_timestamp="+timeStamp+
                "&oauth_token="+lsp.getWithingsBpOAuthToken()+
                "&oauth_version=1.0";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void authenticateWithingsBloodPressureDevice() {

        String oauth_nounce = new SessionIdentifierGenerator().nextSessionId();
        String signature = "";
        long timeStamp = (long) System.currentTimeMillis()/1000;

        String base1 = "GET&https%3A%2F%2Foauth.withings.com%2Faccount%2Frequest_token&" +
                "oauth_callback%3Dhttps%253A%252F%252Fonlinecare.com%252Fdev%252FwithingsRedirect.php" +
                "%26oauth_consumer_key%3D" + CommonConstants.withingsCustomerKey+
                "%26oauth_nonce%3D"+oauth_nounce+
                "%26oauth_signature_method%3DHMAC-SHA1" +
                "%26oauth_timestamp%3D"+timeStamp+
                "%26oauth_version%3D1.0";

        try {
            signature =generateHmacSHA1(base1, CommonConstants.withingsCustomerSecret + "&");

            String url = CommonConstants.withingsCustomerRequestTokenURL +
                    "?oauth_callback=" + CommonConstants.withingsCustomerOauthCallbackUrl +
                    "&oauth_consumer_key=" + CommonConstants.withingsCustomerKey +
                    "&oauth_nonce=" + oauth_nounce +
                    "&oauth_signature="+enc(signature)+
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp="+timeStamp+
                    "&oauth_version=1.0";

            Log.e("tag", "--url = "+url);

            new FetchData(url, AvailableDevicesActivity.this).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
