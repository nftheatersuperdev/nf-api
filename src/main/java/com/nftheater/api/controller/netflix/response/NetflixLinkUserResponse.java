package com.nftheater.api.controller.netflix.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nftheater.api.constant.NetflixAccountType;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class NetflixLinkUserResponse {
    private NetflixAccountType accountType;
    private CustomerResponse user;
    private ZonedDateTime addedDate;
    private String addedBy;
    private String accountStatus;
    private String color;
    @JsonIgnore
    private int sort;
}
