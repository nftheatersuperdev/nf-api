package com.nftheater.api.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.nftheater.api.config.HeaderMapRequestWrapper;
import com.nftheater.api.constant.HeaderRequest;
import com.nftheater.api.dto.AdminUserDto;
import com.nftheater.api.exception.DataNotFoundException;
import com.nftheater.api.service.AdminUserService;
import com.nftheater.api.service.UserInfoService;
import com.nftheater.api.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final SecurityUtils securityUtils;
    private final JwtUtil jwtUtil;
    private final AdminUserService adminUserService;
    private final UserInfoService userInfoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizeToken = securityUtils.getBearerToken(request);
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        if (securityUtils.isBearer(authorizeToken)) {
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
                Set<GrantedAuthority> authorityList = new HashSet<GrantedAuthority>();
                authorityList.add(new SimpleGrantedAuthority(adminUserDto.getModule()));
                Authentication authentication = new UsernamePasswordAuthenticationToken(adminUserDto, null, authorityList);
                log.info("UsernamePasswordAuthenticationToken:" + authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("Cannot find admin user with the request token");
            }
        } else if (securityUtils.isBearerNF(authorizeToken)) {
            String customerToken = securityUtils.getTokenFromRequest(request);
            String username = jwtUtil.extractUsername(customerToken);
            UserDetails userDetails = userInfoService.loadUserByUsername(username);
            if (jwtUtil.validateToken(customerToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
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