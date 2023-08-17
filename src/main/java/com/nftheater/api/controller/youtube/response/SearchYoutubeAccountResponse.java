package com.nftheater.api.controller.youtube.response;

import com.nftheater.api.controller.netflix.response.NetflixAccountResponse;
import com.nftheater.api.controller.response.PaginationResponse;
import lombok.Data;

import java.util.List;

@Data
public class SearchYoutubeAccountResponse {
    private PaginationResponse pagination;
    private List<YoutubeAccountResponse> youtube;
}
