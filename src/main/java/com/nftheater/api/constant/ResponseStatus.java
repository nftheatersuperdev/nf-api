package com.nftheater.api.constant;

public class ResponseStatus {

    private ResponseStatus() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
}
