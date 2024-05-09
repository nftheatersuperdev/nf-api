package com.nftheater.api.controller.packages.request;

import lombok.Data;

@Data
public class UpdatePackageRequest {
    private String name;
    private int day;
    private int price;
    private Boolean isActive;
    private String updatedBy;
}
