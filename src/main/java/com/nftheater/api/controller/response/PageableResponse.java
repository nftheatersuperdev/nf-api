package com.nftheater.api.controller.response;

import lombok.Data;

import java.util.List;

@Data
public class PageableResponse<T> {
    private PaginationResponse pagination;
    private List<T> records;
}
