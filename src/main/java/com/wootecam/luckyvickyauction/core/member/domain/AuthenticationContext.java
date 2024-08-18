package com.wootecam.luckyvickyauction.core.member.domain;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private String principal;

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
