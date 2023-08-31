package com.nftheater.api.controller.dashboard.response.netflix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetflixDashboardResponse {
    private NetflixChangeDateInfo changeDateInfo;
    private NetflixCustomerInfo customerInfo;
    private NetflixDeviceInfo deviceInfo;
}
