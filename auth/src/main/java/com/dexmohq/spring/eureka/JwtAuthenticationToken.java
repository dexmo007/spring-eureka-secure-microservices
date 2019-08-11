package com.dexmohq.spring.eureka;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

        public JwtAuthenticationToken(Jwt source) {
            super(source, source.getClaimAsStringList("authorities").stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Map<String, Object> getTokenAttributes() {
            return getToken().getClaims();
        }

        @Override
        public Object getPrincipal() {
            return getToken().getClaimAsString("user_name");
        }
    }