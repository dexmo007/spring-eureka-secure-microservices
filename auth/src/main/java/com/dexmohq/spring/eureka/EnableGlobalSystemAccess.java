package com.dexmohq.spring.eureka;

import java.lang.annotation.*;

/**
 * Enable global access for the given role, i.e. if authentication with the here specified role is presented,
 * any further security restrictions are circumvented.
 * Intended to be used as an internal system role that is allowed to access any endpoint without the need to
 * explicitly authorize the role on each endpoint.
 * <p>
 * Must only be used on {@link MicroserviceGlobalMethodSecurityConfiguration}, otherwise it will have no effect!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableGlobalSystemAccess {

    /**
     * @return name of the role that represents internal system access
     */
    String roleName() default "ROLE_SYSTEM";

}