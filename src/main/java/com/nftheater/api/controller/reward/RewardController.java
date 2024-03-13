package com.nftheater.api.controller.reward;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.reward.request.CreateRewardRequest;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.service.AdminUserService;
import com.nftheater.api.service.RewardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final AdminUserService adminUserService;

    @GetMapping("/v1/rewards")
    public GeneralResponse<List<RewardDto>> getAllReward() {
        log.info("Start Get all reward");
        List<RewardDto> response = rewardService.getAllActiveReward();
        log.info("End Get all reward size = {}", response.size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PostMapping("/v1/reward")
    public GeneralResponse<UUID> createReward(
            @RequestBody CreateRewardRequest createRewardRequest,
            HttpServletRequest httpServletRequest
    ) throws DataNotFoundException, FirebaseAuthException {
        log.info("Start create reward");
        String bearerToken = httpServletRequest.getHeader("Authorization");
        UUID adminId = adminUserService.getAdminUserByFirebaseToken(bearerToken.substring(7, bearerToken.length())).getId();
        UUID rewardId = rewardService.createReward(createRewardRequest, adminId);
        log.info("End create reward");
        return new GeneralResponse<>(SUCCESS, rewardId);
    }
}
