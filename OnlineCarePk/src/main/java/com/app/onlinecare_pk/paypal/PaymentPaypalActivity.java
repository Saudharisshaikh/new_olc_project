package com.app.onlinecare_pk.paypal;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.util.CheckInternetConnection;
import com.app.onlinecare_pk.util.CustomToast;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.GloabalMethods;
import com.app.onlinecare_pk.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Basic sample using the SDK to make a payment or consent to future payments.
 *
 * For sample mobile backend interactions, see
 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
 */
public class PaymentPaypalActivity extends Activity {
    Activity activity;
    CustomToast customToast;
    OpenActivity openActivity;

    SharedPreferences prefs;

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
    //private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    //private static final String CONFIG_CLIENT_ID = "AfZoGBCXLzzQhGN_XLwduoBIDuu8Q96W_LgWs6TDdo_BFdPu8ux_kGCs-IDY";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PaymentLiveCare.CONFIG_ENVIRONMENT)
            .clientId(PaymentLiveCare.CONFIG_CLIENT_ID)          //.defaultUserEmail("mustafa.09sw77.muet@gmail.com").sandboxUserPassword("checking12")
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("OnlineCare")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    Button btnDonate;
    RadioGroup rgDonate;
    RadioButton radioSelf,radioOther;
    EditText etDonationHowMuchPatients,etDonationFirstName,etDonationLastName
            ,etDonationAddress,etDonationCity,etDonationCountry,etDonationZipCode;//etDonationAmount,
    TextView tvTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_paypal);

        rgDonate = (RadioGroup) findViewById(R.id.rgDonate);
        etDonationHowMuchPatients = (EditText) findViewById(R.id.etDonationHowMuchPatients);
        // etDonationAmount = (EditText) findViewById(R.id.etDonationAmount);
        etDonationFirstName = (EditText) findViewById(R.id.etDonationFirstName);
        etDonationLastName = (EditText) findViewById(R.id.etDonationLastName);
        etDonationAddress = (EditText) findViewById(R.id.etDonationAddress);
        etDonationCity = (EditText) findViewById(R.id.etDonationCity);
        etDonationCountry = (EditText) findViewById(R.id.etDonationCountry);
        etDonationZipCode = (EditText) findViewById(R.id.etDonationZipCode);
        tvTotal = (TextView) findViewById(R.id.tvTotal);

        radioSelf = (RadioButton) findViewById(R.id.radioDonateSelf);
        radioOther = (RadioButton) findViewById(R.id.radioDonateOther);



        etDonationFirstName.setVisibility(View.GONE);
        etDonationLastName.setVisibility(View.GONE);
        etDonationAddress.setVisibility(View.GONE);
        etDonationCity.setVisibility(View.GONE);
        etDonationCountry.setVisibility(View.GONE);
        etDonationZipCode.setVisibility(View.GONE);

        etDonationHowMuchPatients.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (! etDonationHowMuchPatients.getText().toString().isEmpty()) {
                    int am = Integer.parseInt(etDonationHowMuchPatients.getText().toString())*5;
                    //etDonationAmount.setText(am+"");
                    tvTotal.setText("Total: $ "+am);
                }else {
                    //etDonationAmount.setText("");
                    tvTotal.setText("");
                }


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        rgDonate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                //	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

                switch (id) {
                    case -1:
                        DATA.print("--nochoice");
                        break;
                    case R.id.radioDonateSelf:
                        etDonationFirstName.setVisibility(View.GONE);
                        etDonationLastName.setVisibility(View.GONE);
                        etDonationAddress.setVisibility(View.GONE);
                        etDonationCity.setVisibility(View.GONE);
                        etDonationCountry.setVisibility(View.GONE);
                        etDonationZipCode.setVisibility(View.GONE);
                        break;
                    case R.id.radioDonateOther:
                        etDonationFirstName.setVisibility(View.VISIBLE);
                        etDonationLastName.setVisibility(View.VISIBLE);
                        etDonationAddress.setVisibility(View.VISIBLE);
                        etDonationCity.setVisibility(View.VISIBLE);
                        etDonationCountry.setVisibility(View.VISIBLE);
                        etDonationZipCode.setVisibility(View.VISIBLE);

                        break;

                    default:
                        Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();

                        break;
                }

            }
        });

        btnDonate = (Button) findViewById(R.id.btnPay);
        btnDonate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (etDonationHowMuchPatients.getText().toString().isEmpty() ){//||etDonationAmount.getText().toString().isEmpty()

                    Toast.makeText(getApplicationContext(), "Please fill the form !", Toast.LENGTH_SHORT).show();
                } else {
                    onBuyPressed();
                }


            }
        });
        
       /* ActionBar a = getActionBar();
		ColorDrawable c = new ColorDrawable(Color.parseColor("#3bb12c"));  //Color.parseColor("#3bb12c")
		a.setBackgroundDrawable(c);
		a.setTitle(getResources().getString(R.string.payment_paypal_scr_title));*/

        activity = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(this);

        openActivity = new OpenActivity(this);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            pd = new ProgressDialog(PaymentPaypalActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
        }else{
            pd = new ProgressDialog(PaymentPaypalActivity.this);
        }
        pd.setMessage("Please wait...    ");
        pd.setCanceledOnTouchOutside(false);

        checkInternetConnection = new CheckInternetConnection(this);


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }//end oncreate

    public void onBuyPressed() {
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
            customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, Toast.LENGTH_SHORT);
        }else {

            PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

            /*
             * See getStuffToBuy(..) for examples of some available payment options.
             */

            Intent intent = new Intent(PaymentPaypalActivity.this, PaymentActivity.class);

            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

            startActivityForResult(intent, REQUEST_CODE_PAYMENT);



        }
    }




    private PayPalPayment getThingToBuy(String paymentIntent) {

        String itemName = "Online Care Donation";
        //String itemPrice = etDonationAmount.getText().toString();//thingToBuy.getDealConvertedPrice()
        String itemPrice = tvTotal.getText().toString().replace("Total: $ ", "");
        String items = "1";//thingToBuy.getQuantity()
        Double total = Double.parseDouble(items)*Double.parseDouble(itemPrice);//Double.parseDouble(items)*Double.parseDouble(itemPrice)

        //customToast.showToast(itemName+"  "+itemPrice, 0, 0);
        return new PayPalPayment(new BigDecimal(total+""), "USD", itemName,
                paymentIntent);
    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("old jeans with holes", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free rainbow patch", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("long sleeve plaid shirt (no mustache included)", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "hipster jeans", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    /*
     * Add app-provided shipping address to payment
     */
    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(PaymentPaypalActivity.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onProfileSharingPressed(View pressed) {
        Intent intent = new Intent(PaymentPaypalActivity.this, PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());

        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS) );
        return new PayPalOAuthScopes(scopes);
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
                        String donation_from = "";
                        if (radioSelf.isChecked()) {
                            donation_from = "self";
                        } else {
                            donation_from = "other";
                        }
                        String other_name = "";
                        String first_name = etDonationFirstName.getText().toString();
                        String last_name = etDonationLastName.getText().toString();
                        String address = etDonationAddress.getText().toString();
                        String city = etDonationCity.getText().toString();
                        String country = etDonationCountry.getText().toString();
                        String zip = etDonationZipCode.getText().toString();
                        String patient_id = "";//this is dierectly given in method
                        String num_of_patient_amount = etDonationHowMuchPatients.getText().toString();
                        //String amount = etDonationAmount.getText().toString();
                        String amount = tvTotal.getText().toString().replace("Total: $ ", "");
                        String payment_method = "paypal";
                        donate(donation_from, other_name, first_name, last_name, address, city, country, zip, patient_id, num_of_patient_amount, amount, payment_method , test);
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Future Payment code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        Toast.makeText(
                getApplicationContext(), "Client Metadata Id received from SDK", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }




	/*//=========================PayByPaypal=====================================
	class PayByPaypal extends AsyncTask<String, String,String >{
		
		String result = ""; 
		//String url = "http://savnpik.com/services/payment";
		String url = DATA.baseUrl+"payment";
		//user_id amount type info qty deal_id   
		
		String userId;String amount;String type;String info; String quantity; String dealId;
		
		
		
		public PayByPaypal( String userId, String amount,
				String type, String info, String quantity, String dealId) {
			super();
			
			this.userId = userId;
			this.amount = amount;
			this.type = type;
			this.info = info;
			this.quantity = quantity;
			this.dealId = dealId;
		}
		@Override
		protected void onPreExecute() {
			
			pd.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			
			try {
		
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		paramList.add(new BasicNameValuePair("user_id", userId));
		paramList.add(new BasicNameValuePair("amount", amount));
		paramList.add(new BasicNameValuePair("type", type));
		paramList.add(new BasicNameValuePair("info", info));
		paramList.add(new BasicNameValuePair("qty", quantity));
		paramList.add(new BasicNameValuePair("deal_id", dealId));
		
		Log.i("--values in asynch paybypaypal", " user_id "+userId+ "amount "+amount
				+" type "+type+" info "+info+" qty "+quantity+" deal_id "+dealId);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(paramList));
		result = EntityUtils.toString(new DefaultHttpClient().execute(
				httpPost).getEntity());
			
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String res) {
			pd.dismiss();
			
			if(res == null){
				   customToast.showToast(getResources().getString(R.string.toast_server_not_responding), R.drawable.alert_error, Toast.LENGTH_LONG);
				}else{

			Log.i("--response in paybypaypal asynch", res);
			//customToast.showToast(res, 0, Toast.LENGTH_LONG);
			
			try {
				JSONObject data = new JSONObject(res).getJSONObject("data");
				boolean staus = data.getBoolean("status");
				String message = data.getString("message");
				
				if(staus){
					
					AlertDialog.Builder b = new AlertDialog.Builder(PaymentPaypalActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
					b.setTitle(getResources().getString(R.string.alert_dilog_success));
					b.setIcon(R.drawable.alert_ok);
					
					b.setMessage(message);
					
					
					b.setPositiveButton(getResources().getString(R.string.alert_dilog_ok), new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							database.deleteCart(); 
							customToast.showToast(getResources().getString(R.string.toast_deleted_from_cart), 0, 0);
//							int price = Integer.parseInt(thingToBuy.getDiscountedPrice());
//							int items = Integer.parseInt(thingToBuy.getQuantity());
							int coins =  new Double( DATA.totalInUs).intValue();
							
							new GetReward("purchase", coins+"", prefs.getString("userId", "0")).execute();
							
							
							
						}
								});
					
					b.show();
					
				}else {
					
					AlertDialog.Builder b = new AlertDialog.Builder(PaymentPaypalActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
					b.setTitle(getResources().getString(R.string.alert_dilog_error));
					b.setIcon(R.drawable.alert_error);
					
					b.setMessage(message);
					
					
					b.setPositiveButton(getResources().getString(R.string.alert_dilog_ok), new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							
							openActivity.open(MainActivity.class, true);
							
						}
								});
					
					b.show();
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				}//end if(res == null) else
			super.onPostExecute(res);
		}
	}//end inner assync class PayByPaypal 
//========================PayByPaypal================================================
	
	
	 //==============================GetReward==================================	
	class GetReward extends AsyncTask<Void, String, String> {
		String responseString = "";
		
		//String url = "http://savnpik.com/services/addCoins";
		String url = DATA.baseUrl+"addCoins";
		private String type, coins, userId;
		
		

		public GetReward(String type,String coins, String userId) {
			super();
			
			this.type = type;
			this.coins = coins;
			this.userId = userId;
		}

		@Override
		protected void onPreExecute() {
			
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				
				paramList.add(new BasicNameValuePair("type", type));
				paramList.add(new BasicNameValuePair("coins", coins));
				paramList.add(new BasicNameValuePair("user_id", userId));
				
				HttpPost httpPost = new HttpPost(url);
																
				httpPost.setEntity(new UrlEncodedFormEntity(paramList));
				responseString = EntityUtils.toString(new DefaultHttpClient()
						.execute(httpPost).getEntity());
				
				

			} catch (Exception e) { 
									

				e.printStackTrace();
			return null;
			}

			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			pd.dismiss();

			Log.i("response--string", result);
			
			if(result == null){
				customToast.showToast(getString(R.string.toast_internal_servser_error), 0, 0);
			}

			try {
				JSONObject data = new JSONObject(result).getJSONObject("data");
				
				boolean status = data.getBoolean("status");
				String message = data.getString("message");
				//Log.i("========", message);

				if (!message.equals("Already inserted")) {
				 customToast.showToast("Congratulations! you got "+coins+" coins", 0, 0);
				}else {

					customToast.showToast("You have already got reward", 0, 0);

				}

				openActivity.open(MainActivity.class, true);

			} catch (JSONException e) {
				 
				e.printStackTrace();
			}
			
		}

	}// end GetReward
//==============================GetReward==================================
*/


    public void donate(
            String donation_from,
            String other_name,
            String first_name,
            String last_name,
            String address,
            String city,
            String country,
            String zip,
            String patient_id,
            String num_of_patient_amount,
            String amount,
            String payment_method,
            String transaction_id) {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();
        params.put("donation_from", donation_from);
        params.put("other_name", other_name);
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("address", address);
        params.put("city", city);
        params.put("country", country);
        params.put("zip", zip);
        params.put("patient_id", prefs.getString("id", ""));
        params.put("num_of_patient_amount", num_of_patient_amount);
        params.put("amount", amount);
        params.put("payment_method", payment_method);

        params.put("transaction_id", transaction_id);

        DATA.print("-- params in saveDonation: "+params.toString());

        pd.show();
        client.post(DATA.baseUrl+"/saveDonation/"+patient_id,params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                pd.dismiss();
                try{
                    String content = new String(response);
                    DATA.print("-- responce in saveDonation: "+content);
                    customToast.showToast("Your donation has been sent successfully",0,1);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: saveDonation, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.dismiss();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--responce in failure saveDonation: "+content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
}

