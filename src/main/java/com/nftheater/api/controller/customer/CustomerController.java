package com.nftheater.api.controller.customer;

import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.request.ExtendDayCustomerRequest;
import com.nftheater.api.controller.customer.request.SearchCustomerRequest;
import com.nftheater.api.controller.customer.response.CreateCustomerResponse;
import com.nftheater.api.controller.customer.response.CustomerListResponse;
import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.controller.customer.response.SearchCustomerResponse;
import com.nftheater.api.controller.netflix.response.SearchNetflixAccountResponse;
import com.nftheater.api.controller.request.PageableRequest;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/v1/customer/search")
    public GeneralResponse<SearchCustomerResponse> searchCustomer(
            @RequestBody(required = false)SearchCustomerRequest searchCustomerRequest,
            PageableRequest pageableRequest
    ) {
        log.info("Start Search Customer");
        SearchCustomerResponse response = customerService.searchCustomer(searchCustomerRequest, pageableRequest);
        log.info("End Search Customer size : {}", response.getCustomer().size());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PostMapping("/v1/customer")
    public GeneralResponse<CreateCustomerResponse> createCustomer(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
        log.info("Start Create customer with request : {}", createCustomerRequest);
        UUID userId = UUID.fromString(httpServletRequest.getHeader("userId"));
        createCustomerRequest.setCreatedBy(userId);
        CreateCustomerResponse response = customerService.createCustomer(createCustomerRequest);
        log.info("End Create customer with id : {}", response.getUserId());
        return new GeneralResponse<>(SUCCESS, response);
    }

    @GetMapping("/v1/customer/list")
    public GeneralResponse<List<CustomerListResponse>> getCustomerList() {
        log.info("Start Get Customer for option list");
        List<CustomerListResponse> customerListResponseList = customerService.getCustomerList();
        log.info("End Get Customer for option list size : {}",customerListResponseList.size());
        return new GeneralResponse<>(SUCCESS, customerListResponseList);
    }

    @PatchMapping("/v1/customer/{userId}/extend-day")
    public GeneralResponse<CustomerResponse> extendExpiredDateOfCustomer(@PathVariable("userId") String userId,
            @RequestBody ExtendDayCustomerRequest extendDayCustomerRequest,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start Extend expired date for {} days of Customer {}", extendDayCustomerRequest.getExtendDay(), userId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        CustomerResponse response = customerService.extendExpiredDateForCustomer(userId, extendDayCustomerRequest, adminId);
        log.info("End Extend expired date for {} days of Customer {}", extendDayCustomerRequest.getExtendDay(), userId);
        return new GeneralResponse<>(SUCCESS, response);
    }

}
