package com.dexmohq.spring.eureka;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    @GetMapping("/only-user")
    @PreAuthorize("hasRole('USER')")
    public Map<String, Object> onlyUser() {
        return Map.of("msg", "You're a user!");
    }

    @GetMapping("/only-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> onlyAdmin() {
        return Map.of("msg", "You're an admin!");
    }

    @GetMapping("/public")
    public Map<String, Object> publicMsg() {
        return Map.of("msg", "This is public!");
    }

}
