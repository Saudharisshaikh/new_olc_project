package com.app.omranpatient.devices;

import static com.app.omranpatient.devices.customclasses.CustomMethods.enc;
import static com.app.omranpatient.devices.customclasses.CustomMethods.generateHmacSHA1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.omranpatient.R;
import com.app.omranpatient.devices.adapters.MyDevicesAdapter1;
import com.app.omranpatient.devices.asynctasks.FetchData;
import com.app.omranpatient.devices.asynctasks.PostData;
import com.app.omranpatient.devices.beanclasses.BpHeartRateValuesBean;
import com.app.omranpatient.devices.beanclasses.DeviceBean;
import com.app.omranpatient.devices.customclasses.CommonConstants;
import com.app.omranpatient.devices.customclasses.LocalSharedPreferences;
import com.app.omranpatient.devices.customclasses.SessionIdentifierGenerator;
import com.app.omranpatient.devices.interfaces.FetchDataCallbackInterface;
import com.app.omranpatient.devices.parsers.MyDevicesParser;
import com.app.omranpatient.devices.parsers.WithingsBpDeviceParser;

public class MyDevices extends AppCompatActivity implements FetchDataCallbackInterface {

    private static final String TAG = "MyDevices";

    //RecyclerView recyclerMyDevices1;
    GridView gvMyDevices;
    MyDevicesAdapter1 myDevicesAdapter;
    LocalSharedPreferences lsp;
    TextView tvNoDevice;
    ArrayList<DeviceBean> myDevicesList;

    LinearLayout datePickerLayout;
    private static EditText etSelectStartDate,etSelectEndDate;
    Button btnGetMeasures;

    public static boolean showWithingsDateDialog = false;
    public static DeviceBean myDevice;

    public static long startTime, endTime;
    private static boolean isEndDateClicked = false;

    private ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showWithingsDateDialog = false;
        myDevice = null;
        startTime = 0;
        endTime = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();

        showWithingsDateDialog = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (showWithingsDateDialog) {

            invalidateOptionsMenu();

            datePickerLayout.setVisibility(View.VISIBLE);
            etSelectStartDate.setVisibility(View.VISIBLE);
            etSelectEndDate.setVisibility(View.VISIBLE);
            btnGetMeasures.setVisibility(View.VISIBLE);

            etSelectEndDate.setText("");
            etSelectStartDate.setText("");

            tvNoDevice.setVisibility(View.GONE);
            gvMyDevices.setVisibility(View.GONE);

            this.getSupportActionBar().setTitle("Select Date");

        }
        else {

            invalidateOptionsMenu();

            this.getSupportActionBar().setTitle("My Devices");


            datePickerLayout.setVisibility(View.GONE);
            etSelectStartDate.setVisibility(View.GONE);
            etSelectEndDate.setVisibility(View.GONE);
            btnGetMeasures.setVisibility(View.GONE);

            new PostData(this,CommonConstants.onlineCareMainUrl + "myDevices", MyDevices.this).execute();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_devices);

        progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        datePickerLayout = (LinearLayout) findViewById(R.id.datePickerLayout);
        etSelectStartDate = (EditText) findViewById(R.id.etSelectStartDate);
        etSelectEndDate = (EditText)findViewById(R.id.etSelectEndDate);
        btnGetMeasures = (Button)findViewById(R.id.btnGetMeasures);

        etSelectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTime = 0;
                isEndDateClicked = false;
                showDatePickerDialog();
            }
        });

        etSelectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                endTime = 0;
                isEndDateClicked = true;
                showDatePickerDialog();
            }
        });


        btnGetMeasures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (startTime != 0 )
                {
                    if (endTime != 0 )
                    {
                        progressDialog.show();
                        getUserMeasures(myDevice, true);
                        showWithingsDateDialog = false;
                    }
                    else
                    {
                        Toast.makeText(MyDevices.this,"End date not selected",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MyDevices.this,"Start date not selected",Toast.LENGTH_SHORT).show();
                }

            }
        });

        gvMyDevices = (GridView) findViewById(R.id.gvMyDevices);
        tvNoDevice = (TextView) findViewById(R.id.tvNoDevice);
        lsp = new LocalSharedPreferences(this);


            tvNoDevice.setVisibility(View.GONE);

            /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerMyDevices.setLayoutManager(layoutManager);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_add_device) {

            Intent intent = new Intent(MyDevices.this,AvailableDevicesActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_device, menu);

        if (showWithingsDateDialog) {

           menu.findItem(R.id.item_add_device).setVisible(false);
        }
        else {

            menu.findItem(R.id.item_add_device).setVisible(true);
        }
        return true;
    }


    @Override
    public void fetchDataCallback(String result) {

        Log.e(TAG,"--response getMyDevices = "+result);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (result != null) {

            if (result.contains("measuregrps") && result.contains("body")) {

                final WithingsBpDeviceParser myDevicesParser = new WithingsBpDeviceParser(result);
                WithingsBpDataActivity1.withingsBpDataList = new ArrayList<BpHeartRateValuesBean>();
                myDevicesParser.getData(WithingsBpDataActivity1.withingsBpDataList);

                if (WithingsBpDataActivity1.withingsBpDataList != null || WithingsBpDataActivity1.withingsBpDataList.size() > 0 ) {

                    Intent intent = new Intent(MyDevices.this,WithingsBpDataActivity1.class);
                    startActivity(intent);

                    tvNoDevice.setVisibility(View.GONE);

                }
                else {

                    Toast.makeText(MyDevices.this,"No Data Found",Toast.LENGTH_SHORT).show();
                    tvNoDevice.setVisibility(View.VISIBLE);
                }



            }
            else if ((result.contains("myDevices"))){

                result = result.replace("myDevices","");

                final MyDevicesParser myDevicesParser = new MyDevicesParser(result);
                myDevicesList = new ArrayList<DeviceBean>();
                myDevicesParser.getData(myDevicesList);

                myDevicesAdapter = new MyDevicesAdapter1(this, myDevicesList);
                gvMyDevices.setAdapter(myDevicesAdapter);

                gvMyDevices.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub


                        switch (myDevicesList.get(position).getDeviceId()) {

                            case 9: // for withings bp data. there is a bug here. id=1 in all devices but here its 9

                                myDevice = myDevicesList.get(position);

                                getUserMeasures(myDevicesList.get(position), false);

                                break;

                            default:
                                Toast.makeText(MyDevices.this,"Sorry, this device is not available this time.",Toast.LENGTH_SHORT).show();


                        }



                    
					}
				});
                /*myDevicesAdapter.setOnItemClickListener(new MyDevicesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        switch (myDevicesList.get(position).getDeviceId()) {

                            case 9: // for withings bp data. there is a bug here. id=1 in all devices but here its 9

                                myDevice = myDevicesList.get(position);

                                getUserMeasures(myDevicesList.get(position), false);

                                break;

                            default:
                                Toast.makeText(MyDevices.this,"Sorry, this device is not available this time.",Toast.LENGTH_SHORT).show();


                        }



                    }
                });*/


            }


        }
        else {

            Toast.makeText(MyDevices.this,"No devices found", Toast.LENGTH_SHORT).show();
        }

    }


    public void getUserMeasures(DeviceBean myDevice, boolean isDateRangeSelected) {

        String oauth_nounce = new SessionIdentifierGenerator().nextSessionId();
        String signature = "";

        long timeStamp = (long) System.currentTimeMillis()/1000;
        long lasSevenDaysDate = (long) timeStamp - 604800000; // seven days unix epoch time = 604800000

        if (!isDateRangeSelected) {

            startTime = lasSevenDaysDate;
            endTime = timeStamp;
        }

        String base1 = "GET&http%3A%2F%2Fwbsapi.withings.net%2Fmeasure&action%3Dgetmeas" +
                "%26oauth_consumer_key%3D" + CommonConstants.withingsCustomerKey +
                "%26oauth_nonce%3D"+oauth_nounce+
                "%26oauth_signature_method%3DHMAC-SHA1" +
                "%26oauth_timestamp%3D"+timeStamp+
                "%26oauth_token%3D" + myDevice.getoAuthToken() +
                "%26oauth_version%3D1.0" +
                "%26userid%3D" +myDevice.getDeviceUserId() +
                "%26startdate%3D" +startTime +
                "%26enddate%3D"+endTime;

        try {
            signature =generateHmacSHA1(base1, CommonConstants.withingsCustomerSecret + "&" + myDevice.getoAuthSecret());

            String url = CommonConstants.withingsGetMeasuresUrl +
                    "&oauth_consumer_key=" + CommonConstants.withingsCustomerKey +
                    "&oauth_nonce=" + oauth_nounce +
                    "&oauth_signature="+enc(signature)+
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp="+timeStamp+
                    "&oauth_token=" + myDevice.getoAuthToken() +
                    "&oauth_version=1.0" +
                    "&userid=" +myDevice.getDeviceUserId() +
                    "&startdate=" +startTime +
                    "&enddate="+endTime;

            Log.e("tag", "--url = "+url);

            new FetchData(url, MyDevices.this).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);



            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // Do something with the date chosen by the user


            month++;

            String smonth=""+month;
            String sday = day+"";
            if (month < 10)
            {
                smonth = "0"+(month);
            }

            if (day<10)
            {
                sday = "0"+day;
            }



            String selectedDate = year+"-" + smonth +"-"+ sday;

            Log.e(TAG,"--selected date: "+selectedDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date mDate = sdf.parse(selectedDate);
                long timeInMilliseconds = mDate.getTime();

                System.out.println("Date in milli :: " + timeInMilliseconds);

                if (isEndDateClicked) {

                    endTime = timeInMilliseconds/1000;
                    etSelectEndDate.setText(selectedDate);
                }
                else {

                    startTime = timeInMilliseconds/1000;
                    etSelectStartDate.setText(selectedDate);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public void showDatePickerDialog()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(MyDevices.this.getSupportFragmentManager(), "datePicker");
    }
}
