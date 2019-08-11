package com.dexmohq.spring.eureka.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Collection;

public class AuthenticationAwareRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AbstractOAuth2TokenAuthenticationToken) {
            final Collection<String> authHeaders = requestTemplate.headers().get("X-Auth-Type");
            if (authHeaders != null && authHeaders.contains("USER")) {
                final String token = ((AbstractOAuth2TokenAuthenticationToken) auth).getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        }
    }
}
