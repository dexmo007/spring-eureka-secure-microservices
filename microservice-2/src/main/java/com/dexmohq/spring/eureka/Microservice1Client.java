package com.dexmohq.spring.eureka;

import com.dexmohq.spring.eureka.feign.AuthenticateAsSystem;
import com.dexmohq.spring.eureka.feign.AuthenticateAsUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient("microservice-1")
public interface Microservice1Client {

    @GetMapping
    @AuthenticateAsUser
    Map<String, Object> testAsUser();

    @GetMapping
    @AuthenticateAsSystem
    Map<String, Object> testAsSystem();

}
