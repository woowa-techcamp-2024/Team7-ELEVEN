package com.wootecam.luckyvickyauction.core.lock;

import com.wootecam.luckyvickyauction.global.aop.LockProvider;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class LettuceLockProvider implements LockProvider {

    private static final int LOCK_RETRY_DURATION = 50;
    private static final int LOCK_MAX_RETRY = 5;
    private static final int LOCK_EXPIRE_TIME = 5;

    private final RedisTemplate<String, Long> redisOperations;

    @Override
    public void tryLock(String key) {
        int retry = 0;
        while (!lock(key)) {
            if (++retry == LOCK_MAX_RETRY) {
                throw new BadRequestException("TimeOut에 도달했습니다. 최대재시도 횟수: " + LOCK_MAX_RETRY, ErrorCode.G002);
            }

            try {
                Thread.sleep(LOCK_RETRY_DURATION);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("레투스 락 획득! LOCK: " + key);
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
