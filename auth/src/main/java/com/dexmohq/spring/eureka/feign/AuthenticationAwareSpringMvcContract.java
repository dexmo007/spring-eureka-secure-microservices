package com.dexmohq.spring.eureka.feign;

import com.dexmohq.spring.eureka.util.AnnotationUtils;
import feign.MethodMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class AuthenticationAwareSpringMvcContract extends SpringMvcContract {

    private final String systemAuthenticationToken;

    public static void addBearerToken(MethodMetadata data, String token) {
        data.template().header("Authorization", "Bearer " + token);
    }

    protected void authenticateAsSystem(MethodMetadata data) {
        if (systemAuthenticationToken == null) {
            throw new IllegalStateException("Cannot use " + AuthenticateAsSystem.class + " without token. Please specify \"microservices.eureka.auth.system-token\"");
        }
        data.template().header("X-Auth-Type", "SYSTEM");
        addBearerToken(data, systemAuthenticationToken);
    }

    protected void authenticateAsUser(MethodMetadata data) {
        data.template().header("X-Auth-Type", "USER");
    }

    private enum AuthType {
        NONE, USER, SYSTEM
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        validateAuthenticateAnnotations(method.getDeclaringClass());
        validateAuthenticateAnnotations(method);
        AuthType authType = AuthType.NONE;
        if (method.getDeclaringClass().isAnnotationPresent(AuthenticateAsSystem.class)) {
            authType = AuthType.SYSTEM;
        } else if (method.getDeclaringClass().isAnnotationPresent(AuthenticateAsUser.class)) {
            authType = AuthType.USER;
        }
        if (method.isAnnotationPresent(AuthenticateAsSystem.class)) {
            authType = AuthType.SYSTEM;
        } else if (method.isAnnotationPresent(AuthenticateAsUser.class)) {
            authType = AuthType.USER;
        } else if (method.isAnnotationPresent(NoAuthentication.class)) {
            authType = AuthType.NONE;
        }
        switch (authType) {
            case USER:
                authenticateAsUser(data);
                break;
            case SYSTEM:
                authenticateAsSystem(data);
                break;
            case NONE:
                break;
        }
    }

    private static void validateAuthenticateAnnotations(AnnotatedElement annotatedElement) {
        AnnotationUtils.validateOnlyOneOfAnnotationsPresent(annotatedElement,
                NoAuthentication.class, AuthenticateAsUser.class, AuthenticateAsSystem.class);
    }

}
