package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.core.domain.type.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles {

    Role[] value();
}
