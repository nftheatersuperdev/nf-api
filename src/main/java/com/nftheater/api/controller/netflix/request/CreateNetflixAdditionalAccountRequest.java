package com.nftheater.api.controller.netflix.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNetflixAdditionalAccountRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String createdBy;

}
