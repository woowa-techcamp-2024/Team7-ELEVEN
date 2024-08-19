package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.luckyvickyauction.core.member.domain.AuthenticationContext;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthenticationContext authenticationContext;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (isPublicPath(handlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession();
        Object attribute = session.getAttribute("signInMember");
        if (Objects.isNull(attribute)) {
            throw new NotFoundException("세션에 사용자가 존재하지 않습니다.", ErrorCode.AU00);
        }

        SignInInfo signInInfo = (SignInInfo) attribute;
        authenticationContext.setPrincipal(signInInfo);

        authorize(handlerMethod, signInInfo);

        return true;
    }

    private boolean isPublicPath(HandlerMethod handlerMethod) {
        return !handlerMethod.hasMethodAnnotation(BuyerOnly.class)
                && !handlerMethod.hasMethodAnnotation(SellerOnly.class)
                && !handlerMethod.hasMethodAnnotation(Roles.class);
    }

    private void authorize(HandlerMethod handlerMethod, final SignInInfo signInInfo) {
        authorizeSeller(signInInfo, handlerMethod.getMethodAnnotation(SellerOnly.class));
        authorizeBuyer(signInInfo, handlerMethod.getMethodAnnotation(BuyerOnly.class));
        authorizeMultipleRoles(signInInfo, handlerMethod.getMethodAnnotation(Roles.class));
    }

    private void authorizeSeller(SignInInfo signInInfo, SellerOnly sellerAnnotation) {
        if (Objects.nonNull(sellerAnnotation) && !signInInfo.isType(Role.SELLER)) {
            throw new UnauthorizedException("판매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU01);
        }
    }

    private void authorizeBuyer(SignInInfo signInInfo, BuyerOnly buyerAnnotation) {
        if (Objects.nonNull(buyerAnnotation) && !signInInfo.isType(Role.BUYER)) {
            throw new UnauthorizedException("구매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU02);
        }
    }

    private void authorizeMultipleRoles(SignInInfo signInInfo, Roles rolesAnnotation) {
        if (Objects.nonNull(rolesAnnotation)) {
            boolean hasPermission = Arrays.stream(rolesAnnotation.value())
                    .anyMatch(signInInfo::isType);

            if (!hasPermission) {
                String message = String.format("해당 사용자는 이 경로(API)에 접근할 권한이 없습니다. 사용자 권한: %s", signInInfo.role());
                throw new UnauthorizedException(message, ErrorCode.AU03);
            }
        }
    }
}
