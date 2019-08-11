package com.dexmohq.spring.eureka.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "microservices.eureka.auth")
@Data
public class AuthenticationAwareFeignProperties {

    private String systemToken;

}
