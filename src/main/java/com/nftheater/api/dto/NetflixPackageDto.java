package com.nftheater.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NetflixPackageDto {
    private UUID id;
    private String name;
    private int day;
    private int price;
    private String device;
}
