package com.nftheater.api.controller.payment;

import com.nftheater.api.controller.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping("/v1/payment/callback")
    public GeneralResponse<Void> callback(
            @RequestParam(required = false) String refno,
            @RequestParam(required = false) String orderno,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String statusname
    ) {
        log.info("===== Start PaySolution callback =====");
        log.info("Callback with params refNo={}, orderNo={}, status={}, statusName={}", refno, orderno, status, statusname);
        log.info("===== End PaySolution callback =====");
        return new GeneralResponse<>(SUCCESS, null);
    }
}
