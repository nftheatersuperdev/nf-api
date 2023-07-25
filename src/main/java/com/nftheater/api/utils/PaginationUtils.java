package com.nftheater.api.utils;

import com.nftheater.api.controller.response.PaginationResponse;
import org.springframework.data.domain.Page;

public class PaginationUtils {

    private PaginationUtils() {
    }

    public static PaginationResponse createPagination(Page<?> page) {
        PaginationResponse pagination = new PaginationResponse();
        pagination.setPage(page.getPageable().getPageNumber());
        pagination.setSize(page.getSize());
        pagination.setTotalPage(page.getTotalPages());
        pagination.setTotalRecords(page.getTotalElements());

        return pagination;
    }

}
