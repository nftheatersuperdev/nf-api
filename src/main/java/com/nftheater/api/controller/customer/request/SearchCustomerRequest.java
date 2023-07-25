package com.nftheater.api.controller.customer.request;

import lombok.Data;

@Data
public class SearchCustomerRequest {
    private String userId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String lineId;
}
