package com.nftheater.api.constant;

public class ErrorCode {

    private ErrorCode() {
        throw new IllegalStateException("Don't initialize this class");
    }

    public static final String BAD_CREDENTIALS = "bad_credentials";
    public static final String BAD_REQUEST = "bad_request";
    public static final String DATA_NOT_FOUND = "data_not_found";
    public static final String DATA_DUPLICATE = "data_duplicate";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String INVALID_EMAIL_PATTERN = "invalid_email_pattern";
    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";


}
