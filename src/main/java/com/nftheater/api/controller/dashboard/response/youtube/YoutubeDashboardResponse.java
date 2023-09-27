package com.nftheater.api.controller.dashboard.response.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeDashboardResponse {
    private YoutubeChangeDateInfo changeDateInfo;
    private YoutubeCustomerInfo customerInfo;
    private Integer todayTransaction;
}
