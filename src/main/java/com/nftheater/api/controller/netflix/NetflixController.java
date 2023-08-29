package com.nftheater.api.controller.netflix;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.netflix.request.*;
import com.nftheater.api.controller.netflix.response.*;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.AdminUserService;
import com.nftheater.api.service.NetflixService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NetflixController {

    private final NetflixService netflixService;
    private final AdminUserService adminUserService;

    @Secured({Module.ALL, Module.NETFLIX})
    @PostMapping("/v1/netflix/search")
    public GeneralResponse<SearchNetflixAccountResponse> searchNetflix(
            @RequestBody(required = false)SearchNetflixAccountRequest searchNetflixAccountRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start Search Netflix");
        SearchNetflixAccountResponse response = netflixService.searchNetflixAccount(searchNetflixAccountRequest, pageableRequest);
        log.info("End Search Netflix size : {}", response.getNetflix().size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PostMapping("/v1/netflix")
    public GeneralResponse<CreateNetflixAccountResponse> createNetflix(
            HttpServletRequest httpServletRequest,
            @RequestBody CreateNetflixAccountRequest request) throws DataNotFoundException, InvalidRequestException {
        log.info("Start Create Netflix account with request : {}", request);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        request.setCreatedBy(adminId);
        CreateNetflixAccountResponse response = netflixService.createNetflixAccount(request);
        log.info("End Create Netflix account with account id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @GetMapping("/v1/netflix/{accountId}")
    public GeneralResponse<NetflixAccountResponse> getNetflixAccount(@PathVariable("accountId") UUID accountId) throws DataNotFoundException {
        log.info("Start Get Netflix Account : {}", accountId);
        NetflixAccountResponse response = netflixService.getNetflixAccount(accountId);
        log.info("End Get Netflix Account : {}", accountId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PatchMapping("/v1/netflix/{accountId}/user")
    public GeneralResponse<Void> linkUserToNetflixAccount(
            @PathVariable("accountId") UUID accountId,
            @RequestBody UpdateLinkUserNetflixRequest updateLinkUserNetflixRequest,
            HttpServletRequest httpServletRequest)
            throws DataNotFoundException, InvalidRequestException, FirebaseAuthException {
        log.info("Start link user to netflix : {}", accountId);
        String bearerToken = httpServletRequest.getHeader("Authorization");
        UUID userId = adminUserService.getAdminUserByFirebaseToken(bearerToken.substring(7, bearerToken.length())).getId();
        netflixService.linkUserToNetflixAccount(accountId, updateLinkUserNetflixRequest, userId, false);
        log.info("End link user to netflix : {}", accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PostMapping("/v1/netflix/{accountId}/additional")
    public GeneralResponse<CreateNetflixAdditionalAccountResponse> createNetflixAdditionalAccountAndLinkToAccount(
            @PathVariable("accountId") UUID accountId,
            @Valid @RequestBody CreateNetflixAdditionalAccountRequest request,
            HttpServletRequest httpServletRequest)
            throws DataNotFoundException {
        log.info("Start create netflix additional account for netflix {}", accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        request.setCreatedBy(adminId.toString());
        CreateNetflixAdditionalAccountResponse response = netflixService.createNetflixAdditionalAccount(accountId, request);
        log.info("End create netflix additional account id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PatchMapping("/v1/netflix/{accountId}/status/{status}")
    public GeneralResponse<Void> updateNetflixAccountStatus(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("status") Boolean status,
            HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Start update netflix account status id : {} to {}", accountId, status);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        netflixService.updateNetflixAccountStatus(accountId, status, adminId);
        log.info("End disable netflix account status id : {} to {}", accountId, status);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @DeleteMapping("/v1/netflix/{accountId}/user/{userId}")
    public GeneralResponse<Void> removeUserFromNetflixAccount(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("userId") String userId) throws DataNotFoundException {
        log.info("Start remove user {} from netflix : {}", userId, accountId);
        netflixService.removeUserFromNetflixAccount(accountId, userId);
        log.info("End remove user {} from netflix : {}", userId, accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @DeleteMapping("/v1/netflix/{accountId}/additional/{additionalId}/user/{userId}")
    public GeneralResponse<Void> removeUserFromAdditionalNetflixAccount(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("additionalId") UUID additionalId,
            @PathVariable("userId") String userId) throws DataNotFoundException {
        log.info("Start remove user {} from netflix : {} additional : {}", userId, accountId, additionalId);
        netflixService.removeUserFromAdditionalNetflixAccount(accountId, additionalId, userId);
        log.info("End remove user {} from netflix : {} additional : {}", userId, accountId, additionalId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @GetMapping("/v1/netflix/additional/available")
    public GeneralResponse<List<GetAvailableAdditionAccountResponse>> getAvailableAdditionalAccount() {
        log.info("Start get all available additional account.");
        List<GetAvailableAdditionAccountResponse> response = netflixService.getAvailableAdditionAccount();
        log.info("End get all available additional account.");
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @DeleteMapping("/v1/netflix/{accountId}/additional/{additionalId}")
    public GeneralResponse<Void> unlinkAdditionalFromNetflix(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("additionalId") UUID additionalId,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start unlink additional :  {} from netflix : {}", additionalId, accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        netflixService.unlinkAdditionAccount(accountId, additionalId, adminId);
        log.info("End unlink additional :  {} from netflix : {}", additionalId, accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PatchMapping("/v1/netflix/{accountId}/additional/{additionalId}")
    public GeneralResponse<Void> linkExisitingAdditionalToNetflix(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("additionalId") UUID additionalId,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start link additional :  {} from netflix : {}", additionalId, accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        netflixService.linkAdditionAccount(accountId, additionalId, adminId);
        log.info("End link additional :  {} from netflix : {}", additionalId, accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PatchMapping("/v1/netflix/{accountId}")
    public GeneralResponse<UpdateNetflixAccountResponse> updateNetflixAccount(
            @PathVariable("accountId") UUID accountId,
            @RequestBody UpdateNetflixAccountRequest request,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start update netflix account {}", accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        UpdateNetflixAccountResponse response = netflixService.updateNetflixAccount(accountId, adminId, request);
        log.info("End update netflix account {}", accountId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @GetMapping("/v1/netflixes")
    public GeneralResponse<List<GetNetflixAccountResponse>> getAllNetflixAccount() {
        log.info("Start all netflix account");
        List<GetNetflixAccountResponse> netflixAccountResponse = netflixService.getAllNetflixAccount();
        log.info("End all netflix account size : {}", netflixAccountResponse.size());
        return new GeneralResponse<>(SUCCESS, netflixAccountResponse);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PatchMapping("/v1/netflix/{accountId}/additional/{additionalId}/edit")
    public GeneralResponse<UpdateAdditionalAccountResponse> updateAdditionalAccount(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("additionalId") UUID additionalId,
            @RequestBody UpdateAdditionalAccountRequest request,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start update netflix account {}", accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        UpdateAdditionalAccountResponse response = netflixService.updateAdditionalAccount(accountId, additionalId, adminId, request);
        log.info("End update netflix account {}", accountId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @PostMapping("/v1/netflix/{accountId}/user/transfer")
    public GeneralResponse<Void> transferUser(@PathVariable("accountId") UUID toAccountId,
                                              @RequestBody TransferUserRequest transferUserRequest,
                                              HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Start transfer user {} users from account {} to account {}", transferUserRequest.getUserIds().size(), transferUserRequest.getFromAccountId(), toAccountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        netflixService.transferUserToNewAccount(toAccountId, transferUserRequest, adminId);
        log.info("End transfer user {} users from account {} to account {}", transferUserRequest.getUserIds().size(), transferUserRequest.getFromAccountId(), toAccountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.NETFLIX})
    @GetMapping("/v1/netflix/package/{device}")
    public GeneralResponse<List<GetNetflixPackageResponse>> getAllPackage(@PathVariable("device") String device) {
        log.info("Start get all netflix package.");
        List<GetNetflixPackageResponse> responses = netflixService.getAllNetflixPackageByDevice(device);
        log.info("End get all netflix package size : {}", responses.size());
        return new GeneralResponse<>(SUCCESS, responses);
    }

}
