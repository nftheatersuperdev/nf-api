package com.nftheater.api.controller.member;

import com.nftheater.api.controller.member.request.*;
import com.nftheater.api.controller.member.response.AuthenticationResponse;
import com.nftheater.api.controller.member.response.CustomerProfileResponse;
import com.nftheater.api.controller.netflix.response.GetNetflixPackageResponse;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.youtube.response.GetYoutubePackageResponse;
import com.nftheater.api.dto.NetflixPackageDto;
import com.nftheater.api.dto.RewardDto;
import com.nftheater.api.exception.BadCredentialsException;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.*;
import com.nftheater.api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final NetflixService netflixService;
    private final YoutubeService youtubeService;
    private final RewardService rewardService;
    private final EventService eventService;
    private final JwtUtil jwtUtil;

    @PostMapping("/v1/member/login")
    public GeneralResponse<AuthenticationResponse> loginCustomer(@RequestBody AuthenticationRequest authenticationRequest)
            throws DataNotFoundException, BadCredentialsException {
        log.info("Customer login with {}", authenticationRequest);
        try{
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new BadCredentialsException("รหัสลูกค้า และ/หรือ รหัสผ่านไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง");
        }

        UserDetails userDetails = customerService.loadUserByUserId(authenticationRequest.getUsername());

        final String token = jwtUtil.generateToken(userDetails);
        return new GeneralResponse<>(SUCCESS, new AuthenticationResponse(token));
    }

    @GetMapping("/v1/member/profile")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<CustomerProfileResponse> getCustomerProfile(HttpServletRequest request) throws DataNotFoundException {
        log.info("===== Start get customer profile =====");
        CustomerProfileResponse response = customerService.getCustomerFromToken(request);
        log.info("===== End get customer profile =====");
        return new GeneralResponse<>(SUCCESS, response);
    }


    @PostMapping("/v1/member/verify")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<Void> verifyCustomer(HttpServletRequest request, @RequestBody VerifyCustomerRequest verifyCustomerRequest)
            throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start verify customer =====");
        customerService.verifyCustomer(request, verifyCustomerRequest);
        log.info("===== End verify customer =====");
        return new GeneralResponse<>(SUCCESS, null);
    }

    @PostMapping("/v1/member/verify-otp")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<Void> verifyOtp(HttpServletRequest request, @RequestBody VerifyOtpRequest verifyOtpRequest)
            throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start verify otp =====");
        customerService.verifyOtp(request, verifyOtpRequest);
        log.info("===== End verify otp =====");
        return new GeneralResponse<>(SUCCESS, null);
    }

    @PostMapping("/v1/member/request-otp")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<String> requestOtp(HttpServletRequest request, @RequestBody RequestOtpRequest requestOtpRequest)
            throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start request otp =====");
        String refNo = customerService.requestOtp(request, requestOtpRequest.getMobileNo());
        log.info("===== End request otp =====");
        return new GeneralResponse<>(SUCCESS, refNo);
    }

    @PostMapping("/v1/member/reward/{rewardId}/redeem")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<Void> redeemReward(HttpServletRequest request, @PathVariable("rewardId") UUID rewardId)
        throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start redeem reward =====");
        customerService.redeemReward(request, rewardId);
        log.info("===== End redeem reward =====");
        return new GeneralResponse<>(SUCCESS, null);
    }

    @GetMapping("/v1/member/rewards")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<List<RewardDto>> getRewardList() {
        log.info("===== Start get reward list =====");
        List<RewardDto> rewardList = rewardService.getAllActiveReward();
        log.info("===== End get reward list =====");
        return new GeneralResponse<>(SUCCESS, rewardList);
    }

    @GetMapping("/v1/member/netflix/packages")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<List<GetNetflixPackageResponse>> getNetflixPackageForMember(HttpServletRequest request)
            throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start get netflix package for member =====");
        NetflixPackageDto currentPackage = customerService.getMemberCurrentNetflixPackage(request);
        List<GetNetflixPackageResponse> netflixPackages = new ArrayList<>();
        if (currentPackage != null ) {
            netflixPackages = netflixService.getAllNetflixPackageByDevice(currentPackage.getDevice());
        }
        log.info("===== End get netflix package for member =====");
        return new GeneralResponse<>(SUCCESS, netflixPackages);
    }

    @GetMapping("/v1/member/youtube/packages")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<List<GetYoutubePackageResponse>> getYoutubePackageForMember() {
        log.info("===== Start get youtube package for member =====");
        List<GetYoutubePackageResponse> response = youtubeService.getAllYoutubePackage("EXTEND");
        log.info("===== End get youtube package for member =====");
        return new GeneralResponse<>(SUCCESS, response);
    }

    @GetMapping("/v1/member/event/{eventName}/available")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<Boolean> checkEventAvailable(@PathVariable("eventName") String eventName) throws DataNotFoundException {
        log.info("===== Start check Event: {} is available? =====", eventName);
        boolean result = eventService.isEventAvailable(eventName);
        log.info("===== End check Event: {} is available: {}  =====", eventName, result);
        return new GeneralResponse<>(SUCCESS, result);
    }

    @PostMapping("/v1/member/event/{eventName}/register")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<Boolean> registerEvent(
            @PathVariable("eventName") String eventName,
            @RequestBody EventRegisterRequest eventRegisterRequest,
            HttpServletRequest request
    ) throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start register Event: {} =====", eventName);
        UUID registerId = eventService.registerEvent(eventName, eventRegisterRequest, request);
        log.info("===== End register Event: {}  =====", eventName);
        return new GeneralResponse<>(SUCCESS, true);
    }
}
