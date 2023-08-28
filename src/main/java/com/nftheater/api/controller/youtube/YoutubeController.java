package com.nftheater.api.controller.youtube;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.youtube.request.*;
import com.nftheater.api.controller.youtube.response.CreateYoutubeAccountResponse;
import com.nftheater.api.controller.youtube.response.GetYoutubePackageResponse;
import com.nftheater.api.controller.youtube.response.SearchYoutubeAccountResponse;
import com.nftheater.api.controller.youtube.response.YoutubeAccountResponse;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.AdminUserService;
import com.nftheater.api.service.YoutubeService;
import jakarta.servlet.http.HttpServletRequest;
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
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final AdminUserService adminUserService;

    @Secured({Module.ALL, Module.YOUTUBE})
    @PostMapping("/v1/youtube/search")
    public GeneralResponse<SearchYoutubeAccountResponse> searchYoutube(
            @RequestBody(required = false) SearchYoutubeAccountRequest searchYoutubeAccountRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start Search Youtube");
        SearchYoutubeAccountResponse response = youtubeService.searchYoutubeAccount(searchYoutubeAccountRequest, pageableRequest);
        log.info("End Search Youtube size : {}", response.getYoutube().size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @GetMapping("/v1/youtube/{id}")
    public GeneralResponse<YoutubeAccountResponse> getYoutubeAccount(@PathVariable("id") UUID accountId) throws DataNotFoundException{
        log.info("Start get Youtube account : {}",accountId );
        YoutubeAccountResponse response = youtubeService.getYoutubeAccountById(accountId);
        log.info("End get Youtube account : {}",accountId );
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @PostMapping("/v1/youtube")
    public GeneralResponse<CreateYoutubeAccountResponse> createYoutube(
            HttpServletRequest httpServletRequest,
            @RequestBody CreateYoutubeAccountRequest request) throws DataNotFoundException {
        log.info("Start Create Netflix account with request : {}", request);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        request.setCreatedBy(adminId);
        CreateYoutubeAccountResponse response = youtubeService.createYoutubeAccount(request);
        log.info("End Create Youtube account with account id : {}", response.getId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @GetMapping("/v1/youtube/package/{type}")
    public GeneralResponse<List<GetYoutubePackageResponse>> getYoutubePackage(
            @PathVariable("type") String type){
        log.info("Start get youtube package for {}", type);
        List<GetYoutubePackageResponse> response = youtubeService.getAllYoutubePackage(type);
        log.info("End get youtube package for {} size : {}", type, response.size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @PatchMapping("/v1/youtube/{accountId}/user")
    public GeneralResponse<Void> linkUserToYoutubeAccount(
            @PathVariable("accountId") UUID accountId,
            @RequestBody UpdateLinkUserYoutubeRequest updateLinkUserYoutubeRequest,
            HttpServletRequest httpServletRequest)
            throws DataNotFoundException, InvalidRequestException, FirebaseAuthException {
        log.info("Start link user to Youtube : {}", accountId);
        String bearerToken = httpServletRequest.getHeader("Authorization");
        UUID adminId = adminUserService.getAdminUserByFirebaseToken(bearerToken.substring(7, bearerToken.length())).getId();
        youtubeService.linkUserToYoutubeAccount(accountId, updateLinkUserYoutubeRequest, adminId);
        log.info("End link user to netflix : {}", accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @DeleteMapping("/v1/youtube/{accountId}/user/{userId}")
    public GeneralResponse<Void> removeUserFromYoutubeAccount(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("userId") String userId) throws DataNotFoundException {
        log.info("Start remove user {} from youtube : {}", userId, accountId);
        youtubeService.removeUserFromYoutubeAccount(accountId, userId);
        log.info("End remove user {} from youtube : {}", userId, accountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @PostMapping("/v1/youtube/{accountId}/user/transfer")
    public GeneralResponse<Void> transferUser(@PathVariable("accountId") UUID toAccountId,
                                              @RequestBody TransferUserRequest transferUserRequest,
                                              HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Start transfer user {} users from account {} to account {}", transferUserRequest.getUserIds().size(), transferUserRequest.getFromAccountId(), toAccountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        youtubeService.transferUserToNewAccount(toAccountId, transferUserRequest, adminId);
        log.info("End transfer user {} users from account {} to account {}", transferUserRequest.getUserIds().size(), transferUserRequest.getFromAccountId(), toAccountId);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @PatchMapping("/v1/youtube/{accountId}/status/{status}")
    public GeneralResponse<Void> updateYoutubeAccountStatus(
            @PathVariable("accountId") UUID accountId,
            @PathVariable("status") String status,
            HttpServletRequest httpServletRequest) throws DataNotFoundException, InvalidRequestException {
        log.info("Start update netflix account status id : {} to {}", accountId, status);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        youtubeService.updateYoutubeAccountStatus(accountId, status, adminId);
        log.info("End disable netflix account status id : {} to {}", accountId, status);
        return new GeneralResponse<>(SUCCESS, null);
    }

    @Secured({Module.ALL, Module.YOUTUBE})
    @PatchMapping("/v1/youtube/{accountId}")
    public GeneralResponse<YoutubeAccountResponse> updateYoutubeAccount(
            @PathVariable("accountId") UUID accountId,
            @RequestBody UpdateYoutubeAccountRequest request,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start update netflix account {}", accountId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        YoutubeAccountResponse response = youtubeService.updateYoutubeAccount(accountId, adminId, request);
        log.info("End update netflix account {}", accountId);
        return new GeneralResponse<>(SUCCESS, response);
    }

}
