package com.nftheater.api.service;

import com.nftheater.api.client.paysolution.PaySolutionClient;
import com.nftheater.api.client.paysolution.request.CreateSecureLinkRequest;
import com.nftheater.api.client.paysolution.response.CreateSecureLinkResponse;
import com.nftheater.api.config.BusinessConfiguration;
import com.nftheater.api.constant.PaymentType;
import com.nftheater.api.controller.payment.request.CreatePaymentRequest;
import com.nftheater.api.controller.payment.response.CreatePaymentResponse;
import com.nftheater.api.dto.PackageDto;
import com.nftheater.api.entity.CustomerEntity;
import com.nftheater.api.entity.OrderEntity;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.repository.OrderRepository;
import com.nftheater.api.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentService {

    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("YYYY-MM-DD-HH-MM-SS");

    private final BusinessConfiguration businessConfiguration;
    private final PackageService packageService;
    private final CustomerService customerService;
    private final OrderRepository orderRepository;
    private final PaySolutionClient paySolutionClient;

    public CreatePaymentResponse createPayment(HttpServletRequest httpServletRequest, CreatePaymentRequest createPaymentRequest)
            throws InvalidRequestException, DataNotFoundException {

        UserDetails userDetails = customerService.getUserDetail(httpServletRequest);
        CustomerEntity customerEntity = customerService.getCustomerByUserId(userDetails.getUsername());

        PackageDto packageDto = packageService.getPackageDetailById(createPaymentRequest.getPackageId());
        log.info("Found package with name : {}", packageDto.getName());
        String refNo = generateReferenceNo();

        CreateSecureLinkRequest createSecureLinkRequest = new CreateSecureLinkRequest();
        createSecureLinkRequest.setMerchant(businessConfiguration.getPaySolutionMerchantName());
        createSecureLinkRequest.setReturnURL(businessConfiguration.getPaySolutionReturnUrl());
        createSecureLinkRequest.setPostBackURL("");
        createSecureLinkRequest.setPayValue(packageDto.getPrice());
        createSecureLinkRequest.setOrderDetail(generateProductDetail(createPaymentRequest.getPaymentType(), packageDto.getName()));
        createSecureLinkRequest.setExpireDate(generateExpireDate());
        createSecureLinkRequest.setUserEMail(createPaymentRequest.getUserEmail());
        createSecureLinkRequest.setUserTelNo(createPaymentRequest.getUserTelNo());
        createSecureLinkRequest.setOneTime("Y");
        createSecureLinkRequest.setRefNo(refNo);
        createSecureLinkRequest.setMonthInstallment("");
        createSecureLinkRequest.setBankInstallment("");

        ResponseEntity<CreateSecureLinkResponse> createSecureLinkResponse = paySolutionClient.createSecureLink(createSecureLinkRequest);

        if (createSecureLinkResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Create secure link success.");

            OrderEntity createdOrder = new OrderEntity();
            createdOrder.setRefNo(refNo);
            createdOrder.setUserId(customerEntity.getId().toString());
            createdOrder.setPackageId(createPaymentRequest.getPackageId().toString());
            createdOrder.setAmount(BigDecimal.valueOf(packageDto.getPrice()));
            createdOrder.setStatus("PENDING");
            createdOrder.setCreatedDate(ZonedDateTime.now(DateUtil.getTimeZone()));
            createdOrder.setUpdatedDate(ZonedDateTime.now(DateUtil.getTimeZone()));

            orderRepository.save(createdOrder);

            return new CreatePaymentResponse(generatePaymentLink(createSecureLinkResponse.getBody()));
        } else {
            throw new InvalidRequestException("Cannot create secure link.");
        }
    }

    public void updatePayment(String refNo, String orderNo, String status, String statusName) throws DataNotFoundException {
        log.info("Update Payment for refNo : {}", refNo);

        OrderEntity orderEntity = orderRepository.findById(refNo)
                .orElseThrow(() -> new DataNotFoundException("Not found order with refNo : " + refNo));

        orderEntity.setOrderNo(orderNo);
        orderEntity.setStatus(statusName);
        orderEntity.setUpdatedDate(ZonedDateTime.now(DateUtil.getTimeZone()));

        orderRepository.save(orderEntity);
    }

    private String generateExpireDate() {
        Duration duration = businessConfiguration.getPaySolutionExpiredDuration();
        ZonedDateTime expiredDate = ZonedDateTime.now(DateUtil.getTimeZone()).plus(duration);
        return YYYY_MM_DD_HH_MM_SS.format(expiredDate);
    }
    private String generateProductDetail(PaymentType type, String packageName) {
        if (type == PaymentType.NEW) {
            return "สมัคร ".concat(packageName);
        } else {
            return "ต่ออายุ ".concat(packageName);
        }
    }

    private String generateReferenceNo() {
        int length = 12;
        return RandomStringUtils.random(length, false, true);
    }

    private String generatePaymentLink(CreateSecureLinkResponse createSecureLink) {
        String paymentUrl = businessConfiguration.getPaySolutionPaymentUrl();
        return paymentUrl + createSecureLink.getMerchant() + "/" + createSecureLink.getPayValue() + "/" + createSecureLink.getOrderDetail();
    }

}
