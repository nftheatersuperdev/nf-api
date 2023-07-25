package com.nftheater.api.controller.netflix;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.controller.netflix.request.CreateNetflixAccountRequest;
import com.nftheater.api.controller.netflix.request.CreateNetflixAdditionalAccountRequest;
import com.nftheater.api.controller.netflix.request.SearchNetflixAccountRequest;
import com.nftheater.api.controller.netflix.request.UpdateLinkUserNetflixRequest;
import com.nftheater.api.controller.netflix.response.CreateNetflixAccountResponse;
import com.nftheater.api.controller.netflix.response.CreateNetflixAdditionalAccountResponse;
import com.nftheater.api.controller.netflix.response.NetflixAccountResponse;
import com.nftheater.api.controller.netflix.response.SearchNetflixAccountResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NetflixController {

    private final NetflixService netflixService;
    private final AdminUserService adminUserService;

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

    @PostMapping("/v1/netflix")
    public GeneralResponse<CreateNetflixAccountResponse> createNetflix(HttpServletRequest httpServletRequest, @RequestBody CreateNetflixAccountRequest request) throws DataNotFoundException {
        log.info("Start Create Netflix account with request : {}", request);
        UUID userId = UUID.fromString(httpServletRequest.getHeader("userId"));
        request.setCreatedBy(userId);
        CreateNetflixAccountResponse response = netflixService.createNetflixAccount(request);
        log.info("End Create Netflix account with account id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @GetMapping("/v1/netflix/{accountId}")
    public GeneralResponse<NetflixAccountResponse> getNetflixAccount(@PathVariable("accountId") UUID accountId) throws DataNotFoundException {
        log.info("Start Get Netflix Account : {}", accountId);
        NetflixAccountResponse response = netflixService.getNetflixAccount(accountId);
        log.info("End Get Netflix Account : {}", accountId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PatchMapping("/v1/netflix/{accountId}/user")
    public GeneralResponse<Void> linkUserToNetflixAccount(@PathVariable("accountId") UUID accountId, @RequestBody UpdateLinkUserNetflixRequest updateLinkUserNetflixRequest, HttpServletRequest httpServletRequest)
            throws DataNotFoundException, InvalidRequestException, FirebaseAuthException {
        log.info("Start link user to netflix : {}", accountId);
        String bearerToken = httpServletRequest.getHeader("Authorization");
        UUID userId = adminUserService.getAdminUserByFirebaseToken(bearerToken.substring(7, bearerToken.length())).getId();
        netflixService.linkUserToNetflixAccount(accountId, updateLinkUserNetflixRequest, userId);
        log.info("End link user to netflix : {}", accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @PostMapping("/v1/netflix/{accountId}/additional")
    public GeneralResponse<CreateNetflixAdditionalAccountResponse> createNetflixAdditionalAccount(
            @PathVariable("accountId") UUID accountId,
            @Valid @RequestBody CreateNetflixAdditionalAccountRequest request,
            HttpServletRequest httpServletRequest)
            throws DataNotFoundException {
        log.info("Start create netflix additional account for netflix {}", accountId);
        UUID userId = UUID.fromString(httpServletRequest.getHeader("userId"));
        request.setCreatedBy(userId.toString());
        CreateNetflixAdditionalAccountResponse response = netflixService.createNetflixAdditionalAccount(accountId, request);
        log.info("End create netflix additional account id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PatchMapping("/v1/netflix/{accountId}/status/{status}")
    public GeneralResponse<Void> updateNetflixAccountStatus(@PathVariable("accountId") UUID accountId,@PathVariable("status") Boolean status, HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start update netflix account status id : {} to {}", accountId, status);
        UUID userId = UUID.fromString(httpServletRequest.getHeader("userId"));
        netflixService.updateNetflixAccountStatus(accountId, status, userId);
        log.info("End disable netflix account status id : {} to {}", accountId, status);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @DeleteMapping("/v1/netflix/{accountId}/user/{userId}")
    public GeneralResponse<Void> removeUserFromNetflixAccount(@PathVariable("accountId") UUID accountId, @PathVariable("userId") String userId) throws DataNotFoundException {
        log.info("Start remove user {} from netflix : {}", userId, accountId);
        netflixService.removeUserFromNetflixAccount(accountId, userId);
        log.info("End remove user {} from netflix : {}", userId, accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

}
