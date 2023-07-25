package com.nftheater.api.controller.systemconfig.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateSystemConfigRequest {

    private String configName;
    private String configValue;
    private String updatedBy;

}
