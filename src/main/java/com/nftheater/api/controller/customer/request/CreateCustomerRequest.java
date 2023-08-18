package com.nftheater.api.controller.customer.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCustomerRequest {

    private String customerName;
    private String email;
    private String phoneNumber;
    @NotBlank
    private String lineId;
    @NotBlank
    private String lineUrl;
    @NotBlank
    private String account;
    private UUID createdBy;
}
