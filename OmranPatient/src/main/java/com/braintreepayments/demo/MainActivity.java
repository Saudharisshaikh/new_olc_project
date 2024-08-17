package com.braintreepayments.demo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.app.OnlineCareUS_Pt.util.SharedPrefsHelper.PKG_AMOUNT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.api.ApiCallBack;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.api.CustomSnakeBar;
import com.app.OnlineCareUS_Pt.paypal.PaymentLiveCare;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.SharedPrefsHelper;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.ClientToken;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;
import com.braintreepayments.api.models.ThreeDSecureRequest;
import com.braintreepayments.api.models.VenmoAccountNonce;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements PaymentMethodNonceCreatedListener,
        BraintreeCancelListener, BraintreeErrorListener, DropInResult.DropInResultListener, ApiCallBack {

    private static final int DROP_IN_REQUEST = 100;

    private static final String KEY_NONCE = "nonce";

    private PaymentMethodType mPaymentMethodType;
    private PaymentMethodNonce mNonce;

    private CardView mPaymentMethod;
    private ImageView mPaymentMethodIcon;
    private TextView mPaymentMethodTitle;
    private TextView mPaymentMethodDescription;
    private TextView mNonceString;
    private TextView mNonceDetails;
    private TextView mDeviceData;

    private Button mAddPaymentMethodButton;
    private Button mPurchaseButton;
    private ProgressDialog mLoading;

    private boolean mShouldMakePurchase = false;
    private boolean mPurchased = false;

    Activity activity;
    //SharedPreferences prefs;
    SharedPrefsHelper sharedPrefsHelper;
    TextView tvPaymentMethodLbl, tvPaymentInfoLbl, tvAmount;
    ImageView ivBack;
    CardView cvPaymentInfo;
    CustomSnakeBar customSnakeBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braintree_main_activity);


        mPaymentMethod = findViewById(R.id.payment_method);
        mPaymentMethodIcon = findViewById(R.id.payment_method_icon);
        mPaymentMethodTitle = findViewById(R.id.payment_method_title);
        mPaymentMethodDescription = findViewById(R.id.payment_method_description);
        mNonceString = findViewById(R.id.nonce);
        mNonceDetails = findViewById(R.id.nonce_details);
        mDeviceData = findViewById(R.id.device_data);

        mAddPaymentMethodButton = findViewById(R.id.add_payment_method);
        mPurchaseButton = findViewById(R.id.purchase);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_NONCE)) {
                mNonce = savedInstanceState.getParcelable(KEY_NONCE);
            }
        }


        //GM code starts
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        activity = this;
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);

        tvPaymentMethodLbl = findViewById(R.id.tvPaymentMethodLbl);
        tvPaymentInfoLbl = findViewById(R.id.tvPaymentInfoLbl);
        tvAmount = findViewById(R.id.tvAmount);
        cvPaymentInfo = findViewById(R.id.cvPaymentInfo);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });

        SharedPreferences sharedPreferences = Settings.getPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("tokenization_key", true);
        editor.apply();
        //getPreferences(context).getBoolean("tokenization_key", false);
