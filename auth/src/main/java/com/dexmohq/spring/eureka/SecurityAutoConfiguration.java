package com.dexmohq.spring.eureka;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(99)
@CommonsLog
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Auto-configuring OAuth2 JWT Resource Server");
        http
                .authorizeRequests().anyRequest().authenticated().and()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(JwtAuthenticationToken::new).and().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .csrf()
                .disable();
    }

}
