package com.dexmohq.spring.eureka;

import com.dexmohq.spring.eureka.feign.AuthenticateAsSystem;
import com.dexmohq.spring.eureka.feign.AuthenticateAsUser;
import com.dexmohq.spring.eureka.feign.NoAuthentication;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class AuthenticationAwareFeignAutoConfigurationSystemClientTest extends AuthenticationAwareFeignAutoConfigurationBase {

    @FeignClient(name = "system-client", url = "http://foo.bar")
    @AuthenticateAsSystem
    interface SystemClient {

        @GetMapping
        String test();

        @GetMapping
        @AuthenticateAsUser
        String testAsUser();

        @GetMapping
        @AuthenticateAsSystem
        String testAsSystem();

        @GetMapping
        @NoAuthentication
        String testNoAuthentication();
    }


    @Configuration
    @EnableFeignClients(clients = SystemClient.class)
    @CommonsLog
    static class Config extends BaseConfig {
        @Bean
        CommandLineRunner runner(SystemClient client) {
            return args -> {
                log.info("Started with client " + client);
            };
        }
    }

    @Test
    public void testFeignClient_WithAuthAnnotationsOnMethod_AuthenticateAsUser() {
        testFeignClient(Config.class, SystemClient.class, client -> {
            SecurityContextHolder.getContext().setAuthentication(MockJwtAuthentication.of("TEST_USER", "ROLE_USER"));
            client.testAsUser();
        }, request -> {
            assertThat(request.headers()).hasEntrySatisfying("X-Auth-Type", values -> {
                assertThat(values).containsExactly("USER");
            });
            assertThat(request.headers()).hasEntrySatisfying("Authorization", values -> {
                assertThat(values).containsExactly("Bearer token_value");
            });
        });
    }

    @Test
    public void testFeignClient_WithAuthAnnotationsOnMethod_AuthenticateAsSystem() {
        testFeignClient(Config.class, SystemClient.class, SystemClient::testAsSystem, request -> {
            assertThat(request.headers()).hasEntrySatisfying("X-Auth-Type", values -> {
                assertThat(values).containsExactly("SYSTEM");
            });
            assertThat(request.headers()).hasEntrySatisfying("Authorization", values -> {
                assertThat(values).containsExactly("Bearer SYSTEM_TOKEN");
            });
        });
    }

    @Test
    public void testFeignClient_WithAuthAnnotationsOnMethod_ExplicitNoAuthentication() {
        testFeignClient(Config.class, SystemClient.class, SystemClient::testNoAuthentication, request -> {
            assertThat(request.headers()).doesNotContainKey("X-Auth-Type");
            assertThat(request.headers()).doesNotContainKey("Authorization");
        });
    }

    @Test
    public void testFeignClient_WithAuthAnnotationsOnMethod_ImplicitSystemAuthentication() {
        testFeignClient(Config.class, SystemClient.class, SystemClient::test, request -> {
            assertThat(request.headers()).hasEntrySatisfying("X-Auth-Type", values -> {
                assertThat(values).containsExactly("SYSTEM");
            });
            assertThat(request.headers()).hasEntrySatisfying("Authorization", values -> {
                assertThat(values).containsExactly("Bearer SYSTEM_TOKEN");
            });
        });
    }
}