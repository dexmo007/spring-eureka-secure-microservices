package com.dexmohq.spring.eureka.feign;

import java.lang.annotation.*;

/**
 * @author Henrik Drefs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoAuthentication {
}
