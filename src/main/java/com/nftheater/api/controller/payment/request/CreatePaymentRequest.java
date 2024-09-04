package com.nftheater.api.controller.payment.request;

import com.nftheater.api.constant.PaymentType;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePaymentRequest {

    private PaymentType paymentType;
    private UUID packageId;
    private String userEmail;
    private String userTelNo;

}
