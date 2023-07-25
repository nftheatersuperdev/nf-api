package com.nftheater.api.controller.netflix.response;

import com.nftheater.api.controller.customer.response.CustomerResponse;
import lombok.Data;

@Data
public class NetflixAdditionalAccountResponse {
    private String email;
    private String password;
    private CustomerResponse user;

}
