package com.app.onlinecare.paypal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.onlinecare.BaseActivity;
import com.app.onlinecare.Login;
import com.app.onlinecare.R;
import com.app.onlinecare.api.ApiManager;
import com.app.onlinecare.util.CheckInternetConnection;
import com.app.onlinecare.util.CustomToast;
import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.GloabalMethods;
import com.app.onlinecare.util.LiveCareInsurance;
import com.app.onlinecare.util.LiveCareInsuranceCardhelper;
import com.app.onlinecare.util.LiveCareInsuranceInterface;
import com.app.onlinecare.util.OpenActivity;
import com.app.onlinecare.util.SharedPrefsHelper;
import com.braintreepayments.cardform.view.CardForm;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import ss.com.bannerslider.views.BannerSlider;

import static com.app.onlinecare.util.SharedPrefsHelper.PKG_AMOUNT;


public class PaymentLiveCare extends BaseActivity {
	Activity activity;
	CustomToast customToast;
	OpenActivity openActivity;

	SharedPreferences prefs;
	SharedPrefsHelper sharedPrefsHelper;

	CheckInternetConnection checkInternetConnection;

	ProgressDialog pd;

	private static final String TAG = "paymentExample";
	/**
	 * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
	 *
	 * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
	 * from https://developer.paypal.com
	 *
	 * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
	 * without communicating to PayPal's servers.
	 */
	public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

	// note that these credentials will differ between live & sandbox environments.
	//private static final String CONFIG_CLIENT_ID = "AfZoGBCXLzzQhGN_XLwduoBIDuu8Q96W_LgWs6TDdo_BFdPu8ux_kGCs-IDY";


	//private static final String CONFIG_CLIENT_ID = "AYSCmbv7poUco_RJRJGLe1FaHcWlCvFGTpRRDcbdvq8oywmF2mDDUUMsw_NEH8Ebuy1s-oa_pq8wlv1f";

	public static final String CONFIG_CLIENT_ID_JAMAL = "AWA7JUCEgS0fPgwYlVgMk8gxgqXuIlZk4bRapYIdLXCuZj2ASXw9Ukz92020S0eCxAlVNwzhX3BR6eQ4";
	//this is live paypal key- Sir Jamal account
	public static final String CONFIG_CLIENT_ID_PREFS_KEY = "paypal_config_client_id";
	public static final String CONFIG_CLIENT_ID = SharedPrefsHelper.getInstance().get(CONFIG_CLIENT_ID_PREFS_KEY,  CONFIG_CLIENT_ID_JAMAL);

	private static final int REQUEST_CODE_PAYMENT = 1;
//    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
//    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

	private static PayPalConfiguration config = new PayPalConfiguration()
			.environment(CONFIG_ENVIRONMENT)
			.clientId(CONFIG_CLIENT_ID)          //.defaultUserEmail("mustafa.09sw77.muet@gmail.com").sandboxUserPassword("checking12")
			// The following are only used in PayPalFuturePaymentActivity.
			.merchantName("OnlineCare")
			.merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
			.merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

	//LinearLayout InsuranceCont;
	//TextView tvInsurance,tvPolicyNo,tvGroup,tvCode,tvNoInsuranceAdded;
	//Button btnAddInsurance;

	public static Button btnPay;

	//LinearLayout insuranceCont;
	//Spinner spInsurance;
	//EditText etPolicynumber,etGroup,etCode;
	//Button btnVarifyInsurance;
	//String insurance = "";

	LinearLayout layCreditCard;
	TextView tvCrediCardAmount;
	EditText etCardholder,etCardNo,etCardCVV;
	Spinner spCardType,spCardExpiryMonth,spCardExpiryYear;

	public static boolean insuranceOrCredit = false;
	public static String payment_typeIC = "";

	public static String selectedCardType = "",selectedExpMonth = "",selectedExpYear = "",
			cardHolderName = "",cardNo = "",cvvCode = "";

	CardView cardV;//adview
	LinearLayout layFreeOptions;

	CardForm cardForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //before 

