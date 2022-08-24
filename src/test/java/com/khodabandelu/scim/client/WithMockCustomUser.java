package com.khodabandelu.scim.client;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "mahdi";

    String name() default "Mahdi Khodabandelu";

    String password() default "password";

    String[] roles() default {"USER"};

    String organizationId() default "1";
}