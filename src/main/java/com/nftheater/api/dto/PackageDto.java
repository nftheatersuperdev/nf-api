package com.nftheater.api.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class PackageDto {
    private UUID id;
    private String module;
    private String name;
    private int day;
    private int price;
    private String device;
    private String type;
    private Boolean isActive;
    private ZonedDateTime updatedDate;
    private String updatedBy;
}
