package com.app.msu_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.PayerBean;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalMethods;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class BookAppointment extends Activity {

	Button btnBkShareReports,btnBKAccept,btnBKDecline;
	RadioGroup rgEvMor,rgPayment,rgFreeOptions;
	TextView tvSelectDay,tvNumReprtsSelctd,tvScheduleTimeMorning,tvScheduleTimeEvening;
	Builder builder;
	JSONObject jsonObject;
	AsyncHttpClient client;
	String msg, status;
	ProgressDialog pd;
	SharedPreferences prefs;

	TextView tvSelectedDate;

	Activity activity;
	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	/*LinearLayout InsuranceCont;
    TextView tvInsurance,tvPolicyNo,tvGroup,tvCode,tvNoInsuranceAdded;
    Button btnAddInsurance;*/
	ScrollView scrollView;

	/*private String daysArr[];
	private String scheduleIdsArr[];*/


	LinearLayout insuranceCont;
	Spinner spInsurance;
	EditText etPolicynumber,etGroup,etCode;
	Button btnVarifyInsurance;
	String insurance = "";

	LinearLayout layCreditCard;
	TextView tvCrediCardAmount;
	EditText etCardholder,etCardNo,etCardCVV;
	Spinner spCardType,spCardExpiryMonth,spCardExpiryYear;

	//boolean insuranceOrCredit = false;
	//String payment_typeIC = "";

	String selectedCardType = "",selectedExpMonth = "",selectedExpYear = "",
			cardHolderName = "",cardNo = "",cvvCode = "";

	@Override
	protected void onResume() {
		super.onResume();

		if(!DATA.isReprtSelected || DATA.NumOfReprtsSelected == 0) {
			tvNumReprtsSelctd.setText("");
			tvNumReprtsSelctd.setVisibility(View.GONE);
		}
		else {
			tvNumReprtsSelctd.setText("Number of reports selected: "+DATA.NumOfReprtsSelected);
			tvNumReprtsSelctd.setVisibility(View.VISIBLE);

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		//DATA.isAppointmentDaySelected = false;
	}

	//------------------------------------------------------PAYPAL----------------------------------------------------
	/*private static final String TAG = "paymentExample";
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
	private static final String CONFIG_CLIENT_ID = "AYSCmbv7poUco_RJRJGLe1FaHcWlCvFGTpRRDcbdvq8oywmF2mDDUUMsw_NEH8Ebuy1s-oa_pq8wlv1f";
	private static final int REQUEST_CODE_PAYMENT = 1;
	private static PayPalConfiguration config = new PayPalConfiguration()
	            .environment(CONFIG_ENVIRONMENT)
	            .clientId(CONFIG_CLIENT_ID)          //.defaultUserEmail("mustafa.09sw77.muet@gmail.com").sandboxUserPassword("checking12")
	            // The following are only used in PayPalFuturePaymentActivity.
	            .merchantName("OnlineCare")
	            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
	            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));*/
	//------------------------------------------------------PAYPAL----------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_appointment);

		activity = BookAppointment.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		tvSelectDay = (TextView) findViewById(R.id.tvSelectDay);
		tvNumReprtsSelctd = (TextView) findViewById(R.id.tvNumReprtsSelctd);
		tvScheduleTimeMorning = (TextView) findViewById(R.id.tvScheduleTimeMorning);
		tvScheduleTimeEvening = (TextView) findViewById(R.id.tvScheduleTimeEvening);

		tvSelectedDate = (TextView) findViewById(R.id.tvSelectedDate);

		tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));

		btnBKAccept = (Button) findViewById(R.id.btnBKAccept);
		btnBKDecline = (Button) findViewById(R.id.btnBKDecline);
		btnBkShareReports = (Button) findViewById(R.id.btnBkShareReports);

		rgEvMor = (RadioGroup) findViewById(R.id.rgEvMor);
		rgPayment = (RadioGroup) findViewById(R.id.rgPayment);
		rgFreeOptions = (RadioGroup) findViewById(R.id.rgFreeOptions);
		rgFreeOptions.setVisibility(View.GONE);

		scrollView = (ScrollView) findViewById(R.id.kj);
		/*InsuranceCont = (LinearLayout) findViewById(InsuranceCont);
	     tvInsurance = (TextView) findViewById(tvInsurance);
	     tvPolicyNo = (TextView) findViewById(tvPolicyNo);
	     tvGroup = (TextView) findViewById(tvGroup);
	     tvCode = (TextView) findViewById(tvCode);
	     btnAddInsurance = (Button) findViewById(btnAddInsurance);
	     tvNoInsuranceAdded = (TextView) findViewById(tvNoInsuranceAdded);
	     btnAddInsurance.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//initInsuranceDialog();
				}
			});*/

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Requesting appointment...    ");
		pd.setCanceledOnTouchOutside(false);


		builder = new Builder(activity);
		builder.setTitle("Confirm your appointment");
		builder.setMessage("You want to set appointment with "+DATA.doctorsModelForApptmnt.fName+" "+DATA.doctorsModelForApptmnt.lName +" ?");
		builder.setPositiveButton("Yes, confirm", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (DATA.selectedRGFreePaypal.equalsIgnoreCase("paypal")) {
					//initPaypal();
				} else {
					bookAppointmentCall();
				}

			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		//selecting day...
		/*daysArr = new String[1];//DATA.allScheduleFiltered.size()
		scheduleIdsArr = new String[1];//DATA.allScheduleFiltered.size()

		for(int i = 0; i<DATA.allScheduleFiltered.size(); i++) {
			if (DATA.allScheduleFiltered.get(i).day.equalsIgnoreCase(DATA.selected_dayForApptmnt)) {
				daysArr[i] = DATA.allScheduleFiltered.get(i).day;
				scheduleIdsArr[i] = DATA.allScheduleFiltered.get(i).id;
			}
		}

		tvSelectDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Select Day");
				builder.setItems(daysArr, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						DATA.selectedDayIdForAppointment = scheduleIdsArr[item];

						tvSelectDay.setText("Day selected: " + daysArr[item]);

						DATA.isAppointmentDaySelected = true;


						for (int i = 0; i < DATA.allScheduleFiltered.size(); i++) {
							if (DATA.allScheduleFiltered.get(i).day.equalsIgnoreCase(daysArr[item])) {
								tvScheduleTime.setText("Morning from: "+DATA.allScheduleFiltered.get(i).fromTime+" to: "+DATA.allScheduleFiltered.get(i).toTime
										+"\nEvening from: "+DATA.allScheduleFiltered.get(i).eveningFromTime+" to: "+DATA.allScheduleFiltered.get(i).eveningToTime);
							}
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			}
		});*///selecting day ends


		/*for (int i = 0; i < DATA.allScheduleFiltered.size(); i++) {
			if (DATA.allScheduleFiltered.get(i).day.equalsIgnoreCase(DATA.selected_dayForApptmnt)) {
				tvScheduleTime.setText("Morning from: "+DATA.allScheduleFiltered.get(i).fromTime+" to: "+DATA.allScheduleFiltered.get(i).toTime
						+"\nEvening from: "+DATA.allScheduleFiltered.get(i).eveningFromTime+" to: "+DATA.allScheduleFiltered.get(i).eveningToTime);

				tvScheduleTimeMorning.setText(DATA.allScheduleFiltered.get(i).fromTime+" to: "+DATA.allScheduleFiltered.get(i).toTime);
				tvScheduleTimeEvening.setText(DATA.allScheduleFiltered.get(i).eveningFromTime+" to: "+DATA.allScheduleFiltered.get(i).eveningToTime);

				DATA.selectedDayIdForAppointment = DATA.allScheduleFiltered.get(i).id;

				tvSelectDay.setText("Day selected: " + DATA.allScheduleFiltered.get(i).day);

				DATA.isAppointmentDaySelected = true;
			}
		}*/

		//selecting evening/morning
		/*rgEvMor.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.radioMorning:

					DATA.selectedRGEveMor = "morning";

					break;
				case R.id.radioEvening:

					DATA.selectedRGEveMor = "evening";

					break;

				}
			}
		});*///selecting evening/morning ends

		//selecting payment option
		rgPayment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.radioPayPal:

						DATA.selectedRGFreePaypal = "paypal";
						rgFreeOptions.setVisibility(View.GONE);

						insuranceCont.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.GONE);
						//payment_typeIC = "";
						btnBKAccept.setVisibility(View.VISIBLE);

					/*InsuranceCont.setVisibility(View.GONE);
					btnAddInsurance.setVisibility(View.GONE);
					tvNoInsuranceAdded.setVisibility(View.GONE);*/
						break;

					case R.id.radioFreeCare:

						DATA.selectedRGFreePaypal = "free";
						rgFreeOptions.setVisibility(View.VISIBLE);

						insuranceCont.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.GONE);
						//payment_typeIC = "";
						btnBKAccept.setVisibility(View.VISIBLE);

						break;

					case R.id.radioHealthInsurance:

						DATA.selectedRGFreePaypal = "insurance";
						//payment_typeIC = "insurance";
						//insuranceOrCredit = true;
						//DATA.isFreeCare = true;
						rgFreeOptions.setVisibility(View.GONE);
						insuranceCont.setVisibility(View.VISIBLE);
						layCreditCard.setVisibility(View.GONE);
						btnBKAccept.setVisibility(View.GONE);

						scrollView.post(new Runnable() {

							@Override
							public void run() {
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
							}
						});

						break;
					case R.id.radioCreditCard:
						//DATA.isFreeCare = true;
						//insuranceOrCredit = true;
						//payment_typeIC = "credit_card";

						DATA.selectedRGFreePaypal = "credit_card";
						rgFreeOptions.setVisibility(View.GONE);
						insuranceCont.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.VISIBLE);
						btnBKAccept.setVisibility(View.VISIBLE);

						scrollView.post(new Runnable() {

							@Override
							public void run() {
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
							}
						});
						break;

				}
			}
		});//selecting payment option ends

		//selecting free care options
		rgFreeOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {

					case R.id.radioStudent:
					/*InsuranceCont.setVisibility(View.GONE);
					btnAddInsurance.setVisibility(View.GONE);
					tvNoInsuranceAdded.setVisibility(View.GONE);*/

						DATA.selectedRGFreeOptions = "student";
						break;

				/*case R.id.radioEmployee:
					InsuranceCont.setVisibility(View.VISIBLE);
					if (prefs.getString("insurance", "").isEmpty()) {
						InsuranceCont.setVisibility(View.GONE);
						tvNoInsuranceAdded.setVisibility(View.VISIBLE);
						tvNoInsuranceAdded.setText("Please enter your insurance details");
						btnAddInsurance.setVisibility(View.VISIBLE);
					}else {
						InsuranceCont.setVisibility(View.VISIBLE);
						tvNoInsuranceAdded.setVisibility(View.VISIBLE);
						tvNoInsuranceAdded.setText("Please confirm your insurance details");
						tvInsurance.setText(prefs.getString("insurance", "none"));
					    tvPolicyNo.setText(prefs.getString("policy_number", "none"));
					    tvGroup.setText(prefs.getString("group", "none"));
					    tvCode.setText(prefs.getString("code", "none"));
					}


				     scrollView.post(new Runnable() {

				         @Override
				         public void run() {
				        	 scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				         }
				     });

					DATA.selectedRGFreeOptions = "employer";
					break;*/

					case R.id.radioSingleMom:
					/*InsuranceCont.setVisibility(View.GONE);
					tvNoInsuranceAdded.setVisibility(View.GONE);*/
						DATA.selectedRGFreeOptions = "single_mom";
						break;
				}
			}
		});//selecting free care options ends

		btnBkShareReports.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//OpenActivity op = new OpenActivity(activity);
				//op.open(SelectReports.class, false);
			}
		});

		btnBKAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*if(DATA.isAppointmentDaySelected) {

					AlertDialog dialog = builder.create();
					dialog.show();

				}else {
					CustomToast c = new CustomToast(activity);
					c.showToast("You have not selected the appointment day", 0, 0);
				}*/

				if (DATA.selectedRGFreePaypal.equals("credit_card")){
					cardHolderName = etCardholder.getText().toString();
					cardNo = etCardNo.getText().toString();
					cvvCode = etCardCVV.getText().toString();
					if (cardHolderName.isEmpty()){
						etCardholder.setError("Field should not be empty.");
						return;
					}else if (cardNo.isEmpty()){
						etCardNo.setError("Field should not be empty.");
						return;
					}else if (cvvCode.isEmpty()){
						etCardCVV.setError("Field should not be empty.");
						return;
					}
				}
				AlertDialog dialog = builder.create();
				dialog.show();

			}
		});

		btnBKDecline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				//DATA.isAppointmentDaySelected = false;

			}
		});

		//------------------------------------------------------PAYPAL----------------------------------------------------
		 /*Intent intent = new Intent(this, PayPalService.class);
	        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
	        startService(intent);*/
		//------------------------------------------------------PAYPAL----------------------------------------------------




		insuranceCont= (LinearLayout) findViewById(R.id.insuranceCont);
		spInsurance = (Spinner) findViewById(R.id.spInsurance);
		etPolicynumber = (EditText) findViewById(R.id.etPolicynumber);
		etGroup = (EditText) findViewById(R.id.etGroup);
		etCode = (EditText) findViewById(R.id.etCode);;
		btnVarifyInsurance = (Button) findViewById(R.id.btnVarifyInsurance);

		layCreditCard = (LinearLayout) findViewById(R.id.layCreditCard);
		tvCrediCardAmount = (TextView) findViewById(R.id.tvCrediCardAmount);
		etCardholder = (EditText) findViewById(R.id.etCardholder);
		etCardNo = (EditText) findViewById(R.id.etCardNo);
		etCardCVV = (EditText) findViewById(R.id.etCardCVV);
		spCardType = (Spinner) findViewById(R.id.spCardType);
		spCardExpiryMonth = (Spinner) findViewById(R.id.spCardExpiryMonth);
		spCardExpiryYear = (Spinner) findViewById(R.id.spCardExpiryYear);

		spInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				insurance = payerBeens.get(position).payer_id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		btnVarifyInsurance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String policyNo = etPolicynumber.getText().toString();
				String group = etGroup.getText().toString();
				String code = etCode.getText().toString();

				if (policyNo.isEmpty()){
					etPolicynumber.setError("Policy no should not be empty");
				}else if (group.isEmpty()){
					etGroup.setError("Group should not be empty");
				}else if(code.isEmpty()){
					etCode.setError("Code should not be empty");
				}else {
					if (checkInternetConnection.isConnectedToInternet()){
						saveInsuranceInfo(insurance,policyNo,group,code);
					}else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
					}
				}
			}
		});


		final String cardTypeArr[] = {"Visa","MasterCard","Discover","American Express"};
		final String monthsArr[] = {"01","02","03","04","05","06","07","08","09","10","11","12"};
		int year = Calendar.getInstance().get(Calendar.YEAR);
		final String yearsArr[] = {year+"",(year+1)+"",(year+2)+"",(year+3)+"",(year+4)+"",(year+5)+""
				,(year+6)+"",(year+7)+"",(year+8)+"",(year+9)+"",(year+10)+""};
		spCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedCardType = cardTypeArr[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spCardExpiryMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedExpMonth = monthsArr[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spCardExpiryYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedExpYear = yearsArr[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		ArrayAdapter<String> spCardTypeAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,cardTypeArr);
		spCardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCardType.setAdapter(spCardTypeAdapter);


		ArrayAdapter<String> spMonthAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,monthsArr);
		spMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCardExpiryMonth.setAdapter(spMonthAdapter);


		ArrayAdapter<String> spYearAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,yearsArr);
		spYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCardExpiryYear.setAdapter(spYearAdapter);


		if (checkInternetConnection.isConnectedToInternet()){
			getPayers();
			getPackageAmount();
		}else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
		}

	}//oncreate


	/*public void initPaypal() {
		PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
		Intent intent = new Intent(activity, PaymentActivity.class);
		// send the same configuration for restart resiliency
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	}


	private PayPalPayment getThingToBuy(String paymentIntent) {

		String itemName = "Online Care payment";
		//String itemPrice = etDonationAmount.getText().toString();//thingToBuy.getDealConvertedPrice()
		String itemPrice = "40";//tvTotal.getText().toString().replace("Total: $ ", "")
		String items = "1";//thingToBuy.getQuantity()
		Double total = Double.parseDouble(items)*Double.parseDouble(itemPrice);//Double.parseDouble(items)*Double.parseDouble(itemPrice)

		//customToast.showToast(itemName+"  "+itemPrice, 0, 0);
		return new PayPalPayment(new BigDecimal(total+""), "USD", itemName,
				paymentIntent);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm =
						data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					try {
						Log.i(TAG+"====", confirm.toJSONObject().toString(4));
						Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

						JSONObject jobj = new JSONObject(confirm.toJSONObject().toString(4));
						JSONObject reponce = jobj.getJSONObject("response");
						String state = reponce.getString("state");
						String id = reponce.getString("id");
						String createTime = reponce.getString("create_time");
						String intent = reponce.getString("intent");

						String test = state+" , "+id+" , "+createTime+" , "+intent;

						Log.i("--------", test);


	                   *//* String patient_id = prefs.getString("id", "");
	            		String livecare_id = DATA.liveCareIdForPayment+"";
	            		String amount = "40";
	            		String payment_method = "paypal";
	            		String transaction_id = test;


	            		saveTransaction(patient_id, livecare_id, amount, payment_method, transaction_id);*//*
	                   *//* DATA.livecarePaymentPaypalInfo = test;
	                    DATA.isLiveCarePaymentDone = true;
	                    finish();*//*


	        *//*      String userId= prefs.getString("userId", "0");
	            //  String amount= thingToBuy.getDealConvertedPrice();
	              String type = "2";
	              String info=test;
	              String dealId = thingToBuy.getDealId();
	              String quantity= thingToBuy.getQuantity();

	              Double total = DATA.totalInUs; //Integer.parseInt(quantity)*Double.parseDouble(amount)

	                    new PayByPaypal(userId, total+"", type, info, quantity, dealId).execute();*//*
						// thingToBuy
						//user_id amount type info qty deal_id
						*//**
						 *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
						 * or consent completion.
						 * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
						 * for more details.
						 *
						 * For sample mobile backend interactions, see
						 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
						 *//*
						Toast.makeText(
								getApplicationContext(),
								"PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
								.show();

						bookAppointmentCall();

					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ", e);
					}
				}
			}
		}
	}//onactivityResult

	@Override
	public void onDestroy() {
		// Stop service when done
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}*/

	private void bookAppointmentCall() {

		pd.show();

		client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("from_id",prefs.getString("id",""));
		params.put("from","cp");

		params.put("patient_id", DATA.selectedUserCallId);//patient id    prefs.getString("id", "")
		params.put("sub_patient_id", "");//sub patient id   prefs.getString("subPatientID", "")
		params.put("symptom_id", prefs.getString("selectedSympId", ""));//symptom id
		params.put("condition_id", prefs.getString("selectedCondId", ""));//condition id
		params.put("description", prefs.getString("extraInfo", ""));//description
		//params.put("dr_schedule_id", DATA.selectedDayIdForAppointment);//day selected
		//params.put("time", DATA.selectedRGEveMor);//morning or evening
		params.put("payment_method", DATA.selectedRGFreePaypal);//paypal or free
		params.put("free_reason", DATA.selectedRGFreeOptions);//if free.. reason for free
		params.put("report_ids", DATA.selectedReportIdsForApntmt);//report ids (4_3_54_5)

		params.put("appointment_date", prefs.getString("apptmntDate", ""));
		params.put("slot_id", DATA.selectedSlotIdForAppointment);

		if (DATA.selectedRGFreePaypal.equals("credit_card")) {
			params.put("creditCardType", selectedCardType);
			params.put("creditCardNumber", cardNo);
			params.put("expDateMonth", selectedExpMonth);
			params.put("expDateYear", selectedExpYear);
			params.put("cvv2Number", cvvCode);
			params.put("total_amount", "5");
		}

		/*DATA.print("--online care selected report ids: "+DATA.selectedReportIdsForApntmt);
		DATA.print("dr_schedule_id: "+DATA.selectedDayIdForAppointment);*/
		DATA.print("--param in bookAppointment "+params.toString());

		client.get(DATA.baseUrl+"bookAppointment", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in book appointment call: "+content);

					try {

						jsonObject = new JSONObject(content);

						DATA.requestedAppntDate = jsonObject.getString("msg");

						status = jsonObject.getString("status");
						msg = jsonObject.getString("msg");

						if(status.equals("success")) {

							String apptInfo = jsonObject.getString("appointment_info");
							JSONObject appointmentsObject = new JSONObject(apptInfo);

						/*DATA.requestedAppntTime = appointmentsObject.getString("time");
						DATA.requestedAppntDate = appointmentsObject.getString("appointment_date");
						DATA.requestedAppntDay = appointmentsObject.getString("day_name");*/

							OpenActivity op = new OpenActivity(activity);
							op.open(AppntmtReqtd.class, true);
						} else {
							Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						DATA.print("--Exception in bookAppointment: "+e);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: bookAppointment, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure bookAppointment: "+content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	/*Dialog insDialog;
	public void initInsuranceDialog() {
		 insDialog = new Dialog(activity);
		insDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		insDialog.setContentView(R.layout.dialog_insurance);

		final EditText etInsurance = (EditText) insDialog.findViewById(R.id.etInsurance);
		final EditText etPolicynumber = (EditText) insDialog.findViewById(R.id.etPolicynumber);
		final EditText etGroup = (EditText) insDialog.findViewById(R.id.etGroup);
		final EditText etCode = (EditText) insDialog.findViewById(R.id.etCode);
		Button btnSubmitForm = (Button) insDialog.findViewById(R.id.btnSubmitForm);

		Button btnCancel = (Button) insDialog.findViewById(R.id.btnCancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				insDialog.dismiss();
				InsuranceCont.setVisibility(View.GONE);
				btnAddInsurance.setVisibility(View.GONE);
			}
		});

		btnSubmitForm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String insurance = etInsurance.getText().toString();
				String p_no =  etPolicynumber.getText().toString();
				String group =  etGroup.getText().toString();
				String code =  etCode.getText().toString();

				if (insurance.isEmpty() || p_no.isEmpty() || group.isEmpty() || code.isEmpty()) {
					Toast.makeText(activity, "All fields are required", Toast.LENGTH_LONG).show();
				} else {

					if (checkInternetConnection.isConnectedToInternet()) {
						saveInsuranceInfo(insurance, p_no, group, code);
					} else {
						Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_LONG).show();
					}


				}
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(insDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    insDialog.show();
	    insDialog.getWindow().setAttributes(lp);
	}



	public void saveInsuranceInfo(String insurance, String p_no, String group, String code) {
		pd.show();
			  AsyncHttpClient client = new AsyncHttpClient();
			  RequestParams params = new RequestParams();

			  params.put("patient_id", prefs.getString("id", "0"));
			  params.put("insurance", insurance);
			  params.put("policy_number", p_no);
			  params.put("group", group);
			  params.put("code", code);


			  Editor ed = prefs.edit();
			  ed.putString("insurance", insurance);
			  ed.putString("policy_number", p_no);
			  ed.putString("group", group);
			  ed.putString("code", group);
			  ed.commit();

			 DATA.print("--in saveInsuranceInfo params: "+params.toString());

			  client.post(DATA.baseUrl+"/saveInsuranceInfo", params, new AsyncHttpResponseHandler() {
			   @Override

			   public void onSuccess(int statusCode, String content) {
				   pd.dismiss();
				   DATA.print("--reaponce in saveInsuranceInfo "+content);
				   //--reaponce in saveInsuranceInfo {"success":1,"message":"Saved."}

				  try {
					JSONObject jsonObject = new JSONObject(content);
					int success = jsonObject.getInt("success");

					if (success == 1) {
						insDialog.dismiss();

						customToast.showToast("Your information saved", 0, 1);

						 tvInsurance.setText(prefs.getString("insurance", "none"));
						  tvPolicyNo.setText(prefs.getString("policy_number", "none"));
						  tvGroup.setText(prefs.getString("group", "none"));
						  tvCode.setText(prefs.getString("code", "none"));
						  btnAddInsurance.setVisibility(View.GONE);
					} else {
						customToast.showToast("Opps! Some thing went wrong please try again", 0, 1);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(activity, "Internal server error. Type JSON exception", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}


			   }

			   @Override

			   public void onFailure(Throwable error, String content) {
				   pd.dismiss();
				   DATA.print("--onFailure in saveInsuranceInfo "+content);
		Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			   }
			  });

			 }//end saveInsuranceInfo*/




	public void saveInsuranceInfo(String insurance, String p_no, String group, String code) {
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("patient_id", prefs.getString("id", "0"));
		params.put("insurance", insurance);
		params.put("policy_number", p_no);
		params.put("group", group);
		params.put("code", code);


		Editor ed = prefs.edit();
		ed.putString("insurance", insurance);
		ed.putString("policy_number", p_no);
		ed.putString("group", group);
		ed.putString("code", group);
		ed.commit();

		DATA.print("--in saveInsuranceInfo params: "+params.toString());

		client.post(DATA.baseUrl+"/saveInsuranceInfo", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveInsuranceInfo "+content);
					//--reaponce in saveInsuranceInfo {"success":1,"message":"Saved."}
					//{"error":1,"message":"Your Insurance has not been verified, Do you want to pay using Paypal or Credit Card."}
					try {
						JSONObject jsonObject = new JSONObject(content);

						if (jsonObject.has("success")){
							customToast.showToast("Your insurance information varified !", 0, 1);
							btnBKAccept.setVisibility(View.VISIBLE);
						}else if(jsonObject.has("error")){
							new Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setMessage(jsonObject.getString("message")).setPositiveButton("OK",null).show();
						}else {
							customToast.showToast("Opps! Some thing went wrong please try again", 0, 1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Toast.makeText(activity, "Internal server error. Type JSON exception", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveInsuranceInfo, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--onFailure in saveInsuranceInfo "+content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveInsuranceInfo


	String pkgAmount = "5";
	public void getPackageAmount() {
		//DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		client.get(DATA.baseUrl+"getPackageAmount", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getPackageAmount: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						pkgAmount = jsonObject.getJSONObject("data").getString("amount");
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getPackageAmount, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail getPackageAmount: " +content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}

	ArrayList<PayerBean> payerBeens;
	public void getPayers() {

		//DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		client.get(DATA.baseUrl+"getPayers", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getPayers: "+content);
					try {
						payerBeens = new ArrayList<PayerBean>();
						PayerBean payerBean;
						JSONArray jsonArray = new JSONArray(content);
						for (int i = 0; i<jsonArray.length(); i++){
							String id = jsonArray.getJSONObject(i).getString("id");
							String payer_id = jsonArray.getJSONObject(i).getString("payer_id");
							String payer_name = jsonArray.getJSONObject(i).getString("payer_name");

							payerBean = new PayerBean(id,payer_id,payer_name);
							payerBeens.add(payerBean);
							payerBean = null;
						}

						ArrayAdapter<PayerBean> spInsuranceAdapter = new ArrayAdapter<PayerBean>(
								activity,
								R.layout.spinner_item_lay,
								payerBeens);
						spInsuranceAdapter.setDropDownViewResource(R.layout.spinner_item_lay);
						spInsurance.setAdapter(spInsuranceAdapter);

					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getPayers, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail getPayers: " +content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}

}
