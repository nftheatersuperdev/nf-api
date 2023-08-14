package com.nftheater.api.controller.customer.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchCustomerRequest {
    private String userId;
    private String email;
    private String lineId;
    private List<String> status = new ArrayList<>();
    private String account;
}
