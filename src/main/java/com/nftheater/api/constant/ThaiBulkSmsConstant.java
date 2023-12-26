package com.nftheater.api.constant;

public class ThaiBulkSmsConstant {


    private ThaiBulkSmsConstant() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static final String APP_SECRET = "secret";
    public static final String APP_KEY = "key";
    public static final String REQUEST_TOKEN = "token";
    public static final String PIN_CODE= "pin";
    public static final String MSI_SDN = "msisdn";

    public static final String PATH_VERIFY = "/verify";
    public static final String PATH_REQUEST = "/request";

}
