package com.nftheater.api.controller.customer.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckUrlRequest {
    @NotNull
    @NotEmpty
    private String url;
}
