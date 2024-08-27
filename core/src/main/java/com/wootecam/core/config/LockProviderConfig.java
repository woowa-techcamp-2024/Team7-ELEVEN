package com.wootecam.core.config;

import com.wootecam.core.aop.LockProvider;
import com.wootecam.core.lock.LettuceLockProvider;
import com.wootecam.core.lock.NoOperationLockProvider;
import com.wootecam.core.lock.RedissonLockProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LockProviderConfig {

    private final RedissonClient redissonClient;

    @Value("${lock.provider}")
    private String lockProviderType;

    @Bean
    public LockProvider lockProvider(RedisTemplate<String, Long> redisOperations) {
        return switch (lockProviderType) {
            case "lettuce" -> {
                log.info("Lettuce Lock Provider로 락을 관리합니다.");
                yield new LettuceLockProvider(redisOperations);
            }
            case "redisson" -> {
                log.info("Redisson Lock Provider로 락을 관리합니다.");
                yield new RedissonLockProvider(redissonClient);
            }
            case "none" -> {
                log.info("락을 사용하지 않습니다.");
                yield new NoOperationLockProvider();
            }
            default -> throw new IllegalArgumentException("Unknown lock provider type: " + lockProviderType);
        };
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());

        return redisTemplate;
    }

}
