package com.nftheater.api.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.config.HeaderMapRequestWrapper;
import com.nftheater.api.constant.HeaderRequest;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.service.AdminUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final SecurityUtils securityUtils;
    private final AdminUserService adminUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String bearerToken = securityUtils.getBearerToken(request);
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        if (securityUtils.isBearer(bearerToken)) {
            String idToken = securityUtils.getTokenFromRequest(request);
            AdminUserDto adminUserDto = null;
            try {
                adminUserDto = adminUserService.getAdminUserByFirebaseToken(idToken);
            } catch (FirebaseAuthException e) {
                log.error("Firebase Exception : " + e.getLocalizedMessage(), e);
            } catch (DataNotFoundException e) {
                log.error("Data not found Exception : " + e.getLocalizedMessage(), e);
            }

            if (adminUserDto != null) {
                setHeader(requestWrapper, adminUserDto);
                Authentication authentication = new UsernamePasswordAuthenticationToken(adminUserDto, null, null);
                log.info("UsernamePasswordAuthenticationToken:" + authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("Cannot find admin user with the request token");
            }
        }
        filterChain.doFilter(requestWrapper, response);
    }

    private void setHeader(HeaderMapRequestWrapper requestWrapper, AdminUserDto adminUserDto) {
        requestWrapper.addHeader(HeaderRequest.USER_TYPE, "admin");
        requestWrapper.addHeader(HeaderRequest.USER_ID, adminUserDto.getId().toString());
        requestWrapper.addHeader(HeaderRequest.FIREBASE_ID, adminUserDto.getFirebaseId());
        requestWrapper.addHeader(HeaderRequest.EMAIL, adminUserDto.getEmail());
    }
}