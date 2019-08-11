package com.dexmohq.spring.eureka.feign;

import feign.Contract;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties(AuthenticationAwareFeignProperties.class)
@CommonsLog
@RequiredArgsConstructor
public class AuthenticationAwareFeignAutoConfiguration {

    private final AuthenticationAwareFeignProperties properties;

    @PostConstruct
    public void init() {
        log.info("Auto-configuring authenticate aware feign clients");
    }

    @Bean
    // TODO possibly conditional on missing bean
    public Contract contract() {
        return new AuthenticationAwareSpringMvcContract(properties.getSystemToken());
    }

    @Bean
    public RequestInterceptor userAuthenticationRequestInterceptor() {
        return new AuthenticationAwareRequestInterceptor();
    }

}
