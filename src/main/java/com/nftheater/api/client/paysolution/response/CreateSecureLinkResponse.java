package com.nftheater.api.client.paysolution.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateSecureLinkResponse {

    @JsonProperty("merchant")
    private String merchant;
    @JsonProperty("payValue")
    private String payValue;
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
    @JsonProperty("remark")
    private String remark;
    @JsonProperty("monthInstallment")
    private String monthInstallment;
    @JsonProperty("bankInstallment")
    private String bankInstallment;
    @JsonProperty("oneTime")
    private String oneTime;
    @JsonProperty("refNo")
    private String refNo;
}
