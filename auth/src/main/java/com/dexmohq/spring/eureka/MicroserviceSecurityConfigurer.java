package com.dexmohq.spring.eureka;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Extend this class instead of {@link WebSecurityConfigurerAdapter} to configure security for a microservice.
 * This configuration
 * <ul>
 *     <li>makes the server a OAuth2 resource server using JWT. In order for it to work, you need to provide
 *     the configuration property &quot;spring.security.oauth2.resourceserver.jwt.jwk-set-uri&quot;
 *     </li>
 *     <li>disables sessions</li>
 *     <li>disables CSRF</li>
 * </ul>
 * <p>
 * To configure access to the microservice, you should override {@link #configureAccess(HttpSecurity)}.
 * The default implementation expects authentication for any request.
 *
 * @author Henrik Drefs
 */
public abstract class MicroserviceSecurityConfigurer extends WebSecurityConfigurerAdapter {

    /**
     * Only intended to configure access to the application by
     * <code>http.authorizeRequests()</code>
     * <p>
     * The default implementation expects authentication for any request.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    protected void configureAccess(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureAccess(http);
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(JwtAuthenticationToken::new).and().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .csrf()
                .disable();
    }
}
