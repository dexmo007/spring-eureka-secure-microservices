package com.dexmohq.spring.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

@RestController
public class TestController {

    @Autowired
    private Microservice1Client microservice1;

    @GetMapping("/")
    public Map<String, Object> hello(Authentication authentication) {
        return Map.of(
                "user", authentication.getName(),
                "roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toSet())
        );
    }

    @GetMapping("/proxy")
    public Map<String, Object> proxy() {
        final Map<String, Object> res = new HashMap<>(microservice1.testAsUser());
        res.put("source", "microservice-1");
        return res;
    }

    @GetMapping("/system")
    public Map<String, Object> system() {
        final Map<String, Object> res = new HashMap<>(microservice1.testAsSystem());
        res.put("source", "microservice-1");
        return res;
    }

}
