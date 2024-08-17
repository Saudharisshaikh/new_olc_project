package com.app.priorityone_uc.paypal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.priorityone_uc.BaseActivity;
import com.app.priorityone_uc.R;
import com.app.priorityone_uc.api.ApiManager;
import com.app.priorityone_uc.util.DATA;
import com.braintreepayments.cardform.view.CardForm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ActivityCreditCardStripe extends BaseActivity {

    CardForm cardForm;
    ImageView img_back;
    Button btn_addCard,btn_scanCard;
            //,payButton;


    /*ye sir onlinecare ka account hai demo stripe
    Publishable key
    pk_test_eoLsWE9jXisnnOViyG5COPTi00YrcRk4F8
    Secret key
    sk_test_8z8m330X6ygU20yE9HPbgOBy00u21T6imf*/
    private String publishableKey = "pk_test_eoLsWE9jXisnnOViyG5COPTi00YrcRk4F8";//Sir jamal onlinecare demo

    //private String publishableKey = "pk_test_HS1tAHRwFX0mqH9Oan2jFwWd003jpDukuM";//mustafa@gexton.com
//    private String publishableKey = "pk_test_feMthKa2uLqJraWrVFIDaR9j";//Junaid Amin
    //private String paymentIntentClientSecret = "sk_test_QLga8M64wePK4WkMexSzhjdf002sDbhOpI";
    //private Stripe stripe;
    //CardInputWidget cardInputWidget;


    //===========Strip key of Client ================
    //    Securety key: sk_live_jQxRCw2ae96Bxm0FyPCi71BE00mnFmiQD6
    //    Public key: pk_live_bKzewp1Iu98KS2KREYx8oUXM00EvLFnfmK

    //private String publishableKey = "pk_live_bKzewp1Iu98KS2KREYx8oUXM00EvLFnfmK";//Client Tony

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stripe_creditcard);

        btn_addCard = findViewById(R.id.btn_addCard);
        btn_scanCard = findViewById(R.id.btn_scanCard);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardForm =  findViewById(R.id.card_form);
        /*cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .setup(appCompatActivity);*/
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                //.postalCodeRequired(true)
                //.mobileNumberRequired(true)
               // .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase live stream")
                .setup(appCompatActivity);


        btn_scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardForm.isCardScanningAvailable()){
                    cardForm.scanCard(appCompatActivity);
                }else {
                    new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle("Info")
                            .setMessage("Scan function is not available on the device")
                            .setPositiveButton("Got it",null)
                            //.setNegativeButton("Cancel",null)
                            .create().show();
                }

            }
        });

        //PaymentConfiguration.init();
        //stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());



        btn_addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


        //payButton = findViewById(R.id.payButton);
        //cardInputWidget = findViewById(R.id.cardInputWidget);
        /*PaymentConfiguration.init(getApplicationContext(), publishableKey);
        System.out.println("-- pulishable key : "+PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    //final Context context = getApplicationContext();
                    //stripe = new Stripe(context, PaymentConfiguration.getInstance(context).getPublishableKey());
                    stripe.confirmPayment(activity, confirmParams);
                }
            }
        });*/








        /*Button btn_StripeGM = findViewById(R.id.btn_StripeGM);
        btn_StripeGM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //=======================================================================
                //System.out.println("cardno: "+card_number+" exp month: "+ Integer.parseInt(Expiremonth)+" exp year: "+ Integer.parseInt(Expireyear)+" cvv: "+ Cvv2);
                Card card = Card.create("4242424242424242", Integer.parseInt("2"), Integer.parseInt("22"), "123");//new Card("4242424242424242", Integer.parseInt("2"), Integer.parseInt("22"), 123);//4242-4242-4242-4242
                //Card card = new Card("4242424242424242", 12, 2018, "123");//4242-4242-4242-4242
                //card.setName(sharedPrefsHelper.getUser().Studio_Name);
                //card.setAddressZip(sharedPrefsHelper.getUser().zipcode);
                if (!card.validateCard()) {
                    // Show errors
                    System.out.println("--card not validated");
                    //customToast.showToast(getResources().getString(R.string.str288),0,0);
                    return;
                }
                payWithStripe(card);
                //=======================================================================
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        //stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }


    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<ActivityCreditCardStripe> activityRef;

        PaymentResultCallback(@NonNull ActivityCreditCardStripe activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final ActivityCreditCardStripe activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Toast.makeText(activity,"Payment completed"+gson.toJson(paymentIntent),Toast.LENGTH_LONG).show();
                System.out.println("--Payment completed"+gson.toJson(paymentIntent));

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                //Toast.makeText(activity,"Payment failed"+Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(),Toast.LENGTH_LONG).show();
                //System.out.println("--Payment failed"+Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final ActivityCreditCardStripe activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method

            Toast.makeText(activity,"Error"+e.toString(),Toast.LENGTH_LONG).show();
            System.out.println("--Error"+e.toString());

        }
    }





    public static String stripeCardToken = "";
    public void payWithStripe(Card card){
        //dialog_customProgress.showProgressDialog();
        Stripe stripe = new Stripe(activity, publishableKey);//sharedPrefsHelper.get("stripe_key",STRIPE_PUBLISHABLE_KEY_LIVE)
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
        params.put("amount", "80.00");

        ApiManager apiManager = new ApiManager(ApiManager.STRIPE_CHARGE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        //{"status":"success","message":"Your payment has been completed"}{"status":"error","message":""}
        if(apiName.equalsIgnoreCase(ApiManager.STRIPE_CHARGE)){
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
                if(status.equalsIgnoreCase("success")){

                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}


