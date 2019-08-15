package com.dexmohq.spring.eureka;

import com.dexmohq.spring.eureka.feign.*;
import feign.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class AuthenticationAwareFeignAutoConfigurationBase {

    protected final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of( FeignAutoConfiguration.class, AuthenticationAwareFeignAutoConfiguration.class));


    @Configuration
    static class BaseConfig {
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


    protected  <T> void  testFeignClient(Class<?> configClass, Class<T> clientType, Consumer<T> action, Consumer<Request> assertions) {
        this.contextRunner
                .withConfiguration(AutoConfigurations.of(
                        FeignAutoConfiguration.class))
                .withPropertyValues("microservices.eureka.auth.system-token=SYSTEM_TOKEN")
                .withUserConfiguration(configClass).run(context -> {
            assertThat(context).hasSingleBean(clientType);
            final T client = context.getBean(clientType);

            action.accept(client);

            final ArgumentCaptor<Request> arg = ArgumentCaptor.forClass(Request.class);
            Mockito.verify(context.getBean(Client.class)).execute(arg.capture(), Mockito.any());
            assertions.accept(arg.getValue());
        });
    }
}
