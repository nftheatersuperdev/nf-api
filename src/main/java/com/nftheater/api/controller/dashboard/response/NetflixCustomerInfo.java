package com.nftheater.api.controller.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetflixCustomerInfo {
    private int countWaitingExpired;
    private int countWaitingAsk2Status;
    private int countWaitingAsk1Status;
    private int countWaitingAskStatus;
    private int totalCustomer;
    private int totalActiveCustomer;
}
