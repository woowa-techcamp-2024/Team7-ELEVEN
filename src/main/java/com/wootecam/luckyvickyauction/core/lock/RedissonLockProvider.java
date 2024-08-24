package com.wootecam.luckyvickyauction.core.lock;

import com.wootecam.luckyvickyauction.global.aop.LockProvider;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.ServiceUnavailableException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@Slf4j
@RequiredArgsConstructor
public class RedissonLockProvider implements LockProvider {

    private final RedissonClient redissonClient;

    @Override
    public void tryLock(String key) {
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(5, 5, TimeUnit.SECONDS);
            if (!available) {
                throw new ServiceUnavailableException("TimeOut에 도달했습니다.", ErrorCode.G002);
            }
            log.debug("==> 레디슨 락 획득! LOCK: {}", key);
        } catch (InterruptedException e) {
            throw new ServiceUnavailableException("시스템 문제로 락을 획득할 수 없습니다.", ErrorCode.G003);
        }
    }

    @Override
    public void unlock(String key) {
        RLock rLock = redissonClient.getLock(key);
        rLock.unlock();
        log.debug("<== 레디슨 락 해제! LOCK: {}", key);
    }
}
