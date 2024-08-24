package com.wootecam.luckyvickyauction.core.lock;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionLockOperation;
import com.wootecam.luckyvickyauction.global.aop.LockProvider;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockProvider implements LockProvider {

    private static final Logger log = LoggerFactory.getLogger(AuctionLockOperation.class);
    private static final int LOCK_RETRY_DURATION = 50;
    private static final int LOCK_MAX_RETRY = 5;
    private static final int LOCK_EXPIRE_TIME = 5;

    private final RedisTemplate<String, Long> redisOperations;

    @Override
    public void tryLock(String key) {
        int retry = 0;
        while (!lock(key)) {
            if (++retry == LOCK_MAX_RETRY) {
                throw new IllegalStateException("최대 시도 횟수에 도달했습니다. 재시도: " + LOCK_MAX_RETRY);
            }

            try {
                Thread.sleep(LOCK_RETRY_DURATION);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("레투스 락을 획득! LOCK: " + key);
    }

    @Override
    public void unlock(String key) {
        redisOperations.delete(key);
    }

    private boolean lock(String key) {
        Boolean success = redisOperations.opsForValue().setIfAbsent(key, 1L, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
        return success != null && success;
    }
}
