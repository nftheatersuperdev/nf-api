package com.nftheater.api.controller.reward.request;

import lombok.Data;

@Data
public class UpdateRewardRequest {
    private String rewardName;
    private String rewardValue;
    private Integer redeemPoint;
    private Boolean isActive;
}
