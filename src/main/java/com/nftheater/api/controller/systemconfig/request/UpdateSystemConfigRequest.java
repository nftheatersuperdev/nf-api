package com.nftheater.api.controller.systemconfig.request;

import lombok.Data;

@Data
public class UpdateSystemConfigRequest {

    private String configName;
    private String configValue;
    private String updatedBy;

}
