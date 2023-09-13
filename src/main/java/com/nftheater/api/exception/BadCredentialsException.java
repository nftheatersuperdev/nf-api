package com.nftheater.api.exception;

import com.nftheater.api.constant.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadCredentialsException extends BaseCheckedException {

    public BadCredentialsException() { super(ErrorCode.BAD_CREDENTIALS, null, HttpStatus.UNAUTHORIZED); }

    public BadCredentialsException(String message) {
        super(ErrorCode.BAD_CREDENTIALS, message, HttpStatus.UNAUTHORIZED);
    }

    public BadCredentialsException(String code, String message) {
        super(code, message, HttpStatus.UNAUTHORIZED);
    }
}