		setContentView(R.layout.dialog_payment_livecare);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(this.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(lp);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//********************************************Paypal********************************************
		activity = PaymentLiveCare.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(this);
		sharedPrefsHelper = SharedPrefsHelper.getInstance();
		openActivity = new OpenActivity(this);

		btnPay = (Button) findViewById(R.id.btnPay);


		if(getSupportActionBar() != null){
			getSupportActionBar().hide();
		}
		cardV = findViewById(R.id.cardV);

		ImageView ivClose = findViewById(R.id.ivClose);
		ivClose.setOnClickListener(view -> {
			onBackPressed();
		});
        
        /*btnAddInsurance = (Button) findViewById(btnAddInsurance);
        btnAddInsurance.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initInsuranceDialog();
			}
		});*/



		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(PaymentLiveCare.this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(PaymentLiveCare.this);
		}
		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		checkInternetConnection = new CheckInternetConnection(this);


		Intent intent = new Intent(this, PayPalService.class);
		intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		startService(intent);

		//********************************************Paypal********************************************

		//RadioGroup rgPayment = (RadioGroup) findViewById(R.id.rgPayment);

		//RadioGroup rgPayment = new RadioGroup(activity);

		final RadioGroup rgFreeOptions = (RadioGroup) findViewById(R.id.rgFreeOptions);
		layFreeOptions = findViewById(R.id.layFreeOptions);

		RadioButton radioPayPal = findViewById(R.id.radioPayPal);
		RadioButton radioCreditCard = findViewById(R.id.radioCreditCard);
		RadioButton radioHealthInsurance = findViewById(R.id.radioHealthInsurance);
		RadioButton radioFreeCare = findViewById(R.id.radioFreeCare);

		View.OnClickListener radiosClick = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.radioPayPal:

						//DATA.selectedRGFreePaypal = "paypal";
						payment_typeIC = "";
						insuranceOrCredit = false;
						DATA.isFreeCare = false;//for launch paypal in onBuyPressed
						rgFreeOptions.setVisibility(View.GONE);
                        layFreeOptions.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.GONE);
						btnPay.setEnabled(true);

						radioPayPal.setChecked(true);
						radioCreditCard.setChecked(false);
						radioHealthInsurance.setChecked(false);
						radioFreeCare.setChecked(false);

						cardV.setVisibility(View.VISIBLE);//add banner
						//insuranceCont.setVisibility(View.GONE);
						//btnPay.setVisibility(View.VISIBLE);
						//btnAddInsurance.setVisibility(View.GONE);
						//tvNoInsuranceAdded.setVisibility(View.GONE);
						break;

					case R.id.radioFreeCare:

						//DATA.selectedRGFreePaypal = "free";
						payment_typeIC = "";
						insuranceOrCredit = false;
						DATA.isFreeCare = true;
						rgFreeOptions.setVisibility(View.GONE);//VISIBLE // vhange this to vis to show radios GM
                        layFreeOptions.setVisibility(View.VISIBLE);
						layCreditCard.setVisibility(View.GONE);
						btnPay.setEnabled(true);

						radioPayPal.setChecked(false);
						radioCreditCard.setChecked(false);
						radioHealthInsurance.setChecked(false);
						radioFreeCare.setChecked(true);

						cardV.setVisibility(View.GONE);//add banner

						btnPay.setEnabled(false);
						//showFreecareCodeDialog();

						//insuranceCont.setVisibility(View.GONE);
						//btnPay.setVisibility(View.VISIBLE);
						break;

					case R.id.radioHealthInsurance:

