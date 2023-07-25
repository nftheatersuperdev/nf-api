package com.nftheater.api.exception;

import com.nftheater.api.constant.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DataNotFoundException extends BaseCheckedException {
    public DataNotFoundException() {
        super(ErrorCode.DATA_NOT_FOUND, null, HttpStatus.NOT_FOUND);
    }

    public DataNotFoundException(String message) {
        super(ErrorCode.DATA_NOT_FOUND, message, HttpStatus.NOT_FOUND);
    }

    public DataNotFoundException(String code, String message) {
        super(code, message, HttpStatus.NOT_FOUND);
    }
}
