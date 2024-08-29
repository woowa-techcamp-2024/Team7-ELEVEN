package com.wootecam.luckyvickyauction.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(2)
public class TransactionalTimeoutAspect {

    private static final int TIMEOUT_MARGIN = 100;
    private final TransactionTemplate transactionTemplate;

    @Value("${lock.redisson.lease_time: 500}")
    private int leaseTime;  // TODO: [시간을 초기화하고 보관하는 Bean을 별도로 만들어 관리하기] [writeAt: 2024/08/29/17:27] [writeBy: chhs2131]

    @Around("@annotation(transactionalTimeout)")
    public Object handleCustomTransaction(ProceedingJoinPoint joinPoint, TransactionalTimeout transactionalTimeout) {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = leaseTime - TIMEOUT_MARGIN;

        return transactionTemplate.execute((TransactionStatus status) -> {
            try {
                Object result = joinPoint.proceed();
                long elapsedTime = System.currentTimeMillis() - startTime;

                if (elapsedTime > timeoutMillis) {
                    log.debug("트랜잭션 타임아웃을 초과했습니다. 초과시간: {}ms", elapsedTime - timeoutMillis);
                    status.setRollbackOnly();
                    throw new RuntimeException(
                            "Transaction timed out after " + elapsedTime + " ms. Timeout was set to " + timeoutMillis
                                    + " ms.");
                }

                return result;  // 정상 수행한 결과 반환
            } catch (RuntimeException ex) {
                status.setRollbackOnly();
                throw ex;
            } catch (Throwable e) {
                log.error("message={}", e.getMessage(), e);
                throw new RuntimeException("처리할 수 없습니다.");
            }
        });
    }

}
