package com.nftheater.api.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse {
    private Integer page;
    private Integer size;
    private Integer totalPage;
    private Long totalRecords;
    public void setPage(Integer page) {
        this.page = page + 1;
    }
}
