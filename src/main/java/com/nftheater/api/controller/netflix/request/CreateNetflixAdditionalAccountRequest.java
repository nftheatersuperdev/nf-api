package com.nftheater.api.controller.netflix.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateNetflixAdditionalAccountRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String createdBy;

}
