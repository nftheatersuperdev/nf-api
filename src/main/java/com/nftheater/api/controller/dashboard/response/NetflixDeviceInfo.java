package com.nftheater.api.controller.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetflixDeviceInfo {
    private int availableTV;
    private int totalTV;
    private int availableAdditional;
    private int totalAdditional;
    private int availableOther;
    private int totalOther;
}