//        Settings.setEnvironment(activity, 0);//0 = sandbox, 1 = production
//        performReset();

        //prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        String brain_auth_key = sharedPrefsHelper.get("brain_auth_key", "");
        DATA.print("-- braintree key from service : "+brain_auth_key);
        tvAmount.setText(sharedPrefsHelper.get(PKG_AMOUNT, "5"));
        if(TextUtils.isEmpty(brain_auth_key) || brain_auth_key.equalsIgnoreCase("null")){
            //use sir jamals live braintree key and production envirnment if dont recieve key from service
            Settings.setEnvironment(activity, Settings.PRODUCTION_ENVIRNMENT);
        }else {
            if(brain_auth_key.startsWith("sandbox")){
                Settings.SANDBOX_TOKENIZATION_KEY = brain_auth_key;
                Settings.setEnvironment(activity, Settings.SANDBOX_ENVIRNMENT);
            }else if(brain_auth_key.startsWith("production")){
                Settings.PRODUCTION_TOKENIZATION_KEY = brain_auth_key;
                Settings.setEnvironment(activity, Settings.PRODUCTION_ENVIRNMENT);
            }
        }
        //performReset();
        ////GM code ends
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPurchased) {
            mPurchased = false;
            clearNonce();

            try {
                DATA.print("-- Clinet token onresume main activity : "+mAuthorization);
                if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
                    DropInResult.fetchDropInResult(this, mAuthorization, this);
                } else {
                    mAddPaymentMethodButton.setVisibility(VISIBLE);
                }
            } catch (InvalidArgumentException e) {
                mAddPaymentMethodButton.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNonce != null) {
            outState.putParcelable(KEY_NONCE, mNonce);
        }
    }

    public void launchDropIn(View v) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(mAuthorization)
                .requestThreeDSecureVerification(Settings.isThreeDSecureEnabled(this))
                .collectDeviceData(Settings.shouldCollectDeviceData(this))
                .googlePaymentRequest(getGooglePaymentRequest())
                .maskCardNumber(true)
                .maskSecurityCode(true)
                .allowVaultCardOverride(Settings.isSaveCardCheckBoxVisible(this))
                .vaultCard(Settings.defaultVaultSetting(this))
                .vaultManager(Settings.isVaultManagerEnabled(this))
                .cardholderNameStatus(Settings.getCardholderNameStatus(this));
        if (Settings.isThreeDSecureEnabled(this)) {
            dropInRequest.threeDSecureRequest(demoThreeDSecureRequest());
        }

        startActivityForResult(dropInRequest.getIntent(this), DROP_IN_REQUEST);
    }

    private ThreeDSecureRequest demoThreeDSecureRequest() {
        ThreeDSecurePostalAddress billingAddress = new ThreeDSecurePostalAddress()
                .givenName("Jill")
                .surname("Doe")
                .phoneNumber("5551234567")
                .streetAddress("555 Smith St")
                .extendedAddress("#2")
                .locality("Chicago")
                .region("IL")
                .postalCode("12345")
                .countryCodeAlpha2("US");

        ThreeDSecureAdditionalInformation additionalInformation = new ThreeDSecureAdditionalInformation()
                .accountId("account-id");

        ThreeDSecureRequest threeDSecureRequest = new ThreeDSecureRequest()
                .amount("1.00")
                .versionRequested(Settings.getThreeDSecureVersion(this))
                .email("test@email.com")
                .mobilePhoneNumber("3125551234")
                .billingAddress(billingAddress)
                .additionalInformation(additionalInformation);

        return threeDSecureRequest;
    }

    public void purchase(View v) {
        /*Intent intent = new Intent(this, CreateTransactionActivity.class)
                .putExtra(CreateTransactionActivity.EXTRA_PAYMENT_METHOD_NONCE, mNonce);
        startActivity(intent);*/

        braintreePaymentProcess(mNonce.getNonce());

        mPurchased = true;
    }

    @Override
    public void onResult(DropInResult result) {
        if (result.getPaymentMethodType() == null) {
            mAddPaymentMethodButton.setVisibility(VISIBLE);
        } else {
            mAddPaymentMethodButton.setVisibility(GONE);

            mPaymentMethodType = result.getPaymentMethodType();

            mPaymentMethodIcon.setImageResource(result.getPaymentMethodType().getDrawable());
            if (result.getPaymentMethodNonce() != null) {
                displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
            }

            mPurchaseButton.setEnabled(true);
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        super.onPaymentMethodNonceCreated(paymentMethodNonce);

        displayResult(paymentMethodNonce, null);
        safelyCloseLoadingView();

        if (mShouldMakePurchase) {
            purchase(null);
        }
    }

    @Override
    public void onCancel(int requestCode) {
        super.onCancel(requestCode);

        safelyCloseLoadingView();

        mShouldMakePurchase = false;
    }

    @Override
    public void onError(Exception error) {
        super.onError(error);

        safelyCloseLoadingView();

        mShouldMakePurchase = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        safelyCloseLoadingView();

        if (resultCode == RESULT_OK) {
            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
            mPurchaseButton.setEnabled(true);
        } else if (resultCode != RESULT_CANCELED) {
            safelyCloseLoadingView();
            showDialog(((Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR))
                    .getMessage());
        }
    }

    @Override
    protected void reset() {
        mPurchaseButton.setEnabled(false);

        mAddPaymentMethodButton.setVisibility(GONE);

        clearNonce();
    }

    @Override
    protected void onAuthorizationFetched() {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);

            if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
                DropInResult.fetchDropInResult(this, mAuthorization, this);
            } else {
                mAddPaymentMethodButton.setVisibility(VISIBLE);
                //mAddPaymentMethodButton.performClick();
                /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DATA.print("-- code exicute after 1 second");
                            mAddPaymentMethodButton.performClick();
                        }catch (Exception e){e.printStackTrace();}
                    }
                }, 1000);*/
            }
        } catch (InvalidArgumentException e) {
            showDialog(e.getMessage());
        }
    }

    private void displayResult(PaymentMethodNonce paymentMethodNonce, String deviceData) {
        mNonce = paymentMethodNonce;
        mPaymentMethodType = PaymentMethodType.forType(mNonce);

        mPaymentMethodIcon.setImageResource(PaymentMethodType.forType(mNonce).getDrawable());
        mPaymentMethodTitle.setText(paymentMethodNonce.getTypeLabel());
        mPaymentMethodDescription.setText(paymentMethodNonce.getDescription());
        mPaymentMethod.setVisibility(VISIBLE);
        tvPaymentMethodLbl.setVisibility(VISIBLE);
        tvPaymentInfoLbl.setVisibility(VISIBLE);
        cvPaymentInfo.setVisibility(VISIBLE);

        mNonceString.setText(getString(R.string.nonce) + ": " + mNonce.getNonce());
        mNonceString.setVisibility(VISIBLE);

        String details = "";
        if (mNonce instanceof CardNonce) {
            CardNonce cardNonce = (CardNonce) mNonce;

            details = "Card Last Two: " + cardNonce.getLastTwo() + "\n";
            details += "3DS isLiabilityShifted: " + cardNonce.getThreeDSecureInfo().isLiabilityShifted() + "\n";
            details += "3DS isLiabilityShiftPossible: " + cardNonce.getThreeDSecureInfo().isLiabilityShiftPossible();
        } else if (mNonce instanceof PayPalAccountNonce) {
            PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) mNonce;

            details = "First name: " + paypalAccountNonce.getFirstName() + "\n";
            details += "Last name: " + paypalAccountNonce.getLastName() + "\n";
            details += "Email: " + paypalAccountNonce.getEmail() + "\n";
            details += "Phone: " + paypalAccountNonce.getPhone() + "\n";
            details += "Payer id: " + paypalAccountNonce.getPayerId() + "\n";
            details += "Client metadata id: " + paypalAccountNonce.getClientMetadataId() + "\n";
            details += "Billing address: " + formatAddress(paypalAccountNonce.getBillingAddress()) + "\n";
            details += "Shipping address: " + formatAddress(paypalAccountNonce.getShippingAddress());
        } else if (mNonce instanceof VenmoAccountNonce) {
            VenmoAccountNonce venmoAccountNonce = (VenmoAccountNonce) mNonce;

            details = "Username: " + venmoAccountNonce.getUsername();
        } else if (mNonce instanceof GooglePaymentCardNonce) {
            GooglePaymentCardNonce googlePaymentCardNonce = (GooglePaymentCardNonce) mNonce;

            details = "Underlying Card Last Two: " + googlePaymentCardNonce.getLastTwo() + "\n";
            details += "Email: " + googlePaymentCardNonce.getEmail() + "\n";
            details += "Billing address: " + formatAddress(googlePaymentCardNonce.getBillingAddress()) + "\n";
            details += "Shipping address: " + formatAddress(googlePaymentCardNonce.getShippingAddress());
        }

        mNonceDetails.setText(details);
        mNonceDetails.setVisibility(VISIBLE);

        mDeviceData.setText("Device Data: " + deviceData);
        mDeviceData.setVisibility(VISIBLE);

        mAddPaymentMethodButton.setVisibility(GONE);
        mPurchaseButton.setEnabled(true);
    }

    private void clearNonce() {
        mPaymentMethod.setVisibility(GONE);
        mNonceString.setVisibility(GONE);
        mNonceDetails.setVisibility(GONE);
        mDeviceData.setVisibility(GONE);
        mPurchaseButton.setEnabled(false);

        tvPaymentMethodLbl.setVisibility(GONE);
        tvPaymentInfoLbl.setVisibility(GONE);
        cvPaymentInfo.setVisibility(GONE);
    }

    private String formatAddress(PostalAddress address) {
        return address.getRecipientName() + " " + address.getStreetAddress() + " " +
            address.getExtendedAddress() + " " + address.getLocality() + " " + address.getRegion() +
                " " + address.getPostalCode() + " " + address.getCountryCodeAlpha2();
    }

    private String formatAddress(UserAddress address) {
        if(address == null) {
            return "null";
        }
        return address.getName() + " " + address.getAddress1() + " " + address.getAddress2() + " " +
                address.getAddress3() + " " + address.getAddress4() + " " + address.getAddress5() + " " +
                address.getLocality() + " " + address.getAdministrativeArea() + " " + address.getPostalCode() + " " +
                address.getSortingCode() + " " + address.getCountryCode();
    }

    private GooglePaymentRequest getGooglePaymentRequest() {
        return new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice("1.00")
                        .setCurrencyCode("USD")
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .build())
                .emailRequired(true);
    }

    private void safelyCloseLoadingView() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }





    //GM Code Starts
    public void braintreePaymentProcess(String nonce){

        RequestParams params = new RequestParams();
        params.put("nonce", nonce);
        params.put("amount", sharedPrefsHelper.get(PKG_AMOUNT, "5"));
        ApiManager apiManager = new ApiManager(ApiManager.BRAINTREE_PAYMENT_PROCESS, "post", params, this, activity);
        apiManager.loadURL();
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        //{"status":"success","id":"c0hm204s"}
        //{"status":"error","message":"91564: Cannot use a paymentMethodNonce more than once.\n"}
        // {"status":"error","message":"Processor Declined"}
        if(apiName.equalsIgnoreCase(ApiManager.BRAINTREE_PAYMENT_PROCESS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    PaymentLiveCare.transaction_id = jsonObject.getString("id");
                    PaymentLiveCare.isBraintreePaymentDone = true;
                    finish();
                }else {
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Payment Error")
                            .setMessage(jsonObject.optString("message", DATA.CMN_ERR_MSG))
                            .setPositiveButton("Done", null)
                            .create().show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
