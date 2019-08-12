package com.dexmohq.spring.eureka;

import com.dexmohq.spring.eureka.feign.AuthenticateAsUser;
import com.dexmohq.spring.eureka.feign.AuthenticationAwareFeignAutoConfiguration;
import com.dexmohq.spring.eureka.feign.AuthenticationAwareRequestInterceptor;
import com.dexmohq.spring.eureka.feign.AuthenticationAwareSpringMvcContract;
import feign.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class AuthenticationAwareFeignAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AuthenticationAwareFeignAutoConfiguration.class));

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

    @FeignClient(name = "test-client", url = "http://foo.bar")
    interface TestClient {
        @GetMapping
        @AuthenticateAsUser
        String test();
    }

    @Configuration
    @EnableFeignClients(clients = TestClient.class)
    static class Config {
        @Bean
        Client client() {
            return Mockito.mock(Client.class,
                    invocation -> Response.builder()
                            .request(invocation.getArgument(0))
                            .status(200)
                            .body("TEST", StandardCharsets.UTF_8)
                            .build()
            );
        }

        @Bean
        HttpMessageConverters httpMessageConverters() {
            return new HttpMessageConverters(new StringHttpMessageConverter());
        }

    }

    @Test
    public void testFeignClientWithAuthorization() {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(
                        FeignAutoConfiguration.class))
                .withUserConfiguration(Config.class).run(context -> {
            assertThat(context).hasSingleBean(TestClient.class);
            SecurityContextHolder.getContext().setAuthentication(MockJwtAuthentication.of("TEST_USER","ROLE_USER"));
            assertThat(context).getBean(TestClient.class).returns("TEST", TestClient::test);
            final ArgumentCaptor<Request> arg = ArgumentCaptor.forClass(Request.class);
            Mockito.verify(context.getBean(Client.class)).execute(arg.capture(), Mockito.any());
            assertThat(arg.getValue().headers()).hasEntrySatisfying("X-Auth-Type", values -> {
                assertThat(values).containsExactly("USER");
            });
            assertThat(arg.getValue().headers()).hasEntrySatisfying("Authorization", values -> {
                assertThat(values).containsExactly("Bearer token_value");
            });
        });
    }
}
