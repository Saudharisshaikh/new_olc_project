package com.covacard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.covacard.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlayBilling extends BaseActivity {


    //Button btnSubscribeNow;
    ListView lvDummyProducts;

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aa_test);

        lvDummyProducts = findViewById(R.id.lvDummyProducts);


        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && purchases != null) {
                    for (Purchase purchase : purchases) {
                        //handlePurchase(purchase);
                        System.out.println("-- Purchase successfull "+purchase.toString());
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    System.out.println("-- Purchase user cancelled - code: "+billingResult.getResponseCode()+ "  | Message: "+billingResult.getDebugMessage());
                } else {
                    // Handle any other error codes.
                    System.out.println("-- in ELSE - Code: "+billingResult.getResponseCode()+ "  | Message: "+billingResult.getDebugMessage());
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

                System.out.println("-- BillingClientStateListener - onBillingSetupFinished() , billingResult : "+billingResult.getResponseCode() + " | "+billingResult.getDebugMessage());

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    //Dummy product IDs for static responces
                    List<String> skuList = new ArrayList<>();
                    skuList.add("android.test.purchased");
                    skuList.add("android.test.canceled");
                    skuList.add("android.test.refunded");
                    skuList.add("android.test.item_unavailable");
                    /*skuList.add("premium_upgrade");
                     skuList.add("gas");*/

                    //skuList.add("com.onlinecare.covacard_self_monthly");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);//BillingClient.SkuType.INAPP
                    billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                            // Process the result.

                            System.out.println("-- onSkuDetailsResponse in SkuDetailsResponseListener, billingResult responce code | message : "+billingResult.getResponseCode()+" | "+billingResult.getDebugMessage()+" skuDetailsList: "+skuDetailsList);

                            ArrayList<String> itemsList = new ArrayList<>();

                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK  && skuDetailsList != null) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    //handlePurchase(purchase);
                                    System.out.println("-- onSkuDetailsResponse "+skuDetails.toString());
                                    itemsList.add(skuDetails.toString());
                                }
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
                                });
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


    private void launchPurchaseFlow(SkuDetails skuDetails) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
        System.out.println("-- Billing Responce Code : "+responseCode);
        // Handle the result.
    }
}
