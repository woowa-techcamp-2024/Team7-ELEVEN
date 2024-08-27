package com.wootecam.core.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(1)  // 커스텀 AOP의 우선순위를 높게 설정합니다.
public class DistributedLockAspect {

    private final LockProvider lockProvider;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key = getLockName(joinPoint, distributedLock);

        try {
            lockProvider.tryLock(key);
            return joinPoint.proceed();
        } finally {
            lockProvider.unlock(key);
        }

    }

    private String getLockName(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        // 메서드 파라미터 정보 가져오기
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // 파라미터를 컨텍스트에 추가
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // SpEL 파싱
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(distributedLock.value()).getValue(context, String.class);
    }

}
