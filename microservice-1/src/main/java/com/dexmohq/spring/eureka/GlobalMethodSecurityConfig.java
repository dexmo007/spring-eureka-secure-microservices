package com.dexmohq.spring.eureka;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author Henrik Drefs
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableGlobalSystemAccess
@Configuration
public class GlobalMethodSecurityConfig extends MicroserviceGlobalMethodSecurityConfiguration {
}
