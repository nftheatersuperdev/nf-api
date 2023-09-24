package com.nftheater.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RewardDto {
    private UUID id;
    private String rewardName;
    private String rewardValue;
    private Integer redeemPoint;
    private Boolean isActive;
}
