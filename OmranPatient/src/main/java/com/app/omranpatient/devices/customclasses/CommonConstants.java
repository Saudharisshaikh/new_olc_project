package com.app.omranpatient.devices.customclasses;

import com.app.omranpatient.util.DATA;

/**
 * Created by aftab on 06/08/2016.
 */

public class CommonConstants {

    //public static final String onlineCareMainUrl = "https://onlinecare.com/staging/surescript/index.php/medical_devices/";
	public static final String onlineCareMainUrl = DATA.baseUrl;

    public static final String withingsCustomerKey = "e95cef0474928622cb7c9c2cbdcb0529602307c2fd3bf31bb0bd1b21";
    public static final String withingsCustomerSecret = "90456bd51d22565e665076678c7032f9b4cdf306b16ed57b636208f617bf";

    public static final String withingsCustomerOauthCallbackUrl = "https://onlinecare.com/dev/withingsRedirect.php";

    public static final String withingsCustomerRequestTokenURL = "https://oauth.withings.com/account/request_token";
    public static final String withingsUserAuthorizeUrl = "https://oauth.withings.com/account/authorize";
    public static final String withingsUserAccessTokenUrl = "https://oauth.withings.com/account/access_token";
    public static final String withingsGetMeasuresUrl = "http://wbsapi.withings.net/measure?action=getmeas";


    public static final String DEVICE_DATA = "deviceData";
    public static final String WITHINGS_BP_DATA = "withingsBpData";

}

