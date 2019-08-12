package com.dexmohq.spring.eureka.feign;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface AuthenticateAsSystem {
}
