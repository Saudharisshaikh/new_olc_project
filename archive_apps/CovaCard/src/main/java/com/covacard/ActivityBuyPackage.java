package com.covacard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.braintreepayments.cardform.view.CardForm;
import com.loopj.android.http.RequestParams;
import com.covacard.R;
import com.covacard.api.ApiManager;
import com.covacard.util.DATA;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityBuyPackage extends ActivityBaseDrawer {

    CardForm cardForm;
    //TextView tvPackageTitle,tvExpiry,tvPkgPrice;
    TextView tvPkgTitle,tvPkgDes,tvPkgPrice,tvPlanTitle,tvPkgType,tvPkgMode;
    Button btnBuyNow;


    /*ye sir onlinecare ka account hai demo stripe
    Publishable key
    pk_test_eoLsWE9jXisnnOViyG5COPTi00YrcRk4F8
    Secret key
    sk_test_8z8m330X6ygU20yE9HPbgOBy00u21T6imf*/
    private String publishableKey = "pk_test_eoLsWE9jXisnnOViyG5COPTi00YrcRk4F8";//Sir jamal onlinecare demo


    //Google Play Billing Library
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;
    //Google Play Billing Library

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_buy_package);
        getLayoutInflater().inflate(R.layout.activity_buy_package, container_frame);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name)+" Subscription");
            getSupportActionBar().hide();
        }

        tvToolbarTitle.setText(getResources().getString(R.string.app_name)+" Subscription");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/

        btnBuyNow = findViewById(R.id.btnBuyNow);
        /*tvPackageTitle = findViewById(R.id.tvPackageTitle);
        tvExpiry = findViewById(R.id.tvExpiry);
        tvPkgPrice = findViewById(R.id.tvPkgPrice);*/

        tvPkgTitle = findViewById(R.id.tvPkgTitle);
        tvPkgDes = findViewById(R.id.tvPkgDes);
        tvPkgPrice = findViewById(R.id.tvPkgPrice);
        tvPlanTitle = findViewById(R.id.tvPlanTitle);
        tvPkgType = findViewById(R.id.tvPkgType);
        tvPkgMode = findViewById(R.id.tvPkgMode);


        //cardForm =  findViewById(R.id.card_form);
        /*cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .setup(appCompatActivity);*/

        /*cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                //.postalCodeRequired(true)
                //.mobileNumberRequired(true)
                // .mobileNumberExplanation("SMS is required on this number")
                .actionLabel(getResources().getString(R.string.app_name)+" Subscription")
                .setup(appCompatActivity);*/


        /*tvPackageTitle.setText(ActivityPackages.selectedPackageBean.package_name);
        tvExpiry.setText(ActivityPackages.selectedPackageBean.duration_month+ " Month(s)");
        tvPkgPrice.setText(ActivityPackages.selectedPackageBean.amount);*/

        tvPkgTitle.setText(ActivityPackages.selectedPackageBean.package_name);
        tvPkgPrice.setText("US$ "+ActivityPackages.selectedPackageBean.amount);
        tvPlanTitle.setText(ActivityPackages.selectedPackageBean.package_name+" Plan");
        tvPkgType.setText(ActivityPackages.selectedPackageBean.pkg_type);
        tvPkgMode.setText(ActivityPackages.selectedPackageBean.pkg_mode);
        String desc = "This subscription package will renew after "+ActivityPackages.selectedPackageBean.duration_month+" month(s)";
        tvPkgDes.setText(desc);



        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //buyWithStripe();

                if(skuDetailsCurrentPkg != null){
                    launchPurchaseFlow(skuDetailsCurrentPkg);
                }else {
                    initPlayBillingLibrary();
                }
            }
        });



        //Google Play Billing Library
        initPlayBillingLibrary();
        //Google Play Billing Library


        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }

    //Google Play Billing Library
    private void initPlayBillingLibrary(){
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && purchases != null) {
                    for (Purchase purchase : purchases) {
                        //handlePurchase(purchase);
                        if(sharedPrefsHelper.get("debug_logs", false)){
                            System.out.println("-- Purchase successfull "+purchase.toString());
                            System.out.println("-- getPurchaseToken: "+purchase.getPurchaseToken());
                            System.out.println("-- getDeveloperPayload: "+purchase.getDeveloperPayload());
                            System.out.println("-- getPurchaseTime: "+purchase.getPurchaseTime());
                            System.out.println("-- getPurchaseState: "+purchase.getPurchaseState());
                            System.out.println("-- getOrderId: "+purchase.getOrderId());
                            System.out.println("-- getOriginalJson: "+purchase.getOriginalJson());
                            System.out.println("-- getPackageName: "+purchase.getPackageName());
                            System.out.println("-- getAccountIdentifiers: "+purchase.getAccountIdentifiers());
                            System.out.println("-- getQuantity: "+purchase.getQuantity());
                            System.out.println("-- getSignature: "+purchase.getSignature());
                            System.out.println("-- getSkus: "+purchase.getSkus());
                            System.out.println("-- isAcknowledged: "+purchase.isAcknowledged());
                        }

                        inAppPay(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- Purchase user cancelled - code: "+billingResult.getResponseCode()+ "  | Message: "+billingResult.getDebugMessage());
                    }
                } else {
                    // Handle any other error codes.
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- in ELSE - Code: "+billingResult.getResponseCode()+ "  | Message: "+billingResult.getDebugMessage());
                    }
                }
            }
        };

        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if(sharedPrefsHelper.get("debug_logs", false)){
                    System.out.println("-- BillingClientStateListener - onBillingSetupFinished() , billingResult : "+billingResult.getResponseCode() + " | "+billingResult.getDebugMessage());
                }

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    //Dummy product IDs for static responces
                    List<String> skuList = new ArrayList<>();
                    if(sharedPrefsHelper.get("dummy_subscription", false)){
                        skuList.add("android.test.purchased");
                    }else {
                        skuList.add(ActivityPackages.selectedPackageBean.android_pkg_id);
                    }
                    //skuList.add(ActivityPackages.selectedPackageBean.appstore_pkg_id.toLowerCase());
                    //skuList.add("android.test.purchased");

                    /*skuList.add("android.test.purchased");
                    skuList.add("android.test.canceled");
                    skuList.add("android.test.refunded");
                    skuList.add("android.test.item_unavailable");*/
                    /*skuList.add("premium_upgrade");
                     skuList.add("gas");*/

                    //skuList.add("com.onlinecare.covacard_self_monthly");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    String type = sharedPrefsHelper.get("dummy_subscription", false) ? BillingClient.SkuType.INAPP : BillingClient.SkuType.SUBS;
                    params.setSkusList(skuList).setType(type);//BillingClient.SkuType.INAPP
                    billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                            // Process the result.
                            if(sharedPrefsHelper.get("debug_logs", false)){
                                System.out.println("-- onSkuDetailsResponse in SkuDetailsResponseListener, billingResult responce code | message : "+billingResult.getResponseCode()+" | "+billingResult.getDebugMessage()+" skuDetailsList: "+skuDetailsList);
                            }
                            //ArrayList<String> itemsList = new ArrayList<>();

                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && skuDetailsList != null) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    //handlePurchase(purchase);
                                    if(sharedPrefsHelper.get("debug_logs", false)){
                                        System.out.println("-- onSkuDetailsResponse "+skuDetails.toString());
                                    }
                                    //itemsList.add(skuDetails.toString());

                                    skuDetailsCurrentPkg = skuDetails;

                                    launchPurchaseFlow(skuDetailsCurrentPkg);
                                }

                                /*
                                System.out.println("-- hello");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, itemsList);
                                        lvDummyProducts.setAdapter(stringArrayAdapter);

                                        lvDummyProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                launchPurchaseFlow(skuDetailsList.get(position));
                                            }
                                        });
                                        System.out.println("-- adapter "+stringArrayAdapter+ " "+stringArrayAdapter.getCount());
                                    }
                                });*/
                            }
                        }
                    });

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                System.out.println("-- BillingClientStateListener - onBillingServiceDisconnected()");
            }
        });
    }

    SkuDetails skuDetailsCurrentPkg = null;
    private void launchPurchaseFlow(SkuDetails skuDetails) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        if(sharedPrefsHelper.get("debug_logs", false)){
            System.out.println("-- Billing Responce Code : "+responseCode);
        }
        // Handle the result.
    }

    //Google Play Billing Library


    private void inAppPay(Purchase purchase){

        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", ""));
        params.put("package_id", ActivityPackages.selectedPackageBean.id);
        params.put("payment_from", "android");
        params.put("subscription_id", purchase.getPurchaseToken());

        ApiManager apiManager = new ApiManager(ApiManager.INAPP_PAY, "post", params, apiCallBack, activity);
        apiManager.loadURL();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        //stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }



    private void buyWithStripe(){

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

        if(!validated){
            customToast.showToast("Please enter a valid card information" , 0,0);
            return;
        }

                /*Card card = Card.create(cardNo,
                        Integer.parseInt(cardExpMonth),
                        Integer.parseInt(cardExpYear),
                        cardCVV);//new Card("4242424242424242", Integer.parseInt("2"), Integer.parseInt("22"), 123);//4242-4242-4242-4242
                payWithStripe(card);*/


        String confrmMsg = "Are you sure ? Do you want to buy "+ActivityPackages.selectedPackageBean.package_name+" package for US$ "+ActivityPackages.selectedPackageBean.amount + " ?";
        androidx.appcompat.app.AlertDialog alertDialog =
                new androidx.appcompat.app.AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                        .setTitle("Confirm")
                        .setMessage(confrmMsg)
                        .setPositiveButton("Yes Buy", (dialog, which) -> {
                            try {
                                CardParams cardParams = new CardParams(cardNo, Integer.parseInt(cardExpMonth) , Integer.parseInt(cardExpYear), cardCVV, cardHoldername);
                                payWithStripe(cardParams);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton("Not Now",null)
                        .create();
        alertDialog.show();


    }


    public static String stripeCardToken = "";
    public void payWithStripe(CardParams cardParams){
        //dialog_customProgress.showProgressDialog();
        Stripe stripe = new Stripe(activity, sharedPrefsHelper.get("covacard_stripe_key", publishableKey));//sharedPrefsHelper.get("stripe_key",STRIPE_PUBLISHABLE_KEY_LIVE)
        stripe.createCardToken(cardParams, new ApiResultCallback<Token>() {
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

                stripeCardToken = token.getId();

                //sharedPrefsHelper.save(SharedPrefsHelper.STRIPE_TOKEN,token.getId());

                // ActivityMyEventsDetail.isStripPaymentDoneForStream = true;

                /*AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name)+" \nWould you like to save your card details?")
                        .setMessage("Save your card details so next time you won't have to enter your card details")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPrefsHelper.save(SharedPrefsHelper.SAVE_CARD_DETAILS,true);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                alertDialog.show();*/
//                finish();
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
        /*stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        dialog_customProgress.dismissProgressDialog();
                        // Send token to your server
                        System.out.println("-- Token created: "+token.toString()
                                +"\ntiken id: "+token.getId()
                                +"\ntiken type: "+token.getType()
                                +"\ntiken bank account: "+token.getBankAccount()
                                +"\ntiken card: "+token.getCard()
                                +"\ntiken created date: "+token.getCreated()
                                +"\ntiken live mode: "+token.getLivemode()
                                +"\ntiken used: "+token.getUsed()
                                +"\ntiken id: "+token.getId());
                    }
                    public void onError(Exception error) {
                        dialog_customProgress.dismissProgressDialog();
                        // Show localized error message
                        error.printStackTrace();

                        customToast.showToast(error.getLocalizedMessage(),0,0);
                    }
                }
        );*/
    }


    public void stripeCharge(String payment_token){
        RequestParams params = new RequestParams();

        params.put("token", payment_token);
        params.put("patient_id", prefs.getString("id", ""));
        params.put("package_id", ActivityPackages.selectedPackageBean.id);

        ApiManager apiManager = new ApiManager(ApiManager.STRIPE_CHARGE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        //{"status":"success","message":"Your payment has been completed"}{"status":"error","message":""}
        if(apiName.equalsIgnoreCase(ApiManager.STRIPE_CHARGE) || apiName.equalsIgnoreCase(ApiManager.INAPP_PAY)){
            //{"status":"error","msg":"You cannot use a Stripe token more than once: tok_1GP6NQGmbUakGYxG4JzZK0kf."}
            /*{
                "status": "success",
                    "msg": "",
                    "data": {
                "card_details": [
                {
                    "id": "card_1GP7adGmbUakGYxGvJXiq5T8",
                        "object": "card",
                        "address_city": null,
                        "address_country": null,
                        "address_line1": null,
                        "address_line1_check": null,
                        "address_line2": null,
                        "address_state": null,
                        "address_zip": null,
                        "address_zip_check": null,
                        "brand": "Visa",
                        "country": "US",
                        "customer": "cus_Gx0Od8EWzfwmVn",
                        "cvc_check": "pass",
                        "dynamic_last4": null,
                        "exp_month": 5,
                        "exp_year": 2021,
                        "fingerprint": "OTvqNGvRIjsulOpi",
                        "funding": "credit",
                        "last4": "4242",
                        "metadata": [],
                    "name": null,
                        "tokenization_method": null
                }
        ],
                "stripe_customer_id": "cus_Gx0Od8EWzfwmVn"
            }
            }*/
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                //{"status":"success","message":"Your payment has been completed","transaction_id":null}
                if(status.equalsIgnoreCase("success")){
                    AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle("Payment Successful")
                            .setMessage(jsonObject.optString("message"))
                            .setPositiveButton("Done", null)
                            .create();
                    alertDialog.setOnDismissListener(dialog -> {
                        ActivityPackages.isPaymentDone = true;
                        finish();
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}


