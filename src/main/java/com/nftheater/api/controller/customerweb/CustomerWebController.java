package com.nftheater.api.controller.customerweb;

import com.nftheater.api.controller.customer.response.CustomerResponse;
import com.nftheater.api.controller.customerweb.request.AuthenticationRequest;
import com.nftheater.api.controller.customerweb.response.AuthenticationResponse;
import com.nftheater.api.controller.customerweb.response.CustomerProfileResponse;
import com.nftheater.api.controller.response.GeneralResponse;
import com.nftheater.api.exception.BadCredentialsException;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.service.CustomerService;
import com.nftheater.api.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.nftheater.api.constant.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerWebController {
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @PostMapping("/v1/customer-web/login")
    public GeneralResponse<AuthenticationResponse> loginCustomer(@RequestBody AuthenticationRequest authenticationRequest)
            throws DataNotFoundException, BadCredentialsException {
        log.info("Customer login with {}", authenticationRequest);
        try{
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new BadCredentialsException("รหัสลูกค้า และ/หรือ รหัสผ่านไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง");
        }

        UserDetails userDetails = customerService.loadUserByUserId(authenticationRequest.getUsername());

        final String token = jwtUtil.generateToken(userDetails);
        return new GeneralResponse<>(SUCCESS, new AuthenticationResponse(token));
    }

    @GetMapping("/v1/customer-web/profile")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public GeneralResponse<CustomerProfileResponse> getCustomerProfile(HttpServletRequest request) throws DataNotFoundException {
        log.info("===== Start get customer profile =====");
        CustomerProfileResponse response = customerService.getCustomerFromToken(request);
        return new GeneralResponse<>(SUCCESS, response);
    }

}
