package com.wootecam.api.controller.annotation;

import com.wootecam.core.domain.entity.type.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles {

    Role[] value();
}
