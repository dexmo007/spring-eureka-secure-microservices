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
        return external(microservice1.testAsUser());
    }

    @GetMapping("/proxy/only-admin")
    public Map<String, Object> proxyOnlyAdmin() {
        return external(microservice1.onlyAdminAsUser());
    }

    @GetMapping("/system")
    public Map<String, Object> system() {
        return external(microservice1.testAsSystem());
    }

    @GetMapping("/system/only-admin")
    public Map<String, Object> onlyAdminAsSystem() {
        return external(microservice1.onlyAdminAsSystem());
    }

    private static Map<String, Object> external(Map<String,Object> data) {
        final Map<String, Object> res = new HashMap<>(data);
        res.put("source", "microservice-1");
        return res;
    }

}
