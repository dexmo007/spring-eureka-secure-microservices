package com.dexmohq.spring.eureka;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author Henrik Drefs
 */
@Getter
class MockJwtAuthentication extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final String name;

    MockJwtAuthentication(String name, Collection<? extends GrantedAuthority> authorities) {
        super(new Jwt("token_value", Instant.now(), Instant.MAX, Map.of("alg", "RS256", "typ", "JWT"), claims(name, authorities)), authorities);
        this.name = name;
    }

    private static Map<String, Object> claims(String name, Collection<? extends GrantedAuthority> authorities) {
        return Map.of(
                "user_name", name,
                "authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(toList()),
                "jti", UUID.randomUUID().toString(),
                "client_id", "test_client",
                "scope", Collections.singleton("any")
        );
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return name;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public static MockJwtAuthentication of(String name, String... authorities) {
        return new MockJwtAuthentication(name, Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));
    }

}
