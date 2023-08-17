package com.nftheater.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class YoutubePackageDto {
    private UUID id;
    private String name;
    private int day;
    private int price;
    private String type;
}
