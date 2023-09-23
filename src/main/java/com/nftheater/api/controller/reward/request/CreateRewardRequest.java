package com.nftheater.api.controller.reward.request;

import lombok.Data;

@Data
public class CreateRewardRequest {
    private String rewardName;
    private String rewardValue;
    private Integer redeemPoint;
}
