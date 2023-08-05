package com.nftheater.api.controller.netflix.response;

import com.nftheater.api.controller.customer.response.CustomerResponse;
import lombok.Data;

import java.util.UUID;

@Data
public class NetflixAdditionalAccountResponse {
    private UUID additionalId;
    private String email;
    private String password;
    private CustomerResponse user;
    private String accountStatus;

}
