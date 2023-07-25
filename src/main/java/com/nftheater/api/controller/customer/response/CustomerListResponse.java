package com.nftheater.api.controller.customer.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerListResponse {
    private String value;
    private String label;
    private String filterLabel;
}
