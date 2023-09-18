package com.nftheater.api.controller.customer;

import com.nftheater.api.controller.customer.request.CheckUrlRequest;
import com.nftheater.api.controller.customer.request.CreateCustomerRequest;
import com.nftheater.api.controller.customer.request.ExtendDayCustomerRequest;
import com.nftheater.api.controller.customer.request.SearchCustomerRequest;
import com.nftheater.api.controller.customer.response.*;
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

    @GetMapping("/v1/customer/account/{account}")
    public GeneralResponse<List<CustomerListResponse>> getCustomerList(@PathVariable("account") String account) {
        log.info("Start Get Customer for option list");
        List<CustomerListResponse> customerListResponseList = customerService.getCustomerList(account);
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

    @GetMapping("/v1/customer/status/{status}/next")
    public GeneralResponse<String> getNextCustomerStatus(@PathVariable("status") String status) throws DataNotFoundException {
        log.info("Start get next status of {}", status);
        String nextStatus = customerService.getNextStatusForCustomer(status);
        log.info("End get next status of {} = {}", status, nextStatus);
        return new GeneralResponse<>(SUCCESS, nextStatus);
    }

    @PatchMapping("/v1/customer/{customerId}")
    public GeneralResponse<CustomerResponse> UpdateCustomer(
            @PathVariable("customerId") String customerId,
            @RequestBody UpdateCustomerRequest updateCustomerRequest,
            HttpServletRequest httpServletRequest) throws DataNotFoundException {
        log.info("Start update customer {}", customerId);
        UUID adminId = UUID.fromString(httpServletRequest.getHeader("userId"));
        CustomerResponse response = customerService.updateCustomer(customerId, updateCustomerRequest, adminId);
        log.info("End update customer {}", customerId);
        return new GeneralResponse<>(SUCCESS, response);
    }

    @PostMapping("/v1/customer/line-url/isduplicate")
    public GeneralResponse<Boolean> isUrlDuplicate(
            @RequestBody @Valid CheckUrlRequest request) {
        log.info("Start check line Url {}", request.getUrl());
        Boolean isDup = customerService.isUrlDuplicate(request.getUrl());
        log.info("End check Url {}", request.getUrl());
        return new GeneralResponse<>(SUCCESS, isDup);
    }

    @DeleteMapping("/v1/customer/{userId}")
    public GeneralResponse<Void> deleteCustomer(@PathVariable("userId") String userId) throws DataNotFoundException {
        log.info("Start delete customer {}", userId);
        customerService.deleteUserByUserId(userId);
        log.info("End delete customer {}", userId);
        return new GeneralResponse<>(SUCCESS, null);
    }

}
