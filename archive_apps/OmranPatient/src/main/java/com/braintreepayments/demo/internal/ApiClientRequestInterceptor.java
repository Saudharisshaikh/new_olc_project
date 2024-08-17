package com.braintreepayments.demo.internal;

import com.braintreepayments.demo.Settings;

import retrofit.RequestInterceptor;

public class ApiClientRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "braintree/android-demo-app/" + Settings.VERSION_NAME);
        request.addHeader("Accept", "application/json");
    }
}
