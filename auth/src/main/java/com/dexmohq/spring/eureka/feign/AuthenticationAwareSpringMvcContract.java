package com.dexmohq.spring.eureka.feign;

import feign.MethodMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class AuthenticationAwareSpringMvcContract extends SpringMvcContract {

    private final String systemAuthenticationToken;

    public static void addBearerToken(MethodMetadata data, String token) {
        data.template().header("Authorization", "Bearer " + token);
    }

    protected void authenticateAsSystem(MethodMetadata data) {
        data.template().header("X-Auth-Type", "SYSTEM");
        addBearerToken(data, systemAuthenticationToken);
    }

    protected void authenticateAsUser(MethodMetadata data) {
        data.template().header("X-Auth-Type", "USER");
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        validateAuthenticateAnnotations(method);
        if (method.isAnnotationPresent(AuthenticateAsSystem.class)) {
            authenticateAsSystem(data);
        } else if (method.isAnnotationPresent(AuthenticateAsUser.class)) {
            authenticateAsUser(data);
        }
    }

    private static void validateAuthenticateAnnotations(Method method) {
        if (method.isAnnotationPresent(AuthenticateAsSystem.class) &&
                method.isAnnotationPresent(AuthenticateAsUser.class)) {
            throw new IllegalStateException("Cannot use both AuthenticateAsSystem and AuthenticateAsUser annotations");
        }
    }
}
