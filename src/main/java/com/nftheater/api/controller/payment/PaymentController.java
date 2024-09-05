package com.nftheater.api.controller.payment;

import com.nftheater.api.controller.member.response.CustomerProfileResponse;
import com.nftheater.api.controller.payment.request.CreatePaymentRequest;
import com.nftheater.api.controller.payment.response.CreatePaymentResponse;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.exception.InvalidRequestException;
import com.nftheater.api.service.CustomerService;
import com.nftheater.api.service.OrderPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @PostMapping("/v1/payment/post/callback")
    public GeneralResponse<Void> callback(
            @RequestParam(required = false) String refno,
            @RequestParam(required = false) String orderno,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String statusname
    ) throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start PaySolution callback =====");
        log.info("Callback with params refNo={}, orderNo={}, status={}, statusName={}", refno, orderno, status, statusname);
        orderPaymentService.updatePayment(refno, orderno, status, statusname);
        log.info("===== End PaySolution callback =====");
        return new GeneralResponse<>(SUCCESS, null);
    }

    @GetMapping("/v1/payment/get/callback")
    public GeneralResponse<Void> getCallback(
            @RequestParam(required = false) String refno,
            @RequestParam(required = false) String orderno,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String statusname
    ) throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start PaySolution callback =====");
        log.info("Callback with params refNo={}, orderNo={}, status={}, statusName={}", refno, orderno, status, statusname);
        orderPaymentService.updatePayment(refno, orderno, status, statusname);
        log.info("===== End PaySolution callback =====");
        return new GeneralResponse<>(SUCCESS, null);
    }

    @PostMapping("/v1/payments/order-payments")
    public GeneralResponse<CreatePaymentResponse> createOrderPayment(
            HttpServletRequest httpServletRequest,
            @RequestBody CreatePaymentRequest createPaymentRequest)
            throws DataNotFoundException, InvalidRequestException {
        log.info("===== Start create payment =====");
        CreatePaymentResponse paymentResponse = orderPaymentService.createPayment(httpServletRequest, createPaymentRequest);
        log.info("===== End create payment =====");
        return new GeneralResponse<>(SUCCESS, paymentResponse);
    }
}
