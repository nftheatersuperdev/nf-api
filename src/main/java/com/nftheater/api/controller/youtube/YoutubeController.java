package com.nftheater.api.controller.youtube;

import com.nftheater.api.constant.Module;
import com.nftheater.api.controller.netflix.request.CreateNetflixAccountRequest;
import com.nftheater.api.controller.netflix.request.SearchNetflixAccountRequest;
import com.nftheater.api.controller.netflix.response.CreateNetflixAccountResponse;
import com.nftheater.api.controller.netflix.response.SearchNetflixAccountResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.controller.youtube.request.CreateYoutubeAccountRequest;
import com.nftheater.api.controller.youtube.request.SearchYoutubeAccountRequest;
import com.nftheater.api.controller.youtube.response.CreateYoutubeAccountResponse;
import com.nftheater.api.controller.youtube.response.GetYoutubePackageResponse;
import com.nftheater.api.controller.youtube.response.SearchYoutubeAccountResponse;
import com.nftheater.api.exception.DataNotFoundException;
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

    @Secured({Module.ALL, Module.YOUTUBE})
    @PostMapping("/v1/youtube/search")
    public GeneralResponse<SearchYoutubeAccountResponse> searchYoutube(
            @RequestBody(required = false) SearchYoutubeAccountRequest searchYoutubeAccountRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start Search Netflix");
        SearchYoutubeAccountResponse response = youtubeService.searchYoutubeAccount(searchYoutubeAccountRequest, pageableRequest);
        log.info("End Search Netflix size : {}", response.getYoutube().size());
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

}
