package com.dexmohq.spring.eureka;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author Henrik Drefs
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends MicroserviceSecurityConfigurer {

    @Override
    protected void configureAccess(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/public").permitAll()
                .anyRequest().authenticated();
    }
}
