package com.nftheater.api.controller.netflix.response;

import com.nftheater.api.controller.response.PaginationResponse;
import lombok.Data;

import java.util.List;

@Data
public class SearchNetflixAccountResponse {
    private PaginationResponse pagination;
    private List<NetflixAccountResponse> netflix;
}
