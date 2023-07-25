package com.nftheater.api.controller.customer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerResponse {

    private UUID id;
    private String userId;
    private String password;
}
