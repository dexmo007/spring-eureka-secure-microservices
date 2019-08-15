package com.dexmohq.spring.eureka;

import com.dexmohq.spring.eureka.feign.AuthenticateAsSystem;
import com.dexmohq.spring.eureka.feign.AuthenticationAwareRequestInterceptor;
import com.dexmohq.spring.eureka.feign.AuthenticationAwareSpringMvcContract;
import feign.Contract;
import feign.Feign;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class AuthenticationAwareFeignAutoConfigurationBasicTest extends AuthenticationAwareFeignAutoConfigurationBase {

    @Test
    public void testAutoConfigCreatesExpectedBeans() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AuthenticationAwareSpringMvcContract.class);
            assertThat(context).hasSingleBean(AuthenticationAwareRequestInterceptor.class);
        });
    }

    @Test
    public void testIgnoredIfLibraryNotPresent() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(Feign.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Contract.class);
                });
    }

    @FeignClient(name = "client", url = "http://foo.bar")
    interface Client {
        @GetMapping
        @AuthenticateAsSystem
        String test();
    }

    @Configuration
    @EnableFeignClients(clients = Client.class)
    @CommonsLog
    static class Config extends BaseConfig {
        @Bean
        CommandLineRunner runner(Client client) {
            return args -> {
                log.info("Started with client " + client);
            };
        }
    }

    @Test
    public void testFailsWithoutSystemToken() {
        this.contextRunner
                .withUserConfiguration(Config.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasRootCauseInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("Cannot use AuthenticateAsSystem without token");
                });
    }

}
