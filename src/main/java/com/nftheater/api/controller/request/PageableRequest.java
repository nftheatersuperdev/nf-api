package com.nftheater.api.controller.request;

import lombok.Data;

@Data
public class PageableRequest {
    private int page = 1;
    private int size = 10;

    public int getPageZeroIndex() {
        return page - 1;
    }
}
