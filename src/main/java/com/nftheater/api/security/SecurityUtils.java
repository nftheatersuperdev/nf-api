package com.nftheater.api.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

@Slf4j
@Component
public class SecurityUtils {

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = null;
        Cookie cookieToken = WebUtils.getCookie(request, "token");
        if (cookieToken != null) {
            token = cookieToken.getValue();
        } else {
            String bearerToken = getBearerToken(request);
            log.debug("bearerToken " + bearerToken);
            if (isBearer(bearerToken)) {
                token = bearerToken.substring(7, bearerToken.length());
            }
        }
        return token;
    }

    public boolean isBearer(String bearerToken) {
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ");
    }

    public String getBearerToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
