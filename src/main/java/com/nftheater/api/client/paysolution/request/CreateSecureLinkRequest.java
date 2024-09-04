package com.nftheater.api.client.paysolution.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateSecureLinkRequest {

    @JsonProperty("merchant")
    private String merchant;
    @JsonProperty("payValue")
    private Integer payValue;
    @JsonProperty("orderDetail")
    private String orderDetail;
    @JsonProperty("expireDate")
    private String expireDate;
    @JsonProperty("userEMail")
    private String userEMail;
    @JsonProperty("userTelNo")
    private String userTelNo;
    @JsonProperty("postBackURL")
    private String postBackURL;
    @JsonProperty("returnURL")
    private String returnURL;
    @JsonProperty("monthInstallment")
    private String monthInstallment;
    @JsonProperty("bankInstallment")
    private String bankInstallment;
    @JsonProperty("oneTime")
    private String oneTime;
    @JsonProperty("refNo")
    private String refNo;
    
}
