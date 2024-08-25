package com.wootecam.luckyvickyauction.core.lock;

import com.wootecam.luckyvickyauction.global.aop.LockProvider;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.ServiceUnavailableException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class LettuceLockProvider implements LockProvider {

    private final RedisTemplate<String, Long> redisOperations;

    @Value("${lock.lettuce.retry_duration:50}")
    private int retryDuration;
    @Value("${lock.lettuce.max_retry:10}")
    private int maxRetry;
    @Value("${lock.lettuce.lease_time:200}")
    private int leaseTime;

    @Override
    public void tryLock(String key) {
        int retry = 0;
        while (!lock(key)) {
            if (++retry == maxRetry) {
                throw new ServiceUnavailableException("TimeOut에 도달했습니다. 최대재시도 횟수: " + maxRetry, ErrorCode.G002);
            }

            try {
                Thread.sleep(retryDuration);
            } catch (InterruptedException e) {
                throw new ServiceUnavailableException("시스템 문제로 락을 획득할 수 없습니다.", ErrorCode.G003);
            }
        }

        log.debug("레투스 락 획득! LOCK: " + key);
    }

    @Override
    public void unlock(String key) {
        redisOperations.delete(key);
    }

    private boolean lock(String key) {
        Boolean success = redisOperations.opsForValue().setIfAbsent(key, 1L, leaseTime, TimeUnit.MILLISECONDS);
        return success != null && success;
    }
}
