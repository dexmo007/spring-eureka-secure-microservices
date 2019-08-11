package com.dexmohq.spring.eureka;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.stream.Collectors.toSet;

@RestController
public class TestController {

    @GetMapping("/")
    public Map<String, Object> hello(Authentication authentication) {
        return Map.of(
                "user", authentication.getName(),
                "roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toSet())
        );
    }

}
