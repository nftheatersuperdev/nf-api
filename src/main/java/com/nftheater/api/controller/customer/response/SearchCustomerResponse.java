package com.nftheater.api.controller.customer.response;

import com.nftheater.api.controller.response.PaginationResponse;
import lombok.Data;

import java.util.List;

@Data
public class SearchCustomerResponse {

    private PaginationResponse pagination;
    private List<CustomerResponse> customer;
}
