package com.wootecam.luckyvickyauction.global.config;

import com.wootecam.luckyvickyauction.core.lock.LettuceLockProvider;
import com.wootecam.luckyvickyauction.core.lock.NoOperationLockProvider;
import com.wootecam.luckyvickyauction.core.lock.RedissonLockProvider;
import com.wootecam.luckyvickyauction.global.aop.LockProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LockProviderConfig {

    private final RedisTemplate<String, Long> redisOperations;
    private final RedissonClient redissonClient;

    @Value("${lock.provider}")
    private String lockProviderType;

    @Bean
    public LockProvider lockProvider() {
        switch (lockProviderType) {
            case "lettuce":
                log.debug("Lettuce Lock Provider로 락을 관리합니다.");
                return new LettuceLockProvider(redisOperations);
            case "redisson":
                log.debug("Redisson Lock Provider로 락을 관리합니다.");
                return new RedissonLockProvider(redissonClient);
            case "none":
                log.debug("락을 관리하지 않습니다.");
                return new NoOperationLockProvider();
            default:
                throw new IllegalArgumentException("Unknown lock provider type: " + lockProviderType);
        }
    }

}