						//DATA.selectedRGFreePaypal = "free";
						payment_typeIC = "insurance";
						insuranceOrCredit = true;
						DATA.isFreeCare = true;
						rgFreeOptions.setVisibility(View.GONE);
                        layFreeOptions.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.GONE);

						btnPay.setEnabled(false);
						new LiveCareInsurance(activity).showInsurancesList();

						radioPayPal.setChecked(false);
						radioCreditCard.setChecked(false);
						radioHealthInsurance.setChecked(true);
						radioFreeCare.setChecked(false);

						cardV.setVisibility(View.VISIBLE);//add banner
						//insuranceCont.setVisibility(View.VISIBLE);
						//btnPay.setVisibility(View.GONE);
						break;
					case R.id.radioCreditCard:

						//============paypal==============
						/*payment_typeIC = "";
						insuranceOrCredit = false;
						DATA.isFreeCare = false;//for launch paypal in onBuyPressed
						rgFreeOptions.setVisibility(View.GONE);
						layFreeOptions.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.GONE);
						btnPay.setEnabled(true);

						radioPayPal.setChecked(false);
						radioCreditCard.setChecked(true);
						radioHealthInsurance.setChecked(false);
						radioFreeCare.setChecked(false);

						cardV.setVisibility(View.VISIBLE);*///add banner
						//============paypal================

						DATA.isFreeCare = true;
						insuranceOrCredit = true;
						payment_typeIC = "credit_card";
						rgFreeOptions.setVisibility(View.GONE);
                        layFreeOptions.setVisibility(View.GONE);
						layCreditCard.setVisibility(View.VISIBLE);
						btnPay.setEnabled(true);

						radioPayPal.setChecked(false);
						radioCreditCard.setChecked(true);
						radioHealthInsurance.setChecked(false);
						radioFreeCare.setChecked(false);

						cardV.setVisibility(View.GONE);//add banner
						//insuranceCont.setVisibility(View.GONE);
						//btnPay.setVisibility(View.VISIBLE);

						if(TextUtils.isEmpty(stripe_pub_key) || stripe_pub_key.equalsIgnoreCase("null")){
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle(getResources().getString(R.string.app_name))
									.setMessage(paymentMessage)
									.setPositiveButton("Done", null)
									.create().show();
							btnPay.setEnabled(false);

						}else {
							btnPay.setEnabled(true);
						}

						break;

				}
			}
		};
		radioPayPal.setOnClickListener(radiosClick);
		radioCreditCard.setOnClickListener(radiosClick);
		radioHealthInsurance.setOnClickListener(radiosClick);
		radioFreeCare.setOnClickListener(radiosClick);

		//===========================new FreeCare Code===============================================
        EditText etFreeCareCode = findViewById(R.id.etFreeCareCode);
        Button btnSubmitFreeCareCode = findViewById(R.id.btnSubmitFreeCareCode);
        btnSubmitFreeCareCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialogSupport.dismiss();
                String freeCareCode = etFreeCareCode.getText().toString().trim();
                if(TextUtils.isEmpty(freeCareCode)){
                    etFreeCareCode.setError("Please enter a valid free care code");
                }else {
                    checkFreeCareCode(freeCareCode);
                }
            }
        });
        //===========================new FreeCare Code===============================================

		//selecting payment option
				/*rgPayment.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {


					}
				});*///selecting payment option ends
				
				/*InsuranceCont = (LinearLayout) findViewById(InsuranceCont);
			     tvInsurance = (TextView) findViewById(tvInsurance);
			     tvPolicyNo = (TextView) findViewById(tvPolicyNo);
			     tvGroup = (TextView) findViewById(tvGroup);
			     tvCode = (TextView) findViewById(tvCode);
			     tvNoInsuranceAdded = (TextView) findViewById(tvNoInsuranceAdded);*/

				/*insuranceCont= (LinearLayout) findViewById(R.id.insuranceCont);
		 		spInsurance = (Spinner) findViewById(R.id.spInsurance);
		 		etPolicynumber = (EditText) findViewById(R.id.etPolicynumber);
				etGroup = (EditText) findViewById(R.id.etGroup);
				etCode = (EditText) findViewById(R.id.etCode);;
				btnVarifyInsurance = (Button) findViewById(R.id.btnVarifyInsurance);*/

		layCreditCard = (LinearLayout) findViewById(R.id.layCreditCard);
		tvCrediCardAmount = (TextView) findViewById(R.id.tvCrediCardAmount);
		etCardholder = (EditText) findViewById(R.id.etCardholder);
		etCardNo = (EditText) findViewById(R.id.etCardNo);
		etCardCVV = (EditText) findViewById(R.id.etCardCVV);
		spCardType = (Spinner) findViewById(R.id.spCardType);
		spCardExpiryMonth = (Spinner) findViewById(R.id.spCardExpiryMonth);
		spCardExpiryYear = (Spinner) findViewById(R.id.spCardExpiryYear);

		/*spInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				insurance = payerBeens.get(position).payer_id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		btnVarifyInsurance.setOnClickListener(new View.OnClickListener() {
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
		});*/


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

		rgFreeOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
					case R.id.radioStudent:
							/*tvNoInsuranceAdded.setVisibility(View.GONE);
							InsuranceCont.setVisibility(View.GONE);
							btnAddInsurance.setVisibility(View.GONE);*/
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
							break;*/
					case R.id.radioSingleMom:
							/*tvNoInsuranceAdded.setVisibility(View.GONE);
							InsuranceCont.setVisibility(View.GONE);*/
						break;

					default:
						break;
				}
			}
		});


		transaction_id = "";
		if (checkInternetConnection.isConnectedToInternet()){
			//getPayers();
			getPackageAmount();
			paymentCredentials();
		}else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
		}

		BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);
		GloabalMethods gloabalMethods = new GloabalMethods(appCompatActivity);
		gloabalMethods.setupBannerSlider(bannerSlider, false);


		radioPayPal.performClick();//to reset values to paypal


		tvCrediCardAmount.setText("Payment Amount $"+sharedPrefsHelper.get(PKG_AMOUNT, "5"));

		cardForm =  findViewById(R.id.card_form);
		cardForm.cardRequired(true)
				.expirationRequired(true)
				.cvvRequired(true)
				.cardholderName(CardForm.FIELD_REQUIRED)
				//.postalCodeRequired(true)
				//.mobileNumberRequired(true)
				// .mobileNumberExplanation("SMS is required on this number")
				.actionLabel("Virtual Care Charges")
				.setup(appCompatActivity);

	}//oncreate


	public void onBuyPressed(View v) {
		/*
		 * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
		 * Change PAYMENT_INTENT_SALE to
		 *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
		 *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
		 *     later via calls from your server.
		 *
		 * Also, to include additional payment details and an item list, see getStuffToBuy() below.
		 */
		if(!checkInternetConnection.isConnectedToInternet()){
			customToast.showToast("Please check internet connection !", 0, Toast.LENGTH_SHORT);
		}else {

			if (payment_typeIC.equals("credit_card")){
				/*cardHolderName = etCardholder.getText().toString();
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
				}*/

				stripeFormSubmitClick();
				return;
			}

			if (DATA.isFreeCare) {
				DATA.isLiveCarePaymentDone = true;
				finish();
			}else {

				PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

				/*
				 * See getStuffToBuy(..) for examples of some available payment options.
				 */

				Intent intent = new Intent(PaymentLiveCare.this, PaymentActivity.class);

				// send the same configuration for restart resiliency
				intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

				intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

				startActivityForResult(intent, REQUEST_CODE_PAYMENT);


			}
		}
	}




	private PayPalPayment getThingToBuy(String paymentIntent) {

		String itemName = "Virtual Care Charges";
		//String itemPrice = etDonationAmount.getText().toString();//thingToBuy.getDealConvertedPrice()
		String itemPrice = sharedPrefsHelper.get(PKG_AMOUNT, "5");//tvTotal.getText().toString().replace("Total: $ ", "")
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

                    
                   /* String patient_id = prefs.getString("id", "");
            		String livecare_id = DATA.liveCareIdForPayment+"";
            		String amount = "40";
            		String payment_method = "paypal";
            		String transaction_id = test;
            		
            		
            		saveTransaction(patient_id, livecare_id, amount, payment_method, transaction_id);*/
						DATA.livecarePaymentPaypalInfo = test;
						DATA.isLiveCarePaymentDone = true;
						finish();
                    
                    
        /*      String userId= prefs.getString("userId", "0");
            //  String amount= thingToBuy.getDealConvertedPrice(); 
              String type = "2";
              String info=test;
              String dealId = thingToBuy.getDealId();
              String quantity= thingToBuy.getQuantity();
                      
              Double total = DATA.totalInUs; //Integer.parseInt(quantity)*Double.parseDouble(amount)   
                    
                    new PayByPaypal(userId, total+"", type, info, quantity, dealId).execute();*/
						// thingToBuy
						//user_id amount type info qty deal_id
						/**
						 *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
						 * or consent completion.
						 * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
						 * for more details.
						 *
						 * For sample mobile backend interactions, see
						 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
						 */
						Toast.makeText(
								getApplicationContext(),
								"PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
								.show();

					} catch (JSONException e) {
						Log.e(TAG, "an extremely unlikely failure occurred: ", e);
					}
				}
			}
		}else {
			if(liveCareInsuranceCardhelper != null){
				//Insurance card image
				liveCareInsuranceCardhelper.onActivityResult(requestCode, resultCode, data);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}//onactivityResult


	//pick Insurance card image front + back on add new insurance
	LiveCareInsuranceCardhelper liveCareInsuranceCardhelper;
	public void callPicCardImgMethod(int imgFlag, LiveCareInsuranceInterface liveCareInsuranceInterface){
		liveCareInsuranceCardhelper = new LiveCareInsuranceCardhelper(activity, liveCareInsuranceInterface);
		liveCareInsuranceCardhelper.pickInsuranceCardPhoto(imgFlag);
	}

	@Override
	public void onDestroy() {
		// Stop service when done
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}
    
    
   /* public void saveTransaction(
    		String patient_id,
    		String livecare_id,
    		String amount,
    		String payment_method,
    		String transaction_id) {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("patient_id", patient_id);
		params.put("livecare_id", livecare_id);
		params.put("amount", amount);
		params.put("payment_method", payment_method);
		params.put("transaction_id", transaction_id);
		System.out.println("--params in saveTransection patient_id "+patient_id+" livecare_id "+livecare_id+" amount "+amount+" payment_method "+payment_method+" transaction_id "+transaction_id);
		pd.show();
		client.post(DATA.baseUrl+"/saveTransaction/"+patient_id,params ,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, String content) {
				pd.dismiss();
				System.out.println("-- responce in saveTransaction "+content);
				
				Toast.makeText(getApplicationContext(), "Transaction Successfull !", 0).show();
				//finish();
				
				SharedPreferences.Editor ed = prefs.edit();
				ed.putBoolean("livecareTimerRunning", true);
				ed.putString("getLiveCareApptID", DATA.liveCareIdForPayment+"");
				ed.commit();

				Intent intent1 = new Intent();
				intent1.setAction("LIVE_CARE_WAITING_TIMER");
				sendBroadcast(intent1);

				Toast.makeText(PaymentLiveCare.this, "Registered for live care", 0).show();
				openActivity.open(LiveCareWaitingArea.class, true);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				
				pd.dismiss();
				Toast.makeText(getApplicationContext(), "Somrthing went wrong please try again !", 0).show();
				System.out.println("--responce in failure getMedicalHistory: "+content);
	 
			}
		});
		 
	}*/
    
    
    /*Dialog insDialog;
	public void initInsuranceDialog() {
		 insDialog = new Dialog(PaymentLiveCare.this);
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
					Toast.makeText(PaymentLiveCare.this, "All fields are required", Toast.LENGTH_LONG).show();
				} else {
					
					if (checkInternetConnection.isConnectedToInternet()) {
						saveInsuranceInfo(insurance, p_no, group, code);
					} else {
						Toast.makeText(PaymentLiveCare.this, "Please check internet connection", Toast.LENGTH_LONG).show();
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
	}*/
	
	
	
	/*public void saveInsuranceInfo(String insurance, String p_no, String group, String code) {
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
			 
			 System.out.println("--in saveInsuranceInfo params: "+params.toString());

			  client.post(DATA.baseUrl+"/saveInsuranceInfo", params, new AsyncHttpResponseHandler() {
			   @Override
			 
			   public void onSuccess(int statusCode, String content) {
				   pd.dismiss();
				   System.out.println("--reaponce in saveInsuranceInfo "+content);
				   //--reaponce in saveInsuranceInfo {"success":1,"message":"Saved."}
				   //{"error":1,"message":"Your Insurance has not been verified, Do you want to pay using Paypal or Credit Card."}
				  try {
					JSONObject jsonObject = new JSONObject(content);

					  if (jsonObject.has("success")){
						  customToast.showToast("Your insurance information varified !", 0, 1);
						  btnPay.setVisibility(View.VISIBLE);
					  }else if(jsonObject.has("error")){
						  new AlertDialog.Builder(PaymentLiveCare.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setMessage(jsonObject.getString("message")).setPositiveButton("OK",null).show();
					  }else {
						  customToast.showToast("Opps! Some thing went wrong please try again", 0, 1);
					  }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(PaymentLiveCare.this, "Internal server error. Type JSON exception", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				  
				  
			   }

			   @Override

			   public void onFailure(Throwable error, String content) {
				   pd.dismiss();
				   System.out.println("--onFailure in saveInsuranceInfo "+content);
		Toast.makeText(PaymentLiveCare.this, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			   }
			  });

			 }*///end saveInsuranceInfo


	//String pkgAmount = "5";
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
					System.out.println("--reaponce in getPackageAmount: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						String pkgAmount = jsonObject.getJSONObject("data").getString("amount");
						sharedPrefsHelper.save(PKG_AMOUNT, pkgAmount);

						tvCrediCardAmount.setText("Payment Amount $"+pkgAmount);

					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
					}

				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: getPackageAmount, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail getPackageAmount: " +content);
					//new GloabalMethods(activity).checkLogin(content);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}catch (Exception e1){
					e1.printStackTrace();
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}


	Dialog dialogFreeCareCode;
	public void showFreecareCodeDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_freecare_code);
		dialogSupport.setCanceledOnTouchOutside(false);

		EditText etFreeCareCode = dialogSupport.findViewById(R.id.etFreeCareCode);

		Button btnSubmitFreeCareCode = dialogSupport.findViewById(R.id.btnSubmitFreeCareCode);
		Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

		TextView tvMsg = dialogSupport.findViewById(R.id.tvMsg);


		btnSubmitFreeCareCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//dialogSupport.dismiss();
				String freeCareCode = etFreeCareCode.getText().toString().trim();
				if(TextUtils.isEmpty(freeCareCode)){
					etFreeCareCode.setError("Please enter a valid free care code");
				}else {
					checkFreeCareCode(freeCareCode);
				}
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

		dialogFreeCareCode = dialogSupport;
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

	public void checkFreeCareCode(String code){
		transaction_id = code;
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		params.put("hospital_id", Login.HOSPITAL_ID_EMCURA);
		params.put("code", code);

		ApiManager apiManager = new ApiManager(ApiManager.CHECK_FREE_CODE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.CHECK_FREE_CODE)){
			//{"status":"error","message":"Invalid Code."}
			//{"status":"success"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					if(dialogFreeCareCode != null){
						dialogFreeCareCode.dismiss();
					}
					btnPay.setEnabled(true);
					//btnPay.performClick();
					layFreeOptions.setVisibility(View.GONE);
					//cardV.setVisibility(View.VISIBLE);

					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(jsonObject.optString("message"))
							.setPositiveButton("Continue", null)
							.create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {

							btnPay.performClick();
						}
					});
					alertDialog.show();
				}else {
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle(getResources().getString(R.string.app_name))
							.setMessage(jsonObject.optString("message"))
							.setPositiveButton("Done", null)
							.create().show();
				}

			} catch (Exception e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.STRIPE_CHARGE)){

			//{"status":"success","message":"Your payment has been completed"}{"status":"error","message":""}
			//{"status":"success","message":"Your payment has been completed"}
			//{"status":"success","message":"Your payment has been completed","transaction_id":"ch_1HfTXuKcE9q5i86vMwKkSPbW"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					transaction_id = jsonObject.getString("transaction_id");
					if (DATA.isFreeCare) {//this was true on radio creditcard click
						DATA.isLiveCarePaymentDone = true;
						finish();
					}
				}else {
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Payment Error")
							.setMessage(jsonObject.optString("message", DATA.CMN_ERR_MSG))
							.setPositiveButton("Done", null)
							.create().show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.PAYMENT_CREDS)){
			/*{
				"status": "success",
					"data": {
				"stripe": {
					"stripe_pub_key": "pk_test_YGQVbRrz1utJrlEGgXcHp95f00ipD4Kred",
							"stripe_secret_key": "sk_test_T7HgyGoo6CltQh9Bd2Dr4xmG00zqmkVSAq"
				},
				"braintree": {
					"brain_auth_key": null
				}
			},
				"message": "Payment method is not configured. Please contact clinic."
			}*/
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONObject data = jsonObject.getJSONObject("data");
				JSONObject stripe = data.getJSONObject("stripe");
				stripe_pub_key = stripe.getString("stripe_pub_key");
				paymentMessage = jsonObject.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}


	String stripe_pub_key = "", paymentMessage = "Payment method is not configured. Please contact clinic.";
	public static String transaction_id = "";
	private void stripeFormSubmitClick(){
		String cardHoldername = cardForm.getCardholderName();
		String cardNo = cardForm.getCardNumber();
		String cardExpMonth = cardForm.getExpirationMonth();
		String cardExpYear= cardForm.getExpirationYear();
		String cardCVV= cardForm.getCvv();

		//boolean validate = true;

		System.out.println("-- cardHolder : "+cardHoldername);
		System.out.println("-- cardHolder : "+cardNo);
		System.out.println("-- cardHolder : "+cardExpMonth);
		System.out.println("-- cardHolder : "+cardExpYear);
		System.out.println("-- cardHolder : "+cardCVV);

		boolean validated = true;
		if(TextUtils.isEmpty(cardHoldername)){
			validated = false;
			cardForm.getCardholderNameEditText().setError("This field is required");
		}
		if(TextUtils.isEmpty(cardNo)){
			validated = false;
			cardForm.getCardEditText().setError("This field is required");
		}
		if(TextUtils.isEmpty(cardExpMonth) || TextUtils.isEmpty(cardExpYear)){
			validated = false;
			cardForm.getExpirationDateEditText().setError("This field is required");
		}
		if(TextUtils.isEmpty(cardCVV)){
			validated = false;
			cardForm.getCvvEditText().setError("This field is required");
		}

		if(! validated){
			customToast.showToast("Please enter a valid card information" , 0,0);
			return;
		}

		Card card = Card.create(cardNo,
				Integer.parseInt(cardExpMonth),
				Integer.parseInt(cardExpYear),
				cardCVV);//new Card("4242424242424242", Integer.parseInt("2"), Integer.parseInt("22"), 123);//4242-4242-4242-4242

		payWithStripe(card);
	}

	//public static final String publishableKeyJamal = "pk_test_eoLsWE9jXisnnOViyG5COPTi00YrcRk4F8";//Sir jamal onlinecare demo
	public static final String publishableKeyJamal = "pk_live_dhXr5jML6eQ4GXhI5G02VxMD00Ip3V0kEz";//Sir Jamal Live Account Publishable key for stripe
	public static final String PREFS_KEY_SRIPE_PK = "stripe_pub_key_prefskey";

	public void payWithStripe(Card card){
		//dialog_customProgress.showProgressDialog();
		//String publishableKey = sharedPrefsHelper.get(PREFS_KEY_SRIPE_PK, publishableKeyJamal);
		String publishableKey = stripe_pub_key;//changed by maaz- to restore old key got in pin code service just uncommit above line and commit
		System.out.println("-- Publishable key stripe: "+publishableKey);
		Stripe stripe = new Stripe(activity, publishableKey);
		stripe.createToken(card, new ApiResultCallback<Token>() {
			@Override
			public void onSuccess(Token token) {

				// dialog_customProgress.dismissProgressDialog();

				System.out.println("-- Token created: "+token.toString()
						+"\ntiken id: "+token.getId()
						+"\ntiken type: "+token.getType()
						+"\ntiken bank account: "+token.getBankAccount()
						+"\ntiken card: "+token.getCard()
						+"\ntiken created date: "+token.getCreated()
						+"\ntiken live mode: "+token.getLivemode()
						+"\ntiken used: "+token.getUsed()
						+"\ntiken id: "+token.getId());

				stripeCharge(token.getId());
			}

			@Override
			public void onError(@NotNull Exception error) {
				//dialog_customProgress.dismissProgressDialog();
				// Show localized error message
				error.printStackTrace();
				//customToast.showToast(error.getLocalizedMessage(),0,0);
				new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
						.setTitle("Info")
						.setMessage(error.getMessage())
						.setPositiveButton("Got it",null)
						//.setNegativeButton("Cancel",null)
						.create().show();
			}
		});
	}


	public void stripeCharge(String payment_token){
		RequestParams params = new RequestParams();
		params.put("token", payment_token);
		params.put("amount", sharedPrefsHelper.get(PKG_AMOUNT, "5"));
		ApiManager apiManager = new ApiManager(ApiManager.STRIPE_CHARGE, "post", params, apiCallBack, activity);
		apiManager.loadURL();
	}


	public void paymentCredentials(){
		//ApiManager.shouldShowLoader = false;
		RequestParams params = new RequestParams();
		ApiManager apiManager = new ApiManager(ApiManager.PAYMENT_CREDS, "post", params, apiCallBack, activity);
		apiManager.loadURL();
	}


	/*ArrayList<PayerBean> payerBeens;
	public void getPayers() {

		//DATA.showLoaderDefault(activity, "");
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(DATA.baseUrl+"getPayers", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, String content) {
				//DATA.dismissLoaderDefault();
				System.out.println("--reaponce in getPayers: "+content);
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
			}

			@Override
			public void onFailure(Throwable error, String content) {
				//DATA.dismissLoaderDefault();
				System.out.println("--onfail getPayers: " +content);
				Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			}
		});

	}*/
}
