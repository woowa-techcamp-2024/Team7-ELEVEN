package com.wootecam.luckyvickyauction.core.member.domain;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

    private SignInInfo principal;

    public void setPrincipal(SignInInfo principal) {
        this.principal = principal;
    }
}
