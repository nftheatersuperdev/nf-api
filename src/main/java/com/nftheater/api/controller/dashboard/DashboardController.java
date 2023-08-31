package com.nftheater.api.controller.dashboard;

import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.dashboard.response.netflix.NetflixDashboardResponse;
import com.nftheater.api.controller.dashboard.response.youtube.YoutubeDashboardResponse;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Secured({Module.ALL, Module.NETFLIX})
    @GetMapping("/v1/dashboard/netflix")
    public GeneralResponse<NetflixDashboardResponse> getNetflixDashboard() {
        NetflixDashboardResponse dashboardResponse = dashboardService.getNetflixDashboardInfo();
        return new GeneralResponse<>(SUCCESS, dashboardResponse);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @GetMapping("/v1/dashboard/youtube")
    public GeneralResponse<YoutubeDashboardResponse> getYoutubeDashboard() {
        YoutubeDashboardResponse youtubeDashboardResponse = dashboardService.getYoutubeDashboardInfo();
        return new GeneralResponse<>(SUCCESS, youtubeDashboardResponse);
    }

}
