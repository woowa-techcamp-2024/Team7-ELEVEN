package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("signInMember");
        if (Objects.isNull(attribute)) {
            throw new UnauthorizedException("세션에 사용자가 존재하지 않습니다.", ErrorCode.AU00);
        }
        SignInInfo signInInfo = (SignInInfo) attribute;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (isBothPossiblePath(handlerMethod)) {
            return true;
        }
        authorize(handlerMethod, signInInfo);

        return true;
    }

    private boolean isBothPossiblePath(HandlerMethod handlerMethod) {
        return !handlerMethod.hasMethodAnnotation(BuyerOnly.class) && !handlerMethod.hasMethodAnnotation(
                SellerOnly.class);
    }

    private void authorize(HandlerMethod handlerMethod, final SignInInfo signInInfo) {
        if (handlerMethod.hasMethodAnnotation(BuyerOnly.class) && signInInfo.isType(Role.SELLER)) {
            throw new UnauthorizedException("판매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU01);
        }
        if (handlerMethod.hasMethodAnnotation(SellerOnly.class) && signInInfo.isType(Role.BUYER)) {
            throw new UnauthorizedException("구매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU02);
        }
    }
}
